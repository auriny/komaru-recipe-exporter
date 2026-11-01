package ru.auriny.recipeexporter.screen.valoria;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import ru.auriny.recipeexporter.menu.valoria.HeavyWorkbenchMenu;
import ru.auriny.recipeexporter.model.ExportRecipeType;
import ru.auriny.recipeexporter.screen.BaseExporterScreen;
import ru.auriny.recipeexporter.util.RecipeExportHelper;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HeavyWorkbenchScreen extends BaseExporterScreen<HeavyWorkbenchMenu> {
    private EditBox groupInput;

    public HeavyWorkbenchScreen(HeavyWorkbenchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return ExportRecipeType.VALORIA_HEAVY_WORKBENCH.getTexture();
    }

    @Override
    protected void init() {
        super.init();

        for (int i = 0; i < HeavyWorkbenchMenu.INGREDIENT_SLOTS; i++) {
            createTagInput(0, 0, 48, 16);
        }

        this.groupInput = new EditBox(this.font, this.leftPos - 110, this.topPos + 35, 95, 16, Component.literal("Group"));
        this.groupInput.setHint(Component.literal("Category..."));
        this.addRenderableWidget(groupInput);
    }

    @Override
    protected void updateTagInputVisibility() {
        for (int i = 0; i < HeavyWorkbenchMenu.INGREDIENT_SLOTS; i++) {
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
        graphics.drawString(this.font, Component.translatable("komaru_recipe_exporter.label.group"), -110, 24, 0x404040, false);
    }

    @Override
    protected boolean isRecipeValid() {
        boolean hasInputs = false;
        for (int i = 0; i < HeavyWorkbenchMenu.INGREDIENT_SLOTS; i++) {
            if (!menu.getSlot(i).getItem().isEmpty()) {
                hasInputs = true;
                break;
            }
        }
        return hasInputs && !menu.getSlot(HeavyWorkbenchMenu.INGREDIENT_SLOTS).getItem().isEmpty();
    }

    @Override
    protected String getSuggestedFileName() {
        ItemStack result = menu.getSlot(HeavyWorkbenchMenu.INGREDIENT_SLOTS).getItem();
        String base = result.isEmpty() ? "heavy_workbench_recipe" : RecipeJsonHelper.getItemPath(result.getItem());
        return base + "_heavy_workbench";
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
        for (int i = 0; i < HeavyWorkbenchMenu.INGREDIENT_SLOTS; i++) {
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

    @Override
    protected void doExportJson(String fileName, boolean toClipboard) {
        JsonObject json = new JsonObject();
        String group = groupInput.getValue().trim();
        if (!group.isEmpty()) {
            json.addProperty("group", group);
        }
        json.addProperty("type", "valoria:heavy_workbench");

        JsonArray ingredients = new JsonArray();
        for (MergedIngredient ing : collectIngredients()) {
            JsonObject entry = new JsonObject();
            entry.add("ingredient", RecipeJsonHelper.serializeItemOrTag(ing.sample, ing.tag, useTags));
            entry.addProperty("count", ing.count);
            ingredients.add(entry);
        }
        json.add("ingredients", ingredients);
        json.add("result", RecipeJsonHelper.serializeResult(menu.getSlot(HeavyWorkbenchMenu.INGREDIENT_SLOTS).getItem()));

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
        for (MergedIngredient ing : collectIngredients()) {
            String itemStr;
            if (useTags && !ing.tag.isEmpty()) {
                itemStr = "'" + (ing.count > 1 ? (ing.count + "x #") : "#") + ing.tag + "'";
            } else {
                String id = RecipeJsonHelper.getItemId(ing.sample);
                itemStr = "'" + (ing.count > 1 ? (ing.count + "x ") : "") + id + "'";
            }
            items.add(itemStr);
        }
        String outputStr = RecipeJsonHelper.formatKubeJSResult(menu.getSlot(HeavyWorkbenchMenu.INGREDIENT_SLOTS).getItem());
        String group = groupInput.getValue().trim();

        String script = "event.recipes.valoria.heavy_workbench(" + outputStr + ", [" + String.join(", ", items) + "], '" + group + "')";

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
