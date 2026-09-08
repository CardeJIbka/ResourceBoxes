package com.cardejibka.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ResourceBoxItem extends Item {
    public ResourceBoxItem(Properties settings) {
        super(settings);
    }

    protected abstract List<DropEntry> getDrops();

    private int getEnchantmentLevel(Random random, int maxLevel) {
        double randLevel = random.nextDouble();
        if (maxLevel == 1) {
            return 1;
        } else if (maxLevel == 2) {
            return randLevel < 0.70 ? 1 : 2;
        } else if (maxLevel == 3) {
            if (randLevel < 0.55) return 1;
            else if (randLevel < 0.85) return 2;
            else return 3;
        } else if (maxLevel == 4) {
            if (randLevel < 0.40) return 1;
            else if (randLevel < 0.70) return 2;
            else if (randLevel < 0.90) return 3;
            else return 4;
        } else if (maxLevel == 5) {
            if (randLevel < 0.35) return 1;
            else if (randLevel < 0.60) return 2;
            else if (randLevel < 0.80) return 3;
            else if (randLevel < 0.95) return 4;
            else return 5;
        }
        return 1;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (!world.isClientSide) {
            List<DropEntry> drops = getDrops();
            Random random = new Random();
            double rand = random.nextDouble();
            double cumulative = 0;
            DropEntry selectedEntry = null;

            for (DropEntry entry : drops) {
                cumulative += entry.chance;
                if (rand <= cumulative) {
                    selectedEntry = entry;
                    break;
                }
            }

            if (selectedEntry == null) {
                selectedEntry = drops.get(drops.size() - 1);
            }

            Item selectedItem = selectedEntry.item;

            // В 1.20.1 ещё нет DataComponentTypes.TOOL (появился в 1.20.5+), поэтому
            // инструменты определяем через DiggerItem (кирка/топор/лопата/мотыга).
            boolean isEquip = (selectedItem instanceof DiggerItem) ||
                    (selectedItem instanceof Equipable) ||
                    (selectedItem instanceof SwordItem) ||
                    selectedItem == Items.BOW || selectedItem == Items.CROSSBOW ||
                    selectedItem == Items.TRIDENT;

            int maxCount = selectedEntry.maxCount;
            int count;
            if (maxCount <= 1) {
                count = 1;
            } else {
                double randCount = random.nextDouble();
                int range1 = (int) Math.ceil(maxCount * 0.3);
                int range2 = (int) Math.ceil(maxCount * 0.6);
                if (randCount < 0.5) {
                    count = 1 + random.nextInt(Math.max(1, range1));
                } else if (randCount < 0.85) {
                    count = range1 + 1 + random.nextInt(Math.max(1, range2 - range1));
                } else {
                    count = range2 + 1 + random.nextInt(Math.max(1, maxCount - range2));
                }
            }

            ItemStack dropStack = new ItemStack(selectedItem, count);

            if (isEquip) {
                // В 1.20.1 зачарования - обычные объекты Enchantment (не Holder/RegistryEntry как в 1.21),
                // добавляются напрямую через ItemStack#enchant.
                if (selectedItem instanceof Equipable equipable) {
                    EquipmentSlot slot = equipable.getEquipmentSlot();

                    if (random.nextDouble() < 0.30) {
                        double protectionType = random.nextDouble();
                        if (protectionType < 0.33) {
                            dropStack.enchant(Enchantments.ALL_DAMAGE_PROTECTION, getEnchantmentLevel(random, 4));
                        } else if (protectionType < 0.66) {
                            dropStack.enchant(Enchantments.FIRE_PROTECTION, getEnchantmentLevel(random, 4));
                        } else {
                            dropStack.enchant(Enchantments.PROJECTILE_PROTECTION, getEnchantmentLevel(random, 4));
                        }
                    }
                    if (random.nextDouble() < 0.30) {
                        dropStack.enchant(Enchantments.UNBREAKING, getEnchantmentLevel(random, 3));
                    }
                    if (random.nextDouble() < 0.01) {
                        dropStack.enchant(Enchantments.MENDING, 1);
                    }

                    if (slot == EquipmentSlot.HEAD) {
                        if (random.nextDouble() < 0.30) {
                            dropStack.enchant(Enchantments.RESPIRATION, getEnchantmentLevel(random, 3));
                        }
                        if (random.nextDouble() < 0.10) {
                            dropStack.enchant(Enchantments.AQUA_AFFINITY, 1);
                        }
                    } else if (slot == EquipmentSlot.FEET) {
                        if (random.nextDouble() < 0.30) {
                            dropStack.enchant(Enchantments.FALL_PROTECTION, getEnchantmentLevel(random, 4));
                        }
                        if (random.nextDouble() < 0.10) {
                            dropStack.enchant(Enchantments.DEPTH_STRIDER, getEnchantmentLevel(random, 3));
                        }
                        if (random.nextDouble() < 0.10 && dropStack.getEnchantmentLevel(Enchantments.DEPTH_STRIDER) <= 0) {
                            dropStack.enchant(Enchantments.FROST_WALKER, getEnchantmentLevel(random, 2));
                        }
                    }
                } else if (selectedItem instanceof SwordItem) {
                    if (random.nextDouble() < 0.30) {
                        dropStack.enchant(Enchantments.SHARPNESS, getEnchantmentLevel(random, 5));
                    }
                    if (random.nextDouble() < 0.30) {
                        dropStack.enchant(Enchantments.UNBREAKING, getEnchantmentLevel(random, 3));
                    }
                    if (random.nextDouble() < 0.10) {
                        dropStack.enchant(Enchantments.FIRE_ASPECT, getEnchantmentLevel(random, 2));
                    }
                    if (random.nextDouble() < 0.10) {
                        dropStack.enchant(Enchantments.KNOCKBACK, getEnchantmentLevel(random, 2));
                    }
                    if (random.nextDouble() < 0.01) {
                        dropStack.enchant(Enchantments.MENDING, 1);
                    }
                } else if (selectedItem == Items.TRIDENT) {
                    if (random.nextDouble() < 0.30) {
                        dropStack.enchant(Enchantments.LOYALTY, getEnchantmentLevel(random, 3));
                    }
                    if (random.nextDouble() < 0.30) {
                        dropStack.enchant(Enchantments.IMPALING, getEnchantmentLevel(random, 5));
                    }
                    if (random.nextDouble() < 0.10) {
                        dropStack.enchant(Enchantments.CHANNELING, 1);
                    }
                    if (random.nextDouble() < 0.30) {
                        dropStack.enchant(Enchantments.UNBREAKING, getEnchantmentLevel(random, 3));
                    }
                    if (random.nextDouble() < 0.01) {
                        dropStack.enchant(Enchantments.MENDING, 1);
                    }
                } else if (selectedItem == Items.BOW) {
                    if (random.nextDouble() < 0.30) {
                        dropStack.enchant(Enchantments.POWER_ARROWS, getEnchantmentLevel(random, 5));
                    }
                    if (random.nextDouble() < 0.10) {
                        dropStack.enchant(Enchantments.PUNCH_ARROWS, getEnchantmentLevel(random, 2));
                    }
                    if (random.nextDouble() < 0.10) {
                        dropStack.enchant(Enchantments.FLAMING_ARROWS, 1);
                    }
                    if (random.nextDouble() < 0.30) {
                        dropStack.enchant(Enchantments.UNBREAKING, getEnchantmentLevel(random, 3));
                    }
                    AtomicBoolean hasInfinity = new AtomicBoolean(false);
                    if (random.nextDouble() < 0.10) {
                        dropStack.enchant(Enchantments.INFINITY_ARROWS, 1);
                        hasInfinity.set(true);
                    }
                    if (random.nextDouble() < 0.01 && !hasInfinity.get()) {
                        dropStack.enchant(Enchantments.MENDING, 1);
                    }
                } else if (selectedItem == Items.CROSSBOW) {
                    if (random.nextDouble() < 0.30) {
                        dropStack.enchant(Enchantments.QUICK_CHARGE, getEnchantmentLevel(random, 3));
                    }
                    if (random.nextDouble() < 0.30) {
                        dropStack.enchant(Enchantments.UNBREAKING, getEnchantmentLevel(random, 3));
                    }
                    AtomicBoolean hasMultishot = new AtomicBoolean(false);
                    if (random.nextDouble() < 0.10) {
                        dropStack.enchant(Enchantments.MULTISHOT, 1);
                        hasMultishot.set(true);
                    }
                    if (random.nextDouble() < 0.10 && !hasMultishot.get()) {
                        dropStack.enchant(Enchantments.PIERCING, getEnchantmentLevel(random, 4));
                    }
                    if (random.nextDouble() < 0.01) {
                        dropStack.enchant(Enchantments.MENDING, 1);
                    }
                } else if (selectedItem instanceof DiggerItem) {
                    if (random.nextDouble() < 0.30) {
                        dropStack.enchant(Enchantments.BLOCK_EFFICIENCY, getEnchantmentLevel(random, 5));
                    }
                    if (random.nextDouble() < 0.30) {
                        dropStack.enchant(Enchantments.UNBREAKING, getEnchantmentLevel(random, 3));
                    }
                    AtomicBoolean hasFortune = new AtomicBoolean(false);
                    if (random.nextDouble() < 0.10) {
                        dropStack.enchant(Enchantments.BLOCK_FORTUNE, getEnchantmentLevel(random, 3));
                        hasFortune.set(true);
                    }
                    if (random.nextDouble() < 0.10 && !hasFortune.get()) {
                        dropStack.enchant(Enchantments.SILK_TOUCH, 1);
                    }
                    if (random.nextDouble() < 0.01) {
                        dropStack.enchant(Enchantments.MENDING, 1);
                    }
                }
            }

            ItemStack tempStack = itemStack.copy();
            tempStack.shrink(1);
            user.setItemInHand(hand, tempStack);

            if (!user.getInventory().add(dropStack)) {
                user.drop(dropStack, false);
            }

            return InteractionResultHolder.success(user.getItemInHand(hand));
        }
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, level, tooltip, type);

        tooltip.add(Component.translatable("tooltip.resourceboxes.possible_drops").withStyle(ChatFormatting.GRAY));
        for (DropEntry entry : getDrops()) {
            float chancePercent = entry.chance * 100;
            String chanceStr = chancePercent % 1 == 0 ? String.format("%.0f%%", chancePercent) : String.format("%.1f%%", chancePercent);
            MutableComponent itemName = Component.literal(entry.item.getDescription().getString()).withStyle(entry.color);
            MutableComponent tooltipText = itemName.append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(chanceStr).withStyle(ChatFormatting.GRAY));
            if (entry.maxCount > 1) {
                tooltipText = tooltipText.append(Component.literal(" (x1 - x" + entry.maxCount + ")").withStyle(ChatFormatting.GRAY));
            }
            tooltip.add(tooltipText);
        }
    }

    protected record DropEntry(Item item, float chance, ChatFormatting color, int maxCount) {}
}

