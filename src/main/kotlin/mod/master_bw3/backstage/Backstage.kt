package mod.master_bw3.backstage

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import dev.galacticraft.dynamicdimensions.api.DynamicDimensionRegistry
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.util.Identifier
import net.minecraft.util.math.intprovider.UniformIntProvider
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.dimension.DimensionType.MonsterSettings
import net.minecraft.world.dimension.DimensionTypes
import net.minecraft.world.gen.chunk.FlatChunkGenerator
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig
import org.slf4j.LoggerFactory
import java.util.*


object Backstage : ModInitializer {
    private val logger = LoggerFactory.getLogger("backstage")

    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        logger.info("Hello Fabric world!")



        CommandRegistrationCallback.EVENT.register({ dispatcher, registryAccess, environment ->
            dispatcher.register(literal("dimension")
                .requires { source -> source.hasPermissionLevel(2) }
                .then(literal("create")
                    .then(argument("id", StringArgumentType.string())
                        .executes { context ->

                            val server: MinecraftServer = context.source.server
                            val registryAccess = server.registryManager
                            val biomeLookup = registryAccess.getWrapperOrThrow(RegistryKeys.BIOME)
                            val structureSetLookup = registryAccess.getWrapperOrThrow(RegistryKeys.STRUCTURE_SET)
                            val featureLookup = registryAccess.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE)


                            val chunkGenerator = FlatChunkGenerator(
                                FlatChunkGeneratorConfig.getDefaultConfig(
                                    biomeLookup,
                                    structureSetLookup,
                                    featureLookup
                                )
                            )
                            val dt = DimensionType(
                                OptionalLong.of(6000),
                                true,
                                false,
                                false,
                                false,
                                1.0,
                                false,
                                false,
                                -64,
                                384,
                                384,
                                BlockTags.INFINIBURN_OVERWORLD,  // infiniburn
                                Identifier.of("minecraft", "overworld"),
                                1f,  // ambientLight
                                MonsterSettings(false, false, UniformIntProvider.create(0, 0), 0)
                            );
                            // create dim
                            val dynamicDimensionRegistry = DynamicDimensionRegistry.from(server)
                            dynamicDimensionRegistry.createDynamicDimension(
                                Identifier.of("backstage", StringArgumentType.getString(context, "id")),
                                chunkGenerator,
                                dt //registryAccess.getWrapperOrThrow(RegistryKeys.DIMENSION_TYPE).getOrThrow(DimensionTypes.OVERWORLD).value()
                            )
                            1
                        }
                    )
                )
                .then(literal("remove")
                    .then(argument("id", StringArgumentType.string())
                        .executes { context ->

                            val server: MinecraftServer = context.source.server

                            // remove dim
                            val registry = DynamicDimensionRegistry.from(server)
                            registry.deleteDynamicDimension(
                                Identifier.of("backstage", StringArgumentType.getString(context, "id")),
                                null
                            )
                            1
                        }
                    )
                )
            )
        })
    }
}