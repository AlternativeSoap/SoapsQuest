# Random Quest Generator (Premium)

The random quest generator creates new quests at runtime from templates in `random-generator.yml`. Output is saved to `generated.yml` by default (configurable).

**Premium only.** Free JAR does not include generator code or config.

---

## Commands

**Permission:** `soapsquest.generate`  
**Optional:** `soapsquest.generate.bypass-cooldown` skips cooldown

```
/sq generate                    # Random type
/sq generate single               # Single-objective quest
/sq generate multi                # Multi-objective quest
/sq generate sequence             # Sequential multi-objective quest
/sq generate kill 5               # Batch: 5 kill-themed quests
```

Allowed types come from `random-generator.allowed-types` (default: `single`, `multi`, `sequence`).

Batch size is capped by `max-batch-generate` in `config.yml` (default 25). Cooldown is `generate-cooldown` seconds (0 = disabled).

After generation, distribute papers:

```
/sq give <player> <generated_quest_id>
```

Run `/sq reload` to refresh lore on papers already given out.

---

## Enable the generator

```yaml
# random-generator.yml
random-generator:
  enabled: true
  generated-quest-mode: "persistent"   # persistent | temporary | session
  save-location: "generated.yml"
  allowed-types: [single, multi, sequence]
```

| Mode | Behavior |
|------|----------|
| `persistent` | Saved in `generated.yml`, kept after claim |
| `temporary` | Saved but removed from file after claim |
| `session` | Runtime only, not written to disk |

Legacy keys `save-generated-quests` and `temporary-quests` still work but `generated-quest-mode` is preferred.

---

## Key configuration areas

| Section | Purpose |
|---------|---------|
| `tier-pool` / `difficulty-pool` | Weighted random tier and difficulty |
| `objective-weights` | Chance per objective type |
| `objectives.*` | Template pools (targets, amount ranges) |
| `display.name-templates` | Quest title patterns with placeholders |
| `display.lore-styles` | `simple`, `detailed`, or `fancy` lore |
| `reward-pool` | XP, money, items, sigils, chained quests |
| `conditions` | Random unlock requirements by tier |
| `mythicmobs` | Pool for `kill_mythicmob` when MythicMobs is present |
| `milestones` | Auto milestone percentages |

### Amount formats

- Fixed: `amount: 20`
- Range: `amount: [10, 50]`
- By difficulty: `amount-by-difficulty:` with per-difficulty ranges

### Display placeholders

`<tier>`, `<tier_prefix>`, `<tier_color>`, `<difficulty>`, `<target>`, `<amount>`, `<type>`, `<objective>`, `<progress>`

---

## Managing generated quests

- `/sq list` shows generated quests in a separate section (IDs often start with `generated_` or custom `internal-name-formats`).
- `/sq editor <id>` edit in-game (Premium).
- `/sq remove <id>` delete from config (then reload).

---

## Performance settings

`config.yml`:

```yaml
max-generation-retries: 5
generate-cooldown: 0
max-batch-generate: 25
```

Batch generation uses a shared API to reduce reload overhead when creating many quests at once.

---

*Version 1.0.3 - Premium*
