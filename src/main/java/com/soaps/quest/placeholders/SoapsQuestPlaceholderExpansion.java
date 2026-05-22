/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.clip.placeholderapi.expansion.PlaceholderExpansion
 *  org.bukkit.OfflinePlayer
 *  org.jetbrains.annotations.NotNull
 */
package com.soaps.quest.placeholders;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.managers.StatisticManager;
import java.util.UUID;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class SoapsQuestPlaceholderExpansion
extends PlaceholderExpansion {
    private final SoapsQuest plugin;

    public SoapsQuestPlaceholderExpansion(SoapsQuest plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public String getIdentifier() {
        return "soapsquest";
    }

    @NotNull
    public String getAuthor() {
        return "SoapsQuest";
    }

    @NotNull
    public String getVersion() {
        return this.plugin.getPluginMeta().getVersion();
    }

    public boolean persist() {
        return true;
    }

    public boolean canRegister() {
        return true;
    }

    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player != null && params.startsWith("player_")) {
            return this.handlePlayerPlaceholder(player, params);
        }
        return null;
    }

    private String handlePlayerPlaceholder(OfflinePlayer player, String params) {
        UUID playerUuid = player.getUniqueId();
        StatisticManager.PlayerStatistics stats = this.plugin.getStatisticManager().getStatistics(playerUuid);
        if (stats == null) {
            return "0";
        }
        String identifier = params.substring(7);
        if (identifier.equals("quests")) {
            return String.valueOf(stats.totalCompletions);
        }
        if (identifier.equals("sigils")) {
            if (this.plugin.getSigilManager() == null) {
                return "0";
            }
            return String.format("%.2f", this.plugin.getSigilManager().getBalance(playerUuid));
        }
        if (identifier.equals("sigils_raw")) {
            if (this.plugin.getSigilManager() == null) {
                return "0";
            }
            return String.valueOf(this.plugin.getSigilManager().getBalance(playerUuid));
        }
        if (identifier.startsWith("tier_")) {
            String tier = identifier.substring(5);
            return String.valueOf(stats.getTierCompletions(tier));
        }
        if (identifier.startsWith("difficulty_")) {
            String difficulty = identifier.substring(11);
            return String.valueOf(stats.getDifficultyCompletions(difficulty));
        }
        return null;
    }
}

