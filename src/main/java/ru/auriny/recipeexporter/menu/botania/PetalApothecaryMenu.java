package ru.auriny.recipeexporter.menu.botania;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ru.auriny.recipeexporter.menu.BaseExporterMenu;
import ru.auriny.recipeexporter.menu.KREMenus;

public class PetalApothecaryMenu extends BaseExporterMenu {
    private final SimpleContainer container = new SimpleContainer(10);

    public PetalApothecaryMenu(int id, Inventory inventory) {
        super(KREMenus.PETAL_APOTHECARY.get(), id, inventory.player, 10);

        // 8 petal slots: 0 to 7
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 4; col++) {
                this.addSlot(new Slot(container, col + row * 4, 18 + col * 18, 20 + row * 18));
            }
        }
        // Reagent (seed) slot: 8
        this.addSlot(new Slot(container, 8, 45, 58));
        // Result slot: 9
        this.addSlot(new KREResultSlot(container, 9, 124, 35));

        this.addPlayerInventory(inventory);
    }
}
