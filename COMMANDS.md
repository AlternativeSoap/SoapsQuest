# Commands

Complete command reference for SoapsQuest.

---

## Main Command

| Command | Permission | Default | Description |
|---------|------------|---------|-------------|
| `/sq` | `soapsquest.use` | ✅ All | Show available commands |

---

## Player Commands

| Command | Permission | Default | Description |
|---------|------------|---------|-------------|
| `/sq list [page]` | `soapsquest.list` | ✅ All | View all quests (click to accept) |
| `/sq browse` | `soapsquest.use` | ✅ All | Open quest browser GUI |
| `/sq statistics [player]` | `soapsquest.use` | ✅ All | View quest statistics |
| `/sq leaderboard [page]` | `soapsquest.use` | ✅ All | View quest leaderboard |
| `/sq info` | `soapsquest.use` | ✅ All | Show plugin information |
| `/sq help` | `soapsquest.use` | ✅ All | Show command help |

---

## Admin Commands

| Command | Permission | Default | Description |
|---------|------------|---------|-------------|
| `/sq give <player> <quest>` | `soapsquest.give` | ❌ Op | Give quest to player |
| `/sq editor [quest]` | `soapsquest.editor` | ❌ Op | Open quest editor GUI |
| `/sq reload` | `soapsquest.reload` | ❌ Op | Reload all configs |
| `/sq debug` | `soapsquest.debug` | ❌ Op | Toggle debug mode |
| `/sq generate [type]` | `soapsquest.generate` | ❌ Op | Generate random quest |
| `/sq remove <questId>` | `soapsquest.remove` | ❌ Op | Delete a quest |
| `/sq addreward <quest> <type> [args]` | `soapsquest.addreward` | ❌ Op | Add reward to quest |
| `/sq removereward <quest> <index>` | `soapsquest.removereward` | ❌ Op | Remove reward from quest |
| `/sq listreward <quest>` | `soapsquest.admin` | ❌ Op | List quest rewards |

---

## Command Details

### `/sq list [page]`

View all available quests with pagination.

- Click quest names to accept them
- Hover to see details
- 10 quests per page

**Example:**
```
/sq list
/sq list 2
```

### `/sq browse`

Open the interactive quest browser GUI. Browse all quests, view details, and accept quests through a visual interface.

**Aliases:** `browser`, `gui`

**Example:**
```
/sq browse
```

### `/sq statistics [player]`

View quest completion statistics for yourself or another player. Shows total quests completed, tier breakdowns, and difficulty breakdowns.

**Aliases:** `stats`, `statistic`

**Example:**
```
/sq statistics
/sq statistics Notch
```

### `/sq leaderboard [page]`

View the global quest leaderboard showing top players by quest completions.

**Aliases:** `lb`, `top`

**Example:**
```
/sq leaderboard
/sq leaderboard 2
```

### `/sq give <player> <quest>`

Give a quest paper to a player.

**Example:**
```
/sq give Steve zombie_slayer
```

### `/sq editor [quest]`

Open the quest editor GUI. Create new quests or edit existing ones with a visual interface.

**Aliases:** `edit`

**Example:**
```
/sq editor
/sq editor zombie_slayer
```

### `/sq reload`

Reload all configuration files:
- config.yml
- messages.yml  
- quests.yml
- random-generator.yml
- quest-loot.yml

Active quests and player progress are preserved.

### `/sq debug`

Toggle debug mode for troubleshooting. Shows detailed console output for quest events, loot generation, and system operations.

**Example:**
```
/sq debug
```

### `/sq generate [type]`

Generate a random quest (saves to `generated.yml`).

**Types:** `single`, `multi`, `sequence`

**Example:**
```
/sq generate single
/sq generate
```

Requires `random-generator.yml` to be configured.

### `/sq remove <questId>`

Permanently delete a quest from the configuration. Use with caution - this action cannot be undone.

**Example:**
```
/sq remove old_quest
```

### `/sq addreward <quest> <type> [args]`

Add rewards to an existing quest.

**Types:**
- `item` - Hold item in hand, no args needed
- `xp <amount>` - Add XP reward
- `money <amount>` - Add money reward (Vault required)
- `command <command>` - Add command reward

**Example:**
```
/sq addreward zombie_slayer xp 100
/sq addreward zombie_slayer money 50
/sq addreward zombie_slayer item
```

### `/sq removereward <quest> <index>`

Remove a reward from a quest by index.

Use `/sq listreward <quest>` to see indices.

**Example:**
```
/sq removereward zombie_slayer 2
```

### `/sq listreward <quest>`

List all rewards configured for a quest.

**Example:**
```
/sq listreward zombie_slayer
```

---

**[← Back to README](README.md)** | **[Permissions →](PERMISSIONS.md)**
