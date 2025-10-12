package com.cardejibka.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
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
import java.util.concurrent.atomic.AtomicBoolean;
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
            DropEntry selectedEntry = null;

            // Определяем, какой предмет выпадет
            for (DropEntry entry : drops) {
                cumulative += entry.chance;
                if (rand <= cumulative) {
                    selectedEntry = entry;
                    break;
                }
            }

            // Гарантируем выбор последнего предмета, если ничего не выбрано
            if (selectedEntry == null) {
                selectedEntry = drops.getLast();
            }

            Item selectedItem = selectedEntry.item;

            // Определяем, является ли предмет инструментом, броней или оружием (расширили для ranged/special)
            boolean isEquip = selectedItem.getDefaultStack().contains(DataComponentTypes.TOOL) || // Tools
                    selectedItem.getDefaultStack().contains(DataComponentTypes.EQUIPPABLE) || // Armor
                    selectedItem.getRegistryEntry().isIn(ItemTags.SWORDS) || // Swords
                    selectedItem == Items.BOW || selectedItem == Items.CROSSBOW || // Ranged weapons
                    selectedItem == Items.TRIDENT || selectedItem == Items.MACE; // Special weapons

            // Определяем количество с наклонным распределением
            int maxCount = selectedEntry.maxCount;
            int count;
            if (maxCount <= 1) {
                count = 1; // Для предметов с maxCount=1 (например, броня, оружие)
            } else {
                double randCount = random.nextDouble();
                int range1 = (int) Math.ceil(maxCount * 0.3); // 30% от maxCount
                int range2 = (int) Math.ceil(maxCount * 0.6); // 60% от maxCount
                if (randCount < 0.5) {
                    // 50% шанс на 0–30% от maxCount (1–range1)
                    count = 1 + random.nextInt(Math.max(1, range1));
                } else if (randCount < 0.85) {
                    // 35% шанс на 30–60% от maxCount (range1+1–range2)
                    count = range1 + 1 + random.nextInt(Math.max(1, range2 - range1));
                } else {
                    // 15% шанс на 60–100% от maxCount (range2+1–maxCount)
                    count = range2 + 1 + random.nextInt(Math.max(1, maxCount - range2));
                }
            }

            // Создаем стек с выбранным количеством
            ItemStack dropStack = new ItemStack(selectedItem, count);

            // Если это инструмент/броня/оружие, применяем чары
            if (isEquip) {
                // Получаем реестр чар
                Registry<Enchantment> enchantmentRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);

                // Для брони: уточняем по слотам (шлем, ботинки) + общие
                if (selectedItem.getDefaultStack().contains(DataComponentTypes.EQUIPPABLE)) {
                    var equippable = dropStack.get(DataComponentTypes.EQUIPPABLE);
                    if (equippable != null) {
                        var slot = equippable.slot();
                        // Общие для всей брони: Protection (один из типов, 30% шанс), Unbreaking (30%), Mending (1%)
                        // Защита (выбираем случайно один из трех видов)
                        if (random.nextDouble() < 0.30) {  // Увеличено с 0.25
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
                        if (random.nextDouble() < 0.30) {  // Увеличено с 0.25
                            enchantmentRegistry.getEntry(Enchantments.UNBREAKING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 3)));
                        }
                        // Mending
                        if (random.nextDouble() < 0.01) {  // Увеличено с 0.005
                            enchantmentRegistry.getEntry(Enchantments.MENDING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, 1));
                        }

                        // Специфические для шлема (HEAD)
                        if (slot == EquipmentSlot.HEAD) {
                            // Respiration (30% шанс, увеличено с 0.20)
                            if (random.nextDouble() < 0.30) {
                                enchantmentRegistry.getEntry(Enchantments.RESPIRATION.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 3)));
                            }
                            // Aqua Affinity (10% шанс, увеличено с 0.05)
                            if (random.nextDouble() < 0.10) {
                                enchantmentRegistry.getEntry(Enchantments.AQUA_AFFINITY.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, 1));
                            }
                        }
                        // Специфические для ботинок (FEET)
                        else if (slot == EquipmentSlot.FEET) {
                            // Feather Falling (30% шанс, увеличено с 0.20)
                            if (random.nextDouble() < 0.30) {
                                enchantmentRegistry.getEntry(Enchantments.FEATHER_FALLING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 4)));
                            }
                            // Depth Strider (10% шанс)
                            if (random.nextDouble() < 0.10) {
                                enchantmentRegistry.getEntry(Enchantments.DEPTH_STRIDER.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 3)));
                            }
                            // Frost Walker (10% шанс, mutually exclusive with Depth Strider)
                            RegistryEntry<Enchantment> depthStriderEntry = enchantmentRegistry.getEntry(Enchantments.DEPTH_STRIDER.getValue()).orElse(null);
                            if (random.nextDouble() < 0.10 && (depthStriderEntry == null || dropStack.getEnchantments().getLevel(depthStriderEntry) <= 0)) {
                                enchantmentRegistry.getEntry(Enchantments.FROST_WALKER.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 2)));
                            }
                        }
                    }
                }
                // Для мечей: 30% Sharpness, 30% Unbreaking, 10% Fire Aspect, 10% Knockback, 1% Mending
                else if (selectedItem.getRegistryEntry().isIn(ItemTags.SWORDS)) {
                    // Sharpness
                    if (random.nextDouble() < 0.30) {  // Увеличено с 0.20
                        enchantmentRegistry.getEntry(Enchantments.SHARPNESS.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 5)));
                    }
                    // Unbreaking
                    if (random.nextDouble() < 0.30) {  // Увеличено с 0.20
                        enchantmentRegistry.getEntry(Enchantments.UNBREAKING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 3)));
                    }
                    // Fire Aspect
                    if (random.nextDouble() < 0.10) {  // Увеличено с 0.05
                        enchantmentRegistry.getEntry(Enchantments.FIRE_ASPECT.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 2)));
                    }
                    // Knockback
                    if (random.nextDouble() < 0.10) {  // Увеличено с 0.05
                        enchantmentRegistry.getEntry(Enchantments.KNOCKBACK.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 2)));
                    }
                    // Mending
                    if (random.nextDouble() < 0.01) {  // Увеличено с 0.005
                        enchantmentRegistry.getEntry(Enchantments.MENDING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, 1));
                    }
                }
                // Для трезубца (TRIDENT)
                else if (selectedItem == Items.TRIDENT) {
                    // Loyalty (30% шанс)
                    if (random.nextDouble() < 0.30) {  // Увеличено с 0.20
                        enchantmentRegistry.getEntry(Enchantments.LOYALTY.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 3)));
                    }
                    // Impaling (30% шанс)
                    if (random.nextDouble() < 0.30) {  // Увеличено с 0.20
                        enchantmentRegistry.getEntry(Enchantments.IMPALING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 5)));
                    }
                    // Channeling (10% шанс)
                    if (random.nextDouble() < 0.10) {  // Увеличено с 0.05
                        enchantmentRegistry.getEntry(Enchantments.CHANNELING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, 1));
                    }
                    // Unbreaking
                    if (random.nextDouble() < 0.30) {  // Увеличено с 0.20
                        enchantmentRegistry.getEntry(Enchantments.UNBREAKING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 3)));
                    }
                    // Mending
                    if (random.nextDouble() < 0.01) {  // Увеличено с 0.005
                        enchantmentRegistry.getEntry(Enchantments.MENDING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, 1));
                    }
                }
                // Для булавы (MACE)
                else if (selectedItem == Items.MACE) {
                    // Density (30% шанс)
                    if (random.nextDouble() < 0.30) {  // Увеличено с 0.20
                        enchantmentRegistry.getEntry(Enchantments.DENSITY.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 5)));
                    }
                    // Breach (30% шанс)
                    if (random.nextDouble() < 0.30) {  // Увеличено с 0.20
                        enchantmentRegistry.getEntry(Enchantments.BREACH.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 4)));
                    }
                    // Wind Burst (10% шанс)
                    if (random.nextDouble() < 0.10) {  // Увеличено с 0.05
                        enchantmentRegistry.getEntry(Enchantments.WIND_BURST.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 3)));
                    }
                    // Unbreaking
                    if (random.nextDouble() < 0.30) {  // Увеличено с 0.20
                        enchantmentRegistry.getEntry(Enchantments.UNBREAKING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 3)));
                    }
                    // Mending
                    if (random.nextDouble() < 0.01) {  // Увеличено с 0.005
                        enchantmentRegistry.getEntry(Enchantments.MENDING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, 1));
                    }
                }
                // Для лука (BOW)
                else if (selectedItem == Items.BOW) {
                    // Power (30% шанс)
                    if (random.nextDouble() < 0.30) {  // Увеличено с 0.20
                        enchantmentRegistry.getEntry(Enchantments.POWER.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 5)));
                    }
                    // Punch (10% шанс)
                    if (random.nextDouble() < 0.10) {  // Увеличено с 0.05
                        enchantmentRegistry.getEntry(Enchantments.PUNCH.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 2)));
                    }
                    // Flame (10% шанс)
                    if (random.nextDouble() < 0.10) {  // Увеличено с 0.05
                        enchantmentRegistry.getEntry(Enchantments.FLAME.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, 1));
                    }
                    // Unbreaking
                    if (random.nextDouble() < 0.30) {  // Увеличено с 0.20
                        enchantmentRegistry.getEntry(Enchantments.UNBREAKING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 3)));
                    }
                    // Infinity (10% шанс, mutually exclusive with Mending)
                    AtomicBoolean hasInfinity = new AtomicBoolean(false);
                    if (random.nextDouble() < 0.10) {  // Увеличено с 0.05
                        enchantmentRegistry.getEntry(Enchantments.INFINITY.getValue()).ifPresent(enchant -> {
                            dropStack.addEnchantment(enchant, 1);
                            hasInfinity.set(true);
                        });
                    }
                    // Mending (1% шанс, только если нет Infinity)
                    if (random.nextDouble() < 0.01 && !hasInfinity.get()) {  // Увеличено с 0.005
                        enchantmentRegistry.getEntry(Enchantments.MENDING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, 1));
                    }
                }
                // Для арбалета (CROSSBOW)
                else if (selectedItem == Items.CROSSBOW) {
                    // Quick Charge (30% шанс)
                    if (random.nextDouble() < 0.30) {  // Увеличено с 0.20
                        enchantmentRegistry.getEntry(Enchantments.QUICK_CHARGE.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 3)));
                    }
                    // Unbreaking
                    if (random.nextDouble() < 0.30) {  // Увеличено с 0.20
                        enchantmentRegistry.getEntry(Enchantments.UNBREAKING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 3)));
                    }
                    // Multishot (10% шанс, mutually exclusive with Piercing)
                    AtomicBoolean hasMultishot = new AtomicBoolean(false);
                    if (random.nextDouble() < 0.10) {  // Увеличено с 0.05
                        enchantmentRegistry.getEntry(Enchantments.MULTISHOT.getValue()).ifPresent(enchant -> {
                            dropStack.addEnchantment(enchant, 1);
                            hasMultishot.set(true);
                        });
                    }
                    // Piercing (10% шанс, mutually exclusive with Multishot)
                    if (random.nextDouble() < 0.10 && !hasMultishot.get()) {  // Увеличено с 0.05
                        enchantmentRegistry.getEntry(Enchantments.PIERCING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 4)));
                    }
                    // Mending
                    if (random.nextDouble() < 0.01) {  // Увеличено с 0.005
                        enchantmentRegistry.getEntry(Enchantments.MENDING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, 1));
                    }
                }
                // Для инструментов: 30% Efficiency, 30% Unbreaking, 10% Fortune, 10% Silk Touch (mutually exclusive), 1% Mending
                else if (selectedItem.getDefaultStack().contains(DataComponentTypes.TOOL)) {
                    // Efficiency
                    if (random.nextDouble() < 0.30) {  // Увеличено с 0.20
                        enchantmentRegistry.getEntry(Enchantments.EFFICIENCY.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 5)));
                    }
                    // Unbreaking
                    if (random.nextDouble() < 0.30) {  // Увеличено с 0.20
                        enchantmentRegistry.getEntry(Enchantments.UNBREAKING.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 3)));
                    }
                    // Fortune (10% шанс)
                    AtomicBoolean hasFortune = new AtomicBoolean(false);
                    if (random.nextDouble() < 0.10) {  // Увеличено с 0.05
                        enchantmentRegistry.getEntry(Enchantments.FORTUNE.getValue()).ifPresent(enchant -> {
                            dropStack.addEnchantment(enchant, getEnchantmentLevel(random, 3));
                            hasFortune.set(true);
                        });
                    }
                    // Silk Touch (10% шанс, mutually exclusive with Fortune)
                    if (random.nextDouble() < 0.10 && !hasFortune.get()) {  // Увеличено с 0.05
                        enchantmentRegistry.getEntry(Enchantments.SILK_TOUCH.getValue()).ifPresent(enchant -> dropStack.addEnchantment(enchant, 1));
                    }
                    // Mending
                    if (random.nextDouble() < 0.01) {  // Увеличено с 0.005
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
            float chancePercent = entry.chance * 100;
            String chanceStr = chancePercent % 1 == 0 ? String.format("%.0f%%", chancePercent) : String.format("%.1f%%", chancePercent);
            MutableText itemName = Text.literal(entry.item.getName().getString()).formatted(entry.color);
            MutableText tooltipText = itemName.append(Text.literal(": ").formatted(Formatting.GRAY)).append(Text.literal(chanceStr).formatted(Formatting.GRAY));
            if (entry.maxCount > 1) {
                tooltipText = tooltipText.append(Text.literal(" (x1 - x" + entry.maxCount + ")").formatted(Formatting.GRAY));
            }
            textConsumer.accept(tooltipText);
        }
    }

    protected record DropEntry(Item item, float chance, Formatting color, int maxCount) {}
}

