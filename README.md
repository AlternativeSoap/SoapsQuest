# SoapsQuest

[![Version](https://img.shields.io/badge/Version-1.0.0--BETA-blue.svg)](https://github.com/AlternativeSoap/SoapsQuest/releases)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.8-brightgreen.svg)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Premium-red.svg)](LICENSE.md)

Physical quest paper system for Minecraft servers with 31 objective types, multi-objective support, and progress tracking.

---

## Features

- Physical quest papers in player inventories
- 31 objective types (combat, building, collection, survival, movement, leveling)
- Multi-objective and sequential quest support
- Progress tracking (BossBar, ActionBar, Chat)
- Rewards: XP, money, items, commands
- Conditions and requirements
- Random quest generation
- Quest loot system (chests and mobs)
- PlaceholderAPI integration
- GUI browser and editor

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

1. Download from [Releases](https://github.com/AlternativeSoap/SoapsQuest/releases)
2. Place in `plugins/` folder
3. Restart server
4. Edit `plugins/SoapsQuest/quests.yml`
5. Use `/sq give <player> <quest>`

---

## Quick Start

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
Players use `/sq list` to view and accept quests.

---
