package ru.auriny.recipeexporter;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;
import ru.auriny.recipeexporter.menu.botania.*;
import ru.auriny.recipeexporter.menu.valoria.*;
import ru.auriny.recipeexporter.menu.vanilla.*;
import ru.auriny.recipeexporter.model.ExportRecipeType;

public class KRECommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("kre")
                        .then(Commands.argument("recipe_type", ResourceLocationArgument.id())
                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggestResource(
                                        ExportRecipeType.getAllResourceLocations(), builder))
                                .executes(ctx -> {
                                    ResourceLocation loc = ResourceLocationArgument.getId(ctx, "recipe_type");
                                    return openMenu(ctx.getSource(), loc);
                                })
                        )
                        .executes(ctx -> {
                            ctx.getSource().sendFailure(Component.translatable("komaru_recipe_exporter.command.usage",
                                    String.join(", ", ExportRecipeType.getMainIds())));
                            return 0;
                        })
        );
    }

    private static int openMenu(CommandSourceStack source, ResourceLocation location) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("komaru_recipe_exporter.command.player_only"));
            return 0;
        }

        ExportRecipeType type = ExportRecipeType.fromId(location);
        if (type == null) {
            source.sendFailure(Component.translatable("komaru_recipe_exporter.command.unknown",
                    location, String.join(", ", ExportRecipeType.getMainIds())));
            return 0;
        }

        switch (type) {
            // Vanilla
            case CRAFTING_SHAPED -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new ShapedCraftingMenu(id, inv)));

            case CRAFTING_SHAPELESS -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new ShapelessCraftingMenu(id, inv)));

            case SMELTING, BLASTING, SMOKING, CAMPFIRE_COOKING, VALORIA_KILN -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new CookingMenu(id, inv, type.getId())),
                    buf -> buf.writeUtf(type.getId()));

            case STONECUTTING -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new StonecuttingMenu(id, inv)));

            case SMITHING_TRANSFORM, SMITHING_TRIM -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new SmithingMenu(id, inv, type.getId())),
                    buf -> buf.writeUtf(type.getId()));

            // Botania
            case MANA_INFUSION -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new ManaInfusionMenu(id, inv)));

            case PETAL_APOTHECARY -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new PetalApothecaryMenu(id, inv)));

            case RUNIC_ALTAR -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new RunicAltarMenu(id, inv)));

            case PURE_DAISY -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new PureDaisyMenu(id, inv)));

            case ELVEN_TRADE -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new ElvenTradeMenu(id, inv)));

            case TERRA_PLATE -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new TerraPlateMenu(id, inv)));

            case BREW -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new BreweryMenu(id, inv)));

            case ORECHID -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new OrechidMenu(id, inv)));

            // Valoria
            case VALORIA_KEG_BREWERY -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new KegBreweryMenu(id, inv)));

            case VALORIA_JEWELRY -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new JewelryMenu(id, inv)));

            case VALORIA_MANIPULATOR -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new ManipulatorMenu(id, inv)));

            case VALORIA_HEAVY_WORKBENCH -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new HeavyWorkbenchMenu(id, inv)));

            case VALORIA_CRUSHER -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new CrusherMenu(id, inv)));

            case VALORIA_ALCHEMY -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new AlchemyMenu(id, inv)));

            case VALORIA_HEAVY_ANVIL -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new HeavyAnvilMenu(id, inv)));

            case VALORIA_SOUL_INFUSER -> NetworkHooks.openScreen(player, new KREMenuProvider(
                    type.getTitleComponent(), (id, inv, p) -> new SoulInfuserMenu(id, inv)));
        }

        return 1;
    }

    private record KREMenuProvider(Component title, KREMenuFactory factory) implements MenuProvider {
        @Override
        public Component getDisplayName() {
            return title;
        }

        @Override
        public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
            return factory.create(id, inventory, player);
        }
    }

    @FunctionalInterface
    private interface KREMenuFactory {
        AbstractContainerMenu create(int id, Inventory inventory, Player player);
    }
}
