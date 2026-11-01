package ru.auriny.recipeexporter.menu.vanilla;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ru.auriny.recipeexporter.menu.BaseExporterMenu;
import ru.auriny.recipeexporter.menu.KREMenus;

public class StonecuttingMenu extends BaseExporterMenu {
    private final SimpleContainer container = new SimpleContainer(2);

    public StonecuttingMenu(int id, Inventory inventory) {
        super(KREMenus.STONECUTTING.get(), id, inventory.player, 2);

        // Input slot: 0
        this.addSlot(new Slot(container, 0, 20, 33));
        // Result slot: 1
        this.addSlot(new KREResultSlot(container, 1, 143, 33));

        this.addPlayerInventory(inventory);
    }
}
