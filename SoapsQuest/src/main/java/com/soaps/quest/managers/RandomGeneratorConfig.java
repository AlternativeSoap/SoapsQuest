package com.soaps.quest.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.soaps.quest.SoapsQuest;

/**
 * Manages the random quest generator configuration from random-generator.yml
 */
public class RandomGeneratorConfig {
    
    private final SoapsQuest plugin;
    private FileConfiguration config;
    
    // Core settings
    private boolean enabled;
    private boolean saveGeneratedQuests;
    private String saveLocation;
    private List<String> allowedTypes;
    private String defaultTier;
    private String defaultDifficulty;
    private String idPrefix;
    private String internalNameFormat;
    
    // Multi/Sequence settings
    private int multiMinObjectives;
    private int multiMaxObjectives;
    private int sequenceMinObjectives;
    private int sequenceMaxObjectives;
    
    // Objective weights
    private final Map<String, Integer> objectiveWeights;
    private int totalObjectiveWeight;
    
    // Difficulty and Tier pools (references config.yml)
    private List<String> allowedDifficulties;
    private List<String> allowedTiers;
    private boolean difficultyPoolEnabled;
    private boolean tierPoolEnabled;
    
    // Milestone configuration
    private boolean milestonesEnabled;
    private String milestoneMode;
    private List<Integer> fixedMilestones;
    private final List<List<Integer>> randomMilestonePool;
    private final Map<String, List<Integer>> tierBasedMilestones;
    
    // Display templates - type-specific
    private final Map<String, List<String>> displayTemplatesByType;
    private List<String> genericDisplayTemplates;
    private final List<LoreEntry> loreStructure;
    
    // Internal name formats
    private final Map<String, String> internalNameFormats;
    
    // Save strategy and format
    private String saveStrategy;
    private int saveIndent;
    private String saveSortBy;
    private boolean saveIncludeMetadata;
    
    // MythicMobs integration
    private List<String> mythicMobPool;
    private final boolean mythicMobsInstalled;
    
    // Generation retry settings
    private int maxGenerationRetries;
    
    // Objectives pool
    private final Map<String, ObjectiveConfig> objectives;
    
    // Tiers
    private final Map<String, TierConfig> tiers;
    private int totalTierWeight;
    
    // Reward pool - tier-based
    private final Map<String, int[]> xpByTier;
    private final Map<String, int[]> moneyByTier;
    private String itemSelectionMode;
    private int minItems;
    private int maxItems;
    private final List<TieredItemReward> tieredItemRewards;
    private int totalItemWeight;
    
    // Quest paper material settings
    private String materialSelectionMode;
    private String defaultMaterial;
    private final List<WeightedMaterial> randomMaterialPool;
    private final Map<String, List<String>> tierBasedMaterials;
    private int totalMaterialWeight;
    
    public RandomGeneratorConfig(SoapsQuest plugin) {
        this.plugin = plugin;
        this.objectives = new HashMap<>();
        this.tiers = new LinkedHashMap<>();
        this.tieredItemRewards = new ArrayList<>();
        this.objectiveWeights = new HashMap<>();
        this.displayTemplatesByType = new HashMap<>();
        this.genericDisplayTemplates = new ArrayList<>();
        this.loreStructure = new ArrayList<>();
        this.mythicMobPool = new ArrayList<>();
        this.allowedDifficulties = new ArrayList<>();
        this.allowedTiers = new ArrayList<>();
        this.internalNameFormats = new HashMap<>();
        this.xpByTier = new HashMap<>();
        this.randomMaterialPool = new ArrayList<>();
        this.tierBasedMaterials = new HashMap<>();
        this.moneyByTier = new HashMap<>();
        this.tierBasedMilestones = new HashMap<>();
        this.randomMilestonePool = new ArrayList<>();
        
        // Check if MythicMobs is installed
        this.mythicMobsInstalled = plugin.getServer().getPluginManager().getPlugin("MythicMobs") != null;
        
        init();
    }
    
    /**
     * Initialize configuration
     */
    private void init() {
        loadConfig();
        loadMainConfigSettings();
    }
    
    /**
     * Loads settings from the main config.yml file
     */
    private void loadMainConfigSettings() {
        // Load max generation retries from main config.yml
        maxGenerationRetries = plugin.getConfig().getInt("max-generation-retries", 5);
        
        if (maxGenerationRetries < 1) {
            plugin.getLogger().warning("max-generation-retries must be at least 1, defaulting to 5");
            maxGenerationRetries = 5;
        }
    }
    
