package com.soaps.quest.listeners;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.objectives.KillMythicMobObjective;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import com.soaps.quest.utils.QuestPaper;

import io.lumine.mythic.bukkit.MythicBukkit;

/**
 * Listener for MythicMobs entity death events.
 * Handles progress tracking for kill_mythicmob objectives.
 * 
 * This listener is only registered if MythicMobs is installed.
 */
public class MythicMobKillListener implements Listener {
    
    private final SoapsQuest plugin;
    
    public MythicMobKillListener(SoapsQuest plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        // Check if killer is a player
        if (!(event.getEntity().getKiller() instanceof Player player)) {
            return;
        }
        
        // Check if the entity is a MythicMob
        UUID entityUUID = event.getEntity().getUniqueId();
        
        MythicBukkit.inst().getMobManager().getActiveMob(entityUUID).ifPresent(activeMob -> {
            String mythicMobType = activeMob.getMobType();
            
            // Scan player's inventory for quest papers
            ItemStack[] contents = player.getInventory().getContents();
            if (contents == null) {
                return;
            }
            
            for (ItemStack item : contents) {
                if (item == null) {
                    continue;
                }
                
                // Check if this is a quest paper
                String questId = QuestPaper.getQuestId(item, plugin.getQuestIdKey());
                if (questId == null) {
                    continue;
                }
                
                UUID questInstanceId = QuestPaper.getQuestInstanceId(item);
                if (questInstanceId == null) {
                    continue;
                }
                
                Quest quest = plugin.getQuestManager().getQuest(questId);
                if (quest == null || !quest.hasObjectives()) {
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
                
                // Get quest progress
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
                
                // Check all objectives for kill_mythicmob type
                boolean progressMade = false;
                
                for (Objective objective : quest.getObjectives()) {
                    if (!(objective instanceof KillMythicMobObjective)) {
                        continue;
                    }
                    
                    KillMythicMobObjective mythicObjective = (KillMythicMobObjective) objective;
                    
                    // Check if the killed MythicMob matches this objective's type (case-insensitive)
                    if (!mythicObjective.getMobType().equalsIgnoreCase(mythicMobType)) {
                        continue;
                    }
                    
                    // Check if objective is already complete
                    int oldProgress = questProgress.getObjectiveProgress(objective.getObjectiveId());
                    if (oldProgress >= objective.getRequiredAmount()) {
                        continue;
                    }
                    
                    // Increment progress for this MythicMob objective
                    questProgress.incrementObjectiveProgress(objective.getObjectiveId(), 1);
                    int newProgress = questProgress.getObjectiveProgress(objective.getObjectiveId());
                    progressMade = true;
                    
                    // Check for milestones
                    List<Integer> reachedMilestones = objective.checkNewMilestones(player.getUniqueId(), oldProgress, newProgress);
                    for (Integer milestone : reachedMilestones) {
                        plugin.getProgressDisplayManager().showMilestone(player, quest, objective, milestone);
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
        });
    }
}