class OreBoxItem extends ResourceBoxItem {
    public OreBoxItem(Settings settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        List<DropEntry> drops = new ArrayList<>();
        drops.add(new DropEntry(Items.COAL, 0.15f, Formatting.GREEN, 32));
        drops.add(new DropEntry(Items.COPPER_INGOT, 0.15f, Formatting.GREEN, 32));
        drops.add(new DropEntry(Items.REDSTONE, 0.12f, Formatting.GREEN, 48));
        drops.add(new DropEntry(Items.LAPIS_LAZULI, 0.12f, Formatting.GREEN, 48));
        drops.add(new DropEntry(Items.IRON_INGOT, 0.14f, Formatting.BLUE, 24));
        drops.add(new DropEntry(Items.QUARTZ, 0.08f, Formatting.BLUE, 24));
        drops.add(new DropEntry(Items.GOLD_INGOT, 0.10f, Formatting.LIGHT_PURPLE, 24));
        drops.add(new DropEntry(Items.AMETHYST_SHARD, 0.06f, Formatting.LIGHT_PURPLE, 12));
        drops.add(new DropEntry(Items.EMERALD, 0.05f, Formatting.RED, 8));
        drops.add(new DropEntry(Items.DIAMOND, 0.03f, Formatting.RED, 8));
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
        // Common tier (GREEN)
        drops.add(new DropEntry(Items.BEETROOT, 0.077f, Formatting.GREEN, 32));
        drops.add(new DropEntry(Items.SWEET_BERRIES, 0.077f, Formatting.GREEN, 32));
        drops.add(new DropEntry(Items.TROPICAL_FISH, 0.061f, Formatting.GREEN, 32));
        drops.add(new DropEntry(Items.DRIED_KELP, 0.061f, Formatting.GREEN, 32));
        // Uncommon tier (BLUE)
        drops.add(new DropEntry(Items.GLOW_BERRIES, 0.071f, Formatting.BLUE, 24));
        drops.add(new DropEntry(Items.BREAD, 0.071f, Formatting.BLUE, 24));
        drops.add(new DropEntry(Items.CARROT, 0.061f, Formatting.BLUE, 24));
        drops.add(new DropEntry(Items.POTATO, 0.061f, Formatting.BLUE, 24));
        drops.add(new DropEntry(Items.COOKED_COD, 0.051f, Formatting.BLUE, 24));
        drops.add(new DropEntry(Items.COOKED_SALMON, 0.051f, Formatting.BLUE, 24));
        drops.add(new DropEntry(Items.COOKED_CHICKEN, 0.041f, Formatting.BLUE, 24));
        // Rare tier (LIGHT_PURPLE)
        drops.add(new DropEntry(Items.HONEY_BOTTLE, 0.051f, Formatting.LIGHT_PURPLE, 16));
        drops.add(new DropEntry(Items.APPLE, 0.051f, Formatting.LIGHT_PURPLE, 16));
        drops.add(new DropEntry(Items.COOKED_BEEF, 0.041f, Formatting.LIGHT_PURPLE, 16));
        drops.add(new DropEntry(Items.COOKED_PORKCHOP, 0.041f, Formatting.LIGHT_PURPLE, 16));
        // Epic tier (LIGHT_PURPLE)
        drops.add(new DropEntry(Items.CAKE, 0.031f, Formatting.LIGHT_PURPLE, 2));
        drops.add(new DropEntry(Items.GOLDEN_CARROT, 0.031f, Formatting.LIGHT_PURPLE, 12));
        drops.add(new DropEntry(Items.CHORUS_FRUIT, 0.026f, Formatting.LIGHT_PURPLE, 12));
        drops.add(new DropEntry(Items.PUMPKIN_PIE, 0.026f, Formatting.LIGHT_PURPLE, 12));
        // Legendary tier (RED)
        drops.add(new DropEntry(Items.GOLDEN_APPLE, 0.015f, Formatting.RED, 4));
        drops.add(new DropEntry(Items.ENCHANTED_GOLDEN_APPLE, 0.004f, Formatting.RED, 1));
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
        // Common tier (GREEN)
        drops.add(new DropEntry(Items.WOODEN_SWORD, 0.157f, Formatting.GREEN, 1));
        drops.add(new DropEntry(Items.STONE_SWORD, 0.157f, Formatting.GREEN, 1));
        drops.add(new DropEntry(Items.BOW, 0.123f, Formatting.GREEN, 1));
        // Uncommon tier (BLUE)
        drops.add(new DropEntry(Items.IRON_SWORD, 0.128f, Formatting.BLUE, 1));
        drops.add(new DropEntry(Items.CROSSBOW, 0.103f, Formatting.BLUE, 1));
        drops.add(new DropEntry(Items.SHIELD, 0.103f, Formatting.BLUE, 1));
        drops.add(new DropEntry(Items.GOLDEN_SWORD, 0.128f, Formatting.BLUE, 1));
        // Rare tier (LIGHT_PURPLE)
        drops.add(new DropEntry(Items.DIAMOND_SWORD, 0.070f, Formatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.TRIDENT, 0.020f, Formatting.LIGHT_PURPLE, 1));
        // Legendary tier (RED)
        drops.add(new DropEntry(Items.NETHERITE_SWORD, 0.010f, Formatting.RED, 1));
        drops.add(new DropEntry(Items.MACE, 0.003f, Formatting.RED, 1));
        return drops;
    }
}

