package com.soaps.quest.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;

import com.soaps.quest.SoapsQuest;

/**
 * Manages quest tiers/rarities loaded from config.yml
 * Provides weighted random selection and placeholder support
 */
public final class TierManager {
    private final SoapsQuest plugin;
    private final Map<String, Tier> tiers;
    private final Random random;
    private int totalWeight;
    
    public TierManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.tiers = new HashMap<>();
        this.random = new Random();
        this.totalWeight = 0;
        loadTiers();
    }
    
    /**
     * Loads all tiers from config.yml
     */
    public void loadTiers() {
        tiers.clear();
        totalWeight = 0;
        
        ConfigurationSection tiersSection = plugin.getConfig().getConfigurationSection("tiers");
        if (tiersSection == null) {
            plugin.getLogger().warning("No 'tiers' section found in config.yml - using defaults");
            createDefaultTiers();
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
            // Milestones are no longer loaded from config.yml - they should be defined in individual quests
            
            tiers.put(key, tier);
            totalWeight += tier.weight;
            
            plugin.getLogger().fine(() -> String.format("Loaded tier '%s' (weight: %d)", key, tier.weight));
        }
        
        if (tiers.isEmpty()) {
            plugin.getLogger().warning("No valid tiers loaded - creating defaults");
            createDefaultTiers();
        } else {
            plugin.getLogger().info(String.format("Loaded %d tiers from config.yml", tiers.size()));
        }
    }
    
    /**
     * Creates default tiers if config is missing
     */
    private void createDefaultTiers() {
        Tier common = new Tier();
        common.name = "common";
        common.display = "&fCommon";
        common.prefix = "&7[COMMON]";
        common.color = "&f";
        common.weight = 40;
        tiers.put("common", common);
        
        Tier rare = new Tier();
        rare.name = "rare";
        rare.display = "&9Rare";
        rare.prefix = "&9[RARE]";
        rare.color = "&9";
        rare.weight = 30;
        tiers.put("rare", rare);
        
        Tier epic = new Tier();
        epic.name = "epic";
        epic.display = "&5Epic";
        epic.prefix = "&5[EPIC]";
        epic.color = "&5";
        epic.weight = 20;
        tiers.put("epic", epic);
        
        Tier legendary = new Tier();
        legendary.name = "legendary";
        legendary.display = "&6Legendary";
        legendary.prefix = "&6[LEGENDARY]";
        legendary.color = "&6";
        legendary.weight = 10;
        tiers.put("legendary", legendary);
        
        totalWeight = 100;
    }
    
    /**
     * Gets a specific tier by name
     */
    public Tier getTier(String name) {
        String key = name != null ? name.toLowerCase() : "common";
        return tiers.get(key);
    }
    
    /**
     * Gets a tier by name, or returns default if not found
     */
    public Tier getTierOrDefault(String name) {
        Tier tier = getTier(name);
        return tier != null ? tier : getTier("common");
    }
    
    /**
     * Selects a random tier based on weights
     */
    public Tier getRandomTier() {
        if (tiers.isEmpty() || totalWeight <= 0) {
            return getTierOrDefault("common");
        }
        
        int roll = random.nextInt(totalWeight);
        int currentWeight = 0;
        
        for (Tier tier : tiers.values()) {
            currentWeight += tier.weight;
            if (roll < currentWeight) {
                return tier;
            }
        }
        
        // Fallback
        return tiers.values().iterator().next();
    }
    
    /**
     * Checks if a tier exists
     */
    public boolean hasTier(String name) {
        String key = name != null ? name.toLowerCase() : null;
        return tiers.containsKey(key);
    }
    
    /**
     * Gets all tier names
     */
    public java.util.Set<String> getTierNames() {
        return tiers.keySet();
    }
    
    /**
     * Tier configuration class
     */
    public static class Tier {
        public String name;
        public String display;
        public String prefix;
        public String color;
        public int weight;
    }
}
