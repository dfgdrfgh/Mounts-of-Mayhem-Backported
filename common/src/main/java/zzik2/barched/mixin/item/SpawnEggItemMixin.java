package zzik2.barched.mixin.item;

import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zzik2.barched.Barched;

@Mixin(SpawnEggItem.class)
public abstract class SpawnEggItemMixin {

    @Shadow
    public abstract EntityType<?> getType(ItemStack itemStack);

    @Inject(
            method = "useOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/SpawnEggItem;getType(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EntityType;",
                    ordinal = 1
            ),
            cancellable = true
    )
    private void barched$rejectParchedUseOnInPeaceful(
            UseOnContext context,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (context.getLevel().getDifficulty() == Difficulty.PEACEFUL
                && this.getType(context.getItemInHand()) == Barched.EntityType.PARCHED) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Inject(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/SpawnEggItem;getType(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EntityType;"
            ),
            cancellable = true
    )
    private void barched$rejectParchedUseInPeaceful(
            Level level,
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir
    ) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.getDifficulty() == Difficulty.PEACEFUL
                && this.getType(itemStack) == Barched.EntityType.PARCHED) {
            cir.setReturnValue(InteractionResultHolder.fail(itemStack));
        }
    }
}
