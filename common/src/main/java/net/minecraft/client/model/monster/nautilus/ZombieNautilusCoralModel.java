package net.minecraft.client.model.monster.nautilus;

import net.minecraft.client.model.animal.nautilus.NautilusModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;

public class ZombieNautilusCoralModel
extends NautilusModel {
    private final ModelPart corals;

    public ZombieNautilusCoralModel(ModelPart $$0) {
        super($$0);
        ModelPart $$1 = this.nautilus.getChild("shell");
        this.corals = $$1.getChild("corals");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = ZombieNautilusCoralModel.createBodyMesh();
        PartDefinition $$1 = $$0.getRoot().getChild("root").getChild("shell").addOrReplaceChild("corals", CubeListBuilder.create(), PartPose.offset(8.0f, 4.5f, -8.0f));
        PartDefinition $$2 = $$1.addOrReplaceChild("yellow_coral", CubeListBuilder.create(), PartPose.offset(0.0f, -11.0f, 11.0f));
        $$2.addOrReplaceChild("yellow_coral_second", CubeListBuilder.create().texOffs(0, 85).addBox(-4.5f, -3.5f, 0.0f, 6.0f, 8.0f, 0.0f), PartPose.offsetAndRotation(0.0f, 0.0f, 2.0f, 0.0f, -0.7854f, 0.0f));
        $$2.addOrReplaceChild("yellow_coral_first", CubeListBuilder.create().texOffs(0, 85).addBox(-4.5f, -3.5f, 0.0f, 6.0f, 8.0f, 0.0f), PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.7854f, 0.0f));
        PartDefinition $$3 = $$1.addOrReplaceChild("pink_coral", CubeListBuilder.create().texOffs(-8, 94).addBox(-4.5f, 4.5f, 0.0f, 6.0f, 0.0f, 8.0f), PartPose.offset(-12.5f, -18.0f, 11.0f));
        $$3.addOrReplaceChild("pink_coral_second", CubeListBuilder.create().texOffs(-8, 94).addBox(-3.0f, 0.0f, -4.0f, 6.0f, 0.0f, 8.0f), PartPose.offsetAndRotation(-1.5f, 4.5f, 4.0f, 0.0f, 0.0f, 1.5708f));
        PartDefinition $$4 = $$1.addOrReplaceChild("blue_coral", CubeListBuilder.create(), PartPose.offset(-14.0f, 0.0f, 5.5f));
        $$4.addOrReplaceChild("blue_second", CubeListBuilder.create().texOffs(0, 102).addBox(-3.5f, -5.5f, 0.0f, 5.0f, 10.0f, 0.0f), PartPose.offsetAndRotation(0.0f, 0.0f, -2.0f, 0.0f, 0.7854f, 0.0f));
        $$4.addOrReplaceChild("blue_first", CubeListBuilder.create().texOffs(0, 102).addBox(-3.5f, -5.5f, 0.0f, 5.0f, 10.0f, 0.0f), PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, -0.7854f, 0.0f));
        PartDefinition $$5 = $$1.addOrReplaceChild("red_coral", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f));
        $$5.addOrReplaceChild("red_coral_second", CubeListBuilder.create().texOffs(0, 112).addBox(-2.5f, -5.5f, 0.0f, 4.0f, 10.0f, 0.0f), PartPose.offsetAndRotation(-0.5f, -1.0f, 1.5f, 0.0f, -0.829f, 0.0f));
        $$5.addOrReplaceChild("red_coral_first", CubeListBuilder.create().texOffs(0, 112).addBox(-4.5f, -5.5f, 0.0f, 6.0f, 10.0f, 0.0f), PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.7854f, 0.0f));
        return LayerDefinition.create($$0, 128, 128);
    }

    @Override
    public void setupAnim(AbstractNautilus entity, float limbSwing, float limbAmount, float age, float yaw, float pitch) {
        super.setupAnim(entity, limbSwing, limbAmount, age, yaw, pitch);
        this.corals.visible = entity.getBodyArmorItem().isEmpty();
    }
}