class OreBoxItem extends ResourceBoxItem {
    public OreBoxItem(Properties settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        List<DropEntry> drops = new ArrayList<>();
        drops.add(new DropEntry(Items.COAL, 0.15f, ChatFormatting.GREEN, 32));
        drops.add(new DropEntry(Items.COPPER_INGOT, 0.15f, ChatFormatting.GREEN, 32));
        drops.add(new DropEntry(Items.REDSTONE, 0.12f, ChatFormatting.GREEN, 48));
        drops.add(new DropEntry(Items.LAPIS_LAZULI, 0.12f, ChatFormatting.GREEN, 48));
        drops.add(new DropEntry(Items.IRON_INGOT, 0.14f, ChatFormatting.BLUE, 24));
        drops.add(new DropEntry(Items.QUARTZ, 0.08f, ChatFormatting.BLUE, 24));
        drops.add(new DropEntry(Items.GOLD_INGOT, 0.10f, ChatFormatting.LIGHT_PURPLE, 24));
        drops.add(new DropEntry(Items.AMETHYST_SHARD, 0.06f, ChatFormatting.LIGHT_PURPLE, 12));
        drops.add(new DropEntry(Items.EMERALD, 0.05f, ChatFormatting.RED, 8));
        drops.add(new DropEntry(Items.DIAMOND, 0.03f, ChatFormatting.RED, 8));
        return drops;
    }
}

