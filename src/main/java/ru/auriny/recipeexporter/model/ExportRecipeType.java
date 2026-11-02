package ru.auriny.recipeexporter.model;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ru.auriny.recipeexporter.KomaruRecipeExporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum ExportRecipeType {
    // Vanilla
    CRAFTING_SHAPED("minecraft:crafting_shaped", "Shaped Crafting",
            new ResourceLocation("textures/gui/container/crafting_table.png")),
    CRAFTING_SHAPELESS("minecraft:crafting_shapeless", "Shapeless Crafting",
            new ResourceLocation("textures/gui/container/crafting_table.png")),
    SMELTING("minecraft:smelting", "Smelting",
            new ResourceLocation("textures/gui/container/furnace.png")),
    BLASTING("minecraft:blasting", "Blasting",
            new ResourceLocation("textures/gui/container/blast_furnace.png")),
    SMOKING("minecraft:smoking", "Smoking",
            new ResourceLocation("textures/gui/container/smoker.png")),
    CAMPFIRE_COOKING("minecraft:campfire_cooking", "Campfire Cooking",
            new ResourceLocation("textures/gui/container/smoker.png")),
    STONECUTTING("minecraft:stonecutting", "Stonecutting",
            new ResourceLocation("textures/gui/container/stonecutter.png")),
    SMITHING_TRANSFORM("minecraft:smithing_transform", "Smithing Transform",
            new ResourceLocation("textures/gui/container/smithing.png")),
    SMITHING_TRIM("minecraft:smithing_trim", "Smithing Trim",
            new ResourceLocation("textures/gui/container/smithing.png")),

    // Botania
    MANA_INFUSION("botania:mana_infusion", "Mana Infusion",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/mana_infusion.png")),
    PETAL_APOTHECARY("botania:petal_apothecary", "Petal Apothecary",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/petal_apothecary.png")),
    RUNIC_ALTAR("botania:runic_altar", "Runic Altar",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/runic_altar.png")),
    PURE_DAISY("botania:pure_daisy", "Pure Daisy",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/pure_daisy.png")),
    ELVEN_TRADE("botania:elven_trade", "Elven Trade",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/elven_trade.png")),
    TERRA_PLATE("botania:terra_plate", "Terrestrial Agglomeration",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/terra_plate.png")),
    BREW("botania:brew", "Botanical Brewery",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/brewery.png")),
    ORECHID("botania:orechid", "Orechid",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/orechid.png")),

    // Valoria
    VALORIA_KEG_BREWERY("valoria:keg_brewery", "Keg Brewery",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/valoria/keg_brewery.png")),
    VALORIA_JEWELRY("valoria:jewelry", "Jewelry Table",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/valoria/jewelry.png")),
    VALORIA_MANIPULATOR("valoria:manipulator", "Manipulator",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/valoria/manipulator.png")),
    VALORIA_HEAVY_WORKBENCH("valoria:heavy_workbench", "Heavy Workbench",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/valoria/heavy_workbench.png")),
    VALORIA_CRUSHER("valoria:crusher", "Crusher",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/valoria/crusher.png")),
    VALORIA_ALCHEMY("valoria:alchemy", "Alchemy Station",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/valoria/alchemy.png")),
    VALORIA_HEAVY_ANVIL("valoria:heavy_anvil", "Heavy Anvil",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/valoria/heavy_anvil.png")),
    VALORIA_SOUL_INFUSER("valoria:soul_infuser", "Soul Infuser",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/valoria/soul_infuser.png")),
    VALORIA_KILN("valoria:kiln", "Kiln",
            new ResourceLocation("textures/gui/container/furnace.png"));

    private final String id;
    private final ResourceLocation resourceLocation;
    private final String displayName;
    private final ResourceLocation texture;

    ExportRecipeType(String id, String displayName, ResourceLocation texture) {
        this.id = id;
        this.resourceLocation = new ResourceLocation(id);
        this.displayName = displayName;
        this.texture = texture;
    }

    public String getId() {
        return id;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Component getTitleComponent() {
        return Component.translatable("komaru_recipe_exporter.title." + name().toLowerCase(Locale.ROOT));
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public static ExportRecipeType fromId(ResourceLocation location) {
        if (location == null) return null;
        for (ExportRecipeType type : values()) {
            if (type.resourceLocation.equals(location)) {
                return type;
            }
        }
        // Fallback: match by path (e.g. heavy_workbench -> valoria:heavy_workbench)
        for (ExportRecipeType type : values()) {
            if (type.resourceLocation.getPath().equalsIgnoreCase(location.getPath())) {
                return type;
            }
        }
        return null;
    }

    public static ExportRecipeType fromName(String name) {
        if (name == null || name.isBlank()) return null;
        String clean = name.trim().toLowerCase(Locale.ROOT);
        ResourceLocation parsed = ResourceLocation.tryParse(clean);
        if (parsed != null) {
            ExportRecipeType type = fromId(parsed);
            if (type != null) return type;
        }
        for (ExportRecipeType type : values()) {
            if (type.id.equalsIgnoreCase(clean) || type.resourceLocation.getPath().equalsIgnoreCase(clean)) {
                return type;
            }
        }
        return null;
    }

    public static List<ResourceLocation> getAllResourceLocations() {
        List<ResourceLocation> list = new ArrayList<>();
        for (ExportRecipeType type : values()) {
            list.add(type.resourceLocation);
        }
        return list;
    }

    public static List<String> getMainIds() {
        List<String> list = new ArrayList<>();
        for (ExportRecipeType type : values()) {
            list.add(type.id);
        }
        return list;
    }

    public static List<String> getAllSuggestionNames() {
        return getMainIds();
    }
}
