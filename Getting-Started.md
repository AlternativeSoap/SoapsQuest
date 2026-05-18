# Getting Started

How to install SoapsQuest and run your first quest.

## Requirements

- **Paper 1.21+** (Purpur is fine; Spigot/CraftBukkit are not supported)
- **Java 21+**

Optional:

- **Vault** for money rewards
- **PlaceholderAPI** for placeholders and the `placeholder` objective
- **MythicMobs** for `kill_mythicmob`

## Installation

1. Install **SoapsCommon** from https://soapsuniverse.com or https://mythiccraft.io
2. Download **SoapsQuest** (Free or Premium) from the same site
3. Put both jars in your `plugins` folder
4. **Restart** the server (skip `/reload` on first install)
5. Check `plugins/SoapsQuest/` for the default configs and example quests

**Premium** adds the random generator, daily/weekly quests, loot drops, and `/sq editor`. **Free** has everything else, including all 37 objectives.

**Tip:** In `quests.yml` there are `showcase_*` quests (e.g. `showcase_kill`, `showcase_command`) you can give with `/sq give <player> showcase_kill` to test one objective type at a time.

## Your first quest

**Give a quest by command**

```
/sq give <yourname> lumberjack
```

Chop oak logs with the paper in your inventory. When the lore says you are done, right-click the paper.

**Browse quests in-game**

```
/sq browse
```

Pick a quest from the GUI to receive the paper.

**Admins:** `/sq give <player> <questid>` or `/sq editor` (Premium only).

## Default example quests

| Quest ID | Task |
|----------|------|
| lumberjack | Chop 20 oak logs |
| zombie_slayer | Kill 15 zombies |
| gone_fishing | Catch 10 fish |
| iron_miner | Mine 30 iron ore, smelt 20 ingots |
| mob_hunter | Kill zombies, skeletons, spiders |
| baker | Harvest wheat, craft bread |
| shepherd | Shear 15 sheep |
| diamond_rush | Mine diamond ore, craft diamond pickaxe |
| nether_explorer | Blazes, nether wart, brew (needs level 15) |
| master_builder | Sequential build quest |

Edit or delete these in `plugins/SoapsQuest/quests.yml`.

## What players see

1. They get a quest paper (command, GUI, loot, etc.)
2. The paper stays in inventory; any slot counts
3. Progress shows in chat/action bar and on the paper lore
4. When finished, the paper glows and chat says to right-click
5. Right-click to get rewards; the paper is removed

## Paper states

| Look | Meaning |
|------|---------|
| Normal | Active, in progress |
| Enchanted glow | Done, ready to claim |
| Locked (in browser) | Player does not meet conditions yet |

## Useful commands

| Command | What it does |
|---------|----------------|
| `/sq give <player> <questid>` | Give a quest paper |
| `/sq browse` | Quest browser GUI |
| `/sq list` | List quest IDs in chat |
| `/sq reload` | Reload configs |
| `/sq info` | Version and Free/Premium |

Full list: [Commands and Permissions](Commands-and-Permissions.md).

## Config files

| File | Purpose |
|------|---------|
| `config.yml` | General settings |
| `quests.yml` | Your quests (+ showcase examples) |
| `messages.yml` | Plugin messages |
| `tiers.yml` | Tier names and colors |
| `difficulties.yml` | Difficulty levels |
| `gui.yml` | Browser GUI layout |
| `daily.yml` | Daily/weekly (Premium) |
| `quest-loot.yml` | Mob/chest loot (Premium) |
| `random-generator.yml` | Generator (Premium) |

Defaults are listed in [Default Configs](Default-Configs.md).
