package com.devmaster.random_dungeon_spawners.misc;

import fabricator77.netherdungeons.WorldGenNetherDungeons;

import java.lang.reflect.Field;
import java.util.List;

public final class NetherDungeonsCompat {

    private NetherDungeonsCompat() {}

    public static void apply(List<String> mobs) {

        try {
            Field f = WorldGenNetherDungeons.class.getDeclaredField("dungeonMobs");
            f.setAccessible(true);
            ((List<?>) f.get(null)).clear();
        } catch (Exception e) {
            System.err.println("[RandomDungeonSpawners] Could not clear Nether Dungeons default mobs: " + e);
        }

        for (String id : mobs) {
            WorldGenNetherDungeons.addDungeonMob(id, 100);
        }

        System.out.println("[RandomDungeonSpawners] Added " + mobs.size() + " mobs to Nether Dungeons spawners");
    }
}