package zzik2.barched.mixin.client.renderer.entity;

import net.minecraft.client.model.CamelModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.CamelRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CamelSaddleGlintLayer;
import net.minecraft.world.entity.animal.camel.Camel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CamelRenderer.class)
public abstract class CamelRendererMixin extends MobRenderer<Camel, CamelModel<Camel>> {
    protected CamelRendererMixin(EntityRendererProvider.Context context, CamelModel<Camel> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void barched$addSaddleFoil(
            EntityRendererProvider.Context context,
            ModelLayerLocation modelLayerLocation,
            CallbackInfo ci
    ) {
        this.addLayer(new CamelSaddleGlintLayer(this, context.getModelSet()));
    }
}
