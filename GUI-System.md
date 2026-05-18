# GUI System

SoapsQuest has two GUI screens: the Quest Browser and the Quest Editor. Players use the browser to find and pick up quests. Admins use the editor to create and manage quests directly in-game.

---

## Quest Browser

The quest browser is available to all players. It shows every quest available on the server in a scrollable grid.

**How to open it:**

```
/sq browse
```

Permission required: `soapsquest.browse` (given to all players by default)

**What players see:**

Each quest appears as an item in the grid. Hovering over it shows the quest details: name, type, difficulty, tier, number of objectives, number of conditions, and number of rewards. Players click a quest to receive the paper.

If a quest is locked (the player does not meet the conditions), it shows up with a different appearance and the unmet requirements are listed in the tooltip.

**Browser layout:**

The quest browser is a 54-slot inventory (a 6-row chest). Quests fill most of the slots. Along the bottom row, there are navigation buttons:

| Slot | Button | What It Does |
|------|--------|-------------|
| 45 | Previous Page | Go to the previous page of quests |
| 49 | Close | Close the browser |
| 50 | Quest Editor | Opens the quest editor (only visible to admins) |
| 53 | Next Page | Go to the next page of quests |

Any empty slots are filled with a glass pane spacer item.

---

## Quest Editor (Premium)

> **[PREMIUM]** The Quest Editor requires the SoapsQuest Premium version. Get it at [SoapsUniverse.com](https://SoapsUniverse.com)

The quest editor lets admins create and modify quests through the game interface instead of editing files. Everything done in the editor is saved to `quests.yml` immediately.

**How to open it:**

```
/sq editor
```

Permission required: `soapsquest.gui.editor`

You can also reach the editor by clicking the "Quest Editor" button in the bottom row of the quest browser.

### Creating a New Quest

1. Open the editor with `/sq editor`.
2. Click the green **Create New Quest** button.
3. Type the quest ID in chat (like `my_epic_quest`). Use lowercase and underscores only.
4. The quest is created and the details editor opens automatically.

### Editing a Quest

In the editor, each quest in the grid can be clicked to open its details screen. The details screen has buttons for every part of the quest:

| Button | What You Can Change |
|--------|-------------------|
| Edit Display Name | The quest name players see |
| Edit Description | A short description (used in browser tooltip) |
| Edit Type | Standard, daily, or weekly |
| Edit Difficulty | Easy, normal, hard, expert, nightmare |
| Edit Tier | Common, uncommon, rare, epic, legendary, mythic |
| Lock-to-Player Toggle | Whether the paper binds to the first person who picks it up |
| Edit Material | The item that represents the quest paper |
| Edit Objectives | Add, remove, or change objectives |
| Edit Conditions | Add, remove, or change conditions |
| Edit Rewards | Add, remove, or change rewards |
| Delete Quest | Permanently removes the quest |

Changes are saved automatically as you make them. You do not need to click a save button.

### Adding Objectives in the Editor

1. Open a quest and click **Edit Objectives**.
2. Click **Add Objective** (the green dye button).
3. Follow the click-through menus to choose the objective type, target, and amount.
4. The objective is added and you are returned to the objective list.

### Adding Rewards in the Editor

1. Open a quest and click **Edit Rewards**.
2. Click **Add Reward** (the green dye button).
3. Choose the reward type (XP, Money, Item, Command, or Quest Chain).
4. Enter the amount or details when prompted.

### Adding Conditions in the Editor

1. Open a quest and click **Edit Conditions**.
2. Click **Add Condition** (the green dye button).
3. Choose the condition type and fill in the required values.

---

## Customizing the GUI (gui.yml)

You can change how both the browser and editor look by editing `plugins/SoapsQuest/gui.yml`.

**Things you can change:**

- The title text shown at the top of the inventory
- The filler item (what fills empty slots)
- The material and name of the navigation buttons
- Which slots the quest items appear in
- The layout of the quest item tooltip in the browser

**Example: Changing the browser title**

```yaml
quest-browser:
  title: "&6&lMy Server Quests"
```

**Example: Changing the filler item**

```yaml
quest-browser:
  filler-item:
    material: BLACK_STAINED_GLASS_PANE
    name: "&7"
```

**Example: Changing which slots quests appear in**

```yaml
quest-browser:
  layout:
    quest-slots: [10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34]
```

Slot numbers follow the standard Minecraft inventory grid numbering (0 to 53 for a 6-row chest).

**Example: Changing what the quest item tooltip shows**

```yaml
quest-browser:
  quest-item:
    display: "&e<quest_display>"
    lore:
      - "&7<quest_description>"
      - ""
      - "&8Difficulty: &f<quest_difficulty>"
      - "&8Tier: &f<quest_tier>"
      - ""
      - "&aClick to receive"
```

Available placeholders for quest items in the browser:

| Placeholder | What It Shows |
|------------|--------------|
| `<quest_display>` | The quest display name |
| `<quest_description>` | The quest description |
| `<quest_type>` | Standard, daily, or weekly |
| `<quest_difficulty>` | The difficulty label |
| `<quest_tier>` | The tier label |
| `<quest_objective_count>` | Number of objectives |
| `<quest_condition_count>` | Number of conditions |
| `<quest_reward_count>` | Number of rewards |
| `<quest_origin>` | Whether this was hand-made or randomly generated |
