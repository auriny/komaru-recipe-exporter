package ru.auriny.recipeexporter.menu.valoria;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ru.auriny.recipeexporter.menu.BaseExporterMenu;
import ru.auriny.recipeexporter.menu.KREMenus;

public class KegBreweryMenu extends BaseExporterMenu {
    public static final int INGREDIENT_SLOTS = 4;
    private final SimpleContainer container = new SimpleContainer(INGREDIENT_SLOTS + 1);

    public KegBreweryMenu(int id, Inventory inventory) {
        super(KREMenus.VALORIA_KEG_BREWERY.get(), id, inventory.player, INGREDIENT_SLOTS + 1);

        this.addSlot(new Slot(container, 0, 34, 26));
        this.addSlot(new Slot(container, 1, 52, 26));
        this.addSlot(new Slot(container, 2, 34, 44));
        this.addSlot(new Slot(container, 3, 52, 44));

        this.addSlot(new KREResultSlot(container, INGREDIENT_SLOTS, 124, 35));

        this.addPlayerInventory(inventory);
    }
}
