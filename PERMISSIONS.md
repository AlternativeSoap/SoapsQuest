# 🔒 SoapsQuest Permissions

Complete permission reference for SoapsQuest plugin.

---

## 📋 Table of Contents

- [Permission Overview](#permission-overview)
- [Default Permissions](#default-permissions)
- [Admin Permissions](#admin-permissions)
- [Player Permissions](#player-permissions)
- [Custom Quest Permissions](#custom-quest-permissions)
- [Permission Setup Examples](#permission-setup-examples)
- [Wildcard Permissions](#wildcard-permissions)

---

## 🎯 Permission Overview

SoapsQuest uses a simple permission system with sensible defaults:
- **Players** get basic quest interaction permissions by default
- **Admins** need explicit permissions for management commands
- **Custom Permissions** can be added to individual quests

All permissions follow the format: `soapsquest.<category>.<action>`

---

## ✅ Default Permissions

These permissions are granted to **all players** by default (no permission plugin required):

| Permission | Description | Default |
|------------|-------------|---------|
| `soapsquest.help` | View help menu with `/sq` | ✅ True |
| `soapsquest.list` | View quest list with `/sq list` | ✅ True |
| `soapsquest.list.click` | Click quests in list to accept | ✅ True |

**Note**: Players can interact with quests without any permission setup!

---

## 👑 Admin Permissions

These permissions are for server administrators and require explicit grant:

### Command Permissions

| Permission | Description | Default | Command |
|------------|-------------|---------|---------|
| `soapsquest.give` | Give quests to players | ❌ Op | `/sq give <player> <quest>` |
| `soapsquest.reload` | Reload plugin configuration | ❌ Op | `/sq reload` |
| `soapsquest.generate` | Generate random quests | ❌ Op | `/sq generate <player> <amount>` |

### Management Permission

| Permission | Description | Default |
|------------|-------------|---------|
| `soapsquest.admin` | All admin commands | ❌ Op |

**Grants**:
- `soapsquest.give`
- `soapsquest.reload`
- `soapsquest.generate`

---

## 👥 Player Permissions

### Quest Interaction

| Permission | Description | Default | Usage |
|------------|-------------|---------|-------|
| `soapsquest.list` | View available quests | ✅ True | `/sq list` command |
| `soapsquest.list.click` | Click to accept quests | ✅ True | Click quest in list |

### Quest Paper Actions

| Permission | Description | Default | Usage |
|------------|-------------|---------|-------|
| `soapsquest.complete` | Complete quests | ✅ True | Right-click quest paper |
| `soapsquest.progress` | Track quest progress | ✅ True | Automatic tracking |

**Note**: These permissions are automatically granted and don't need configuration.

---

## 🎨 Custom Quest Permissions

You can add permission requirements to individual quests in `quests.yml`:

### Basic Quest Permission

```yaml
dragon_slayer:
  display: "&dDragon Slayer"
  # ... other properties ...
  conditions:
    permission: "soapsquest.legendary"
```

**Effect**: Only players with `soapsquest.legendary` permission can:
- See the quest in `/sq list`
- Accept the quest
- Progress the quest

### Multiple Permission Examples

```yaml
# VIP Only Quest
vip_quest:
  display: "&6VIP Quest"
  conditions:
    permission: "soapsquest.vip"

# Donor Quest
donor_quest:
  display: "&5Donor Quest"
  conditions:
    permission: "soapsquest.donor"

# Staff Quest
staff_quest:
  display: "&cStaff Quest"
  conditions:
    permission: "soapsquest.staff"

# MVP Quest
mvp_quest:
  display: "&eMVP Quest"
  conditions:
    permission: "soapsquest.mvp"
```

### Custom Permission Naming

You can use **any permission name** you want:

```yaml
custom_quest:
  display: "&aCustom Quest"
  conditions:
    permission: "myserver.customrank.questaccess"
```

**Best Practices**:
- Use descriptive names: `soapsquest.tier.legendary`
- Group by category: `soapsquest.vip`, `soapsquest.donor`
- Match existing rank permissions: `essentials.vip` → use same in quest

---

## ⚙️ Permission Setup Examples

### LuckPerms Setup

```bash
# Grant all admin permissions to Admins
/lp group admin permission set soapsquest.admin true

# Grant quest giving to Moderators
/lp group moderator permission set soapsquest.give true

# Grant VIP quest access to VIP rank
/lp group vip permission set soapsquest.vip true

# Grant legendary quest access to specific player
/lp user AlternativeSoap permission set soapsquest.legendary true
```

### PermissionsEx Setup

```bash
# Grant admin permissions
/pex group admin add soapsquest.admin

# Grant quest giving to moderators
/pex group moderator add soapsquest.give

# Grant VIP quest access
/pex group vip add soapsquest.vip

# Grant legendary access to player
/pex user AlternativeSoap add soapsquest.legendary
```

### GroupManager Setup

```bash
# Grant admin permissions
/manuaddp admin soapsquest.admin

# Grant quest giving to moderators
/manuaddp moderator soapsquest.give

# Grant VIP quest access
/manuaddp vip soapsquest.vip

# Grant legendary access to player
/manuaddp AlternativeSoap soapsquest.legendary
```

---

## 🌟 Wildcard Permissions

### `soapsquest.*`

**Description**: Grants **ALL** SoapsQuest permissions  
**Includes**:
- All admin commands
- All player commands
- All default permissions

**Usage**:
```bash
# LuckPerms
/lp group owner permission set soapsquest.* true

# PermissionsEx
/pex group owner add soapsquest.*

# GroupManager
/manuaddp owner soapsquest.*
```

⚠️ **Warning**: Only grant to trusted admins/owners!

---

## 🎯 Permission Hierarchy

### Recommended Permission Structure

```
soapsquest.*                          # Root (Owner only)
├── soapsquest.admin                  # All admin commands
│   ├── soapsquest.give              # Give quests
│   ├── soapsquest.reload            # Reload configs
│   └── soapsquest.generate          # Generate quests
│
├── soapsquest.list                   # View quest list (default)
├── soapsquest.list.click            # Click to accept (default)
│
└── Custom Quest Permissions
    ├── soapsquest.vip               # VIP quests
    ├── soapsquest.donor             # Donor quests
    ├── soapsquest.legendary         # Legendary quests
    └── soapsquest.mvp               # MVP quests
```

---

## 📊 Permission Matrix

| Action | Permission Node | Default | Admin | Moderator | Player |
|--------|----------------|---------|-------|-----------|--------|
| View help | `soapsquest.help` | ✅ True | ✅ | ✅ | ✅ |
| List quests | `soapsquest.list` | ✅ True | ✅ | ✅ | ✅ |
| Click quests | `soapsquest.list.click` | ✅ True | ✅ | ✅ | ✅ |
| Give quests | `soapsquest.give` | ❌ Op | ✅ | ✅ | ❌ |
| Reload config | `soapsquest.reload` | ❌ Op | ✅ | ❌ | ❌ |
| Generate quests | `soapsquest.generate` | ❌ Op | ✅ | ❌ | ❌ |
| All admin | `soapsquest.admin` | ❌ Op | ✅ | ❌ | ❌ |
| All permissions | `soapsquest.*` | ❌ Op | ✅ | ❌ | ❌ |

---

## 🔐 Security Recommendations

### For Production Servers

1. **Don't Grant Wildcards** - Avoid `soapsquest.*` except for owners
2. **Limit Admin Access** - Only trusted staff should have `soapsquest.admin`
3. **Audit Quest Giving** - Monitor who has `soapsquest.give` permission
4. **Custom Permissions** - Use custom permissions for premium/VIP quests
5. **Regular Reviews** - Periodically review who has admin permissions

### For Testing Servers

1. **Grant Wildcards Freely** - `soapsquest.*` for easy testing
2. **Test All Ranks** - Verify custom quest permissions work correctly
3. **Document Changes** - Keep track of permission changes during testing

---

## 🎨 Custom Permission Examples

### Tiered Quest Access

```yaml
# Common tier - Everyone can access (no permission)
zombie_slayer:
  display: "&aZombie Slayer"
  tier: common
  # No permission required

# Rare tier - VIP access required
diamond_collector:
  display: "&bDiamond Collector"
  tier: rare
  conditions:
    permission: "soapsquest.vip"

# Epic tier - Donor access required
nether_explorer:
  display: "&cNether Explorer"
  tier: epic
  conditions:
    permission: "soapsquest.donor"

# Legendary tier - MVP access required
dragon_slayer:
  display: "&dDragon Slayer"
  tier: legendary
  conditions:
    permission: "soapsquest.mvp"
```

### Difficulty-Based Permissions

```yaml
# Easy - Everyone
easy_quest:
  difficulty: easy
  # No permission

# Normal - Members only
normal_quest:
  difficulty: normal
  conditions:
    permission: "soapsquest.member"

# Hard - VIP only
hard_quest:
  difficulty: hard
  conditions:
    permission: "soapsquest.vip"

# Nightmare - Admin only
nightmare_quest:
  difficulty: nightmare
  conditions:
    permission: "soapsquest.admin.quest"
```

---

## ❓ Troubleshooting

### "You don't have permission to use this command"

**Solution**:
1. Check player has the required permission node
2. Verify permission plugin is installed and working
3. Check permission plugin configuration files
4. Use permission check command: `/lp user <player> check soapsquest.give`

### "Quest not appearing in /sq list"

**Solution**:
1. Check if quest has a `permission` condition
2. Verify player has that permission
3. Test with admin account (should see all quests)
4. Check quest isn't filtered by other conditions

### "Permission not working after grant"

**Solution**:
1. Player may need to relog
2. Run permission reload: `/lp reload` or `/pex reload`
3. Verify permission was granted to correct group/player
4. Check for permission conflicts (negations)

---

## 📖 Related Documentation

- [**COMMANDS.md**](COMMANDS.md) - Complete command reference
- [**README.md**](README.md) - Plugin overview and features
- [**Configuration Guide**](https://github.com/AlternativeSoap/SoapsQuest/wiki/Configuration) - Config file setup

---

## 💬 Support

Need help with permissions?

- **Discord**: [discord.gg/soapsuniverse](https://discord.gg/soapsuniverse)
- **Issues**: [GitHub Issues](https://github.com/AlternativeSoap/SoapsQuest/issues)
- **Wiki**: [GitHub Wiki](https://github.com/AlternativeSoap/SoapsQuest/wiki)

---

<div align="center">

**[⬅️ Back to README](README.md)**

</div>
