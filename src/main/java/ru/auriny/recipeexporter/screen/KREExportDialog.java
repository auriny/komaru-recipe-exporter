package ru.auriny.recipeexporter.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class KREExportDialog {
    private final BaseExporterScreen<?> parent;
    private boolean visible = false;
    private Component title = Component.empty();
    private EditBox nameInput;
    private Button exportButton;
    private Button copyButton;
    private Button cancelButton;
    private Consumer<String> onExport;
    private Consumer<String> onCopy;

    private int x;
    private int y;
    private final int width = 240;
    private final int height = 100;

    public KREExportDialog(BaseExporterScreen<?> parent) {
        this.parent = parent;
    }

    public void init(int screenWidth, int screenHeight, Font font) {
        String currentVal = nameInput != null ? nameInput.getValue() : "";
        boolean wasFocused = nameInput != null && nameInput.isFocused();

        this.x = (screenWidth - this.width) / 2;
        this.y = (screenHeight - this.height) / 2;

        this.nameInput = new EditBox(font, x + 12, y + 40, width - 24, 20,
                Component.translatable("komaru_recipe_exporter.dialog.filename"));
        this.nameInput.setMaxLength(64);
        this.nameInput.setValue(currentVal);
        this.nameInput.setFocused(wasFocused || visible);
        this.nameInput.setHint(Component.translatable("komaru_recipe_exporter.dialog.filename_placeholder"));

        int btnWidth = 68;
        int btnSpacing = 6;
        int startBtnX = x + 12;
        int btnY = y + 68;

        this.exportButton = Button.builder(Component.translatable("komaru_recipe_exporter.dialog.export"), b -> confirmExport())
                .pos(startBtnX, btnY)
                .size(btnWidth, 20)
                .build();

        this.copyButton = Button.builder(Component.translatable("komaru_recipe_exporter.dialog.copy"), b -> confirmCopy())
                .pos(startBtnX + btnWidth + btnSpacing, btnY)
                .size(btnWidth, 20)
                .build();

        this.cancelButton = Button.builder(Component.translatable("komaru_recipe_exporter.dialog.cancel"), b -> close())
                .pos(startBtnX + (btnWidth + btnSpacing) * 2, btnY)
                .size(btnWidth, 20)
                .build();
    }

    public void open(Component title, String defaultName, Consumer<String> onExport, Consumer<String> onCopy) {
        this.title = title;
        this.onExport = onExport;
        this.onCopy = onCopy;
        this.visible = true;

        if (this.nameInput != null) {
            this.nameInput.setValue(defaultName != null ? defaultName : "");
            this.nameInput.setFocused(true);
            this.nameInput.setCursorPosition(nameInput.getValue().length());
            this.nameInput.setHighlightPos(0);
        }
    }

    public void close() {
        this.visible = false;
        if (this.nameInput != null) {
            this.nameInput.setFocused(false);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    private String getEnteredName() {
        String raw = nameInput != null ? nameInput.getValue().trim() : "";
        if (raw.isEmpty()) {
            return "recipe";
        }
        return raw;
    }

    private void confirmExport() {
        if (onExport != null) {
            onExport.accept(getEnteredName());
        }
        close();
    }

    private void confirmCopy() {
        if (onCopy != null) {
            onCopy.accept(getEnteredName());
        }
        close();
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        // Dark modal overlay over the entire screen
        graphics.fill(0, 0, parent.width, parent.height, 0xB0000000);

        // Dialog background panel with beveled borders
        graphics.fill(x, y, x + width, y + height, 0xFF232323);
        graphics.renderOutline(x, y, width, height, 0xFF555555);
        graphics.renderOutline(x + 1, y + 1, width - 2, height - 2, 0xFF141414);

        // Titles
        Font font = Minecraft.getInstance().font;
        graphics.drawCenteredString(font, title, x + width / 2, y + 10, 0xFFFFFF);
        graphics.drawString(font, Component.translatable("komaru_recipe_exporter.dialog.filename"), x + 12, y + 27, 0xAAAAAA, false);

        // Widgets
        nameInput.render(graphics, mouseX, mouseY, partialTick);
        exportButton.render(graphics, mouseX, mouseY, partialTick);
        copyButton.render(graphics, mouseX, mouseY, partialTick);
        cancelButton.render(graphics, mouseX, mouseY, partialTick);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;

        nameInput.mouseClicked(mouseX, mouseY, button);
        exportButton.mouseClicked(mouseX, mouseY, button);
        copyButton.mouseClicked(mouseX, mouseY, button);
        cancelButton.mouseClicked(mouseX, mouseY, button);

        return true;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!visible) return false;

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            confirmExport();
            return true;
        }

        if (nameInput.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        return true;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (!visible) return false;
        return nameInput.charTyped(codePoint, modifiers);
    }
}
