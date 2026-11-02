package ru.auriny.recipeexporter.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import ru.auriny.recipeexporter.menu.BaseExporterMenu;
import ru.auriny.recipeexporter.util.RecipeJsonHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseExporterScreen<M extends BaseExporterMenu> extends AbstractContainerScreen<M> {
    protected Button exportJsonButton;
    protected Button exportKubeJSButton;
    protected Checkbox useTagsCheckbox;
    protected boolean useTags = false;
    protected final List<EditBox> tagInputs = new ArrayList<>();
    protected final KREExportDialog exportDialog = new KREExportDialog(this);

    public BaseExporterScreen(M menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.minecraft = Minecraft.getInstance();
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    protected abstract ResourceLocation getTextureLocation();

    protected abstract boolean isRecipeValid();

    protected abstract void doExportJson(String fileName, boolean toClipboard);

    protected abstract void doExportKubeJS(String fileName, boolean toClipboard);

    protected String getSuggestedFileName() {
        if (menu.slots.isEmpty()) return "recipe";
        ItemStack result = menu.slots.get(0).getItem();
        return result.isEmpty() ? "recipe" : RecipeJsonHelper.getItemPath(result);
    }

    @Override
    protected void init() {
        super.init();
        tagInputs.clear();

        int buttonWidth = 40;
        int spacing = 4;
        int startX = this.leftPos + 85;

        this.useTagsCheckbox = new Checkbox(
                this.leftPos - 75, this.topPos + 10,
                70, 20,
                Component.translatable("komaru_recipe_exporter.gui.use_tags"),
                this.useTags
        ) {
            @Override
            public void onPress() {
                super.onPress();
                useTags = this.selected();
                updateTagInputVisibility();
            }
        };

        this.exportJsonButton = Button.builder(Component.translatable("komaru_recipe_exporter.gui.json"), button -> {
                    if (!isRecipeValid()) return;
                    if (Screen.hasControlDown()) {
                        doExportJson(getSuggestedFileName(), true);
                    } else {
                        exportDialog.open(
                                Component.translatable("komaru_recipe_exporter.dialog.title.json"),
                                getSuggestedFileName(),
                                customName -> doExportJson(customName, false),
                                customName -> doExportJson(customName, true)
                        );
                    }
                })
                .pos(startX, this.topPos + 58)
                .size(buttonWidth, 20)
                .tooltip(Tooltip.create(Component.translatable("komaru_recipe_exporter.gui.tooltip.json")))
                .build();

        this.exportKubeJSButton = Button.builder(Component.translatable("komaru_recipe_exporter.gui.kubejs"), button -> {
                    if (!isRecipeValid()) return;
                    if (Screen.hasControlDown()) {
                        doExportKubeJS("exported_recipes", true);
                    } else {
                        exportDialog.open(
                                Component.translatable("komaru_recipe_exporter.dialog.title.kubejs"),
                                "exported_recipes",
                                customName -> doExportKubeJS(customName, false),
                                customName -> doExportKubeJS(customName, true)
                        );
                    }
                })
                .pos(startX + buttonWidth + spacing, this.topPos + 58)
                .size(buttonWidth, 20)
                .tooltip(Tooltip.create(Component.translatable("komaru_recipe_exporter.gui.tooltip.kubejs")))
                .build();

        this.addRenderableWidget(useTagsCheckbox);
        this.addRenderableWidget(exportJsonButton);
        this.addRenderableWidget(exportKubeJSButton);

        this.exportDialog.init(this.width, this.height, this.font);
    }

    protected EditBox createTagInput(int x, int y, int width, int height) {
        EditBox box = new EditBox(this.font, x, y, width, height, Component.empty());
        box.setVisible(false);
        this.tagInputs.add(box);
        this.addRenderableWidget(box);
        return box;
    }

    protected void updateTagInputVisibility() {
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (exportDialog.isVisible()) {
            this.renderBackground(graphics);
            exportDialog.render(graphics, mouseX, mouseY, partialTick);
            return;
        }

        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
        updateTagInputVisibility();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(getTextureLocation(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (exportDialog.isVisible()) {
            return exportDialog.keyPressed(keyCode, scanCode, modifiers);
        }
        for (EditBox box : tagInputs) {
            if (box.isFocused()) {
                if (box.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (exportDialog.isVisible()) {
            return exportDialog.charTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (exportDialog.isVisible()) {
            return exportDialog.mouseClicked(mouseX, mouseY, button);
        }
        for (EditBox box : tagInputs) {
            if (!box.isMouseOver(mouseX, mouseY) && box.isFocused()) {
                box.setFocused(false);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (exportDialog.isVisible()) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (exportDialog.isVisible()) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (exportDialog.isVisible()) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
}
