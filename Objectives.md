# Objectives

Objectives define what a player needs to do to complete a quest. SoapsQuest has over 30 objective types covering combat, mining, crafting, farming, exploration, and more.

---

## Objective Format

Every objective follows this structure:

```yaml
objectives:
  - type: kill          # The objective type
    target: ZOMBIE      # What to target (entity, block, item - depends on type)
    amount: 10          # How many
```

Some objectives only need `type` and `amount` (no target). Some support `ANY` as a target to accept anything.

---

## Combat Objectives

### `kill` - Kill Entities
Kill a specific type of mob.

```yaml
- type: kill
  target: ZOMBIE
  amount: 20
```

**Target:** Any valid Minecraft entity type - `ZOMBIE`, `SKELETON`, `CREEPER`, `BLAZE`, `ENDER_DRAGON`, etc.
**Special targets:** `ANY` (any mob), `HOSTILE` (any hostile mob), `PASSIVE` (any passive mob).

### `damage` - Deal Damage
Deal a total amount of damage.

```yaml
- type: damage
  amount: 500
```

**Target:** Optional. If set, only counts damage to that entity type.

### `bowshoot` - Shoot Arrows
Shoot arrows with a bow.

```yaml
- type: bowshoot
  amount: 50
```

### `projectile` - Launch Projectiles
Launch any projectile.

```yaml
- type: projectile
  target: SNOWBALL
  amount: 30
```

**Target:** `SNOWBALL`, `EGG`, `ENDER_PEARL`, `ARROW`, or `ANY`.

### `death` - Die
Die a certain number of times. Yes, really.

```yaml
- type: death
  amount: 3
```

---

## Mining & Building Objectives

### `break` - Break Blocks
Break/mine blocks.

```yaml
- type: break
  target: DIAMOND_ORE
  amount: 10
```

**Target:** Any block material - `STONE`, `IRON_ORE`, `OAK_LOG`, `WHEAT`, etc. Use `ANY` for any block.

### `place` - Place Blocks
Place blocks in the world.

```yaml
- type: place
  target: COBBLESTONE
  amount: 100
```

### `interact` - Interact with Blocks
Right-click on specific blocks.

```yaml
- type: interact
  target: CHEST
  amount: 20
```

**Target:** `CHEST`, `FURNACE`, `CRAFTING_TABLE`, `LEVER`, `ANVIL`, etc.

---

## Collection & Crafting Objectives

### `collect` - Collect Items
Pick up items from the ground.

```yaml
- type: collect
  target: DIAMOND
  amount: 10
```

### `craft` - Craft Items
Craft items at a crafting table.

```yaml
- type: craft
  target: IRON_SWORD
  amount: 5
```

### `smelt` - Smelt Items
Smelt items in a furnace, blast furnace, or smoker.

```yaml
- type: smelt
  target: IRON_INGOT
  amount: 32
```

### `enchant` - Enchant Items
Enchant items at an enchanting table.

```yaml
- type: enchant
  target: ANY
  amount: 5
```

### `brew` - Brew Potions
Brew potions in a brewing stand.

```yaml
- type: brew
  target: ANY
  amount: 10
```

### `drop` - Drop Items
Drop items from your inventory.

```yaml
- type: drop
  target: COBBLESTONE
  amount: 64
```

---

## Fishing

### `fish` - Catch Fish
Catch items with a fishing rod.

```yaml
- type: fish
  target: COD
  amount: 15
```

**Target:** `COD`, `SALMON`, `TROPICAL_FISH`, `PUFFERFISH`, or `ANY` (catches anything, including treasure).

---

## Farming & Animals

### `breed` - Breed Animals
Breed two animals together.

```yaml
- type: breed
  target: COW
  amount: 10
```

**Target:** `COW`, `SHEEP`, `PIG`, `CHICKEN`, `HORSE`, `WOLF`, or `ANY`.

### `tame` - Tame Animals
Tame a wild animal.

```yaml
- type: tame
  target: WOLF
  amount: 3
```

**Target:** `WOLF`, `CAT`, `HORSE`, `PARROT`, or `ANY`.

### `shear` - Shear Entities
Shear sheep or mooshrooms.

```yaml
- type: shear
  target: SHEEP
  amount: 15
```

