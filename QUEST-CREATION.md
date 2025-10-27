# Quest Creation

Complete guide to creating and managing quests in SoapsQuest.

---

## 📋 Table of Contents

1. [Creating Quests (GUI Method)](#-creating-quests-gui-method)
2. [Creating Quests (YAML Method)](#-creating-quests-yaml-method)
3. [Quest Types](#-quest-types)
4. [Objective Types](#-objective-types)
5. [Rewards](#-rewards)
6. [Conditions](#-conditions)
7. [Advanced Features](#-advanced-features)
8. [Example Quests](#-example-quests)

---

## 🎨 Creating Quests (GUI Method)

The easiest way to create quests is using the in-game editor.

### Step 1: Open the Editor

```
/sq editor
```

### Step 2: Create New Quest

1. Click **"Create Quest"** icon
2. Enter a unique quest ID in chat (e.g., `zombie_slayer`)
3. The editor opens with your new quest

### Step 3: Configure Quest

Click icons to edit:

| Icon | Function |
|------|----------|
| **Name** | Set quest display name |
| **Description** | Add lore lines |
| **Tier** | Choose rarity (common, rare, epic, legendary) |
| **Difficulty** | Choose difficulty (easy, normal, hard, nightmare) |
| **Objectives** | Add/edit objectives |
| **Rewards** | Add/edit rewards |
| **Conditions** | Add requirements |
| **Save** | Save and close |
| **Delete** | Delete quest |

### Step 4: Add Objectives

1. Click **"Add Objective"**
2. Choose objective type
3. Configure target (entity, block, item)
4. Set amount required
5. Click **"Confirm"**

### Step 5: Add Rewards

1. Click **"Add Reward"**
2. Choose reward type:
   - **XP** – Enter amount in chat
   - **Money** – Enter amount in chat (Vault required)
   - **Item** – Hold item and click
   - **Command** – Enter command in chat
3. Confirm

### Step 6: Save Quest

Click **"Save Quest"** to save to `quests.yml`.

---

## 📝 Creating Quests (YAML Method)

Edit `plugins/SoapsQuest/quests.yml` directly.

### Basic Quest Structure

---

## Basic Structure

Every quest requires these fields:

```yaml
quest_id:
  display: "&aQuest Name"              # Required
  tier: common                         # Optional
  difficulty: easy                     # Optional
  objectives:                          # Required (at least 1)
    - type: kill
      target: ZOMBIE
      amount: 10
  reward:                              # Required (at least 1)
    xp: 100
```

### Required Fields

| Field | Description |
|-------|-------------|
| `display` | Quest name with color codes |
| `objectives` | List of objectives (minimum 1) |
| `reward` | Rewards (xp, money, items, or commands) |

### Optional Fields

| Field | Description |
|-------|-------------|
| `tier` | Rarity (common, rare, epic, legendary, or custom) |
| `difficulty` | Difficulty (easy, normal, hard, nightmare, or custom) |
| `material` | Quest paper item (default: PAPER) |
| `sequential` | Complete objectives in order (true/false) |
| `lock-to-player` | Bind to first player (true/false) |
| `milestones` | Progress notifications `[25, 50, 75]` |
| `lore` | Custom quest paper lore |
| `conditions` | Requirements (see [Conditions](#conditions)) |

---

## Objective Types

All objectives use the universal `target` field.

### Combat Objectives (6 types)

**kill** - Kill entities
```yaml
- type: kill
  target: ZOMBIE        # Specific entity
  amount: 10

- type: kill
  target: HOSTILE       # Any hostile mob
  amount: 20

- type: kill
  target: ANY           # Any entity
  amount: 30
```

**kill_mythicmob** - Kill MythicMobs (requires MythicMobs plugin)
```yaml
- type: kill_mythicmob
  target: SkeletonKing  # MythicMob internal name
  amount: 1
```

**damage** - Deal damage
```yaml
- type: damage
  target: ZOMBIE        # Optional filter
  amount: 100           # Half-hearts
```

**death** - Die X times
```yaml
- type: death
  amount: 1
```

**bowshoot** - Shoot arrows
```yaml
- type: bowshoot
  amount: 50
```

**projectile** - Launch projectiles
```yaml
- type: projectile
  target: SNOWBALL      # SNOWBALL, EGG, ENDER_PEARL, or ANY
  amount: 25
```

### Building Objectives (3 types)

**break** - Break blocks
```yaml
- type: break
  target: STONE         # Specific block
  amount: 100

- type: break
  target: ANY           # Any block
  amount: 500
```

**place** - Place blocks
```yaml
- type: place
  target: COBBLESTONE
  amount: 200
```

**interact** - Interact with blocks
```yaml
- type: interact
  target: CHEST         # CHEST, FURNACE, etc.
  amount: 5
```

### Collection Objectives (7 types)

**collect** - Pick up items
```yaml
- type: collect
  target: DIAMOND
  amount: 5
```

**craft** - Craft items
```yaml
- type: craft
  target: IRON_SWORD
  amount: 3
```

**smelt** - Smelt items
```yaml
- type: smelt
  target: IRON_INGOT
  amount: 32
```

**fish** - Catch fish
```yaml
- type: fish
  target: COD           # COD, SALMON, or ANY
  amount: 20
```

**brew** - Brew potions
```yaml
- type: brew
  target: ANY           # Any potion
  amount: 5
```

**enchant** - Enchant items
```yaml
- type: enchant
  target: ANY           # Required field
  amount: 10
```

**drop** - Drop items
```yaml
- type: drop
  target: DIRT
  amount: 64
```

### Survival Objectives (6 types)

**consume** - Eat/drink items
```yaml
- type: consume
  target: APPLE
  amount: 10
```

**tame** - Tame animals
```yaml
- type: tame
  target: WOLF          # WOLF, CAT, HORSE
  amount: 3
```

**trade** - Trade with villagers
```yaml
- type: trade
  target: ANY           # Required field
  amount: 10
```

**shear** - Shear animals
```yaml
- type: shear
  target: SHEEP
  amount: 20
```

**sleep** - Sleep in bed
```yaml
- type: sleep
  amount: 5
```

**heal** - Regenerate health
```yaml
- type: heal
  amount: 100           # Half-hearts
```

### Movement Objectives (3 types)

**move** - Walk/run distance
```yaml
- type: move
  amount: 1000          # Blocks
```

**jump** - Jump X times
```yaml
- type: jump
  amount: 100
```

**vehicle** - Travel in vehicle
```yaml
- type: vehicle
  amount: 500           # Blocks
```

### Leveling Objectives (3 types)

**level** / **gainlevel** - Gain XP levels
```yaml
- type: level
  amount: 10            # Levels to gain
```

**reachlevel** - Reach specific level
```yaml
- type: reachlevel
  level: 30             # Use 'level' field, not 'amount'
```

### Misc Objectives (4 types)

**chat** - Send messages
```yaml
- type: chat
  amount: 50
```

**firework** - Launch fireworks
```yaml
- type: firework
  amount: 10
```

**command** - Execute commands
```yaml
- type: command
  amount: 5
```

**placeholder** - PlaceholderAPI (requires PlaceholderAPI)
```yaml
- type: placeholder
  placeholder: "%player_health%"
  value: "20"
  amount: 1
```

---

## Rewards

### XP Reward
```yaml
reward:
  xp: 100
```

### Money Reward (requires Vault)
```yaml
reward:
  money: 500
```

### Item Rewards

**Basic item:**
```yaml
reward:
  items:
    - material: DIAMOND_SWORD
      amount: 1
```

**Custom item:**
```yaml
reward:
  items:
    - material: DIAMOND_SWORD
      name: "&aLegendary Blade"
      lore:
        - "&7A powerful weapon"
        - "&7From the quest masters"
      enchantments:
        - "SHARPNESS:5"
        - "UNBREAKING:3"
      amount: 1
      chance: 100           # 0-100% drop rate
```

**Multiple items:**
```yaml
reward:
  items:
    - material: DIAMOND
      amount: 5
      chance: 100
    - material: EMERALD
      amount: 3
      chance: 50          # 50% chance
```

### Command Rewards
```yaml
reward:
  commands:
    - "give {player} minecraft:apple 5"
    - "broadcast {player} completed a quest!"
```

**Placeholders:** `{player}`, `{quest_id}`, `{quest_name}`

---

## Conditions

Requirements that must be met.

### Progress Conditions

Checked during quest progression:

```yaml
conditions:
  min-level: 10                          # Minimum XP level
  max-level: 50                          # Maximum XP level
  min-money: 1000                        # Minimum balance (Vault)
  world: ["world", "world_nether"]       # Allowed worlds
  gamemode: ["SURVIVAL", "ADVENTURE"]    # Allowed gamemodes
  time: DAY                              # DAY or NIGHT
  permission: "soapsquest.vip"           # Required permission
  placeholder: "%player_health% >= 10"   # PlaceholderAPI (requires PAPI)
```

### Locking Conditions

Consume resources to unlock:

```yaml
conditions:
  cost: 500                              # Money cost (Vault)
  item: "DIAMOND:10,EMERALD:5"           # Required items
  consume-item: true                     # Remove items when unlocking
```

### Active Quest Limit

```yaml
conditions:
  active-limit: 3                        # Max concurrent quests with this ID
```

---

## Advanced Features

### Multi-Objective Quests

Complete multiple objectives (any order):

```yaml
multi_quest:
  display: "&eGatherer"
  objectives:
    - type: break
      target: STONE
      amount: 100
    - type: collect
      target: WHEAT
      amount: 64
    - type: craft
      target: BREAD
      amount: 32
  reward:
    xp: 300
```

### Sequential Quests

Complete objectives in order (one at a time):

```yaml
sequential_quest:
  display: "&aApprentice Training"
  sequential: true
  objectives:
    - type: collect
      target: WOOD
      amount: 32
    - type: craft
      target: WOODEN_PICKAXE
      amount: 1
    - type: break
      target: COBBLESTONE
      amount: 64
  reward:
    xp: 200
```

### Lock-to-Player

Bind quest to first player who makes progress:

```yaml
locked_quest:
  display: "&cPersonal Challenge"
  lock-to-player: true
  objectives:
    - type: kill
      target: ZOMBIE
      amount: 50
  reward:
    xp: 500
```

### Custom Quest Paper

```yaml
custom_quest:
  display: "&5Epic Quest"
  material: ENCHANTED_BOOK        # Quest paper material
  lore:
    - "&7A special quest"
    - "&7Complete for rewards"
  objectives:
    - type: kill
      target: ENDERMAN
      amount: 10
  reward:
    xp: 1000
```

---

## Examples

### Simple Kill Quest
```yaml
zombie_slayer:
  display: "&aZombie Slayer"
  tier: common
  difficulty: easy
  objectives:
    - type: kill
      target: ZOMBIE
      amount: 10
  reward:
    xp: 100
    money: 50
```

### Mining Quest
```yaml
diamond_miner:
  display: "&bDiamond Miner"
  tier: rare
  difficulty: normal
  objectives:
    - type: break
      target: DIAMOND_ORE
      amount: 5
  reward:
    xp: 500
    items:
      - material: DIAMOND_PICKAXE
        name: "&bMiner's Pick"
        enchantments:
          - "EFFICIENCY:4"
        chance: 100
  conditions:
    min-level: 20
    world: ["world"]
```

### Multi-Objective Quest
```yaml
master_crafter:
  display: "&eMaster Crafter"
  tier: epic
  difficulty: hard
  objectives:
    - type: craft
      target: IRON_SWORD
      amount: 5
    - type: craft
      target: IRON_PICKAXE
      amount: 3
    - type: enchant
      target: ANY
      amount: 10
  reward:
    xp: 750
    money: 500
    commands:
      - "give {player} minecraft:anvil 1"
```

### VIP Quest with Requirements
```yaml
vip_challenge:
  display: "&dVIP Challenge"
  tier: legendary
  difficulty: nightmare
  objectives:
    - type: kill
      target: WITHER
      amount: 1
  reward:
    xp: 5000
    money: 2000
    items:
      - material: NETHER_STAR
        name: "&dVIP Trophy"
        chance: 100
  conditions:
    permission: "soapsquest.vip"
    cost: 1000
    min-level: 50
```

---

**[← Back to README](README.md)** | **[Configuration →](CONFIGURATION.md)** | **[Random Generator →](RANDOM-GENERATOR.md)**
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
      target: OAK_LOG
      amount: 10
    - type: craft
      target: CRAFTING_TABLE
      amount: 1
    - type: craft
      target: WOODEN_PICKAXE
      amount: 1
```

### Multi-Objective Quests
Complete multiple objectives (any order):
```yaml
multi_quest:
  display: "&eGatherer"
  objectives:
    - type: break
      target: STONE
      amount: 100
    - type: collect
      target: WHEAT
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
      target: ENDER_DRAGON
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
      target: STONE
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
    - type: break
      target: OAK_LOG
      amount: 10
    - type: craft
      target: CRAFTING_TABLE
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
      target: ZOMBIE
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
    - type: break
      target: STONE
      amount: 1000
    - type: break
      target: IRON_ORE
      amount: 100
    - type: break
      target: DIAMOND_ORE
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
      target: ENDER_DRAGON
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
      target: IRON_ORE
      amount: 20
    - type: smelt
      target: IRON_INGOT
      amount: 20
    - type: craft
      target: IRON_SWORD
      amount: 1
    - type: craft
      target: IRON_CHESTPLATE
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
   - Validate target names (entities/blocks/items)

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

### ❌ Wrong Field Names

```yaml
objectives:
  - type: kill
    entity: ZOMBIE  # ❌ Wrong (deprecated field name)
```

✅ **Correct:**
```yaml
objectives:
  - type: kill
    target: ZOMBIE  # ✅ Use 'target' for all objectives
```

### ❌ Wrong Target Names

```yaml
objectives:
  - type: collect
    target: diamond_ore  # ❌ Wrong (lowercase)
```

✅ **Correct:**
```yaml
objectives:
  - type: collect
    target: DIAMOND_ORE  # ✅ Uppercase
```

### ❌ Missing Amount

```yaml
objectives:
  - type: kill
    target: ZOMBIE  # ❌ No amount specified
```

✅ **Correct:**
```yaml
objectives:
  - type: kill
    target: ZOMBIE
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
