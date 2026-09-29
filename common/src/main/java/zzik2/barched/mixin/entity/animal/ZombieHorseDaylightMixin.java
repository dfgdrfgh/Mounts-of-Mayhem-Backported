package zzik2.barched.mixin.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieHorse.class)
public abstract class ZombieHorseDaylightMixin extends AbstractHorse {

    protected ZombieHorseDaylightMixin(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void barched$match12111DaylightBurning(CallbackInfo ci) {
        if (!this.isAlive() || !this.barched$isAffectedByDaylight()) {
            return;
        }

        // Mounts of Mayhem makes Horse Armor act as the Zombie Horse's
        // daylight protection. Horse armor is stored in the BODY slot.
        if (this.getBodyArmorItem().isEmpty()) {
            this.igniteForSeconds(8.0F);
        }
    }

    @Unique
    private boolean barched$isAffectedByDaylight() {
        if (this.level().isClientSide() || !this.level().isDay()) {
            return false;
        }

        float brightness = this.getLightLevelDependentMagicValue();
        if (brightness <= 0.5F) {
            return false;
        }

        BlockPos eyePos = BlockPos.containing(this.getX(), this.getEyeY(), this.getZ());
        return this.random.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F
                && !this.isInWaterRainOrBubble()
                && this.level().canSeeSky(eyePos);
    }
}
