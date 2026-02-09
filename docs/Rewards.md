# Rewards

Rewards are given to players when they right-click a completed quest paper. There are five reward types, and you can mix and match as many as you want.

---

## Reward Types

### XP

Gives experience points.

```yaml
reward:
  xp: 100
```

With a chance (70% chance to receive):

```yaml
reward:
  xp: 100
  xp-chance: 70
```

---

### Money

Gives currency through Vault. **Requires Vault + an economy plugin** (like EssentialsX, CMI, etc.).

```yaml
reward:
  money: 500
```

With a chance:

```yaml
reward:
  money: 500
  money-chance: 80
```

---

### Items

Gives items to the player's inventory.

```yaml
reward:
  items:
    - material: DIAMOND
      amount: 5
```

Full item options:

```yaml
reward:
  items:
    - material: DIAMOND_SWORD
      amount: 1
      name: "&6Hero's Blade"
      lore:
        - "&7A powerful weapon"
        - "&7earned through questing"
      enchantments:
        - "SHARPNESS:5"
        - "UNBREAKING:3"
        - "FIRE_ASPECT:2"
      chance: 100
```

| Property | Required | Description |
|:---------|:---------|:------------|
| `material` | Yes | Minecraft item type (e.g., `DIAMOND_SWORD`) |
| `amount` | No | Number of items (default: 1) |
| `name` | No | Custom display name with color codes |
| `lore` | No | Custom lore lines |
| `enchantments` | No | List of `"ENCHANTMENT:level"` |
| `chance` | No | Drop chance 1-100% (default: 100) |

#### Multiple Items

```yaml
reward:
  items:
    - material: DIAMOND
      amount: 3
      chance: 100
    - material: EMERALD
      amount: 5
      chance: 100
    - material: NETHERITE_INGOT
      amount: 1
      chance: 25
```

The player always gets diamonds and emeralds, but only has a 25% chance at the netherite ingot.

---

### Commands

Runs server commands when the quest is completed. Use `{player}` as a placeholder for the player's name.

```yaml
reward:
  commands:
    - "give {player} experience_bottle 16"
    - "broadcast &6{player} &7completed a quest!"
    - "title {player} title {\"text\":\"Quest Complete!\",\"color\":\"gold\"}"
```

With a chance:

```yaml
reward:
  command-chance: 50
  commands:
    - "give {player} diamond 10"
```

Commands run from console with full permissions — so anything goes.

---

### Quest Reward

Awards another quest paper on completion. This lets you create **quest chains** where finishing one quest leads to the next.

Simple format:

```yaml
reward:
  quest: "next_quest_id"
```

With chance:

```yaml
reward:
  quest:
    quest-id: "next_quest_id"
    chance: 50
```

The player gets a 50% chance to receive the next quest paper on completion.

---

## Combining Rewards

You can use all reward types together:

```yaml
reward:
  xp: 250
  money: 100
  items:
    - material: DIAMOND
      amount: 3
      chance: 100
    - material: GOLDEN_APPLE
      amount: 1
      chance: 50
  commands:
    - "broadcast &e{player} &7finished the epic quest!"
  quest:
    quest-id: "sequel_quest"
    chance: 75
```

---

## Managing Rewards In-Game

You can add and remove rewards without editing config files:

**Add a held item as a reward:**
```
/sq addreward <quest> item
```
Then type `HAND` in chat to add whatever you're holding.

**Add XP:**
```
/sq addreward <quest> xp 500
```

**Add money:**
```
/sq addreward <quest> money 250
```

**Add a command:**
```
/sq addreward <quest> command give {player} diamond 10
```

**List rewards:**
```
/sq listreward <quest>
```

**Remove a reward by index:**
```
/sq removereward <quest> 1
```

---

## Difficulty Scaling

Rewards are scaled by the quest's difficulty multiplier:

| Difficulty | Reward Multiplier |
|:-----------|:-----------------|
| Easy | 0.75x |
| Normal | 1.0x |
| Hard | 1.5x |
| Expert | 2.0x |
| Nightmare | 2.5x |

A quest with `xp: 100` on **Hard** difficulty gives `150 XP`.

---

## Next Steps

- [Conditions](Conditions) — Quest requirements and unlock costs
- [Creating Quests](Creating-Quests) — Full quest creation guide
