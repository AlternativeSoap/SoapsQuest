# Placeholders

SoapsQuest integrates with **PlaceholderAPI** to expose quest statistics in scoreboards, tab lists, holograms, and anywhere that supports PAPI placeholders.

---

## Requirements

- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) must be installed on your server
- SoapsQuest registers its expansion automatically on startup

---

## Available Placeholders

| Placeholder | Returns |
|:------------|:--------|
| `%soapsquest_player_quests%` | Total quests completed by the player |
| `%soapsquest_player_tier_<tier>%` | Completions for a specific tier |
| `%soapsquest_player_difficulty_<difficulty>%` | Completions for a specific difficulty |

### Tier Placeholders

```
%soapsquest_player_tier_common%
%soapsquest_player_tier_uncommon%
%soapsquest_player_tier_rare%
%soapsquest_player_tier_epic%
%soapsquest_player_tier_legendary%
%soapsquest_player_tier_mythic%
```

If you've added custom tiers, use their ID:
```
%soapsquest_player_tier_divine%
```

### Difficulty Placeholders

```
%soapsquest_player_difficulty_easy%
%soapsquest_player_difficulty_normal%
%soapsquest_player_difficulty_hard%
%soapsquest_player_difficulty_expert%
%soapsquest_player_difficulty_nightmare%
```

---

## Usage Examples

### Scoreboard (using FeatherBoard, AnimatedScoreboard, etc.)

```
&6Quests Completed: &f%soapsquest_player_quests%
&eLegendary: &f%soapsquest_player_tier_legendary%
```

### Tab List (using TAB plugin)

```
%soapsquest_player_quests% quests done
```

### Hologram

```
Top Questers
%soapsquest_player_quests%
```

---

## Next Steps

- [Default Configs](Default-Configs.md) - See all config files
- [FAQ](FAQ.md) - Common questions
