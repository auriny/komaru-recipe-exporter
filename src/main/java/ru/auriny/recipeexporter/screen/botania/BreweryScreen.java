package ru.auriny.recipeexporter.screen.botania;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import ru.auriny.recipeexporter.menu.botania.BreweryMenu;
import ru.auriny.recipeexporter.model.ExportRecipeType;
import ru.auriny.recipeexporter.screen.BaseExporterScreen;
import ru.auriny.recipeexporter.util.RecipeExportHelper;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

import java.util.ArrayList;
import java.util.List;

public class BreweryScreen extends BaseExporterScreen<BreweryMenu> {
    private EditBox brewIdInput;

    public BreweryScreen(BreweryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return ExportRecipeType.BREW.getTexture();
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < 6; i++) {
            createTagInput(0, 0, 48, 16);
        }

        this.brewIdInput = new EditBox(this.font, this.leftPos - 110, this.topPos + 35, 95, 16, Component.literal("Brew ID"));
        this.brewIdInput.setValue("botania:speed");
        this.addRenderableWidget(brewIdInput);
    }

    @Override
    protected void updateTagInputVisibility() {
        for (int i = 0; i < 6; i++) {
            int x = this.leftPos - 160 + (i % 3) * 50;
            int y = this.topPos + 60 + (i / 3) * 20;
            EditBox box = tagInputs.get(i);
            box.setX(x);
            box.setY(y);
            box.setVisible(useTags && !menu.getSlot(i).getItem().isEmpty());
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(this.font, Component.literal("Brew ID"), -110, 24, 0x404040, false);
    }

    @Override
    protected boolean isRecipeValid() {
        boolean hasIngredients = false;
        for (int i = 0; i < 6; i++) {
            if (!menu.getSlot(i).getItem().isEmpty()) {
                hasIngredients = true;
                break;
            }
        }
        return hasIngredients && !brewIdInput.getValue().trim().isEmpty();
    }

    @Override
    protected String getSuggestedFileName() {
        String brew = brewIdInput.getValue().trim();
        if (brew.isEmpty()) return "brew_recipe";
        return brew.replace(":", "_") + "_brew";
    }

    @Override
    protected void doExportJson(String fileName, boolean toClipboard) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "botania:brew");
        json.addProperty("brew", brewIdInput.getValue().trim());

        JsonArray ingredients = new JsonArray();
        for (int i = 0; i < 6; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                String tagVal = tagInputs.get(i).getValue();
                ingredients.add(RecipeJsonHelper.serializeItemOrTag(stack, tagVal, useTags));
            }
        }
        json.add("ingredients", ingredients);

        String jsonString = RecipeExportHelper.toPrettyJson(json);
        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(jsonString);
        } else {
            RecipeExportHelper.exportJsonFile("brewery", fileName, jsonString);
        }
    }

    @Override
    protected void doExportKubeJS(String fileName, boolean toClipboard) {
        StringBuilder script = new StringBuilder();
        String brewId = brewIdInput.getValue().trim();

        script.append("event.recipes.botania.brew(\n");
        script.append("    '").append(brewId).append("',\n");
        script.append("    [\n");

        List<String> items = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
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

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (brewIdInput.isFocused() && brewIdInput.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!brewIdInput.isMouseOver(mouseX, mouseY) && brewIdInput.isFocused()) brewIdInput.setFocused(false);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
