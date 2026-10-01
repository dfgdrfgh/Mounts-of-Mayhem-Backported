package zzik2.mombackport.mixin.client.model.geom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zzik2.mombackport.MomBackportClient;

import java.util.Map;

@Mixin(LayerDefinitions.class)
public class LayerDefinitionsMixin {

    @Shadow @Final private static CubeDeformation OUTER_ARMOR_DEFORMATION;
    @Shadow @Final private static CubeDeformation INNER_ARMOR_DEFORMATION;

    @Unique
    private static ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> mombackport$builder;

    @Redirect(method = "createRoots", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap;builder()Lcom/google/common/collect/ImmutableMap$Builder;"))
    private static ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> mombackport$captureBuilder() {
        mombackport$builder = ImmutableMap.builder();
        return mombackport$builder;
    }

    @Inject(method = "createRoots", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;", shift = At.Shift.AFTER, ordinal = 84))
    private static void mombackport$createRoots(CallbackInfoReturnable<Map<ModelLayerLocation, LayerDefinition>> cir) {
        LayerDefinition layerDefinition2 = LayerDefinition.create(HumanoidArmorModel.createBodyLayer(OUTER_ARMOR_DEFORMATION), 64, 32);
        LayerDefinition layerDefinition4 = LayerDefinition.create(HumanoidArmorModel.createBodyLayer(INNER_ARMOR_DEFORMATION), 64, 32);

        mombackport$builder.put(MomBackportClient.ModelLayers.PARCHED, MomBackportClient.SkeletonModel.createSingleModelDualBodyLayer());
        mombackport$builder.put(MomBackportClient.ModelLayers.PARCHED_INNER_ARMOR, layerDefinition4);
        mombackport$builder.put(MomBackportClient.ModelLayers.PARCHED_OUTER_ARMOR, layerDefinition2);
        mombackport$builder.put(MomBackportClient.ModelLayers.PARCHED_OUTER_LAYER, LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.25F), 0.0F), 64, 32));
    }
}
