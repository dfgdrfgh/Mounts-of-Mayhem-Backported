package zzik2.mombackport.mixin.advancements.critereon;

import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zzik2.mombackport.bridge.advancements.critereon.EntityFlagsPredicate$BuilderBridge;
import zzik2.mombackport.bridge.advancements.critereon.EntityFlagsPredicateBridge;

import java.util.Optional;

@Mixin(EntityFlagsPredicate.Builder.class)
public abstract class EntityFlagsPredicate$BuilderMixin implements EntityFlagsPredicate$BuilderBridge {

    @Unique private Optional<Boolean> mombackport$isInWater = Optional.empty();

    @Unique private Optional<Boolean> mombackport$isFallFlying = Optional.empty();

    @Override
    public EntityFlagsPredicate.Builder setIsInWater(Boolean value) {
        this.mombackport$isInWater = Optional.of(value);
        return (EntityFlagsPredicate.Builder) (Object) this;
    }

    @Override
    public EntityFlagsPredicate.Builder setIsFallFlying(Boolean value) {
        this.mombackport$isFallFlying = Optional.of(value);
        return (EntityFlagsPredicate.Builder) (Object) this;
    }

    @Inject(method = "build", at = @At("RETURN"))
    private void mombackport$transferExtras(CallbackInfoReturnable<EntityFlagsPredicate> cir) {
        EntityFlagsPredicateBridge bridge = (EntityFlagsPredicateBridge) (Object) cir.getReturnValue();
        bridge.mombackport$setIsInWater(this.mombackport$isInWater);
        bridge.mombackport$setIsFallFlying(this.mombackport$isFallFlying);
    }
}
