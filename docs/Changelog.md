# Changelog

All notable changes to SoapsQuest.

---

## v1.0.0 — Initial Release

The first public release of SoapsQuest.

### Quests
- Over **30 objective types** — kill, mine, craft, fish, brew, enchant, tame, breed, trade, and many more
- **Multi-objective quests** — combine multiple goals into a single quest
- **Sequential objectives** — force players to complete goals in a specific order
- **Quest chaining** — link quests together so completing one unlocks the next
- **Quest paper system** — physical items players hold, with real-time progress updates in the item lore

### Rewards
- XP, money (via Vault), items, and command-based rewards
- Item rewards with enchantments, custom names, lore, and drop chances
- Multi-reward support per quest
- Reward scaling based on quest difficulty

### Conditions
- Lock quests behind level requirements, world restrictions, gamemodes, items, economy costs, permissions, and more
- Conditions display on quest papers so players know what's needed
- Combine multiple conditions per quest

### Tiers & Difficulties
- **6 default tiers** — Common, Uncommon, Rare, Epic, Legendary, Mythic
- **5 default difficulties** — Easy, Normal, Hard, Very Hard, Extreme
- Both are fully customizable with configurable weights and multipliers

### Random Quest Generator
- Generate unlimited unique quests from weighted objective and reward pools
- Customizable name templates, lore styles, and reward ranges
- MythicMobs integration for randomized custom mob objectives
- Full control over what gets generated and how often

### Daily & Weekly Quests
- Assign recurring quests with automatic reset schedules
- Configurable reset times and notification settings
- Players who miss a reset get quests on next login

### Quest Loot
- Unbound quest papers from mob drops and chest loot
- Per-mob and per-chest configuration with adjustable drop rates

### GUI
- Full **in-game quest browser** — players can browse, view details, and claim quests from a chest GUI
- Full **in-game quest editor** — create and edit quests, objectives, rewards, and conditions without touching config files
- Customizable GUI layout, titles, buttons, and items through `gui.yml`

### Quality of Life
- Configurable progress messages (action bar, boss bar, chat, title, or subtitle)
- Sound effects on progress, completion, and quest claim
- Lock-to-player binding for quest papers
- Abandon-on-drop and container-store protection
- Async processing and batch saving for server performance
- Automatic data cleanup for inactive players
- Full PlaceholderAPI support
- All messages customizable through `messages.yml`
- MiniMessage and legacy color code support

### Commands
- `/sq` — Main command hub with help, list, give, remove, reload, stats, and more
- `/sq generate` — Random quest generation
- `/sq reward` — Manual reward management
- Built-in permission system with sensible defaults
