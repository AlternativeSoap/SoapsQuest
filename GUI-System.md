# GUI System

SoapsQuest provides inventory menus for browsing quests, editing quests (Premium), and viewing active progress. Layouts are defined in `gui.yml`. The master switch is `gui.enabled` in `config.yml`.

If `gui.enabled` is `false`, all quest GUIs are blocked and players see a config message.

---

## Quest Browser (`/sq browse`)

**Permission:** `soapsquest.gui.browser`  
**Aliases:** `/sq gui`, `/sq browser`

Players browse available quests and click to receive a quest paper. Locked quests (failed conditions) show as unavailable.

Configured under `gui.yml` -> `quest-browser`:

| Setting | Purpose |
|---------|---------|
| `title`, `size` | Menu title and rows (multiples of 9) |
| `quest-slots` | Slot indices for quest icons |
| `quest-item` | Lore template with placeholders |
| `next-page` / `prev-page` | Pagination controls |
| `editor-button` | Opens editor (Premium, needs `soapsquest.gui.editor`) |
| `close-button` | Close menu |

Quest item placeholders include `<quest_display>`, `<quest_tier>`, `<quest_difficulty>`, `<quest_objective_count>`, `<quest_reward_count>`, and `<quest_origin>`.

---

## Active Quests (`/sq active`)

**Permission:** `soapsquest.gui.myquests` (listed in `plugin.yml`)  
**Aliases:** `/sq myquests`, `/sq quests`

Shows quest papers currently in the target player's inventory with per-objective progress.

```
/sq active              # Your own quests
/sq active <player>     # Another player (needs soapsquest.progress.others)
```

There is **no** `/sq progress` command. Progress is shown on the paper lore and in this GUI. Staff viewing other players requires `soapsquest.progress.others`, not `soapsquest.progress`.

---

## Quest Editor (Premium)

**Permission:** `soapsquest.gui.editor`

```
/sq editor
/sq editor <quest_id>
```

Opens the in-game quest authoring workflow:

1. **Quest Editor** main menu: list quests, create new, pagination
2. **Quest Details**: edit display, tier, difficulty, sequential flag, lore
3. **Objective Editor**: add objectives by type with guided prompts
4. **Condition Editor**: add unlock requirements
5. **Reward Editor**: add XP, money, items, commands, quest rewards

Free edition shows a premium-only message if a player runs `/sq editor`.

GUI sections in `gui.yml`:

- `quest-editor`
- `quest-details`
- `objective-editor`
- `condition-editor`
- `reward-editor`

---

## Disabling GUIs

```yaml
# config.yml
gui:
  enabled: false
```

This blocks browser, editor, and active quest menus. Commands still work (`/sq give`, `/sq list`, etc.).

---

## Text formatting

GUI titles and item text support:

- Legacy `&` color codes
- MiniMessage tags where noted in `gui.yml` comments

SoapsCommon handles rendering when the suite GUI integration is active.

---

## Reloading GUI config

```
/sq reload
```

Reloads `gui.yml` along with other configs. Open menus may need to be closed and reopened.

---

*Version 1.0.3*
