# 🎲 Random Quest Generator Guide

Complete guide to using and configuring the SoapsQuest random quest generation system.

---

## Table of Contents

1. [Overview](#overview)
2. [Using the Generator](#using-the-generator)
3. [Configuration](#configuration)
4. [Objective Pools](#objective-pools)
5. [Reward Pools](#reward-pools)
6. [Advanced Settings](#advanced-settings)
7. [Examples](#examples)

---

## Overview

The Random Quest Generator allows you to create procedurally generated quests with randomized objectives, rewards, tiers, and difficulties. This is perfect for:

- **Daily/Weekly Quests** - Generate fresh quests automatically
- **Player-Specific Challenges** - Create unique quests per player
- **Testing** - Quickly generate quests for development
- **Dynamic Content** - Keep your server fresh with new quests

### How It Works

1. Admin runs `/sq generate <player> [tier] [difficulty]`
2. Plugin selects random objective type from configured pools
3. Generates appropriate amounts based on min/max ranges
4. Selects rewards from reward pools
5. Creates and gives quest paper to player

---

## Using the Generator

### Basic Command

```
/sq generate <player>
```

Generates a quest with random tier and difficulty.

**Example:**
```
/sq generate Steve
```

### Specify Tier

```
/sq generate <player> <tier>
```

**Available Tiers:** `common`, `rare`, `epic`, `legendary`

**Example:**
```
/sq generate Steve legendary
```

### Specify Tier and Difficulty

```
/sq generate <player> <tier> <difficulty>
```

**Available Difficulties:** `easy`, `normal`, `hard`, `nightmare`

**Example:**
```
/sq generate Steve epic hard
```

### Permissions

| Permission | Description |
|-----------|-------------|
| `soapsquest.generate` | Use the generate command (default: op) |
| `soapsquest.admin` | Full admin access including generation |

---

## Configuration

Configuration is stored in `plugins/SoapsQuest/random-generator.yml`

### Basic Structure

```yaml
generator:
  enabled: true
  
  # Naming
  naming:
    prefixes:
      - "Legendary"
      - "Epic"
      - "Heroic"
    suffixes:
      - "Challenge"
      - "Quest"
      - "Trial"
  
  # Objective pools
  objectives:
    # ... objective configurations
  
  # Reward pools
  rewards:
    # ... reward configurations
```

---

## Objective Pools

### Combat Objectives

#### Kill Entities

```yaml
objectives:
  kill:
    enabled: true
    weight: 10  # Higher = more likely to be selected
    
    entities:
      - ZOMBIE
      - SKELETON
      - CREEPER
      - SPIDER
      - ENDERMAN
      - BLAZE
      - WITHER_SKELETON
    
    # Amount ranges
    min-amount: 5
    max-amount: 50
    
    # Scaling by difficulty
    difficulty-multipliers:
      easy: 0.5    # 50% of base amount
      normal: 1.0  # 100% (no change)
      hard: 1.5    # 150% of base amount
      nightmare: 2.0  # 200% of base amount
```

**How it works:**
- Random entity selected from list
- Random amount between min/max
- Multiplied by difficulty multiplier

**Example Results:**
- Easy: Kill 3-25 Zombies
- Normal: Kill 5-50 Zombies
- Hard: Kill 8-75 Zombies
- Nightmare: Kill 10-100 Zombies

#### Damage Dealt

```yaml
objectives:
  damage:
    enabled: true
    weight: 5
    
    entities:
      - ANY
      - ZOMBIE
      - SKELETON
      - ENDER_DRAGON
    
    min-amount: 50  # Half-hearts
    max-amount: 500
    
    difficulty-multipliers:
      easy: 0.5
      normal: 1.0
      hard: 1.5
      nightmare: 2.0
```

### Building Objectives

#### Break Blocks

```yaml
objectives:
  break_block:
    enabled: true
    weight: 8
    
    materials:
      - STONE
      - COBBLESTONE
      - DIRT
      - OAK_LOG
      - BIRCH_LOG
      - SPRUCE_LOG
      - IRON_ORE
      - COAL_ORE
      - DIAMOND_ORE
    
    min-amount: 10
    max-amount: 200
    
    difficulty-multipliers:
      easy: 0.5
      normal: 1.0
      hard: 1.5
      nightmare: 2.0
    
    # Material-specific multipliers
    material-multipliers:
      DIAMOND_ORE: 0.1   # Very rare, reduce amount
      IRON_ORE: 0.5      # Rare, reduce amount
      STONE: 2.0         # Common, increase amount
```

#### Place Blocks

```yaml
objectives:
  place_block:
    enabled: true
    weight: 6
    
    materials:
      - COBBLESTONE
      - STONE_BRICKS
      - OAK_PLANKS
      - GLASS
      - WOOL
    
    min-amount: 20
    max-amount: 500
    
    difficulty-multipliers:
      easy: 0.5
      normal: 1.0
      hard: 1.5
      nightmare: 2.0
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

## Examples

### Example Configuration

```yaml
generator:
  enabled: true
  
  naming:
    enabled: true
    prefixes:
      common: ["Simple", "Basic"]
      rare: ["Skilled", "Advanced"]
      epic: ["Master", "Elite"]
      legendary: ["Legendary", "Divine"]
    
    objective-names:
      kill: "Slayer"
      break_block: "Miner"
      collect: "Collector"
    
    suffixes: ["Challenge", "Quest"]
  
  objectives:
    kill:
      enabled: true
      weight: 10
      entities: [ZOMBIE, SKELETON, CREEPER]
      min-amount: 5
      max-amount: 50
      difficulty-multipliers:
        easy: 0.5
        normal: 1.0
        hard: 1.5
        nightmare: 2.0
    
    break_block:
      enabled: true
      weight: 8
      materials: [STONE, OAK_LOG, IRON_ORE]
      min-amount: 10
      max-amount: 200
      difficulty-multipliers:
        easy: 0.5
        normal: 1.0
        hard: 1.5
        nightmare: 2.0
  
  rewards:
    xp:
      enabled: true
      chance: 90
      min-amount: 50
      max-amount: 1000
      tier-multipliers:
        common: 1.0
        rare: 1.5
        epic: 2.0
        legendary: 3.0
    
    money:
      enabled: true
      chance: 80
      min-amount: 10
      max-amount: 1000
      tier-multipliers:
        common: 1.0
        rare: 1.5
        epic: 2.5
        legendary: 5.0
```

### Generated Quest Examples

#### Common Easy Quest
```yaml
simple_slayer_challenge:
  display: "&7Simple Slayer Challenge"
  tier: common
  difficulty: easy
  objectives:
    - type: kill
      entity: ZOMBIE
      amount: 3
  reward:
    xp: 75
    money: 25
```

#### Legendary Nightmare Quest
```yaml
divine_miner_quest:
  display: "&6Divine Miner Quest"
  tier: legendary
  difficulty: nightmare
  objectives:
    - type: break_block
      material: DIAMOND_ORE
      amount: 40
  reward:
    xp: 4500
    money: 8000
    items:
      - material: NETHERITE_PICKAXE
        name: "&6Divine Pickaxe"
        enchantments:
          - "EFFICIENCY:5"
          - "FORTUNE:3"
          - "UNBREAKING:3"
  conditions:
    min-level: 50
    cost: 10000
```

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

## Troubleshooting

### No Quests Generated

**Check:**
- `enabled: true` in config
- At least one objective type enabled
- Weight values are > 0
- Min/max amounts are valid

### Unbalanced Quests

**Solutions:**
- Adjust difficulty multipliers
- Modify tier multipliers for rewards
- Add material-specific multipliers
- Review min/max ranges

### Too Easy/Hard

**Adjust:**
- Difficulty multipliers
- Amount ranges (min/max)
- Material multipliers
- Reward scaling

---

## Need Help?

- **Discord**: [discord.gg/soapsuniverse](https://discord.gg/soapsuniverse)
- **Issues**: [GitHub Issues](https://github.com/AlternativeSoap/SoapsQuest/issues)
- **Wiki**: [Complete Documentation](WIKI.md)

---

[← Back to README](README.md) | [Configuration Guide →](CONFIGURATION.md) | [Quest Creation →](QUEST-CREATION.md)
