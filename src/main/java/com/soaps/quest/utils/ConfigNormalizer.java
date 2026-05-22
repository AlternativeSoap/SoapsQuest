/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 */
package com.soaps.quest.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigNormalizer {
    public static void normalizeGeneratedQuest(Map<String, Object> questData) {
        if (questData == null || questData.isEmpty()) {
            return;
        }
        ConfigNormalizer.normalizeTier(questData);
        ConfigNormalizer.normalizeDifficulty(questData);
        ConfigNormalizer.nestQuestPaperMaterial(questData);
        ConfigNormalizer.ensureLockToPlayer(questData);
        ConfigNormalizer.normalizeRewardStructure(questData);
        ConfigNormalizer.normalizeObjectives(questData);
        ConfigNormalizer.normalizeLore(questData);
        ConfigNormalizer.normalizeBooleans(questData);
        ConfigNormalizer.ensureType(questData);
    }

    private static void normalizeTier(Map<String, Object> questData) {
        Object tier = questData.get("tier");
        if (tier instanceof String) {
            String tierStr = (String)tier;
            questData.put("tier", tierStr.toLowerCase());
        }
    }

    private static void normalizeDifficulty(Map<String, Object> questData) {
        Object difficulty = questData.get("difficulty");
        if (difficulty instanceof String) {
            String diffStr = (String)difficulty;
            questData.put("difficulty", diffStr.toLowerCase());
        } else if (difficulty == null) {
            questData.put("difficulty", "easy");
        }
    }

    private static void nestQuestPaperMaterial(Map<String, Object> questData) {
        Object material = questData.get("material");
        if (material != null) {
            Map<String, Object> questPaperSection;
            Object questPaper = questData.get("quest_paper");
            if (questPaper instanceof Map) {
                Map existingSection = (Map)questPaper;
                questPaperSection = existingSection;
            } else {
                questPaperSection = new LinkedHashMap();
                questData.put("quest_paper", questPaperSection);
            }
            questPaperSection.put("material", material);
            questData.remove("material");
        }
    }

    private static void ensureLockToPlayer(Map<String, Object> questData) {
        if (!questData.containsKey("lock-to-player")) {
            questData.put("lock-to-player", false);
        }
    }

    private static void normalizeRewardStructure(Map<String, Object> questData) {
        Object rewards = questData.get("rewards");
        if (rewards != null) {
            Object normalizedReward = null;
            if (rewards instanceof List) {
                Object firstElement;
                List rewardList = (List)rewards;
                if (!rewardList.isEmpty() && (firstElement = rewardList.get(0)) instanceof Map) {
                    normalizedReward = firstElement;
                }
            } else if (rewards instanceof Map || rewards instanceof ConfigurationSection) {
                normalizedReward = rewards;
            }
            if (normalizedReward != null) {
                questData.put("reward", normalizedReward);
                questData.remove("rewards");
            }
        }
        if (!questData.containsKey("reward")) {
            questData.put("reward", new LinkedHashMap());
        }
    }

    private static void normalizeObjectives(Map<String, Object> questData) {
        Object objectives = questData.get("objectives");
        if (objectives instanceof List) {
            List objList = (List)objectives;
            ArrayList normalizedObjectives = new ArrayList();
            for (Object obj : objList) {
                if (!(obj instanceof Map)) continue;
                Map objMap = (Map)obj;
                LinkedHashMap<String, Object> objectiveMap = new LinkedHashMap<String, Object>(objMap);
                if (!objectiveMap.containsKey("type")) {
                    objectiveMap.put("type", "kill");
                }
                if (!objectiveMap.containsKey("target")) {
                    objectiveMap.put("target", "ZOMBIE");
                }
                if (!objectiveMap.containsKey("amount")) {
                    objectiveMap.put("amount", 1);
                }
                normalizedObjectives.add(objectiveMap);
            }
            questData.put("objectives", normalizedObjectives);
        }
    }

    private static void normalizeLore(Map<String, Object> questData) {
        Object lore = questData.get("lore");
        if (lore instanceof String) {
            String loreStr = (String)lore;
            questData.put("lore", Collections.singletonList(loreStr));
        } else if (lore == null) {
            questData.put("lore", Collections.singletonList("&7A randomly generated quest"));
        }
    }

    private static void normalizeBooleans(Map<String, Object> questData) {
        ConfigNormalizer.normalizeBooleanField(questData, "sequential");
        ConfigNormalizer.normalizeBooleanField(questData, "lock-to-player");
    }

    private static void normalizeBooleanField(Map<String, Object> questData, String fieldName) {
        Object value = questData.get(fieldName);
        if (value instanceof String) {
            String strValue = (String)value;
            questData.put(fieldName, "true".equalsIgnoreCase(strValue));
        }
    }

    private static void ensureType(Map<String, Object> questData) {
        if (!questData.containsKey("type")) {
            questData.put("type", "single");
        }
    }

    public static boolean validateStructure(String questId, Map<String, Object> questData) {
        boolean valid = true;
        ArrayList<String> issues = new ArrayList<String>();
        if (!questData.containsKey("display")) {
            issues.add("Missing 'display' field");
            valid = false;
        }
        if (!questData.containsKey("objectives")) {
            issues.add("Missing 'objectives' field");
            valid = false;
        } else {
            Object objectives = questData.get("objectives");
            if (!(objectives instanceof List) || ((List)objectives).isEmpty()) {
                issues.add("'objectives' must be a non-empty list");
                valid = false;
            }
        }
        if (!questData.containsKey("reward")) {
            issues.add("Missing 'reward' field");
        }
        if (!questData.containsKey("tier")) {
            issues.add("Missing 'tier' field");
        }
        if (!questData.containsKey("type")) {
            issues.add("Missing 'type' field");
        }
        if (!issues.isEmpty()) {
            System.out.println("[ConfigNormalizer] Quest '" + questId + "' validation issues:");
            for (String issue : issues) {
                System.out.println("  - " + issue);
            }
        }
        return valid;
    }

    public static void normalizeConfigurationSection(ConfigurationSection section) {
        if (section == null) {
            return;
        }
        LinkedHashMap<String, Object> questData = new LinkedHashMap<String, Object>();
        for (String key : section.getKeys(false)) {
            questData.put(key, section.get(key));
        }
        ConfigNormalizer.normalizeGeneratedQuest(questData);
        for (String key : questData.keySet()) {
            section.set(key, questData.get(key));
        }
    }
}

