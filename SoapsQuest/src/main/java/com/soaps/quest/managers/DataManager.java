package com.soaps.quest.managers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.quests.QuestProgress;

/**
 * Manages player quest progress data persistence using UUID-based quest instances.
 * Each quest paper has a unique UUID, allowing multiple quests of the same type.
 * Handles loading, saving, and automatic saving at intervals.
 */
public final class DataManager {
    
    private final SoapsQuest plugin;
    private File dataFile;
    private FileConfiguration dataConfig;
    private BukkitRunnable autosaveTask;
    
    // Maps player UUID to their active quest instances (quest instance UUID -> QuestProgress)
    // This allows players to have multiple quest papers with separate progress tracking
    private final Map<UUID, Map<UUID, QuestProgress>> playerActiveQuests;
    
    /**
     * Constructor for DataManager.
     * 
     * @param plugin Plugin instance
     */
    public DataManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.playerActiveQuests = new HashMap<>();
        loadData();
    }
    
    /**
     * Load player data from playerdata.yml.
     * Loads UUID-based quest instances per player.
     */
    public void loadData() {
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        dataFile = new File(dataFolder, "playerdata.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(java.util.logging.Level.SEVERE, "Could not create playerdata.yml!", e);
            }
        }
        
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        
        // Load quest instances per player
        loadQuestInstances();
    }
    
    /**
     * Load saved quest instances from file.
     * Each player has multiple quest instances tracked by unique UUIDs.
     * Completed/redeemed quests are automatically removed during load to prevent clutter.
     */
    private void loadQuestInstances() {
        playerActiveQuests.clear();
        
        ConfigurationSection playersSection = dataConfig.getConfigurationSection("players");
        if (playersSection == null) {
            return;
        }
        
        for (String playerUuidStr : playersSection.getKeys(false)) {
            try {
                UUID playerUuid = UUID.fromString(playerUuidStr);
                Map<UUID, QuestProgress> activeQuests = new HashMap<>();
                
                ConfigurationSection questsSection = dataConfig.getConfigurationSection("players." + playerUuidStr + ".quests");
                if (questsSection == null) {
                    continue;
                }
                
                for (String questInstanceUuidStr : questsSection.getKeys(false)) {
                    try {
                        UUID questInstanceUuid = UUID.fromString(questInstanceUuidStr);
                        String path = "players." + playerUuidStr + ".quests." + questInstanceUuidStr;
                        
                        String questId = dataConfig.getString(path + ".questId");
                        int progress = dataConfig.getInt(path + ".progress", 0);
                        int required = dataConfig.getInt(path + ".required", 1);
                        boolean redeemed = dataConfig.getBoolean(path + ".redeemed", false);
                        
                        // Load owner UUID if present (for ownership-locked quests)
                        UUID ownerUuid = null;
                        String ownerUuidStr = dataConfig.getString(path + ".owner");
                        if (ownerUuidStr != null) {
                            try {
                                ownerUuid = UUID.fromString(ownerUuidStr);
                            } catch (IllegalArgumentException e) {
                                plugin.getLogger().log(Level.WARNING, "Invalid owner UUID in quest data: {0}", ownerUuidStr);
                            }
                        }
                        
                        // Skip loading redeemed quests to prevent clutter (they're completed)
                        if (redeemed) {
                            continue;
                        }
                        
                        if (questId != null) {
                            // Load per-objective progress
                            java.util.Map<String, Integer> objectiveProgress = new java.util.HashMap<>();
                            ConfigurationSection objectivesSection = dataConfig.getConfigurationSection(path + ".objectives");
                            if (objectivesSection != null) {
                                for (String objectiveId : objectivesSection.getKeys(false)) {
                                    int objProgress = dataConfig.getInt(path + ".objectives." + objectiveId, 0);
                                    objectiveProgress.put(objectiveId, objProgress);
                                }
                            }
                            
                            QuestProgress questProgress = new QuestProgress(
                                questInstanceUuid,
                                questId,
                                progress,
                                required,
                                redeemed,
                                ownerUuid,
                                objectiveProgress
                            );
                            
                            // Load current objective index for sequential quests
                            int currentObjectiveIndex = dataConfig.getInt(path + ".currentObjectiveIndex", 0);
                            questProgress.setCurrentObjectiveIndex(currentObjectiveIndex);
                            
                            activeQuests.put(questInstanceUuid, questProgress);
                        }
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().log(Level.WARNING, "Invalid quest instance UUID in playerdata.yml: {0}", questInstanceUuidStr);
                    }
                }
                
                if (!activeQuests.isEmpty()) {
                    playerActiveQuests.put(playerUuid, activeQuests);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().log(Level.WARNING, "Invalid player UUID in playerdata.yml: {0}", playerUuidStr);
            }
        }
        
        plugin.getLogger().log(Level.INFO, "Loaded {0} players with active quest instances", playerActiveQuests.size());
    }
    
    /**
     * Save all player quest progress to file.
     * Only saves active (non-redeemed) quest instances to prevent clutter.
     */
    public void saveData() {
        // Clear old data
        dataConfig.set("players", null);
        
        // Save current quest instances per player
        for (Map.Entry<UUID, Map<UUID, QuestProgress>> playerEntry : playerActiveQuests.entrySet()) {
            UUID playerUuid = playerEntry.getKey();
            Map<UUID, QuestProgress> activeQuests = playerEntry.getValue();
            
            for (Map.Entry<UUID, QuestProgress> questEntry : activeQuests.entrySet()) {
                UUID questInstanceUuid = questEntry.getKey();
                QuestProgress progress = questEntry.getValue();
                
                // Skip saving redeemed quests (they're complete, no need to persist)
                if (progress.isRedeemed()) {
                    continue;
                }
                
                String path = "players." + playerUuid.toString() + ".quests." + questInstanceUuid.toString();
                dataConfig.set(path + ".questId", progress.getQuestId());
                dataConfig.set(path + ".progress", progress.getCurrentProgress());
                dataConfig.set(path + ".required", progress.getRequiredAmount());
                dataConfig.set(path + ".redeemed", progress.isRedeemed());
                // Save owner UUID if quest is bound to a player
                if (progress.getOwnerUUID() != null) {
                    dataConfig.set(path + ".owner", progress.getOwnerUUID().toString());
                }
                // Save current objective index for sequential quests
                dataConfig.set(path + ".currentObjectiveIndex", progress.getCurrentObjectiveIndex());
                // Save per-objective progress
                if (!progress.getAllObjectiveProgress().isEmpty()) {
                    for (java.util.Map.Entry<String, Integer> entry : progress.getAllObjectiveProgress().entrySet()) {
                        dataConfig.set(path + ".objectives." + entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        
        // Save to file synchronously
        saveDataFile();
    }
    
    /**
     * Save all player quest progress asynchronously.
     */
    public void saveDataAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveData();
            }
        }.runTaskAsynchronously(plugin);
    }
    
    /**
     * Write data config to file.
     */
    private void saveDataFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(java.util.logging.Level.SEVERE, "Could not save playerdata.yml!", e);
        }
    }
    
    /**
     * Start automatic saving task.
     * Prevents duplicate tasks by cancelling existing task before starting new one.
     */
    public void startAutosave() {
        // Stop any existing autosave task to prevent duplicates on reload
        stopAutosave();
        
        int interval = plugin.getConfig().getInt("autosave-interval", 5);
        long ticks = interval * 60 * 20; // Convert minutes to ticks
        
        autosaveTask = new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getLogger().info("Auto-saving player data...");
                saveDataAsync();
            }
        };
        
        autosaveTask.runTaskTimerAsynchronously(plugin, ticks, ticks);
        plugin.getLogger().log(Level.INFO, "Autosave enabled (interval: {0} minutes)", interval);
    }
    
    /**
     * Stop automatic saving task.
     */
    public void stopAutosave() {
        if (autosaveTask != null) {
            autosaveTask.cancel();
            autosaveTask = null;
        }
    }
    
    /**
     * Reload data from file.
     * Note: This will overwrite current in-memory progress!
     */
    public void reload() {
        loadData();
    }
    
    /**
     * Get the data configuration.
     * 
     * @return Data FileConfiguration
     */
    public FileConfiguration getConfig() {
        return dataConfig;
    }
    
    /**
     * Register a new quest instance for a player.
     * Called when a quest paper is given to a player.
     * 
     * @param player The player
     * @param questInstanceUuid Unique UUID for this quest instance
     * @param questId The quest ID
     * @param requiredAmount Required amount to complete
     */
    public void registerQuestInstance(Player player, UUID questInstanceUuid, String questId, int requiredAmount) {
        UUID playerUuid = player.getUniqueId();
        playerActiveQuests.putIfAbsent(playerUuid, new HashMap<>());
        
        QuestProgress progress = new QuestProgress(questInstanceUuid, questId, requiredAmount);
        playerActiveQuests.get(playerUuid).put(questInstanceUuid, progress);
    }
    
    /**
     * Get a specific quest instance by UUID.
     * 
     * @param player The player
     * @param questInstanceUuid Quest instance UUID
     * @return QuestProgress, or null if not found
     */
    public QuestProgress getQuestInstance(Player player, UUID questInstanceUuid) {
        Map<UUID, QuestProgress> quests = playerActiveQuests.get(player.getUniqueId());
        if (quests == null) {
            return null;
        }
        return quests.get(questInstanceUuid);
    }
    
    /**
     * Get a specific quest instance by UUID (using player UUID).
     * 
     * @param playerUuid The player UUID
     * @param questInstanceUuid Quest instance UUID
     * @return QuestProgress, or null if not found
     */
    public QuestProgress getQuestInstance(UUID playerUuid, UUID questInstanceUuid) {
        Map<UUID, QuestProgress> quests = playerActiveQuests.get(playerUuid);
        if (quests == null) {
            return null;
        }
        return quests.get(questInstanceUuid);
    }
    
    /**
     * Get all active quest instances for a player.
     * 
     * @param player The player
     * @return Map of quest instance UUID to QuestProgress
     */
    public Map<UUID, QuestProgress> getActiveQuests(Player player) {
        return playerActiveQuests.getOrDefault(player.getUniqueId(), new HashMap<>());
    }
    
    /**
     * Find the owner UUID of a quest instance.
     * Searches all players' data to find who owns this quest instance.
     * 
     * @param questInstanceUuid Quest instance UUID to search for
     * @return Owner UUID if found and quest is bound, null otherwise
     */
    public UUID findQuestInstanceOwner(UUID questInstanceUuid) {
        for (Map.Entry<UUID, Map<UUID, QuestProgress>> playerEntry : playerActiveQuests.entrySet()) {
            QuestProgress progress = playerEntry.getValue().get(questInstanceUuid);
            if (progress != null && progress.isBound()) {
                return progress.getOwnerUUID();
            }
        }
        return null;
    }
    
    /**
     * Get a quest instance for a locked quest by searching in the owner's data.
     * For locked quests, the progress is stored with the original owner, not the current holder.
     * 
     * @param questInstanceUuid Quest instance UUID to search for
     * @return QuestProgress if found, null otherwise
     */
    public QuestProgress getLockedQuestInstance(UUID questInstanceUuid) {
        for (Map.Entry<UUID, Map<UUID, QuestProgress>> playerEntry : playerActiveQuests.entrySet()) {
            QuestProgress progress = playerEntry.getValue().get(questInstanceUuid);
            if (progress != null) {
                return progress;
            }
        }
        return null;
    }
    
    /**
     * Remove a quest instance (called when redeemed).
     * This allows the player to receive the same quest type again.
     * 
     * @param player The player
     * @param questInstanceUuid Quest instance UUID to remove
     */
    public void removeQuestInstance(Player player, UUID questInstanceUuid) {
        Map<UUID, QuestProgress> quests = playerActiveQuests.get(player.getUniqueId());
        if (quests != null) {
            quests.remove(questInstanceUuid);
            
            // Clean up empty player entries
            if (quests.isEmpty()) {
                playerActiveQuests.remove(player.getUniqueId());
            }
        }
    }
    
    /**
     * Increment progress for a specific quest instance.
     * 
     * @param player The player
     * @param questInstanceUuid Quest instance UUID
     * @return True if progress was incremented, false if quest not found
     */
    public boolean incrementQuestProgress(Player player, UUID questInstanceUuid) {
        QuestProgress progress = getQuestInstance(player, questInstanceUuid);
        if (progress != null && !progress.isRedeemed()) {
            progress.incrementProgress();
            return true;
        }
        return false;
    }
    
    /**
     * Mark a quest instance as redeemed.
     * The instance will be removed on next save to prevent clutter.
     * 
     * @param player The player
     * @param questInstanceUuid Quest instance UUID
     */
    public void markQuestRedeemed(Player player, UUID questInstanceUuid) {
        QuestProgress progress = getQuestInstance(player, questInstanceUuid);
        if (progress != null) {
            progress.setRedeemed();
            // Remove immediately to allow new quests of same type
            removeQuestInstance(player, questInstanceUuid);
        }
    }
    
    /**
     * Transfer or adopt a quest instance for unlocked quests.
     * This allows quest papers to be traded between players.
     * The quest progress is copied to the new player's data.
     * Also registers the quest with the QuestManager's queue system.
     * 
     * @param newPlayer The player receiving the quest
     * @param questInstanceUuid The quest instance UUID from the paper
     * @param questId The quest ID
     * @param required The required amount
     * @return QuestProgress for the new player, or null if transfer failed
     */
    public QuestProgress transferOrAdoptQuest(Player newPlayer, UUID questInstanceUuid, String questId, int required) {
        // Check if the new player already has this quest instance
        QuestProgress existingProgress = getQuestInstance(newPlayer, questInstanceUuid);
        if (existingProgress != null) {
            return existingProgress;
        }
        
        // Look for the quest progress in any other player's data and remove it
        QuestProgress originalProgress = null;
        UUID originalOwnerUuid = null;
        
        for (Map.Entry<UUID, Map<UUID, QuestProgress>> entry : playerActiveQuests.entrySet()) {
            UUID playerUuid = entry.getKey();
            Map<UUID, QuestProgress> playerQuests = entry.getValue();
            QuestProgress progress = playerQuests.get(questInstanceUuid);
            
            if (progress != null) {
                originalProgress = progress;
                originalOwnerUuid = playerUuid;
                break;
            }
        }
        
        // Create new progress for the new player, preserving the original progress if found
        int currentProgress = (originalProgress != null) ? originalProgress.getCurrentProgress() : 0;
        boolean redeemed = (originalProgress != null) && originalProgress.isRedeemed();
        
        // Remove quest from original owner's data (for unlocked quests, progress follows the paper)
        if (originalOwnerUuid != null) {
            Map<UUID, QuestProgress> originalPlayerQuests = playerActiveQuests.get(originalOwnerUuid);
            if (originalPlayerQuests != null) {
                originalPlayerQuests.remove(questInstanceUuid);
                // Clean up empty player entries
                if (originalPlayerQuests.isEmpty()) {
                    playerActiveQuests.remove(originalOwnerUuid);
                }
            }
        }
        
        // Create new progress for this player (no owner for unlocked quests)
        QuestProgress newProgress = new QuestProgress(
            questInstanceUuid,
            questId,
            currentProgress,
            required,
            redeemed,
            null  // No owner for unlocked/transferred quests
        );
        
        // Add to player's active quests
        Map<UUID, QuestProgress> playerQuests = playerActiveQuests.get(newPlayer.getUniqueId());
        if (playerQuests == null) {
            playerQuests = new HashMap<>();
            playerActiveQuests.put(newPlayer.getUniqueId(), playerQuests);
        }
        playerQuests.put(questInstanceUuid, newProgress);
        
        // Update the quest paper's owner UUID in the player's inventory
        org.bukkit.inventory.ItemStack[] contents = newPlayer.getInventory().getContents();
        if (contents != null) {
            for (int i = 0; i < contents.length; i++) {
                org.bukkit.inventory.ItemStack item = contents[i];
                if (item == null) {
                    continue;
                }
                
                java.util.UUID paperInstanceUuid = com.soaps.quest.utils.QuestPaper.getQuestInstanceId(item);
                if (paperInstanceUuid != null && paperInstanceUuid.equals(questInstanceUuid)) {
                    // Transfer ownership of the paper
                    org.bukkit.inventory.ItemStack updatedPaper = com.soaps.quest.utils.QuestPaper.transferQuestPaperOwnership(
                        item, newPlayer, plugin.getPlayerUuidKey()
                    );
                    newPlayer.getInventory().setItem(i, updatedPaper);
                    break;
                }
            }
        }
        
        // Register with QuestManager's queue system
        plugin.getQuestManager().addQuestToQueue(newPlayer, questId, questInstanceUuid);
        
        // Save the data immediately
        saveDataAsync();
        
        return newProgress;
    }
}
