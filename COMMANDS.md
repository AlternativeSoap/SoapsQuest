# 📝 SoapsQuest Commands

Complete command reference for SoapsQuest plugin.

---

## 📋 Table of Contents

- [Main Command](#main-command)
- [Admin Commands](#admin-commands)
- [Player Commands](#player-commands)
- [Command Arguments](#command-arguments)
- [Usage Examples](#usage-examples)

---

## 🎮 Main Command

### `/soapsquest` (Alias: `/sq`)

**Description**: Main command for SoapsQuest plugin  
**Permission**: `soapsquest.help` (default: all players)  
**Usage**: `/sq [subcommand]`

Running `/sq` or `/soapsquest` without arguments displays the help menu.

---

## 👑 Admin Commands

### `/sq give <player> <quest_id>`

**Description**: Give a quest paper to a player  
**Permission**: `soapsquest.give`  
**Usage**: `/sq give Steve zombie_slayer`

**Arguments**:
- `<player>` - Target player name (must be online)
- `<quest_id>` - Quest ID from quests.yml

**Example**:
```
/sq give AlternativeSoap dragon_slayer
```

**Notes**:
- Quest is given as a physical paper item
- Player must have inventory space
- Quest ID must exist in quests.yml

---

### `/sq reload`

**Description**: Reload all configuration files  
**Permission**: `soapsquest.reload`  
**Usage**: `/sq reload`

**Reloads**:
- `config.yml` - Main configuration
- `messages.yml` - All messages
- `quests.yml` - All quest definitions
- `random-generator.yml` - Random quest settings

**Example**:
```
/sq reload
```

**Notes**:
- Active quests remain in progress
- Player data is not affected
- Changes take effect immediately
- Autosave task is safely restarted

---

### `/sq generate <player> <amount>`

**Description**: Generate random quests for a player  
**Permission**: `soapsquest.generate`  
**Usage**: `/sq generate Steve 3`

**Arguments**:
- `<player>` - Target player name (must be online)
- `<amount>` - Number of random quests to generate

**Example**:
```
/sq generate AlternativeSoap 5
```

**Notes**:
- Requires `random-generator.yml` configuration
- Quests are randomly generated based on settings
- Each quest is given as a physical paper
- Player must have sufficient inventory space

---

## 👥 Player Commands

### `/sq list`

**Description**: View all available quests in an interactive list  
**Permission**: `soapsquest.list` (default: all players)  
**Usage**: `/sq list`

**Features**:
- **Clickable Quest Names** - Click to accept quest
- **Hover Tooltips** - Shows quest details:
  - Display name with tier and difficulty colors
  - All objectives with progress requirements
  - Reward summary (XP, money, items)
  - Conditions and requirements
- **Color Coded** - Quests colored by tier
- **Pagination** - Supports large quest lists

**Example**:
```
/sq list
```

**Hover Tooltip Format**:
```
════════════════════════
[Legendary] Dragon Slayer
════════════════════════
Objectives:
▸ Kill 1 ENDER_DRAGON

Rewards:
▸ 5000 XP
▸ $2000
▸ Dragon Wings
▸ Trophy of Victory

Conditions:
▸ Level 50+
▸ 16x Ender Pearl (consumed)
════════════════════════
```

**Permission for Clicking**: `soapsquest.list.click`

---

## 🔧 Command Arguments

### Player Names
- Must be an online player
- Case-insensitive
- No spaces (use in-game name exactly)

### Quest IDs
- Must match quest ID in `quests.yml`
- Case-sensitive
- No spaces (use underscores: `dragon_slayer`)

### Amount
- Positive integer only
- No decimal values
- Typical range: 1-10

---

## 💡 Usage Examples

### Giving Quests

```bash
# Give a common quest
/sq give Steve zombie_slayer

# Give a legendary quest
/sq give AlternativeSoap dragon_slayer

# Give multiple quests (run multiple times)
/sq give Steve quest_1
/sq give Steve quest_2
/sq give Steve quest_3
```

### Generating Random Quests

```bash
# Generate 1 random quest
/sq generate Steve 1

# Generate 5 random quests
/sq generate AlternativeSoap 5

# Generate for multiple players
/sq generate Player1 3
/sq generate Player2 3
/sq generate Player3 3
```

### Reloading Configuration

```bash
# After editing quests.yml
/sq reload

# After editing messages.yml
/sq reload

# After editing config.yml
/sq reload
```

### Viewing Quests

```bash
# Player views all quests
/sq list

# Player clicks on a quest name to accept it
# Player hovers over quest to see details
```

---

## 🎯 Command Workflow

### Admin Workflow: Creating and Distributing Quests

1. **Create Quest** - Edit `quests.yml` to define new quest
2. **Reload Config** - Run `/sq reload` to load changes
3. **Test Quest** - Give yourself the quest: `/sq give YourName quest_id`
4. **Complete Objectives** - Test the quest progression
5. **Verify Rewards** - Complete quest and verify rewards
6. **Distribute** - Give quest to players: `/sq give PlayerName quest_id`

### Player Workflow: Accepting and Completing Quests

1. **View Quests** - Run `/sq list` to see all available quests
2. **Inspect Quest** - Hover over quest name to see details
3. **Accept Quest** - Click quest name to accept it (receives physical paper)
4. **Track Progress** - Complete objectives (BossBar shows progress)
5. **Complete Quest** - Right-click quest paper when all objectives are done
6. **Claim Rewards** - Rewards are automatically given upon completion

---

## 🔒 Permission Requirements

| Command | Permission Node | Default |
|---------|----------------|---------|
| `/sq` | `soapsquest.help` | ✅ All Players |
| `/sq list` | `soapsquest.list` | ✅ All Players |
| `/sq list` (click) | `soapsquest.list.click` | ✅ All Players |
| `/sq give` | `soapsquest.give` | ❌ Op Only |
| `/sq reload` | `soapsquest.reload` | ❌ Op Only |
| `/sq generate` | `soapsquest.generate` | ❌ Op Only |

See [PERMISSIONS.md](PERMISSIONS.md) for complete permission documentation.

---

## ⚠️ Common Issues

### "Player not found"
- Ensure player is online
- Check spelling of player name
- Player name is case-insensitive but must be exact

### "Quest not found"
- Verify quest ID exists in `quests.yml`
- Quest IDs are case-sensitive
- Run `/sq reload` after editing quests.yml

### "Player inventory is full"
- Player needs at least 1 free inventory slot
- Ask player to clear inventory space
- Quest paper will not be given if inventory is full

### "You don't have permission"
- Check player has required permission node
- Verify permission plugin is working
- Default permissions should work for players

---

## 📖 Related Documentation

- [**PERMISSIONS.md**](PERMISSIONS.md) - Complete permission reference
- [**README.md**](README.md) - Plugin overview and features
- [**CHANGELOG.md**](CHANGELOG.md) - Version history

---

## 💬 Support

Need help with commands?

- **Discord**: [discord.gg/soapsuniverse](https://discord.gg/soapsuniverse)
- **Issues**: [GitHub Issues](https://github.com/AlternativeSoap/SoapsQuest/issues)
- **Wiki**: [GitHub Wiki](https://github.com/AlternativeSoap/SoapsQuest/wiki)

---

<div align="center">

**[⬅️ Back to README](README.md)**

</div>
