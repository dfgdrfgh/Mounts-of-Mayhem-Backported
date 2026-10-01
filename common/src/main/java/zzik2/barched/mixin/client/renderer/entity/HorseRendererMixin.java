package zzik2.barched.mixin.client.renderer.entity;

import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.client.renderer.entity.layers.UndeadHorseArmorLayer;
import net.minecraft.client.renderer.entity.layers.UndeadHorseSaddleGlintLayer;
import net.minecraft.world.entity.animal.horse.Horse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HorseRenderer.class)
public abstract class HorseRendererMixin extends AbstractHorseRenderer<Horse, HorseModel<Horse>> {
    protected HorseRendererMixin(EntityRendererProvider.Context context, HorseModel<Horse> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void barched$replaceHorseEquipmentLayers(EntityRendererProvider.Context context, CallbackInfo ci) {
        // 1.21.11 horse armor uses the equipment/horse_body textures and layered leather rendering.
        // Remove the 1.21.1 layer so it cannot double-render or use the legacy horse armor texture path.
        this.layers.removeIf(layer -> layer instanceof HorseArmorLayer);
        this.addLayer(new UndeadHorseArmorLayer<>(this, context.getModelSet()));
        this.addLayer(new UndeadHorseSaddleGlintLayer<>(this, context.getModelSet(), ModelLayers.HORSE));
    }
}
