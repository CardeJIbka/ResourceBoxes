package com.cardejibka.item;

import com.cardejibka.ResourceBoxes;
import com.cardejibka.block.ResourceBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ResourceBoxesItems {
    private ResourceBoxesItems() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, ResourceBoxes.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ResourceBoxes.MOD_ID);

    public static final RegistryObject<Item> ORE_BOX = ITEMS.register("ore_box",
            () -> new OreBoxItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> FOOD_BOX = ITEMS.register("food_box",
            () -> new FoodBoxItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> WEAPON_BOX = ITEMS.register("weapon_box",
            () -> new WeaponBoxItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> TOOL_BOX = ITEMS.register("tool_box",
            () -> new ToolBoxItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ARMOR_BOX = ITEMS.register("armor_box",
            () -> new ArmorBoxItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> NETHER_BOX = ITEMS.register("nether_box",
            () -> new NetherBoxItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> END_BOX = ITEMS.register("end_box",
            () -> new EndBoxItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> TRIM_BOX = ITEMS.register("trim_box",
            () -> new TrimBoxItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> RESOURCE_SHARD = ITEMS.register("resource_shard",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> RESOURCE_GEM = ITEMS.register("resource_gem",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<CreativeModeTab> RESOURCE_BOXES_GROUP = CREATIVE_TABS.register("resourceboxes_group",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.resourceboxes.resourceboxes_group"))
                    .icon(() -> new ItemStack(ORE_BOX.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ORE_BOX.get());
                        output.accept(FOOD_BOX.get());
                        output.accept(WEAPON_BOX.get());
                        output.accept(TOOL_BOX.get());
                        output.accept(ARMOR_BOX.get());
                        output.accept(TRIM_BOX.get());
                        output.accept(NETHER_BOX.get());
                        output.accept(END_BOX.get());
                        output.accept(RESOURCE_SHARD.get());
                        output.accept(RESOURCE_GEM.get());
                        output.accept(ResourceBlocks.RESOURCE_BLOCK.get().asItem());
                        output.accept(ResourceBlocks.RESOURCE_ORE.get().asItem());
                        output.accept(ResourceBlocks.DEEPSLATE_RESOURCE_ORE.get().asItem());
                    })
                    .build());
}