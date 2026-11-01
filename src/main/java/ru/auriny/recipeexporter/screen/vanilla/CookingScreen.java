package ru.auriny.recipeexporter.screen.vanilla;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import ru.auriny.recipeexporter.menu.vanilla.CookingMenu;
import ru.auriny.recipeexporter.model.ExportRecipeType;
import ru.auriny.recipeexporter.screen.BaseExporterScreen;
import ru.auriny.recipeexporter.util.RecipeExportHelper;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

public class CookingScreen extends BaseExporterScreen<CookingMenu> {
    private EditBox xpInput;
    private EditBox timeInput;

    public CookingScreen(CookingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return menu.getRecipeType().getTexture();
    }

    @Override
    protected void init() {
        super.init();

        createTagInput(0, 0, 60, 16);

        int defaultTime = menu.getRecipeType() == ExportRecipeType.SMELTING ? 200 : 100;
        float defaultXp = 0.1f;

        this.xpInput = new EditBox(this.font, this.leftPos - 75, this.topPos + 35, 60, 16, Component.literal("XP"));
        this.xpInput.setValue(String.valueOf(defaultXp));
        this.addRenderableWidget(xpInput);

        this.timeInput = new EditBox(this.font, this.leftPos - 75, this.topPos + 60, 60, 16, Component.literal("Ticks"));
        this.timeInput.setValue(String.valueOf(defaultTime));
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
        graphics.drawString(this.font, Component.translatable("komaru_recipe_exporter.label.experience"), -100, 39, 0x404040, false);
        graphics.drawString(this.font, Component.translatable("komaru_recipe_exporter.label.cooking_time"), -115, 64, 0x404040, false);
    }

    @Override
    protected boolean isRecipeValid() {
        return !menu.getSlot(0).getItem().isEmpty() && !menu.getSlot(1).getItem().isEmpty();
    }

    @Override
    protected String getSuggestedFileName() {
        ItemStack result = menu.getSlot(1).getItem();
        String base = result.isEmpty() ? "cooking_recipe" : RecipeJsonHelper.getItemPath(result.getItem());
        return base + "_" + menu.getRecipeType().name().toLowerCase();
    }

    @Override
    protected void doExportJson(String fileName, boolean toClipboard) {
        ExportRecipeType type = menu.getRecipeType();
        JsonObject json = new JsonObject();
        json.addProperty("type", type.getId());

        ItemStack inputStack = menu.getSlot(0).getItem();
        String tagVal = tagInputs.get(0).getValue();
        json.add("ingredient", RecipeJsonHelper.serializeItemOrTag(inputStack, tagVal, useTags));

        ItemStack outputStack = menu.getSlot(1).getItem();
        json.addProperty("result", RecipeJsonHelper.getItemId(outputStack));

        float xp = 0.1f;
        try {
            xp = Float.parseFloat(xpInput.getValue().trim());
        } catch (Exception ignored) {}
        json.addProperty("experience", xp);

        int time = 200;
        try {
            time = Integer.parseInt(timeInput.getValue().trim());
        } catch (Exception ignored) {}
        json.addProperty("cookingtime", time);

        String jsonString = RecipeExportHelper.toPrettyJson(json);
        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(jsonString);
        } else {
            RecipeExportHelper.exportJsonFile("cooking", fileName, jsonString);
        }
    }

    @Override
    protected void doExportKubeJS(String fileName, boolean toClipboard) {
        ExportRecipeType type = menu.getRecipeType();
        String methodName;
        switch (type) {
            case BLASTING -> methodName = "event.blasting";
            case SMOKING -> methodName = "event.smoking";
            case CAMPFIRE_COOKING -> methodName = "event.campfireCooking";
            case VALORIA_KILN -> methodName = "event.recipes.valoria.kiln";
            default -> methodName = "event.smelting";
        }

        ItemStack inputStack = menu.getSlot(0).getItem();
        ItemStack outputStack = menu.getSlot(1).getItem();

        String inputStr = RecipeJsonHelper.formatKubeJSItem(inputStack, tagInputs.get(0).getValue(), useTags);
        String outputStr = RecipeJsonHelper.formatKubeJSResult(outputStack);

        float xp = 0.1f;
        try {
            xp = Float.parseFloat(xpInput.getValue().trim());
        } catch (Exception ignored) {}

        int time = 200;
        try {
            time = Integer.parseInt(timeInput.getValue().trim());
        } catch (Exception ignored) {}

        String script = methodName + "(" + outputStr + ", " + inputStr + ").xp(" + xp + ").cookingTime(" + time + ")";

        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(script);
        } else {
            RecipeExportHelper.exportKubeJSRecipe(fileName, script);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (xpInput.isFocused() && xpInput.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (timeInput.isFocused() && timeInput.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!xpInput.isMouseOver(mouseX, mouseY) && xpInput.isFocused()) xpInput.setFocused(false);
        if (!timeInput.isMouseOver(mouseX, mouseY) && timeInput.isFocused()) timeInput.setFocused(false);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
