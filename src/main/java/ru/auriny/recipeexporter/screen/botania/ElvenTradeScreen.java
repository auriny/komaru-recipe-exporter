package ru.auriny.recipeexporter.screen.botania;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import ru.auriny.recipeexporter.menu.botania.ElvenTradeMenu;
import ru.auriny.recipeexporter.model.ExportRecipeType;
import ru.auriny.recipeexporter.screen.BaseExporterScreen;
import ru.auriny.recipeexporter.util.RecipeExportHelper;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

import java.util.ArrayList;
import java.util.List;

public class ElvenTradeScreen extends BaseExporterScreen<ElvenTradeMenu> {
    public ElvenTradeScreen(ElvenTradeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return ExportRecipeType.ELVEN_TRADE.getTexture();
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < 4; i++) {
            createTagInput(0, 0, 48, 16);
        }
    }

    @Override
    protected void updateTagInputVisibility() {
        for (int i = 0; i < 4; i++) {
            int x = this.leftPos - 110 + (i % 2) * 50;
            int y = this.topPos + 35 + (i / 2) * 20;
            EditBox box = tagInputs.get(i);
            box.setX(x);
            box.setY(y);
            box.setVisible(useTags && !menu.getSlot(i).getItem().isEmpty());
        }
    }

    @Override
    protected boolean isRecipeValid() {
        boolean hasInputs = false;
        for (int i = 0; i < 4; i++) {
            if (!menu.getSlot(i).getItem().isEmpty()) {
                hasInputs = true;
                break;
            }
        }
        boolean hasOutputs = false;
        for (int i = 4; i < 8; i++) {
            if (!menu.getSlot(i).getItem().isEmpty()) {
                hasOutputs = true;
                break;
            }
        }
        return hasInputs && hasOutputs;
    }

    @Override
    protected String getSuggestedFileName() {
        ItemStack result = menu.getSlot(4).getItem();
        String base = result.isEmpty() ? "elven_trade_recipe" : RecipeJsonHelper.getItemPath(result.getItem());
        return base + "_elven_trade";
    }

    @Override
    protected void doExportJson(String fileName, boolean toClipboard) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "botania:elven_trade");

        JsonArray ingredients = new JsonArray();
        for (int i = 0; i < 4; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                String tagVal = tagInputs.get(i).getValue();
                ingredients.add(RecipeJsonHelper.serializeItemOrTag(stack, tagVal, useTags));
            }
        }
        json.add("ingredients", ingredients);

        JsonArray outputs = new JsonArray();
        for (int i = 4; i < 8; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                outputs.add(RecipeJsonHelper.serializeResult(stack));
            }
        }
        json.add("output", outputs);

        String jsonString = RecipeExportHelper.toPrettyJson(json);
        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(jsonString);
        } else {
            RecipeExportHelper.exportJsonFile("elven_trade", fileName, jsonString);
        }
    }

    @Override
    protected void doExportKubeJS(String fileName, boolean toClipboard) {
        List<String> inputList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                inputList.add(RecipeJsonHelper.formatKubeJSItem(stack, tagInputs.get(i).getValue(), useTags));
            }
        }

        List<String> outputList = new ArrayList<>();
        for (int i = 4; i < 8; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                outputList.add(RecipeJsonHelper.formatKubeJSResult(stack));
            }
        }

        String inputsJoined = "[" + String.join(", ", inputList) + "]";
        String outputsJoined = outputList.size() == 1 ? outputList.get(0) : "[" + String.join(", ", outputList) + "]";

        String script = "event.recipes.botania.elven_trade(" + outputsJoined + ", " + inputsJoined + ")";

        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(script);
        } else {
            RecipeExportHelper.exportKubeJSRecipe(fileName, script);
        }
    }
}
