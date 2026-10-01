package zzik2.barched.mixin.entity.animal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ItemBasedSteering;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.ItemEntity;
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

@Mixin(Pig.class)
public abstract class PigMixin implements SaddleItemBridge {
    @Shadow @Final private ItemBasedSteering steering;

    @Unique
    private static final String BARCHED_SADDLE_TAG = "BarchedSaddleItem";

    @Unique
    private static final EntityDataAccessor<ItemStack> BARCHED_SADDLE_ITEM =
            SynchedEntityData.defineId(Pig.class, EntityDataSerializers.ITEM_STACK);

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
            Pig pig = (Pig) (Object) this;
            tag.put(BARCHED_SADDLE_TAG, saddle.save(pig.registryAccess()));
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void barched$loadSaddleItem(CompoundTag tag, CallbackInfo ci) {
        Pig pig = (Pig) (Object) this;
        ItemStack saddle;
        if (tag.contains(BARCHED_SADDLE_TAG, 10)) {
            saddle = ItemStack.parse(pig.registryAccess(), tag.getCompound(BARCHED_SADDLE_TAG)).orElse(ItemStack.EMPTY);
        } else {
            saddle = pig.isSaddled() ? new ItemStack(Items.SADDLE) : ItemStack.EMPTY;
        }
        this.barched$setSaddleItem(saddle);
    }

    @Redirect(
            method = "dropEquipment",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/Pig;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"
            )
    )
    private ItemEntity barched$dropStoredSaddle(Pig pig, ItemLike ignored) {
        ItemStack saddle = this.barched$getSaddleItem();
        if (!saddle.isEmpty() && EnchantmentHelper.has(saddle, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
            return null;
        }

        ItemEntity dropped = pig.spawnAtLocation(saddle.isEmpty() ? new ItemStack(Items.SADDLE) : saddle.copy());
        if (dropped != null) {
            this.barched$setSaddleItem(ItemStack.EMPTY);
            pig.gameEvent(GameEvent.UNEQUIP);
        }
        return dropped;
    }

    @Override
    public ItemStack barched$getSaddleItem() {
        return ((Pig) (Object) this).getEntityData().get(BARCHED_SADDLE_ITEM);
    }

    @Override
    public void barched$setSaddleItem(ItemStack stack) {
        Pig pig = (Pig) (Object) this;
        ItemStack stored = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        this.steering.setSaddle(!stored.isEmpty());
        pig.getEntityData().set(BARCHED_SADDLE_ITEM, stored);
    }
}
