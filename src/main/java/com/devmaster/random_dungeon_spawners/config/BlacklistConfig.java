package com.devmaster.random_dungeon_spawners.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlacklistConfig {

    private static Configuration config;

    public static List<String> ENTITY_BLACKLIST = new ArrayList<>();
    public static List<String> ENTITY_WHITELIST = new ArrayList<>();

    public static boolean RANDOMIZE_ALL_SPAWNERS = true;
    public static FilterMode FILTER_MODE = FilterMode.BLACKLIST;

    public enum FilterMode {
        BLACKLIST,
        WHITELIST
    }

    public static void init(File configFile) {
        config = new Configuration(configFile);
        syncConfig();
    }

    public static void syncConfig() {

        String[] defaultBlacklist = new String[] {
                "minecraft:wither",
                "minecraft:ender_dragon",
                "minecraft:elder_guardian",
                "minecraft:slime",
                "minecraft:zombie_pigman",
                "minecraft:giant",
                "draconicevolution:guardian_wither",
                "draconicevolution:draconic_guardian",
                "aquamirae:maze_mother",
                "blue_skies:seclam",
                "cataclysm:deepling_warlock",
                "deeperdarker:shriek_worm",
                "mowziesmobs:grottol",
                "iceandfire:dread_horse",
                "alexmobs:bone_serpent_part"
        };

        String[] defaultWhitelist = new String[] {
                "minecraft:zombie",
                "minecraft:skeleton",
                "minecraft:spider"
        };

        FILTER_MODE = FilterMode.valueOf(
                config.getString(
                        "filterMode",
                        "DungeonSpawnerBlacklist",
                        FilterMode.BLACKLIST.name(),
                        "BLACKLIST = allow all except listed mobs\n" +
                                "WHITELIST = allow only listed mobs"
                ).toUpperCase()
        );

        ENTITY_BLACKLIST = Arrays.asList(config.getStringList(
                "blacklistedEntities",
                "DungeonSpawnerBlacklist",
                defaultBlacklist,
                "List of entity IDs excluded when in BLACKLIST mode"
        ));

        ENTITY_WHITELIST = Arrays.asList(config.getStringList(
                "whitelistedEntities",
                "DungeonSpawnerBlacklist",
                defaultWhitelist,
                "List of entity IDs allowed when in WHITELIST mode"
        ));

        RANDOMIZE_ALL_SPAWNERS = config.getBoolean(
                "randomizeAllSpawners",
                "DungeonSpawnerBlacklist",
                true,
                "If true, any mob spawner placed in the world will be randomized"
        );

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static boolean isEntityAllowed(String entityId) {
        if (FILTER_MODE == FilterMode.WHITELIST) {
            return ENTITY_WHITELIST.contains(entityId);
        }
        return !ENTITY_BLACKLIST.contains(entityId);
    }
}