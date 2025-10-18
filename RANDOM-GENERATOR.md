# Random Quest Generator

Guide to random quest generation in SoapsQuest.

---

## Overview

Generate procedural quests with randomized objectives and rewards.

**Use cases:**
- Testing and development
- Dynamic quest content
- Quick quest templates

**How it works:**
1. Run `/sq generate [type]`
2. Plugin selects random objectives from `random-generator.yml`
3. Generates amounts based on difficulty
4. Selects rewards from tier-based pools
5. Saves to `generated.yml`
6. Returns quest ID for distribution

---

## Commands

### Generate Random Quest

```
/sq generate [type]
```

**Types:** `single`, `multi`, `sequence`

**Permission:** `soapsquest.generate` (op)

**Examples:**
```
/sq generate              # Random type
/sq generate single       # Single objective
/sq generate multi        # Multiple objectives (any order)
/sq generate sequence     # Sequential objectives
```

### After Generation

Output example:
```
[SoapsQuest] Generated quest: quest_common_kill_12345
Use /sq give <player> quest_common_kill_12345
```

Give to player:
```
/sq give Steve quest_common_kill_12345
```

---

## Configuration

Edit `plugins/SoapsQuest/random-generator.yml`

### Basic Settings

```yaml
random-generator:
  enabled: true
  save-generated-quests: true
  allowed-types: [single, multi, sequence]
  save-location: "generated.yml"
```

### Objective Configuration

All objectives use the universal `target` field:

```yaml
objectives:
  kill_zombies:
    objective: kill
    target: [ZOMBIE]            # Entity types
    amount: [15, 40]            # [min, max]
  
  break_stone:
    objective: break
    target: [STONE, COBBLESTONE]  # Block types
    amount: [50, 200]
  
  collect_valuables:
    objective: collect
    target: [IRON_INGOT, GOLD_INGOT]  # Item types
    amount: [5, 15]
```

### Amount Scaling by Difficulty

```yaml
objectives:
  kill_hostile:
    objective: kill
    target: [ZOMBIE, SKELETON, CREEPER]
    amount-by-difficulty:
      easy: [10, 25]
      normal: [20, 40]
      hard: [40, 75]
      nightmare: [75, 150]
```

### Objective Weights

Control how often objectives appear:

```yaml
objective-weights:
  kill: 40              # Most common
  break: 30
  collect: 15
  craft: 15
  fish: 10
  place: 20
  # ... etc
  death: 2              # Least common
```

Higher weight = more likely to be selected.

### Reward Pools

#### XP Rewards

```yaml
reward-pool:
  xp:
    common: [25, 100]
    rare: [100, 250]
    epic: [250, 400]
    legendary: [400, 500]
```

#### Money Rewards

```yaml
  money:
    common: [10, 100]
    rare: [100, 400]
    epic: [400, 750]
    legendary: [750, 1000]
```

#### Item Rewards

```yaml
  items:
    selection-mode: "weighted"
    min-items: 1
    max-items: 3
    
    pool:
      - material: IRON_INGOT
        amount: [1, 5]
        tiers: [common, rare]
        weight: 50
      
      - material: DIAMOND
        amount: [1, 2]
        tiers: [epic, legendary]
        weight: 15
        min-difficulty: hard
```

**Fields:**
- **material:** Item type
- **amount:** [min, max] or single number
- **tiers:** Which tiers can receive this reward
- **weight:** Selection probability
- **min-difficulty:** Minimum difficulty required

### Display Templates

Quest names based on objective type:

```yaml
display-templates:
  kill:
    - "&c<target> Slayer"
    - "&4Hunt &f<amount> &4<target>s"
    - "&cEliminate &f<amount> &c<target>"
  
  break:
    - "&8<target> Breaker"
    - "&7Mine &f<amount> &7<target>"
  
  collect:
    - "&e<target> Collector"
    - "&6Gather &f<amount> &6<target>s"
```

**Placeholders:**
- `<target>` - Entity/block/item name
- `<amount>` - Required amount
- `<tier>` - Quest tier
- `<difficulty>` - Quest difficulty

