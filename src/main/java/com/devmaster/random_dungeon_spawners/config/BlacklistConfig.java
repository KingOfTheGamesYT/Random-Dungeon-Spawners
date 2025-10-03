package com.devmaster.random_dungeon_spawners.config;

import net.minecraft.resources.ResourceLocation;

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
                Arrays.asList(
                        "minecraft:wither",
                        "minecraft:ender_dragon",
                        "minecraft:elder_guardian",
                        "minecraft:slime",
                        "minecraft:zombified_piglin",
                        "minecraft:giant",
                        "draconicevolution:guardian_wither",
                        "draconicevolution:draconic_guardian"

                ),
                obj -> {
                    if (!(obj instanceof String)) return false;
                    ResourceLocation id = ResourceLocation.tryParse((String) obj);
                    return id != null && ForgeRegistries.ENTITY_TYPES.containsKey(id);
                }
        );

        builder.pop();
        COMMON_CONFIG = builder.build();
    }
}