# GUI System

SoapsQuest includes two main GUIs: the **Quest Browser** for players and the **Quest Editor** for admins. Both are fully customizable through `gui.yml`.

---

## Quest Browser

**Command:** `/sq browse` (or `/sq browser`, `/sq gui`)
**Permission:** `soapsquest.gui.browser`

The browser is an inventory menu where players can see all available quests and click to receive a quest paper.

### What Players See

Each quest shows:
- Quest display name
- Description
- Type (single, multi, sequential)
- Difficulty and tier
- Number of objectives, conditions, and rewards
- Origin (manual or generated)

Click a quest to receive its paper in your inventory.

### Layout

The browser uses a 54-slot inventory (6 rows) with:
- **Quest items** in the center slots
- **Previous/Next page** buttons for pagination
- **Close button** in the bottom center
- **Editor button** (if the player has `soapsquest.gui.editor` permission)
- **Filler items** (gray glass panes) fill empty border slots

---

## Quest Editor

**Command:** `/sq editor` or `/sq editor <questId>`
**Permission:** `soapsquest.gui.editor`

The editor lets you create and modify quests entirely from the game — no config editing needed. Changes save instantly.

### Editor Flow

```
Quest Editor (list)
    ↓ click a quest
Quest Details
    ├── Edit Display Name (chat input)
    ├── Edit Description (chat input)
    ├── Edit Type (click to cycle)
    ├── Edit Difficulty (click to cycle)
    ├── Edit Tier (click to cycle)
    ├── Edit Material (chat input)
    ├── Lock-to-Player Toggle
    ├── Edit Objectives → Objective Editor
    │       ├── Add Objective → Type Selector → Chat input for target/amount
    │       └── Remove Objective (click)
    ├── Edit Conditions → Condition Editor
    │       ├── Add Condition → Type Selector → Chat input for value
    │       └── Remove Condition (click)
    ├── Edit Rewards → Reward Editor
    │       ├── Add Reward → Type Selector → Chat input for value
    │       └── Remove Reward (click)
    └── Delete Quest (requires chat confirmation)
```

### Creating a New Quest

1. Open `/sq editor`
2. Click the **green emerald block** ("Create New Quest")
3. Type a unique quest ID in chat
4. The quest details editor opens with default values
5. Click each button to configure the quest
6. Click **Close** when done — everything saves automatically

### Editing Objectives

In the quest details, click **Edit Objectives**:

- Each existing objective shows as an item with its type, target, and amount
- Click **green dye** to add a new objective
- A type selector opens showing all 30+ objective types
- Select a type, then enter the target and amount in chat
- Click an existing objective to remove it

### Editing Rewards

Click **Edit Rewards** in the quest details:

- Existing rewards show with their type and values
- Click **green dye** to add a reward
- Choose the reward type (XP, money, item, command)
- Enter the value in chat
- For item rewards, type `HAND` to use whatever item you're holding

### Editing Conditions

Click **Edit Conditions** in the quest details:

- Existing conditions show with their type and values
- Click **green dye** to add a condition
- Choose the condition type
- Enter the value in chat

---

## Customizing the GUI

Edit `gui.yml` to change the appearance of any GUI. You can customize:

- **Title** — the text at the top of the inventory
- **Size** — inventory row count (multiples of 9, up to 54)
- **Filler items** — background items in empty slots
- **Button materials** — what items the buttons look like
- **Button names and lore** — text on each button
- **Button positions** — which slot each button occupies
- **Quest item layout** — which slots display quest items

### Example: Changing the Browser Title

```yaml
quest-browser:
  title: "&6&lMy Server &7| Quest Board"
```

### Example: Changing Button Materials

```yaml
quest-browser:
  next-page:
    material: ARROW
    name: "&aNext →"
    slot: 53
  prev-page:
    material: ARROW
    name: "&c← Previous"
    slot: 45
```

### Example: Changing Quest Item Display

```yaml
quest-browser:
  quest-item:
    default-icon: WRITABLE_BOOK
    display: "&e<quest_display>"
    lore:
      - "&7<quest_description>"
      - ""
      - "&8Difficulty: &f<quest_difficulty>"
      - "&8Tier: &f<quest_tier>"
      - ""
      - "&aClick to receive quest paper"
```

**Quest item placeholders:** `<quest_display>`, `<quest_description>`, `<quest_type>`, `<quest_difficulty>`, `<quest_tier>`, `<quest_objective_count>`, `<quest_condition_count>`, `<quest_reward_count>`, `<quest_origin>`

---

## Next Steps

- [Placeholders](Placeholders) — PlaceholderAPI integration
- [Commands & Permissions](Commands-and-Permissions) — GUI-related commands and permissions
