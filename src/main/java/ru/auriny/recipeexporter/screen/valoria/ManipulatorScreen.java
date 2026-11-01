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
import ru.auriny.recipeexporter.menu.valoria.ManipulatorMenu;
import ru.auriny.recipeexporter.model.ExportRecipeType;
import ru.auriny.recipeexporter.screen.BaseExporterScreen;
import ru.auriny.recipeexporter.util.RecipeExportHelper;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

import java.util.ArrayList;
import java.util.List;

public class ManipulatorScreen extends BaseExporterScreen<ManipulatorMenu> {
    private static final String[] CORES = {"infernal_core", "nature_core", "aquarius_core", "void_core", "empty"};
    private static final String[] CORE_NAMES = {"Infernal", "Nature", "Aquarius", "Void", "None"};
    private int currentCoreIdx = 0;

    private Button coreCycleButton;
    private EditBox coresCountInput;
    private EditBox timeInput;

    public ManipulatorScreen(ManipulatorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return ExportRecipeType.VALORIA_MANIPULATOR.getTexture();
    }

    @Override
    protected void init() {
        super.init();
        createTagInput(0, 0, 50, 16);
        createTagInput(0, 0, 50, 16);

        this.coreCycleButton = Button.builder(Component.literal("Core: " + CORE_NAMES[currentCoreIdx]), b -> {
                    currentCoreIdx = (currentCoreIdx + 1) % CORES.length;
                    b.setMessage(Component.literal("Core: " + CORE_NAMES[currentCoreIdx]));
                })
                .pos(this.leftPos - 110, this.topPos + 15)
                .size(95, 20)
                .build();
        this.addRenderableWidget(coreCycleButton);

        this.coresCountInput = new EditBox(this.font, this.leftPos - 110, this.topPos + 50, 45, 16, Component.literal("Cores"));
        this.coresCountInput.setValue("1");
        this.addRenderableWidget(coresCountInput);

        this.timeInput = new EditBox(this.font, this.leftPos - 60, this.topPos + 50, 45, 16, Component.literal("Time"));
        this.timeInput.setValue("200");
        this.addRenderableWidget(timeInput);
    }

    @Override
    protected void updateTagInputVisibility() {
        EditBox tag1 = tagInputs.get(0);
        tag1.setX(this.leftPos - 110);
        tag1.setY(this.topPos + 80);
        tag1.setVisible(useTags && !menu.getSlot(0).getItem().isEmpty());

        EditBox tag2 = tagInputs.get(1);
        tag2.setX(this.leftPos - 55);
        tag2.setY(this.topPos + 80);
        tag2.setVisible(useTags && !menu.getSlot(1).getItem().isEmpty());
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(this.font, Component.translatable("komaru_recipe_exporter.label.cores"), -110, 39, 0x404040, false);
        graphics.drawString(this.font, Component.translatable("komaru_recipe_exporter.label.cooking_time"), -60, 39, 0x404040, false);
    }

    @Override
    protected boolean isRecipeValid() {
        return !menu.getSlot(0).getItem().isEmpty() && !menu.getSlot(2).getItem().isEmpty();
    }

    @Override
    protected String getSuggestedFileName() {
        ItemStack result = menu.getSlot(2).getItem();
        String base = result.isEmpty() ? "manipulator_recipe" : RecipeJsonHelper.getItemPath(result.getItem());
        return base + "_manipulator";
    }

    @Override
    protected void doExportJson(String fileName, boolean toClipboard) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "valoria:manipulator");

        String core = CORES[currentCoreIdx];
        json.addProperty("core", core);

        int cores = 1;
        try {
            cores = Integer.parseInt(coresCountInput.getValue().trim());
        } catch (Exception ignored) {}
        json.addProperty("cores", cores);

        int time = 200;
        try {
            time = Integer.parseInt(timeInput.getValue().trim());
        } catch (Exception ignored) {}
        json.addProperty("time", time);

        JsonArray ingredients = new JsonArray();
        for (int i = 0; i < 2; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                ingredients.add(RecipeJsonHelper.serializeItemOrTag(stack, tagInputs.get(i).getValue(), useTags));
            }
        }
        json.add("ingredients", ingredients);
        json.add("output", RecipeJsonHelper.serializeResult(menu.getSlot(2).getItem()));

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
        for (int i = 0; i < 2; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                items.add(RecipeJsonHelper.formatKubeJSItem(stack, tagInputs.get(i).getValue(), useTags));
            }
        }
        String outputStr = RecipeJsonHelper.formatKubeJSResult(menu.getSlot(2).getItem());

        String core = CORES[currentCoreIdx];
        int cores = 1;
        try {
            cores = Integer.parseInt(coresCountInput.getValue().trim());
        } catch (Exception ignored) {}

        int time = 200;
        try {
            time = Integer.parseInt(timeInput.getValue().trim());
        } catch (Exception ignored) {}

        String script = "event.recipes.valoria.manipulator(" + outputStr + ", [" + String.join(", ", items) + "], '" + core + "', " + cores + ", " + time + ")";

        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(script);
        } else {
            RecipeExportHelper.exportKubeJSRecipe(fileName, script);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (coresCountInput.isFocused() && coresCountInput.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (timeInput.isFocused() && timeInput.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!coresCountInput.isMouseOver(mouseX, mouseY) && coresCountInput.isFocused()) coresCountInput.setFocused(false);
        if (!timeInput.isMouseOver(mouseX, mouseY) && timeInput.isFocused()) timeInput.setFocused(false);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
