# Quest Loot System - Testing Guide

## 🧪 How to Test the Quest Loot System

This guide provides comprehensive methods to test and verify the Quest Loot Integration System works correctly.

---

## Prerequisites

1. **Enable the System**
   ```yaml
   quest-loot:
     enabled: true
   ```

2. **Configure for Testing**
   - Set high chances (50-100%) for faster testing
   - Use specific worlds you're testing in
   - Start with manual mode for predictable results

3. **Enable Debug Mode**
   ```
   /sq debug toggle
   ```
   This will show detailed logs about loot generation in the console.

---

## Testing Chest Loot

### Step 1: Configure Chest Loot
```yaml
chest:
  enabled: true
  chance: 100              # 100% for guaranteed testing
  amount-min: 2
  amount-max: 3
  worlds: ["world"]        # Your test world
  source-mode: "manual"    # Predictable for testing
  quests:
    - "your_quest_id_here"  # Use a quest from quests.yml
```

### Step 2: Reload Configuration
```
/sq reload
```

### Step 3: Test Natural Chest Loot
1. **Find naturally generated chests:**
   - Dungeons
   - Mineshafts
   - Villages
   - Desert temples
   - Jungle temples
   - Strongholds
   - End cities

2. **Open the chest** - Quest papers should appear in the loot

3. **Verify:**
   - Check console for debug messages
   - Papers should be unbound (no player name yet)
   - Amount should be 2-3 papers
   - Quest ID should match your configured quest

### Step 4: Test with Structure Spawn
If you have structure spawn commands:
```
/locate structure minecraft:desert_pyramid
/tp @s ~ ~ ~
```
Find and open chests in those structures.

---

## Testing Mob Drops

### Step 1: Configure Mob Loot
```yaml
mobs:
  enabled: true
  default-chance: 0        # Turn off default
  worlds: ["world"]
  types:
    ZOMBIE:
      chance: 100          # 100% for guaranteed testing
      amount-min: 2
      amount-max: 3
```

### Step 2: Reload Configuration
```
/sq reload
```

### Step 3: Test Mob Kills
1. **Spawn test mobs:**
   ```
   /summon zombie ~ ~ ~
   ```

2. **Kill the zombie**

3. **Verify:**
   - Check console for debug messages
   - Quest papers should drop
   - Amount should be 2-3 papers
   - Papers should be unbound

### Step 4: Test Different Mob Types
Configure and test:
- Common mobs (ZOMBIE, SKELETON, CREEPER)
- Elite mobs (WITHER_SKELETON, BLAZE)
- Boss mobs (WITHER, ENDER_DRAGON)

---

## Testing Source Modes

### Manual Mode
```yaml
source-mode: "manual"
quests:
  - "quest_1"
  - "quest_2"
  - "quest_3"
```

**Expected:** Only configured quests appear

**Test:** Kill 10+ mobs, verify only quest_1, quest_2, or quest_3 appear

---

### Random Mode
```yaml
source-mode: "random"
```

**Prerequisite:** Ensure random-generator.yml is enabled and configured

**Expected:** New quests generated each time

**Test:** 
1. Kill 10+ mobs
2. Check `generated.yml` for new quest entries
3. Verify quest papers have randomized names/objectives

---

### Mixed Mode
```yaml
source-mode: "mixed"
```

**Expected:** 50/50 mix of manual and random quests

**Test:**
1. Collect 20+ quest papers
2. Verify mix of manual quest IDs and generated quest IDs
3. Check `generated.yml` for new entries

---

## Testing Quest Paper Binding

### Unbound Papers
1. **Generate loot (chest or mob)**
2. **Pick up the paper**
3. **Check lore** - Should NOT have player name initially
4. **Trade to another player**
5. **Other player uses it** - Should bind to them

### Locked Quest Papers
If your quests have `lock-to-player: true`:
1. Paper should bind immediately on first progress
2. Cannot be traded once bound
3. Check lore shows "Bound to: [PlayerName]"

---

## Testing World Filters

### Single World
```yaml
chest:
  worlds: ["world"]
mobs:
  worlds: ["world"]
```

**Test:**
1. In "world" - Should generate loot ✓
2. In "world_nether" - Should NOT generate loot ✗
3. In "world_the_end" - Should NOT generate loot ✗

### Multiple Worlds
```yaml
worlds: ["world", "world_nether"]
```

**Test:**
1. In "world" - Should work ✓
2. In "world_nether" - Should work ✓
3. In "world_the_end" - Should NOT work ✗

### All Worlds
```yaml
worlds: []
```

**Test:** Should work in ALL worlds ✓

---

## Testing Amount Ranges

```yaml
amount-min: 1
amount-max: 5
```

**Test:**
1. Generate 50+ loot instances
2. Track amount per drop
3. Verify:
   - Minimum never below 1
   - Maximum never above 5
   - Distribution is random across range

---

## Testing Global Limits

```yaml
max-per-event: 3
```

**Configure mob with high amount:**
```yaml
ZOMBIE:
  chance: 100
  amount-min: 10
  amount-max: 20
```

**Test:**
1. Kill zombie
2. Count dropped quest papers
3. Should never exceed 3 (max-per-event)

---

## Debug Console Output

With `/sq debug toggle` enabled, you'll see:

