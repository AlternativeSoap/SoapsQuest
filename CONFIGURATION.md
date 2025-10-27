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
| `random-generator.yml` | Random quest generation (see [RANDOM-GENERATOR.md](RANDOM-GENERATOR.md)) |
| `quest-loot.yml` | Chest loot and mob drops |
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

Configure quest drops from chests and mobs:

```yaml
quest-loot:
  enabled: true
  
  chest:
    enabled: true
    chance: 10              # 10% chance per chest
    amount-min: 1
    amount-max: 2
    worlds: ["world", "world_nether"]
    source-mode: "mixed"    # manual | random | mixed
    quests:
      - "starter_quest"
      - "rare_treasure"
  
  mobs:
    enabled: true
    default-chance: 5
    worlds: ["world"]
    types:
      ZOMBIE:
        chance: 12
        amount-min: 1
        amount-max: 2
      ENDER_DRAGON:
        chance: 100
        amount-min: 3
        amount-max: 6
```

**Source Modes:**
- `manual` → Use specific quests from config
- `random` → Generate new random quests
- `mixed` → 50/50 mix

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
