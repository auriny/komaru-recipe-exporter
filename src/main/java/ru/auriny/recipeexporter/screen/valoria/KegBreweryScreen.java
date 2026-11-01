package ru.auriny.recipeexporter.screen.valoria;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import ru.auriny.recipeexporter.menu.valoria.KegBreweryMenu;
import ru.auriny.recipeexporter.model.ExportRecipeType;
import ru.auriny.recipeexporter.screen.BaseExporterScreen;
import ru.auriny.recipeexporter.util.RecipeExportHelper;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

import java.util.ArrayList;
import java.util.List;

public class KegBreweryScreen extends BaseExporterScreen<KegBreweryMenu> {
    private EditBox timeInput;

    public KegBreweryScreen(KegBreweryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return ExportRecipeType.VALORIA_KEG_BREWERY.getTexture();
    }

    @Override
    protected void init() {
        super.init();

        for (int i = 0; i < KegBreweryMenu.INGREDIENT_SLOTS; i++) {
            createTagInput(0, 0, 48, 16);
        }

        this.timeInput = new EditBox(this.font, this.leftPos - 110, this.topPos + 35, 95, 16, Component.literal("Time"));
        this.timeInput.setValue("1600");
        this.addRenderableWidget(timeInput);
    }

    @Override
    protected void updateTagInputVisibility() {
        for (int i = 0; i < KegBreweryMenu.INGREDIENT_SLOTS; i++) {
            int col = i % 2;
            int row = i / 2;
            int x = this.leftPos - 110 + col * 50;
            int y = this.topPos + 60 + row * 20;

            EditBox box = tagInputs.get(i);
            box.setX(x);
            box.setY(y);
            box.setVisible(useTags && !menu.getSlot(i).getItem().isEmpty());
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(this.font, Component.translatable("komaru_recipe_exporter.label.cooking_time"), -110, 24, 0x404040, false);
    }

    @Override
    protected boolean isRecipeValid() {
        boolean hasInputs = false;
        for (int i = 0; i < KegBreweryMenu.INGREDIENT_SLOTS; i++) {
            if (!menu.getSlot(i).getItem().isEmpty()) {
                hasInputs = true;
                break;
            }
        }
        return hasInputs && !menu.getSlot(KegBreweryMenu.INGREDIENT_SLOTS).getItem().isEmpty();
    }

    @Override
    protected String getSuggestedFileName() {
        ItemStack result = menu.getSlot(KegBreweryMenu.INGREDIENT_SLOTS).getItem();
        String base = result.isEmpty() ? "keg_brewery_recipe" : RecipeJsonHelper.getItemPath(result.getItem());
        return base + "_keg_brewery";
    }

    @Override
    protected void doExportJson(String fileName, boolean toClipboard) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "valoria:keg_brewery");

        int time = 1600;
        try {
            time = Integer.parseInt(timeInput.getValue().trim());
        } catch (Exception ignored) {}
        json.addProperty("time", time);

        JsonArray ingredients = new JsonArray();
        for (int i = 0; i < KegBreweryMenu.INGREDIENT_SLOTS; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                ingredients.add(RecipeJsonHelper.serializeItemOrTag(stack, tagInputs.get(i).getValue(), useTags));
            }
        }
        json.add("ingredients", ingredients);
        json.add("output", RecipeJsonHelper.serializeResult(menu.getSlot(KegBreweryMenu.INGREDIENT_SLOTS).getItem()));

        String jsonString = RecipeExportHelper.toPrettyJson(json);
        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(jsonString);
        } else {
            RecipeExportHelper.exportJsonFile("valoria", fileName, jsonString);
        }
    }

    @Override
    protected void doExportKubeJS(String fileName, boolean toClipboard) {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < KegBreweryMenu.INGREDIENT_SLOTS; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                items.add(RecipeJsonHelper.formatKubeJSItem(stack, tagInputs.get(i).getValue(), useTags));
            }
        }
        String outputStr = RecipeJsonHelper.formatKubeJSResult(menu.getSlot(KegBreweryMenu.INGREDIENT_SLOTS).getItem());

        int time = 1600;
        try {
            time = Integer.parseInt(timeInput.getValue().trim());
        } catch (Exception ignored) {}

        String script = "event.recipes.valoria.keg_brewery(" + outputStr + ", [" + String.join(", ", items) + "], " + time + ")";

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
