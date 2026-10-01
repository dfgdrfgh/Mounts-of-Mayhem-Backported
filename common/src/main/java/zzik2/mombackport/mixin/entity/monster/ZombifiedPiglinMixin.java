package zzik2.mombackport.mixin.entity.monster;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.SpearUseGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.mombackport.MomBackport;

@Mixin(ZombifiedPiglin.class)
public class ZombifiedPiglinMixin extends Zombie {

    @Unique private RandomSource mombackport$randomSource;

    public ZombifiedPiglinMixin(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "addBehaviourGoals", at = @At("HEAD"))
    private void mombackport$addBehaviourGoals(CallbackInfo ci) {
        this.goalSelector.addGoal(1, new SpearUseGoal(this, 1.0D, 1.0D, 10.0F, 2.0F));
    }

    @Inject(method = "populateDefaultEquipmentSlots", at = @At("HEAD"), order = 2000)
    private void mombackport$capture(RandomSource randomSource, DifficultyInstance difficultyInstance, CallbackInfo ci) {
        this.mombackport$randomSource = randomSource;
    }

    @ModifyArg(method = "populateDefaultEquipmentSlots", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;<init>(Lnet/minecraft/world/level/ItemLike;)V", ordinal = 0), index = 0)
    private ItemLike mombackport$Item(ItemLike arg) {
        return mombackport$randomSource.nextFloat() < MomBackport.getConfig().getZombifiedPiglinSpearSpawnChanceAsFloat() ? MomBackport.Items.GOLDEN_SPEAR : arg;
    }
}
