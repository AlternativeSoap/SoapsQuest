# Examples

Ready-to-use quest configurations you can paste directly into your `quests.yml`.

---

## Beginner Quest - Simple Kill Quest

A single-objective quest with basic rewards.

```yaml
  spider_cleanup:
    material: SPIDER_EYE
    display: "<gradient:#555555:#FF5555>Spider Cleanup</gradient>"
    tier: common
    difficulty: easy
    lore:
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
      - "<#FF5555>Spiders are everywhere!"
      - "<#AAAAAA>Clear out the creepy crawlies."
      - ""
      - "<#AAAAAA>➤ Kill 10 spiders"
      - ""
      - "<#55FF55>Rewards: <#FFFFFF>String & XP"
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
    objectives:
      - type: kill
        target: SPIDER
        amount: 10
    reward:
      xp: 50
      items:
        - material: STRING
          amount: 16
          chance: 100
```

---

## Farming Quest - Multi-Objective

Multiple objectives that can be done in any order.

```yaml
  farm_life:
    material: WHEAT
    display: "<gradient:#55FF55:#FFD700>Farm Life</gradient>"
    tier: common
    difficulty: easy
    lore:
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
      - "<#55FF55>Live the farm life!"
      - "<#AAAAAA>Grow crops and raise animals."
      - ""
      - "<#AAAAAA>➤ Break 20 wheat"
      - "<#AAAAAA>➤ Breed 5 cows"
      - "<#AAAAAA>➤ Shear 10 sheep"
      - ""
      - "<#55FF55>Rewards: <#FFFFFF>Golden carrots & XP"
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
    objectives:
      - type: break
        target: WHEAT
        amount: 20
      - type: breed
        target: COW
        amount: 5
      - type: shear
        target: SHEEP
        amount: 10
    reward:
      xp: 150
      money: 75
      items:
        - material: GOLDEN_CARROT
          amount: 8
          chance: 100
```

---

## Sequential Quest - Step by Step

Objectives must be completed in order.

```yaml
  survival_basics:
    material: CRAFTING_TABLE
    display: "<gradient:#FFD700:#FF8C00>Survival Basics</gradient>"
    tier: uncommon
    difficulty: easy
    sequential: true
    milestones: [25, 50, 75]
    lore:
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
      - "<#FFD700>Learn the basics of survival!"
      - ""
      - "<#AAAAAA>Step 1: Chop 10 logs"
      - "<#AAAAAA>Step 2: Craft a crafting table"
      - "<#AAAAAA>Step 3: Craft a wooden pickaxe"
      - "<#AAAAAA>Step 4: Mine 20 stone"
      - ""
      - "<#55FF55>Rewards: <#FFFFFF>Stone tools & food"
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
    objectives:
      - type: break
        target: OAK_LOG
        amount: 10
      - type: craft
        target: CRAFTING_TABLE
        amount: 1
      - type: craft
        target: WOODEN_PICKAXE
        amount: 1
      - type: break
        target: STONE
        amount: 20
    reward:
      xp: 200
      items:
        - material: STONE_PICKAXE
          name: "<#AAAAAA>Starter Pickaxe"
          enchantments:
            - "EFFICIENCY:1"
          chance: 100
        - material: COOKED_BEEF
          amount: 16
          chance: 100
```

---

## Locked Quest - With Conditions

A quest that requires payment and a minimum level to unlock.

```yaml
  treasure_hunt:
    material: CHEST
    display: "<gradient:#FFD700:#FFAA00>Treasure Hunt</gradient>"
    tier: rare
    difficulty: normal
    lock-to-player: true
    lore:
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
      - "<#FFD700>Legends speak of buried treasure..."
      - "<#AAAAAA>Collect rare items from across the land."
      - ""
      - "<#AAAAAA>➤ Collect 5 emeralds"
      - "<#AAAAAA>➤ Collect 3 diamonds"
      - "<#AAAAAA>➤ Fish 10 fish"
      - ""
      - "<#FF5555>Requires: Level 10, 500 coins"
      - ""
      - "<#55FF55>Rewards: <#FFFFFF>Enchanted gear & gold"
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
    objectives:
      - type: collect
        target: EMERALD
        amount: 5
      - type: collect
        target: DIAMOND
        amount: 3
      - type: fish
        target: ANY
        amount: 10
    reward:
      xp: 400
      money: 250
      items:
        - material: GOLDEN_APPLE
          amount: 3
          chance: 100
        - material: DIAMOND_HELMET
          name: "<#FFD700>Treasure Hunter's Helm"
          enchantments:
            - "PROTECTION:2"
            - "UNBREAKING:2"
          chance: 50
    conditions:
      min-level: 10
      cost: 500
```

---

## Quest Chain - Quest as Reward

Completing one quest gives the player the next one.

