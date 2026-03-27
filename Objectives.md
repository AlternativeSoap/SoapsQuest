# Objectives

Objectives are the tasks players need to complete to finish a quest. Each quest can have one or more objectives. This page lists every objective type available in SoapsQuest.

---

## How to Add Objectives

Objectives go in the `objectives` section of your quest, as a list. Each item in the list needs a `type`, a `target`, and an `amount`:

```yaml
objectives:
  - type: kill
    target: ZOMBIE
    amount: 10
```

For quests with multiple objectives, just add more items to the list:

```yaml
objectives:
  - type: kill
    target: ZOMBIE
    amount: 10
  - type: mine
    target: DIAMOND_ORE
    amount: 5
```

If your quest has `sequential: true`, objectives are completed one at a time in the order you list them.

---

## Combat Objectives

### kill

Kill a specific type of mob.

```yaml
- type: kill
  target: ZOMBIE
  amount: 20
```

The `target` is a Minecraft entity type name in uppercase (like `ZOMBIE`, `SKELETON`, `CREEPER`, `BLAZE`, `ENDER_DRAGON`).

Use `ANY` to count kills of any mob type:

```yaml
- type: kill
  target: ANY
  amount: 50
```

### kill_mythicmob

Kill a custom mob defined in MythicMobs.

> **Note:** This objective requires the MythicMobs plugin to be installed on your server.

```yaml
- type: kill_mythicmob
  target: SkeletonKing
  amount: 1
```

The `target` is the MythicMobs internal mob name (case-sensitive, exactly as defined in your MythicMobs config).

### kill_player

Kill another player.

```yaml
- type: kill_player
  amount: 5
```

This counts player versus player kills. No `target` is needed.

### bow_kill

Kill a mob using a bow.

```yaml
- type: bow_kill
  target: SKELETON
  amount: 10
```

Only bow kills count. Any other weapon does not progress this objective.

### tnt_kill

Kill a mob with TNT (the explosion must kill it).

```yaml
- type: tnt_kill
  target: ANY
  amount: 3
```

---

## Mining and Building Objectives

### break

Break a specific block.

```yaml
- type: break
  target: DIAMOND_ORE
  amount: 10
```

The `target` is a Minecraft block name in uppercase. Players need to actually break the block with a tool.

### mine

Same as break, but specifically for mining ores and stone-type blocks.

```yaml
- type: mine
  target: IRON_ORE
  amount: 30
```

### place

Place a specific block.

```yaml
- type: place
  target: COBBLESTONE
  amount: 50
```

### smelt

Smelt a specific item in a furnace, blast furnace, or smoker.

```yaml
- type: smelt
  target: IRON_INGOT
  amount: 20
```

The `target` is what comes out of the furnace (the product, not the raw material).

### craft

Craft a specific item using a crafting table or inventory crafting.

```yaml
- type: craft
  target: BREAD
  amount: 10
```

---

## Collection Objectives

### collect

Pick up a specific item from the ground or collect it from breaking a block.

```yaml
- type: collect
  target: NETHER_WART
  amount: 16
```

### pickup

Pick up a specific item from the ground (dropped items only).

```yaml
- type: pickup
  target: BONE
  amount: 20
```

### consume

Eat or drink a specific item.

```yaml
- type: consume
  target: BREAD
  amount: 10
```

Drinking potions also counts if the target is the correct potion item name.

---

## Fishing Objectives

### fish

Catch something with a fishing rod.

```yaml
- type: fish
  target: ANY
  amount: 10
```

You can target specific fish types like `COD`, `SALMON`, `TROPICAL_FISH`, `PUFFERFISH`, or use `ANY` to count any catch at all (including junk and treasure).

---

## Farming and Animal Objectives

### harvest

Harvest a fully grown crop.

```yaml
- type: harvest
  target: WHEAT
  amount: 30
```

This only counts crops that were fully grown when broken.

### breed

Breed two animals to produce a baby.

```yaml
- type: breed
  target: COW
  amount: 5
```

Use `ANY` to count any animal breeding.

### tame

Tame a tameable animal.

```yaml
- type: tame
  target: WOLF
  amount: 3
```

Tameable mobs include `WOLF`, `CAT`, `HORSE`, `LLAMA`, and others.

### shear

Shear a sheep.

```yaml
- type: shear
  target: SHEEP
  amount: 15
```

### milk

Milk a cow with a bucket.

```yaml
- type: milk
  target: COW
  amount: 5
```

---

## Survival Objectives

### die

Die a certain number of times.

```yaml
- type: die
  amount: 3
```

No `target` is needed. Any cause of death counts.

### sleep

Sleep in a bed.

```yaml
- type: sleep
  amount: 5
```

Counts each time the player sleeps through the night.

### eat

Eat any food item.

```yaml
- type: eat
  target: ANY
  amount: 20
```

You can specify a food type like `COOKED_BEEF` or use `ANY`.

### brew

