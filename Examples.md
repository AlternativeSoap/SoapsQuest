# Examples

Copy-paste quest examples you can drop into your `quests.yml`. All examples go under the top-level `quests:` key.

---

## Simple Kill Quest

A basic single-objective quest that rewards items and XP.

```yaml
quests:
  zombie_slayer:
    material: ROTTEN_FLESH
    display: "<gradient:#FF5555:#CC0000>Zombie Slayer</gradient>"
    tier: common
    difficulty: easy
    lore:
      - "<#AAAAAA>Slay zombies to protect the village."
    objectives:
      - type: kill
        target: ZOMBIE
        amount: 15
    reward:
      xp: 100
      money: 50
      items:
        - material: IRON_SWORD
          name: "<#FF5555>Zombie Slayer"
          enchantments:
            - "SHARPNESS:1"
          chance: 100
```

---

## Multi-Objective Quest

A quest with two objectives. Players must complete both (in any order, unless `sequential: true` is set).

```yaml
quests:
  iron_miner:
    material: IRON_PICKAXE
    display: "<gradient:#AAAAAA:#FFFFFF>Iron Miner</gradient>"
    tier: uncommon
    difficulty: normal
    lore:
      - "<#AAAAAA>Mine and smelt iron ore."
    objectives:
      - type: break
        target: IRON_ORE
        amount: 30
      - type: smelt
        target: IRON_INGOT
        amount: 20
    reward:
      xp: 200
      money: 100
      items:
        - material: IRON_PICKAXE
          name: "<#FFFFFF>Miner's Pickaxe"
          enchantments:
            - "EFFICIENCY:2"
            - "UNBREAKING:2"
          chance: 100
```

---

## Sequential Quest

Objectives must be completed in order — step 1 before step 2, and so on.

```yaml
quests:
  master_builder:
    material: BRICKS
    display: "<gradient:#FFD700:#FF8C00>Master Builder</gradient>"
    tier: epic
    difficulty: hard
    sequential: true
    milestones: [25, 50, 75]
    lore:
      - "<#AAAAAA>Build your way to glory!"
      - "<#AAAAAA>Complete each step in order."
    objectives:
      - type: place
        target: COBBLESTONE
        amount: 50
      - type: craft
        target: STONE_BRICKS
        amount: 20
      - type: place
        target: STONE_BRICKS
        amount: 30
      - type: craft
        target: GLASS_PANE
        amount: 10
    reward:
      xp: 400
      money: 250
      items:
        - material: DIAMOND_AXE
          name: "<gradient:#FFD700:#FF8C00>Builder's Axe</gradient>"
          enchantments:
            - "EFFICIENCY:4"
            - "UNBREAKING:3"
          chance: 100
```

---

## Quest with Conditions

Restrict who can receive or progress the quest.

```yaml
quests:
  nether_explorer:
    material: NETHERRACK
    display: "<gradient:#FF5555:#FFAA00>Nether Explorer</gradient>"
    tier: rare
    difficulty: hard
    lock-to-player: true
    milestones: [50]
    lore:
      - "<#AAAAAA>Brave the dangers of the Nether."
    objectives:
      - type: kill
        target: BLAZE
        amount: 25
      - type: collect
        target: NETHER_WART
        amount: 16
      - type: brew
        target: ANY
        amount: 5
    reward:
      xp: 750
      money: 400
      items:
        - material: BLAZE_ROD
          amount: 8
          chance: 100
    conditions:
      min-level: 15
```

### Available Conditions

```yaml
    conditions:
      min-level: 10                # Player must be at least level 10
      world: ["world"]             # Only works in these worlds
      permission: "custom.perm"    # Player needs this permission
      cost: 500                    # Costs 500 (Vault) to start
      item: "DIAMOND:5"            # Costs 5 diamonds to start
      consume-item: true           # Whether the item cost is consumed
      gamemode: ["SURVIVAL"]       # Player must be in survival
      active-limit: 1              # Max concurrent active quests of this type
```

---

## Quest with Command Rewards

Run commands when the player completes the quest. Use `{player}` for the player's name.

```yaml
quests:
  first_steps:
    material: LEATHER_BOOTS
    display: "<gradient:#55FF55:#55FFFF>First Steps</gradient>"
    tier: common
    difficulty: easy
    lore:
      - "<#AAAAAA>Walk 500 blocks to explore the world."
    objectives:
      - type: move
        amount: 500
    reward:
      xp: 50
      commands:
        - "broadcast {player} took their first steps!"
        - "give {player} leather_boots{Enchantments:[{id:protection,lvl:1}]} 1"
```

---

## Quest Chain (Quest Rewards Another Quest)

Give a new quest as a reward for completing one. Use `quest:` in the reward section.

```yaml
quests:
  hunter_part1:
    material: WOODEN_SWORD
    display: "<#55FF55>Hunter - Part 1"
    tier: common
    difficulty: easy
    lore:
      - "<#AAAAAA>Hunt hostile mobs. Part 1 of 2."
    objectives:
      - type: kill
        target: HOSTILE
        amount: 20
    reward:
      xp: 100
      quest: hunter_part2

  hunter_part2:
    material: IRON_SWORD
    display: "<#FFAA00>Hunter - Part 2"
    tier: uncommon
    difficulty: normal
    lore:
      - "<#AAAAAA>The hunt continues. Defeat stronger foes."
    objectives:
      - type: kill
        target: SKELETON
        amount: 25
      - type: kill
        target: CREEPER
        amount: 15
    reward:
      xp: 300
      money: 200
      items:
        - material: DIAMOND_SWORD
          name: "<#FFAA00>Hunter's Blade"
          enchantments:
            - "SHARPNESS:2"
            - "UNBREAKING:2"
          chance: 100
```