class FoodBoxItem extends ResourceBoxItem {
    public FoodBoxItem(Properties settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        List<DropEntry> drops = new ArrayList<>();
        drops.add(new DropEntry(Items.BEETROOT, 0.077f, ChatFormatting.GREEN, 32));
        drops.add(new DropEntry(Items.SWEET_BERRIES, 0.077f, ChatFormatting.GREEN, 32));
        drops.add(new DropEntry(Items.TROPICAL_FISH, 0.061f, ChatFormatting.GREEN, 32));
        drops.add(new DropEntry(Items.DRIED_KELP, 0.061f, ChatFormatting.GREEN, 32));
        drops.add(new DropEntry(Items.GLOW_BERRIES, 0.071f, ChatFormatting.BLUE, 24));
        drops.add(new DropEntry(Items.BREAD, 0.071f, ChatFormatting.BLUE, 24));
        drops.add(new DropEntry(Items.CARROT, 0.061f, ChatFormatting.BLUE, 24));
        drops.add(new DropEntry(Items.POTATO, 0.061f, ChatFormatting.BLUE, 24));
        drops.add(new DropEntry(Items.COOKED_COD, 0.051f, ChatFormatting.BLUE, 24));
        drops.add(new DropEntry(Items.COOKED_SALMON, 0.051f, ChatFormatting.BLUE, 24));
        drops.add(new DropEntry(Items.COOKED_CHICKEN, 0.041f, ChatFormatting.BLUE, 24));
        drops.add(new DropEntry(Items.HONEY_BOTTLE, 0.051f, ChatFormatting.LIGHT_PURPLE, 16));
        drops.add(new DropEntry(Items.APPLE, 0.051f, ChatFormatting.LIGHT_PURPLE, 16));
        drops.add(new DropEntry(Items.COOKED_BEEF, 0.041f, ChatFormatting.LIGHT_PURPLE, 16));
        drops.add(new DropEntry(Items.COOKED_PORKCHOP, 0.041f, ChatFormatting.LIGHT_PURPLE, 16));
        drops.add(new DropEntry(Items.CAKE, 0.031f, ChatFormatting.LIGHT_PURPLE, 2));
        drops.add(new DropEntry(Items.GOLDEN_CARROT, 0.031f, ChatFormatting.LIGHT_PURPLE, 12));
        drops.add(new DropEntry(Items.CHORUS_FRUIT, 0.026f, ChatFormatting.LIGHT_PURPLE, 12));
        drops.add(new DropEntry(Items.PUMPKIN_PIE, 0.026f, ChatFormatting.LIGHT_PURPLE, 12));
        drops.add(new DropEntry(Items.GOLDEN_APPLE, 0.015f, ChatFormatting.RED, 4));
        drops.add(new DropEntry(Items.ENCHANTED_GOLDEN_APPLE, 0.004f, ChatFormatting.RED, 1));
        return drops;
    }
}

