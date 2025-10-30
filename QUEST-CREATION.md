# Quest Creation

Complete guide to creating and managing quests in SoapsQuest.

---

## 📋 Quick Navigation

- [GUI Method](#-gui-method) – Create quests in-game
- [YAML Method](#-yaml-method) – Edit configuration files
- [Objective Types](#-objective-types) – All 31 objective types
- [Rewards](#-rewards) – XP, money, items, commands
- [Conditions](#-conditions) – Requirements and restrictions
- [Examples](#-examples) – Ready-to-use quest templates

---

## 🎨 GUI Method

### Open Editor

```
/sq editor
```

### Create Quest

1. Click **"Create Quest"**
2. Enter unique ID (e.g., `zombie_slayer`)
3. Configure using GUI icons:
   - **Name** – Display name
   - **Tier** – Rarity level
   - **Difficulty** – Challenge level
   - **Objectives** – Add tasks
   - **Rewards** – Add rewards
   - **Conditions** – Add requirements
4. Click **"Save"**

---

## 📝 YAML Method

Edit `plugins/SoapsQuest/quests.yml`:

### Basic Structure

```yaml
quest_id:
  display: "&aQuest Name"
  tier: common
  difficulty: easy
  objectives:
    - type: kill
      target: ZOMBIE
      amount: 10
  reward:
    xp: 100
```

### Quest Types

**Single Objective:**
```yaml
simple_quest:
  display: "&aZombie Slayer"
  objectives:
    - type: kill
      target: ZOMBIE
      amount: 10
  reward:
    xp: 100
```

**Multi-Objective** (any order):
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
  reward:
    xp: 300
```

**Sequential** (in order):
```yaml
sequential_quest:
  display: "&aTraining"
  sequential: true
  objectives:
    - type: collect
      target: OAK_LOG
      amount: 32
    - type: craft
      target: CRAFTING_TABLE
      amount: 1
  reward:
    xp: 200
```

---

## ⚔️ Objective Types

**Total: 33+ objective types**

All objectives use the unified `<amount>` field and `<target>` where applicable.

### Combat (6)

**kill** – Kill entities
```yaml
- type: kill
  target: ZOMBIE      # Specific entity
  amount: 10

- type: kill
  target: HOSTILE     # Any hostile mob
  amount: 20
```

**kill_mythicmob** – Kill MythicMobs (requires MythicMobs)
```yaml
- type: kill_mythicmob
  target: SkeletonKing
  amount: 1
```

**damage** – Deal damage
```yaml
- type: damage
  target: ZOMBIE      # Optional
  amount: 100         # Half-hearts
```

**death** – Die X times
```yaml
- type: death
  amount: 1
```

**bowshoot** – Shoot arrows
```yaml
- type: bowshoot
  amount: 50
```

**projectile** – Launch projectiles
```yaml
- type: projectile
  target: SNOWBALL    # SNOWBALL, EGG, ENDER_PEARL, or ANY
  amount: 25
```

---

### Building (3)

**break** – Break blocks
```yaml
- type: break
  target: STONE
  amount: 100
```

**place** – Place blocks
```yaml
- type: place
  target: COBBLESTONE
  amount: 200
```

**interact** – Interact with blocks
```yaml
- type: interact
  target: CHEST
  amount: 5
```

---

### Collection (7)

**collect** – Pick up items
```yaml
- type: collect
  target: DIAMOND
  amount: 5
```

**craft** – Craft items
```yaml
- type: craft
  target: IRON_SWORD
  amount: 3
```

**smelt** – Smelt items
```yaml
- type: smelt
  target: IRON_INGOT
  amount: 32
```

**fish** – Catch fish
```yaml
- type: fish
  target: COD         # COD, SALMON, or ANY
  amount: 20
```

**brew** – Brew potions
```yaml
- type: brew
  target: ANY
  amount: 5
```

**enchant** – Enchant items
```yaml
- type: enchant
  target: ANY
  amount: 10
```

**drop** – Drop items
```yaml
- type: drop
  target: DIRT
  amount: 64
```

---

### Survival (6)

**consume** – Eat/drink
```yaml
- type: consume
  target: APPLE
  amount: 10
```

**tame** – Tame animals
```yaml
- type: tame
  target: WOLF
  amount: 3
```

**trade** – Trade with villagers
```yaml
- type: trade
  target: ANY
  amount: 10
```

**shear** – Shear animals
```yaml
- type: shear
  target: SHEEP
  amount: 20
```

**sleep** – Sleep in bed
```yaml
- type: sleep
  amount: 5
```

**heal** – Regenerate health
```yaml
- type: heal
  amount: 100         # Half-hearts
```

---

### Movement (3)

**move** – Walk/run distance
```yaml
- type: move
  amount: 1000        # Blocks
```

**jump** – Jump X times
```yaml
- type: jump
  amount: 100
```

**vehicle** – Travel in vehicle
```yaml
- type: vehicle
  amount: 500         # Blocks
```

---

### Leveling (3)

**level / gainlevel** – Gain XP levels
```yaml
- type: level
  amount: 10
```

**reachlevel** – Reach specific level
```yaml
- type: reachlevel
  level: 30           # Use 'level', not 'amount'
```

---

### Misc (3)

**chat** – Send messages
```yaml
- type: chat
  amount: 50
```

**firework** – Launch fireworks
```yaml
- type: firework
  amount: 10
```

**command** – Execute commands
```yaml
- type: command
  amount: 5
```

---

## 🎁 Rewards

### XP
```yaml
reward:
  xp: 100
```

### Money
Requires Vault.
```yaml
reward:
  money: 500
```

### Items

**Basic:**
```yaml
reward:
  items:
    - material: DIAMOND_SWORD
      amount: 1
```

**Custom:**
```yaml
reward:
  items:
    - material: DIAMOND_SWORD
      name: "&aLegendary Blade"
      lore:
        - "&7A powerful weapon"
      enchantments:
        - "SHARPNESS:5"
        - "UNBREAKING:3"
      amount: 1
      chance: 100       # 0-100%
```

**Multiple:**
```yaml
reward:
  items:
    - material: DIAMOND
      amount: 5
      chance: 100       # Always
    - material: EMERALD
      amount: 3
      chance: 50        # 50% chance
```

**MMOItems & Custom Items:**

To add MMOItems or any custom item with NBT data as rewards:

**GUI Method:**
1. Open Quest Editor → Quest Details → Edit Rewards
2. Click "Add Reward"
3. Select "Item Reward"
4. Hold the MMOItem/custom item in your hand
5. Type `hand` in chat
6. The held item (with all NBT data) will be saved as a reward

**Chat Input Alternative:**
- Type `DIAMOND 5` for basic items
- Type `hand` to use whatever you're holding (preserves NBT, enchantments, custom names, etc.)

**YAML Representation:**

When saved via the `hand` method, it stores as:
```yaml
reward:
  items:
    - material: HAND   # References the held item when saved
      amount: 1
      chance: 100
```

This works for:
- MMOItems custom items
- Heads with custom textures
- Items with complex NBT data
- Any custom plugin item

### Commands
```yaml
reward:
  commands:
    - "give {player} minecraft:apple 5"
    - "broadcast {player} completed a quest!"
```

Placeholders: `{player}`, `{quest_id}`, `{quest_name}`

### Quest Rewards

Give another quest as a reward:

```yaml
reward:
  quest: "follow_up_quest"
```

**Example Chain:**
```yaml
starter_quest:
  display: "&aBeginning"
  objectives:
    - type: break
      target: OAK_LOG
      amount: 10
  reward:
    xp: 50
    quest: "intermediate_quest"

intermediate_quest:
  display: "&eNext Step"
  objectives:
    - type: kill
      target: ZOMBIE
      amount: 20
  reward:
    xp: 200
    quest: "final_quest"
```

When a player completes `starter_quest`, they automatically receive `intermediate_quest`.

---

## 🔒 Conditions

### Progress Conditions
Checked during quest:
```yaml
conditions:
  min-level: 10
  max-level: 50
  min-money: 1000
  world: ["world", "world_nether"]
  gamemode: ["SURVIVAL", "ADVENTURE"]
  time: DAY                    # DAY or NIGHT
  permission: "soapsquest.vip"
```

### Locking Conditions
Consume resources to unlock:
```yaml
conditions:
  cost: 500                    # Money cost (Vault)
  item: "DIAMOND:10,EMERALD:5"
  consume-item: true
```

### Quest Limits
```yaml
conditions:
  active-limit: 3              # Max concurrent with this ID
```

---

## 🚀 Advanced Features

### Lock-to-Player
```yaml
personal_quest:
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
  material: ENCHANTED_BOOK
  lore:
    - "&7A special quest"
  objectives:
    - type: kill
      target: ENDERMAN
      amount: 10
  reward:
    xp: 1000
```

### Milestones
```yaml
milestone_quest:
  display: "&aProgress Quest"
  milestones: [25, 50, 75]
  objectives:
    - type: break
      target: STONE
      amount: 1000
  reward:
    xp: 500
```

---

## 📚 Examples

### Beginner Quest
```yaml
starter_quest:
  display: "&aWelcome"
  tier: common
  difficulty: easy
  objectives:
    - type: break
      target: OAK_LOG
      amount: 10
  reward:
    xp: 50
    money: 100
```

### Combat Quest
```yaml
zombie_slayer:
  display: "&cZombie Slayer"
  tier: rare
  difficulty: normal
  objectives:
    - type: kill
      target: ZOMBIE
      amount: 50
  reward:
    xp: 500
    items:
      - material: DIAMOND_SWORD
        name: "&cZombie Slayer"
        enchantments:
          - "SHARPNESS:3"
        chance: 100
  conditions:
    min-level: 15
```

### Mining Quest
```yaml
diamond_miner:
  display: "&bDiamond Miner"
  tier: epic
  difficulty: hard
  objectives:
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
        chance: 100
```

### Boss Quest
```yaml
dragon_slayer:
  display: "&5&lDragon Slayer"
  tier: legendary
  difficulty: nightmare
  material: DRAGON_HEAD
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
    commands:
      - "give {player} minecraft:nether_star 5"
  conditions:
    min-level: 50
    cost: 10000
```

### Daily Quest
```yaml
daily_fishing:
  display: "&bDaily Fishing"
  tier: common
  difficulty: easy
  objectives:
    - type: fish
      target: ANY
      amount: 20
  reward:
    xp: 200
    money: 300
  conditions:
    active-limit: 1
```

---

## ⚠️ Common Mistakes

**❌ Wrong target names:**
```yaml
target: diamond_ore  # Wrong (lowercase)
```
✅ Use uppercase: `target: DIAMOND_ORE`

**❌ Missing amount:**
```yaml
- type: kill
  target: ZOMBIE     # No amount!
```
✅ Always include: `amount: 10`

**❌ Invalid indentation:**
```yaml
reward:
xp: 100              # Not indented
```
✅ Use 2 spaces: `  xp: 100`

---

## � Random Quest Generation

Generate procedural quests with randomized objectives and rewards.

### Command Usage

```
/sq generate [type]
```

**Types:**
- `single` – One objective
- `multi` – Multiple objectives (any order)
- `sequence` – Sequential objectives

**Example:**
```
/sq generate single
→ Generated quest: quest_common_kill_12345
→ Use /sq give <player> quest_common_kill_12345
```

Generated quests are saved to `plugins/SoapsQuest/generated.yml`.

---

### Configuration

Edit `plugins/SoapsQuest/random-generator.yml`

**Basic Settings:**

```yaml
random-generator:
  enabled: true
  save-generated-quests: true
  save-location: "generated.yml"
  allowed-types: [single, multi, sequence]
```

---

### Objective Pools

Define objectives that can be randomly selected:

```yaml
objectives:
  kill_zombies:
    objective: kill
    target: [ZOMBIE]
    amount: [10, 30]
  
  break_stone:
    objective: break
    target: [STONE, COBBLESTONE]
    amount: [50, 200]
  
  collect_items:
    objective: collect
    target: [IRON_INGOT, GOLD_INGOT]
    amount: [5, 15]
```

**Amount Scaling by Difficulty:**

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

---

### Objective Weights

Control how often objectives appear:

```yaml
objective-weights:
  kill: 40              # Most common
  break: 30
  collect: 15
  craft: 15
  fish: 10
  death: 2              # Least common
```

Higher weight = more likely to be selected.

---

### Reward Pools

**XP Rewards:**
```yaml
reward-pool:
  xp:
    common: [25, 100]
    rare: [100, 250]
    epic: [250, 400]
    legendary: [400, 500]
```

**Money Rewards:**
```yaml
  money:
    common: [10, 100]
    rare: [100, 400]
    epic: [400, 750]
    legendary: [750, 1000]
```

**Item Rewards:**
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

---

### Display Templates

Quest names based on objective type:

```yaml
display-templates:
  kill:
    - "&c<target> Slayer"
    - "&4Hunt &f<amount> &4<target>s"
  
  break:
    - "&8<target> Breaker"
    - "&7Mine &f<amount> &7<target>"
  
  collect:
    - "&e<target> Collector"
    - "&6Gather &f<amount> &6<target>s"
```

**Placeholders:**
- `<target>` – Entity/block/item name
- `<amount>` – Required amount
- `<tier>` – Quest tier
- `<difficulty>` – Quest difficulty

---

### Random Conditions

Add random requirements to quests:

```yaml
conditions:
  enabled: true
  
  min-level:
    enabled: true
    chance: 40          # 40% of quests require level
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

---

### Example Configurations

**Simple Kill Quests:**
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
```

**Mining Quests:**
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
```

**Collection Quests:**
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

### Testing Random Generation

Enable debug mode:
```
/sq debug
```

Generate test quests:
```
/sq generate single
/sq generate multi
/sq generate sequence
```

Check generated quests in `plugins/SoapsQuest/generated.yml`.

---

### Balancing Tips

- **Weights:** Higher = more common (kill: 40, death: 2)
- **Amounts:** Use `[min, max]` for variety
- **Difficulty Scaling:** Use `amount-by-difficulty` for progression
- **Tier Rewards:** Higher tiers = better rewards
- **Performance:** Don't enable too many objective types (5-10 optimal)
- **Lists:** Keep material/entity lists reasonable (10-20 items)
- **Max Items:** Limit max-items to 3-5 per quest

---

### Common Random Generation Mistakes

**❌ Wrong objective type names:**
```yaml
objectives:
  mine_stone:
    objective: break_block  # Wrong name
    blocks: [STONE]
```

✅ Use correct names: `objective: break` with `target: [STONE]`

**❌ Missing required fields:**
```yaml
objectives:
  enchant_items:
    objective: enchant
    amount: [5, 15]         # Missing target!
```

✅ Include target: `target: [ANY]`

---

## �🆘 Resources

- **Discord**: [discord.gg/soapsuniverse](https://discord.gg/soapsuniverse)
- **Issues**: [GitHub Issues](https://github.com/AlternativeSoap/SoapsQuest/issues)
- **Materials**: [Spigot Material Enum](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html)
- **Entities**: [Spigot EntityType Enum](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html)

---

**[← Back to README](README.md)** | **[Configuration →](CONFIGURATION.md)**

---

Licensed under the MIT License © 2025 AlternativeSoap
