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
