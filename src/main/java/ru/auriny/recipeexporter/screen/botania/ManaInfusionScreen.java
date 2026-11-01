package ru.auriny.recipeexporter.screen.botania;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import ru.auriny.recipeexporter.menu.botania.ManaInfusionMenu;
import ru.auriny.recipeexporter.model.ExportRecipeType;
import ru.auriny.recipeexporter.screen.BaseExporterScreen;
import ru.auriny.recipeexporter.util.RecipeExportHelper;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

public class ManaInfusionScreen extends BaseExporterScreen<ManaInfusionMenu> {
    private EditBox manaInput;

    public ManaInfusionScreen(ManaInfusionMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return ExportRecipeType.MANA_INFUSION.getTexture();
    }

    @Override
    protected void init() {
        super.init();

        createTagInput(0, 0, 60, 16); // input slot tag
        createTagInput(0, 0, 60, 16); // catalyst slot tag

        this.manaInput = new EditBox(this.font, this.leftPos - 75, this.topPos + 60, 60, 16, Component.literal("Mana"));
        this.manaInput.setValue("3000");
        this.addRenderableWidget(manaInput);
    }

    @Override
    protected void updateTagInputVisibility() {
        EditBox tagIn = tagInputs.get(0);
        tagIn.setX(this.leftPos - 75);
        tagIn.setY(this.topPos + 10);
        tagIn.setVisible(useTags && !menu.getSlot(0).getItem().isEmpty());

        EditBox tagCat = tagInputs.get(1);
        tagCat.setX(this.leftPos - 75);
        tagCat.setY(this.topPos + 32);
        tagCat.setVisible(useTags && !menu.getSlot(1).getItem().isEmpty());
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(this.font, Component.translatable("komaru_recipe_exporter.label.mana"), -110, 64, 0x404040, false);
    }

    @Override
    protected boolean isRecipeValid() {
        return !menu.getSlot(0).getItem().isEmpty() && !menu.getSlot(2).getItem().isEmpty();
    }

    @Override
    protected String getSuggestedFileName() {
        ItemStack result = menu.getSlot(2).getItem();
        String base = result.isEmpty() ? "mana_infusion_recipe" : RecipeJsonHelper.getItemPath(result.getItem());
        return base + "_mana_infusion";
    }

    @Override
    protected void doExportJson(String fileName, boolean toClipboard) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "botania:mana_infusion");

        ItemStack inputStack = menu.getSlot(0).getItem();
        String tagVal = tagInputs.get(0).getValue();
        json.add("input", RecipeJsonHelper.serializeItemOrTag(inputStack, tagVal, useTags));

        int mana = 3000;
        try {
            mana = Integer.parseInt(manaInput.getValue().trim());
        } catch (Exception ignored) {}
        json.addProperty("mana", mana);

        ItemStack outputStack = menu.getSlot(2).getItem();
        json.add("output", RecipeJsonHelper.serializeResult(outputStack));

        ItemStack catalystStack = menu.getSlot(1).getItem();
        if (!catalystStack.isEmpty()) {
            json.add("catalyst", RecipeJsonHelper.serializeBlockOrTag(catalystStack, tagInputs.get(1).getValue(), useTags));
        }

        String jsonString = RecipeExportHelper.toPrettyJson(json);
        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(jsonString);
        } else {
            RecipeExportHelper.exportJsonFile("mana_infusion", fileName, jsonString);
        }
    }

    @Override
    protected void doExportKubeJS(String fileName, boolean toClipboard) {
        ItemStack inputStack = menu.getSlot(0).getItem();
        ItemStack catalystStack = menu.getSlot(1).getItem();
        ItemStack outputStack = menu.getSlot(2).getItem();

        String inputStr = RecipeJsonHelper.formatKubeJSItem(inputStack, tagInputs.get(0).getValue(), useTags);
        String outputStr = RecipeJsonHelper.formatKubeJSResult(outputStack);

        int mana = 3000;
        try {
            mana = Integer.parseInt(manaInput.getValue().trim());
        } catch (Exception ignored) {}

        String script;
        if (!catalystStack.isEmpty()) {
            String catalystStr = RecipeJsonHelper.formatKubeJSItem(catalystStack, tagInputs.get(1).getValue(), useTags);
            script = "event.recipes.botania.mana_infusion(" + outputStr + ", " + inputStr + ", " + mana + ", " + catalystStr + ")";
        } else {
            script = "event.recipes.botania.mana_infusion(" + outputStr + ", " + inputStr + ", " + mana + ")";
        }

        if (toClipboard) {
            RecipeExportHelper.copyToClipboard(script);
        } else {
            RecipeExportHelper.exportKubeJSRecipe(fileName, script);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (manaInput.isFocused() && manaInput.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!manaInput.isMouseOver(mouseX, mouseY) && manaInput.isFocused()) manaInput.setFocused(false);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
