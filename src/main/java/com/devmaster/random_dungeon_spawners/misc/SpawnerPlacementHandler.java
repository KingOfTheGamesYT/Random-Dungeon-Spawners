package com.devmaster.random_dungeon_spawners.misc;

import com.devmaster.random_dungeon_spawners.config.BlacklistConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = "random_dungeon_spawners")
public class SpawnerPlacementHandler {

    private static final Random random = new Random();

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!BlacklistConfig.RANDOMIZE_ALL_SPAWNERS.get()) return;

        Level level = (Level) event.getLevel();
        if (event.getPlacedBlock().getBlock() == Blocks.SPAWNER) {
            BlockPos pos = event.getPos();
            BlockEntity be = level.getBlockEntity(pos);

            if (be instanceof SpawnerBlockEntity spawner) {
                EntityType<?> randomMob = DungeonHooks.getRandomDungeonMob(level.random);
                spawner.getSpawner().setEntityId(randomMob, level, level.random, pos);
            }
        }
    }
}
