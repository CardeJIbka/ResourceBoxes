package com.cardejibka.item;

import com.cardejibka.ResourceBoxes;
import com.cardejibka.block.ResourceBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ResourceBoxesItems {
    private ResourceBoxesItems() {}

    // Определяем кастомную вкладку
    public static final RegistryKey<ItemGroup> RESOURCE_BOXES_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(ResourceBoxes.MOD_ID, "resourceboxes_group"));

    // Регистрируем предметы
    public static final Item ORE_BOX = register("ore_box", new OreBoxItem(new Item.Settings() {
        {
            registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ResourceBoxes.MOD_ID, "ore_box")));
            maxCount(64);
        }
    }));
    public static final Item FOOD_BOX = register("food_box", new FoodBoxItem(new Item.Settings() {
        {
            registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ResourceBoxes.MOD_ID, "food_box")));
            maxCount(64);
        }
    }));
    public static final Item WEAPON_BOX = register("weapon_box", new WeaponBoxItem(new Item.Settings() {
        {
            registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ResourceBoxes.MOD_ID, "weapon_box")));
            maxCount(64);
        }
    }));
    public static final Item RICH_ORE_BOX = register("rich_ore_box", new RichOreBoxItem(new Item.Settings() {
        {
            registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ResourceBoxes.MOD_ID, "rich_ore_box")));
            maxCount(64);
        }
    }));

    // Метод регистрации предмета
    private static Item register(String name, Item item) {
        Identifier id = Identifier.of(ResourceBoxes.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        return Registry.register(Registries.ITEM, key, item);
    }

    public static void initialize() {
        // Регистрируем кастомную вкладку
        Registry.register(Registries.ITEM_GROUP, RESOURCE_BOXES_GROUP, ItemGroup.create(null, 0)
                .displayName(Text.translatable("itemGroup.resourceboxes.resourceboxes_group"))
                .icon(() -> new ItemStack(ORE_BOX))
                .entries((displayContext, entries) -> {
                    entries.add(ORE_BOX);
                    entries.add(FOOD_BOX);
                    entries.add(WEAPON_BOX);
                    entries.add(RICH_ORE_BOX);
                    entries.add(ResourceBlocks.RESOURCE_ORE);
                })
                .build());
    }
}