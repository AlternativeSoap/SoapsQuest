# Random Quest Generator

The random quest generator creates unique quests on the fly from configurable templates. Instead of writing hundreds of quests by hand, you set up objective pools, reward ranges, and name templates - and the generator does the rest.

---

## Generating Quests

### Commands

```
/sq generate                    → Generate one random quest (default type)
/sq generate single             → Generate a single-objective quest
/sq generate multi              → Generate a multi-objective quest (2-4 objectives, any order)
/sq generate sequence           → Generate a sequential quest (2-4 objectives, in order)
/sq generate single 10          → Generate 10 single-objective quests at once
```

**Permission:** `soapsquest.generate`

**Batch limit:** Configured in `config.yml` under `max-batch-generate` (default: 25).

**Cooldown:** Configured in `config.yml` under `generate-cooldown` (default: 0 = no cooldown).

---

## How It Works

When you generate a quest, the plugin:

1. **Picks a tier** - weighted random from the tier pool
2. **Picks a difficulty** - weighted random from the difficulty pool
3. **Selects objectives** - weighted random from the objective pool
4. **Picks random targets and amounts** - from configured ranges
5. **Generates a display name** - from name templates
6. **Generates lore** - based on the chosen lore style
7. **Creates rewards** - XP, money, and items based on tier ranges
8. **Applies conditions** - optionally adds level/money/cost requirements
9. **Sets milestones** - based on tier
10. **Saves the quest** - to `generated.yml`

The generated quest immediately appears in `/sq list` and the quest browser.

---

## Configuration Overview

Everything lives in `random-generator.yml`. Here are the key sections:

### Master Settings

```yaml
random-generator:
  enabled: true                          # Master toggle
  save-generated-quests: true            # Save to generated.yml
  allowed-types: [single, multi, sequence]  # Which types can be generated
  save-location: "generated.yml"         # Where generated quests are stored
```

### Objective Weights

Controls how often each objective type is picked:

```yaml
objective-weights:
  kill: 45        # Very common
  break: 40
  collect: 30
  craft: 30
  fish: 18
  enchant: 12
  brew: 12
  move: 20
  death: 3        # Rare
  firework: 5
```

Higher weight = more likely to be selected. Set to `0` to disable.

### Multi/Sequence Settings

```yaml
multi-objective:
  min-objectives: 2
  max-objectives: 4

sequence-objective:
  min-objectives: 2
  max-objectives: 4
```

---

## Objective Pools

Each objective pool defines what targets and amounts are used:

```yaml
objectives:
  kill_zombies:
    objective: kill
    target: [ZOMBIE]
    amount: [15, 40]          # Random between 15 and 40

  break_stone:
    objective: break
    target: [STONE, COBBLESTONE, ANDESITE, DIORITE, GRANITE]
    amount-by-difficulty:     # Different ranges per difficulty
      easy: [30, 80]
      normal: [50, 200]
      hard: [150, 400]
      nightmare: [300, 800]
```

**`amount: [min, max]`** - random number in range.

**`amount-by-difficulty`** - different ranges per difficulty level.

**`target: [LIST]`** - one is randomly chosen.

---

## Display Name Templates

The generator picks a random name from templates per objective type:

```yaml
display:
  name-templates:
    kill:
      - "<gradient:#FF5555:#CC0000><target> Slayer</gradient>"
      - "<#FF4444>Hunt <white><amount></white> <target>s"
    break:
      - "<#AAAAAA>Mine <white><amount></white> <target>"
      - "<gradient:#888888:#FFFFFF><target> Breaker</gradient>"
    craft:
      - "<gradient:#55FFFF:#00AAAA>Crafting Order: <white><target></white></gradient>"
```

**Available placeholders:** `<tier>`, `<tier_prefix>`, `<tier_color>`, `<difficulty>`, `<target>`, `<amount>`, `<type>`

---

## Lore Styles

Three built-in styles:

```yaml
display:
  lore-style: "detailed"     # Options: simple, detailed, fancy
```

- **simple** - minimal info, just the task
- **detailed** - bordered with tier and difficulty info
- **fancy** - different layout per tier (common gets simple, legendary gets decorative)

---

## Reward Pools

### XP and Money Ranges (per tier)

```yaml
reward-pool:
  xp:
    common: [25, 100]
    rare: [100, 250]
    epic: [250, 500]
    legendary: [400, 800]
    mythic: [750, 1500]

  money:
    common: [10, 100]
    rare: [100, 400]
    epic: [400, 1000]
    legendary: [750, 2000]
    mythic: [1500, 4000]
```

### Item Rewards

Weighted item pool with tier and difficulty restrictions:

```yaml
reward-pool:
  items:
    selection-mode: "weighted"
    min-items: 1
    max-items: 3

    pool:
      - material: DIAMOND
        amount: [1, 3]
        tiers: [rare, epic]
        weight: 30

      - material: NETHERITE_SWORD
        amount: 1
        name: "&6&l⚔ Legendary Blade ⚔"
        enchantments:
          - "SHARPNESS:7"
          - "LOOTING:4"
        tiers: [legendary]
        weight: 8
        min-difficulty: hard
```

### Quest Rewards

Generated quests can also reward other quest papers:

```yaml
reward-pool:
  quests:
    enabled: true
    chance: 15
    pool:
      - quest-id: lumberjack
        tiers: [common, uncommon]
        chance: 80
      - quest-id: diamond_rush
        tiers: [epic, legendary]
        chance: 40
        min-difficulty: hard
```

---

## Condition Generation

Conditions can be randomly applied to generated quests:

```yaml
conditions:
  enabled: true

  min-level:
    enabled: true
    chance: 40                # 40% chance to add a level requirement
    by-tier:
      common: 0
      rare: 10
      epic: 25
      legendary: 50

  cost:
    enabled: true
    chance: 20
    by-tier:
      common: 50
      rare: 250
      epic: 1000
      legendary: 5000
```

Each condition type has its own chance and tier-based values.

---

## Milestone Generation

```yaml
milestones:
  enabled: true
  mode: "tier-based"            # Options: default, random, tier-based

  default: [25, 50, 75]

  tier-based:
    common: [50]
    rare: [25, 75]
    epic: [25, 50, 75]
    legendary: [20, 40, 60, 80]
```

---

## Quest Paper Material

Controls what item the generated quest paper looks like:

```yaml
quest-paper-material:
  selection-mode: "random"      # Options: default, random, tier-based
  default-material: "PAPER"

  random-pool:
    - material: PAPER
      weight: 50
    - material: MAP
      weight: 20
    - material: ENCHANTED_BOOK
      weight: 10

  tier-based:
    common: [PAPER, MAP]
    legendary: [ENCHANTED_BOOK, KNOWLEDGE_BOOK, NETHER_STAR]
```

---

## MythicMobs Support

If you have MythicMobs installed, you can add custom mobs to the kill objective pool:

```yaml
mythicmobs:
  enabled: false
  pool:
    - SkeletalKnight
    - SkeletonKing
    - FireDemon
```

---

## Next Steps

- [Daily & Weekly Quests](Daily-and-Weekly-Quests.md) - Automatic quest rotation
- [Quest Loot System](Quest-Loot-System.md) - Quest papers from mobs and chests