class ToolBoxItem extends ResourceBoxItem {
    public ToolBoxItem(Settings settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        List<DropEntry> drops = new ArrayList<>();
        // Common tier (GREEN) - Wooden tools
        drops.add(new DropEntry(Items.WOODEN_PICKAXE, 0.120f, Formatting.GREEN, 1));
        drops.add(new DropEntry(Items.WOODEN_AXE, 0.120f, Formatting.GREEN, 1));
        drops.add(new DropEntry(Items.WOODEN_SHOVEL, 0.120f, Formatting.GREEN, 1));
        drops.add(new DropEntry(Items.WOODEN_HOE, 0.120f, Formatting.GREEN, 1));
        // Uncommon tier (BLUE) - Stone tools
        drops.add(new DropEntry(Items.STONE_PICKAXE, 0.080f, Formatting.BLUE, 1));
        drops.add(new DropEntry(Items.STONE_AXE, 0.080f, Formatting.BLUE, 1));
        drops.add(new DropEntry(Items.STONE_SHOVEL, 0.080f, Formatting.BLUE, 1));
        drops.add(new DropEntry(Items.STONE_HOE, 0.080f, Formatting.BLUE, 1));
        // Rare tier (LIGHT_PURPLE) - Diamond tools
        drops.add(new DropEntry(Items.DIAMOND_PICKAXE, 0.040f, Formatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.DIAMOND_AXE, 0.040f, Formatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.DIAMOND_SHOVEL, 0.040f, Formatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.DIAMOND_HOE, 0.040f, Formatting.LIGHT_PURPLE, 1));
        // Legendary tier (RED) - Netherite tools
        drops.add(new DropEntry(Items.NETHERITE_PICKAXE, 0.010f, Formatting.RED, 1));
        drops.add(new DropEntry(Items.NETHERITE_AXE, 0.010f, Formatting.RED, 1));
        drops.add(new DropEntry(Items.NETHERITE_SHOVEL, 0.010f, Formatting.RED, 1));
        drops.add(new DropEntry(Items.NETHERITE_HOE, 0.010f, Formatting.RED, 1));
        return drops;
    }
}

