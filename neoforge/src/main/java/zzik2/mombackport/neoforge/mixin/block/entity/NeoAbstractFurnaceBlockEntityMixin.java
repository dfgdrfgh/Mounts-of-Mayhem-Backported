package zzik2.mombackport.neoforge.mixin.block.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jline.utils.Log;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zzik2.mombackport.MomBackport;

//why the hell NeoForge is weirdo
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class NeoAbstractFurnaceBlockEntityMixin {

    @Inject(method = "canPlaceItem", at = @At("RETURN"), cancellable = true)
    private void mombackport$canPlaceItem(int i, ItemStack arg, CallbackInfoReturnable<Boolean> cir) {
        if (arg.is(MomBackport.Items.WOODEN_SPEAR)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getBurnDuration", at = @At("RETURN"), cancellable = true)
    private void mombackport$getBurnDuration(ItemStack arg, CallbackInfoReturnable<Integer> cir) {
        if (arg.is(MomBackport.Items.WOODEN_SPEAR)) {
            cir.setReturnValue(200);
        }
    }
}
