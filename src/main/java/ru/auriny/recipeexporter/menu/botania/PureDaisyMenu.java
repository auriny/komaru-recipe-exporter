package ru.auriny.recipeexporter.menu.botania;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ru.auriny.recipeexporter.menu.BaseExporterMenu;
import ru.auriny.recipeexporter.menu.KREMenus;

public class PureDaisyMenu extends BaseExporterMenu {
    private final SimpleContainer container = new SimpleContainer(2);

    public PureDaisyMenu(int id, Inventory inventory) {
        super(KREMenus.PURE_DAISY.get(), id, inventory.player, 2);

        // Input slot: 0
        this.addSlot(new Slot(container, 0, 48, 35));
        // Output slot: 1
        this.addSlot(new KREResultSlot(container, 1, 108, 35));

        this.addPlayerInventory(inventory);
    }
}
