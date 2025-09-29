package com.devmaster.random_dungeon_spawners.misc;

import com.devmaster.random_dungeon_spawners.config.BlacklistConfig;

import net.minecraft.entity.EntityType;

import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

@Mod("random_dungeon_spawners")
public class Random_Dungeon_Spawners {
    public static final Logger LOGGER = LogManager.getLogger("Random Dungeon Spawners");
    public static final String MOD_ID = "random_dungeon_spawners";

    public Random_Dungeon_Spawners() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BlacklistConfig.COMMON_CONFIG, "random_dungeon_spawners-config.toml");
    }

    private void setup(final FMLCommonSetupEvent event) {
        DungeonHooks.removeDungeonMob(EntityType.ZOMBIE);
        DungeonHooks.removeDungeonMob(EntityType.SKELETON);
        DungeonHooks.removeDungeonMob(EntityType.SPIDER);

        List<String> blacklist = (List<String>) BlacklistConfig.ENTITY_BLACKLIST.get();

        List<EntityType<?>> validMobs = ForgeRegistries.ENTITIES.getValues().stream()
                .filter(type -> {
                    if (type == null || type.getClassification() == null || type.getRegistryName() == null) return false;
                    if (type.getClassification().getPeacefulCreature()) return false;

                    String id = type.getRegistryName().toString();
                    if (blacklist.contains(id)) {
                        System.out.println("[DungeonSpawner] Skipping blacklisted mob: " + id);
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());

        System.out.println("[RandomDungeonSpawners] Adding " + validMobs.size() + " mobs to dungeon list");

        for (EntityType<?> type : validMobs) {
            DungeonHooks.addDungeonMob(type, 100); // Equal weight
        }
    }
    private void doClientStuff(final FMLClientSetupEvent event) {
    }
}