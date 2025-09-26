package com.cardejibka.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import com.cardejibka.ResourceBoxes;
import net.minecraft.world.gen.feature.PlacedFeature;

public class ModOreGeneration {
    public static final RegistryKey<PlacedFeature> RESOURCE_ORE_PLACED_KEY = RegistryKey.of(
            RegistryKeys.PLACED_FEATURE, Identifier.of(ResourceBoxes.MOD_ID, "resource_ore_placed")
    );

    public static void generateOres() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                RESOURCE_ORE_PLACED_KEY
        );
    }
}