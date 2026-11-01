package ru.auriny.recipeexporter;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ru.auriny.recipeexporter.menu.KREMenus;
import ru.auriny.recipeexporter.screen.botania.*;
import ru.auriny.recipeexporter.screen.valoria.*;
import ru.auriny.recipeexporter.screen.vanilla.*;

@Mod(KomaruRecipeExporter.MODID)
public class KomaruRecipeExporter {
    public static final String MODID = "komaru_recipe_exporter";

    public KomaruRecipeExporter() {
        @SuppressWarnings("removal") IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        KREMenus.MENUS.register(modEventBus);
        modEventBus.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Vanilla screens
            MenuScreens.register(KREMenus.SHAPED_CRAFTING.get(), ShapedCraftingScreen::new);
            MenuScreens.register(KREMenus.SHAPELESS_CRAFTING.get(), ShapelessCraftingScreen::new);
            MenuScreens.register(KREMenus.COOKING.get(), CookingScreen::new);
            MenuScreens.register(KREMenus.STONECUTTING.get(), StonecuttingScreen::new);
            MenuScreens.register(KREMenus.SMITHING.get(), SmithingScreen::new);

            // Botania screens
            MenuScreens.register(KREMenus.MANA_INFUSION.get(), ManaInfusionScreen::new);
            MenuScreens.register(KREMenus.PETAL_APOTHECARY.get(), PetalApothecaryScreen::new);
            MenuScreens.register(KREMenus.RUNIC_ALTAR.get(), RunicAltarScreen::new);
            MenuScreens.register(KREMenus.PURE_DAISY.get(), PureDaisyScreen::new);
            MenuScreens.register(KREMenus.ELVEN_TRADE.get(), ElvenTradeScreen::new);
            MenuScreens.register(KREMenus.TERRA_PLATE.get(), TerraPlateScreen::new);
            MenuScreens.register(KREMenus.BREWERY.get(), BreweryScreen::new);
            MenuScreens.register(KREMenus.ORECHID.get(), OrechidScreen::new);

            // Valoria screens
            MenuScreens.register(KREMenus.VALORIA_KEG_BREWERY.get(), KegBreweryScreen::new);
            MenuScreens.register(KREMenus.VALORIA_JEWELRY.get(), JewelryScreen::new);
            MenuScreens.register(KREMenus.VALORIA_MANIPULATOR.get(), ManipulatorScreen::new);
            MenuScreens.register(KREMenus.VALORIA_HEAVY_WORKBENCH.get(), HeavyWorkbenchScreen::new);
            MenuScreens.register(KREMenus.VALORIA_CRUSHER.get(), CrusherScreen::new);
            MenuScreens.register(KREMenus.VALORIA_ALCHEMY.get(), AlchemyScreen::new);
            MenuScreens.register(KREMenus.VALORIA_HEAVY_ANVIL.get(), HeavyAnvilScreen::new);
            MenuScreens.register(KREMenus.VALORIA_SOUL_INFUSER.get(), SoulInfuserScreen::new);
        });
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        KRECommands.register(event.getDispatcher());
    }
}
