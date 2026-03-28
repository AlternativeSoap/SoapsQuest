# Introduction

SoapsQuest is a quest plugin for Paper 1.21+ servers. It uses physical quest paper items in player inventories to track progress — no NPCs, no abstract menus, no invisible state. Players hold their quests, and right-click them to complete or claim rewards.

---

## Key Concepts

### Quest Papers

Every quest is a physical paper item in the player's inventory. When a player receives a quest, they get an item they can hover over to see progress, and right-click to claim rewards when complete. Lost papers can be re-given by an admin.

### Objectives

Each quest has one or more objectives — the tasks players need to complete. There are **37 objective types** covering combat, mining, building, crafting, farming, movement, exploration, leveling, and more. See the [Objectives](Objectives.md) page for the full list.

### Rewards

When all objectives are done, players right-click their quest paper to claim rewards. Rewards include commands, items, money (via Vault), experience, and quest chains (giving another quest as a reward). See the [Rewards](Rewards.md) page.

### Conditions

Quests can have conditions that control when or where they can be started or progressed. Conditions include world restrictions, permission checks, time-of-day, weather, biome, and more. See the [Conditions](Conditions.md) page.

### Tiers and Difficulties

Organize quests into tiers (like "Beginner", "Advanced") and assign difficulty levels that affect how quests appear in GUIs and messages. See the [Tiers and Difficulties](Tiers-and-Difficulties.md) page.

---

## Features

### Core (Free)

- **37 Objective Types** — kill, break, place, craft, fish, tame, move, enchant, brew, trade, breed, shear, harvest, explore biomes, and many more
- **Physical Quest Papers** — quests live in inventory as items with progress info on hover
- **Flexible Reward System** — commands, items, money, XP, and quest chains
- **Condition System** — restrict quests by world, permission, biome, time, weather, level, and more
- **Tiers and Difficulties** — organize and label quests with custom categories
- **Quest Browser GUI** — players browse and pick up available quests from an in-game menu
- **Active Quests GUI** — players view their current active quests
- **Sequential Objectives** — force objectives to complete in order within a quest
- **MiniMessage Formatting** — full MiniMessage support for all display text (gradients, hex colors, hover/click events)
- **PlaceholderAPI Support** — expose quest data for scoreboards, holograms, and other plugins
- **MythicMobs Support** — track kills of custom MythicMobs creatures
- **Vault Integration** — use money as a reward type
- **Multiple Quests** — players can hold multiple active quests at once (configurable limit)
- **Admin Commands** — give, remove, reset, complete, copy, and manage quests from the command line
- **Player Commands** — abandon quests, view active quests, browse available quests
- **Statistics Tracking** — track quests completed per player
- **Config Validation** — warns on load if quest configs have errors
- **Data Cleanup** — automatic removal of stale player data
- **Prevent Workstation Exploits** — optional setting to prevent cartography table use on quest papers

### Premium

- **Random Quest Generator** — generate random quests from configurable templates, weighted pools, and rarity settings
- **Daily and Weekly Quests** — quests that automatically rotate on a schedule, with configurable reset times
- **Quest Loot System** — quest papers drop from mobs and appear in loot chests
- **In-Game Quest Editor GUI** — create and edit quests directly from a GUI inside the game

---

## Requirements

- **Paper 1.21+** (or any Paper fork like Purpur)
- **Java 21+**

### Optional Dependencies

- **Vault** — required for money rewards
- **PlaceholderAPI** — required for quest placeholders and the `placeholder` objective type
- **MythicMobs** — required for the `kill_mythicmob` objective type

---

## Quick Links

- [Getting Started](Getting-Started.md)
- [Creating Quests](Creating-Quests.md)
- [Objectives](Objectives.md)
- [Rewards](Rewards.md)
- [Conditions](Conditions.md)
- [Commands and Permissions](Commands-and-Permissions.md)
- [GUI System](GUI-System.md)
- [Tiers and Difficulties](Tiers-and-Difficulties.md)
- [Placeholders](Placeholders.md)
- [Daily and Weekly Quests](Daily-and-Weekly-Quests.md) *(Premium)*
- [Random Quest Generator](Random-Quest-Generator.md) *(Premium)*
- [Quest Loot System](Quest-Loot-System.md) *(Premium)*
- [Examples](Examples.md)
- [FAQ](FAQ.md)
- [Default Configs](Default-Configs.md)
