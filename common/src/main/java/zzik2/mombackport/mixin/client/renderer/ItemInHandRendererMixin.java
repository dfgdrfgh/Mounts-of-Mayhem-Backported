package zzik2.mombackport.mixin.client.renderer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.effects.SpearAnimations;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwingAnimationType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.mombackport.MomBackport;
import zzik2.mombackport.bridge.entity.LivingEntityBridge;
import zzik2.mombackport.bridge.entity.PlayerBridge;
import zzik2.mombackport.bridge.item.ItemStackBridge;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Unique private boolean mombackport$firstPersonAttack = false;

    @Shadow protected abstract void applyItemArmAttackTransform(PoseStack arg, HumanoidArm arg2, float g);

    @Shadow protected abstract void applyItemArmTransform(PoseStack poseStack, HumanoidArm humanoidArm, float f);

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/UseAnim;", ordinal = 0))
    private void mombackport$firstPersonUse(AbstractClientPlayer abstractClientPlayer, float f, float g, InteractionHand interactionHand, float h, ItemStack itemStack, float i, PoseStack poseStack, MultiBufferSource multiBufferSource, int j, CallbackInfo ci, @Local(ordinal = 0) HumanoidArm humanoidArm , @Local(ordinal = 1) int q) {
        if (itemStack.getUseAnimation() == MomBackport.UseAnim.MOMBACKPORT$SPEAR) {
            poseStack.translate((float)q * 0.56F, -0.52F, -0.72F);
            float l = (float)itemStack.getUseDuration(abstractClientPlayer) - ((float)abstractClientPlayer.getUseItemRemainingTicks() - f + 1.0F);
            SpearAnimations.firstPersonUse(((LivingEntityBridge) abstractClientPlayer).getTicksSinceLastKineticHitFeedback(f), poseStack, l, humanoidArm, itemStack);
        }
    }

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 12, shift = At.Shift.BEFORE))
    private void mombackport$firstPersonAttack(AbstractClientPlayer abstractClientPlayer, float f, float g, InteractionHand interactionHand, float h, ItemStack itemStack, float i, PoseStack poseStack, MultiBufferSource multiBufferSource, int j, CallbackInfo ci) {
        SwingAnimationType type = ((ItemStackBridge) (Object) itemStack).getSwingAnimation().type();
        if (type == SwingAnimationType.STAB) {
            mombackport$firstPersonAttack = true;
        }
    }

    @Redirect(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 12))
    private void mombackport$firstPersonAttack0(PoseStack instance, float f, float g, float h) {
        if (!mombackport$firstPersonAttack) instance.translate(f, g, h);
    }

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V", ordinal = 8, shift = At.Shift.AFTER))
    private void mombackport$firstpersonAttack1(AbstractClientPlayer abstractClientPlayer, float f, float g, InteractionHand interactionHand, float h, ItemStack itemStack, float i, PoseStack poseStack, MultiBufferSource multiBufferSource, int j, CallbackInfo ci, @Local(ordinal = 0) HumanoidArm humanoidArm , @Local(ordinal = 1) int q) {
        if (mombackport$firstPersonAttack) SpearAnimations.firstPersonAttack(h, poseStack, q, humanoidArm);
    }

    @Redirect(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmAttackTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V", ordinal = 1))
    private void mombackport$firstPersonAttack2(ItemInHandRenderer instance, PoseStack poseStack, HumanoidArm humanoidArm, float f) {
        if (!mombackport$firstPersonAttack) this.applyItemArmAttackTransform(poseStack, humanoidArm, f);
    }

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmAttackTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V", ordinal = 1, shift = At.Shift.AFTER))
    private void mombackport$firstPersonAttack3(AbstractClientPlayer abstractClientPlayer, float f, float g, InteractionHand interactionHand, float h, ItemStack itemStack, float i, PoseStack poseStack, MultiBufferSource multiBufferSource, int j, CallbackInfo ci) {
        mombackport$firstPersonAttack = false;
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean mombackport$firstPersonAttack4(ItemStack arg, ItemStack arg2, Operation<Boolean> original) {
        if (this.shouldInstantlyReplaceVisibleItem(arg, arg2)) {
            return true;
        }
        return original.call(arg, arg2);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float mombackport$tick(LocalPlayer instance, float v) {
        return ((PlayerBridge) instance).getItemSwapScale(v);
    }

    private boolean shouldInstantlyReplaceVisibleItem(ItemStack itemStack, ItemStack itemStack2) {
        if (MomBackport.ItemStack.matchesIgnoringComponents(itemStack, itemStack2, MomBackport.DataComponentType::ignoreSwapAnimation)) {
            return true;
        } else {
            return false; //return !this.itemModelResolver.shouldPlaySwapAnimation(itemStack2); TODO
        }
    }
}