class WeaponBoxItem extends ResourceBoxItem {
    public WeaponBoxItem(Properties settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        // Items.MACE отсутствует в 1.20.1 (добавлен в 1.21) - убран из списка.
        List<DropEntry> drops = new ArrayList<>();
        drops.add(new DropEntry(Items.WOODEN_SWORD, 0.156f, ChatFormatting.GREEN, 1));
        drops.add(new DropEntry(Items.STONE_SWORD, 0.156f, ChatFormatting.GREEN, 1));
        drops.add(new DropEntry(Items.BOW, 0.123f, ChatFormatting.GREEN, 1));
        drops.add(new DropEntry(Items.IRON_SWORD, 0.128f, ChatFormatting.BLUE, 1));
        drops.add(new DropEntry(Items.CROSSBOW, 0.103f, ChatFormatting.BLUE, 1));
        drops.add(new DropEntry(Items.SHIELD, 0.103f, ChatFormatting.BLUE, 1));
        drops.add(new DropEntry(Items.GOLDEN_SWORD, 0.128f, ChatFormatting.BLUE, 1));
        drops.add(new DropEntry(Items.DIAMOND_SWORD, 0.070f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.TRIDENT, 0.020f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.NETHERITE_SWORD, 0.013f, ChatFormatting.RED, 1));
        return drops;
    }
}

