package ru.auriny.recipeexporter.menu.vanilla;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ru.auriny.recipeexporter.menu.BaseExporterMenu;
import ru.auriny.recipeexporter.menu.KREMenus;
import ru.auriny.recipeexporter.model.ExportRecipeType;

public class CookingMenu extends BaseExporterMenu {
    private final SimpleContainer container = new SimpleContainer(2);
    private final ExportRecipeType recipeType;

    public CookingMenu(int id, Inventory inventory, String typeName) {
        super(KREMenus.COOKING.get(), id, inventory.player, 2);
        ExportRecipeType type = ExportRecipeType.fromName(typeName);
        this.recipeType = type != null ? type : ExportRecipeType.SMELTING;

        // Input slot: 0
        this.addSlot(new Slot(container, 0, 56, 17));
        // Result slot: 1
        this.addSlot(new KREResultSlot(container, 1, 116, 35));

        this.addPlayerInventory(inventory);
    }

    public ExportRecipeType getRecipeType() {
        return recipeType;
    }
}
