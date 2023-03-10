package net.fabricmc.BuildingDimension;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.BuildingDimension.Commands.SwitchDimension;
import net.fabricmc.BuildingDimension.Commands.SyncDimension;
import net.fabricmc.BuildingDimension.Commands.Teleport;
import net.fabricmc.BuildingDimension.World.SavedData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.literal;

public class BuildingDimension implements ModInitializer {

	public static final String MOD_ID = "building_dimension";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Identifier OVERWORLD = new Identifier(MOD_ID, "overworld");

	public static final RegistryKey<DimensionOptions> OVERWORLD_KEY = RegistryKey.of(
			Registry.DIMENSION_KEY,
			OVERWORLD
	);

	public static RegistryKey<World> OVERWORLD_WORLD_KEY = RegistryKey.of(
			Registry.WORLD_KEY,
			OVERWORLD_KEY.getValue()
	);

	public static SavedData WORLD_DATA;

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(this::registerCommands);
		registerEvents();

		OVERWORLD_WORLD_KEY = RegistryKey.of(
				Registry.WORLD_KEY,
				OVERWORLD
		);
	}

	private void registerCommands(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
		dispatcher
				.register(literal("creative")
						.executes(SwitchDimension::switch_dim)

						.then(literal("sync")
								.executes(SyncDimension::sync_chunk_one)
								.then(CommandManager.argument("radius", IntegerArgumentType.integer(0, 8))
												.executes(SyncDimension::sync_chunk_radius)
								)
						)
						.then(literal("teleport")
								.then(CommandManager.argument("player", EntityArgumentType.player())
										.executes(Teleport::teleport)
								)
						)
				);
	}

	private void registerEvents() {
		net.fabricmc.BuildingDimension.Events.SyncDimension.init();
	}
}
