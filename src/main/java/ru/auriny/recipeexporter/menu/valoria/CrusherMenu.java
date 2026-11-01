package ru.auriny.recipeexporter.menu.valoria;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ru.auriny.recipeexporter.menu.BaseExporterMenu;
import ru.auriny.recipeexporter.menu.KREMenus;

public class CrusherMenu extends BaseExporterMenu {
    private final SimpleContainer container = new SimpleContainer(2);

    public CrusherMenu(int id, Inventory inventory) {
        super(KREMenus.VALORIA_CRUSHER.get(), id, inventory.player, 2);

        this.addSlot(new Slot(container, 0, 48, 35));
        this.addSlot(new KREResultSlot(container, 1, 120, 35));

        this.addPlayerInventory(inventory);
    }
}
