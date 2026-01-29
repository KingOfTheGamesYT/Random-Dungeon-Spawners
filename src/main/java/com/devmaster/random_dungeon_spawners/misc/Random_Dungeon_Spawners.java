package com.devmaster.random_dungeon_spawners.misc;

import com.devmaster.random_dungeon_spawners.config.BlacklistConfig;

import net.minecraft.block.SpawnerBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
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

    public static final String MOD_ID = "random_dungeon_spawners";
    public static final Logger LOGGER = LogManager.getLogger();

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

            for (EntityType<?> type : ForgeRegistries.ENTITIES.getValues()) {
                if (type == null || type.getClassification() == null) continue;

                // Hostile mobs only
                if (type.getClassification().getPeacefulCreature()) continue;

                ResourceLocation id = type.getRegistryName();
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

        World world = event.getWorld() instanceof World ? (World) event.getWorld() : null;
        if (world == null || VALID_MOBS.isEmpty()) return;

        TileEntity tile = world.getTileEntity(event.getPos());
        if (tile instanceof MobSpawnerTileEntity) {
            MobSpawnerTileEntity spawner = (MobSpawnerTileEntity) tile;
            EntityType<?> randomMob = VALID_MOBS.get(world.rand.nextInt(VALID_MOBS.size()));
            spawner.getSpawnerBaseLogic().setEntityType(randomMob);
        }
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }
}
