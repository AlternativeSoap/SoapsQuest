# Objectives

Objectives are the tasks players need to complete to finish a quest. Each quest can have one or more objectives. This page lists every objective type available in SoapsQuest.

---

## How to Add Objectives

Objectives go in the `objectives` section of your quest, as a list. Each item in the list needs a `type`, a `target` (for most types), and an `amount`:

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
  - type: break
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

Special filters:

- `ANY` — counts kills of any mob
- `HOSTILE` — counts kills of hostile mobs only
- `PASSIVE` — counts kills of passive mobs only

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

### damage

Deal a certain amount of damage to entities.

```yaml
- type: damage
  target: ZOMBIE
  amount: 100
```

The `amount` is total damage dealt (in half-hearts). Use a specific entity type or omit the target to count damage dealt to anything.

### bowshoot

Shoot arrows with a bow a certain number of times.

```yaml
- type: bowshoot
  amount: 100
```

Counts each arrow shot, not kills.

### projectile

Launch projectiles of a specific type.

```yaml
- type: projectile
  target: ARROW
  amount: 50
```

Includes arrows, snowballs, ender pearls, tridents, and more. Use `ANY` to count all projectile launches.

---

## Mining and Building Objectives

### break

Break a specific block.

```yaml
- type: break
  target: DIAMOND_ORE
  amount: 10
```

The `target` is a Minecraft block material name in uppercase. Use `ANY` to count any block broken.

### place

Place a specific block.

```yaml
- type: place
  target: COBBLESTONE
  amount: 50
```

Use `ANY` to count any block placed.

### smelt

Smelt a specific item in a furnace, blast furnace, or smoker.

```yaml
- type: smelt
  target: IRON_INGOT
  amount: 20
```

The `target` is what comes out of the furnace (the product, not the raw material). Use `ANY` to count any smelted item.

### craft

Craft a specific item using a crafting table or inventory crafting.

```yaml
- type: craft
  target: BREAD
  amount: 10
```

Use `ANY` to count any item crafted.

### enchant

Enchant an item at an enchanting table.

```yaml
- type: enchant
  target: DIAMOND_SWORD
  amount: 5
```

Use `ANY` to count any item enchanted.

### anvil_repair

Repair an item on an anvil.

```yaml
- type: anvil_repair
  target: DIAMOND_SWORD
  amount: 5
```

Use `ANY` to count any item repaired on an anvil. Target is optional.

---

## Collection Objectives

### collect

Pick up a specific item from the ground or collect it from breaking a block.

```yaml
- type: collect
  target: NETHER_WART
  amount: 16
```

Use `ANY` to count any item collected.

### consume

Eat or drink a specific item.

```yaml
- type: consume
  target: BREAD
  amount: 10
```

Drinking potions also counts if the target is the correct potion item name. Use `ANY` to count any food or drink consumed.

### drop

Drop a specific item from the inventory.

```yaml
- type: drop
  target: STICK
  amount: 5
```

Use `ANY` to count any item dropped.

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

This only counts crops that were fully grown when broken. Target is optional — omit it or use `ANY` to count any crop harvest.

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

Tameable mobs include `WOLF`, `CAT`, `HORSE`, `LLAMA`, and others. Use `ANY` to count any taming.

### shear

Shear an entity (typically sheep).

```yaml
- type: shear
  target: SHEEP
  amount: 15
```

Use `ANY` to count shearing any entity.

---

## Survival Objectives

### death

Die a certain number of times.

```yaml
- type: death
  amount: 3
```

No `target` is needed. Any cause of death counts.

### sleep

Sleep in a bed.

```yaml
- type: sleep
  amount: 5
```

Counts each time the player enters a bed.

### heal

Regenerate a certain amount of health.

```yaml
- type: heal
  amount: 20
```

The `amount` is total health regenerated (in half-hearts). Target is optional — you can filter by heal reason or leave blank for any healing.

### brew

Brew a potion in a brewing stand.

```yaml
- type: brew
  target: ANY
  amount: 5
```

Counts each completed brew cycle. Use `ANY` to count any potion brewed.

---

## Movement Objectives

### move

Travel a certain distance in blocks.

```yaml
- type: move
  amount: 1000
```

All movement counts: walking, swimming, flying, riding. The `amount` is in blocks (distance).

### ride_vehicle / vehicle

Ride a vehicle or mount for a certain distance.

```yaml
- type: vehicle
  target: ANY
  amount: 500
```

Vehicles include horses, minecarts, boats, pigs, and striders. Use a specific entity type to limit it to one kind, or `ANY` to count all.

### elytra_fly

Glide a certain distance using an elytra.

```yaml
- type: elytra_fly
  amount: 500
```

Only elytra flight distance counts. The `amount` is in blocks.

---

## Exploration Objectives

### explore_biome

Enter a specific biome (or discover any new biome).

```yaml
- type: explore_biome
  target: JUNGLE
  amount: 3
```

