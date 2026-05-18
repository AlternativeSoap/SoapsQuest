# FAQ

Frequently asked questions about SoapsQuest.

---

## General

### What server software does SoapsQuest support?

SoapsQuest requires **Paper 1.21+** or any Paper fork (like Purpur). It does not run on Spigot or CraftBukkit because it uses Paper-specific APIs.

### What Java version do I need?

Java 21 or newer.

### Does SoapsQuest need a database?

No. All data is stored in flat YAML files inside the plugin folder. No external database setup is required.

### How do quest papers work?

Every quest is a physical paper item in the player's inventory. Players can hover over it to see their progress, and right-click it when all objectives are done to claim the reward. If a player loses their paper, an admin can re-give it with `/sq give <player> <questid>`.

---

## Setup and Configuration

### How do I install SoapsQuest?

1. Install **SoapsCommon**, then download SoapsQuest (Free or Premium) from [SoapsUniverse.com](https://soapsuniverse.com) or [MythicCraft.io](https://mythiccraft.io) and place the jar in your server's `plugins/` folder
2. Start (or restart) the server
3. Edit `plugins/SoapsQuest/quests.yml` to define quests
4. Run `/sq reload`

See the [Getting Started](Getting-Started.md) guide for more detail.

### How do I create a quest?

Define quests in `plugins/SoapsQuest/quests.yml`. Each quest needs a unique ID, at least one objective, and a reward. See [Creating Quests](Creating-Quests.md) and [Examples](Examples.md).

### How do I reload changes?

Use `/sq reload`. This reloads all config files without restarting the server.

### Can I use hex colors and gradients?

Yes. SoapsQuest supports MiniMessage formatting in display names, lore, and item names. Examples:

- Hex color: `<#FF5555>Red text`
- Gradient: `<gradient:#55FF55:#55FFFF>Gradient text</gradient>`
- Bold: `<bold>Bold text</bold>`

Legacy `&` color codes also work.

---

## Quests

### Can players hold multiple quests at once?

Yes. The maximum number of active quests per player is configurable in `config.yml` under `max-active-quests`.

### Can I chain quests together?

Yes. Add `quest: next_quest_id` in the reward section. When the player completes the first quest, they automatically receive the next one.

### What happens if a player loses their quest paper?

An admin can re-give it with `/sq give <player> <questid>`. Progress is saved — the player keeps their existing progress.

### Can I lock a quest to one player?

Yes. Set `lock-to-player: true` in the quest config. The quest paper will bind to the first player who receives it.

### Can objectives be done in a specific order?

Yes. Set `sequential: true` in the quest config. Objectives will be tracked one at a time in the order they're listed.

### How do milestone notifications work?

Add `milestones: [25, 50, 75]` to a quest. Players will get a message when they reach those percentage thresholds of overall quest completion.

---

## Rewards

### What reward types are available?

- `xp:` — experience points
- `money:` — Vault economy money (requires Vault)
- `items:` — item rewards with optional enchantments and drop chance
- `commands:` — server commands (use `{player}`, `<player>`, or `%player%` for the player name)
- `quest:` — give another quest as a chain reward

### How do I add or remove rewards in-game?

Use these commands:
- `/sq addreward <quest> <type> <value>` — add a reward
- `/sq removereward <quest> <type>` — remove a reward
- `/sq listreward <quest>` — view rewards on a quest

### Do item rewards support enchantments?

Yes. Add an `enchantments` list to the item:

```yaml
items:
  - material: DIAMOND_SWORD
    name: "<#55FFFF>Cool Sword"
    enchantments:
      - "SHARPNESS:3"
      - "UNBREAKING:2"
    chance: 100
```

### What does the `chance` field do on item rewards?

It's a percentage (1-100) controlling the drop chance. Use `100` for a guaranteed reward.

---

## Permissions

### What permission do players need to use the Quest Browser GUI?

`soapsquest.gui.browser` — this lets players open the quest browser with `/sq browse`.

### What permission do players need to see their active quests?

`soapsquest.gui.myquests` — this lets players use `/sq active` to open the active quests GUI.

### What's the admin permission?

`soapsquest.admin` grants full access to all admin commands (give, remove, reset, complete, copy, reload, etc).

### Where can I see all permissions?

See the full list on the [Commands and Permissions](Commands-and-Permissions.md) page.

---

## Integrations

### How do I set up Vault money rewards?

1. Install [Vault](https://www.spigotmc.org/resources/vault.34315/) and an economy plugin (like EssentialsX)
2. Add `money: <amount>` in your quest reward section

### How do I use PlaceholderAPI?

1. Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
2. SoapsQuest will automatically register its placeholders
3. See the [Placeholders](Placeholders.md) page for all available placeholders

### How do I use MythicMobs objectives?

1. Install [MythicMobs](https://www.spigotmc.org/resources/mythicmobs.5702/)
2. Use the `kill_mythicmob` objective type with the MythicMob's internal name as the target:

```yaml
objectives:
  - type: kill_mythicmob
    target: SkeletonKing
    amount: 1
```

The target name must match the MythicMobs mob name exactly (case-sensitive).

---

## Troubleshooting

### My quest doesn't seem to be tracking progress

- Run `/sq reload` and read the chat message — it reports how many quests loaded. Fix any errors shown before reload completes.
- Check the console for validation warnings (wrong objective fields, missing `command` vs `target`, etc.)
- Make sure the objective type and fields are correct (see [Objectives](Objectives.md))
- The quest paper must be in the player's inventory (any slot)
- Only **active** quests track progress. If the player has more papers than `max-active-quests`, extra papers are **queued** and will not progress until they become active.
- Check if **conditions** block progress (wrong world, gamemode, permission, etc.)
- For **sequential** quests, only the current objective in the list counts

### Players can't open the Quest Browser

Make sure they have the `soapsquest.gui.browser` permission. Check with `/sq debug` if you have admin permissions.

### The quest paper disappeared

Quest papers are normal inventory items. If a player dies and doesn't recover their items, the paper is gone. An admin can re-give it with `/sq give <player> <questid>`. Consider using a keep-inventory plugin or the `lock-to-player` option.

### The command objective does not progress

- Use the `command` field (not `target`). Example: `command: help` with `amount: 5`, then run `/help` five times.
- The quest must be **active** (not queued). Check `/sq active`.
- Test with `/sq give <player> showcase_command`.

### The placeholder objective does not progress

- **PlaceholderAPI** must be installed.
- The placeholder must return a **number** (SoapsQuest reads the numeric value until it reaches `amount`).
- Use `placeholder: player_level` — with or without `%` wrappers.
- Test with `/sq give <player> showcase_placeholder`.

### explore_biome does not count

- Walk into the biome (movement must change blocks). Standing still does not count.
- Use biome names like `plains`, `jungle`, or `DEEP_OCEAN` (registry keys work; uppercase legacy names still work).
- Test with `/sq give <player> showcase_explore_biome`.

### What is the difference between Free and Premium?

| Feature | Free | Premium |
|---------|------|---------|
| 37 objectives, papers, rewards, conditions | Yes | Yes |
| Quest browser & active quests GUI | Yes | Yes |
| Random quest generator | No | Yes |
| Daily / weekly quests | No | Yes |
| Quest loot (mob/chest drops) | No | Yes |
| In-game quest editor (`/sq editor`) | No | Yes |

Download the edition you need from [SoapsUniverse.com](https://soapsuniverse.com) or [MythicCraft.io](https://mythiccraft.io).

### Commands aren't working

- Make sure you're using `/sq` (the only alias)
- Check that you have the correct permissions
- Run `/sq help` to see available commands for your permission level

### How do I check my plugin version?

Run `/sq info` to see the current version and plugin details.