class ArmorBoxItem extends ResourceBoxItem {
    public ArmorBoxItem(Settings settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        List<DropEntry> drops = new ArrayList<>();
        // Common tier (GREEN) - Leather armor
        drops.add(new DropEntry(Items.LEATHER_HELMET, 0.120f, Formatting.GREEN, 1));
        drops.add(new DropEntry(Items.LEATHER_CHESTPLATE, 0.120f, Formatting.GREEN, 1));
        drops.add(new DropEntry(Items.LEATHER_LEGGINGS, 0.120f, Formatting.GREEN, 1));
        drops.add(new DropEntry(Items.LEATHER_BOOTS, 0.120f, Formatting.GREEN, 1));
        // Uncommon tier (BLUE) - Iron armor
        drops.add(new DropEntry(Items.IRON_HELMET, 0.080f, Formatting.BLUE, 1));
        drops.add(new DropEntry(Items.IRON_CHESTPLATE, 0.080f, Formatting.BLUE, 1));
        drops.add(new DropEntry(Items.IRON_LEGGINGS, 0.080f, Formatting.BLUE, 1));
        drops.add(new DropEntry(Items.IRON_BOOTS, 0.080f, Formatting.BLUE, 1));
        // Rare tier (LIGHT_PURPLE) - Diamond armor
        drops.add(new DropEntry(Items.DIAMOND_HELMET, 0.040f, Formatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.DIAMOND_CHESTPLATE, 0.040f, Formatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.DIAMOND_LEGGINGS, 0.040f, Formatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.DIAMOND_BOOTS, 0.040f, Formatting.LIGHT_PURPLE, 1));
        // Legendary tier (RED) - Netherite armor
        drops.add(new DropEntry(Items.NETHERITE_HELMET, 0.010f, Formatting.RED, 1));
        drops.add(new DropEntry(Items.NETHERITE_CHESTPLATE, 0.010f, Formatting.RED, 1));
        drops.add(new DropEntry(Items.NETHERITE_LEGGINGS, 0.010f, Formatting.RED, 1));
        drops.add(new DropEntry(Items.NETHERITE_BOOTS, 0.010f, Formatting.RED, 1));
        return drops;
    }
}

