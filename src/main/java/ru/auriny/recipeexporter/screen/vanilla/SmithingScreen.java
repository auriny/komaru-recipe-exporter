package ru.auriny.recipeexporter.screen.vanilla;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import ru.auriny.recipeexporter.menu.vanilla.SmithingMenu;
import ru.auriny.recipeexporter.model.ExportRecipeType;
import ru.auriny.recipeexporter.screen.BaseExporterScreen;
import ru.auriny.recipeexporter.util.RecipeExportHelper;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

public class SmithingScreen extends BaseExporterScreen<SmithingMenu> {
    public SmithingScreen(SmithingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return menu.getRecipeType().getTexture();
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < 3; i++) {
            createTagInput(0, 0, 50, 16);
        }
    }

    @Override
    protected void updateTagInputVisibility() {
        for (int i = 0; i < 3; i++) {
            EditBox box = tagInputs.get(i);
            box.setX(this.leftPos - 75);
            box.setY(this.topPos + 10 + i * 22);
            box.setVisible(useTags && !menu.getSlot(i).getItem().isEmpty());
        }
    }

    @Override
    protected boolean isRecipeValid() {
        boolean inputsValid = !menu.getSlot(0).getItem().isEmpty()
                && !menu.getSlot(1).getItem().isEmpty()
                && !menu.getSlot(2).getItem().isEmpty();
        if (menu.getRecipeType() == ExportRecipeType.SMITHING_TRIM) {
            return inputsValid;
        }
        return inputsValid && !menu.getSlot(3).getItem().isEmpty();
    }

    @Override
    protected String getSuggestedFileName() {
        boolean isTrim = menu.getRecipeType() == ExportRecipeType.SMITHING_TRIM;
        ItemStack result = menu.getSlot(3).getItem();
        if (isTrim) {
            ItemStack base = menu.getSlot(1).getItem();
            String baseName = base.isEmpty() ? "item" : RecipeJsonHelper.getItemPath(base.getItem());
            return baseName + "_smithing_trim";
        } else {
            String base = result.isEmpty() ? "smithing_recipe" : RecipeJsonHelper.getItemPath(result.getItem());
            return base + "_smithing";
        }
    }

    @Override
    protected void doExportJson(String fileName, boolean toClipboard) {
        boolean isTrim = menu.getRecipeType() == ExportRecipeType.SMITHING_TRIM;
        JsonObject json = new JsonObject();
        json.addProperty("type", isTrim ? "minecraft:smithing_trim" : "minecraft:smithing_transform");

        ItemStack template = menu.getSlot(0).getItem();
        ItemStack base = menu.getSlot(1).getItem();
        ItemStack addition = menu.getSlot(2).getItem();

        json.add("template", RecipeJsonHelper.serializeItemOrTag(template, tagInputs.get(0).getValue(), useTags));
        json.add("base", RecipeJsonHelper.serializeItemOrTag(base, tagInputs.get(1).getValue(), useTags));
        json.add("addition", RecipeJsonHelper.serializeItemOrTag(addition, tagInputs.get(2).getValue(), useTags));

        if (!isTrim) {
            json.add("result", RecipeJsonHelper.serializeResult(menu.getSlot(3).getItem()));
        }

        String jsonString = RecipeExportHelper.toPrettyJson(json);
        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(jsonString);
        } else {
            RecipeExportHelper.exportJsonFile("smithing", fileName, jsonString);
        }
    }

    @Override
    protected void doExportKubeJS(String fileName, boolean toClipboard) {
        boolean isTrim = menu.getRecipeType() == ExportRecipeType.SMITHING_TRIM;
        ItemStack template = menu.getSlot(0).getItem();
        ItemStack base = menu.getSlot(1).getItem();
        ItemStack addition = menu.getSlot(2).getItem();

        String templateStr = RecipeJsonHelper.formatKubeJSItem(template, tagInputs.get(0).getValue(), useTags);
        String baseStr = RecipeJsonHelper.formatKubeJSItem(base, tagInputs.get(1).getValue(), useTags);
        String additionStr = RecipeJsonHelper.formatKubeJSItem(addition, tagInputs.get(2).getValue(), useTags);

        String script;
        if (isTrim) {
            script = "event.smithingTrim(" + templateStr + ", " + baseStr + ", " + additionStr + ")";
        } else {
            String resultStr = RecipeJsonHelper.formatKubeJSResult(menu.getSlot(3).getItem());
            script = "event.smithing(" + resultStr + ", " + templateStr + ", " + baseStr + ", " + additionStr + ")";
        }

        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(script);
        } else {
            RecipeExportHelper.exportKubeJSRecipe(fileName, script);
        }
    }
}
