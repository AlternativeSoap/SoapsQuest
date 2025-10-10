package com.soaps.quest.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.managers.DifficultyManager;
import com.soaps.quest.managers.TierManager;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import com.soaps.quest.quests.QuestTier;

/**
 * Central placeholder management system for all quest-related text.
 * Handles global placeholders used across messages.yml, random-generator.yml, quests.yml, and all lore/display text.
 * 
 * PLACEHOLDER FORMATS:
 * - <placeholder>    - INTERNAL SoapsQuest placeholders (use this format everywhere)
 * - %placeholder%    - EXTERNAL PlaceholderAPI placeholders (reserved for future PlaceholderAPI integration)
 * 
 * Supported Global Placeholders (work everywhere - messages.yml, quests.yml, random-generator.yml):
 * 
 * Basic Placeholders:
 * - <prefix>         - Plugin prefix from messages.yml
 * - <player>         - Player name
 * - <quest>          - Quest display name
 * - <objective>      - Detailed objective description (e.g., "Kill 10 Zombies")
 * - <type>           - Quest or objective type (e.g., "single", "multi", "sequence")
 * - <progress>       - Current progress (e.g., "5/10")
 * - <amount>         - Required amount (e.g., "10")
 * - <index>          - Reward index (for multiple rewards)
 * 
 * Tier Placeholders:
 * - <tier>           - Tier display name with color (e.g., "&9Rare")
 * - <tier_color>     - Tier color code only (e.g., "&9")
 * - <tier_prefix>    - Tier prefix with color (e.g., "&9[RARE]")
 * 
 * Difficulty Placeholders:
 * - <difficulty>     - Difficulty display name with color (e.g., "&6Hard")
 * - <difficulty_color> - Difficulty color code (if defined)
 * 
 * Objective-Specific Placeholders (context-dependent):
 * - <entity>         - Entity type (kill, tame, shear objectives)
 * - <block>          - Block type (break, place, interact objectives)
 * - <item>           - Item type (collect, craft, smelt, fish, consume, trade objectives)
 * - <mob>            - MythicMob name (kill_mythicmob objective)
 * 
 * Note: Objective-specific placeholders only work when an objective is in context.
 * Example: In random-generator.yml display templates, <entity> works for "kill" objectives.
 */
public class PlaceholderManager {
    private final SoapsQuest plugin;
    
    public PlaceholderManager(SoapsQuest plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Replace all placeholders in a string with their values.
     * This is the main entry point for placeholder replacement.
     * 
     * @param text Text containing placeholders
     * @param context Context object containing relevant data
     * @return Text with placeholders replaced
     */
    public String replacePlaceholders(String text, PlaceholderContext context) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        String result = text;
        
        // Replace global placeholders
        result = replaceGlobalPlaceholders(result);
        
        // Replace player placeholders
        result = replacePlayerPlaceholders(result, context);
        
        // Replace quest placeholders
        result = replaceQuestPlaceholders(result, context);
        
        // Replace objective placeholders
        result = replaceObjectivePlaceholders(result, context);
        
        // Replace objective-specific placeholders (entity, block, item, mob)
        result = replaceObjectiveSpecificPlaceholders(result, context);
        
        // Replace tier placeholders
        result = replaceTierPlaceholders(result, context);
        
        // Replace difficulty placeholders
        result = replaceDifficultyPlaceholders(result, context);
        
        // NOTE: %placeholder% format is RESERVED for PlaceholderAPI integration
        // Do not use % format for internal placeholders - use <> format only
        
        // Return with & codes intact - caller will handle color translation
        return result;
    }
    
    /**
     * Replace global placeholders like <prefix>
     */
    private String replaceGlobalPlaceholders(String text) {
        // Get prefix from messages.yml (handle both config and messages.yml)
        String prefix = null;
        if (plugin.getConfig().isConfigurationSection("messages")) {
            prefix = plugin.getConfig().getString("messages.prefix");
        }
        // If not found, try to get directly from config root as fallback
        if (prefix == null) {
            prefix = plugin.getConfig().getString("prefix");
        }
        if (prefix == null) {
            prefix = "&7[&bSoapsQuest&7]";
        }
        return text.replace("<prefix>", prefix);
    }
    
    /**
     * Replace player-related placeholders
     */
    private String replacePlayerPlaceholders(String text, PlaceholderContext context) {
        if (context.player != null) {
            text = text.replace("<player>", context.player.getName());
        }
        return text;
    }
    
