package com.cardejibka.item;


import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public abstract class ResourceBoxItem extends Item {
    public ResourceBoxItem(Settings settings) {
        super(settings);
    }

    protected abstract List<DropEntry> getDrops();

    // Helper method to select enchantment level based on max level
    private int getEnchantmentLevel(Random random, int maxLevel) {
        double randLevel = random.nextDouble();
        if (maxLevel == 1) {
            return 1; // For Mending, Silk Touch
        } else if (maxLevel == 2) { // Fire Aspect, Knockback
            return randLevel < 0.70 ? 1 : 2; // 70% level 1, 30% level 2
        } else if (maxLevel == 3) { // Unbreaking, Fortune
            if (randLevel < 0.55) return 1; // 55% level 1
            else if (randLevel < 0.85) return 2; // 30% level 2
            else return 3; // 15% level 3
        } else if (maxLevel == 4) { // Protection, Fire Protection, Projectile Protection
            if (randLevel < 0.40) return 1; // 40% level 1
            else if (randLevel < 0.70) return 2; // 30% level 2
            else if (randLevel < 0.90) return 3; // 20% level 3
            else return 4; // 10% level 4
        } else if (maxLevel == 5) { // Sharpness, Efficiency
            if (randLevel < 0.35) return 1; // 35% level 1
            else if (randLevel < 0.60) return 2; // 25% level 2
            else if (randLevel < 0.80) return 3; // 20% level 3
            else if (randLevel < 0.95) return 4; // 15% level 4
            else return 5; // 5% level 5
        }
        return 1; // Fallback to level 1
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!world.isClient) {
            // Получаем список дропов
            List<DropEntry> drops = getDrops();
            Random random = new Random();
            double rand = random.nextDouble();
            double cumulative = 0;
            Item selectedItem = null;

            // Определяем, какой предмет выпадет
            for (int i = 0; i < drops.size(); i++) {
                DropEntry entry = drops.get(i);
                cumulative += entry.chance;
                if (rand < cumulative || i == drops.size() - 1) {
                    selectedItem = entry.item;
                    break;
                }
            }

            // Гарантируем выбор последнего предмета, если ничего не выбрано
            if (selectedItem == null) {
                selectedItem = drops.get(drops.size() - 1).item;
            }

            // Определяем, является ли предмет инструментом, броней или оружием
            boolean isEquip = selectedItem.getDefaultStack().contains(DataComponentTypes.TOOL) || // Tools
                    selectedItem.getDefaultStack().contains(DataComponentTypes.EQUIPPABLE) || // Armor
                    selectedItem.getRegistryEntry().isIn(ItemTags.SWORDS); // Swords

            // Определяем количество
            int count;
            if (isEquip) {
                count = 1;
            } else {
                double randCount = random.nextDouble();
                if (randCount < 0.50) {
                    count = 1;
                } else if (randCount < 0.75) {
                    count = 2;
                } else if (randCount < 0.88) {
                    count = 3;
                } else if (randCount < 0.95) {
                    count = 4;
                } else if (randCount < 0.98) {
                    count = 5;
                } else if (randCount < 0.995) {
                    count = 6;
                } else {
                    count = 7;
                }
            }

            // Создаем стек с выбранным количеством
            ItemStack dropStack = new ItemStack(selectedItem, count);

            // Если это инструмент/броня/оружие, применяем чары
            if (isEquip) {
                // Получаем реестр чар
                Registry<Enchantment> enchantmentRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);

                // Для брони: 25% на защиту (Protection, Fire Protection, Projectile Protection), 25% на Unbreaking, 0.5% на Mending
                if (selectedItem.getDefaultStack().contains(DataComponentTypes.EQUIPPABLE)) {
                    // Защита (выбираем случайно один из трех видов)
                    if (random.nextDouble() < 0.25) {
                        RegistryEntry<Enchantment> enchant;
                        double protectionType = random.nextDouble();
                        if (protectionType < 0.33) {
                            enchant = enchantmentRegistry.getEntry(Enchantments.PROTECTION.getValue()).orElse(null);
                            if (enchant != null) {
                                dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 4));
                            }
                        } else if (protectionType < 0.66) {
                            enchant = enchantmentRegistry.getEntry(Enchantments.FIRE_PROTECTION.getValue()).orElse(null);
                            if (enchant != null) {
                                dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 4));
                            }
                        } else {
                            enchant = enchantmentRegistry.getEntry(Enchantments.PROJECTILE_PROTECTION.getValue()).orElse(null);
                            if (enchant != null) {
                                dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 4));
                            }
                        }
                    }
                    // Unbreaking
                    if (random.nextDouble() < 0.25) {
                        enchantmentRegistry.getEntry(Enchantments.UNBREAKING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 3)));
                    }
                    // Mending
                    if (random.nextDouble() < 0.005) {
                        enchantmentRegistry.getEntry(Enchantments.MENDING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, 1));
                    }
                }
                // Для мечей: 20% Sharpness, 20% Unbreaking, 5% Fire Aspect, 5% Knockback, 0.5% Mending
                else if (selectedItem.getRegistryEntry().isIn(ItemTags.SWORDS)) {
                    // Sharpness
                    if (random.nextDouble() < 0.20) {
                        enchantmentRegistry.getEntry(Enchantments.SHARPNESS.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 5)));
                    }
                    // Unbreaking
                    if (random.nextDouble() < 0.20) {
                        enchantmentRegistry.getEntry(Enchantments.UNBREAKING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 3)));
                    }
                    // Fire Aspect
                    if (random.nextDouble() < 0.05) {
                        enchantmentRegistry.getEntry(Enchantments.FIRE_ASPECT.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 2)));
                    }
                    // Knockback
                    if (random.nextDouble() < 0.05) {
                        enchantmentRegistry.getEntry(Enchantments.KNOCKBACK.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 2)));
                    }
                    // Mending
                    if (random.nextDouble() < 0.005) {
                        enchantmentRegistry.getEntry(Enchantments.MENDING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, 1));
                    }
                }
                // Для инструментов: 20% Efficiency, 20% Unbreaking, 5% Fortune, 5% Silk Touch, 0.5% Mending
                else if (selectedItem.getDefaultStack().contains(DataComponentTypes.TOOL)) {
                    // Efficiency
                    if (random.nextDouble() < 0.20) {
                        enchantmentRegistry.getEntry(Enchantments.EFFICIENCY.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 5)));
                    }
                    // Unbreaking
                    if (random.nextDouble() < 0.20) {
                        enchantmentRegistry.getEntry(Enchantments.UNBREAKING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 3)));
                    }
                    // Fortune
                    if (random.nextDouble() < 0.05) {
                        enchantmentRegistry.getEntry(Enchantments.FORTUNE.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 3)));
                    }
                    // Silk Touch
                    if (random.nextDouble() < 0.05) {
                        enchantmentRegistry.getEntry(Enchantments.SILK_TOUCH.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, 1));
                    }
                    // Mending
                    if (random.nextDouble() < 0.005) {
                        enchantmentRegistry.getEntry(Enchantments.MENDING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, 1));
                    }
                }
            }

            // Создаем временную копию стака и уменьшаем его на 1
            ItemStack tempStack = itemStack.copy();
            tempStack.decrement(1);
            user.setStackInHand(hand, tempStack);

            // Выдаем выбранный предмет
            if (!user.getInventory().insertStack(dropStack)) {
                // Если инвентарь полон, выбрасываем предмет в мир
                user.dropItem(dropStack, false);
            }

            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }

    @Override
    @Deprecated
    public void appendTooltip(ItemStack stack, TooltipContext context, net.minecraft.component.type.TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("tooltip.resourceboxes.possible_drops").formatted(Formatting.GRAY));
        for (DropEntry entry : getDrops()) {
            String chanceStr = String.format("%.1f%%", entry.chance * 100);
            MutableText itemName = Text.literal(entry.item.getName().getString()).formatted(entry.color);
            MutableText tooltipText = itemName.append(Text.literal(": ").formatted(Formatting.GRAY)).append(Text.literal(chanceStr).formatted(Formatting.GRAY));
            textConsumer.accept(tooltipText);
        }
    }

    protected record DropEntry(Item item, float chance, Formatting color) {}
}

