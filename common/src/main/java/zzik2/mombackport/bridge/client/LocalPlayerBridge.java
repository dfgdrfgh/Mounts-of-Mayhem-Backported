package zzik2.mombackport.bridge.client;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;

public interface LocalPlayerBridge {

    default HitResult raycastHitResult(float f, Entity entity) {
        return null;
    }
}
