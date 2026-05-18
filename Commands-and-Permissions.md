# Commands and Permissions

This page lists every command in SoapsQuest, what it does, and who can use it.

The main command is `/soapsquest`. You can also use `/sq` as a shortcut. Both work the same.

---

## Player Commands

These commands are available to all players by default.

| Command | Permission | What It Does |
|---------|-----------|-------------|
| `/sq browse` | `soapsquest.gui.browser` | Opens the quest browser GUI so players can see and pick up available quests |
| `/sq active` | `soapsquest.gui.myquests` | Opens a GUI showing your currently active quests |
| `/sq statistic` | `soapsquest.statistic` | Shows how many quests you have completed |
| `/sq statistic <player>` | `soapsquest.statistic` | Shows how many quests another player has completed |
| `/sq list` | `soapsquest.list` | Lists all quest IDs currently loaded (paginated) |
| `/sq list <page>` | `soapsquest.list` | View a specific page of the quest list |
| `/sq abandon <questid>` | `soapsquest.abandon` | Abandons a quest you are currently carrying and removes the paper |
| `/sq help` | `soapsquest.use` | Shows all available commands |
| `/sq info` | `soapsquest.use` | Shows plugin version and information |

---

## Admin Commands

These commands require operator level or the specific permission listed.

### General Management

| Command | Permission | What It Does |
|---------|-----------|-------------|
| `/sq reload` | `soapsquest.reload` | Reloads all config files without restarting the server. Validates quests.yml before applying. |
| `/sq give <player> <questid>` | `soapsquest.give` | Gives a quest paper to a player |
| `/sq give <questid>` | `soapsquest.give` | Gives a quest paper to yourself |
| `/sq remove <questid>` | `soapsquest.remove` | Permanently removes a quest from the config |
| `/sq copy <questid> <newid>` | `soapsquest.copy` | Duplicates an existing quest under a new ID |
| `/sq reset <player> <questid>` | `soapsquest.reset` | Resets quest progress for a player |
| `/sq complete <player> <questid>` | `soapsquest.complete` | Marks a quest as complete for a player and gives the rewards |
| `/sq debug toggle` | `soapsquest.debug` | Toggles verbose debug logging on or off |
| `/sq editor` | `soapsquest.gui.editor` | Opens the in-game quest editor GUI **(Premium)** |
| `/sq editor <questid>` | `soapsquest.gui.editor` | Opens the editor directly on a specific quest **(Premium)** |

### Quest Reward Commands

| Command | Permission | What It Does |
|---------|-----------|-------------|
| `/sq addreward <questid> xp <amount>` | `soapsquest.addreward` | Adds an XP reward to a quest |
| `/sq addreward <questid> money <amount>` | `soapsquest.addreward` | Adds a money reward to a quest |
| `/sq addreward <questid> command <cmd>` | `soapsquest.addreward` | Adds a command reward to a quest |
| `/sq addreward <questid> item` | `soapsquest.addreward` | Adds the item you are holding as a reward |
| `/sq listreward <questid>` | `soapsquest.listreward` | Lists all rewards on a quest |
| `/sq removereward <questid> <index>` | `soapsquest.removereward` | Removes a reward from a quest by its number in the list |

### Random Quest Generator (Premium)

> **[PREMIUM]** These commands require the SoapsQuest Premium version.

| Command | Permission | What It Does |
|---------|-----------|-------------|
| `/sq generate` | `soapsquest.generate` | Generates a single random quest |
| `/sq generate <type>` | `soapsquest.generate` | Generates a random quest of a specific type (kill, break, fish, etc.) |
| `/sq generate <type> <count>` | `soapsquest.generate` | Generates multiple random quests at once (max configurable, default 25) |

---

## Command Aliases

| Alias | Same As |
|-------|---------|
| `/sq` | `/soapsquest` |

---

## Permissions Reference

### Player Permissions

| Permission | What It Allows | Default |
|-----------|---------------|---------|
| `soapsquest.use` | Basic permission to use quest commands | Everyone |
| `soapsquest.gui.browser` | Open the quest browser GUI | Everyone |
| `soapsquest.gui.myquests` | Open the active quests GUI | Everyone |
| `soapsquest.list` | List all available quests | Everyone |
| `soapsquest.list.click` | Click to claim quests from the `/sq list` output | Everyone |
| `soapsquest.statistic` | View quest statistics | Everyone |
| `soapsquest.progress` | Check your own quest progress | Everyone |
| `soapsquest.abandon` | Abandon an active quest | Everyone |

### Admin Permissions

| Permission | What It Allows | Default |
|-----------|---------------|---------|
| `soapsquest.admin` | Access to admin commands and viewing other players' statistics | OP only |
| `soapsquest.give` | Give quest papers to players | OP only |
| `soapsquest.reload` | Reload plugin configuration | OP only |
| `soapsquest.remove` | Remove quests from configuration | OP only |
| `soapsquest.copy` | Duplicate quests | OP only |
| `soapsquest.reset` | Reset player quest progress | OP only |
| `soapsquest.complete` | Force-complete quests for players | OP only |
| `soapsquest.addreward` | Add rewards to quests via commands | OP only |
| `soapsquest.removereward` | Remove rewards from quests via commands | OP only |
| `soapsquest.listreward` | View reward lists for quests | OP only |
| `soapsquest.progress.others` | Check other players' quest progress | OP only |
| `soapsquest.debug` | Toggle debug mode | OP only |
| `soapsquest.gui.editor` | Open the in-game quest editor **(Premium)** | OP only |
| `soapsquest.generate` | Use the random quest generator commands **(Premium)** | OP only |

### Wildcard

| Permission | What It Gives |
|-----------|--------------|
| `soapsquest.*` | All SoapsQuest permissions at once |

---

## Per-Quest Permissions

You can restrict individual quests to players who have a certain permission. This is done using the `conditions` section in the quest config, not a dedicated permission node.

For example, to make a quest only available to VIP players:

```yaml
my_vip_quest:
  display: "VIP Quest"
  objectives:
    - type: kill
      target: ZOMBIE
      amount: 10
  reward:
    xp: 500
  conditions:
    permission: "rank.vip"
```

Players without the `rank.vip` permission will see the quest as locked in the browser.

See [Conditions](Conditions.md) for more details on restricting quests.
