# Changelog

## v1.0.0

Initial release of SoapsQuest.

### Quests

- Quest papers as physical Minecraft items. Players carry their quest paper in their inventory.
- Quest progress tracked automatically based on what the player does.
- Quests support a name, lore, tier, difficulty, objectives, rewards, and conditions.
- Sequential mode: objectives must be completed in a specific order.
- Quest types: standard, daily (premium), weekly (premium), and randomly generated (premium).
- Right-click on a completed quest paper to claim the reward.

### Objectives

- 37 built-in objective types covering combat, mining, crafting, farming, fishing, exploration, trading, taming, and more.
- MythicMobs integration for custom mob kill objectives (requires MythicMobs).
- Each objective supports a target and a required amount.

### Rewards

- XP rewards (Minecraft experience points).
- Money rewards via Vault (requires Vault and an economy plugin).
- Item rewards with support for custom names, lore, and enchantments.
- Command rewards with support for the `{player}` placeholder.
- Quest chain rewards: give the player a new quest paper upon completion.
- Difficulty multipliers for XP and money rewards.

### Conditions

- Lock quests behind requirements that players must meet.
- Condition types: minimum level, maximum level, cost (money), required item, permission, world, game mode, active quest limit, and completed quest requirement.
- Locked quests appear as lock papers in the quest browser.

### Tiers and Difficulties

- Six built-in tiers: Common, Uncommon, Rare, Epic, Legendary, Mythic.
- Each tier has a configurable display name, color, and item icon.
- Five built-in difficulties: Easy, Normal, Hard, Expert, Nightmare.
- Each difficulty has configurable XP and money multipliers.
- Custom tiers and difficulties can be added in `tiers.yml` and `difficulties.yml`.

### Random Quest Generator (Premium)

- Automatically generate quests for players on demand.
- Configurable objective pools, name templates, and reward ranges.
- Supports multi-objective quests with configurable counts.
- Milestone generation for long-term goals.
- MythicMobs support for generated kill objectives.

### Daily and Weekly Quests (Premium)

- Automatically assign daily quests to players on a schedule.
- Automatically assign weekly quests to players on a schedule.
- Configurable assign time, quest lists, and notification modes.
- Notification modes: title, action bar, chat, or a combination.

### Quest Loot System (Premium)

- Add quest papers as loot in chests and mob drops.
- Chest loot supports vanilla loot tables and specific chest types.
- Mob drop loot supports per-mob configuration with drop chances.
- Configurable minimum and maximum level requirements for drops.

### GUI System

- Quest browser GUI: players can browse available quests and take them.
- Quest editor GUI (admin, premium): create and edit quests through an in-game menu without editing YAML.
- Objective, condition, and reward editor GUIs inside the quest editor.
- Fully configurable GUI layout and item appearances in `gui.yml`.

### Placeholders

- PlaceholderAPI support for displaying quest data in other plugins.
- Placeholders for total quests completed, by tier, and by difficulty.

### Quality of Life

- `/sq reload` to reload all configs without restarting.
- `/sq give <player> <quest-id>` to give quest papers to players directly.
- Quest statistics command for admins.
- MiniMessage formatting support for all display names, lore, and messages.
- All messages fully customizable in `messages.yml`.
- All sounds configurable in `config.yml`.

### Commands

- `/sq` (alias for `/soapsquest`) with full subcommand support.
- Per-quest permission nodes for locking quest access by permission.
- Admin-only commands protected by the `soapsquest.admin` permission.
