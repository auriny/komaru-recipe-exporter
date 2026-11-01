package ru.auriny.recipeexporter.menu.botania;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ru.auriny.recipeexporter.menu.BaseExporterMenu;
import ru.auriny.recipeexporter.menu.KREMenus;

public class TerraPlateMenu extends BaseExporterMenu {
    private final SimpleContainer container = new SimpleContainer(9);

    public TerraPlateMenu(int id, Inventory inventory) {
        super(KREMenus.TERRA_PLATE.get(), id, inventory.player, 9);

        // 8 input slots: 0 to 7 (2 rows of 4)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 4; col++) {
                this.addSlot(new Slot(container, col + row * 4, 20 + col * 18, 26 + row * 18));
            }
        }
        // Result slot: 8
        this.addSlot(new KREResultSlot(container, 8, 128, 35));

        this.addPlayerInventory(inventory);
    }
}
