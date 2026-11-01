package ru.auriny.recipeexporter.screen.botania;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import ru.auriny.recipeexporter.menu.botania.PureDaisyMenu;
import ru.auriny.recipeexporter.model.ExportRecipeType;
import ru.auriny.recipeexporter.screen.BaseExporterScreen;
import ru.auriny.recipeexporter.util.RecipeExportHelper;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

public class PureDaisyScreen extends BaseExporterScreen<PureDaisyMenu> {
    private EditBox timeInput;

    public PureDaisyScreen(PureDaisyMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return ExportRecipeType.PURE_DAISY.getTexture();
    }

    @Override
    protected void init() {
        super.init();
        createTagInput(0, 0, 60, 16);

        this.timeInput = new EditBox(this.font, this.leftPos - 75, this.topPos + 35, 60, 16, Component.literal("Ticks"));
        this.timeInput.setValue("150");
        this.addRenderableWidget(timeInput);
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
        graphics.drawString(this.font, Component.translatable("komaru_recipe_exporter.label.cooking_time"), -115, 39, 0x404040, false);
    }

    @Override
    protected boolean isRecipeValid() {
        return !menu.getSlot(0).getItem().isEmpty() && !menu.getSlot(1).getItem().isEmpty();
    }

    @Override
    protected String getSuggestedFileName() {
        ItemStack result = menu.getSlot(1).getItem();
        String base = result.isEmpty() ? "pure_daisy_recipe" : RecipeJsonHelper.getItemPath(result.getItem());
        return base + "_pure_daisy";
    }

    @Override
    protected void doExportJson(String fileName, boolean toClipboard) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "botania:pure_daisy");

        ItemStack inputStack = menu.getSlot(0).getItem();
        String tagVal = tagInputs.get(0).getValue();
        json.add("input", RecipeJsonHelper.serializeBlockOrTag(inputStack, tagVal, useTags));

        ItemStack outputStack = menu.getSlot(1).getItem();
        JsonObject outputObj = new JsonObject();
        outputObj.addProperty("name", RecipeJsonHelper.getItemId(outputStack));
        json.add("output", outputObj);

        int time = 150;
        try {
            time = Integer.parseInt(timeInput.getValue().trim());
        } catch (Exception ignored) {}
        json.addProperty("time", time);

        String jsonString = RecipeExportHelper.toPrettyJson(json);
        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(jsonString);
        } else {
            RecipeExportHelper.exportJsonFile("pure_daisy", fileName, jsonString);
        }
    }

    @Override
    protected void doExportKubeJS(String fileName, boolean toClipboard) {
        ItemStack inputStack = menu.getSlot(0).getItem();
        ItemStack outputStack = menu.getSlot(1).getItem();

        String inputStr = RecipeJsonHelper.formatKubeJSItem(inputStack, tagInputs.get(0).getValue(), useTags);
        String outputStr = RecipeJsonHelper.formatKubeJSResult(outputStack);

        int time = 150;
        try {
            time = Integer.parseInt(timeInput.getValue().trim());
        } catch (Exception ignored) {}

        String script = "event.recipes.botania.pure_daisy(" + outputStr + ", " + inputStr + ").time(" + time + ")";

        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(script);
        } else {
            RecipeExportHelper.exportKubeJSRecipe(fileName, script);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (timeInput.isFocused() && timeInput.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!timeInput.isMouseOver(mouseX, mouseY) && timeInput.isFocused()) timeInput.setFocused(false);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
