package zzik2.mombackport.bridge;

import net.minecraft.world.entity.EquipmentSlot;

public interface InteractionHandBridge {

    default EquipmentSlot asEquipmentSlot() {
        return null;
    }
}