class NetherBoxItem extends ResourceBoxItem {
    public NetherBoxItem(Settings settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        List<DropEntry> drops = new ArrayList<>();
        // Common tier (GREEN) — базовые Nether-ресурсы, часто для крафта
        drops.add(new DropEntry(Items.QUARTZ, 0.17f, Formatting.GREEN, 16));
        drops.add(new DropEntry(Items.NETHER_WART, 0.17f, Formatting.GREEN, 16));
        drops.add(new DropEntry(Items.GOLD_NUGGET, 0.14f, Formatting.GREEN, 32));
        // Uncommon tier (BLUE) — полезные для фарма/крафта
        drops.add(new DropEntry(Items.BLAZE_ROD, 0.13f, Formatting.BLUE, 8));
        drops.add(new DropEntry(Items.GHAST_TEAR, 0.115f, Formatting.BLUE, 4));
        drops.add(new DropEntry(Items.NETHER_BRICK, 0.10f, Formatting.BLUE, 16));
        // Rare tier (LIGHT_PURPLE) — редкие, для зелий/крафта
        drops.add(new DropEntry(Items.QUARTZ_BLOCK, 0.05f, Formatting.LIGHT_PURPLE, 4));  // Уменьшено для баланса
        drops.add(new DropEntry(Items.ANCIENT_DEBRIS, 0.04f, Formatting.LIGHT_PURPLE, 1));  // Уменьшено для баланса
        drops.add(new DropEntry(Items.GLOWSTONE_DUST, 0.05f, Formatting.LIGHT_PURPLE, 16));  // Добавлена светопыль
        // Legendary tier (RED) — топ Nether-лут, очень редко
        drops.add(new DropEntry(Items.NETHERITE_SCRAP, 0.02f, Formatting.RED, 2));
        drops.add(new DropEntry(Items.NETHERITE_INGOT, 0.015f, Formatting.RED, 1));
        return drops;
    }
}

