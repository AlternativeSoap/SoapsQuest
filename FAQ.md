# FAQ

Common questions from server owners running SoapsQuest.

## General

### What server software does SoapsQuest support?

**Paper 1.21+** or a Paper fork like Purpur. It does not run on Spigot or CraftBukkit.

### What Java version do I need?

Java 21 or newer.

### Does SoapsQuest need a database?

No. Player and quest data are stored in YAML files under `plugins/SoapsQuest/`.

### How do quest papers work?

Each quest is a real item in the player's inventory. Lore shows progress. When objectives are done, the player right-clicks the paper to claim rewards. Lost papers can be replaced with `/sq give <player> <questid>`; progress is saved.

## Setup

### How do I install SoapsQuest?

1. Install **SoapsCommon**
2. Download SoapsQuest (Free or Premium) from https://soapsuniverse.com or https://mythiccraft.io
3. Put the jars in `plugins/` and restart
4. Edit `plugins/SoapsQuest/quests.yml`
5. Run `/sq reload`

Details: [Getting Started](Getting-Started.md).

### How do I create a quest?

Add a quest block in `quests.yml` with an ID, `display`, at least one objective, and a `reward`. See [Creating Quests](Creating-Quests.md) and [Examples](Examples.md).

### How do I reload changes?

`/sq reload` reloads configs without a full server restart. Fix any errors it prints before expecting quests to work.

### Can I use hex colors and gradients?

Yes. MiniMessage works in names and lore, for example `<#FF5555>Red` or `<gradient:#55FF55:#55FFFF>Text</gradient>`. Legacy `&` codes also work.

## Quests

### Can players hold multiple quests at once?

Yes, up to `max-active-quests` in `config.yml`. Extra papers wait in a queue until a slot is free. Only **active** papers gain progress.

### Can I chain quests?

Put `quest: next_quest_id` under `reward`. The next paper is given when they claim the first quest.

### What if a player loses their paper?

Run `/sq give <player> <questid>`. Their progress is still stored.

### Can I lock a quest to one player?

Set `lock-to-player: true`. The paper binds to whoever receives it first.

### Can objectives be done in order?

Set `sequential: true`. Only the current objective in the list counts until it is finished.

### How do milestones work?

Add `milestones: [25, 50, 75]` on the quest. Players get a message at those completion percentages.

## Rewards

### What reward types exist?

- `xp:` experience
- `money:` Vault economy (needs Vault)
- `items:` items, optional enchants and `chance`
- `commands:` run commands (`{player}`, `<player>`, or `%player%` for the player name)
- `quest:` give another quest

### In-game reward commands

- `/sq addreward <quest> <type> <value>`
- `/sq removereward <quest> <index>`
- `/sq listreward <quest>`

### Item enchantments on rewards?

```yaml
items:
  - material: DIAMOND_SWORD
    name: "<#55FFFF>Cool Sword"
    enchantments:
      - "SHARPNESS:3"
    chance: 100
```

`chance` is 1-100 (100 = always).

## Permissions

### Quest browser GUI

`soapsquest.gui.browser` for `/sq browse`.

### Active quests GUI

`soapsquest.gui.myquests` for `/sq active`.

### Admin access

`soapsquest.admin` for give, remove, reset, reload, and similar commands.

Full list: [Commands and Permissions](Commands-and-Permissions.md).

## Integrations

### Vault money

Install Vault and an economy plugin (e.g. EssentialsX). Use `money: <amount>` in rewards.

### PlaceholderAPI

Install PlaceholderAPI. SoapsQuest registers its own placeholders. List: [Placeholders](Placeholders.md).

### MythicMobs kills

```yaml
objectives:
  - type: kill_mythicmob
    target: SkeletonKing
    amount: 1
```

`target` must match the MythicMobs internal mob name exactly.

## Troubleshooting

### Quest progress not updating

- Run `/sq reload` and read the message (quest count and errors)
- Check the console for validation warnings on objectives
- Confirm the objective `type` and fields match [Objectives](Objectives.md) (`command` uses `command:`, not `target`)
- Paper must be in inventory
- Quest must be **active**, not queued (see `max-active-quests`)
- Conditions (world, gamemode, permission) can block progress
- With `sequential: true`, only the current step counts

### Players cannot open the browser

Give `soapsquest.gui.browser`. Admins can use `/sq debug toggle` for more logging.

### Paper disappeared

Quest papers are normal items. Death without keep-inventory can lose them. Re-give with `/sq give`. Consider `lock-to-player: true` or a keep-inventory plugin.

### Command objective stuck

- Use `command: help` not `target: help`
- Quest must be active (`/sq active`)
- Test: `/sq give <player> showcase_command` then run `/help` repeatedly

### Placeholder objective stuck

- PlaceholderAPI must be installed
- The placeholder must return a **number**
- Example: `placeholder: player_level` (with or without `%`)
- Test: `showcase_placeholder`

### explore_biome not counting

- Player must walk into the biome (not stand still)
- Try `plains`, `jungle`, or `DEEP_OCEAN`
- Test: `showcase_explore_biome`

### Free vs Premium

| Feature | Free | Premium |
|---------|------|---------|
| 37 objectives, papers, rewards, conditions | Yes | Yes |
| Browser + active GUIs | Yes | Yes |
| Random generator | No | Yes |
| Daily / weekly | No | Yes |
| Mob/chest loot | No | Yes |
| `/sq editor` | No | Yes |

Download from https://soapsuniverse.com or https://mythiccraft.io.

### Commands not working

Use `/sq` (alias). Check permissions. Run `/sq help`.

### Plugin version

`/sq info`
