package ru.auriny.recipeexporter.util;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class RecipeJsonHelper {
    public static String getItemId(ItemStack stack) {
        if (stack.isEmpty()) return "minecraft:air";
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return key != null ? key.toString() : "minecraft:air";
    }

    public static String getItemPath(ItemStack stack) {
        if (stack.isEmpty()) return "air";
        return getItemPath(stack.getItem());
    }

    public static String getItemPath(net.minecraft.world.item.Item item) {
        if (item == null) return "air";
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        return key != null ? key.getPath() : "air";
    }

    public static JsonObject serializeItemOrTag(ItemStack stack, String tagValue, boolean useTags) {
        JsonObject obj = new JsonObject();
        if (useTags && tagValue != null && !tagValue.trim().isEmpty()) {
            obj.addProperty("tag", tagValue.trim());
        } else {
            obj.addProperty("item", getItemId(stack));
        }
        if (stack.getCount() > 1) {
            obj.addProperty("count", stack.getCount());
        }
        return obj;
    }

    public static JsonObject serializeBlockOrTag(ItemStack stack, String tagValue, boolean useTags) {
        JsonObject obj = new JsonObject();
        if (useTags && tagValue != null && !tagValue.trim().isEmpty()) {
            obj.addProperty("type", "tag");
            obj.addProperty("tag", tagValue.trim());
        } else {
            obj.addProperty("type", "block");
            obj.addProperty("block", getItemId(stack));
        }
        return obj;
    }

    public static JsonObject serializeResult(ItemStack stack) {
        JsonObject result = new JsonObject();
        result.addProperty("item", getItemId(stack));
        if (stack.getCount() > 1) {
            result.addProperty("count", stack.getCount());
        }
        return result;
    }

    public static String formatKubeJSItem(ItemStack stack, String tagValue, boolean useTags) {
        if (useTags && tagValue != null && !tagValue.trim().isEmpty()) {
            return "'#" + tagValue.trim() + "'";
        }
        return "'" + getItemId(stack) + "'";
    }

    public static String formatKubeJSResult(ItemStack stack) {
        String id = getItemId(stack);
        if (stack.getCount() > 1) {
            return "Item.of('" + id + "', " + stack.getCount() + ")";
        }
        return "'" + id + "'";
    }
}
