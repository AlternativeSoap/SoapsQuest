/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package com.soaps.quest.managers;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.utils.WeightedRandomPicker;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class TierManager {
    private final SoapsQuest plugin;
    private final Map<String, Tier> tiers;
    private final WeightedRandomPicker<Tier> picker;
    private FileConfiguration config;

    public TierManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.tiers = new LinkedHashMap<String, Tier>();
        this.picker = new WeightedRandomPicker<Tier>(t -> t.weight);
        this.loadTiers();
    }

    public void loadTiers() {
        this.tiers.clear();
        File file = new File(this.plugin.getDataFolder(), "tiers.yml");
        if (!file.exists()) {
            this.plugin.saveResource("tiers.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration((File)file);
        ConfigurationSection tiersSection = this.config.getConfigurationSection("tiers");
        if (tiersSection == null) {
            this.plugin.getLogger().warning("No 'tiers' section found in tiers.yml - using defaults");
            this.createDefaultTiers();
            return;
        }
        for (String key : tiersSection.getKeys(false)) {
            ConfigurationSection tierSection = tiersSection.getConfigurationSection(key);
            if (tierSection == null) continue;
            Tier tier = new Tier();
            tier.name = key;
            tier.display = tierSection.getString("display", "&f" + key);
            tier.prefix = tierSection.getString("prefix", "&7[" + key.toUpperCase() + "]");
            tier.color = tierSection.getString("color", "&f");
            tier.weight = tierSection.getInt("weight", 1);
            if (tier.weight <= 0) {
                this.plugin.getLogger().warning(String.format("Tier '%s' has invalid weight %d \u2014 must be > 0; defaulting to 1", key, tier.weight));
                tier.weight = 1;
            }
            this.tiers.put(key, tier);
            this.plugin.getLogger().fine(() -> String.format("Loaded tier '%s' (weight: %d)", key, tier.weight));
        }
        if (this.tiers.isEmpty()) {
            this.plugin.getLogger().warning("No valid tiers loaded - creating defaults");
            this.createDefaultTiers();
        } else {
            this.plugin.debugLog(String.format("Loaded %d tiers from tiers.yml", this.tiers.size()));
        }
    }

    private void createDefaultTiers() {
        this.addDefault("common", "&fCommon", "&7[COMMON]", "&f", 40);
        this.addDefault("uncommon", "&2Uncommon", "&2[UNCOMMON]", "&2", 32);
        this.addDefault("rare", "&9Rare", "&9[RARE]", "&9", 25);
        this.addDefault("epic", "&5Epic", "&5[EPIC]", "&5", 18);
        this.addDefault("legendary", "&6Legendary", "&6[LEGENDARY]", "&6", 12);
        this.addDefault("mythic", "&d&lMythic", "&d&l[MYTHIC]", "&d", 8);
    }

    private void addDefault(String name, String display, String prefix, String color, int weight) {
        Tier t = new Tier();
        t.name = name;
        t.display = display;
        t.prefix = prefix;
        t.color = color;
        t.weight = weight;
        this.tiers.put(name, t);
    }

    public Tier getTier(String name) {
        if (name == null) {
            return null;
        }
        return this.tiers.get(name.toLowerCase());
    }

    public Tier getTierOrDefault(String name) {
        Tier tier = this.getTier(name);
        if (tier != null) {
            return tier;
        }
        Tier common = this.tiers.get("common");
        if (common != null) {
            return common;
        }
        if (!this.tiers.isEmpty()) {
            return this.tiers.values().iterator().next();
        }
        return null;
    }

    public Tier getRandomTier() {
        if (this.tiers.isEmpty()) {
            return this.getTierOrDefault("common");
        }
        Tier result = this.picker.pick(this.tiers.values());
        return result != null ? result : this.getTierOrDefault("common");
    }

    public boolean hasTier(String name) {
        String key = name != null ? name.toLowerCase() : null;
        return this.tiers.containsKey(key);
    }

    public Set<String> getTierNames() {
        return this.tiers.keySet();
    }

    public List<String> getSortedTierNames() {
        return new ArrayList<String>(this.tiers.keySet());
    }

    public void reload() {
        this.plugin.debugLog("Reloading tier configurations...");
        this.loadTiers();
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public static class Tier {
        public String name;
        public String display;
        public String prefix;
        public String color;
        public int weight;
    }
}

