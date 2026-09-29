package zzik2.barched.mixin.entity.monster;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.SpearUseGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.barched.Barched;

@Mixin(Zombie.class)
public class ZombieMixin extends Monster {

    protected ZombieMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "addBehaviourGoals", at = @At("HEAD"))
    private void barched$addBehaviourGoals(CallbackInfo ci) {
        this.goalSelector.addGoal(2, new SpearUseGoal(this, 1.0D, 1.0D, 10.0F, 2.0F));
    }

    // 2 -> 3
    @ModifyArg(method = "addBehaviourGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 0))
    private int barched$addBehaviourGoals(int i) {
        return 3;
    }

    @ModifyArg(method = "populateDefaultEquipmentSlots", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"), index = 0)
    private int barched$nextInt(int i) {
        if (i == 3) {
            return 6;
        }
        return i;
    }

    @ModifyArg(method = "populateDefaultEquipmentSlots", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;<init>(Lnet/minecraft/world/level/ItemLike;)V", ordinal = 1), index = 0)
    private ItemLike barched$useSpearForEquipmentRoll(ItemLike original, @Local(ordinal = 0) int i) {
        return i == 1 ? Barched.Items.IRON_SPEAR : original;
    }

    @Inject(method = "populateDefaultEquipmentSlots", at = @At("TAIL"))
    private void barched$overrideSpearByConfig(RandomSource randomSource, DifficultyInstance difficultyInstance, CallbackInfo ci) {
        float overrideChance = Barched.getConfig().getZombieOverrideSpearSpawnChanceAsFloat();
        if (overrideChance > 0.0F && randomSource.nextFloat() < overrideChance) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Barched.Items.IRON_SPEAR));
        }
    }
}
