# Commands and Permissions

Main command: `/soapsquest` (alias `/sq`). Both are identical.

**Important:** `plugin.yml` lists `progress` in the usage string, but there is **no** `/sq progress` subcommand. View progress on quest paper lore or with `/sq active`. Use `soapsquest.progress.others` for `/sq active <player>`.

---

## Player commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/sq help` | `soapsquest.use` | Command list |
| `/sq info` | `soapsquest.use` | Version and edition (Free/Premium) |
| `/sq browse` | `soapsquest.gui.browser` | Quest browser GUI |
| `/sq gui` | `soapsquest.gui.browser` | Same as browse |
| `/sq active` | `soapsquest.gui.myquests` | Your active quests GUI |
| `/sq active <player>` | `soapsquest.progress.others` | Another player's active quests |
| `/sq myquests` | `soapsquest.gui.myquests` | Alias for active |
| `/sq quests` | `soapsquest.gui.myquests` | Alias for active |
| `/sq list` | `soapsquest.list` | Paginated quest ID list |
| `/sq list <page>` | `soapsquest.list` | Specific page |
| `/sq statistic` | `soapsquest.statistic` | Your completion stats |
| `/sq abandon <questid>` | `soapsquest.abandon` | Remove a quest paper you hold |

`/sq abandon` has no extra permission check in code beyond being a player command.

---

## Admin commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/sq reload` | `soapsquest.reload` | Reload configs (validates `quests.yml` first) |
| `/sq give [player] <questid>` | `soapsquest.give` | Give quest paper |
| `/sq remove <questid>` | `soapsquest.remove` | Remove quest from config |
| `/sq copy <source> <newid>` | `soapsquest.admin` | Duplicate a quest |
| `/sq reset <player> <questid>` | `soapsquest.admin` | Reset progress on held paper |
| `/sq complete <player> <questid>` | `soapsquest.admin` | Force-complete held paper |
| `/sq debug toggle` | `soapsquest.debug` | Toggle debug logging |
| `/sq statistic <player>` | `soapsquest.admin` | View another player's stats |

### Reward commands

| Command | Permission |
|---------|------------|
| `/sq addreward <quest> xp <amount>` | `soapsquest.addreward` |
| `/sq addreward <quest> money <amount>` | `soapsquest.addreward` |
| `/sq addreward <quest> sigils <amount>` | `soapsquest.addreward` (Premium) |
| `/sq addreward <quest> command <cmd>` | `soapsquest.addreward` |
| `/sq addreward <quest> item` | `soapsquest.addreward` (held item) |
| `/sq listreward <quest>` | `soapsquest.listreward` |
| `/sq removereward <quest> <index>` | `soapsquest.removereward` |

### Premium commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/sq editor [questid]` | `soapsquest.gui.editor` | In-game quest editor |
| `/sq generate [type] [count]` | `soapsquest.generate` | Random quest generator |
| `/sq sigils balance <player>` | `soapsquest.sigils` | Show Sigil balance |
| `/sq sigils give\|take\|set <player> <amount>` | `soapsquest.sigils` | Modify Sigils |
| `/sq sigils reset <player>` | `soapsquest.sigils` | Reset Sigils to 0 |
| `/sq drop <questid> [x y z [world]]` | `soapsquest.drop` | Drop unbound paper |
| `/sq drop entity <entity\|uuid> <questid>` | `soapsquest.drop` | Drop at entity |

Generator cooldown bypass: `soapsquest.generate.bypass-cooldown`

---

## Permissions reference

### Player defaults

| Permission | Description | Default |
|------------|-------------|---------|
| `soapsquest.use` | Base command access | Everyone |
| `soapsquest.gui.browser` | Quest browser | Everyone |
| `soapsquest.gui.myquests` | Active quests GUI | Everyone |
| `soapsquest.list` | Quest list command | Everyone |
| `soapsquest.list.click` | Click quests in `/sq list` output | Everyone |
| `soapsquest.statistic` | Own statistics | Everyone |
| `soapsquest.progress` | Documented for own progress (no `/sq progress` command) | Everyone |
| `soapsquest.abandon` | Abandon quests | Everyone |

### Staff defaults

| Permission | Description | Default |
|------------|-------------|---------|
| `soapsquest.admin` | Copy, reset, complete, others' statistics | OP |
| `soapsquest.give` | Give papers | OP |
| `soapsquest.reload` | Reload | OP |
| `soapsquest.remove` | Remove quests | OP |
| `soapsquest.copy` | Listed in plugin.yml (copy uses `admin` in code) | OP |
| `soapsquest.reset` | Listed in plugin.yml (reset uses `admin` in code) | OP |
| `soapsquest.complete` | Listed in plugin.yml (complete uses `admin` in code) | OP |
| `soapsquest.addreward` | Add rewards via command | OP |
| `soapsquest.removereward` | Remove rewards | OP |
| `soapsquest.listreward` | List rewards | OP |
| `soapsquest.progress.others` | `/sq active <player>` | OP |
| `soapsquest.debug` | Debug toggle | OP |
| `soapsquest.gui.editor` | Editor GUI (Premium) | OP |
| `soapsquest.generate` | Generator (Premium) | OP |
| `soapsquest.sigils` | Sigil admin (Premium) | OP |
| `soapsquest.drop` | World drop (Premium) | OP |

### Wildcard

`soapsquest.*` grants all nodes above.

---

## Per-quest permission

Use `conditions.permission` on a quest to lock browser pickup:

```yaml
conditions:
  permission: "rank.vip"
```

Root-level `permission:` on the quest gates progress after pickup. See [Conditions](Conditions.md).

---

*Version 1.0.3*
