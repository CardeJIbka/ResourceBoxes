package com.cardejibka;

import com.cardejibka.block.ResourceBlocks;
import com.cardejibka.item.ResourceBoxesItems;
import com.cardejibka.loot.ModLootModifierSerializers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ResourceBoxes.MOD_ID)
public class ResourceBoxes {
	public static final String MOD_ID = "resourceboxes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public ResourceBoxes() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		ResourceBoxesItems.ITEMS.register(modEventBus);
		ResourceBoxesItems.CREATIVE_TABS.register(modEventBus);
		ResourceBlocks.BLOCKS.register(modEventBus);
		ResourceBlocks.BLOCK_ITEMS.register(modEventBus);
		ModLootModifierSerializers.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);

		ResourceBlocks.registerModBlocks();

		LOGGER.info("Launched");
	}
}