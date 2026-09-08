package com.cardejibka.block;

import com.cardejibka.ResourceBoxes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ResourceBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, ResourceBoxes.MOD_ID);
    public static final DeferredRegister<Item> BLOCK_ITEMS =
            DeferredRegister.create(Registries.ITEM, ResourceBoxes.MOD_ID);

    public static final RegistryObject<Block> RESOURCE_ORE = registerBlock("resource_ore",
            () -> new ResourceOreBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final RegistryObject<Block> DEEPSLATE_RESOURCE_ORE = registerBlock("deepslate_resource_ore",
            () -> new ResourceOreBlock(BlockBehaviour.Properties.of()
                    .strength(4.5f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final RegistryObject<Block> RESOURCE_BLOCK = registerBlock("resource_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4.5f, 4.5f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    private static RegistryObject<Block> registerBlockWithoutBlockItem(String name, Supplier<Block> block) {
        return BLOCKS.register(name, block);
    }

    private static RegistryObject<Block> registerBlock(String name, Supplier<Block> block) {
        RegistryObject<Block> registered = BLOCKS.register(name, block);
        registerBlockItem(name, registered);
        return registered;
    }

    private static void registerBlockItem(String name, RegistryObject<Block> block) {
        BLOCK_ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void registerModBlocks() {
        ResourceBoxes.LOGGER.info("Registering Mod Blocks for " + ResourceBoxes.MOD_ID);
    }
}