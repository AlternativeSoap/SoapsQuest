package com.soaps.quest.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;

import com.soaps.quest.SoapsQuest;

/**
 * Manages quest difficulties loaded from config.yml
 * Provides weighted random selection and scaling multipliers
 */
public final class DifficultyManager {
    private final SoapsQuest plugin;
    private final Map<String, Difficulty> difficulties;
    private final Random random;
    private int totalWeight;
    
    public DifficultyManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.difficulties = new HashMap<>();
        this.random = new Random();
        this.totalWeight = 0;
        loadDifficulties();
    }
    
    /**
     * Loads all difficulties from config.yml
     */
    private void loadDifficulties() {
        difficulties.clear();
        totalWeight = 0;
        
        ConfigurationSection difficultiesSection = plugin.getConfig().getConfigurationSection("difficulties");
        if (difficultiesSection == null) {
            plugin.getLogger().warning("No 'difficulties' section found in config.yml - using defaults");
            createDefaultDifficulties();
            return;
        }
        
        for (String key : difficultiesSection.getKeys(false)) {
            ConfigurationSection diffSection = difficultiesSection.getConfigurationSection(key);
            if (diffSection == null) continue;
            
            Difficulty difficulty = new Difficulty();
            difficulty.name = key;
            difficulty.display = diffSection.getString("display", "&e" + key);
            difficulty.weight = diffSection.getInt("weight", 1);
            
            ConfigurationSection multiplierSection = diffSection.getConfigurationSection("multiplier");
            if (multiplierSection != null) {
                difficulty.objectiveMultiplier = multiplierSection.getDouble("objective-amount", 1.0);
                difficulty.rewardMultiplier = multiplierSection.getDouble("reward", 1.0);
            } else {
                difficulty.objectiveMultiplier = 1.0;
                difficulty.rewardMultiplier = 1.0;
            }
            
            difficulties.put(key, difficulty);
            totalWeight += difficulty.weight;
            
            plugin.getLogger().fine(() -> String.format("Loaded difficulty '%s' (weight: %d, obj×%.1f, reward×%.1f)", 
                key, difficulty.weight, difficulty.objectiveMultiplier, difficulty.rewardMultiplier));
        }
        
        if (difficulties.isEmpty()) {
            plugin.getLogger().warning("No valid difficulties loaded - creating defaults");
            createDefaultDifficulties();
        } else {
            plugin.getLogger().info(String.format("Loaded %d difficulties from config.yml", difficulties.size()));
        }
    }
    
    /**
     * Creates default difficulties if config is missing
     */
    private void createDefaultDifficulties() {
        Difficulty easy = new Difficulty();
        easy.name = "easy";
        easy.display = "&aEasy";
        easy.weight = 50;
        easy.objectiveMultiplier = 0.8;
        easy.rewardMultiplier = 0.8;
        difficulties.put("easy", easy);
        
        Difficulty normal = new Difficulty();
        normal.name = "normal";
        normal.display = "&eNormal";
        normal.weight = 35;
        normal.objectiveMultiplier = 1.0;
        normal.rewardMultiplier = 1.0;
        difficulties.put("normal", normal);
        
        Difficulty hard = new Difficulty();
        hard.name = "hard";
        hard.display = "&cHard";
        hard.weight = 15;
        hard.objectiveMultiplier = 1.5;
        hard.rewardMultiplier = 1.5;
        difficulties.put("hard", hard);
        
        totalWeight = 100;
    }
    
    /**
     * Gets a specific difficulty by name
     */
    public Difficulty getDifficulty(String name) {
        String key = name != null ? name.toLowerCase() : "normal";
        return difficulties.get(key);
    }
    
    /**
     * Gets a difficulty by name, or returns default if not found
     */
    public Difficulty getDifficultyOrDefault(String name) {
        Difficulty difficulty = getDifficulty(name);
        return difficulty != null ? difficulty : getDifficulty("normal");
    }
    
    /**
     * Selects a random difficulty based on weights
     */
    public Difficulty getRandomDifficulty() {
        if (difficulties.isEmpty() || totalWeight <= 0) {
            return getDifficultyOrDefault("normal");
        }
        
        int roll = random.nextInt(totalWeight);
        int currentWeight = 0;
        
        for (Difficulty difficulty : difficulties.values()) {
            currentWeight += difficulty.weight;
            if (roll < currentWeight) {
                return difficulty;
            }
        }
        
        // Fallback
        return difficulties.values().iterator().next();
    }
    
    /**
     * Checks if a difficulty exists
     */
    public boolean hasDifficulty(String name) {
        String key = name != null ? name.toLowerCase() : null;
        return difficulties.containsKey(key);
    }
    
    /**
     * Gets all difficulty names
     */
    public java.util.Set<String> getDifficultyNames() {
        return difficulties.keySet();
    }
    
    /**
     * Difficulty configuration class
     */
    public static class Difficulty {
        public String name;
        public String display;
        public int weight;
        public double objectiveMultiplier;
        public double rewardMultiplier;
    }
}
