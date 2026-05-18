# SoapsQuest

A quest plugin for Paper 1.21+ that uses physical quest papers in the player's inventory. **37 objective types**, flexible rewards, conditions, tiers, a Quest Browser GUI, PlaceholderAPI support, and more.

**Current version: 1.0.1**

---

## Downloads

| Edition | Jar name | Build command |
|---------|----------|---------------|
| **Premium** | `SoapsQuest-1.0.1-Premium.jar` | `mvn package` (default) |
| **Free** | `SoapsQuest-1.0.1-Free.jar` | `mvn package -P free` |

Premium includes the random quest generator, daily/weekly quests, quest loot system, and in-game quest editor. Free includes all core quest features and 37 objective types.

You also need **SoapsCommon** on your server (listed as a dependency in `plugin.yml`).

---

## Features

- **Physical Quest Papers** — quests are real items in inventory, showing progress on hover and claimed by right-click
- **37 Objective Types** — kill mobs, break blocks, place blocks, craft items, fish, tame animals, brew potions, enchant gear, trade with villagers, explore biomes, gain levels, and many more
- **Reward System** — commands, items, money (Vault), XP, and quest chains
- **Condition System** — restrict quests by world, permission, biome, time, weather, level, and more
- **Tiers and Difficulties** — organize quests into customizable categories
- **Quest Browser GUI** — in-game menu where players browse and pick up quests
- **Active Quests GUI** — players view their current quests in a GUI
- **Sequential Objectives** — force players to complete objectives in order
- **MiniMessage Formatting** — gradients, hex colors, hover/click events
- **PlaceholderAPI Support** — expose quest data for scoreboards and other plugins
- **MythicMobs Support** — use MythicMobs creatures as kill targets
- **Statistics Tracking** — per-player quest completion stats

### Premium Features

- **Random Quest Generator** — generate quests from weighted templates
- **Daily and Weekly Quests** — auto-rotating quests on a schedule
- **Quest Loot System** — quest papers drop from mobs and spawn in chests
- **In-Game Quest Editor** — create and edit quests from a GUI

---

## Requirements

- Paper 1.21+ (or a Paper fork like Purpur)
- Java 21+

### Optional

- [Vault](https://www.spigotmc.org/resources/vault.34315/) — for money rewards
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) — for placeholders and the `placeholder` objective
- [MythicMobs](https://www.spigotmc.org/resources/mythicmobs.5702/) — for the `kill_mythicmob` objective

---

## Quick Start

1. Install **SoapsCommon**, then drop `SoapsQuest-1.0.1-Premium.jar` or `SoapsQuest-1.0.1-Free.jar` into your server's `plugins/` folder
2. **Restart** the server (do not use `/reload` for first install)
3. Edit `plugins/SoapsQuest/quests.yml` — try the built-in `showcase_*` quests to test each objective type
4. Run `/sq reload` — you should see how many quests loaded
5. Give a quest: `/sq give <player> <questid>` or open `/sq browse`

See the [Getting Started](Getting-Started.md) guide for a full walkthrough.

---

## Documentation

| Page | Description |
|------|-------------|
| [Introduction](Introduction.md) | Plugin overview, key concepts, and features |
| [Getting Started](Getting-Started.md) | Installation and first quest setup |
| [Creating Quests](Creating-Quests.md) | Quest YAML format and configuration |
| [Objectives](Objectives.md) | All 37 objective types with examples |
| [Rewards](Rewards.md) | Reward types and configuration |
| [Conditions](Conditions.md) | Quest conditions reference |
| [Commands and Permissions](Commands-and-Permissions.md) | Every command and permission |
| [GUI System](GUI-System.md) | Quest Browser and Active Quests GUIs |
| [Tiers and Difficulties](Tiers-and-Difficulties.md) | Tier and difficulty setup |
| [Placeholders](Placeholders.md) | PlaceholderAPI placeholders |
| [Daily and Weekly Quests](Daily-and-Weekly-Quests.md) | Rotating quest schedules *(Premium)* |
| [Random Quest Generator](Random-Quest-Generator.md) | Random quest generation *(Premium)* |
| [Quest Loot System](Quest-Loot-System.md) | Mob drops and chest loot *(Premium)* |
| [Examples](Examples.md) | Copy-paste quest examples |
| [FAQ](FAQ.md) | Common questions and answers |
| [Default Configs](Default-Configs.md) | Default config file contents |
| [Changelog](CHANGELOG.md) | Version history |

---

## Changelog

See `V1.0.1 Changelog.txt` in the plugin release package, or the [1.0.1 release notes](https://github.com/AlternativeSoap/SoapsQuest/blob/main/docs/CHANGELOG.md) in this repo.

---

## Support

- GitHub: [AlternativeSoap/SoapsQuest](https://github.com/AlternativeSoap/SoapsQuest)
- Discord: [discord.gg/SoapsUniverse](https://discord.gg/SoapsUniverse)
