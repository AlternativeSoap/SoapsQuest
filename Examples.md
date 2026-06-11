# Examples

Copy-paste quest examples for common server setups. After editing, run `/sq reload`.

---

## Simple kill quest

```yaml
zombie_slayer:
  material: ROTTEN_FLESH
  display: "<gradient:#FF5555:#CC0000>Zombie Slayer</gradient>"
  tier: common
  difficulty: easy
  objectives:
    - type: kill
      target: ZOMBIE
      amount: 15
  reward:
    xp: 100
    money: 50
```

---

## Multi-step sequential quest

```yaml
master_builder:
  display: "<#FFD700>Master Builder"
  tier: epic
  difficulty: hard
  sequential: true
  milestones: [25, 50, 75]
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
  reward:
    xp: 400
    money: 250
```

---

## VIP-gated quest

```yaml
vip_hunt:
  display: "<#FFAA00>VIP Hunt"
  tier: rare
  difficulty: normal
  conditions:
    permission: "rank.vip"
  objectives:
    - type: kill
      target: BLAZE
      amount: 10
  reward:
    xp: 300
    sigils: 40
```

---

## Sigil unlock and reward

```yaml
sigil_contract:
  display: "<#FFD700>Sigil Contract"
  tier: uncommon
  conditions:
    min-sigils: 15
    sigil-cost: 10
  objectives:
    - type: collect
      target: DIAMOND
      amount: 5
  reward:
    sigils: 50
    money: 200
```

---

## Quest chain

```yaml
chapter_one:
  display: "<#55FF55>Chapter 1"
  objectives:
    - type: break
      target: OAK_LOG
      amount: 16
  reward:
    xp: 50
    quest:
      quest-id: chapter_two
      chance: 100

chapter_two:
  display: "<#55FF55>Chapter 2"
  objectives:
    - type: kill
      target: ZOMBIE
      amount: 10
  reward:
    xp: 100
```

---

## Lock to first player

```yaml
nether_explorer:
  display: "<#FF5555>Nether Explorer"
  lock-to-player: true
  conditions:
    min-level: 15
  objectives:
    - type: kill
      target: BLAZE
      amount: 25
  reward:
    xp: 750
    money: 400
```

---

## Command and placeholder objectives

```yaml
community_quest:
  display: "<#FFFF55>Community Check-in"
  objectives:
    - type: chat
      text: "daily"
      amount: 1
    - type: command
      command: help
      amount: 1
    - type: placeholder
      placeholder: player_level
      amount: 10
  reward:
    xp: 75
```

---

## Item reward with enchants

```yaml
fisher_reward:
  display: "<#55FFFF>Master Angler"
  objectives:
    - type: fish
      target: ANY
      amount: 25
  reward:
    items:
      - material: FISHING_ROD
        name: "<#55FFFF>Lucky Rod"
        enchantments:
          - "LUCK_OF_THE_SEA:2"
          - "LURE:2"
        chance: 100
```

---

## Testing every objective type

Default `quests.yml` includes `showcase_<type>` for all 37 types:

```
/sq give <player> showcase_smelt
/sq give <player> showcase_kill_mythicmob
```

See [Objectives](Objectives.md) for the full type list.

---

*Version 1.0.3*
