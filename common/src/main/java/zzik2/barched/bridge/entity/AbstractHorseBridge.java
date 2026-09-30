package zzik2.barched.bridge.entity;

import net.minecraft.world.item.ItemStack;

public interface AbstractHorseBridge extends MobBridge {

    default boolean isMobControlled() {
        return false;
    }

    default ItemStack barched$getSaddleItem() {
        return ItemStack.EMPTY;
    }
}
