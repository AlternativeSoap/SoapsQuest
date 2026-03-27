# Tiers and Difficulties

SoapsQuest has two separate rating systems for quests: tiers and difficulties. They serve different purposes, though both can affect rewards and how quests feel to players.

---

## Tiers

A tier represents the rarity or prestige of a quest, similar to item rarity in RPG games. Think of it like Common versus Legendary.

Tiers control:
- The rarity label shown on the quest paper
- The color or style of the label
- How often that tier shows up when using the random generator (higher weight = appears more often)

**Default tiers:**

| Tier | Color | Weight (Frequency) |
|------|-------|-------------------|
| Common | White | 40 |
| Uncommon | Dark Green | 32 |
| Rare | Blue | 25 |
| Epic | Purple | 18 |
| Legendary | Gold | 12 |
| Mythic | Light Purple (Bold) | 8 |

The weight number controls how frequently this tier is selected during random generation. A weight of 40 means Common is picked roughly 5 times more often than Mythic (weight 8).

### Setting a Quest Tier

```yaml
my_quest:
  display: "Rare Quest"
  tier: rare
  objectives:
    - type: kill
      target: ZOMBIE
      amount: 50
  reward:
    xp: 500
```

### Customizing Tiers (tiers.yml)

Open `plugins/SoapsQuest/tiers.yml` to change how tiers work:

```yaml
tiers:
  common:
    display: "&fCommon"
    prefix: "&7[COMMON]"
    color: "&f"
    weight: 40

  rare:
    display: "&9Rare"
    prefix: "&9[RARE]"
    color: "&9"
    weight: 25
```

**Options:**

| Option | What It Does |
|--------|-------------|
| `display` | The tier name shown to players |
| `prefix` | A short label shown before the quest name |
| `color` | The color code used for this tier |
| `weight` | How likely this tier is to be selected during random generation |

### Adding a Custom Tier

You can add as many tiers as you want. Just add a new entry:

```yaml
tiers:
  divine:
    display: "&e&lDivine"
    prefix: "&e[DIVINE]"
    color: "&e"
    weight: 4
```

Then use it on any quest with `tier: divine`.

---

## Difficulties

A difficulty represents how challenging a quest is and directly affects numbers. It uses multipliers to scale both the amount players need to do and how much they get as a reward.

**Default difficulties:**

| Difficulty | Objective Multiplier | Reward Multiplier | Weight |
|-----------|---------------------|-------------------|--------|
| Easy | 0.75x | 0.75x | 50 |
| Normal | 1.0x | 1.0x | 35 |
| Hard | 1.5x | 1.5x | 20 |
| Expert | 2.0x | 2.0x | 10 |
| Nightmare | 2.5x | 2.5x | 5 |

### How Scaling Works

The objective and reward multipliers are applied to the base values you write in the quest config.

**Example:** A quest with `amount: 20` for a kill objective and `xp: 500`:

| Difficulty | Kills Required | XP Earned |
|-----------|---------------|-----------|
| Easy | 15 (20 x 0.75) | 375 |
| Normal | 20 (20 x 1.0) | 500 |
| Hard | 30 (20 x 1.5) | 750 |
| Expert | 40 (20 x 2.0) | 1000 |
| Nightmare | 50 (20 x 2.5) | 1250 |

This means you only need to write your quest once with base values, and the difficulty handles the rest automatically.

### Setting a Quest Difficulty

```yaml
my_quest:
  display: "Hard Quest"
  difficulty: hard
  objectives:
    - type: kill
      target: ZOMBIE
      amount: 20
  reward:
    xp: 500
```

### Customizing Difficulties (difficulties.yml)

Open `plugins/SoapsQuest/difficulties.yml` to change difficulty settings:

```yaml
difficulties:
  hard:
    display: "&cHard"
    weight: 20
    multiplier:
      objective-amount: 1.5
      reward: 1.5
```

**Options:**

| Option | What It Does |
|--------|-------------|
| `display` | The difficulty label shown to players |
| `weight` | How often this difficulty is chosen during random generation |
| `multiplier.objective-amount` | How much to multiply the required amounts by |
| `multiplier.reward` | How much to multiply XP and money rewards by |

### Adding a Custom Difficulty

```yaml
difficulties:
  insane:
    display: "&4&lInsane"
    weight: 2
    multiplier:
      objective-amount: 4.0
      reward: 4.0
```

Then use `difficulty: insane` on any quest.

---

## Default Settings

You can set a default tier and difficulty that applies to new quests created through the in-game editor. Set these in `config.yml`:

```yaml
default-tier: common
default-difficulty: normal
```

Quests made by editing `quests.yml` directly will use whatever tier and difficulty you write in, or no tier/difficulty at all if you leave those lines out.
