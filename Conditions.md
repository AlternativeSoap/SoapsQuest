# Conditions

Conditions are optional requirements that must be met before a quest can be activated or progressed. Some conditions just check a requirement (like level). Others cost something to unlock (like money or items), which puts the quest paper in a **locked** state until the player pays.

---

## How Locking Works

When a quest has a **cost** or **item cost** condition:

1. The quest paper starts in a **locked** state with special lore
2. The player must right-click the paper to unlock it
3. The cost is deducted (money removed, items consumed)
4. The quest activates and starts tracking progress

Conditions without costs (level, world, gamemode) are checked continuously - the quest won't progress until the condition is met.

---

## Condition Types

### `min-level` - Minimum XP Level

Player must be at least this XP level.

```yaml
conditions:
  min-level: 10
```

---

### `cost` - Money Cost

Deducts money when the player unlocks the quest. **Requires Vault.**

```yaml
conditions:
  cost: 500
```

The quest paper is locked until the player right-clicks and pays 500.

---

### `item` - Item Requirement / Cost

Requires the player to have specific items. Add `consume-item: true` to take the items when unlocking.

**Check only (items are NOT consumed):**

```yaml
conditions:
  item: "DIAMOND:5"
```

**Consume items on unlock:**

```yaml
conditions:
  item: "DIAMOND:5"
  consume-item: true
```

Format is `MATERIAL:amount`.

---

### `permission` - Permission Node

Requires a specific permission.

```yaml
conditions:
  permission: "quest.vip.exclusive"
```

Players without this permission can't activate or progress the quest.

---

### `world` - World Restriction

Quest can only be done in specific worlds.

```yaml
conditions:
  world: ["world", "world_nether"]
```

---

### `gamemode` - Gamemode Restriction

Player must be in a specific gamemode.

```yaml
conditions:
  gamemode: ["SURVIVAL"]
```

Valid values: `SURVIVAL`, `ADVENTURE`, `CREATIVE`, `SPECTATOR`.

---

### `active-limit` - Maximum Active Quests

Limits how many quests of this type a player can have active at once.

```yaml
conditions:
  active-limit: 1
```

---

## Combining Conditions

You can use multiple conditions on a single quest:

```yaml
conditions:
  min-level: 15
  cost: 1000
  world: ["world_nether"]
  permission: "quest.nether"
  item: "BLAZE_ROD:5"
  consume-item: true
  gamemode: ["SURVIVAL"]
```

All conditions must be met. The money is deducted and blaze rods consumed when the player unlocks the quest.

---

## Locked Quest Paper Appearance

When a quest paper is locked, its lore shows:

```
━━━━━━━━━━━━━━━━━━━━
⚠ QUEST LOCKED ⚠
━━━━━━━━━━━━━━━━━━━━

This quest requires payment
or items to unlock.

→ Right-click to unlock!

Requirements:
• 1000 coins
• 5x Blaze Rod

━━━━━━━━━━━━━━━━━━━━
```

This appearance is customizable in `messages.yml` under `quest-locked-lore`.

---

## Next Steps

- [Tiers & Difficulties](Tiers-and-Difficulties.md) - Rarity and scaling system
- [Creating Quests](Creating-Quests.md) - Full quest creation guide
