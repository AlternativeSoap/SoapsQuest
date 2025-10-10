# 🎯 Quest Creation Guide

Learn how to create custom quests for SoapsQuest with detailed examples and explanations.

---

## Table of Contents

1. [Basic Quest Structure](#basic-quest-structure)
2. [Objective Types](#objective-types)
3. [Reward System](#reward-system)
4. [Conditions & Requirements](#conditions--requirements)
5. [Advanced Features](#advanced-features)
6. [Example Quests](#example-quests)

---

## Basic Quest Structure

Every quest in `quests.yml` follows this structure:

```yaml
quest_id:
  display: "&aQuest Display Name"
  tier: common
  difficulty: easy
  objectives:
    - type: objective_type
      # objective-specific settings
  reward:
    # reward types
```

### Required Fields

| Field | Type | Description |
|-------|------|-------------|
| `display` | String | Quest name shown to players (supports color codes) |
| `tier` | String | Quest rarity tier (from config.yml) |
| `difficulty` | String | Quest difficulty (from config.yml) |
| `objectives` | List | List of objectives to complete |
| `reward` | Object | Rewards given on completion |

### Optional Fields

| Field | Type | Description |
|-------|------|-------------|
| `description` | List | Multi-line description |
| `sequential` | Boolean | Force objectives to be completed in order |
| `conditions` | Object | Requirements to accept/progress quest |
| `quest_paper` | Object | Customize the physical quest item |

---

## Objective Types

### Combat Objectives

#### Kill Entities

Kill specific entities or entity types:

```yaml
objectives:
  - type: kill
    entity: ZOMBIE  # Specific entity
    amount: 10
```

**Entity Options:**
- Specific: `ZOMBIE`, `SKELETON`, `CREEPER`, `COW`, etc.
- Any: `ANY` - Any entity
- Category: `HOSTILE` - Hostile mobs only
- Category: `PASSIVE` - Passive mobs only

**Example - Kill Any Hostile:**
```yaml
objectives:
  - type: kill
    entity: HOSTILE
    amount: 20
```

#### Kill MythicMobs

Requires MythicMobs plugin:

```yaml
objectives:
  - type: kill_mythicmob
    mob: "SkeletonKing"  # Internal MythicMob name
    amount: 1
```

#### Deal Damage

Deal a specific amount of damage:

```yaml
objectives:
  - type: damage
    entity: ZOMBIE  # Optional: specific entity
    amount: 100  # Damage in half-hearts
```

#### Death Objective

Die a certain number of times (for hardcore challenges):

```yaml
objectives:
  - type: death
    amount: 1
```

#### Ranged Combat

Shoot arrows or projectiles:

```yaml
objectives:
  - type: bowshoot
    amount: 50
```

```yaml
objectives:
  - type: projectile
    projectile: SNOWBALL  # Optional: specific projectile
    amount: 25
```

---

### Building Objectives

#### Break Blocks

Break specific blocks:

```yaml
objectives:
  - type: break_block
    material: STONE
    amount: 100
```

**Material Lists:**
```yaml
objectives:
  - type: break_block
    material:
      - OAK_LOG
      - BIRCH_LOG
      - SPRUCE_LOG
    amount: 50
```

#### Place Blocks

Place specific blocks:

```yaml
objectives:
  - type: place_block
    material: COBBLESTONE
    amount: 200
```

#### Interact with Blocks

Right-click specific blocks:

```yaml
objectives:
  - type: interact
    material: CHEST
    amount: 5
```

---

### Collection Objectives

#### Collect Items

Pick up items from the ground:

```yaml
objectives:
  - type: collect
    material: DIAMOND
    amount: 5
```

#### Craft Items

Craft specific items:

```yaml
objectives:
  - type: craft
    material: IRON_SWORD
    amount: 1
```

#### Smelt Items

Smelt items in a furnace:

```yaml
objectives:
  - type: smelt
    material: IRON_INGOT
    amount: 10
```

#### Fish

Catch fish or treasures:

```yaml
objectives:
  - type: fish
    material: COD  # Optional: specific catch
    amount: 20
```

#### Brew Potions

Brew potions:

```yaml
objectives:
  - type: brew
    potion: STRENGTH  # Optional: specific potion type
    amount: 3
```

#### Enchant Items

Enchant items at an enchanting table:

```yaml
objectives:
  - type: enchant
    amount: 5
```

#### Drop Items

Drop specific items:

```yaml
objectives:
  - type: drop
    material: DIRT
    amount: 64
```

---

### Survival Objectives

#### Consume Items

Eat or drink items:

```yaml
objectives:
  - type: consume
    material: BREAD
    amount: 10
```

#### Tame Animals

Tame tameable mobs:

```yaml
objectives:
  - type: tame
    entity: WOLF
    amount: 2
```

#### Trade with Villagers

Trade with villagers:

```yaml
objectives:
  - type: trade
    amount: 5
```

#### Shear Sheep

Shear sheep:

```yaml
objectives:
  - type: shear
    amount: 10
```

#### Sleep in Bed

Sleep in a bed:

```yaml
objectives:
  - type: sleep
    amount: 3
```

#### Heal Health

Regenerate health:

```yaml
objectives:
  - type: heal
    amount: 20  # Half-hearts
```

---

### Movement Objectives

#### Walk/Run Distance

Travel a specific distance:

```yaml
objectives:
  - type: move
    distance: 1000  # Blocks
```

#### Jump

Jump a number of times:

```yaml
objectives:
  - type: jump
    amount: 100
```

#### Use Vehicles

Travel in boats, minecarts, or horses:

```yaml
objectives:
  - type: vehicle
    vehicle: BOAT  # Optional: specific vehicle
    distance: 500  # Blocks
```

---

### Leveling Objectives

#### Reach Level

Reach a specific XP level:

```yaml
objectives:
  - type: reachlevel
    level: 30
```

#### Gain Levels

Gain a number of levels:

```yaml
objectives:
  - type: gainlevel
    amount: 5
```

#### Generic Level

Alternative level objective:

```yaml
objectives:
  - type: level
    amount: 10
```

---

### Miscellaneous Objectives

#### Send Chat Messages

Send messages in chat:

```yaml
objectives:
  - type: chat
    message: "Hello World"  # Optional: specific message
    amount: 5
```

#### Launch Fireworks

Launch fireworks:

```yaml
objectives:
  - type: firework
    amount: 10
```

#### Execute Commands

Run specific commands:

```yaml
objectives:
  - type: command
    command: "spawn"  # Without /
    amount: 1
```

#### PlaceholderAPI

Use PAPI placeholders as objectives:

```yaml
objectives:
  - type: placeholder
    placeholder: "%player_level%"
    value: "50"  # Target value
```

---

## Reward System

### XP Rewards

Give experience points:

```yaml
reward:
  xp: 100
```

### Money Rewards

Give economy money (requires Vault):

```yaml
reward:
  money: 500
```

### Item Rewards

#### Simple Items

```yaml
reward:
  items:
    - material: DIAMOND
      amount: 5
```

#### Custom Items

```yaml
reward:
  items:
    - material: DIAMOND_SWORD
      amount: 1
      name: "&6Legendary Blade"
      lore:
        - "&7A powerful weapon"
        - "&7Forged by heroes"
      enchantments:
        - "SHARPNESS:5"
        - "UNBREAKING:3"
```

#### Chance-Based Rewards

```yaml
reward:
  items:
    - material: DIAMOND
      amount: 3
      chance: 100  # Always drops
      
    - material: NETHERITE_INGOT
      amount: 1
      chance: 50  # 50% chance
      
    - material: DRAGON_EGG
      amount: 1
      chance: 1  # 1% chance (rare)
```

### Command Rewards

Execute console commands:

```yaml
reward:
  commands:
    - "give {player} minecraft:elytra 1"
    - "tp {player} 0 100 0"
```

**Available Placeholders:**
- `{player}` - Player name
- `{quest_id}` - Quest identifier
- `{quest_name}` - Quest display name

### Combined Rewards

```yaml
reward:
  xp: 500
  money: 1000
  items:
    - material: DIAMOND_SWORD
      name: "&aReward Sword"
      chance: 100
  commands:
    - "give {player} minecraft:golden_apple 5"
```

---

## Conditions & Requirements

### Progress Conditions

These are checked while progressing objectives:

#### Level Requirements

```yaml
conditions:
  min-level: 10  # Minimum level
  max-level: 30  # Maximum level
```

#### Money Requirements

Requires Vault:

```yaml
conditions:
  min-money: 1000  # Minimum balance
```

#### World Restrictions

Only progress in specific worlds:

```yaml
conditions:
  world:
    - world
    - world_nether
```

#### Gamemode Restrictions

```yaml
conditions:
  gamemode:
    - SURVIVAL
    - ADVENTURE
```

#### Time Restrictions

```yaml
conditions:
  time: DAY  # DAY or NIGHT
```

#### Permission Requirements

```yaml
conditions:
  permission: "quests.vip"
```

#### PlaceholderAPI Conditions

Requires PlaceholderAPI:

```yaml
conditions:
  placeholder:
    placeholder: "%player_level%"
    value: ">=50"  # Operators: ==, !=, >, <, >=, <=
```

### Locking Conditions

These lock the quest until paid/unlocked:

#### Money Cost

Pay to unlock quest:

```yaml
conditions:
  cost: 5000  # Money required
```

#### Item Cost

Consume items to unlock:

```yaml
conditions:
  item:
    - material: DIAMOND
      amount: 10
  consume-item: true  # Remove items when unlocking
```

### Active Quest Limits

Limit concurrent quests with same ID:

```yaml
conditions:
  active-limit: 1  # Only 1 quest with this ID can be active
```

---

## Advanced Features

### Sequential Objectives

Force objectives to be completed in order:

```yaml
my_quest:
  display: "&aOrdered Quest"
  tier: rare
  difficulty: normal
  sequential: true  # Enable sequential mode
  objectives:
    - type: collect
      material: OAK_LOG
      amount: 10
    - type: craft
      material: CRAFTING_TABLE
      amount: 1
    - type: craft
      material: WOODEN_PICKAXE
      amount: 1
  reward:
    xp: 100
```

### Multi-Objective Quests

Complete multiple objectives (any order):

```yaml
gathering_quest:
  display: "&eGatherer"
  tier: common
  difficulty: easy
  objectives:
    - type: break_block
      material: STONE
      amount: 100
    - type: break_block
      material: OAK_LOG
      amount: 50
    - type: collect
      material: WHEAT
      amount: 32
  reward:
    money: 500
```

### Custom Quest Papers

Customize the physical quest item:

```yaml
my_quest:
  display: "&cSpecial Quest"
  tier: legendary
  difficulty: hard
  quest_paper:
    material: ENCHANTED_BOOK
    name: "&6&l✦ {quest_name} ✦"
    lore:
      - "&7Tier: {tier}"
      - "&7Difficulty: {difficulty}"
      - ""
      - "&e&lOBJECTIVES:"
      - "&7- Complete all tasks"
      - ""
      - "&aRight-click to complete!"
    enchantments:
      - "DURABILITY:1"
    hide-enchants: true
    glowing: true
  objectives:
    - type: kill
      entity: ENDER_DRAGON
      amount: 1
  reward:
    xp: 10000
```

---

## Example Quests

### Beginner Quest

```yaml
starter_quest:
  display: "&aWelcome to the Server"
  tier: common
  difficulty: easy
  description:
    - "&7Complete simple tasks to get started"
  objectives:
    - type: break_block
      material: OAK_LOG
      amount: 10
    - type: craft
      material: CRAFTING_TABLE
      amount: 1
  reward:
    xp: 50
    money: 100
    items:
      - material: STONE_SWORD
        name: "&aStarter Sword"
        chance: 100
```

### Combat Quest

```yaml
zombie_slayer:
  display: "&cZombie Slayer"
  tier: rare
  difficulty: normal
  description:
    - "&7Eliminate zombies threatening the village"
  objectives:
    - type: kill
      entity: ZOMBIE
      amount: 50
  reward:
    xp: 500
    money: 750
    items:
      - material: DIAMOND_SWORD
        name: "&cZombie Slayer"
        enchantments:
          - "SHARPNESS:3"
          - "SMITE:5"
        chance: 100
  conditions:
    min-level: 15
    world:
      - world
```

### Gathering Quest

```yaml
master_miner:
  display: "&7Master Miner"
  tier: epic
  difficulty: hard
  objectives:
    - type: break_block
      material: STONE
      amount: 1000
    - type: break_block
      material: IRON_ORE
      amount: 100
    - type: break_block
      material: DIAMOND_ORE
      amount: 20
  reward:
    xp: 2000
    money: 5000
    items:
      - material: DIAMOND_PICKAXE
        name: "&bMaster's Pickaxe"
        enchantments:
          - "EFFICIENCY:5"
          - "FORTUNE:3"
          - "UNBREAKING:3"
        chance: 100
  conditions:
    min-level: 25
    gamemode:
      - SURVIVAL
```

### Boss Quest

```yaml
dragon_slayer:
  display: "&5&lDragon Slayer"
  tier: legendary
  difficulty: nightmare
  description:
    - "&7Defeat the Ender Dragon"
    - "&cOnly the bravest dare attempt this"
  quest_paper:
    material: DRAGON_HEAD
    name: "&5&l✦ DRAGON SLAYER ✦"
    lore:
      - "&7The ultimate challenge"
      - ""
      - "&aRight-click to complete"
    glowing: true
  objectives:
    - type: kill
      entity: ENDER_DRAGON
      amount: 1
  reward:
    xp: 10000
    money: 50000
    items:
      - material: ELYTRA
        name: "&5Dragon Wings"
        chance: 100
      - material: DRAGON_EGG
        chance: 50
    commands:
      - "give {player} minecraft:nether_star 5"
  conditions:
    min-level: 50
    cost: 10000
```

### Daily Quest (with Active Limit)

```yaml
daily_fishing:
  display: "&bDaily Fishing"
  tier: common
  difficulty: easy
  description:
    - "&7Catch fish for daily rewards"
    - "&7(Once per day)"
  objectives:
    - type: fish
      amount: 20
  reward:
    xp: 200
    money: 300
  conditions:
    active-limit: 1  # Only 1 active at a time
```

### Sequential Crafting Quest

```yaml
blacksmith_apprentice:
  display: "&7Blacksmith Apprentice"
  tier: rare
  difficulty: normal
  sequential: true  # Must complete in order
  objectives:
    - type: collect
      material: IRON_ORE
      amount: 20
    - type: smelt
      material: IRON_INGOT
      amount: 20
    - type: craft
      material: IRON_SWORD
      amount: 1
    - type: craft
      material: IRON_CHESTPLATE
      amount: 1
  reward:
    xp: 800
    money: 1500
    items:
      - material: ANVIL
        amount: 1
        chance: 100
```

---

## Quest Testing

### Testing Checklist

1. **Syntax Validation**
   - Verify YAML syntax is correct
   - Check indentation (2 spaces, no tabs)
   - Validate material/entity names

2. **In-Game Testing**
   ```
   /sq reload
   /sq give <player> <quest_id>
   ```

3. **Progress Tracking**
   - Complete objectives and verify progress
   - Check BossBar/ActionBar updates
   - Test milestone notifications

4. **Reward Distribution**
   - Verify all rewards are given
   - Test chance-based rewards multiple times
   - Check command execution

5. **Condition Testing**
   - Test with/without requirements met
   - Verify locked quests unlock properly
   - Test world/gamemode restrictions

---

## Common Mistakes

### ❌ Wrong Material Names

```yaml
objectives:
  - type: collect
    material: diamond_ore  # ❌ Wrong (lowercase)
```

✅ **Correct:**
```yaml
objectives:
  - type: collect
    material: DIAMOND_ORE  # ✅ Uppercase
```

### ❌ Missing Amount

```yaml
objectives:
  - type: kill
    entity: ZOMBIE  # ❌ No amount specified
```

✅ **Correct:**
```yaml
objectives:
  - type: kill
    entity: ZOMBIE
    amount: 10  # ✅ Amount specified
```

### ❌ Invalid Indentation

```yaml
reward:
xp: 100  # ❌ Not indented
```

✅ **Correct:**
```yaml
reward:
  xp: 100  # ✅ Properly indented
```

---

## Need Help?

- **Discord**: [discord.gg/soapsuniverse](https://discord.gg/soapsuniverse)
- **Issues**: [GitHub Issues](https://github.com/AlternativeSoap/SoapsQuest/issues)
- **Material List**: [Spigot Material Enum](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html)
- **Entity List**: [Spigot EntityType Enum](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html)

---

[← Back to README](README.md) | [Configuration Guide →](CONFIGURATION.md)
