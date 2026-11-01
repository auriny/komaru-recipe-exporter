package ru.auriny.recipeexporter.menu.botania;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ru.auriny.recipeexporter.menu.BaseExporterMenu;
import ru.auriny.recipeexporter.menu.KREMenus;

public class ElvenTradeMenu extends BaseExporterMenu {
    private final SimpleContainer container = new SimpleContainer(8);

    public ElvenTradeMenu(int id, Inventory inventory) {
        super(KREMenus.ELVEN_TRADE.get(), id, inventory.player, 8);

        // 4 input slots: 0 to 3 (2x2 grid)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                this.addSlot(new Slot(container, col + row * 2, 34 + col * 18, 26 + row * 18));
            }
        }
        // 4 output slots: 4 to 7 (2x2 grid)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                this.addSlot(new KREResultSlot(container, 4 + col + row * 2, 106 + col * 18, 26 + row * 18));
            }
        }

        this.addPlayerInventory(inventory);
    }
}
