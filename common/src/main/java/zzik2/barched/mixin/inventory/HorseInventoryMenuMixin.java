package zzik2.barched.mixin.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HorseInventoryMenu.class)
public abstract class HorseInventoryMenuMixin {
    @Shadow @Final private Container horseContainer;
    @Shadow @Final private AbstractHorse horse;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void barched$replaceSaddleSlot(
            int containerId,
            Inventory playerInventory,
            Container horseInventory,
            AbstractHorse horse,
            int inventoryColumns,
            CallbackInfo ci
    ) {
        HorseInventoryMenu menu = (HorseInventoryMenu) (Object) this;
        Slot saddleSlot = new Slot(this.horseContainer, 0, 8, 18) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.SADDLE) && !this.hasItem() && HorseInventoryMenuMixin.this.horse.isSaddleable();
            }

            @Override
            public boolean isActive() {
                return HorseInventoryMenuMixin.this.horse.isSaddleable();
            }

            @Override
            public boolean mayPickup(Player player) {
                ItemStack stack = this.getItem();
                return stack.isEmpty()
                        || player.isCreative()
                        || !EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public void setByPlayer(ItemStack stack, ItemStack previous) {
                super.setByPlayer(stack, previous);
                if (!HorseInventoryMenuMixin.this.horse.level().isClientSide()
                        && !ItemStack.isSameItemSameComponents(previous, stack)) {
                    HorseInventoryMenuMixin.this.horse.gameEvent(
                            stack.isEmpty() ? GameEvent.UNEQUIP : GameEvent.EQUIP
                    );
                }
            }
        };
        saddleSlot.index = 0;
        menu.slots.set(0, saddleSlot);
    }
}
