# PlaceholderAPI

PlaceholderAPI integration for SoapsQuest.

> **Requirement:** [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) must be installed.

---

## 📊 Player Statistics

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%soapsquest_player_quests%` | Total quests completed | `47` |
| `%soapsquest_player_rank%` | Global rank position | `5` |
| `%soapsquest_total_players%` | Total players with stats | `150` |

---

## 🎯 Tier Statistics

Shows quest completions per tier. Works with **any custom tier** from `config.yml`.

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%soapsquest_player_tier_<tier>%` | Quests completed in tier | `15` |

**Examples:**
- `%soapsquest_player_tier_common%` → `25`
- `%soapsquest_player_tier_rare%` → `18`
- `%soapsquest_player_tier_epic%` → `8`
- `%soapsquest_player_tier_legendary%` → `2`
- `%soapsquest_player_tier_mythic%` → `5` (if custom tier exists)

---

## ⚔️ Difficulty Statistics

Shows quest completions per difficulty. Works with **any custom difficulty** from `config.yml`.

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%soapsquest_player_difficulty_<difficulty>%` | Quests completed at difficulty | `12` |

**Examples:**
- `%soapsquest_player_difficulty_easy%` → `20`
- `%soapsquest_player_difficulty_normal%` → `15`
- `%soapsquest_player_difficulty_hard%` → `8`
- `%soapsquest_player_difficulty_nightmare%` → `3`

---

## 🏆 Global Leaderboard

Top players by total quest completions (positions 1-50).

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%soapsquest_leaderboard_quests_<position>%` | Player name at position | `Notch` |
| `%soapsquest_leaderboard_quests_<position>_score%` | Quest count at position | `142` |

**Examples:**
- `%soapsquest_leaderboard_quests_1%` → `Notch`
- `%soapsquest_leaderboard_quests_1_score%` → `142`
- `%soapsquest_leaderboard_quests_10%` → `Steve`
- `%soapsquest_leaderboard_quests_10_score%` → `87`

---

## 🎨 Tier Leaderboard

Top players per tier. Works with **any custom tier**.

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%soapsquest_leaderboard_tier_<tier>_<position>%` | Player name | `Notch` |
| `%soapsquest_leaderboard_tier_<tier>_<position>_score%` | Quest count | `85` |

**Examples:**
- `%soapsquest_leaderboard_tier_common_1%` → `Steve`
- `%soapsquest_leaderboard_tier_common_1_score%` → `120`
- `%soapsquest_leaderboard_tier_epic_1%` → `Herobrine`
- `%soapsquest_leaderboard_tier_epic_1_score%` → `32`

---

## ⚡ Difficulty Leaderboard

Top players per difficulty. Works with **any custom difficulty**.

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%soapsquest_leaderboard_difficulty_<difficulty>_<position>%` | Player name | `Notch` |
| `%soapsquest_leaderboard_difficulty_<difficulty>_<position>_score%` | Quest count | `95` |

**Examples:**
- `%soapsquest_leaderboard_difficulty_easy_1%` → `Steve`
- `%soapsquest_leaderboard_difficulty_easy_1_score%` → `150`
- `%soapsquest_leaderboard_difficulty_nightmare_1%` → `Notch`
- `%soapsquest_leaderboard_difficulty_nightmare_1_score%` → `12`

---

## 📋 Usage Examples

### Hologram - Global Leaderboard

```yaml
- "&6&lTop Quest Completers"
- "&e1. &f%soapsquest_leaderboard_quests_1% &7- &a%soapsquest_leaderboard_quests_1_score%"
- "&e2. &f%soapsquest_leaderboard_quests_2% &7- &a%soapsquest_leaderboard_quests_2_score%"
- "&e3. &f%soapsquest_leaderboard_quests_3% &7- &a%soapsquest_leaderboard_quests_3_score%"
```

### Hologram - Tier-Specific

```yaml
- "&5&lTop Epic Quest Completers"
- "&e1. &f%soapsquest_leaderboard_tier_epic_1% &7- &d%soapsquest_leaderboard_tier_epic_1_score%"
- "&e2. &f%soapsquest_leaderboard_tier_epic_2% &7- &d%soapsquest_leaderboard_tier_epic_2_score%"
- "&e3. &f%soapsquest_leaderboard_tier_epic_3% &7- &d%soapsquest_leaderboard_tier_epic_3_score%"
```

### Scoreboard - Player Stats

```yaml
- "&aYour Quest Stats"
- "&7Total: &f%soapsquest_player_quests%"
- "&7Rank: &f#%soapsquest_player_rank%"
- "&7Epic: &f%soapsquest_player_tier_epic%"
- "&7Hard: &f%soapsquest_player_difficulty_hard%"
```

---

**[← Back to README](README.md)** | **[Wiki →](WIKI.md)**

