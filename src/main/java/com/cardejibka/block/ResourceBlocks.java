package com.cardejibka.block;

import com.cardejibka.ResourceBoxes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ResourceBlocks {

    public static final Block RESOURCE_ORE = registerBlock("resource_ore",
            new ResourceOreBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ResourceBoxes.MOD_ID, "resource_ore")))
                    .strength(3.0f, 3.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)));

    public static final Block DEEPSLATE_RESOURCE_ORE = registerBlock("deepslate_resource_ore",
            new ResourceOreBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ResourceBoxes.MOD_ID, "deepslate_resource_ore")))
                    .strength(4.5f, 3.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)));

    public static final Block RESOURCE_BLOCK = registerBlock("resource_block",
            new Block(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ResourceBoxes.MOD_ID, "resource_block")))
                    .strength(4.5f, 4.5f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)));

    private static Block registerBlockWithoutBlockItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(ResourceBoxes.MOD_ID, name), block);
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(ResourceBoxes.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(ResourceBoxes.MOD_ID, name),
                new BlockItem(block, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ResourceBoxes.MOD_ID, name)))));
    }

    public static void registerModBlocks() {
        ResourceBoxes.LOGGER.info("Registering Mod Blocks for " + ResourceBoxes.MOD_ID);
    }
}