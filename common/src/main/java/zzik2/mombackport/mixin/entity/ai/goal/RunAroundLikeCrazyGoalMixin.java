package zzik2.mombackport.mixin.entity.ai.goal;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import zzik2.mombackport.bridge.entity.AbstractHorseBridge;

@Mixin(RunAroundLikeCrazyGoal.class)
public abstract class RunAroundLikeCrazyGoalMixin implements AbstractHorseBridge {

    @Shadow @Final private AbstractHorse horse;

    @ModifyExpressionValue(method = "canUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;isVehicle()Z", ordinal = 0))
    private boolean mombackport$canUse(boolean original) {
        return original && !((AbstractHorseBridge) this.horse).isMobControlled();
    }
}