class ToolBoxItem extends ResourceBoxItem {
    public ToolBoxItem(Properties settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        List<DropEntry> drops = new ArrayList<>();
        drops.add(new DropEntry(Items.WOODEN_PICKAXE, 0.120f, ChatFormatting.GREEN, 1));
        drops.add(new DropEntry(Items.WOODEN_AXE, 0.120f, ChatFormatting.GREEN, 1));
        drops.add(new DropEntry(Items.WOODEN_SHOVEL, 0.120f, ChatFormatting.GREEN, 1));
        drops.add(new DropEntry(Items.WOODEN_HOE, 0.120f, ChatFormatting.GREEN, 1));
        drops.add(new DropEntry(Items.STONE_PICKAXE, 0.080f, ChatFormatting.BLUE, 1));
        drops.add(new DropEntry(Items.STONE_AXE, 0.080f, ChatFormatting.BLUE, 1));
        drops.add(new DropEntry(Items.STONE_SHOVEL, 0.080f, ChatFormatting.BLUE, 1));
        drops.add(new DropEntry(Items.STONE_HOE, 0.080f, ChatFormatting.BLUE, 1));
        drops.add(new DropEntry(Items.DIAMOND_PICKAXE, 0.040f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.DIAMOND_AXE, 0.040f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.DIAMOND_SHOVEL, 0.040f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.DIAMOND_HOE, 0.040f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.NETHERITE_PICKAXE, 0.010f, ChatFormatting.RED, 1));
        drops.add(new DropEntry(Items.NETHERITE_AXE, 0.010f, ChatFormatting.RED, 1));
        drops.add(new DropEntry(Items.NETHERITE_SHOVEL, 0.010f, ChatFormatting.RED, 1));
        drops.add(new DropEntry(Items.NETHERITE_HOE, 0.010f, ChatFormatting.RED, 1));
        return drops;
    }
}

class ArmorBoxItem extends ResourceBoxItem {
    public ArmorBoxItem(Properties settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        List<DropEntry> drops = new ArrayList<>();
        drops.add(new DropEntry(Items.LEATHER_HELMET, 0.120f, ChatFormatting.GREEN, 1));
        drops.add(new DropEntry(Items.LEATHER_CHESTPLATE, 0.120f, ChatFormatting.GREEN, 1));
        drops.add(new DropEntry(Items.LEATHER_LEGGINGS, 0.120f, ChatFormatting.GREEN, 1));
        drops.add(new DropEntry(Items.LEATHER_BOOTS, 0.120f, ChatFormatting.GREEN, 1));
        drops.add(new DropEntry(Items.IRON_HELMET, 0.080f, ChatFormatting.BLUE, 1));
        drops.add(new DropEntry(Items.IRON_CHESTPLATE, 0.080f, ChatFormatting.BLUE, 1));
        drops.add(new DropEntry(Items.IRON_LEGGINGS, 0.080f, ChatFormatting.BLUE, 1));
        drops.add(new DropEntry(Items.IRON_BOOTS, 0.080f, ChatFormatting.BLUE, 1));
        drops.add(new DropEntry(Items.DIAMOND_HELMET, 0.040f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.DIAMOND_CHESTPLATE, 0.040f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.DIAMOND_LEGGINGS, 0.040f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.DIAMOND_BOOTS, 0.040f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.NETHERITE_HELMET, 0.010f, ChatFormatting.RED, 1));
        drops.add(new DropEntry(Items.NETHERITE_CHESTPLATE, 0.010f, ChatFormatting.RED, 1));
        drops.add(new DropEntry(Items.NETHERITE_LEGGINGS, 0.010f, ChatFormatting.RED, 1));
        drops.add(new DropEntry(Items.NETHERITE_BOOTS, 0.010f, ChatFormatting.RED, 1));
        return drops;
    }
}