### Lore Structure

```yaml
lore-structure:
  header:
    mode: "random"
    entries:
      - "<tier_color>═══════════════════════════"
      - "<tier_color>╔═══════════════════════════╗"
  
  quest-info:
    mode: "tier-based"
    common:
      - "&7Tier: <tier> &8| &7Type: &f<type>"
      - "&7Task: &f<objective>"
    legendary:
      - "<tier_color>&l  <tier> <type> Quest"
      - "&7Task: &f<objective>"
      - "<tier_color>&l  ⚔ LEGENDARY TASK ⚔"
```

### Conditions

Add random requirements to quests:

```yaml
conditions:
  enabled: true
  
  min-level:
    enabled: true
    chance: 40              # 40% chance to add
    by-tier:
      common: 0
      rare: 10
      epic: 25
      legendary: 50
  
  cost:
    enabled: true
    chance: 20
    by-tier:
      common: 50
      rare: 250
      epic: 1000
      legendary: 5000
```

### Quest Paper Material

```yaml
quest-paper-material:
  selection-mode: "random"      # or "tier-based"
  default-material: "PAPER"
  random-pool:
    - material: PAPER
      weight: 50
    - material: ENCHANTED_BOOK
      weight: 10
```

---

## Available Objective Types

**Total: 31 types**

### Combat (6)
- `kill` - Kill entities
- `kill_mythicmob` - Kill MythicMobs
- `damage` - Deal damage
- `death` - Die X times
- `bowshoot` - Shoot arrows
- `projectile` - Launch projectiles

### Building (3)
- `break` - Break blocks
- `place` - Place blocks
- `interact` - Interact with blocks

### Collection (7)
- `collect` - Pick up items
- `craft` - Craft items
- `smelt` - Smelt items
- `fish` - Catch fish
- `brew` - Brew potions
- `enchant` - Enchant items
- `drop` - Drop items

### Survival (6)
- `consume` - Eat/drink items
- `tame` - Tame animals
- `trade` - Trade with villagers
- `shear` - Shear animals
- `sleep` - Sleep in bed
- `heal` - Regenerate health

### Movement (3)
- `move` - Walk/run distance
- `jump` - Jump X times
- `vehicle` - Travel in vehicle

### Leveling (3)
- `level` - Gain XP levels
- `gainlevel` - Gain XP levels
- `reachlevel` - Reach specific level

### Misc (4)
- `chat` - Send messages
- `firework` - Launch fireworks
- `command` - Execute commands
- `placeholder` - PlaceholderAPI conditions

---

## Example Configuration

### Simple Kill Quest Pool

```yaml
objectives:
  kill_zombies:
    objective: kill
    target: [ZOMBIE]
    amount: [10, 30]
  
  kill_skeletons:
    objective: kill
    target: [SKELETON]
    amount: [10, 30]
  
  kill_hostile:
    objective: kill
    target: [HOSTILE]
    amount-by-difficulty:
      easy: [15, 25]
      normal: [30, 50]
      hard: [50, 100]
```

### Mining Quest Pool

```yaml
objectives:
  mine_stone:
    objective: break
    target: [STONE, COBBLESTONE]
    amount: [100, 300]
  
  mine_ores:
    objective: break
    target: [COAL_ORE, IRON_ORE, GOLD_ORE]
    amount: [20, 50]
  
  mine_diamonds:
    objective: break
    target: [DIAMOND_ORE]
    amount: [3, 10]
```

### Collection Quest Pool

```yaml
objectives:
  gather_food:
    objective: collect
    target: [WHEAT, CARROT, POTATO]
    amount: [32, 64]
  
  gather_valuables:
    objective: collect
    target: [IRON_INGOT, GOLD_INGOT, DIAMOND]
    amount: [5, 20]
```

---

## Tips

### Balancing

- **Weights:** Higher = more common (kill: 40, death: 2)
- **Amounts:** Use `[min, max]` for variety
- **Difficulty scaling:** Use `amount-by-difficulty` for proper progression
- **Tier rewards:** Higher tiers = better rewards

