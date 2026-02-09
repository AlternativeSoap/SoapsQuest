# Daily and Weekly Quests

SoapsQuest can automatically distribute quests on a schedule — daily, weekly, or both. Set up a pool of quests, pick a reset time, and the plugin handles everything.

---

## Configuration

Edit `daily.yml`:

```yaml
daily:
  enabled: false
  quests: []
  reset-time: "00:00"
  randomize: false
  count: 1

weekly:
  enabled: false
  quests: []
  reset-day: "MONDAY"
  reset-time: "00:00"
  randomize: false
  count: 1

notifications:
  mode: "actionbar"
  sound:
    enabled: true
    type: "ENTITY_PLAYER_LEVELUP"
```

---

## Setting Up Daily Quests

1. Create some quests in `quests.yml` (or use existing ones)
2. Add their IDs to the daily quest pool
3. Enable it

```yaml
daily:
  enabled: true
  quests:
    - lumberjack
    - zombie_slayer
    - gone_fishing
    - baker
    - shepherd
  reset-time: "06:00"
  randomize: true
  count: 3
```

This gives 3 random quests from the pool every day at 6:00 AM server time.

### Settings

| Option | Description |
|:-------|:-----------|
| `enabled` | Turn daily quests on/off |
| `quests` | List of quest IDs from `quests.yml` |
| `reset-time` | Time of day to reset (24-hour format, server timezone) |
| `randomize` | `true` = random picks from pool, `false` = rotate in order |
| `count` | How many quests to distribute per reset |

---

## Setting Up Weekly Quests

Same concept, but resets once per week:

```yaml
weekly:
  enabled: true
  quests:
    - iron_miner
    - mob_hunter
    - diamond_rush
    - nether_explorer
  reset-day: "MONDAY"
  reset-time: "00:00"
  randomize: true
  count: 1
```

### Additional Options

| Option | Description |
|:-------|:-----------|
| `reset-day` | Day of the week: `MONDAY`, `TUESDAY`, `WEDNESDAY`, `THURSDAY`, `FRIDAY`, `SATURDAY`, `SUNDAY` |

---

## Reset Notifications

When quests reset, online players are notified.

```yaml
notifications:
  mode: "actionbar"
  sound:
    enabled: true
    type: "ENTITY_PLAYER_LEVELUP"
```

| Mode | Description |
|:-----|:-----------|
| `actionbar` | Shows a message above the hotbar |
| `chat` | Sends a chat message |
| `bossbar` | Shows a boss bar notification |
| `none` | No notification |

Notification messages are customizable in `messages.yml`:

```
recurring-quest-reset-daily
recurring-quest-reset-weekly
recurring-quest-received-daily
recurring-quest-received-weekly
```

---

## How It Works

1. At the configured reset time, the plugin fires the reset
2. All online players receive their new quest papers automatically
3. Players who were offline get their quests on next login
4. The plugin tracks which recurring quests each player has already received to avoid duplicates
5. Previous quest progress from the same recurring slot is not affected

---

## Next Steps

- [Quest Loot System](Quest-Loot-System) — Quest papers from mob kills and chests
- [Random Quest Generator](Random-Quest-Generator) — Generate quests for the rotation pool
