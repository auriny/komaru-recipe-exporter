package ru.auriny.recipeexporter.screen.valoria;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import ru.auriny.recipeexporter.menu.valoria.AlchemyMenu;
import ru.auriny.recipeexporter.model.ExportRecipeType;
import ru.auriny.recipeexporter.screen.BaseExporterScreen;
import ru.auriny.recipeexporter.util.RecipeExportHelper;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AlchemyScreen extends BaseExporterScreen<AlchemyMenu> {
    private int currentLevel = 1;
    private Button levelButton;
    private EditBox groupInput;

    public AlchemyScreen(AlchemyMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return ExportRecipeType.VALORIA_ALCHEMY.getTexture();
    }

    @Override
    protected void init() {
        super.init();

        for (int i = 0; i < AlchemyMenu.INGREDIENT_SLOTS; i++) {
            createTagInput(0, 0, 48, 16);
        }

        this.levelButton = Button.builder(Component.literal("Tier: " + currentLevel), b -> {
                    currentLevel = (currentLevel % 3) + 1;
                    b.setMessage(Component.literal("Tier: " + currentLevel));
                })
                .pos(this.leftPos - 110, this.topPos + 15)
                .size(95, 20)
                .build();
        this.addRenderableWidget(levelButton);

        this.groupInput = new EditBox(this.font, this.leftPos - 110, this.topPos + 40, 95, 16, Component.literal("Group"));
        this.groupInput.setHint(Component.literal("Category..."));
        this.addRenderableWidget(groupInput);
    }

    @Override
    protected void updateTagInputVisibility() {
        for (int i = 0; i < AlchemyMenu.INGREDIENT_SLOTS; i++) {
            int col = i % 3;
            int row = i / 3;
            int x = this.leftPos - 160 + col * 50;
            int y = this.topPos + 60 + row * 18;

            EditBox box = tagInputs.get(i);
            box.setX(x);
            box.setY(y);
            box.setVisible(useTags && !menu.getSlot(i).getItem().isEmpty());
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
    }

    @Override
    protected boolean isRecipeValid() {
        boolean hasInputs = false;
        for (int i = 0; i < AlchemyMenu.INGREDIENT_SLOTS; i++) {
            if (!menu.getSlot(i).getItem().isEmpty()) {
                hasInputs = true;
                break;
            }
        }
        return hasInputs && !menu.getSlot(AlchemyMenu.INGREDIENT_SLOTS).getItem().isEmpty();
    }

    @Override
    protected String getSuggestedFileName() {
        ItemStack result = menu.getSlot(AlchemyMenu.INGREDIENT_SLOTS).getItem();
        String base = result.isEmpty() ? "alchemy_recipe" : RecipeJsonHelper.getItemPath(result.getItem());
        return base + "_alchemy";
    }

    private static class MergedIngredient {
        ItemStack sample;
        String tag;
        int count;

        MergedIngredient(ItemStack sample, String tag, int count) {
            this.sample = sample;
            this.tag = tag;
            this.count = count;
        }
    }

    private List<MergedIngredient> collectIngredients() {
        Map<String, MergedIngredient> map = new LinkedHashMap<>();
        for (int i = 0; i < AlchemyMenu.INGREDIENT_SLOTS; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                String tag = tagInputs.get(i).getValue().trim();
                String key = (useTags && !tag.isEmpty()) ? ("#tag:" + tag) : RecipeJsonHelper.getItemId(stack);
                int count = Math.max(1, stack.getCount());
                if (map.containsKey(key)) {
                    map.get(key).count += count;
                } else {
                    map.put(key, new MergedIngredient(stack, tag, count));
                }
            }
        }
        return new ArrayList<>(map.values());
    }

    private JsonObject createJsonRecipe() {
        JsonObject json = new JsonObject();
        String group = groupInput.getValue().trim();
        if (!group.isEmpty()) {
            json.addProperty("group", group);
        }
        json.addProperty("type", "valoria:alchemy");
        json.addProperty("level", currentLevel);

        JsonArray ingredients = new JsonArray();
        for (MergedIngredient ing : collectIngredients()) {
            JsonObject entry = new JsonObject();
            entry.add("ingredient", RecipeJsonHelper.serializeItemOrTag(ing.sample, ing.tag, useTags));
            entry.addProperty("count", ing.count);
            ingredients.add(entry);
        }
        json.add("ingredients", ingredients);
        json.add("result", RecipeJsonHelper.serializeResult(menu.getSlot(AlchemyMenu.INGREDIENT_SLOTS).getItem()));
        return json;
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

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (groupInput.isFocused() && groupInput.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!groupInput.isMouseOver(mouseX, mouseY) && groupInput.isFocused()) groupInput.setFocused(false);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
