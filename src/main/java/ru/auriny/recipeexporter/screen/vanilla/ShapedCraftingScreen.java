package ru.auriny.recipeexporter.screen.vanilla;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import ru.auriny.recipeexporter.menu.vanilla.ShapedCraftingMenu;
import ru.auriny.recipeexporter.model.ExportRecipeType;
import ru.auriny.recipeexporter.screen.BaseExporterScreen;
import ru.auriny.recipeexporter.util.RecipeExportHelper;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

import java.util.HashMap;
import java.util.Map;

public class ShapedCraftingScreen extends BaseExporterScreen<ShapedCraftingMenu> {
    private Checkbox fixedPositionCheckbox;
    private boolean isFixed = false;

    public ShapedCraftingScreen(ShapedCraftingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return ExportRecipeType.CRAFTING_SHAPED.getTexture();
    }

    @Override
    protected void init() {
        super.init();

        this.fixedPositionCheckbox = new Checkbox(
                this.leftPos - 75, this.topPos + 35,
                70, 20,
                Component.literal("Fixed"),
                this.isFixed
        ) {
            @Override
            public void onPress() {
                super.onPress();
                isFixed = this.selected();
            }
        };
        this.addRenderableWidget(fixedPositionCheckbox);

        for (int i = 0; i < 9; i++) {
            createTagInput(0, 0, 48, 16);
        }
    }

    @Override
    protected void updateTagInputVisibility() {
        for (int i = 0; i < 9; i++) {
            int x = this.leftPos - 160 + (i % 3) * 50;
            int y = this.topPos + 60 + (i / 3) * 20;
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
        String base = result.isEmpty() ? "shaped_recipe" : RecipeJsonHelper.getItemPath(result.getItem());
        return base + "_shaped";
    }

    @Override
    protected void doExportJson(String fileName, boolean toClipboard) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shaped");

        JsonArray pattern = new JsonArray();
        JsonObject key = new JsonObject();
        char currentKey = 'A';

        for (int row = 0; row < 3; row++) {
            StringBuilder patternRow = new StringBuilder();
            for (int col = 0; col < 3; col++) {
                int slotIdx = row * 3 + col;
                ItemStack stack = menu.getSlot(slotIdx).getItem();
                if (!stack.isEmpty()) {
                    String tagVal = tagInputs.get(slotIdx).getValue();
                    key.add(String.valueOf(currentKey), RecipeJsonHelper.serializeItemOrTag(stack, tagVal, useTags));
                    patternRow.append(currentKey++);
                } else {
                    patternRow.append(" ");
                }
            }
            pattern.add(patternRow.toString());
        }

        json.add("pattern", pattern);
        json.add("key", key);
        json.add("result", RecipeJsonHelper.serializeResult(menu.getSlot(9).getItem()));

        if (isFixed) {
            JsonObject conditions = new JsonObject();
            conditions.addProperty("fixed", true);
            json.add("conditions", conditions);
        }

        String jsonString = RecipeExportHelper.toPrettyJson(json);
        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(jsonString);
        } else {
            RecipeExportHelper.exportJsonFile("shaped", fileName, jsonString);
        }
    }

    @Override
    protected void doExportKubeJS(String fileName, boolean toClipboard) {
        StringBuilder script = new StringBuilder();
        ItemStack output = menu.getSlot(9).getItem();

        script.append("event.shaped(\n");
        script.append("    ").append(RecipeJsonHelper.formatKubeJSResult(output)).append(",\n");
        script.append("    [\n");

        char currentKey = 'A';
        Map<String, Character> itemToKey = new HashMap<>();

        for (int row = 0; row < 3; row++) {
            script.append("        '");
            for (int col = 0; col < 3; col++) {
                int slotIdx = row * 3 + col;
                ItemStack stack = menu.getSlot(slotIdx).getItem();
                if (!stack.isEmpty()) {
                    String itemStr = RecipeJsonHelper.formatKubeJSItem(stack, tagInputs.get(slotIdx).getValue(), useTags);
                    if (!itemToKey.containsKey(itemStr)) {
                        itemToKey.put(itemStr, currentKey++);
                    }
                    script.append(itemToKey.get(itemStr));
                } else {
                    script.append(" ");
                }
            }
            script.append(row < 2 ? "',\n" : "'\n");
        }
        script.append("    ],\n");
        script.append("    {\n");

        boolean first = true;
        for (Map.Entry<String, Character> entry : itemToKey.entrySet()) {
            if (!first) script.append(",\n");
            script.append("        ").append(entry.getValue()).append(": ").append(entry.getKey());
            first = false;
        }
        script.append("\n    }");

        if (isFixed) {
            script.append(",\n    { fixed: true }");
        }
        script.append("\n)");

        String resultStr = script.toString();
        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(resultStr);
        } else {
            RecipeExportHelper.exportKubeJSRecipe(fileName, resultStr);
        }
    }
}
