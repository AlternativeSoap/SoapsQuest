# Rewards

Rewards are granted when a player **claims** a completed quest paper (right-click). Define them under the `reward:` key on each quest in `quests.yml`.

---

## Reward types

| Key | Requires | Description |
|-----|----------|-------------|
| `xp` | Nothing | Minecraft experience points |
| `money` | Vault + economy | Currency via Vault |
| `sigils` | Nothing | SoapsQuest Sigils (stored in `sigils.yml`) |
| `items` | Nothing | List of item stacks with optional enchantments |
| `commands` | Nothing | Console commands run on claim |
| `quest` | Target quest exists | Gives another quest paper |

You can combine multiple types in one reward block.

---

## XP

```yaml
reward:
  xp: 250
```

---

## Money

```yaml
reward:
  money: 100.50
```

Requires Vault and a registered economy provider. If Vault is missing, money is skipped and a warning may appear in console.

---

## Sigils

```yaml
reward:
  sigils: 50
```

Sigils work on **Free and Premium**. Balances persist in `plugins/SoapsQuest/sigils.yml`.

Optional chance (default 100):

```yaml
reward:
  sigils: 25
  sigils-chance: 75
```

---

## Items

```yaml
reward:
  items:
    - material: DIAMOND_SWORD
      amount: 1
      name: "<#55FF55>Quest Blade"
      lore:
        - "<#AAAAAA>Awarded for bravery"
      enchantments:
        - "SHARPNESS:3"
        - "UNBREAKING:2"
      chance: 100
    - material: BREAD
      amount: 8
      chance: 100
```

| Item field | Description |
|------------|-------------|
| `material` | Bukkit material name (required) |
| `amount` | Stack size (default 1) |
| `name` | Display name (MiniMessage or `&` codes) |
| `lore` | Lore lines |
| `enchantments` | List of `ENCHANT:level` strings |
| `chance` | Drop chance 0-100 (default 100) |
| `flags` | Item flags such as `HIDE_ENCHANTS` |
| `unbreakable` | `true` / `false` |

The plugin also supports **MMOItems** custom items when MMOItems is installed (same item format as other Soaps plugins).

---

## Commands

```yaml
reward:
  commands:
    - "give {player} emerald 5"
    - "broadcast &a{player} completed a quest!"
```

Player placeholders: `{player}`, `<player>`, `%player%`.

Optional execution chance:

```yaml
reward:
  commands:
    - "say Quest complete!"
  command-chance: 50
```

---

## Quest chain reward

Give another quest paper when the player claims:

```yaml
reward:
  xp: 100
  quest:
    quest-id: chapter_two
    chance: 100
```

The chained paper is created and given on claim. The target quest must exist in `quests.yml` or `generated.yml`.

---

## Command-line reward editing

Staff can add rewards without editing YAML:

```
/sq addreward <questid> xp <amount>
/sq addreward <questid> money <amount>
/sq addreward <questid> sigils <amount>    # Premium only
/sq addreward <questid> command <cmd>
/sq addreward <questid> item               # Uses held item
/sq listreward <questid>
/sq removereward <questid> <index>
```

Permissions: `soapsquest.addreward`, `soapsquest.listreward`, `soapsquest.removereward`.

After command edits, run `/sq reload` to refresh existing papers.

---

## Completion messages

`messages.yml` supports `<quest_rewards>` in completion text to list granted rewards on multiple lines.

---

## Random generator rewards (Premium)

Generated quests pull rewards from `random-generator.yml` pools (`reward-pool`, tier ranges, item pools, and templates). The YAML format matches `quests.yml` reward blocks so you can copy templates between files.

---

*Version 1.0.3*
