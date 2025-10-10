# 📝 Configuration Guide

Complete guide to configuring SoapsQuest for your server.

---

## Table of Contents

1. [Config Files Overview](#config-files-overview)
2. [config.yml](#configyml)
3. [messages.yml](#messagesyml)
4. [quests.yml](#questsyml)
5. [random-generator.yml](#random-generatoryml)

---

## Config Files Overview

SoapsQuest uses four main configuration files located in `plugins/SoapsQuest/`:

| File | Purpose |
|------|---------|
| `config.yml` | Main plugin settings, tiers, difficulties, progress display |
| `messages.yml` | All player-facing messages and translations |
| `quests.yml` | Quest definitions with objectives, rewards, and conditions |
| `random-generator.yml` | Random quest generation settings |

---

## config.yml

### Basic Settings

```yaml
# Plugin Settings
settings:
  # Auto-save interval in minutes (0 to disable)
  autosave-interval: 5
  
  # Debug mode for troubleshooting
  debug: false
  
  # Language file to use
  language: "en_US"
```

### Tier Configuration

Define quest rarity tiers with custom prefixes and colors:

```yaml
tiers:
  common:
    prefix: "&7[Common]"
    color: "&7"
    
  rare:
    prefix: "&9[Rare]"
    color: "&9"
    
  epic:
    prefix: "&5[Epic]"
    color: "&5"
    
  legendary:
    prefix: "&6[Legendary]"
    color: "&6"
```

**Add Custom Tiers:**
```yaml
tiers:
  mythic:
    prefix: "&c&l[MYTHIC]"
    color: "&c"
```

### Difficulty Configuration

Define quest difficulty levels with colors:

```yaml
difficulties:
  easy:
    color: "&a"
    
  normal:
    color: "&e"
    
  hard:
    color: "&c"
    
  nightmare:
    color: "&4"
```

**Add Custom Difficulties:**
```yaml
difficulties:
  impossible:
    color: "&4&l"
```

### Progress Display

Configure how quest progress is shown to players:

```yaml
progress:
  # Display type: BOSSBAR, ACTIONBAR, or CHAT
  type: BOSSBAR
  
  # BossBar settings (if type is BOSSBAR)
  bossbar:
    color: YELLOW
    style: PROGRESS
    
  # Update frequency in ticks (20 ticks = 1 second)
  update-interval: 20
```

**BossBar Colors:**
- `BLUE`, `GREEN`, `PINK`, `PURPLE`, `RED`, `WHITE`, `YELLOW`

**BossBar Styles:**
- `PROGRESS` - Solid bar
- `NOTCHED_6` - 6 notches
- `NOTCHED_10` - 10 notches
- `NOTCHED_12` - 12 notches
- `NOTCHED_20` - 20 notches

### Milestone Notifications

Alert players at specific progress percentages:

```yaml
milestones:
  enabled: true
  percentages:
    - 25
    - 50
    - 75
```

### Quest Limits

Control how many quests players can have active:

```yaml
limits:
  # Max active quests per player (-1 for unlimited)
  max-active-quests: 5
  
  # Max quests in history per player
  max-quest-history: 100
```

---

## messages.yml

### Message Structure

All messages support color codes (`&a`, `&b`, etc.) and placeholders:

```yaml
messages:
  quest-accepted: "&aYou have accepted quest: &e{quest_name}"
  quest-completed: "&6✓ Quest Complete: &e{quest_name}"
  quest-abandoned: "&cYou have abandoned: &e{quest_name}"
```

### Available Placeholders

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `{quest_name}` | Quest display name | "Kill 10 Zombies" |
| `{quest_id}` | Quest identifier | "zombie_slayer" |
| `{tier}` | Quest tier | "Rare" |
| `{difficulty}` | Quest difficulty | "Hard" |
| `{progress}` | Current progress | "7" |
| `{required}` | Required amount | "10" |
| `{percentage}` | Progress percentage | "70" |
| `{player}` | Player name | "Steve" |
| `{objective_type}` | Objective type | "kill" |

### Message Categories

**Quest Actions:**
```yaml
quest-accepted: "&aQuest accepted: &e{quest_name}"
quest-completed: "&6✓ Completed: &e{quest_name}"
quest-abandoned: "&cAbandoned: &e{quest_name}"
quest-already-active: "&cYou already have this quest active!"
quest-already-completed: "&cYou have already completed this quest!"
```

**Progress Messages:**
```yaml
progress-update: "&e{quest_name} &7- &a{progress}/{required}"
milestone-reached: "&6Milestone! &e{percentage}% &7complete"
objective-completed: "&a✓ Objective complete!"
```

**Error Messages:**
```yaml
quest-not-found: "&cQuest not found: &e{quest_id}"
quest-limit-reached: "&cYou have reached the maximum active quest limit!"
conditions-not-met: "&cYou do not meet the requirements for this quest."
insufficient-funds: "&cYou need &e{cost} &cto unlock this quest!"
```

---

## quests.yml

See [QUEST-CREATION.md](QUEST-CREATION.md) for detailed quest configuration examples.

### Basic Quest Structure

```yaml
quest_id:
  # Display name (supports color codes)
  display: "&aQuest Name"
  
  # Quest tier (from config.yml)
  tier: common
  
  # Quest difficulty (from config.yml)
  difficulty: easy
  
  # Quest description (optional)
  description:
    - "&7Complete tasks to earn rewards!"
    - "&7Line 2 of description"
  
  # Objectives (list of tasks)
  objectives:
    - type: kill
      entity: ZOMBIE
      amount: 10
  
  # Rewards
  reward:
    xp: 100
    money: 50
  
  # Conditions (optional)
  conditions:
    min-level: 10
```

### Quest Paper Customization

Customize the physical quest item:

```yaml
quest_paper:
  material: PAPER
  name: "&e{quest_name}"
  lore:
    - "&7Tier: {tier}"
    - "&7Difficulty: {difficulty}"
    - ""
    - "&eRight-click to complete!"
  enchantments:
    - "DURABILITY:1"
  hide-enchants: true
```

---

## random-generator.yml

Configure random quest generation (if using `/sq generate`):

```yaml
generator:
  # Enable random quest generation
  enabled: true
  
  # Objective pools
  objectives:
    kill:
      entities:
        - ZOMBIE
        - SKELETON
        - CREEPER
      min-amount: 5
      max-amount: 50
      
    break_block:
      blocks:
        - STONE
        - DIRT
        - OAK_LOG
      min-amount: 10
      max-amount: 100
  
  # Reward ranges
  rewards:
    xp:
      min: 50
      max: 500
    money:
      min: 10
      max: 200
```

---

## Color Codes Reference

### Standard Minecraft Color Codes

| Code | Color | Code | Format |
|------|-------|------|--------|
| `&0` | Black | `&k` | Obfuscated |
| `&1` | Dark Blue | `&l` | **Bold** |
| `&2` | Dark Green | `&m` | ~~Strikethrough~~ |
| `&3` | Dark Aqua | `&n` | Underline |
| `&4` | Dark Red | `&o` | *Italic* |
| `&5` | Dark Purple | `&r` | Reset |
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

### Hex Colors (1.16+)

Use hex colors with `&#RRGGBB` format:
```yaml
display: "&#FF5733My Quest"
```

---

## Best Practices

### Performance

1. **Autosave Interval**: 5-10 minutes for most servers
2. **Progress Updates**: Default (20 ticks) is optimal
3. **Quest Limits**: Set reasonable limits (5-10 active quests)

### Customization

1. **Color Consistency**: Use consistent colors for tiers/difficulties
2. **Clear Messages**: Keep messages concise and informative
3. **Balanced Rewards**: Scale rewards with difficulty

### Localization

1. **Multiple Languages**: Copy `messages.yml` to `messages_es.yml` for Spanish, etc.
2. **Character Support**: Use UTF-8 encoding for special characters
3. **Testing**: Test all messages in-game before deploying

---

## Troubleshooting

### Config Not Loading

1. Check for YAML syntax errors (use [YAML validator](https://www.yamllint.com/))
2. Ensure proper indentation (2 spaces, no tabs)
3. Check server console for error messages

### Messages Not Showing

1. Verify placeholder names are correct
2. Check color code formatting
3. Ensure message key exists in `messages.yml`

### Quests Not Working

1. Validate quest structure in `quests.yml`
2. Check objective types are valid
3. Verify material/entity names are correct (uppercase)

---

## Example Configurations

### Minimal Setup

```yaml
# config.yml
settings:
  autosave-interval: 5

tiers:
  common:
    prefix: "&7[Common]"
    color: "&7"

difficulties:
  easy:
    color: "&a"

progress:
  type: BOSSBAR
```

### Advanced Setup

```yaml
# config.yml
settings:
  autosave-interval: 5
  debug: false

tiers:
  common:
    prefix: "&7[Common]"
    color: "&7"
  rare:
    prefix: "&9[Rare]"
    color: "&9"
  epic:
    prefix: "&5[Epic]"
    color: "&5"
  legendary:
    prefix: "&6[Legendary]"
    color: "&6"

difficulties:
  easy:
    color: "&a"
  normal:
    color: "&e"
  hard:
    color: "&c"
  nightmare:
    color: "&4"

progress:
  type: BOSSBAR
  bossbar:
    color: YELLOW
    style: PROGRESS
  update-interval: 20

milestones:
  enabled: true
  percentages: [25, 50, 75]

limits:
  max-active-quests: 5
  max-quest-history: 100
```

---

## Need Help?

- **Discord**: [discord.gg/soapsuniverse](https://discord.gg/soapsuniverse)
- **Issues**: [GitHub Issues](https://github.com/AlternativeSoap/SoapsQuest/issues)
- **Wiki**: [GitHub Wiki](https://github.com/AlternativeSoap/SoapsQuest/wiki)

---

[← Back to README](README.md) | [Quest Creation Guide →](QUEST-CREATION.md)
