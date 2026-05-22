/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package com.soaps.quest.premium;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.managers.DifficultyManager;
import com.soaps.quest.managers.QuestGeneratorService;
import com.soaps.quest.managers.TierManager;
import com.soaps.quest.objectives.ObjectiveRegistry;
import com.soaps.quest.premium.RandomGeneratorConfig;
import com.soaps.quest.utils.ConfigNormalizer;
import com.soaps.quest.utils.PlaceholderManager;
import com.soaps.quest.utils.WeightedRandomPicker;
import com.soaps.quest.utils.YamlUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class QuestGenerator
implements QuestGeneratorService {
    private final Map<UUID, Long> cooldowns = new HashMap<UUID, Long>();
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

    @Override
    public String generateQuest() {
        return this.generateQuest(null, null);
    }

    @Override
    public String generateQuest(String type) {
        return this.generateQuest(type, null);
    }

    public String generateQuest(String type, String difficulty) {
        int maxRetries = this.config.getMaxGenerationRetries();
        for (int attempt = 1; attempt <= maxRetries; ++attempt) {
            try {
                String questId = this.attemptQuestGeneration(type, difficulty);
                if (questId != null) {
                    if (attempt > 1) {
                        this.plugin.getLogger().info(String.format("Successfully generated quest '%s' on attempt %d/%d", questId, attempt, maxRetries));
                    }
                    return questId;
                }
                if (attempt >= maxRetries) continue;
                this.plugin.getLogger().warning(String.format("Quest generation attempt %d/%d failed, retrying...", attempt, maxRetries));
                continue;
            }
            catch (Exception e) {
                if (attempt < maxRetries) {
                    this.plugin.getLogger().warning(String.format("Quest generation attempt %d/%d encountered an error, retrying... Error: %s", attempt, maxRetries, e.getMessage()));
                    continue;
                }
                this.plugin.getLogger().severe(String.format("Quest generation failed after %d attempts. Last error: %s", maxRetries, e.getMessage()));
                if (!this.plugin.isDebugMode()) continue;
                for (StackTraceElement element : e.getStackTrace()) {
                    this.plugin.getLogger().severe(String.format("  at %s", element));
                }
            }
        }
        this.plugin.getLogger().severe(String.format("Failed to generate quest after %d attempts. Check your configuration.", maxRetries));
        return null;
    }

    private String attemptQuestGeneration(String type, String difficulty) {
        List<Integer> milestones;
        Map<String, Object> conditions;
        List<Map<String, Object>> objectives;
        TierManager.Tier tier;
        DifficultyManager.Difficulty diff;
        if (type == null) {
            List<String> allowedTypes = this.config.getAllowedTypes();
            if (allowedTypes.isEmpty()) {
                return null;
            }
            type = allowedTypes.get(this.random.nextInt(allowedTypes.size()));
        }
        if ((diff = this.selectDifficulty(difficulty)) == null) {
            diff = this.plugin.getDifficultyManager().getDifficultyOrDefault(this.config.getDefaultDifficulty());
        }
        if ((tier = this.selectTier()) == null) {
            tier = this.plugin.getTierManager().getTierOrDefault(this.config.getDefaultTier());
        }
        this.plugin.getLogger().fine(String.format("[Generator] Applied tier '%s' with color '%s' and difficulty '%s'", tier.name, tier.color, diff.name));
        String questId = this.generateUniqueId(type, tier.name, diff.name, null);
        LinkedHashMap<String, Object> questData = new LinkedHashMap<String, Object>();
        if (type.equals("single")) {
            Map<String, Object> objective = this.generateRandomObjective(diff);
            if (objective == null) {
                return null;
            }
            objectives = Collections.singletonList(objective);
        } else {
            int minObj = type.equals("multi") ? this.config.getMultiMinObjectives() : this.config.getSequenceMinObjectives();
            int maxObj = type.equals("multi") ? this.config.getMultiMaxObjectives() : this.config.getSequenceMaxObjectives();
            int numObjectives = this.random.nextInt(maxObj - minObj + 1) + minObj;
            objectives = this.generateMultipleObjectives(numObjectives, diff);
            if (objectives == null || objectives.isEmpty()) {
                this.plugin.getLogger().log(Level.WARNING, "[Generator] Failed to generate objectives for {0} quest", type);
                return null;
            }
            for (int i = 0; i < objectives.size(); ++i) {
                if (objectives.get(i) != null) continue;
                this.plugin.getLogger().warning(String.format("[Generator] Objective at index %d is null, aborting quest generation", i));
                return null;
            }
        }
        String displayName = this.generateDisplayName(objectives.get(0), tier, diff);
        questData.put("display", displayName);
        List<String> lore = this.generateQuestLore(tier, diff, type);
        questData.put("lore", lore);
        questData.put("type", type);
        questData.put("tier", tier.name.toLowerCase());
        questData.put("difficulty", diff.name.toLowerCase());
        String material = this.selectQuestPaperMaterial(tier);
        questData.put("material", material);
        questData.put("objectives", objectives);
        if (this.isSimilarQuestExists(objectives)) {
            this.plugin.getLogger().fine("[Generator] Similar quest already exists, retrying...");
            return null;
        }
        if (type.equals("sequence")) {
            questData.put("sequential", true);
        }
        questData.put("lock-to-player", false);
        if (this.config.isTemporaryGeneratedQuests()) {
            questData.put("temporary", true);
        }
        Map<String, Object> rewards = this.generateRewards(tier, diff);
        if (!rewards.isEmpty()) {
            questData.put("reward", rewards);
        }
        if (!(conditions = this.generateConditions(tier, diff)).isEmpty()) {
            questData.put("conditions", conditions);
        }
        if ((milestones = this.generateMilestones(tier)) != null && !milestones.isEmpty()) {
            questData.put("milestones", milestones);
        }
        if (this.config.isSaveGeneratedQuests()) {
            if (!this.saveQuest(questId, questData)) {
                return null;
            }
        } else {
            this.plugin.getQuestManager().loadQuests();
        }
        return questId;
    }

    private DifficultyManager.Difficulty selectDifficulty(String difficultyName) {
        DifficultyManager difficultyManager = this.plugin.getDifficultyManager();
        if (difficultyName != null) {
            return difficultyManager.getDifficulty(difficultyName);
        }
        List<String> allowedDifficulties = this.config.getAllowedDifficulties();
        if (allowedDifficulties == null || allowedDifficulties.isEmpty()) {
            return difficultyManager.getRandomDifficulty();
        }
        ArrayList<DifficultyManager.Difficulty> filtered = new ArrayList<DifficultyManager.Difficulty>();
        for (String diffName : allowedDifficulties) {
            DifficultyManager.Difficulty diff = difficultyManager.getDifficulty(diffName);
            if (diff == null) continue;
            filtered.add(diff);
        }
        if (filtered.isEmpty()) {
            return difficultyManager.getRandomDifficulty();
        }
        WeightedRandomPicker<DifficultyManager.Difficulty> picker = new WeightedRandomPicker<DifficultyManager.Difficulty>(d -> d.weight);
        DifficultyManager.Difficulty result = picker.pick((List<DifficultyManager.Difficulty>)filtered);
        return result != null ? result : (DifficultyManager.Difficulty)filtered.get(0);
    }

    private TierManager.Tier selectTier() {
        TierManager tierManager = this.plugin.getTierManager();
        List<String> allowedTiers = this.config.getAllowedTiers();
        if (allowedTiers == null || allowedTiers.isEmpty()) {
            return tierManager.getRandomTier();
        }
        ArrayList<TierManager.Tier> filtered = new ArrayList<TierManager.Tier>();
        for (String tierName : allowedTiers) {
            TierManager.Tier tier = tierManager.getTier(tierName);
            if (tier == null) continue;
            filtered.add(tier);
        }
        if (filtered.isEmpty()) {
            return tierManager.getRandomTier();
        }
        WeightedRandomPicker<TierManager.Tier> picker = new WeightedRandomPicker<TierManager.Tier>(t -> t.weight);
        TierManager.Tier result = picker.pick((List<TierManager.Tier>)filtered);
        return result != null ? result : (TierManager.Tier)filtered.get(0);
    }

    private List<Integer> generateMilestones(TierManager.Tier tier) {
        if (!this.config.isMilestonesEnabled()) {
            return null;
        }
        String mode = this.config.getMilestoneMode();
        if (mode == null || mode.equalsIgnoreCase("none")) {
            return null;
        }
        switch (mode.toLowerCase()) {
            case "default": {
                return this.config.getFixedMilestones();
            }
            case "random": {
                List<List<Integer>> pool = this.config.getRandomMilestonePool();
                if (pool != null && !pool.isEmpty()) {
                    return pool.get(this.random.nextInt(pool.size()));
                }
                return null;
            }
            case "tier-based": {
                Map<String, List<Integer>> tierMilestones = this.config.getTierBasedMilestones();
                if (tierMilestones != null && tier != null) {
                    return tierMilestones.get(tier.name.toLowerCase());
                }
                return null;
            }
        }
        this.plugin.getLogger().log(Level.WARNING, "Unknown milestone mode: {0}", mode);
        return null;
    }

    private Map<String, Object> generateRandomObjective(DifficultyManager.Difficulty difficulty) {
        String objectiveName = this.selectWeightedObjectiveType();
        if (objectiveName == null) {
            return null;
        }
        return this.buildObjective(objectiveName, difficulty);
    }

    private List<Map<String, Object>> generateMultipleObjectives(int count, DifficultyManager.Difficulty difficulty) {
        ArrayList<Map<String, Object>> objectives = new ArrayList<Map<String, Object>>();
        HashSet<String> usedObjectiveNames = new HashSet<String>();
        int retries = 0;
        int maxRetries = count * 3;
        for (int i = 0; i < count && retries < maxRetries; ++i) {
            String objectiveName = this.selectWeightedObjectiveType(usedObjectiveNames);
            if (objectiveName == null) {
                ++retries;
                --i;
                continue;
            }
            Map<String, Object> objective = this.buildObjective(objectiveName, difficulty);
            if (objective != null) {
                objectives.add(objective);
                usedObjectiveNames.add(objectiveName);
                continue;
            }
            ++retries;
            --i;
            this.plugin.getLogger().warning(String.format("[Generator] Failed to build objective '%s', retrying...", objectiveName));
        }
        if (objectives.size() < count) {
            this.plugin.getLogger().warning(String.format("[Generator] Could only generate %d/%d objectives after %d retries", objectives.size(), count, retries));
        }
        return objectives;
    }

    private String selectWeightedObjectiveType() {
        return this.selectWeightedObjectiveType(new HashSet<String>());
    }

    private String selectWeightedObjectiveType(Set<String> excludedTypes) {
        Integer mythicWeight;
        int totalWeight = 0;
        HashMap<String, Integer> candidateObjectives = new HashMap<String, Integer>();
        for (Map.Entry<String, RandomGeneratorConfig.ObjectiveConfig> entry : this.config.getObjectives().entrySet()) {
            String objectiveName = entry.getKey();
            RandomGeneratorConfig.ObjectiveConfig objConfig = entry.getValue();
            String type = objConfig.type();
            if (excludedTypes.contains(objectiveName) || type.equalsIgnoreCase("kill_mythicmob") && !this.config.isMythicMobsInstalled()) continue;
            if (!ObjectiveRegistry.isRegistered(type)) {
                this.plugin.getLogger().warning(String.format("[Generator] Skipped objective '%s' - type '%s' is not registered", objectiveName, type));
                continue;
            }
            Integer weight = this.config.getObjectiveWeights().get(type);
            if (weight == null || weight <= 0) continue;
            candidateObjectives.put(objectiveName, weight);
            totalWeight += weight.intValue();
        }
        if (this.config.isMythicMobsInstalled() && !this.config.getMythicMobPool().isEmpty() && (mythicWeight = this.config.getObjectiveWeights().get("kill_mythicmob")) != null && mythicWeight > 0 && !excludedTypes.contains("kill_mythicmob")) {
            candidateObjectives.put("kill_mythicmob", mythicWeight);
            totalWeight += mythicWeight.intValue();
        }
        if (totalWeight == 0) {
            this.plugin.getLogger().warning("No valid objectives available for generation! Check your config.");
            return null;
        }
        String result = (String)WeightedRandomPicker.pickFromMap(candidateObjectives);
        return result;
    }

    private Map<String, Object> buildObjective(String objectiveName, DifficultyManager.Difficulty difficulty) {
        int[] difficultyRange;
        LinkedHashMap<String, Object> objective = new LinkedHashMap<String, Object>();
        if (objectiveName.equalsIgnoreCase("kill_mythicmob")) {
            List<String> mobPool = this.config.getMythicMobPool();
            if (mobPool.isEmpty()) {
                this.plugin.getLogger().warning("MythicMobs objective selected but mob pool is empty!");
                return null;
            }
            objective.put("type", "kill_mythicmob");
            String mob = mobPool.get(this.random.nextInt(mobPool.size()));
            objective.put("target", mob);
            int amount = this.random.nextInt(3) + 1;
            amount = (int)Math.ceil((double)amount * difficulty.objectiveMultiplier);
            objective.put("amount", Math.max(1, amount));
            return objective;
        }
        RandomGeneratorConfig.ObjectiveConfig objConfig = this.config.getObjectiveByName(objectiveName);
        if (objConfig == null) {
            this.plugin.getLogger().warning(String.format("Objective '%s' not found in config!", objectiveName));
            return null;
        }
        objective.put("type", objConfig.type());
        if (this.requiresOptions(objConfig.type())) {
            if (objConfig.supportsAny()) {
                if (objConfig.type().equalsIgnoreCase("command")) {
                    objective.put("command", "help");
                } else if (objConfig.type().equalsIgnoreCase("placeholder")) {
                    objective.put("placeholder", "player_level");
                } else {
                    objective.put("target", "ANY");
                }
            } else {
                if (objConfig.options().isEmpty()) {
                    this.plugin.getLogger().warning(String.format("Objective '%s' has no options configured!", objectiveName));
                    return null;
                }
                String option = objConfig.options().get(this.random.nextInt(objConfig.options().size()));
                if (objConfig.type().equalsIgnoreCase("command")) {
                    objective.put("command", option);
                } else if (objConfig.type().equalsIgnoreCase("placeholder")) {
                    objective.put("placeholder", option);
                } else {
                    objective.put("target", option);
                }
            }
        }
        int minAmount = objConfig.minAmount();
        int maxAmount = objConfig.maxAmount();
        if (objConfig.amountByDifficulty() != null && !objConfig.amountByDifficulty().isEmpty() && (difficultyRange = objConfig.amountByDifficulty().get(difficulty.name)) != null && difficultyRange.length >= 2) {
            minAmount = difficultyRange[0];
            maxAmount = difficultyRange[1];
        }
        int amount = this.random.nextInt(maxAmount - minAmount + 1) + minAmount;
        if (objConfig.amountByDifficulty() == null) {
            amount = (int)Math.ceil((double)amount * difficulty.objectiveMultiplier);
        }
        if (objConfig.type().equalsIgnoreCase("reachlevel")) {
            objective.put("level", Math.max(1, amount));
        } else {
            objective.put("amount", Math.max(1, amount));
        }
        return objective;
    }

    private boolean requiresOptions(String type) {
        return switch (type.toLowerCase()) {
            case "jump", "move", "sleep", "death", "reachlevel", "gainlevel", "level", "chat", "bowshoot", "projectile", "firework", "vehicle", "damage", "heal" -> false;
            default -> true;
        };
    }

    private List<String> generateQuestLore(TierManager.Tier tier, DifficultyManager.Difficulty difficulty, String questType) {
        List<RandomGeneratorConfig.LoreEntry> loreStructure;
        ArrayList<String> lore = new ArrayList<String>();
        if (!this.config.getLoreStyles().isEmpty()) {
            String selectedStyle = this.config.getLoreStyle();
            Object styleData = this.config.getLoreStyles().get(selectedStyle);
            if (styleData != null) {
                if (styleData instanceof List<?> styleLines) {
                    for (Object lineObj : styleLines) {
                        lore.add(this.replaceLorePlaceholders(String.valueOf(lineObj), tier, difficulty, questType));
                    }
                    return lore;
                }
                if (styleData instanceof Map<?, ?> tierVariants) {
                    List<?> tierLines = (List<?>) tierVariants.get(tier.name.toLowerCase());
                    if (tierLines == null || tierLines.isEmpty()) {
                        List<String> tierNames = this.plugin.getTierManager().getSortedTierNames();
                        for (String fallback : tierNames) {
                            tierLines = (List<?>) tierVariants.get(fallback);
                            if (tierLines != null && !tierLines.isEmpty()) {
                                break;
                            }
                        }
                    }
                    if (tierLines != null && !tierLines.isEmpty()) {
                        for (Object lineObj : tierLines) {
                            lore.add(this.replaceLorePlaceholders(String.valueOf(lineObj), tier, difficulty, questType));
                        }
                        return lore;
                    }
                }
            }
        }
        if (!(loreStructure = this.config.getLoreStructure()).isEmpty()) {
            for (RandomGeneratorConfig.LoreEntry entry : loreStructure) {
                Objects.requireNonNull(entry);
                if (entry instanceof RandomGeneratorConfig.LoreEntry.Fixed fixed) {
                    lore.add(this.replaceLorePlaceholders(fixed.line(), tier, difficulty, questType));
                    continue;
                }
                if (entry instanceof RandomGeneratorConfig.LoreEntry.RandomGroup randomGroup) {
                    if (randomGroup.options() == null || randomGroup.options().isEmpty()) continue;
                    String selectedLine = randomGroup.options().get(this.random.nextInt(randomGroup.options().size()));
                    lore.add(this.replaceLorePlaceholders(selectedLine, tier, difficulty, questType));
                    continue;
                }
                if (entry instanceof RandomGeneratorConfig.LoreEntry.TierBased tierBased) {
                    if (tierBased.tierEntries() == null || tier == null) continue;
                    List<String> tierLines = tierBased.tierEntries().get(tier.name.toLowerCase());
                    if (tierLines == null || tierLines.isEmpty()) {
                        List<String> tierNames = this.plugin.getTierManager().getSortedTierNames();
                        for (String fallback : tierNames) {
                            tierLines = tierBased.tierEntries().get(fallback);
                            if (tierLines != null && !tierLines.isEmpty()) break;
                        }
                    }
                    if (tierLines == null) continue;
                    for (String line : tierLines) {
                        lore.add(this.replaceLorePlaceholders(line, tier, difficulty, questType));
                    }
                }
            }
            return lore;
        }
        lore.add("&7Complete this quest for rewards!");
        lore.add("");
        lore.add("&7Progress: &f<progress>");
        lore.add("&7Objective: &f<objective>");
        lore.add("&7Amount: &f<amount>");
        return lore;
    }

    private String replaceLorePlaceholders(String line, TierManager.Tier tier, DifficultyManager.Difficulty difficulty, String questType) {
        if (line == null) {
            return "";
        }
        PlaceholderManager.PlaceholderContext context = new PlaceholderManager.PlaceholderContext().tier(tier).difficulty(difficulty);
        String result = this.placeholderManager.replacePlaceholders(line, context);
        if (questType != null) {
            result = result.replace("<type>", this.capitalize(questType));
        }
        return result;
    }

    private String generateDisplayName(Map<String, Object> firstObjective, TierManager.Tier tier, DifficultyManager.Difficulty difficulty) {
        String type = (String)firstObjective.get("type");
        List<String> templates = null;
        if (!this.config.getNameTemplates().isEmpty() && ((templates = this.config.getNameTemplates().get(type)) == null || templates.isEmpty())) {
            templates = this.config.getNameTemplates().get("generic");
        }
        if ((templates == null || templates.isEmpty()) && ((templates = this.config.getDisplayTemplatesByType().get(type)) == null || templates.isEmpty())) {
            templates = this.config.getGenericDisplayTemplates();
        }
        if (templates == null || templates.isEmpty()) {
            templates = List.of("&7[Quest] &f<type> Challenge");
        }
        String template = templates.get(this.random.nextInt(templates.size()));
        PlaceholderManager.PlaceholderContext context = new PlaceholderManager.PlaceholderContext().tier(tier).difficulty(difficulty);
        template = this.placeholderManager.replacePlaceholders(template, context);
        template = template.replace("<type>", type != null ? this.capitalize(type) : "Unknown");
        if (firstObjective.containsKey("amount")) {
            template = template.replace("<amount>", String.valueOf(firstObjective.get("amount")));
        }
        String targetValue = null;
        if (firstObjective.containsKey("entity")) {
            String entityName = this.formatName((String)firstObjective.get("entity"));
            template = template.replace("<entity>", entityName);
            targetValue = entityName;
            if (!firstObjective.containsKey("item")) {
                template = template.replace("<item>", entityName);
            }
        }
        if (firstObjective.containsKey("block")) {
            String blockName = this.formatName((String)firstObjective.get("block"));
            template = template.replace("<block>", blockName);
            targetValue = blockName;
            if (!firstObjective.containsKey("item")) {
                template = template.replace("<item>", blockName);
            }
        }
        if (firstObjective.containsKey("item")) {
            String itemName = this.formatName((String)firstObjective.get("item"));
            template = template.replace("<item>", itemName);
            targetValue = itemName;
        }
        if (firstObjective.containsKey("mob")) {
            String mobName = (String)firstObjective.get("mob");
            template = template.replace("<entity>", mobName);
            targetValue = mobName;
            if (!firstObjective.containsKey("item")) {
                template = template.replace("<item>", mobName);
            }
        }
        if (firstObjective.containsKey("target")) {
            String targetName;
            targetValue = targetName = this.formatName((String)firstObjective.get("target"));
        }
        if (targetValue != null) {
            template = template.replace("<target>", targetValue);
            template = template.replace("<entity>", targetValue);
            template = template.replace("<block>", targetValue);
            template = template.replace("<item>", targetValue);
        }
        return template;
    }

    private Map<String, Object> generateRewards(TierManager.Tier tier, DifficultyManager.Difficulty difficulty) {
        if (!this.config.getRewardTemplates().isEmpty()) {
            Map<String, Object> templatedReward = this.generateRewardsFromTemplates(tier, difficulty);
            if (!templatedReward.isEmpty()) {
                return templatedReward;
            }
        }
        List<RandomGeneratorConfig.TieredQuestReward> questPool;
        int questRewardRoll;
        List<RandomGeneratorConfig.TieredItemReward> itemPool;
        int[] moneyRange;
        LinkedHashMap<String, Object> rewards = new LinkedHashMap<String, Object>();
        int[] xpRange = this.config.getXpByTier().get(tier.name.toLowerCase());
        if (xpRange != null && xpRange.length >= 2) {
            int xp = this.random.nextInt(xpRange[1] - xpRange[0] + 1) + xpRange[0];
            xp = (int)Math.ceil((double)xp * difficulty.rewardMultiplier);
            rewards.put("xp", xp);
        }
        if ((moneyRange = this.config.getMoneyByTier().get(tier.name.toLowerCase())) != null && moneyRange.length >= 2) {
            int money = this.random.nextInt(moneyRange[1] - moneyRange[0] + 1) + moneyRange[0];
            money = (int)Math.ceil((double)money * difficulty.rewardMultiplier);
            rewards.put("money", money);
        }
        if (!(itemPool = this.config.getTieredItemRewards()).isEmpty()) {
            ArrayList<RandomGeneratorConfig.TieredItemReward> validItems = new ArrayList<RandomGeneratorConfig.TieredItemReward>();
            String tierNameLower = tier.name.toLowerCase();
            for (RandomGeneratorConfig.TieredItemReward itemReward : itemPool) {
                if (!itemReward.tiers().stream().anyMatch(t -> t.equalsIgnoreCase(tierNameLower)) || itemReward.minDifficulty() != null && !this.isEqualOrHarderDifficulty(difficulty, itemReward.minDifficulty())) continue;
                validItems.add(itemReward);
            }
            if (!validItems.isEmpty()) {
                List<RandomGeneratorConfig.TieredItemReward> selectedItems;
                String selectionMode = this.config.getItemSelectionMode();
                int minItems = this.config.getMinItems();
                int maxItems = this.config.getMaxItems();
                int numItems = this.random.nextInt(maxItems - minItems + 1) + minItems;
                numItems = Math.min(numItems, validItems.size());
                if (selectionMode == null) {
                    selectedItems = new ArrayList<>(validItems);
                    Collections.shuffle(selectedItems);
                    selectedItems = selectedItems.subList(0, numItems);
                } else {
                    switch (selectionMode) {
                        case "weighted": {
                            selectedItems = this.selectWeightedItems(validItems, numItems);
                            break;
                        }
                        case "all": {
                            selectedItems = validItems;
                            break;
                        }
                        default: {
                            selectedItems = new ArrayList<>(validItems);
                            Collections.shuffle(selectedItems);
                            selectedItems = selectedItems.subList(0, numItems);
                        }
                    }
                }
                ArrayList<Map<String, Object>> items = new ArrayList<>();
                for (RandomGeneratorConfig.TieredItemReward tieredItemReward : selectedItems) {
                    int amount = this.random.nextInt(tieredItemReward.maxAmount() - tieredItemReward.minAmount() + 1) + tieredItemReward.minAmount();
                    amount = (int)Math.ceil((double)amount * difficulty.rewardMultiplier);
                    LinkedHashMap<String, Object> item = new LinkedHashMap<String, Object>();
                    item.put("material", tieredItemReward.material());
                    item.put("amount", Math.max(1, amount));
                    if (!tieredItemReward.isCustomItem()) {
                        if (tieredItemReward.name() != null) {
                            item.put("name", tieredItemReward.name());
                        }
                        if (tieredItemReward.lore() != null && !tieredItemReward.lore().isEmpty()) {
                            item.put("lore", tieredItemReward.lore());
                        }
                        if (tieredItemReward.enchantments() != null && !tieredItemReward.enchantments().isEmpty()) {
                            item.put("enchantments", tieredItemReward.enchantments());
                        }
                        if (tieredItemReward.flags() != null && !tieredItemReward.flags().isEmpty()) {
                            item.put("flags", tieredItemReward.flags());
                        }
                        if (tieredItemReward.unbreakable() != null && tieredItemReward.unbreakable().booleanValue()) {
                            item.put("unbreakable", true);
                        }
                        if (tieredItemReward.customModelData() != null) {
                            item.put("custom-model-data", tieredItemReward.customModelData());
                        }
                    }
                    items.add(item);
                }
                if (!items.isEmpty()) {
                    rewards.put("items", items);
                }
            }
        }
        if (this.config.isQuestRewardsEnabled() && (questRewardRoll = this.random.nextInt(100)) < this.config.getQuestRewardChance() && !(questPool = this.config.getTieredQuestRewards()).isEmpty()) {
            ArrayList<RandomGeneratorConfig.TieredQuestReward> validQuests = new ArrayList<RandomGeneratorConfig.TieredQuestReward>();
            for (RandomGeneratorConfig.TieredQuestReward questReward : questPool) {
                if (!questReward.tiers().stream().anyMatch(t -> t.equalsIgnoreCase(tier.name)) || questReward.minDifficulty() != null && !this.isEqualOrHarderDifficulty(difficulty, questReward.minDifficulty())) continue;
                validQuests.add(questReward);
            }
            if (!validQuests.isEmpty()) {
                RandomGeneratorConfig.TieredQuestReward selectedQuest = (RandomGeneratorConfig.TieredQuestReward)validQuests.get(this.random.nextInt(validQuests.size()));
                int questChanceRoll = this.random.nextInt(100);
                if (questChanceRoll < selectedQuest.chance()) {
                    LinkedHashMap<String, Object> questReward = new LinkedHashMap<String, Object>();
                    questReward.put("quest-id", selectedQuest.questId());
                    questReward.put("chance", selectedQuest.chance());
                    rewards.put("quest", questReward);
                }
            }
        }
        return rewards;
    }

    private Map<String, Object> generateRewardsFromTemplates(TierManager.Tier tier, DifficultyManager.Difficulty difficulty) {
        List<RandomGeneratorConfig.RewardTemplate> templates = this.config.getRewardTemplates();
        if (templates.isEmpty()) {
            return Collections.emptyMap();
        }
        String tierName = tier.name.toLowerCase();
        ArrayList<RandomGeneratorConfig.RewardTemplate> valid = new ArrayList<RandomGeneratorConfig.RewardTemplate>();
        int totalWeight = 0;
        for (RandomGeneratorConfig.RewardTemplate template : templates) {
            boolean tierMatch = template.tiers().stream().anyMatch(t -> t.equalsIgnoreCase(tierName));
            if (!tierMatch) {
                continue;
            }
            if (template.minDifficulty() != null && !this.isEqualOrHarderDifficulty(difficulty, template.minDifficulty())) {
                continue;
            }
            valid.add(template);
            totalWeight += Math.max(1, template.weight());
        }
        if (valid.isEmpty()) {
            return Collections.emptyMap();
        }

        RandomGeneratorConfig.RewardTemplate selected = null;
        if (totalWeight <= 0) {
            selected = valid.get(this.random.nextInt(valid.size()));
        } else {
            int roll = this.random.nextInt(totalWeight);
            int current = 0;
            for (RandomGeneratorConfig.RewardTemplate template : valid) {
                current += Math.max(1, template.weight());
                if (roll >= current) {
                    continue;
                }
                selected = template;
                break;
            }
            if (selected == null) {
                selected = valid.get(0);
            }
        }
        return this.normalizeTemplateReward(selected.reward(), difficulty);
    }

    private Map<String, Object> normalizeTemplateReward(Map<String, Object> source, DifficultyManager.Difficulty difficulty) {
        LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
        if (source == null || source.isEmpty()) {
            return out;
        }

        if (source.containsKey("xp")) {
            int xp = this.resolveIntAmount(source.get("xp"), 0);
            xp = (int)Math.ceil((double)xp * difficulty.rewardMultiplier);
            if (xp > 0) {
                out.put("xp", xp);
            }
        }
        if (source.containsKey("xp-chance")) {
            out.put("xp-chance", source.get("xp-chance"));
        }

        if (source.containsKey("money")) {
            int money = this.resolveIntAmount(source.get("money"), 0);
            money = (int)Math.ceil((double)money * difficulty.rewardMultiplier);
            if (money > 0) {
                out.put("money", money);
            }
        }
        if (source.containsKey("money-chance")) {
            out.put("money-chance", source.get("money-chance"));
        }

        if (source.containsKey("sigils")) {
            int sigils = this.resolveIntAmount(source.get("sigils"), 0);
            sigils = (int)Math.ceil((double)sigils * difficulty.rewardMultiplier);
            if (sigils > 0) {
                out.put("sigils", sigils);
            }
        }
        if (source.containsKey("sigils-chance")) {
            out.put("sigils-chance", source.get("sigils-chance"));
        }

        if (source.containsKey("items") && source.get("items") instanceof List<?> rawItems) {
            ArrayList<Map<String, Object>> normalizedItems = new ArrayList<Map<String, Object>>();
            for (Object rawItem : rawItems) {
                if (!(rawItem instanceof Map<?, ?> rawMap)) {
                    continue;
                }
                LinkedHashMap<String, Object> itemOut = new LinkedHashMap<String, Object>();
                for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                    itemOut.put(String.valueOf(entry.getKey()), entry.getValue());
                }
                if (itemOut.containsKey("amount")) {
                    int amount = this.resolveIntAmount(itemOut.get("amount"), 1);
                    amount = (int)Math.ceil((double)amount * difficulty.rewardMultiplier);
                    itemOut.put("amount", Math.max(1, amount));
                }
                normalizedItems.add(itemOut);
            }
            if (!normalizedItems.isEmpty()) {
                out.put("items", normalizedItems);
            }
        }

        if (source.containsKey("commands")) {
            out.put("commands", source.get("commands"));
        }
        if (source.containsKey("command-chance")) {
            out.put("command-chance", source.get("command-chance"));
        }
        if (source.containsKey("quest")) {
            out.put("quest", source.get("quest"));
        }

        return out;
    }

    private int resolveIntAmount(Object raw, int defaultValue) {
        if (raw == null) {
            return defaultValue;
        }
        if (raw instanceof Number n) {
            return n.intValue();
        }
        if (raw instanceof List<?> list && !list.isEmpty()) {
            if (list.size() == 1 && list.get(0) instanceof Number n) {
                return n.intValue();
            }
            if (list.size() >= 2 && list.get(0) instanceof Number min && list.get(1) instanceof Number max) {
                int minVal = min.intValue();
                int maxVal = max.intValue();
                if (maxVal < minVal) {
                    int temp = minVal;
                    minVal = maxVal;
                    maxVal = temp;
                }
                return this.random.nextInt(maxVal - minVal + 1) + minVal;
            }
        }
        return defaultValue;
    }

    private Map<String, Object> generateConditions(TierManager.Tier tier, DifficultyManager.Difficulty difficulty) {
        LinkedHashMap<String, Object> conditions = new LinkedHashMap<String, Object>();
        if (!this.config.isConditionsEnabled()) {
            return conditions;
        }
        String tierName = tier.name.toLowerCase();
        String diffName = difficulty.name.toLowerCase();
        block28: for (Map.Entry<String, RandomGeneratorConfig.ConditionConfig> entry : this.config.getConditionConfigs().entrySet()) {
            String condType = entry.getKey();
            RandomGeneratorConfig.ConditionConfig condConfig = entry.getValue();
            if (this.random.nextInt(100) >= condConfig.chance()) continue;
            switch (condType) {
                case "min-level": {
                    int level = this.getNumericValue(condConfig.byTier(), tierName, 0);
                    if (level <= 0) {
                        level = this.getNumericValue(condConfig.byDifficulty(), diffName, 0);
                    }
                    if (level <= 0) continue block28;
                    conditions.put("min-level", level);
                    break;
                }
                case "max-level": {
                    int n;
                    Number n2;
                    Object defaultVal = condConfig.defaultValue();
                    if (defaultVal instanceof Number) {
                        n2 = (Number)defaultVal;
                        n = n2.intValue();
                    } else {
                        n = 100;
                    }
                    int maxLevel = n;
                    conditions.put("max-level", maxLevel);
                    break;
                }
                case "min-money": {
                    int money = this.getNumericValue(condConfig.byTier(), tierName, 0);
                    if (money <= 0) continue block28;
                    conditions.put("min-money", money);
                    break;
                }
                case "world": {
                    List<String> worlds = condConfig.options();
                    if (worlds == null || worlds.isEmpty()) continue block28;
                    conditions.put("world", worlds.get(this.random.nextInt(worlds.size())));
                    break;
                }
                case "gamemode": {
                    List<String> modes = condConfig.options();
                    if (modes == null || modes.isEmpty()) continue block28;
                    conditions.put("gamemode", modes.get(this.random.nextInt(modes.size())));
                    break;
                }
                case "time": {
                    List<String> times = condConfig.options();
                    if (times == null || times.isEmpty()) continue block28;
                    conditions.put("time", times.get(this.random.nextInt(times.size())));
                    break;
                }
                case "permission": {
                    String permStr;
                    Object perm = condConfig.byTier().get(tierName);
                    if (!(perm instanceof String) || (permStr = (String)perm).isEmpty()) continue block28;
                    conditions.put("permission", permStr);
                    break;
                }
                case "cost": {
                    int cost = this.getNumericValue(condConfig.byTier(), tierName, 0);
                    if (cost <= 0) continue block28;
                    cost = (int)Math.ceil((double)cost * difficulty.rewardMultiplier);
                    conditions.put("cost", cost);
                    break;
                }
                case "item": {
                    String[] parts;
                    String itemStr;
                    Object itemVal = condConfig.byTier().get(tierName);
                    if (!(itemVal instanceof String) || (itemStr = (String)itemVal).isEmpty() || (parts = itemStr.split(":")).length != 2) continue block28;
                    LinkedHashMap<String, Object> itemCond = new LinkedHashMap<String, Object>();
                    itemCond.put("material", parts[0]);
                    try {
                        itemCond.put("amount", Integer.parseInt(parts[1]));
                    }
                    catch (NumberFormatException e) {
                        itemCond.put("amount", 1);
                    }
                    if (condConfig.consumeItem()) {
                        itemCond.put("consume", true);
                    }
                    conditions.put("item", itemCond);
                    break;
                }
                case "active-limit": {
                    int n;
                    Number n2;
                    Object defaultVal = condConfig.defaultValue();
                    if (defaultVal instanceof Number) {
                        n2 = (Number)defaultVal;
                        n = n2.intValue();
                    } else {
                        n = 3;
                    }
                    int limit = n;
                    conditions.put("active-limit", limit);
                    break;
                }
                case "placeholder": {
                    List<String> expressions = condConfig.options();
                    if (expressions == null || expressions.isEmpty()) break;
                    conditions.put("placeholder", expressions.get(this.random.nextInt(expressions.size())));
                }
            }
        }
        return conditions;
    }

    private int getNumericValue(Map<String, Object> map, String key, int defaultValue) {
        if (map == null || !map.containsKey(key)) {
            return defaultValue;
        }
        Object value = map.get(key);
        if (value instanceof Number) {
            Number n = (Number)value;
            return n.intValue();
        }
        return defaultValue;
    }

    private boolean isSimilarQuestExists(List<Map<String, Object>> objectives) {
        String target;
        if (objectives == null || objectives.isEmpty()) {
            return false;
        }
        Map<String, Object> firstObj = objectives.get(0);
        String type = (String)firstObj.get("type");
        String string = target = firstObj.containsKey("target") ? String.valueOf(firstObj.get("target")) : null;
        if (type == null) {
            return false;
        }
        try {
            File generatedFile = new File(this.plugin.getDataFolder(), this.config.getSaveLocation());
            if (!generatedFile.exists()) {
                return false;
            }
            YamlConfiguration generatedConfig = YamlConfiguration.loadConfiguration((File)generatedFile);
            for (String questId : generatedConfig.getKeys(false)) {
                String existingTarget;
                Object firstExisting;
                List existingObjectives = generatedConfig.getList(questId + ".objectives");
                if (existingObjectives == null || existingObjectives.isEmpty() || !((firstExisting = existingObjectives.get(0)) instanceof Map)) continue;
                Map existingObj = (Map)firstExisting;
                String existingType = String.valueOf(existingObj.get("type"));
                String string2 = existingTarget = existingObj.containsKey("target") ? String.valueOf(existingObj.get("target")) : null;
                if (!type.equalsIgnoreCase(existingType) || (target != null || existingTarget != null) && (target == null || !target.equalsIgnoreCase(existingTarget))) continue;
                return true;
            }
        }
        catch (Exception e) {
            this.plugin.getLogger().fine(() -> "[Generator] Error checking for similar quests: " + e.getMessage());
        }
        return false;
    }

    private List<RandomGeneratorConfig.TieredItemReward> selectWeightedItems(List<RandomGeneratorConfig.TieredItemReward> items, int count) {
        ArrayList<RandomGeneratorConfig.TieredItemReward> selected = new ArrayList<RandomGeneratorConfig.TieredItemReward>();
        ArrayList<RandomGeneratorConfig.TieredItemReward> pool = new ArrayList<RandomGeneratorConfig.TieredItemReward>(items);
        block0: for (int i = 0; i < count && !pool.isEmpty(); ++i) {
            int totalWeight = pool.stream().mapToInt(item -> item.weight()).sum();
            int randomWeight = this.random.nextInt(totalWeight);
            int currentWeight = 0;
            for (RandomGeneratorConfig.TieredItemReward item2 : pool) {
                if (randomWeight >= (currentWeight += item2.weight())) continue;
                selected.add(item2);
                pool.remove(item2);
                continue block0;
            }
        }
        return selected;
    }

    private boolean isEqualOrHarderDifficulty(DifficultyManager.Difficulty current, String minDifficultyName) {
        DifficultyManager.Difficulty minDiff = this.plugin.getDifficultyManager().getDifficulty(minDifficultyName);
        if (minDiff == null) {
            return true;
        }
        return current.rewardMultiplier >= minDiff.rewardMultiplier;
    }

    private String generateUniqueId(String questType, String tierName, String difficultyName, String playerName) {
        String id;
        File generatedFile = new File(this.plugin.getDataFolder(), this.config.getSaveLocation());
        YamlConfiguration generatedConfig = YamlConfiguration.loadConfiguration((File)generatedFile);
        Map<String, String> formats = this.config.getInternalNameFormats();
        Object format = formats.getOrDefault(questType, formats.get("default"));
        if (format == null || ((String)format).isEmpty()) {
            String legacyFormat = this.config.getInternalNameFormat();
            format = legacyFormat != null && !legacyFormat.isEmpty() ? legacyFormat : this.config.getIdPrefix() + "%id%";
        }
        int counter = 1;
        if (questType != null && questType.equals("sequence")) {
            for (String key : generatedConfig.getKeys(false)) {
                if (!key.startsWith("seq_")) continue;
                ++counter;
            }
        }
        int attempts = 0;
        int maxAttempts = 1000;
        do {
            long timestamp = System.currentTimeMillis();
            int randomNum = this.random.nextInt(10000);
            id = ((String)format).replace("<id>", String.valueOf(timestamp % 100000L) + "_" + randomNum).replace("<tier>", tierName != null ? tierName : "unknown").replace("<diff>", difficultyName != null ? difficultyName : "unknown").replace("<type>", questType != null ? questType : "unknown").replace("<player>", playerName != null ? playerName : "none").replace("<counter>", String.valueOf(counter)).replace("<objective>", "quest");
            ++counter;
            if (++attempts < maxAttempts) continue;
            this.plugin.getLogger().log(Level.SEVERE, "[Generator] Failed to generate unique quest ID after {0} attempts!", maxAttempts);
            return "fallback_" + UUID.randomUUID().toString().substring(0, 8);
        } while (generatedConfig.contains(id) || this.plugin.getQuestManager().questExists(id));
        return id;
    }

    private boolean saveQuest(String questId, Map<String, Object> questData) {
        try {
            File generatedFile;
            ConfigNormalizer.normalizeGeneratedQuest(questData);
            if (!ConfigNormalizer.validateStructure(questId, questData)) {
                this.plugin.getLogger().warning(() -> String.format("[Generator] Quest '%s' failed structure validation but will be saved anyway", questId));
            }
            if (!(generatedFile = new File(this.plugin.getDataFolder(), this.config.getSaveLocation())).exists()) {
                generatedFile.createNewFile();
            }
            YamlConfiguration generatedConfig = YamlConfiguration.loadConfiguration((File)generatedFile);
            for (Map.Entry<String, Object> entry : questData.entrySet()) {
                generatedConfig.set(questId + "." + entry.getKey(), entry.getValue());
            }
            YamlUtil.atomicSave((FileConfiguration)generatedConfig, generatedFile);
            this.plugin.getQuestManager().loadQuests();
            return true;
        }
        catch (IOException e) {
            this.plugin.getLogger().severe(() -> "Failed to save generated quest: " + e.getMessage());
            return false;
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private String formatName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return Arrays.stream(name.split("_")).map(this::capitalize).reduce((a, b) -> a + " " + b).orElse(name);
    }

    private String selectQuestPaperMaterial(TierManager.Tier tier) {
        String mode = this.config.getMaterialSelectionMode();
        if (mode == null) {
            mode = "default";
        }
        return switch (mode.toLowerCase()) {
            case "random" -> {
                List<RandomGeneratorConfig.WeightedMaterial> pool = this.config.getRandomMaterialPool();
                if (pool.isEmpty()) {
                    yield this.config.getDefaultMaterial();
                }
                int totalWeight = this.config.getTotalMaterialWeight();
                int randomValue = this.random.nextInt(totalWeight);
                int currentWeight = 0;
                for (RandomGeneratorConfig.WeightedMaterial wm : pool) {
                    if (randomValue >= (currentWeight += wm.weight())) continue;
                    yield wm.material();
                }
                yield pool.get(0).material();
            }
            case "tier-based", "tier_based" -> {
                Map<String, List<String>> tierMaterials = this.config.getTierBasedMaterials();
                List<String> materials = tierMaterials.get(tier.name.toLowerCase());
                if (materials == null || materials.isEmpty()) {
                    yield this.config.getDefaultMaterial();
                }
                yield materials.get(this.random.nextInt(materials.size()));
            }
            default -> this.config.getDefaultMaterial();
        };
    }

    @Override
    public boolean isEnabled() {
        return this.config.isEnabled();
    }

    @Override
    public List<String> getAllowedTypes() {
        return this.config.getAllowedTypes();
    }

    @Override
    public int getMaxBatchGenerate() {
        return this.plugin.getConfig().getInt("max-batch-generate", 25);
    }

    @Override
    public long getCooldownSeconds() {
        return this.plugin.getConfig().getInt("generate-cooldown", 0);
    }

    @Override
    public boolean isOnCooldown(UUID playerUUID) {
        long cooldownSeconds = this.getCooldownSeconds();
        if (cooldownSeconds <= 0L) {
            return false;
        }
        Long lastUsed = this.cooldowns.get(playerUUID);
        if (lastUsed == null) {
            return false;
        }
        long elapsed = (System.currentTimeMillis() - lastUsed) / 1000L;
        return elapsed < cooldownSeconds;
    }

    @Override
    public long getCooldownRemaining(UUID playerUUID) {
        long cooldownSeconds = this.getCooldownSeconds();
        if (cooldownSeconds <= 0L) {
            return 0L;
        }
        Long lastUsed = this.cooldowns.get(playerUUID);
        if (lastUsed == null) {
            return 0L;
        }
        long elapsed = (System.currentTimeMillis() - lastUsed) / 1000L;
        return Math.max(0L, cooldownSeconds - elapsed);
    }

    @Override
    public void startCooldown(UUID playerUUID) {
        this.cooldowns.put(playerUUID, System.currentTimeMillis());
    }

    @Override
    public void reload() {
        this.config.loadConfig();
        this.cooldowns.clear();
    }
}

