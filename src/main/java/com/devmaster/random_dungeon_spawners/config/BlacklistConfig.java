package com.devmaster.random_dungeon_spawners.config;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;

public class BlacklistConfig {

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ENTITY_BLACKLIST;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("DungeonSpawnerBlacklist");

        ENTITY_BLACKLIST = builder.defineList(
                "blacklistedEntities",
                Arrays.asList("minecraft:ender_dragon", "minecraft:wither", "minecraft:elder_guardian"),
                o -> o instanceof String && isValidEntity((String) o)
        );

        builder.pop();
        COMMON_CONFIG = builder.build();
    }

    private static boolean isValidEntity(String id) {
        ResourceLocation rl = ResourceLocation.tryCreate(id);
        return rl != null && ForgeRegistries.ENTITIES.containsKey(rl);
    }
}
