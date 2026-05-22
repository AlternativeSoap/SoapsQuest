/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package com.soaps.quest.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigMigrationUtil {
    private static final List<String> LEGACY_AMOUNT_FIELDS = List.of("kills", "blocks", "distance", "count", "level");

    public static int migrateLegacyAmountKeys(YamlConfiguration config, Logger logger) {
        int migratedCount = 0;
        Set<String> questKeys = config.getKeys(false);
        for (String questKey : questKeys) {
            List<Map<?, ?>> objectives;
            ConfigurationSection quest = config.getConfigurationSection(questKey);
            if (quest == null || !quest.contains("objectives") || (objectives = quest.getMapList("objectives")).isEmpty()) continue;
            for (int i = 0; i < objectives.size(); ++i) {
                Object raw = objectives.get(i);
                if (!(raw instanceof Map)) continue;
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) raw;
                boolean migrated = false;
                if (map.containsKey("amount")) continue;
                for (String legacyField : LEGACY_AMOUNT_FIELDS) {
                    if (!map.containsKey(legacyField)) continue;
                    Object value = map.get(legacyField);
                    map.put("amount", value);
                    map.remove(legacyField);
                    migrated = true;
                    ++migratedCount;
                    if (logger == null) break;
                    logger.log(Level.FINE, "[Migration] Quest ''{0}'' objective {1}: migrated ''{2}'' to ''amount''", new Object[]{questKey, i, legacyField});
                    break;
                }
                if (migrated || map.containsKey("amount")) continue;
                map.put("amount", 1);
            }
            quest.set("objectives", (Object)objectives);
        }
        return migratedCount;
    }

    public static int migrateLegacyAmountKeys(YamlConfiguration config) {
        return ConfigMigrationUtil.migrateLegacyAmountKeys(config, null);
    }

    public static boolean migrateSingleObjective(Map<String, Object> objectiveMap) {
        if (objectiveMap == null || objectiveMap.containsKey("amount")) {
            return false;
        }
        for (String legacyField : LEGACY_AMOUNT_FIELDS) {
            if (!objectiveMap.containsKey(legacyField)) continue;
            Object value = objectiveMap.get(legacyField);
            objectiveMap.put("amount", value);
            objectiveMap.remove(legacyField);
            return true;
        }
        if (!objectiveMap.containsKey("amount")) {
            objectiveMap.put("amount", 1);
        }
        return false;
    }

    public static int validateAmountFields(YamlConfiguration config, Logger logger) {
        int missingCount = 0;
        Set<String> questKeys = config.getKeys(false);
        for (String questKey : questKeys) {
            List<Map<?, ?>> objectives;
            ConfigurationSection quest = config.getConfigurationSection(questKey);
            if (quest == null || !quest.contains("objectives") || (objectives = quest.getMapList("objectives")).isEmpty()) continue;
            for (int i = 0; i < objectives.size(); ++i) {
                Object raw = objectives.get(i);
                if (!(raw instanceof Map)) continue;
                @SuppressWarnings("unchecked")
                Map<String, Object> obj = (Map<String, Object>) raw;
                if (obj.containsKey("amount")) continue;
                ++missingCount;
                if (logger == null) continue;
                logger.log(Level.WARNING, "[Validation] Quest ''{0}'' objective {1} missing ''amount'' field", new Object[]{questKey, i});
            }
        }
        return missingCount;
    }

    public static boolean needsMigration(YamlConfiguration config) {
        Set<String> questKeys = config.getKeys(false);
        for (String questKey : questKeys) {
            List<Map<?, ?>> objectives;
            ConfigurationSection quest = config.getConfigurationSection(questKey);
            if (quest == null || !quest.contains("objectives") || (objectives = quest.getMapList("objectives")).isEmpty()) continue;
            for (Map<?, ?> obj : objectives) {
                for (String legacyField : LEGACY_AMOUNT_FIELDS) {
                    if (!obj.containsKey(legacyField)) continue;
                    return true;
                }
                if (obj.containsKey("amount")) continue;
                return true;
            }
        }
        return false;
    }
}

