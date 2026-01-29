package com.devmaster.random_dungeon_spawners.config;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;

public class BlacklistConfig {

    public static ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ENTITY_BLACKLIST;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ENTITY_WHITELIST;
    public static ForgeConfigSpec.EnumValue<FilterMode> FILTER_MODE;
    public static ForgeConfigSpec.BooleanValue RANDOMIZE_ALL_SPAWNERS;

    public enum FilterMode {
        BLACKLIST,
        WHITELIST
    }

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("DungeonSpawner");

        FILTER_MODE = builder.defineEnum(
                "filterMode",
                FilterMode.BLACKLIST
        );

        ENTITY_BLACKLIST = builder.defineList(
                "blacklistedEntities",
                Arrays.asList(
                        "minecraft:ender_dragon",
                        "minecraft:wither",
                        "minecraft:elder_guardian",
                        "minecraft:slime",
                        "minecraft:giant",
                        "minecraft:zombified_piglin"
                ),
                o -> o instanceof String && isValidEntity((String) o)
        );

        ENTITY_WHITELIST = builder.defineList(
                "whitelistedEntities",
                Arrays.asList(
                        "minecraft:zombie",
                        "minecraft:skeleton",
                        "minecraft:spider"
                ),
                o -> o instanceof String && isValidEntity((String) o)
        );

        RANDOMIZE_ALL_SPAWNERS = builder.define(
                "randomizeAllSpawners",
                true
        );

        builder.pop();
        COMMON_CONFIG = builder.build();
    }

    private static boolean isValidEntity(String id) {
        ResourceLocation rl = ResourceLocation.tryCreate(id);
        return rl != null && ForgeRegistries.ENTITIES.containsKey(rl);
    }

    public static boolean isEntityAllowed(String entityId) {
        if (FILTER_MODE.get() == FilterMode.WHITELIST) {
            return ENTITY_WHITELIST.get().contains(entityId);
        }
        return !ENTITY_BLACKLIST.get().contains(entityId);
    }
}

