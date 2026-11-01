package ru.auriny.recipeexporter.model;

import net.minecraft.resources.ResourceLocation;
import ru.auriny.recipeexporter.KomaruRecipeExporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum ExportRecipeType {
    // Vanilla
    CRAFTING_SHAPED("minecraft:crafting_shaped", "Shaped Crafting",
            new ResourceLocation("textures/gui/container/crafting_table.png"),
            "shaped", "crafting_shaped"),
    CRAFTING_SHAPELESS("minecraft:crafting_shapeless", "Shapeless Crafting",
            new ResourceLocation("textures/gui/container/crafting_table.png"),
            "shapeless", "crafting_shapeless"),
    SMELTING("minecraft:smelting", "Smelting",
            new ResourceLocation("textures/gui/container/furnace.png"),
            "smelting", "furnace"),
    BLASTING("minecraft:blasting", "Blasting",
            new ResourceLocation("textures/gui/container/blast_furnace.png"),
            "blasting", "blast_furnace"),
    SMOKING("minecraft:smoking", "Smoking",
            new ResourceLocation("textures/gui/container/smoker.png"),
            "smoking", "smoker"),
    CAMPFIRE_COOKING("minecraft:campfire_cooking", "Campfire Cooking",
            new ResourceLocation("textures/gui/container/smoker.png"),
            "campfire_cooking", "campfire"),
    STONECUTTING("minecraft:stonecutting", "Stonecutting",
            new ResourceLocation("textures/gui/container/stonecutter.png"),
            "stonecutting", "stonecutter"),
    SMITHING_TRANSFORM("minecraft:smithing_transform", "Smithing Transform",
            new ResourceLocation("textures/gui/container/smithing.png"),
            "smithing_transform", "smithing"),
    SMITHING_TRIM("minecraft:smithing_trim", "Smithing Trim",
            new ResourceLocation("textures/gui/container/smithing.png"),
            "smithing_trim", "trim"),

    // Botania
    MANA_INFUSION("botania:mana_infusion", "Mana Infusion",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/mana_infusion.png"),
            "mana_infusion", "mana_pool", "pool"),
    PETAL_APOTHECARY("botania:petal_apothecary", "Petal Apothecary",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/petal_apothecary.png"),
            "petal_apothecary", "apothecary"),
    RUNIC_ALTAR("botania:runic_altar", "Runic Altar",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/runic_altar.png"),
            "runic_altar", "altar"),
    PURE_DAISY("botania:pure_daisy", "Pure Daisy",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/pure_daisy.png"),
            "pure_daisy", "daisy"),
    ELVEN_TRADE("botania:elven_trade", "Elven Trade",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/elven_trade.png"),
            "elven_trade", "alfheim", "portal"),
    TERRA_PLATE("botania:terra_plate", "Terrestrial Agglomeration",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/terra_plate.png"),
            "terra_plate", "terrestrial_agglomeration", "plate"),
    BREW("botania:brew", "Botanical Brewery",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/brewery.png"),
            "brew", "brewery"),
    ORECHID("botania:orechid", "Orechid",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/orechid.png"),
            "orechid", "orechid_ignem", "marimorphosis"),

    // Valoria
    VALORIA_KEG_BREWERY("valoria:keg_brewery", "Keg Brewery",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/valoria/keg_brewery.png"),
            "keg", "keg_brewery"),
    VALORIA_JEWELRY("valoria:jewelry", "Jewelry Table",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/valoria/jewelry.png"),
            "jewelry", "jewelry_table"),
    VALORIA_MANIPULATOR("valoria:manipulator", "Manipulator",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/valoria/manipulator.png"),
            "manipulator"),
    VALORIA_HEAVY_WORKBENCH("valoria:heavy_workbench", "Heavy Workbench",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/valoria/heavy_workbench.png"),
            "heavy_workbench", "workbench"),
    VALORIA_CRUSHER("valoria:crusher", "Crusher",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/valoria/crusher.png"),
            "crusher", "stone_crusher"),
    VALORIA_ALCHEMY("valoria:alchemy", "Alchemy Station",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/valoria/alchemy.png"),
            "alchemy", "alchemy_station"),
    VALORIA_HEAVY_ANVIL("valoria:heavy_anvil", "Heavy Anvil",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/valoria/heavy_anvil.png"),
            "heavy_anvil", "anvil_valoria"),
    VALORIA_SOUL_INFUSER("valoria:soul_infuser", "Soul Infuser",
            new ResourceLocation(KomaruRecipeExporter.MODID, "textures/gui/valoria/soul_infuser.png"),
            "soul_infuser", "infuser"),
    VALORIA_KILN("valoria:kiln", "Kiln",
            new ResourceLocation("textures/gui/container/furnace.png"),
            "kiln", "valoria_kiln");

    private final String id;
    private final String displayName;
    private final ResourceLocation texture;
    private final List<String> aliases;

    ExportRecipeType(String id, String displayName, ResourceLocation texture, String... aliases) {
        this.id = id;
        this.displayName = displayName;
        this.texture = texture;
        this.aliases = Arrays.asList(aliases);
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public net.minecraft.network.chat.Component getTitleComponent() {
        return net.minecraft.network.chat.Component.translatable("komaru_recipe_exporter.title." + name().toLowerCase(Locale.ROOT));
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public static ExportRecipeType fromName(String name) {
        if (name == null || name.isBlank()) return null;
        String clean = name.trim().toLowerCase(Locale.ROOT);
        for (ExportRecipeType type : values()) {
            if (type.id.equalsIgnoreCase(clean)) {
                return type;
            }
            for (String alias : type.aliases) {
                if (alias.equalsIgnoreCase(clean)) {
                    return type;
                }
            }
        }
        return null;
    }

    public static List<String> getAllSuggestionNames() {
        List<String> list = new ArrayList<>();
        for (ExportRecipeType type : values()) {
            list.add(type.id);
            list.addAll(type.aliases);
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
}
