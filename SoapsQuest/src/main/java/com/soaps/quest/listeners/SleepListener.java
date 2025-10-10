package com.soaps.quest.listeners;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import com.soaps.quest.utils.QuestPaper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * Listener for player sleep events.
 */
public class SleepListener implements Listener {
    
    private final SoapsQuest plugin;
    
    public SleepListener(SoapsQuest plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) return;
        
        for (ItemStack item : contents) {
            if (item == null) continue;
            
            if (!QuestPaper.isQuestPaper(item, plugin.getQuestIdKey())) continue;
            
            UUID questInstanceId = QuestPaper.getQuestInstanceId(item);
            if (questInstanceId == null) continue;
            
            String questId = QuestPaper.getQuestId(item, plugin.getQuestIdKey());
            if (questId == null) continue;
            
            Quest quest = plugin.getQuestManager().getQuest(questId);
            if (quest == null || !quest.hasObjectives()) continue;
            
            // Check if player has required permission for this quest
            if (!quest.hasPermission(player)) {
                continue;
            }
            
            QuestProgress questProgress;
            if (quest.isLockToPlayer()) {
                questProgress = plugin.getDataManager().getLockedQuestInstance(questInstanceId);
            } else {
                questProgress = plugin.getDataManager().getQuestInstance(player, questInstanceId);
            }
            
            if (questProgress == null || questProgress.isRedeemed() || questProgress.isComplete(quest)) continue;
            
            if (quest.isLockToPlayer()) {
                if (!questProgress.isBound()) {
                    questProgress.setOwnerUUID(player.getUniqueId());
                } else if (!questProgress.getOwnerUUID().equals(player.getUniqueId())) {
                    continue;
                }
            }
            
            boolean progressMade = false;
            for (Objective objective : quest.getObjectives()) {
                int oldProgress = objective.getCurrentProgress(player.getUniqueId());
                if (objective.handleEvent(player, event)) {
                    int newProgress = objective.getCurrentProgress(player.getUniqueId());
                    questProgress.setObjectiveProgress(objective.getObjectiveId(), newProgress);
                    progressMade = true;
                    
                    // Check for milestones
                    List<Integer> reachedMilestones = objective.checkNewMilestones(player.getUniqueId(), oldProgress, newProgress);
                    for (Integer milestone : reachedMilestones) {
                        plugin.getProgressDisplayManager().showMilestone(player, quest, objective, milestone);
                    }
                }
            }
            
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
