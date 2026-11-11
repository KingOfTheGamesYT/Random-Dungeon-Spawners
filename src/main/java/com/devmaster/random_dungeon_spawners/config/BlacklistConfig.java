package com.devmaster.random_dungeon_spawners.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlacklistConfig {

    private static Configuration config;
    public static boolean RANDOMIZE_ALL_SPAWNERS = true;
    public static boolean RANDOMIZE_NETHER_DUNGEONS = true;
    public static List<String> ENTITY_BLACKLIST = new ArrayList<String>();
    public static List<String> ENTITY_WHITELIST = new ArrayList<String>();
    public static FilterMode FILTER_MODE = FilterMode.BLACKLIST;
    public static List<String> NETHER_ENTITY_BLACKLIST = new ArrayList<String>();
    public static List<String> NETHER_ENTITY_WHITELIST = new ArrayList<String>();
    public static FilterMode NETHER_FILTER_MODE = FilterMode.BLACKLIST;

    public enum FilterMode {
        BLACKLIST,
        WHITELIST
    }

    public static void init(File configFile) {
        config = new Configuration(configFile);
        syncConfig();
    }

    private static FilterMode parseMode(String value) {
        try {
            return FilterMode.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return FilterMode.BLACKLIST;
        }
    }

    public static void syncConfig() {
        String[] defaultBlacklist = new String[] {
                "WitherBoss",
                "EnderDragon",
                "DraconicEvolution.GuardianWither",
                "DraconicEvolution.ChaosGuardian",
                "Giant",
                "Slime",
                "PigZombie"
        };

        String[] defaultWhitelist = new String[] {
                "Zombie",
                "Skeleton",
                "Spider"
        };

        String[] defaultNetherBlacklist = new String[] {
                "WitherBoss",
                "EnderDragon",
                "DraconicEvolution.GuardianWither",
                "DraconicEvolution.ChaosGuardian",
                "Giant",
                "PigZombie",
                "Slime"
        };

        String[] defaultNetherWhitelist = new String[] {
                "Blaze",
                "Skeleton",
                "LavaSlime"
        };

        RANDOMIZE_ALL_SPAWNERS = config.getBoolean(
                "randomizeAllSpawners",
                "General",
                true,
                "If true, any mob spawner a player places is given a random mob (picked using the DungeonSpawnerFilter lists)."
        );

        RANDOMIZE_NETHER_DUNGEONS = config.getBoolean(
                "randomizeNetherDungeons",
                "General",
                true,
                "If true and the Nether Dungeons mod is installed, its spawners use a randomized mob list (NetherDungeonSpawnerFilter lists)."
        );

        FILTER_MODE = parseMode(config.getString(
                "filterMode",
                "DungeonSpawnerFilter",
                FilterMode.BLACKLIST.name(),
                "Filter mode for dungeon spawners. " +
                        "BLACKLIST = allow all except listed mobs. " +
                        "WHITELIST = allow only listed mobs."
        ));

        ENTITY_BLACKLIST = Arrays.asList(config.getStringList(
                "entityBlacklist",
                "DungeonSpawnerFilter",
                defaultBlacklist,
                "Entities that will NOT be added to dungeon spawners (used in BLACKLIST mode)."
        ));

        ENTITY_WHITELIST = Arrays.asList(config.getStringList(
                "entityWhitelist",
                "DungeonSpawnerFilter",
                defaultWhitelist,
                "Entities that ARE allowed in dungeon spawners (used in WHITELIST mode)."
        ));

        NETHER_FILTER_MODE = parseMode(config.getString(
                "netherFilterMode",
                "NetherDungeonSpawnerFilter",
                FilterMode.BLACKLIST.name(),
                "Filter mode for Nether Dungeons spawners. " +
                        "BLACKLIST = allow all except listed mobs. " +
                        "WHITELIST = allow only listed mobs."
        ));

        NETHER_ENTITY_BLACKLIST = Arrays.asList(config.getStringList(
                "netherEntityBlacklist",
                "NetherDungeonSpawnerFilter",
                defaultNetherBlacklist,
                "Entities that will NOT be added to Nether Dungeons spawners (used in BLACKLIST mode)."
        ));

        NETHER_ENTITY_WHITELIST = Arrays.asList(config.getStringList(
                "netherEntityWhitelist",
                "NetherDungeonSpawnerFilter",
                defaultNetherWhitelist,
                "Entities that ARE allowed in Nether Dungeons spawners (used in WHITELIST mode)."
        ));

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

    public static boolean isNetherEntityAllowed(String entityId) {
        if (NETHER_FILTER_MODE == FilterMode.WHITELIST) {
            return NETHER_ENTITY_WHITELIST.contains(entityId);
        }
        return !NETHER_ENTITY_BLACKLIST.contains(entityId);
    }
}