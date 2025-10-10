# SoapsQuest

A lightweight Minecraft quest plugin for Paper servers using physical quest papers to track progress.

## ✨ v1.1.0 - Interactive Quest List

**NEW:** Click any quest in `/sq list` to instantly claim it! Hover for rich tooltips showing objectives, rewards, and requirements. See [CHANGELOGS.md](CHANGELOGS.md) for details.

## Features

- **✨ Clickable Quest List** - Click to claim quests directly from `/sq list`
- **Rich Hover Tooltips** - Preview objectives, rewards, and requirements before claiming
- **28 Quest Types** - Kill, Break, Collect, Craft, Fish, MythicMobs, and more
- **Physical Quest Papers** - Real items bound to players with live progress updates
- **Quest Tiers** - COMMON, RARE, EPIC, LEGENDARY with colored prefixes
- **Milestones** - Progress notifications at custom percentages
- **Reward Chances** - Randomized rewards with configurable drop rates
- **Multi-Objective Quests** - Sequential or parallel objective completion
- **Rich Rewards** - XP, money (Vault), items with enchantments, commands
- **Hot-Reload** - Update configuration without restart
- **Async Saving** - Auto-save player data without lag

## Requirements

- **Paper** 1.20.4+ (or any Paper-based server)
- **Java** 21+
- **Vault** (optional, for money rewards)
- **MythicMobs** (optional, for MythicMobs quests)

## Installation

1. Download `SoapsQuest-X.X.X.jar` from releases
2. Place in `plugins/` folder
3. Restart server
4. Edit `plugins/SoapsQuest/quests.yml` to configure quests
5. Customize messages in `messages.yml`

## Commands

All commands use `/soapsquest` or `/sq` alias.

| Command | Description | Permission |
|---------|-------------|------------|
| `/sq give <player> <quest>` | Give a quest paper | `soapsquest.give` (op) |
| `/sq list [page]` | **✨ Interactive quest list** - Click to claim! | `soapsquest.list` (all) |
| `/sq generate <player> [tier] [difficulty]` | Generate random quest | `soapsquest.generate` (op) |
| `/sq reload` | Reload configuration | `soapsquest.reload` (op) |
| `/sq listreward <quest>` | List quest rewards | `soapsquest.admin` (op) |
| `/sq addreward <quest> <type> [args]` | Add quest reward | `soapsquest.addreward` (op) |
| `/sq removereward <quest> <index>` | Remove reward by index | `soapsquest.removereward` (op) |
| `/sq help` | Display command help | `soapsquest.use` (all) |

### ✨ NEW: Interactive Quest List

The `/sq list` command now features **clickable quest entries** with rich hover tooltips:

- **Click any quest** to instantly claim it (no more typing `/sq give`!)
- **Hover tooltips** show:
  - Quest objectives (first 3, with "... and X more" if needed)
  - All rewards (XP, money, items, commands) with color-coding
  - Requirements (level, cost, permissions, items)
  - Click instruction or manual claim command
- **Permission-based**: Players need `soapsquest.list.click` to click (default: true)
- **Console compatible**: Plain text fallback for console users

**Example:**
```
/sq list
  ━━━━━━ SoapsQuest - Quest List ━━━━━━
  
  Regular Quests:
    • [COMMON] Zombie Hunter | SINGLE [Normal]  ← Click me!
    • [RARE] Deep Miner | SINGLE [Normal]
    • [EPIC] Diamond Collector | SINGLE [Hard]
  
  (Hover to see quest details & rewards!)
```

**Permissions:**
- `soapsquest.list` - View quest list (default: true)
- `soapsquest.list.click` - Click to claim quests (default: true)

## Permissions

