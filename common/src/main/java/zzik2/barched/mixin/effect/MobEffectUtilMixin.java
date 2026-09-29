package zzik2.barched.mixin.effect;

import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zzik2.barched.Barched;

@Mixin(MobEffectUtil.class)
public abstract class MobEffectUtilMixin {
    @Inject(method = "hasWaterBreathing", at = @At("RETURN"), cancellable = true)
    private static void barched$breathOfTheNautilusCountsAsWaterBreathing(
            LivingEntity entity,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!cir.getReturnValue() && entity.hasEffect(Barched.MobEffects.BREATH_OF_THE_NAUTILUS)) {
            cir.setReturnValue(true);
        }
    }
}
