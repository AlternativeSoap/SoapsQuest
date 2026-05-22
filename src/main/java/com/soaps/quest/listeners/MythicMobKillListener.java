/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.bukkit.MythicBukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.inventory.ItemStack
 */
package com.soaps.quest.listeners;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.objectives.KillMythicMobObjective;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.utils.QuestPaper;
import io.lumine.mythic.bukkit.MythicBukkit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class MythicMobKillListener
implements Listener {
    private final SoapsQuest plugin;

    public MythicMobKillListener(SoapsQuest plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityDeath(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (!(player instanceof Player)) {
            return;
        }
        Player player2 = player;
        UUID entityUUID = event.getEntity().getUniqueId();
        MythicBukkit.inst().getMobManager().getActiveMob(entityUUID).ifPresent(activeMob -> {
            boolean progressMade;
            Quest quest;
            UUID questInstanceId;
            String questId;
            ItemStack item;
            String mythicMobType = activeMob.getMobType();
            ItemStack[] contents = player2.getInventory().getContents();
            if (contents == null) {
                return;
            }
            Map<String, UUID> activeSnapshot = this.plugin.getQuestManager().snapshotActiveQuestInstances(player2);
            ItemStack[] arr$ = contents;
            int len$ = arr$.length;
            for (int i$ = 0; !(i$ >= len$ || (item = arr$[i$]) != null && (questId = QuestPaper.getQuestId(item, this.plugin.getQuestIdKey())) != null && (questInstanceId = QuestPaper.getQuestInstanceId(item)) != null && activeSnapshot.get(questId) != null && activeSnapshot.get(questId).equals(questInstanceId) && (quest = this.plugin.getQuestManager().getQuest(questId)) != null && quest.hasObjectives() && !QuestPaper.isLocked(item) && quest.hasPermission(player2) && (progressMade = this.plugin.getQuestProgressTracker().trackProgress(player2, item, 1, (p, q, progress, paper) -> {
                boolean objectiveCompleted;
                boolean made;
                block3: {
                    List<Objective> objectives;
                    block2: {
                        KillMythicMobObjective mythicObj;
                        Objective currentObjective;
                        made = false;
                        objectiveCompleted = false;
                        objectives = q.getObjectives();
                        if (!q.isSequential()) break block2;
                        int currentIndex = progress.getCurrentObjectiveIndex();
                        if (currentIndex >= objectives.size() || (currentObjective = objectives.get(currentIndex)).isComplete(p.getUniqueId()) || !(currentObjective instanceof KillMythicMobObjective) || !(mythicObj = (KillMythicMobObjective)currentObjective).getMobType().equalsIgnoreCase(mythicMobType)) break block3;
                        progress.incrementObjectiveProgress(currentObjective.getObjectiveId(), 1);
                        made = true;
                        if (progress.getObjectiveProgress(currentObjective.getObjectiveId()) < currentObjective.getRequiredAmount()) break block3;
                        objectiveCompleted = true;
                        progress.advanceToNextObjective(objectives.size());
                        break block3;
                    }
                    for (Objective objective : objectives) {
                        KillMythicMobObjective mythicObj;
                        if (objective.isComplete(p.getUniqueId()) || !(objective instanceof KillMythicMobObjective) || !(mythicObj = (KillMythicMobObjective)objective).getMobType().equalsIgnoreCase(mythicMobType)) continue;
                        progress.incrementObjectiveProgress(objective.getObjectiveId(), 1);
                        made = true;
                    }
                }
                if (made && q.isSequential() && objectiveCompleted && !progress.isComplete(q)) {
                    p.sendMessage(this.plugin.getMessageManager().getMessage("objective-sequential-complete"));
                }
                return made;
            }))); ++i$) {
            }
        });
    }
}

