# Changelog

All notable changes to SoapsQuest.

---

## [1.0.0-BETA] - 2025-10-27

### Initial Release

**Core Features:**
- Physical quest paper system
- 31 objective types (combat, building, collection, survival, movement, leveling, misc)
- Multi-objective and sequential quest support
- Reward system (XP, money, items, commands)
- 12 condition types
- Interactive `/sq list` with click-to-accept
- Quest browser GUI (`/sq browse`)
- Quest editor GUI (`/sq editor`)
- Real-time progress tracking (BossBar, ActionBar, Chat)
- Customizable tier and difficulty systems
- Milestone notifications
- Quest statistics (`/sq statistics`)
- Quest leaderboards (`/sq leaderboard`)
- Random quest generator
- Quest loot system (chest loot and mob drops)
- Debug mode for troubleshooting

**PlaceholderAPI Integration:**
- Dynamic leaderboard placeholders
- Global, tier-specific, and difficulty-specific leaderboards
- Player statistics and ranking
- Automatic cache with configurable refresh
- Support for all custom tiers and difficulties

**Performance:**
- Async quest progress processing
- Batch save system for data persistence
- Thread-safe concurrent operations
- Optimized for 100+ concurrent players
- Configurable batch sizes and intervals

**Integrations:**
- Vault support for economy
- PlaceholderAPI for conditions and leaderboards
- MythicMobs for custom mob objectives

**Documentation:**
- Complete wiki and guides
- Command reference
- Permission reference
- Configuration guide
- Quest creation guide
- Random generator guide
- PlaceholderAPI reference

---

## Version Numbering

- **Major (X.0.0)** - Breaking changes, major overhauls
- **Minor (1.X.0)** - New features, non-breaking changes
- **Patch (1.0.X)** - Bug fixes, minor improvements

---

## Upgrade Notes

Always backup before updating:
- `plugins/SoapsQuest/quests.yml`
- `plugins/SoapsQuest/config.yml`
- `plugins/SoapsQuest/messages.yml`
- `plugins/SoapsQuest/player-data/`

---

**[← Back to README](README.md)**
