package com.soaps.quest.managers;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.objectives.ObjectiveRegistry;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import com.soaps.quest.quests.QuestTier;
import com.soaps.quest.utils.QuestPaper;

/**
 * Manages all quests loaded from configuration.
 * Handles quest creation, retrieval, validation, and multi-paper queue management.
 * 
 * Multi-Paper Queue System:
 * - Tracks active and queued quest papers per player and quest type
 * - Only papers in player inventory are considered valid
 * - Automatically activates next queued paper when active paper is completed/removed
 * - New quest papers of the same type are automatically queued behind active ones
 */
public class QuestManager {
    
    private final SoapsQuest plugin;
    private final Map<String, Quest> quests;
    
    // Multi-paper queue management
    // Maps: Player UUID -> Quest Type -> Queue of Quest Instance UUIDs
    // First UUID in queue = active quest, rest = queued quests
    private final Map<UUID, Map<String, Queue<UUID>>> playerQuestQueues;
    
    /**
     * Constructor for QuestManager.
     * 
     * @param plugin Plugin instance
     */
    public QuestManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.quests = new HashMap<>();
        this.playerQuestQueues = new HashMap<>();
    }
    
    /**
     * Load all quests from quests.yml and generated.yml.
     */
    public void loadQuests() {
        quests.clear();
        
        // Load quests.yml
        File questsFile = new File(plugin.getDataFolder(), "quests.yml");
        if (!questsFile.exists()) {
            plugin.saveResource("quests.yml", false);
        }
        
        int loaded = 0;
        loaded += loadQuestsFromFile(questsFile, "quests.yml", false);
        
        // Load generated.yml if it exists
        File generatedFile = new File(plugin.getDataFolder(), "generated.yml");
        if (generatedFile.exists()) {
            loaded += loadQuestsFromFile(generatedFile, "generated.yml", true);
        }
        
        plugin.getLogger().log(Level.INFO, "Loaded {0} quests.", loaded);
    }
    
    /**
     * Load quests from a specific file.
     * 
     * @param file The file to load from
     * @param fileName Name of the file for logging
     * @param isGenerated Whether this is a generated quest file
     * @return Number of quests loaded
     */
    private int loadQuestsFromFile(File file, String fileName, boolean isGenerated) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        // For generated.yml, quests are at root level, not under "quests:" section
        ConfigurationSection questsSection;
        if (isGenerated) {
            questsSection = config.getRoot();
        } else {
            questsSection = config.getConfigurationSection("quests");
            if (questsSection == null) {
                plugin.getLogger().log(Level.WARNING, "No quests section found in {0}!", new Object[]{fileName});
                return 0;
            }
        }
        
        int loaded = 0;
        if (questsSection == null) {
            plugin.getLogger().log(Level.WARNING, "No quests section found in {0}!", new Object[]{fileName});
            return 0;
        }
        Set<String> questKeys = questsSection.getKeys(false);
        
        for (String questId : questKeys) {
            try {
                ConfigurationSection questSection = questsSection.getConfigurationSection(questId);
                Quest quest = loadQuest(questId, questSection);
                if (quest != null) {
                    quests.put(questId, quest);
                    loaded++;
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error loading quest ''{0}'' from {1}: {2}", 
                    new Object[]{questId, fileName, e.getMessage()});
            }
        }
        
        return loaded;
    }
    
    /**
     * Load a single quest from configuration.
     * Only supports the modern objectives list format.
     * 
     * @param questId Quest identifier
     * @param section Configuration section
     * @return Loaded Quest, or null if invalid
     */
    private Quest loadQuest(String questId, ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        
        // Only support the modern multi-objective format
        if (!section.contains("objectives")) {
            plugin.getLogger().log(Level.WARNING, "Quest ''{0}'' is missing ''objectives'' section! Legacy format is no longer supported.", questId);
            return null;
        }
        
        return loadQuestWithObjectives(questId, section);
    }
    
    /**
     * Load a quest with the new multi-objective format.
     * 
     * @param questId Quest identifier
     * @param section Configuration section
     * @return Loaded Quest with objectives, or null if invalid
     */
    private Quest loadQuestWithObjectives(String questId, ConfigurationSection section) {
        String display = section.getString("display", questId);
        
        // Load custom lore if present
        java.util.List<String> customLore = null;
        if (section.contains("lore")) {
            customLore = section.getStringList("lore");
        }
        
        // Load custom material if present (defaults to PAPER)
        Material material = Material.PAPER;
        if (section.contains("material")) {
            String materialString = section.getString("material");
            if (materialString != null) {
                try {
                    material = Material.valueOf(materialString.toUpperCase());
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().log(Level.WARNING, "Quest ''{0}'' has invalid material: {1}, defaulting to PAPER", new Object[]{questId, materialString});
                }
            }
        }
        
        // Load lock-to-player setting (defaults to true for safety)
        boolean lockToPlayer = section.getBoolean("lock-to-player", true);
        
        // Load sequential setting (defaults to false)
        boolean sequential = section.getBoolean("sequential", false);
        
        // Load optional permission requirement
        String permission = section.getString("permission", null);
        
        // Load quest tier (defaults to COMMON)
        String tierString = section.getString("tier", "COMMON");
        QuestTier tier = QuestTier.fromString(tierString);
        
        // Load quest difficulty (defaults to "normal")
        String difficulty = section.getString("difficulty", "normal");
        
        // Get difficulty multiplier for objectives (rewards are scaled when given)
        DifficultyManager.Difficulty difficultyConfig = plugin.getDifficultyManager().getDifficulty(difficulty);
        double objectiveMultiplier = difficultyConfig != null ? difficultyConfig.objectiveMultiplier : 1.0;
        
        // Load custom milestones
        List<Integer> milestones = section.getIntegerList("milestones");
        
        // Load quest conditions (optional)
        ConfigurationSection conditions = section.getConfigurationSection("conditions");
        
        // Load objectives list
        java.util.List<Map<?, ?>> objectivesList = section.getMapList("objectives");
        if (objectivesList.isEmpty()) {
            plugin.getLogger().log(Level.WARNING, "Quest ''{0}'' has objectives key but no objectives defined!", questId);
            return null;
        }
        
        java.util.List<Objective> objectives = new java.util.ArrayList<>();
        int objIndex = 0;
        
        for (Map<?, ?> objectiveMap : objectivesList) {
            // Create a temporary section from the map
            ConfigurationSection objSection = plugin.getConfig().createSection("temp_" + questId + "_" + objIndex);
            for (Map.Entry<?, ?> entry : objectiveMap.entrySet()) {
                objSection.set(String.valueOf(entry.getKey()), entry.getValue());
            }
            
            // Apply objective amount multiplier
            if (objSection.contains("amount") && objectiveMultiplier != 1.0) {
                int baseAmount = objSection.getInt("amount");
                int scaledAmount = (int) Math.ceil(baseAmount * objectiveMultiplier);
                objSection.set("amount", Math.max(1, scaledAmount));
            }
            
            // Apply quest-level milestones to this objective (if not already set)
            if (!objSection.contains("milestones") && !milestones.isEmpty()) {
                objSection.set("milestones", milestones);
            }
            
            String objectiveId = questId + "_obj_" + objIndex;
            String objectiveType = objSection.getString("type");
            
            // Check if this is a kill_mythicmob objective and MythicMobs is not installed
            if ("kill_mythicmob".equalsIgnoreCase(objectiveType) && !ObjectiveRegistry.isMythicMobsInstalled()) {
                plugin.getLogger().log(Level.WARNING, "Quest ''{0}'' objective {1} uses kill_mythicmob but MythicMobs plugin is not installed - skipping objective", 
                    new Object[]{questId, objIndex});
                objIndex++;
                continue;
            }
            
            // Validate objective configuration before creation
            ObjectiveRegistry.ValidationResult validation = ObjectiveRegistry.validateObjective(objSection);
            if (!validation.valid) {
                plugin.getLogger().log(Level.WARNING, 
                    "[Validation] Quest ''{0}'' objective {1} failed validation: {2}", 
                    new Object[]{questId, objIndex, validation.errorMessage});
                if (validation.missingFields != null && validation.missingFields.length > 0) {
                    plugin.getLogger().log(Level.WARNING, 
                        "[Validation] Missing fields: {0}", 
                        String.join(", ", validation.missingFields));
                }
                objIndex++;
                continue;
            }
            
            Objective objective = ObjectiveRegistry.createObjective(objectiveId, objSection);
            
            if (objective != null) {
                objectives.add(objective);
            } else {
                plugin.getLogger().log(Level.WARNING, "Quest ''{0}'' has invalid objective at index {1}", new Object[]{questId, objIndex});
            }
            
            objIndex++;
        }
        
        if (objectives.isEmpty()) {
            plugin.getLogger().log(Level.WARNING, "[Validation] Quest ''{0}'' skipped - no valid objectives", questId);
            return null;
        }
        
        // Create a new multi-objective quest using the new constructor
        // We need to create a concrete implementation since Quest is abstract
        return new Quest(questId, display, objectives, customLore, material, lockToPlayer, sequential, permission, tier, milestones, difficulty, conditions) {
            @Override
            public boolean checkProgress(Player player, Object context) {
                // Not used for multi-objective quests
                return false;
            }
            
            @Override
            public String getObjectiveDescription() {
                if (objectives.isEmpty()) {
                    return "No objectives";
                }
                // Return first objective description, or count if multiple
                if (objectives.size() == 1) {
                    return objectives.get(0).getDescription();
                }
                return objectives.size() + " objectives";
            }
        };
    }
    
    /**
     * Get a quest by its ID.
     * 
     * @param questId Quest identifier
     * @return Quest, or null if not found
     */
    public Quest getQuest(String questId) {
        return quests.get(questId);
    }
    
    /**
     * Get all quest IDs.
     * 
     * @return Set of quest IDs
     */
    public Set<String> getQuestIds() {
        return quests.keySet();
    }
    
    /**
     * Get all loaded quests.
     * 
     * @return Map of quest IDs to Quest objects
     */
    public Map<String, Quest> getAllQuests() {
        return new HashMap<>(quests);
    }
    
    /**
     * Check if a quest exists.
     * 
     * @param questId Quest identifier
     * @return True if quest exists
     */
    public boolean questExists(String questId) {
        return quests.containsKey(questId);
    }
    
    /**
     * Reload all quests from configuration.
     * Note: Player progress data is maintained separately in DataManager and is not affected by quest reloads.
     */
    public void reload() {
        // Simply reload quests - progress data is now managed by DataManager
        // The legacy Quest.playerProgress system is deprecated and no longer used
        loadQuests();
    }
    
    // ============================
    // Multi-Paper Queue Management
    // ============================
    
    /**
     * Add a quest paper to the queue for a player.
     * Handles inventory-aware tracking and configurable behavior.
     * 
     * @param player The player
     * @param questId The quest type ID
     * @param questInstanceUuid The unique quest instance UUID
     * @return Action taken: "activated" (became active), "queued" (added to queue), 
     *         "blocked" (not added due to config), or "replaced" (old quest cancelled)
     */
    public String addQuestToQueue(Player player, String questId, UUID questInstanceUuid) {
        UUID playerUuid = player.getUniqueId();
        
        // Ensure player has a queue map
        playerQuestQueues.putIfAbsent(playerUuid, new HashMap<>());
        Map<String, Queue<UUID>> questTypeQueues = playerQuestQueues.get(playerUuid);
        
        // Clean up the queue first by removing papers not in inventory
        cleanupQuestQueue(player, questId);
        
        // Get or create queue for this quest type
        Queue<UUID> questQueue = questTypeQueues.computeIfAbsent(questId, k -> new LinkedList<>());
        
        if (questQueue.isEmpty()) {
            // No active quest of this type, make this one active
            questQueue.offer(questInstanceUuid);
            updateQuestPaperStatus(player, questId, questInstanceUuid, true); // Mark as active
            return "activated";
        } else {
            // There's already an active quest of this type - always queue new ones
            questQueue.offer(questInstanceUuid);
            updateQuestPaperStatus(player, questId, questInstanceUuid, false); // Mark as queued
            return "queued";
        }
    }
    
    /**
     * Remove a quest from the queue and activate the next one if available.
     * Called when a quest is completed, redeemed, or the paper is removed.
     * 
     * @param player The player
     * @param questId The quest type ID
     * @param questInstanceUuid The quest instance UUID to remove
     */
    public void removeQuestFromQueue(Player player, String questId, UUID questInstanceUuid) {
        UUID playerUuid = player.getUniqueId();
        Map<String, Queue<UUID>> questTypeQueues = playerQuestQueues.get(playerUuid);
        
        if (questTypeQueues == null) {
            return;
        }
        
        Queue<UUID> questQueue = questTypeQueues.get(questId);
        if (questQueue == null) {
            return;
        }
        
        // Remove the quest from queue
        boolean wasActive = questQueue.peek() != null && questQueue.peek().equals(questInstanceUuid);
        questQueue.remove(questInstanceUuid);
        
        // If this was the active quest, activate the next one
        if (wasActive && !questQueue.isEmpty()) {
            UUID nextActiveUuid = questQueue.peek();
            updateQuestPaperStatus(player, questId, nextActiveUuid, true); // Mark next as active
            
            // Send activation message to player
            Quest quest = getQuest(questId);
            if (quest != null) {
                player.sendMessage(plugin.getMessageManager().getMessage("quest-activated", 
                    Map.of("quest", quest.getDisplay())));
            }
        }
        
        // Clean up empty queues
        if (questQueue.isEmpty()) {
            questTypeQueues.remove(questId);
        }
        if (questTypeQueues.isEmpty()) {
            playerQuestQueues.remove(playerUuid);
        }
    }
    
    /**
     * Get the active quest instance UUID for a specific quest type.
     * 
     * @param player The player
     * @param questId The quest type ID
     * @return Active quest instance UUID, or null if no active quest of this type
     */
    public UUID getActiveQuestInstance(Player player, String questId) {
        UUID playerUuid = player.getUniqueId();
        Map<String, Queue<UUID>> questTypeQueues = playerQuestQueues.get(playerUuid);
        
        if (questTypeQueues == null) {
            return null;
        }
        
        Queue<UUID> questQueue = questTypeQueues.get(questId);
        if (questQueue == null || questQueue.isEmpty()) {
            return null;
        }
        
        // Clean up queue first
        cleanupQuestQueue(player, questId);
        
        // Return the first (active) quest
        return questQueue.peek();
    }
    
    /**
     * Check if a quest instance is active (not queued).
     * 
     * @param player The player
     * @param questId The quest type ID
     * @param questInstanceUuid The quest instance UUID
     * @return True if this instance is active (first in queue)
     */
    public boolean isQuestActive(Player player, String questId, UUID questInstanceUuid) {
        UUID activeInstance = getActiveQuestInstance(player, questId);
        return activeInstance != null && activeInstance.equals(questInstanceUuid);
    }
    
    /**
     * Get the position of a quest in the queue (0 = active, 1+ = queued position).
     * 
     * @param player The player
     * @param questId The quest type ID
     * @param questInstanceUuid The quest instance UUID
     * @return Queue position (0-based), or -1 if not found
     */
    public int getQuestQueuePosition(Player player, String questId, UUID questInstanceUuid) {
        UUID playerUuid = player.getUniqueId();
        Map<String, Queue<UUID>> questTypeQueues = playerQuestQueues.get(playerUuid);
        
        if (questTypeQueues == null) {
            return -1;
        }
        
        Queue<UUID> questQueue = questTypeQueues.get(questId);
        if (questQueue == null) {
            return -1;
        }
        
        cleanupQuestQueue(player, questId);
        
        int position = 0;
        for (UUID uuid : questQueue) {
            if (uuid.equals(questInstanceUuid)) {
                return position;
            }
            position++;
        }
        
        return -1;
    }
    
    /**
     * Clean up a quest queue by removing quest instances that don't have 
     * corresponding papers in the player's inventory.
     * This ensures inventory-aware tracking.
     * 
     * @param player The player
     * @param questId The quest type ID
     */
    private void cleanupQuestQueue(Player player, String questId) {
        UUID playerUuid = player.getUniqueId();
        Map<String, Queue<UUID>> questTypeQueues = playerQuestQueues.get(playerUuid);
        
        if (questTypeQueues == null) {
            return;
        }
        
        Queue<UUID> questQueue = questTypeQueues.get(questId);
        if (questQueue == null) {
            return;
        }
        
        // Get all quest papers of this type in player's inventory
        java.util.Set<UUID> inventoryQuestUuids = getQuestPapersInInventory(player, questId);
        
        // Remove queue entries that don't have corresponding papers
        UUID oldActiveUuid = questQueue.peek();
        
        questQueue.removeIf(uuid -> {
            boolean shouldRemove = !inventoryQuestUuids.contains(uuid);
            if (shouldRemove) {
                // Also remove from DataManager
                plugin.getDataManager().removeQuestInstance(player, uuid);
            }
            return shouldRemove;
        });
        
        // If active quest was removed, activate the next one
        UUID newActiveUuid = questQueue.peek();
        if (oldActiveUuid != null && newActiveUuid != null && !oldActiveUuid.equals(newActiveUuid)) {
            updateQuestPaperStatus(player, questId, newActiveUuid, true); // Mark new active
        }
        
        // Clean up empty queues
        if (questQueue.isEmpty()) {
            questTypeQueues.remove(questId);
        }
        if (questTypeQueues.isEmpty()) {
            playerQuestQueues.remove(playerUuid);
        }
    }
    
    /**
     * Get all quest paper instance UUIDs of a specific type from player's inventory.
     * 
     * @param player The player
     * @param questId The quest type ID
     * @return Set of quest instance UUIDs found in inventory
     */
    private java.util.Set<UUID> getQuestPapersInInventory(Player player, String questId) {
        java.util.Set<UUID> foundUuids = new java.util.HashSet<>();
        
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) {
            return foundUuids;
        }
        
        for (ItemStack item : contents) {
            if (item == null) {
                continue;
            }
            
            // Check if this is a quest paper for the specified quest type
            String paperQuestId = QuestPaper.getQuestId(item, plugin.getQuestIdKey());
            if (paperQuestId == null || !paperQuestId.equals(questId)) {
                continue;
            }
            
            // For locked quests, check if it belongs to this player
            // For unlocked quests, accept papers from any player
            Quest quest = getQuest(questId);
            if (quest != null && quest.isLockToPlayer()) {
                if (!QuestPaper.belongsToPlayer(item, player, plugin.getPlayerUuidKey())) {
                    continue;
                }
            }
            
            // Get the quest instance UUID
            UUID instanceUuid = QuestPaper.getQuestInstanceId(item);
            if (instanceUuid != null) {
                foundUuids.add(instanceUuid);
            }
        }
        
        return foundUuids;
    }
    
    /**
     * Update quest paper status (active/queued) in player's inventory.
     * 
     * @param player The player
     * @param questId The quest type ID
     * @param questInstanceUuid The quest instance UUID
     * @param isActive True for active, false for queued
     */
    private void updateQuestPaperStatus(Player player, String questId, UUID questInstanceUuid, boolean isActive) {
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) {
            return;
        }
        
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null) {
                continue;
            }
            
            // Check if this is the quest paper we're looking for
            String paperQuestId = QuestPaper.getQuestId(item, plugin.getQuestIdKey());
            UUID paperInstanceUuid = QuestPaper.getQuestInstanceId(item);
            
            // Match by quest ID and instance UUID
            // For unlocked quests, we don't check paper ownership (will be transferred)
            if (paperQuestId != null && paperQuestId.equals(questId) && 
                paperInstanceUuid != null && paperInstanceUuid.equals(questInstanceUuid)) {
                
                // Update the paper's lore to reflect active/queued status
                Quest quest = getQuest(questId);
                QuestProgress progress = null;
                
                if (quest != null) {
                    if (quest.isLockToPlayer()) {
                        // For locked quests, find progress in owner's data
                        progress = plugin.getDataManager().getLockedQuestInstance(questInstanceUuid);
                    } else {
                        // For unlocked quests, check current player or transfer
                        progress = plugin.getDataManager().getQuestInstance(player, questInstanceUuid);
                        if (progress == null) {
                            progress = plugin.getDataManager().transferOrAdoptQuest(
                                player, questInstanceUuid, questId, quest.getRequiredAmount()
                            );
                        }
                    }
                }
                
                if (quest != null && progress != null) {
                    ItemStack updatedPaper = QuestPaper.updateQuestPaperWithStatus(
                        item, quest, progress, plugin.getMessageManager(), quest.getCustomLore(), isActive);
                    player.getInventory().setItem(i, updatedPaper);
                }
                
                break; // Found the paper, no need to continue
            }
        }
    }
    
    /**
     * Refresh all quest queues for a player by scanning their inventory.
     * Called when player joins or inventory changes significantly.
     * 
     * @param player The player
     */
    public void refreshPlayerQueues(Player player) {
        UUID playerUuid = player.getUniqueId();
        
        // Scan inventory and rebuild queues
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) {
            // Clear queues if inventory is null
            playerQuestQueues.remove(playerUuid);
            return;
        }
        
        // Group papers by quest type
        Map<String, java.util.List<UUID>> papersByType = new HashMap<>();
        
        for (ItemStack item : contents) {
            if (item == null) {
                continue;
            }
            
            String questId = QuestPaper.getQuestId(item, plugin.getQuestIdKey());
            UUID instanceUuid = QuestPaper.getQuestInstanceId(item);
            
            // For unlocked quests, accept papers even if they don't belong to this player yet
            if (questId != null && instanceUuid != null) {
                
                // Check if player has this quest instance, transfer if needed
                Quest quest = getQuest(questId);
                QuestProgress progress = null;
                
                if (quest != null) {
                    if (quest.isLockToPlayer()) {
                        // For locked quests, find progress in owner's data
                        progress = plugin.getDataManager().getLockedQuestInstance(instanceUuid);
                    } else {
                        // For unlocked quests, check current player or transfer
                        progress = plugin.getDataManager().getQuestInstance(player, instanceUuid);
                        if (progress == null) {
                            progress = plugin.getDataManager().transferOrAdoptQuest(
                                player, instanceUuid, questId, quest.getRequiredAmount()
                            );
                        }
                    }
                }
                
                // For locked quests that we don't have progress for, check if it's bound to someone else
                if (progress == null && quest != null && quest.isLockToPlayer()) {
                    // Search for the owner of this quest instance
                    UUID ownerUuid = plugin.getDataManager().findQuestInstanceOwner(instanceUuid);
                    if (ownerUuid != null && !ownerUuid.equals(player.getUniqueId())) {
                        // Found it - notify the player that this quest is locked to someone else
                        org.bukkit.OfflinePlayer owner = org.bukkit.Bukkit.getOfflinePlayer(ownerUuid);
                        String ownerName = owner.getName() != null ? owner.getName() : "Unknown";
                        player.sendMessage(plugin.getMessageManager().getMessage("quest-locked-to-other",
                            Map.of("player", ownerName)));
                    }
                }
                
                // Only add to queue if we have progress (either existing or transferred)
                if (progress != null) {
                    papersByType.computeIfAbsent(questId, k -> new java.util.ArrayList<>()).add(instanceUuid);
                }
            }
        }
        
        // Clear and rebuild queues completely for this player
        Map<String, Queue<UUID>> newQuestQueues = new HashMap<>();
        
        for (Map.Entry<String, java.util.List<UUID>> entry : papersByType.entrySet()) {
            String questId = entry.getKey();
            java.util.List<UUID> instances = entry.getValue();
            
            if (!instances.isEmpty()) {
                // Create new queue for this quest type
                Queue<UUID> queue = new LinkedList<>();
                
                // Add all instances to queue in order found
                for (int i = 0; i < instances.size(); i++) {
                    UUID instanceUuid = instances.get(i);
                    boolean isActive = (i == 0); // First one is active
                    
                    queue.offer(instanceUuid);
                    
                    // Update paper status
                    updateQuestPaperStatus(player, questId, instanceUuid, isActive);
                }
                
                newQuestQueues.put(questId, queue);
            }
        }
        
        // Replace old queues with new ones (or remove if empty)
        if (newQuestQueues.isEmpty()) {
            playerQuestQueues.remove(playerUuid);
        } else {
            playerQuestQueues.put(playerUuid, newQuestQueues);
        }
    }
}
