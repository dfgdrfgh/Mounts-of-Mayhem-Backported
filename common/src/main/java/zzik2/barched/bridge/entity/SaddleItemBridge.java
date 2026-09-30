package zzik2.barched.bridge.entity;

import net.minecraft.world.item.ItemStack;

public interface SaddleItemBridge {
    default ItemStack barched$getSaddleItem() {
        return ItemStack.EMPTY;
    }

    default void barched$setSaddleItem(ItemStack stack) {
    }
}
