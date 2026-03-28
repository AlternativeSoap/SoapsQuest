# Quest Loot System

> **[PREMIUM]** The Quest Loot System requires the SoapsQuest Premium version. Get it at [SoapsUniverse.com](https://SoapsUniverse.com)

The Quest Loot System lets quest papers appear naturally in the world. Players can find quest papers by killing mobs or opening chests. This makes quests feel like discoveries rather than things players have to seek out through menus.

---

## How It Works

- **Mob drops:** When a player kills a mob, there is a configurable chance that a quest paper drops alongside the normal loot.
- **Chest loot:** When a player opens a chest, there is a configurable chance that a quest paper is added to the chest contents.

Both systems work independently. You can enable one, the other, or both.

The configuration lives in `plugins/SoapsQuest/quest-loot.yml`.

---

## Global Settings

At the top of `quest-loot.yml`:

```yaml
quest-loot:
  enabled: true
  max-per-event: 4
  obey-plugin-restrictions: true
```

| Option | What It Does |
|--------|-------------|
| `enabled` | Master switch. Set to `false` to disable the entire loot system. |
| `max-per-event` | The maximum number of quest papers that can drop or appear per single event (one mob kill or one chest open). |
| `obey-plugin-restrictions` | If `true`, the system respects any restrictions set by other plugins (like land protection plugins). |

---

## Chest Loot

This section controls whether quest papers can appear inside chests when players open them.

```yaml
chest:
  enabled: true
  chance: 10
  amount-min: 1
  amount-max: 2
  worlds: ["world", "world_nether"]
  source-mode: "manual"
  quests:
    - "welcome_quest"
    - "fishing_challenge"
    - "mob_hunter"
```

| Option | What It Does |
|--------|-------------|
| `enabled` | Turn chest loot on or off. |
| `chance` | The percentage chance (0 to 100) that a quest paper appears when a player opens any chest. |
| `amount-min` | The minimum number of papers that can appear at once. |
| `amount-max` | The maximum number of papers that can appear at once. |
| `worlds` | Which worlds chest loot is active in. |
| `source-mode` | Controls which quests can appear as chest loot. See below. |
| `quests` | The list of quest IDs used when source-mode is `manual`. |

### Source Modes

The source mode controls where the plugin picks which quest to put in the chest:

| Mode | What It Does |
|------|-------------|
| `manual` | Only quests listed under `quests` can appear. You fully control what shows up. |
| `random` | Any quest in the system can appear, chosen randomly. |
| `mixed` | Quests from the `quests` list appear more often, but random quests can also show up. |

Use `manual` if you want specific quests to be "discoverable" through chests and not everything.

---

## Mob Drops

This section controls whether quest papers can drop from mobs when players kill them.

```yaml
mobs:
  enabled: true
  default-chance: 5
  worlds: ["world"]
  types:
    ZOMBIE:
      chance: 12
      amount-min: 1
      amount-max: 2
    SKELETON:
      chance: 8
      amount-min: 1
      amount-max: 1
    WITHER:
      chance: 100
      amount-min: 5
      amount-max: 8
```

| Option | What It Does |
|--------|-------------|
| `enabled` | Turn mob drops on or off. |
| `default-chance` | The default chance (percentage) for any mob not listed under `types`. |
| `worlds` | Which worlds mob drops are active in. |
| `types` | Per-mob overrides. Let you set a specific chance and amount range for individual mob types. |

### Per-Mob Settings

You can give any mob type its own drop chance and amount:

```yaml
types:
  ZOMBIE:
    chance: 12
    amount-min: 1
    amount-max: 2
```

If a mob type is not listed here, the `default-chance` is used instead, with `amount-min: 1` and `amount-max: 1`.

### Suggested Mob Chances

Here is a starting point for balancing mob drop rates:

| Mob | Suggested Chance | Notes |
|-----|-----------------|-------|
| Zombie | 10-15% | Common mob, keep chance low |
| Skeleton | 8-12% | Common, slightly lower than zombie |
| Creeper | 10-15% | Common, average rate |
| Spider | 7-10% | Common, lower rate |
| Wither Skeleton | 20-30% | Rare nether mob, higher rate makes sense |
| Blaze | 15-20% | Nether mob, moderate rate |
| Enderman | 20-25% | Somewhat rare, higher rate |
| Wither | 100% | Boss mob, always drop |
| Ender Dragon | 100% | Boss mob, always drop |

---

## Example: Full Quest Loot Config

```yaml
quest-loot:
  enabled: true
  max-per-event: 3
  obey-plugin-restrictions: true

  chest:
    enabled: true
    chance: 8
    amount-min: 1
    amount-max: 1
    worlds: ["world"]
    source-mode: "manual"
    quests:
      - "starter_quest"
      - "exploration_quest"

  mobs:
    enabled: true
    default-chance: 5
    worlds: ["world"]
    types:
      ZOMBIE:
        chance: 10
      SKELETON:
        chance: 8
      ENDER_DRAGON:
        chance: 100
        amount-min: 2
        amount-max: 3
```

---

## Tips

- Start with low chances (5-10%) and increase them if quests are not being discovered often enough.
- Use `source-mode: manual` with a curated list for chest loot to avoid overwhelming players with too many quest types.
- Boss mobs like the Wither and Ender Dragon at 100% chance make for memorable moments where players always get a quest paper after a big fight.
- The `max-per-event` setting prevents players from getting flooded with papers from a single event.
