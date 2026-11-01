package ru.auriny.recipeexporter.menu.botania;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ru.auriny.recipeexporter.menu.BaseExporterMenu;
import ru.auriny.recipeexporter.menu.KREMenus;

public class RunicAltarMenu extends BaseExporterMenu {
    private final SimpleContainer container = new SimpleContainer(17);

    public RunicAltarMenu(int id, Inventory inventory) {
        super(KREMenus.RUNIC_ALTAR.get(), id, inventory.player, 17);

        // 16 ingredient slots: 0 to 15
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                this.addSlot(new Slot(container, col + row * 4, 14 + col * 18, 12 + row * 18));
            }
        }
        // Result slot: 16
        this.addSlot(new KREResultSlot(container, 16, 124, 35));

        this.addPlayerInventory(inventory);
    }
}
