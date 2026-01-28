package com.devmaster.random_dungeon_spawners.misc;

import com.devmaster.random_dungeon_spawners.config.BlacklistConfig;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.block.BlockMobSpawner;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.entity.EntityList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mod(modid = Random_Dungeon_Spawners.MODID, name = Random_Dungeon_Spawners.NAME, version = Random_Dungeon_Spawners.VERSION)
public class Random_Dungeon_Spawners {

    public static final String MODID = "random_dungeon_spawners";
    public static final String NAME = "Random Dungeon Spawners";
    public static final String VERSION = "1.0.1";

    private static List<String> validMobs = new ArrayList<>();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File configFile = new File(event.getModConfigurationDirectory(), MODID + ".cfg");
        BlacklistConfig.init(configFile);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        DungeonHooks.removeDungeonMob(new ResourceLocation("minecraft", "zombie"));
        DungeonHooks.removeDungeonMob(new ResourceLocation("minecraft", "skeleton"));
        DungeonHooks.removeDungeonMob(new ResourceLocation("minecraft", "spider"));

        Set<ResourceLocation> entityKeys = EntityList.getEntityNameList();
        validMobs.clear();

        for (ResourceLocation rl : entityKeys) {
            String id = rl.toString();
            Class<?> entityClass = EntityList.getClass(rl);

            if (entityClass == null) continue;

            // Only hostile mobs (monsters & slimes)
            if (!(net.minecraft.entity.monster.EntityMob.class.isAssignableFrom(entityClass))) {
                continue;
            }

            if (!BlacklistConfig.isEntityAllowed(id)) {
                System.out.println("[RandomDungeonSpawners] Skipping filtered mob: " + id);
                continue;
            }


            validMobs.add(id);
        }

        System.out.println("[RandomDungeonSpawners] Adding " + validMobs.size() + " mobs to dungeon list");

        for (String mob : validMobs) {
            DungeonHooks.addDungeonMob(new ResourceLocation(mob), 100);
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent event) {
        if (!BlacklistConfig.RANDOMIZE_ALL_SPAWNERS) return;

        World world = event.getWorld();

        if (event.getPlacedBlock().getBlock() instanceof BlockMobSpawner) {
            TileEntity tile = world.getTileEntity(event.getPos());
            if (tile instanceof TileEntityMobSpawner && !validMobs.isEmpty()) {
                TileEntityMobSpawner spawner = (TileEntityMobSpawner) tile;
                MobSpawnerBaseLogic logic = spawner.getSpawnerBaseLogic();

                // Pick a random mob from the valid list
                String mobId = validMobs.get(world.rand.nextInt(validMobs.size()));
                ResourceLocation rl = new ResourceLocation(mobId);

                if (EntityList.isRegistered(rl)) {
                    logic.setEntityId(rl);
                }
            }
        }
    }
}