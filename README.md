# 📜 SoapsQuest

[![Version](https://img.shields.io/badge/Version-1.0.2-blue.svg)](https://github.com/AlternativeSoap/SoapsQuest/releases)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.8-brightgreen.svg)](https://www.spigotmc.org/)
[![Paper API](https://img.shields.io/badge/Paper%20API-1.21.8-blue.svg)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Commercial-red.svg)](LICENSE.md)
[![Status](https://img.shields.io/badge/Status-Beta-yellow.svg)](https://github.com/AlternativeSoap/SoapsQuest)

A powerful and flexible quest plugin for Minecraft Paper servers featuring physical quest papers, dynamic objectives, and an intuitive clickable interface.

> ⚠️ **Beta Release**: This is a beta version. While extensively tested, please report any issues on [GitHub Issues](https://github.com/AlternativeSoap/SoapsQuest/issues).

---

## 🌟 Features

### 🎯 **Quest System**
- **Physical Quest Papers** - Quests exist as tangible items in player inventories
- **33+ Objective Types** - Combat, building, collection, survival, movement, leveling, and more
- **Multi-Objective Quests** - Complete multiple tasks in one quest
- **Sequential Objectives** - Force objectives to be completed in order
- **Progress Tracking** - Real-time BossBar, action-bar or chat progress display with customizable colors

### 🎨 **Customization**
- **Tier System** - Common, Rare, Epic, Legendary (fully customizable)
- **Difficulty Levels** - Easy, Normal, Hard, Nightmare (fully customizable)
- **Custom Quest Papers** - Set custom materials, names, lore, and enchantments
- **Milestone Notifications** - Alert players at custom progress percentages
- **Color Coded Messages** - Full color code support throughout

### 🎁 **Rewards**
- **Multiple Reward Types** - XP, Money (Vault), Items, Commands
- **Custom Items** - Add custom names, lore, and enchantments to rewards
- **Chance-Based Rewards** - Set drop rates for individual rewards (0-100%)
- **Command Execution** - Run any console command as a reward

### 🔒 **Conditions & Requirements**
- **12 Condition Types** - Level, money, world, gamemode, time, permissions, and more
- **Quest Locking** - Require money or items to unlock quests
- **Active Limits** - Restrict concurrent quests per player
- **PlaceholderAPI Support** - Use PAPI expressions as conditions

### 💻 **User Interface**
- **Clickable Quest List** - `/sq list` shows all quests with hover tooltips
- **Interactive Quest Papers** - Right-click to complete quests
- **Hover Tooltips** - View quest details before accepting
- **Click-to-Accept** - One-click quest acceptance from list

### 🔌 **Integrations**
- **Vault** - Economy support for money rewards and costs
- **PlaceholderAPI** - Use placeholders in conditions and messages
- **MythicMobs** - Kill MythicMobs as quest objectives

### ⚡ **Performance**
- **Zero Idle CPU Usage** - Optimized event-driven architecture
- **Memory Efficient** - <50 MB RAM usage with automatic cleanup
- **Async Data Saving** - Non-blocking autosave system
- **Production Ready** - Comprehensive stability testing (100/100 score)

---

## 📋 Requirements

- **Minecraft Server**: Paper 1.21.8+ (or Spigot/Bukkit with Adventure API)
- **Java**: Java 21 or higher
- **Optional Dependencies**:
  - Vault (for economy features)
  - PlaceholderAPI (for placeholder conditions)
  - MythicMobs (for MythicMobs kill objectives)

---

## 📥 Installation

1. Download `SoapsQuest-1.0.2.jar` from [Releases](https://github.com/AlternativeSoap/SoapsQuest/releases)
2. Place the JAR file in your server's `plugins/` folder
3. Restart your server (do not use plugin managers for first install)
4. Configure quests in `plugins/SoapsQuest/quests.yml`
5. Customize messages in `plugins/SoapsQuest/messages.yml`
6. Adjust settings in `plugins/SoapsQuest/config.yml`

> 💡 **First Time Setup**: Default configuration includes 10 example quests. Test these before creating custom quests.

---

## 🚀 Quick Start

### Creating Your First Quest

Edit `plugins/SoapsQuest/quests.yml`:

```yaml
my_first_quest:
  display: "&aWelcome Quest"
  tier: common
  difficulty: easy
  objectives:
    - type: kill
      entity: ZOMBIE
      amount: 10
  reward:
    xp: 100
    money: 50
    items:
      - material: DIAMOND_SWORD
        name: "&aStarter Sword"
        enchantments:
          - "SHARPNESS:2"
        chance: 100
```

### Giving Quests to Players

```
/sq give <player> my_first_quest
```

### Viewing All Quests

Players can use:
```
/sq list
```
*Click on any quest to accept it! Hover to see details.*

---

## 📚 Documentation

### Quick Links
- **[Wiki](WIKI.md)** - Complete documentation hub
- **[Commands](COMMANDS.md)** - Command reference
- **[Permissions](PERMISSIONS.md)** - Permission nodes
- **[Configuration](CONFIGURATION.md)** - Config.yml setup and customization
- **[Quest Creation](QUEST-CREATION.md)** - Create custom quests with examples
- **[Random Generator](RANDOM-GENERATOR.md)** - Generate procedural quests dynamically

### Feature Overview
- **33+ Objective Types** - Combat, building, collection, survival, movement, and more
- **12 Condition Types** - Control quest access and progression requirements
- **4 Reward Types** - XP, money, items, and commands with full customization

**[View All Features in Wiki →](WIKI.md)**

---

## 🤝 Support

- **Discord**: [discord.gg/soapsuniverse](https://discord.gg/soapsuniverse)
- **Issues**: [GitHub Issues](https://github.com/AlternativeSoap/SoapsQuest/issues)
- **Documentation**: [Wiki](WIKI.md)

---

## 📝 License

This project is licensed under a Commercial License - see [LICENSE.md](LICENSE.md) for details.

**Summary**: Single server license, no redistribution, no decompilation.

---

## 🙏 Credits

**Author**: AlternativeSoap  
**Version**: 1.0.2  
**Release Date**: October 2025  
**Repository**: [github.com/AlternativeSoap/SoapsQuest](https://github.com/AlternativeSoap/SoapsQuest)

### How to Help

1. **Install and Test**: Use SoapsQuest on your test server
2. **Try All Features**: Test commands, objectives, rewards, conditions
3. **Report Issues**: Open [GitHub Issues](https://github.com/AlternativeSoap/SoapsQuest/issues) for bugs
4. **Share Feedback**: Join [Discord](https://discord.gg/soapsuniverse) to discuss

---

<div align="center">

**Made with ❤️ for the Minecraft community**

</div>
