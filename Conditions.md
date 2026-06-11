# Conditions

Conditions control **when** a player can accept or start a quest. They are defined as flat keys under `conditions:` on each quest in `quests.yml`.

When a condition fails, the player sees a message and cannot receive the quest from the browser (or cannot progress, for some checks at pickup time).

---

## Flat condition keys (quests.yml)

These keys are read directly from the `conditions:` section:

| Key | Requires | Description |
|-----|----------|-------------|
| `min-level` | Nothing | Player XP level must be at least this value |
| `max-level` | Nothing | Player XP level must be at most this value |
| `min-money` | Vault | Player balance must meet minimum (not consumed) |
| `cost` | Vault | Money deducted on accept when `consumeResources` runs |
| `min-sigils` | Nothing | Minimum Sigil balance (not consumed) |
| `sigil-cost` | Nothing | Sigils deducted on accept |
| `permission` | Nothing | Player must have this permission node |
| `world` | Nothing | List of allowed world names |
| `active-limit` | Nothing | Max number of **active quest types** in the player's queue |
| `item` | Nothing | Format `MATERIAL:amount` required in inventory |
| `consume-item` | Nothing | `true` removes items when the quest is accepted |
| `time` | Nothing | `DAY` or `NIGHT` (Minecraft world time) |
| `gamemode` | Nothing | List of allowed gamemodes, e.g. `["SURVIVAL"]` |
| `placeholder` | PlaceholderAPI | Expression such as `%player_level% >= 30` |
| `completed-quests` | Nothing | List of quest IDs the player must have completed before |

### Example

```yaml
sigil_master_contract:
  display: "<#55FFFF>Sigil Contract"
  conditions:
    min-sigils: 15
    sigil-cost: 10
  objectives:
    - type: kill
      target: ZOMBIE
      amount: 20
  reward:
    sigils: 50
    money: 120
```

---

## `active-limit` behavior

Counts **active quest types** in the player's queue, not total papers. Multiple copies of the same quest ID share one type slot. Queued copies of the same ID do not count toward the limit.

```yaml
conditions:
  active-limit: 2
```

---

## `completed-quests`

Requires the player to have **claimed** (finished) specific quests before:

```yaml
conditions:
  completed-quests:
    - lumberjack
    - zombie_slayer
```

---

## Placeholder conditions

Requires PlaceholderAPI. The value is resolved for the player, then compared:

```yaml
conditions:
  placeholder: "%vault_eco_balance% >= 1000"
```

Supported operators: `>=`, `<=`, `==`, `!=`, `>`, `<`.

Install required expansions first, for example:

```
/papi ecloud download Player
/papi reload
```

---

## Editor condition types (Premium GUI)

The in-game condition editor uses typed entries with a `type:` field. Registered types:

| Type | Purpose |
|------|---------|
| `level` | Minimum level |
| `max-level` | Maximum level |
| `money` | Minimum balance |
| `money-cost` | Deduct money on unlock |
| `sigils` | Minimum Sigils |
| `sigil-cost` | Deduct Sigils on unlock |
| `permission` | Permission node |
| `world` | Allowed worlds |
| `active-limit` | Active quest type cap |
| `item` | Required items (not consumed) |
| `item-cost` | Required items (consumed) |
| `time` | `DAY` or `NIGHT` |
| `gamemode` | Allowed gamemodes |
| `placeholder` | PlaceholderAPI expression |
| `require-completed-quests` | Minimum total completions count |
| `require-quest-completed` | Specific quest IDs completed |

The flat keys in `quests.yml` map to the same logic as these editor types.

---

## Random generator conditions (Premium)

`random-generator.yml` can randomly attach conditions to generated quests with per-tier values and chance percentages. See [Random Quest Generator](Random-Quest-Generator.md).

---

## Root `permission` field

Separate from `conditions.permission`, a quest-level `permission:` field gates **progress** on the paper after pickup. Use `conditions.permission` to lock the browser entry.

---

*Version 1.0.3*
