# Resource Boxes
Resource Boxes is a Minecraft mod for Fabric that adds special "Resource Boxes," which, when used, dispense random resources, items, or equipment. Boxes can be found in chests of various structures (dungeons, villages, fortresses, etc.) with set probabilities, or crafted from the new "Resource Ore," which generates in the Overworld. Each box has a unique loot table with probabilities and chances for enchantments on tools, weapons, and armor.
The mod makes exploration more exciting by adding an element of luck to loot, and simplifies resource gathering in survival mode. The current version is for Fabric 1.21.8, but ports to other Minecraft versions are planned.
## Main Features

- 8 types of boxes: Ore Box (ores and ingots), Food Box (food), Weapon Box (weapons), Tool Box (tools), Armor Box (armor), Nether Box (Nether resources), End Box (End resources), and Trim Box (armor trim patterns).
- Random loot: Each box type has a drop table with probabilities and maximum item quantities. For equipment — chance for random enchantments (up to Mending with low probability).
- World generation: Resource Ore and Deepslate Resource Ore generate in the Overworld from Y=-64 to 32 (deepslate version below Y=0). Added to Overworld biomes in the UNDERGROUND_ORES stage.
- Loot in structures: Boxes and Resource Gems are integrated into Minecraft structure loot tables with balanced probabilities (see "Spawn Locations" section).
- New items: Resource Shard (from ore), Resource Gem (from shards), Resource Block (block from gems).
- Tooltip hints: Show possible drops with probabilities and quantity ranges.
- Compatibility: Fabric 1.21.8+. Works with JEI/REI for viewing recipes and loot. Support for other versions is planned.

## Usage
### Mining


- **Resource Ore / Deepslate Resource Ore**: Rarely generates in the Overworld from Y=-64 to 32. Deepslate version below Y=0. Mine with an iron pickaxe or higher.

- **With Silk Touch**: Drops the ore block itself.
- **Without Silk Touch**: Drops Resource Shards (1 base, Fortune increases drop according to ore_drops formula).
- **Explosion (TNT, etc.)**: Shards with decay (chance of loss).



## Opening Boxes
Right-click to use a box. It will disappear, dispensing a random item (or stack) from its table.

- For equipment (weapons, tools, armor): 30% chance for main enchantments (Sharpness, Efficiency, etc.), 10% for secondary ones, 1% for Mending. Enchantment levels are random (1-5 depending on type).
- Quantity: For stacks — from 1 to maxCount with distribution (more common in the middle values).