/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 */
package com.soaps.quest.listeners;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.conditions.ConditionRegistry;
import com.soaps.quest.conditions.ConditionResult;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import com.soaps.quest.utils.PlaceholderManager;
import com.soaps.quest.utils.QuestItemInteractionGuard;
import com.soaps.quest.utils.QuestPaper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class QuestPaperListener
implements Listener {
    private final SoapsQuest plugin;
    private final QuestItemInteractionGuard interactionGuard;

    public QuestPaperListener(SoapsQuest plugin) {
        this.plugin = plugin;
        this.interactionGuard = new QuestItemInteractionGuard(plugin);
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        QuestProgress questProgress;
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        if (!this.interactionGuard.isQuestPaper(item)) {
            return;
        }
        if (item == null) {
            return;
        }
        event.setUseInteractedBlock(Result.DENY);
        event.setUseItemInHand(Result.DENY);
        event.setCancelled(true);
        String questId = QuestPaper.getQuestId(item, this.plugin.getQuestIdKey());
        if (questId == null) {
            return;
        }
        Quest quest = this.plugin.getQuestManager().getQuest(questId);
        if (quest == null) {
            player.sendMessage(this.plugin.getMessageManager().getMessage("quest-not-found", Map.of("quest", questId)));
            return;
        }
        if (QuestPaper.isLocked(item)) {
            ConditionResult result = ConditionRegistry.checkConditions(player, quest, quest.getConditions(), true);
            if (result.isSuccess()) {
                QuestPaper.setLocked(item, false);
                ItemStack updatedPaper = QuestPaper.updateQuestPaper(item, quest, player, this.plugin.getMessageManager());
                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), updatedPaper);
                player.sendMessage(this.plugin.getMessageManager().getMessage("quest-unlocked"));
                return;
            }
            player.sendMessage(this.plugin.getMessageManager().getMessage("quest-unlock-failed", Map.of("reason", result.getMessage())));
            return;
        }
        if (!quest.hasPermission(player)) {
            player.sendMessage(this.plugin.getMessageManager().getMessage("quest-no-permission"));
            return;
        }
        UUID questInstanceId = QuestPaper.getQuestInstanceId(item);
        if (questInstanceId == null) {
            player.sendMessage(this.plugin.getMessageManager().getMessage("quest-not-found", Map.of("quest", questId)));
            return;
        }
        if (quest.isLockToPlayer()) {
            questProgress = this.plugin.getDataManager().getLockedQuestInstance(questInstanceId);
        } else {
            questProgress = this.plugin.getDataManager().getQuestInstance(player, questInstanceId);
            if (questProgress == null) {
                questProgress = this.plugin.getDataManager().transferOrAdoptQuest(player, questInstanceId, questId, quest.getRequiredAmount());
            }
        }
        if (questProgress == null) {
            UUID ownerUuid;
            if (quest.isLockToPlayer() && (ownerUuid = this.plugin.getDataManager().findQuestInstanceOwner(questInstanceId)) != null && !ownerUuid.equals(player.getUniqueId())) {
                OfflinePlayer owner = Bukkit.getOfflinePlayer((UUID)ownerUuid);
                String ownerName = owner.getName() != null ? owner.getName() : "Unknown";
                player.sendMessage(this.plugin.getMessageManager().getMessage("quest-locked-to-other", Map.of("player", ownerName)));
                return;
            }
            player.sendMessage(this.plugin.getMessageManager().getMessage("quest-not-found", Map.of("quest", questId)));
            return;
        }
        if (quest.isLockToPlayer() && questProgress.isBound() && !questProgress.getOwnerUUID().equals(player.getUniqueId())) {
            OfflinePlayer owner = Bukkit.getOfflinePlayer((UUID)questProgress.getOwnerUUID());
            String ownerName = owner.getName() != null ? owner.getName() : "Unknown";
            player.sendMessage(this.plugin.getMessageManager().getMessage("quest-locked-to-other", Map.of("player", ownerName)));
            return;
        }
        if (!questProgress.isClaimable()) {
            HashMap<String, String> placeholders = new HashMap<String, String>();
            placeholders.put("quest", quest.getDisplay());
            if (quest.hasObjectives()) {
                if (quest.isSequential()) {
                    List<Objective> objectives = quest.getObjectives();
                    int currentIndex = questProgress.getCurrentObjectiveIndex();
                    if (currentIndex < objectives.size()) {
                        Objective currentObjective = objectives.get(currentIndex);
                        int objProgress = questProgress.getObjectiveProgress(currentObjective.getObjectiveId());
                        int objRequired = currentObjective.getRequiredAmount();
                        placeholders.put("objective", currentObjective.getDescription());
                        placeholders.put("progress", String.valueOf(objProgress));
                        placeholders.put("amount", String.valueOf(objRequired));
                        player.sendMessage(this.plugin.getMessageManager().getMessage("objective-sequential-progress", placeholders));
                    } else {
                        player.sendMessage(this.plugin.getMessageManager().getMessage("quest-progress-display", placeholders));
                    }
                } else {
                    String[] lines;
                    PlaceholderManager placeholderManager = new PlaceholderManager(this.plugin);
                    PlaceholderManager.PlaceholderContext context = new PlaceholderManager.PlaceholderContext().player(player).quest(quest).progress(questProgress);
                    String message = this.plugin.getMessageManager().getRawMessage("quest-progress-display");
                    message = placeholderManager.replacePlaceholders(message, context);
                    for (String line : lines = message.split("\n")) {
                        if (line.trim().isEmpty()) continue;
                        player.sendMessage(this.plugin.getMessageManager().parseColorCodes(line));
                    }
                }
            } else {
                placeholders.put("objective", quest.getObjectiveDescription());
                placeholders.put("progress", String.valueOf(questProgress.getCurrentProgress()));
                placeholders.put("amount", String.valueOf(questProgress.getRequiredAmount()));
                player.sendMessage(this.plugin.getMessageManager().getMessage("quest-progress-display", placeholders));
            }
            return;
        }
        if (questProgress.isRedeemed()) {
            player.sendMessage(this.plugin.getMessageManager().getMessage("quest-already-redeemed", Map.of("quest", quest.getDisplay())));
            return;
        }
        if (quest.isLockToPlayer() && questProgress.isBound() && !questProgress.getOwnerUUID().equals(player.getUniqueId())) {
            OfflinePlayer owner = Bukkit.getOfflinePlayer((UUID)questProgress.getOwnerUUID());
            String ownerName = owner.getName() != null ? owner.getName() : "Unknown";
            player.sendMessage(this.plugin.getMessageManager().getMessage("quest-locked-to-other", Map.of("player", ownerName)));
            return;
        }
        this.plugin.getRewardManager().giveRewards(questId, player);
        this.plugin.getStatisticManager().incrementRewardsClaimed(player.getUniqueId());
        this.plugin.getQuestLogger().logQuestCompletion(player, quest);
        this.plugin.getDataManager().markQuestRedeemed(player, questInstanceId);
        if (this.plugin.getRecurringQuestManager() != null) {
            this.plugin.getRecurringQuestManager().handleQuestClaimed(player, questId);
        }
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), null);
        }
        this.plugin.debugLog(Level.INFO, "[Claim] Removing quest ''{0}'' (UUID: {1}) from queue", quest.getDisplay(), questInstanceId);
        this.plugin.getQuestManager().removeQuestFromQueue(player, questId, questInstanceId);
        this.plugin.debugLog(Level.INFO, "[Claim] Refreshing player queues after claim", new Object[0]);
        this.plugin.getQuestManager().refreshPlayerQueues(player);
        this.plugin.getPlayerListener().scheduleProtectedPaperUpdate(player);
        this.plugin.debugLog(Level.INFO, "[Claim] Scheduled protected paper update", new Object[0]);
        if (quest.isTemporary() && this.plugin.getQuestManager().isGeneratedQuest(questId)) {
            this.plugin.getQuestManager().removeQuest(questId);
            this.plugin.getRewardManager().reload();
        }
        this.plugin.getDataManager().saveDataAsync();
        player.sendMessage(this.plugin.getMessageManager().getMessage("quest-completion-redeemed", Map.of("quest", quest.getDisplay())));
    }
}