class OreBoxItem extends ResourceBoxItem {
    public OreBoxItem(Settings settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        List<DropEntry> drops = new ArrayList<>();
        drops.add(new DropEntry(Items.COAL, 0.16f, Formatting.GREEN));
        drops.add(new DropEntry(Items.COPPER_INGOT, 0.16f, Formatting.GREEN));
        drops.add(new DropEntry(Items.REDSTONE, 0.13f, Formatting.GREEN));
        drops.add(new DropEntry(Items.LAPIS_LAZULI, 0.13f, Formatting.GREEN));
        drops.add(new DropEntry(Items.IRON_INGOT, 0.16f, Formatting.BLUE));
        drops.add(new DropEntry(Items.QUARTZ, 0.10f, Formatting.BLUE));
        drops.add(new DropEntry(Items.GOLD_INGOT, 0.12f, Formatting.LIGHT_PURPLE));
        drops.add(new DropEntry(Items.DIAMOND, 0.02f, Formatting.RED));
        drops.add(new DropEntry(Items.EMERALD, 0.017f, Formatting.RED));
        drops.add(new DropEntry(Items.NETHERITE_SCRAP, 0.003f, Formatting.YELLOW));
        return drops;
    }
}

class FoodBoxItem extends ResourceBoxItem {
    public FoodBoxItem(Settings settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        List<DropEntry> drops = new ArrayList<>();
        drops.add(new DropEntry(Items.IRON_CHESTPLATE, 0.4f, Formatting.GRAY));
        drops.add(new DropEntry(Items.GOLDEN_CHESTPLATE, 0.3f, Formatting.BLUE));
        drops.add(new DropEntry(Items.DIAMOND_CHESTPLATE, 0.2f, Formatting.LIGHT_PURPLE));
        drops.add(new DropEntry(Items.NETHERITE_CHESTPLATE, 0.1f, Formatting.RED));
        return drops;
    }
}

class WeaponBoxItem extends ResourceBoxItem {
    public WeaponBoxItem(Settings settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        List<DropEntry> drops = new ArrayList<>();
        drops.add(new DropEntry(Items.WOODEN_SWORD, 0.4f, Formatting.GRAY));
        drops.add(new DropEntry(Items.STONE_SWORD, 0.3f, Formatting.BLUE));
        drops.add(new DropEntry(Items.IRON_SWORD, 0.2f, Formatting.LIGHT_PURPLE));
        drops.add(new DropEntry(Items.DIAMOND_SWORD, 0.1f, Formatting.RED));
        return drops;
    }
}


class RichOreBoxItem extends ResourceBoxItem {
    public RichOreBoxItem(Settings settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        List<DropEntry> drops = new ArrayList<>();
        drops.add(new DropEntry(Items.IRON_PICKAXE, 0.6f, Formatting.BLUE)); // Алмаз - синий
        drops.add(new DropEntry(Items.DIAMOND_PICKAXE, 0.4f, Formatting.RED)); // Алмазный блок - красный
        return drops;
    }
}