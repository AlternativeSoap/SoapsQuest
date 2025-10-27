# Quest Loot System

Quest papers can naturally appear in chest loot and mob drops. Players discover quests while exploring or fighting monsters.

---

## 📋 Overview

**Chest Loot** – Naturally generated chests have a chance to contain quest papers

**Mob Drops** – Mobs can drop quest papers when killed

**Quest Sources:**
- `manual` – Use specific quests from your config
- `random` – Generate new random quests on-the-fly
- `mixed` – 50/50 mix of both

---

## ⚙️ Configuration

File: `plugins/SoapsQuest/quest-loot.yml`

### Basic Setup

```yaml
quest-loot:
  enabled: true
  debug-logs: false  # Enable for troubleshooting
```

---

### Chest Loot

```yaml
  chest:
    enabled: true
    chance: 10              # 10% chance per chest
    amount-min: 1
    amount-max: 2
    worlds: ["world", "world_nether"]
    source-mode: "mixed"    # manual | random | mixed
    quests:
      - "starter_adventure"
      - "hidden_artifact"
    structures:             # Optional filter
      - "minecraft:ancient_city"
      - "minecraft:desert_pyramid"
```

**Options:**
- `chance` – Percentage chance per chest (0-100)
- `amount-min/max` – Number of quest papers dropped
- `worlds` – List of allowed worlds (empty = all worlds)
- `source-mode` – How quests are selected
- `quests` – Manual quest list (used when source-mode is `manual` or `mixed`)
- `structures` – Filter by structure type (empty = all structures)

---

### Mob Drops

```yaml
  mobs:
    enabled: true
    default-chance: 5       # Default for unlisted mobs
    worlds: ["world"]
    types:
      ZOMBIE:
        chance: 12
        amount-min: 1
        amount-max: 2
      SKELETON:
        chance: 8
        amount-min: 1
        amount-max: 1
      ENDER_DRAGON:
        chance: 100
        amount-min: 3
        amount-max: 6
```

**Options:**
- `default-chance` – Chance for mobs not listed in `types`
- `worlds` – List of allowed worlds (empty = all worlds)
- `types` – Per-mob configuration with custom chances and amounts

---

## 📦 Source Modes

### Manual Mode

Uses specific quests you define.

**Best for:**
- Curated quest experiences
- Story-driven progression
- Themed dungeons
- Controlling exactly what players find

```yaml
source-mode: "manual"
quests:
  - "zombie_slayer"
  - "treasure_hunter"
  - "diamond_miner"
```

---

### Random Mode

Generates brand new quests using your random generator settings.

**Best for:**
- Infinite quest variety
- Keeping content fresh
- Testing and development
- Less manual creation work

```yaml
source-mode: "random"
```

**Requires:** `random-generator.yml` to be configured

---

### Mixed Mode

50% chance for manual quests, 50% chance for random generation.

**Best for:**
- Best of both worlds
- Some curated + some variety
- Balanced approach

```yaml
source-mode: "mixed"
quests:
  - "epic_boss_quest"
  - "legendary_item"
```

---

## 🌍 World Filtering

Control which worlds can have quest loot:

```yaml
chest:
  worlds: ["world", "world_nether"]
```

```yaml
mobs:
  worlds: ["world"]
```

**Empty list = all worlds allowed**

---

## 🏛️ Structure Filtering

Only generate quests in specific structures (chests only):

```yaml
chest:
  structures:
    - "minecraft:ancient_city"
    - "minecraft:desert_pyramid"
    - "minecraft:jungle_temple"
    - "minecraft:shipwreck"
    - "minecraft:buried_treasure"
```

**Empty list = all structures allowed**

---

## 🎯 Per-Mob Configuration

Configure different chances and amounts for each mob:

```yaml
mobs:
  types:
    ZOMBIE:
      chance: 12          # 12% drop chance
      amount-min: 1
      amount-max: 2
    
    SKELETON:
      chance: 8
      amount-min: 1
      amount-max: 1
    
    ENDER_DRAGON:
      chance: 100         # Always drops
      amount-min: 3
      amount-max: 6       # Boss drops 3-6 quests!
    
    WITHER:
      chance: 100
      amount-min: 5
      amount-max: 10
```

Unlisted mobs use `default-chance`.

---

## 📝 Example Configurations

### Dungeon Treasure

```yaml
quest-loot:
  chest:
    enabled: true
    chance: 25
    source-mode: "manual"
    quests:
      - "dungeon_explorer"
      - "treasure_hunt"
    structures:
      - "minecraft:dungeon"
      - "minecraft:mineshaft"
```

---

### Boss Rewards

```yaml
quest-loot:
  mobs:
    enabled: true
    types:
      WITHER:
        chance: 100
        amount-min: 3
        amount-max: 5
      ENDER_DRAGON:
        chance: 100
        amount-min: 5
        amount-max: 10
      WARDEN:
        chance: 75
        amount-min: 2
        amount-max: 4
```

---

### Random World Loot

```yaml
quest-loot:
  chest:
    enabled: true
    chance: 5
    source-mode: "random"
    worlds: ["world"]
```

---

### Nether Quests

```yaml
quest-loot:
  mobs:
    enabled: true
    worlds: ["world_nether"]
    types:
      PIGLIN:
        chance: 10
        amount-min: 1
        amount-max: 1
      BLAZE:
        chance: 15
        amount-min: 1
        amount-max: 2
      GHAST:
        chance: 20
        amount-min: 1
        amount-max: 3
```

---

## 🔧 Testing

### Enable Debug Mode

```
/sq debug
```

Shows detailed console output for all loot generation.

### Test Configuration

Set chances to 100% temporarily:

```yaml
chest:
  chance: 100
mobs:
  types:
    ZOMBIE:
      chance: 100
```

### Reload Configuration

```
/sq reload
```

---

## ⚠️ Troubleshooting

### No Quests Dropping

**Check:**
- `enabled: true` in quest-loot.yml
- World is in `worlds:` list
- Console for errors
- `/sq debug` for detailed logging

**Solution:**
```
/sq reload
/sq debug
```

### Adjust Drop Rates

**Too common:**
```yaml
chance: 5  # Decrease
```

**Too rare:**
```yaml
chance: 25  # Increase
```

### Random Mode Not Working

**Check:**
- `random-generator.yml` is configured
- `enabled: true` in random-generator.yml
- Console for generation errors

---

**[← Back to README](README.md)** | **[Configuration →](CONFIGURATION.md)**
