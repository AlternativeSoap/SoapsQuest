# Tiers and Difficulties

SoapsQuest uses two systems to categorize quests: **tiers** (rarity) and **difficulties** (scaling). Both are fully customizable.

---

## Tiers

Tiers define how rare a quest is. They affect the display name prefix and how often quests of that tier appear during random generation.

### Default Tiers

| Tier | Display | Prefix | Weight |
|:-----|:--------|:-------|:-------|
| Common | `&fCommon` | `&7[COMMON]` | 40 |
| Uncommon | `&2Uncommon` | `&2[UNCOMMON]` | 32 |
| Rare | `&9Rare` | `&9[RARE]` | 25 |
| Epic | `&5Epic` | `&5[EPIC]` | 18 |
| Legendary | `&6Legendary` | `&6[LEGENDARY]` | 12 |
| Mythic | `&d&lMythic` | `&d&l[MYTHIC]` | 8 |

**Weight** controls how often this tier is picked during random quest generation. Higher weight = more common.

### Customizing Tiers

Edit `tiers.yml`:

```yaml
tiers:
  common:
    display: "&fCommon"
    prefix: "&7[COMMON]"
    color: "&f"
    weight: 40

  uncommon:
    display: "&2Uncommon"
    prefix: "&2[UNCOMMON]"
    color: "&2"
    weight: 32

  rare:
    display: "&9Rare"
    prefix: "&9[RARE]"
    color: "&9"
    weight: 25

  epic:
    display: "&5Epic"
    prefix: "&5[EPIC]"
    color: "&5"
    weight: 18

  legendary:
    display: "&6Legendary"
    prefix: "&6[LEGENDARY]"
    color: "&6"
    weight: 12

  mythic:
    display: "&d&lMythic"
    prefix: "&d&l[MYTHIC]"
    color: "&d"
    weight: 8
```

### Adding Your Own Tier

Just add a new entry:

```yaml
tiers:
  divine:
    display: "&b&lDivine"
    prefix: "&b&l[DIVINE]"
    color: "&b"
    weight: 3
```

Then use it in your quests:

```yaml
my_quest:
  tier: divine
```

---

## Difficulties

Difficulties scale the objective amounts and reward values through multipliers.

### Default Difficulties

| Difficulty | Display | Objective Scale | Reward Scale | Weight |
|:-----------|:--------|:---------------|:-------------|:-------|
| Easy | `&aEasy` | 0.75x | 0.75x | 50 |
| Normal | `&eNormal` | 1.0x | 1.0x | 35 |
| Hard | `&cHard` | 1.5x | 1.5x | 20 |
| Expert | `&6Expert` | 2.0x | 2.0x | 10 |
| Nightmare | `&4&lNightmare` | 2.5x | 2.5x | 5 |

### How Scaling Works

If a quest has:
```yaml
objectives:
  - type: kill
    target: ZOMBIE
    amount: 20
reward:
  xp: 100
```

| Difficulty | Zombies to Kill | XP Reward |
|:-----------|:---------------|:----------|
| Easy | 15 | 75 |
| Normal | 20 | 100 |
| Hard | 30 | 150 |
| Expert | 40 | 200 |
| Nightmare | 50 | 250 |

### Customizing Difficulties

Edit `difficulties.yml`:

```yaml
difficulties:
  easy:
    display: "&aEasy"
    weight: 50
    multiplier:
      objective-amount: 0.75
      reward: 0.75

  normal:
    display: "&eNormal"
    weight: 35
    multiplier:
      objective-amount: 1.0
      reward: 1.0

  hard:
    display: "&cHard"
    weight: 20
    multiplier:
      objective-amount: 1.5
      reward: 1.5

  expert:
    display: "&6Expert"
    weight: 10
    multiplier:
      objective-amount: 2.0
      reward: 2.0

  nightmare:
    display: "&4&lNightmare"
    weight: 5
    multiplier:
      objective-amount: 2.5
      reward: 2.5
```

### Adding a Custom Difficulty

```yaml
difficulties:
  impossible:
    display: "&4&l&oImpossible"
    weight: 1
    multiplier:
      objective-amount: 5.0
      reward: 5.0
```

---

## Using Tiers and Difficulties in Quests

```yaml
my_quest:
  tier: epic
  difficulty: hard
  objectives:
    - type: kill
      target: WITHER_SKELETON
      amount: 20
  reward:
    xp: 500
    money: 300
```

This quest shows the `&5[EPIC]` prefix and scales the 20 kills to 30 (1.5x hard). Rewards scale to 750 XP and 450 money.

---

## Default Tier and Difficulty

In `config.yml`, you can set what new quests default to:

```yaml
default-tier: common
default-difficulty: normal
```

Quests without an explicit tier or difficulty use these values.

---

## Next Steps

- [Random Quest Generator](Random-Quest-Generator.md) - Tiers and difficulties are central to generation
- [Creating Quests](Creating-Quests.md) - Use tiers and difficulties in your quests
