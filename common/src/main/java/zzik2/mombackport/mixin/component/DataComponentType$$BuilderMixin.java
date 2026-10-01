package zzik2.mombackport.mixin.component;

import net.minecraft.core.component.DataComponentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zzik2.mombackport.bridge.component.DataComponentType$BuilderBridge;
import zzik2.mombackport.bridge.component.DataComponentTypeBridge;

@Mixin(DataComponentType.Builder.class)
public class DataComponentType$$BuilderMixin<T> implements DataComponentType$BuilderBridge<T> {

    private boolean ignoreSwapAnimation;

    @Override
    public DataComponentType.Builder<T> ignoreSwapAnimation() {
        this.ignoreSwapAnimation = true;
        return (DataComponentType.Builder<T>) (Object) this;
    }

    @Inject(method = "build", at = @At("RETURN"))
    private void mombackport$onBuild(CallbackInfoReturnable<DataComponentType<T>> cir) {
        if (this.ignoreSwapAnimation) ((DataComponentTypeBridge) cir.getReturnValue()).mombackport$setIgnoreSwapAnimation(true);
    }
}
