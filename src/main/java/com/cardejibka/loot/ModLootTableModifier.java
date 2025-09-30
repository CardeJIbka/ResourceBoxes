package com.cardejibka.loot;

import com.cardejibka.item.ResourceBoxesItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class ModLootTableModifier {

    private static final Identifier SIMPLE_DUNGEON_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/simple_dungeon");
    private static final Identifier ABANDONED_MINESHAFT_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/abandoned_mineshaft");

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            // Проверяем, что это таблица для сундуков данжей или заброшенных шахт
            if (SIMPLE_DUNGEON_LOOT_TABLE_ID.equals(key.getValue()) || ABANDONED_MINESHAFT_LOOT_TABLE_ID.equals(key.getValue())) {
                // Создаем новый пул для ore_box
                LootPool.Builder oreBoxPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))  // 1 попытка
                        .with(ItemEntry.builder(ResourceBoxesItems.ORE_BOX)  // Добавляем ore_box
                                .weight(1)  // Вес: 1 (низкий шанс, ~2-5%)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f))))  // 1 предмет
                        .conditionally(net.minecraft.loot.condition.RandomChanceLootCondition.builder(0.05f));  // Шанс 5%

                // Добавляем пул в таблицу добычи
                tableBuilder.pool(oreBoxPool.build());
            }
        });
    }
}