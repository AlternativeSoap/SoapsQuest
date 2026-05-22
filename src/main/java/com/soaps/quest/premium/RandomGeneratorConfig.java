/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.bukkit.MythicBukkit
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package com.soaps.quest.premium;

import com.soaps.quest.SoapsQuest;
import io.lumine.mythic.bukkit.MythicBukkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class RandomGeneratorConfig {
    private final SoapsQuest plugin;
    private FileConfiguration config;
    private boolean enabled;
    private boolean saveGeneratedQuests;
    private boolean temporaryGeneratedQuests;
    private String saveLocation;
    private List<String> allowedTypes;
    private String defaultTier;
    private String defaultDifficulty;
    private String idPrefix;
    private String internalNameFormat;
    private int multiMinObjectives;
    private int multiMaxObjectives;
    private int sequenceMinObjectives;
    private int sequenceMaxObjectives;
    private final Map<String, Integer> objectiveWeights;
    private int totalObjectiveWeight;
    private List<String> allowedDifficulties;
    private List<String> allowedTiers;
    private boolean difficultyPoolEnabled;
    private boolean tierPoolEnabled;
    private boolean milestonesEnabled;
    private String milestoneMode;
    private List<Integer> fixedMilestones;
    private final List<List<Integer>> randomMilestonePool;
    private final Map<String, List<Integer>> tierBasedMilestones;
    private final Map<String, List<String>> displayTemplatesByType;
    private List<String> genericDisplayTemplates;
    private final List<LoreEntry> loreStructure;
    private String loreStyle;
    private final Map<String, List<String>> nameTemplates;
    private final Map<String, Object> loreStyles;
    private final Map<String, String> internalNameFormats;
    private boolean mythicMobsEnabled;
    private List<String> mythicMobPool;
    private final boolean mythicMobsInstalled;
    private int maxGenerationRetries;
    private final Map<String, ObjectiveConfig> objectives;
    private final Map<String, int[]> xpByTier;
    private final Map<String, int[]> moneyByTier;
    private String itemSelectionMode;
    private int minItems;
    private int maxItems;
    private final List<TieredItemReward> tieredItemRewards;
    private int totalItemWeight;
    private boolean questRewardsEnabled;
    private int questRewardChance;
    private final List<TieredQuestReward> tieredQuestRewards;
    private final List<RewardTemplate> rewardTemplates;
    private int totalRewardTemplateWeight;
    private String materialSelectionMode;
    private String defaultMaterial;
    private final List<WeightedMaterial> randomMaterialPool;
    private final Map<String, List<String>> tierBasedMaterials;
    private int totalMaterialWeight;
    private boolean conditionsEnabled;
    private final Map<String, ConditionConfig> conditionConfigs;

    public RandomGeneratorConfig(SoapsQuest plugin) {
        this.plugin = plugin;
        this.objectives = new HashMap<String, ObjectiveConfig>();
        this.tieredItemRewards = new ArrayList<TieredItemReward>();
        this.tieredQuestRewards = new ArrayList<TieredQuestReward>();
        this.rewardTemplates = new ArrayList<RewardTemplate>();
        this.objectiveWeights = new HashMap<String, Integer>();
        this.displayTemplatesByType = new HashMap<String, List<String>>();
        this.genericDisplayTemplates = new ArrayList<String>();
        this.loreStructure = new ArrayList<LoreEntry>();
        this.mythicMobPool = new ArrayList<String>();
        this.allowedDifficulties = new ArrayList<String>();
        this.allowedTiers = new ArrayList<String>();
        this.internalNameFormats = new HashMap<String, String>();
        this.xpByTier = new HashMap<String, int[]>();
        this.randomMaterialPool = new ArrayList<WeightedMaterial>();
        this.tierBasedMaterials = new HashMap<String, List<String>>();
        this.conditionConfigs = new HashMap<String, ConditionConfig>();
        this.moneyByTier = new HashMap<String, int[]>();
        this.tierBasedMilestones = new HashMap<String, List<Integer>>();
        this.randomMilestonePool = new ArrayList<List<Integer>>();
        this.nameTemplates = new HashMap<String, List<String>>();
        this.loreStyles = new HashMap<String, Object>();
        this.mythicMobsInstalled = plugin.getServer().getPluginManager().getPlugin("MythicMobs") != null;
        this.init();
    }

    private void init() {
        this.loadConfig();
        this.loadMainConfigSettings();
    }

    private void loadMainConfigSettings() {
        this.maxGenerationRetries = this.plugin.getConfig().getInt("max-generation-retries", 5);
        if (this.maxGenerationRetries < 1) {
            this.plugin.getLogger().warning("max-generation-retries must be at least 1, defaulting to 5");
            this.maxGenerationRetries = 5;
        }
    }

    public void loadConfig() {
        File configFile = new File(this.plugin.getDataFolder(), "random-generator.yml");
        if (!configFile.exists()) {
            this.plugin.saveResource("random-generator.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration((File)configFile);
        this.parseConfig();
    }

    private void parseConfig() {
        ConfigurationSection sequenceSection;
        ConfigurationSection root = this.config.getConfigurationSection("random-generator");
        if (root == null) {
            this.plugin.getLogger().warning("random-generator section not found in random-generator.yml!");
            this.enabled = false;
            return;
        }
        this.enabled = root.getBoolean("enabled", true);
        this.saveGeneratedQuests = root.getBoolean("save-generated-quests", true);
        this.temporaryGeneratedQuests = root.getBoolean("temporary-quests", false);
        this.saveLocation = root.getString("save-location", "generated.yml");
        this.allowedTypes = root.getStringList("allowed-types");
        if (this.allowedTypes.isEmpty()) {
            this.allowedTypes = Arrays.asList("single", "multi", "sequence");
        }
        this.idPrefix = root.getString("id-prefix", "generated_");
        this.internalNameFormat = root.getString("internal-name-format", null);
        this.parseDifficultyPool(root.getConfigurationSection("difficulty-pool"));
        this.parseTierPool(root.getConfigurationSection("tier-pool"));
        this.parseMilestones(root.getConfigurationSection("milestones"));
        ConfigurationSection multiSection = root.getConfigurationSection("multi-objective");
        if (multiSection != null) {
            this.multiMinObjectives = multiSection.getInt("min-objectives", 2);
            this.multiMaxObjectives = multiSection.getInt("max-objectives", 4);
        }
        if ((sequenceSection = root.getConfigurationSection("sequence-objective")) != null) {
            this.sequenceMinObjectives = sequenceSection.getInt("min-objectives", 2);
            this.sequenceMaxObjectives = sequenceSection.getInt("max-objectives", 4);
        }
        this.parseObjectiveWeights(root.getConfigurationSection("objective-weights"));
        this.parseConditions(root.getConfigurationSection("conditions"));
        this.parseDisplaySettings(root.getConfigurationSection("display"));
        this.parseDisplayTemplates(root);
        this.parseDescriptions(root);
        this.parseMythicMobs(root);
        this.parseInternalNameFormats(root.getConfigurationSection("internal-name-formats"));
        this.parseObjectives(root.getConfigurationSection("objectives"));
        this.parseRewardPool(root.getConfigurationSection("reward-pool"));
        this.parseQuestPaperMaterial(root.getConfigurationSection("quest-paper-material"));
    }

    /**
     * Parses YAML amount as {@code [min, max]}: two-number list, one-number list, or single number.
     */
    private static int[] parseMinMaxAmount(Object amountObj) {
        if (amountObj == null) {
            return null;
        }
        if (amountObj instanceof List<?> amountList) {
            if (amountList.size() >= 2) {
                return new int[]{((Number) amountList.get(0)).intValue(), ((Number) amountList.get(1)).intValue()};
            }
            if (amountList.size() == 1) {
                int a = ((Number) amountList.get(0)).intValue();
                return new int[]{a, a};
            }
            return null;
        }
        if (amountObj instanceof Number n) {
            int a = n.intValue();
            return new int[]{a, a};
        }
        return null;
    }

    private static List<String> listObjectToStrings(Object obj) {
        if (!(obj instanceof List<?> list)) {
            return null;
        }
        ArrayList<String> out = new ArrayList<>();
        for (Object o : list) {
            out.add(String.valueOf(o));
        }
        return out.isEmpty() ? null : out;
    }

    private void parseObjectives(ConfigurationSection section) {
        this.objectives.clear();
        if (section == null) {
            this.plugin.getLogger().warning("No objectives section found in random-generator.yml - generator will have no objectives!");
            return;
        }
        for (String key : section.getKeys(false)) {
            try {
                String type;
                HashMap<String, int[]> amountByDifficulty;
                int maxAmount;
                int minAmount;
                boolean supportsAny;
                ArrayList<String> options;
                String objName;
                block31: {
                    ConfigurationSection objSection;
                    block30: {
                        ConfigurationSection amountByDiffSection;
                        objSection = section.getConfigurationSection(key);
                        if (objSection == null) {
                            this.plugin.getLogger().warning(String.format("Objective '%s' is malformed (not a section) - skipping silently", key));
                            continue;
                        }
                        boolean objEnabled = objSection.getBoolean("enabled", true);
                        if (!objEnabled) {
                            this.plugin.getLogger().fine(String.format("Objective '%s' is disabled - skipping", key));
                            continue;
                        }
                        objName = key;
                        options = new ArrayList<>();
                        supportsAny = false;
                        minAmount = 1;
                        maxAmount = 10;
                        amountByDifficulty = null;
                        type = objSection.contains("objective") ? objSection.getString("objective") : key;
                        if (type == null || type.isEmpty()) {
                            this.plugin.getLogger().warning(String.format("Objective '%s' has no type - skipping", key));
                            continue;
                        }
                        List<String> optionsList = null;
                        if (objSection.contains("target")) {
                            optionsList = objSection.getStringList("target");
                        } else if (objSection.contains("entities")) {
                            optionsList = objSection.getStringList("entities");
                        } else if (objSection.contains("blocks")) {
                            optionsList = objSection.getStringList("blocks");
                        } else if (objSection.contains("items")) {
                            optionsList = objSection.getStringList("items");
                        }
                        if (optionsList != null && !optionsList.isEmpty()) {
                            if (optionsList.size() == 1 && optionsList.get(0).equalsIgnoreCase("ANY")) {
                                supportsAny = true;
                                options = new ArrayList<>();
                            } else {
                                options = new ArrayList<>(optionsList);
                            }
                        }
                        if ((amountByDiffSection = objSection.getConfigurationSection("amount-by-difficulty")) == null) break block30;
                        amountByDifficulty = new HashMap<String, int[]>();
                        for (String difficulty : amountByDiffSection.getKeys(false)) {
                            List<?> amountList = amountByDiffSection.getList(difficulty);
                            if (amountList == null || amountList.size() < 2) continue;
                            int min = ((Number)amountList.get(0)).intValue();
                            int max = ((Number)amountList.get(1)).intValue();
                            amountByDifficulty.put(difficulty, new int[]{min, max});
                        }
                        if (amountByDifficulty.isEmpty()) break block31;
                        int[] firstRange = (int[])amountByDifficulty.values().iterator().next();
                        minAmount = firstRange[0];
                        maxAmount = firstRange[1];
                        break block31;
                    }
                    Object amountObj = objSection.get("amount");
                    if (amountObj != null) {
                        int[] range = RandomGeneratorConfig.parseMinMaxAmount(amountObj);
                        if (range != null) {
                            minAmount = range[0];
                            maxAmount = range[1];
                        }
                    }
                }
                if (minAmount > maxAmount) {
                    this.plugin.getLogger().warning(String.format("Objective '%s' has invalid amount range [%d, %d] - swapping values", key, minAmount, maxAmount));
                    int temp = minAmount;
                    minAmount = maxAmount;
                    maxAmount = temp;
                }
                ObjectiveConfig objConfig = new ObjectiveConfig(objName, type, true, options, minAmount, maxAmount, supportsAny, amountByDifficulty);
                this.objectives.put(key, objConfig);
                this.plugin.getLogger().fine(String.format("Loaded objective '%s' (type: %s, amount: [%d, %d], ANY: %b)", key, objConfig.type(), objConfig.minAmount(), objConfig.maxAmount(), objConfig.supportsAny()));
            }
            catch (Exception e) {
                this.plugin.getLogger().warning(String.format("Error parsing objective '%s': %s - skipping silently", key, e.getMessage()));
            }
        }
        if (this.objectives.isEmpty()) {
            this.plugin.getLogger().warning("No valid objectives loaded! Generator will not work.");
        } else {
            this.plugin.debugLog(Level.INFO, "Loaded {0} quest objectives for random generator", this.objectives.size());
        }
    }

    private void parseRewardPool(ConfigurationSection section) {
        ConfigurationSection questsSection;
        ConfigurationSection itemsSection;
        ConfigurationSection moneySection;
        this.tieredItemRewards.clear();
        this.xpByTier.clear();
        this.moneyByTier.clear();
        this.totalItemWeight = 0;
        this.rewardTemplates.clear();
        this.totalRewardTemplateWeight = 0;
        if (section == null) {
            return;
        }
        List<?> templateList = section.getList("templates");
        if (templateList != null) {
            for (Object rawTemplate : templateList) {
                if (!(rawTemplate instanceof Map<?, ?> rawMap)) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> templateMap = (Map<String, Object>) rawMap;
                List<String> tiers = RandomGeneratorConfig.listObjectToStrings(templateMap.get("tiers"));
                String minDifficulty = (String)templateMap.get("min-difficulty");
                int weight = templateMap.containsKey("weight") ? ((Number)templateMap.get("weight")).intValue() : 1;
                Object rewardRaw = templateMap.get("reward");
                Map<String, Object> rewardData = new LinkedHashMap<String, Object>();
                if (rewardRaw instanceof Map<?, ?> nestedReward) {
                    for (Map.Entry<?, ?> entry : nestedReward.entrySet()) {
                        rewardData.put(String.valueOf(entry.getKey()), entry.getValue());
                    }
                } else {
                    for (Map.Entry<String, Object> entry : templateMap.entrySet()) {
                        String key = entry.getKey();
                        if (key.equalsIgnoreCase("tiers") || key.equalsIgnoreCase("min-difficulty") || key.equalsIgnoreCase("weight") || key.equalsIgnoreCase("enabled")) {
                            continue;
                        }
                        rewardData.put(key, entry.getValue());
                    }
                }
                if (rewardData.isEmpty()) {
                    continue;
                }
                if (tiers == null || tiers.isEmpty()) {
                    tiers = List.of("common", "uncommon", "rare", "epic", "legendary", "mythic");
                }
                RewardTemplate rewardTemplate = new RewardTemplate(rewardData, tiers, minDifficulty, Math.max(1, weight));
                this.rewardTemplates.add(rewardTemplate);
                this.totalRewardTemplateWeight += rewardTemplate.weight();
            }
        }
        ConfigurationSection xpSection = section.getConfigurationSection("xp");
        if (xpSection != null) {
            for (String tier : xpSection.getKeys(false)) {
                List<?> xpList = xpSection.getList(tier);
                if (xpList == null || xpList.size() < 2) continue;
                this.xpByTier.put(tier, new int[]{((Number)xpList.get(0)).intValue(), ((Number)xpList.get(1)).intValue()});
            }
        }
        if ((moneySection = section.getConfigurationSection("money")) != null) {
            for (String tier : moneySection.getKeys(false)) {
                List<?> moneyList = moneySection.getList(tier);
                if (moneyList == null || moneyList.size() < 2) continue;
                this.moneyByTier.put(tier, new int[]{((Number)moneyList.get(0)).intValue(), ((Number)moneyList.get(1)).intValue()});
            }
        }
        if ((itemsSection = section.getConfigurationSection("items")) != null) {
            this.itemSelectionMode = itemsSection.getString("selection-mode", "weighted");
            this.minItems = itemsSection.getInt("min-items", 1);
            this.maxItems = itemsSection.getInt("max-items", 3);
            List<?> itemsList = itemsSection.getList("pool");
            if (itemsList != null) {
                for (Object rawItem : itemsList) {
                    if (!(rawItem instanceof Map<?, ?> rawMap)) {
                        continue;
                    }
                    @SuppressWarnings("unchecked")
                    Map<String, Object> itemMap = (Map<String, Object>) rawMap;
                    String material = (String)itemMap.get("material");
                    int rMinAmount = 0;
                    int rMaxAmount = 0;
                    List<String> rTiers = RandomGeneratorConfig.listObjectToStrings(itemMap.get("tiers"));
                    String rMinDifficulty = (String)itemMap.get("min-difficulty");
                    int rWeight = itemMap.containsKey("weight") ? ((Number)itemMap.get("weight")).intValue() : 1;
                    boolean isCustomItem = false;
                    if (material != null) {
                        String materialLower = material.toLowerCase();
                        isCustomItem = materialLower.startsWith("mmoitem:") || materialLower.startsWith("mmoitems:");
                    }
                    int[] amountRange = RandomGeneratorConfig.parseMinMaxAmount(itemMap.get("amount"));
                    if (amountRange != null) {
                        rMinAmount = amountRange[0];
                        rMaxAmount = amountRange[1];
                    }
                    String rName = (String)itemMap.get("name");
                    List<String> rLore = RandomGeneratorConfig.listObjectToStrings(itemMap.get("lore"));
                    List<String> rEnchantments = RandomGeneratorConfig.listObjectToStrings(itemMap.get("enchantments"));
                    List<String> rFlags = RandomGeneratorConfig.listObjectToStrings(itemMap.get("flags"));
                    Boolean unbreakable = null;
                    Object unbreakableObj = itemMap.get("unbreakable");
                    if (unbreakableObj instanceof Boolean unbreakableBool) {
                        unbreakable = unbreakableBool;
                    }
                    Integer customModelData = null;
                    Object customModelDataObj = itemMap.get("custom-model-data");
                    if (customModelDataObj instanceof Number) {
                        Number customModelNum = (Number)customModelDataObj;
                        customModelData = customModelNum.intValue();
                    }
                    if (material == null || rTiers == null || rTiers.isEmpty()) continue;
                    TieredItemReward reward = new TieredItemReward(material, rMinAmount, rMaxAmount, rTiers, rMinDifficulty, rWeight, isCustomItem, rName, rLore, rEnchantments, rFlags, unbreakable, customModelData);
                    this.tieredItemRewards.add(reward);
                    this.totalItemWeight += rWeight;
                }
            }
        }
        if ((questsSection = section.getConfigurationSection("quests")) != null) {
            this.questRewardsEnabled = questsSection.getBoolean("enabled", false);
            this.questRewardChance = questsSection.getInt("chance", 15);
            List<?> questsList = questsSection.getList("pool");
            if (questsList != null) {
                for (Object rawQuest : questsList) {
                    if (!(rawQuest instanceof Map<?, ?> rawMap)) {
                        continue;
                    }
                    @SuppressWarnings("unchecked")
                    Map<String, Object> questMap = (Map<String, Object>) rawMap;
                    String questId = (String)questMap.get("quest-id");
                    List<String> qTiers = RandomGeneratorConfig.listObjectToStrings(questMap.get("tiers"));
                    String qMinDifficulty = (String)questMap.get("min-difficulty");
                    int qChance = questMap.containsKey("chance") ? ((Number)questMap.get("chance")).intValue() : 100;
                    if (questId == null || qTiers == null || qTiers.isEmpty()) continue;
                    this.tieredQuestRewards.add(new TieredQuestReward(questId, qTiers, qMinDifficulty, qChance));
                }
            }
        }
    }

    private void parseQuestPaperMaterial(ConfigurationSection section) {
        ConfigurationSection tierSection;
        List<?> poolList;
        this.randomMaterialPool.clear();
        this.tierBasedMaterials.clear();
        this.totalMaterialWeight = 0;
        if (section == null) {
            this.materialSelectionMode = "fixed";
            this.defaultMaterial = "PAPER";
            return;
        }
        this.materialSelectionMode = section.getString("selection-mode", "fixed");
        this.defaultMaterial = section.getString("default-material", "PAPER");
        if (section.contains("random-pool") && (poolList = section.getList("random-pool")) != null) {
            for (Object rawEntry : poolList) {
                if (!(rawEntry instanceof Map<?, ?> rawMap)) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> entry = (Map<String, Object>) rawMap;
                String wmMaterial = (String)entry.get("material");
                int wmWeight = entry.containsKey("weight") ? ((Number)entry.get("weight")).intValue() : 1;
                this.randomMaterialPool.add(new WeightedMaterial(wmMaterial, wmWeight));
                this.totalMaterialWeight += wmWeight;
            }
        }
        if (section.contains("tier-based") && (tierSection = section.getConfigurationSection("tier-based")) != null) {
            for (String tier : tierSection.getKeys(false)) {
                List<String> materials = tierSection.getStringList(tier);
                this.tierBasedMaterials.put(tier, materials);
            }
        }
    }

    private void parseConditions(ConfigurationSection section) {
        this.conditionConfigs.clear();
        if (section == null) {
            this.conditionsEnabled = false;
            return;
        }
        this.conditionsEnabled = section.getBoolean("enabled", false);
        if (!this.conditionsEnabled) {
            return;
        }
        for (String key : section.getKeys(false)) {
            int chance;
            boolean enabled;
            ConfigurationSection condSection;
            if (key.equals("enabled") || (condSection = section.getConfigurationSection(key)) == null || !(enabled = condSection.getBoolean("enabled", false)) || (chance = condSection.getInt("chance", 0)) <= 0) continue;
            HashMap<String, Object> byTier = new HashMap<String, Object>();
            ConfigurationSection byTierSection = condSection.getConfigurationSection("by-tier");
            if (byTierSection != null) {
                for (String tier : byTierSection.getKeys(false)) {
                    byTier.put(tier, byTierSection.get(tier));
                }
            }
            HashMap<String, Object> byDifficulty = new HashMap<String, Object>();
            ConfigurationSection byDiffSection = condSection.getConfigurationSection("by-difficulty");
            if (byDiffSection != null) {
                for (String diff : byDiffSection.getKeys(false)) {
                    byDifficulty.put(diff, byDiffSection.get(diff));
                }
            }
            Object defaultValue = condSection.get("default");
            List<String> options = condSection.getStringList("options");
            if (options.isEmpty()) {
                options = condSection.getStringList("allowed-worlds");
            }
            if (options.isEmpty()) {
                options = condSection.getStringList("allowed-modes");
            }
            if (options.isEmpty()) {
                options = condSection.getStringList("expressions");
            }
            boolean consumeItem = condSection.getBoolean("consume-item", false);
            this.conditionConfigs.put(key, new ConditionConfig(key, enabled, chance, byTier, byDifficulty, defaultValue, options, consumeItem));
            this.plugin.getLogger().fine(String.format("Loaded condition config '%s' (chance: %d%%)", key, chance));
        }
        this.plugin.debugLog(Level.INFO, "Loaded {0} condition configs for random generator", this.conditionConfigs.size());
    }

    private void parseObjectiveWeights(ConfigurationSection section) {
        this.objectiveWeights.clear();
        this.totalObjectiveWeight = 0;
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            int weight = section.getInt(key, 1);
            if (key.equalsIgnoreCase("kill_mythicmob") && (!this.mythicMobsInstalled || !this.mythicMobsEnabled)) continue;
            this.objectiveWeights.put(key, weight);
            this.totalObjectiveWeight += weight;
        }
    }

    private void parseDifficultyPool(ConfigurationSection section) {
        if (section == null) {
            this.difficultyPoolEnabled = false;
            this.defaultDifficulty = "normal";
            this.allowedDifficulties = null;
            return;
        }
        this.difficultyPoolEnabled = section.getBoolean("enabled", true);
        List<String> configuredList = section.getStringList("allowed");
        this.defaultDifficulty = section.getString("default", "normal");
        this.allowedDifficulties = configuredList.isEmpty() ? null : configuredList;
    }

    private void parseTierPool(ConfigurationSection section) {
        if (section == null) {
            this.tierPoolEnabled = false;
            this.defaultTier = "common";
            this.allowedTiers = null;
            return;
        }
        this.tierPoolEnabled = section.getBoolean("enabled", true);
        List<String> configuredList = section.getStringList("allowed");
        this.defaultTier = section.getString("default", "common");
        this.allowedTiers = configuredList.isEmpty() ? null : configuredList;
    }

    private void parseMilestones(ConfigurationSection section) {
        ConfigurationSection tierSection;
        List<?> randomPool;
        this.tierBasedMilestones.clear();
        this.randomMilestonePool.clear();
        if (section == null) {
            this.milestonesEnabled = false;
            this.milestoneMode = "none";
            return;
        }
        this.milestonesEnabled = section.getBoolean("enabled", true);
        this.milestoneMode = section.getString("mode", "tier-based");
        this.fixedMilestones = new ArrayList<Integer>();
        List<?> defaultList = section.getList("default");
        if (defaultList != null) {
            for (Object obj : defaultList) {
                if (!(obj instanceof Number)) continue;
                Number number = (Number)obj;
                this.fixedMilestones.add(number.intValue());
            }
        }
        if ((randomPool = section.getList("random-pool")) != null) {
            for (Object poolEntry : randomPool) {
                if (!(poolEntry instanceof List<?> entry)) {
                    continue;
                }
                ArrayList<Integer> milestones = new ArrayList<Integer>();
                for (Object obj : entry) {
                    if (!(obj instanceof Number)) continue;
                    Number number = (Number)obj;
                    milestones.add(number.intValue());
                }
                if (milestones.isEmpty()) continue;
                this.randomMilestonePool.add(milestones);
            }
        }
        if ((tierSection = section.getConfigurationSection("tier-based")) != null) {
            for (String tier : tierSection.getKeys(false)) {
                ArrayList<Integer> milestones = new ArrayList<Integer>();
                List<?> milestoneList = tierSection.getList(tier);
                if (milestoneList != null) {
                    for (Object obj : milestoneList) {
                        if (!(obj instanceof Number)) continue;
                        Number number = (Number)obj;
                        milestones.add(number.intValue());
                    }
                }
                if (milestones.isEmpty()) continue;
                this.tierBasedMilestones.put(tier, milestones);
            }
        }
    }

    private void parseDisplaySettings(ConfigurationSection section) {
        ConfigurationSection loreStylesSection;
        ConfigurationSection nameTemplatesSection;
        if (section == null) {
            this.loreStyle = "simple";
            return;
        }
        this.loreStyle = section.getString("lore-style", "simple");
        if (this.loreStyle == null || this.loreStyle.isEmpty()) {
            this.loreStyle = "simple";
        }
        if ((nameTemplatesSection = section.getConfigurationSection("name-templates")) != null) {
            this.nameTemplates.clear();
            for (String key : nameTemplatesSection.getKeys(false)) {
                List<String> templates = nameTemplatesSection.getStringList(key);
                if (templates.isEmpty()) continue;
                this.nameTemplates.put(key, templates);
            }
        }
        if ((loreStylesSection = section.getConfigurationSection("lore-styles")) != null) {
            this.loreStyles.clear();
            for (String styleKey : loreStylesSection.getKeys(false)) {
                Object styleValue = loreStylesSection.get(styleKey);
                if (styleValue instanceof List) {
                    this.loreStyles.put(styleKey, loreStylesSection.getStringList(styleKey));
                    continue;
                }
                if (!loreStylesSection.isConfigurationSection(styleKey)) continue;
                ConfigurationSection styleSection = loreStylesSection.getConfigurationSection(styleKey);
                HashMap<String, List<String>> tierVariants = new HashMap<String, List<String>>();
                if (styleSection != null) {
                    for (String tierKey : styleSection.getKeys(false)) {
                        tierVariants.put(tierKey, styleSection.getStringList(tierKey));
                    }
                }
                this.loreStyles.put(styleKey, tierVariants);
            }
        }
        this.plugin.debugLog(Level.INFO, "Display settings loaded: lore-style={0}, name-templates={1}, lore-styles={2}", this.loreStyle, this.nameTemplates.size(), this.loreStyles.size());
    }

    private void parseDisplayTemplates(ConfigurationSection root) {
        this.displayTemplatesByType.clear();
        this.genericDisplayTemplates.clear();
        ConfigurationSection templatesSection = root.getConfigurationSection("display-templates");
        if (templatesSection != null) {
            for (String key : templatesSection.getKeys(false)) {
                List<String> templates = templatesSection.getStringList(key);
                if (templates.isEmpty()) continue;
                this.displayTemplatesByType.put(key, templates);
            }
            if (this.displayTemplatesByType.containsKey("generic")) {
                this.genericDisplayTemplates = this.displayTemplatesByType.get("generic");
            }
        }
        if (this.displayTemplatesByType.isEmpty() && this.genericDisplayTemplates.isEmpty()) {
            this.genericDisplayTemplates.add("&7[Quest] &f<type> Challenge");
            this.genericDisplayTemplates.add("&7Guild Contract");
        }
    }

    private void parseDescriptions(ConfigurationSection root) {
        this.parseLoreStructure(root);
    }

    private void parseLoreStructure(ConfigurationSection root) {
        this.loreStructure.clear();
        ConfigurationSection loreSection = root.getConfigurationSection("lore-structure");
        if (loreSection != null) {
            this.parseNewLoreStructure(loreSection);
            return;
        }
        if (!root.contains("lore_structure")) {
            return;
        }
        List<?> loreList = root.getList("lore_structure");
        if (loreList == null || loreList.isEmpty()) {
            return;
        }
        for (Object obj : loreList) {
            if (obj instanceof String) {
                String fixedLine = (String)obj;
                this.loreStructure.add(new LoreEntry.Fixed(fixedLine));
                continue;
            }
            if (!(obj instanceof Map<?, ?> map) || !map.containsKey("random_group")) {
                continue;
            }
            Object groupObj = map.get("random_group");
            if (!(groupObj instanceof List<?> optRaw) || optRaw.isEmpty()) {
                continue;
            }
            List<String> options = new ArrayList<>();
            for (Object o : optRaw) {
                options.add(String.valueOf(o));
            }
            this.loreStructure.add(new LoreEntry.RandomGroup(options));
        }
    }

    private void parseNewLoreStructure(ConfigurationSection loreSection) {
        block12: for (String sectionName : loreSection.getKeys(false)) {
            ConfigurationSection section = loreSection.getConfigurationSection(sectionName);
            if (section == null) continue;
            String mode = section.getString("mode", "random");
            if (mode == null) {
                mode = "random";
            }
            List<String> entries = section.getStringList("entries");
            switch (mode.toLowerCase()) {
                case "static": {
                    for (String entry : entries) {
                        this.loreStructure.add(new LoreEntry.Fixed(entry));
                    }
                    continue block12;
                }
                case "random": {
                    if (entries.isEmpty()) continue block12;
                    this.loreStructure.add(new LoreEntry.RandomGroup(entries));
                    break;
                }
                case "all": {
                    for (String entry : entries) {
                        this.loreStructure.add(new LoreEntry.Fixed(entry));
                    }
                    continue block12;
                }
                case "tier-based": {
                    HashMap<String, List<String>> tierEntries = new HashMap<String, List<String>>();
                    for (String tier : section.getKeys(false)) {
                        List<String> tierLines;
                        if (tier.equals("mode") || (tierLines = section.getStringList(tier)).isEmpty()) continue;
                        tierEntries.put(tier, tierLines);
                    }
                    if (tierEntries.isEmpty()) continue block12;
                    this.loreStructure.add(new LoreEntry.TierBased(tierEntries));
                    break;
                }
                default: {
                    this.plugin.getLogger().warning(String.format("Unknown lore mode '%s' in section '%s' - skipping", mode, sectionName));
                }
            }
        }
    }

    private void parseMythicMobs(ConfigurationSection root) {
        this.mythicMobPool.clear();
        ConfigurationSection mmSection = root.getConfigurationSection("mythicmobs");
        if (mmSection != null) {
            this.mythicMobsEnabled = mmSection.getBoolean("enabled", true);
            this.mythicMobPool = mmSection.getStringList("pool");
        } else {
            this.mythicMobsEnabled = true;
            this.mythicMobPool = root.getStringList("mythicmob-pool");
        }
        if (!this.mythicMobsInstalled) {
            this.mythicMobsEnabled = false;
            this.mythicMobPool.clear();
            return;
        }
        if (!this.mythicMobsEnabled) {
            this.mythicMobPool.clear();
            return;
        }
        if (this.mythicMobPool.isEmpty()) {
            this.mythicMobPool.add("SkeletalKnight");
        }
        this.validateMythicMobs();
    }

    private void validateMythicMobs() {
        if (!this.mythicMobsInstalled || this.mythicMobPool.isEmpty()) {
            return;
        }
        try {
            MythicBukkit mythicMobs = MythicBukkit.inst();
            ArrayList<String> invalidMobs = new ArrayList<String>();
            for (String mobName : new ArrayList<String>(this.mythicMobPool)) {
                if (mythicMobs.getMobManager().getMobNames().contains(mobName)) continue;
                invalidMobs.add(mobName);
                this.mythicMobPool.remove(mobName);
            }
            if (!invalidMobs.isEmpty()) {
                this.plugin.getLogger().warning(String.format("Removed %d invalid MythicMobs from pool: %s", invalidMobs.size(), String.join((CharSequence)", ", invalidMobs)));
            }
            if (this.mythicMobPool.isEmpty()) {
                this.plugin.getLogger().warning("MythicMobs pool is empty after validation! No mythicmob quests can be generated.");
            }
        }
        catch (Exception e) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to validate MythicMobs: {0}", e.getMessage());
        }
    }

    private void parseInternalNameFormats(ConfigurationSection section) {
        this.internalNameFormats.clear();
        if (section == null) {
            this.internalNameFormats.put("single", "quest_<tier>_<objective>_<id>");
            this.internalNameFormats.put("multi", "multi_<diff>_<tier>_<id>");
            this.internalNameFormats.put("sequence", "seq_<tier>_<counter>");
            this.internalNameFormats.put("default", "generated_<id>");
            return;
        }
        for (String key : section.getKeys(false)) {
            String format = section.getString(key);
            if (format == null || format.isEmpty()) continue;
            this.internalNameFormats.put(key, format);
        }
        if (!this.internalNameFormats.containsKey("default")) {
            this.internalNameFormats.put("default", "generated_%id%");
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isSaveGeneratedQuests() {
        return this.saveGeneratedQuests;
    }

    public boolean isTemporaryGeneratedQuests() {
        return this.temporaryGeneratedQuests;
    }

    public String getSaveLocation() {
        return this.saveLocation;
    }

    public List<String> getAllowedTypes() {
        return this.allowedTypes;
    }

    public String getDefaultTier() {
        return this.defaultTier;
    }

    public String getDefaultDifficulty() {
        return this.defaultDifficulty;
    }

    public String getIdPrefix() {
        return this.idPrefix;
    }

    public String getInternalNameFormat() {
        return this.internalNameFormat;
    }

    public int getMultiMinObjectives() {
        return this.multiMinObjectives;
    }

    public int getMultiMaxObjectives() {
        return this.multiMaxObjectives;
    }

    public int getSequenceMinObjectives() {
        return this.sequenceMinObjectives;
    }

    public int getSequenceMaxObjectives() {
        return this.sequenceMaxObjectives;
    }

    public Map<String, Integer> getObjectiveWeights() {
        return this.objectiveWeights;
    }

    public int getTotalObjectiveWeight() {
        return this.totalObjectiveWeight;
    }

    public List<String> getAllowedDifficulties() {
        return this.allowedDifficulties;
    }

    public List<String> getAllowedTiers() {
        return this.allowedTiers;
    }

    public boolean isDifficultyPoolEnabled() {
        return this.difficultyPoolEnabled;
    }

    public boolean isTierPoolEnabled() {
        return this.tierPoolEnabled;
    }

    public Map<String, List<String>> getDisplayTemplatesByType() {
        return this.displayTemplatesByType;
    }

    public List<String> getGenericDisplayTemplates() {
        return this.genericDisplayTemplates;
    }

    public Map<String, String> getInternalNameFormats() {
        return this.internalNameFormats;
    }

    public List<LoreEntry> getLoreStructure() {
        return this.loreStructure;
    }

    public boolean isMythicMobsEnabled() {
        return this.mythicMobsEnabled;
    }

    public List<String> getMythicMobPool() {
        return this.mythicMobPool;
    }

    public boolean isMythicMobsInstalled() {
        return this.mythicMobsInstalled;
    }

    public Map<String, ObjectiveConfig> getObjectives() {
        return this.objectives;
    }

    public Map<String, int[]> getXpByTier() {
        return this.xpByTier;
    }

    public Map<String, int[]> getMoneyByTier() {
        return this.moneyByTier;
    }

    public String getItemSelectionMode() {
        return this.itemSelectionMode;
    }

    public int getMinItems() {
        return this.minItems;
    }

    public int getMaxItems() {
        return this.maxItems;
    }

    public List<TieredItemReward> getTieredItemRewards() {
        return this.tieredItemRewards;
    }

    public int getTotalItemWeight() {
        return this.totalItemWeight;
    }

    public boolean isQuestRewardsEnabled() {
        return this.questRewardsEnabled;
    }

    public int getQuestRewardChance() {
        return this.questRewardChance;
    }

    public List<TieredQuestReward> getTieredQuestRewards() {
        return this.tieredQuestRewards;
    }

    public List<RewardTemplate> getRewardTemplates() {
        return this.rewardTemplates;
    }

    public int getTotalRewardTemplateWeight() {
        return this.totalRewardTemplateWeight;
    }

    public int getMaxGenerationRetries() {
        return this.maxGenerationRetries;
    }

    public String getMaterialSelectionMode() {
        return this.materialSelectionMode;
    }

    public String getDefaultMaterial() {
        return this.defaultMaterial;
    }

    public List<WeightedMaterial> getRandomMaterialPool() {
        return this.randomMaterialPool;
    }

    public Map<String, List<String>> getTierBasedMaterials() {
        return this.tierBasedMaterials;
    }

    public int getTotalMaterialWeight() {
        return this.totalMaterialWeight;
    }

    public boolean isMilestonesEnabled() {
        return this.milestonesEnabled;
    }

    public String getMilestoneMode() {
        return this.milestoneMode;
    }

    public List<Integer> getFixedMilestones() {
        return this.fixedMilestones;
    }

    public List<List<Integer>> getRandomMilestonePool() {
        return this.randomMilestonePool;
    }

    public Map<String, List<Integer>> getTierBasedMilestones() {
        return this.tierBasedMilestones;
    }

    public String getLoreStyle() {
        return this.loreStyle;
    }

    public Map<String, List<String>> getNameTemplates() {
        return this.nameTemplates;
    }

    public Map<String, Object> getLoreStyles() {
        return this.loreStyles;
    }

    public boolean isConditionsEnabled() {
        return this.conditionsEnabled;
    }

    public Map<String, ConditionConfig> getConditionConfigs() {
        return this.conditionConfigs;
    }

    public List<ObjectiveConfig> getObjectivesByType(String type) {
        ArrayList<ObjectiveConfig> result = new ArrayList<ObjectiveConfig>();
        for (ObjectiveConfig objConfig : this.objectives.values()) {
            if (!objConfig.type().equalsIgnoreCase(type)) continue;
            result.add(objConfig);
        }
        return result;
    }

    public ObjectiveConfig getObjectiveByName(String name) {
        return this.objectives.get(name);
    }

    public record ObjectiveConfig(String name, String type, boolean enabled, List<String> options, int minAmount, int maxAmount, boolean supportsAny, Map<String, int[]> amountByDifficulty) {
    }

    public record TieredItemReward(String material, int minAmount, int maxAmount, List<String> tiers, String minDifficulty, int weight, boolean isCustomItem, String name, List<String> lore, List<String> enchantments, List<String> flags, Boolean unbreakable, Integer customModelData) {
    }

    public record TieredQuestReward(String questId, List<String> tiers, String minDifficulty, int chance) {
    }

    public record RewardTemplate(Map<String, Object> reward, List<String> tiers, String minDifficulty, int weight) {
    }

    public record WeightedMaterial(String material, int weight) {
    }

    public record ConditionConfig(String type, boolean enabled, int chance, Map<String, Object> byTier, Map<String, Object> byDifficulty, Object defaultValue, List<String> options, boolean consumeItem) {
    }

    public static sealed interface LoreEntry {

        public record TierBased(Map<String, List<String>> tierEntries) implements LoreEntry
        {
        }

        public record RandomGroup(List<String> options) implements LoreEntry
        {
        }

        public record Fixed(String line) implements LoreEntry
        {
        }
    }
}

