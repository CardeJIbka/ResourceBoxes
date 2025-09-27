package com.cardejibka.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;


public class ResourceOreBlock extends ExperienceDroppingBlock {
    public ResourceOreBlock(Settings settings) {
        super(UniformIntProvider.create(0, 2), settings);  // Диапазон XP: 1-3 (минимум, максимум)
    }

    @Override
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
        super.onStacksDropped(state, world, pos, tool, dropExperience);
    }
}