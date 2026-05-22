/*
 * Decompiled with CFR 0.152.
 */
package com.soaps.quest.managers;

import com.soaps.quest.SoapsQuest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class StatisticManager {
    private final SoapsQuest plugin;
    private final Map<UUID, PlayerStatistics> statisticsCache;

    public StatisticManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.statisticsCache = new HashMap<UUID, PlayerStatistics>();
    }

    public void loadStatistics() {
        this.statisticsCache.clear();
        this.plugin.debugLog("StatisticManager initialized");
    }

    public PlayerStatistics getStatistics(UUID playerUuid) {
        return this.statisticsCache.computeIfAbsent(playerUuid, uuid -> this.plugin.getDataManager().loadPlayerStatistics((UUID)uuid));
    }

    public void incrementCompletion(UUID playerUuid, String tier, String difficulty) {
        PlayerStatistics stats = this.getStatistics(playerUuid);
        ++stats.totalCompletions;
        if (tier != null) {
            String tierKey = tier.toLowerCase();
            stats.tierCompletions.put(tierKey, stats.tierCompletions.getOrDefault(tierKey, 0) + 1);
        }
        if (difficulty != null) {
            String diffKey = difficulty.toLowerCase();
            stats.difficultyCompletions.put(diffKey, stats.difficultyCompletions.getOrDefault(diffKey, 0) + 1);
        }
        this.plugin.getDataManager().savePlayerStatistics(playerUuid, stats);
        this.plugin.debugLog(Level.INFO, "[Statistics] Player {0} completed a quest. Total: {1}", playerUuid, stats.totalCompletions);
    }

    public void incrementRewardsClaimed(UUID playerUuid) {
        PlayerStatistics stats = this.getStatistics(playerUuid);
        ++stats.rewardsClaimed;
        this.plugin.getDataManager().savePlayerStatistics(playerUuid, stats);
    }

    public void clearCache() {
        this.statisticsCache.clear();
    }

    public void saveAllStatistics() {
        if (this.statisticsCache.isEmpty()) {
            return;
        }
        int savedCount = 0;
        for (Map.Entry<UUID, PlayerStatistics> entry : this.statisticsCache.entrySet()) {
            this.plugin.getDataManager().savePlayerStatisticsDirect(entry.getKey(), entry.getValue());
            ++savedCount;
        }
        if (savedCount > 0) {
            this.plugin.getDataManager().flushToDisk();
        }
    }

    public static class PlayerStatistics {
        public int totalCompletions;
        public Map<String, Integer> tierCompletions;
        public Map<String, Integer> difficultyCompletions;
        public int rewardsClaimed;

        public PlayerStatistics() {
            this.totalCompletions = 0;
            this.tierCompletions = new HashMap<String, Integer>();
            this.difficultyCompletions = new HashMap<String, Integer>();
            this.rewardsClaimed = 0;
        }

        public PlayerStatistics(int totalCompletions, Map<String, Integer> tierCompletions, Map<String, Integer> difficultyCompletions, int rewardsClaimed) {
            this.totalCompletions = totalCompletions;
            this.tierCompletions = tierCompletions != null ? new HashMap<String, Integer>(tierCompletions) : new HashMap();
            this.difficultyCompletions = difficultyCompletions != null ? new HashMap<String, Integer>(difficultyCompletions) : new HashMap();
            this.rewardsClaimed = rewardsClaimed;
        }

        public int getTierCompletions(String tier) {
            return this.tierCompletions.getOrDefault(tier.toLowerCase(), 0);
        }

        public int getDifficultyCompletions(String difficulty) {
            return this.difficultyCompletions.getOrDefault(difficulty.toLowerCase(), 0);
        }
    }
}