| Permission | Description | Default |
|-----------|-------------|---------|
| `soapsquest.*` | All permissions | op |
| `soapsquest.use` | Access help and basic commands | everyone |
| `soapsquest.give` | Give quest papers | op |
| `soapsquest.list` | View quest list | everyone |
| `soapsquest.list.click` | **✨ Click quests to claim** | everyone |
| `soapsquest.generate` | Generate random quests | op |
| `soapsquest.reload` | Reload configuration | op |
| `soapsquest.admin` | Administrative commands | op |
| `soapsquest.addreward` | Add quest rewards | op |
| `soapsquest.removereward` | Remove quest rewards | op |

**Note:** Remove `soapsquest.list.click` from player groups to disable clickable quests (they can still use `/sq give`).

## Configuration

**quests.yml** - Define all quests with full documentation in-file  
**config.yml** - Plugin settings (autosave interval, display options, config-version tracking)  
**messages.yml** - Customizable messages with color code support  
**random-generator.yml** - Random quest generation rules and pools

### Config Version Tracking

SoapsQuest now tracks configuration versions in `config.yml`:

```yaml
# Configuration version (DO NOT MODIFY - used for automatic updates)
config-version: 1
```

When you upgrade SoapsQuest, the plugin will detect outdated configs and log warnings. Always backup your configs before updating!

### Example Quest

```yaml
diamond_collector:
  type: collect
  item: DIAMOND
  amount: 10
  display: "&b&lDiamond Collector"
  tier: EPIC                    # Purple [EPIC] prefix
  milestones: [50]              # Progress notification at 50%
  lock-to-player: true
  reward:
    xp: 300
    items:
      - material: DIAMOND_PICKAXE
        name: "&bLucky Pickaxe"
        enchantments:
          - "LOOT_BONUS_BLOCKS:3"
        chance: 100             # 100% drop rate
      - material: DIAMOND
        amount: 3
        chance: 50              # 50% chance for bonus diamonds
```

### Quest Types

**Combat:** kill, kill_mythicmob, damage, death  
**Building:** break, place  
**Gathering:** collect, fish, shear  
**Crafting:** craft, smelt, brew, enchant  
**Social:** trade, interact, chat, command  
**Survival:** consume, tame, sleep, heal, level  
**Movement:** move, ride_vehicle  
**Special:** shoot_bow, launch_firework, placeholder

### Reward Types

- **XP** - Experience points with optional chance
- **Money** - Requires Vault plugin, with optional chance
- **Items** - Custom items with enchantments, lore, and drop chance
- **Commands** - Execute any server command with `<player>` placeholder

### Quest Features

- **Tiers:** COMMON, RARE, EPIC, LEGENDARY (colored name prefixes)
- **Milestones:** Progress notifications at custom percentages [25, 50, 75]
- **Sequential Mode:** Complete objectives in specific order
- **Multi-Objective:** Combine multiple objectives in one quest
- **Entity Filters:** ANY, HOSTILE, PASSIVE, or specific entity types
- **Reward Chances:** Randomized drops (0-100% per reward)

## Building

```bash
git clone https://github.com/yourusername/SoapsQuest.git
cd SoapsQuest
mvn clean package
```

Output: `target/SoapsQuest-X.X.X.jar`

**Requirements:**
- Java 21+
- Maven 3.9+
- Paper API 1.20.4+

## Performance & Error Handling

### Quest List Optimization
- Quest tooltips are generated on-demand (lazy loading)
- Paginated list prevents memory issues with large quest databases
- Efficient reward parsing with fallback for corrupted data

### Error Handling
- **Corrupted Rewards:** Graceful fallbacks with console warnings
- **Invalid Quest Data:** Skipped with logging, doesn't crash server
- **Permission Issues:** Clear user feedback when click permission missing
- **Null Safety:** Modern Java patterns with proper null checking

### Best Practices
- Keep quest list pages small (default: 10 per page) for performance
- Monitor console for reward parsing warnings
- Test permission changes with `/sq list` before production
- Always backup `quests.yml` before bulk edits

---

**Author:** AlternativeSoap  
**Website:** www.soapsuniverse.com  
**Support:** GitHub Issues  
**Version:** 1.1.0 (See [CHANGELOGS.md](CHANGELOGS.md))
