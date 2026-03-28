# Creating Quests

There are two ways to create quests in SoapsQuest. You can use the in-game Quest Editor GUI (Premium), or you can edit the `quests.yml` file directly. Both methods produce the same result.

---

## Method 1: In-Game Quest Editor (Premium)

> **[PREMIUM]** The Quest Editor requires the SoapsQuest Premium version. Get it at [SoapsUniverse.com](https://SoapsUniverse.com)

The quest editor is a point-and-click interface that lets you build quests without touching any files.

**How to use it:**

1. Run `/sq editor` to open the quest editor. You need the `soapsquest.gui.editor` permission.
2. Click **Create New Quest** (the green emerald block).
3. Type the quest ID in chat when prompted. This is the internal name used in commands, like `my_quest`. Use lowercase letters and underscores only.
4. The editor opens a details screen. Click each button to set the quest name, difficulty, tier, objectives, rewards, and conditions.
5. Changes save automatically. Close the editor when you are done.

All quests made in the editor are saved to `quests.yml` automatically.

---

## Method 2: Edit quests.yml

Open the file at `plugins/SoapsQuest/quests.yml` with a text editor. Add your quest using the format below, then run `/sq reload` to apply the changes.

### Minimal Quest

This is the smallest valid quest. It has one objective and one reward:

```yaml
quests:
  my_quest:
    display: "My Quest"
    objectives:
      - type: kill
        target: ZOMBIE
        amount: 10
    reward:
      xp: 100
```

### Full Quest Example

This shows every available option:

```yaml
quests:
  my_full_quest:
    display: "<gradient:#55FF55:#00CC44>My Quest</gradient>"
    material: PAPER
    tier: rare
    difficulty: hard
    sequential: false
    lock-to-player: false
    milestones: [25, 50, 75]
    lore:
      - "<#AAAAAA>A description of the quest."
      - "<#AAAAAA>More lore text here."
    objectives:
      - type: kill
        target: ZOMBIE
        amount: 20
      - type: craft
        target: IRON_SWORD
        amount: 1
    reward:
      xp: 500
      money: 250
      items:
        - material: DIAMOND
          amount: 3
          chance: 100
      commands:
        - "broadcast {player} finished My Quest!"
    conditions:
      min-level: 10
      permission: "rank.member"
```

---

## Quest Properties Explained

### Basic Info

| Property | What It Does | Required? |
|----------|-------------|-----------|
| `display` | The quest name shown to players. Supports color codes. | Yes |
| `material` | The Minecraft item that represents the quest paper. Default is `PAPER`. | No |
| `tier` | The rarity tier of the quest. Must match a tier defined in `tiers.yml`. | No |
| `difficulty` | The difficulty level. Must match a difficulty in `difficulties.yml`. | No |

### How It Works

| Property | What It Does | Default |
|----------|-------------|---------|
| `sequential` | If true, objectives must be completed one at a time in order. | `false` |
| `lock-to-player` | If true, only the first player to pick up the paper can use it. | `false` |
| `milestones` | A list of progress percentages where a milestone notification is sent. Example: `[25, 50, 75]` | None |

### Lore (Item Description)

The `lore` section lets you add custom text lines to the item tooltip. These appear below the quest name when hovering over the paper.

```yaml
lore:
  - "<#AAAAAA>Complete this to earn great rewards."
  - "<#FF5555>Danger level: High"
```

You can use MiniMessage formatting or legacy color codes (`&a`, `&c`, etc.).

---

## Objectives

Every quest needs at least one objective. Objectives go under the `objectives` section as a list:

```yaml
objectives:
  - type: kill
    target: ZOMBIE
    amount: 10
  - type: break
    target: DIAMOND_ORE
    amount: 5
```

Each objective needs a `type`, a `target`, and an `amount`. The full list of objective types is in [Objectives](Objectives.md).

---

## Rewards

Quests need at least one type of reward. You can combine as many types as you like:

```yaml
reward:
  xp: 200
  money: 100
  items:
    - material: DIAMOND
      amount: 3
      chance: 100
  commands:
    - "give {player} golden_apple 1"
  quest: "next_quest_id"
```

The `quest` reward gives the player a specific quest paper as a reward, which is how you create quest chains. See [Rewards](Rewards.md) for full details.

---

## Conditions

Conditions are optional requirements a player must meet before they can start the quest. If conditions are not met, the quest paper appears locked in the browser:

```yaml
conditions:
  min-level: 10
  cost: 500
  permission: "rank.vip"
  world: ["world", "world_nether"]
  gamemode: ["SURVIVAL"]
```

See [Conditions](Conditions.md) for the full list.

---

## Quest Types

You can control what type of quest this is. The type affects how it works with the daily/weekly system.

| Type | What It Means |
|------|--------------|
| `standard` | A regular one-time quest (default) |
| `daily` | Counted as a daily quest (used with the daily system) |
| `weekly` | Counted as a weekly quest (used with the weekly system) |

To set the type, add this to your quest:

```yaml
my_quest:
  display: "Daily Challenge"
  type: daily
  objectives:
    - type: kill
      target: ZOMBIE
      amount: 5
  reward:
    xp: 100
```

---

## After Creating a Quest

After adding a quest to `quests.yml`:

1. Run `/sq reload` to apply the changes.
2. Run `/sq list` to confirm the quest shows up.
3. Run `/sq give <yourname> <questid>` to test it yourself.
4. Check the browser with `/sq browse` to make sure it appears correctly.
