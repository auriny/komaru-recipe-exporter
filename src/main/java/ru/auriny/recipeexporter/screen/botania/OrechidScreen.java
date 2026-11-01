package ru.auriny.recipeexporter.screen.botania;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import ru.auriny.recipeexporter.menu.botania.OrechidMenu;
import ru.auriny.recipeexporter.model.ExportRecipeType;
import ru.auriny.recipeexporter.screen.BaseExporterScreen;
import ru.auriny.recipeexporter.util.RecipeExportHelper;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

public class OrechidScreen extends BaseExporterScreen<OrechidMenu> {
    private EditBox weightInput;

    public OrechidScreen(OrechidMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return ExportRecipeType.ORECHID.getTexture();
    }

    @Override
    protected void init() {
        super.init();
        createTagInput(0, 0, 60, 16);

        this.weightInput = new EditBox(this.font, this.leftPos - 75, this.topPos + 35, 60, 16, Component.literal("Weight"));
        this.weightInput.setValue("20000");
        this.addRenderableWidget(weightInput);
    }

    @Override
    protected void updateTagInputVisibility() {
        EditBox tagBox = tagInputs.get(0);
        tagBox.setX(this.leftPos - 75);
        tagBox.setY(this.topPos + 10);
        tagBox.setVisible(useTags && !menu.getSlot(0).getItem().isEmpty());
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(this.font, Component.translatable("komaru_recipe_exporter.label.weight"), -110, 39, 0x404040, false);
    }

    @Override
    protected boolean isRecipeValid() {
        return !menu.getSlot(0).getItem().isEmpty() && !menu.getSlot(1).getItem().isEmpty();
    }

    @Override
    protected String getSuggestedFileName() {
        ItemStack result = menu.getSlot(1).getItem();
        String base = result.isEmpty() ? "orechid_recipe" : RecipeJsonHelper.getItemPath(result.getItem());
        return base + "_orechid";
    }

    @Override
    protected void doExportJson(String fileName, boolean toClipboard) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "botania:orechid");

        ItemStack inputStack = menu.getSlot(0).getItem();
        String tagVal = tagInputs.get(0).getValue();
        json.add("input", RecipeJsonHelper.serializeBlockOrTag(inputStack, tagVal, useTags));

        ItemStack outputStack = menu.getSlot(1).getItem();
        JsonObject outputObj = new JsonObject();
        outputObj.addProperty("type", "block");
        outputObj.addProperty("block", RecipeJsonHelper.getItemId(outputStack));
        json.add("output", outputObj);

        int weight = 20000;
        try {
            weight = Integer.parseInt(weightInput.getValue().trim());
        } catch (Exception ignored) {}
        json.addProperty("weight", weight);

        String jsonString = RecipeExportHelper.toPrettyJson(json);
        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(jsonString);
        } else {
            RecipeExportHelper.exportJsonFile("orechid", fileName, jsonString);
        }
    }

    @Override
    protected void doExportKubeJS(String fileName, boolean toClipboard) {
        ItemStack inputStack = menu.getSlot(0).getItem();
        ItemStack outputStack = menu.getSlot(1).getItem();

        String inputStr = RecipeJsonHelper.formatKubeJSItem(inputStack, tagInputs.get(0).getValue(), useTags);
        String outputStr = RecipeJsonHelper.formatKubeJSResult(outputStack);

        int weight = 20000;
        try {
            weight = Integer.parseInt(weightInput.getValue().trim());
        } catch (Exception ignored) {}

        String script = "event.recipes.botania.orechid(" + outputStr + ", " + inputStr + ", " + weight + ")";

        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(script);
        } else {
            RecipeExportHelper.exportKubeJSRecipe(fileName, script);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (weightInput.isFocused() && weightInput.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!weightInput.isMouseOver(mouseX, mouseY) && weightInput.isFocused()) weightInput.setFocused(false);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
