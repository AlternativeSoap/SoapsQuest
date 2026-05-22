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

public final class DifficultyManager {
    private final SoapsQuest plugin;
    private final Map<String, Difficulty> difficulties;
    private final WeightedRandomPicker<Difficulty> picker;
    private FileConfiguration config;

    public DifficultyManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.difficulties = new LinkedHashMap<String, Difficulty>();
        this.picker = new WeightedRandomPicker<Difficulty>(d -> d.weight);
        this.loadDifficulties();
    }

    private void loadDifficulties() {
        this.difficulties.clear();
        File file = new File(this.plugin.getDataFolder(), "difficulties.yml");
        if (!file.exists()) {
            this.plugin.saveResource("difficulties.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration((File)file);
        ConfigurationSection difficultiesSection = this.config.getConfigurationSection("difficulties");
        if (difficultiesSection == null) {
            this.plugin.getLogger().warning("No 'difficulties' section found in difficulties.yml - using defaults");
            this.createDefaultDifficulties();
            return;
        }
        for (String key : difficultiesSection.getKeys(false)) {
            ConfigurationSection multiplierSection;
            ConfigurationSection diffSection = difficultiesSection.getConfigurationSection(key);
            if (diffSection == null) continue;
            Difficulty difficulty = new Difficulty();
            difficulty.name = key;
            difficulty.display = diffSection.getString("display", "&e" + key);
            difficulty.color = diffSection.getString("color", "&e");
            difficulty.weight = diffSection.getInt("weight", 1);
            if (difficulty.weight <= 0) {
                this.plugin.getLogger().warning(String.format("Difficulty '%s' has invalid weight %d \u2014 must be > 0; defaulting to 1", key, difficulty.weight));
                difficulty.weight = 1;
            }
            if ((multiplierSection = diffSection.getConfigurationSection("multiplier")) != null) {
                difficulty.objectiveMultiplier = multiplierSection.getDouble("objective-amount", 1.0);
                difficulty.rewardMultiplier = multiplierSection.getDouble("reward", 1.0);
            } else {
                difficulty.objectiveMultiplier = 1.0;
                difficulty.rewardMultiplier = 1.0;
            }
            this.difficulties.put(key, difficulty);
            this.plugin.getLogger().fine(() -> String.format("Loaded difficulty '%s' (weight: %d, obj\u00d7%.1f, reward\u00d7%.1f)", key, difficulty.weight, difficulty.objectiveMultiplier, difficulty.rewardMultiplier));
        }
        if (this.difficulties.isEmpty()) {
            this.plugin.getLogger().warning("No valid difficulties loaded - creating defaults");
            this.createDefaultDifficulties();
        } else {
            this.plugin.debugLog(String.format("Loaded %d difficulties from difficulties.yml", this.difficulties.size()));
        }
    }

    private void createDefaultDifficulties() {
        this.addDefault("easy", "&aEasy", "&a", 50, 0.75, 0.75);
        this.addDefault("normal", "&eNormal", "&e", 35, 1.0, 1.0);
        this.addDefault("hard", "&cHard", "&c", 20, 1.5, 1.5);
        this.addDefault("expert", "&6Expert", "&6", 10, 2.0, 2.0);
        this.addDefault("nightmare", "&4&lNightmare", "&4", 5, 2.5, 2.5);
    }

    private void addDefault(String name, String display, String color, int weight, double objMult, double rewardMult) {
        Difficulty d = new Difficulty();
        d.name = name;
        d.display = display;
        d.color = color;
        d.weight = weight;
        d.objectiveMultiplier = objMult;
        d.rewardMultiplier = rewardMult;
        this.difficulties.put(name, d);
    }

    public Difficulty getDifficulty(String name) {
        if (name == null) {
            return null;
        }
        return this.difficulties.get(name.toLowerCase());
    }

    public Difficulty getDifficultyOrDefault(String name) {
        Difficulty difficulty = this.getDifficulty(name);
        if (difficulty != null) {
            return difficulty;
        }
        Difficulty normal = this.difficulties.get("normal");
        if (normal != null) {
            return normal;
        }
        if (!this.difficulties.isEmpty()) {
            return this.difficulties.values().iterator().next();
        }
        return null;
    }

    public Difficulty getRandomDifficulty() {
        if (this.difficulties.isEmpty()) {
            return this.getDifficultyOrDefault("normal");
        }
        Difficulty result = this.picker.pick(this.difficulties.values());
        return result != null ? result : this.getDifficultyOrDefault("normal");
    }

    public boolean hasDifficulty(String name) {
        String key = name != null ? name.toLowerCase() : null;
        return this.difficulties.containsKey(key);
    }

    public Set<String> getDifficultyNames() {
        return this.difficulties.keySet();
    }

    public List<String> getSortedDifficultyNames() {
        return new ArrayList<String>(this.difficulties.keySet());
    }

    public void reload() {
        this.plugin.debugLog("Reloading difficulty configurations...");
        this.loadDifficulties();
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public static class Difficulty {
        public String name;
        public String display;
        public String color;
        public int weight;
        public double objectiveMultiplier;
        public double rewardMultiplier;
    }
}

