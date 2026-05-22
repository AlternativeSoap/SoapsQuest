/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.entity.EntityPickupItemEvent
 *  org.bukkit.event.inventory.CraftItemEvent
 *  org.bukkit.event.inventory.FurnaceExtractEvent
 *  org.bukkit.event.player.PlayerExpChangeEvent
 *  org.bukkit.inventory.ItemStack
 */
package com.soaps.quest.managers;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.conditions.ConditionRegistry;
import com.soaps.quest.conditions.ConditionResult;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import com.soaps.quest.utils.QuestPaper;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;

public class QuestProgressTracker {
    private final SoapsQuest plugin;

    public QuestProgressTracker(SoapsQuest plugin) {
        this.plugin = plugin;
    }

    public boolean trackProgress(Player player, ItemStack questPaper, int progressAmount, ProgressCallback progressCallback) {
        boolean progressMade;
        ConditionResult conditionResult;
        String questId;
        if (questPaper == null || !QuestPaper.isQuestPaper(questPaper, this.plugin.getQuestIdKey())) {
            this.plugin.debugLog(Level.FINE, "[Progress] trackProgress() called but paper is null or not a quest paper", new Object[0]);
            return false;
        }
        this.plugin.debugLog(Level.INFO, "[Progress] trackProgress() called for player ''{0}''", player.getName());
        UUID questInstanceId = QuestPaper.getQuestInstanceId(questPaper);
        if (questInstanceId == null) {
            this.plugin.debugLog(Level.WARNING, "[Progress] Quest paper has no instance UUID - paper may be corrupted", new Object[0]);
            return false;
        }
        boolean foundInInventory = false;
        ItemStack[] contents = player.getInventory().getContents();
        if (contents != null) {
            for (ItemStack item : contents) {
                if (item == null || !item.equals((Object)questPaper)) continue;
                foundInInventory = true;
                break;
            }
        }
        if (!foundInInventory) {
            this.plugin.debugLog(Level.WARNING, "[Progress] Quest paper UUID {0} not found in player inventory - may be duplicated or removed", questInstanceId);
        }
        if ((questId = QuestPaper.getQuestId(questPaper, this.plugin.getQuestIdKey())) == null) {
            this.plugin.debugLog(Level.WARNING, "[Progress] Quest paper has no quest ID", new Object[0]);
            return false;
        }
        Quest quest = this.plugin.getQuestManager().getQuest(questId);
        if (quest == null) {
            this.plugin.debugLog(Level.WARNING, "[Progress] Quest ''{0}'' not found", questId);
            return false;
        }
        if (QuestPaper.isLocked(questPaper)) {
            this.plugin.debugLog(Level.INFO, "[Progress] Skipping locked quest paper for quest ''{0}'' (UUID: {1})", questId, questInstanceId);
            this.plugin.debugLog(Level.INFO, "[Progress] This quest has cost/consume-item conditions and was not unlocked", new Object[0]);
            this.plugin.debugLog(Level.INFO, "[Progress] Paper should have been unlocked when given via /sq give command", new Object[0]);
            return false;
        }
        if (!quest.hasPermission(player)) {
            this.plugin.debugLog(Level.INFO, "[Progress] Player ''{0}'' lacks permission for quest ''{1}'' (UUID: {2})", player.getName(), questId, questInstanceId);
            return false;
        }
        QuestProgress questProgress = this.getQuestProgress(player, quest, questInstanceId);
        if (questProgress == null) {
            this.plugin.debugLog(Level.WARNING, "[Progress] CRITICAL: No progress data for quest ''{0}'' (UUID: {1})", questId, questInstanceId);
            this.plugin.debugLog(Level.WARNING, "[Progress] This quest paper exists in inventory but has no progress data!", new Object[0]);
            this.plugin.debugLog(Level.WARNING, "[Progress] Quest will not be able to track progress until data is created", new Object[0]);
            this.plugin.debugLog(Level.WARNING, "[Progress] Try refreshing player queues or rejoining the server", new Object[0]);
            return false;
        }
        if (questProgress.isRedeemed()) {
            this.plugin.debugLog(Level.INFO, "[Progress] Skipping already redeemed quest ''{0}'' (UUID: {1})", questId, questInstanceId);
            return false;
        }
        if (questProgress.isComplete(quest)) {
            this.plugin.debugLog(Level.INFO, "[Progress] Skipping already complete quest ''{0}'' (UUID: {1})", questId, questInstanceId);
            return false;
        }
        if (!this.plugin.getQuestManager().isQuestActive(player, questId, questInstanceId)) {
            this.plugin.debugLog(Level.INFO, "[Progress] Skipping inactive quest ''{0}'' (UUID: {1})", questId, questInstanceId);
            return false;
        }
        if (quest.getConditions() != null && !(conditionResult = ConditionRegistry.checkConditions(player, quest, quest.getConditions(), false)).isSuccess()) {
            this.plugin.debugLog(Level.INFO, "[Progress] Quest conditions not met for quest ''{0}'' (UUID: {1}): {2}", questId, questInstanceId, conditionResult.getMessage());
            return false;
        }
        if (quest.isLockToPlayer()) {
            if (questProgress.isBound() && !questProgress.getOwnerUUID().equals(player.getUniqueId())) {
                return false;
            }
            if (!questProgress.isBound()) {
                this.bindQuestToPlayer(player, quest, questProgress, questPaper, questInstanceId);
            }
        }
        if (progressMade = progressCallback.checkProgress(player, quest, questProgress, questPaper)) {
            this.plugin.debugLog(Level.INFO, "[Progress] Quest ''{0}'' (UUID: {1}) progress updated for ''{2}''", quest.getDisplay(), questInstanceId, player.getName());
            boolean wasClaimableBeforeProgress = questProgress.isClaimable();
            boolean isCompleteNow = questProgress.isComplete(quest);
            if (isCompleteNow && !wasClaimableBeforeProgress) {
                this.plugin.debugLog(Level.INFO, "[Progress] Quest ''{0}'' (UUID: {1}) reached COMPLETION - marking as CLAIMABLE", quest.getDisplay(), questInstanceId);
                questProgress.setClaimable(true);
                this.plugin.getStatisticManager().incrementCompletion(player.getUniqueId(), quest.getTier(), quest.getDifficulty());
                this.plugin.debugLog(Level.INFO, "[Statistics] Incremented completion stats for player ''{0}''", player.getName());
                this.plugin.debugLog(Level.INFO, "[Queue] Removing completed quest ''{0}'' (UUID: {1}) from active queue", quest.getDisplay(), questInstanceId);
                this.plugin.getQuestManager().completeQuest(player, questId, questInstanceId);
                this.plugin.getProgressDisplayManager().showCompletion(player, quest);
                player.sendMessage(this.plugin.getMessageManager().getMessage("quest-claimable", Map.of("quest", quest.getDisplay())));
            } else {
                this.plugin.getProgressDisplayManager().showProgress(player, quest, questProgress);
            }
            boolean isActive = this.plugin.getQuestManager().isQuestActive(player, questId, questInstanceId);
            QuestPaper.updateQuestPaperWithStatus(questPaper, quest, questProgress, this.plugin.getMessageManager(), quest.getCustomLore(), isActive);
            this.plugin.getDataManager().saveDataAsync();
            return true;
        }
        return false;
    }

