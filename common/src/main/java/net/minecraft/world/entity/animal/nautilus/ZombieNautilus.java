package net.minecraft.world.entity.animal.nautilus;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biomes;
import org.jetbrains.annotations.Nullable;
import zzik2.barched.Barched;
import zzik2.barched.bridge.entity.MobBridge;

public class ZombieNautilus extends AbstractNautilus implements MobBridge {
    private static final EntityDataAccessor<Boolean> CORAL = SynchedEntityData.defineId(ZombieNautilus.class, EntityDataSerializers.BOOLEAN);

    public ZombieNautilus(EntityType<? extends ZombieNautilus> type, Level level) { super(type, level); }
    public static AttributeSupplier.Builder createAttributes() { return AbstractNautilus.createAttributes().add(Attributes.MOVEMENT_SPEED, 1.1F); }
    @Override public @Nullable ZombieNautilus getBreedOffspring(ServerLevel level, AgeableMob other) { return null; }
    @Override public boolean canFallInLove() { return false; }
    @Override public boolean isBaby() { return false; }
    @Override public EquipmentSlot sunProtectionSlot() { return EquipmentSlot.BODY; }
    @Override protected Brain.Provider<ZombieNautilus> brainProvider() { return ZombieNautilusAi.brainProvider(); }
    @Override protected Brain<?> makeBrain(Dynamic<?> data) { return ZombieNautilusAi.makeBrain(this.brainProvider().makeBrain(data)); }
    @Override public Brain<ZombieNautilus> getBrain() { return (Brain<ZombieNautilus>)super.getBrain(); }
    @Override protected void customServerAiStep() {
        this.getBrain().tick((ServerLevel)this.level(), this);
        ZombieNautilusAi.updateActivity(this);
        super.customServerAiStep();
    }
    @Override public void aiStep() { super.aiStep(); this.burnUndead(); }
    @Override protected SoundEvent getAmbientSound() { return this.isUnderWater() ? Barched.SoundEvents.ZOMBIE_NAUTILUS_AMBIENT : Barched.SoundEvents.ZOMBIE_NAUTILUS_AMBIENT_LAND; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return this.isUnderWater() ? Barched.SoundEvents.ZOMBIE_NAUTILUS_HURT : Barched.SoundEvents.ZOMBIE_NAUTILUS_HURT_LAND; }
    @Override protected SoundEvent getDeathSound() { return this.isUnderWater() ? Barched.SoundEvents.ZOMBIE_NAUTILUS_DEATH : Barched.SoundEvents.ZOMBIE_NAUTILUS_DEATH_LAND; }
    @Override protected SoundEvent getDashSound() { return this.isUnderWater() ? Barched.SoundEvents.ZOMBIE_NAUTILUS_DASH : Barched.SoundEvents.ZOMBIE_NAUTILUS_DASH_LAND; }
    @Override protected SoundEvent getDashReadySound() { return this.isUnderWater() ? Barched.SoundEvents.ZOMBIE_NAUTILUS_DASH_READY : Barched.SoundEvents.ZOMBIE_NAUTILUS_DASH_READY_LAND; }
    @Override protected void playEatingSound() { this.makeSound(Barched.SoundEvents.ZOMBIE_NAUTILUS_EAT); }
    @Override protected SoundEvent getSwimSound() { return Barched.SoundEvents.ZOMBIE_NAUTILUS_SWIM; }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(CORAL, false); }
    public boolean isCoral() { return this.entityData.get(CORAL); }
    public void setCoral(boolean value) { this.entityData.set(CORAL, value); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); this.setCoral("minecraft:warm".equals(tag.getString("variant"))); }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putString("variant", this.isCoral() ? "minecraft:warm" : "minecraft:temperate"); }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData group) {
        this.setCoral(level.getBiome(this.blockPosition()).is(Biomes.WARM_OCEAN));
        return super.finalizeSpawn(level, difficulty, reason, group);
    }
    @Override public boolean canBeLeashed() { return !this.isAggravated() && !this.isMobControlled(); }
}
