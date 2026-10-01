package zzik2.mombackport.mixin.client.model;

import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.SwingAnimationType;
import net.minecraft.world.item.component.SwingAnimation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.mombackport.MomBackport;
import zzik2.mombackport.MomBackportClient;
import zzik2.mombackport.bridge.client.HumanoidModelBridge;
import zzik2.mombackport.bridge.entity.LivingEntityBridge;

@Mixin(AbstractZombieModel.class)
public abstract class AbstractZombieModelMixin<T extends Monster> extends HumanoidModel<T> implements HumanoidModelBridge<T> {

    @Shadow
    public abstract boolean isAggressive(T arg);

    @Unique private T mombackport$monster;
    @Unique private float mombackport$h;

    public AbstractZombieModelMixin(ModelPart modelPart) {
        super(modelPart);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/monster/Monster;FFFFF)V", at = @At("HEAD"), order = 2000)
    private void mombackport$setupAnim(T monster, float f, float g, float h, float i, float j, CallbackInfo ci) {
        this.mombackport$monster = monster;
        this.mombackport$h = h;
    }

    @Redirect(method = "setupAnim(Lnet/minecraft/world/entity/monster/Monster;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/AnimationUtils;animateZombieArms(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;ZFF)V"), order = 2000)
    private void mombackport$setupAnim(ModelPart h, ModelPart i, boolean j, float arg, float arg2) {
        MomBackportClient.AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, this.isAggressive(mombackport$monster), this.attackTime, mombackport$h, (LivingEntity) mombackport$monster);
    }

    @Override
    public ArmPose abstractZombie$super$getArmPose(T livingEntity, HumanoidArm humanoidArm, @Nullable HumanoidModel.ArmPose fallback) {
        return mombackport$getArmPose(livingEntity, humanoidArm, fallback);
    }

    @Override
    public ArmPose getArmPose(T livingEntity, HumanoidArm humanoidArm, HumanoidModel.ArmPose fallback) {
        return mombackport$getArmPose(livingEntity, humanoidArm, fallback);
    }

    @Override
    public @Nullable ArmPose getArmPoseFallback() {
        return ArmPose.EMPTY;
    }

    @Unique
    public ArmPose mombackport$getArmPose(T livingEntity, HumanoidArm humanoidArm, HumanoidModel.ArmPose fallback) {
        boolean aggressive = this.isAggressive(livingEntity);

        if (!aggressive) {
            SwingAnimation swingAnimation = (SwingAnimation)((LivingEntityBridge) livingEntity).getItemHeldByArm(humanoidArm.getOpposite()).get(MomBackport.DataComponents.SWING_ANIMATION);
            if (swingAnimation != null && swingAnimation.type() == SwingAnimationType.STAB) {
                return MomBackportClient.ArmPose.SPEAR;
            }
        }

        HumanoidModel.ArmPose superPose = super$getArmPose(livingEntity, humanoidArm, getArmPoseFallback());
        if (superPose == MomBackportClient.ArmPose.SPEAR) {
            return aggressive ? superPose : getArmPoseFallback();
        }
        return superPose;
    }
}