    /**
     * Replace quest-related placeholders
     */
    private String replaceQuestPlaceholders(String text, PlaceholderContext context) {
        if (context.quest != null) {
            text = text.replace("<quest>", context.quest.getDisplay());
            text = text.replace("<type>", context.quest.getClass().getSimpleName().replace("Quest", ""));
        }
        
        if (context.questType != null) {
            text = text.replace("<type>", capitalize(context.questType));
        }
        
        return text;
    }
    
    /**
     * Replace objective-related placeholders
     */
    private String replaceObjectivePlaceholders(String text, PlaceholderContext context) {
        // Progress and amount
        if (context.progress != null) {
            text = text.replace("<progress>", String.valueOf(context.progress.getCurrentProgress()));
            text = text.replace("<amount>", String.valueOf(context.progress.getRequiredAmount()));
            
            // Combined progress display
            String progressDisplay = context.progress.getCurrentProgress() + "/" + context.progress.getRequiredAmount();
            text = text.replace("<progress_display>", progressDisplay);
        } else {
            // Fallback to individual values
            if (context.currentProgress >= 0) {
                text = text.replace("<progress>", String.valueOf(context.currentProgress));
            }
            if (context.requiredAmount > 0) {
                text = text.replace("<amount>", String.valueOf(context.requiredAmount));
            }
        }
        
        // Objective description
        if (context.objective != null) {
            text = text.replace("<objective>", context.objective.getDescription());
        } else if (context.objectiveDescription != null) {
            text = text.replace("<objective>", context.objectiveDescription);
        }
        
        // Index for rewards
        if (context.index >= 0) {
            text = text.replace("<index>", String.valueOf(context.index));
        }
        
        return text;
    }
    
    /**
     * Replace tier-related placeholders
     */
    private String replaceTierPlaceholders(String text, PlaceholderContext context) {
        TierManager.Tier tier = context.tier;
        
        // Try to get tier from quest if not provided
        if (tier == null && context.quest != null) {
            // Convert legacy QuestTier enum to TierManager.Tier
            QuestTier questTier = context.quest.getTier();
            if (questTier != null) {
                tier = plugin.getTierManager().getTier(questTier.name().toLowerCase());
            }
        }
        
        // Try to get tier by name if provided
        if (tier == null && context.tierName != null) {
            tier = plugin.getTierManager().getTier(context.tierName);
        }
        
        if (tier != null) {
            // New format placeholders
            text = text.replace("<tier>", tier.display);
            text = text.replace("<tier_color>", tier.color);
            text = text.replace("<tier_prefix>", tier.prefix);
            text = text.replace("<tier_display>", tier.display);
        }
        
        return text;
    }
    
    /**
     * Replace difficulty-related placeholders
     */
    private String replaceDifficultyPlaceholders(String text, PlaceholderContext context) {
        DifficultyManager.Difficulty difficulty = context.difficulty;
        
        // Try to get difficulty by name if provided
        if (difficulty == null && context.difficultyName != null) {
            difficulty = plugin.getDifficultyManager().getDifficulty(context.difficultyName);
        }
        
        if (difficulty != null) {
            text = text.replace("<difficulty>", difficulty.display);
            // Note: Difficulty doesn't have a color field currently, use display color if needed
            text = text.replace("<difficulty_color>", "&f");
            text = text.replace("<difficulty_display>", difficulty.display);
        }
        
        return text;
    }
    
