package zzik2.mombackport.bridge.client;

import net.minecraft.world.item.component.PiercingWeapon;

public interface MultiPlayerGameModeBridge {

    default void piercingAttack(PiercingWeapon piercingWeapon) {
    }
}
