# Daily and Weekly Quests

> **[PREMIUM]** Daily and weekly quests require the SoapsQuest Premium version. Get it at [SoapsUniverse.com](https://SoapsUniverse.com)

Daily and weekly quests are quests that the plugin gives out automatically on a schedule. Daily quests reset every day. Weekly quests reset once a week. Players receive their quest papers automatically without needing to browse or ask an admin.

---

## How It Works

1. You enable daily or weekly quests in `daily.yml` and list which quests to give.
2. At the reset time you configure, the plugin distributes new quest papers to all online players.
3. Players who are offline when the reset happens will receive their papers the next time they log in.
4. The same player will not receive the same quest twice in a row until the full quest pool has cycled through.

---

## Setting Up Daily Quests

Open `plugins/SoapsQuest/daily.yml` and edit the `daily` section:

```yaml
daily:
  enabled: true
  quests:
    - "zombie_slayer"
    - "gone_fishing"
    - "lumberjack"
  reset-time: "06:00"
  randomize: false
  count: 1
```

**Options:**

| Option | What It Does |
|--------|-------------|
| `enabled` | Set to `true` to turn on daily quests. `false` to disable. |
| `quests` | A list of quest IDs to give out as daily quests. |
| `reset-time` | The time of day to reset and give new quests. Use 24-hour format like `06:00` or `22:30`. |
| `randomize` | If `true`, a random subset of quests from the list is chosen each day. If `false`, it cycles through the list in order. |
| `count` | How many quests from the list to give each player per reset. |

---

## Setting Up Weekly Quests

Edit the `weekly` section in the same `daily.yml` file:

```yaml
weekly:
  enabled: true
  quests:
    - "diamond_rush"
    - "nether_explorer"
    - "master_builder"
  reset-day: "MONDAY"
  reset-time: "06:00"
  randomize: true
  count: 1
```

**Options:**

The weekly section has all the same options as daily, plus one more:

| Option | What It Does |
|--------|-------------|
| `reset-day` | The day of the week the weekly quests reset. Options: `MONDAY`, `TUESDAY`, `WEDNESDAY`, `THURSDAY`, `FRIDAY`, `SATURDAY`, `SUNDAY`. |

---

## Reset Notifications

When the reset happens, online players receive a notification. You can control how this notification is shown:

```yaml
notifications:
  mode: "actionbar"
  sound:
    enabled: true
    type: "ENTITY_PLAYER_LEVELUP"
```

**Notification modes:**

| Mode | Where the Message Shows |
|------|------------------------|
| `actionbar` | Above the hotbar (quick, non-intrusive) |
| `chat` | In the chat box |
| `none` | No notification (the quest papers are still given) |

The sound plays to the player at the same time as the notification.

---

## Marking Quests as Daily or Weekly

Any quest can be used as a daily or weekly quest. Just add its quest ID to the list in `daily.yml`.

If you also want to mark the quest itself as a daily or weekly type (for display purposes or tracking), add the type to the quest in `quests.yml`:

```yaml
zombie_daily:
  display: "Daily: Zombie Slayer"
  type: daily
  objectives:
    - type: kill
      target: ZOMBIE
      amount: 10
  reward:
    xp: 150
    money: 50
```

---

## Tips

- You can list as many quests as you want in each pool. If you have 7 quests in the daily pool and `count: 1`, each day a different quest from the pool is given until the list runs out, then it cycles again.
- Setting `randomize: true` and `count: 3` is a popular setup for giving players 3 random daily quests each day.
- Players who are offline at reset time will get their new papers delivered on their next login. They will not miss a reset.
- If a player already has an active daily quest from this cycle, they are not given a duplicate.
