package com.soaps.quest.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import com.soaps.quest.utils.QuestPaper;

/**
 * Listener for quest paper interactions.
 */
public class QuestPaperListener implements Listener {
    
    private final SoapsQuest plugin;
    
    /**
     * Constructor for QuestPaperListener.
     * 
     * @param plugin Plugin instance
     */
    public QuestPaperListener(SoapsQuest plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Handle player right-clicking with quest paper.
     * 
     * @param event PlayerInteractEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        // Check if player is right-clicking
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        
        // Check if player is holding a quest paper
        if (!QuestPaper.isQuestPaper(item, plugin.getQuestIdKey())) {
            return;
        }
        
        // Explicit null check for flow analysis (isQuestPaper returns false for null items)
        if (item == null) {
            return;
        }
        
        // Cancel the event to prevent block placement
        event.setCancelled(true);
        
        // Get quest ID from paper
        String questId = QuestPaper.getQuestId(item, plugin.getQuestIdKey());
        if (questId == null) {
            return;
        }
        
        // Get the quest
        Quest quest = plugin.getQuestManager().getQuest(questId);
        if (quest == null) {
            player.sendMessage(plugin.getMessageManager().getMessage("quest-not-found",
                Map.of("quest", questId)));
            return;
        }
        
        // Check if quest is locked (has cost or consume-item requirement)
        if (QuestPaper.isLocked(item)) {
            // Quest is locked - attempt to unlock it
            // Check conditions with consumeResources=true to consume money and items
            com.soaps.quest.conditions.ConditionResult result = 
                plugin.getConditionChecker().checkConditions(
                    player, 
                    quest, 
                    quest.getConditions(), 
                    true  // consumeResources=true
                );
            
            if (result.isSuccess()) {
                // Player met all conditions and resources were consumed
                // Unlock the quest
                QuestPaper.setLocked(item, false);
                
                // Update the quest paper with normal lore (remove locked lore)
                ItemStack updatedPaper = QuestPaper.updateQuestPaper(
                    item, 
                    quest, 
                    player, 
                    plugin.getMessageManager()
                );
                
                // Replace the item in player's hand with updated paper
                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), updatedPaper);
                
                // Send success message
                player.sendMessage(plugin.getMessageManager().getMessage("quest-unlocked"));
                return;
            } else {
                // Player doesn't meet conditions
                player.sendMessage(plugin.getMessageManager().getMessage("quest-unlock-failed",
                    Map.of("reason", result.getMessage())));
                return;
            }
        }
        
        // Check if player has required permission for this quest
        if (!quest.hasPermission(player)) {
            player.sendMessage(plugin.getMessageManager().getMessage("quest-no-permission"));
            return;
        }
        
        // Get the quest instance UUID from the paper
        UUID questInstanceId = QuestPaper.getQuestInstanceId(item);
        if (questInstanceId == null) {
            player.sendMessage(plugin.getMessageManager().getMessage("quest-not-found",
                Map.of("quest", questId)));
            return;
        }
        
        // Get the quest progress - for locked quests, search in owner's data
        QuestProgress questProgress;
        if (quest.isLockToPlayer()) {
            // For locked quests, find the progress in the owner's data (not current holder)
            questProgress = plugin.getDataManager().getLockedQuestInstance(questInstanceId);
        } else {
            // For unlocked quests, check current player first
            questProgress = plugin.getDataManager().getQuestInstance(player, questInstanceId);
            
            // If not found, try to transfer/adopt it
            if (questProgress == null) {
                questProgress = plugin.getDataManager().transferOrAdoptQuest(
                    player, 
                    questInstanceId, 
                    questId, 
                    quest.getRequiredAmount()
                );
            }
        }
        
        // If still null, quest not found
        if (questProgress == null) {
            // For locked quests, check if it belongs to someone else
            if (quest.isLockToPlayer()) {
                UUID ownerUuid = plugin.getDataManager().findQuestInstanceOwner(questInstanceId);
                if (ownerUuid != null && !ownerUuid.equals(player.getUniqueId())) {
                    // Quest is locked to another player
                    org.bukkit.OfflinePlayer owner = org.bukkit.Bukkit.getOfflinePlayer(ownerUuid);
                    String ownerName = owner.getName() != null ? owner.getName() : "Unknown";
                    player.sendMessage(plugin.getMessageManager().getMessage("quest-locked-to-other",
                        Map.of("player", ownerName)));
                    return;
                }
            }
            // Generic quest not found message
            player.sendMessage(plugin.getMessageManager().getMessage("quest-not-found",
                Map.of("quest", questId)));
            return;
        }
        
        // For locked quests, verify the current player is the owner
        if (quest.isLockToPlayer() && questProgress.isBound()) {
            if (!questProgress.getOwnerUUID().equals(player.getUniqueId())) {
                org.bukkit.OfflinePlayer owner = org.bukkit.Bukkit.getOfflinePlayer(questProgress.getOwnerUUID());
                String ownerName = owner.getName() != null ? owner.getName() : "Unknown";
                player.sendMessage(plugin.getMessageManager().getMessage("quest-locked-to-other",
                    Map.of("player", ownerName)));
                return;
            }
        }
        
        // Check if quest is complete
        if (!questProgress.isComplete(quest)) {
            // Display current progress only - no "cannot redeem" message
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("quest", quest.getDisplay());
            
            // Calculate progress based on quest type
            if (quest.hasObjectives()) {
                if (quest.isSequential()) {
                    // SEQUENTIAL: Show only current active objective
                    java.util.List<com.soaps.quest.objectives.Objective> objectives = quest.getObjectives();
                    int currentIndex = questProgress.getCurrentObjectiveIndex();
                    
                    if (currentIndex < objectives.size()) {
                        com.soaps.quest.objectives.Objective currentObjective = objectives.get(currentIndex);
                        int objProgress = questProgress.getObjectiveProgress(currentObjective.getObjectiveId());
                        int objRequired = currentObjective.getRequiredAmount();
                        
                        placeholders.put("objective", currentObjective.getDescription());
                        placeholders.put("progress", String.valueOf(objProgress));
                        placeholders.put("amount", String.valueOf(objRequired));
                        
                        // Use sequential progress message
                        player.sendMessage(plugin.getMessageManager().getMessage("objective-sequential-progress", placeholders));
                    } else {
                        // All objectives complete but isComplete check failed? Show generic message
                        player.sendMessage(plugin.getMessageManager().getMessage("quest-progress-display", placeholders));
                    }
                } else {
                    // NON-SEQUENTIAL: Count completed objectives
                    int completedCount = 0;
                    int totalCount = quest.getObjectives().size();
                    
                    StringBuilder objectiveDetails = new StringBuilder();
                    for (com.soaps.quest.objectives.Objective objective : quest.getObjectives()) {
                        int objProgress = questProgress.getObjectiveProgress(objective.getObjectiveId());
                        int objRequired = objective.getRequiredAmount();
                        boolean objComplete = objProgress >= objRequired;
                        
                        if (objComplete) {
                            completedCount++;
                        }
                        
                        if (objectiveDetails.length() > 0) {
                            objectiveDetails.append(", ");
                        }
                        objectiveDetails.append(objective.getDescription())
                                       .append(": ")
                                       .append(objProgress)
                                       .append("/")
                                       .append(objRequired);
                    }
                    
                    placeholders.put("objective", objectiveDetails.toString());
                    placeholders.put("progress", String.valueOf(completedCount));
                    placeholders.put("amount", String.valueOf(totalCount));
                    
                    player.sendMessage(plugin.getMessageManager().getMessage("quest-progress-display", placeholders));
                }
            } else {
                // Single-objective quest
                placeholders.put("objective", quest.getObjectiveDescription());
                placeholders.put("progress", String.valueOf(questProgress.getCurrentProgress()));
                placeholders.put("amount", String.valueOf(questProgress.getRequiredAmount()));
                
                player.sendMessage(plugin.getMessageManager().getMessage("quest-progress-display", placeholders));
            }
            
            return;
        }
        
        // Check if this quest paper is active (not queued)
        if (!plugin.getQuestManager().isQuestActive(player, questId, questInstanceId)) {
            // Quest is complete but not active - show completion but explain it's queued
            int queuePosition = plugin.getQuestManager().getQuestQueuePosition(player, questId, questInstanceId);
            if (quest.isSequential()) {
                player.sendMessage(plugin.getMessageManager().getMessage("all-sequential-complete"));
                player.sendMessage(plugin.getMessageManager().parseColorCodes(
                    "&7(Position " + (queuePosition + 1) + " in queue - will be redeemable when active)"));
            } else {
                player.sendMessage(plugin.getMessageManager().parseColorCodes(
                    plugin.getMessageManager().getRawMessage("prefix") + " &aQuest complete: &2" + quest.getDisplay() + 
                    "&a! &7(Position " + (queuePosition + 1) + " in queue - will be redeemable when active)"));
            }
            return;
        }
        
        // Quest is complete and active - check if already redeemed
        if (questProgress.isRedeemed()) {
            player.sendMessage(plugin.getMessageManager().getMessage("quest-already-redeemed",
                Map.of("quest", quest.getDisplay())));
            return;
        }
        
        // Check ownership if quest is locked to player
        if (quest.isLockToPlayer() && questProgress.isBound()) {
            if (!questProgress.getOwnerUUID().equals(player.getUniqueId())) {
                org.bukkit.OfflinePlayer owner = org.bukkit.Bukkit.getOfflinePlayer(questProgress.getOwnerUUID());
                String ownerName = owner.getName() != null ? owner.getName() : "Unknown";
                player.sendMessage(plugin.getMessageManager().getMessage("quest-locked-to-other",
                    Map.of("player", ownerName)));
                return;
            }
        }
        
        // Give rewards (no condition check here - conditions are checked during progress)
        plugin.getRewardManager().giveRewards(questId, player);
        
        // Mark quest as redeemed and remove from active quests
        // This allows the player to receive the same quest type again
        plugin.getDataManager().markQuestRedeemed(player, questInstanceId);
        
        // Remove from queue system and activate next quest if available
        plugin.getQuestManager().removeQuestFromQueue(player, questId, questInstanceId);
        
        // Save data after redemption
        plugin.getDataManager().saveDataAsync();
        
        // Remove the paper from inventory (consume it)
        // Note: item is guaranteed non-null here because we checked it at the start of the method
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), null);
        }
        
        // Send success message with rewards redeemed
        player.sendMessage(plugin.getMessageManager().getMessage("quest-completion-redeemed",
            Map.of("quest", quest.getDisplay())));
    }
}
