package zzik2.barched.mixin.entity.monster;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.SpearUseGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.barched.Barched;

@Mixin(Zombie.class)
public class ZombieMixin extends Monster {

    @Unique
    private int barched$equipmentRoll = -1;

    protected ZombieMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "addBehaviourGoals", at = @At("HEAD"))
    private void barched$addBehaviourGoals(CallbackInfo ci) {
        this.goalSelector.addGoal(2, new SpearUseGoal(this, 1.0D, 1.0D, 10.0F, 2.0F));
    }

    // 1.21.11 moves ZombieAttackGoal from priority 2 to 3 so spear use can occupy priority 2.
    @ModifyArg(
            method = "addBehaviourGoals",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",
                    ordinal = 0
            )
    )
    private int barched$moveMeleeGoalToPriorityThree(int priority) {
        return 3;
    }

    @Inject(method = "populateDefaultEquipmentSlots", at = @At("HEAD"))
    private void barched$resetEquipmentRoll(RandomSource randomSource, DifficultyInstance difficultyInstance, CallbackInfo ci) {
        this.barched$equipmentRoll = -1;
    }

    /**
     * 1.21.1 rolls 0..2: 0 = sword, 1/2 = shovel.
     * 1.21.11 rolls 0..5: 0 = sword, 1 = spear, 2..5 = shovel.
     *
     * Capture the generated value directly instead of relying on MixinExtras local capture;
     * the latter is not stable against the 1.21.1 bytecode and prevented the client from starting.
     */
    @Redirect(
            method = "populateDefaultEquipmentSlots",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I")
    )
    private int barched$expandAndRememberEquipmentRoll(RandomSource randomSource, int bound) {
        int roll = randomSource.nextInt(bound == 3 ? 6 : bound);
        this.barched$equipmentRoll = roll;
        return roll;
    }

    @ModifyArg(
            method = "populateDefaultEquipmentSlots",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;<init>(Lnet/minecraft/world/level/ItemLike;)V",
                    ordinal = 1
            ),
            index = 0
    )
    private ItemLike barched$useSpearForEquipmentRoll(ItemLike original) {
        return this.barched$equipmentRoll == 1 ? Barched.Items.IRON_SPEAR : original;
    }

    @Inject(method = "populateDefaultEquipmentSlots", at = @At("TAIL"))
    private void barched$overrideSpearByConfig(RandomSource randomSource, DifficultyInstance difficultyInstance, CallbackInfo ci) {
        float overrideChance = Barched.getConfig().getZombieOverrideSpearSpawnChanceAsFloat();
        if (overrideChance > 0.0F && randomSource.nextFloat() < overrideChance) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Barched.Items.IRON_SPEAR));
        }
    }
}