    private QuestProgress getQuestProgress(Player player, Quest quest, UUID questInstanceId) {
        if (quest.isLockToPlayer()) {
            return this.plugin.getDataManager().getLockedQuestInstance(questInstanceId);
        }
        return this.plugin.getDataManager().getQuestInstance(player, questInstanceId);
    }

    private void bindQuestToPlayer(Player player, Quest quest, QuestProgress questProgress, ItemStack questPaper, UUID questInstanceId) {
        questProgress.setOwnerUUID(player.getUniqueId());
        this.plugin.debugLog(Level.INFO, "[Binding] Quest ''{0}'' (UUID: {1}) bound to player ''{2}'' on FIRST PROGRESS", quest.getDisplay(), questInstanceId, player.getName());
        QuestPaper.updateQuestPaperWithStatus(questPaper, quest, questProgress, this.plugin.getMessageManager(), quest.getCustomLore(), true);
        this.plugin.getDataManager().saveDataAsync();
        player.sendMessage(this.plugin.getMessageManager().getMessage("quest-bound-to-you", Map.of("quest", quest.getDisplay())));
    }

    public boolean trackObjectiveProgress(Player player, Quest quest, Objective objective, QuestProgress questProgress, Event event) {
        String objectiveId = objective.getObjectiveId();
        int oldProgress = questProgress.getObjectiveProgress(objectiveId);
        if (oldProgress >= objective.getRequiredAmount()) {
            return false;
        }
        objective.setCurrentProgress(player.getUniqueId(), oldProgress);
        if (objective.handleEvent(player, event)) {
            int newProgress;
            int internalProgress = objective.getCurrentProgress(player.getUniqueId());
            if (internalProgress > oldProgress) {
                questProgress.setObjectiveProgress(objectiveId, internalProgress);
                newProgress = internalProgress;
            } else {
                int incrementAmount = 1;
                if (event instanceof EntityPickupItemEvent) {
                    EntityPickupItemEvent pickupEvent = (EntityPickupItemEvent)event;
                    incrementAmount = pickupEvent.getItem().getItemStack().getAmount();
                } else if (event instanceof CraftItemEvent) {
                    CraftItemEvent craftEvent = (CraftItemEvent)event;
                    incrementAmount = craftEvent.getRecipe().getResult().getAmount();
                    if (craftEvent.isShiftClick()) {
                        incrementAmount = Math.min(incrementAmount * 64, objective.getRequiredAmount() - oldProgress);
                    }
                } else if (event instanceof PlayerExpChangeEvent) {
                    PlayerExpChangeEvent expEvent = (PlayerExpChangeEvent)event;
                    incrementAmount = expEvent.getAmount();
                } else if (event instanceof FurnaceExtractEvent) {
                    FurnaceExtractEvent extractEvent = (FurnaceExtractEvent)event;
                    incrementAmount = extractEvent.getItemAmount();
                }
                questProgress.incrementObjectiveProgress(objectiveId, incrementAmount);
                newProgress = questProgress.getObjectiveProgress(objectiveId);
            }
            this.plugin.debugLog(Level.INFO, "[Progress] Objective ''{0}'' progress: {1} -> {2}", objectiveId, oldProgress, newProgress);
            List<Integer> reachedMilestones = objective.checkNewMilestones(player.getUniqueId(), oldProgress, newProgress);
            for (Integer milestone : reachedMilestones) {
                this.plugin.getProgressDisplayManager().showMilestone(player, quest, objective, milestone);
            }
            return true;
        }
        return false;
    }

    @FunctionalInterface
    public static interface ProgressCallback {
        public boolean checkProgress(Player var1, Quest var2, QuestProgress var3, ItemStack var4);
    }
}

