# Changelog

## 1.0.3

Release date: 2026

### Overview

Current documented release. Physical quest papers, 37 objective types, Free and Premium builds, and full `quests.yml` showcase set.

### Features

- 37 objective types with `showcase_<type>` test quests in default `quests.yml`
- Active/Queued paper system for duplicate quest IDs
- Sigils currency in rewards and conditions (Free and Premium)
- Anti-cheat placed-block tracking (`anti-cheat.enabled` in `config.yml`)
- Smelt target validation (must have furnace recipe output)
- Craft material alias resolution (e.g. `BANNER` to `WHITE_BANNER`)
- PlaceholderAPI expansion for completions, tiers, difficulties, and sigils
- Progress display modes: actionbar, chat, bossbar, none
- Premium: random generator, daily/weekly quests, quest loot, in-game editor

### Documentation

- Server owner wiki in `docs/` verified against `plugin.yml`, command handlers, PlaceholderAPI expansion, and `ObjectiveRegistry`

---

## 1.0.2

Release date: May 2026

### Quest queue and progress

- Fixed multiple copies of the same quest completing from one action
- Different quest IDs can be Active at once; duplicate IDs stay Queued until claim
- Recurring daily/weekly papers register progress and join the queue
- Active/Queued lore updates after queue refresh

### Logging

- Claim messages no longer spam console by default
- `log-quest-completions` and `log-admin-actions` in `config.yml` (default false)

### Conditions

- `active-limit` counts Active quest types in the queue, not all papers in playerdata

### Quality of life

- Removed dead queue branches from `/sq give` and GUI give paths
- FAQ and Objectives docs updated for Active/Queued behavior

---

## 1.0.1

Release date: May 2026

### Objective fixes

- `command`: commands matched without leading slash; subcommands supported
- `placeholder`: polls PlaceholderAPI when installed
- `explore_biome`: registry key matching on Paper 1.21+
- Internal objectives no longer double-count on event path

### Quest tracking

- Direct objectives respect active queue, conditions, and sequential order
- Queued papers do not gain progress until Active

### Rewards and conditions

- Command rewards support `{player}`, `<player>`, `%player%`
- `completed-quests` accepts flat list in quest YAML

### Configuration

- `quests.yml` header documents all 37 objective types
- `/sq reload` reports quest count
- `/sq info` shows edition and objective count

---

## Requirements (all versions)

- Paper 1.21+, Java 21+, SoapsCommon
- Optional: Vault, PlaceholderAPI, MythicMobs

---

*Current wiki version: 1.0.3*