class NetherBoxItem extends ResourceBoxItem {
    public NetherBoxItem(Properties settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        List<DropEntry> drops = new ArrayList<>();
        drops.add(new DropEntry(Items.QUARTZ, 0.17f, ChatFormatting.GREEN, 16));
        drops.add(new DropEntry(Items.NETHER_WART, 0.17f, ChatFormatting.GREEN, 16));
        drops.add(new DropEntry(Items.GOLD_NUGGET, 0.14f, ChatFormatting.GREEN, 32));
        drops.add(new DropEntry(Items.BLAZE_ROD, 0.13f, ChatFormatting.BLUE, 8));
        drops.add(new DropEntry(Items.GHAST_TEAR, 0.115f, ChatFormatting.BLUE, 4));
        drops.add(new DropEntry(Items.NETHER_BRICK, 0.10f, ChatFormatting.BLUE, 16));
        drops.add(new DropEntry(Items.QUARTZ_BLOCK, 0.05f, ChatFormatting.LIGHT_PURPLE, 4));
        drops.add(new DropEntry(Items.ANCIENT_DEBRIS, 0.04f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.GLOWSTONE_DUST, 0.05f, ChatFormatting.LIGHT_PURPLE, 16));
        drops.add(new DropEntry(Items.NETHERITE_SCRAP, 0.02f, ChatFormatting.RED, 2));
        drops.add(new DropEntry(Items.NETHERITE_INGOT, 0.015f, ChatFormatting.RED, 1));
        return drops;
    }
}

class EndBoxItem extends ResourceBoxItem {
    public EndBoxItem(Properties settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        List<DropEntry> drops = new ArrayList<>();
        drops.add(new DropEntry(Items.CHORUS_FRUIT, 0.18f, ChatFormatting.GREEN, 8));
        drops.add(new DropEntry(Items.PURPUR_BLOCK, 0.16f, ChatFormatting.GREEN, 4));
        drops.add(new DropEntry(Items.ENDER_PEARL, 0.16f, ChatFormatting.BLUE, 8));
        drops.add(new DropEntry(Items.CHORUS_FLOWER, 0.10f, ChatFormatting.BLUE, 4));
        drops.add(new DropEntry(Items.END_STONE, 0.12f, ChatFormatting.BLUE, 8));
        drops.add(new DropEntry(Items.DRAGON_BREATH, 0.08f, ChatFormatting.LIGHT_PURPLE, 2));
        drops.add(new DropEntry(Items.END_CRYSTAL, 0.06f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.SHULKER_SHELL, 0.06f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.END_ROD, 0.06f, ChatFormatting.LIGHT_PURPLE, 4));
        drops.add(new DropEntry(Items.ELYTRA, 0.02f, ChatFormatting.RED, 1));
        return drops;
    }
}

class TrimBoxItem extends ResourceBoxItem {
    public TrimBoxItem(Properties settings) {
        super(settings);
    }

    @Override
    protected List<DropEntry> getDrops() {
        List<DropEntry> drops = new ArrayList<>();
        drops.add(new DropEntry(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, ChatFormatting.BLUE, 1));
        drops.add(new DropEntry(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, ChatFormatting.BLUE, 1));
        drops.add(new DropEntry(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, ChatFormatting.BLUE, 1));
        drops.add(new DropEntry(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, 0.06f, ChatFormatting.LIGHT_PURPLE, 1));
        drops.add(new DropEntry(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, 0.03f, ChatFormatting.RED, 1));
        drops.add(new DropEntry(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, 0.03f, ChatFormatting.RED, 1));
        drops.add(new DropEntry(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, 0.03f, ChatFormatting.RED, 1));
        drops.add(new DropEntry(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, 0.03f, ChatFormatting.RED, 1));
        drops.add(new DropEntry(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, 0.03f, ChatFormatting.RED, 1));
        drops.add(new DropEntry(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, 0.03f, ChatFormatting.RED, 1));
        drops.add(new DropEntry(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, 0.03f, ChatFormatting.RED, 1));
        drops.add(new DropEntry(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 0.25f, ChatFormatting.RED, 1));
        return drops;
    }
}