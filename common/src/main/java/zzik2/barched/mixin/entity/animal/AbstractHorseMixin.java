package zzik2.barched.mixin.entity.animal;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.barched.Barched;
import zzik2.barched.bridge.entity.AbstractHorseBridge;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin implements AbstractHorseBridge {

    @Unique
    private static final EntityDataAccessor<ItemStack> BARCHED_SADDLE_ITEM =
            SynchedEntityData.defineId(AbstractHorse.class, EntityDataSerializers.ITEM_STACK);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void barched$defineSaddleStack(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(BARCHED_SADDLE_ITEM, ItemStack.EMPTY);
    }

    @Inject(method = "containerChanged", at = @At("TAIL"))
    private void barched$syncSaddleStack(Container container, CallbackInfo ci) {
        AbstractHorse horse = (AbstractHorse) (Object) this;
        if (horse.level().isClientSide()) {
            return;
        }

        ItemStack saddle = horse.getSlot(400).get();
        if (!ItemStack.matches(horse.getEntityData().get(BARCHED_SADDLE_ITEM), saddle)) {
            horse.getEntityData().set(BARCHED_SADDLE_ITEM, saddle.copy());
        }
    }

    @Inject(method = "dropEquipment", at = @At("TAIL"))
    private void barched$clearDroppedSaddle(CallbackInfo ci) {
        AbstractHorse horse = (AbstractHorse) (Object) this;
        ItemStack saddle = this.barched$getSaddleItem();
        if (!saddle.isEmpty()
                && !EnchantmentHelper.has(saddle, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
            this.barched$setSaddleItem(ItemStack.EMPTY);
            horse.gameEvent(GameEvent.UNEQUIP);
        }
    }

    @Redirect(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V"))
    private void barched$modifyGoal(GoalSelector instance, int i, Goal arg) {
        if (arg instanceof PanicGoal) {
            instance.addGoal(i, new Barched.MountPanicGoal((AbstractHorse) (Object) this, 1.2D));
            return;
        }
        instance.addGoal(i, arg);
    }

    @Override
    public boolean isMobControlled() {
        return false;
    }

    @Override
    public ItemStack barched$getSaddleItem() {
        return ((AbstractHorse) (Object) this).getEntityData().get(BARCHED_SADDLE_ITEM);
    }

    @Override
    public void barched$setSaddleItem(ItemStack stack) {
        AbstractHorse horse = (AbstractHorse) (Object) this;
        ItemStack stored = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        horse.getSlot(400).set(stored);
        if (!horse.level().isClientSide()) {
            horse.getEntityData().set(BARCHED_SADDLE_ITEM, stored.copy());
        }
    }
}
