# 📚 SoapsQuest Wiki

Complete documentation hub for SoapsQuest plugin.

---

## Getting Started

- [Installation Guide](README.md#-installation)
- [Quick Start](README.md#-quick-start)
- [Requirements](README.md#-requirements)

---

## Documentation

### Core Guides

- **[Configuration Guide](CONFIGURATION.md)** - Complete config.yml, messages.yml, and settings reference
- **[Quest Creation Guide](QUEST-CREATION.md)** - Learn how to create custom quests with examples
- **[Random Generator Guide](RANDOM-GENERATOR.md)** - Generate procedural quests with customizable pools
- **[Commands Reference](COMMANDS.md)** - All available commands and their usage
- **[Permissions Reference](PERMISSIONS.md)** - Complete permission nodes documentation

### Features

- [Objective Types](#objective-types)
- [Reward System](#reward-system)
- [Conditions & Requirements](#conditions--requirements)
- [Quest Papers](#quest-papers)
- [Progress Tracking](#progress-tracking)
- [Integrations](#integrations)

---

## Objective Types

SoapsQuest supports 33+ objective types across multiple categories:

### Combat Objectives (7 types)
- `kill` - Kill entities (specific, ANY, HOSTILE, PASSIVE)
- `kill_mythicmob` - Kill MythicMobs creatures
- `damage` - Deal damage to entities
- `death` - Die (for hardcore challenges)
- `bowshoot` - Shoot arrows
- `projectile` - Launch projectiles
- `entity_kill` - Alternative kill objective

**[View Combat Objective Examples →](QUEST-CREATION.md#combat-objectives)**

### Building Objectives (3 types)
- `break_block` - Break specific blocks
- `place_block` - Place specific blocks
- `interact` - Interact with blocks (right-click)

**[View Building Objective Examples →](QUEST-CREATION.md#building-objectives)**

### Collection Objectives (7 types)
- `collect` - Pick up items from the ground
- `craft` - Craft items
- `smelt` - Smelt items in furnaces
- `fish` - Catch fish or treasures
- `brew` - Brew potions
- `enchant` - Enchant items
- `drop` - Drop items

**[View Collection Objective Examples →](QUEST-CREATION.md#collection-objectives)**

### Survival Objectives (6 types)
- `consume` - Eat or drink items
- `tame` - Tame animals
- `trade` - Trade with villagers
- `shear` - Shear sheep
- `sleep` - Sleep in beds
- `heal` - Regenerate health

**[View Survival Objective Examples →](QUEST-CREATION.md#survival-objectives)**

### Movement Objectives (3 types)
- `move` - Walk/run distance
- `jump` - Jump
- `vehicle` - Travel in vehicles (boats, minecarts, horses)

**[View Movement Objective Examples →](QUEST-CREATION.md#movement-objectives)**

### Leveling Objectives (3 types)
- `level` - Generic level objective
- `gainlevel` - Gain experience levels
- `reachlevel` - Reach specific level

**[View Leveling Objective Examples →](QUEST-CREATION.md#leveling-objectives)**

### Miscellaneous Objectives (4 types)
- `chat` - Send chat messages
- `firework` - Launch fireworks
- `command` - Execute commands
- `placeholder` - PlaceholderAPI expressions

**[View Miscellaneous Objective Examples →](QUEST-CREATION.md#miscellaneous-objectives)**

---

## Reward System

### Available Reward Types

| Type | Description | Requirements |
|------|-------------|--------------|
| `xp` | Experience points | None |
| `money` | Economy currency | Vault |
| `items` | Physical items | None |
| `commands` | Console commands | None |

### Item Rewards Features
- Custom names and lore
- Enchantments
- Chance-based drops (0-100%)
- Glow effects
- Hide flags

**[View Reward System Details →](QUEST-CREATION.md#reward-system)**

---

## Conditions & Requirements

### Progress Conditions
Checked while completing objectives:
- `min-level` / `max-level` - Level requirements
- `min-money` - Money requirements
- `world` - World restrictions
- `gamemode` - Gamemode restrictions
- `time` - Time of day (DAY/NIGHT)
- `permission` - Permission nodes
- `placeholder` - PlaceholderAPI expressions

### Locking Conditions
Quest starts locked, must be unlocked:
- `cost` - Money to unlock
- `item` - Items to unlock
- `consume-item` - Consume unlock items

### Limits
- `active-limit` - Max concurrent quests

**[View All Conditions →](QUEST-CREATION.md#conditions--requirements)**

---

## Quest Papers

### Physical Quest System

Quests exist as physical items (papers) in player inventories:
- Right-click to complete/claim rewards
- Fully customizable appearance
- Support for custom materials
- Enchantment glow effects
- Custom names and lore

### Customization Options

```yaml
quest_paper:
  material: PAPER
  name: "&e{quest_name}"
  lore:
    - "&7Tier: {tier}"
    - "&7Difficulty: {difficulty}"
  enchantments:
    - "DURABILITY:1"
  hide-enchants: true
  glowing: true
```

**[View Quest Paper Customization →](QUEST-CREATION.md#custom-quest-papers)**

---

## Progress Tracking

### Display Types

**BossBar** (Default)
- Persistent progress bar at top of screen
- Customizable colors and styles
- Smooth progress updates

**ActionBar**
- Progress shown above hotbar
- Less intrusive than BossBar
- Quick progress updates

**Chat**
- Progress messages in chat
- Traditional approach
- Good for minimal UIs

### Milestone Notifications

Alert players at custom percentages:
- Default: 25%, 50%, 75%
- Fully customizable
- Can be disabled

**[Configure Progress Display →](CONFIGURATION.md#progress-display)**

---

## Integrations

### Vault Integration

**Features:**
- Economy rewards (money)
- Economy costs (quest unlocking)
- Permission checks

**Setup:**
1. Install Vault plugin
2. Install economy plugin (EssentialsX, etc.)
3. Restart server
4. Use `money` in rewards/costs

### PlaceholderAPI Integration

**Features:**
- Placeholder conditions
- Placeholder objectives
- Dynamic quest requirements

**Setup:**
1. Install PlaceholderAPI
2. Install relevant expansions
3. Use placeholders in conditions

**Example:**
```yaml
conditions:
  placeholder:
    placeholder: "%player_level%"
    value: ">=50"
```

### MythicMobs Integration

**Features:**
- Kill MythicMobs as objectives
- Support for custom mob names

**Setup:**
1. Install MythicMobs plugin
2. Create MythicMobs
3. Use `kill_mythicmob` objective

**Example:**
```yaml
objectives:
  - type: kill_mythicmob
    mob: "SkeletonKing"
    amount: 1
```

---

## Commands & Permissions

### Player Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/sq list` | `soapsquest.list` | View all available quests |
| `/sq active` | `soapsquest.active` | View active quests |
| `/sq abandon <quest>` | `soapsquest.abandon` | Abandon a quest |

### Admin Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/sq give <player> <quest>` | `soapsquest.admin.give` | Give quest to player |
| `/sq reload` | `soapsquest.admin.reload` | Reload configuration |
| `/sq reset <player>` | `soapsquest.admin.reset` | Reset player data |

**[View All Commands →](COMMANDS.md)** | **[View All Permissions →](PERMISSIONS.md)**

---

## Configuration Files

### config.yml
Main plugin settings:
- Tier definitions
- Difficulty definitions
- Progress display settings
- Quest limits
- Autosave settings

**[View Configuration Guide →](CONFIGURATION.md#configyml)**

### messages.yml
All player-facing messages:
- Quest notifications
- Progress messages
- Error messages
- Fully translatable

**[View Messages Configuration →](CONFIGURATION.md#messagesyml)**

### quests.yml
Quest definitions:
- All quest configurations
- Objectives
- Rewards
- Conditions

**[View Quest Creation Guide →](QUEST-CREATION.md)**

### random-generator.yml
Random quest generation settings (optional feature)

**[View Random Generator Guide →](RANDOM-GENERATOR.md)**

---

## Examples

### Example Quests

- [Beginner Quest](QUEST-CREATION.md#beginner-quest)
- [Combat Quest](QUEST-CREATION.md#combat-quest)
- [Gathering Quest](QUEST-CREATION.md#gathering-quest)
- [Boss Quest](QUEST-CREATION.md#boss-quest)
- [Daily Quest](QUEST-CREATION.md#daily-quest-with-active-limit)
- [Sequential Crafting Quest](QUEST-CREATION.md#sequential-crafting-quest)

### Configuration Examples

- [Minimal Setup](CONFIGURATION.md#minimal-setup)
- [Advanced Setup](CONFIGURATION.md#advanced-setup)
- [Custom Tiers](CONFIGURATION.md#tier-configuration)
- [Custom Difficulties](CONFIGURATION.md#difficulty-configuration)

---

## Troubleshooting

### Common Issues

**Quests not loading**
- Check YAML syntax
- Verify file encoding (UTF-8)
- Check console for errors
- Use `/sq reload` after changes

**Rewards not working**
- Verify Vault is installed (for money)
- Check material names (must be UPPERCASE)
- Test chance-based rewards multiple times

**Progress not tracking**
- Verify objective type is correct
- Check condition requirements
- Ensure player meets all conditions
- Check world/gamemode restrictions

**[View Full Troubleshooting Guide →](CONFIGURATION.md#troubleshooting)**

---

## Performance

### Optimization

SoapsQuest is designed for zero impact on server performance:

- **CPU**: 0% idle usage (event-driven)
- **RAM**: <50 MB with automatic cleanup
- **I/O**: Async autosave (non-blocking)
- **Thread Safety**: Full concurrent support

### Best Practices

1. Set reasonable autosave intervals (5-10 minutes)
2. Limit active quests per player (5-10)
3. Use efficient objective types
4. Test on development server first

---

## Support

### Get Help

- **Discord**: [discord.gg/soapsuniverse](https://discord.gg/soapsuniverse)
- **GitHub Issues**: [Report bugs](https://github.com/AlternativeSoap/SoapsQuest/issues)
- **GitHub Discussions**: [Ask questions](https://github.com/AlternativeSoap/SoapsQuest/discussions)

### Contributing

We welcome contributions! Please see our contributing guidelines.

### License

SoapsQuest is licensed under a Commercial License - see [LICENSE.md](LICENSE.md)

---

## Version Information

- **Current Version**: 1.0.0
- **Minecraft Version**: 1.21.8+
- **Java Version**: 21+
- **Release Date**: October 2025

**[View Changelog →](CHANGELOG.md)**

---

<div align="center">

**[← Back to README](README.md)**

Made with ❤️ by AlternativeSoap

</div>
