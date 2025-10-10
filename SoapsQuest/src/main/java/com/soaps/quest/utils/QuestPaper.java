package com.soaps.quest.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.soaps.quest.managers.MessageManager;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestTier;

import net.kyori.adventure.text.Component;

/**
 * Utility class for creating and managing quest paper items.
 * Quest papers are bound to players via PersistentDataContainer.
 */
public class QuestPaper {
    
    // Key for tracking if rewards have been redeemed
    private static final String REDEEMED_KEY_SUFFIX = "_redeemed";
    
    /**
     * Create a quest paper item for a player.
     * 
     * @param quest The quest
     * @param player The player
     * @param messageManager MessageManager for lore formatting
     * @param questKey NamespacedKey for quest ID
     * @param playerKey NamespacedKey for player UUID
     * @return Quest paper ItemStack
     */
    public static ItemStack createQuestPaper(Quest quest, Player player, 
                                            MessageManager messageManager,
                                            NamespacedKey questKey, 
                                            NamespacedKey playerKey) {
        return createQuestPaper(quest, player, messageManager, questKey, playerKey, null);
    }
    
    /**
     * Create a quest paper item for a player with custom lore.
     * 
     * @param quest The quest
     * @param player The player
     * @param messageManager MessageManager for lore formatting
     * @param questKey NamespacedKey for quest ID
     * @param playerKey NamespacedKey for player UUID
     * @param customLore Custom lore lines from config (null to use default)
     * @return Quest paper ItemStack
     */
    public static ItemStack createQuestPaper(Quest quest, Player player, 
                                            MessageManager messageManager,
                                            NamespacedKey questKey, 
                                            NamespacedKey playerKey,
                                            List<String> customLore) {
        ItemStack paper = new ItemStack(quest.getMaterial());
        ItemMeta meta = paper.getItemMeta();
        
        if (meta == null) {
            return paper;
        }
        
        // Set display name - quest.getDisplay() may already contain tier prefix from quest definition
        // Just use the display as-is
        Component displayName = messageManager.parseColorCodes(quest.getDisplay());
        meta.displayName(displayName);
        
        // Set lore with initial progress (always 0 for new papers)
        List<Component> lore = buildQuestLore(quest, messageManager, customLore);
        meta.lore(lore);
        
        // Generate a unique quest instance UUID for this specific paper
        // This allows players to have multiple quests of the same type with separate progress
        UUID questInstanceId = UUID.randomUUID();
        
        // Store quest ID, player UUID, and quest instance UUID in PersistentDataContainer
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(questKey, PersistentDataType.STRING, quest.getQuestId());
        container.set(playerKey, PersistentDataType.STRING, player.getUniqueId().toString());
        
        // Store the unique quest instance UUID - this is the key for tracking progress
        NamespacedKey questInstanceKey = new NamespacedKey("soapsquest", "quest_instance_id");
        container.set(questInstanceKey, PersistentDataType.STRING, questInstanceId.toString());
        
        // Make quest papers non-stackable by adding a unique timestamp
        // Each paper gets a unique creation time, preventing stacking
        NamespacedKey timestampKey = new NamespacedKey("soapsquest", "creation_time");
        container.set(timestampKey, PersistentDataType.LONG, System.currentTimeMillis());
        
        // Check if quest should start locked (has cost or consume-item conditions)
        if (quest.getConditions() != null) {
            boolean hasCost = quest.getConditions().contains("cost");
            boolean hasConsumeItem = quest.getConditions().getBoolean("consume-item", false);
            
            if (hasCost || hasConsumeItem) {
                // Mark quest as locked
                NamespacedKey lockedKey = new NamespacedKey("soapsquest", "quest_locked");
                container.set(lockedKey, PersistentDataType.BYTE, (byte) 1);
                
                // Update lore to show locked state
                meta.lore(buildLockedLore(quest, messageManager));
            }
        }
        
        paper.setItemMeta(meta);
        return paper;
    }
    