class EndBoxItem extends ResourceBoxItem {
    public EndBoxItem(Settings settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        List<DropEntry> drops = new ArrayList<>();
        // Common tier (GREEN) — базовые End-ресурсы, для телепорта/крафта
        drops.add(new DropEntry(Items.CHORUS_FRUIT, 0.18f, Formatting.GREEN, 8));  // +0.03f
        drops.add(new DropEntry(Items.PURPUR_BLOCK, 0.16f, Formatting.GREEN, 4));  // +0.02f
        // Uncommon tier (BLUE) — полезные для выживания/крафта
        drops.add(new DropEntry(Items.ENDER_PEARL, 0.15f, Formatting.BLUE, 8));  // +0.03f
        drops.add(new DropEntry(Items.CHORUS_FLOWER, 0.10f, Formatting.BLUE, 4));
        drops.add(new DropEntry(Items.END_STONE, 0.12f, Formatting.BLUE, 8));  // +0.02f
        // Rare tier (LIGHT_PURPLE) — редкие, для энчанта/крафта
        drops.add(new DropEntry(Items.DRAGON_BREATH, 0.08f, Formatting.LIGHT_PURPLE, 2));
        drops.add(new DropEntry(Items.END_CRYSTAL, 0.06f, Formatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.SHULKER_SHELL, 0.06f, Formatting.LIGHT_PURPLE, 1));  // Rare tier
        drops.add(new DropEntry(Items.END_ROD, 0.06f, Formatting.LIGHT_PURPLE, 4));  // Rare tier
        // Legendary tier (RED) — топ End-лут, супер-редко
        drops.add(new DropEntry(Items.ELYTRA, 0.02f, Formatting.RED, 1));
        return drops;
    }
}

