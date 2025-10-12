# 📝 Commands

Complete command reference for SoapsQuest.

---

## Main Command

### `/soapsquest` (Alias: `/sq`)

**Description**: Main command for SoapsQuest  
**Permission**: `soapsquest.use` (default: true)  
**Usage**: `/sq [subcommand]`

Running `/sq` without arguments shows available commands.

---

## Admin Commands

### `/sq give <player> <quest_id>`

Give a quest paper to a player.

**Permission**: `soapsquest.give` (default: op)  
**Usage**: `/sq give <player> <quest_id>`

**Example**:
```
/sq give Steve zombie_slayer
```

---

### `/sq reload`

Reload all configuration files.

**Permission**: `soapsquest.reload` (default: op)  
**Usage**: `/sq reload`

**Reloads**:
- config.yml
- messages.yml
- quests.yml
- random-generator.yml

**Note**: Active quests and player progress are preserved.

---

### `/sq generate <player> <amount>`

Generate random quests for a player.

**Permission**: `soapsquest.generate` (default: op)  
**Usage**: `/sq generate <player> <amount>`

**Example**:
```
/sq generate Steve 3
```

**Requires**: random-generator.yml configured

---

### `/sq addreward <quest> <type> [args...]`

Add rewards to an existing quest.

**Permission**: `soapsquest.addreward` (default: op)  
**Usage**:
```
/sq addreward <quest> item          (while holding item)
/sq addreward <quest> xp <amount>
/sq addreward <quest> money <amount>
/sq addreward <quest> command <command>
```

**Example**:
```
/sq addreward zombie_slayer xp 100
/sq addreward zombie_slayer money 50
/sq addreward zombie_slayer item
```

---

### `/sq removereward <quest> <index>`

Remove a reward from a quest.

**Permission**: `soapsquest.removereward` (default: op)  
**Usage**: `/sq removereward <quest> <index>`

**Example**:
```
/sq listreward zombie_slayer    (shows indices)
/sq removereward zombie_slayer 2
```

---

### `/sq listreward <quest>`

List all rewards for a quest.

**Permission**: `soapsquest.admin` (default: op)  
**Usage**: `/sq listreward <quest>`

**Example**:
```
/sq listreward zombie_slayer
```

---

## Player Commands

### `/sq list [page]`

View all available quests with interactive tooltips.

**Permission**: `soapsquest.list` (default: true)  
**Usage**: `/sq list [page]`

**Features**:
- Click quest names to accept (requires `soapsquest.list.click`)
- Hover to see objectives, rewards, and requirements
- 10 quests per page with pagination
- Separates regular and generated quests

**Example**:
```
/sq list
/sq list 2
```

---

### `/sq info`

Display plugin information including author, version, and links.

**Permission**: `soapsquest.use` (default: true)  
**Usage**: `/sq info`

**Displays**:
- Plugin version and status
- Author information
- Discord server link
- Website/GitHub link

**Example**:
```
/sq info
```

---

### `/sq help`

---

## Permission Summary

| Command | Permission | Default |
|---------|------------|---------|
| `/sq help` | `soapsquest.use` | ✅ True |
| `/sq info` | `soapsquest.use` | ✅ True |
| `/sq list` | `soapsquest.list` | ✅ True |
| `/sq list` (click) | `soapsquest.list.click` | ✅ True |
| `/sq give` | `soapsquest.give` | ❌ Op |
| `/sq reload` | `soapsquest.reload` | ❌ Op |
| `/sq generate` | `soapsquest.generate` | ❌ Op |
| `/sq addreward` | `soapsquest.addreward` | ❌ Op |
| `/sq removereward` | `soapsquest.removereward` | ❌ Op |
| `/sq listreward` | `soapsquest.admin` | ❌ Op |

See [PERMISSIONS.md](PERMISSIONS.md) for details.

---

**[⬅️ Back to README](README.md)**
