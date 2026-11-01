package ru.auriny.recipeexporter.menu.valoria;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ru.auriny.recipeexporter.menu.BaseExporterMenu;
import ru.auriny.recipeexporter.menu.KREMenus;

public class AlchemyMenu extends BaseExporterMenu {
    public static final int INGREDIENT_SLOTS = 18;
    private final SimpleContainer container = new SimpleContainer(INGREDIENT_SLOTS + 1);

    public AlchemyMenu(int id, Inventory inventory) {
        super(KREMenus.VALORIA_ALCHEMY.get(), id, inventory.player, INGREDIENT_SLOTS + 1);

        // 18 ingredient slots: 6 columns x 3 rows
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 6; col++) {
                int slotIndex = row * 6 + col;
                this.addSlot(new Slot(container, slotIndex, 8 + col * 18, 17 + row * 18));
            }
        }

        // Result slot
        this.addSlot(new KREResultSlot(container, INGREDIENT_SLOTS, 146, 35));

        this.addPlayerInventory(inventory);
    }
}
