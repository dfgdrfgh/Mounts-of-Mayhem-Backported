package zzik2.barched.mixin.entity.monster;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.monster.Drowned;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.entity.monster.Drowned$DrownedGoToBeachGoal")
public abstract class DrownedGoToBeachGoalMixin extends MoveToBlockGoal {
    @Shadow @Final private Drowned drowned;

    protected DrownedGoToBeachGoalMixin(PathfinderMob mob, double speed, int range, int verticalRange) {
        super(mob, speed, range, verticalRange);
    }

    @Inject(method = "start", at = @At("HEAD"), cancellable = true)
    private void barched$keepAmphibiousNavigation(CallbackInfo ci) {
        // The 1.21.1 goal installs groundNavigation here. In 1.21.11 the
        // amphibious navigator is retained, including after leaving a beach.
        this.drowned.setSearchingForLand(false);
        super.start();
        ci.cancel();
    }
}
