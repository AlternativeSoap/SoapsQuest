# Default Configs

Files created in `plugins/SoapsQuest/` on first run.

---

## All editions

| File | Purpose |
|------|---------|
| `config.yml` | Core settings, progress display, sounds, anti-cheat, performance |
| `quests.yml` | Quest definitions and showcase quests |
| `messages.yml` | Player-facing messages |
| `gui.yml` | Browser, editor, and active quest menu layouts |
| `tiers.yml` | Rarity tiers (common through mythic) |
| `difficulties.yml` | Difficulty scaling multipliers |
| `playerdata.yml` | Per-player quest instance progress (auto) |
| `statistics.yml` | Completion counts by tier and difficulty (auto) |
| `sigils.yml` | Sigil balances (auto) |

### `config.yml` highlights

```yaml
gui:
  enabled: true
debug: false
autosave-interval: 5
default-tier: common
default-difficulty: normal
progress-display:
  mode: "actionbar"    # actionbar | chat | bossbar | none
abandon-on-drop: true
abandon-on-container-store: true
prevent-workstation-placement: true
anti-cheat:
  enabled: true
```

### `quests.yml`

Top-level `quests:` map. Each key is a quest ID. See [Creating Quests](Creating-Quests.md). Ships with starter quests and 37 `showcase_<type>` entries.

### `gui.yml`

Sections: `quest-browser`, `quest-editor`, `quest-details`, `objective-editor`, `condition-editor`, `reward-editor`, `player-quests`.

### `tiers.yml` / `difficulties.yml`

Define `display`, `color`, `weight`, `description`, and (for difficulties) `multiplier` blocks. See [Tiers and Difficulties](Tiers-and-Difficulties.md).

---

## Premium only

These files are bundled only in the Premium JAR:

| File | Purpose |
|------|---------|
| `daily.yml` | Daily and weekly recurring quest schedules |
| `random-generator.yml` | Random quest generator templates and reward pools |
| `quest-loot.yml` | Chest and mob quest paper drops |
| `generated.yml` | Storage for generated quest IDs (created on first generate) |

---

## Data files (do not hand-edit while server is running)

| File | Contents |
|------|----------|
| `playerdata.yml` | Active progress per quest instance UUID |
| `statistics.yml` | Historical completion stats |
| `sigils.yml` | Economy balances |

Use `/sq reload` after editing YAML configs. Data files are written on autosave (`autosave-interval` minutes) and shutdown.

---

## Config migration

SoapsQuest runs migration helpers on load for legacy reward formats in generated files. Watch console on upgrade for migration log lines.

---

## Reload order

`/sq reload` refreshes, in order:

1. `config.yml`
2. `messages.yml`
3. `difficulties.yml`, `tiers.yml`
4. `quests.yml` (with validation)
5. Rewards
6. `daily.yml` (Premium)
7. `quest-loot.yml` (Premium)
8. Random generator config (Premium)
9. `gui.yml`

---

*Version 1.0.3*
