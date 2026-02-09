# Creating Quests

There are two ways to create quests: the **in-game GUI editor** and **editing the config file** directly. Both work. The GUI is easier for quick quests; the config gives you full control.

---

## Method 1: GUI Editor

1. Run `/sq editor`
2. Click the **green emerald block** to create a new quest
3. Type a unique quest ID in chat (e.g., `mining_challenge`)
4. Use the GUI to set:
   - **Display Name** - what players see
   - **Type** - how the quest works
   - **Tier** - rarity level
   - **Difficulty** - scaling level
   - **Material** - what item the quest paper looks like
   - **Lock-to-Player** - bind to first owner
   - **Objectives** - what to do
   - **Conditions** - requirements to start
   - **Rewards** - what players earn
5. Click **Close** - all changes save instantly

---

## Method 2: Config File (quests.yml)

Open `plugins/SoapsQuest/quests.yml` and add your quest under the `quests:` section.

### Minimal Quest

The bare minimum you need:

```yaml
quests:
  my_quest:
    display: "&aMy First Quest"
    objectives:
      - type: kill
        target: ZOMBIE
        amount: 10
    reward:
      xp: 100
```

That's a working quest. Kill 10 zombies, get 100 XP.

### Full Quest Structure

Here's every option available:

```yaml
quests:
  quest_id:                          # Unique ID (lowercase, underscores)
    display: "&eMy Quest"            # Display name (color codes or MiniMessage)
    material: PAPER                  # Item material for the quest paper
    tier: common                     # common/uncommon/rare/epic/legendary/mythic
    difficulty: normal               # easy/normal/hard/expert/nightmare
    sequential: false                # true = objectives in order, false = any order
    lock-to-player: false            # true = binds to first player who progresses
    milestones: [25, 50, 75]         # Progress % notifications
    permission: "some.permission"    # Permission required to use this quest
    lore:                            # Custom description on the quest paper
      - "&7Line one"
      - "&7Line two"

    objectives:                      # At least one required
      - type: kill
        target: ZOMBIE
        amount: 10

    reward:                          # At least one reward type
      xp: 100
      money: 50
      items:
        - material: DIAMOND
          amount: 3
      commands:
        - "give {player} emerald 5"

    conditions:                      # Optional requirements
      min-level: 10
      cost: 500
```

---

## Quest Properties Explained

### `display`
The name shown to players. Supports both legacy color codes (`&a`, `&6`) and MiniMessage format (`<gradient:#FF5555:#CC0000>Name</gradient>`).

### `material`
What item the quest paper looks like in the inventory. Default is `PAPER`. Can be any valid Minecraft item - `BOOK`, `MAP`, `DIAMOND`, `ENCHANTED_BOOK`, etc.

### `tier`
Rarity level. Affects the display prefix and random generation weight. See [Tiers & Difficulties](Tiers-and-Difficulties).

### `difficulty`
Scales objective amounts and reward values through multipliers. See [Tiers & Difficulties](Tiers-and-Difficulties).

### `sequential`
- `false` (default) - All objectives can be worked on simultaneously
- `true` - Objectives must be completed in order, one at a time

### `lock-to-player`
- `false` (default) - Any player can pick up and work on the quest
- `true` - The quest binds to the first player who makes progress. Nobody else can use it.

### `milestones`
A list of progress percentages where the player gets a notification. Example: `[25, 50, 75]` sends "25% complete!", "50% complete!", etc.

### `permission`
A permission node required to activate the quest. Players without it can't progress.

### `lore`
Custom description lines displayed on the quest paper item. Supports color codes and MiniMessage.

---

## Quest Types at a Glance

| Type | # of Objectives | Behavior |
|:-----|:----------------|:---------|
| **Single** | 1 | One task, done |
| **Multi** | 2+ | All objectives at once, any order |
| **Sequential** | 2+ with `sequential: true` | One by one, in order |

---

## Item Rewards in Detail

```yaml
reward:
  items:
    - material: DIAMOND_SWORD
      amount: 1
      name: "&6Hero's Blade"              # Custom display name
      lore:                                 # Custom lore lines
        - "&7A reward for the brave"
      enchantments:                         # Enchantments
        - "SHARPNESS:3"
        - "UNBREAKING:2"
      chance: 100                           # Drop chance (1-100%)
```

The `chance` field makes rewards random. Set it to `50` and the player has a 50% chance to receive that item.

---

## Using Custom Lore

Write descriptive quest papers that look great in-game:

```yaml
my_quest:
  display: "<gradient:#FF5555:#CC0000>Zombie Slayer</gradient>"
  lore:
    - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
    - "<#FF5555>The undead are rising!"
    - "<#AAAAAA>Slay zombies to protect the village."
    - ""
    - "<#AAAAAA>➤ Kill 15 zombies"
    - ""
    - "<#55FF55>Rewards: <#FFFFFF>Iron sword & XP"
    - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
```

---

## After Creating a Quest

If you edited the config file, run `/sq reload` to load the changes. If you used the GUI editor, changes save automatically.

Test your quest:
```
/sq give YourName quest_id
```

---

## Next Steps

- [Objectives](Objectives) - All 30+ objective types
- [Rewards](Rewards) - Reward configuration in depth
- [Conditions](Conditions) - Requirements, costs, and locks
- [Examples](Examples) - Ready-to-use quest configs
