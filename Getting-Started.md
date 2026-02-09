# Getting Started

This page walks you through installing SoapsQuest and getting your first quest running in under 5 minutes.

---

## Requirements

- **Minecraft Server:** Paper 1.21 or newer
- **Java:** 21+
- **Optional:** Vault (for money rewards), PlaceholderAPI (for placeholders), MythicMobs (for custom mob objectives)

---

## Installation

1. Download the `SoapsQuest.jar` file
2. Drop it into your server's `plugins/` folder
3. Start (or restart) the server
4. The plugin generates its config files automatically in `plugins/SoapsQuest/`

That's it. The plugin comes with several pre-built quests so you can test immediately.

---

## Your First Quest in 60 Seconds

### Option A: Use the GUI Editor

1. Run `/sq editor` in-game
2. Click the **green emerald block** ("Create New Quest")
3. Type a quest ID in chat (e.g., `my_first_quest`)
4. Edit the display name, objectives, and rewards using the GUI
5. Click **Close** — changes save automatically

### Option B: Give a Pre-Built Quest

The plugin ships with example quests. Try:

```
/sq give YourName lumberjack
```

You'll receive a quest paper. Check your inventory — it's right there. Break 20 oak logs to complete it, then right-click the paper to claim rewards.

### Option C: Browse Available Quests

```
/sq browse
```

Opens the quest browser GUI. Click any quest to receive its paper.

---

## Verifying It Works

Run `/sq list` to see all loaded quests. You should see the default quests:

- **Lumberjack** — Break 20 oak logs
- **Zombie Slayer** — Kill 15 zombies
- **Gone Fishing** — Catch 10 fish
- **Iron Miner** — Mine 30 iron ore, smelt 20 iron ingots
- **Mob Hunter** — Kill zombies, skeletons, and spiders
- **Baker** — Break wheat, craft bread
- **Shepherd** — Shear 15 sheep
- **Diamond Rush** — Mine diamonds, craft a diamond pickaxe
- **Nether Explorer** — Kill blazes, collect nether wart, brew potions
- **Master Builder** — Sequential building quest

---

## The Gameplay Flow

Here's what a player experiences:

```
Receive quest paper → Paper appears in inventory
                           ↓
              Work on objectives (kill, mine, craft, etc.)
                           ↓
              Progress shows in action bar / boss bar
                           ↓
              Milestones trigger at 25%, 50%, 75% (if set)
                           ↓
              All objectives complete → Paper updates
                           ↓
              Right-click paper → Claim rewards
                           ↓
              Paper is consumed, stats are recorded
```

### Quest Paper States

| State | What it means |
|:------|:-------------|
| **Active** | Currently tracking progress |
| **Queued** | Waiting behind another quest of the same type |
| **Claimable** | All objectives done — right-click to claim |
| **Redeemed** | Rewards already claimed |
| **Locked** | Has a cost/requirement — right-click to unlock first |

---

## Key Commands to Know

| Command | What it does |
|:--------|:-------------|
| `/sq help` | Show all commands |
| `/sq browse` | Open the quest browser GUI |
| `/sq editor` | Open the quest editor GUI |
| `/sq list` | List all quests in chat |
| `/sq give <player> <quest>` | Give a quest paper to a player |
| `/sq statistic` | View your quest stats |
| `/sq reload` | Reload all config files |

---

## Config Files Overview

After first launch, you'll find these files in `plugins/SoapsQuest/`:

| File | Purpose |
|:-----|:--------|
| `config.yml` | Core settings — progress display, sounds, performance, behavior |
| `quests.yml` | All your quest definitions |
| `messages.yml` | Every message the plugin sends — fully customizable |
| `gui.yml` | GUI layout and item configuration |
| `tiers.yml` | Tier/rarity definitions |
| `difficulties.yml` | Difficulty levels and scaling multipliers |
| `daily.yml` | Daily and weekly quest rotation |
| `random-generator.yml` | Random quest generation templates |
| `quest-loot.yml` | Mob drop and chest loot settings |

---

## Next Steps

- [Creating Quests](Creating-Quests) — Learn how to build your own quests
- [Commands & Permissions](Commands-and-Permissions) — Full command and permission reference
- [Default Configs](Default-Configs) — See every config file with all options
