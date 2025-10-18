# SoapsQuest

[![Version](https://img.shields.io/badge/Version-1.0.0--BETA-blue.svg)](https://github.com/AlternativeSoap/SoapsQuest/releases)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.8-brightgreen.svg)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Premium-red.svg)](LICENSE.md)

Physical quest paper system for Minecraft Paper servers with 31 objective types, multi-objective support, and dynamic progress tracking.

---

## Features

- **Physical Quest Papers** - Quest items in player inventories
- **31 Objective Types** - Combat, building, collection, survival, movement, leveling
- **Multi-Objective Support** - Multiple tasks per quest (parallel or sequential)
- **Progress Tracking** - BossBar, ActionBar, or chat modes
- **Reward System** - XP, money (Vault), items, console commands
- **Condition System** - Requirements and restrictions for quests
- **Random Generation** - Procedural quest creation
- **PlaceholderAPI** - Leaderboards and statistics integration
- **Performance Optimized** - Async processing for high-load servers

---

## Requirements

- **Server**: Paper 1.21.8+ (or Spigot/Bukkit with Adventure API)
- **Java**: 21+

### Optional Dependencies

- **Vault** - Economy features
- **PlaceholderAPI** - Placeholders and leaderboards
- **MythicMobs** - MythicMobs kill objectives

---

## Installation

1. Download `SoapsQuest-1.0.0-BETA.jar` from [Releases](https://github.com/AlternativeSoap/SoapsQuest/releases)
2. Place in `plugins/` folder
3. Restart server
4. Edit `plugins/SoapsQuest/quests.yml` to create quests
5. Use `/sq give <player> <quest>` to distribute quests

> **Note**: Default configuration includes example quests for testing.

---

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

### Give Quest to Player

```
/sq give <player> my_quest
```

### Players View Available Quests

```
/sq list
```

Click quest names to accept them.

---

## Documentation

| Guide | Description |
|-------|-------------|
| **[Wiki](WIKI.md)** | Documentation hub |
| **[Commands](COMMANDS.md)** | Command reference |
| **[Permissions](PERMISSIONS.md)** | Permission nodes |
| **[Configuration](CONFIGURATION.md)** | Complete configuration guide |
| **[Quest Creation](QUEST-CREATION.md)** | Quest creation guide with examples |
| **[Random Generator](RANDOM-GENERATOR.md)** | Random quest generation |
| **[PlaceholderAPI](PLACEHOLDERAPI.md)** | Placeholder reference |
| **[Changelog](CHANGELOG.md)** | Version history |

---

## Support

- **Discord**: [discord.gg/soapsuniverse](https://discord.gg/soapsuniverse)
- **Issues**: [GitHub Issues](https://github.com/AlternativeSoap/SoapsQuest/issues)

---

## License

Premium License - See [LICENSE.md](LICENSE.md)

Single server license, no redistribution, no decompilation.

---

**Author**: AlternativeSoap  
**Version**: 1.0.0-BETA  
**Repository**: [github.com/AlternativeSoap/SoapsQuest](https://github.com/AlternativeSoap/SoapsQuest)
