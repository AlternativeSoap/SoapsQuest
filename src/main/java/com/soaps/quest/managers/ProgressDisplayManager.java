/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.bossbar.BossBar
 *  net.kyori.adventure.bossbar.BossBar$Color
 *  net.kyori.adventure.bossbar.BossBar$Overlay
 *  net.kyori.adventure.text.Component
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.NamespacedKey
 *  org.bukkit.Registry
 *  org.bukkit.Sound
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package com.soaps.quest.managers;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.managers.MessageManager;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class ProgressDisplayManager {
    private final SoapsQuest plugin;
    private final MessageManager messageManager;
    private DisplayMode displayMode;
    private int chatInterval;
    private BossBar.Color bossbarColor;
    private BossBar.Overlay bossbarOverlay;
    private int bossbarDuration;
    private final Map<String, Integer> progressCounts;
    private final Map<UUID, BossBar> activeBossbars;
    private final Map<UUID, BukkitTask> bossbarRemovalTasks;

    public ProgressDisplayManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
        this.progressCounts = new HashMap<String, Integer>();
        this.activeBossbars = new HashMap<UUID, BossBar>();
        this.bossbarRemovalTasks = new HashMap<UUID, BukkitTask>();
        this.loadConfiguration();
    }

    private void loadConfiguration() {
        FileConfiguration config = this.plugin.getConfig();
        String modeStr = config.getString("progress-display.mode", "actionbar");
        String finalModeStr = modeStr != null ? modeStr.toUpperCase() : "ACTIONBAR";
        try {
            this.displayMode = DisplayMode.valueOf(finalModeStr);
        }
        catch (IllegalArgumentException e) {
            this.plugin.getLogger().warning(() -> "Invalid progress display mode: " + finalModeStr + ", defaulting to ACTIONBAR");
            this.displayMode = DisplayMode.ACTIONBAR;
        }
        this.chatInterval = config.getInt("progress-display.interval", 5);
        if (this.chatInterval < 1) {
            this.chatInterval = 1;
        }
        if (this.displayMode == DisplayMode.BOSSBAR) {
            String colorStr = config.getString("progress-display.bossbar.color", "GREEN");
            String finalColorStr = colorStr != null ? colorStr.toUpperCase() : "GREEN";
            this.bossbarColor = this.parseBossBarColor(finalColorStr);
            String styleStr = config.getString("progress-display.bossbar.style", "SEGMENTED_10");
            String finalStyleStr = styleStr != null ? styleStr.toUpperCase() : "SEGMENTED_10";
            this.bossbarOverlay = this.parseBossBarOverlay(finalStyleStr);
            this.bossbarDuration = config.getInt("progress-display.bossbar.duration", 5);
            if (this.bossbarDuration < 1) {
                this.bossbarDuration = 1;
            }
        }
    }

    public void showProgress(Player player, Quest quest, int currentProgress) {
        if (this.displayMode == DisplayMode.NONE) {
            return;
        }
        int requiredAmount = quest.getRequiredAmount();
        if (currentProgress >= requiredAmount) {
            return;
        }
        HashMap<String, String> placeholders = new HashMap<String, String>();
        placeholders.put("quest", quest.getDisplay());
        placeholders.put("objective", quest.getObjectiveDescription());
        placeholders.put("progress", String.valueOf(currentProgress));
        placeholders.put("amount", String.valueOf(requiredAmount));
        switch (this.displayMode.ordinal()) {
            case 0: {
                this.showChatProgress(player, quest, placeholders);
                break;
            }
            case 1: {
                this.showActionbarProgress(player, placeholders);
                break;
            }
            case 2: {
                this.showBossbarProgress(player, placeholders, currentProgress, requiredAmount);
                break;
            }
        }
    }

    public void showProgress(Player player, Quest quest, QuestProgress questProgress) {
        if (this.displayMode == DisplayMode.NONE) {
            return;
        }
        if (!quest.hasObjectives()) {
            this.showProgress(player, quest, questProgress.getCurrentProgress());
            return;
        }
        StringBuilder progressText = new StringBuilder();
        int completedCount = 0;
        int totalCount = quest.getObjectives().size();
        for (Objective objective : quest.getObjectives()) {
            int objRequired;
            boolean objComplete;
            int objProgress = questProgress.getObjectiveProgress(objective.getObjectiveId());
            boolean bl = objComplete = objProgress >= (objRequired = objective.getRequiredAmount());
            if (objComplete) {
                ++completedCount;
            }
            if (progressText.length() > 0) {
                progressText.append(" | ");
            }
            String status = objComplete ? "\u2713" : "";
            progressText.append(status).append(objective.getType().toUpperCase()).append(": ").append(objProgress).append("/").append(objRequired);
        }
        HashMap<String, String> placeholders = new HashMap<String, String>();
        placeholders.put("quest", quest.getDisplay());
        placeholders.put("objective", progressText.toString());
        placeholders.put("progress", String.valueOf(completedCount));
        placeholders.put("amount", String.valueOf(totalCount));
        placeholders.put("objectives_complete", String.valueOf(completedCount));
        placeholders.put("objectives_total", String.valueOf(totalCount));
        if (completedCount >= totalCount) {
            return;
        }
        switch (this.displayMode.ordinal()) {
            case 0: {
                this.showChatProgress(player, quest, placeholders);
                break;
            }
            case 1: {
                this.showActionbarProgress(player, placeholders);
                break;
            }
            case 2: {
                this.showBossbarProgress(player, placeholders, completedCount, totalCount);
                break;
            }
        }
    }

    private void showChatProgress(Player player, Quest quest, Map<String, String> placeholders) {
        Component message;
        String key = quest.getQuestId() + "-" + String.valueOf(player.getUniqueId());
        int count = this.progressCounts.getOrDefault(key, 0) + 1;
        this.progressCounts.put(key, count);
        if (count % this.chatInterval == 0 && (message = this.messageManager.getMessage("quest-progress-updated", placeholders)) != null) {
            player.sendMessage(message);
        }
    }

    private void showActionbarProgress(Player player, Map<String, String> placeholders) {
        Component message = this.messageManager.getMessage("quest-progress-actionbar", placeholders);
        if (message != null) {
            player.sendActionBar(message);
        }
    }

    private void showBossbarProgress(Player player, Map<String, String> placeholders, int currentProgress, int requiredAmount) {
        UUID playerId = player.getUniqueId();
        Component titleComponent = this.messageManager.getMessage("quest-progress-bossbar", placeholders);
        float progress = (float)Math.min(1.0, (double)currentProgress / (double)requiredAmount);
        BossBar bossBar = this.activeBossbars.get(playerId);
        if (bossBar == null) {
            bossBar = BossBar.bossBar((Component)titleComponent, (float)progress, (BossBar.Color)this.bossbarColor, (BossBar.Overlay)this.bossbarOverlay);
            this.activeBossbars.put(playerId, bossBar);
            player.showBossBar(bossBar);
        } else {
            bossBar.name(titleComponent);
            bossBar.progress(progress);
        }
        BukkitTask existingTask = this.bossbarRemovalTasks.remove(playerId);
        if (existingTask != null) {
            existingTask.cancel();
        }
        BukkitTask removalTask = Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> {
            BossBar bar = this.activeBossbars.remove(playerId);
            if (bar != null) {
                player.hideBossBar(bar);
            }
            this.bossbarRemovalTasks.remove(playerId);
        }, (long)this.bossbarDuration * 20L);
        this.bossbarRemovalTasks.put(playerId, removalTask);
    }

    public void showCompletion(Player player, Quest quest) {
        String soundName;
        if (this.displayMode == DisplayMode.BOSSBAR) {
            BukkitTask removalTask;
            UUID playerId = player.getUniqueId();
            BossBar bar = this.activeBossbars.remove(playerId);
            if (bar != null) {
                player.hideBossBar(bar);
            }
            if ((removalTask = this.bossbarRemovalTasks.remove(playerId)) != null) {
                removalTask.cancel();
            }
        }
        String key = quest.getQuestId() + "-" + String.valueOf(player.getUniqueId());
        this.progressCounts.remove(key);
        Component message = this.messageManager.getMessage("quest-complete", Map.of("quest", quest.getDisplay()));
        if (message != null) {
            player.sendMessage(message);
        }
        if ((soundName = this.plugin.getConfig().getString("completion-sound", "UI_TOAST_CHALLENGE_COMPLETE")) != null && !soundName.equalsIgnoreCase("none")) {
            try {
                String soundKey = soundName.toLowerCase().replace('_', '.');
                NamespacedKey nsKey = NamespacedKey.minecraft((String)soundKey);
                Sound sound = (Sound)Registry.SOUNDS.get(nsKey);
                Location loc = player.getLocation();
                if (loc != null && sound != null) {
                    float volume = (float)this.plugin.getConfig().getDouble("completion-sound-volume", 1.0);
                    float pitch = (float)this.plugin.getConfig().getDouble("completion-sound-pitch", 1.0);
                    player.playSound(loc, sound, volume, pitch);
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
    }

    public void showMilestone(Player player, Quest quest, Objective objective, int milestone) {
        String soundName;
        HashMap<String, String> placeholders = new HashMap<String, String>();
        placeholders.put("quest", quest.getDisplay());
        placeholders.put("objective", objective.getDescription());
        placeholders.put("milestone", String.valueOf(milestone));
        placeholders.put("progress_percent", String.valueOf(milestone));
        Component message = this.messageManager.getMessage("quest-milestone", placeholders);
        if (message != null) {
            player.sendMessage(message);
        }
        if ((soundName = this.plugin.getConfig().getString("milestone-sound", "ENTITY_PLAYER_LEVELUP")) != null && !soundName.equalsIgnoreCase("none")) {
            try {
                String soundKey = soundName.toLowerCase().replace('_', '.');
                NamespacedKey key = NamespacedKey.minecraft((String)soundKey);
                Sound sound = (Sound)Registry.SOUNDS.get(key);
                Location playerLoc = player.getLocation();
                if (playerLoc != null && sound != null) {
                    float volume = (float)this.plugin.getConfig().getDouble("milestone-sound-volume", 1.0);
                    float pitch = (float)this.plugin.getConfig().getDouble("milestone-sound-pitch", 1.0);
                    player.playSound(playerLoc, sound, volume, pitch);
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
    }

    public void clearProgress(Player player, String questId) {
        BukkitTask removalTask;
        String key = questId + "-" + String.valueOf(player.getUniqueId());
        this.progressCounts.remove(key);
        UUID playerId = player.getUniqueId();
        BossBar bar = this.activeBossbars.remove(playerId);
        if (bar != null) {
            player.hideBossBar(bar);
        }
        if ((removalTask = this.bossbarRemovalTasks.remove(playerId)) != null) {
            removalTask.cancel();
        }
    }

    public void clearPlayer(Player player) {
        BukkitTask removalTask;
        UUID playerId = player.getUniqueId();
        BossBar bar = this.activeBossbars.remove(playerId);
        if (bar != null) {
            player.hideBossBar(bar);
        }
        if ((removalTask = this.bossbarRemovalTasks.remove(playerId)) != null) {
            removalTask.cancel();
        }
        this.progressCounts.entrySet().removeIf(entry -> ((String)entry.getKey()).endsWith("-" + String.valueOf(playerId)));
    }

    public void reload() {
        for (Map.Entry<UUID, BossBar> entry : this.activeBossbars.entrySet()) {
            Player player = Bukkit.getPlayer((UUID)entry.getKey());
            if (player == null) continue;
            player.hideBossBar(entry.getValue());
        }
        this.activeBossbars.clear();
        for (BukkitTask task : this.bossbarRemovalTasks.values()) {
            task.cancel();
        }
        this.bossbarRemovalTasks.clear();
        this.progressCounts.clear();
        this.loadConfiguration();
    }

    private BossBar.Color parseBossBarColor(String value) {
        if (value == null) {
            return BossBar.Color.GREEN;
        }
        return switch (value) {
            case "PINK" -> BossBar.Color.PINK;
            case "BLUE" -> BossBar.Color.BLUE;
            case "RED" -> BossBar.Color.RED;
            case "GREEN" -> BossBar.Color.GREEN;
            case "YELLOW" -> BossBar.Color.YELLOW;
            case "PURPLE" -> BossBar.Color.PURPLE;
            case "WHITE" -> BossBar.Color.WHITE;
            default -> {
                this.plugin.getLogger().warning(() -> "Invalid bossbar color: " + value + ", defaulting to GREEN");
                yield BossBar.Color.GREEN;
            }
        };
    }

    private BossBar.Overlay parseBossBarOverlay(String value) {
        if (value == null) {
            return BossBar.Overlay.NOTCHED_10;
        }
        return switch (value) {
            case "SOLID", "PROGRESS" -> BossBar.Overlay.PROGRESS;
            case "SEGMENTED_6", "NOTCHED_6" -> BossBar.Overlay.NOTCHED_6;
            case "SEGMENTED_10", "NOTCHED_10" -> BossBar.Overlay.NOTCHED_10;
            case "SEGMENTED_12", "NOTCHED_12" -> BossBar.Overlay.NOTCHED_12;
            case "SEGMENTED_20", "NOTCHED_20" -> BossBar.Overlay.NOTCHED_20;
            default -> {
                this.plugin.getLogger().warning(() -> "Invalid bossbar style: " + value + ", defaulting to SEGMENTED_10");
                yield BossBar.Overlay.NOTCHED_10;
            }
        };
    }

    public DisplayMode getDisplayMode() {
        return this.displayMode;
    }

    public static enum DisplayMode {
        CHAT,
        ACTIONBAR,
        BOSSBAR,
        NONE;

    }
}

