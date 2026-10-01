package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.CamelModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.item.ItemStack;
import zzik2.barched.bridge.entity.SaddleItemBridge;

@Environment(EnvType.CLIENT)
public class CamelSaddleGlintLayer extends RenderLayer<Camel, CamelModel<Camel>> {
    private final CamelModel<Camel> model;

    public CamelSaddleGlintLayer(
            RenderLayerParent<Camel, CamelModel<Camel>> parent,
            EntityModelSet modelSet
    ) {
        super(parent);
        ModelPart root = modelSet.bakeLayer(ModelLayers.CAMEL);
        this.model = new CamelModel<>(root);

        root.getAllParts().forEach(part -> part.skipDraw = true);

        ModelPart body = root.getChild("body");
        ModelPart head = body.getChild("head");
        body.getChild("saddle").skipDraw = false;
        head.getChild("bridle").skipDraw = false;
        head.getChild("reins").skipDraw = false;
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            Camel camel,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ItemStack saddle = ((SaddleItemBridge) (Object) camel).barched$getSaddleItem();
        if (!camel.isSaddled() || !saddle.hasFoil()) {
            return;
        }

        this.getParentModel().copyPropertiesTo(this.model);
        this.model.prepareMobModel(camel, limbSwing, limbSwingAmount, partialTick);
        this.model.setupAnim(camel, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        VertexConsumer glint = bufferSource.getBuffer(RenderType.armorEntityGlint());
        this.model.renderToBuffer(
                poseStack,
                glint,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                -1
        );
    }
}
