package zzik2.barched.bridge.entity;

public interface AbstractHorseBridge extends MobBridge, SaddleItemBridge {

    default boolean isMobControlled() {
        return false;
    }
}
