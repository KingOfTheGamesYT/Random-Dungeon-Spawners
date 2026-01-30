package com.devmaster.random_dungeon_spawners.misc;

import com.devmaster.random_dungeon_spawners.config.BlacklistConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SpawnerBlock;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


@Mod("random_dungeon_spawners")
public class Random_Dungeon_Spawners {
    public static final Logger LOGGER = LogManager.getLogger("Random Dungeon Spawners");
    public static final String MOD_ID = "random_dungeon_spawners";
    private static final List<EntityType<?>> VALID_MOBS = new ArrayList<>();

    public Random_Dungeon_Spawners() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BlacklistConfig.COMMON_CONFIG, "random_dungeon_spawners-config.toml");
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

            // Remove vanilla mobs
            DungeonHooks.removeDungeonMob(EntityType.ZOMBIE);
            DungeonHooks.removeDungeonMob(EntityType.SKELETON);
            DungeonHooks.removeDungeonMob(EntityType.SPIDER);

            VALID_MOBS.clear();

            for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES.getValues()) {
                if (type == null || type.getCategory() == null) continue;

                // Hostile mobs only
                if (type.getCategory().isFriendly()) continue;

                ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
                if (id == null) continue;

                if (!BlacklistConfig.isEntityAllowed(id.toString())) {
                    LOGGER.debug("Skipping filtered mob: {}", id);
                    continue;
                }

                VALID_MOBS.add(type);
            }

            LOGGER.info("Adding {} mobs to dungeon spawner pool", VALID_MOBS.size());

            for (EntityType<?> type : VALID_MOBS) {
                DungeonHooks.addDungeonMob(type, 100);
            }
        });
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!BlacklistConfig.RANDOMIZE_ALL_SPAWNERS.get()) return;
        if (!(event.getPlacedBlock().getBlock() instanceof SpawnerBlock)) return;
        BlockPos pos = event.getPos();
        Level world = event.getLevel() instanceof Level ? (Level) event.getLevel() : null;
        if (world == null || VALID_MOBS.isEmpty()) return;

        BlockEntity tile = world.getBlockEntity(event.getPos());
        if (tile instanceof SpawnerBlockEntity) {
            SpawnerBlockEntity spawner = (SpawnerBlockEntity) tile;
            EntityType<?> randomMob = VALID_MOBS.get(world.random.nextInt(VALID_MOBS.size()));
            spawner.getSpawner().setEntityId(randomMob,world,world.random,pos);
        }
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }
}