class TrimBoxItem extends ResourceBoxItem {
    public TrimBoxItem(Settings settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        List<DropEntry> drops = new ArrayList<>();
        // Uncommon tier (BLUE)
        drops.add(new DropEntry(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, Formatting.BLUE, 1));
        drops.add(new DropEntry(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, Formatting.BLUE, 1));
        drops.add(new DropEntry(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, Formatting.BLUE, 1));
        // Rare tier (LIGHT_PURPLE)
        drops.add(new DropEntry(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, Formatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, Formatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, Formatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, Formatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, Formatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, Formatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, Formatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, Formatting.LIGHT_PURPLE, 1));
        // Legendary tier (RED)
        drops.add(new DropEntry(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, 0.03f, Formatting.RED, 1));
        drops.add(new DropEntry(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, 0.03f, Formatting.RED, 1));
        drops.add(new DropEntry(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, 0.03f, Formatting.RED, 1));
        drops.add(new DropEntry(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, 0.03f, Formatting.RED, 1));
        drops.add(new DropEntry(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, 0.03f, Formatting.RED, 1));
        drops.add(new DropEntry(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, 0.03f, Formatting.RED, 1));
        drops.add(new DropEntry(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, 0.03f, Formatting.RED, 1));
        // Upgrade template (RED)
        drops.add(new DropEntry(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 0.13f, Formatting.RED, 1));
        return drops;
    }
}