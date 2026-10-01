package zzik2.mombackport.mixin.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.effects.SpearAnimations;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwingAnimationType;
import net.minecraft.world.item.component.SwingAnimation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.mombackport.MomBackport;
import zzik2.mombackport.MomBackportClient;
import zzik2.mombackport.bridge.client.HumanoidModelBridge;
import zzik2.mombackport.bridge.entity.LivingEntityBridge;
import zzik2.mombackport.bridge.item.ItemStackBridge;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> implements HumanoidModelBridge<T> {

    @Shadow public HumanoidModel.ArmPose rightArmPose;

    @Shadow @Final public ModelPart rightArm;

    @Shadow @Final public ModelPart head;

    @Shadow public HumanoidModel.ArmPose leftArmPose;

    @Shadow @Final public ModelPart leftArm;

    @Inject(method = "prepareMobModel(Lnet/minecraft/world/entity/LivingEntity;FFF)V", at = @At("HEAD"))
    private void mombackport$prepareMobModel(T livingEntity, float f, float g, float h, CallbackInfo ci) {
        this.rightArmPose = mombackport$updateArmPose(livingEntity, HumanoidArm.RIGHT, this.rightArmPose);
        this.leftArmPose = mombackport$updateArmPose(livingEntity, HumanoidArm.LEFT, this.leftArmPose);
    }

    @Unique
    private HumanoidModel.ArmPose mombackport$updateArmPose(T livingEntity, HumanoidArm humanoidArm, HumanoidModel.ArmPose currentArmPose) {
        HumanoidModel.ArmPose pose = getArmPose(livingEntity, humanoidArm, getArmPoseFallback());
        if (pose != null) return pose;
        if (currentArmPose == MomBackportClient.ArmPose.SPEAR) return HumanoidModel.ArmPose.EMPTY;
        return currentArmPose != null ? currentArmPose : HumanoidModel.ArmPose.EMPTY;
    }

    @Inject(method = "setupAttackAnimation", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;sin(F)F", ordinal = 4, shift = At.Shift.AFTER), cancellable = true)
    private void mombackport$setupAttackAnimation(T livingEntity, float f, CallbackInfo ci) {
        ItemStack itemStack = livingEntity.getMainHandItem();
        if (((ItemStackBridge) (Object) itemStack).getSwingAnimation().type() == SwingAnimationType.STAB) {
            SpearAnimations.thirdPersonAttackHand((HumanoidModel) (Object) this, livingEntity);
            ci.cancel();
        }
    }

    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    private void mombackport$poseRightArm(T livingEntity, CallbackInfo ci) {
        if (this.rightArmPose == MomBackportClient.ArmPose.SPEAR) {
            SpearAnimations.thirdPersonHandUse(this.rightArm, this.head, true, ((LivingEntityBridge) livingEntity).mombackport$getUseItemStackForArm(HumanoidArm.RIGHT), livingEntity);
            ci.cancel();
        }
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    private void mombackport$poseLeftArm(T livingEntity, CallbackInfo ci) {
        if (this.leftArmPose == MomBackportClient.ArmPose.SPEAR) {
            SpearAnimations.thirdPersonHandUse(this.leftArm, this.head, false, ((LivingEntityBridge) livingEntity).mombackport$getUseItemStackForArm(HumanoidArm.LEFT), livingEntity);
            ci.cancel();
        }
    }

    @Override
    public HumanoidModel.ArmPose super$getArmPose(T livingEntity, HumanoidArm humanoidArm, HumanoidModel.ArmPose fallback) {
        return mombackport$getArmPose(livingEntity, humanoidArm, fallback);
    }

    @Override
    public HumanoidModel.ArmPose getArmPose(T livingEntity, HumanoidArm humanoidArm, HumanoidModel.ArmPose fallback) {
        return mombackport$getArmPose(livingEntity, humanoidArm, fallback);
    }

    @Override
    public HumanoidModel.ArmPose getArmPoseFallback() {
        return null;
    }

    @Unique
    public HumanoidModel.ArmPose mombackport$getArmPose(T livingEntity, HumanoidArm humanoidArm, HumanoidModel.ArmPose fallback) {
        ItemStack itemStack = ((LivingEntityBridge) livingEntity).getItemHeldByArm(humanoidArm);
        SwingAnimation swingAnimation = (SwingAnimation)itemStack.get(MomBackport.DataComponents.SWING_ANIMATION);
        if (swingAnimation != null && swingAnimation.type() == SwingAnimationType.STAB && livingEntity.swinging) {
            return MomBackportClient.ArmPose.SPEAR;
        } else {
            return itemStack.is(MomBackport.ItemTags.SPEARS) ? MomBackportClient.ArmPose.SPEAR : fallback;
        }
    }
}