### Testing

1. Enable generator: `enabled: true`
2. Run `/sq generate single` multiple times
3. Check `generated.yml` for results
4. Adjust weights and amounts as needed
5. Test with `/sq give <player> <quest_id>`

### Common Mistakes

❌ **Wrong field names:**
- Don't use `entity` for break objectives
- Don't use `blocks` for kill objectives
- Always use `target` (universal field)

❌ **Missing required fields:**
- `enchant` needs `target: ANY`
- `trade` needs `target: ANY`
- `reachlevel` uses `level` not `amount`

✅ **Correct usage:**
```yaml
objectives:
  enchant_items:
    objective: enchant
    target: [ANY]         # Required!
    amount: [5, 15]
  
  mine_blocks:
    objective: break
    target: [STONE]       # Use 'target' not 'blocks'
    amount: [100, 200]
```

---

**[← Back to README](README.md)** | **[Quest Creation →](QUEST-CREATION.md)** | **[Configuration →](CONFIGURATION.md)**
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

**Total: 31 objective types**

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

**✅ NOW IMPLEMENTED** - Add random conditions to generated quests:

```yaml
conditions:
  enabled: true
  
  # Progress conditions (checked during quest)
  min-level:
    enabled: true
    chance: 40          # 40% of quests will have min-level requirement
    by-tier:            # Tier-based values
      common: 0
      rare: 10
      epic: 25
      legendary: 50
    by-difficulty:      # Difficulty-based values (higher priority than tier)
      easy: 0
      normal: 5
      hard: 15
      nightmare: 30
  
  max-level:
    enabled: false
    chance: 10
    default: 100
  
  min-money:
    enabled: true
    chance: 25          # 25% of quests require minimum balance
    by-tier:
      common: 100
      rare: 500
      epic: 2000
      legendary: 10000
  
  world:
    enabled: false
    chance: 15
    allowed-worlds:
      - world
      - world_nether
      - world_the_end
  
  gamemode:
    enabled: false
    chance: 20
    allowed-modes:
      - SURVIVAL
      - ADVENTURE
  
  time:
    enabled: false
    chance: 10
    options:
      - DAY
      - NIGHT
  
  permission:
    enabled: false
    chance: 30
    by-tier:
      common: "soapsquest.tier.common"
      rare: "soapsquest.tier.rare"
      epic: "soapsquest.tier.epic"
      legendary: "soapsquest.tier.legendary"
  
  # Locking conditions (consume resources to unlock)
  cost:
    enabled: true
    chance: 20          # 20% of quests cost money to unlock
    by-tier:
      common: 50
      rare: 250
      epic: 1000
      legendary: 5000
  
  item:
    enabled: false
    chance: 15
    consume-item: true
    by-tier:
      common: "IRON_INGOT:5"
      rare: "GOLD_INGOT:3"
      epic: "DIAMOND:2"
      legendary: "NETHERITE_INGOT:1"
  
  # Limits
  active-limit:
    enabled: false
    chance: 5
    default: 3
  
  # PlaceholderAPI conditions
  placeholder:
    enabled: false
    chance: 10
    expressions:
      - "%player_health% >= 15"
      - "%player_food_level% >= 10"
      - "%vault_eco_balance% >= 1000"
```

**How It Works:**
1. Each condition has an `enabled` flag and a `chance` percentage
2. When generating a quest, the plugin rolls for each enabled condition
3. If the roll succeeds, that condition is added to the quest
4. Values are selected from `by-tier` or `by-difficulty` maps
5. `by-difficulty` takes priority over `by-tier` when both exist

**Example Generated Quest with Conditions:**
```yaml
quest_rare_kill_12345:
  display: "&9Elite Zombie Slayer"
  tier: rare
  difficulty: normal
  objectives:
    - type: kill
      entity: ZOMBIE
      amount: 25
  reward:
    xp: 175
    money: 250
  conditions:         # ← Randomly added based on configuration
    min-level: 10     # From min-level.by-tier.rare
    cost: 250         # From cost.by-tier.rare (must pay $250 to unlock)
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
