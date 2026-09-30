package net.minecraft.world.entity.animal.nautilus;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.level.*;
import org.jetbrains.annotations.Nullable;
import zzik2.barched.Barched;
import zzik2.barched.bridge.entity.MobBridge;
import zzik2.barched.nautilus.ZombieNautilusVariant;

public class ZombieNautilus extends AbstractNautilus implements MobBridge {
    private static final EntityDataAccessor<String> VARIANT = SynchedEntityData.defineId(ZombieNautilus.class, EntityDataSerializers.STRING);

    public ZombieNautilus(EntityType<? extends ZombieNautilus> type, Level level) { super(type, level); }
    public static AttributeSupplier.Builder createAttributes() { return AbstractNautilus.createAttributes().add(Attributes.MOVEMENT_SPEED, 1.1F); }
    @Override public @Nullable ZombieNautilus getBreedOffspring(ServerLevel level, AgeableMob other) { return null; }
    @Override public boolean isBaby() { return false; }
    @Override public EquipmentSlot sunProtectionSlot() { return EquipmentSlot.BODY; }
    @Override protected Brain.Provider<ZombieNautilus> brainProvider() { return ZombieNautilusAi.brainProvider(); }
    @Override protected Brain<?> makeBrain(Dynamic<?> data) { return ZombieNautilusAi.makeBrain(this.brainProvider().makeBrain(data)); }
    @Override public Brain<ZombieNautilus> getBrain() { return (Brain<ZombieNautilus>)super.getBrain(); }
    @Override protected void customServerAiStep() {
        net.minecraft.util.profiling.ProfilerFiller profiler = this.level().getProfiler();
        profiler.push("zombieNautilusBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        profiler.pop();
        profiler.push("zombieNautilusActivityUpdate");
        ZombieNautilusAi.updateActivity(this);
        profiler.pop();
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
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, ZombieNautilusVariant.defaultVariant(this.registryAccess()).unwrapKey().orElseThrow().location().toString());
    }
    public Holder<ZombieNautilusVariant> getVariant() {
        ResourceKey<ZombieNautilusVariant> key = ResourceKey.create(ZombieNautilusVariant.REGISTRY, ResourceLocation.parse(this.entityData.get(VARIANT)));
        return this.registryAccess().registryOrThrow(ZombieNautilusVariant.REGISTRY).getHolder(key)
                .<Holder<ZombieNautilusVariant>>map(holder -> holder)
                .orElseGet(() -> ZombieNautilusVariant.defaultVariant(this.registryAccess()));
    }
    public void setVariant(Holder<ZombieNautilusVariant> variant) {
        this.entityData.set(VARIANT, variant.unwrapKey().orElseThrow().location().toString());
    }
    public boolean isCoral() { return this.getVariant().value().model() == ZombieNautilusVariant.ModelType.WARM; }
    public void setCoral(boolean value) {
        this.registryAccess().registryOrThrow(ZombieNautilusVariant.REGISTRY)
                .getHolder(value ? ZombieNautilusVariant.WARM : ZombieNautilusVariant.TEMPERATE).ifPresent(this::setVariant);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ResourceLocation id = ResourceLocation.tryParse(tag.getString("variant"));
        if (id != null) this.registryAccess().registryOrThrow(ZombieNautilusVariant.REGISTRY)
                .getHolder(ResourceKey.create(ZombieNautilusVariant.REGISTRY, id)).ifPresent(this::setVariant);
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("variant", this.getVariant().unwrapKey().orElseThrow().location().toString());
    }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData group) {
        ZombieNautilusVariant.select(level, this.blockPosition()).ifPresent(this::setVariant);
        return super.finalizeSpawn(level, difficulty, reason, group);
    }
    @Override public boolean canBeLeashed() { return !this.isAggravated() && !this.isMobControlled(); }
}
