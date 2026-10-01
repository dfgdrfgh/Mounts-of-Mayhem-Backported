package zzik2.barched.mixin.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ItemBasedSteering;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzik2.barched.bridge.entity.SaddleItemBridge;

@Mixin(Strider.class)
public abstract class StriderMixin implements SaddleItemBridge {
    @Shadow @Final private ItemBasedSteering steering;

    @Unique
    private static final String BARCHED_SADDLE_TAG = "BarchedSaddleItem";

    @Unique
    private static final EntityDataAccessor<ItemStack> BARCHED_SADDLE_ITEM =
            SynchedEntityData.defineId(Strider.class, EntityDataSerializers.ITEM_STACK);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void barched$defineSaddleItem(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(BARCHED_SADDLE_ITEM, ItemStack.EMPTY);
    }

    @Inject(method = "equipSaddle", at = @At("TAIL"))
    private void barched$rememberSaddle(ItemStack stack, @Nullable SoundSource source, CallbackInfo ci) {
        this.barched$setSaddleItem(stack);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void barched$saveSaddleItem(CompoundTag tag, CallbackInfo ci) {
        ItemStack saddle = this.barched$getSaddleItem();
        if (!saddle.isEmpty()) {
            Strider strider = (Strider) (Object) this;
            tag.put(BARCHED_SADDLE_TAG, saddle.save(strider.registryAccess()));
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void barched$loadSaddleItem(CompoundTag tag, CallbackInfo ci) {
        Strider strider = (Strider) (Object) this;
        ItemStack saddle;
        if (tag.contains(BARCHED_SADDLE_TAG, 10)) {
            saddle = ItemStack.parse(strider.registryAccess(), tag.getCompound(BARCHED_SADDLE_TAG)).orElse(ItemStack.EMPTY);
        } else {
            saddle = strider.isSaddled() ? new ItemStack(Items.SADDLE) : ItemStack.EMPTY;
        }
        this.barched$setSaddleItem(saddle);
    }

    @Redirect(
            method = "dropEquipment",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/monster/Strider;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"
            )
    )
    private ItemEntity barched$dropStoredSaddle(Strider strider, ItemLike ignored) {
        ItemStack saddle = this.barched$getSaddleItem();
        if (!saddle.isEmpty() && EnchantmentHelper.has(saddle, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
            return null;
        }

        ItemEntity dropped = strider.spawnAtLocation(saddle.isEmpty() ? new ItemStack(Items.SADDLE) : saddle.copy());
        if (dropped != null) {
            this.barched$setSaddleItem(ItemStack.EMPTY);
            strider.gameEvent(GameEvent.UNEQUIP);
        }
        return dropped;
    }

    @Override
    public ItemStack barched$getSaddleItem() {
        return ((Strider) (Object) this).getEntityData().get(BARCHED_SADDLE_ITEM);
    }

    @Override
    public void barched$setSaddleItem(ItemStack stack) {
        Strider strider = (Strider) (Object) this;
        ItemStack stored = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        this.steering.setSaddle(!stored.isEmpty());
        strider.getEntityData().set(BARCHED_SADDLE_ITEM, stored);
    }
}
