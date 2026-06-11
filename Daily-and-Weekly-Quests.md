# Daily and Weekly Quests (Premium)

Recurring quests automatically give players quest papers on a schedule. Configure `plugins/SoapsQuest/daily.yml`. This file is **Premium only** and is not included in the Free JAR.

---

## Overview

- **Daily** quests reset every day at `reset-time` (server timezone).
- **Weekly** quests reset on `reset-day` at `reset-time`.
- Online players are notified on reset. Players who join mid-cycle receive any quests they have not yet gotten this cycle.
- Quest IDs must exist under `quests:` in `quests.yml`.
- If a quest has `conditions.permission`, only players with that permission receive it.

---

## Enable daily quests

```yaml
daily:
  enabled: true
  quests:
    - zombie_slayer
    - lumberjack
    - gone_fishing
  reset-time: "06:00"
  selection:
    random: true
    count: 1
  completion-bonus:
    enabled: false
    reward:
      xp: 150
      sigils: 25
      money: 100
```

| Field | Description |
|-------|-------------|
| `enabled` | Master toggle for daily cycle |
| `quests` | Pool of quest IDs to assign |
| `reset-time` | `HH:mm` 24-hour format |
| `selection.random` | `true` = pick random subset from pool |
| `selection.count` | How many quests to give per cycle |
| `completion-bonus` | Optional extra reward when all daily quests are finished |

---

## Enable weekly quests

```yaml
weekly:
  enabled: true
  quests:
    - iron_miner
    - mob_hunter
  reset-day: "MONDAY"
  reset-time: "06:00"
  selection:
    random: true
    count: 1
  completion-bonus:
    enabled: false
    reward:
      xp: 600
      sigils: 100
      money: 400
```

Valid `reset-day` values: `MONDAY` through `SUNDAY`.

---

## Reset notifications

```yaml
notifications:
  mode: "actionbar"   # actionbar | chat | title | bossbar | none
  sound:
    enabled: true
    type: "ENTITY_PLAYER_LEVELUP"
    volume: 1.0
    pitch: 1.0
```

Message text is in `messages.yml` under `recurring-quest-reset-*` keys.

---

## How papers behave

Recurring quest papers use the same Active/Queued rules as `/sq give` papers. Progress is tracked in `playerdata.yml`. When a cycle resets, assignment records clear and eligible players receive fresh papers.

---

## Setup checklist

1. Confirm Premium JAR is installed.
2. Add quest IDs to `daily.quests` or `weekly.quests`.
3. Set `enabled: true` on the cycle you want.
4. `/sq reload`
5. Test with a short quest pool and one player online across a reset time.

---

*Version 1.0.3 - Premium*
