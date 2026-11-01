package ru.auriny.recipeexporter.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class BaseExporterMenu extends AbstractContainerMenu {
    protected final Player player;
    protected final int customSlotCount;

    protected BaseExporterMenu(MenuType<?> menuType, int id, Player player, int customSlotCount) {
        super(menuType, id);
        this.player = player;
        this.customSlotCount = customSlotCount;
    }

    protected void addPlayerInventory(Inventory inventory) {
        addPlayerInventory(inventory, 8, 84, 142);
    }

    protected void addPlayerInventory(Inventory inventory, int startX, int inventoryStartY, int hotbarStartY) {
        // Main inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, startX + col * 18, inventoryStartY + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, startX + col * 18, hotbarStartY));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        for (int i = 0; i < customSlotCount; i++) {
            Slot slot = this.slots.get(i);
            if (slot.hasItem()) {
                player.getInventory().placeItemBackInInventory(slot.getItem());
                slot.set(ItemStack.EMPTY);
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index < customSlotCount) {
                if (!this.moveItemStackTo(slotStack, customSlotCount, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(slotStack, 0, customSlotCount, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, slotStack);
            broadcastChanges();
        }
        return itemstack;
    }

    public static class KREResultSlot extends Slot {
        public KREResultSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return true;
        }
    }
}
