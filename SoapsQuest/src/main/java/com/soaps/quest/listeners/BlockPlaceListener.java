package com.soaps.quest.listeners;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import com.soaps.quest.quests.QuestType;
import com.soaps.quest.utils.QuestPaper;

/**
 * Listener for block place quests.
 */
public class BlockPlaceListener implements Listener {
    
    private final SoapsQuest plugin;
    
    /**
     * Constructor for BlockPlaceListener.
     * 
     * @param plugin Plugin instance
     */
    public BlockPlaceListener(SoapsQuest plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Handle block place events for quest progress.
     * Uses UUID-based quest instance tracking for separate progress per paper.
     * 
     * @param event BlockPlaceEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        // Iterate through player's inventory to find quest papers
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) {
            return;
        }
        
        for (ItemStack item : contents) {
            if (item == null) {
                continue;
            }
            
            // Check if this is a quest paper
            if (!QuestPaper.isQuestPaper(item, plugin.getQuestIdKey())) {
                continue;
            }
            
            // Get quest instance UUID from the paper
            UUID questInstanceId = QuestPaper.getQuestInstanceId(item);
            if (questInstanceId == null) {
                continue;
            }
            
            // Get quest ID first to check if it's locked
            String questId = QuestPaper.getQuestId(item, plugin.getQuestIdKey());
            if (questId == null) {
                continue;
            }
            
            Quest quest = plugin.getQuestManager().getQuest(questId);
            if (quest == null) {
                continue;
            }
            
            // Skip locked quests - they cannot make progress until unlocked
            if (QuestPaper.isLocked(item)) {
                continue;
            }
            
            // Check if player has required permission for this quest
            if (!quest.hasPermission(player)) {
                continue;
            }
            
            // Skip if quest is legacy type but not PLACE
            if (!quest.hasObjectives() && quest.getType() != QuestType.PLACE) {
                continue;
            }
            
            // Get the quest progress - for locked quests, search in owner's data
            QuestProgress questProgress;
            if (quest.isLockToPlayer()) {
                questProgress = plugin.getDataManager().getLockedQuestInstance(questInstanceId);
            } else {
                questProgress = plugin.getDataManager().getQuestInstance(player, questInstanceId);
            }
            
            if (questProgress == null || questProgress.isRedeemed()) {
                continue;
            }
            
            // Skip if already complete
            if (questProgress.isComplete(quest)) {
                continue;
            }
            
            // Check ownership if quest is locked to player
            if (quest.isLockToPlayer()) {
                // If quest is not yet bound, bind it to this player
                if (!questProgress.isBound()) {
                    questProgress.setOwnerUUID(player.getUniqueId());
                }
                // If quest is bound to someone else, silently skip (don't spam messages)
                else if (!questProgress.getOwnerUUID().equals(player.getUniqueId())) {
                    continue;
                }
            }
            
            // Only update progress for active quests (not queued ones)
            if (!plugin.getQuestManager().isQuestActive(player, questId, questInstanceId)) {
                continue;
            }
            
            // Check quest conditions before allowing progress (without consuming resources)
            if (quest.getConditions() != null) {
                com.soaps.quest.conditions.ConditionResult conditionResult = plugin.getConditionChecker()
                    .checkConditions(player, quest, quest.getConditions(), false);
                
                if (!conditionResult.isSuccess()) {
                    // Silently skip progress if conditions not met (don't spam player)
                    continue;
                }
            }
            
            // Handle progress based on quest type
            boolean progressMade = false;
            
            if (quest.hasObjectives()) {
                // NEW: Multi-objective system
                for (Objective objective : quest.getObjectives()) {
                    int oldProgress = objective.getCurrentProgress(player.getUniqueId());
                    if (objective.handleEvent(player, event)) {
                        int newProgress = objective.getCurrentProgress(player.getUniqueId());
                        // Sync objective progress to quest progress
                        questProgress.setObjectiveProgress(objective.getObjectiveId(), newProgress);
                        progressMade = true;
                        
                        // Check for milestones
                        List<Integer> reachedMilestones = objective.checkNewMilestones(player.getUniqueId(), oldProgress, newProgress);
                        for (Integer milestone : reachedMilestones) {
                            plugin.getProgressDisplayManager().showMilestone(player, quest, objective, milestone);
                        }
                    }
                }
            } else {
                // LEGACY: Single-type quest
                if (quest.checkProgress(player, event.getBlock().getType())) {
                    questProgress.incrementProgress();
                    progressMade = true;
                }
            }
            
            if (progressMade) {
                // Update the quest paper's lore to reflect new progress with active status
                QuestPaper.updateQuestPaperWithStatus(item, quest, questProgress,
                    plugin.getMessageManager(), quest.getCustomLore(), true);
                
                // Send progress update notification
                if (questProgress.isComplete(quest)) {
                    plugin.getProgressDisplayManager().showCompletion(player, quest);
                } else {
                    plugin.getProgressDisplayManager().showProgress(player, quest, questProgress);
                }
                
                // Save progress to file asynchronously
                plugin.getDataManager().saveDataAsync();
            }
        }
    }
}
