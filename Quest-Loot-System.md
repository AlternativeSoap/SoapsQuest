# Quest Loot System

Quest papers can drop naturally from mobs and appear in chests throughout the world. This turns quests into discoverable loot - players find them while playing, not just from commands.

---

## Configuration

Edit `quest-loot.yml`:

```yaml
quest-loot:
  enabled: true
  max-per-event: 4
  obey-plugin-restrictions: true

  chest:
    enabled: true
    chance: 10
    amount-min: 1
    amount-max: 2
    worlds: ["world", "world_nether"]
    source-mode: "manual"
    quests:
      - "lumberjack"
      - "zombie_slayer"
      - "mob_hunter"

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
      CREEPER:
        chance: 10
      WITHER:
        chance: 100
        amount-min: 5
        amount-max: 8
      ENDER_DRAGON:
        chance: 100
        amount-min: 3
        amount-max: 6
```

---

## Chest Loot

Quest papers can appear when players open naturally generated chests (dungeon chests, mineshaft chests, etc.).

| Option | Description |
|:-------|:-----------|
| `enabled` | Toggle chest loot on/off |
| `chance` | Percentage chance a quest appears per chest (0-100) |
| `amount-min` | Minimum quest papers per chest |
| `amount-max` | Maximum quest papers per chest |
| `worlds` | Which worlds this applies to (empty = all worlds) |
| `source-mode` | Where quests come from (see below) |
| `quests` | List of quest IDs when using `manual` mode |

### Source Modes

| Mode | Description |
|:-----|:-----------|
| `manual` | Picks from the `quests` list-specific quest IDs you've defined |
| `random` | Generates new quests using the random quest generator |
| `mixed` | 50/50 between manual and random |

---

## Mob Drops

Quest papers drop when players kill mobs.

| Option | Description |
|:-------|:-----------|
| `enabled` | Toggle mob drops on/off |
| `default-chance` | Default drop % for mobs not individually listed |
| `worlds` | Which worlds this applies to (empty = all worlds) |

### Per-Mob Configuration

Under `types`, list each mob type with custom settings:

```yaml
types:
  ZOMBIE:
    chance: 12
    amount-min: 1
    amount-max: 2

  WITHER_SKELETON:
    chance: 25
    amount-min: 1
    amount-max: 3

  WITHER:
    chance: 100
    amount-min: 5
    amount-max: 8
```

Mobs not listed use the `default-chance` value.

### Suggested Setup

| Mob Category | Suggested Chance |
|:-------------|:----------------|
| Common mobs (Zombie, Skeleton, Spider) | 5-12% |
| Uncommon mobs (Enderman, Blaze) | 15-25% |
| Boss mobs (Wither, Ender Dragon) | 100% |

---

## Unbound Quest Papers

Quest papers from the loot system drop as **unbound** - they don't belong to any player yet. When a player picks one up:

1. The paper automatically binds to that player
2. The quest activates (or queues behind an existing quest)
3. The player gets a pickup notification

If the quest has `lock-to-player: true`, it permanently binds to whoever picks it up first.

---

## Global Settings

```yaml
quest-loot:
  max-per-event: 4
  obey-plugin-restrictions: true
```

| Option | Description |
|:-------|:-----------|
| `max-per-event` | Maximum quest papers that can drop from a single event (prevents loot spam) |
| `obey-plugin-restrictions` | When true, respects quest permissions and conditions |

---

## Next Steps

- [GUI System](GUI-System.md) - Quest browser and editor
- [Random Quest Generator](Random-Quest-Generator.md) - Used for `random` source mode
