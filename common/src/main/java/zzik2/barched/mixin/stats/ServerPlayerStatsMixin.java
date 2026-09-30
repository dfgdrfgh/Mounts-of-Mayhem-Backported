package zzik2.barched.mixin.stats;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.barched.stat.BarchedStats;

@Mixin(ServerPlayer.class)
public class ServerPlayerStatsMixin {
    @Inject(method = "checkRidingStatistics", at = @At("TAIL"))
    private void barched$awardNautilusDistance(double x, double y, double z, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer)(Object)this;
        if (player.isPassenger()
                && (x != 0.0D || y != 0.0D || z != 0.0D)
                && player.getVehicle() instanceof AbstractNautilus) {
            int distance = Math.round((float)Math.sqrt(x * x + y * y + z * z) * 100.0F);
            player.awardStat(Stats.CUSTOM.get(BarchedStats.NAUTILUS_ONE_CM), distance);
        }
    }
}
