package net.minecraft.client.renderer.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.CamelHuskModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.CamelHusk;
import net.minecraft.client.renderer.entity.layers.CamelHuskSaddleLayer;

@Environment(EnvType.CLIENT)
public class CamelHuskRenderer extends MobRenderer<CamelHusk, CamelHuskModel<CamelHusk>> {

    private static final ResourceLocation CAMEL_HUSK_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/camel/camel_husk.png");

    public CamelHuskRenderer(EntityRendererProvider.Context context, ModelLayerLocation modelLayerLocation) {
        super(context, new CamelHuskModel<>(context.bakeLayer(modelLayerLocation)), 0.7F);
        this.addLayer(new CamelHuskSaddleLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(CamelHusk  camel) {
        return CAMEL_HUSK_LOCATION;
    }
}
