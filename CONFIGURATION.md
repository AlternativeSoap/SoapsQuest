# Configuration

Complete configuration guide for SoapsQuest.

---

## 📁 Configuration Files

SoapsQuest uses five configuration files located in `plugins/SoapsQuest/`:

| File | Purpose |
|------|---------|
| `config.yml` | Core settings, tiers, difficulties, and progress display |
| `messages.yml` | All messages and translations |
| `quests.yml` | Quest definitions (see [QUEST-CREATION.md](QUEST-CREATION.md)) |
| `random-generator.yml` | Random quest generation (see [QUEST-CREATION.md](QUEST-CREATION.md#-random-quest-generation)) |
| `quest-loot.yml` | Chest loot and mob drops (see below) |
| `gui.yml` | GUI customization |

---

## ⚙️ config.yml

### Core Settings

```yaml
# Autosave interval for player progress (minutes)
autosave-interval: 5

# Max attempts when generating random quests
max-generation-retries: 5
```

- `autosave-interval` → Saves player progress every X minutes
- `max-generation-retries` → Prevents infinite loops during generation

---

### Structured Logging

Configure JSON-formatted logs for quest events (v1.0.2+):

```yaml
logging:
  structured: true              # Enable JSON-formatted logs
  log-completions: true         # Log quest completions
  log-acceptances: true         # Log quest acceptances
  log-generations: true         # Log random quest generations
  log-edits: false              # Log quest edits (can be verbose)
```

**Benefits:**
- Easy to parse with external tools
- Human-readable JSON format
- Track quest analytics and player progress
- Monitor quest generation patterns

**Example Log Output:**
```json
[SoapsQuest] QuestCompleted {
  "player": "Soap_",
  "quest": "zombie_hunter",
  "tier": "epic",
  "rewards": ["xp:250", "money:500"],
  "timestamp": "2025-11-05T17:30:14Z"
}
```

---

### Quest Tiers (Rarity)

Define custom rarity levels with weights for random generation:

```yaml
tiers:
  common:
    display: "&fCommon"
    prefix: "&7[COMMON]"
    color: "&f"
    weight: 40
  
  rare:
    display: "&9Rare"
    prefix: "&9[RARE]"
    color: "&9"
    weight: 30
  
  epic:
    display: "&5Epic"
    prefix: "&5[EPIC]"
    color: "&5"
    weight: 20
  
  legendary:
    display: "&6Legendary"
    prefix: "&6[LEGENDARY]"
    color: "&6"
    weight: 10
```

**Fields:**
- `display` → Tier name shown to players
- `prefix` → Prefix in quest names
- `color` → Default color code
- `weight` → Random generation probability (higher = more common)

**Add custom tiers** by copying the format (e.g., `mythic`, `divine`).

---

### Difficulty Levels

Define difficulty levels with multipliers that scale objectives and rewards:

```yaml
difficulties:
  easy:
    display: "&aEasy"
    weight: 50
    multiplier:
      objective-amount: 0.8    # 20% fewer objectives
      reward: 0.8              # 20% fewer rewards
  
  normal:
    display: "&eNormal"
    weight: 35
    multiplier:
      objective-amount: 1.0
      reward: 1.0
  
  hard:
    display: "&cHard"
    weight: 15
    multiplier:
      objective-amount: 1.5    # 50% more objectives
      reward: 1.5              # 50% more rewards
  
  nightmare:
    display: "&4&lNightmare"
    weight: 5
    multiplier:
      objective-amount: 2.0    # 2x objectives
      reward: 2.5              # 2.5x rewards
```

**Fields:**
- `display` → Difficulty name
- `weight` → Random generation probability
- `multiplier.objective-amount` → Scales objective requirements
- `multiplier.reward` → Scales rewards

---

### Progress Display

Choose how players see quest progress:

```yaml
progress-display:
  mode: "actionbar"    # Options: actionbar, chat, bossbar, none
  
  # Chat mode: Show every X updates
  interval: 5
  
  # Bossbar mode settings
  bossbar:
    color: "GREEN"             # PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE
    style: "SEGMENTED_10"      # SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20
    duration: 5                # Seconds to display
```

**Modes:**
- `actionbar` → Above hotbar (recommended)
- `chat` → Chat messages
- `bossbar` → Boss health bar
- `none` → Disable progress updates

---

### Milestone Notifications

Play sounds when reaching quest milestones:

```yaml
milestone-sound: "ENTITY_PLAYER_LEVELUP"
milestone-sound-volume: 1.0    # 0.0 to 1.0
milestone-sound-pitch: 1.0     # 0.5 to 2.0
```

Set `milestone-sound: "none"` to disable.

Configure per-quest milestones in `quests.yml`:
```yaml
milestones: [25, 50, 75]
```

---

## 💬 messages.yml

All messages support color codes (`&a`, `&b`, etc.) and placeholders.

### Available Placeholders

| Placeholder | Description |
|-------------|-------------|
| `{quest_name}` | Quest display name |
| `{quest_id}` | Quest ID |
| `{tier}` | Quest tier |
| `{difficulty}` | Quest difficulty |
| `{progress}` | Current progress |
| `{required}` | Required amount |
| `{percentage}` | Progress percentage |
| `{player}` | Player name |

### Example Messages

```yaml
quest-received: "&aYou received quest: &e{quest_name}"
quest-completed: "&6✓ Quest Complete: &e{quest_name}"
progress-update: "&e{quest_name} &7- &a{progress}/{required}"
milestone-reached: "&6Milestone! &e{percentage}% complete"
quest-not-found: "&cQuest not found: &e{quest_id}"
no-permission: "&cYou don't have permission!"
```

Messages regenerate automatically if deleted.

---

## 🎨 gui.yml

Customize GUI icons and appearance. See the file for full customization options.

---

## 📦 quest-loot.yml

Quest papers can naturally appear in chest loot and mob drops. Players discover quests while exploring or fighting monsters.

### Overview

**Chest Loot** – Naturally generated chests have a chance to contain quest papers

**Mob Drops** – Mobs can drop quest papers when killed

**Quest Sources:**
- `manual` – Use specific quests from your config
- `random` – Generate new random quests on-the-fly
- `mixed` – 50/50 mix of both

---

### Basic Setup

```yaml
quest-loot:
  enabled: true
  debug-logs: false  # Enable for troubleshooting
```

---

### Chest Loot Configuration

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

### Mob Drops Configuration

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

### Source Modes

**Manual Mode** – Uses specific quests you define:
```yaml
source-mode: "manual"
quests:
  - "zombie_slayer"
  - "treasure_hunter"
```

**Random Mode** – Generates brand new quests using your random generator settings:
```yaml
source-mode: "random"
```

**Mixed Mode** – 50% chance for manual quests, 50% chance for random generation:
```yaml
source-mode: "mixed"
quests:
  - "epic_boss_quest"
  - "legendary_item"
```

---

### World & Structure Filtering

Control which worlds can have quest loot:
```yaml
chest:
  worlds: ["world", "world_nether"]

mobs:
  worlds: ["world"]
```
**Empty list = all worlds allowed**

Only generate quests in specific structures (chests only):
```yaml
chest:
  structures:
    - "minecraft:ancient_city"
    - "minecraft:desert_pyramid"
    - "minecraft:jungle_temple"
    - "minecraft:shipwreck"
```
**Empty list = all structures allowed**

---

### Example Configurations

**Dungeon Treasure:**
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

**Boss Rewards:**
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
```

**Nether Quests:**
```yaml
quest-loot:
  mobs:
    enabled: true
    worlds: ["world_nether"]
    types:
      PIGLIN:
        chance: 10
      BLAZE:
        chance: 15
      GHAST:
        chance: 20
```

---

### Testing & Troubleshooting

**Enable Debug Mode:**
```
/sq debug
```

**Test Configuration** (set chances to 100% temporarily):
```yaml
chest:
  chance: 100
mobs:
  types:
    ZOMBIE:
      chance: 100
```

**Reload Configuration:**
```
/sq reload
```

**If quests aren't dropping:**
- Check `enabled: true` in quest-loot.yml
- Verify world is in `worlds:` list
- Check console for errors
- Use `/sq debug` for detailed logging

---

## 🎨 Color Codes

| Code | Color | Code | Format |
|------|-------|------|--------|
| `&0` | Black | `&l` | **Bold** |
| `&1` | Dark Blue | `&m` | ~~Strike~~ |
| `&2` | Dark Green | `&n` | Underline |
| `&3` | Dark Aqua | `&o` | *Italic* |
| `&4` | Dark Red | `&r` | Reset |
| `&5` | Dark Purple |
| `&6` | Gold |
| `&7` | Gray |
| `&8` | Dark Gray |
| `&9` | Blue |
| `&a` | Green |
| `&b` | Aqua |
| `&c` | Red |
| `&d` | Light Purple |
| `&e` | Yellow |
| `&f` | White |

**Hex colors (1.16+)**: `&#RRGGBB`

---

**[← Back to README](README.md)** | **[Quest Creation →](QUEST-CREATION.md)**

---

Licensed under the MIT License © 2025 AlternativeSoap