```
[QuestLoot] Generating 2 quest(s) from ZOMBIE at Location{world=world,x=100,y=64,z=200}
[QuestLoot] Added 2 quest paper(s) to ZOMBIE drops
[QuestLoot] Generated quest: quest_common_kill_12345
```

### What to Look For:
- ✓ "Generating X quest(s)" - Loot triggered
- ✓ "Added X quest paper(s)" - Successfully added
- ✗ "Skipping" messages - Checks failed (world, chance, etc.)
- ✗ "Failed to generate" - Generation errors

---

## Testing Checklist

### Initial Setup
- [ ] quest-loot.yml exists in plugins/SoapsQuest/
- [ ] Configuration loads without errors
- [ ] `/sq reload` works
- [ ] Debug mode enables with `/sq debug toggle`

### Chest Loot
- [ ] Chests spawn with quest papers (100% chance)
- [ ] Amount within configured range
- [ ] Only spawns in allowed worlds
- [ ] Manual mode uses configured quests
- [ ] Random mode generates new quests
- [ ] Mixed mode shows variety

### Mob Drops
- [ ] Mobs drop quest papers (100% chance)
- [ ] Amount within configured range
- [ ] Only drops in allowed worlds
- [ ] Per-mob-type configuration works
- [ ] Default chance works for unlisted mobs
- [ ] Boss mobs drop more papers

### Quest Papers
- [ ] Papers are unbound initially
- [ ] Papers bind when picked up (if unlocked)
- [ ] Papers bind on first progress (if locked)
- [ ] Papers have correct quest ID
- [ ] Lore displays correctly
- [ ] Multiple papers of same quest have unique UUIDs

### Integration
- [ ] Works with manual quests from quests.yml
- [ ] Works with random generator
- [ ] Generated quests saved to generated.yml
- [ ] Papers are completable
- [ ] Progress tracking works
- [ ] Rewards are granted

### Performance
- [ ] No lag when opening chests
- [ ] No lag when killing mobs
- [ ] Console not spammed (when debug off)
- [ ] Server runs smoothly with system enabled

---

## Common Issues & Solutions

### No Quest Papers Dropping

**Check:**
1. Is system enabled? `quest-loot.enabled: true`
2. Is chest/mob loot enabled?
3. Are you in an allowed world?
4. Did chance roll succeed? (Try 100% for testing)
5. Check console for error messages

**Solution:**
```
/sq debug toggle
```
Test again and watch console for clues.

---

### Papers Not Binding

**Check:**
1. Is quest lock-to-player enabled?
2. Did player make any progress?
3. Check paper NBT data

**Solution:**
Unlocked quests only bind when player makes first progress or completes it.

---

### Wrong Quests Generating

**Check:**
1. Source mode setting
2. Manual quest IDs exist
3. Random generator is enabled

**Solution:**
```yaml
source-mode: "manual"
quests:
  - "existing_quest_id"  # Must exist in quests.yml
```

---

### Papers Stackable

**Issue:** Multiple quest papers stacking together

**This Should Not Happen:** Each paper has unique UUID and timestamp

**If it happens:** Report as bug - papers should never stack

---

## Advanced Testing

### Load Testing
1. Configure high drop rates (100%)
2. Create mob farm
3. AFK for 1 hour
4. Verify:
   - No memory leaks
   - No console spam
   - All papers functional

### Multi-World Testing
1. Configure different settings per world
2. Test in each world
3. Verify world-specific configs apply

### Permission Testing
1. Configure `obey-plugin-restrictions: true`
2. Create quest with permission requirement
3. Test with player without permission
4. Should not drop for that player

---

## Recommended Test Sequence

### Quick Test (5 minutes)
1. Set chest chance: 100%
2. Set zombie chance: 100%
3. Enable debug mode
4. Find a dungeon chest - open it
5. Spawn and kill zombie
6. Pick up papers
7. Verify they work

### Full Test (30 minutes)
1. Test each source mode
2. Test 5+ different mob types
3. Test multiple worlds
4. Test amount ranges
5. Test max-per-event limit
6. Complete a dropped quest
7. Verify random generation
8. Test with another player

### Production Test (Before Launch)
1. Set realistic chances (5-10%)
2. Disable debug mode
3. Play normally for 1+ hour
4. Collect feedback
5. Adjust chances based on drop rate
6. Monitor server performance

---

## Performance Benchmarks

**Expected Performance:**
- Chest open: < 5ms overhead
- Mob death: < 3ms overhead
- No noticeable lag
- Minimal console output (debug off)

**If experiencing issues:**
- Lower chances
- Reduce amount-max
- Limit to specific worlds
- Check for other plugin conflicts

---

## Final Verification

Before considering testing complete:

✅ All features work as documented
✅ No console errors
✅ Papers are completable
✅ Integration with existing quests works
✅ Random generation works (if enabled)
✅ Performance is acceptable
✅ Configuration reloads work
✅ Debug mode helps troubleshooting

---

## Support & Troubleshooting

If issues persist:
1. Check `/sq debug toggle` output
2. Review server console for errors
3. Verify quest-loot.yml syntax
4. Test with minimal config first
5. Add complexity gradually

**Remember:** High chances (50-100%) make testing faster, but use realistic chances (5-15%) for production!
