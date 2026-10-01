package zzik2.barched.mixin.entity.vehicle;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Boat.class)
public class BoatMixin {

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;startRiding(Lnet/minecraft/world/entity/Entity;)Z"
            )
    )
    private boolean barched$preventNautilusAutoBoarding(Entity passenger, Entity vehicle) {
        if (passenger instanceof AbstractNautilus) {
            vehicle.push(passenger);
            return false;
        }
        return passenger.startRiding(vehicle);
    }
}
