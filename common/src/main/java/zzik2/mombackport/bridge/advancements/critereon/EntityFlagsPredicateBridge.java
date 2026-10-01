package zzik2.mombackport.bridge.advancements.critereon;

import java.util.Optional;

public interface EntityFlagsPredicateBridge {

    default Optional<Boolean> mombackport$isInWater() {
        return Optional.empty();
    }

    default Optional<Boolean> mombackport$isFallFlying() {
        return Optional.empty();
    }

    default void mombackport$setIsInWater(Optional<Boolean> value) {
    }

    default void mombackport$setIsFallFlying(Optional<Boolean> value) {
    }
}
