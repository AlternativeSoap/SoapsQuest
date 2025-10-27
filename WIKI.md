# Wiki

Documentation hub for SoapsQuest.

---

## Getting Started

1. Download from [Releases](https://github.com/AlternativeSoap/SoapsQuest/releases)
2. Place in `plugins/` folder
3. Restart server
4. Edit `plugins/SoapsQuest/quests.yml`
5. Give quests with `/sq give <player> <quest>`

## Documentation

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

## Quick Example

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
```

Give to player:
```
/sq give <player> my_quest
```

---

## Features

- Physical quest papers
- 31 objective types
- Multi-objective support
- Progress tracking
- Rewards and conditions
- Random generation
- Quest loot system
- PlaceholderAPI integration

---

## Support

- Discord: [discord.gg/soapsuniverse](https://discord.gg/soapsuniverse)
- Issues: [GitHub Issues](https://github.com/AlternativeSoap/SoapsQuest/issues)

---

**[← Back to README](README.md)**
