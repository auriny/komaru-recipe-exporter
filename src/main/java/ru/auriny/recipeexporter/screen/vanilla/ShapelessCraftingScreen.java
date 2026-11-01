package ru.auriny.recipeexporter.screen.vanilla;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import ru.auriny.recipeexporter.menu.vanilla.ShapelessCraftingMenu;
import ru.auriny.recipeexporter.model.ExportRecipeType;
import ru.auriny.recipeexporter.screen.BaseExporterScreen;
import ru.auriny.recipeexporter.util.RecipeExportHelper;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

import java.util.ArrayList;
import java.util.List;

public class ShapelessCraftingScreen extends BaseExporterScreen<ShapelessCraftingMenu> {
    public ShapelessCraftingScreen(ShapelessCraftingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return ExportRecipeType.CRAFTING_SHAPELESS.getTexture();
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < 9; i++) {
            createTagInput(0, 0, 48, 16);
        }
    }

    @Override
    protected void updateTagInputVisibility() {
        for (int i = 0; i < 9; i++) {
            int x = this.leftPos - 160 + (i % 3) * 50;
            int y = this.topPos + 35 + (i / 3) * 20;
            EditBox box = tagInputs.get(i);
            box.setX(x);
            box.setY(y);
            box.setVisible(useTags && !menu.getSlot(i).getItem().isEmpty());
        }
    }

    @Override
    protected boolean isRecipeValid() {
        boolean hasInputs = false;
        for (int i = 0; i < 9; i++) {
            if (!menu.getSlot(i).getItem().isEmpty()) {
                hasInputs = true;
                break;
            }
        }
        return hasInputs && !menu.getSlot(9).getItem().isEmpty();
    }

    @Override
    protected String getSuggestedFileName() {
        ItemStack result = menu.getSlot(9).getItem();
        String base = result.isEmpty() ? "shapeless_recipe" : RecipeJsonHelper.getItemPath(result.getItem());
        return base + "_shapeless";
    }

    @Override
    protected void doExportJson(String fileName, boolean toClipboard) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shapeless");

        JsonArray ingredients = new JsonArray();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                String tagVal = tagInputs.get(i).getValue();
                ingredients.add(RecipeJsonHelper.serializeItemOrTag(stack, tagVal, useTags));
            }
        }

        json.add("ingredients", ingredients);
        json.add("result", RecipeJsonHelper.serializeResult(menu.getSlot(9).getItem()));

        String jsonString = RecipeExportHelper.toPrettyJson(json);
        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(jsonString);
        } else {
            RecipeExportHelper.exportJsonFile("shapeless", fileName, jsonString);
        }
    }

    @Override
    protected void doExportKubeJS(String fileName, boolean toClipboard) {
        StringBuilder script = new StringBuilder();
        ItemStack output = menu.getSlot(9).getItem();

        script.append("event.shapeless(\n");
        script.append("    ").append(RecipeJsonHelper.formatKubeJSResult(output)).append(",\n");
        script.append("    [\n");

        List<String> items = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                items.add("        " + RecipeJsonHelper.formatKubeJSItem(stack, tagInputs.get(i).getValue(), useTags));
            }
        }
        script.append(String.join(",\n", items)).append("\n");
        script.append("    ]\n)");

        String resultStr = script.toString();
        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(resultStr);
        } else {
            RecipeExportHelper.exportKubeJSRecipe(fileName, resultStr);
        }
    }
}
