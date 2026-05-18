# Introduction

SoapsQuest is a quest plugin for Paper 1.21+ servers. Progress is tracked on **quest papers** in the player's inventory. No NPC quest givers and no separate quest menu. Players hold the paper, do the tasks, and right-click to claim rewards.

## Key concepts

### Quest papers

Each quest is a paper item. Players can read progress in the lore and right-click when everything is done. If someone loses the paper, an admin can run `/sq give <player> <questid>` and progress is kept.

### Objectives

Objectives are the tasks on the quest (kill mobs, mine blocks, run a command, etc.). There are **37 types**. See [Objectives](Objectives.md).

### Rewards

After all objectives are done, the player right-clicks the paper to get rewards: items, XP, money (Vault), commands, or another quest. See [Rewards](Rewards.md).

### Conditions

You can require a level, permission, world, money, completed quests, and more before a player can pick up a quest. See [Conditions](Conditions.md).

### Tiers and difficulties

Tiers group quests (e.g. Beginner, Expert). Difficulties change how quests show in lists and GUIs. See [Tiers and Difficulties](Tiers-and-Difficulties.md).

## What is included

### Free

- 37 objective types (kill, break, craft, fish, move, command, placeholder, and more)
- Physical quest papers with live progress on the item
- Rewards, conditions, tiers, difficulties
- Quest browser and active quests GUI
- Sequential objectives (one step at a time)
- MiniMessage colors and gradients
- PlaceholderAPI placeholders
- MythicMobs kill objectives
- Vault money rewards
- Admin commands (give, reset, reload, etc.)

### Premium

Everything in Free, plus:

- Random quest generator
- Daily and weekly quests
- Quest papers from mob kills and chest loot
- In-game quest editor (`/sq editor`)

## Requirements

- Paper 1.21+ (Purpur works)
- Java 21+

Optional:

- **Vault** for money rewards
- **PlaceholderAPI** for placeholders and the `placeholder` objective
- **MythicMobs** for `kill_mythicmob`

## More reading

- [Getting Started](Getting-Started.md)
- [Creating Quests](Creating-Quests.md)
- [Objectives](Objectives.md)
- [Rewards](Rewards.md)
- [Conditions](Conditions.md)
- [Commands and Permissions](Commands-and-Permissions.md)
- [GUI System](GUI-System.md)
- [Examples](Examples.md)
- [FAQ](FAQ.md)
- [Changelog](CHANGELOG.md)

Premium guides: [Daily and Weekly Quests](Daily-and-Weekly-Quests.md), [Random Quest Generator](Random-Quest-Generator.md), [Quest Loot System](Quest-Loot-System.md).
