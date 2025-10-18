# Configuration

Complete configuration guide for SoapsQuest.

---

## Configuration Files

SoapsQuest uses four configuration files in `plugins/SoapsQuest/`:

| File | Purpose |
|------|---------|
| `config.yml` | Core settings, tiers, difficulties, progress display |
| `messages.yml` | All messages and translations |
| `quests.yml` | Quest definitions |
| `random-generator.yml` | Random quest generation settings |

---

## config.yml

### Core Settings

```yaml
# Autosave interval for player progress (minutes)
autosave-interval: 5

# Max attempts to generate random quests
max-generation-retries: 5
```

### Tiers (Rarity)

Fully customizable quest rarity levels:

```yaml
tiers:
  common:
    display: "&fCommon"
    prefix: "&7[COMMON]"
    color: "&f"
    weight: 40        # Random generation weight
  
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

**Add custom tiers** (mythic, divine, etc.) by copying the format.

### Difficulties

Fully customizable difficulty levels with multipliers:

```yaml
difficulties:
  easy:
    display: "&aEasy"
    weight: 50
    multiplier:
      objective-amount: 0.8   # 20% fewer objectives
      reward: 0.8             # 20% fewer rewards
  
  normal:
    display: "&eNormal"
    weight: 35
    multiplier:
      objective-amount: 1.0   # Normal
      reward: 1.0
  
  hard:
    display: "&cHard"
    weight: 15
    multiplier:
      objective-amount: 1.5   # 50% more objectives
      reward: 1.5             # 50% more rewards
  
  nightmare:
    display: "&4&lNightmare"
    weight: 5
    multiplier:
      objective-amount: 2.0   # 2x objectives
      reward: 2.5             # 2.5x rewards
```

**Multipliers scale objectives and rewards** based on difficulty.

### Progress Display

```yaml
progress-display:
  mode: "actionbar"    # actionbar, chat, bossbar, none
  
  # Chat mode: Show every X updates
  interval: 5
  
  # Bossbar mode settings
  bossbar:
    color: "GREEN"             # PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE
    style: "SEGMENTED_10"      # SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20
    duration: 5                # Seconds to display
```

### Milestone Notifications

Sound effect when reaching quest milestones (25%, 50%, 75%):

```yaml
milestone-sound: "ENTITY_PLAYER_LEVELUP"    # Use "none" to disable
milestone-sound-volume: 1.0                 # 0.0 to 1.0
milestone-sound-pitch: 1.0                  # 0.5 to 2.0
```

Configure per-quest milestones in `quests.yml`:
```yaml
milestones: [25, 50, 75]
```

---

## messages.yml

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

Messages auto-regenerate if deleted.

---

## quests.yml

See [QUEST-CREATION.md](QUEST-CREATION.md) for detailed examples.

### Basic Quest Structure

```yaml
quest_id:
  display: "&aQuest Name"
  tier: common
  difficulty: easy
  objectives:
    - type: kill
      entity: ZOMBIE
      amount: 10
  reward:
    xp: 100
    money: 50
  conditions:
    min-level: 10
```

### Quest Paper Customization

```yaml
quest_paper:
  material: PAPER
  name: "&e{quest_name}"
  lore:
    - "&7Tier: {tier}"
    - "&7Difficulty: {difficulty}"
  enchantments:
    - "DURABILITY:1"
  hide-enchants: true
```

---

## random-generator.yml

See [RANDOM-GENERATOR.md](RANDOM-GENERATOR.md) for full documentation.

```yaml
generator:
  enabled: true
  objectives:
    kill:
      entities: [ZOMBIE, SKELETON, CREEPER]
      min-amount: 5
      max-amount: 50
  rewards:
    xp:
      min: 50
      max: 500
```

---

## Color Codes

| Code | Color | Code | Format |
|------|-------|------|--------|
| `&0` | Black | `&l` | **Bold** |
| `&1` | Dark Blue | `&m` | ~~Strike~~ |
| `&2` | Dark Green | `&n` | Underline |
| `&3` | Dark Aqua | `&o` | *Italic* |
| `&4` | Dark Red | `&r` | Reset |
| `&5` | Dark Purple | | |
| `&6` | Gold | | |
| `&7` | Gray | | |
| `&8` | Dark Gray | | |
| `&9` | Blue | | |
| `&a` | Green | | |
| `&b` | Aqua | | |
| `&c` | Red | | |
| `&d` | Light Purple | | |
| `&e` | Yellow | | |
| `&f` | White | | |

**Hex colors (1.16+)**: `&#RRGGBB`

---

**[← Back to README](README.md)** | **[Quest Creation →](QUEST-CREATION.md)**
