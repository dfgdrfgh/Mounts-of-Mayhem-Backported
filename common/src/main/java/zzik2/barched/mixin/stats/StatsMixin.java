package zzik2.barched.mixin.stats;

import net.minecraft.stats.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.barched.stat.BarchedStats;

@Mixin(Stats.class)
public class StatsMixin {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void barched$registerNautilusDistanceStat(CallbackInfo ci) {
        BarchedStats.register();
    }
}
