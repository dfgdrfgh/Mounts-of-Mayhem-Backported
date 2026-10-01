package zzik2.barched.mixin.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Saddleable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.barched.bridge.entity.SaddleItemBridge;

@Mixin(SaddleLayer.class)
public abstract class SaddleLayerMixin<T extends Entity & Saddleable, M extends EntityModel<T>> extends RenderLayer<T, M> {
    @Shadow @Final private M model;

    protected SaddleLayerMixin(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void barched$renderSaddleFoil(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            T entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            CallbackInfo ci
    ) {
        if (!entity.isSaddled()
                || !(entity instanceof SaddleItemBridge saddleBridge)
                || !saddleBridge.barched$getSaddleItem().hasFoil()) {
            return;
        }

        this.getParentModel().copyPropertiesTo(this.model);
        this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        VertexConsumer glint = bufferSource.getBuffer(RenderType.armorEntityGlint());
        this.model.renderToBuffer(
                poseStack,
                glint,
                packedLight,
                OverlayTexture.NO_OVERLAY
        );
    }
}
