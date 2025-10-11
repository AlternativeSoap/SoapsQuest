# 🎲 Random Quest Generator Guide

Complete guide to using and configuring the SoapsQuest random quest generation system.

> ⚠️ **IMPORTANT:** This document has been updated to reflect the **actual implementation** in the plugin. Previous versions of this documentation contained examples that don't match the plugin code.

---

## Table of Contents

1. [Overview](#overview)
2. [Using the Generator](#using-the-generator)
3. [Available Objectives](#available-objectives)
4. [Configuration Format](#configuration-format)
5. [Common Mistakes](#common-mistakes)
6. [Examples](#examples)
7. [Troubleshooting](#troubleshooting)

---

## Overview

The Random Quest Generator allows you to create procedurally generated quests with randomized objectives, rewards, and quest types. This is perfect for:

- **Testing** - Quickly generate quests for development
- **Dynamic Content** - Create unique quest variations
- **Quest Templates** - Generate base quests that can be distributed

### How It Works

1. Admin runs `/sq generate [type]` (e.g., `/sq generate single`)
2. Plugin selects random objective from configured pools in `random-generator.yml`
3. Generates appropriate amounts based on difficulty scaling
4. Selects rewards from tier-based reward pools
5. Saves the quest to `plugins/SoapsQuest/generated.yml`
6. Displays the generated quest ID in chat
7. Admin can then use `/sq give <player> <questId>` to distribute the quest

---

## Using the Generator

### Basic Command

```
/sq generate
```

Generates a random quest type (single, multi, or sequence).

**Example:**
```
/sq generate
```

### Specify Quest Type

```
/sq generate <type>
```

**Available Types:** `single`, `multi`, `sequence`

**Example:**
```
/sq generate single    # Single objective quest
/sq generate multi     # Multiple objectives (any order)
/sq generate sequence  # Multiple objectives (must complete in order)
```

### After Generation

The command will output the generated quest ID in chat. Example:
```
[SoapsQuest] Generated quest: quest_common_kill_12345
Use /sq give <player> quest_common_kill_12345
```

Use the `/sq give` command to distribute:
```
/sq give <player> quest_common_kill_12345
```

### Permissions

| Permission | Description |
|-----------|-------------|
| `soapsquest.generate` | Use the generate command (default: op) |
| `soapsquest.admin` | Full admin access including generation |

---

## Available Objectives

These are the **actual** objective types registered in the plugin (from `ObjectiveRegistry.java`):

### Combat Objectives (6 types)
- `kill` - Kill entities (supports ANY, HOSTILE, PASSIVE, or specific entity)
- `kill_mythicmob` - Kill MythicMobs (requires MythicMobs plugin)
- `damage` - Deal damage to entities
- `death` - Die a certain number of times
- `bowshoot` / `shoot_bow` - Shoot arrows with a bow
- `projectile` - Launch projectiles (snowballs, eggs, etc.)

### Building Objectives (3 types)
- `break` - Break blocks (supports ANY or specific block)
- `place` - Place blocks (supports ANY or specific block)
- `interact` - Interact with blocks (right-click)

### Collection Objectives (7 types)
- `collect` - Pick up items from ground
- `craft` - Craft items
- `smelt` - Smelt items in furnace
- `fish` - Catch fish
- `brew` - Brew potions
- `enchant` - Enchant items (**requires `item` field**)
- `drop` - Drop items

### Survival Objectives (6 types)
- `consume` - Eat or drink items
- `tame` - Tame animals
- `trade` - Trade with villagers (**requires `item` field**)
- `shear` - Shear animals
- `sleep` - Sleep in beds
- `heal` - Regenerate health

### Movement Objectives (3 types)
- `move` - Walk/run distance (in blocks)
- `jump` - Jump a number of times
- `vehicle` / `ride_vehicle` - Travel in vehicles

### Leveling Objectives (3 types)
- `level` - Gain experience levels
- `gainlevel` - Gain experience levels (same as level)
- `reachlevel` - Reach a specific level (**uses `level` field, not `amount`**)

### Miscellaneous Objectives (4 types)
- `chat` - Send chat messages
- `firework` / `launch_firework` - Launch fireworks
- `command` - Execute commands
- `placeholder` - PlaceholderAPI expressions

**Total: 33 objective types**

---

## Configuration Format

Configuration is stored in `plugins/SoapsQuest/random-generator.yml`

### Actual Structure (from the plugin)

```yaml
random-generator:
  enabled: true
  save-generated-quests: true
  allowed-types: [single, multi, sequence]
  
  # How objectives are defined (use 'objective', not 'type')
  objectives:
    objective_name:
      objective: kill    # The objective type
      entities: [ZOMBIE, SKELETON]
      amount: [10, 50]   # Or amount-by-difficulty
  
  # How rewards are defined
  reward-pool:
    xp:
      common: [25, 100]
      rare: [100, 250]
    money:
      common: [10, 100]
      rare: [100, 400]
```

---

## Common Mistakes

### ❌ Wrong Objective Type Names

**Don't use:**
- `break_block` → Use `break`
- `place_block` → Use `place`

**Example:**
```yaml
# ❌ WRONG
objectives:
  mine_stone:
    objective: break_block  # Will fail validation
    blocks: [STONE]
    amount: [50, 200]

# ✅ CORRECT
objectives:
  mine_stone:
    objective: break        # Correct type name
    blocks: [STONE]
    amount: [50, 200]
```

### ❌ Missing Required Fields

**Trade and Enchant need `item` field:**

```yaml
# ❌ WRONG
objectives:
  trade_quest:
    objective: trade
    amount: [5, 20]  # Missing 'item' field!

# ✅ CORRECT
objectives:
  trade_quest:
    objective: trade
    items: [ANY]     # Or specific item
    amount: [5, 20]
```

### ❌ Wrong Field Names

**The config uses specific field names:**

```yaml
# ❌ WRONG - using 'materials' for break objective
objectives:
  break_stone:
    objective: break
    materials: [STONE]  # Wrong field name!

# ✅ CORRECT - use 'blocks' for break/place
objectives:
  break_stone:
    objective: break
    blocks: [STONE]     # Correct field name
```

**Field name guide:**
- Break/Place: use `blocks:`
- Collect/Craft/Fish/Smelt/Enchant/Trade/Brew: use `items:`
- Kill/Tame/Shear: use `entities:`

---

## Objective Pools

### Available Objective Types in SoapsQuest

The following objective types are actually registered and available in the plugin:

#### Combat Objectives
- `kill` - Kill entities (ANY, HOSTILE, PASSIVE, or specific entity)
- `kill_mythicmob` - Kill MythicMobs (requires MythicMobs plugin)
- `damage` - Deal damage to entities
- `death` - Die a certain number of times
- `bowshoot` / `shoot_bow` - Shoot arrows with a bow
- `projectile` - Launch projectiles (snowballs, eggs, etc.)

#### Building Objectives
- `break` - Break blocks (ANY or specific block)
- `place` - Place blocks (ANY or specific block)
- `interact` - Interact with blocks (right-click)

#### Collection Objectives
- `collect` - Pick up items from ground (ANY or specific item)
- `craft` - Craft items (ANY or specific item)
- `smelt` - Smelt items in furnace
- `fish` - Catch fish (ANY or specific fish type)
- `brew` - Brew potions (ANY or specific potion)
- `enchant` - Enchant items at enchanting table (ANY or specific item)
- `drop` - Drop items (ANY or specific item)

#### Survival Objectives
- `consume` - Eat or drink items
- `tame` - Tame animals (ANY or specific entity)
- `trade` - Trade with villagers (ANY or specific item)
- `shear` - Shear animals (ANY or specific entity)
- `sleep` - Sleep in beds
- `heal` - Regenerate health (ANY or specific reason)

#### Movement Objectives
- `move` - Walk/run distance (in blocks)
- `jump` - Jump a number of times
- `vehicle` / `ride_vehicle` - Travel in vehicles (ANY or specific vehicle)

#### Leveling Objectives
- `level` - Gain experience levels
- `gainlevel` - Gain experience levels (same as level)
- `reachlevel` - Reach a specific level

#### Miscellaneous Objectives
- `chat` - Send chat messages
- `firework` / `launch_firework` - Launch fireworks
- `command` - Execute commands
- `placeholder` - PlaceholderAPI expressions (requires PlaceholderAPI)

---

### Example Configurations

#### Kill Objective

```yaml
objectives:
  kill_hostile:
    objective: kill
    entities: [ZOMBIE, SKELETON, CREEPER, SPIDER]
    amount-by-difficulty:
      easy: [10, 25]
      normal: [20, 40]
      hard: [40, 75]
      nightmare: [75, 150]
```

#### Break Objective (use 'break', not 'break_block')

```yaml
objectives:
  break_stone:
    objective: break
    blocks: [STONE, COBBLESTONE, ANDESITE, DIORITE]
    amount: [50, 200]
```

#### Place Objective (use 'place', not 'place_block')

```yaml
objectives:
  place_blocks:
    objective: place
    blocks: [OAK_PLANKS, STONE_BRICKS, COBBLESTONE]
    amount: [20, 100]
```

### Collection Objectives

#### Collect Items

```yaml
objectives:
  collect:
    enabled: true
    weight: 10
    
    materials:
      - DIAMOND
      - EMERALD
      - GOLD_INGOT
      - IRON_INGOT
      - WHEAT
      - CARROT
      - POTATO
    
    min-amount: 1
    max-amount: 64
    
    difficulty-multipliers:
      easy: 0.5
      normal: 1.0
      hard: 1.5
      nightmare: 2.0
    
    material-multipliers:
      DIAMOND: 0.3
      EMERALD: 0.2
      WHEAT: 2.0
```

#### Craft Items

```yaml
objectives:
  craft:
    enabled: true
    weight: 7
    
    materials:
      - STICK
      - CRAFTING_TABLE
      - CHEST
      - FURNACE
      - IRON_SWORD
      - IRON_PICKAXE
      - DIAMOND_SWORD
    
    min-amount: 1
    max-amount: 16
    
    difficulty-multipliers:
      easy: 0.5
      normal: 1.0
      hard: 1.5
      nightmare: 2.0
```

#### Fish

```yaml
objectives:
  fish:
    enabled: true
    weight: 5
    
    materials:
      - COD
      - SALMON
      - TROPICAL_FISH
      - PUFFERFISH
      - null  # Any fish
    
    min-amount: 5
    max-amount: 50
    
    difficulty-multipliers:
      easy: 0.5
      normal: 1.0
      hard: 1.5
      nightmare: 2.0
```

### Movement Objectives

#### Travel Distance

```yaml
objectives:
  move:
    enabled: true
    weight: 6
    
    min-amount: 100    # Blocks
    max-amount: 10000
    
    difficulty-multipliers:
      easy: 0.5
      normal: 1.0
      hard: 1.5
      nightmare: 2.0
```

#### Jump

```yaml
objectives:
  jump:
    enabled: true
    weight: 4
    
    min-amount: 50
    max-amount: 500
    
    difficulty-multipliers:
      easy: 0.5
      normal: 1.0
      hard: 1.5
      nightmare: 2.0
```

### Survival Objectives

#### Consume Items

```yaml
objectives:
  consume:
    enabled: true
    weight: 5
    
    materials:
      - BREAD
      - COOKED_BEEF
      - COOKED_PORKCHOP
      - GOLDEN_APPLE
      - POTION
    
    min-amount: 5
    max-amount: 32
    
    difficulty-multipliers:
      easy: 0.5
      normal: 1.0
      hard: 1.5
      nightmare: 2.0
```

---

## Reward Pools

### XP Rewards

```yaml
rewards:
  xp:
    enabled: true
    chance: 90  # 90% chance to give XP
    
    min-amount: 50
    max-amount: 1000
    
    # Tier multipliers
    tier-multipliers:
      common: 1.0
      rare: 1.5
      epic: 2.0
      legendary: 3.0
    
    # Difficulty multipliers
    difficulty-multipliers:
      easy: 0.5
      normal: 1.0
      hard: 1.5
      nightmare: 2.0
```

**Example Results:**
- Common Easy: 25-500 XP
- Legendary Nightmare: 150-6000 XP

### Money Rewards

Requires Vault plugin.

```yaml
rewards:
  money:
    enabled: true
    chance: 80  # 80% chance to give money
    
    min-amount: 10
    max-amount: 1000
    
    tier-multipliers:
      common: 1.0
      rare: 1.5
      epic: 2.5
      legendary: 5.0
    
    difficulty-multipliers:
      easy: 0.5
      normal: 1.0
      hard: 1.5
      nightmare: 2.0
```

### Item Rewards

```yaml
rewards:
  items:
    enabled: true
    max-items: 3  # Max different items per quest
    
    # Item pools by tier
    common:
      - material: IRON_SWORD
        min-amount: 1
        max-amount: 1
        chance: 50
        enchantments:
          - "SHARPNESS:1-2"  # Random level 1-2
      
      - material: IRON_INGOT
        min-amount: 5
        max-amount: 20
        chance: 100
    
    rare:
      - material: DIAMOND_SWORD
        min-amount: 1
        max-amount: 1
        chance: 70
        enchantments:
          - "SHARPNESS:2-4"
          - "UNBREAKING:1-2"
      
      - material: DIAMOND
        min-amount: 3
        max-amount: 10
        chance: 100
    
    epic:
      - material: DIAMOND_CHESTPLATE
        min-amount: 1
        max-amount: 1
        chance: 80
        name: "&5Epic Chestplate"
        enchantments:
          - "PROTECTION:3-4"
          - "UNBREAKING:2-3"
      
      - material: EMERALD
        min-amount: 5
        max-amount: 20
        chance: 100
    
    legendary:
      - material: NETHERITE_SWORD
        min-amount: 1
        max-amount: 1
        chance: 100
        name: "&6&lLegendary Blade"
        lore:
          - "&7Forged by the gods"
        enchantments:
          - "SHARPNESS:5"
          - "UNBREAKING:3"
          - "FIRE_ASPECT:2"
      
      - material: NETHER_STAR
        min-amount: 1
        max-amount: 3
        chance: 50
```

### Command Rewards

```yaml
rewards:
  commands:
    enabled: true
    chance: 20  # 20% chance for command rewards
    
    # Command pools by tier
    common:
      - "give {player} minecraft:golden_apple 1"
      - "effect give {player} minecraft:speed 60 1"
    
    rare:
      - "give {player} minecraft:golden_apple 3"
      - "give {player} minecraft:experience_bottle 10"
    
    epic:
      - "give {player} minecraft:enchanted_golden_apple 1"
      - "give {player} minecraft:elytra 1"
    
    legendary:
      - "give {player} minecraft:enchanted_golden_apple 5"
      - "give {player} minecraft:nether_star 1"
      - "give {player} minecraft:dragon_egg 1"
```

---

## Advanced Settings

### Quest Naming

Generate dynamic quest names:

```yaml
naming:
  enabled: true
  
  # Format: [prefix] [objective] [suffix]
  prefixes:
    common:
      - "Simple"
      - "Basic"
      - "Novice"
    rare:
      - "Skilled"
      - "Advanced"
      - "Expert"
    epic:
      - "Master"
      - "Elite"
      - "Supreme"
    legendary:
      - "Legendary"
      - "Mythical"
      - "Divine"
  
  # Objective-specific names
  objective-names:
    kill: "Slayer"
    break_block: "Miner"
    collect: "Collector"
    craft: "Crafter"
    fish: "Fisher"
  
  suffixes:
    - "Challenge"
    - "Quest"
    - "Trial"
    - "Mission"
    - "Task"
```

**Example Generated Names:**
- Common: "Simple Slayer Challenge"
- Rare: "Skilled Miner Quest"
- Epic: "Master Collector Trial"
- Legendary: "Divine Crafter Mission"

### Multi-Objective Generation

```yaml
multi-objective:
  enabled: true
  
  # Chance for multi-objective quests
  chance-by-tier:
    common: 10   # 10% chance
    rare: 25     # 25% chance
    epic: 50     # 50% chance
    legendary: 75  # 75% chance
  
  min-objectives: 2
  max-objectives: 5
  
  # Force sequential completion
  sequential-chance: 30  # 30% chance
```

### Conditions

Add random conditions to generated quests:

```yaml
conditions:
  enabled: true
  
  level-requirement:
    enabled: true
    chance: 50  # 50% chance
    
    min-level-by-tier:
      common: 0
      rare: 10
      epic: 25
      legendary: 50
  
  money-cost:
    enabled: true
    chance: 20  # 20% chance
    
    cost-by-tier:
      common: 100
      rare: 500
      epic: 2000
      legendary: 10000
  
  world-restriction:
    enabled: true
    chance: 15  # 15% chance
    
    worlds:
      - world
      - world_nether
      - world_the_end
```

---

## Important: Actual Configuration Format

The examples above were simplified for documentation. The **actual** `random-generator.yml` file uses a different format. Here's what's actually implemented:

### Real Configuration Structure

```yaml
random-generator:
  enabled: true
  save-generated-quests: true
  allowed-types: [single, multi, sequence]
  
  # Quest naming
  internal-name-formats:
    single: "quest_<tier>_<objective>_<id>"
    multi: "multi_<tier>_<id>"
    sequence: "seq_<tier>_<counter>"
  
  # Display templates (for quest names)
  display-templates:
    kill:
      - "&c<entity> Slayer"
      - "&4Hunt &f<amount> &4<entity>s"
    
    break:
      - "&8<block> Breaker"
      - "&7Mine &f<amount> &7<block>"
    
    collect:
      - "&e<item> Collector"
      - "&6Gather &f<amount> &6<item>s"
  
  # Objectives (actual format from random-generator.yml)
  objectives:
    # Combat objectives
    kill_hostile:
      objective: kill
      entities: [ZOMBIE, SKELETON, CREEPER, SPIDER]
      amount-by-difficulty:
        easy: [10, 25]
        normal: [20, 40]
        hard: [40, 75]
        nightmare: [75, 150]
    
    # Building objectives (use 'break', not 'break_block')
    break_stone:
      objective: break
      blocks: [STONE, COBBLESTONE, ANDESITE, DIORITE]
      amount: [50, 200]
    
    # Collection objectives
    collect_resources:
      objective: collect
      items: [WHEAT, CARROT, POTATO]
      amount: [10, 64]
    
    # Crafting objectives
    craft_tools:
      objective: craft
      items: [WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE]
      amount: [3, 10]
    
    # Fishing objectives
    catch_fish:
      objective: fish
      items: [COD, SALMON, TROPICAL_FISH, PUFFERFISH]
      amount: [10, 50]
    
    # Enchanting (MUST include item field, use ANY for any item)
    enchant_items:
      objective: enchant
      items: [ANY]
      amount: [5, 20]
    
    # Trading (MUST include item field, use ANY for any trade)
    trade_villagers:
      objective: trade
      items: [ANY]
      amount: [5, 20]
  
  # Reward pool (actual format)
  reward-pool:
    xp:
      common: [25, 100]
      rare: [100, 250]
      epic: [250, 400]
      legendary: [400, 500]
    
    money:
      common: [10, 100]
      rare: [100, 400]
      epic: [400, 750]
      legendary: [750, 1000]
```

### Generated Quest Examples

When you use `/sq generate single`, the plugin will create quests based on your `random-generator.yml` config. Here are examples of what gets generated:

#### Common Easy Quest
```yaml
quest_common_kill_12345:
  display: "&cZombie Slayer"
  tier: common
  difficulty: easy
  objectives:
    - type: kill
      entity: ZOMBIE
      amount: 15
  reward:
    xp: 75
    money: 50
```

#### Legendary Nightmare Quest
```yaml
quest_legendary_break_67890:
  display: "&8Diamond Miner"
  tier: legendary
  difficulty: nightmare
  objectives:
    - type: break
      block: DIAMOND_ORE
      amount: 40
  reward:
    xp: 4500
    money: 8000
    items:
      - material: DIAMOND_PICKAXE
        name: "&6Divine Pickaxe"
        enchantments:
          - "EFFICIENCY:5"
          - "FORTUNE:3"
          - "UNBREAKING:3"
        chance: 100
```

**Note:** Generated quests are saved to `plugins/SoapsQuest/generated.yml` by default.

---

## Best Practices

### Balance

1. **Weight Distribution**: Higher weights for common objectives
2. **Amount Ranges**: Keep ranges reasonable for gameplay
3. **Reward Scaling**: Match rewards to difficulty and time investment
4. **Material Multipliers**: Adjust for rarity

### Testing

1. Generate 10-20 quests of each tier/difficulty
2. Test completion times
3. Verify rewards feel appropriate
4. Check for impossible combinations

### Performance

1. Don't enable too many objective types (5-10 is optimal)
2. Keep material/entity lists reasonable (10-20 items)
3. Limit max-items to 3-5 per quest
4. Use appropriate weights to reduce calculation time

### Player Experience

1. Clear quest names that describe the objective
2. Balanced difficulty progression
3. Appropriate rewards for effort
4. Varied objectives to keep things interesting

---

## Key Differences: Documentation vs Implementation

### Objective Type Names
| Documentation Says | Plugin Actually Uses |
|-------------------|---------------------|
| `break_block` | `break` |
| `place_block` | `place` |
| `shoot_bow` | `bowshoot` (or `shoot_bow` as alias) |
| `launch_firework` | `firework` (or `launch_firework` as alias) |
| `ride_vehicle` | `vehicle` (or `ride_vehicle` as alias) |

### Required Fields
| Objective Type | Required Fields |
|---------------|----------------|
| `break`, `place` | `block`, `amount` |
| `kill`, `tame`, `shear` | `entity`, `amount` |
| `collect`, `craft`, `smelt`, `fish` | `item`, `amount` |
| `enchant`, `trade`, `brew` | `item`, `amount` (use `ANY` if not specific) |
| `damage`, `heal`, `drop`, `projectile` | `amount` (entity/item/reason optional) |
| `move`, `jump`, `bowshoot`, `firework`, `chat`, `interact` | `amount` |
| `reachlevel` | `level` |
| `gainlevel`, `level` | `amount` |
| `kill_mythicmob` | `mob`, `amount` |

### Config Field Names
| random-generator.yml uses | Not what you might expect |
|--------------------------|---------------------------|
| `objective:` | (not `type:`) |
| `blocks:` | (not `materials:` for break/place) |
| `items:` | (not `materials:` for collect/craft) |
| `entities:` | (for kill objectives) |

## Troubleshooting

### No Quests Generated

**Check:**
- `enabled: true` in random-generator.yml
- At least one objective configured in `objectives:` section
- Objective names match format: `objective_name:` not `type:`
- Use correct field names (`blocks` not `materials` for break/place)

### Quest Validation Errors

**Common Issues:**
```
Unknown objective type: 'break_block'
```
**Fix:** Use `break` instead of `break_block`

```
Missing required fields for 'trade' objective: item
```
**Fix:** Add `item: ANY` or specific item to trade objective

```
Missing required fields for 'enchant' objective: item
```
**Fix:** Add `item: ANY` or specific item to enchant objective

### Quests Not Working

**Solutions:**
1. Check console for validation warnings
2. Verify objective types match registered types (see ObjectiveRegistry)
3. Ensure required fields are present
4. Use uppercase for material/entity names (e.g., `DIAMOND_ORE` not `diamond_ore`)

---

## Need Help?

- **Discord**: [discord.gg/soapsuniverse](https://discord.gg/soapsuniverse)
- **Issues**: [GitHub Issues](https://github.com/AlternativeSoap/SoapsQuest/issues)
- **Wiki**: [Complete Documentation](WIKI.md)

---

[← Back to README](README.md) | [Configuration Guide →](CONFIGURATION.md) | [Quest Creation →](QUEST-CREATION.md)
