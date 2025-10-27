# Changelog

All notable changes to SoapsQuest.

---

## v1.0.0 - 2025-10-27

### Initial Production Release

**Core Features**
- Physical quest paper system
- 31 objective types (combat, building, collection, survival, movement, leveling, misc)
- Multi-objective and sequential quest support
- Single, multi, and sequential quest types
- Quest browser GUI (`/sq browse`)
- Quest editor GUI (`/sq editor`)
- Interactive `/sq list` with click-to-accept

**Progress Tracking**
- Real-time progress tracking (BossBar, ActionBar, Chat)
- Milestone notifications at 25%, 50%, 75%
- Configurable progress display modes

**Rewards & Conditions**
- XP, money, item, and command rewards
- 12 condition types (level, money, world, gamemode, time, permission, etc.)
- Lock-to-player system
- Active quest limits

**Quest Generation**
- Random quest generator (`/sq generate`)
- Configurable objectives, rewards, and conditions
- Tier and difficulty weighting
- Single, multi, and sequential generation

**Quest Loot System**
- Quest drops from naturally generated chests
- Quest drops from mob kills
- Manual, random, and mixed source modes
- World and structure filtering

**PlaceholderAPI Integration**
- Player statistics placeholders
- Global leaderboards
- Tier-specific leaderboards
- Difficulty-specific leaderboards
- Support for all custom tiers and difficulties

**Integrations**
- Vault for economy features
- PlaceholderAPI for placeholders and leaderboards
- MythicMobs for custom mob objectives

**Performance**
- Async quest progress processing
- Batch save system
- Thread-safe concurrent operations
- Optimized for 100+ concurrent players

---

## Version Numbering

- **Major (X.0.0)** – Breaking changes, major overhauls
- **Minor (1.X.0)** – New features, non-breaking changes
- **Patch (1.0.X)** – Bug fixes, minor improvements

---

## Upgrade Notes

Always backup before updating:
- `plugins/SoapsQuest/quests.yml`
- `plugins/SoapsQuest/config.yml`
- `plugins/SoapsQuest/messages.yml`
- `plugins/SoapsQuest/player-data/`

---

**[← Back to README](README.md)**

