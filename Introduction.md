# Introduction to SoapsQuest

SoapsQuest adds a physical quest system to your Minecraft server. Instead of typed commands or popup menus, players receive actual items called quest papers. These papers sit in the player inventory and track progress automatically as players play normally.

---

## How It Works

Here is the basic flow from start to finish:

1. **A player receives a quest paper.** This can happen through a command, by picking it up off the ground after killing a mob, or by opening a chest that contains one.
2. **The paper goes in their inventory.** Progress is tracked automatically. Players do not need to do anything special except play normally and do what the quest asks.
3. **When all tasks are done, the paper glows.** The plugin sends a message telling the player to right-click the paper to collect their rewards.
4. **The player right-clicks the paper in their hand.** Rewards are given, and the paper disappears.

That is the whole system. Players just carry the paper and play. Everything else happens in the background.

---

## Feature Overview

**Quest Papers**

Quests are physical item stacks. The item tooltip shows the quest name, what needs to be done, and the current progress. Players always know what they are working on just by hovering over the item.

**30+ Objective Types**

Players can be asked to do nearly anything. Kill mobs, mine blocks, break crops, craft items, smelt ore, fish, brew potions, tame animals, breed animals, ride vehicles, travel distances, reach an experience level, gain levels, eat food, sleep in a bed, and more.

**Multiple Objectives**

A single quest can require several different tasks. You can also make objectives go in order (called a sequential quest), so the second task only unlocks after the first one is finished. This lets you build quests with a story-like structure.

**Milestones**

Quests can alert players when they hit certain progress points (like 25%, 50%, or 75% complete). This keeps players engaged on longer quests so they know they are making progress.

**Tier System**

Every quest has a rarity tier, such as Common, Rare, or Legendary. Tiers are cosmetic labels by default, but they also control things like how often a tier shows up when using the random generator and how much rewards scale.

**Difficulty System**

Every quest also has a difficulty level, such as Easy, Hard, or Nightmare. Difficulty uses multipliers to automatically scale how much players need to do and how much they earn as rewards.

**Conditions and Locking**

You can put requirements on any quest. A quest can require a minimum player level, a set amount of money, a specific item in the player inventory, a permission node, a specific world, or a specific game mode. Quests with unmet conditions appear as locked papers in the browser, and the player cannot start them until they qualify.

**Rewards**

Players can earn experience points, money (requires Vault), specific items with names and enchantments, command outputs (like running a console command when a quest finishes), and even the next quest paper in a series to create quest chains.

**Statistics**

The plugin tracks how many quests each player has completed. Players and admins can check these numbers using a command.

**Progress Display**

Quest progress is shown to the player as they work. You can choose between showing progress in the action bar (just above the hotbar), as a boss bar at the top of the screen, or in chat.

**Quest Browser**

Players can open an in-game GUI to browse all available quests, see what conditions are required, and pick up quest papers directly from the menu. No commands needed from the player side.

**In-Game Quest Editor (Premium)**

> **[PREMIUM]** This feature requires the SoapsQuest Premium version. Get it at [SoapsUniverse.com](https://SoapsUniverse.com)

Admins can create and fully edit quests through a point-and-click in-game GUI. No file editing required.

**Random Quest Generator (Premium)**

> **[PREMIUM]** This feature requires the SoapsQuest Premium version.

The plugin can automatically create quests on demand using pools of objectives, name templates, and reward tables that you configure. Great for servers that want fresh content without hand-crafting every single quest.

**Daily and Weekly Quests (Premium)**

> **[PREMIUM]** This feature requires the SoapsQuest Premium version.

Set specific quests to distribute to players on a timed schedule. Daily quests reset every day at a time you choose. Weekly quests reset once a week. Players receive their papers automatically when they log in.

**Quest Loot System (Premium)**

> **[PREMIUM]** This feature requires the SoapsQuest Premium version.

Quest papers can drop from mobs when players kill them, or appear inside chests when players open them. This turns quest discovery into a natural part of gameplay rather than something players have to seek out.

---

## Optional Add-ons

SoapsQuest works on its own, but installing these plugins enables extra features:

| Plugin | What It Enables |
|--------|-----------------|
| Vault | Money as a reward or as a cost condition to unlock quests |
| PlaceholderAPI | Use SoapsQuest data in scoreboards, holograms, tab lists, and other plugins |
| MythicMobs | The "kill mythic mob" objective type for custom boss quests |

None of these are required. The plugin works fine without them.
