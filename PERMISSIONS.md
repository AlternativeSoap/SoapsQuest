# Permissions

Permission reference for SoapsQuest.

---

## Permission Nodes

### Player Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `soapsquest.use` | ✅ All | Basic command access |
| `soapsquest.list` | ✅ All | View quest list |
| `soapsquest.list.click` | ✅ All | Click to accept quests from list |
| `soapsquest.browse` | ✅ All | Open quest browser GUI |
| `soapsquest.statistics` | ✅ All | View quest statistics |
| `soapsquest.leaderboard` | ✅ All | View quest leaderboard |

### Admin Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `soapsquest.give` | ❌ Op | Give quests to players |
| `soapsquest.editor` | ❌ Op | Open quest editor GUI |
| `soapsquest.reload` | ❌ Op | Reload configuration |
| `soapsquest.debug` | ❌ Op | Toggle debug mode |
| `soapsquest.generate` | ❌ Op | Generate random quests |
| `soapsquest.remove` | ❌ Op | Delete quests |
| `soapsquest.addreward` | ❌ Op | Add rewards to quests |
| `soapsquest.removereward` | ❌ Op | Remove rewards from quests |
| `soapsquest.admin` | ❌ Op | List quest rewards |

### Wildcard

| Permission | Description |
|------------|-------------|
| `soapsquest.*` | All permissions |

---

## Custom Quest Permissions

Restrict quest access using permission requirements:

```yaml
vip_quest:
  display: "&6VIP Quest"
  conditions:
    permission: "soapsquest.vip"
  # ...
```

Only players with `soapsquest.vip` can:
- See the quest in `/sq list`
- Accept the quest
- Progress through it

---

## Permission Matrix

| Action | Permission | Admin | Player |
|--------|------------|-------|--------|
| View commands | `soapsquest.use` | ✅ | ✅ |
| List quests | `soapsquest.list` | ✅ | ✅ |
| Click to accept | `soapsquest.list.click` | ✅ | ✅ |
| Browse GUI | `soapsquest.browse` | ✅ | ✅ |
| View statistics | `soapsquest.statistics` | ✅ | ✅ |
| View leaderboard | `soapsquest.leaderboard` | ✅ | ✅ |
| Give quests | `soapsquest.give` | ✅ | ❌ |
| Quest editor | `soapsquest.editor` | ✅ | ❌ |
| Reload config | `soapsquest.reload` | ✅ | ❌ |
| Debug mode | `soapsquest.debug` | ✅ | ❌ |
| Generate quests | `soapsquest.generate` | ✅ | ❌ |
| Delete quests | `soapsquest.remove` | ✅ | ❌ |
| Manage rewards | `soapsquest.addreward` | ✅ | ❌ |
| All permissions | `soapsquest.*` | ✅ | ❌ |

---

**[← Back to README](README.md)** | **[Commands →](COMMANDS.md)**
