# Wiki

Documentation hub for SoapsQuest.

---

## Getting Started

| Topic | Description |
|-------|-------------|
| [Installation](#installation) | How to install SoapsQuest |
| [Quick Start](#quick-start) | Create and give your first quest |
| [Requirements](#requirements) | Server and dependency requirements |

## Core Documentation

| Guide | Description |
|-------|-------------|
| **[Commands](COMMANDS.md)** | All available commands |
| **[Permissions](PERMISSIONS.md)** | Permission nodes and access control |
| **[Configuration](CONFIGURATION.md)** | Complete configuration guide |
| **[Quest Creation](QUEST-CREATION.md)** | Create custom quests |
| **[Random Generator](RANDOM-GENERATOR.md)** | Random quest generation |
| **[PlaceholderAPI](PLACEHOLDERAPI.md)** | Placeholder reference |
| **[Changelog](CHANGELOG.md)** | Version history |

---

## Installation

1. Download `SoapsQuest-1.0.0-BETA.jar` from [Releases](https://github.com/AlternativeSoap/SoapsQuest/releases)
2. Place in `plugins/` folder  
3. Restart server
4. Edit quest configurations
5. Give quests to players

## Quick Start

### Create a Quest

Edit `plugins/SoapsQuest/quests.yml`:

```yaml
my_quest:
  display: "&aZombie Slayer"
  tier: common
  difficulty: easy
  objectives:
    - type: kill
      target: ZOMBIE
      amount: 10
  reward:
    xp: 100
    money: 50
```

### Give to Player

```
/sq give <player> my_quest
```

### Players Accept Quests

```
/sq list
```

Click to accept.

## Requirements

### Required
- **Server**: Paper 1.21.8+
- **Java**: 21+

### Optional
- **Vault** - Economy
- **PlaceholderAPI** - Placeholders and leaderboards  
- **MythicMobs** - MythicMobs objectives

---

## Features

### Quest System
- Physical quest papers
- 31 objective types
- Multi-objective support
- Sequential quest support
- Progress tracking (BossBar/ActionBar/Chat)

### Customization
- Tier system (fully customizable)
- Difficulty levels (fully customizable)
- Custom quest papers
- Milestone notifications

### Rewards & Conditions
- XP, money, items, commands
- Level, money, world, gamemode, time, permission requirements
- PlaceholderAPI conditions
- Quest locking (money/item costs)
- Active quest limits

### Integrations
- **Vault** - Economy
- **PlaceholderAPI** - Placeholders and leaderboards
- **MythicMobs** - Custom mob objectives

### Performance
- Async processing
- Batch save system
- Thread-safe operations
- Optimized for 100+ players

---

## Support

- **Discord**: [discord.gg/soapsuniverse](https://discord.gg/soapsuniverse)
- **Issues**: [GitHub Issues](https://github.com/AlternativeSoap/SoapsQuest/issues)

---

**[← Back to README](README.md)**
