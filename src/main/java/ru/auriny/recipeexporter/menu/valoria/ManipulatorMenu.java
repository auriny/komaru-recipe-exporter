package ru.auriny.recipeexporter.menu.valoria;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ru.auriny.recipeexporter.menu.BaseExporterMenu;
import ru.auriny.recipeexporter.menu.KREMenus;

public class ManipulatorMenu extends BaseExporterMenu {
    private final SimpleContainer container = new SimpleContainer(3);

    public ManipulatorMenu(int id, Inventory inventory) {
        super(KREMenus.VALORIA_MANIPULATOR.get(), id, inventory.player, 3);

        this.addSlot(new Slot(container, 0, 36, 44));
        this.addSlot(new Slot(container, 1, 72, 44));
        this.addSlot(new KREResultSlot(container, 2, 128, 44));

        this.addPlayerInventory(inventory);
    }
}
