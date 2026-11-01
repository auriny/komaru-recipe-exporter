package ru.auriny.recipeexporter.screen.vanilla;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import ru.auriny.recipeexporter.menu.vanilla.StonecuttingMenu;
import ru.auriny.recipeexporter.model.ExportRecipeType;
import ru.auriny.recipeexporter.screen.BaseExporterScreen;
import ru.auriny.recipeexporter.util.RecipeExportHelper;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

public class StonecuttingScreen extends BaseExporterScreen<StonecuttingMenu> {
    public StonecuttingScreen(StonecuttingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return ExportRecipeType.STONECUTTING.getTexture();
    }

    @Override
    protected void init() {
        super.init();
        createTagInput(0, 0, 60, 16);
    }

    @Override
    protected void updateTagInputVisibility() {
        EditBox tagBox = tagInputs.get(0);
        tagBox.setX(this.leftPos - 75);
        tagBox.setY(this.topPos + 35);
        tagBox.setVisible(useTags && !menu.getSlot(0).getItem().isEmpty());
    }

    @Override
    protected boolean isRecipeValid() {
        return !menu.getSlot(0).getItem().isEmpty() && !menu.getSlot(1).getItem().isEmpty();
    }

    @Override
    protected String getSuggestedFileName() {
        ItemStack result = menu.getSlot(1).getItem();
        String base = result.isEmpty() ? "stonecutting_recipe" : RecipeJsonHelper.getItemPath(result.getItem());
        return base + "_stonecutting";
    }

    @Override
    protected void doExportJson(String fileName, boolean toClipboard) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:stonecutting");

        ItemStack inputStack = menu.getSlot(0).getItem();
        String tagVal = tagInputs.get(0).getValue();
        json.add("ingredient", RecipeJsonHelper.serializeItemOrTag(inputStack, tagVal, useTags));

        ItemStack outputStack = menu.getSlot(1).getItem();
        json.addProperty("result", RecipeJsonHelper.getItemId(outputStack));
        json.addProperty("count", Math.max(1, outputStack.getCount()));

        String jsonString = RecipeExportHelper.toPrettyJson(json);
        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(jsonString);
        } else {
            RecipeExportHelper.exportJsonFile("stonecutting", fileName, jsonString);
        }
    }

    @Override
    protected void doExportKubeJS(String fileName, boolean toClipboard) {
        ItemStack inputStack = menu.getSlot(0).getItem();
        ItemStack outputStack = menu.getSlot(1).getItem();

        String inputStr = RecipeJsonHelper.formatKubeJSItem(inputStack, tagInputs.get(0).getValue(), useTags);
        String outputStr = RecipeJsonHelper.formatKubeJSResult(outputStack);

        String script = "event.stonecutting(" + outputStr + ", " + inputStr + ")";

        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(script);
        } else {
            RecipeExportHelper.exportKubeJSRecipe(fileName, script);
        }
    }
}
