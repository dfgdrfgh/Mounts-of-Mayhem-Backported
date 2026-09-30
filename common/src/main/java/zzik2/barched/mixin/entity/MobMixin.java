package zzik2.barched.mixin.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zzik2.barched.Barched;
import zzik2.barched.item.NautilusArmorItem;
import zzik2.barched.bridge.entity.MobBridge;
import zzik2.barched.bridge.item.ItemStackBridge;
import zzik2.zreflex.mixin.ModifyAccess;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements MobBridge {

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyAccess(access = Opcodes.ACC_PUBLIC, removeFinal = true)
    @Shadow
    public final InteractionResult interact(Player player, InteractionHand interactionHand) {
        return null;
    }

    @Shadow protected abstract boolean isSunBurnTick();

    @Inject(method = "equipItemIfPossible", at = @At("HEAD"), cancellable = true)
    private void barched$restrictNautilusArmorAutoEquip(ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (stack.getItem() instanceof NautilusArmorItem && !((Object) this instanceof AbstractNautilus)) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }
        if (stack.getItem() instanceof AnimalArmorItem animalArmor
                && animalArmor.getBodyType() == AnimalArmorItem.BodyType.EQUESTRIAN
                && !((Object) this instanceof Horse)
                && !((Object) this instanceof ZombieHorse)) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Redirect(
            method = "doHurtTarget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSources;mobAttack(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/damagesource/DamageSource;"
            )
    )
    private DamageSource barched$weaponDamageSource(DamageSources sources, LivingEntity attacker) {
        ItemStack weapon = this.getWeaponItem();
        return ((ItemStackBridge) (Object) weapon).getDamageSource(
                (LivingEntity) (Object) this,
                () -> sources.mobAttack(attacker)
        );
    }

    @Inject(method = "doHurtTarget", at = @At("TAIL"))
    private void barched$doHurtTarget(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        this.lungeForwardMaybe();
    }

    @Inject(method = "canReplaceCurrentItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void barched$compareSpearsAsWeapons(ItemStack candidate, ItemStack current, CallbackInfoReturnable<Boolean> cir) {
        boolean candidateIsSpear = candidate.has(Barched.DataComponents.PIERCING_WEAPON);
        boolean currentIsSpear = current.has(Barched.DataComponents.PIERCING_WEAPON);
        if (!candidateIsSpear && !currentIsSpear) {
            return;
        }

        if (current.isEmpty()) {
            cir.setReturnValue(true);
            return;
        }

        ItemAttributeModifiers candidateModifiers = candidate.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        ItemAttributeModifiers currentModifiers = current.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        TagKey<Item> preferredWeapons = this.barched$preferredWeapons();
        if (preferredWeapons != null && candidate.is(preferredWeapons) != current.is(preferredWeapons)) {
            cir.setReturnValue(candidate.is(preferredWeapons));
            return;
        }

        double baseDamage = this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                ? this.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) : 0.0D;
        double candidateDamage = barched$attackDamage(candidateModifiers, baseDamage);
        double currentDamage = barched$attackDamage(currentModifiers, baseDamage);

        if (candidateDamage != currentDamage) {
            cir.setReturnValue(candidateDamage > currentDamage);
            return;
        }

        int candidateEnchantments = candidate.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).entrySet().size();
        int currentEnchantments = current.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).entrySet().size();
        if (candidateEnchantments != currentEnchantments) {
            cir.setReturnValue(candidateEnchantments > currentEnchantments);
            return;
        }

        int candidateDamageValue = candidate.getDamageValue();
        int currentDamageValue = current.getDamageValue();
        if (candidateDamageValue != currentDamageValue) {
            cir.setReturnValue(candidateDamageValue < currentDamageValue);
            return;
        }

        cir.setReturnValue(candidate.has(DataComponents.CUSTOM_NAME) && !current.has(DataComponents.CUSTOM_NAME));
    }

    @Unique
    private TagKey<Item> barched$preferredWeapons() {
        Object self = this;
        if (self instanceof WitherSkeleton) return null;
        if (self instanceof AbstractSkeleton) return Barched.ItemTags.SKELETON_PREFERRED_WEAPONS;
        if (self instanceof Drowned) return Barched.ItemTags.DROWNED_PREFERRED_WEAPONS;
        if (self instanceof Pillager) return Barched.ItemTags.PILLAGER_PREFERRED_WEAPONS;
        if (self instanceof Piglin piglin && !piglin.isBaby()) return Barched.ItemTags.PIGLIN_PREFERRED_WEAPONS;
        return null;
    }

    @Unique
    private static double barched$attackDamage(ItemAttributeModifiers modifiers, double baseDamage) {
        // 1.21.1 compute(base, slot) combines every attribute. 1.21.11 also
        // filters by attribute, so attack speed must not affect weapon choice.
        double damage = baseDamage;
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (!entry.slot().test(EquipmentSlot.MAINHAND) || !entry.attribute().equals(Attributes.ATTACK_DAMAGE)) {
                continue;
            }
            double amount = entry.modifier().amount();
            damage += switch (entry.modifier().operation()) {
                case ADD_VALUE -> amount;
                case ADD_MULTIPLIED_BASE -> amount * baseDamage;
                case ADD_MULTIPLIED_TOTAL -> amount * damage;
            };
        }
        return damage;
    }

    @Override
    public EquipmentSlot sunProtectionSlot() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public void burnUndead() {
        if (this.isAlive() && this.isSunBurnTick()) {
            EquipmentSlot equipmentSlot = this.sunProtectionSlot();
            ItemStack itemStack = this.getItemBySlot(equipmentSlot);
            if (!itemStack.isEmpty()) {
                if (itemStack.isDamageableItem()) {
                    Item item = itemStack.getItem();
                    itemStack.setDamageValue(itemStack.getDamageValue() + this.random.nextInt(2));
                    if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
                        this.onEquippedItemBroken(item, equipmentSlot);
                        this.setItemSlot(equipmentSlot, ItemStack.EMPTY);
                    }
                }

            } else {
                this.igniteForSeconds(8.0F);
            }
        }
    }

    @Override
    public float chargeSpeedModifier() {
        return 1.0F;
    }
}