---

## Fishing Quest

```yaml
quests:
  gone_fishing:
    material: FISHING_ROD
    display: "<gradient:#5555FF:#55FFFF>Gone Fishing</gradient>"
    tier: common
    difficulty: easy
    lore:
      - "<#AAAAAA>Catch some fish for dinner."
    objectives:
      - type: fish
        target: ANY
        amount: 10
    reward:
      xp: 75
      money: 40
      items:
        - material: FISHING_ROD
          name: "<#55FFFF>Lucky Rod"
          enchantments:
            - "LUCK_OF_THE_SEA:1"
            - "LURE:1"
          chance: 100
```

---

## Farming Quest

```yaml
quests:
  farmer:
    material: WHEAT
    display: "<gradient:#FFD700:#FF8C00>Farmer</gradient>"
    tier: common
    difficulty: easy
    lore:
      - "<#AAAAAA>Harvest crops and bake bread."
    objectives:
      - type: harvest
        target: WHEAT
        amount: 30
      - type: craft
        target: BREAD
        amount: 10
    reward:
      xp: 100
      money: 60
      items:
        - material: GOLDEN_APPLE
          amount: 2
          chance: 100
```

---

## Crafting and Enchanting Quest

```yaml
quests:
  enchanter:
    material: ENCHANTING_TABLE
    display: "<gradient:#AA00FF:#FF55FF>Apprentice Enchanter</gradient>"
    tier: uncommon
    difficulty: normal
    sequential: true
    lore:
      - "<#AAAAAA>Learn the art of enchanting."
    objectives:
      - type: craft
        target: ENCHANTING_TABLE
        amount: 1
      - type: enchant
        target: ANY
        amount: 5
    reward:
      xp: 300
      money: 150
      items:
        - material: LAPIS_LAZULI
          amount: 32
          chance: 100
```

---

## Exploration Quest

```yaml
quests:
  biome_explorer:
    material: COMPASS
    display: "<gradient:#55FFFF:#55FF55>Biome Explorer</gradient>"
    tier: uncommon
    difficulty: normal
    lore:
      - "<#AAAAAA>Discover new biomes across the world."
    objectives:
      - type: explore_biome
        target: ANY
        amount: 5
    reward:
      xp: 200
      money: 100
      items:
        - material: COMPASS
          name: "<#55FFFF>Explorer's Compass"
          chance: 100
```

---

## Custom Quest Paper Appearance

Customize the physical paper item players receive.

```yaml
quests:
  royal_decree:
    display: "<gradient:#FFD700:#FFE680>Royal Decree</gradient>"
    tier: legendary
    difficulty: expert
    quest_paper:
      material: BOOK
      name: "<#FFD700>Royal Decree"
      glowing: true
    lore:
      - "<#FFD700>⚜ A decree from the King himself ⚜"
      - "<#AAAAAA>Prove your loyalty to the crown."
    objectives:
      - type: kill
        target: ANY
        amount: 100
      - type: break
        target: DIAMOND_ORE
        amount: 20
    reward:
      xp: 1000
      money: 500
      items:
        - material: DIAMOND
          amount: 10
          chance: 100
      commands:
        - "broadcast {player} has completed the Royal Decree!"
```

---

## Animal Taming Quest

```yaml
quests:
  animal_whisperer:
    material: BONE
    display: "<gradient:#55FF55:#FFAA00>Animal Whisperer</gradient>"
    tier: common
    difficulty: easy
    lore:
      - "<#AAAAAA>Tame wolves and breed animals."
    objectives:
      - type: tame
        target: WOLF
        amount: 3
      - type: breed
        target: COW
        amount: 5
    reward:
      xp: 100
      money: 50
      items:
        - material: BONE
          amount: 16
          chance: 100
        - material: LEAD
          amount: 3
          chance: 100
```

---

## MythicMobs Quest

> Requires [MythicMobs](https://www.spigotmc.org/resources/mythicmobs.5702/) to be installed.

```yaml
quests:
  slay_the_boss:
    material: DRAGON_HEAD
    display: "<gradient:#FF5555:#AA0000>Slay the Skeleton King</gradient>"
    tier: legendary
    difficulty: expert
    lock-to-player: true
    lore:
      - "<#AAAAAA>Defeat the Skeleton King deep in the dungeon."
    objectives:
      - type: kill_mythicmob
        target: SkeletonKing
        amount: 1
    reward:
      xp: 2000
      money: 1000
      items:
        - material: NETHERITE_SWORD
          name: "<gradient:#FF5555:#FFD700>King Slayer</gradient>"
          enchantments:
            - "SHARPNESS:5"
            - "FIRE_ASPECT:2"
          chance: 100
      commands:
        - "broadcast {player} has slain the Skeleton King!"
```

---

## Tips

- Quest IDs must be unique and lowercase with underscores (e.g. `zombie_slayer`, `iron_miner`)
- All quests go under the top-level `quests:` key in `quests.yml`
- Use `/sq reload` after editing to apply changes
- Use `/sq give <player> <questid>` to test quests
- MiniMessage formatting works in `display`, `lore`, and item `name` fields
- The `chance` field on item rewards is a percentage (1-100) — use 100 for guaranteed drops
- `{player}` in command rewards is replaced with the completing player's name
