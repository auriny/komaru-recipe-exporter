package ru.auriny.recipeexporter.menu.botania;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ru.auriny.recipeexporter.menu.BaseExporterMenu;
import ru.auriny.recipeexporter.menu.KREMenus;

public class BreweryMenu extends BaseExporterMenu {
    private final SimpleContainer container = new SimpleContainer(7);

    public BreweryMenu(int id, Inventory inventory) {
        super(KREMenus.BREWERY.get(), id, inventory.player, 7);

        // 6 ingredient slots: 0 to 5 (2 rows of 3)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(container, col + row * 3, 26 + col * 18, 26 + row * 18));
            }
        }
        // Result/Flask slot: 6
        this.addSlot(new KREResultSlot(container, 6, 124, 35));

        this.addPlayerInventory(inventory);
    }
}
