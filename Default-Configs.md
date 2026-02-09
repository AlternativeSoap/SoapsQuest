# Default Configs

Every default configuration file shipped with SoapsQuest. Copy any of these to start fresh or compare with your current setup.

---

## Table of Contents

- [config.yml](#configyml)
- [quests.yml](#questsyml)
- [messages.yml](#messagesyml)
- [gui.yml](#guiyml)
- [tiers.yml](#tiersyml)
- [difficulties.yml](#difficultiesyml)
- [daily.yml](#dailyyml)
- [quest-loot.yml](#quest-lootyml)
- [random-generator.yml](#random-generatoryml)

---

## config.yml

<details>
<summary>Click to expand</summary>

```yaml
# ##############################################################
# #                                                            #
# #                      SoapsQuest                            #
# #                                                            #
# #           Created by: AlternativeSoap                      #
# #           Website:    www.SoapsUniverse.com                #
# #           Support:    https://discord.gg/mawAzwFq          #
# #                                                            #
# ##############################################################

# ════════════════════════════════════════════════════════════════════════════
# CORE SETTINGS
# ════════════════════════════════════════════════════════════════════════════

debug: false                            # Enable verbose debug logging (restart required)
autosave-interval: 5                    # Player progress save interval (minutes)
max-generation-retries: 5               # Max random quest generation attempts
generate-cooldown: 0                    # Cooldown in seconds between /sq generate (0 = disabled)
max-batch-generate: 25                  # Max quests per /sq generate [type] [count]

# Default values for new quests created via /sq editor
default-tier: common
default-difficulty: normal

# ════════════════════════════════════════════════════════════════════════════
# DATA CLEANUP
# ════════════════════════════════════════════════════════════════════════════

data-cleanup:
  enabled: true                         # Enable automatic data cleanup
  remove-inactive-after-days: 90        # Remove data for players offline this long
  cleanup-on-startup: true              # Run cleanup when plugin loads
  cleanup-on-save: true                 # Run cleanup on each autosave

# ════════════════════════════════════════════════════════════════════════════
# PERFORMANCE
# ════════════════════════════════════════════════════════════════════════════

performance:
  async-processing-enabled: true        # Enable async quest processing
  batch-size: 50                        # Operations per batch
  processing-interval-ticks: 20         # Batch processing interval (ticks)
  batch-save-enabled: true              # Enable batch save optimization
  batch-save-interval-ticks: 100        # Batch save interval (ticks)

# ════════════════════════════════════════════════════════════════════════════
# PROGRESS DISPLAY
# Modes: actionbar | chat | bossbar | none
# ════════════════════════════════════════════════════════════════════════════

progress-display:
  mode: "actionbar"                     # actionbar = above hotbar, bossbar = top bar
  interval: 5                           # Chat mode: show every X updates
  bossbar:
    color: "GREEN"
    style: "SEGMENTED_10"
    duration: 5                         # Seconds to display bossbar

# ════════════════════════════════════════════════════════════════════════════
# SOUNDS
# ════════════════════════════════════════════════════════════════════════════

milestone-sound: "ENTITY_PLAYER_LEVELUP"
milestone-sound-volume: 1.0
milestone-sound-pitch: 1.0

completion-sound: "UI_TOAST_CHALLENGE_COMPLETE"
completion-sound-volume: 1.0
completion-sound-pitch: 1.0

# ════════════════════════════════════════════════════════════════════════════
# QUEST PAPER BEHAVIOR
# ════════════════════════════════════════════════════════════════════════════

abandon-on-drop: true
abandon-on-container-store: true
prevent-workstation-placement: true
```

</details>

---

## quests.yml

<details>
<summary>Click to expand</summary>

```yaml
# ##############################################################
# #                                                            #
# #                      SoapsQuest                            #
# #                                                            #
# #           Created by: AlternativeSoap                      #
# #           Website:    www.SoapsUniverse.com                #
# #           Support:    https://discord.gg/mawAzwFq          #
# #                                                            #
# ##############################################################

# ╔════════════════════════════════════════════════════════════════════════════╗
# ║                        SoapsQuest - Quests                                ║
# ║  Define your server's quests here. Each quest needs a unique ID,          ║
# ║  at least one objective, and at least one reward.                         ║
# ║  Commands: /sq give <player> <quest> | /sq list | /sq browse              ║
# ╚════════════════════════════════════════════════════════════════════════════╝
#
# ════════════════════════════════════════════════════════════════════════════
# QUEST STRUCTURE
# ════════════════════════════════════════════════════════════════════════════
#
#   quest_id:                              # Unique internal ID (lowercase, underscores)
#     display: "<#55FF55>Quest Name"       # Display name (MiniMessage or legacy colors)
#     material: PAPER                      # Optional: Item material for the quest paper
#     tier: common                         # Optional: Tier from tiers.yml
#     difficulty: easy                     # Optional: Difficulty from difficulties.yml
#     sequential: false                    # Optional: true = objectives must be done in order
#     milestones: [25, 50, 75]             # Optional: Progress % notifications
#     lock-to-player: false                # Optional: true = quest binds to first player
#     lore:                                # Optional: Custom item lore
#       - "<#AAAAAA>Description line 1"
#
#     objectives:                          # Required: At least 1 objective
#       - type: kill
#         target: ZOMBIE
#         amount: 10
#
#     reward:                              # Required: At least one reward type
#       xp: 100
#       money: 50
#       items:
#         - material: DIAMOND
#           amount: 5
#       commands:
#         - "broadcast {player} completed a quest!"
#
#     conditions:                          # Optional: Requirements to start/unlock
#       min-level: 10
#       world: ["world"]
#       permission: "custom.perm"
#       cost: 500
#       item: "DIAMOND:5"
#       consume-item: true
#       gamemode: ["SURVIVAL"]
#       active-limit: 1

quests:

  lumberjack:
    material: OAK_LOG
    display: "<gradient:#55FF55:#00CC44>Lumberjack</gradient>"
    tier: common
    difficulty: easy
    lore:
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
      - "<#FFFFFF>Chop down some trees!"
      - "<#AAAAAA>A simple task for any adventurer."
      - ""
      - "<#AAAAAA>➤ Break 20 logs"
      - ""
      - "<#55FF55>Rewards: <#FFFFFF>Wooden tools & bread"
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
    objectives:
      - type: break
        target: OAK_LOG
        amount: 20
    reward:
      xp: 50
      money: 25
      items:
        - material: STONE_AXE
          name: "<#55FF55>Lumberjack's Axe"
          enchantments:
            - "EFFICIENCY:1"
          chance: 100
        - material: BREAD
          amount: 8
          chance: 100

  zombie_slayer:
    material: ROTTEN_FLESH
    display: "<gradient:#FF5555:#CC0000>Zombie Slayer</gradient>"
    tier: common
    difficulty: easy
    lore:
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
      - "<#FF5555>The undead are rising!"
      - "<#AAAAAA>Slay zombies to protect the village."
      - ""
      - "<#AAAAAA>➤ Kill 15 zombies"
      - ""
      - "<#55FF55>Rewards: <#FFFFFF>Iron sword & XP"
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
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
            - "UNBREAKING:1"
          chance: 100

  gone_fishing:
    material: FISHING_ROD
    display: "<gradient:#5555FF:#55FFFF>Gone Fishing</gradient>"
    tier: common
    difficulty: easy
    lore:
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
      - "<#55FFFF>Relax by the water."
      - "<#AAAAAA>Catch some fish for dinner."
      - ""
      - "<#AAAAAA>➤ Catch 10 fish"
      - ""
      - "<#55FF55>Rewards: <#FFFFFF>Enchanted fishing rod"
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
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
        - material: COOKED_COD
          amount: 8
          chance: 100

  iron_miner:
    material: IRON_PICKAXE
    display: "<gradient:#AAAAAA:#FFFFFF>Iron Miner</gradient>"
    tier: uncommon
    difficulty: normal
    lore:
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
      - "<#FFFFFF>Dig deep for iron!"
      - "<#AAAAAA>Mine and smelt iron ore."
      - ""
      - "<#AAAAAA>➤ Mine 30 iron ore"
      - "<#AAAAAA>➤ Smelt 20 iron ingots"
      - ""
      - "<#55FF55>Rewards: <#FFFFFF>Iron gear"
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
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
        - material: IRON_INGOT
          amount: 16
          chance: 100

  mob_hunter:
    material: BOW
    display: "<gradient:#FFAA00:#FF5555>Mob Hunter</gradient>"
    tier: uncommon
    difficulty: normal
    lore:
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
      - "<#FFAA00>Hunt the night creatures."
      - "<#AAAAAA>Clear out hostile mobs!"
      - ""
      - "<#AAAAAA>➤ Kill 20 zombies"
      - "<#AAAAAA>➤ Kill 15 skeletons"
      - "<#AAAAAA>➤ Kill 10 spiders"
      - ""
      - "<#55FF55>Rewards: <#FFFFFF>Enchanted bow & arrows"
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
    objectives:
      - type: kill
        target: ZOMBIE
        amount: 20
      - type: kill
        target: SKELETON
        amount: 15
      - type: kill
        target: SPIDER
        amount: 10
    reward:
      xp: 250
      money: 150
      items:
        - material: BOW
          name: "<#FFAA00>Hunter's Bow"
          enchantments:
            - "POWER:2"
            - "INFINITY:1"
          chance: 100
        - material: ARROW
          amount: 64
          chance: 100

  baker:
    material: BREAD
    display: "<gradient:#FFD700:#FF8C00>Baker</gradient>"
    tier: common
    difficulty: easy
    lore:
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
      - "<#FFD700>Bake some fresh bread!"
      - "<#AAAAAA>Harvest wheat and craft bread."
      - ""
      - "<#AAAAAA>➤ Break 30 wheat"
      - "<#AAAAAA>➤ Craft 10 bread"
      - ""
      - "<#55FF55>Rewards: <#FFFFFF>Golden apples & XP"
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
    objectives:
      - type: break
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

  shepherd:
    material: WHITE_WOOL
    display: "<gradient:#FF55FF:#FFAAFF>Shepherd</gradient>"
    tier: common
    difficulty: easy
    lore:
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
      - "<#FF55FF>Tend to the flock!"
      - "<#AAAAAA>Shear sheep and collect wool."
      - ""
      - "<#AAAAAA>➤ Shear 15 sheep"
      - ""
      - "<#55FF55>Rewards: <#FFFFFF>Colorful wool & shears"
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
    objectives:
      - type: shear
        target: SHEEP
        amount: 15
    reward:
      xp: 75
      money: 35
      items:
        - material: SHEARS
          name: "<#FF55FF>Shepherd's Shears"
          enchantments:
            - "EFFICIENCY:2"
            - "UNBREAKING:2"
          chance: 100

  diamond_rush:
    material: DIAMOND
    display: "<gradient:#55FFFF:#5555FF>Diamond Rush</gradient>"
    tier: rare
    difficulty: hard
    lore:
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
      - "<#55FFFF>Strike it rich!"
      - "<#AAAAAA>Mine deep and find diamonds."
      - ""
      - "<#AAAAAA>➤ Mine 10 diamond ore"
      - "<#AAAAAA>➤ Craft a diamond pickaxe"
      - ""
      - "<#55FF55>Rewards: <#FFFFFF>Enchanted diamond pickaxe"
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
    objectives:
      - type: break
        target: DIAMOND_ORE
        amount: 10
      - type: craft
        target: DIAMOND_PICKAXE
        amount: 1
    reward:
      xp: 500
      money: 300
      items:
        - material: DIAMOND_PICKAXE
          name: "<gradient:#55FFFF:#5555FF>Fortune Seeker</gradient>"
          enchantments:
            - "FORTUNE:3"
            - "EFFICIENCY:3"
            - "UNBREAKING:3"
          chance: 100
        - material: DIAMOND
          amount: 5
          chance: 100

  nether_explorer:
    material: NETHERRACK
    display: "<gradient:#FF5555:#FFAA00>Nether Explorer</gradient>"
    tier: rare
    difficulty: hard
    lock-to-player: true
    milestones: [50]
    lore:
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
      - "<#FF5555>Venture into the Nether!"
      - "<#AAAAAA>Brave the dangers below."
      - ""
      - "<#AAAAAA>➤ Kill 25 blazes"
      - "<#AAAAAA>➤ Collect 16 nether wart"
      - "<#AAAAAA>➤ Brew 5 potions"
      - ""
      - "<#55FF55>Rewards: <#FFFFFF>Blaze gear & potions"
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
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
        - material: EXPERIENCE_BOTTLE
          amount: 16
          chance: 100
    conditions:
      min-level: 15

  master_builder:
    material: BRICKS
    display: "<gradient:#FFD700:#FF8C00>Master Builder</gradient>"
    tier: epic
    difficulty: hard
    sequential: true
    milestones: [25, 50, 75]
    lore:
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
      - "<#FFD700><bold>Sequential Quest</bold>"
      - "<#AAAAAA>Build your way to glory!"
      - "<#AAAAAA>Complete each step in order."
      - ""
      - "<#AAAAAA>Step 1: Place 50 cobblestone"
      - "<#AAAAAA>Step 2: Craft 20 stone bricks"
      - "<#AAAAAA>Step 3: Place 30 stone bricks"
      - "<#AAAAAA>Step 4: Craft 10 glass panes"
      - ""
      - "<#55FF55>Rewards: <#FFFFFF>Builder's toolkit"
      - "<#AAAAAA><st>━━━━━━━━━━━━━━━━━━━━━━━━</st>"
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
        - material: GOLDEN_APPLE
          amount: 3
          chance: 100
```

</details>

---

## messages.yml

<details>
<summary>Click to expand</summary>

> This file is large. Showing the key sections - the actual file contains all customizable messages for the plugin.

```yaml
prefix: "<dark_gray>[<gradient:#FF8C00:#FFD700>SoapsQuest</gradient><dark_gray>]"

# General
no-permission: "<prefix> <#FF5555>You don't have permission to do that."
player-not-found: "<prefix> <#FF5555>Player '<#FFFFFF><player><#FF5555>' not found."
quest-not-found: "<prefix> <#FF5555>Quest '<#FFFFFF><quest><#FF5555>' not found."
config-reloaded: "<prefix> <gradient:#55FFFF:#55FF55>Configuration reloaded successfully.</gradient>"

# Quest Receiving
quest-received: "<prefix> <#55FF55>✓ <gradient:#55FF55:#55FFFF>New Quest:</gradient> <#FFFFFF><quest><#55FF55>."
quest-received-queued: "<prefix> <#FFD700>📋 Quest Queued: <#FFFFFF><quest> <#AAAAAA>(will activate after current quest)."
quest-already-active: "<prefix> <#FF5555>You already have this quest active."

# Quest Paper Interaction
quest-progress-display: |
  <prefix> <gradient:#555555:#333333><st>━━━━━━━━━━━━━━━━━━━━━━</st></gradient>
  <gradient:#FFD700:#FFAA00>Quest:</gradient> <#FFFFFF><quest>
  <objective>
  <prefix> <gradient:#555555:#333333><st>━━━━━━━━━━━━━━━━━━━━━━</st></gradient>

quest-completion-redeemed: |
  <prefix> <gradient:#55FF55:#00FF88><st>━━━━━━━━━━━━━━━━━━━━━━</st></gradient>
  <#55FF55>✓ <gradient:#55FF55:#55FFFF>Quest Complete:</gradient> <#FFFFFF><quest>
  <gradient:#FFD700:#FFAA00>Rewards have been claimed.</gradient>
  <prefix> <gradient:#55FF55:#00FF88><st>━━━━━━━━━━━━━━━━━━━━━━</st></gradient>

# Progress Display
quest-progress-actionbar: "<#FFFFFF><quest><#AAAAAA>: <#55FF55><progress><#AAAAAA>/<#55FF55><amount> <#555555>• <#FFD700><objective>"
quest-progress-bossbar: "<#FFFFFF><quest><#AAAAAA>: <#55FF55><progress><#AAAAAA>/<#55FF55><amount> <#555555>• <#FFD700><objective>"
quest-complete: "<prefix> <#55FF55>✓ <gradient:#55FF55:#55FFFF>Quest Complete:</gradient> <#FFFFFF><quest><#55FF55>!"
quest-claimable: "<prefix> <#55FF55>✓ <gradient:#55FF55:#55FFFF>Quest Complete!</gradient> '<#FFFFFF><quest><#55FF55>' <#FFD700>Right-click to claim rewards."

# Milestones
quest-milestone: "<prefix> <gradient:#FFAA00:#FFD700>★ Milestone:</gradient> <#FFFFFF><quest> <#AAAAAA>- <gradient:#FFAA00:#FFD700><milestone>% complete</gradient>"

# Quest Ownership
quest-not-yours: "<prefix> <#FF5555>⚠ This quest is bound to another player."
quest-bound-to-you: "<prefix> <#55FF55>✓ Quest '<#FFFFFF><quest><#55FF55>' is now bound to you."
quest-abandoned: "<prefix> <#FF5555>✗ Quest '<#FFFFFF><quest><#FF5555>' abandoned."
quest-dropped: "<prefix> <#FF5555>✗ You dropped '<#FFFFFF><quest><#FF5555>'. Quest progress abandoned."
quest-picked-up: "<prefix> <#55FF55>✓ You picked up a quest: '<#FFFFFF><quest><#55FF55>'!"

# Locking System
quest-unlocked: "<prefix> <#55FF55>✓ <gradient:#55FF55:#55FFFF>Quest unlocked successfully.</gradient> <#AAAAAA>Start making progress!"
quest-unlock-failed: "<prefix> <#FF5555>✗ Cannot unlock quest: <#FFFFFF><reason><#FF5555>."

# Statistics
statistic-header: "<gradient:#FFAA00:#555555><st>━━━━</st></gradient> <gradient:#FFD700:#FF8C00>SoapsQuest Statistics</gradient> <#AAAAAA>for <#FFFFFF><player> <gradient:#555555:#FFAA00><st>━━━━</st></gradient>"
statistic-total: "<#AAAAAA>Total Quests Completed: <gradient:#55FF55:#55FFFF><total></gradient>"

# Recurring Quests
recurring-quest-reset-daily: "<#55FF55>✓ <gradient:#55FF55:#00FF88>Daily quests have been reset. New quests are available!</gradient>"
recurring-quest-reset-weekly: "<#FFAA00>✓ <gradient:#FFAA00:#FF8C00>Weekly quests have been reset. New challenges await!</gradient>"
```

All messages support MiniMessage formatting and color codes. Every message sent by the plugin can be customized here.

</details>

---

## gui.yml

<details>
<summary>Click to expand</summary>

```yaml
# Quest Browser GUI
quest-browser:
  title: "&6&lSoapsQuest &7| Quest Browser"
  size: 54
  fill-empty: true
  filler-item:
    material: GRAY_STAINED_GLASS_PANE
    name: "&7"
  next-page:
    material: LIME_DYE
    name: "&aNext Page"
    lore:
      - "&7Click to view next page"
    slot: 53
  prev-page:
    material: RED_DYE
    name: "&cPrevious Page"
    lore:
      - "&7Click to view previous page"
    slot: 45
  close-button:
    material: BARRIER
    name: "&cClose"
    lore:
      - "&7Click to close"
    slot: 49
  editor-button:
    material: BOOKSHELF
    name: "&dQuest Editor"
    lore:
      - "&7Open the Quest Editor GUI"
    slot: 50
  quest-item:
    default-icon: WRITABLE_BOOK
    display: "&e<quest_display>"
    lore:
      - "&7<quest_description>"
      - ""
      - "&8Type: &f<quest_type>"
      - "&8Difficulty: &f<quest_difficulty>"
      - "&8Tier: &f<quest_tier>"
      - ""
      - "&7Objectives: &f<quest_objective_count>"
      - "&7Conditions: &f<quest_condition_count>"
      - "&7Rewards: &f<quest_reward_count>"
      - "&7Origin: &f<quest_origin>"
      - ""
      - "&aClick to receive quest paper"
  layout:
    quest-slots: [10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34]

# Quest Editor Main Menu
quest-editor:
  title: "&6&lSoapsQuest &7| Quest Editor"
  size: 54
  fill-empty: true
  filler-item:
    material: GRAY_STAINED_GLASS_PANE
    name: "&7"
  back-button:
    material: ARROW
    name: "&e&l← Back to Quest Browser"
    slot: 45
  close-button:
    material: BARRIER
    name: "&cClose"
    slot: 49
  add-quest:
    enabled: true
    material: EMERALD_BLOCK
    name: "&aCreate New Quest"
    lore:
      - "&7Start a new quest wizard"
    slot: 48
  layout:
    quest-slots: [10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34]

# Quest Details Editor
quest-details:
  title: "&6&lSoapsQuest &7| Edit: <quest_name>"
  size: 54
  fill-empty: true
  filler-item:
    material: GRAY_STAINED_GLASS_PANE
    name: "&7"
  back-button:
    material: ARROW
    name: "&7← Back"
    slot: 45
  save-button:
    material: EMERALD
    name: "&a✓ Close"
    lore:
      - "&7Changes are saved instantly!"
    slot: 53
  edit-display:
    material: NAME_TAG
    name: "&eEdit Display Name"
    slot: 11
  edit-description:
    material: WRITABLE_BOOK
    name: "&eEdit Description"
    slot: 13
  edit-type:
    material: COMPASS
    name: "&eEdit Type"
    slot: 15
  edit-difficulty:
    material: IRON_SWORD
    name: "&eEdit Difficulty"
    slot: 20
  edit-tier:
    material: DIAMOND
    name: "&eEdit Tier"
    slot: 22
  toggle-lock:
    material: TRIPWIRE_HOOK
    name: "&eLock-to-Player Toggle"
    slot: 24
  edit-material:
    material: ITEM_FRAME
    name: "&eEdit Material"
    slot: 29
  edit-objectives:
    material: TARGET
    name: "&eEdit Objectives"
    slot: 31
  edit-conditions:
    material: REDSTONE_TORCH
    name: "&eEdit Conditions"
    slot: 33
  edit-rewards:
    material: CHEST
    name: "&eEdit Rewards"
    slot: 40
  delete-quest:
    material: BARRIER
    name: "&cDelete Quest"
    slot: 49

# Objective Editor
objective-editor:
  title: "&6&lSoapsQuest &7| Objectives: <quest_name>"
  size: 54
  fill-empty: true
  filler-item:
    material: GRAY_STAINED_GLASS_PANE
    name: "&7"
  back-button:
    material: ARROW
    name: "&7← Back"
    slot: 45
  add-objective:
    material: LIME_DYE
    name: "&a+ Add Objective"
    slot: 49
  layout:
    objective-slots: [10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34]

# Condition Editor
condition-editor:
  title: "&6&lSoapsQuest &7| Conditions: <quest_name>"
  size: 54
  fill-empty: true
  filler-item:
    material: GRAY_STAINED_GLASS_PANE
    name: "&7"
  back-button:
    material: ARROW
    name: "&7← Back"
    slot: 45
  add-condition:
    material: LIME_DYE
    name: "&a+ Add Condition"
    slot: 49

# Reward Editor
reward-editor:
  title: "&6&lSoapsQuest &7| Rewards: <quest_name>"
  size: 54
  fill-empty: true
  filler-item:
    material: GRAY_STAINED_GLASS_PANE
    name: "&7"
  back-button:
    material: ARROW
    name: "&7← Back"
    slot: 45
  add-reward:
    material: LIME_DYE
    name: "&a+ Add Reward"
    slot: 49
```

</details>

---

## tiers.yml

<details>
<summary>Click to expand</summary>

```yaml
tiers:
  common:
    display: "&fCommon"
    prefix: "&7[COMMON]"
    color: "&f"
    weight: 40

  uncommon:
    display: "&2Uncommon"
    prefix: "&2[UNCOMMON]"
    color: "&2"
    weight: 32

  rare:
    display: "&9Rare"
    prefix: "&9[RARE]"
    color: "&9"
    weight: 25

  epic:
    display: "&5Epic"
    prefix: "&5[EPIC]"
    color: "&5"
    weight: 18

  legendary:
    display: "&6Legendary"
    prefix: "&6[LEGENDARY]"
    color: "&6"
    weight: 12

  mythic:
    display: "&d&lMythic"
    prefix: "&d&l[MYTHIC]"
    color: "&d"
    weight: 8
```

</details>

---

## difficulties.yml

<details>
<summary>Click to expand</summary>

```yaml
difficulties:
  easy:
    display: "&aEasy"
    weight: 50
    multiplier:
      objective-amount: 0.75
      reward: 0.75

  normal:
    display: "&eNormal"
    weight: 35
    multiplier:
      objective-amount: 1.0
      reward: 1.0

  hard:
    display: "&cHard"
    weight: 20
    multiplier:
      objective-amount: 1.5
      reward: 1.5

  expert:
    display: "&6Expert"
    weight: 10
    multiplier:
      objective-amount: 2.0
      reward: 2.0

  nightmare:
    display: "&4&lNightmare"
    weight: 5
    multiplier:
      objective-amount: 2.5
      reward: 2.5
```

</details>

---

## daily.yml

<details>
<summary>Click to expand</summary>

```yaml
daily:
  enabled: false
  quests: []
  reset-time: "00:00"
  randomize: false
  count: 1

weekly:
  enabled: false
  quests: []
  reset-day: "MONDAY"
  reset-time: "00:00"
  randomize: false
  count: 1

notifications:
  mode: "actionbar"
  sound:
    enabled: true
    type: "ENTITY_PLAYER_LEVELUP"
```

</details>

---

## quest-loot.yml

<details>
<summary>Click to expand</summary>

```yaml
quest-loot:
  enabled: true
  max-per-event: 4
  obey-plugin-restrictions: true

  chest:
    enabled: true
    chance: 10
    amount-min: 1
    amount-max: 2
    worlds: ["world", "world_nether"]
    source-mode: "manual"
    quests:
      - "welcome_quest"
      - "fishing_challenge"
      - "mob_hunter"

  mobs:
    enabled: true
    default-chance: 5
    worlds: ["world"]
    types:
      ZOMBIE:
        chance: 12
        amount-min: 1
        amount-max: 2
      SKELETON:
        chance: 8
        amount-min: 1
        amount-max: 1
      CREEPER:
        chance: 10
        amount-min: 1
        amount-max: 2
      SPIDER:
        chance: 7
      WITHER_SKELETON:
        chance: 25
        amount-min: 1
        amount-max: 3
      BLAZE:
        chance: 15
        amount-min: 1
        amount-max: 2
      ENDERMAN:
        chance: 20
        amount-min: 1
        amount-max: 2
      WITHER:
        chance: 100
        amount-min: 5
        amount-max: 8
      ENDER_DRAGON:
        chance: 100
        amount-min: 3
        amount-max: 6
      PILLAGER:
        chance: 12
      VINDICATOR:
        chance: 15
        amount-min: 1
        amount-max: 2
```

</details>

---

## random-generator.yml

<details>
<summary>Click to expand</summary>

> This file is very large (1400+ lines). Showing the structure - the full file ships with the plugin and contains objective pools, reward pools, name templates, lore styles, and condition generation settings.

```yaml
random-generator:
  enabled: true
  save-generated-quests: true
  allowed-types: [single, multi, sequence]
  save-location: "generated.yml"

  # Internal name format for generated quest IDs
  internal-name-formats:
    single: "quest_<tier>_<objective>_<id>"
    multi: "multi_<tier>_<id>"
    sequence: "seq_<tier>_<counter>"

  # Tier and difficulty pool settings
  difficulty-pool:
    enabled: true
    allowed: []      # Empty = all allowed
    default: "normal"

  tier-pool:
    enabled: true
    allowed: []      # Empty = all allowed
    default: "common"

  # Milestone settings
  milestones:
    enabled: true
    mode: "tier-based"
    tier-based:
      common: [50]
      rare: [25, 75]
      epic: [25, 50, 75]
      legendary: [20, 40, 60, 80]

  # Condition generation
  conditions:
    enabled: true
    min-level:
      enabled: true
      chance: 40
    cost:
      enabled: true
      chance: 20

  # Quest paper material selection
  quest-paper-material:
    selection-mode: "random"
    default-material: "PAPER"

  # Objective weights (higher = more common)
  objective-weights:
    kill: 45
    break: 40
    collect: 30
    craft: 30
    # ... (30+ types configured)

  # Display name templates per objective type
  display:
    lore-style: "detailed"
    name-templates:
      kill:
        - "<gradient:#FF5555:#CC0000><target> Slayer</gradient>"
      break:
        - "<#AAAAAA>Mine <white><amount></white> <target>"
      # ... (templates for all types)

  # Objective pools with target lists and amount ranges
  objectives:
    kill_zombies:
      objective: kill
      target: [ZOMBIE]
      amount: [15, 40]
    break_stone:
      objective: break
      target: [STONE, COBBLESTONE, ANDESITE]
      amount-by-difficulty:
        easy: [30, 80]
        hard: [150, 400]
    # ... (60+ objective configurations)

  # Reward pools per tier
  reward-pool:
    xp:
      common: [25, 100]
      legendary: [400, 800]
    money:
      common: [10, 100]
      legendary: [750, 2000]
    items:
      selection-mode: "weighted"
      min-items: 1
      max-items: 3
      pool:
        # 40+ item reward configurations with tier/difficulty filters
```

The full file is included with the plugin. See the `random-generator.yml` in your `plugins/SoapsQuest/` folder for the complete configuration.

</details>
