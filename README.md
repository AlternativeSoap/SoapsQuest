# SoapsQuest

Quest plugin for **Paper 1.21+**. Players keep quest papers in their inventory, see progress on the item, and right-click to claim rewards when finished.

Current version: **1.0.1**

## Download

Install **SoapsCommon** first, then grab SoapsQuest (Free or Premium):

- https://soapsuniverse.com
- https://mythiccraft.io

**Free** has all 37 objective types, physical quest papers, rewards, conditions, tiers, and the quest browser GUIs.

**Premium** adds the random quest generator, daily/weekly quests, quest loot drops, and `/sq editor`.

## Quick start

1. Drop both jars into `plugins/`.
2. Restart the server (not `/reload` on first install).
3. Edit `plugins/SoapsQuest/quests.yml`.
4. Run `/sq reload`.
5. Try `/sq give <player> lumberjack` or `/sq browse`.

## Documentation

| Page | What it covers |
|------|----------------|
| [Getting Started](docs/Getting-Started.md) | Install and first quest |
| [Introduction](docs/Introduction.md) | How the plugin works |
| [Creating Quests](docs/Creating-Quests.md) | Quest YAML format |
| [Objectives](docs/Objectives.md) | All 37 objective types |
| [Rewards](docs/Rewards.md) | Reward setup |
| [Conditions](docs/Conditions.md) | Quest requirements |
| [Commands and Permissions](docs/Commands-and-Permissions.md) | `/sq` commands |
| [GUI System](docs/GUI-System.md) | Browser and active quest GUIs |
| [Tiers and Difficulties](docs/Tiers-and-Difficulties.md) | Tiers and difficulty labels |
| [Placeholders](docs/Placeholders.md) | PlaceholderAPI |
| [Daily and Weekly Quests](docs/Daily-and-Weekly-Quests.md) | Premium rotating quests |
| [Random Quest Generator](docs/Random-Quest-Generator.md) | Premium generator |
| [Quest Loot System](docs/Quest-Loot-System.md) | Premium mob/chest loot |
| [Examples](docs/Examples.md) | Ready-to-copy quests |
| [FAQ](docs/FAQ.md) | Common questions |
| [Default Configs](docs/Default-Configs.md) | Default YAML files |
| [Changelog](docs/CHANGELOG.md) | Version history |

## Requirements

- Paper 1.21+ (Purpur and other Paper forks work)
- Java 21+

Optional: Vault (money), PlaceholderAPI (placeholders + `placeholder` objective), MythicMobs (`kill_mythicmob` objective).

## Support

- Docs repo: https://github.com/AlternativeSoap/SoapsQuest
- Discord: https://discord.gg/SoapsUniverse
