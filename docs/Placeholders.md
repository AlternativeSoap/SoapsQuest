# Placeholders

SoapsQuest supports PlaceholderAPI, which lets you display quest data in other plugins like scoreboards, holograms, tab lists, and chat formatting plugins.

> **Note:** PlaceholderAPI must be installed on your server for these to work. You can download it from [PlaceholderAPI's SpigotMC page](https://www.spigotmc.org/resources/placeholderapi.6245/).

---

## Available Placeholders

### Player Quest Count

| Placeholder | What It Shows |
|------------|--------------|
| `%soapsquest_player_quests%` | Total number of quests the player has completed |

### By Tier

These show how many quests the player has completed in each tier:

| Placeholder | What It Shows |
|------------|--------------|
| `%soapsquest_player_quests_common%` | Completed common quests |
| `%soapsquest_player_quests_uncommon%` | Completed uncommon quests |
| `%soapsquest_player_quests_rare%` | Completed rare quests |
| `%soapsquest_player_quests_epic%` | Completed epic quests |
| `%soapsquest_player_quests_legendary%` | Completed legendary quests |
| `%soapsquest_player_quests_mythic%` | Completed mythic quests |

If you add custom tiers in `tiers.yml`, placeholders for those tiers follow the same format: `%soapsquest_player_quests_<tiername>%`

### By Difficulty

These show how many quests the player has completed at each difficulty:

| Placeholder | What It Shows |
|------------|--------------|
| `%soapsquest_player_quests_easy%` | Completed easy quests |
| `%soapsquest_player_quests_normal%` | Completed normal quests |
| `%soapsquest_player_quests_hard%` | Completed hard quests |
| `%soapsquest_player_quests_expert%` | Completed expert quests |
| `%soapsquest_player_quests_nightmare%` | Completed nightmare quests |

---

## Usage Examples

**Scoreboard (using a plugin like AnimatedScoreboard or FastBoard):**

```
Quests Completed: %soapsquest_player_quests%
Legendary Quests: %soapsquest_player_quests_legendary%
```

**Tab List (using a plugin like TAB):**

```
Quests: %soapsquest_player_quests%
```

**Hologram (using a plugin like HolographicDisplays or DecentHolograms):**

```
/holo addline <name> Total Quests: %soapsquest_player_quests%
```

**Chat Formatting (using a plugin like EssentialsX Chat or ChatControl):**

```
{%soapsquest_player_quests% quests completed}
```

The exact syntax depends on which plugins you are using. Refer to those plugins documentation for the exact format to use placeholders.

---

## Testing Placeholders

You can test that a placeholder works by using the PlaceholderAPI command:

```
/papi parse <player> %soapsquest_player_quests%
```

This shows what the placeholder outputs for a specific player.
