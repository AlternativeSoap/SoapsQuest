# Creating Quests

Quests live in `plugins/SoapsQuest/quests.yml` under the top-level `quests:` key. Each quest needs a **unique ID**, a **display** name, at least one **objective**, and at least one **reward**.

---

## Minimal quest

```yaml
quests:
  my_first_quest:
    display: "<#55FF55>My First Quest"
    objectives:
      - type: kill
        target: ZOMBIE
        amount: 5
    reward:
      xp: 50
```

Reload with `/sq reload`, then distribute:

```
/sq give <player> my_first_quest
```

---

## Quest ID rules

- Use lowercase letters, numbers, and underscores.
- The ID is internal. Players see `display` instead.
- Generated quests use IDs like `generated_*` (Premium).

---

## Full field reference

| Field | Required | Description |
|-------|----------|-------------|
| `display` | Yes | Name on the quest paper. Supports MiniMessage and legacy `&` colors |
| `objectives` | Yes | List of objective blocks. See [Objectives](Objectives.md) |
| `reward` | Yes | Reward block. See [Rewards](Rewards.md) |
| `material` | No | Icon material for browser GUI and default paper icon |
| `tier` | No | Tier ID from `tiers.yml` (default from `config.yml`) |
| `difficulty` | No | Difficulty ID from `difficulties.yml` |
| `sequential` | No | `true` = objectives must finish in list order |
| `milestones` | No | Progress percentages for milestone messages, e.g. `[25, 50, 75]` |
| `lock-to-player` | No | `true` = paper binds to the first player who receives it |
| `lore` | No | Custom lore lines on the quest paper |
| `quest_paper` | No | Override paper material, name, and glow |
| `conditions` | No | Unlock requirements. See [Conditions](Conditions.md) |
| `permission` | No | Root-level permission that gates **progress** (not browser lock) |

### `quest_paper` block

```yaml
quest_paper:
  material: BOOK
  name: "<#FFD700>Custom Paper Name"
  glowing: false
```

### Root `permission` vs `conditions.permission`

- `conditions.permission` blocks picking up the quest in the browser when the player lacks the node.
- Root `permission` on the quest blocks **progress** even if the player already holds the paper.

---

## Objectives section

Each objective is a list item with at least `type` and the fields that type requires:

```yaml
objectives:
  - type: break
    target: OAK_LOG
    amount: 20
  - type: kill
    target: ZOMBIE
    amount: 10
```

Optional per objective:

| Field | Description |
|-------|-------------|
| `milestones` | Override quest-level milestones for this objective only |

Special field names (not `target`):

| Type | Fields |
|------|--------|
| `reachlevel` | `level` |
| `command` | `command`, `amount` |
| `placeholder` | `placeholder`, `amount` |
| `vehicle` | `vehicle` (or `target`), `amount` or `distance` |
| `chat` | `text` (or `target`), `amount` |
| `move` | `amount` or `distance` |

Type aliases: `shoot_bow`, `launch_firework`, `ride_vehicle`, `level` (same as `gainlevel`).

---

## Reward section

At least one reward key is required:

```yaml
reward:
  xp: 100
  money: 50
  sigils: 25
  items:
    - material: DIAMOND
      amount: 3
      chance: 100
  commands:
    - "broadcast {player} finished a quest!"
  quest:
    quest-id: next_quest_id
    chance: 100
```

See [Rewards](Rewards.md) for every field.

---

## Conditions section

Flat keys under `conditions:` (not a nested list):

```yaml
conditions:
  min-level: 10
  max-level: 50
  min-money: 100
  cost: 500
  min-sigils: 50
  sigil-cost: 25
  world: ["world", "world_nether"]
  permission: "rank.vip"
  completed-quests: ["starter_quest"]
  item: "DIAMOND:5"
  consume-item: true
  gamemode: ["SURVIVAL"]
  active-limit: 2
  time: "DAY"
  placeholder: "%player_level% >= 30"
```

See [Conditions](Conditions.md) for behavior and consumable costs.

---

## Validation on reload

`/sq reload` checks every quest for:

- Valid YAML syntax
- Presence of `display` and `objectives`
- Registered objective types and required fields

Invalid quests abort the reload and keep the previous config active.

---

## Testing new quests

1. Add the quest block to `quests.yml`.
2. `/sq reload`
3. `/sq give <player> <quest_id>`
4. Complete objectives and right-click the paper to claim.

Use `showcase_<type>` quests in the default `quests.yml` as templates for each objective type.

---

## In-game editor (Premium)

Premium servers can create and edit quests without hand-editing YAML:

```
/sq editor
/sq editor <quest_id>
```

Requires `soapsquest.gui.editor`. Changes save to `quests.yml` or `generated.yml`. Run `/sq reload` to refresh papers already in player inventories.

---

*Version 1.0.3*
