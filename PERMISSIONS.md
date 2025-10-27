# Permissions

Permission reference for SoapsQuest.

---

## 👤 Player Permissions

Default permissions for all players.

| Permission | Description |
|------------|-------------|
| `soapsquest.use` | Basic command access |
| `soapsquest.list` | View quest list (`/sq list`) |
| `soapsquest.browse` | Open quest browser GUI (`/sq browse`) |
| `soapsquest.statistics` | View quest statistics (`/sq statistics`) |
| `soapsquest.leaderboard` | View quest leaderboard (`/sq leaderboard`) |

---

## 🔧 Admin Permissions

Permissions for server administrators (OP only by default).

| Permission | Description |
|------------|-------------|
| `soapsquest.give` | Give quests to players |
| `soapsquest.editor` | Open quest editor GUI |
| `soapsquest.generate` | Generate random quests |
| `soapsquest.reload` | Reload configuration files |
| `soapsquest.debug` | Toggle debug mode |
| `soapsquest.remove` | Delete quests permanently |
| `soapsquest.addreward` | Add rewards to quests |
| `soapsquest.removereward` | Remove rewards from quests |
| `soapsquest.admin` | List quest rewards |

---

## ⭐ Wildcard Permission

| Permission | Description |
|------------|-------------|
| `soapsquest.*` | All permissions |

---

## 🔒 Custom Quest Permissions

Restrict quest access using conditions:

```yaml
vip_quest:
  display: "&6VIP Quest"
  objectives:
    - type: kill
      target: ZOMBIE
      amount: 10
  reward:
    xp: 100
  conditions:
    permission: "soapsquest.vip"
```

Only players with `soapsquest.vip` can:
- See the quest in `/sq list`
- Accept the quest
- Progress through it

---

## 📊 Permission Matrix

| Action | Permission | Admin | Player |
|--------|------------|:-----:|:------:|
| View commands | `soapsquest.use` | ✅ | ✅ |
| List quests | `soapsquest.list` | ✅ | ✅ |
| Browse GUI | `soapsquest.browse` | ✅ | ✅ |
| View statistics | `soapsquest.statistics` | ✅ | ✅ |
| View leaderboard | `soapsquest.leaderboard` | ✅ | ✅ |
| Give quests | `soapsquest.give` | ✅ | ❌ |
| Quest editor | `soapsquest.editor` | ✅ | ❌ |
| Generate quests | `soapsquest.generate` | ✅ | ❌ |
| Reload config | `soapsquest.reload` | ✅ | ❌ |
| Debug mode | `soapsquest.debug` | ✅ | ❌ |
| Delete quests | `soapsquest.remove` | ✅ | ❌ |
| Manage rewards | `soapsquest.addreward` | ✅ | ❌ |
| All permissions | `soapsquest.*` | ✅ | ❌ |

---

**[← Back to README](README.md)** | **[Commands →](COMMANDS.md)**

