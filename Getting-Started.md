# Getting Started

This guide walks through a clean SoapsQuest install on a Paper 1.21+ server.

---

## 1. Install dependencies

1. Install **SoapsCommon** (required). See the [SoapsCommon wiki](https://github.com/AlternativeSoap/SoapsCommon) if needed.
2. Download **SoapsQuest** (Free or Premium) from [SoapsUniverse.com](https://www.soapsuniverse.com) or your marketplace source.
3. Optional but recommended: **Vault** (economy), **PlaceholderAPI**, **MythicMobs** (only if you use `kill_mythicmob` objectives).

---

## 2. Add the plugin

1. Place `SoapsQuest-1.0.3-Free.jar` or `SoapsQuest-1.0.3-Premium.jar` in your server's `plugins/` folder.
2. Start or restart the server.
3. Confirm the console shows SoapsQuest enabled without errors.

Premium servers should see:

```
Premium features unlocked
```

Free servers will note that the generator, daily/weekly quests, loot system, and editor GUI are not included.

---

## 3. Verify the install

Run these commands in-game or from console where applicable:

| Command | Expected result |
|---------|-----------------|
| `/sq info` | Shows version **1.0.3** and Free or Premium edition |
| `/sq list` | Lists quest IDs from `quests.yml` |
| `/sq browse` | Opens the quest browser (players only) |

Give yourself a test quest:

```
/sq give <yourname> lumberjack
```

You should receive a quest paper. Break oak logs to see progress update (action bar by default).

---

## 4. Configure quests

Default files are created in `plugins/SoapsQuest/`:

| File | Purpose |
|------|---------|
| `config.yml` | Core settings, progress display, sounds |
| `quests.yml` | Your quest definitions |
| `messages.yml` | Player-facing text |
| `gui.yml` | Menu layouts |
| `tiers.yml` | Rarity tiers |
| `difficulties.yml` | Difficulty scaling |
| `playerdata.yml` | Player progress (auto-managed) |
| `statistics.yml` | Completion counts (auto-managed) |
| `sigils.yml` | Sigil balances (auto-managed) |

Premium also extracts `daily.yml`, `random-generator.yml`, and `quest-loot.yml`.

Edit `quests.yml`, then reload:

```
/sq reload
```

Reload validates YAML before applying. If `quests.yml` has errors, the old config stays active until you fix them.

---

## 5. Set permissions

Default permissions are listed in [Commands and Permissions](Commands-and-Permissions.md). A typical survival setup:

- Give all players `soapsquest.use`, `soapsquest.gui.browser`, `soapsquest.gui.myquests`, `soapsquest.list`, `soapsquest.abandon`.
- Keep admin commands (`give`, `reload`, `editor`, `generate`) for staff groups.

**Note:** There is no `/sq progress` command. Players view progress on the quest paper lore and in the Active Quests GUI (`/sq active`). Staff use `soapsquest.progress.others` to open `/sq active <player>` for another player.

---

## 6. Test objective types

`quests.yml` includes `showcase_<type>` quests for every objective type. Quick test:

```
/sq give <player> showcase_kill
/sq give <player> showcase_break
```

See [Objectives](Objectives.md) for the full list of 37 types.

---

## 7. Optional: enable Premium features

If you run Premium:

1. **Random generator:** set `random-generator.enabled: true` in `random-generator.yml`, then `/sq generate`.
2. **Daily/weekly:** configure `daily.yml`, set `daily.enabled: true` or `weekly.enabled: true`.
3. **Quest loot:** set `quest-loot.enabled: true` in `quest-loot.yml`.
4. **Editor:** `/sq editor` (requires `soapsquest.gui.editor`).

Reload after config changes: `/sq reload`.

---

## Troubleshooting a fresh install

| Problem | Check |
|---------|-------|
| Plugin does not load | SoapsCommon installed? Java 21? Paper 1.21+? |
| No quests in `/sq list` | Syntax errors in `quests.yml`? Run `/sq reload` and read errors |
| Money rewards fail | Vault installed with an economy provider? |
| Placeholder objectives stuck | PlaceholderAPI installed? Required expansion downloaded? |
| Premium features say unavailable | Premium JAR installed, not Free? |

More answers in [FAQ](FAQ.md).

---

*Version 1.0.3*
