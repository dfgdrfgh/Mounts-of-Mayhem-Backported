package zzik2.mombackport.bridge.level;

import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;

public interface CollisionGetterBridge {

    default BlockHitResult clipIncludingBorder(ClipContext clipContext) {
        return null;
    }
}
