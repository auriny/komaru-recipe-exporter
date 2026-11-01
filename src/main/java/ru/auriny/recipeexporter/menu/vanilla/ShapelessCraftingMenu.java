package ru.auriny.recipeexporter.menu.vanilla;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ru.auriny.recipeexporter.menu.BaseExporterMenu;
import ru.auriny.recipeexporter.menu.KREMenus;

public class ShapelessCraftingMenu extends BaseExporterMenu {
    private final SimpleContainer container = new SimpleContainer(10);

    public ShapelessCraftingMenu(int id, Inventory inventory) {
        super(KREMenus.SHAPELESS_CRAFTING.get(), id, inventory.player, 10);

        // 9 crafting slots: 0 to 8
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(container, col + row * 3, 30 + col * 18, 17 + row * 18));
            }
        }
        // Result slot: 9
        this.addSlot(new KREResultSlot(container, 9, 124, 35));

        this.addPlayerInventory(inventory);
    }
}
