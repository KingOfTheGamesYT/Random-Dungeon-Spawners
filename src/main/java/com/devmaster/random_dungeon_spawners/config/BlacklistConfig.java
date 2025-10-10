package com.devmaster.random_dungeon_spawners.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlacklistConfig {

    private static Configuration config;
    public static List<String> ENTITY_BLACKLIST = new ArrayList<String>();

    public static void init(File configFile) {
        config = new Configuration(configFile);
        syncConfig();
    }

    public static void syncConfig() {
        // Default blacklist for Minecraft 1.7.10 + Draconic Evolution
        String[] defaultBlacklist = new String[] {
                // Vanilla bosses
                "WitherBoss",
                "EnderDragon",
                "Giant",

                // Draconic Evolution bosses (1.7.10 entity IDs)
                "DraconicEvolution.GuardianWither",
                "DraconicEvolution.ChaosGuardian",

                // Optional: remove passive or special mobs you don't want
                "Slime",
                "PigZombie"
        };

        ENTITY_BLACKLIST = Arrays.asList(config.getStringList(
                "blacklistedEntities",
                "DungeonSpawnerBlacklist",
                defaultBlacklist,
                "List of entity IDs to exclude from dungeon spawners"
        ));

        if (config.hasChanged()) {
            config.save();
        }
    }
}