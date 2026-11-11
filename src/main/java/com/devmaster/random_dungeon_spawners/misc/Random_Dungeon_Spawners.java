package com.devmaster.random_dungeon_spawners.misc;

import com.devmaster.random_dungeon_spawners.config.BlacklistConfig;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = Random_Dungeon_Spawners.MODID, name = Random_Dungeon_Spawners.NAME, version = Random_Dungeon_Spawners.VERSION, dependencies = "after:NetherDungeons")
public class Random_Dungeon_Spawners {

    public static final String MODID = "random_dungeon_spawners";
    public static final String NAME = "Random Dungeon Spawners";
    public static final String VERSION = "1.0.2";
    private static List<String> validMobs = new ArrayList<String>();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File configFile = new File(event.getModConfigurationDirectory(), MODID + ".cfg");
        BlacklistConfig.init(configFile);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        DungeonHooks.removeDungeonMob("Zombie");
        DungeonHooks.removeDungeonMob("Skeleton");
        DungeonHooks.removeDungeonMob("Spider");

        validMobs = collectMobs(false);

        System.out.println("[RandomDungeonSpawners] Filter mode: " + BlacklistConfig.FILTER_MODE);
        System.out.println("[RandomDungeonSpawners] Adding " + validMobs.size() + " mobs to dungeon list");

        for (String mob : validMobs) {
            DungeonHooks.addDungeonMob(mob, 100);
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (!BlacklistConfig.RANDOMIZE_NETHER_DUNGEONS) return;
        if (!Loader.isModLoaded("NetherDungeons")) return;

        List<String> netherMobs = collectMobs(true);
        if (netherMobs.isEmpty()) {
            System.err.println("[RandomDungeonSpawners] Nether Dungeons mob list is empty after filtering, leaving its defaults alone");
            return;
        }
        NetherDungeonsCompat.apply(netherMobs);
    }

    private static List<String> collectMobs(boolean nether) {
        List<String> result = new ArrayList<String>();

        for (Object keyObj : EntityList.stringToClassMapping.keySet()) {
            String id = (String) keyObj;
            Class<?> entityClass = (Class<?>) EntityList.stringToClassMapping.get(id);

            if (entityClass == null) continue;
            if (!EntityMob.class.isAssignableFrom(entityClass)) continue;

            boolean allowed = nether ? BlacklistConfig.isNetherEntityAllowed(id)
                    : BlacklistConfig.isEntityAllowed(id);
            if (!allowed) {
                System.out.println("[RandomDungeonSpawners] Skipping filtered mob: " + id);
                continue;
            }
            result.add(id);
        }
        return result;
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent event) {
        if (!BlacklistConfig.RANDOMIZE_ALL_SPAWNERS) return;

        World world = event.world;
        if (world.isRemote || validMobs.isEmpty()) return;
        if (world.getBlock(event.x, event.y, event.z) != Blocks.mob_spawner) return;

        TileEntity tile = world.getTileEntity(event.x, event.y, event.z);
        if (!(tile instanceof TileEntityMobSpawner)) return;

        String mobId = validMobs.get(world.rand.nextInt(validMobs.size()));
        if (!EntityList.stringToClassMapping.containsKey(mobId)) return;

        ((TileEntityMobSpawner) tile).func_145881_a().setEntityName(mobId);
        tile.markDirty();
        world.markBlockForUpdate(event.x, event.y, event.z);
    }
}