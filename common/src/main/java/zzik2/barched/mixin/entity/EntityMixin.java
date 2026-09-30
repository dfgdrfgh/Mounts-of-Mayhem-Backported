package zzik2.barched.mixin.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zzik2.barched.Barched;
import zzik2.barched.bridge.entity.EntityBridge;
import zzik2.barched.bridge.entity.PlayerBridge;
import zzik2.barched.bridge.entity.SaddleItemBridge;
import zzik2.barched.item.NautilusArmorItem;
import zzik2.barched.util.EntityAttachmentUtil;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityBridge {

    @Shadow public abstract void setPosRaw(double d, double e, double f);

    @Shadow public abstract void setYRot(float f);

    @Shadow public abstract void setXRot(float f);

    @Shadow public abstract void setOldPosAndRot();

    @Shadow protected abstract void reapplyPosition();

    @Shadow public abstract Vec3 calculateViewVector(float g, float h);

    @Shadow public abstract float getXRot();

    @Shadow public abstract float getYHeadRot();

    @Shadow @Nullable public abstract LivingEntity getControllingPassenger();

    @Shadow public abstract boolean isAlive();

    @Shadow
    public abstract Vec3 position();

    private Vec3 lastKnownSpeed;
    @Nullable private Vec3 lastKnownPosition;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void barched$init(EntityType<?> entityType, Level level, CallbackInfo ci) {
        this.lastKnownSpeed = Vec3.ZERO;
    }

    @Inject(method = "reapplyPosition", at = @At("HEAD"))
    private void barched$reapplyPosition(CallbackInfo ci) {
        this.lastKnownPosition = null;
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void barched$shearMountEquipment(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Entity self = (Entity) (Object) this;
        if (self.isVehicle() || player.isSecondaryUseActive()) {
            return;
        }

        ItemStack shears = player.getItemInHand(hand);
        if (!shears.is(Items.SHEARS)) {
            return;
        }

        ItemStack bodyArmor = ItemStack.EMPTY;
        SoundEvent bodyUnequipSound = null;
        if (self instanceof AbstractHorse horse) {
            ItemStack candidate = horse.getBodyArmorItem();
            if (candidate.getItem() instanceof AnimalArmorItem armor
                    && armor.getBodyType() == AnimalArmorItem.BodyType.EQUESTRIAN) {
                bodyArmor = candidate;
                bodyUnequipSound = Barched.SoundEvents.HORSE_ARMOR_UNEQUIP;
            }
        } else if (self instanceof AbstractNautilus nautilus) {
            ItemStack candidate = nautilus.getBodyArmorItem();
            if (candidate.getItem() instanceof NautilusArmorItem) {
                bodyArmor = candidate;
                bodyUnequipSound = Barched.SoundEvents.NAUTILUS_ARMOR_UNEQUIP;
            }
        }

        if (!bodyArmor.isEmpty()
                && (player.isCreative()
                || !EnchantmentHelper.has(bodyArmor, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE))) {
            if (!self.level().isClientSide()) {
                ItemStack removed = bodyArmor.copy();
                ((LivingEntity) self).setItemSlot(EquipmentSlot.BODY, ItemStack.EMPTY);
                self.spawnAtLocation(
                        removed,
                        EntityAttachmentUtil.averageY(self, EntityAttachment.PASSENGER)
                );
                self.gameEvent(GameEvent.SHEAR, player);
                self.playSound(bodyUnequipSound);
                shears.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }

            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }

        if (!(self instanceof Saddleable saddleable) || !(self instanceof SaddleItemBridge saddleBridge)) {
            return;
        }

        ItemStack saddle = saddleBridge.barched$getSaddleItem();
        if (!saddleable.isSaddled()
                || saddle.isEmpty()
                || (!player.isCreative()
                && EnchantmentHelper.has(saddle, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE))) {
            return;
        }

        if (!self.level().isClientSide()) {
            ItemStack removed = saddle.copy();
            saddleBridge.barched$setSaddleItem(ItemStack.EMPTY);
            self.spawnAtLocation(
                    removed,
                    EntityAttachmentUtil.averageY(self, EntityAttachment.PASSENGER)
            );
            self.gameEvent(GameEvent.SHEAR, player);
            self.playSound(Barched.SoundEvents.SADDLE_UNEQUIP);
            shears.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        }

        cir.setReturnValue(InteractionResult.SUCCESS);
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V", shift = At.Shift.AFTER, ordinal = 0))
    private void barched$baseTick(CallbackInfo ci) {
        this.computeSpeed();
    }

    @Override
    public void computeSpeed() {
        if (this.lastKnownPosition == null) {
            this.lastKnownPosition = this.position();
        }

        this.lastKnownSpeed = this.position().subtract(this.lastKnownPosition);
        this.lastKnownPosition = this.position();
    }

    @Override
    public Vec3 getHeadLookAngle() {
        return this.calculateViewVector(this.getXRot(), this.getYHeadRot());
    }

    @Override
    public Vec3 getKnownSpeed() {
        LivingEntity var2 = this.getControllingPassenger();
        if (var2 instanceof Player) {
            Player player = (Player)var2;
            if (this.isAlive()) {
                return ((PlayerBridge) player).getKnownSpeed();
            }
        }

        return this.lastKnownSpeed;
    }
}
