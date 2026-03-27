# Commands and Permissions

This page lists every command in SoapsQuest, what it does, and who can use it.

The main command is `/soapsquest`. You can also use `/sq` as a shortcut. Both work the same.

---

## Player Commands

These commands are available to all players by default.

| Command | What It Does |
|---------|-------------|
| `/sq browse` | Opens the quest browser GUI so players can see and pick up available quests |
| `/sq stats` | Shows how many quests you have completed |
| `/sq stats <player>` | Shows how many quests another player has completed |

---

## Admin Commands

These commands require operator level or the specific permission listed.

### General Management

| Command | Permission | What It Does |
|---------|-----------|-------------|
| `/sq reload` | `soapsquest.admin` | Reloads all config files without restarting the server |
| `/sq list` | `soapsquest.admin` | Lists all quest IDs currently loaded |
| `/sq give <player> <questid>` | `soapsquest.admin` | Gives a quest paper to a player |
| `/sq take <player> <questid>` | `soapsquest.admin` | Removes an active quest from a player |
| `/sq complete <player> <questid>` | `soapsquest.admin` | Marks a quest as complete for a player and gives the rewards |
| `/sq reset <player> <questid>` | `soapsquest.admin` | Resets quest progress for a player |
| `/sq editor` | `soapsquest.gui.editor` | Opens the in-game quest editor GUI **(Premium)** |

### Quest Reward Commands

| Command | Permission | What It Does |
|---------|-----------|-------------|
| `/sq reward add <questid> xp <amount>` | `soapsquest.admin` | Adds an XP reward to a quest |
| `/sq reward add <questid> money <amount>` | `soapsquest.admin` | Adds a money reward to a quest |
| `/sq reward add <questid> command <cmd>` | `soapsquest.admin` | Adds a command reward to a quest |
| `/sq reward list <questid>` | `soapsquest.admin` | Lists all rewards on a quest |
| `/sq reward remove <questid> <index>` | `soapsquest.admin` | Removes a reward from a quest by its number in the list |

### Random Quest Generator (Premium)

> **[PREMIUM]** These commands require the SoapsQuest Premium version.

| Command | Permission | What It Does |
|---------|-----------|-------------|
| `/sq generate` | `soapsquest.generate` | Generates a single random quest |
| `/sq generate <type>` | `soapsquest.generate` | Generates a random quest of a specific type (kill, mine, fish, etc.) |
| `/sq generate <type> <count>` | `soapsquest.generate` | Generates multiple random quests at once |

---

## Command Aliases

You can use any of these instead of `/soapsquest`:

| Alias | Same As |
|-------|---------|
| `/sq` | `/soapsquest` |
| `/squest` | `/soapsquest` |
| `/quests` | `/soapsquest` |

---

## Permissions Reference

### Player Permissions

| Permission | What It Allows | Default |
|-----------|---------------|---------|
| `soapsquest.browse` | Open the quest browser GUI | Everyone |
| `soapsquest.stats` | View your own quest statistics | Everyone |
| `soapsquest.stats.others` | View other players quest statistics | Everyone |

### Admin Permissions

| Permission | What It Allows | Default |
|-----------|---------------|---------|
| `soapsquest.admin` | Access to all admin commands | OP only |
| `soapsquest.gui.editor` | Open the in-game quest editor **(Premium)** | OP only |
| `soapsquest.generate` | Use the random quest generator commands **(Premium)** | OP only |
| `soapsquest.reload` | Use the reload command | OP only |

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
