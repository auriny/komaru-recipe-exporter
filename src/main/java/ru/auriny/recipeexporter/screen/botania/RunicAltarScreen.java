package ru.auriny.recipeexporter.screen.botania;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import ru.auriny.recipeexporter.menu.botania.RunicAltarMenu;
import ru.auriny.recipeexporter.model.ExportRecipeType;
import ru.auriny.recipeexporter.screen.BaseExporterScreen;
import ru.auriny.recipeexporter.util.RecipeExportHelper;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

import java.util.ArrayList;
import java.util.List;

public class RunicAltarScreen extends BaseExporterScreen<RunicAltarMenu> {
    private EditBox manaInput;

    public RunicAltarScreen(RunicAltarMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return ExportRecipeType.RUNIC_ALTAR.getTexture();
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < 16; i++) {
            createTagInput(0, 0, 48, 16);
        }

        this.manaInput = new EditBox(this.font, this.leftPos - 75, this.topPos + 35, 60, 16, Component.literal("Mana"));
        this.manaInput.setValue("5200");
        this.addRenderableWidget(manaInput);
    }

    @Override
    protected void updateTagInputVisibility() {
        for (int i = 0; i < 16; i++) {
            int x = this.leftPos - 210 + (i % 4) * 50;
            int y = this.topPos + 55 + (i / 4) * 20;
            EditBox box = tagInputs.get(i);
            box.setX(x);
            box.setY(y);
            box.setVisible(useTags && !menu.getSlot(i).getItem().isEmpty());
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(this.font, Component.translatable("komaru_recipe_exporter.label.mana"), -110, 39, 0x404040, false);
    }

    @Override
    protected boolean isRecipeValid() {
        boolean hasIngredients = false;
        for (int i = 0; i < 16; i++) {
            if (!menu.getSlot(i).getItem().isEmpty()) {
                hasIngredients = true;
                break;
            }
        }
        return hasIngredients && !menu.getSlot(16).getItem().isEmpty();
    }

    @Override
    protected String getSuggestedFileName() {
        ItemStack result = menu.getSlot(16).getItem();
        String base = result.isEmpty() ? "runic_altar_recipe" : RecipeJsonHelper.getItemPath(result.getItem());
        return base + "_runic_altar";
    }

    @Override
    protected void doExportJson(String fileName, boolean toClipboard) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "botania:runic_altar");

        JsonArray ingredients = new JsonArray();
        for (int i = 0; i < 16; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                String tagVal = tagInputs.get(i).getValue();
                ingredients.add(RecipeJsonHelper.serializeItemOrTag(stack, tagVal, useTags));
            }
        }
        json.add("ingredients", ingredients);

        int mana = 5200;
        try {
            mana = Integer.parseInt(manaInput.getValue().trim());
        } catch (Exception ignored) {}
        json.addProperty("mana", mana);

        json.add("output", RecipeJsonHelper.serializeResult(menu.getSlot(16).getItem()));

        String jsonString = RecipeExportHelper.toPrettyJson(json);
        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(jsonString);
        } else {
            RecipeExportHelper.exportJsonFile("runic_altar", fileName, jsonString);
        }
    }

    @Override
    protected void doExportKubeJS(String fileName, boolean toClipboard) {
        StringBuilder script = new StringBuilder();
        ItemStack output = menu.getSlot(16).getItem();

        int mana = 5200;
        try {
            mana = Integer.parseInt(manaInput.getValue().trim());
        } catch (Exception ignored) {}

        script.append("event.recipes.botania.runic_altar(\n");
        script.append("    ").append(RecipeJsonHelper.formatKubeJSResult(output)).append(",\n");
        script.append("    [\n");

        List<String> items = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                items.add("        " + RecipeJsonHelper.formatKubeJSItem(stack, tagInputs.get(i).getValue(), useTags));
            }
        }
        script.append(String.join(",\n", items)).append("\n");
        script.append("    ],\n");
        script.append("    ").append(mana).append("\n)");

        String resultStr = script.toString();
        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(resultStr);
        } else {
            RecipeExportHelper.exportKubeJSRecipe(fileName, resultStr);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (manaInput.isFocused() && manaInput.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!manaInput.isMouseOver(mouseX, mouseY) && manaInput.isFocused()) manaInput.setFocused(false);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
