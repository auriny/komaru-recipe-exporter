package ru.auriny.recipeexporter.screen.valoria;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import ru.auriny.recipeexporter.menu.valoria.SoulInfuserMenu;
import ru.auriny.recipeexporter.model.ExportRecipeType;
import ru.auriny.recipeexporter.screen.BaseExporterScreen;
import ru.auriny.recipeexporter.util.RecipeExportHelper;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

public class SoulInfuserScreen extends BaseExporterScreen<SoulInfuserMenu> {
    private EditBox timeInput;
    private EditBox soulsInput;

    public SoulInfuserScreen(SoulInfuserMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return ExportRecipeType.VALORIA_SOUL_INFUSER.getTexture();
    }

    @Override
    protected void init() {
        super.init();
        createTagInput(0, 0, 60, 16);

        this.timeInput = new EditBox(this.font, this.leftPos - 110, this.topPos + 35, 45, 16, Component.literal("Time"));
        this.timeInput.setValue("200");
        this.addRenderableWidget(timeInput);

        this.soulsInput = new EditBox(this.font, this.leftPos - 60, this.topPos + 35, 45, 16, Component.literal("Souls"));
        this.soulsInput.setValue("100");
        this.addRenderableWidget(soulsInput);
    }

    @Override
    protected void updateTagInputVisibility() {
        EditBox tagBox = tagInputs.get(0);
        tagBox.setX(this.leftPos - 110);
        tagBox.setY(this.topPos + 65);
        tagBox.setVisible(useTags && !menu.getSlot(0).getItem().isEmpty());
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(this.font, Component.translatable("komaru_recipe_exporter.label.cooking_time"), -110, 24, 0x404040, false);
        graphics.drawString(this.font, Component.translatable("komaru_recipe_exporter.label.souls"), -60, 24, 0x404040, false);
    }

    @Override
    protected boolean isRecipeValid() {
        return !menu.getSlot(0).getItem().isEmpty() && !menu.getSlot(2).getItem().isEmpty();
    }

    @Override
    protected String getSuggestedFileName() {
        ItemStack result = menu.getSlot(2).getItem();
        String base = result.isEmpty() ? "soul_infuser_recipe" : RecipeJsonHelper.getItemPath(result.getItem());
        return base + "_soul_infuser";
    }

    @Override
    protected void doExportJson(String fileName, boolean toClipboard) {
        JsonObject json = createJsonRecipe();
        String jsonString = RecipeExportHelper.toPrettyJson(json);
        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(jsonString);
        } else {
            RecipeExportHelper.exportJsonFile("valoria", fileName, jsonString);
        }
    }

    @Override
    protected void doExportKubeJS(String fileName, boolean toClipboard) {
        JsonObject json = createJsonRecipe();
        String script = "event.custom(" + RecipeExportHelper.toPrettyJson(json) + ")";
        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(script);
        } else {
            RecipeExportHelper.exportKubeJSRecipe(fileName, script);
        }
    }

    private JsonObject createJsonRecipe() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "valoria:soul_infuser");

        ItemStack inputStack = menu.getSlot(0).getItem();
        json.add("ingredient", RecipeJsonHelper.serializeItemOrTag(inputStack, tagInputs.get(0).getValue(), useTags));
        json.add("output", RecipeJsonHelper.serializeResult(menu.getSlot(2).getItem()));

        int time = 200;
        try {
            time = Integer.parseInt(timeInput.getValue().trim());
        } catch (Exception ignored) {}
        json.addProperty("time", time);

        int souls = 100;
        try {
            souls = Integer.parseInt(soulsInput.getValue().trim());
        } catch (Exception ignored) {}
        json.addProperty("souls", souls);

        return json;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (timeInput.isFocused() && timeInput.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (soulsInput.isFocused() && soulsInput.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!timeInput.isMouseOver(mouseX, mouseY) && timeInput.isFocused()) timeInput.setFocused(false);
        if (!soulsInput.isMouseOver(mouseX, mouseY) && soulsInput.isFocused()) soulsInput.setFocused(false);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