    /**
     * Update the lore of a quest paper with current progress.
     * 
     * @param paper The quest paper item
     * @param quest The quest
     * @param player The player
     * @param messageManager MessageManager for lore formatting
     * @return Updated ItemStack
     */
    public static ItemStack updateQuestPaper(ItemStack paper, Quest quest, Player player, 
                                            MessageManager messageManager) {
        return updateQuestPaper(paper, quest, player, messageManager, null);
    }
    
    /**
     * Update the lore of a quest paper with current progress and custom lore.
     * 
     * @param paper The quest paper item
     * @param quest The quest
     * @param player The player
     * @param messageManager MessageManager for lore formatting
     * @param customLore Custom lore from config (null to use default)
     * @return Updated ItemStack
     */
    public static ItemStack updateQuestPaper(ItemStack paper, Quest quest, Player player, 
                                            MessageManager messageManager, List<String> customLore) {
        if (paper == null) {
            return paper;
        }
        
        ItemMeta meta = paper.getItemMeta();
        if (meta == null) {
            return paper;
        }
        
        // Update lore (note: this method uses simplified lore, prefer updateQuestPaperWithStatus)
        List<Component> lore = buildQuestLore(quest, messageManager, customLore);
        meta.lore(lore);
        paper.setItemMeta(meta);
        
        return paper;
    }
    
    /**
     * Update the lore of a quest paper using UUID-based quest progress with status indicator.
     * This version adds [Active] or [Queued] status to the lore.
     * 
     * @param paper The quest paper item
     * @param quest The quest
     * @param questProgress The quest progress instance
     * @param messageManager MessageManager for lore formatting
     * @param customLore Custom lore from config (null to use default)
     * @param isActive True if this paper is active, false if queued
     * @return Updated ItemStack
     */
    public static ItemStack updateQuestPaperWithStatus(ItemStack paper, Quest quest, 
                                                      com.soaps.quest.quests.QuestProgress questProgress,
                                                      MessageManager messageManager, List<String> customLore,
                                                      boolean isActive) {
        if (paper == null) {
            return paper;
        }
        
        ItemMeta meta = paper.getItemMeta();
        if (meta == null) {
            return paper;
        }
        
        // Check if already redeemed
        boolean isRedeemed = questProgress.isRedeemed();
        
        // Update lore with current progress from QuestProgress and status
        List<Component> lore = buildQuestLoreWithStatus(quest, questProgress, messageManager, customLore, isRedeemed, isActive);
        meta.lore(lore);
        paper.setItemMeta(meta);
        
        return paper;
    }
    
    /**
     * Check if an item is a quest paper.
     * 
     * @param item ItemStack to check
     * @param questKey NamespacedKey for quest ID
     * @return True if it's a quest paper
     */
    public static boolean isQuestPaper(ItemStack item, NamespacedKey questKey) {
        if (item == null) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(questKey, PersistentDataType.STRING);
    }
    
    /**
     * Get the quest ID from a quest paper.
     * 
     * @param item Quest paper item
     * @param questKey NamespacedKey for quest ID
     * @return Quest ID, or null if not found
     */
    public static String getQuestId(ItemStack item, NamespacedKey questKey) {
        if (!isQuestPaper(item, questKey)) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.get(questKey, PersistentDataType.STRING);
    }
    
