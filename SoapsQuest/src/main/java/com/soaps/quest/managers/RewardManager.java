package com.soaps.quest.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.rewards.CommandReward;
import com.soaps.quest.rewards.ItemReward;
import com.soaps.quest.rewards.MoneyReward;
import com.soaps.quest.rewards.Reward;
import com.soaps.quest.rewards.RewardEntry;
import com.soaps.quest.rewards.XPReward;

/**
 * Manages quest rewards including loading from config and execution.
 */
public class RewardManager {
    
    private final SoapsQuest plugin;
    private final Map<String, List<Reward>> questRewards;
    private final Map<String, List<RewardEntry>> questRewardEntries;
    
    /**
     * Constructor for RewardManager.
     * 
     * @param plugin Plugin instance
     */
    public RewardManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.questRewards = new HashMap<>();
        this.questRewardEntries = new HashMap<>();
    }
    
    /**
     * Load all rewards from quests.yml.
     */
    public void loadRewards() {
        questRewards.clear();
        questRewardEntries.clear();
        
        // Load quests.yml
        File questsFile = new File(plugin.getDataFolder(), "quests.yml");
        if (!questsFile.exists()) {
            plugin.saveResource("quests.yml", false);
        }
        
        YamlConfiguration questsConfig = YamlConfiguration.loadConfiguration(questsFile);
        ConfigurationSection questsSection = questsConfig.getConfigurationSection("quests");
        if (questsSection == null) {
            return;
        }
        
        for (String questId : questsSection.getKeys(false)) {
            ConfigurationSection questSection = questsSection.getConfigurationSection(questId);
            if (questSection == null || !questSection.contains("reward")) {
                continue;
            }
            
            ConfigurationSection rewardSection = questSection.getConfigurationSection("reward");
            List<Reward> rewards = loadRewardsForQuest(rewardSection);
            List<RewardEntry> rewardEntries = loadRewardEntriesForQuest(rewardSection);
            
            if (!rewards.isEmpty()) {
                questRewards.put(questId, rewards);
            }
            
            if (!rewardEntries.isEmpty()) {
                questRewardEntries.put(questId, rewardEntries);
            }
        }
    }
    
    /**
     * Load rewards for a specific quest from configuration.
     * 
     * @param rewardSection Reward configuration section
     * @return List of Reward objects
     */
    private List<Reward> loadRewardsForQuest(ConfigurationSection rewardSection) {
        List<Reward> rewards = new ArrayList<>();
        
        if (rewardSection == null) {
            return rewards;
        }
        
        // XP Reward
        if (rewardSection.contains("xp")) {
            int xp = rewardSection.getInt("xp");
            int chance = rewardSection.getInt("xp-chance", 100);
            rewards.add(new XPReward(xp, chance));
        }
        
        // Money Reward
        if (rewardSection.contains("money")) {
            double money = rewardSection.getDouble("money");
            int chance = rewardSection.getInt("money-chance", 100);
            rewards.add(new MoneyReward(plugin, money, chance));
        }
        
        // Item Rewards
        if (rewardSection.contains("items")) {
            List<?> itemsList = rewardSection.getList("items");
            if (itemsList != null) {
                for (Object obj : itemsList) {
                    if (obj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> itemMap = (Map<String, Object>) obj;
                        ItemReward itemReward = loadItemReward(itemMap);
                        if (itemReward != null) {
                            rewards.add(itemReward);
                        }
                    }
                }
            }
        }
        
        // Command Rewards
        if (rewardSection.contains("commands")) {
            List<String> commands = rewardSection.getStringList("commands");
            int commandChance = rewardSection.getInt("command-chance", 100);
            for (String command : commands) {
                rewards.add(new CommandReward(command, commandChance));
            }
        }
        
        return rewards;
    }
    
    /**
     * Load reward entries for a specific quest from configuration.
     * 
     * @param rewardSection Reward configuration section
     * @return List of RewardEntry objects
     */
    private List<RewardEntry> loadRewardEntriesForQuest(ConfigurationSection rewardSection) {
        List<RewardEntry> rewardEntries = new ArrayList<>();
        
        if (rewardSection == null) {
            return rewardEntries;
        }
        
        // XP Reward
        if (rewardSection.contains("xp")) {
            int xp = rewardSection.getInt("xp");
            Map<String, Object> data = Map.of("amount", xp);
            rewardEntries.add(new RewardEntry(RewardEntry.RewardType.XP, data));
        }
        
        // Money Reward
        if (rewardSection.contains("money")) {
            double money = rewardSection.getDouble("money");
            Map<String, Object> data = Map.of("amount", money);
            rewardEntries.add(new RewardEntry(RewardEntry.RewardType.MONEY, data));
        }
        
        // Item Rewards
        if (rewardSection.contains("items")) {
            List<?> itemsList = rewardSection.getList("items");
            if (itemsList != null) {
                for (Object obj : itemsList) {
                    if (obj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> itemMap = (Map<String, Object>) obj;
                        
                        String materialString = (String) itemMap.get("material");
                        if (materialString != null) {
                            Map<String, Object> data = new HashMap<>(itemMap);
                            data.putIfAbsent("amount", 1);
                            rewardEntries.add(new RewardEntry(RewardEntry.RewardType.ITEM, data));
                        }
                    }
                }
            }
        }
        
        // Command Rewards
        if (rewardSection.contains("commands")) {
            List<String> commands = rewardSection.getStringList("commands");
            for (String command : commands) {
                Map<String, Object> data = Map.of("command", command);
                rewardEntries.add(new RewardEntry(RewardEntry.RewardType.COMMAND, data));
            }
        }
        
        return rewardEntries;
    }
    
    /**
     * Load an item reward from a configuration map.
     * 
     * @param itemMap Item configuration map
     * @return ItemReward, or null if invalid
     */
    private ItemReward loadItemReward(Map<String, Object> itemMap) {
        String materialString = (String) itemMap.get("material");
        if (materialString == null) {
            return null;
        }
        
        // Check if this is a custom item (MMOItems)
        CustomItemManager customItemManager = plugin.getCustomItemManager();
        if (customItemManager != null && customItemManager.isCustomItem(materialString)) {
            ItemStack customItem = customItemManager.parseCustomItem(materialString);
            if (customItem != null) {
                int amount = itemMap.containsKey("amount") ? (int) itemMap.get("amount") : 1;
                int chance = itemMap.containsKey("chance") ? (int) itemMap.get("chance") : 100;
                
                // Custom items already have their properties set
                return new ItemReward(customItem, amount, chance);
            } else {
                plugin.getLogger().log(Level.WARNING, "Could not parse custom item: {0}", materialString);
                return null;
            }
        }
        
        // Vanilla item handling with NBT-style customization
        try {
            Material material = Material.valueOf(materialString.toUpperCase());
            int amount = itemMap.containsKey("amount") ? (int) itemMap.get("amount") : 1;
            int chance = itemMap.containsKey("chance") ? (int) itemMap.get("chance") : 100;
            
            // Check if item has any NBT-style properties
            boolean hasCustomProperties = itemMap.containsKey("name") || 
                                         itemMap.containsKey("lore") || 
                                         itemMap.containsKey("enchantments") ||
                                         itemMap.containsKey("flags") ||
                                         itemMap.containsKey("unbreakable") ||
                                         itemMap.containsKey("custom-model-data");
            
            if (hasCustomProperties) {
                // Use NBTItemBuilder for items with custom properties
                com.soaps.quest.utils.NBTItemBuilder builder = new com.soaps.quest.utils.NBTItemBuilder(material, amount);
                
                // Set name
                String name = (String) itemMap.get("name");
                if (name != null) {
                    builder.setName(name);
                }
                
                // Set lore
                @SuppressWarnings("unchecked")
                List<String> lore = (List<String>) itemMap.get("lore");
                if (lore != null) {
                    builder.setLore(lore);
                }
                
                // Add enchantments
                @SuppressWarnings("unchecked")
                List<String> enchantList = (List<String>) itemMap.get("enchantments");
                if (enchantList != null) {
                    Map<Enchantment, Integer> enchantments = com.soaps.quest.utils.NBTItemBuilder.parseEnchantments(enchantList);
                    builder.addEnchantments(enchantments);
                }
                
                // Add flags
                @SuppressWarnings("unchecked")
                List<String> flagsList = (List<String>) itemMap.get("flags");
                if (flagsList != null) {
                    builder.addFlags(com.soaps.quest.utils.NBTItemBuilder.parseFlags(flagsList));
                }
                
                // Set unbreakable
                if (itemMap.containsKey("unbreakable") && itemMap.get("unbreakable") instanceof Boolean) {
                    builder.setUnbreakable((Boolean) itemMap.get("unbreakable"));
                }
                
                // Set custom model data
                if (itemMap.containsKey("custom-model-data") && itemMap.get("custom-model-data") instanceof Number) {
                    builder.setCustomModelData(((Number) itemMap.get("custom-model-data")).intValue());
                }
                
                ItemStack customizedItem = builder.build();
                return new ItemReward(customizedItem, chance);
                
            } else {
                // Simple vanilla item without custom properties
                ItemStack simpleItem = new ItemStack(material, amount);
                return new ItemReward(simpleItem, chance);
            }
            
        } catch (IllegalArgumentException e) {
            plugin.getLogger().log(Level.WARNING, "Invalid material: {0}", materialString);
            return null;
        }
    }
    
    /**
     * Give all rewards for a quest to a player.
     * 
     * @param questId Quest identifier
     * @param player Player to receive rewards
     * @return True if all rewards were given successfully
     */
    public boolean giveRewards(String questId, Player player) {
        List<Reward> rewards = questRewards.get(questId);
        if (rewards == null || rewards.isEmpty()) {
            return true; // No rewards to give
        }
        
        // Get the difficulty multiplier for this quest
        double rewardMultiplier = 1.0;
        Quest quest = plugin.getQuestManager().getQuest(questId);
        if (quest != null && quest.getDifficulty() != null) {
            DifficultyManager.Difficulty difficultyConfig = plugin.getDifficultyManager().getDifficulty(quest.getDifficulty());
            if (difficultyConfig != null) {
                rewardMultiplier = difficultyConfig.rewardMultiplier;
            }
        }
        
        boolean allSuccess = true;
        int rewardsFailed = 0;
        
        for (Reward reward : rewards) {
            // Apply difficulty multiplier if needed
            Reward scaledReward = reward;
            if (rewardMultiplier != 1.0) {
                scaledReward = scaleReward(reward, rewardMultiplier);
            }
            
            // Use giveWithChance to respect chance system
            boolean given = scaledReward.giveWithChance(player);
            
            if (!given) {
                // Check if it failed due to chance or actual error
                if (reward.getChance() < 100) {
                    // Failed due to chance, not an error
                    rewardsFailed++;
                } else {
                    // Failed for other reason, log warning
                    plugin.getLogger().log(Level.WARNING, "Failed to give reward to {0}: {1}", new Object[]{player.getName(), reward.getDescription()});
                    allSuccess = false;
                }
            }
        }
        
        // Notify player if some rewards were not received due to chance
        if (rewardsFailed > 0) {
            net.kyori.adventure.text.Component message = plugin.getMessageManager().getMessage("reward-chance-failed");
            if (message != null) {
                player.sendMessage(message);
            }
        }
        
        return allSuccess;
    }
    
    /**
     * Scale a reward by a difficulty multiplier.
     * Creates a new scaled version of the reward.
     * 
     * @param reward The original reward
     * @param multiplier The multiplier to apply
     * @return Scaled reward instance
     */
    private Reward scaleReward(Reward reward, double multiplier) {
        switch (reward) {
            case XPReward xpReward -> {
                int scaledAmount = (int) Math.ceil(xpReward.getAmount() * multiplier);
                return new XPReward(Math.max(1, scaledAmount), xpReward.getChance());
            }
            case MoneyReward moneyReward -> {
                double scaledAmount = moneyReward.getAmount() * multiplier;
                return new MoneyReward(plugin, Math.max(0.01, scaledAmount), moneyReward.getChance());
            }
            case ItemReward itemReward -> {
                ItemStack originalItem = itemReward.getItem();
                int scaledAmount = (int) Math.ceil(originalItem.getAmount() * multiplier);
                ItemStack scaledItem = originalItem.clone();
                scaledItem.setAmount(Math.max(1, scaledAmount));
                return new ItemReward(scaledItem, Math.max(1, scaledAmount), itemReward.getChance());
            }
            default -> {
            }
        }
        
        // For other reward types (commands, etc.), return as-is
        return reward;
    }
    
    /**
     * Add an item reward to a quest in the configuration.
     * 
     * @param questId Quest identifier
     * @param item ItemStack to add as reward
     * @return True if added successfully
     */
    public boolean addItemReward(String questId, ItemStack item) {
        // Load quests.yml
        File questsFile = new File(plugin.getDataFolder(), "quests.yml");
        YamlConfiguration questsConfig = YamlConfiguration.loadConfiguration(questsFile);
        
        String path = "quests." + questId + ".reward.items";
        List<Map<String, Object>> items = new ArrayList<>();
        
        // Load existing items
        if (questsConfig.contains(path)) {
            List<?> existingItems = questsConfig.getList(path);
            if (existingItems != null) {
                for (Object obj : existingItems) {
                    if (obj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> itemMap = (Map<String, Object>) obj;
                        items.add(itemMap);
                    }
                }
            }
        }
        
        // Create new item map
        Map<String, Object> newItem = new HashMap<>();
        newItem.put("material", item.getType().name());
        newItem.put("amount", item.getAmount());
        
        if (item.getItemMeta() != null) {
            if (item.getItemMeta().hasDisplayName()) {
                newItem.put("name", net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                    .legacySection().serialize(item.getItemMeta().displayName()));
            }
            
            if (item.getItemMeta().hasLore() && item.getItemMeta().lore() != null) {
                List<String> lore = new ArrayList<>();
                List<net.kyori.adventure.text.Component> itemLore = item.getItemMeta().lore();
                if (itemLore != null) { // Additional null safety even after hasLore check
                    for (net.kyori.adventure.text.Component component : itemLore) {
                        lore.add(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                                .legacySection().serialize(component));
                    }
                }
                newItem.put("lore", lore);
            }
            
            if (item.getItemMeta().hasEnchants()) {
                List<String> enchants = new ArrayList<>();
                for (Map.Entry<Enchantment, Integer> entry : item.getItemMeta().getEnchants().entrySet()) {
                    enchants.add(entry.getKey().getKey().getKey().toUpperCase() + ":" + entry.getValue());
                }
                newItem.put("enchantments", enchants);
            }
        }
        
        items.add(newItem);
        questsConfig.set(path, items);
        
        // Save quests.yml
        try {
            questsConfig.save(questsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save quests.yml", e);
            return false;
        }
        
        // Reload rewards
        loadRewards();
        
        return true;
    }
    
    /**
     * Remove a reward from a quest by index (unified system).
     * 
     * @param questId Quest identifier
     * @param index Reward index (1-based to match display)
     * @return True if removed successfully
     */
    public boolean removeReward(String questId, int index) {
        List<RewardEntry> rewardEntries = getRewardList(questId);
        
        // Convert to 0-based index
        int arrayIndex = index - 1;
        
        if (arrayIndex < 0 || arrayIndex >= rewardEntries.size()) {
            return false;
        }
        
        // Load quests.yml
        File questsFile = new File(plugin.getDataFolder(), "quests.yml");
        YamlConfiguration questsConfig = YamlConfiguration.loadConfiguration(questsFile);
        
        RewardEntry toRemove = rewardEntries.get(arrayIndex);
        String rewardPath = "quests." + questId + ".reward";
        
        switch (toRemove.getType()) {
            case XP -> questsConfig.set(rewardPath + ".xp", null);
            case MONEY -> questsConfig.set(rewardPath + ".money", null);
            case ITEM -> {
                // For items, we need to remove the specific item from the list
                List<?> items = questsConfig.getList(rewardPath + ".items");
                if (items != null) {
                    // Count item rewards before this index
                    int itemIndex = 0;
                    for (int i = 0; i < arrayIndex; i++) {
                        if (rewardEntries.get(i).getType() == RewardEntry.RewardType.ITEM) {
                            itemIndex++;
                        }
                    }
                    if (itemIndex < items.size()) {
                        items.remove(itemIndex);
                        questsConfig.set(rewardPath + ".items", items);
                    }
                }
            }
            case COMMAND -> {
                // For commands, we need to remove the specific command from the list
                List<String> commands = questsConfig.getStringList(rewardPath + ".commands");
                if (!commands.isEmpty()) {
                    // Count command rewards before this index
                    int commandIndex = 0;
                    for (int i = 0; i < arrayIndex; i++) {
                        if (rewardEntries.get(i).getType() == RewardEntry.RewardType.COMMAND) {
                            commandIndex++;
                        }
                    }
                    if (commandIndex < commands.size()) {
                        commands.remove(commandIndex);
                        questsConfig.set(rewardPath + ".commands", commands);
                    }
                }
            }
        }
        
        // Save quests.yml
        try {
            questsConfig.save(questsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save quests.yml", e);
            return false;
        }
        
        loadRewards();
        return true;
    }
    
    /**
     * Get all rewards for a quest.
     * 
     * @param questId Quest identifier
     * @return List of Reward objects
     */
    public List<Reward> getRewards(String questId) {
        return questRewards.getOrDefault(questId, new ArrayList<>());
    }
    
    /**
     * Get all reward entries for a quest (unified format).
     * 
     * @param questId Quest identifier
     * @return List of RewardEntry objects
     */
    public List<RewardEntry> getRewardList(String questId) {
        return questRewardEntries.getOrDefault(questId, new ArrayList<>());
    }
    
    /**
     * Add an XP reward to a quest.
     * 
     * @param questId Quest identifier
     * @param amount XP amount
     * @return True if added successfully
     */
    public boolean addXPReward(String questId, int amount) {
        // Load quests.yml
        File questsFile = new File(plugin.getDataFolder(), "quests.yml");
        YamlConfiguration questsConfig = YamlConfiguration.loadConfiguration(questsFile);
        
        String path = "quests." + questId + ".reward.xp";
        questsConfig.set(path, amount);
        
        // Save quests.yml
        try {
            questsConfig.save(questsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save quests.yml", e);
            return false;
        }
        
        loadRewards();
        return true;
    }
    
    /**
     * Add a money reward to a quest.
     * 
     * @param questId Quest identifier
     * @param amount Money amount
     * @return True if added successfully
     */
    public boolean addMoneyReward(String questId, double amount) {
        // Load quests.yml
        File questsFile = new File(plugin.getDataFolder(), "quests.yml");
        YamlConfiguration questsConfig = YamlConfiguration.loadConfiguration(questsFile);
        
        String path = "quests." + questId + ".reward.money";
        questsConfig.set(path, amount);
        
        // Save quests.yml
        try {
            questsConfig.save(questsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save quests.yml", e);
            return false;
        }
        
        loadRewards();
        return true;
    }
    
    /**
     * Add a command reward to a quest.
     * 
     * @param questId Quest identifier
     * @param command Command to execute
     * @return True if added successfully
     */
    public boolean addCommandReward(String questId, String command) {
        // Load quests.yml
        File questsFile = new File(plugin.getDataFolder(), "quests.yml");
        YamlConfiguration questsConfig = YamlConfiguration.loadConfiguration(questsFile);
        
        String path = "quests." + questId + ".reward.commands";
        List<String> commands = questsConfig.getStringList(path);
        commands.add(command);
        questsConfig.set(path, commands);
        
        // Save quests.yml
        try {
            questsConfig.save(questsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save quests.yml", e);
            return false;
        }
        
        loadRewards();
        return true;
    }
    
    /**
     * Reload all rewards from configuration.
     */
    public void reload() {
        loadRewards();
    }
}