Brew a potion in a brewing stand.

```yaml
- type: brew
  target: ANY
  amount: 5
```

Counts each completed brew cycle. Use `ANY` to count any potion.

### enchant

Enchant an item at an enchanting table.

```yaml
- type: enchant
  target: ANY
  amount: 5
```

Use `ANY` to count any item enchanted.

---

## Movement Objectives

### travel

Travel a certain distance in blocks.

```yaml
- type: travel
  amount: 1000
```

All movement counts: walking, swimming, flying, riding. The number is in blocks.

### swim

Swim a certain distance.

```yaml
- type: swim
  amount: 200
```

Only swimming movement counts.

### sprint

Sprint a certain distance.

```yaml
- type: sprint
  amount: 500
```

Only sprinting movement counts.

### sneak

Sneak (crouch) a certain distance while holding the sneak key.

```yaml
- type: sneak
  amount: 100
```

### ride_vehicle

Ride a vehicle or mount for a certain distance.

```yaml
- type: ride_vehicle
  target: ANY
  amount: 500
```

Vehicles include horses, minecarts, boats, pigs, and striders. Use a specific entity type to limit it to one kind, or `ANY` to count all.

---

## Leveling Objectives

### reach_level

Reach a specific experience level. Progress counts up as the player gains levels.

```yaml
- type: reach_level
  amount: 30
```

The `amount` is the target level. Progress is tracked as the player levels up toward it.

### gain_levels

Gain a certain number of experience levels, regardless of current level.

```yaml
- type: gain_levels
  amount: 10
```

This counts levels gained during the quest, not total levels.

### gain_xp

Gain a certain amount of raw experience points.

```yaml
- type: gain_xp
  amount: 5000
```

---

## Miscellaneous Objectives

### trade

Complete a villager trade a certain number of times.

```yaml
- type: trade
  target: ANY
  amount: 10
```

You can use `ANY` to count all trades, or specify a material to only count trades that produce that item (like `EMERALD`).

### open_chest

Open a chest a certain number of times.

```yaml
- type: open_chest
  target: CHEST
  amount: 20
```

Target options: `CHEST`, `BARREL`, `SHULKER_BOX`, `TRAPPED_CHEST`, or `ANY`.

### drop_item

Drop a specific item from the inventory.

```yaml
- type: drop_item
  target: STICK
  amount: 5
```

---

## Quick Reference Table

| Type | What Players Need to Do | `target` Examples |
|------|------------------------|------------------|
| `kill` | Kill mobs | `ZOMBIE`, `SKELETON`, `ANY` |
| `kill_mythicmob` | Kill a MythicMobs mob | MythicMobs mob name |
| `kill_player` | Kill players | (none needed) |
| `bow_kill` | Kill mobs with a bow | `SKELETON`, `ANY` |
| `tnt_kill` | Kill mobs with TNT | `CREEPER`, `ANY` |
| `break` | Break blocks | `DIRT`, `OAK_LOG`, `STONE` |
| `mine` | Mine ores or stone | `IRON_ORE`, `DIAMOND_ORE` |
| `place` | Place blocks | `COBBLESTONE`, `OAK_LOG` |
| `smelt` | Smelt items in a furnace | `IRON_INGOT`, `COOKED_BEEF` |
| `craft` | Craft items | `BREAD`, `IRON_SWORD` |
| `collect` | Collect items | `NETHER_WART`, `WHEAT` |
| `pickup` | Pick up dropped items | `BONE`, `ARROW` |
| `consume` | Eat or drink | `BREAD`, `COOKED_COD` |
| `fish` | Fish with a rod | `COD`, `SALMON`, `ANY` |
| `harvest` | Harvest grown crops | `WHEAT`, `CARROTS` |
| `breed` | Breed animals | `COW`, `SHEEP`, `ANY` |
| `tame` | Tame animals | `WOLF`, `CAT`, `HORSE` |
| `shear` | Shear sheep | `SHEEP` |
| `milk` | Milk cows | `COW` |
| `die` | Die | (none needed) |
| `sleep` | Sleep in a bed | (none needed) |
| `eat` | Eat food | `BREAD`, `ANY` |
| `brew` | Brew potions | `ANY` |
| `enchant` | Enchant items | `ANY` |
| `travel` | Travel any distance | (none needed) |
| `swim` | Swim a distance | (none needed) |
| `sprint` | Sprint a distance | (none needed) |
| `sneak` | Sneak a distance | (none needed) |
| `ride_vehicle` | Ride a mount or vehicle | `HORSE`, `MINECART`, `ANY` |
| `reach_level` | Reach an XP level | (none needed) |
| `gain_levels` | Gain XP levels | (none needed) |
| `gain_xp` | Gain experience points | (none needed) |
| `trade` | Trade with a villager | `ANY` |
| `open_chest` | Open containers | `CHEST`, `BARREL`, `ANY` |
| `drop_item` | Drop an item | `STICK`, `DIRT` |
