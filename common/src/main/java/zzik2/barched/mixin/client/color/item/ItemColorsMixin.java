package zzik2.barched.mixin.client.color.item;

import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemColors.class)
public abstract class ItemColorsMixin {

    @Inject(method = "getColor", at = @At("HEAD"), cancellable = true)
    private void barched$useBakedZombieHorseEggColors(
            ItemStack stack,
            int tintIndex,
            CallbackInfoReturnable<Integer> cir
    ) {
        if (stack.is(Items.ZOMBIE_HORSE_SPAWN_EGG)) {
            // 1.21.11 spawn eggs are pre-colored textures; the 1.21.1 SpawnEggItem tint
            // would otherwise recolor the baked texture.
            cir.setReturnValue(-1);
        }
    }
}
