# Changelog

## 1.0.4

Release date: May 2026

### Stability

- Fixed shutdown task scheduling race on server stop
- Safer autosave and batch save paths
- Improved recurring task shutdown handling

### Objectives

- Fixed random/manual chat objectives in edge cases
- Improved jump objective detection
- Fixed craft objective alias handling (e.g. BANNER resolves correctly)
- Smelt target validation prevents invalid/no-recipe targets

### Rewards and completion

- Multiline completion reward placeholder: `<quest_rewards>`
- Unified completion messaging (no duplicate notifications)
- Hardened quest list reward display with SIGILS support
- Clearer workstation placement denial messages

### Anti-cheat

- Placed-block tracking for relevant objective types
- Prevents place-then-break farming by default
- Toggle: `anti-cheat.enabled: true` in config.yml

### PlaceholderAPI

- Improved expansion registration feedback
- Safer placeholder request handling
- External PlaceholderAPI pass-through in message processing

### Loot and generator (Premium)

- Loot-table source filtering via `allowed-loot-tables`
- Batch generation path for random quest generator
- Canonical mode: `generated-quest-mode: persistent | temporary | session`

---

## 1.0.3

Release date: 2026

### Overview

Physical quest papers, 37 objective types, Free and Premium builds, full `quests.yml` showcase set.

### Features

- 37 objective types with `showcase_<type>` test quests
- Active/Queued paper system for duplicate quest IDs
- Sigils currency in rewards and conditions
- Anti-cheat placed-block tracking
- Smelt target validation and craft material alias resolution
- PlaceholderAPI expansion for completions, tiers, difficulties, and sigils
- Progress display modes: actionbar, chat, bossbar, none
- Premium: random generator, daily/weekly quests, quest loot, in-game editor

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
- `log-quest-completions` and `log-admin-actions` in config.yml (default false)

### Conditions

- `active-limit` counts Active quest types in the queue, not all papers in playerdata

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
