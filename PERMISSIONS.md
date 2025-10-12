# 🔒 Permissions

Complete permission reference for SoapsQuest.

---

## Permission Nodes

### Player Permissions (Default: True)

| Permission | Description |
|------------|-------------|
| `soapsquest.use` | Access to basic commands |
| `soapsquest.list` | View quest list |
| `soapsquest.list.click` | Click to accept quests from list |

### Admin Permissions (Default: Op)

| Permission | Description | Command |
|------------|-------------|---------|
| `soapsquest.give` | Give quests to players | `/sq give` |
| `soapsquest.reload` | Reload configuration | `/sq reload` |
| `soapsquest.generate` | Generate random quests | `/sq generate` |
| `soapsquest.addreward` | Add rewards to quests | `/sq addreward` |
| `soapsquest.removereward` | Remove rewards from quests | `/sq removereward` |
| `soapsquest.admin` | Access to listreward | `/sq listreward` |

### Wildcard Permissions

| Permission | Description |
|------------|-------------|
| `soapsquest.*` | All permissions |

---

## Custom Quest Permissions

Add permission requirements to individual quests:

```yaml
vip_quest:
  display: "&6VIP Quest"
  tier: epic
  difficulty: hard
  conditions:
    permission: "soapsquest.vip"
  # ...
```

Only players with `soapsquest.vip` can:
- See the quest in `/sq list`
- Accept the quest
- Progress through it

### Examples

```yaml
# VIP-only quest
vip_quest:
  conditions:
    permission: "soapsquest.vip"

# Donor-only quest
donor_quest:
  conditions:
    permission: "soapsquest.donor"

# Use any permission name
custom_quest:
  conditions:
    permission: "myserver.customrank"
```

---

## Permission Setup

### LuckPerms

```bash
# Give all permissions to admin group
/lp group admin permission set soapsquest.* true

# Give quest giving to moderators
/lp group moderator permission set soapsquest.give true

# Give VIP quest access
/lp group vip permission set soapsquest.vip true
```

### PermissionsEx

```bash
# Admin permissions
/pex group admin add soapsquest.*

# Moderator permissions
/pex group moderator add soapsquest.give

# VIP quest access
/pex group vip add soapsquest.vip
```

---

## Permission Matrix

| Action | Permission | Admin | Moderator | Player |
|--------|------------|-------|-----------|--------|
| View commands | `soapsquest.use` | ✅ | ✅ | ✅ |
| List quests | `soapsquest.list` | ✅ | ✅ | ✅ |
| Click to accept | `soapsquest.list.click` | ✅ | ✅ | ✅ |
| Give quests | `soapsquest.give` | ✅ | ✅ | ❌ |
| Reload config | `soapsquest.reload` | ✅ | ❌ | ❌ |
| Generate quests | `soapsquest.generate` | ✅ | ❌ | ❌ |
| Manage rewards | `soapsquest.addreward` | ✅ | ❌ | ❌ |
| All permissions | `soapsquest.*` | ✅ | ❌ | ❌ |

---

**[⬅️ Back to README](README.md)**
