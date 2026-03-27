# Rewards

This page explains the different types of rewards you can give players when they complete a quest.

---

## How Rewards Work

Rewards go in the `reward` section of a quest. You can include one type or combine several types together. All rewards are given at once when the player right-clicks the quest paper to claim it.

```yaml
reward:
  xp: 200
  money: 100
  items:
    - material: DIAMOND
      amount: 3
      chance: 100
  commands:
    - "broadcast {player} finished a quest!"
```

---

## Experience Points (XP)

Give the player raw experience points (the kind that fills the XP bar).

```yaml
reward:
  xp: 500
```

You can also make XP a chance reward, so it is not always given:

```yaml
reward:
  xp: 500
  xp-chance: 75
```

This gives 500 XP 75% of the time and nothing 25% of the time.

---

## Money

Give the player in-game currency.

> **Note:** Money rewards require the **Vault** plugin. If Vault is not installed, money rewards are ignored.

```yaml
reward:
  money: 250
```

---

## Items

Give the player one or more items. Items can be basic or fully customized.

**Basic item:**

```yaml
reward:
  items:
    - material: DIAMOND
      amount: 3
      chance: 100
```

**Fully customized item:**

```yaml
reward:
  items:
    - material: DIAMOND_SWORD
      amount: 1
      name: "<#FF5555>The Dragon Slayer"
      lore:
        - "<#AAAAAA>A sword of legend."
        - "<#AAAAAA>Feared by all who see it."
      enchantments:
        - "SHARPNESS:5"
        - "UNBREAKING:3"
        - "FIRE_ASPECT:2"
      chance: 100
```

**Item reward options:**

| Option | What It Does |
|--------|-------------|
| `material` | The Minecraft item type (required) |
| `amount` | How many of the item to give. Default is 1. |
| `name` | A custom display name. Supports color codes. |
| `lore` | Custom description lines on the item. Supports color codes. |
| `enchantments` | A list of enchantments in the format `ENCHANTMENT_NAME:level`. |
| `chance` | The percentage chance (0 to 100) this item is given. 100 means always. |

You can list multiple items, each with their own chance:

```yaml
reward:
  items:
    - material: DIAMOND
      amount: 3
      chance: 100
    - material: EMERALD
      amount: 5
      chance: 50
    - material: NETHERITE_INGOT
      amount: 1
      chance: 10
```

---

## Commands

Run one or more commands when the player claims their reward. This is very flexible because you can run any console command.

```yaml
reward:
  commands:
    - "give {player} golden_apple 1"
    - "broadcast {player} just completed a quest!"
    - "lp user {player} permission set rank.adventurer true"
```

Use `{player}` anywhere in the command and it will be replaced with the player name.

Commands run from the console (as if an admin typed them), so they can do anything a console command can do, including running other plugin commands.

---

## Quest Reward (Quest Chains)

Give the player a new quest paper as the reward. This is how you connect quests into a series where completing one unlocks the next.

```yaml
reward:
  quest: "chapter_2"
```

When the player claims the reward, they receive the quest paper for `chapter_2`. That quest can then give `chapter_3` as its reward, and so on. This creates a full quest chain.

---

## Combining Rewards

You can use all reward types together. Players receive all of them at once:

```yaml
reward:
  xp: 1000
  money: 500
  items:
    - material: DIAMOND
      amount: 3
      chance: 100
    - material: ELYTRA
      amount: 1
      chance: 5
  commands:
    - "broadcast {player} completed The Dragon's Trial!"
  quest: "endgame_questline"
```

---

## Managing Rewards with Commands

You can add or remove rewards from existing quests using commands, without editing the config file:

| Command | What It Does |
|---------|-------------|
| `/sq reward list <questid>` | Lists all current rewards on a quest |
| `/sq reward add <questid> xp <amount>` | Adds an XP reward |
| `/sq reward add <questid> money <amount>` | Adds a money reward |
| `/sq reward add <questid> command <cmd>` | Adds a command reward |
| `/sq reward remove <questid> <number>` | Removes a reward by its position in the list |

---

## Difficulty Scaling

If your quest has a difficulty set, rewards can scale automatically. The multiplier from the difficulty is applied to XP and money rewards.

For example, with an `easy` difficulty that has a 0.75 multiplier, a quest with `xp: 1000` will give 750 XP. With a `hard` difficulty at 1.5, the same quest gives 1500 XP.

This means you can write one reward amount and let the difficulty system handle the rest automatically.

| Difficulty | Default Multiplier |
|------------|-------------------|
| Easy | 0.75x |
| Normal | 1.0x |
| Hard | 1.5x |
| Expert | 2.0x |
| Nightmare | 2.5x |

See [Tiers and Difficulties](Tiers-and-Difficulties.md) to customize these values.
