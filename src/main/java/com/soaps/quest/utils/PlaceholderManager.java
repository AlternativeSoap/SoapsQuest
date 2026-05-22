/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  org.bukkit.entity.Player
 */
package com.soaps.quest.utils;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.managers.DifficultyManager;
import com.soaps.quest.managers.TierManager;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import com.soaps.quest.utils.ColorUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class PlaceholderManager {
    private static SoapsQuest staticPlugin = null;
    private final SoapsQuest plugin;

    public static void setPlugin(SoapsQuest pluginInstance) {
        staticPlugin = pluginInstance;
    }

    public static SoapsQuest getPlugin() {
        return staticPlugin;
    }

    public static Component parseQuestPlaceholders(String text, Quest quest) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        String replaced = PlaceholderManager.replaceQuestPlaceholders(text, quest);
        return ColorUtil.colorize(replaced);
    }

    public static String replaceQuestPlaceholders(String text, Quest quest) {
        Object descPreview;
        if (text == null || quest == null) {
            return text != null ? text : "";
        }
        String result = text.replace("<quest_id>", quest.getQuestId()).replace("<quest_name>", PlaceholderManager.escapeForMiniMessage(quest.getDisplay())).replace("<quest_display>", PlaceholderManager.escapeForMiniMessage(quest.getDisplay())).replace("<quest_type>", quest.hasObjectives() ? "OBJECTIVES" : "CUSTOM").replace("<quest_difficulty>", quest.getDifficulty() != null ? quest.getDifficulty() : "normal").replace("<quest_tier>", quest.getTier() != null ? quest.getTier() : "common").replace("<quest_material>", quest.getMaterial() != null ? quest.getMaterial().name() : "PAPER");
        int objectiveCount = quest.getObjectives() != null ? quest.getObjectives().size() : 0;
        result = result.replace("<quest_objective_count>", String.valueOf(objectiveCount));
        result = result.replace("<objective_count>", String.valueOf(objectiveCount));
        int conditionCount = 0;
        if (quest.getConditions() != null) {
            conditionCount = quest.getConditions().getKeys(false).size();
        }
        result = result.replace("<quest_condition_count>", String.valueOf(conditionCount));
        result = result.replace("<condition_count>", String.valueOf(conditionCount));
        int rewardCount = 0;
        if (staticPlugin != null) {
            rewardCount = staticPlugin.getRewardManager().getRewardList(quest.getQuestId()).size();
        }
        result = result.replace("<quest_reward_count>", String.valueOf(rewardCount));
        result = result.replace("<reward_count>", String.valueOf(rewardCount));
        boolean locked = quest.isLockToPlayer();
        result = result.replace("<quest_locked>", String.valueOf(locked));
        result = result.replace("<lock_status>", locked ? "&cLocked" : "&aUnlocked");
        String origin = "Manual";
        if (staticPlugin != null) {
            origin = staticPlugin.getQuestManager().isGeneratedQuest(quest.getQuestId()) ? "Generated" : "Manual";
        }
        result = result.replace("<quest_origin>", origin);
        List<String> description = quest.getCustomLore();
        Object object = descPreview = description != null && !description.isEmpty() ? description.get(0) : "No description";
        if (((String)descPreview).length() > 50) {
            descPreview = ((String)descPreview).substring(0, 47) + "...";
        }
        result = result.replace("<quest_description>", PlaceholderManager.escapeForMiniMessage((String)descPreview));
        return result;
    }

    public static List<Component> parseQuestPlaceholdersList(List<String> lines, Quest quest) {
        if (lines == null || lines.isEmpty()) {
            return List.of();
        }
        return lines.stream().map(line -> PlaceholderManager.parseQuestPlaceholders(line, quest)).collect(Collectors.toList());
    }

    public static List<String> replaceQuestPlaceholdersList(List<String> lines, Quest quest) {
        if (lines == null || lines.isEmpty()) {
            return List.of();
        }
        return lines.stream().map(line -> PlaceholderManager.replaceQuestPlaceholders(line, quest)).collect(Collectors.toList());
    }

    public static String modernizePlaceholders(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("%quest_name%", "<quest_name>").replace("%quest_display%", "<quest_display>").replace("%quest_id%", "<quest_id>").replace("%quest_type%", "<quest_type>").replace("%quest_difficulty%", "<quest_difficulty>").replace("%quest_tier%", "<quest_tier>").replace("%objective_count%", "<quest_objective_count>").replace("%condition_count%", "<quest_condition_count>").replace("%reward_count%", "<quest_reward_count>").replace("%quest_description%", "<quest_description>");
    }

    private static String escapeForMiniMessage(String text) {
        boolean hasMiniMessageTags;
        if (text == null) {
            return "";
        }
        boolean bl = hasMiniMessageTags = text.contains("<gradient") || text.contains("<#") || text.contains("<color") || text.contains("<bold") || text.contains("<italic") || text.contains("<underlined") || text.contains("<strikethrough") || text.contains("<obfuscated") || text.contains("<reset") || text.contains("<rainbow") || text.contains("<transition") || text.contains("<hover") || text.contains("<click") || text.contains("<font") || text.contains("<red") || text.contains("<green") || text.contains("<blue") || text.contains("<yellow") || text.contains("<aqua") || text.contains("<gold") || text.contains("<gray") || text.contains("<dark_") || text.contains("<light_") || text.contains("<white") || text.contains("<black");
        if (hasMiniMessageTags) {
            return text;
        }
        return text.replace("<", "\\<").replace(">", "\\>");
    }

    public PlaceholderManager(SoapsQuest plugin) {
        this.plugin = plugin;
    }

    public String replacePlaceholders(String text, PlaceholderContext context) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String result = text;
        result = this.replaceGlobalPlaceholders(result);
        result = this.replacePlayerPlaceholders(result, context);
        result = this.replaceQuestPlaceholders(result, context);
        result = this.replaceObjectivePlaceholders(result, context);
        result = this.replaceObjectiveSpecificPlaceholders(result, context);
        result = this.replaceTierPlaceholders(result, context);
        result = this.replaceDifficultyPlaceholders(result, context);
        return result;
    }

    private String replaceGlobalPlaceholders(String text) {
        String prefix = null;
        if (this.plugin.getMessageManager() != null && this.plugin.getMessageManager().getConfig() != null) {
            prefix = this.plugin.getMessageManager().getConfig().getString("prefix");
        }
        if (prefix == null) {
            prefix = "&7[&bSoapsQuest&7]";
        }
        return text.replace("<prefix>", prefix);
    }

    private String replacePlayerPlaceholders(String text, PlaceholderContext context) {
        if (context.player != null) {
            text = text.replace("<player>", context.player.getName());
        }
        return text;
    }

    private String replaceQuestPlaceholders(String text, PlaceholderContext context) {
        if (context.quest != null) {
            text = text.replace("<quest>", context.quest.getDisplay());
            text = text.replace("<type>", context.quest.getClass().getSimpleName().replace("Quest", ""));
        }
        if (context.questType != null) {
            text = text.replace("<type>", this.capitalize(context.questType));
        }
        return text;
    }

    private String replaceObjectivePlaceholders(String text, PlaceholderContext context) {
        if (context.progress != null) {
            text = text.replace("<progress>", String.valueOf(context.progress.getCurrentProgress()));
            text = text.replace("<amount>", String.valueOf(context.progress.getRequiredAmount()));
            String progressDisplay = context.progress.getCurrentProgress() + "/" + context.progress.getRequiredAmount();
            text = text.replace("<progress_display>", progressDisplay);
        } else {
            if (context.currentProgress >= 0) {
                text = text.replace("<progress>", String.valueOf(context.currentProgress));
            }
            if (context.requiredAmount > 0) {
                text = text.replace("<amount>", String.valueOf(context.requiredAmount));
            }
        }
        if (context.objective != null) {
            text = text.replace("<objective>", context.objective.getDescription());
        } else if (context.objectiveDescription != null) {
            text = text.replace("<objective>", context.objectiveDescription);
        } else if (context.quest != null && context.progress != null && context.quest.hasObjectives()) {
            List<Objective> objectives = context.quest.getObjectives();
            if (objectives == null || objectives.isEmpty()) {
                text = text.replace("<objective>", "");
            } else {
                StringBuilder sb = new StringBuilder();
                boolean hasAny = false;
                for (Objective obj : objectives) {
                    if (obj == null || obj.getDescription() == null || obj.getDescription().isEmpty()) continue;
                    String objectiveId = obj.getObjectiveId();
                    int progress = context.progress.getObjectiveProgress(objectiveId);
                    int required = obj.getRequiredAmount();
                    if (!hasAny) {
                        sb.append("&7Objective:\n");
                        hasAny = true;
                    }
                    String color = progress >= required ? "&8&m" : (progress > 0 ? "&a" : "&f");
                    sb.append(color).append(obj.getDescription()).append(": ").append(progress).append("/").append(required).append("\n");
                }
                String result = hasAny ? sb.toString().trim() : "";
                text = text.replace("<objective>", result);
            }
        }
        if (context.index >= 0) {
            text = text.replace("<index>", String.valueOf(context.index));
        }
        return text;
    }

    private String replaceTierPlaceholders(String text, PlaceholderContext context) {
        String questTier;
        TierManager.Tier tier = context.tier;
        if (tier == null && context.quest != null && (questTier = context.quest.getTier()) != null) {
            tier = this.plugin.getTierManager().getTier(questTier.toLowerCase());
        }
        if (tier == null && context.tierName != null) {
            tier = this.plugin.getTierManager().getTier(context.tierName);
        }
        if (tier != null) {
            text = text.replace("<tier>", tier.display);
            text = text.replace("<tier_color>", tier.color);
            text = text.replace("<tier_prefix>", tier.prefix);
            text = text.replace("<tier_display>", tier.display);
        }
        return text;
    }

    private String replaceDifficultyPlaceholders(String text, PlaceholderContext context) {
        DifficultyManager.Difficulty difficulty = context.difficulty;
        if (difficulty == null && context.difficultyName != null) {
            difficulty = this.plugin.getDifficultyManager().getDifficulty(context.difficultyName);
        }
        if (difficulty != null) {
            text = text.replace("<difficulty>", difficulty.display);
            text = text.replace("<difficulty_color>", difficulty.color != null ? difficulty.color : "&f");
            text = text.replace("<difficulty_display>", difficulty.display);
        }
        return text;
    }

    private String replaceObjectiveSpecificPlaceholders(String text, PlaceholderContext context) {
        String mob;
        String item;
        String block;
        String entity;
        if (context.objectiveData == null || context.objectiveData.isEmpty()) {
            return text;
        }
        String objectiveType = context.objectiveData.get("type");
        if (objectiveType == null) {
            return text;
        }
        if (text.contains("<entity>") && context.objectiveData.containsKey("entity") && (entity = context.objectiveData.get("entity")) != null) {
            entity = this.formatName(entity);
            text = text.replace("<entity>", entity);
        }
        if (text.contains("<block>") && context.objectiveData.containsKey("block") && (block = context.objectiveData.get("block")) != null) {
            block = this.formatName(block);
            text = text.replace("<block>", block);
        }
        if (text.contains("<item>") && context.objectiveData.containsKey("item") && (item = context.objectiveData.get("item")) != null) {
            item = this.formatName(item);
            text = text.replace("<item>", item);
        }
        if (text.contains("<mob>") && context.objectiveData.containsKey("mob") && (mob = context.objectiveData.get("mob")) != null) {
            text = text.replace("<mob>", mob);
        }
        return text;
    }

    private String formatName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        String[] parts = name.toLowerCase().split("_");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length; ++i) {
            if (i > 0) {
                result.append(" ");
            }
            result.append(this.capitalize(parts[i]));
        }
        return result.toString();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

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
        private Map<String, String> objectiveData;

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
                this.objectiveData = new HashMap<String, String>();
            }
            this.objectiveData.put(key, value);
            return this;
        }
    }
}

