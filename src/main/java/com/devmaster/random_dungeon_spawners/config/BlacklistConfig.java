package com.devmaster.random_dungeon_spawners.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlacklistConfig {

    private static Configuration config;
    public static List<String> ENTITY_BLACKLIST = new ArrayList<>();
    public static boolean RANDOMIZE_ALL_SPAWNERS = true;

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

        ENTITY_BLACKLIST = Arrays.asList(config.getStringList(
                "blacklistedEntities",
                "DungeonSpawnerBlacklist",
                defaultBlacklist,
                "List of entity IDs to exclude from dungeon spawners"
        ));

        RANDOMIZE_ALL_SPAWNERS = config.getBoolean(
                "randomizeAllSpawners",
                "DungeonSpawnerBlacklist",
                true,
                "If true, any mob spawner placed in the world (by players) will be randomized"
        );

        if (config.hasChanged()) {
            config.save();
        }
    }
}