# Introduction

SoapsQuest is a quest system built around one core idea: **quests are physical items**. When a player receives a quest, they get an actual item — a quest paper — in their inventory. They carry it, right-click it to check progress, and right-click again when it's done to claim rewards. No floating UI, no invisible background tasks. It's tangible.

---

## How It Works

### The Flow

1. **Player receives a quest paper** — through a command, the GUI browser, a mob drop, or a chest
2. **The quest activates** — the paper goes into their inventory and starts tracking progress
3. **Player works on objectives** — kill mobs, mine blocks, fish, craft, or any of 30+ objective types
4. **Progress updates live** — shown in the action bar, boss bar, or chat
5. **Quest completes** — the paper updates its lore to show it's done
6. **Player right-clicks to claim** — rewards are given, the paper is consumed

That's it. Simple for the player, deeply configurable for the server owner.

---

## Feature Overview

### Quest Papers
Every quest is a real item. By default it's paper, but you can change it to a book, map, enchanted book, or any item. Each paper stores its data internally — the quest ID, the owner, the progress, and whether it's been claimed.

Papers can be:
- **Locked to a player** — only the original owner can use it
- **Tradeable** — anyone who picks it up can continue the quest
- **Dropped or stored** — configurable abandon behavior when dropped or put in a chest

### Objective Types
Over 30 objective types covering every major gameplay action:

| Category | Types |
|:---------|:------|
| **Combat** | Kill mobs, deal damage, shoot arrows, launch projectiles |
| **Mining & Building** | Break blocks, place blocks, interact with blocks |
| **Collection & Crafting** | Collect items, craft, smelt, enchant, brew |
| **Farming & Animals** | Breed, tame, shear, trade with villagers |
| **Survival** | Eat food, sleep, heal, die, drop items |
| **Movement** | Walk distance, jump, ride vehicles |
| **Leveling** | Reach a level, gain levels |
| **Special** | Send chat messages, launch fireworks, kill MythicMobs |

### Multi-Objective Quests
Quests can have multiple objectives. Two modes:
- **Parallel** — all objectives can be worked on at the same time
- **Sequential** — objectives must be completed in order, one after the other

### Tier System
Quests have a rarity tier that affects their display and how often they appear in random generation:

| Tier | Weight |
|:-----|:-------|
| Common | 40 |
| Uncommon | 32 |
| Rare | 25 |
| Epic | 18 |
| Legendary | 12 |
| Mythic | 8 |

You can add your own tiers or modify existing ones.

### Difficulty System
Each quest has a difficulty that scales objective amounts and reward values:

| Difficulty | Objective Scale | Reward Scale |
|:-----------|:---------------|:-------------|
| Easy | 0.75x | 0.75x |
| Normal | 1.0x | 1.0x |
| Hard | 1.5x | 1.5x |
| Expert | 2.0x | 2.0x |
| Nightmare | 2.5x | 2.5x |

A "Kill 20 Zombies" quest on **Hard** becomes "Kill 30 Zombies" with 1.5x the rewards.

### Random Quest Generator
Generate unlimited unique quests from weighted templates. The generator picks random objectives, tiers, difficulties, display names, and rewards — all configurable. Great for keeping content fresh without manually writing hundreds of quests.

### Daily & Weekly Quests
Set up automatic quest rotation. Pick a pool of quests, set a reset time, and the plugin handles everything — distributing quests on reset and notifying players.

### Quest Loot
Quest papers can drop from mobs and appear in chests. Configure drop chances per mob type, per world, with min/max amounts. Boss mobs can have 100% drop rates with multiple papers.

### In-Game GUI
A full quest browser and editor — right from the game. Browse quests, claim papers, or create and edit quests without ever opening a config file.

### Conditions System
Quests can have requirements before they can be started:
- Minimum XP level
- Money cost (deducted on unlock)
- Item cost (consumed on unlock)
- World restrictions
- Permission requirements
- Gamemode restrictions
- And more

### Rewards
Five reward types:
- **XP** — experience points
- **Money** — economy currency (Vault)
- **Items** — with custom names, enchantments, and drop chances
- **Commands** — any server command
- **Quest** — award another quest paper, creating quest chains

### Statistics
Track player quest completions, broken down by tier and difficulty. View your own or other players' stats in-game.

### PlaceholderAPI
Integrate quest stats into scoreboards, tab lists, or anywhere PlaceholderAPI is supported.

---

## Optional Dependencies

| Plugin | What it adds |
|:-------|:-------------|
| **Vault** | Economy support — money rewards and money conditions |
| **PlaceholderAPI** | Placeholder expansion for external plugins |
| **MythicMobs** | `kill_mythicmob` objective type for custom mobs |

None of these are required. SoapsQuest works fully standalone.

---

## Next Steps

→ [Getting Started](Getting-Started) — Install the plugin and set up your first quest
