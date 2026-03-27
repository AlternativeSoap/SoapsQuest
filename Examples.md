# Quest Examples

These are ready-to-use quest configs you can copy and paste into your `quests.yml` file. After adding them, run `/sq reload` to load the changes.

---

## 1. Simple Kill Quest (spider_cleanup)

A basic quest where players kill a set number of spiders. Great for beginners learning how quests work.

```yaml
spider_cleanup:
  name: "<red>Spider Cleanup"
  lore:
    - "<gray>The caves are overrun with spiders."
    - "<gray>Clear them out for a reward."
  tier: common
  difficulty: easy
  objectives:
    - type: kill_mob
      target: spider
      amount: 20
  rewards:
    - type: xp
      amount: 150
    - type: money
      amount: 50
```

---

## 2. Multi-Objective Farming Quest (farm_life)

A quest that requires players to do several farming tasks. All objectives must be completed before the reward can be claimed.

```yaml
farm_life:
  name: "<green>Farm Life"
  lore:
    - "<gray>Time to get your hands dirty."
    - "<gray>Plant crops, breed animals, and harvest your fields."
  tier: uncommon
  difficulty: normal
  objectives:
    - type: plant_crop
      target: wheat
      amount: 32
    - type: breed_animal
      target: cow
      amount: 5
    - type: harvest_crop
      target: wheat
      amount: 32
  rewards:
    - type: xp
      amount: 400
    - type: money
      amount: 150
    - type: item
      material: GOLDEN_HOE
      amount: 1
```

---

## 3. Sequential Starter Quest (survival_basics)

A quest that guides new players through basic survival steps. Each objective must be completed in order.

```yaml
survival_basics:
  name: "<yellow>Survival Basics"
  lore:
    - "<gray>Learn the basics of survival."
    - "<gray>You must complete each step in order."
  tier: common
  difficulty: easy
  sequential: true
  objectives:
    - type: craft_item
      target: CRAFTING_TABLE
      amount: 1
    - type: craft_item
      target: WOODEN_PICKAXE
      amount: 1
    - type: mine_block
      target: stone
      amount: 16
    - type: craft_item
      target: FURNACE
      amount: 1
  rewards:
    - type: xp
      amount: 200
    - type: item
      material: IRON_PICKAXE
      amount: 1
```

---

## 4. Locked Quest with Conditions (treasure_hunt)

A harder quest that requires players to be at least level 10 and pay a small entry fee before they can accept it. Players without the requirements will see it as a locked paper.

```yaml
treasure_hunt:
  name: "<gold>Treasure Hunt"
  lore:
    - "<gray>Find the hidden treasure deep underground."
    - "<gray>Only experienced adventurers may attempt this."
  tier: rare
  difficulty: hard
  conditions:
    - type: min-level
      level: 10
    - type: cost
      amount: 100
  objectives:
    - type: mine_block
      target: diamond_ore
      amount: 5
    - type: kill_mob
      target: zombie
      amount: 30
    - type: reach_y_level
      level: 12
      comparison: below
  rewards:
    - type: xp
      amount: 1000
    - type: money
      amount: 500
    - type: item
      material: DIAMOND
      amount: 5
```

---

## 5. Quest Chain (hunter_rank_1, hunter_rank_2, hunter_rank_3)

Three quests that link together. Completing the first automatically gives the player the second quest paper, and so on. This creates a progression system.

```yaml
hunter_rank_1:
  name: "<gray>Hunter Rank 1"
  lore:
    - "<gray>Prove yourself as a hunter."
    - "<gray>Kill 25 hostile mobs to advance."
  tier: common
  difficulty: easy
  objectives:
    - type: kill_mob
      target: zombie
      amount: 10
    - type: kill_mob
      target: skeleton
      amount: 10
    - type: kill_mob
      target: spider
      amount: 5
  rewards:
    - type: xp
      amount: 300
    - type: quest
      quest-id: hunter_rank_2

hunter_rank_2:
  name: "<green>Hunter Rank 2"
  lore:
    - "<gray>You are getting better."
    - "<gray>Take on harder challenges."
  tier: uncommon
  difficulty: normal
  objectives:
    - type: kill_mob
      target: creeper
      amount: 15
    - type: kill_mob
      target: witch
      amount: 10
    - type: kill_mob
      target: enderman
      amount: 5
  rewards:
    - type: xp
      amount: 600
    - type: money
      amount: 200
    - type: quest
      quest-id: hunter_rank_3

hunter_rank_3:
  name: "<gold>Hunter Rank 3"
  lore:
    - "<gray>Only the most skilled hunters reach this rank."
    - "<gray>Prove your worth."
  tier: epic
  difficulty: hard
  objectives:
    - type: kill_mob
      target: blaze
      amount: 20
    - type: kill_mob
      target: wither_skeleton
      amount: 10
    - type: kill_mob
      target: ender_dragon
      amount: 1
  rewards:
    - type: xp
      amount: 2000
    - type: money
      amount: 1000
    - type: item
      material: NETHERITE_SWORD
      amount: 1
      name: "<gold>Hunter's Blade"
      lore:
        - "<gray>Awarded to the greatest of hunters."
```

---

## 6. VIP-Only Quest (vip_bounty)

A quest only players with the `vip` rank (or the `soapsquest.vip` permission) can accept. Regular players will not see it as available.

```yaml
vip_bounty:
  name: "<light_purple>VIP Bounty"
  lore:
    - "<gray>An exclusive bounty for VIP members."
    - "<light_purple>VIP access required."
  tier: legendary
  difficulty: expert
  conditions:
    - type: permission
      permission: soapsquest.vip
  objectives:
    - type: kill_mob
      target: wither
      amount: 1
    - type: kill_mob
      target: elder_guardian
      amount: 3
  rewards:
    - type: xp
      amount: 5000
    - type: money
      amount: 2500
    - type: item
      material: NETHER_STAR
      amount: 3
```

---

## Tips for Using These Examples

- Copy the YAML exactly, including the indentation. YAML is sensitive to spacing.
- Add these quests inside your `quests.yml` file at the same level as other quests.
- After pasting, run `/sq reload` to load the new quests without restarting your server.
- You can test quests using `/sq give <player> <quest-id>` to give yourself a quest paper directly.
- Adjust numbers, rewards, and objectives to fit your server style.
