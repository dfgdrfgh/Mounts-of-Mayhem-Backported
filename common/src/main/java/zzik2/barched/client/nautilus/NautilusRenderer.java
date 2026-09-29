package zzik2.barched.client.nautilus;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.animal.nautilus.*;
import net.minecraft.client.model.monster.nautilus.ZombieNautilusCoralModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.animal.nautilus.ZombieNautilus;
import zzik2.barched.item.NautilusArmorItem;

public class NautilusRenderer extends MobRenderer<AbstractNautilus, NautilusModel> {
    private final NautilusModel adult = this.model;
    private final NautilusModel baby = new NautilusModel(NautilusModel.createBabyBodyLayer().bakeRoot());
    private final NautilusModel coral = new ZombieNautilusCoralModel(ZombieNautilusCoralModel.createBodyLayer().bakeRoot());
    public NautilusRenderer(EntityRendererProvider.Context context) {
        super(context, new NautilusModel(NautilusModel.createBodyLayer().bakeRoot()), 0.7F);
        this.addLayer(new EquipmentLayer(this));
    }
    @Override public ResourceLocation getTextureLocation(AbstractNautilus entity) {
        if (entity instanceof ZombieNautilus zombie) return ResourceLocation.withDefaultNamespace(zombie.isCoral() ? "textures/entity/nautilus/zombie_nautilus_coral.png" : "textures/entity/nautilus/zombie_nautilus.png");
        return ResourceLocation.withDefaultNamespace(entity.isBaby() ? "textures/entity/nautilus/nautilus_baby.png" : "textures/entity/nautilus/nautilus.png");
    }
    @Override public void render(AbstractNautilus entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        this.model = entity instanceof ZombieNautilus zombie && zombie.isCoral() ? this.coral : entity.isBaby() ? this.baby : this.adult;
        super.render(entity, yaw, partialTick, pose, buffers, light);
    }
    private static class EquipmentLayer extends RenderLayer<AbstractNautilus, NautilusModel> {
        private final NautilusModel armor = new NautilusArmorModel(NautilusArmorModel.createBodyLayer().bakeRoot());
        private final NautilusModel saddle = new NautilusSaddleModel(NautilusSaddleModel.createSaddleLayer().bakeRoot());
        EquipmentLayer(RenderLayerParent<AbstractNautilus, NautilusModel> parent) { super(parent); }
        @Override public void render(PoseStack pose, MultiBufferSource buffers, int light, AbstractNautilus entity, float limbSwing, float limbAmount, float partialTick, float age, float yaw, float pitch) {
            if (entity.isBaby()) return;
            if (entity.getBodyArmorItem().getItem() instanceof NautilusArmorItem item) draw(armor, item.getTexture(), pose, buffers, light, entity, limbSwing, limbAmount, age, yaw, pitch);
            if (entity.isSaddled()) draw(saddle, ResourceLocation.withDefaultNamespace("textures/entity/equipment/nautilus_saddle/saddle.png"), pose, buffers, light, entity, limbSwing, limbAmount, age, yaw, pitch);
        }
        private void draw(NautilusModel model, ResourceLocation texture, PoseStack pose, MultiBufferSource buffers, int light, AbstractNautilus entity, float limbSwing, float limbAmount, float age, float yaw, float pitch) {
            this.getParentModel().copyPropertiesTo(model);
            model.setupAnim(entity, limbSwing, limbAmount, age, yaw, pitch);
            model.renderToBuffer(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(texture)), light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
