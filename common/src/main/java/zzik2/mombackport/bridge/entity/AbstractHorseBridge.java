package zzik2.mombackport.bridge.entity;

public interface AbstractHorseBridge extends MobBridge {

    default boolean isMobControlled() {
        return false;
    }
}
