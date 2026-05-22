/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.EntityType
 */
package com.soaps.quest.features.loot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

public class QuestLootConfig {
    private final boolean enabled;
    private final int maxPerEvent;
    private final boolean obeyPluginRestrictions;
    private final ChestLootSettings chestSettings;
    private final MobLootSettings mobSettings;

    public QuestLootConfig(ConfigurationSection config) {
        ConfigurationSection lootSection = config.getConfigurationSection("quest-loot");
        if (lootSection == null) {
            this.enabled = false;
            this.maxPerEvent = 4;
            this.obeyPluginRestrictions = true;
            this.chestSettings = new ChestLootSettings(null);
            this.mobSettings = new MobLootSettings(null);
            return;
        }
        this.enabled = lootSection.getBoolean("enabled", true);
        this.maxPerEvent = lootSection.getInt("max-per-event", 4);
        this.obeyPluginRestrictions = lootSection.getBoolean("obey-plugin-restrictions", true);
        this.chestSettings = new ChestLootSettings(lootSection.getConfigurationSection("chest"));
        this.mobSettings = new MobLootSettings(lootSection.getConfigurationSection("mobs"));
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getMaxPerEvent() {
        return this.maxPerEvent;
    }

    public boolean shouldObeyPluginRestrictions() {
        return this.obeyPluginRestrictions;
    }

    public ChestLootSettings getChestSettings() {
        return this.chestSettings;
    }

    public MobLootSettings getMobSettings() {
        return this.mobSettings;
    }

    public static class ChestLootSettings {
        private final boolean enabled;
        private final double chance;
        private final int amountMin;
        private final int amountMax;
        private final List<String> worlds;
        private final String sourceMode;
        private final List<String> questIds;

        public ChestLootSettings(ConfigurationSection section) {
            if (section == null) {
                this.enabled = false;
                this.chance = 10.0;
                this.amountMin = 1;
                this.amountMax = 2;
                this.worlds = new ArrayList<String>();
                this.sourceMode = "mixed";
                this.questIds = new ArrayList<String>();
                return;
            }
            this.enabled = section.getBoolean("enabled", true);
            this.chance = section.getDouble("chance", 10.0);
            this.amountMin = section.getInt("amount-min", 1);
            this.amountMax = section.getInt("amount-max", 2);
            this.worlds = section.getStringList("worlds");
            this.sourceMode = section.getString("source-mode", "mixed");
            this.questIds = section.getStringList("quests");
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public double getChance() {
            return this.chance;
        }

        public int getAmountMin() {
            return this.amountMin;
        }

        public int getAmountMax() {
            return this.amountMax;
        }

        public List<String> getWorlds() {
            return this.worlds;
        }

        public String getSourceMode() {
            return this.sourceMode;
        }

        public List<String> getQuestIds() {
            return this.questIds;
        }

        public boolean isWorldAllowed(String worldName) {
            return this.worlds.isEmpty() || this.worlds.contains(worldName);
        }
    }

    public static class MobLootSettings {
        private final boolean enabled;
        private final double defaultChance;
        private final List<String> worlds;
        private final String sourceMode;
        private final List<String> questIds;
        private final Map<EntityType, MobTypeSettings> mobTypes;

        public MobLootSettings(ConfigurationSection section) {
            if (section == null) {
                this.enabled = false;
                this.defaultChance = 5.0;
                this.worlds = new ArrayList<String>();
                this.sourceMode = null;
                this.questIds = new ArrayList<String>();
                this.mobTypes = new HashMap<EntityType, MobTypeSettings>();
                return;
            }
            this.enabled = section.getBoolean("enabled", true);
            this.defaultChance = section.getDouble("default-chance", 5.0);
            this.worlds = section.getStringList("worlds");
            this.sourceMode = section.getString("source-mode", null);
            this.questIds = section.getStringList("quests");
            this.mobTypes = new HashMap<EntityType, MobTypeSettings>();
            ConfigurationSection typesSection = section.getConfigurationSection("types");
            if (typesSection != null) {
                for (String entityName : typesSection.getKeys(false)) {
                    try {
                        EntityType entityType = EntityType.valueOf((String)entityName.toUpperCase());
                        ConfigurationSection mobSection = typesSection.getConfigurationSection(entityName);
                        if (mobSection == null) continue;
                        this.mobTypes.put(entityType, new MobTypeSettings(mobSection, this.defaultChance));
                    }
                    catch (IllegalArgumentException illegalArgumentException) {}
                }
            }
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public double getDefaultChance() {
            return this.defaultChance;
        }

        public List<String> getWorlds() {
            return this.worlds;
        }

        public boolean isWorldAllowed(String worldName) {
            return this.worlds.isEmpty() || this.worlds.contains(worldName);
        }

        public String getSourceMode() {
            return this.sourceMode;
        }

        public List<String> getQuestIds() {
            return this.questIds;
        }

        public boolean hasMobTypeSettings(EntityType type) {
            return this.mobTypes.containsKey(type);
        }

        public MobTypeSettings getMobTypeSettings(EntityType type) {
            return this.mobTypes.get(type);
        }

        public double getChanceForMob(EntityType type) {
            if (this.mobTypes.containsKey(type)) {
                return this.mobTypes.get(type).getChance();
            }
            return this.defaultChance;
        }

        public int getAmountMinForMob(EntityType type) {
            if (this.mobTypes.containsKey(type)) {
                return this.mobTypes.get(type).getAmountMin();
            }
            return 1;
        }

        public int getAmountMaxForMob(EntityType type) {
            if (this.mobTypes.containsKey(type)) {
                return this.mobTypes.get(type).getAmountMax();
            }
            return 1;
        }
    }

    public static class MobTypeSettings {
        private final double chance;
        private final int amountMin;
        private final int amountMax;

        public MobTypeSettings(ConfigurationSection section, double defaultChance) {
            this.chance = section.getDouble("chance", defaultChance);
            this.amountMin = section.getInt("amount-min", 1);
            this.amountMax = section.getInt("amount-max", 1);
        }

        public double getChance() {
            return this.chance;
        }

        public int getAmountMin() {
            return this.amountMin;
        }

        public int getAmountMax() {
            return this.amountMax;
        }
    }
}

