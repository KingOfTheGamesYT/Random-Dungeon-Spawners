package com.devmaster.random_dungeon_spawners.misc;

import com.devmaster.random_dungeon_spawners.config.BlacklistConfig;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.EntityList;

import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = Random_Dungeon_Spawners.MODID, name = Random_Dungeon_Spawners.NAME, version = Random_Dungeon_Spawners.VERSION)
public class Random_Dungeon_Spawners {

    public static final String MODID = "random_dungeon_spawners";
    public static final String NAME = "Random Dungeon Spawners";
    public static final String VERSION = "1.0";
    private static List<String> validMobs = new ArrayList<String>();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File configFile = new File(event.getModConfigurationDirectory(), MODID + ".cfg");
        BlacklistConfig.init(configFile);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Remove vanilla dungeon mobs
        DungeonHooks.removeDungeonMob("Zombie");
        DungeonHooks.removeDungeonMob("Skeleton");
        DungeonHooks.removeDungeonMob("Spider");

        validMobs.clear();

        System.out.println("[RandomDungeonSpawners] Filter mode: " + BlacklistConfig.FILTER_MODE);

        for (Object keyObj : EntityList.stringToClassMapping.keySet()) {
            String id = (String) keyObj;
            Class<?> entityClass = (Class<?>) EntityList.stringToClassMapping.get(id);

            if (entityClass == null) continue;
            if (!EntityMob.class.isAssignableFrom(entityClass)) continue;

            if (!BlacklistConfig.isEntityAllowed(id)) {
                System.out.println("[RandomDungeonSpawners] Skipping filtered mob: " + id);
                continue;
            }

            validMobs.add(id);
        }

        System.out.println("[RandomDungeonSpawners] Adding " + validMobs.size() + " mobs to dungeon list");

        for (String mob : validMobs) {
            DungeonHooks.addDungeonMob(mob, 100);
        }
    }
}