# Quest Loot System

Quest papers can naturally appear in chest loot and mob drops. Players discover quests while exploring or fighting monsters.

---

## How It Works

## How It Works

**Chest Loot:** Naturally generated chests (dungeons, temples, etc.) have a chance to contain quest papers.

**Mob Drops:** Mobs can drop quest papers when killed.

**Quest Sources:**
- **manual** - Use specific quests from your config
- **random** - Generate new random quests
- **mixed** - 50/50 mix of both

---

## Configuration

File: `plugins/SoapsQuest/quest-loot.yml`

### Basic Setup
```yaml
quest-loot:
  enabled: true
  debug-logs: false  # Set to true for debugging
```

### Chest Loot
```yaml
  chest:
    enabled: true
    chance: 10  # 10% chance per chest
    amount-min: 1
    amount-max: 2
    worlds: ["world", "world_nether"]
    source-mode: "mixed"  # manual | random | mixed
    quests:
      - "starter_adventure"
      - "hidden_artifact"
    structures:  # Optional filter
      - "minecraft:ancient_city"
```

### Mob Drops
```yaml
  mobs:
    enabled: true
    default-chance: 5  # Default for unlisted mobs
    worlds: ["world"]
    types:
      ZOMBIE:
        chance: 12
        amount-min: 1
        amount-max: 2
      SKELETON:
        chance: 8
      ENDER_DRAGON:
        chance: 100
        amount-min: 3
        amount-max: 6
```

---

## Testing

Enable debug mode to see detailed output:
```
/sq debug
```

Set chances to 100% for testing:
```yaml
chest:
  chance: 100
mobs:
  types:
    ZOMBIE:
      chance: 100
```

Reload after changes:
```
/sq reload
```

---

## Source Modes

### Manual Mode

Uses specific quests from config.
### Manual Mode

Uses specific quests you define in the config.

**Good for:**
- Curated quest experiences
- Story-driven progression
- Themed dungeons with specific quests
- Controlling exactly what players find

**Example:**
```yaml
source-mode: "manual"
quests:
  - "zombie_slayer"
  - "treasure_hunter"
  - "diamond_miner"
```

### Random Mode

Generates brand new quests on-the-fly using your random generator settings.

**Good for:**
- Infinite quest variety
- Keeping content fresh
- Testing and development
- Less manual quest creation work

**Example:**
```yaml
source-mode: "random"
```

### Mixed Mode

50% chance to use manual quests, 50% chance to generate random ones.

**Good for:**
- Best of both worlds
- Some curated content + some variety
- Balanced approach

**Example:**
```yaml
source-mode: "mixed"
quests:
  - "epic_boss_quest"  # Sometimes get this
  - "legendary_item"   # Sometimes get this
# Other times: random quest
```

---

## 🌍 World and Structure Filtering

### World Filter

Control which worlds can have quest loot:

```yaml
chest:
  worlds: ["world", "world_nether"]  # Only these worlds
```

```yaml
mobs:
  worlds: ["world"]  # Only overworld mobs drop quests
```

**Empty list = all worlds allowed**

### Structure Filter (Chests Only)

Only generate quests in specific structures:

```yaml
chest:
  structures:
    - "minecraft:ancient_city"
    - "minecraft:desert_pyramid"
    - "minecraft:jungle_temple"
```

**Empty list = all structures allowed**

---

## 🎯 Per-Mob Configuration

Configure different chances and amounts for each mob type:

```yaml
mobs:
  types:
    ZOMBIE:
      chance: 12       # 12% chance
      amount-min: 1
      amount-max: 2
    
    SKELETON:
      chance: 8        # 8% chance
      amount-min: 1
      amount-max: 1
    
    ENDER_DRAGON:
      chance: 100      # Always drops
      amount-min: 3
      amount-max: 6    # Boss drops 3-6 quests!
```

Unlisted mobs use `default-chance`.

---

## Example Configurations

### Dungeon Treasure

```yaml
quest-loot:
  chest:
    enabled: true
    chance: 25
    source-mode: "manual"
    quests:
      - "dungeon_boss_key"
    structures:
      - "minecraft:dungeon"
```

### Boss Rewards

```yaml
quest-loot:
  mobs:
    types:
      WITHER:
        chance: 100
        amount-min: 3
        amount-max: 5
      ENDER_DRAGON:
        chance: 100
        amount-min: 5
        amount-max: 10
```

### Random World Loot

```yaml
quest-loot:
  chest:
    enabled: true
    chance: 5
    source-mode: "random"
    worlds: ["world"]
```

---

## Troubleshooting

**No quests dropping:**
- Check `enabled: true`
- Verify world in `worlds:` list
- Run `/sq reload`
- Check console for errors

**Adjust rarity:**
```yaml
chance: 25  # Increase
chance: 2   # Decrease
```

**Random mode not working:**
- Configure `random-generator.yml`
- Set `enabled: true`
- Check console for errors

---

**[← Back to README](README.md)** | **[Configuration →](CONFIGURATION.md)**
