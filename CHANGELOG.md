# Changelog

All notable changes to SoapsQuest.

---

## [1.0.0-BETA] – 2025-10-28

### Initial Free Open-Source Release

**Conversion & Licensing**
- Converted SoapsQuest to MIT-licensed free edition
- Open-sourced documentation and configuration
- Prepared repository for public release

**Core Features**
- Physical quest paper system
- 33+ objective types (combat, building, collection, survival, movement, leveling, misc)
- Multi-objective and sequential quest support
- Quest browser GUI (`/sq browse`)
- Quest editor GUI (`/sq editor`)
- Interactive `/sq list` with click-to-accept

**Progress Tracking**
- Real-time progress tracking (BossBar, ActionBar, Chat)
- Milestone notifications at 25%, 50%, 75%
- Configurable progress display modes

**Rewards & Conditions**
- XP, money, item, and command rewards
- Quest reward type for quest chains
- Unified `<amount>` field across all objectives
- Condition system (level, money, world, gamemode, time, permission, etc.)
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
- MMOItems for custom item rewards

**Documentation Overhaul**
- Comprehensive README with quick start
- Complete WIKI navigation hub
- Detailed QUEST-CREATION guide
- RANDOM-GENERATOR configuration guide
- QUEST-LOOT-SYSTEM documentation
- Full COMMANDS reference
- PERMISSIONS matrix
- CONFIGURATION examples
- PLACEHOLDERAPI integration guide

---

## Version Numbering

- **Major (X.0.0)** – Breaking changes, major overhauls
- **Minor (1.X.0)** – New features, non-breaking changes
- **Patch (1.0.X)** – Bug fixes, minor improvements
- **BETA** – Pre-release testing phase

---

## Upgrade Notes

Always backup before updating:
- `plugins/SoapsQuest/quests.yml`
- `plugins/SoapsQuest/config.yml`
- `plugins/SoapsQuest/messages.yml`
- `plugins/SoapsQuest/player-data/`

---

**[← Back to README](README.md)**

---

Licensed under the MIT License © 2025 AlternativeSoap