    /**
     * Replace objective-specific placeholders that depend on the objective type.
     * These placeholders only work when an objective is provided in the context.
     * 
     * Supported placeholders:
     * - <entity> - Entity type (for kill, tame, shear objectives)
     * - <block>  - Block type (for break, place, interact objectives)
     * - <item>   - Item type (for collect, craft, smelt, fish, consume, trade objectives)
     * - <mob>    - MythicMob name (for kill_mythicmob objective)
     */
    private String replaceObjectiveSpecificPlaceholders(String text, PlaceholderContext context) {
        // Only process if we have objective data
        if (context.objectiveData == null || context.objectiveData.isEmpty()) {
            return text;
        }
        
        // Get the objective type to determine which placeholders are valid
        String objectiveType = context.objectiveData.get("type");
        if (objectiveType == null) {
            return text;
        }
        
        // Replace entity placeholder (for kill, tame, shear objectives)
        if (text.contains("<entity>") && context.objectiveData.containsKey("entity")) {
            String entity = context.objectiveData.get("entity");
            if (entity != null) {
                // Format entity name nicely (ZOMBIE -> Zombie, IRON_GOLEM -> Iron Golem)
                entity = formatName(entity);
                text = text.replace("<entity>", entity);
            }
        }
        
        // Replace block placeholder (for break, place, interact objectives)
        if (text.contains("<block>") && context.objectiveData.containsKey("block")) {
            String block = context.objectiveData.get("block");
            if (block != null) {
                // Format block name nicely (STONE -> Stone, IRON_ORE -> Iron Ore)
                block = formatName(block);
                text = text.replace("<block>", block);
            }
        }
        
        // Replace item placeholder (for collect, craft, smelt, fish, consume, trade objectives)
        if (text.contains("<item>") && context.objectiveData.containsKey("item")) {
            String item = context.objectiveData.get("item");
            if (item != null) {
                // Format item name nicely (DIAMOND_SWORD -> Diamond Sword)
                item = formatName(item);
                text = text.replace("<item>", item);
            }
        }
        
        // Replace mob placeholder (for kill_mythicmob objective)
        if (text.contains("<mob>") && context.objectiveData.containsKey("mob")) {
            String mob = context.objectiveData.get("mob");
            if (mob != null) {
                text = text.replace("<mob>", mob);
            }
        }
        
        return text;
    }
    
    /**
     * Format a material/entity name to be more readable.
     * Example: IRON_ORE -> Iron Ore, ZOMBIE -> Zombie
     */
    private String formatName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        
        // Split by underscores and capitalize each word
        String[] parts = name.toLowerCase().split("_");
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                result.append(" ");
            }
            result.append(capitalize(parts[i]));
        }
        
        return result.toString();
    }
    
    /**
     * Capitalize first letter of a string
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    /**
     * Context object to pass data for placeholder replacement.
     * Use the builder pattern for easy construction.
     */
    public static class PlaceholderContext {
        private Player player;
        private Quest quest;
        private Objective objective;
        private QuestProgress progress;
        private TierManager.Tier tier;
        private String tierName;
        private DifficultyManager.Difficulty difficulty;
        private String difficultyName;
        private String questType;
        private String objectiveDescription;
        private int currentProgress = -1;
        private int requiredAmount = -1;
        private int index = -1;
        private Map<String, String> objectiveData;  // For objective-specific placeholders (entity, block, item, mob)
        
        public PlaceholderContext() {
        }
        
        public PlaceholderContext player(Player player) {
            this.player = player;
            return this;
        }
        
        public PlaceholderContext quest(Quest quest) {
            this.quest = quest;
            return this;
        }
        
        public PlaceholderContext objective(Objective objective) {
            this.objective = objective;
            return this;
        }
        
        public PlaceholderContext progress(QuestProgress progress) {
            this.progress = progress;
            return this;
        }
        
        public PlaceholderContext tier(TierManager.Tier tier) {
            this.tier = tier;
            return this;
        }
        
        public PlaceholderContext tierName(String tierName) {
            this.tierName = tierName;
            return this;
        }
        
        public PlaceholderContext difficulty(DifficultyManager.Difficulty difficulty) {
            this.difficulty = difficulty;
            return this;
        }
        
        public PlaceholderContext difficultyName(String difficultyName) {
            this.difficultyName = difficultyName;
            return this;
        }
        
        public PlaceholderContext questType(String questType) {
            this.questType = questType;
            return this;
        }
        
        public PlaceholderContext objectiveDescription(String objectiveDescription) {
            this.objectiveDescription = objectiveDescription;
            return this;
        }
        
        public PlaceholderContext currentProgress(int currentProgress) {
            this.currentProgress = currentProgress;
            return this;
        }
        
        public PlaceholderContext requiredAmount(int requiredAmount) {
            this.requiredAmount = requiredAmount;
            return this;
        }
        
        public PlaceholderContext index(int index) {
            this.index = index;
            return this;
        }
        
        public PlaceholderContext objectiveData(Map<String, String> objectiveData) {
            this.objectiveData = objectiveData;
            return this;
        }
        
        public PlaceholderContext addObjectiveData(String key, String value) {
            if (this.objectiveData == null) {
                this.objectiveData = new HashMap<>();
            }
            this.objectiveData.put(key, value);
            return this;
        }
    }
}
