package ru.auriny.recipeexporter.menu.botania;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ru.auriny.recipeexporter.menu.BaseExporterMenu;
import ru.auriny.recipeexporter.menu.KREMenus;

public class ManaInfusionMenu extends BaseExporterMenu {
    private final SimpleContainer container = new SimpleContainer(3);

    public ManaInfusionMenu(int id, Inventory inventory) {
        super(KREMenus.MANA_INFUSION.get(), id, inventory.player, 3);

        // Input slot: 0
        this.addSlot(new Slot(container, 0, 48, 35));
        // Catalyst slot: 1
        this.addSlot(new Slot(container, 1, 48, 57));
        // Result slot: 2
        this.addSlot(new KREResultSlot(container, 2, 108, 35));

        this.addPlayerInventory(inventory);
    }
}
