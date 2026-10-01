package zzik2.mombackport.mixin.component;

import net.minecraft.core.component.DataComponentType;
import org.spongepowered.asm.mixin.Mixin;
import zzik2.mombackport.bridge.component.DataComponentTypeBridge;

@Mixin(DataComponentType.class)
public interface DataComponentTypeMixin extends DataComponentTypeBridge {
}
