package zzik2.barched.mixin.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zzik2.barched.Barched;
import zzik2.barched.bridge.entity.MobBridge;
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
        double baseDamage = this.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
        double candidateDamage = candidateModifiers.compute(baseDamage, EquipmentSlot.MAINHAND);
        double currentDamage = currentModifiers.compute(baseDamage, EquipmentSlot.MAINHAND);

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
