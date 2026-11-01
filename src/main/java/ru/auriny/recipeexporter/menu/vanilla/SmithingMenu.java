package ru.auriny.recipeexporter.menu.vanilla;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ru.auriny.recipeexporter.menu.BaseExporterMenu;
import ru.auriny.recipeexporter.menu.KREMenus;
import ru.auriny.recipeexporter.model.ExportRecipeType;

public class SmithingMenu extends BaseExporterMenu {
    private final SimpleContainer container = new SimpleContainer(4);
    private final ExportRecipeType recipeType;

    public SmithingMenu(int id, Inventory inventory, String typeName) {
        super(KREMenus.SMITHING.get(), id, inventory.player, 4);
        ExportRecipeType type = ExportRecipeType.fromName(typeName);
        this.recipeType = type != null ? type : ExportRecipeType.SMITHING_TRANSFORM;

        // Template slot: 0
        this.addSlot(new Slot(container, 0, 8, 48));
        // Base slot: 1
        this.addSlot(new Slot(container, 1, 26, 48));
        // Addition slot: 2
        this.addSlot(new Slot(container, 2, 44, 48));
        // Result slot: 3
        this.addSlot(new KREResultSlot(container, 3, 98, 48));

        this.addPlayerInventory(inventory);
    }

    public ExportRecipeType getRecipeType() {
        return recipeType;
    }
}
