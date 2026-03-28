# Getting Started

This page walks you through installing SoapsQuest and setting up your first quest.

---

## Requirements

Before installing, make sure your server meets these requirements:

- **Paper 1.21 or newer** (or a Paper fork like Purpur — Spigot and CraftBukkit are not supported)
- **Java 21 or newer**

Optional plugins that add extra features:

- **Vault** (for money rewards and costs)
- **PlaceholderAPI** (for placeholders in other plugins)
- **MythicMobs** (for the kill mythic mob objective)

---

## Installation

1. Download the SoapsQuest `.jar` file from [SoapsUniverse.com](https://SoapsUniverse.com) or the plugin page.
2. Place the `.jar` file into your server `plugins` folder.
3. Restart your server. Do not use `/reload`. A full restart is required.
4. SoapsQuest will create a `SoapsQuest` folder inside `plugins` with all the config files.

That is it. The plugin is now running with a set of example quests included.

---

## Your First Quest

SoapsQuest comes with several example quests already set up so you can see how it works right away.

**Option 1: Give a quest using a command**

```
/sq give <yourname> lumberjack
```

This gives you the "Lumberjack" quest paper. Put it in your hand, chop some oak logs, then right-click the paper when the task is done.

**Option 2: Browse quests through the GUI**

```
/sq browse
```

This opens the quest browser where you can see all available quests and click one to receive the paper.

**Option 3: Give yourself a paper from the console or the editor**

If you have admin permissions, you can use `/sq give <player> <questid>` or open the quest editor with `/sq editor` (Premium) to manage quests directly.

---

## The Default Quests

The plugin ships with these example quests to get you started:

| Quest ID | What It Tasks Players With |
|----------|---------------------------|
| lumberjack | Chop 20 oak logs |
| zombie_slayer | Kill 15 zombies |
| gone_fishing | Catch 10 fish |
| iron_miner | Mine 30 iron ore and smelt 20 ingots |
| mob_hunter | Kill 20 zombies, 15 skeletons, and 10 spiders |
| baker | Break 30 wheat and craft 10 bread |
| shepherd | Shear 15 sheep |
| diamond_rush | Mine 10 diamond ore and craft a diamond pickaxe |
| nether_explorer | Kill 25 blazes, collect nether wart, brew potions (requires level 15) |
| master_builder | Place cobblestone, craft stone bricks, place stone bricks, craft glass panes (sequential) |

You can edit or remove these as you like. All quests are defined in `plugins/SoapsQuest/quests.yml`.

---

## How Players Experience Quests

Here is what a player goes through from start to finish:

1. They receive a quest paper, either from a command, a chest drop, or a mob drop.
2. The paper sits in their inventory. They do not need to hold it or do anything special.
3. As they complete the tasks, progress updates appear above the hotbar.
4. When all tasks are done, the paper starts glowing and a message tells them to right-click it.
5. They hold the paper and right-click. Rewards are delivered and the paper is removed.

---

## Quest Paper States

Quest papers change appearance based on their current state:

| State | What It Means |
|-------|---------------|
| Normal paper | Quest is active and in progress |
| Glowing paper | All tasks are done, player can claim rewards |
| Locked paper | The quest has conditions the player has not met yet |

---

## Key Commands for Getting Started

| Command | What It Does |
|---------|-------------|
| `/sq give <player> <questid>` | Give a quest paper to a player |
| `/sq browse` | Open the quest browser GUI |
| `/sq list` | See all available quest IDs in chat |
| `/sq reload` | Reload the plugin config files |

See [Commands and Permissions](Commands-and-Permissions.md) for the full list.

---

## Config Files

After the first run, you will find these files in `plugins/SoapsQuest/`:

| File | What It Controls |
|------|-----------------|
| `config.yml` | Core settings like progress display and autosave interval |
| `quests.yml` | All your quests |
| `messages.yml` | Every message the plugin sends, fully customizable |
| `tiers.yml` | Quest tier names, colors, and weights |
| `difficulties.yml` | Quest difficulty levels and multipliers |
| `gui.yml` | How the quest browser GUI looks |
| `daily.yml` | Daily and weekly quest settings (Premium) |
| `quest-loot.yml` | Mob drops and chest loot settings (Premium) |
| `random-generator.yml` | Random quest generation settings (Premium) |

See [Default Configs](Default-Configs.md) for the full default content of each file.
