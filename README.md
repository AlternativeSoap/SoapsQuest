# SoapsQuest

[![Version](https://img.shields.io/badge/Version-1.0.0--BETA-blue.svg)](https://github.com/AlternativeSoap/SoapsQuest/releases)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.8-brightgreen.svg)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE.md)

A free physical quest system for Minecraft Paper 1.21.8+ featuring 33+ objective types, GUI editor, customizable rewards, and dynamic quest generation.

---

## ✨ Key Features

- **Physical Quest Papers** – Quest items exist in player inventories
- **33+ Objective Types** – Combat, building, collection, survival, movement, and more
- **Multi-Objective Quests** – Complete multiple objectives in any order or sequentially
- **Real-Time Progress** – BossBar, ActionBar, or Chat tracking
- **Flexible Rewards** – XP, money, custom items, command execution, and quest rewards
- **Quest Conditions** – Level requirements, costs, permissions, and PlaceholderAPI support
- **Random Generation** – Auto-generate infinite quests with configurable templates
- **Quest Loot System** – Quests drop from chests and mob kills
- **Interactive GUIs** – Browse, create, and edit quests in-game
- **Leaderboards** – Track player progress with PlaceholderAPI integration

---

## 📋 Requirements

- **Server**: Paper 1.21.8+ (Spigot/Bukkit compatible)
- **Java**: 21+

### Optional Dependencies

- **[Vault](https://www.spigotmc.org/resources/vault.34315/)** – Economy rewards and costs
- **[PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)** – Leaderboards and conditions
- **[MythicMobs](https://www.spigotmc.org/resources/mythicmobs.5702/)** – Custom mob objectives
- **[MMOItems](https://www.spigotmc.org/resources/mmoitems.39267/)** – Custom item rewards (use `material: HAND` while holding item)

---

## 🚀 Installation

1. Download `SoapsQuest.jar` from [Releases](https://github.com/AlternativeSoap/SoapsQuest/releases)
2. Place in your `plugins/` folder
3. Restart the server
4. Configure `plugins/SoapsQuest/quests.yml`
5. Give quests with `/sq give <player> <quest>`

---

## ⚡ Quick Start

Create your first quest in `plugins/SoapsQuest/quests.yml`:

```yaml
zombie_slayer:
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

Give it to a player:

```
/sq give Steve zombie_slayer
```

Players can also browse and accept quests:

```
/sq list
/sq browse
```

---

## 📚 Documentation

| Guide | Description |
|-------|-------------|
| **[COMMANDS & PERMISSIONS](COMMANDS.md)** | All commands and permission nodes |
| **[CONFIGURATION](CONFIGURATION.md)** | All config files including quest loot |
| **[QUEST CREATION](QUEST-CREATION.md)** | Create quests manually and randomly |
| **[PLACEHOLDERAPI](PLACEHOLDERAPI.md)** | Placeholder integration |
| **[CHANGELOG](CHANGELOG.md)** | Version history |

---

## 💬 Support

- **Discord**: [discord.gg/soapsuniverse](https://discord.gg/soapsuniverse)
- **Issues**: [GitHub Issues](https://github.com/AlternativeSoap/SoapsQuest/issues)

---

## 📄 License

Licensed under the MIT License © 2025 AlternativeSoap  
Free for all personal and commercial servers.

See [LICENSE.md](LICENSE.md) for full details.

---

Licensed under the MIT License © 2025 AlternativeSoap
