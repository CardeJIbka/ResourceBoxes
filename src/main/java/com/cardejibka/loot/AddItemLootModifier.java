package com.cardejibka.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

public class AddItemLootModifier extends LootModifier {

    public static final Codec<AddItemLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst)
            .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item))
            .and(Codec.FLOAT.fieldOf("chance").forGetter(m -> m.chance))
            .apply(inst, AddItemLootModifier::new));

    private final Item item;
    private final float chance;

    public AddItemLootModifier(net.minecraft.world.level.storage.loot.predicates.LootItemCondition[] conditionsIn, Item item, float chance) {
        super(conditionsIn);
        this.item = item;
        this.chance = chance;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        RandomSource random = context.getRandom();
        if (random.nextFloat() < chance) {
            generatedLoot.add(new ItemStack(item));
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return ModLootModifierSerializers.ADD_ITEM.get();
    }
}