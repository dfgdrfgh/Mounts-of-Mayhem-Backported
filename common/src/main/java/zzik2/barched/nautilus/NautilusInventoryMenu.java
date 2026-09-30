package zzik2.barched.nautilus;

import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;
import zzik2.barched.Barched;
import zzik2.barched.item.NautilusArmorItem;

public class NautilusInventoryMenu extends AbstractContainerMenu {
    private final Container inventory;
    public final AbstractNautilus nautilus;
    public NautilusInventoryMenu(int id, Inventory playerInventory, Container inventory, AbstractNautilus nautilus) {
        super(null, id);
        this.inventory = inventory;
        this.nautilus = nautilus;
        inventory.startOpen(playerInventory.player);
        this.addSlot(new Slot(inventory, 0, 8, 18) {
            @Override public boolean mayPlace(ItemStack stack) { return stack.is(Items.SADDLE) && nautilus.isSaddleable(); }
            @Override public boolean isActive() { return nautilus.isSaddleable(); }
            @Override public boolean mayPickup(Player player) { return player.isCreative() || !net.minecraft.world.item.enchantment.EnchantmentHelper.has(this.getItem(), net.minecraft.world.item.enchantment.EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE); }
            @Override public int getMaxStackSize() { return 1; }
            @Override public void setByPlayer(ItemStack stack, ItemStack previous) {
                super.setByPlayer(stack, previous);
                if (!nautilus.level().isClientSide() && !ItemStack.isSameItemSameComponents(previous, stack)) {
                    if (!previous.isEmpty() && !stack.isEmpty()) {
                        nautilus.playSound(nautilus.isUnderWater() ? Barched.SoundEvents.NAUTILUS_SADDLE_UNDERWATER_EQUIP : Barched.SoundEvents.NAUTILUS_SADDLE_EQUIP);
                    }
                    nautilus.gameEvent(stack.isEmpty() ? GameEvent.UNEQUIP : GameEvent.EQUIP);
                }
            }
        });
        this.addSlot(new Slot(inventory, 1, 8, 36) {
            @Override public boolean mayPlace(ItemStack stack) { return stack.getItem() instanceof NautilusArmorItem && nautilus.isSaddleable(); }
            @Override public boolean isActive() { return nautilus.isSaddleable(); }
            @Override public boolean mayPickup(Player player) { return player.isCreative() || !net.minecraft.world.item.enchantment.EnchantmentHelper.has(this.getItem(), net.minecraft.world.item.enchantment.EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE); }
            @Override public int getMaxStackSize() { return 1; }
        });
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        for (int col = 0; col < 9; col++) this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
    }
    @Override public boolean stillValid(Player player) { return !nautilus.hasInventoryChanged(inventory) && nautilus.isAlive() && player.canInteractWithEntity(nautilus, 4.0D) && inventory.stillValid(player); }
    @Override public void removed(Player player) { super.removed(player); inventory.stopOpen(player); }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < 2) {
            if (!this.moveItemStackTo(stack, 2, this.slots.size(), true)) return ItemStack.EMPTY;
        } else if (this.slots.get(1).mayPlace(stack) && !this.slots.get(1).hasItem()) {
            if (!this.moveItemStackTo(stack, 1, 2, false)) return ItemStack.EMPTY;
        } else if (this.slots.get(0).mayPlace(stack) && !this.slots.get(0).hasItem()) {
            if (!this.moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
        } else if (index < 29) {
            if (!this.moveItemStackTo(stack, 29, 38, false)) return ItemStack.EMPTY;
        } else if (!this.moveItemStackTo(stack, 2, 29, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return original;
    }
}
