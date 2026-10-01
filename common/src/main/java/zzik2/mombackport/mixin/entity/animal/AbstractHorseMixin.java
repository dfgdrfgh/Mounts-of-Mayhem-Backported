package zzik2.mombackport.mixin.entity.animal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import zzik2.mombackport.MomBackport;
import zzik2.mombackport.bridge.entity.AbstractHorseBridge;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin implements AbstractHorseBridge {

    @Redirect(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V"))
    private void mombackport$modifyGoal(GoalSelector instance, int i, Goal arg) {
        if (arg instanceof PanicGoal) {
            instance.addGoal(i, new MomBackport.MountPanicGoal((AbstractHorse) (Object) this, 1.2D));
            return;
        }
        instance.addGoal(i, arg);
    }

    @Override
    public boolean isMobControlled() {
        return false;
    }
}