Use a specific biome name (uppercase, matching Minecraft biome names like `JUNGLE`, `DESERT`, `DEEP_OCEAN`) or omit the target / use `ANY` to count entering any biome.

---

## Leveling Objectives

### reachlevel

Reach a specific experience level.

```yaml
- type: reachlevel
  level: 30
```

Note: this type uses `level` instead of `amount`. Progress is tracked as the player levels up toward the target.

### gainlevel

Gain a certain number of experience levels, regardless of current level.

```yaml
- type: gainlevel
  amount: 10
```

This counts levels gained during the quest, not total levels.

### xp_pickup

Collect a certain amount of raw experience points from orbs.

```yaml
- type: xp_pickup
  amount: 5000
```

---

## Interaction Objectives

### interact

Interact with (right-click) a specific block type.

```yaml
- type: interact
  target: LEVER
  amount: 10
```

Use `ANY` to count any block interaction.

### trade

Complete a villager trade a certain number of times.

```yaml
- type: trade
  target: ANY
  amount: 10
```

You can use `ANY` to count all trades, or specify a material to only count trades that produce that item (like `EMERALD`).

---

## Miscellaneous Objectives

### jump

Jump a certain number of times.

```yaml
- type: jump
  amount: 100
```

### chat

Send a certain number of chat messages.

```yaml
- type: chat
  amount: 50
```

You can optionally require a specific message text in the `target` field, or leave it blank for any chat message.

### command

Execute a specific command a certain number of times.

```yaml
- type: command
  command: "/help"
  amount: 5
```

Note: this type uses `command` instead of `target` to specify which command to track.

### placeholder

Check a PlaceholderAPI value.

> **Note:** This objective requires PlaceholderAPI to be installed on your server.

```yaml
- type: placeholder
  placeholder: "%player_level%"
  amount: 30
```

Note: this type uses `placeholder` instead of `target`.

### firework

Launch firework rockets.

```yaml
- type: firework
  amount: 20
```

---

## Quick Reference Table

| Type | What Players Need to Do | `target` Examples |
|------|------------------------|------------------|
| `kill` | Kill mobs | `ZOMBIE`, `SKELETON`, `ANY`, `HOSTILE`, `PASSIVE` |
| `kill_mythicmob` | Kill a MythicMobs mob | MythicMobs mob name |
| `damage` | Deal damage to entities | `ZOMBIE`, `ANY` (optional) |
| `bowshoot` | Shoot arrows with a bow | (none needed) |
| `projectile` | Launch projectiles | `ARROW`, `SNOWBALL`, `ANY` |
| `break` | Break blocks | `DIAMOND_ORE`, `OAK_LOG`, `ANY` |
| `place` | Place blocks | `COBBLESTONE`, `OAK_LOG`, `ANY` |
| `smelt` | Smelt items in a furnace | `IRON_INGOT`, `COOKED_BEEF`, `ANY` |
| `craft` | Craft items | `BREAD`, `IRON_SWORD`, `ANY` |
| `enchant` | Enchant items | `DIAMOND_SWORD`, `ANY` |
| `anvil_repair` | Repair items on an anvil | `DIAMOND_SWORD`, `ANY` (optional) |
| `collect` | Collect items | `NETHER_WART`, `WHEAT`, `ANY` |
| `consume` | Eat or drink | `BREAD`, `COOKED_COD`, `ANY` |
| `drop` | Drop items | `STICK`, `DIRT`, `ANY` |
| `fish` | Fish with a rod | `COD`, `SALMON`, `ANY` |
| `harvest` | Harvest grown crops | `WHEAT`, `CARROTS`, `ANY` (optional) |
| `breed` | Breed animals | `COW`, `SHEEP`, `ANY` |
| `tame` | Tame animals | `WOLF`, `CAT`, `HORSE`, `ANY` |
| `shear` | Shear entities | `SHEEP`, `ANY` |
| `death` | Die | (none needed) |
| `sleep` | Sleep in a bed | (none needed) |
| `heal` | Regenerate health | (optional) |
| `brew` | Brew potions | `ANY` |
| `move` | Travel any distance | (none needed) |
| `vehicle` | Ride a mount or vehicle | `HORSE`, `MINECART`, `ANY` |
| `elytra_fly` | Glide with an elytra | (none needed) |
| `explore_biome` | Enter a biome | `JUNGLE`, `DESERT`, `ANY` (optional) |
| `reachlevel` | Reach an XP level | uses `level` field |
| `gainlevel` | Gain XP levels | (none needed) |
| `xp_pickup` | Collect XP from orbs | (none needed) |
| `interact` | Interact with blocks | `LEVER`, `OAK_BUTTON`, `ANY` |
| `trade` | Trade with villagers | `EMERALD`, `ANY` |
| `jump` | Jump | (none needed) |
| `chat` | Send chat messages | (optional text) |
| `command` | Execute commands | uses `command` field |
| `placeholder` | Check PAPI values | uses `placeholder` field |
| `firework` | Launch fireworks | (none needed) |
