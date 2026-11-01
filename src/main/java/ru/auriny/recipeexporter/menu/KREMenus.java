package ru.auriny.recipeexporter.menu;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.auriny.recipeexporter.KomaruRecipeExporter;
import ru.auriny.recipeexporter.menu.botania.*;
import ru.auriny.recipeexporter.menu.valoria.*;
import ru.auriny.recipeexporter.menu.vanilla.*;

public class KREMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, KomaruRecipeExporter.MODID);

    // Vanilla
    public static final RegistryObject<MenuType<ShapedCraftingMenu>> SHAPED_CRAFTING =
            MENUS.register("shaped_crafting", () -> IForgeMenuType.create((id, inv, data) -> new ShapedCraftingMenu(id, inv)));

    public static final RegistryObject<MenuType<ShapelessCraftingMenu>> SHAPELESS_CRAFTING =
            MENUS.register("shapeless_crafting", () -> IForgeMenuType.create((id, inv, data) -> new ShapelessCraftingMenu(id, inv)));

    public static final RegistryObject<MenuType<CookingMenu>> COOKING =
            MENUS.register("cooking", () -> IForgeMenuType.create((id, inv, data) -> new CookingMenu(id, inv, data.readUtf())));

    public static final RegistryObject<MenuType<StonecuttingMenu>> STONECUTTING =
            MENUS.register("stonecutting", () -> IForgeMenuType.create((id, inv, data) -> new StonecuttingMenu(id, inv)));

    public static final RegistryObject<MenuType<SmithingMenu>> SMITHING =
            MENUS.register("smithing", () -> IForgeMenuType.create((id, inv, data) -> new SmithingMenu(id, inv, data.readUtf())));

    // Botania
    public static final RegistryObject<MenuType<ManaInfusionMenu>> MANA_INFUSION =
            MENUS.register("mana_infusion", () -> IForgeMenuType.create((id, inv, data) -> new ManaInfusionMenu(id, inv)));

    public static final RegistryObject<MenuType<PetalApothecaryMenu>> PETAL_APOTHECARY =
            MENUS.register("petal_apothecary", () -> IForgeMenuType.create((id, inv, data) -> new PetalApothecaryMenu(id, inv)));

    public static final RegistryObject<MenuType<RunicAltarMenu>> RUNIC_ALTAR =
            MENUS.register("runic_altar", () -> IForgeMenuType.create((id, inv, data) -> new RunicAltarMenu(id, inv)));

    public static final RegistryObject<MenuType<PureDaisyMenu>> PURE_DAISY =
            MENUS.register("pure_daisy", () -> IForgeMenuType.create((id, inv, data) -> new PureDaisyMenu(id, inv)));

    public static final RegistryObject<MenuType<ElvenTradeMenu>> ELVEN_TRADE =
            MENUS.register("elven_trade", () -> IForgeMenuType.create((id, inv, data) -> new ElvenTradeMenu(id, inv)));

    public static final RegistryObject<MenuType<TerraPlateMenu>> TERRA_PLATE =
            MENUS.register("terra_plate", () -> IForgeMenuType.create((id, inv, data) -> new TerraPlateMenu(id, inv)));

    public static final RegistryObject<MenuType<BreweryMenu>> BREWERY =
            MENUS.register("brewery", () -> IForgeMenuType.create((id, inv, data) -> new BreweryMenu(id, inv)));

    public static final RegistryObject<MenuType<OrechidMenu>> ORECHID =
            MENUS.register("orechid", () -> IForgeMenuType.create((id, inv, data) -> new OrechidMenu(id, inv)));

    // Valoria
    public static final RegistryObject<MenuType<KegBreweryMenu>> VALORIA_KEG_BREWERY =
            MENUS.register("valoria_keg_brewery", () -> IForgeMenuType.create((id, inv, data) -> new KegBreweryMenu(id, inv)));

    public static final RegistryObject<MenuType<JewelryMenu>> VALORIA_JEWELRY =
            MENUS.register("valoria_jewelry", () -> IForgeMenuType.create((id, inv, data) -> new JewelryMenu(id, inv)));

    public static final RegistryObject<MenuType<ManipulatorMenu>> VALORIA_MANIPULATOR =
            MENUS.register("valoria_manipulator", () -> IForgeMenuType.create((id, inv, data) -> new ManipulatorMenu(id, inv)));

    public static final RegistryObject<MenuType<HeavyWorkbenchMenu>> VALORIA_HEAVY_WORKBENCH =
            MENUS.register("valoria_heavy_workbench", () -> IForgeMenuType.create((id, inv, data) -> new HeavyWorkbenchMenu(id, inv)));

    public static final RegistryObject<MenuType<CrusherMenu>> VALORIA_CRUSHER =
            MENUS.register("valoria_crusher", () -> IForgeMenuType.create((id, inv, data) -> new CrusherMenu(id, inv)));

    public static final RegistryObject<MenuType<AlchemyMenu>> VALORIA_ALCHEMY =
            MENUS.register("valoria_alchemy", () -> IForgeMenuType.create((id, inv, data) -> new AlchemyMenu(id, inv)));

    public static final RegistryObject<MenuType<HeavyAnvilMenu>> VALORIA_HEAVY_ANVIL =
            MENUS.register("valoria_heavy_anvil", () -> IForgeMenuType.create((id, inv, data) -> new HeavyAnvilMenu(id, inv)));

    public static final RegistryObject<MenuType<SoulInfuserMenu>> VALORIA_SOUL_INFUSER =
            MENUS.register("valoria_soul_infuser", () -> IForgeMenuType.create((id, inv, data) -> new SoulInfuserMenu(id, inv)));
}
