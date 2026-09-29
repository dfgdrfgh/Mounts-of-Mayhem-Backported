package zzik2.barched.mixin.entity.ambient;

import net.minecraft.world.entity.ambient.Bat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Bat.class)
public abstract class BatMixin {

    @Inject(method = "isHalloween", at = @At("HEAD"), cancellable = true)
    private static void barched$removeSeasonalSpawnBoost(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
