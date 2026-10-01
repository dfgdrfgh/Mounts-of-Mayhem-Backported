package zzik2.mombackport.mixin.advancements.critereon;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zzik2.mombackport.bridge.advancements.critereon.EntityFlagsPredicateBridge;
import zzik2.zreflex.mixin.ModifyAccess;

import java.util.Optional;

@Mixin(EntityFlagsPredicate.class)
public abstract class EntityFlagsPredicateMixin implements EntityFlagsPredicateBridge {

    @ModifyAccess(access = Opcodes.ACC_PUBLIC, removeFinal = true)
    @Shadow
    public static Codec<EntityFlagsPredicate> CODEC;

    @Unique private Optional<Boolean> mombackport$isInWater = Optional.empty();

    @Unique private Optional<Boolean> mombackport$isFallFlying = Optional.empty();

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void mombackport$extendCodec(CallbackInfo ci) {
        MapCodec<EntityFlagsPredicate> baseMap = ((MapCodec.MapCodecCodec<EntityFlagsPredicate>) CODEC).codec();

        MapCodec<Pair<Optional<Boolean>, Optional<Boolean>>> extrasMap = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("is_in_water").forGetter(Pair::getFirst),
                Codec.BOOL.optionalFieldOf("is_fall_flying").forGetter(Pair::getSecond)
        ).apply(instance, Pair::of));

        MapCodec<EntityFlagsPredicate> combined = Codec.mapPair(baseMap, extrasMap).xmap(
                pair -> {
                    EntityFlagsPredicate predicate = pair.getFirst();
                    Pair<Optional<Boolean>, Optional<Boolean>> extras = pair.getSecond();
                    EntityFlagsPredicateBridge bridge = (EntityFlagsPredicateBridge) (Object) predicate;
                    bridge.mombackport$setIsInWater(extras.getFirst());
                    bridge.mombackport$setIsFallFlying(extras.getSecond());
                    return predicate;
                },
                predicate -> {
                    EntityFlagsPredicateBridge bridge = (EntityFlagsPredicateBridge) (Object) predicate;
                    return Pair.of(predicate, Pair.of(bridge.mombackport$isInWater(), bridge.mombackport$isFallFlying()));
                }
        );

        CODEC = combined.codec();
    }

    @Inject(method = "matches", at = @At("RETURN"), cancellable = true)
    private void mombackport$matches(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            return;
        }

        if (this.mombackport$isInWater.isPresent() && entity.isInWater() != this.mombackport$isInWater.get()) {
            cir.setReturnValue(false);
            return;
        }

        if (this.mombackport$isFallFlying.isPresent() && entity instanceof LivingEntity livingEntity) {
            if (livingEntity.isFallFlying() != this.mombackport$isFallFlying.get()) {
                cir.setReturnValue(false);
            }
        }
    }

    @Override
    public Optional<Boolean> mombackport$isInWater() {
        return this.mombackport$isInWater;
    }

    @Override
    public Optional<Boolean> mombackport$isFallFlying() {
        return this.mombackport$isFallFlying;
    }

    @Override
    public void mombackport$setIsInWater(Optional<Boolean> value) {
        this.mombackport$isInWater = value;
    }

    @Override
    public void mombackport$setIsFallFlying(Optional<Boolean> value) {
        this.mombackport$isFallFlying = value;
    }
}
