package com.cardejibka;

import com.cardejibka.block.ResourceBlocks;
import com.cardejibka.item.ResourceBoxesItems;
import com.cardejibka.loot.ModLootTableModifier;
import com.cardejibka.worldgen.ModOreGeneration;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceBoxes implements ModInitializer {
	public static final String MOD_ID = "resourceboxes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ResourceBoxesItems.initialize();
		ResourceBlocks.registerModBlocks();
		ModOreGeneration.generateOres();
		ModLootTableModifier.modifyLootTables();

		LOGGER.info("Launched");
	}
}