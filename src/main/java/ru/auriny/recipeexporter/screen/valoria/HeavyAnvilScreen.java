package ru.auriny.recipeexporter.screen.valoria;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import ru.auriny.recipeexporter.menu.valoria.HeavyAnvilMenu;
import ru.auriny.recipeexporter.model.ExportRecipeType;
import ru.auriny.recipeexporter.screen.BaseExporterScreen;
import ru.auriny.recipeexporter.util.RecipeExportHelper;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

public class HeavyAnvilScreen extends BaseExporterScreen<HeavyAnvilMenu> {
    private EditBox hitsInput;
    private EditBox instabilityInput;
    private EditBox speedInput;

    public HeavyAnvilScreen(HeavyAnvilMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return ExportRecipeType.VALORIA_HEAVY_ANVIL.getTexture();
    }

    @Override
    protected void init() {
        super.init();
        createTagInput(0, 0, 60, 16);

        this.hitsInput = new EditBox(this.font, this.leftPos - 110, this.topPos + 25, 45, 16, Component.literal("Hits"));
        this.hitsInput.setValue("10");
        this.addRenderableWidget(hitsInput);

        this.instabilityInput = new EditBox(this.font, this.leftPos - 60, this.topPos + 25, 45, 16, Component.literal("Instability"));
        this.instabilityInput.setValue("3");
        this.addRenderableWidget(instabilityInput);

        this.speedInput = new EditBox(this.font, this.leftPos - 110, this.topPos + 60, 95, 16, Component.literal("Speed"));
        this.speedInput.setValue("0.05");
        this.addRenderableWidget(speedInput);
    }

    @Override
    protected void updateTagInputVisibility() {
        EditBox tagBox = tagInputs.get(0);
        tagBox.setX(this.leftPos - 110);
        tagBox.setY(this.topPos + 85);
        tagBox.setVisible(useTags && !menu.getSlot(0).getItem().isEmpty());
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(this.font, Component.translatable("komaru_recipe_exporter.label.hits"), -110, 14, 0x404040, false);
        graphics.drawString(this.font, Component.translatable("komaru_recipe_exporter.label.instability"), -60, 14, 0x404040, false);
        graphics.drawString(this.font, Component.translatable("komaru_recipe_exporter.label.speed"), -110, 49, 0x404040, false);
    }

    @Override
    protected boolean isRecipeValid() {
        return !menu.getSlot(0).getItem().isEmpty() && !menu.getSlot(1).getItem().isEmpty();
    }

    @Override
    protected String getSuggestedFileName() {
        ItemStack result = menu.getSlot(1).getItem();
        String base = result.isEmpty() ? "heavy_anvil_recipe" : RecipeJsonHelper.getItemPath(result.getItem());
        return base + "_heavy_anvil";
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
        json.addProperty("type", "valoria:heavy_anvil");

        ItemStack inputStack = menu.getSlot(0).getItem();
        json.add("ingredient", RecipeJsonHelper.serializeItemOrTag(inputStack, tagInputs.get(0).getValue(), useTags));
        json.add("result", RecipeJsonHelper.serializeResult(menu.getSlot(1).getItem()));

        int hits = 10;
        try {
            hits = Integer.parseInt(hitsInput.getValue().trim());
        } catch (Exception ignored) {}
        json.addProperty("required_hits", hits);

        int instab = 3;
        try {
            instab = Integer.parseInt(instabilityInput.getValue().trim());
        } catch (Exception ignored) {}
        json.addProperty("max_instability", instab);

        float speed = 0.05f;
        try {
            speed = Float.parseFloat(speedInput.getValue().trim());
        } catch (Exception ignored) {}
        json.addProperty("cursor_speed", speed);

        return json;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (hitsInput.isFocused() && hitsInput.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (instabilityInput.isFocused() && instabilityInput.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (speedInput.isFocused() && speedInput.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!hitsInput.isMouseOver(mouseX, mouseY) && hitsInput.isFocused()) hitsInput.setFocused(false);
        if (!instabilityInput.isMouseOver(mouseX, mouseY) && instabilityInput.isFocused()) instabilityInput.setFocused(false);
        if (!speedInput.isMouseOver(mouseX, mouseY) && speedInput.isFocused()) speedInput.setFocused(false);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
