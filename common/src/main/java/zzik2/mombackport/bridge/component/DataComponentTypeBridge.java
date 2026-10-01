package zzik2.mombackport.bridge.component;

public interface DataComponentTypeBridge {

    default boolean ignoreSwapAnimation() {
        return false;
    }

    default void mombackport$setIgnoreSwapAnimation(boolean value) {
    }
}
