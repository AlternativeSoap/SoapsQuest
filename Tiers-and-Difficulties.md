# Tiers and Difficulties

Tiers label **rarity**. Difficulties label **challenge level**. Both appear in quest lore, GUIs, placeholders, and statistics.

Configure them in `tiers.yml` and `difficulties.yml`. Reference them on each quest:

```yaml
my_quest:
  display: "<#55FF55>Example"
  tier: rare
  difficulty: hard
  objectives:
    - type: kill
      target: ZOMBIE
      amount: 20
  reward:
    xp: 200
```

Defaults for new editor quests come from `config.yml`:

```yaml
default-tier: common
default-difficulty: normal
```

---

## Tiers (`tiers.yml`)

Default tiers: `common`, `uncommon`, `rare`, `epic`, `legendary`, `mythic`.

| Field | Description |
|-------|-------------|
| `display` | Player-facing name (`&` or MiniMessage) |
| `prefix` | Short tag, e.g. `&9[RARE]` |
| `color` | Base color code for generic tier text |
| `weight` | Relative spawn weight for random generation (higher = more common) |
| `description` | Tooltip text in GUIs |

Example:

```yaml
tiers:
  rare:
    display: "&9Rare"
    prefix: "&9[RARE]"
    color: "&9"
    weight: 25
    description: "&9Quests worth seeking out for better loot."
```

You may add, remove, or rename tiers. Order in the file controls GUI sort order.

### Placeholder mapping

Completion counts per tier: `%soapsquest_tier_<tierid>%` (also accepts `player_` prefix). See [Placeholders](Placeholders.md).

---

## Difficulties (`difficulties.yml`)

Default difficulties: `easy`, `normal`, `hard`, `expert`, `nightmare`.

| Field | Description |
|-------|-------------|
| `display` | Player-facing name |
| `color` | Base color code |
| `weight` | Relative spawn weight for random generation |
| `description` | Tooltip text |
| `multiplier.objective-amount` | Scales required objective amounts |
| `multiplier.reward` | Scales reward values |

Example:

```yaml
difficulties:
  hard:
    display: "&cHard"
    color: "&c"
    weight: 20
    description: "&cMore demanding objectives with better rewards."
    multiplier:
      objective-amount: 1.5
      reward: 1.5
```

`easy` at `0.75` means 25% fewer targets and 25% less loot. `nightmare` at `2.5` doubles-and-a-half both.

### Placeholder mapping

Completion counts per difficulty: `%soapsquest_difficulty_<difficultyid>%`.

---

## Where tiers and difficulties appear

- Quest paper lore (manual and generated)
- Quest browser and editor GUIs
- `/sq statistic` breakdown
- Random quest generator pools (`tier-pool`, `difficulty-pool` in `random-generator.yml`)
- PlaceholderAPI expansion

---

## Tips for server owners

- Keep tier names aligned with your economy (common dailies vs mythic raid quests).
- Use difficulty multipliers instead of hand-tuning every amount in large quest sets.
- After editing `tiers.yml` or `difficulties.yml`, run `/sq reload`.

---

*Version 1.0.3*
