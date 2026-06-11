# Placeholders

SoapsQuest registers a PlaceholderAPI expansion with identifier `soapsquest`. Requires PlaceholderAPI on the server.

Test with:

```
/papi parse <player> %soapsquest_quests%
```

Both `%soapsquest_<id>%` and `%soapsquest_player_<id>%` work (the expansion strips an optional `player_` prefix).

---

## Completion statistics

| Placeholder | Returns |
|-------------|---------|
| `%soapsquest_quests%` | Total quests completed |
| `%soapsquest_player_quests%` | Same (alternate form) |

---

## Sigils

| Placeholder | Returns |
|-------------|---------|
| `%soapsquest_sigils%` | Sigil balance (2 decimal places) |
| `%soapsquest_sigils_raw%` | Sigil balance (raw number) |
| `%soapsquest_player_sigils%` | Same as sigils |
| `%soapsquest_player_sigils_raw%` | Same as sigils_raw |

Sigils work on Free and Premium. Returns `0` if data is unavailable.

---

## By tier

Completions per tier ID from `tiers.yml`:

| Placeholder | Example tier |
|-------------|--------------|
| `%soapsquest_tier_common%` | common |
| `%soapsquest_tier_uncommon%` | uncommon |
| `%soapsquest_tier_rare%` | rare |
| `%soapsquest_tier_epic%` | epic |
| `%soapsquest_tier_legendary%` | legendary |
| `%soapsquest_tier_mythic%` | mythic |

Custom tiers use `%soapsquest_tier_<tierid>%`.

---

## By difficulty

Completions per difficulty ID from `difficulties.yml`:

| Placeholder | Example |
|-------------|---------|
| `%soapsquest_difficulty_easy%` | easy |
| `%soapsquest_difficulty_normal%` | normal |
| `%soapsquest_difficulty_hard%` | hard |
| `%soapsquest_difficulty_expert%` | expert |
| `%soapsquest_difficulty_nightmare%` | nightmare |

---

## Placeholder objective (quests)

Separate from the expansion above. Use the `placeholder` **objective type** in `quests.yml`:

```yaml
- type: placeholder
  placeholder: player_level
  amount: 30
```

Requires PlaceholderAPI. Some placeholders need eCloud expansions:

```
/papi ecloud download Player
/papi reload
```

See [Objectives](Objectives.md).

---

## Message placeholders

`messages.yml` and quest lore support internal placeholders such as `<quest_display>`, `<tier>`, `<progress>`, and `<quest_rewards>`. External PlaceholderAPI tags can pass through in messages when PAPI is installed.

---

## Usage examples

**Scoreboard:**

```
Quests: %soapsquest_quests%
Legendary: %soapsquest_tier_legendary%
Sigils: %soapsquest_sigils%
```

**TAB header:**

```
%player_name% | %soapsquest_quests% quests
```

---

*Version 1.0.3 - verified against SoapsQuestPlaceholderExpansion*
