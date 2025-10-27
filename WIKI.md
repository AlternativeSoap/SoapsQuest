# SoapsQuest Wiki

Central documentation hub for SoapsQuest.

---

## 🚀 Quick Start

1. [Download](https://github.com/AlternativeSoap/SoapsQuest/releases) and place in `plugins/` folder
2. Restart server
3. Edit `plugins/SoapsQuest/quests.yml`
4. Give quests: `/sq give <player> <quest>`
5. Players browse: `/sq list` or `/sq browse`

---

## 📚 Documentation

### Core Documentation

- **[README](README.md)** → Plugin overview and quick start
- **[COMMANDS](COMMANDS.md)** → All available commands
- **[CONFIGURATION](CONFIGURATION.md)** → Config files explained
- **[QUEST CREATION](QUEST-CREATION.md)** → Create and manage quests
- **[RANDOM GENERATOR](RANDOM-GENERATOR.md)** → Automatic quest generation
- **[PLACEHOLDERAPI](PLACEHOLDERAPI.md)** → Placeholder list
- **[CHANGELOG](CHANGELOG.md)** → Version updates

---

## ✨ Features

- **Physical Quest Papers** – Quest items exist in inventories
- **31 Objective Types** – Combat, building, collection, survival, movement, leveling
- **Multi-Objective Quests** – Complete multiple tasks in any order or sequentially
- **Real-Time Progress** – BossBar, ActionBar, or Chat tracking
- **Flexible Rewards** – XP, money, items, commands
- **Quest Conditions** – Level, money, permission, PlaceholderAPI requirements
- **Random Generation** – Auto-generate infinite quests
- **Quest Loot System** – Quests drop from chests and mobs
- **Interactive GUIs** – Browse, create, and edit quests in-game
- **Leaderboards** – PlaceholderAPI integration

---

## 🎯 Example Quest

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

Give to player:
```
/sq give Steve zombie_slayer
```

---

## 💬 Support

- **Discord**: [discord.gg/soapsuniverse](https://discord.gg/soapsuniverse)
- **Issues**: [GitHub Issues](https://github.com/AlternativeSoap/SoapsQuest/issues)

---

**[← Back to README](README.md)**

