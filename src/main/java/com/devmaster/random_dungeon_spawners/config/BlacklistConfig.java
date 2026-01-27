package com.devmaster.random_dungeon_spawners.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlacklistConfig {

    private static Configuration config;

    public static List<String> ENTITY_BLACKLIST = new ArrayList<String>();
    public static List<String> ENTITY_WHITELIST = new ArrayList<String>();

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
                //Vanilla Bosses
                "WitherBoss",
                "EnderDragon",

                // Draconic Evolution bosses (1.7.10 entity IDs)
                "DraconicEvolution.GuardianWither",
                "DraconicEvolution.ChaosGuardian",

                //Neutral
                "Giant",
                "Slime",
                "PigZombie"
        };

        String[] defaultWhitelist = new String[] {
                "Zombie",
                "Skeleton",
                "Spider"
        };

        FILTER_MODE = FilterMode.valueOf(
                config.getString(
                        "filterMode",
                        "DungeonSpawnerFilter",
                        FilterMode.BLACKLIST.name(),
                        "Filter mode for dungeon spawners." +
                                "BLACKLIST = allow all except listed mobs" +
                                "WHITELIST = allow only listed mobs"
                ).toUpperCase()
        );

        ENTITY_BLACKLIST = Arrays.asList(config.getStringList(
                "entityBlacklist",
                "DungeonSpawnerFilter",
                defaultBlacklist,
                "Entities that will NOT be added to dungeon spawners (used in  BLACKLIST mode"
        ));

        ENTITY_WHITELIST = Arrays.asList(config.getStringList(
                "entityWhitelist",
                "DungeonSpawnerFilter",
                defaultWhitelist,
                "Entities that ARE allowed in dungeon spawners (used in WHITELIST mode)"
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
}
