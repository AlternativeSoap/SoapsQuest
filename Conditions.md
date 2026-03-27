# Conditions

Conditions are optional requirements that players must meet before they can start a quest. If a player does not meet the conditions, the quest paper appears as a locked item in the quest browser. The player can see which quest it is but cannot pick it up until they qualify.

---

## How Locking Works

1. The player opens the quest browser with `/sq browse`.
2. Any quest they do not qualify for shows up as a locked paper item.
3. The item tooltip lists the requirements they have not met.
4. Once they meet all the requirements, they can click the quest to receive the paper normally.

Conditions check automatically. Players do not need to do anything to "unlock" a quest; it happens as soon as they meet the criteria.

---

## Adding Conditions to a Quest

Conditions go in the `conditions` section of your quest:

```yaml
my_quest:
  display: "Expert Quest"
  objectives:
    - type: kill
      target: ENDER_DRAGON
      amount: 1
  reward:
    xp: 5000
  conditions:
    min-level: 30
    permission: "rank.veteran"
```

You can combine as many conditions as you like. The player must meet **all** of them to start the quest.

---

## All Condition Types

### Minimum Level

Require the player to be at a certain experience level or above.

```yaml
conditions:
  min-level: 20
```

The player must be at least level 20.

### Maximum Level

The player must be at or below a certain level. Useful for quests meant for early-game players.

```yaml
conditions:
  max-level: 10
```

### Money Cost

Require the player to pay an amount of money to start the quest.

> **Note:** This requires the **Vault** plugin. If Vault is not installed, this condition is ignored.

```yaml
conditions:
  cost: 500
```

The money is taken from the player when they pick up the quest paper. If they cannot afford it, the quest stays locked.

### Item Cost

Require the player to have a specific item in their inventory.

```yaml
conditions:
  item: "DIAMOND:5"
```

This requires the player to have 5 diamonds.

By default, the item is taken from the player when they start the quest. If you want the player to keep the item and just prove they have it, add `consume-item: false`:

```yaml
conditions:
  item: "DIAMOND:5"
  consume-item: false
```

### Permission

Require the player to have a specific permission node. Use this with a permissions plugin like LuckPerms.

```yaml
conditions:
  permission: "rank.vip"
```

Players without this permission see the quest as locked.

### World

Require the player to be in a specific world when they pick up the quest.

```yaml
conditions:
  world: ["world", "world_nether"]
```

The player must be in one of the listed worlds at the time they try to start the quest.

### Gamemode

Require the player to be in a specific game mode.

```yaml
conditions:
  gamemode: ["SURVIVAL"]
```

Options are `SURVIVAL`, `CREATIVE`, `ADVENTURE`, and `SPECTATOR`. Use a list to allow multiple game modes.

### Active Quest Limit

Limit how many active quests a player can have at once.

```yaml
conditions:
  active-limit: 1
```

If the player already has this many active quests (or more), they cannot pick up this one. Setting it to `1` means they can only work on one quest at a time.

### Completed Quests Required

Require the player to have already completed certain quests.

```yaml
conditions:
  completed-quests:
    - "starter_quest"
    - "chapter_1"
```

The player must have completed all listed quests before this one becomes available. This is another way to build quest chains where quests unlock in order.

---

## Combining Conditions

You can use multiple conditions together. The player must meet every single one:

```yaml
conditions:
  min-level: 25
  cost: 1000
  permission: "rank.member"
  world: ["world"]
  gamemode: ["SURVIVAL"]
  active-limit: 1
```

In this example, a player needs to be at least level 25, able to pay 1000 coins, have the `rank.member` permission, be in the `world` world, be in survival mode, and not already have an active quest.

---

## How Locked Papers Look

When a quest is locked, the quest paper item in the browser shows a lock icon or color change with the unmet conditions listed in the tooltip. Players can read exactly what they need to do to unlock it.

The exact appearance of locked papers can be customized in `messages.yml`.
