# 🎯 Quest Creation Guide

Learn how to create custom quests for SoapsQuest with detailed examples and explanations.

---

## Table of Contents

1. [Basic Quest Structure](#basic-quest-structure)
2. [Objective Types](#objective-types)
3. [Reward System](#reward-system)
4. [Conditions & Requirements](#conditions--requirements)
5. [Advanced Features](#advanced-features)
6. [Random Quest Generation](#random-quest-generation)
7. [Example Quests](#example-quests)

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
| `objectives` | List | List of objectives to complete |
| `reward` | Object | Rewards given on completion |

### Optional Fields

| Field | Type | Description |
|-------|------|-------------|
| `tier` | String | Quest rarity tier (affects random generation) |
| `difficulty` | String | Quest difficulty (scales objectives/rewards) |
| `description` | List | Multi-line description |
| `sequential` | Boolean | Complete objectives in order |
| `lock-to-player` | Boolean | Bind quest to first player who progresses |
| `milestones` | List | Progress percentages for notifications |
| `quest_paper` | Object | Customize physical quest item |

---

## Objective Types

### Combat Objectives

#### Kill Entities
```yaml
objectives:
  - type: kill
    entity: ZOMBIE  # ZOMBIE, SKELETON, CREEPER, ANY, HOSTILE, PASSIVE
    amount: 10
```

#### Kill MythicMobs (requires MythicMobs plugin)
```yaml
objectives:
  - type: kill_mythicmob
    mob: "SkeletonKing"  # Internal MythicMob name
    amount: 1
```

#### Deal Damage
```yaml
objectives:
  - type: damage
    entity: ZOMBIE  # Optional filter
    amount: 100  # Half-hearts
```

#### Death Count
```yaml
objectives:
  - type: death
    amount: 1
```

#### Ranged Combat
```yaml
objectives:
  - type: bowshoot
    amount: 50
  - type: projectile
    projectile: SNOWBALL  # Optional: SNOWBALL, EGG, ENDER_PEARL
    amount: 25
```

### Building Objectives

#### Break Blocks
```yaml
objectives:
  - type: break
    material: STONE  # Single material
    amount: 100
```
```yaml
objectives:
  - type: break
    material:  # Multiple materials
      - STONE
      - COBBLESTONE
    amount: 50
```

#### Place Blocks
```yaml
objectives:
  - type: place
    material: COBBLESTONE
    amount: 200
```

#### Interact with Blocks
```yaml
objectives:
  - type: interact
    material: CHEST
    amount: 5
```

### Collection Objectives

#### Collect Items
```yaml
objectives:
  - type: collect
    material: DIAMOND
    amount: 5
```

#### Craft Items
```yaml
objectives:
  - type: craft
    material: IRON_SWORD
    amount: 1
```

#### Smelt Items
```yaml
objectives:
  - type: smelt
    material: IRON_INGOT
    amount: 10
```

#### Fish
```yaml
objectives:
  - type: fish
    material: COD  # Optional: COD, SALMON, etc.
    amount: 20
```

#### Brew Potions
```yaml
objectives:
  - type: brew
    potion: STRENGTH  # Optional potion type
    amount: 3
```

#### Enchant Items
```yaml
objectives:
  - type: enchant
    amount: 5
```

#### Drop Items
```yaml
objectives:
  - type: drop
    material: DIRT
    amount: 64
```

### Survival Objectives

#### Consume Items
```yaml
objectives:
  - type: consume
    material: BREAD
    amount: 10
```

#### Tame Animals
```yaml
objectives:
  - type: tame
    entity: WOLF
    amount: 2
```

#### Trade with Villagers
```yaml
objectives:
  - type: trade
    amount: 5
```

#### Shear Sheep
```yaml
objectives:
  - type: shear
    amount: 10
```

#### Sleep in Bed
```yaml
objectives:
  - type: sleep
    amount: 3
```

#### Heal Health
```yaml
objectives:
  - type: heal
    amount: 20  # Half-hearts
```

### Movement Objectives

#### Walk/Run Distance
```yaml
objectives:
  - type: move
    distance: 1000  # Blocks
```

#### Jump
```yaml
objectives:
  - type: jump
    amount: 100
```

#### Use Vehicles
```yaml
objectives:
  - type: vehicle
    vehicle: BOAT  # Optional: BOAT, MINECART, HORSE
    distance: 500
```

### Leveling Objectives

#### Reach Level
```yaml
objectives:
  - type: reachlevel
    level: 30
```

#### Gain Levels
```yaml
objectives:
  - type: gainlevel
    amount: 5
```

### Miscellaneous Objectives

#### Send Chat Messages
```yaml
objectives:
  - type: chat
    message: "Hello World"  # Optional specific message
    amount: 5
```

#### Launch Fireworks
```yaml
objectives:
  - type: firework
    amount: 10
```

#### Execute Commands
```yaml
objectives:
  - type: command
    command: "spawn"  # Without /
    amount: 1
```

#### PlaceholderAPI (requires PlaceholderAPI)
```yaml
objectives:
  - type: placeholder
    placeholder: "%player_level%"
    value: "50"  # Target value
```

---

## Reward System

### XP Rewards
```yaml
reward:
  xp: 100
```

### Money Rewards (requires Vault)
```yaml
reward:
  money: 500
```

### Item Rewards

#### Basic Items
```yaml
reward:
  items:
    - material: DIAMOND
      amount: 5
```

#### Advanced Items
```yaml
reward:
  items:
    - material: DIAMOND_SWORD
      amount: 1
      name: "&6Legendary Blade"
      lore:
        - "&7Forged by heroes"
        - "&7Unbreakable weapon"
      enchantments:
        - "SHARPNESS:5"
        - "UNBREAKING:3"
      flags:  # Item flags
        - "HIDE_ENCHANTS"
      unbreakable: true  # Makes item unbreakable
      chance: 100  # Drop chance 0-100%
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
```

### Command Rewards
```yaml
reward:
  commands:
    - "give {player} minecraft:elytra 1"
    - "tp {player} 0 100 0"
    - "broadcast {player} completed quest!"
```

**Placeholders:** `{player}`, `{quest_id}`, `{quest_name}`

---

## Conditions & Requirements

### Progress Conditions (checked during gameplay)

#### Level Requirements
```yaml
conditions:
  min-level: 10
  max-level: 30
```

#### Money Requirements (requires Vault)
```yaml
conditions:
  min-money: 1000
```

#### World Restrictions
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

#### PlaceholderAPI Conditions (requires PlaceholderAPI)
```yaml
conditions:
  placeholder:
    placeholder: "%player_level%"
    value: ">=50"  # Operators: ==, !=, >, <, >=, <=
```

### Locking Conditions (consume resources to unlock)

#### Money Cost (requires Vault)
```yaml
conditions:
  cost: 5000
```

#### Item Cost
```yaml
conditions:
  item: "DIAMOND:10,EMERALD:5"  # MATERIAL:AMOUNT
  consume-item: true  # Remove items when unlocking
```

### Active Quest Limits
```yaml
conditions:
  active-limit: 1  # Max concurrent quests with this ID
```

---

## Advanced Features

### Sequential Objectives
Complete objectives in order (one at a time):
```yaml
sequential_quest:
  display: "&aApprentice Training"
  sequential: true
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
```

### Multi-Objective Quests
Complete multiple objectives (any order):
```yaml
multi_quest:
  display: "&eGatherer"
  objectives:
    - type: break
      material: STONE
      amount: 100
    - type: collect
      material: WHEAT
      amount: 32
```

### Lock-to-Player System
Bind quest to first player who makes progress:
```yaml
personal_quest:
  display: "&cPersonal Challenge"
  lock-to-player: true  # Quest becomes non-tradeable
  objectives:
    - type: kill
      entity: ENDER_DRAGON
      amount: 1
```

### Custom Quest Papers
Override default PAPER material and appearance:
```yaml
special_quest:
  display: "&5Legendary Quest"
  quest_paper:
    material: ENCHANTED_BOOK
    name: "&6✦ {quest_name} ✦"
    lore:
      - "&7Tier: {tier}"
      - "&7Difficulty: {difficulty}"
      - "&eRight-click to complete!"
    enchantments:
      - "DURABILITY:1"
    glowing: true  # Makes item glow
    hide-enchants: true
```

### Milestone Notifications
Show progress messages at specific percentages:
```yaml
milestone_quest:
  display: "&aProgress Quest"
  milestones: [25, 50, 75]  # Notify at 25%, 50%, 75% complete
  objectives:
    - type: break
      material: STONE
      amount: 1000
```

### Difficulty Scaling
Quests automatically scale based on difficulty (configured in config.yml):
- **Easy**: 80% objective amount, 80% rewards
- **Normal**: 100% objective amount, 100% rewards  
- **Hard**: 150% objective amount, 150% rewards
- **Nightmare**: 200% objective amount, 250% rewards

---

## Random Quest Generation

Generate quests automatically with `/sq generate [type]`:

### Generation Types
- **single**: One objective
- **multi**: 2-4 objectives (any order)
- **sequence**: 2-4 objectives (in order)

### Configuration (random-generator.yml)

#### Objective Weights
Control how often objectives appear:
```yaml
objective-weights:
  kill: 40      # Most common
  break: 30
  collect: 15
  craft: 15
  move: 10      # Less common
  firework: 3   # Rare
```

#### Difficulty Scaling
Different amounts per difficulty:
```yaml
objectives:
  kill_zombies:
    objective: kill
    entities: [ZOMBIE]
    amount-by-difficulty:
      easy: [10, 25]
      normal: [20, 40]
      hard: [40, 75]
      nightmare: [75, 150]
```

#### Reward Pools
Weighted random rewards:
```yaml
reward-pool:
  items:
    selection-mode: "weighted"
    pool:
      - material: IRON_INGOT
        amount: [1, 5]
        tiers: [common, rare]
        weight: 50
      - material: DIAMOND
        amount: [1, 2]
        tiers: [epic, legendary]
        weight: 15
```

#### Conditions
Add requirements automatically:
```yaml
conditions:
  min-level:
    enabled: true
    chance: 40  # 40% of quests get level requirements
    by-tier:
      common: 0
      rare: 10
      epic: 25
      legendary: 50
```

### Generated Quest Saving
```yaml
save-generated-quests: true
save-location: "generated.yml"  # Saved to plugins/SoapsQuest/generated.yml
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
