package net.minecraft.client.model.animal.nautilus;

import net.minecraft.client.animation.definitions.NautilusAnimation;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class NautilusModel
extends HierarchicalModel<AbstractNautilus> {
    private static final float SWIM_ANIMATION_SPEED_MAX = 2.0f;
    private static final float SWIM_ANIMATION_SCALE_FACTOR = 3.0f;
    private static final float IDLE_SWIM_ANIMATION_SPEED = 0.2f;
    private static final float IDLE_SWIM_ANIMATION_SCALE = 5.0f;
    protected final ModelPart body;
    protected final ModelPart nautilus;
    private final ModelPart root;

    public NautilusModel(ModelPart $$0) {
        this.root = $$0;
        this.nautilus = $$0.getChild("root");
        this.body = this.nautilus.getChild("body");

    }

    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(NautilusModel.createBodyMesh(), 128, 128);
    }

    public static MeshDefinition createBodyMesh() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0f, 29.0f, -6.0f));
        $$2.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0f, -10.0f, -7.0f, 14.0f, 10.0f, 16.0f, new CubeDeformation(0.0f)).texOffs(0, 26).addBox(-7.0f, 0.0f, -7.0f, 14.0f, 8.0f, 20.0f, new CubeDeformation(0.0f)).texOffs(48, 26).addBox(-7.0f, 0.0f, 6.0f, 14.0f, 8.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.offset(0.0f, -13.0f, 5.0f));
        PartDefinition $$3 = $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 54).addBox(-5.0f, -4.51f, -3.0f, 10.0f, 8.0f, 14.0f, new CubeDeformation(0.0f)).texOffs(0, 76).addBox(-5.0f, -4.51f, 7.0f, 10.0f, 8.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.offset(0.0f, -8.5f, 12.3f));
        $$3.addOrReplaceChild("upper_mouth", CubeListBuilder.create().texOffs(54, 54).addBox(-5.0f, -2.0f, 0.0f, 10.0f, 4.0f, 4.0f, new CubeDeformation(-0.001f)), PartPose.offset(0.0f, -2.51f, 7.0f));
        $$3.addOrReplaceChild("inner_mouth", CubeListBuilder.create().texOffs(54, 70).addBox(-3.0f, -2.0f, -0.5f, 6.0f, 4.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.offset(0.0f, -0.51f, 7.5f));
        $$3.addOrReplaceChild("lower_mouth", CubeListBuilder.create().texOffs(54, 62).addBox(-5.0f, -1.98f, 0.0f, 10.0f, 4.0f, 4.0f, new CubeDeformation(-0.001f)), PartPose.offset(0.0f, 1.49f, 7.0f));
        return $$0;
    }

    public static LayerDefinition createBabyBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(-0.5f, 28.0f, -0.5f));
        $$2.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0f, -4.0f, -1.0f, 7.0f, 4.0f, 7.0f, new CubeDeformation(0.0f)).texOffs(0, 11).addBox(-6.0f, 0.0f, -1.0f, 7.0f, 4.0f, 9.0f, new CubeDeformation(0.0f)).texOffs(23, 11).addBox(-6.0f, 0.0f, 5.0f, 7.0f, 4.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.offset(3.0f, -8.0f, -2.0f));
        PartDefinition $$3 = $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 24).addBox(-2.5f, -3.01f, -1.0f, 5.0f, 4.0f, 7.0f, new CubeDeformation(0.0f)).texOffs(0, 35).addBox(-2.5f, -3.01f, 4.1f, 5.0f, 4.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.offset(0.5f, -5.0f, 3.0f));
        $$3.addOrReplaceChild("upper_mouth", CubeListBuilder.create().texOffs(24, 24).addBox(-2.5f, -1.0f, 0.0f, 5.0f, 2.0f, 2.0f, new CubeDeformation(-0.001f)), PartPose.offset(0.0f, -2.01f, 3.9f));
        $$3.addOrReplaceChild("inner_mouth", CubeListBuilder.create().texOffs(24, 32).addBox(-1.5f, -1.0f, -1.0f, 3.0f, 2.0f, 2.0f, new CubeDeformation(0.0f)), PartPose.offset(0.0f, -1.01f, 4.9f));
        $$3.addOrReplaceChild("lower_mouth", CubeListBuilder.create().texOffs(24, 28).addBox(-2.5f, -1.0f, 0.0f, 5.0f, 2.0f, 2.0f, new CubeDeformation(-0.001f)), PartPose.offset(0.0f, -0.01f, 3.9f));
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override public ModelPart root() { return this.root; }

    @Override
    public void setupAnim(AbstractNautilus entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        this.applyBodyRotation(headYaw, headPitch);
        this.animateWalk(NautilusAnimation.SWIMMING, limbSwing + ageInTicks / 5.0F, limbSwingAmount + 0.2F, 2.0F, 3.0F);
    }

    private void applyBodyRotation(float $$0, float $$1) {
        $$0 = Mth.clamp($$0, -10.0f, 10.0f);
        $$1 = Mth.clamp($$1, -10.0f, 10.0f);
        this.body.yRot = $$0 * ((float)Math.PI / 180);
        this.body.xRot = $$1 * ((float)Math.PI / 180);
    }
}