### `trade` - Trade with Villagers
Complete trades with villagers.

```yaml
- type: trade
  target: ANY
  amount: 10
```

---

## Survival Objectives

### `consume` - Eat or Drink
Consume food or potions.

```yaml
- type: consume
  target: GOLDEN_APPLE
  amount: 5
```

### `sleep` - Sleep in Beds
Sleep in a bed.

```yaml
- type: sleep
  amount: 5
```

### `heal` - Regenerate Health
Heal a total amount of HP.

```yaml
- type: heal
  amount: 100
```

---

## Movement Objectives

### `move` - Travel Distance
Walk, run, or sprint a distance in blocks.

```yaml
- type: move
  amount: 5000
```

### `jump` - Jump
Jump a number of times.

```yaml
- type: jump
  amount: 200
```

### `vehicle` - Ride Vehicles
Travel distance while riding a vehicle or entity.

```yaml
- type: vehicle
  amount: 1000
```

---

## Leveling Objectives

### `reachlevel` - Reach a Level
Reach a specific XP level.

```yaml
- type: reachlevel
  amount: 30
```

### `gainlevel` - Gain Levels
Gain a number of XP levels (doesn't matter what you start from).

```yaml
- type: gainlevel
  amount: 10
```

---

## Miscellaneous Objectives

### `chat` - Send Chat Messages
Send messages in chat.

```yaml
- type: chat
  amount: 50
```

### `firework` - Launch Fireworks
Launch firework rockets.

```yaml
- type: firework
  amount: 10
```

### `kill_mythicmob` - Kill MythicMobs
Kill custom MythicMobs mobs. **Requires MythicMobs plugin.**

```yaml
- type: kill_mythicmob
  target: SkeletonKing
  amount: 1
```

**Target:** The MythicMobs internal mob name (case-sensitive).

---

## Multi-Objective Quests

Add multiple objectives to a single quest:

```yaml
objectives:
  - type: break
    target: IRON_ORE
    amount: 30
  - type: smelt
    target: IRON_INGOT
    amount: 20
  - type: craft
    target: IRON_PICKAXE
    amount: 1
```

By default, all objectives can be worked on at the same time (parallel). Add `sequential: true` to the quest to force them in order:

```yaml
my_quest:
  sequential: true
  objectives:
    - type: break
      target: OAK_LOG
      amount: 20
    - type: craft
      target: CRAFTING_TABLE
      amount: 1
    - type: craft
      target: WOODEN_PICKAXE
      amount: 1
```

The player must finish breaking logs before crafting unlocks.

---

## Quick Reference Table

| Type | Target | Description |
|:-----|:-------|:------------|
| `kill` | Entity type | Kill mobs |
| `break` | Block type | Break blocks |
| `place` | Block type | Place blocks |
| `collect` | Item type | Pick up items |
| `craft` | Item type | Craft items |
| `smelt` | Item type | Smelt in furnace |
| `fish` | Fish/item type | Catch with fishing rod |
| `enchant` | Item type | Enchant items |
| `consume` | Item type | Eat or drink |
| `tame` | Entity type | Tame animals |
| `trade` | Item type | Trade with villagers |
| `brew` | Potion type | Brew potions |
| `shear` | Entity type | Shear sheep/mooshrooms |
| `breed` | Entity type | Breed animals |
| `interact` | Block type | Right-click blocks |
| `kill_mythicmob` | MythicMob ID | Kill MythicMobs mobs |
| `damage` | _(optional)_ | Deal damage |
| `death` | - | Die X times |
| `sleep` | - | Sleep in beds |
| `heal` | - | Regenerate HP |
| `drop` | Item type | Drop items |
| `move` | - | Travel blocks |
| `jump` | - | Jump X times |
| `chat` | - | Send messages |
| `bowshoot` | - | Shoot arrows |
| `projectile` | Projectile type | Launch projectiles |
| `firework` | - | Launch fireworks |
| `vehicle` | - | Ride vehicles |
| `reachlevel` | - | Reach XP level |
| `gainlevel` | - | Gain XP levels |

---

## Next Steps

- [Rewards](Rewards) - What players get when they finish
- [Conditions](Conditions) - Requirements before a quest can start
