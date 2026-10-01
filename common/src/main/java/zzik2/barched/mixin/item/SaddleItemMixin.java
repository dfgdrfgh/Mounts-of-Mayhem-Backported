package zzik2.barched.mixin.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SaddleItem;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SaddleItem.class)
public abstract class SaddleItemMixin {

    @Inject(method = "interactLivingEntity", at = @At("HEAD"), cancellable = true)
    private void barched$match12111HorseSaddleInteraction(
            ItemStack stack,
            Player player,
            LivingEntity target,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (target instanceof AbstractNautilus nautilus) {
            if (!target.isAlive() || nautilus.isSaddled() || !nautilus.isSaddleable()) {
                return;
            }
            if (!player.level().isClientSide) {
                nautilus.equipSaddle(stack.split(1), SoundSource.NEUTRAL);
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }

        if (!(target instanceof Horse)
                && !(target instanceof ZombieHorse)
                && !(target instanceof SkeletonHorse)) {
            return;
        }
        if (!(target instanceof Saddleable saddleable)
                || !target.isAlive()
                || saddleable.isSaddled()) {
            return;
        }

        if (!player.level().isClientSide) {
            saddleable.equipSaddle(stack.split(1), SoundSource.NEUTRAL);
            target.level().gameEvent(target, GameEvent.EQUIP, target.position());
        }

        cir.setReturnValue(InteractionResult.sidedSuccess(player.level().isClientSide));
    }
}
