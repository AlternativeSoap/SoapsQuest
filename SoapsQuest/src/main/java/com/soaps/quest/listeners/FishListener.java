package com.soaps.quest.listeners;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import com.soaps.quest.utils.QuestPaper;

/**
 * Listener for fishing events.
 * Tracks progress for fish-related objectives.
 */
public class FishListener implements Listener {
    
    private final SoapsQuest plugin;
    
    public FishListener(SoapsQuest plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
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
            
            // Get quest ID
            String questId = QuestPaper.getQuestId(item, plugin.getQuestIdKey());
            if (questId == null) {
                continue;
            }
            
            Quest quest = plugin.getQuestManager().getQuest(questId);
            if (quest == null) {
                continue;
            }
            
            // Skip if quest doesn't have objectives
            if (!quest.hasObjectives()) {
                continue;
            }
            
            // Check if player has required permission for this quest
            if (!quest.hasPermission(player)) {
                continue;
            }
            
            // Get quest progress
            QuestProgress questProgress;
            if (quest.isLockToPlayer()) {
                questProgress = plugin.getDataManager().getLockedQuestInstance(questInstanceId);
            } else {
                questProgress = plugin.getDataManager().getActiveQuests(player).get(questInstanceId);
            }
            
            if (questProgress == null || questProgress.isComplete(quest)) {
                continue;
            }
            
            boolean progressMade = false;
            
            // Check objectives
            for (Objective objective : quest.getObjectives()) {
                int oldProgress = objective.getCurrentProgress(player.getUniqueId());
                if (objective.handleEvent(player, event)) {
                    int newProgress = objective.getCurrentProgress(player.getUniqueId());
                    questProgress.setObjectiveProgress(
                        objective.getObjectiveId(),
                        newProgress
                    );
                    progressMade = true;
                    
                    // Check for milestones
                    List<Integer> reachedMilestones = objective.checkNewMilestones(player.getUniqueId(), oldProgress, newProgress);
                    for (Integer milestone : reachedMilestones) {
                        plugin.getProgressDisplayManager().showMilestone(player, quest, objective, milestone);
                    }
                }
            }
            
            // Update if progress was made
            if (progressMade) {
                QuestPaper.updateQuestPaperWithStatus(item, quest, questProgress,
                    plugin.getMessageManager(), quest.getCustomLore(), true);
                
                if (questProgress.isComplete(quest)) {
                    plugin.getProgressDisplayManager().showCompletion(player, quest);
                } else {
                    plugin.getProgressDisplayManager().showProgress(player, quest, questProgress);
                }
            }
        }
    }
}
