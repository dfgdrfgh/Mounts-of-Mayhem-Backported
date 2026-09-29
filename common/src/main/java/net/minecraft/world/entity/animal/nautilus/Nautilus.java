package net.minecraft.world.entity.animal.nautilus;

import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import zzik2.barched.Barched;


import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.animal.nautilus.NautilusAi;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Nautilus
extends AbstractNautilus {
    private static final int NAUTILUS_TOTAL_AIR_SUPPLY = 300;

    public Nautilus(EntityType<? extends Nautilus> $$0, Level $$1) {
        super((EntityType<? extends AbstractNautilus>)$$0, $$1);
    }

    protected Brain.Provider<Nautilus> brainProvider() {
        return NautilusAi.brainProvider();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> $$0) {
        return NautilusAi.makeBrain(this.brainProvider().makeBrain($$0));
    }

    public Brain<Nautilus> getBrain() {
        return (Brain<Nautilus>)super.getBrain();
    }

    @Override
    public @Nullable Nautilus getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        Nautilus $$2 = Barched.EntityType.NAUTILUS.create($$0);
        if ($$2 != null && this.isTame()) {
            $$2.setOwnerUUID(this.getOwnerUUID());
            $$2.setTame(true, true);
        }
        return $$2;
    }

    @Override
    protected void customServerAiStep() {
        net.minecraft.util.profiling.ProfilerFiller $$1 = this.level().getProfiler();
        $$1.push("nautilusBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        $$1.pop();
        $$1.push("nautilusActivityUpdate");
        NautilusAi.updateActivity(this);
        $$1.pop();
        super.customServerAiStep();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isBaby()) {
            return this.isUnderWater() ? Barched.SoundEvents.BABY_NAUTILUS_AMBIENT : Barched.SoundEvents.BABY_NAUTILUS_AMBIENT_ON_LAND;
        }
        return this.isUnderWater() ? Barched.SoundEvents.NAUTILUS_AMBIENT : Barched.SoundEvents.NAUTILUS_AMBIENT_ON_LAND;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        if (this.isBaby()) {
            return this.isUnderWater() ? Barched.SoundEvents.BABY_NAUTILUS_HURT : Barched.SoundEvents.BABY_NAUTILUS_HURT_ON_LAND;
        }
        return this.isUnderWater() ? Barched.SoundEvents.NAUTILUS_HURT : Barched.SoundEvents.NAUTILUS_HURT_ON_LAND;
    }

    @Override
    protected SoundEvent getDeathSound() {
        if (this.isBaby()) {
            return this.isUnderWater() ? Barched.SoundEvents.BABY_NAUTILUS_DEATH : Barched.SoundEvents.BABY_NAUTILUS_DEATH_ON_LAND;
        }
        return this.isUnderWater() ? Barched.SoundEvents.NAUTILUS_DEATH : Barched.SoundEvents.NAUTILUS_DEATH_ON_LAND;
    }

    @Override
    protected SoundEvent getDashSound() {
        return this.isUnderWater() ? Barched.SoundEvents.NAUTILUS_DASH : Barched.SoundEvents.NAUTILUS_DASH_ON_LAND;
    }

    @Override
    protected SoundEvent getDashReadySound() {
        return this.isUnderWater() ? Barched.SoundEvents.NAUTILUS_DASH_READY : Barched.SoundEvents.NAUTILUS_DASH_READY_ON_LAND;
    }

    @Override
    protected void playEatingSound() {
        SoundEvent $$0 = this.isBaby() ? Barched.SoundEvents.BABY_NAUTILUS_EAT : Barched.SoundEvents.NAUTILUS_EAT;
        this.makeSound($$0);
    }

    @Override
    protected SoundEvent getSwimSound() {
        return this.isBaby() ? Barched.SoundEvents.BABY_NAUTILUS_SWIM : Barched.SoundEvents.NAUTILUS_SWIM;
    }

    @Override
    public int getMaxAirSupply() {
        return 300;
    }

    protected void handleAirSupply(ServerLevel $$0, int $$1) {
        if (this.isAlive() && !this.isInWater()) {
            this.setAirSupply($$1 - 1);
            if (this.getAirSupply() <= -20) {
                this.setAirSupply(0);
                this.hurt(this.damageSources().dryOut(), 2.0f);
            }
        } else {
            this.setAirSupply(300);
        }
    }

    @Override
    public void baseTick() {
        Level level;
        int $$0 = this.getAirSupply();
        super.baseTick();
        if (!this.isNoAi() && (level = this.level()) instanceof ServerLevel) {
            ServerLevel $$1 = (ServerLevel)level;
            this.handleAirSupply($$1, $$0);
        }
    }

    @Override
    public boolean canBeLeashed() {
        return !this.isAggravated();
    }

}

