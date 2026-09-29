package zzik2.barched.mixin.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import zzik2.barched.Barched;

@Mixin(Drowned.class)
public abstract class DrownedMixin extends Zombie {

    public DrownedMixin(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    // 1.21.11 uses one amphibious navigator and keeps mounted drowned upright.
    @org.spongepowered.asm.mixin.injection.Inject(method = "<init>", at = @org.spongepowered.asm.mixin.injection.At("TAIL"))
    private void barched$amphibiousNavigation(EntityType<? extends Drowned> type, Level level, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        this.navigation = new net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation(this, level);
        this.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.STEP_HEIGHT).setBaseValue(1.0D);
    }

    @org.spongepowered.asm.mixin.Shadow abstract boolean wantsToSwim();

    @org.spongepowered.asm.mixin.injection.Inject(method = "updateSwimming", at = @org.spongepowered.asm.mixin.injection.At("HEAD"), cancellable = true)
    private void barched$updateSwimming(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (!this.level().isClientSide()) this.setSwimming(this.isEffectiveAi() && this.isUnderWater() && this.wantsToSwim());
        ci.cancel();
    }

    @com.llamalad7.mixinextras.injector.ModifyReturnValue(method = "isVisuallySwimming", at = @org.spongepowered.asm.mixin.injection.At("RETURN"))
    private boolean barched$keepRiderUpright(boolean original) { return original && !this.isPassenger(); }

    @Override
    public boolean wantsToPickUp(ItemStack itemStack) {
        return itemStack.is(Barched.ItemTags.SPEARS) ? false : super.wantsToPickUp(itemStack);
    }

    @org.spongepowered.asm.mixin.injection.Inject(method = "finalizeSpawn", at = @org.spongepowered.asm.mixin.injection.At("RETURN"))
    private void barched$spawnZombieNautilus(net.minecraft.world.level.ServerLevelAccessor level, net.minecraft.world.DifficultyInstance difficulty, net.minecraft.world.entity.MobSpawnType reason, net.minecraft.world.entity.SpawnGroupData group, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<net.minecraft.world.entity.SpawnGroupData> cir) {
        if ((reason == net.minecraft.world.entity.MobSpawnType.NATURAL || reason == net.minecraft.world.entity.MobSpawnType.STRUCTURE)
                && this.getMainHandItem().is(net.minecraft.world.item.Items.TRIDENT) && level.getRandom().nextFloat() < 0.5F
                && !this.isBaby() && !level.getBiome(this.blockPosition()).is(net.minecraft.tags.BiomeTags.MORE_FREQUENT_DROWNED_SPAWNS)) {
            net.minecraft.world.entity.animal.nautilus.ZombieNautilus mount = Barched.EntityType.ZOMBIE_NAUTILUS.create(this.level());
            if (mount != null) {
                if (reason == net.minecraft.world.entity.MobSpawnType.STRUCTURE) mount.setPersistenceRequired();
                mount.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                mount.finalizeSpawn(level, difficulty, reason, null);
                this.startRiding(mount, false);
                level.addFreshEntity(mount);
            }
        }
    }
}
