package com.soaps.quest.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.managers.RandomGeneratorConfig.ObjectiveConfig;
import com.soaps.quest.objectives.ObjectiveRegistry;
import com.soaps.quest.utils.PlaceholderManager;

/**
 * Upgraded random quest generator with difficulty scaling, weighted objectives,
 * display templates, and MythicMobs integration.
 */
public class QuestGenerator {
    private final SoapsQuest plugin;
    private final RandomGeneratorConfig config;
    private final Random random;
    private final PlaceholderManager placeholderManager;
    
    public QuestGenerator(SoapsQuest plugin, RandomGeneratorConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.random = new Random();
        this.placeholderManager = new PlaceholderManager(plugin);
    }
    
    /**
     * Generates a random quest with random type and difficulty
     */
    public String generateQuest() {
        return generateQuest(null, null);
    }
    
    /**
     * Generates a random quest of a specific type
     */
    public String generateQuest(String type) {
        return generateQuest(type, null);
    }
    
    /**
     * Full quest generation with all parameters and retry logic
     * @param type Quest type (single/multi/sequence) or null for random
     * @param difficulty Difficulty name or null for random
     * @return Generated quest ID, or null if all attempts failed
     */
    public String generateQuest(String type, String difficulty) {
        int maxRetries = config.getMaxGenerationRetries();
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                String questId = attemptQuestGeneration(type, difficulty);
                
                if (questId != null) {
                    // Success!
                    if (attempt > 1) {
                        plugin.getLogger().info(String.format(
                            "Successfully generated quest '%s' on attempt %d/%d", 
                            questId, attempt, maxRetries
                        ));
                    }
                    return questId;
                }
                
                // Failed but no exception - log and retry
                if (attempt < maxRetries) {
                    plugin.getLogger().warning(String.format(
                        "Quest generation attempt %d/%d failed, retrying...", 
                        attempt, maxRetries
                    ));
                }
            } catch (Exception e) {
                // Exception during generation - log and retry
                if (attempt < maxRetries) {
                    plugin.getLogger().warning(String.format(
                        "Quest generation attempt %d/%d encountered an error, retrying... Error: %s", 
                        attempt, maxRetries, e.getMessage()
                    ));
                } else {
                    plugin.getLogger().severe(String.format(
                        "Quest generation failed after %d attempts. Last error: %s", 
                        maxRetries, e.getMessage()
                    ));
                    if (plugin.getConfig().getBoolean("debug", false)) {
                        for (StackTraceElement element : e.getStackTrace()) {
                            plugin.getLogger().severe(String.format("  at %s", element));
                        }
                    }
                }
            }
        }
        
        // All attempts failed
        plugin.getLogger().severe(String.format(
            "Failed to generate quest after %d attempts. Check your configuration.", 
            maxRetries
        ));
        return null;
    }
    
    /**
     * Internal method that performs a single quest generation attempt
     * @param type Quest type (single/multi/sequence) or null for random
     * @param difficulty Difficulty name or null for random
     * @return Generated quest ID, or null if generation failed
     */
    private String attemptQuestGeneration(String type, String difficulty) {
        // Select random type if not specified
        if (type == null) {
            List<String> allowedTypes = config.getAllowedTypes();
            if (allowedTypes.isEmpty()) return null;
            type = allowedTypes.get(random.nextInt(allowedTypes.size()));
        }
        
        // Select difficulty using DifficultyManager
        DifficultyManager.Difficulty diff = selectDifficulty(difficulty);
        if (diff == null) {
            diff = plugin.getDifficultyManager().getDifficultyOrDefault(config.getDefaultDifficulty());
        }
        
        // Select tier using TierManager
        TierManager.Tier tier = selectTier();
        if (tier == null) {
            tier = plugin.getTierManager().getTierOrDefault(config.getDefaultTier());
        }
        
        // Log tier and difficulty selection for debugging
        plugin.getLogger().fine(String.format("[Generator] Applied tier '%s' with color '%s' and difficulty '%s'", 
            tier.name, tier.color, diff.name));
        
        // Generate unique ID
        String questId = generateUniqueId(type, tier.name, diff.name, null);
        
        // Generate quest data
        Map<String, Object> questData = new LinkedHashMap<>();
        
        // Generate objectives
        List<Map<String, Object>> objectives;
        if (type.equals("single")) {
            Map<String, Object> objective = generateRandomObjective(diff);
            if (objective == null) return null;
            objectives = Collections.singletonList(objective);
        } else {
            int minObj = type.equals("multi") ? config.getMultiMinObjectives() : config.getSequenceMinObjectives();
            int maxObj = type.equals("multi") ? config.getMultiMaxObjectives() : config.getSequenceMaxObjectives();
            int numObjectives = random.nextInt(maxObj - minObj + 1) + minObj;
            objectives = generateMultipleObjectives(numObjectives, diff);
            if (objectives == null || objectives.isEmpty()) return null;
        }
        
        // Generate display name with placeholders
        String displayName = generateDisplayName(objectives.get(0), tier, diff);
        questData.put("display", displayName);
        
        // Generate quest lore
        List<String> lore = generateQuestLore(tier, diff, type);
        questData.put("lore", lore);
        
        // Set type and tier
        questData.put("type", type);
        questData.put("tier", tier.name.toUpperCase());
        
        // Select and set material
        String material = selectQuestPaperMaterial(tier);
        questData.put("material", material);
        
        // Add permission
        questData.put("permission", "soapsquests.quest." + questId);
        
        // Add objectives
        questData.put("objectives", objectives);
        
        // Sequential flag for sequence quests
        if (type.equals("sequence")) {
            questData.put("sequential", true);
        }
        
        // Generate rewards with tier-based ranges and difficulty scaling
        Map<String, Object> rewards = generateRewards(tier, diff);
        if (!rewards.isEmpty()) {
            questData.put("rewards", Collections.singletonList(rewards));
        }
        
        // Add milestones based on configuration
        List<Integer> milestones = generateMilestones(tier);
        if (milestones != null && !milestones.isEmpty()) {
            questData.put("milestones", milestones);
        }
        
        // Save quest
        if (config.isSaveGeneratedQuests()) {
            if (!saveQuest(questId, questData)) {
                return null;
            }
        } else {
            // Just reload to register in memory
            plugin.getQuestManager().loadQuests();
        }
        
        return questId;
    }
    
    /**
     * Selects a difficulty using DifficultyManager
     */
    private DifficultyManager.Difficulty selectDifficulty(String difficultyName) {
        DifficultyManager difficultyManager = plugin.getDifficultyManager();
        
        if (difficultyName != null) {
            return difficultyManager.getDifficulty(difficultyName);
        }
        
        // Filter by config allowed list
        List<String> allowedDifficulties = config.getAllowedDifficulties();
        
        // If no filter, use random from manager
        if (allowedDifficulties == null || allowedDifficulties.isEmpty()) {
            return difficultyManager.getRandomDifficulty();
        }
        
        // Filter and select weighted random from allowed list
        List<DifficultyManager.Difficulty> filtered = new ArrayList<>();
        int totalWeight = 0;
        
        for (String diffName : allowedDifficulties) {
            DifficultyManager.Difficulty diff = difficultyManager.getDifficulty(diffName);
            if (diff != null) {
                filtered.add(diff);
                totalWeight += diff.weight;
            }
        }
        
        if (totalWeight == 0 || filtered.isEmpty()) {
            return difficultyManager.getRandomDifficulty();
        }
        
        int roll = random.nextInt(totalWeight);
        int currentWeight = 0;
        
        for (DifficultyManager.Difficulty diff : filtered) {
            currentWeight += diff.weight;
            if (roll < currentWeight) {
                return diff;
            }
        }
        
        return filtered.get(0);
    }
    
    /**
     * Selects a tier using TierManager
     */
    private TierManager.Tier selectTier() {
        TierManager tierManager = plugin.getTierManager();
        
        // Filter by config allowed list
        List<String> allowedTiers = config.getAllowedTiers();
        
        // If no filter, use random from manager
        if (allowedTiers == null || allowedTiers.isEmpty()) {
            return tierManager.getRandomTier();
        }
        
        // Filter and select weighted random from allowed list
        List<TierManager.Tier> filtered = new ArrayList<>();
        int totalWeight = 0;
        
        for (String tierName : allowedTiers) {
            TierManager.Tier tier = tierManager.getTier(tierName);
            if (tier != null) {
                filtered.add(tier);
                totalWeight += tier.weight;
            }
        }
        
        if (totalWeight == 0 || filtered.isEmpty()) {
            return tierManager.getRandomTier();
        }
        
        int roll = random.nextInt(totalWeight);
        int currentWeight = 0;
        
        for (TierManager.Tier tier : filtered) {
            currentWeight += tier.weight;
            if (roll < currentWeight) {
                return tier;
            }
        }
        
        return filtered.get(0);
    }
    
    /**
     * Generates milestones based on configuration mode
     * @param tier Quest tier for tier-based mode
     * @return List of milestone percentages, or null if disabled
     */
    private List<Integer> generateMilestones(TierManager.Tier tier) {
        if (!config.isMilestonesEnabled()) {
            return null;
        }
        
        String mode = config.getMilestoneMode();
        if (mode == null || mode.equalsIgnoreCase("none")) {
            return null;
        }
        
        switch (mode.toLowerCase()) {
            case "default" -> {
                // Return default milestones
                return config.getFixedMilestones();
            }
                
            case "random" -> {
                // Pick random milestone set from pool
                List<List<Integer>> pool = config.getRandomMilestonePool();
                if (pool != null && !pool.isEmpty()) {
                    return pool.get(random.nextInt(pool.size()));
                }
                return null;
            }
                
            case "tier-based" -> {
                // Get milestones for this tier
                Map<String, List<Integer>> tierMilestones = config.getTierBasedMilestones();
                if (tierMilestones != null && tier != null) {
                    return tierMilestones.get(tier.name.toLowerCase());
                }
                return null;
            }
                
            default -> {
                plugin.getLogger().log(Level.WARNING, "Unknown milestone mode: {0}", mode);
                return null;
            }
        }
    }
    
    /**
     * Generates a random objective using weighted selection
     * NEW: Returns objective selected from named-objectives pool
     */
    private Map<String, Object> generateRandomObjective(DifficultyManager.Difficulty difficulty) {
        String objectiveName = selectWeightedObjectiveType();
        if (objectiveName == null) return null;
        
        return buildObjective(objectiveName, difficulty);
    }
    
    /**
     * Generates multiple objectives without duplicates
     * NEW: Works with named-objectives system - prevents same objective being used twice
     */
    private List<Map<String, Object>> generateMultipleObjectives(int count, DifficultyManager.Difficulty difficulty) {
        List<Map<String, Object>> objectives = new ArrayList<>();
        Set<String> usedObjectiveNames = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            String objectiveName = selectWeightedObjectiveType(usedObjectiveNames);
            if (objectiveName == null) break;
            
            Map<String, Object> objective = buildObjective(objectiveName, difficulty);
            if (objective != null) {
                objectives.add(objective);
                usedObjectiveNames.add(objectiveName);
            }
        }
        
        return objectives;
    }
    
    /**
     * Selects an objective type based on weights
     */
    private String selectWeightedObjectiveType() {
        return selectWeightedObjectiveType(new HashSet<>());
    }
    
    /**
     * Selects a named objective based on type weights, excluding used names
     * NEW: Supports named-objectives system - returns objective name, not type
     */
    private String selectWeightedObjectiveType(Set<String> excludedTypes) {
        int totalWeight = 0;
        Map<String, Integer> candidateObjectives = new HashMap<>();
        
        // Build list of all available objective names and their weights
        for (Map.Entry<String, ObjectiveConfig> entry : config.getObjectives().entrySet()) {
            String objectiveName = entry.getKey();
            ObjectiveConfig objConfig = entry.getValue();
            String type = objConfig.type;
            
            // Skip excluded objective names
            if (excludedTypes.contains(objectiveName)) continue;
            
            // Skip MythicMobs if not installed
            if (type.equalsIgnoreCase("kill_mythicmob") && !config.isMythicMobsInstalled()) continue;
            
            // Validate that objective type is registered
            if (!ObjectiveRegistry.isRegistered(type)) {
                plugin.getLogger().warning(String.format("[Generator] Skipped objective '%s' - type '%s' is not registered", objectiveName, type));
                continue;
            }
            
            // Get weight for this objective's type
            Integer weight = config.getObjectiveWeights().get(type);
            if (weight == null || weight <= 0) continue;
            
            candidateObjectives.put(objectiveName, weight);
            totalWeight += weight;
        }
        
        // Handle MythicMobs objectives separately (if installed)
        if (config.isMythicMobsInstalled() && !config.getMythicMobPool().isEmpty()) {
            Integer mythicWeight = config.getObjectiveWeights().get("kill_mythicmob");
            if (mythicWeight != null && mythicWeight > 0) {
                if (!excludedTypes.contains("kill_mythicmob")) {
                    candidateObjectives.put("kill_mythicmob", mythicWeight);
                    totalWeight += mythicWeight;
                }
            }
        }
        
        if (totalWeight == 0) {
            plugin.getLogger().warning("No valid objectives available for generation! Check your config.");
            return null;
        }
        
        // Weighted random selection
        int roll = random.nextInt(totalWeight);
        int currentWeight = 0;
        
        for (Map.Entry<String, Integer> entry : candidateObjectives.entrySet()) {
            currentWeight += entry.getValue();
            if (roll < currentWeight) {
                return entry.getKey();
            }
        }
        
        // Fallback
        return candidateObjectives.keySet().iterator().hasNext() 
            ? candidateObjectives.keySet().iterator().next() : null;
    }
    
    /**
     * Builds an objective map from a named objective
     * NEW: Works with named-objectives system and supports ANY keyword
     * @param objectiveName The internal name of the objective (e.g., "kill_hostile_mobs")
     */
    private Map<String, Object> buildObjective(String objectiveName, DifficultyManager.Difficulty difficulty) {
        Map<String, Object> objective = new LinkedHashMap<>();
        
        // Handle MythicMobs objective
        if (objectiveName.equalsIgnoreCase("kill_mythicmob")) {
            List<String> mobPool = config.getMythicMobPool();
            if (mobPool.isEmpty()) {
                plugin.getLogger().warning("MythicMobs objective selected but mob pool is empty!");
                return null;
            }
            
            objective.put("type", "kill_mythicmob");
            String mob = mobPool.get(random.nextInt(mobPool.size()));
            objective.put("mob", mob);
            
            int amount = random.nextInt(3) + 1; // 1-3 mythic mobs
            amount = (int) Math.ceil(amount * difficulty.objectiveMultiplier);
            objective.put("amount", Math.max(1, amount));
            
            return objective;
        }
        
        // Handle named objectives
        ObjectiveConfig objConfig = config.getObjectiveByName(objectiveName);
        if (objConfig == null) {
            plugin.getLogger().warning(String.format("Objective '%s' not found in config!", objectiveName));
            return null;
        }
        
        objective.put("type", objConfig.type);
        
        // Check if this objective type requires options (entity/block/item)
        if (requiresOptions(objConfig.type)) {
            // Handle ANY keyword
            if (objConfig.supportsAny) {
                // For ANY objectives, set the field to "ANY"
                // The objective system will accept any valid target
                String optionKey = getOptionKey(objConfig.type);
                objective.put(optionKey, "ANY");
            } else {
                // Select random option from the list
                if (objConfig.options.isEmpty()) {
                    plugin.getLogger().warning(String.format("Objective '%s' has no options configured!", objectiveName));
                    return null;
                }
                
                String option = objConfig.options.get(random.nextInt(objConfig.options.size()));
                String optionKey = getOptionKey(objConfig.type);
                objective.put(optionKey, option);
            }
        }
        // Else: objective doesn't need options (e.g., jump, move, gainlevel)
        
        // Generate amount with difficulty scaling
        int minAmount = objConfig.minAmount;
        int maxAmount = objConfig.maxAmount;
        
        // Check if amount-by-difficulty is configured for this objective
        if (objConfig.amountByDifficulty != null && !objConfig.amountByDifficulty.isEmpty()) {
            // Try to get amount range for current difficulty
            int[] difficultyRange = objConfig.amountByDifficulty.get(difficulty.name);
            if (difficultyRange != null && difficultyRange.length >= 2) {
                minAmount = difficultyRange[0];
                maxAmount = difficultyRange[1];
            }
        }
        
        int amount = random.nextInt(maxAmount - minAmount + 1) + minAmount;
        // Don't apply difficulty multiplier if amount-by-difficulty is used (already scaled)
        if (objConfig.amountByDifficulty == null) {
            amount = (int) Math.ceil(amount * difficulty.objectiveMultiplier);
        }
        
        // Use correct field name based on objective type
        String amountKey = getAmountKey(objConfig.type);
        objective.put(amountKey, Math.max(1, amount));
        
        return objective;
    }
    
    /**
     * Gets the option key for an objective type
     */
    private String getOptionKey(String type) {
        return switch (type.toLowerCase()) {
            case "kill", "breed", "tame", "shear" -> "entity";
            case "break", "place" -> "block";
            default -> "item";
        };
    }
    
    /**
     * Checks if an objective type requires options (entity/block/item).
     * Some objectives only need an amount and don't require options.
     */
    private boolean requiresOptions(String type) {
        return switch (type.toLowerCase()) {
            // Objectives that DON'T require options (only need amount/level)
            case "jump", "move", "sleep", "death", "reachlevel", "gainlevel", 
                 "level", "chat", "bowshoot", "projectile", "firework", 
                 "vehicle", "interact", "damage", "heal", "trade" -> false;
            // All other objectives require options (entity/block/item)
            default -> true;
        };
    }
    
    /**
     * Gets the amount field name for an objective type.
     * Most objectives use "amount", but some use specialized fields like "level".
     */
    private String getAmountKey(String type) {
        return switch (type.toLowerCase()) {
            case "reachlevel" -> "level";  // reachlevel uses "level" field
            default -> "amount";            // Default: use "amount" field (move also uses "amount")
        };
    }
    
    /**
     * Generates quest lore with support for random, static, and tier-based modes
     * Supports placeholders: <tier>, <tier_prefix>, <tier_color>, 
     *                        <difficulty>, <progress>, <objective>, <amount>, <type>
     */
    private List<String> generateQuestLore(TierManager.Tier tier, DifficultyManager.Difficulty difficulty, String questType) {
        List<String> lore = new ArrayList<>();
        
        // Check if lore_structure is defined in config
        List<RandomGeneratorConfig.LoreEntry> loreStructure = config.getLoreStructure();
        
        if (loreStructure.isEmpty()) {
            // Fallback: Use simple default lore if no lore_structure defined
            lore.add("&7Complete this quest for rewards!");
            lore.add(""); // Empty line for spacing
            lore.add("&7Progress: &f<progress>");
            lore.add("&7Objective: &f<objective>");
            lore.add("&7Amount: &f<amount>");
        } else {
            // Process enhanced lore structure
            for (RandomGeneratorConfig.LoreEntry entry : loreStructure) {
                switch (entry.mode) {
                    case FIXED -> {
                        // Add fixed line with placeholder replacement
                        lore.add(replaceLorePlaceholders(entry.fixedLine, tier, difficulty, questType));
                    }
                    case RANDOM -> {
                        // Pick one random line from options
                        if (entry.randomOptions != null && !entry.randomOptions.isEmpty()) {
                            String selectedLine = entry.randomOptions.get(random.nextInt(entry.randomOptions.size()));
                            lore.add(replaceLorePlaceholders(selectedLine, tier, difficulty, questType));
                        }
                    }
                    case TIER_BASED -> {
                        // Use tier-specific lines
                        if (entry.tierEntries != null && tier != null) {
                            List<String> tierLines = entry.tierEntries.get(tier.name.toLowerCase());
                            
                            // Fallback to common if specific tier not found
                            if (tierLines == null || tierLines.isEmpty()) {
                                tierLines = entry.tierEntries.get("common");
                            }
                            
                            // Add all tier-specific lines with placeholder replacement
                            if (tierLines != null) {
                                for (String line : tierLines) {
                                    lore.add(replaceLorePlaceholders(line, tier, difficulty, questType));
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return lore;
    }
    
    /**
     * Replaces metadata placeholders in lore lines
     */
    private String replaceLorePlaceholders(String line, TierManager.Tier tier, DifficultyManager.Difficulty difficulty, String questType) {
        if (line == null) return "";
        
        // Build placeholder context with tier and difficulty
        PlaceholderManager.PlaceholderContext context = new PlaceholderManager.PlaceholderContext()
                .tier(tier)
                .difficulty(difficulty);
        
        // Use PlaceholderManager for consistent placeholder replacement
        String result = placeholderManager.replacePlaceholders(line, context);
        
        // Quest type placeholder (special case for generator)
        if (questType != null) {
            result = result.replace("<type>", capitalize(questType));
        }
        
        // Note: <progress>, <objective>, <amount> are left as-is
        // They will be replaced by the quest paper system when displaying
        
        return result;
    }
    
    /**
     * Generates a display name using templates
     */
    private String generateDisplayName(Map<String, Object> firstObjective, TierManager.Tier tier, DifficultyManager.Difficulty difficulty) {
        String type = (String) firstObjective.get("type");
        
        // Try to get type-specific templates first
        List<String> templates = config.getDisplayTemplatesByType().get(type);
        
        // Fallback to generic templates if no type-specific ones exist
        if (templates == null || templates.isEmpty()) {
            templates = config.getGenericDisplayTemplates();
        }
        
        // Final fallback if still empty
        if (templates.isEmpty()) {
            templates = List.of("&7[Quest] &f<type> Challenge");
        }
        
        String template = templates.get(random.nextInt(templates.size()));
        
        // Build placeholder context with tier and difficulty
        PlaceholderManager.PlaceholderContext context = new PlaceholderManager.PlaceholderContext()
                .tier(tier)
                .difficulty(difficulty);
        
        // Use PlaceholderManager for consistent tier/difficulty replacement
        template = placeholderManager.replacePlaceholders(template, context);
        
        // Replace objective-specific placeholders
        template = template.replace("<type>", type != null ? capitalize(type) : "Unknown");
        
        // Replace amount placeholder
        if (firstObjective.containsKey("amount")) {
            template = template.replace("<amount>", String.valueOf(firstObjective.get("amount")));
        }
        
        // Replace entity/block/item placeholders
        if (firstObjective.containsKey("entity")) {
            String entityName = formatName((String) firstObjective.get("entity"));
            template = template.replace("<entity>", entityName);
            // Also replace <item> with entity name for consistency in some templates
            if (!firstObjective.containsKey("item")) {
                template = template.replace("<item>", entityName);
            }
        }
        if (firstObjective.containsKey("block")) {
            String blockName = formatName((String) firstObjective.get("block"));
            template = template.replace("<block>", blockName);
            // Also replace <item> with block name for consistency in some templates
            if (!firstObjective.containsKey("item")) {
                template = template.replace("<item>", blockName);
            }
        }
        if (firstObjective.containsKey("item")) {
            template = template.replace("<item>", formatName((String) firstObjective.get("item")));
        }
        if (firstObjective.containsKey("mob")) {
            String mobName = (String) firstObjective.get("mob");
            template = template.replace("<entity>", mobName);
            // Also replace <item> with mob name for consistency
            if (!firstObjective.containsKey("item")) {
                template = template.replace("<item>", mobName);
            }
        }
        
        // Add tier prefix
        return tier.prefix + " " + template;
    }
    
    /**
     * Generates rewards with tier-based ranges and difficulty scaling
     */
    private Map<String, Object> generateRewards(TierManager.Tier tier, DifficultyManager.Difficulty difficulty) {
        Map<String, Object> rewards = new LinkedHashMap<>();
        
        // XP reward - tier-based
        int[] xpRange = config.getXpByTier().get(tier.name);
        if (xpRange != null && xpRange.length >= 2) {
            int xp = random.nextInt(xpRange[1] - xpRange[0] + 1) + xpRange[0];
            xp = (int) Math.ceil(xp * difficulty.rewardMultiplier);
            rewards.put("xp", xp);
        }
        
        // Money reward - tier-based
        int[] moneyRange = config.getMoneyByTier().get(tier.name);
        if (moneyRange != null && moneyRange.length >= 2) {
            int money = random.nextInt(moneyRange[1] - moneyRange[0] + 1) + moneyRange[0];
            money = (int) Math.ceil(money * difficulty.rewardMultiplier);
            rewards.put("money", money);
        }
        
        // Item rewards - tier-based with selection mode
        List<RandomGeneratorConfig.TieredItemReward> itemPool = config.getTieredItemRewards();
        if (!itemPool.isEmpty()) {
            // Filter items by tier
            List<RandomGeneratorConfig.TieredItemReward> validItems = new ArrayList<>();
            for (RandomGeneratorConfig.TieredItemReward itemReward : itemPool) {
                if (itemReward.tiers.contains(tier.name)) {
                    // Check min difficulty if specified
                    if (itemReward.minDifficulty == null || 
                        isEqualOrHarderDifficulty(difficulty, itemReward.minDifficulty)) {
                        validItems.add(itemReward);
                    }
                }
            }
            
            if (!validItems.isEmpty()) {
                String selectionMode = config.getItemSelectionMode();
                int minItems = config.getMinItems();
                int maxItems = config.getMaxItems();
                int numItems = random.nextInt(maxItems - minItems + 1) + minItems;
                numItems = Math.min(numItems, validItems.size());
                
                List<RandomGeneratorConfig.TieredItemReward> selectedItems;
                if (null == selectionMode) {
                    // Random selection
                    selectedItems = new ArrayList<>(validItems);
                    Collections.shuffle(selectedItems);
                    selectedItems = selectedItems.subList(0, numItems);
                } else switch (selectionMode) {
                    case "weighted" -> selectedItems = selectWeightedItems(validItems, numItems);
                    case "all" -> selectedItems = validItems;
                    default -> {
                        // Random selection
                        selectedItems = new ArrayList<>(validItems);
                        Collections.shuffle(selectedItems);
                        selectedItems = selectedItems.subList(0, numItems);
                    }
                }
                
                List<Map<String, Object>> items = new ArrayList<>();
                for (RandomGeneratorConfig.TieredItemReward itemReward : selectedItems) {
                    int amount = random.nextInt(itemReward.maxAmount - itemReward.minAmount + 1) + itemReward.minAmount;
                    amount = (int) Math.ceil(amount * difficulty.rewardMultiplier);
                    
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("material", itemReward.material);
                    item.put("amount", Math.max(1, amount));
                    
                    // Add NBT-style custom properties if present (only for vanilla items)
                    // Custom items (MMOItems) already have their properties built-in
                    if (!itemReward.isCustomItem) {
                        if (itemReward.name != null) {
                            item.put("name", itemReward.name);
                        }
                        if (itemReward.lore != null && !itemReward.lore.isEmpty()) {
                            item.put("lore", itemReward.lore);
                        }
                        if (itemReward.enchantments != null && !itemReward.enchantments.isEmpty()) {
                            item.put("enchantments", itemReward.enchantments);
                        }
                        if (itemReward.flags != null && !itemReward.flags.isEmpty()) {
                            item.put("flags", itemReward.flags);
                        }
                        if (itemReward.unbreakable != null && itemReward.unbreakable) {
                            item.put("unbreakable", true);
                        }
                        if (itemReward.customModelData != null) {
                            item.put("custom-model-data", itemReward.customModelData);
                        }
                    }
                    
                    items.add(item);
                }
                
                if (!items.isEmpty()) {
                    rewards.put("items", items);
                }
            }
        }
        
        return rewards;
    }
    
    /**
     * Selects items using weighted random selection
     */
    private List<RandomGeneratorConfig.TieredItemReward> selectWeightedItems(
            List<RandomGeneratorConfig.TieredItemReward> items, int count) {
        List<RandomGeneratorConfig.TieredItemReward> selected = new ArrayList<>();
        List<RandomGeneratorConfig.TieredItemReward> pool = new ArrayList<>(items);
        
        for (int i = 0; i < count && !pool.isEmpty(); i++) {
            int totalWeight = pool.stream().mapToInt(item -> item.weight).sum();
            int randomWeight = random.nextInt(totalWeight);
            int currentWeight = 0;
            
            for (RandomGeneratorConfig.TieredItemReward item : pool) {
                currentWeight += item.weight;
                if (randomWeight < currentWeight) {
                    selected.add(item);
                    pool.remove(item);
                    break;
                }
            }
        }
        
        return selected;
    }
    
    /**
     * Checks if current difficulty is equal or harder than minimum difficulty
     */
    private boolean isEqualOrHarderDifficulty(DifficultyManager.Difficulty current, String minDifficultyName) {
        DifficultyManager.Difficulty minDiff = plugin.getDifficultyManager().getDifficulty(minDifficultyName);
        if (minDiff == null) return true; // If min difficulty doesn't exist, allow it
        
        // Compare by reward multiplier (higher = harder)
        return current.rewardMultiplier >= minDiff.rewardMultiplier;
    }
    
    /**
     * Generates a unique quest ID with optional format support
     * @param questType Type of quest (single/multi/sequence)
     * @param tierName Name of the tier (common/rare/epic/legendary)
     * @param difficultyName Name of the difficulty (easy/normal/hard)
     * @param playerName Name of the player (null if not applicable)
     */
    private String generateUniqueId(String questType, String tierName, String difficultyName, String playerName) {
        String id;
        File generatedFile = new File(plugin.getDataFolder(), config.getSaveLocation());
        FileConfiguration generatedConfig = YamlConfiguration.loadConfiguration(generatedFile);
        
        // Get the appropriate format for this quest type
        Map<String, String> formats = config.getInternalNameFormats();
        String format = formats.getOrDefault(questType, formats.get("default"));
        
        // Fallback to legacy format if no format configured
        if (format == null || format.isEmpty()) {
            String legacyFormat = config.getInternalNameFormat();
            if (legacyFormat != null && !legacyFormat.isEmpty()) {
                format = legacyFormat;
            } else {
                format = config.getIdPrefix() + "%id%";
            }
        }
        
        // Counter for sequence quests
        int counter = 1;
        if (questType != null && questType.equals("sequence")) {
            // Count existing sequence quests to generate unique counter
            for (String key : generatedConfig.getKeys(false)) {
                if (key.startsWith("seq_")) {
                    counter++;
                }
            }
        }
        
        int attempts = 0;
        int maxAttempts = 1000; // Prevent infinite loop
        
        do {
            // Replace placeholders
            long timestamp = System.currentTimeMillis();
            int randomNum = random.nextInt(10000);
            
            id = format
                .replace("<id>", String.valueOf(timestamp % 100000) + "_" + randomNum)
                .replace("<tier>", tierName != null ? tierName : "unknown")
                .replace("<diff>", difficultyName != null ? difficultyName : "unknown")
                .replace("<type>", questType != null ? questType : "unknown")
                .replace("<player>", playerName != null ? playerName : "none")
                .replace("<counter>", String.valueOf(counter))
                .replace("<objective>", "quest"); // Placeholder, could be enhanced
            
            counter++; // Increment for next iteration if ID already exists
            attempts++;
            
            if (attempts >= maxAttempts) {
                plugin.getLogger().log(Level.SEVERE, "[Generator] Failed to generate unique quest ID after {0} attempts!", maxAttempts);
                return "fallback_" + UUID.randomUUID().toString().substring(0, 8);
            }
        } while (generatedConfig.contains(id) || plugin.getQuestManager().questExists(id));
        
        return id;
    }
    
    /**
     * Saves a quest to file
     */
    private boolean saveQuest(String questId, Map<String, Object> questData) {
        try {
            File generatedFile = new File(plugin.getDataFolder(), config.getSaveLocation());
            
            if (!generatedFile.exists()) {
                generatedFile.createNewFile();
            }
            
            FileConfiguration generatedConfig = YamlConfiguration.loadConfiguration(generatedFile);
            
            // Save quest data at root level (not under "quests:" section)
            // This matches how generated.yml is loaded in QuestManager
            for (Map.Entry<String, Object> entry : questData.entrySet()) {
                generatedConfig.set(questId + "." + entry.getKey(), entry.getValue());
            }
            
            generatedConfig.save(generatedFile);
            
            // Reload quests to register the newly generated quest
            plugin.getQuestManager().loadQuests();
            
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe(() -> "Failed to save generated quest: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Capitalizes a string
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    /**
     * Formats a material/entity name for display
     */
    private String formatName(String name) {
        if (name == null || name.isEmpty()) return name;
        return Arrays.stream(name.split("_"))
            .map(this::capitalize)
            .reduce((a, b) -> a + " " + b)
            .orElse(name);
    }
    
    /**
     * Selects a quest paper material based on configuration
     * 
     * @param tier Quest tier
     * @return Material name as string
     */
    private String selectQuestPaperMaterial(TierManager.Tier tier) {
        String mode = config.getMaterialSelectionMode();
        if (mode == null) mode = "default";
        
        return switch (mode.toLowerCase()) {
            case "random" -> {
                // Select from weighted random pool
                List<RandomGeneratorConfig.WeightedMaterial> pool = config.getRandomMaterialPool();
                if (pool.isEmpty()) {
                    yield config.getDefaultMaterial();
                }
                
                int totalWeight = config.getTotalMaterialWeight();
                int randomValue = random.nextInt(totalWeight);
                int currentWeight = 0;
                
                for (RandomGeneratorConfig.WeightedMaterial wm : pool) {
                    currentWeight += wm.weight;
                    if (randomValue < currentWeight) {
                        yield wm.material;
                    }
                }
                
                // Fallback (should never reach here)
                yield pool.get(0).material;
            }
            case "tier-based", "tier_based" -> {
                // Select based on quest tier
                Map<String, List<String>> tierMaterials = config.getTierBasedMaterials();
                List<String> materials = tierMaterials.get(tier.name.toLowerCase());
                
                if (materials == null || materials.isEmpty()) {
                    yield config.getDefaultMaterial();
                }
                
                // Random selection from tier's material list
                yield materials.get(random.nextInt(materials.size()));
            }
            default -> // Default mode - always use default-material (PAPER)
                    config.getDefaultMaterial();
        };
    }
}
