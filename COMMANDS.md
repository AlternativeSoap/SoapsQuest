# Commands

Complete command reference for SoapsQuest.

---

## 📋 Player Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/sq` | `soapsquest.use` | Show main help menu |
| `/sq list [page]` | `soapsquest.list` | View all quests (click to accept) |
| `/sq browse` | `soapsquest.use` | Open quest browser GUI |
| `/sq statistics [player]` | `soapsquest.use` | View quest completion stats |
| `/sq leaderboard [page]` | `soapsquest.use` | View top quest completers |
| `/sq help` | `soapsquest.use` | Show command help |
| `/sq info` | `soapsquest.use` | Show plugin information |

---

## 🔧 Admin Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/sq give <player> <quest>` | `soapsquest.give` | Give quest paper to player |
| `/sq editor [quest]` | `soapsquest.editor` | Open quest editor GUI |
| `/sq generate [type]` | `soapsquest.generate` | Generate random quest |
| `/sq reload` | `soapsquest.reload` | Reload all configuration files |
| `/sq debug` | `soapsquest.debug` | Toggle debug mode |
| `/sq remove <questId>` | `soapsquest.remove` | Delete a quest permanently |
| `/sq addreward <quest> <type> [args]` | `soapsquest.addreward` | Add reward to quest |
| `/sq removereward <quest> <index>` | `soapsquest.removereward` | Remove reward from quest |
| `/sq listreward <quest>` | `soapsquest.admin` | List all quest rewards |

---

## 📖 Command Details

## 📖 Command Details

### `/sq list [page]`

View all available quests with pagination (10 per page).

- Click quest names to accept
- Hover for quest details
- Respects permission conditions

**Examples:**
```
/sq list
/sq list 2
```

---

### `/sq browse`

Open the interactive quest browser GUI.

- Visual quest browsing
- Filter by tier and difficulty
- Click to view details and accept

**Example:**
```
/sq browse
```

---

### `/sq statistics [player]`

View quest completion statistics.

- Total quests completed
- Tier breakdowns
- Difficulty breakdowns

**Examples:**
```
/sq statistics
/sq statistics Notch
```

---

### `/sq leaderboard [page]`

View the global quest leaderboard.

**Examples:**
```
/sq leaderboard
/sq leaderboard 2
```

---

### `/sq give <player> <quest>`

Give a quest paper to a player.

**Permission:** `soapsquest.give`

**Example:**
```
/sq give Steve zombie_slayer
```

---

### `/sq editor [quest]`

Open the quest editor GUI.

- Create new quests
- Edit existing quests
- Configure objectives, rewards, and conditions
- Delete quests

**Permission:** `soapsquest.editor`

**Examples:**
```
/sq editor
/sq editor zombie_slayer
```

---

### `/sq generate [type]`

Generate a random quest.

**Permission:** `soapsquest.generate`

**Types:**
- `single` – One objective
- `multi` – Multiple objectives (any order)
- `sequence` – Sequential objectives

**Examples:**
```
/sq generate
/sq generate single
/sq generate multi
```

Generated quests are saved to `generated.yml`.

---

### `/sq reload`

Reload all configuration files.

**Permission:** `soapsquest.reload`

Reloads:
- `config.yml`
- `messages.yml`
- `quests.yml`
- `random-generator.yml`
- `quest-loot.yml`

**Note:** Active quest progress is preserved.

---

### `/sq debug`

Toggle debug mode for troubleshooting.

**Permission:** `soapsquest.debug`

Enables detailed console logging for:
- Quest events
- Loot generation
- Progress tracking
- System operations

---

### `/sq remove <questId>`

Permanently delete a quest.

**Permission:** `soapsquest.remove`

**Warning:** This action cannot be undone.

**Example:**
```
/sq remove old_quest
```

---

### `/sq addreward <quest> <type> [args]`

Add rewards to an existing quest.

**Permission:** `soapsquest.addreward`

**Types:**
- `item` – Hold item in hand
- `xp <amount>` – XP reward
- `money <amount>` – Money reward (Vault)
- `command <command>` – Command reward

**Examples:**
```
/sq addreward zombie_slayer xp 100
/sq addreward zombie_slayer money 50
/sq addreward zombie_slayer item
/sq addreward zombie_slayer command give {player} diamond 1
```

---

### `/sq removereward <quest> <index>`

Remove a reward from a quest.

**Permission:** `soapsquest.removereward`

Use `/sq listreward <quest>` to see indices.

**Example:**
```
/sq removereward zombie_slayer 2
```

---

### `/sq listreward <quest>`

List all rewards for a quest.

**Permission:** `soapsquest.admin`

**Example:**
```
/sq listreward zombie_slayer
```

---

**[← Back to README](README.md)** | **[Configuration →](CONFIGURATION.md)**
