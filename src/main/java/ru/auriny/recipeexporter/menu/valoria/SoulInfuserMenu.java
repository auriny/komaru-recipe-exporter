package ru.auriny.recipeexporter.menu.valoria;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ru.auriny.recipeexporter.menu.BaseExporterMenu;
import ru.auriny.recipeexporter.menu.KREMenus;

public class SoulInfuserMenu extends BaseExporterMenu {
    private final SimpleContainer container = new SimpleContainer(3);

    public SoulInfuserMenu(int id, Inventory inventory) {
        super(KREMenus.VALORIA_SOUL_INFUSER.get(), id, inventory.player, 3);

        this.addSlot(new Slot(container, 0, 36, 35));
        this.addSlot(new Slot(container, 1, 72, 35));
        this.addSlot(new KREResultSlot(container, 2, 128, 35));

        this.addPlayerInventory(inventory);
    }
}
