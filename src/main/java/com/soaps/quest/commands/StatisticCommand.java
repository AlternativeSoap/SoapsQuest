/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.soaps.quest.commands;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.managers.DifficultyManager;
import com.soaps.quest.managers.StatisticManager;
import com.soaps.quest.managers.TierManager;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatisticCommand {
    private final SoapsQuest plugin;

    public StatisticCommand(SoapsQuest plugin) {
        this.plugin = plugin;
    }

    public boolean handle(CommandSender sender, String[] args) {
        String targetName;
        UUID targetUuid;
        if (args.length >= 2) {
            if (!sender.hasPermission("soapsquest.admin")) {
                sender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
                return true;
            }
            String targetPlayerName = args[1];
            Player targetPlayer = Bukkit.getPlayer((String)targetPlayerName);
            if (targetPlayer != null) {
                targetUuid = targetPlayer.getUniqueId();
                targetName = targetPlayer.getName();
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached((String)targetPlayerName);
                if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
                    sender.sendMessage(this.plugin.getMessageManager().getMessage("player-not-found", Map.of("player", targetPlayerName)));
                    return true;
                }
                targetUuid = offlinePlayer.getUniqueId();
                targetName = offlinePlayer.getName() != null ? offlinePlayer.getName() : targetPlayerName;
            }
        } else {
            if (!(sender instanceof Player)) {
                if (sender != null) {
                    sender.sendMessage(this.plugin.getMessageManager().getMessage("player-only"));
                }
                return true;
            }
            Player player = (Player)sender;
            targetUuid = player.getUniqueId();
            targetName = player.getName();
        }
        if (!sender.hasPermission("soapsquest.statistic")) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        StatisticManager.PlayerStatistics stats = this.plugin.getStatisticManager().getStatistics(targetUuid);
        if (stats == null) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("statistic-no-data", Map.of("player", targetName)));
            return true;
        }
        this.displayStatistics(sender, targetName, stats);
        return true;
    }

    private void displayStatistics(CommandSender sender, String playerName, StatisticManager.PlayerStatistics stats) {
        sender.sendMessage(this.plugin.getMessageManager().getMessage("statistic-header", Map.of("player", playerName)));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("statistic-total", Map.of("total", String.valueOf(stats.totalCompletions))));
        if (!stats.tierCompletions.isEmpty()) {
            StringBuilder tierDisplay = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, Integer> entry : stats.tierCompletions.entrySet()) {
                if (entry.getValue() <= 0) continue;
                if (!first) {
                    tierDisplay.append(" &8| ");
                }
                String tierName = entry.getKey();
                TierManager.Tier tierInfo = this.plugin.getTierManager().getTier(tierName);
                if (tierInfo != null) {
                    tierDisplay.append(tierInfo.color).append(tierInfo.display).append(": &a").append(entry.getValue());
                } else {
                    String displayName = tierName.substring(0, 1).toUpperCase() + tierName.substring(1);
                    tierDisplay.append("&7").append(displayName).append(": &a").append(entry.getValue());
                }
                first = false;
            }
            if (tierDisplay.length() > 0) {
                sender.sendMessage(this.plugin.getMessageManager().parseColorCodes(tierDisplay.toString()));
            }
        }
        if (!stats.difficultyCompletions.isEmpty()) {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&fDifficulties:"));
            for (Map.Entry<String, Integer> entry : stats.difficultyCompletions.entrySet()) {
                if (entry.getValue() <= 0) continue;
                String difficultyName = entry.getKey();
                DifficultyManager.Difficulty difficultyInfo = this.plugin.getDifficultyManager().getDifficulty(difficultyName);
                if (difficultyInfo != null) {
                    sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("  &7" + difficultyInfo.display + ": &a" + String.valueOf(entry.getValue())));
                    continue;
                }
                String displayName = difficultyName.substring(0, 1).toUpperCase() + difficultyName.substring(1);
                sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("  &7" + displayName + ": &a" + String.valueOf(entry.getValue())));
            }
        }
        sender.sendMessage(this.plugin.getMessageManager().getMessage("statistic-rewards", Map.of("claimed", String.valueOf(stats.rewardsClaimed))));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("statistic-footer"));
    }
}

