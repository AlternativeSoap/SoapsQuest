# Random Quest Generator

> **[PREMIUM]** The Random Quest Generator requires the SoapsQuest Premium version. Get it at [SoapsUniverse.com](https://SoapsUniverse.com)

The Random Quest Generator creates quests automatically using pools of objectives, names, and rewards that you configure. Admins can generate quests on demand. The generated quests are saved just like any hand-made quest and can be given to players normally.

---

## How to Use It

Generate a single random quest:
```
/sq generate
```

Generate a quest of a specific type (like kill, mine, fish):
```
/sq generate kill
```

Generate multiple quests at once:
```
/sq generate kill 5
```

Permission required: `soapsquest.generate`

The maximum number of quests you can generate at once is set in `config.yml` under `max-batch-generate` (default: 25).

---

## Configuration Overview

All generator settings live in `plugins/SoapsQuest/random-generator.yml`. The file has several sections:

- **Objective weights** - which objective types are included and how likely each is
- **Objective pools** - specific targets and amounts for each objective type
- **Name templates** - how random quest names are built
- **Lore styles** - how the item tooltip is formatted
- **Reward pools** - XP, money, and items for generated quests
- **Condition settings** - whether generated quests include conditions and how likely each type is

---

## Objective Weights

This section controls which objective types can appear in generated quests and how likely each one is to be chosen.

```yaml
objectives:
  weights:
    kill: 30
    mine: 25
    fish: 15
    craft: 20
    collect: 20
    travel: 10
    brew: 10
    enchant: 10
    smelt: 15
    break: 20
    harvest: 15
    breed: 10
    tame: 5
```

Higher weight means that objective type will show up more often. Setting a type to 0 removes it from the pool.

### Multi-Objective Settings

You can control how many objectives a generated quest gets:

```yaml
objectives:
  multi:
    enabled: true
    min-objectives: 1
    max-objectives: 3
    sequential-chance: 20
```

| Option | What It Does |
|--------|-------------|
| `enabled` | Allow generated quests to have more than one objective. |
| `min-objectives` | The fewest objectives a generated quest can have. |
| `max-objectives` | The most objectives a generated quest can have. |
| `sequential-chance` | The percentage chance (0 to 100) that a multi-objective quest will be sequential (objectives must be done in order). |

---

## Objective Pools

For each objective type, you can define what targets are available and how many the player has to do.

```yaml
objectives:
  pools:
    kill:
      targets: ["ZOMBIE", "SKELETON", "SPIDER", "CREEPER", "BLAZE", "ENDERMAN"]
      amount:
        min: 10
        max: 100
      amount-by-difficulty:
        easy:
          min: 5
          max: 20
        normal:
          min: 15
          max: 50
        hard:
          min: 30
          max: 100
```

| Option | What It Does |
|--------|-------------|
| `targets` | The list of valid targets for this objective type. One is chosen at random. |
| `amount.min` and `amount.max` | The base range for how many the player needs to do. |
| `amount-by-difficulty` | Override the amount range depending on the quest difficulty. This is optional. |

---

## Name Templates

Quest names are built from templates with placeholders.

```yaml
names:
  templates:
    kill:
      - "Slay the {target}"
      - "Hunt the {target}"
      - "{target} Slayer"
    mine:
      - "Mining for {target}"
      - "The {target} Miner"
    fish:
      - "Gone Fishing"
      - "Fisherman's Quest"
```

Placeholders available in name templates:

| Placeholder | What It Replaces With |
|------------|----------------------|
| `{target}` | The objective target (formatted nicely, like "Zombie" instead of "ZOMBIE") |
| `{amount}` | The objective amount |
| `{tier}` | The quest tier name |
| `{difficulty}` | The quest difficulty name |

---

## Lore Styles

The `lore-style` setting controls how the item tooltip is formatted for generated quests.

```yaml
names:
  lore-style: "detailed"
```

| Style | What It Looks Like |
|-------|-------------------|
| `simple` | Just the objective listed plainly |
| `detailed` | More information including rewards and difficulty |
| `fancy` | Decorative formatting with colors and borders |

---

## Reward Pools

Generated quests are given random rewards from these pools.

```yaml
rewards:
  xp:
    common:
      min: 50
      max: 150
    rare:
      min: 200
      max: 400
    legendary:
      min: 800
      max: 1500

  money:
    common:
      min: 25
      max: 75
    rare:
      min: 100
      max: 300

  items:
    - material: DIAMOND
      weight: 10
      tiers: ["rare", "epic", "legendary", "mythic"]
      min-difficulty: "normal"
      amount-min: 1
      amount-max: 3
    - material: EMERALD
      weight: 25
      tiers: ["uncommon", "rare", "epic"]
      amount-min: 1
      amount-max: 5
    - material: GOLD_INGOT
      weight: 40
      tiers: ["common", "uncommon"]
      amount-min: 2
      amount-max: 8
```

**XP and money pools:** Set a min and max per tier. Generated quests of that tier will give a random amount in that range.

**Item pools:** Each item entry has:

| Option | What It Does |
|--------|-------------|
| `material` | The Minecraft item type. |
| `weight` | How likely this item is to be chosen (higher = more likely). |
| `tiers` | Which quest tiers this item can appear in. |
| `min-difficulty` | The minimum difficulty required for this item to appear. |
| `amount-min` / `amount-max` | How many of the item can be given. |

---

## Condition Generation

You can have the generator automatically apply conditions to some generated quests.

```yaml
conditions:
  chance-per-tier:
    common: 0
    uncommon: 10
    rare: 25
    epic: 40
    legendary: 60
    mythic: 80
  types:
    min-level:
      enabled: true
      chance: 30
      values:
        easy: [5, 10]
        normal: [10, 20]
        hard: [20, 35]
    cost:
      enabled: true
      chance: 20
```

`chance-per-tier` sets the overall chance that any condition is added to a generated quest of that tier. Higher tiers get more conditions, which makes them feel more exclusive.

`types` lets you fine-tune which condition types can appear and how likely they are. Setting `enabled: false` for a type removes it from generated quests entirely.

---

## Milestone Generation

Generated quests can automatically include milestones:

```yaml
milestones:
  enabled: true
  chance: 50
  points: [25, 50, 75]
```

When `enabled` is true, there is a `chance` percentage that a generated quest will include milestone notifications at the listed progress percentages.

---

## MythicMobs Support

If MythicMobs is installed, you can add mythic mob targets to the kill pool:

```yaml
objectives:
  pools:
    kill_mythicmob:
      targets: ["SkeletonKing", "FireDragon", "GiantSpider"]
      amount:
        min: 1
        max: 3
```

The generator will then occasionally create kill quests targeting your custom MythicMobs bosses.

---

## Tips

- Keep the target lists long enough that generated quests feel varied. If you only have 2 mob types in the kill pool, all kill quests will feel repetitive.
- Use `amount-by-difficulty` to give easier difficulties shorter tasks and hard difficulties longer ones.
- The `sequential-chance` setting adds a lot of variety. A 20% chance means about 1 in 5 multi-objective quests will be sequential.
- Generated quests are saved like any other quest and can be viewed with `/sq list`.
