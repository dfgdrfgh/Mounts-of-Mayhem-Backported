package zzik2.barched.mixin.client.renderer.entity;

import net.minecraft.client.model.ChestedHorseModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.ChestedHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.UndeadHorseSaddleGlintLayer;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestedHorseRenderer.class)
public abstract class ChestedHorseRendererMixin<T extends AbstractChestedHorse>
        extends AbstractHorseRenderer<T, ChestedHorseModel<T>> {

    protected ChestedHorseRendererMixin(EntityRendererProvider.Context context, ChestedHorseModel<T> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void barched$addSaddleFoil(
            EntityRendererProvider.Context context,
            float shadowRadius,
            ModelLayerLocation modelLayerLocation,
            CallbackInfo ci
    ) {
        this.addLayer(new UndeadHorseSaddleGlintLayer<>(this, context.getModelSet(), modelLayerLocation));
    }
}
