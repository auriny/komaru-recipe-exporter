package ru.auriny.recipeexporter.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Files;
import java.nio.file.Path;

public class RecipeExportHelper {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String toPrettyJson(JsonElement element) {
        return GSON.toJson(element);
    }

    public static void copyToClipboard(String text) {
        Minecraft.getInstance().keyboardHandler.setClipboard(text);
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(
                    Component.translatable("komaru_recipe_exporter.message.copied_clipboard"), true);
        }
    }

    public static void exportJsonFile(String subDir, String fileName, String jsonContent) {
        try {
            Path dir = FMLPaths.GAMEDIR.get().resolve("crafting_recipes");
            if (subDir != null && !subDir.isEmpty()) {
                dir = dir.resolve(subDir);
            }
            Files.createDirectories(dir);

            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = "recipe_" + System.currentTimeMillis();
            }
            fileName = sanitizeFileName(fileName);
            if (!fileName.endsWith(".json")) {
                fileName = fileName + ".json";
            }

            Path recipePath = dir.resolve(fileName);
            Files.writeString(recipePath, jsonContent);

            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(
                        Component.translatable("komaru_recipe_exporter.message.saved_json", fileName), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(
                        Component.translatable("komaru_recipe_exporter.message.error_json", e.getMessage()), false);
            }
        }
    }

    public static void exportKubeJSRecipe(String scriptName, String recipeSnippet) {
        try {
            Path scriptsDir = FMLPaths.GAMEDIR.get().resolve("kubejs/server_scripts");
            Files.createDirectories(scriptsDir);

            if (scriptName == null || scriptName.trim().isEmpty()) {
                scriptName = "exported_recipes";
            }
            scriptName = sanitizeFileName(scriptName);
            if (!scriptName.endsWith(".js")) {
                scriptName = scriptName + ".js";
            }

            Path recipePath = scriptsDir.resolve(scriptName);

            String existingContent = "";
            if (Files.exists(recipePath)) {
                existingContent = Files.readString(recipePath);
            }

            if (!existingContent.contains("ServerEvents.recipes")) {
                existingContent = "ServerEvents.recipes(event => {\n\n});\n";
            }

            String trimmedRecipe = recipeSnippet.trim();
            if (!existingContent.contains(trimmedRecipe)) {
                String updatedContent = existingContent.replace("});", "    " + trimmedRecipe + "\n});");
                Files.writeString(recipePath, updatedContent);
            }

            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(
                        Component.translatable("komaru_recipe_exporter.message.exported_kubejs", scriptName), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(
                        Component.translatable("komaru_recipe_exporter.message.error_kubejs", e.getMessage()), false);
            }
        }
    }

    private static String sanitizeFileName(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|\\s]+", "_").trim();
    }
}
