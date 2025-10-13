package com.cardejibka.loot;

import com.cardejibka.item.ResourceBoxesItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.util.Identifier;

public class ModLootTableModifier {

    private static final Identifier SIMPLE_DUNGEON_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/simple_dungeon");
    private static final Identifier ABANDONED_MINESHAFT_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/abandoned_mineshaft");

    private static final Identifier VILLAGE_PLAINS_CHEST_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/village/village_plains_house");
    private static final Identifier VILLAGE_DESERT_CHEST_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/village/village_desert_house");
    private static final Identifier VILLAGE_SAVANNA_CHEST_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/village/village_savanna_house");
    private static final Identifier VILLAGE_SNOWY_CHEST_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/village/village_snowy_house");
    private static final Identifier VILLAGE_TAIGA_CHEST_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/village/village_taiga_house");

    private static final Identifier STRONGHOLD_CORRIDOR_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/stronghold_corridor");
    private static final Identifier STRONGHOLD_LIBRARY_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/stronghold_library");
    private static final Identifier END_CITY_TREASURE_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/end_city_treasure");
    private static final Identifier BASTION_TREASURE_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/bastion_treasure");
    private static final Identifier BASTION_HOGLIN_STABLE_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/bastion_hoglin_stable");
    private static final Identifier RUINED_PORTAL_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/ruined_portal");
    private static final Identifier PILLAGER_OUTPOST_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/pillager_outpost");
    private static final Identifier BURIED_TREASURE_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/buried_treasure");
    private static final Identifier ANCIENT_CITY_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/ancient_city");
    private static final Identifier JUNGLE_TEMPLE_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/jungle_temple");
    private static final Identifier DESERT_PYRAMID_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/desert_pyramid");
    private static final Identifier SHIPWRECK_TREASURE_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/shipwreck_treasure");
    private static final Identifier OCEAN_RUIN_WARM_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/underwater_ruin_warm");
    private static final Identifier WITCH_HUT_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/witch_hut");

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (SIMPLE_DUNGEON_LOOT_TABLE_ID.equals(key.getValue()) ||
                    JUNGLE_TEMPLE_LOOT_TABLE_ID.equals(key.getValue()) ||
                    DESERT_PYRAMID_LOOT_TABLE_ID.equals(key.getValue()) ||
                    SHIPWRECK_TREASURE_LOOT_TABLE_ID.equals(key.getValue()) ||
                    OCEAN_RUIN_WARM_LOOT_TABLE_ID.equals(key.getValue()) ||
                    WITCH_HUT_LOOT_TABLE_ID.equals(key.getValue())) {
                LootPool.Builder resourceGemPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .with(ItemEntry.builder(ResourceBoxesItems.RESOURCE_GEM)
                                .weight(1)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f))))
                        .conditionally(RandomChanceLootCondition.builder(0.03f));
                tableBuilder.pool(resourceGemPool.build());
            }

            if (SIMPLE_DUNGEON_LOOT_TABLE_ID.equals(key.getValue()) ||
                    ABANDONED_MINESHAFT_LOOT_TABLE_ID.equals(key.getValue()) ||
                    OCEAN_RUIN_WARM_LOOT_TABLE_ID.equals(key.getValue())) {
                LootPool.Builder oreBoxPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .with(ItemEntry.builder(ResourceBoxesItems.ORE_BOX)
                                .weight(1)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f))))
                        .conditionally(RandomChanceLootCondition.builder(0.05f));
                tableBuilder.pool(oreBoxPool.build());
            }

            if (VILLAGE_PLAINS_CHEST_LOOT_TABLE_ID.equals(key.getValue()) ||
                    VILLAGE_DESERT_CHEST_LOOT_TABLE_ID.equals(key.getValue()) ||
                    VILLAGE_SAVANNA_CHEST_LOOT_TABLE_ID.equals(key.getValue()) ||
                    VILLAGE_SNOWY_CHEST_LOOT_TABLE_ID.equals(key.getValue()) ||
                    VILLAGE_TAIGA_CHEST_LOOT_TABLE_ID.equals(key.getValue())) {
                LootPool.Builder foodBoxPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .with(ItemEntry.builder(ResourceBoxesItems.FOOD_BOX)
                                .weight(2)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f))))
                        .conditionally(RandomChanceLootCondition.builder(0.10f));
                tableBuilder.pool(foodBoxPool.build());
            }

            if (PILLAGER_OUTPOST_LOOT_TABLE_ID.equals(key.getValue()) ||
                    STRONGHOLD_CORRIDOR_LOOT_TABLE_ID.equals(key.getValue()) ||
                    STRONGHOLD_LIBRARY_LOOT_TABLE_ID.equals(key.getValue()) ||
                    DESERT_PYRAMID_LOOT_TABLE_ID.equals(key.getValue()) ||
                    WITCH_HUT_LOOT_TABLE_ID.equals(key.getValue())) {
                LootPool.Builder weaponBoxPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .with(ItemEntry.builder(ResourceBoxesItems.WEAPON_BOX)
                                .weight(1)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f))))
                        .conditionally(RandomChanceLootCondition.builder(0.07f));
                tableBuilder.pool(weaponBoxPool.build());
            }

            if (ABANDONED_MINESHAFT_LOOT_TABLE_ID.equals(key.getValue()) ||
                    VILLAGE_PLAINS_CHEST_LOOT_TABLE_ID.equals(key.getValue()) ||
                    VILLAGE_DESERT_CHEST_LOOT_TABLE_ID.equals(key.getValue()) ||
                    VILLAGE_SAVANNA_CHEST_LOOT_TABLE_ID.equals(key.getValue()) ||
                    VILLAGE_SNOWY_CHEST_LOOT_TABLE_ID.equals(key.getValue()) ||
                    VILLAGE_TAIGA_CHEST_LOOT_TABLE_ID.equals(key.getValue()) ||
                    OCEAN_RUIN_WARM_LOOT_TABLE_ID.equals(key.getValue())) {
                LootPool.Builder toolBoxPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .with(ItemEntry.builder(ResourceBoxesItems.TOOL_BOX)
                                .weight(1)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f))))
                        .conditionally(RandomChanceLootCondition.builder(0.06f));
                tableBuilder.pool(toolBoxPool.build());
            }

            if (STRONGHOLD_CORRIDOR_LOOT_TABLE_ID.equals(key.getValue()) ||
                    STRONGHOLD_LIBRARY_LOOT_TABLE_ID.equals(key.getValue()) ||
                    BURIED_TREASURE_LOOT_TABLE_ID.equals(key.getValue()) ||
                    SHIPWRECK_TREASURE_LOOT_TABLE_ID.equals(key.getValue())) {
                LootPool.Builder armorBoxPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .with(ItemEntry.builder(ResourceBoxesItems.ARMOR_BOX)
                                .weight(1)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f))))
                        .conditionally(RandomChanceLootCondition.builder(0.08f));
                tableBuilder.pool(armorBoxPool.build());
            }

            if (ANCIENT_CITY_LOOT_TABLE_ID.equals(key.getValue()) ||
                    STRONGHOLD_CORRIDOR_LOOT_TABLE_ID.equals(key.getValue()) ||
                    STRONGHOLD_LIBRARY_LOOT_TABLE_ID.equals(key.getValue())) {
                LootPool.Builder trimBoxPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .with(ItemEntry.builder(ResourceBoxesItems.TRIM_BOX)
                                .weight(1)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f))))
                        .conditionally(RandomChanceLootCondition.builder(0.04f));
                tableBuilder.pool(trimBoxPool.build());
            }

            if (BASTION_TREASURE_LOOT_TABLE_ID.equals(key.getValue()) ||
                    BASTION_HOGLIN_STABLE_LOOT_TABLE_ID.equals(key.getValue()) ||
                    RUINED_PORTAL_LOOT_TABLE_ID.equals(key.getValue())) {
                LootPool.Builder netherBoxPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .with(ItemEntry.builder(ResourceBoxesItems.NETHER_BOX)
                                .weight(1)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f))))
                        .conditionally(RandomChanceLootCondition.builder(0.06f));
                tableBuilder.pool(netherBoxPool.build());
            }

            if (END_CITY_TREASURE_LOOT_TABLE_ID.equals(key.getValue())) {
                LootPool.Builder endBoxPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .with(ItemEntry.builder(ResourceBoxesItems.END_BOX)
                                .weight(1)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f))))
                        .conditionally(RandomChanceLootCondition.builder(0.05f));
                tableBuilder.pool(endBoxPool.build());
            }
        });
    }
}