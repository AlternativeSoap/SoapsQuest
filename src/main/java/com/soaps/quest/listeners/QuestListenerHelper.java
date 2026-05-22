package com.soaps.quest.listeners;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.objectives.PlaceholderObjective;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import com.soaps.quest.utils.QuestPaper;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public final class QuestListenerHelper {
    private QuestListenerHelper() {
    }

    public static boolean processObjectives(SoapsQuest plugin, Player player, Quest quest, QuestProgress progress, Event event) {
        boolean objectiveCompleted;
        boolean made;
        block3: {
            List<Objective> objectives;
            block2: {
                Objective currentObjective;
                int objProgress;
                made = false;
                objectiveCompleted = false;
                objectives = quest.getObjectives();
                if (!quest.isSequential()) break block2;
                int currentIndex = progress.getCurrentObjectiveIndex();
                if (currentIndex >= objectives.size() || (objProgress = progress.getObjectiveProgress((currentObjective = objectives.get(currentIndex)).getObjectiveId())) >= currentObjective.getRequiredAmount() || !plugin.getQuestProgressTracker().trackObjectiveProgress(player, quest, currentObjective, progress, event)) break block3;
                made = true;
                if (progress.getObjectiveProgress(currentObjective.getObjectiveId()) < currentObjective.getRequiredAmount()) break block3;
                objectiveCompleted = true;
                progress.advanceToNextObjective(objectives.size());
                break block3;
            }
            for (Objective objective : objectives) {
                int objProgress = progress.getObjectiveProgress(objective.getObjectiveId());
                if (objProgress >= objective.getRequiredAmount() || !plugin.getQuestProgressTracker().trackObjectiveProgress(player, quest, objective, progress, event)) continue;
                made = true;
            }
        }
        if (made && quest.isSequential() && objectiveCompleted && !progress.isComplete(quest)) {
            player.sendMessage(plugin.getMessageManager().getMessage("objective-sequential-complete"));
        }
        return made;
    }

    public static void scanAndTrackProgress(SoapsQuest plugin, Player player, Event event) {
        QuestListenerHelper.scanAndTrackProgress(plugin, player, event, 1);
    }

    public static void scanAndTrackProgress(SoapsQuest plugin, Player player, Event event, int amount) {
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) {
            return;
        }
        Map<String, UUID> activeSnapshot = plugin.getQuestManager().snapshotActiveQuestInstances(player);
        for (ItemStack questPaper : contents) {
            Quest quest;
            String questId;
            UUID instanceId;
            if (questPaper == null || !QuestPaper.isQuestPaper(questPaper, plugin.getQuestIdKey()) || (questId = QuestPaper.getQuestId(questPaper, plugin.getQuestIdKey())) == null || (instanceId = QuestPaper.getQuestInstanceId(questPaper)) == null || !QuestListenerHelper.shouldTrackInstance(activeSnapshot, questId, instanceId) || (quest = plugin.getQuestManager().getQuest(questId)) == null || !quest.hasObjectives()) continue;
            plugin.getQuestProgressTracker().trackProgress(player, questPaper, amount, (p, q, progress, paper) -> QuestListenerHelper.processObjectives(plugin, p, q, progress, event));
        }
    }

    public static void scanAndTrackDirect(SoapsQuest plugin, Player player, Event event) {
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) {
            return;
        }
        Map<String, UUID> activeSnapshot = plugin.getQuestManager().snapshotActiveQuestInstances(player);
        for (ItemStack questPaper : contents) {
            Quest quest;
            String questId;
            UUID instanceId;
            if (questPaper == null || !QuestPaper.isQuestPaper(questPaper, plugin.getQuestIdKey()) || (questId = QuestPaper.getQuestId(questPaper, plugin.getQuestIdKey())) == null || (instanceId = QuestPaper.getQuestInstanceId(questPaper)) == null || !QuestListenerHelper.shouldTrackInstance(activeSnapshot, questId, instanceId) || (quest = plugin.getQuestManager().getQuest(questId)) == null || !quest.hasObjectives()) continue;
            plugin.getQuestProgressTracker().trackProgress(player, questPaper, 1, (p, q, progress, paper) -> QuestListenerHelper.trackDirectObjectives(plugin, p, q, progress, event));
        }
    }

    public static void scanAndTrackPlaceholder(SoapsQuest plugin, Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) {
            return;
        }
        Map<String, UUID> activeSnapshot = plugin.getQuestManager().snapshotActiveQuestInstances(player);
        for (ItemStack questPaper : contents) {
            Quest quest;
            String questId;
            UUID instanceId;
            if (questPaper == null || !QuestPaper.isQuestPaper(questPaper, plugin.getQuestIdKey()) || (questId = QuestPaper.getQuestId(questPaper, plugin.getQuestIdKey())) == null || (instanceId = QuestPaper.getQuestInstanceId(questPaper)) == null || !QuestListenerHelper.shouldTrackInstance(activeSnapshot, questId, instanceId) || (quest = plugin.getQuestManager().getQuest(questId)) == null || !quest.hasObjectives()) continue;
            plugin.getQuestProgressTracker().trackProgress(player, questPaper, 1, (p, q, progress, paper) -> QuestListenerHelper.processPlaceholderObjectives(plugin, p, q, progress));
        }
    }

    private static boolean shouldTrackInstance(Map<String, UUID> activeSnapshot, String questId, UUID instanceId) {
        UUID activeInstance = activeSnapshot.get(questId);
        return activeInstance != null && activeInstance.equals(instanceId);
    }

    public static boolean trackDirectObjectives(SoapsQuest plugin, Player player, Quest quest, QuestProgress progress, Event event) {
        boolean progressMade = false;
        boolean objectiveCompleted = false;
        List<Objective> objectives = quest.getObjectives();
        UUID playerId = player.getUniqueId();
        if (quest.isSequential()) {
            int currentIndex = progress.getCurrentObjectiveIndex();
            if (currentIndex >= objectives.size()) {
                return false;
            }
            Objective objective = objectives.get(currentIndex);
            int oldProgress = QuestListenerHelper.syncObjectiveProgressFromPersisted(progress, objective, playerId);
            if (oldProgress >= objective.getRequiredAmount() || !objective.handleEvent(player, event)) {
                return false;
            }
            progressMade = QuestListenerHelper.applyDirectObjectiveUpdate(plugin, player, quest, progress, objective, playerId, oldProgress);
            if (progressMade && progress.getObjectiveProgress(objective.getObjectiveId()) >= objective.getRequiredAmount()) {
                objectiveCompleted = true;
                progress.advanceToNextObjective(objectives.size());
            }
        } else {
            for (Objective objective : objectives) {
                int oldProgress = QuestListenerHelper.syncObjectiveProgressFromPersisted(progress, objective, playerId);
                if (oldProgress >= objective.getRequiredAmount() || !objective.handleEvent(player, event)) continue;
                if (QuestListenerHelper.applyDirectObjectiveUpdate(plugin, player, quest, progress, objective, playerId, oldProgress)) {
                    progressMade = true;
                }
            }
        }
        if (progressMade && quest.isSequential() && objectiveCompleted && !progress.isComplete(quest)) {
            player.sendMessage(plugin.getMessageManager().getMessage("objective-sequential-complete"));
        }
        return progressMade;
    }

    public static boolean processPlaceholderObjectives(SoapsQuest plugin, Player player, Quest quest, QuestProgress progress) {
        boolean progressMade = false;
        boolean objectiveCompleted = false;
        UUID playerId = player.getUniqueId();
        List<Objective> objectives = quest.getObjectives();
        int currentIndex = quest.isSequential() ? progress.getCurrentObjectiveIndex() : -1;
        for (int i = 0; i < objectives.size(); ++i) {
            if (quest.isSequential() && i != currentIndex) {
                continue;
            }
            Objective objective = objectives.get(i);
            if (!(objective instanceof PlaceholderObjective placeholderObjective)) {
                continue;
            }
            int oldProgress = QuestListenerHelper.syncObjectiveProgressFromPersisted(progress, objective, playerId);
            if (oldProgress >= objective.getRequiredAmount()) {
                continue;
            }
            if (!placeholderObjective.checkPlaceholder(player)) {
                continue;
            }
            if (!QuestListenerHelper.applyDirectObjectiveUpdate(plugin, player, quest, progress, objective, playerId, oldProgress)) {
                continue;
            }
            progressMade = true;
            if (quest.isSequential() && progress.getObjectiveProgress(objective.getObjectiveId()) >= objective.getRequiredAmount()) {
                objectiveCompleted = true;
                progress.advanceToNextObjective(objectives.size());
                break;
            }
        }
        if (progressMade && quest.isSequential() && objectiveCompleted && !progress.isComplete(quest)) {
            player.sendMessage(plugin.getMessageManager().getMessage("objective-sequential-complete"));
        }
        return progressMade;
    }

    private static int syncObjectiveProgressFromPersisted(QuestProgress progress, Objective objective, UUID playerId) {
        int persistedProgress = progress.getObjectiveProgress(objective.getObjectiveId());
        objective.setCurrentProgress(playerId, persistedProgress);
        return persistedProgress;
    }

    private static boolean applyDirectObjectiveUpdate(SoapsQuest plugin, Player player, Quest quest, QuestProgress progress, Objective objective, UUID playerId, int oldProgress) {
        int newProgress = objective.getCurrentProgress(playerId);
        progress.setObjectiveProgress(objective.getObjectiveId(), newProgress);
        List<Integer> reachedMilestones = objective.checkNewMilestones(playerId, oldProgress, newProgress);
        for (Integer milestone : reachedMilestones) {
            plugin.getProgressDisplayManager().showMilestone(player, quest, objective, milestone);
        }
        return true;
    }
}
