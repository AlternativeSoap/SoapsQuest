# 📜 SoapsQuest

[![Version](https://img.shields.io/badge/Version-1.0.0-blue.svg)](https://github.com/AlternativeSoap/SoapsQuest/releases)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.8-brightgreen.svg)](https://www.spigotmc.org/)
[![Paper API](https://img.shields.io/badge/Paper%20API-1.21.8-blue.svg)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Premium-red.svg)](LICENSE.md)
[![Status](https://img.shields.io/badge/Status-Premium%20Beta-yellow.svg)](https://github.com/AlternativeSoap/SoapsQuest)

A powerful and flexible quest plugin for Minecraft Paper servers featuring physical quest papers, dynamic objectives, and an intuitive clickable interface.

---

## 🌟 Key Features

- **Physical Quest Papers** - Quests as tangible items in player inventories
- **33+ Objective Types** - Combat, building, collection, survival, movement, leveling, and more
- **Multi-Objective Quests** - Complete multiple tasks in one quest
- **Real-time Progress Tracking** - BossBar, action bar, or chat notifications
- **Customizable Tiers & Difficulty** - Common, Rare, Epic, Legendary with configurable difficulty levels
- **Multiple Reward Types** - XP, money (Vault), items, and console commands
- **Condition System** - 12 condition types including level, money, world, permissions, and PlaceholderAPI
- **Interactive UI** - Click-to-accept quests from `/sq list` with hover tooltips

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

1. Download `SoapsQuest-1.0.0.jar` from [Releases](https://github.com/AlternativeSoap/SoapsQuest/releases)
2. Place the JAR file in your server's `plugins/` folder
3. Restart your server (do not use plugin managers for first install)
4. Configure quests in `plugins/SoapsQuest/quests.yml`
5. Customize messages in `plugins/SoapsQuest/messages.yml`
6. Adjust settings in `plugins/SoapsQuest/config.yml`

> � **First Time Setup**: Default configuration includes 10 example quests. Test these before creating custom quests.

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

- **[Wiki](WIKI.md)** - Complete documentation hub
- **[Commands](COMMANDS.md)** - Command reference
- **[Permissions](PERMISSIONS.md)** - Permission nodes
- **[Configuration](CONFIGURATION.md)** - Config.yml setup and customization
- **[Quest Creation](QUEST-CREATION.md)** - Create custom quests with examples
- **[Random Generator](RANDOM-GENERATOR.md)** - Generate procedural quests dynamically
- **[Changelog](CHANGELOG.md)** - Version history and release notes

---

## 🤝 Support

- **Discord**: [discord.gg/soapsuniverse](https://discord.gg/soapsuniverse)
- **Issues**: [GitHub Issues](https://github.com/AlternativeSoap/SoapsQuest/issues)
- **Documentation**: [Wiki](WIKI.md)

---

## 📝 License

This project is licensed under a Premium License - see [LICENSE.md](LICENSE.md) for details.

**Summary**: Premium software requiring purchase, single server license, no redistribution, no decompilation.

---

## 🙏 Credits

**Author**: AlternativeSoap  
**Version**: 1.0.0  
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
