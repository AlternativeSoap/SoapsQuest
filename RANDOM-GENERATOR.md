# Random Quest Generator

Guide to automatic quest generation in SoapsQuest.

---

## 📋 Overview

Generate procedural quests with randomized objectives and rewards.

**Usage:**
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

---

## ⚙️ Configuration

Edit `plugins/SoapsQuest/random-generator.yml`

### Basic Settings

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

### Conditions

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

## 🎯 Available Objective Types

**Total: 33+ types**

### Combat (6)
- `kill` – Kill entities
- `kill_mythicmob` – Kill MythicMobs
- `damage` – Deal damage
- `death` – Die X times
- `bowshoot` – Shoot arrows
- `projectile` – Launch projectiles

### Building (3)
- `break` – Break blocks
- `place` – Place blocks
- `interact` – Interact with blocks

### Collection (7)
- `collect` – Pick up items
- `craft` – Craft items
- `smelt` – Smelt items
- `fish` – Catch fish
- `brew` – Brew potions
- `enchant` – Enchant items
- `drop` – Drop items

### Survival (6)
- `consume` – Eat/drink items
- `tame` – Tame animals
- `trade` – Trade with villagers
- `shear` – Shear animals
- `sleep` – Sleep in bed
- `heal` – Regenerate health

### Movement (3)
- `move` – Walk/run distance
- `jump` – Jump X times
- `vehicle` – Travel in vehicle

### Leveling (3)
- `level` – Gain XP levels
- `gainlevel` – Gain XP levels
- `reachlevel` – Reach specific level

### Misc (3)
- `chat` – Send messages
- `firework` – Launch fireworks
- `command` – Execute commands

---

## 📝 Example Configurations

### Simple Kill Quests

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

### Mining Quests

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

### Collection Quests

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

## ⚠️ Common Mistakes

### ❌ Wrong Objective Type Names

```yaml
# ❌ WRONG
objectives:
  mine_stone:
    objective: break_block  # Wrong name
    blocks: [STONE]
    amount: [50, 200]

# ✅ CORRECT
objectives:
  mine_stone:
    objective: break        # Correct name
    target: [STONE]         # Use 'target' field
    amount: [50, 200]
```

### ❌ Missing Required Fields

```yaml
# ❌ WRONG - enchant needs target
objectives:
  enchant_items:
    objective: enchant
    amount: [5, 15]         # Missing target!

# ✅ CORRECT
objectives:
  enchant_items:
    objective: enchant
    target: [ANY]           # Required!
    amount: [5, 15]
```

---

## 🔍 Testing

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

## 💡 Tips

### Balancing

- **Weights:** Higher = more common (kill: 40, death: 2)
- **Amounts:** Use `[min, max]` for variety
- **Difficulty Scaling:** Use `amount-by-difficulty` for progression
- **Tier Rewards:** Higher tiers = better rewards

### Performance

- Don't enable too many objective types (5-10 optimal)
- Keep material/entity lists reasonable (10-20 items)
- Limit max-items to 3-5 per quest
- Use appropriate weights

---

## 🆘 Need Help?

- **Discord**: [discord.gg/soapsuniverse](https://discord.gg/soapsuniverse)
- **Issues**: [GitHub Issues](https://github.com/AlternativeSoap/SoapsQuest/issues)

---

**[← Back to README](README.md)** | **[Quest Creation →](QUEST-CREATION.md)** | **[Configuration →](CONFIGURATION.md)**

---

Licensed under the MIT License © 2025 AlternativeSoap
