package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.CamelHuskModel;
import net.minecraft.client.model.CamelModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.CamelHusk;

@Environment(EnvType.CLIENT)
public class CamelHuskSaddleLayer extends RenderLayer<CamelHusk, CamelHuskModel<CamelHusk>> {

    private static final ResourceLocation SADDLE_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/entity/equipment/camel_husk_saddle/saddle.png");

    private final CamelModel<CamelHusk> model;
    private final ModelPart saddle;
    private final ModelPart bridle;
    private final ModelPart reins;

    public CamelHuskSaddleLayer(
            RenderLayerParent<CamelHusk, CamelHuskModel<CamelHusk>> parent,
            EntityModelSet modelSet
    ) {
        super(parent);
        this.model = new CamelModel<>(modelSet.bakeLayer(ModelLayers.CAMEL));

        ModelPart root = this.model.root();
        root.getAllParts().forEach(part -> part.skipDraw = true);

        ModelPart body = root.getChild("body");
        ModelPart head = body.getChild("head");
        this.saddle = body.getChild("saddle");
        this.bridle = head.getChild("bridle");
        this.reins = head.getChild("reins");

        this.saddle.skipDraw = false;
        this.bridle.skipDraw = false;
        this.reins.skipDraw = false;
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            CamelHusk camelHusk,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (!camelHusk.isSaddled()) {
            return;
        }

        this.getParentModel().copyPropertiesTo(this.model);
        this.model.prepareMobModel(camelHusk, limbSwing, limbSwingAmount, partialTick);
        this.model.setupAnim(camelHusk, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        this.saddle.visible = true;
        this.bridle.visible = true;
        this.reins.visible = camelHusk.isVehicle();

        VertexConsumer vertexConsumer =
                bufferSource.getBuffer(RenderType.entityCutoutNoCull(SADDLE_TEXTURE));
        this.model.renderToBuffer(
                poseStack,
                vertexConsumer,
                packedLight,
                OverlayTexture.NO_OVERLAY
        );
    }
}
