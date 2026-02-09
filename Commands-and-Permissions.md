# Commands and Permissions

All commands use `/soapsquest` or the shorthand `/sq`.

---

## Commands

### General

| Command | Description | Permission |
|:--------|:-----------|:-----------|
| `/sq help` | Show all available commands | None |
| `/sq info` | Show plugin version and info | None |
| `/sq reload` | Reload all configuration files | `soapsquest.reload` |
| `/sq debug toggle` | Toggle debug logging on/off | `soapsquest.debug` |

### Quest Management

| Command | Description | Permission |
|:--------|:-----------|:-----------|
| `/sq give <player> <quest>` | Give a quest paper to a player | `soapsquest.give` |
| `/sq list [page]` | List all quests (paginated, 10 per page) | `soapsquest.list` |
| `/sq browse` | Open the quest browser GUI | `soapsquest.gui.browser` |
| `/sq editor [quest]` | Open the quest editor GUI | `soapsquest.gui.editor` |
| `/sq remove <quest>` | Permanently delete a quest from the config | `soapsquest.remove` |
| `/sq abandon <quest>` | Abandon a quest you're holding | `soapsquest.abandon` |

### Random Generation

| Command | Description | Permission |
|:--------|:-----------|:-----------|
| `/sq generate` | Generate a random quest | `soapsquest.generate` |
| `/sq generate <type>` | Generate a specific type (`single`, `multi`, `sequence`) | `soapsquest.generate` |
| `/sq generate <type> <count>` | Generate multiple quests at once (max 25) | `soapsquest.generate` |

### Rewards

| Command | Description | Permission |
|:--------|:-----------|:-----------|
| `/sq addreward <quest> item` | Add held item as reward (type `HAND` in chat) | `soapsquest.addreward` |
| `/sq addreward <quest> xp <amount>` | Add XP reward | `soapsquest.addreward` |
| `/sq addreward <quest> money <amount>` | Add money reward | `soapsquest.addreward` |
| `/sq addreward <quest> command <cmd>` | Add command reward | `soapsquest.addreward` |
| `/sq removereward <quest> <index>` | Remove a reward by index | `soapsquest.removereward` |
| `/sq listreward <quest>` | List all rewards on a quest | `soapsquest.listreward` |

### Statistics

| Command | Description | Permission |
|:--------|:-----------|:-----------|
| `/sq statistic` | View your own quest stats | `soapsquest.statistic` |
| `/sq statistic <player>` | View another player's stats | `soapsquest.admin` |

---

## Command Aliases

| Full Command | Aliases |
|:-------------|:--------|
| `/soapsquest` | `/sq` |
| `/sq browse` | `/sq browser`, `/sq gui` |
| `/sq editor` | `/sq edit` |
| `/sq statistic` | `/sq statistics`, `/sq stats` |

---

## Permissions

### Player Permissions

These are safe to give to regular players:

| Permission | Description | Default |
|:-----------|:-----------|:--------|
| `soapsquest.use` | Basic permission to use quest commands | ✅ Everyone |
| `soapsquest.list` | View the quest list | ✅ Everyone |
| `soapsquest.list.click` | Click quests in `/sq list` to claim them | ✅ Everyone |
| `soapsquest.gui.browser` | Open the quest browser GUI | ✅ Everyone |
| `soapsquest.progress` | Check your own quest progress | ✅ Everyone |
| `soapsquest.statistic` | View your own statistics | ✅ Everyone |
| `soapsquest.abandon` | Abandon quests | ✅ Everyone |

### Admin Permissions

These should only be given to staff:

| Permission | Description | Default |
|:-----------|:-----------|:--------|
| `soapsquest.give` | Give quest papers to players | OP |
| `soapsquest.generate` | Generate random quests | OP |
| `soapsquest.reload` | Reload configuration | OP |
| `soapsquest.gui.editor` | Open the quest editor GUI | OP |
| `soapsquest.addreward` | Add rewards to quests | OP |
| `soapsquest.removereward` | Remove rewards from quests | OP |
| `soapsquest.listreward` | List rewards on quests | OP |
| `soapsquest.remove` | Remove quests from config | OP |
| `soapsquest.progress.others` | Check other players' progress | OP |
| `soapsquest.admin` | View other players' stats, admin features | OP |
| `soapsquest.debug` | Toggle debug mode | OP |

### Wildcard

| Permission | Description |
|:-----------|:-----------|
| `soapsquest.*` | Grants all SoapsQuest permissions |

---

## Per-Quest Permissions

You can require a specific permission to use individual quests by adding a `permission` condition:

```yaml
my_quest:
  conditions:
    permission: "quest.vip.exclusive"
```

Players without `quest.vip.exclusive` won't be able to activate or progress that quest.

---

## Next Steps

- [Creating Quests](Creating-Quests) - Build your first quest
- [Rewards](Rewards) - All reward types explained