    /**
     * Loads or creates the random-generator.yml file
     */
    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "random-generator.yml");
        
        // Create default config if it doesn't exist
        if (!configFile.exists()) {
            plugin.saveResource("random-generator.yml", false);
        }
        
        config = YamlConfiguration.loadConfiguration(configFile);
        parseConfig();
    }
    
    /**
     * Parses all configuration values
     */
    private void parseConfig() {
        ConfigurationSection root = config.getConfigurationSection("random-generator");
        if (root == null) {
            plugin.getLogger().warning("random-generator section not found in random-generator.yml!");
            enabled = false;
            return;
        }
        
        // Core settings
        enabled = root.getBoolean("enabled", true);
        saveGeneratedQuests = root.getBoolean("save-generated-quests", true);
        saveLocation = root.getString("save-location", "generated.yml");
        allowedTypes = root.getStringList("allowed-types");
        if (allowedTypes.isEmpty()) {
            allowedTypes = Arrays.asList("single", "multi", "sequence");
        }
        idPrefix = root.getString("id-prefix", "generated_");
        internalNameFormat = root.getString("internal-name-format", null);
        
        // Parse new pool-based difficulty/tier system
        parseDifficultyPool(root.getConfigurationSection("difficulty-pool"));
        parseTierPool(root.getConfigurationSection("tier-pool"));
        
        // Parse milestone configuration
        parseMilestones(root.getConfigurationSection("milestones"));
        
        // Multi/Sequence settings
        ConfigurationSection multiSection = root.getConfigurationSection("multi-objective");
        if (multiSection != null) {
            multiMinObjectives = multiSection.getInt("min-objectives", 2);
            multiMaxObjectives = multiSection.getInt("max-objectives", 4);
        }
        
        ConfigurationSection sequenceSection = root.getConfigurationSection("sequence-objective");
        if (sequenceSection != null) {
            sequenceMinObjectives = sequenceSection.getInt("min-objectives", 2);
            sequenceMaxObjectives = sequenceSection.getInt("max-objectives", 4);
        }
        
        // Parse new sections
        parseObjectiveWeights(root.getConfigurationSection("objective-weights"));
        parseDisplayTemplates(root);
        parseDescriptions(root);
        parseMythicMobs(root);
        parseInternalNameFormats(root.getConfigurationSection("internal-name-formats"));
        parseSaveFormat(root);
        
        // Parse objectives
        parseObjectives(root.getConfigurationSection("objectives"));
        
        // Parse tiers
        parseTiers(root.getConfigurationSection("tiers"));
        
        // Parse reward pool
        parseRewardPool(root.getConfigurationSection("reward-pool"));
        
        // Parse quest paper material
        parseQuestPaperMaterial(root.getConfigurationSection("quest-paper-material"));
    }
    
    /**
     * Parses the objectives pool configuration
     */
    private void parseObjectives(ConfigurationSection section) {
        objectives.clear();
        if (section == null) {
            plugin.getLogger().warning("No objectives section found in random-generator.yml - generator will have no objectives!");
            return;
        }
        
        for (String key : section.getKeys(false)) {
            try {
                ConfigurationSection objSection = section.getConfigurationSection(key);
                if (objSection == null) {
                    plugin.getLogger().warning(String.format("Objective '%s' is malformed (not a section) - skipping silently", key));
                    continue;
                }
                
                // Check enabled flag (default: true)
                boolean objEnabled = objSection.getBoolean("enabled", true);
                if (!objEnabled) {
                    plugin.getLogger().fine(String.format("Objective '%s' is disabled - skipping", key));
                    continue;
                }
                
                ObjectiveConfig objConfig = new ObjectiveConfig();
                objConfig.name = key; // Store the internal name
                objConfig.enabled = true;
                objConfig.options = new ArrayList<>();
                
                // NEW FORMAT: Parse "objective" field (type of quest)
                // Example: objective: kill
                if (objSection.contains("objective")) {
                    objConfig.type = objSection.getString("objective");
                } else {
                    // LEGACY FORMAT: Use key as type (backwards compatibility)
                    objConfig.type = key;
                }
                
                if (objConfig.type == null || objConfig.type.isEmpty()) {
                    plugin.getLogger().warning(String.format("Objective '%s' has no type - skipping", key));
                    continue;
                }
                
                // Parse entities/blocks/items list
                List<String> optionsList = null;
                if (objSection.contains("entities")) {
                    optionsList = objSection.getStringList("entities");
                } else if (objSection.contains("blocks")) {
                    optionsList = objSection.getStringList("blocks");
                } else if (objSection.contains("items")) {
                    optionsList = objSection.getStringList("items");
                }
                
                // Check for ANY keyword support
                objConfig.supportsAny = false;
                if (optionsList != null && !optionsList.isEmpty()) {
                    // Check if list contains "ANY"
                    if (optionsList.size() == 1 && optionsList.get(0).equalsIgnoreCase("ANY")) {
                        objConfig.supportsAny = true;
                        objConfig.options = new ArrayList<>(); // Empty list means ANY
                    } else {
                        objConfig.options = optionsList;
                    }
                }
                
                // Parse amount-by-difficulty (new format) or regular amount (legacy)
                ConfigurationSection amountByDiffSection = objSection.getConfigurationSection("amount-by-difficulty");
                if (amountByDiffSection != null) {
                    // New format: amount-by-difficulty map
                    objConfig.amountByDifficulty = new HashMap<>();
                    for (String difficulty : amountByDiffSection.getKeys(false)) {
                        List<?> amountList = amountByDiffSection.getList(difficulty);
                        if (amountList != null && amountList.size() >= 2) {
                            int min = ((Number) amountList.get(0)).intValue();
                            int max = ((Number) amountList.get(1)).intValue();
                            objConfig.amountByDifficulty.put(difficulty, new int[]{min, max});
                        }
                    }
                    
                    // Use first difficulty's range as default min/max for backwards compatibility
                    if (!objConfig.amountByDifficulty.isEmpty()) {
                        int[] firstRange = objConfig.amountByDifficulty.values().iterator().next();
                        objConfig.minAmount = firstRange[0];
                        objConfig.maxAmount = firstRange[1];
                    } else {
                        objConfig.minAmount = 1;
                        objConfig.maxAmount = 10;
                    }
                } else {
                    // Legacy format: single amount or range
                    objConfig.amountByDifficulty = null;
                    
                    // Check for amount field variants: "level" (reachlevel), "distance" (move), or "amount" (default)
                    Object amountObj = objSection.get("level");
                    if (amountObj == null) {
                        amountObj = objSection.get("distance");
                    }
                    if (amountObj == null) {
                        amountObj = objSection.get("amount");
                    }
                    
                    if (amountObj != null) {
                        switch (amountObj) {
                            case List<?> amountList when amountList.size() >= 2 -> {
                                objConfig.minAmount = ((Number) amountList.get(0)).intValue();
                                objConfig.maxAmount = ((Number) amountList.get(1)).intValue();
                            }
                            case List<?> amountList when amountList.size() == 1 -> {
                                // Single value - use as both min and max
                                int amount = ((Number) amountList.get(0)).intValue();
                                objConfig.minAmount = amount;
                                objConfig.maxAmount = amount;
                            }
                            case Number numberAmount -> {
                                // Fixed amount
                                int amount = numberAmount.intValue();
                                objConfig.minAmount = amount;
                                objConfig.maxAmount = amount;
                            }
                            default -> {
                                // Default range (invalid type)
                                objConfig.minAmount = 1;
                                objConfig.maxAmount = 10;
                            }
                        }
                    } else {
                        // Default range (null amount)
                        objConfig.minAmount = 1;
                        objConfig.maxAmount = 10;
                    }
                }
                
                // Validate amount range
                if (objConfig.minAmount > objConfig.maxAmount) {
                    plugin.getLogger().warning(String.format("Objective '%s' has invalid amount range [%d, %d] - swapping values",
                        key, objConfig.minAmount, objConfig.maxAmount));
                    int temp = objConfig.minAmount;
                    objConfig.minAmount = objConfig.maxAmount;
                    objConfig.maxAmount = temp;
                }
                
                // Store objective by its internal name (key)
                // Multiple objectives of the same type are now supported!
                objectives.put(key, objConfig);
                plugin.getLogger().fine(String.format("Loaded objective '%s' (type: %s, amount: [%d, %d], ANY: %b)",
                    key, objConfig.type, objConfig.minAmount, objConfig.maxAmount, objConfig.supportsAny));
                
            } catch (Exception e) {
                plugin.getLogger().warning(String.format("Error parsing objective '%s': %s - skipping silently", key, e.getMessage()));
            }
        }
        
        if (objectives.isEmpty()) {
            plugin.getLogger().warning("No valid objectives loaded! Generator will not work.");
        } else {
            plugin.getLogger().info(String.format("Loaded %d quest objectives for random generator", objectives.size()));
        }
    }
    
    /**
     * Parses the tier configuration
     */
    private void parseTiers(ConfigurationSection section) {
        tiers.clear();
        totalTierWeight = 0;
        if (section == null) return;
        
        for (String key : section.getKeys(false)) {
            ConfigurationSection tierSection = section.getConfigurationSection(key);
            if (tierSection == null) continue;
            
            TierConfig tierConfig = new TierConfig();
            tierConfig.name = key;
            tierConfig.color = tierSection.getString("color", "&7");
            tierConfig.weight = tierSection.getInt("weight", 1);
            
            List<?> milestonesList = tierSection.getList("milestones");
            if (milestonesList != null) {
                tierConfig.milestones = new ArrayList<>();
                for (Object obj : milestonesList) {
                    tierConfig.milestones.add(((Number) obj).intValue());
                }
            }
            
            tiers.put(key, tierConfig);
            totalTierWeight += tierConfig.weight;
        }
    }
    
    /**
     * Parses the reward pool configuration - supports tier-based rewards
     */
    private void parseRewardPool(ConfigurationSection section) {
        tieredItemRewards.clear();
        xpByTier.clear();
        moneyByTier.clear();
        totalItemWeight = 0;
        
        if (section == null) return;
        
        // Parse tier-based XP rewards
        ConfigurationSection xpSection = section.getConfigurationSection("xp");
        if (xpSection != null) {
            for (String tier : xpSection.getKeys(false)) {
                List<?> xpList = xpSection.getList(tier);
                if (xpList != null && xpList.size() >= 2) {
                    xpByTier.put(tier, new int[]{
                        ((Number) xpList.get(0)).intValue(),
                        ((Number) xpList.get(1)).intValue()
                    });
                }
            }
        }
        
        // Parse tier-based money rewards
        ConfigurationSection moneySection = section.getConfigurationSection("money");
        if (moneySection != null) {
            for (String tier : moneySection.getKeys(false)) {
                List<?> moneyList = moneySection.getList(tier);
                if (moneyList != null && moneyList.size() >= 2) {
                    moneyByTier.put(tier, new int[]{
                        ((Number) moneyList.get(0)).intValue(),
                        ((Number) moneyList.get(1)).intValue()
                    });
                }
            }
        }
        
        // Parse item selection settings
        ConfigurationSection itemsSection = section.getConfigurationSection("items");
        if (itemsSection != null) {
            itemSelectionMode = itemsSection.getString("selection-mode", "weighted");
            minItems = itemsSection.getInt("min-items", 1);
            maxItems = itemsSection.getInt("max-items", 3);
            
            // Parse tiered items
            @SuppressWarnings("unchecked")
            List<Map<?, ?>> itemsList = (List<Map<?, ?>>) itemsSection.getList("pool");
            if (itemsList != null) {
                for (Map<?, ?> itemMap : itemsList) {
                    TieredItemReward reward = new TieredItemReward();
                    reward.material = (String) itemMap.get("material");
                    
                    // Detect if this is a custom item (MMOItems)
                    if (reward.material != null) {
                        String materialLower = reward.material.toLowerCase();
                        reward.isCustomItem = materialLower.startsWith("mmoitem:") || 
                                             materialLower.startsWith("mmoitems:");
                    }
                    
                    // Parse tiers list
                    Object tiersObj = itemMap.get("tiers");
                    if (tiersObj instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<String> tiersList = (List<String>) tiersObj;
                        reward.tiers = tiersList;
                    }
                    
                    reward.minDifficulty = (String) itemMap.get("min-difficulty");
                    reward.weight = itemMap.containsKey("weight") ? 
                        ((Number) itemMap.get("weight")).intValue() : 1;
                    
                    // Parse amount - can be either a list [min, max] or a single number
                    Object amountObj = itemMap.get("amount");
                    switch (amountObj) {
                        case List<?> amountList when amountList.size() >= 2 -> {
                            reward.minAmount = ((Number) amountList.get(0)).intValue();
                            reward.maxAmount = ((Number) amountList.get(1)).intValue();
                        }
                        case List<?> amountList when amountList.size() == 1 -> {
                            int amount = ((Number) amountList.get(0)).intValue();
                            reward.minAmount = amount;
                            reward.maxAmount = amount;
                        }
                        case Number amountNum -> {
                            int amount = amountNum.intValue();
                            reward.minAmount = amount;
                            reward.maxAmount = amount;
                        }
                        case null, default -> {
                            // No amount specified or invalid type
                        }
                    }
                    
                    // Parse custom item properties (optional)
                    reward.name = (String) itemMap.get("name");
                    
                    Object loreObj = itemMap.get("lore");
                    if (loreObj instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<String> loreList = (List<String>) loreObj;
                        reward.lore = loreList;
                    }
                    
                    Object enchantsObj = itemMap.get("enchantments");
                    if (enchantsObj instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<String> enchantsList = (List<String>) enchantsObj;
                        reward.enchantments = enchantsList;
                    }
                    
                    Object flagsObj = itemMap.get("flags");
                    if (flagsObj instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<String> flagsList = (List<String>) flagsObj;
                        reward.flags = flagsList;
                    }
                    
                    Object unbreakableObj = itemMap.get("unbreakable");
                    if (unbreakableObj instanceof Boolean unbreakableBool) {
                        reward.unbreakable = unbreakableBool;
                    }
                    
                    // Parse custom-model-data (for resource packs)
                    Object customModelDataObj = itemMap.get("custom-model-data");
                    if (customModelDataObj instanceof Number customModelNum) {
                        reward.customModelData = customModelNum.intValue();
                    }
                    
                    if (reward.material != null && reward.tiers != null && !reward.tiers.isEmpty()) {
                        tieredItemRewards.add(reward);
                        totalItemWeight += reward.weight;
                    }
                }
            }
        }
    }
    
    /**
     * Parses quest paper material configuration
     */
    private void parseQuestPaperMaterial(ConfigurationSection section) {
        randomMaterialPool.clear();
        tierBasedMaterials.clear();
        totalMaterialWeight = 0;
        
        if (section == null) {
            // Default settings
            materialSelectionMode = "fixed";
            defaultMaterial = "PAPER";
            return;
        }
        
        materialSelectionMode = section.getString("selection-mode", "fixed");
        defaultMaterial = section.getString("default-material", "PAPER");
        
        // Parse random pool
        if (section.contains("random-pool")) {
            @SuppressWarnings("unchecked")
            List<Map<?, ?>> poolList = (List<Map<?, ?>>) section.getList("random-pool");
            if (poolList != null) {
                for (Map<?, ?> entry : poolList) {
                    WeightedMaterial wm = new WeightedMaterial();
                    wm.material = (String) entry.get("material");
                    wm.weight = entry.containsKey("weight") ? 
                        ((Number) entry.get("weight")).intValue() : 1;
                    
                    randomMaterialPool.add(wm);
                    totalMaterialWeight += wm.weight;
                }
            }
        }
        
        // Parse tier-based materials
        if (section.contains("tier-based")) {
            ConfigurationSection tierSection = section.getConfigurationSection("tier-based");
            if (tierSection != null) {
                for (String tier : tierSection.getKeys(false)) {
                    List<String> materials = tierSection.getStringList(tier);
                    tierBasedMaterials.put(tier, materials);
                }
            }
        }
    }
    
    /**
     * Parses objective weights
     */
    private void parseObjectiveWeights(ConfigurationSection section) {
        objectiveWeights.clear();
        totalObjectiveWeight = 0;
        if (section == null) return;
        
        for (String key : section.getKeys(false)) {
            int weight = section.getInt(key, 1);
            
            // Skip MythicMobs objectives if plugin not installed
            if (key.equalsIgnoreCase("kill_mythicmob") && !mythicMobsInstalled) {
                continue;
            }
            
            objectiveWeights.put(key, weight);
            totalObjectiveWeight += weight;
        }
    }
    
    /**
     * Parses difficulty pool that references config.yml
     */
    private void parseDifficultyPool(ConfigurationSection section) {
        if (section == null) {
            difficultyPoolEnabled = false;
            defaultDifficulty = "normal";
            allowedDifficulties = null;
            return;
        }
        
        difficultyPoolEnabled = section.getBoolean("enabled", true);
        List<String> configuredList = section.getStringList("allowed");
        defaultDifficulty = section.getString("default", "normal");
        
        // FIXED: Only set to null if empty, otherwise use the configured list
        if (configuredList.isEmpty()) {
            // If empty, allow all from config.yml
            allowedDifficulties = null;
        } else {
            // Use the configured allowed list
            allowedDifficulties = configuredList;
        }
    }
    
    /**
     * Parses tier pool that references config.yml
     */
    private void parseTierPool(ConfigurationSection section) {
        if (section == null) {
            tierPoolEnabled = false;
            defaultTier = "common";
            allowedTiers = null;
            return;
        }
        
        tierPoolEnabled = section.getBoolean("enabled", true);
        List<String> configuredList = section.getStringList("allowed");
        defaultTier = section.getString("default", "common");
        
        // FIXED: Only set to null if empty, otherwise use the configured list
        if (configuredList.isEmpty()) {
            // If empty, allow all from config.yml
            allowedTiers = null;
        } else {
            // Use the configured allowed list
            allowedTiers = configuredList;
        }
    }
    
    /**
     * Parses milestone configuration
     */
    private void parseMilestones(ConfigurationSection section) {
        tierBasedMilestones.clear();
        randomMilestonePool.clear();
        
        if (section == null) {
            milestonesEnabled = false;
            milestoneMode = "none";
            return;
        }
        
        milestonesEnabled = section.getBoolean("enabled", true);
        milestoneMode = section.getString("mode", "tier-based");
        
        // Parse default milestones
        fixedMilestones = new ArrayList<>();
        List<?> defaultList = section.getList("default");
        if (defaultList != null) {
            for (Object obj : defaultList) {
                if (obj instanceof Number number) {
                    fixedMilestones.add(number.intValue());
                }
            }
        }
        
        // Parse random pool
        @SuppressWarnings("unchecked")
        List<List<?>> randomPool = (List<List<?>>) section.getList("random-pool");
        if (randomPool != null) {
            for (List<?> entry : randomPool) {
                List<Integer> milestones = new ArrayList<>();
                for (Object obj : entry) {
                    if (obj instanceof Number number) {
                        milestones.add(number.intValue());
                    }
                }
                if (!milestones.isEmpty()) {
                    randomMilestonePool.add(milestones);
                }
            }
        }
        
        // Parse tier-based milestones
        ConfigurationSection tierSection = section.getConfigurationSection("tier-based");
        if (tierSection != null) {
            for (String tier : tierSection.getKeys(false)) {
                List<Integer> milestones = new ArrayList<>();
                List<?> milestoneList = tierSection.getList(tier);
                if (milestoneList != null) {
                    for (Object obj : milestoneList) {
                        if (obj instanceof Number number) {
                            milestones.add(number.intValue());
                        }
                    }
                }
                if (!milestones.isEmpty()) {
                    tierBasedMilestones.put(tier, milestones);
                }
            }
        }
    }
    
    /**
     * Parses display templates (quest titles) - supports type-specific templates
     */
    private void parseDisplayTemplates(ConfigurationSection root) {
        displayTemplatesByType.clear();
        genericDisplayTemplates.clear();
        
        // Check if we have the new type-specific format
        ConfigurationSection templatesSection = root.getConfigurationSection("display-templates");
        if (templatesSection != null) {
            for (String key : templatesSection.getKeys(false)) {
                List<String> templates = templatesSection.getStringList(key);
                if (!templates.isEmpty()) {
                    displayTemplatesByType.put(key, templates);
                }
            }
            
            // Extract generic templates as fallback
            if (displayTemplatesByType.containsKey("generic")) {
                genericDisplayTemplates = displayTemplatesByType.get("generic");
            }
        }
        
        // Fallback: if no templates found, add defaults
        if (displayTemplatesByType.isEmpty() && genericDisplayTemplates.isEmpty()) {
            genericDisplayTemplates.add("&7[Quest] &f<type> Challenge");
            genericDisplayTemplates.add("&7Guild Contract");
        }
    }
    
    /**
     * Parses descriptions (legacy format) - kept for backwards compatibility
     */
    private void parseDescriptions(ConfigurationSection root) {
        // Legacy descriptions are no longer used, but we parse lore_structure
        parseLoreStructure(root);
    }
    
    /**
     * Parses lore structure with enhanced support for modes and tier-based sections
     * New Format:
     * lore-structure:
     *   section-name:
     *     mode: "random" | "static" | "tier-based" | "all"
     *     entries: [list of strings]
     *     common/rare/epic/legendary: [tier-specific entries]
     * 
     * Legacy Format (still supported):
     * lore_structure:
     *   - "&7First line always shown"
     *   - random_group:
     *       - "&7Option A"
     */
    private void parseLoreStructure(ConfigurationSection root) {
        loreStructure.clear();
        
        // Try new format first: lore-structure
        ConfigurationSection loreSection = root.getConfigurationSection("lore-structure");
        if (loreSection != null) {
            parseNewLoreStructure(loreSection);
            return;
        }
        
        // Fallback to legacy format: lore_structure (list format)
        if (!root.contains("lore_structure")) {
            return;
        }
        
        List<?> loreList = root.getList("lore_structure");
        if (loreList == null || loreList.isEmpty()) {
            return;
        }
        
        for (Object obj : loreList) {
            if (obj instanceof String fixedLine) {
                // Fixed line
                loreStructure.add(LoreEntry.fixed(fixedLine));
            } else if (obj instanceof Map) {
                // Could be random_group
                @SuppressWarnings("unchecked")
                Map<String, ?> map = (Map<String, ?>) obj;
                if (map.containsKey("random_group")) {
                    Object groupObj = map.get("random_group");
                    if (groupObj instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<String> options = (List<String>) groupObj;
                        if (!options.isEmpty()) {
                            loreStructure.add(LoreEntry.randomGroup(options));
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Parses the new enhanced lore-structure format
     */
    private void parseNewLoreStructure(ConfigurationSection loreSection) {
        // Get all section names in order
        for (String sectionName : loreSection.getKeys(false)) {
            ConfigurationSection section = loreSection.getConfigurationSection(sectionName);
            if (section == null) continue;
            
            String mode = section.getString("mode", "random");
            if (mode == null) mode = "random"; // Safety check
            List<String> entries = section.getStringList("entries");
            
            switch (mode.toLowerCase()) {
                case "static" -> {
                    // Add all entries as-is
                    for (String entry : entries) {
                        loreStructure.add(LoreEntry.fixed(entry));
                    }
                }
                    
                case "random" -> {
                    // Add as random group (pick one)
                    if (!entries.isEmpty()) {
                        loreStructure.add(LoreEntry.randomGroup(entries));
                    }
                }
                    
                case "all" -> {
                    // Add all entries (same as static but clearer intent)
                    for (String entry : entries) {
                        loreStructure.add(LoreEntry.fixed(entry));
                    }
                }
                    
                case "tier-based" -> {
                    // Tier-specific entries
                    Map<String, List<String>> tierEntries = new HashMap<>();
                    for (String tier : new String[]{"common", "rare", "epic", "legendary"}) {
                        List<String> tierLines = section.getStringList(tier);
                        if (!tierLines.isEmpty()) {
                            tierEntries.put(tier, tierLines);
                        }
                    }
                    if (!tierEntries.isEmpty()) {
                        loreStructure.add(LoreEntry.tierBased(tierEntries));
                    }
                }
                    
                default -> plugin.getLogger().warning(String.format(
                        "Unknown lore mode '%s' in section '%s' - skipping", mode, sectionName));
            }
        }
    }
    
    /**
     * Parses MythicMobs pool
     */
    private void parseMythicMobs(ConfigurationSection root) {
        if (!mythicMobsInstalled) {
            mythicMobPool.clear();
            return;
        }
        
        mythicMobPool = root.getStringList("mythicmob-pool");
        if (mythicMobPool.isEmpty()) {
            mythicMobPool.add("SkeletalKnight");
        }
    }
    
    /**
     * Parses internal name formats for quest ID generation
     */
    private void parseInternalNameFormats(ConfigurationSection section) {
        internalNameFormats.clear();
        
        if (section == null) {
            // Set defaults
            internalNameFormats.put("single", "quest_<tier>_<objective>_<id>");
            internalNameFormats.put("multi", "multi_<diff>_<tier>_<id>");
            internalNameFormats.put("sequence", "seq_<tier>_<counter>");
            internalNameFormats.put("default", "generated_<id>");
            return;
        }
        
        // Parse configured formats
        for (String key : section.getKeys(false)) {
            String format = section.getString(key);
            if (format != null && !format.isEmpty()) {
                internalNameFormats.put(key, format);
            }
        }
        
        // Ensure default format exists
        if (!internalNameFormats.containsKey("default")) {
            internalNameFormats.put("default", "generated_%id%");
        }
    }
    
    /**
     * Parses save strategy and format options
     */
    private void parseSaveFormat(ConfigurationSection root) {
        // Parse save-strategy
        saveStrategy = root.getString("save-strategy", "single");
        
        // Parse save-format section
        ConfigurationSection formatSection = root.getConfigurationSection("save-format");
        if (formatSection != null) {
            saveIndent = formatSection.getInt("indent", 2);
            saveSortBy = formatSection.getString("sort-by", "timestamp");
            saveIncludeMetadata = formatSection.getBoolean("include-metadata", true);
        } else {
            // Defaults
            saveIndent = 2;
            saveSortBy = "timestamp";
            saveIncludeMetadata = true;
        }
    }
    
    // Getters
    public boolean isEnabled() { return enabled; }
    public boolean isSaveGeneratedQuests() { return saveGeneratedQuests; }
    public String getSaveLocation() { return saveLocation; }
    public List<String> getAllowedTypes() { return allowedTypes; }
    public String getDefaultTier() { return defaultTier; }
    public String getDefaultDifficulty() { return defaultDifficulty; }
    public String getIdPrefix() { return idPrefix; }
    public String getInternalNameFormat() { return internalNameFormat; }
    public int getMultiMinObjectives() { return multiMinObjectives; }
    public int getMultiMaxObjectives() { return multiMaxObjectives; }
    public int getSequenceMinObjectives() { return sequenceMinObjectives; }
    public int getSequenceMaxObjectives() { return sequenceMaxObjectives; }
    public Map<String, Integer> getObjectiveWeights() { return objectiveWeights; }
    public int getTotalObjectiveWeight() { return totalObjectiveWeight; }
    public List<String> getAllowedDifficulties() { return allowedDifficulties; }
    public List<String> getAllowedTiers() { return allowedTiers; }
    public boolean isDifficultyPoolEnabled() { return difficultyPoolEnabled; }
    public boolean isTierPoolEnabled() { return tierPoolEnabled; }
    public Map<String, List<String>> getDisplayTemplatesByType() { return displayTemplatesByType; }
    public List<String> getGenericDisplayTemplates() { return genericDisplayTemplates; }
    public Map<String, String> getInternalNameFormats() { return internalNameFormats; }
    public List<LoreEntry> getLoreStructure() { return loreStructure; }
    public List<String> getMythicMobPool() { return mythicMobPool; }
    public boolean isMythicMobsInstalled() { return mythicMobsInstalled; }
    public Map<String, ObjectiveConfig> getObjectives() { return objectives; }
    public Map<String, TierConfig> getTiers() { return tiers; }
    public int getTotalTierWeight() { return totalTierWeight; }
    public Map<String, int[]> getXpByTier() { return xpByTier; }
    public Map<String, int[]> getMoneyByTier() { return moneyByTier; }
    public String getItemSelectionMode() { return itemSelectionMode; }
    public int getMinItems() { return minItems; }
    public int getMaxItems() { return maxItems; }
    public List<TieredItemReward> getTieredItemRewards() { return tieredItemRewards; }
    public int getTotalItemWeight() { return totalItemWeight; }
    public String getSaveStrategy() { return saveStrategy; }
    public int getSaveIndent() { return saveIndent; }
    public String getSaveSortBy() { return saveSortBy; }
    public boolean isSaveIncludeMetadata() { return saveIncludeMetadata; }
    public int getMaxGenerationRetries() { return maxGenerationRetries; }
    public String getMaterialSelectionMode() { return materialSelectionMode; }
    public String getDefaultMaterial() { return defaultMaterial; }
    public List<WeightedMaterial> getRandomMaterialPool() { return randomMaterialPool; }
    public Map<String, List<String>> getTierBasedMaterials() { return tierBasedMaterials; }
    public int getTotalMaterialWeight() { return totalMaterialWeight; }
    public boolean isMilestonesEnabled() { return milestonesEnabled; }
    public String getMilestoneMode() { return milestoneMode; }
    public List<Integer> getFixedMilestones() { return fixedMilestones; }
    public List<List<Integer>> getRandomMilestonePool() { return randomMilestonePool; }
    public Map<String, List<Integer>> getTierBasedMilestones() { return tierBasedMilestones; }
    
    /**
     * Get all objectives of a specific type.
     * Supports the new named-objectives system where multiple objectives can have the same type.
     * 
     * @param type The objective type (e.g., "kill", "break", "collect")
     * @return List of ObjectiveConfig entries matching the type (never null, may be empty)
     */
    public List<ObjectiveConfig> getObjectivesByType(String type) {
        List<ObjectiveConfig> result = new ArrayList<>();
        for (ObjectiveConfig objConfig : objectives.values()) {
            if (objConfig.type.equalsIgnoreCase(type)) {
                result.add(objConfig);
            }
        }
        return result;
    }
    
    /**
     * Get a specific objective by its internal name.
     * 
     * @param name The internal name/key of the objective (e.g., "kill_hostile_mobs")
     * @return The ObjectiveConfig, or null if not found
     */
    public ObjectiveConfig getObjectiveByName(String name) {
        return objectives.get(name);
    }
    
    /**
     * Configuration for an objective type
     */
    public static class ObjectiveConfig {
        public String name; // Internal name/key of the objective (e.g., "kill_hostile_mobs")
        public String type; // Type of objective (e.g., "kill", "break", "collect")
        public boolean enabled;
        public List<String> options; // entities, blocks, or items (can contain "ANY")
        public int minAmount;
        public int maxAmount;
        public boolean supportsAny; // True if this objective can use ANY keyword
        public Map<String, int[]> amountByDifficulty; // Optional difficulty-scaled amounts
    }
    
    /**
     * Configuration for a tier
     */
    public static class TierConfig {
        public String name;
        public String color;
        public int weight;
        public List<Integer> milestones;
    }
    
    /**
     * Configuration for an item reward
     */
    public static class ItemReward {
        public String material;
        public int minAmount;
        public int maxAmount;
    }
    
    /**
     * Configuration for a tiered item reward with restrictions
     */
    public static class TieredItemReward {
        public String material;
        public int minAmount;
        public int maxAmount;
        public List<String> tiers; // Which tiers this item can appear in
        public String minDifficulty; // Optional minimum difficulty
        public int weight; // Weight for random selection
        public boolean isCustomItem; // True if this is a custom item (MMOItems)
        
        // NBT-style custom properties (optional, for vanilla items)
        public String name; // Custom display name (supports & color codes)
        public List<String> lore; // Custom lore lines (supports & color codes)
        public List<String> enchantments; // Enchantments (format: "ENCHANT_ID:level")
        public List<String> flags; // Item flags (HIDE_ENCHANTS, HIDE_UNBREAKABLE, etc.)
        public Boolean unbreakable; // Unbreakable flag
        public Integer customModelData; // Custom model data for resource packs
    }
    
    /**
     * Represents a lore entry with support for multiple modes:
     * - Fixed: Single line that always appears
     * - Random Group: Pick one random line from options
     * - Tier-Based: Different lines for different quest tiers
     */
    public static class LoreEntry {
        public enum Mode {
            FIXED,        // Single fixed line
            RANDOM,       // Pick one from options
            TIER_BASED    // Different lines per tier
        }
        
        public Mode mode;
        public String fixedLine; // Used when mode = FIXED
        public List<String> randomOptions; // Used when mode = RANDOM
        public Map<String, List<String>> tierEntries; // Used when mode = TIER_BASED
        
        public static LoreEntry fixed(String line) {
            LoreEntry entry = new LoreEntry();
            entry.mode = Mode.FIXED;
            entry.fixedLine = line;
            return entry;
        }
        
        public static LoreEntry randomGroup(List<String> options) {
            LoreEntry entry = new LoreEntry();
            entry.mode = Mode.RANDOM;
            entry.randomOptions = options;
            return entry;
        }
        
        public static LoreEntry tierBased(Map<String, List<String>> tierEntries) {
            LoreEntry entry = new LoreEntry();
            entry.mode = Mode.TIER_BASED;
            entry.tierEntries = tierEntries;
            return entry;
        }
        
        // Legacy support
        public boolean isRandomGroup() {
            return mode == Mode.RANDOM;
        }
    }
    
    /**
     * Represents a weighted material for random selection
     */
    public static class WeightedMaterial {
        public String material;
        public int weight;
    }
}