    /**
     * Get the player UUID from a quest paper.
     * 
     * @param item Quest paper item
     * @param playerKey NamespacedKey for player UUID
     * @return Player UUID, or null if not found
     */
    public static UUID getPlayerUUID(ItemStack item, NamespacedKey playerKey) {
        if (item == null) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String uuidString = container.get(playerKey, PersistentDataType.STRING);
        
        if (uuidString == null) {
            return null;
        }
        
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Get the quest instance UUID from a quest paper.
     * This UUID is unique per quest paper and used to track progress separately.
     * 
     * @param item Quest paper item
     * @return Quest instance UUID, or null if not found
     */
    public static UUID getQuestInstanceId(ItemStack item) {
        if (item == null) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey questInstanceKey = new NamespacedKey("soapsquest", "quest_instance_id");
        String uuidString = container.get(questInstanceKey, PersistentDataType.STRING);
        
        if (uuidString == null) {
            return null;
        }
        
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Check if a quest paper is locked (requires unlock before progress).
     * 
     * @param item Quest paper item
     * @return True if the quest is locked
     */
    public static boolean isLocked(ItemStack item) {
        if (item == null) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey lockedKey = new NamespacedKey("soapsquest", "quest_locked");
        return container.getOrDefault(lockedKey, PersistentDataType.BYTE, (byte) 0) == 1;
    }
    
    /**
     * Set the locked state of a quest paper.
     * 
     * @param item Quest paper item
     * @param locked True to lock, false to unlock
     */
    public static void setLocked(ItemStack item, boolean locked) {
        if (item == null) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey lockedKey = new NamespacedKey("soapsquest", "quest_locked");
        container.set(lockedKey, PersistentDataType.BYTE, locked ? (byte) 1 : (byte) 0);
        item.setItemMeta(meta);
    }
    
    /**
     * Check if a quest paper belongs to a specific player.
     * 
     * @param item Quest paper item
     * @param player Player to check
     * @param playerKey NamespacedKey for player UUID
     * @return True if the paper belongs to the player
     */
    public static boolean belongsToPlayer(ItemStack item, Player player, NamespacedKey playerKey) {
        UUID paperUUID = getPlayerUUID(item, playerKey);
        return paperUUID != null && paperUUID.equals(player.getUniqueId());
    }
    
    /**
     * Update the player UUID on a quest paper for transferring unlocked quests.
     * This allows quest papers to be transferred between players.
     * 
     * @param item Quest paper item to update
     * @param newPlayer The new owner player
     * @param playerKey NamespacedKey for player UUID
     * @return Updated ItemStack with new player UUID
     */
    public static ItemStack transferQuestPaperOwnership(ItemStack item, Player newPlayer, NamespacedKey playerKey) {
        if (item == null || !item.hasItemMeta()) {
            return item;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(playerKey, PersistentDataType.STRING, newPlayer.getUniqueId().toString());
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Build quest lore from either custom config or default template.
     * Used only for initial quest paper creation (progress is always 0).
     * For updating existing papers, use buildQuestLoreWithStatus instead.
     * 
     * @param quest The quest
     * @param messageManager MessageManager for lore formatting
     * @param customLore Custom lore from config (null to use default)
     * @return List of formatted lore components
     */
    private static List<Component> buildQuestLore(Quest quest,
                                                  MessageManager messageManager,
                                                  List<String> customLore) {
        List<Component> lore = new ArrayList<>();
        
        // Calculate the correct amount value and unit based on quest type
        String progressValue = "0";
        String amountValue;
        String progressUnit; // What we're counting (e.g., "objectives", "steps", or empty for items)
        String questType; // Quest type display name
        
        if (quest.hasObjectives()) {
            if (quest.isSequential()) {
                // Sequential: show total number of objectives as "steps"
                amountValue = String.valueOf(quest.getObjectives().size());
                progressUnit = " objectives";
                questType = "Sequential";
            } else if (quest.getObjectives().size() == 1) {
                // Single objective: show the objective's required amount
                amountValue = String.valueOf(quest.getObjectives().get(0).getRequiredAmount());
                progressUnit = "";
                questType = "Single";
            } else {
                // Multi-objective: show total number of objectives
                amountValue = String.valueOf(quest.getObjectives().size());
                progressUnit = " objectives";
                questType = "Multi";
            }
        } else {
            // Legacy single-objective quest
            amountValue = String.valueOf(quest.getRequiredAmount());
            progressUnit = "";
            questType = "Single";
        }
        
        // Use custom lore if provided, otherwise use default template
        if (customLore != null && !customLore.isEmpty()) {
            for (String line : customLore) {
                String formatted = line
                    .replace("<quest>", quest.getDisplay())
                    .replace("<objective>", quest.getObjectiveDescription())
                    .replace("<progress>", progressValue)
                    .replace("<amount>", amountValue)
                    .replace("<progress_unit>", progressUnit)
                    .replace("<type>", questType);
                
                // Apply tier placeholders
                if (quest.getTier() != null) {
                    QuestTier tier = quest.getTier();
                    String tierDisplay = tier.name().substring(0, 1).toUpperCase() + tier.name().substring(1).toLowerCase();
                    formatted = formatted
                        .replace("<tier>", tier.getColor() + tierDisplay)
                        .replace("<tier_prefix>", tier.getPrefix())
                        .replace("<tier_color>", tier.getColor());
                }
                
                // Apply difficulty placeholder
                if (quest.getDifficulty() != null) {
                    formatted = formatted.replace("<difficulty>", quest.getDifficulty());
                }
                
                lore.add(messageManager.parseColorCodes(formatted));
            }
        } else {
            // Use default template from messages.yml
            List<String> defaultTemplate = messageManager.getConfig().getStringList("quest-default-lore");
            if (!defaultTemplate.isEmpty()) {
                for (String line : defaultTemplate) {
                    String formatted = line
                        .replace("<quest>", quest.getDisplay())
                        .replace("<objective>", quest.getObjectiveDescription())
                        .replace("<progress>", progressValue)
                        .replace("<amount>", amountValue)
                        .replace("<progress_unit>", progressUnit)
                        .replace("<type>", questType);
                    
                    // Apply tier placeholders
                    if (quest.getTier() != null) {
                        QuestTier tier = quest.getTier();
                        String tierDisplay = tier.name().substring(0, 1).toUpperCase() + tier.name().substring(1).toLowerCase();
                        formatted = formatted
                            .replace("<tier>", tier.getColor() + tierDisplay)
                            .replace("<tier_prefix>", tier.getPrefix())
                            .replace("<tier_color>", tier.getColor());
                    }
                    
                    // Apply difficulty placeholder
                    if (quest.getDifficulty() != null) {
                        formatted = formatted.replace("<difficulty>", quest.getDifficulty());
                    }
                    
                    lore.add(messageManager.parseColorCodes(formatted));
                }
            }
        }
        
        // Add objective details for multi-objective and sequential quests
        if (quest.hasObjectives() && quest.getObjectives().size() > 1) {
            lore.add(Component.empty());
            
            if (quest.isSequential()) {
                // Sequential: Show all objectives with step numbers
                lore.add(messageManager.parseColorCodes("&7&lObjectives (Sequential):"));
                int stepNum = 1;
                for (com.soaps.quest.objectives.Objective obj : quest.getObjectives()) {
                    lore.add(messageManager.parseColorCodes("&8  " + stepNum + ". &f" + obj.getDescription()));
                    stepNum++;
                }
            } else {
                // Multi: Show all objectives with bullets
                lore.add(messageManager.parseColorCodes("&7&lObjectives:"));
                for (com.soaps.quest.objectives.Objective obj : quest.getObjectives()) {
                    lore.add(messageManager.parseColorCodes("&8  • &f" + obj.getDescription()));
                }
            }
        }
        
        // New papers are never complete or redeemed
        
        return lore;
    }
    
    /**
     * Build locked quest lore showing requirements to unlock.
     * 
     * @param quest The quest
     * @param messageManager MessageManager for lore formatting
     * @return List of formatted locked lore components
     */
    private static List<Component> buildLockedLore(Quest quest, MessageManager messageManager) {
        List<Component> lore = new ArrayList<>();
        
        // Get locked lore template from messages.yml
        List<String> lockedTemplate = messageManager.getConfig().getStringList("quest-locked-lore");
        
        // Build requirements list
        StringBuilder requirements = new StringBuilder();
        org.bukkit.configuration.ConfigurationSection conditions = quest.getConditions();
        
        if (conditions != null) {
            if (conditions.contains("cost")) {
                double cost = conditions.getDouble("cost");
                requirements.append("&e$").append(String.format("%.2f", cost));
            }
            
            if (conditions.contains("item") && conditions.getBoolean("consume-item", false)) {
                String itemStr = conditions.getString("item");
                if (itemStr != null && itemStr.contains(":")) {
                    String[] parts = itemStr.split(":");
                    if (requirements.length() > 0) {
                        requirements.append("\n");
                    }
                    requirements.append("&e").append(parts[1]).append("x ")
                               .append(parts[0].replace("_", " ").toLowerCase());
                }
            }
        }
        
        // Apply template
        for (String line : lockedTemplate) {
            String formatted = line
                .replace("<quest>", quest.getDisplay())
                .replace("<requirements>", requirements.toString());
            
            lore.add(messageManager.parseColorCodes(formatted));
        }
        
        return lore;
    }
    
    /**
     * Build quest lore using QuestProgress for UUID-based tracking with status indicator.
     * 
     * @param quest The quest
     * @param questProgress The quest progress instance
     * @param messageManager MessageManager for lore formatting
     * @param customLore Custom lore from config (null to use default)
     * @param isRedeemed Whether the quest has been redeemed
     * @param isActive True if active, false if queued
     * @return List of formatted lore components
     */
    private static List<Component> buildQuestLoreWithStatus(Quest quest, 
                                                           com.soaps.quest.quests.QuestProgress questProgress,
                                                           MessageManager messageManager,
                                                           List<String> customLore,
                                                           boolean isRedeemed,
                                                           boolean isActive) {
        List<Component> lore = new ArrayList<>();
        
        // Add status indicator at the top
        if (isActive) {
            lore.add(messageManager.parseColorCodes("&7[&aActive&7]"));
        } else {
            lore.add(messageManager.parseColorCodes("&7[&6Queued&7]"));
        }
        
        // Add ownership indicator if quest is locked and bound to a player
        if (quest.isLockToPlayer() && questProgress.isBound()) {
            UUID ownerUuid = questProgress.getOwnerUUID();
            if (ownerUuid != null) {
                org.bukkit.OfflinePlayer owner = org.bukkit.Bukkit.getOfflinePlayer(ownerUuid);
                String ownerName = owner.getName() != null ? owner.getName() : "Unknown";
                lore.add(messageManager.parseColorCodes("&7Bound to: &e" + ownerName));
            }
        }
        
        lore.add(Component.empty()); // Add spacing
        
        // Calculate progress values and unit based on quest type
        String progressValue;
        String amountValue;
        String progressUnit; // What we're counting (e.g., "objectives", "steps", or empty for items)
        String questType; // Quest type display name
        
        if (quest.hasObjectives()) {
            if (quest.isSequential()) {
                // Sequential: show current objective number (1-based for display)
                int currentIndex = questProgress.getCurrentObjectiveIndex();
                int totalCount = quest.getObjectives().size();
                // currentIndex is 0-based, so add 1 for display (but cap at total)
                int displayNumber = Math.min(currentIndex + 1, totalCount);
                progressValue = String.valueOf(displayNumber);
                amountValue = String.valueOf(totalCount);
                progressUnit = " objectives";
                questType = "Sequential";
            } else if (quest.getObjectives().size() == 1) {
                // Single objective in objectives list: show actual progress
                com.soaps.quest.objectives.Objective objective = quest.getObjectives().get(0);
                int objProgress = questProgress.getObjectiveProgress(objective.getObjectiveId());
                progressValue = String.valueOf(objProgress);
                amountValue = String.valueOf(objective.getRequiredAmount());
                progressUnit = "";
                questType = "Single";
            } else {
                // Multi-objective: count completed objectives
                int completedCount = 0;
                int totalCount = quest.getObjectives().size();
                
                for (com.soaps.quest.objectives.Objective objective : quest.getObjectives()) {
                    int objProgress = questProgress.getObjectiveProgress(objective.getObjectiveId());
                    if (objProgress >= objective.getRequiredAmount()) {
                        completedCount++;
                    }
                }
                
                progressValue = String.valueOf(completedCount);
                amountValue = String.valueOf(totalCount);
                progressUnit = " objectives";
                questType = "Multi";
            }
        } else {
            // Legacy single-objective quest: use quest's own progress tracking
            progressValue = String.valueOf(questProgress.getCurrentProgress());
            amountValue = String.valueOf(questProgress.getRequiredAmount());
            progressUnit = "";
            questType = "Single";
        }
        
        // Use custom lore if provided, otherwise use default template
        if (customLore != null && !customLore.isEmpty()) {
            for (String line : customLore) {
                String formatted = line
                    .replace("<quest>", quest.getDisplay())
                    .replace("<objective>", quest.getObjectiveDescription())
                    .replace("<progress>", progressValue)
                    .replace("<amount>", amountValue)
                    .replace("<progress_unit>", progressUnit)
                    .replace("<type>", questType);
                
                // Apply tier placeholders
                if (quest.getTier() != null) {
                    QuestTier tier = quest.getTier();
                    String tierDisplay = tier.name().substring(0, 1).toUpperCase() + tier.name().substring(1).toLowerCase();
                    formatted = formatted
                        .replace("<tier>", tier.getColor() + tierDisplay)
                        .replace("<tier_prefix>", tier.getPrefix())
                        .replace("<tier_color>", tier.getColor());
                }
                
                // Apply difficulty placeholder
                if (quest.getDifficulty() != null) {
                    formatted = formatted.replace("<difficulty>", quest.getDifficulty());
                }
                
                lore.add(messageManager.parseColorCodes(formatted));
            }
        } else {
            // Use default template from messages.yml
            List<String> defaultTemplate = messageManager.getConfig().getStringList("quest-default-lore");
            if (!defaultTemplate.isEmpty()) {
                for (String line : defaultTemplate) {
                    String formatted = line
                        .replace("<quest>", quest.getDisplay())
                        .replace("<objective>", quest.getObjectiveDescription())
                        .replace("<progress>", progressValue)
                        .replace("<amount>", amountValue)
                        .replace("<progress_unit>", progressUnit)
                        .replace("<type>", questType);
                    
                    // Apply tier placeholders
                    if (quest.getTier() != null) {
                        QuestTier tier = quest.getTier();
                        String tierDisplay = tier.name().substring(0, 1).toUpperCase() + tier.name().substring(1).toLowerCase();
                        formatted = formatted
                            .replace("<tier>", tier.getColor() + tierDisplay)
                            .replace("<tier_prefix>", tier.getPrefix())
                            .replace("<tier_color>", tier.getColor());
                    }
                    
                    // Apply difficulty placeholder
                    if (quest.getDifficulty() != null) {
                        formatted = formatted.replace("<difficulty>", quest.getDifficulty());
                    }
                    
                    lore.add(messageManager.parseColorCodes(formatted));
                }
            }
        }
        
        // Add detailed objective list for multi-objective quests
        if (quest.hasObjectives() && quest.getObjectives().size() > 1) {
            lore.add(Component.empty());
            lore.add(messageManager.parseColorCodes("&7Objectives:"));
            
            java.util.List<com.soaps.quest.objectives.Objective> objectives = quest.getObjectives();
            int currentIndex = questProgress.getCurrentObjectiveIndex();
            
            for (int i = 0; i < objectives.size(); i++) {
                com.soaps.quest.objectives.Objective obj = objectives.get(i);
                int objProgress = questProgress.getObjectiveProgress(obj.getObjectiveId());
                int objRequired = obj.getRequiredAmount();
                
                String prefix;
                String objDescription = obj.getDescription();
                boolean isCompleted = objProgress >= objRequired;
                
                if (isCompleted) {
                    // ✓ Completed objective (green)
                    prefix = " &a✓ ";
                    objDescription = "&a" + objDescription + " &7(" + objProgress + "/" + objRequired + ")";
                } else if (quest.isSequential()) {
                    // Sequential quest logic
                    if (i == currentIndex) {
                        // > Active objective (yellow)
                        prefix = " &e> ";
                        objDescription = "&e" + objDescription + " &7(" + objProgress + "/" + objRequired + ")";
                    } else if (i < currentIndex) {
                        // This shouldn't happen (completed objectives already handled above)
                        prefix = " &a✓ ";
                        objDescription = "&a" + objDescription + " &7(" + objProgress + "/" + objRequired + ")";
                    } else {
                        // Future/locked objective (gray, no progress shown)
                        prefix = " &7  ";
                        objDescription = "&7" + objDescription + " &8(Locked)";
                    }
                } else {
                    // Non-sequential: all incomplete objectives are active (yellow)
                    prefix = " &e> ";
                    objDescription = "&e" + objDescription + " &7(" + objProgress + "/" + objRequired + ")";
                }
                
                lore.add(messageManager.parseColorCodes(prefix + objDescription));
            }
        }
        
        // Add completion/redemption status
        if (isRedeemed) {
            lore.add(Component.empty());
            lore.add(messageManager.parseColorCodes("&7[&aRedeemed&7]"));
        } else if (questProgress.isComplete(quest)) {
            lore.add(Component.empty());
            lore.add(messageManager.parseColorCodes(messageManager.getRawMessage("paper-lore-complete")));
            // Only show redemption hint for active papers
            if (isActive) {
                lore.add(messageManager.parseColorCodes("&e&oRight-click to redeem rewards!"));
            } else {
                lore.add(messageManager.parseColorCodes("&7&oWill be redeemable when active"));
            }
        }
        
        return lore;
    }
    

    
    /**
     * Check if a quest paper has been redeemed.
     * 
     * @param item Quest paper item
     * @param questKey NamespacedKey for quest ID
     * @return True if redeemed
     */
    public static boolean isRedeemed(ItemStack item, NamespacedKey questKey) {
        if (!isQuestPaper(item, questKey)) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey redeemedKey = new NamespacedKey(questKey.getNamespace(), 
            questKey.getKey() + REDEEMED_KEY_SUFFIX);
        return container.has(redeemedKey, PersistentDataType.BYTE);
    }
    
    /**
     * Mark a quest paper as redeemed.
     * 
     * @param item Quest paper item
     * @param questKey NamespacedKey for quest ID
     */
    public static void markAsRedeemed(ItemStack item, NamespacedKey questKey) {
        if (!isQuestPaper(item, questKey)) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey redeemedKey = new NamespacedKey(questKey.getNamespace(), 
            questKey.getKey() + REDEEMED_KEY_SUFFIX);
        container.set(redeemedKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
    }
    
    /**
     * Update all quest papers in a player's inventory for a specific quest.
     * 
     * @param player The player
     * @param quest The quest to update papers for
     * @param messageManager MessageManager for lore formatting
     * @param questIdKey NamespacedKey for quest ID
     * @param playerUuidKey NamespacedKey for player UUID
     */
    public static void updateQuestPapersInInventory(Player player, Quest quest, 
                                                    MessageManager messageManager,
                                                    NamespacedKey questIdKey,
                                                    NamespacedKey playerUuidKey) {
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) {
            return; // Inventory contents can be null in edge cases
        }
        
        for (ItemStack item : contents) {
            if (item == null) {
                continue;
            }
            
            // Check if this is a quest paper for this specific quest
            String questId = getQuestId(item, questIdKey);
            if (questId == null || !questId.equals(quest.getQuestId())) {
                continue;
            }
            
            // Check if it belongs to this player
            if (!belongsToPlayer(item, player, playerUuidKey)) {
                continue;
            }
            
            // Update the paper with current progress
            updateQuestPaper(item, quest, player, messageManager, quest.getCustomLore());
        }
    }
}