```yaml
  hunter_rank_1:
    material: WOODEN_SWORD
    display: "<#55FF55>Hunter Rank I"
    tier: common
    difficulty: easy
    lore:
      - "<#AAAAAA>Prove yourself as a hunter."
      - "<#AAAAAA>➤ Kill 10 zombies"
    objectives:
      - type: kill
        target: ZOMBIE
        amount: 10
    reward:
      xp: 50
      quest: "hunter_rank_2"

  hunter_rank_2:
    material: STONE_SWORD
    display: "<#FFD700>Hunter Rank II"
    tier: uncommon
    difficulty: normal
    lore:
      - "<#AAAAAA>The hunt continues."
      - "<#AAAAAA>➤ Kill 10 skeletons"
      - "<#AAAAAA>➤ Kill 5 creepers"
    objectives:
      - type: kill
        target: SKELETON
        amount: 10
      - type: kill
        target: CREEPER
        amount: 5
    reward:
      xp: 150
      money: 100
      quest:
        quest-id: "hunter_rank_3"
        chance: 100

  hunter_rank_3:
    material: IRON_SWORD
    display: "<gradient:#FF5555:#FFAA00>Hunter Rank III</gradient>"
    tier: rare
    difficulty: hard
    lock-to-player: true
    milestones: [50]
    lore:
      - "<#AAAAAA>The final trial."
      - "<#AAAAAA>➤ Kill 20 endermen"
      - "<#AAAAAA>➤ Kill 3 withers"
    objectives:
      - type: kill
        target: ENDERMAN
        amount: 20
      - type: kill
        target: WITHER
        amount: 3
    reward:
      xp: 1000
      money: 500
      items:
        - material: DIAMOND_SWORD
          name: "<gradient:#FF5555:#FFD700>Master Hunter's Blade</gradient>"
          enchantments:
            - "SHARPNESS:5"
            - "LOOTING:3"
            - "UNBREAKING:3"
          chance: 100
```

---

## VIP Quest - Permission Locked

Only players with a specific permission can use this quest.

```yaml
  vip_bounty:
    material: ENCHANTED_BOOK
    display: "<gradient:#FFD700:#FF8C00>★ VIP Bounty ★</gradient>"
    tier: epic
    difficulty: hard
    lore:
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
      - "<#FFD700>An exclusive quest for VIP members."
      - ""
      - "<#AAAAAA>➤ Kill 50 hostile mobs"
      - ""
      - "<#FF5555>Requires: VIP rank"
      - ""
      - "<#55FF55>Rewards: <#FFFFFF>Netherite ingot & XP"
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
    objectives:
      - type: kill
        target: ANY
        amount: 50
    reward:
      xp: 800
      money: 500
      items:
        - material: NETHERITE_INGOT
          amount: 1
          chance: 100
    conditions:
      permission: "soapsquest.vip"
```

---

## Random Chance Rewards

A quest where not all rewards are guaranteed.

```yaml
  gambler_quest:
    material: MAP
    display: "<gradient:#FFD700:#55FF55>Lucky Strike</gradient>"
    tier: rare
    difficulty: normal
    lore:
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
      - "<#FFD700>Feeling lucky?"
      - "<#AAAAAA>Mine ores and hope for good drops."
      - ""
      - "<#AAAAAA>➤ Mine 50 ores (any type)"
      - ""
      - "<#55FF55>Rewards: <#FFFFFF>Random loot!"
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
    objectives:
      - type: break
        target: IRON_ORE
        amount: 50
    reward:
      xp: 200
      items:
        - material: IRON_INGOT
          amount: 16
          chance: 100          # Always
        - material: GOLD_INGOT
          amount: 8
          chance: 75           # 75% chance
        - material: DIAMOND
          amount: 3
          chance: 30           # 30% chance
        - material: EMERALD
          amount: 5
          chance: 15           # 15% chance
        - material: NETHERITE_SCRAP
          amount: 1
          chance: 5            # 5% chance - jackpot!
```

---

## Nether Adventure - World Restricted

A quest that only works in the Nether.

```yaml
  nether_harvester:
    material: NETHER_WART
    display: "<gradient:#FF5555:#FFAA00>Nether Harvester</gradient>"
    tier: rare
    difficulty: hard
    lore:
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
      - "<#FF5555>Gather resources from the Nether."
      - ""
      - "<#AAAAAA>➤ Collect 32 nether wart"
      - "<#AAAAAA>➤ Kill 10 blazes"
      - "<#AAAAAA>➤ Break 64 netherrack"
      - ""
      - "<#FF5555>⚠ Nether only"
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
    objectives:
      - type: collect
        target: NETHER_WART
        amount: 32
      - type: kill
        target: BLAZE
        amount: 10
      - type: break
        target: NETHERRACK
        amount: 64
    reward:
      xp: 600
      money: 350
      items:
        - material: BLAZE_ROD
          amount: 12
          chance: 100
    conditions:
      world: ["world_nether"]
      min-level: 20
```

---

## Tips

- All quest IDs must be unique and lowercase with underscores
- Every quest needs at least one objective and one reward
- Run `/sq reload` after editing `quests.yml` to load changes
- Test with `/sq give YourName quest_id` before making it public
- Combine these examples to create your own unique quests

---

## Next Steps

- [Creating Quests](Creating-Quests) - Full creation guide
- [Objectives](Objectives) - All objective types
- [Rewards](Rewards) - All reward types
- [Conditions](Conditions) - All condition types
