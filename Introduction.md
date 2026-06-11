# Introduction

SoapsQuest turns quests into physical items. Each quest is a **quest paper** in the player's inventory. Lore updates as objectives are completed. When everything is done, the player right-clicks the paper to claim rewards.

This wiki is written for **server owners** configuring SoapsQuest on a live network.

---

## Core concepts

**Quest papers**  
A quest paper is a normal inventory item with custom name and lore. Progress is stored per paper instance, not only per player name.

**Objectives**  
Tasks defined in YAML (kill mobs, break blocks, travel distance, and 34 more types). See [Objectives](Objectives.md).

**Active vs Queued**  
If a player holds multiple papers for the **same quest ID**, only one copy is **Active** and gains progress. Extra copies are **Queued** until the active copy is claimed or removed. **Different quest IDs** can all be Active at the same time.

**Conditions**  
Optional gates before a player can pick up or start a quest (level, money, completed quests, permissions, and more). See [Conditions](Conditions.md).

**Tiers and difficulties**  
Cosmetic rarity labels plus scaling for generated content. See [Tiers and Difficulties](Tiers-and-Difficulties.md).

**Sigils**  
SoapsQuest's built-in currency stored in `sigils.yml`. Used in rewards and unlock conditions on both Free and Premium.

---

## Editions: Free and Premium

SoapsQuest ships as two JAR builds from the same codebase:

- **SoapsQuest-1.0.3-Free.jar** excludes Premium-only code and config templates.
- **SoapsQuest-1.0.3-Premium.jar** includes the full feature set.

On startup, Premium logs `Premium features unlocked`. Free logs which features are unavailable.

### Feature matrix

| Feature | Free | Premium |
|---------|:----:|:-------:|
| Quest papers and progress tracking | Yes | Yes |
| All 37 objective types | Yes | Yes |
| `quests.yml` manual authoring | Yes | Yes |
| Showcase quests (`showcase_*`) | Yes | Yes |
| Quest browser GUI (`/sq browse`) | Yes | Yes |
| Active quests GUI (`/sq active`) | Yes | Yes |
| Chat, action bar, or boss bar progress display | Yes | Yes |
| Tiers (`tiers.yml`) | Yes | Yes |
| Difficulties (`difficulties.yml`) | Yes | Yes |
| Conditions (level, money, sigils, items, etc.) | Yes | Yes |
| Sigil rewards in `quests.yml` | Yes | Yes |
| Sigil conditions (`min-sigils`, `sigil-cost`) | Yes | Yes |
| Vault money rewards | Yes | Yes |
| PlaceholderAPI expansion | Yes | Yes |
| MythicMobs `kill_mythicmob` objective | Yes* | Yes* |
| Anti-cheat (placed-block tracking) | Yes | Yes |
| In-game quest editor (`/sq editor`) | No | Yes |
| Random quest generator (`/sq generate`) | No | Yes |
| `random-generator.yml` and `generated.yml` | No | Yes |
| Daily and weekly recurring quests (`daily.yml`) | No | Yes |
| Quest loot in chests and mob drops (`quest-loot.yml`) | No | Yes |
| `/sq sigils` balance management | No | Yes |
| `/sq drop` to place unbound papers in the world | No | Yes |
| `/sq addreward ... sigils` command | No | Yes |

\*Requires MythicMobs installed on the server.

---

## Optional integrations

| Plugin | Used for |
|--------|----------|
| **Vault** | Money rewards and `cost` / `min-money` conditions |
| **PlaceholderAPI** | `placeholder` objectives, condition expressions, message placeholders |
| **MythicMobs** | `kill_mythicmob` objective |
| **SoapsCommon** | Shared GUI framework and startup checks (required) |

---

## What to read next

1. [Getting Started](Getting-Started.md) if you are installing for the first time.
2. [Creating Quests](Creating-Quests.md) to author your first quest.
3. [Commands and Permissions](Commands-and-Permissions.md) before handing commands to staff.

---

*Version 1.0.3*
