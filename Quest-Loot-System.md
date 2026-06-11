# Quest Loot System (Premium)

Quest loot adds **unbound** quest papers to natural chest loot and mob drops. When a player picks up the paper for the first time, it binds to them and enters their quest queue like a normal paper.

Configure `plugins/SoapsQuest/quest-loot.yml`. **Premium only.**

---

## Master toggle

```yaml
quest-loot:
  enabled: true
  max-per-event: 4
```

`max-per-event` limits papers added per single loot roll to prevent spam.

---

## Chest loot

```yaml
chest:
  enabled: true
  chance: 10
  amount-min: 1
  amount-max: 2
  worlds:
    - "world"
    - "world_nether"
  allowed-loot-tables:
    - "chests/simple_dungeon"
    - "chests/abandoned_mineshaft"
    - "chests/buried_treasure"
    - "archaeology"
    - "fishing"
  source-mode: "manual"
  quests:
    - lumberjack
    - zombie_slayer
```

| Field | Description |
|-------|-------------|
| `chance` | Percent chance per chest open |
| `worlds` | Empty list = all worlds |
| `allowed-loot-tables` | Substring match on loot table keys (natural sources only) |
| `source-mode` | `manual`, `random`, or `mixed` |
| `quests` | Quest ID pool for manual/mixed modes |

### Source modes

| Mode | Behavior |
|------|----------|
| `manual` | Random quest from `quests` list |
| `random` | New quest from random generator |
| `mixed` | 50% manual, 50% random per roll |

---

## Mob drops

```yaml
mobs:
  enabled: true
  default-chance: 5
  source-mode: "manual"
  worlds:
    - "world"
  quests: []    # Falls back to chest quest pool when empty
  types:
    ZOMBIE:
      chance: 12
      amount-min: 1
      amount-max: 2
    ENDER_DRAGON:
      chance: 100
      amount-min: 3
      amount-max: 6
```

Entity names must match Bukkit `EntityType` values in `UPPER_CASE`. Per-type `chance` overrides `default-chance`.

---

## Restrictions

```yaml
obey-plugin-restrictions: true
```

When `true`, the loot system respects quest conditions, permissions, and active limits before dropping a paper.

---

## Setup checklist

1. Premium JAR installed.
2. Set `quest-loot.enabled: true`.
3. Fill `chest.quests` with valid quest IDs from `quests.yml`.
4. Tune `chance` and mob `types` for your economy.
5. `/sq reload`
6. Test by opening dungeon chests or killing configured mobs in allowed worlds.

For dynamically generated loot papers, set `source-mode: random` and ensure `random-generator.yml` is enabled.

---

*Version 1.0.3 - Premium*
