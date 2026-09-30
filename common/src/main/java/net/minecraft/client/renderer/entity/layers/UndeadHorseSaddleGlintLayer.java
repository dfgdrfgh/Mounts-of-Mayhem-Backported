package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import zzik2.barched.bridge.entity.AbstractHorseBridge;

@Environment(EnvType.CLIENT)
public class UndeadHorseSaddleGlintLayer extends RenderLayer<AbstractHorse, HorseModel<AbstractHorse>> {
    private final HorseModel<AbstractHorse> model;

    public UndeadHorseSaddleGlintLayer(
            RenderLayerParent<AbstractHorse, HorseModel<AbstractHorse>> parent,
            EntityModelSet modelSet,
            ModelLayerLocation modelLayerLocation
    ) {
        super(parent);
        ModelPart root = modelSet.bakeLayer(modelLayerLocation);
        this.model = new HorseModel<>(root);

        root.getAllParts().forEach(part -> part.skipDraw = true);

        ModelPart body = root.getChild("body");
        ModelPart head = root.getChild("head_parts");
        body.getChild("saddle").skipDraw = false;
        head.getChild("left_saddle_mouth").skipDraw = false;
        head.getChild("right_saddle_mouth").skipDraw = false;
        head.getChild("left_saddle_line").skipDraw = false;
        head.getChild("right_saddle_line").skipDraw = false;
        head.getChild("head_saddle").skipDraw = false;
        head.getChild("mouth_saddle_wrap").skipDraw = false;
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            AbstractHorse horse,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ItemStack saddle = ((AbstractHorseBridge) (Object) horse).barched$getSaddleItem();
        if (!horse.isSaddled() || !saddle.hasFoil()) {
            return;
        }

        this.getParentModel().copyPropertiesTo(this.model);
        this.model.prepareMobModel(horse, limbSwing, limbSwingAmount, partialTick);
        this.model.setupAnim(horse, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

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
