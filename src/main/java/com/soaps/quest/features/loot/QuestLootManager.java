/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.EntityType
 */
package com.soaps.quest.features.loot;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.features.loot.QuestLootConfig;
import java.io.File;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

public class QuestLootManager {
    private final SoapsQuest plugin;
    private QuestLootConfig config;
    private final File configFile;

    public QuestLootManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "quest-loot.yml");
        this.initialize();
    }

    private void initialize() {
        this.loadConfig();
    }

    public void loadConfig() {
        if (!this.configFile.exists()) {
            this.plugin.saveResource("quest-loot.yml", false);
            this.plugin.debugLog(Level.INFO, "Created default quest-loot.yml configuration", new Object[0]);
        }
        try {
            YamlConfiguration fileConfig = YamlConfiguration.loadConfiguration((File)this.configFile);
            this.config = new QuestLootConfig((ConfigurationSection)fileConfig);
            if (this.config.isEnabled()) {
                this.plugin.debugLog(Level.INFO, "Quest Loot System enabled", new Object[0]);
                if (this.config.getChestSettings().isEnabled()) {
                    this.plugin.debugLog(Level.INFO, "  - Chest loot: enabled ({0}% chance)", this.config.getChestSettings().getChance());
                }
                if (this.config.getMobSettings().isEnabled()) {
                    this.plugin.debugLog(Level.INFO, "  - Mob loot: enabled ({0}% default chance)", this.config.getMobSettings().getDefaultChance());
                }
            } else {
                this.plugin.debugLog(Level.INFO, "Quest Loot System disabled", new Object[0]);
            }
        }
        catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to load quest-loot.yml: " + e.getMessage(), e);
            this.config = new QuestLootConfig((ConfigurationSection)this.plugin.getConfig());
        }
    }

    public void reload() {
        this.loadConfig();
    }

    public boolean isEnabled() {
        return this.config.isEnabled();
    }

    public boolean isDebugEnabled() {
        return this.plugin.isDebugMode();
    }

    public int getMaxPerEvent() {
        return this.config.getMaxPerEvent();
    }

    public boolean shouldObeyPluginRestrictions() {
        return this.config.shouldObeyPluginRestrictions();
    }

    public boolean isChestLootEnabled() {
        return this.config.isEnabled() && this.config.getChestSettings().isEnabled();
    }

    public double getChestChance() {
        return this.config.getChestSettings().getChance();
    }

    public int getChestAmountMin() {
        return this.config.getChestSettings().getAmountMin();
    }

    public int getChestAmountMax() {
        return this.config.getChestSettings().getAmountMax();
    }

    public List<String> getChestWorlds() {
        return this.config.getChestSettings().getWorlds();
    }

    public String getChestSourceMode() {
        return this.config.getChestSettings().getSourceMode();
    }

    public String getMobSourceMode() {
        String mobMode = this.config.getMobSettings().getSourceMode();
        return mobMode != null ? mobMode : this.getChestSourceMode();
    }

    public List<String> getChestQuestIds() {
        return this.config.getChestSettings().getQuestIds();
    }

    public boolean isChestWorldAllowed(String worldName) {
        return this.config.getChestSettings().isWorldAllowed(worldName);
    }

    public boolean rollChestChance() {
        double chance = this.getChestChance();
        double roll = ThreadLocalRandom.current().nextDouble(0.0, 100.0);
        return roll < chance;
    }

    public int getRandomChestAmount() {
        int max;
        int min = this.getChestAmountMin();
        if (min >= (max = this.getChestAmountMax())) {
            return min;
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public boolean isMobLootEnabled() {
        return this.config.isEnabled() && this.config.getMobSettings().isEnabled();
    }

    public double getMobDefaultChance() {
        return this.config.getMobSettings().getDefaultChance();
    }

    public List<String> getMobWorlds() {
        return this.config.getMobSettings().getWorlds();
    }

    public boolean isMobWorldAllowed(String worldName) {
        return this.config.getMobSettings().isWorldAllowed(worldName);
    }

    public double getMobChance(EntityType type) {
        return this.config.getMobSettings().getChanceForMob(type);
    }

    public int getMobAmountMin(EntityType type) {
        return this.config.getMobSettings().getAmountMinForMob(type);
    }

    public int getMobAmountMax(EntityType type) {
        return this.config.getMobSettings().getAmountMaxForMob(type);
    }

    public boolean rollMobChance(EntityType type) {
        double chance = this.getMobChance(type);
        double roll = ThreadLocalRandom.current().nextDouble(0.0, 100.0);
        return roll < chance;
    }

    public int getRandomMobAmount(EntityType type) {
        int max;
        int min = this.getMobAmountMin(type);
        if (min >= (max = this.getMobAmountMax(type))) {
            return min;
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public List<String> getMobQuestIds() {
        List<String> mobQuests = this.config.getMobSettings().getQuestIds();
        return mobQuests.isEmpty() ? this.getChestQuestIds() : mobQuests;
    }

    public void debug(String message) {
        if (this.isDebugEnabled()) {
            this.plugin.getLogger().log(Level.INFO, "[QuestLoot] {0}", new Object[]{message});
        }
    }

    public void debug(String format, Object ... args) {
        if (this.isDebugEnabled()) {
            this.plugin.getLogger().log(Level.INFO, "[QuestLoot] {0}", new Object[]{String.format(format, args)});
        }
    }
}

