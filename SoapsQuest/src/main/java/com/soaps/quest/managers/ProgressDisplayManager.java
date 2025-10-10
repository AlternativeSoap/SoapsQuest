package com.soaps.quest.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;

import net.kyori.adventure.text.Component;

/**
 * Manages quest progress notifications with configurable display modes.
 * Supports chat (with interval throttling), actionbar, bossbar, and none modes.
 */
public class ProgressDisplayManager {
    
    private final SoapsQuest plugin;
    private final MessageManager messageManager;
    
    // Configuration
    private DisplayMode displayMode;
    private int chatInterval;
    private BarColor bossbarColor;
    private BarStyle bossbarStyle;
    private int bossbarDuration;
    
    // Progress tracking for chat interval
    private final Map<String, Integer> progressCounts; // questId-playerUUID -> count
    
    // Active bossbars for players
    private final Map<UUID, BossBar> activeBossbars;
    
    /**
     * Display mode enumeration.
     */
    public enum DisplayMode {
        CHAT,
        ACTIONBAR,
        BOSSBAR,
        NONE
    }
    
    /**
     * Constructor for ProgressDisplayManager.
     * 
     * @param plugin Plugin instance
     */
    public ProgressDisplayManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
        this.progressCounts = new HashMap<>();
        this.activeBossbars = new HashMap<>();
        
        loadConfiguration();
    }
    
    /**
     * Load configuration from config.yml.
     * Private to prevent overridable method call in constructor warning.
     */
    private void loadConfiguration() {
        FileConfiguration config = plugin.getConfig();
        
        // Load display mode
        String modeStr = config.getString("progress-display.mode", "actionbar");
        final String finalModeStr = (modeStr != null) ? modeStr.toUpperCase() : "ACTIONBAR";
        try {
            this.displayMode = DisplayMode.valueOf(finalModeStr);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning(() -> "Invalid progress display mode: " + finalModeStr + ", defaulting to ACTIONBAR");
            this.displayMode = DisplayMode.ACTIONBAR;
        }
        
        // Load chat interval
        this.chatInterval = config.getInt("progress-display.interval", 5);
        if (this.chatInterval < 1) {
            this.chatInterval = 1;
        }
        
        // Load bossbar settings (only if mode is BOSSBAR)
        if (this.displayMode == DisplayMode.BOSSBAR) {
            String colorStr = config.getString("progress-display.bossbar.color", "GREEN");
            final String finalColorStr = (colorStr != null) ? colorStr.toUpperCase() : "GREEN";
            try {
                this.bossbarColor = BarColor.valueOf(finalColorStr);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning(() -> "Invalid bossbar color: " + finalColorStr + ", defaulting to GREEN");
                this.bossbarColor = BarColor.GREEN;
            }
            
            String styleStr = config.getString("progress-display.bossbar.style", "SEGMENTED_10");
            final String finalStyleStr = (styleStr != null) ? styleStr.toUpperCase() : "SEGMENTED_10";
            try {
                this.bossbarStyle = BarStyle.valueOf(finalStyleStr);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning(() -> "Invalid bossbar style: " + finalStyleStr + ", defaulting to SEGMENTED_10");
                this.bossbarStyle = BarStyle.SEGMENTED_10;
            }
            
            this.bossbarDuration = config.getInt("progress-display.bossbar.duration", 5);
            if (this.bossbarDuration < 1) {
                this.bossbarDuration = 1;
            }
        }
    }
    
    /**
     * Show progress notification to player based on configured mode.
     * 
     * @param player Player to show progress to
     * @param quest Quest being progressed
     * @param currentProgress Current progress value
     */
    public void showProgress(Player player, Quest quest, int currentProgress) {
        // Skip if mode is NONE
        if (displayMode == DisplayMode.NONE) {
            return;
        }
        
        int requiredAmount = quest.getRequiredAmount();
        
        // Don't show progress if quest is complete (handled by redemption)
        if (currentProgress >= requiredAmount) {
            return;
        }
        
        // Build placeholders
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("quest", quest.getDisplay());
        placeholders.put("objective", quest.getObjectiveDescription());
        placeholders.put("progress", String.valueOf(currentProgress));
        placeholders.put("amount", String.valueOf(requiredAmount));
        
        switch (displayMode) {
            case CHAT -> showChatProgress(player, quest, placeholders);
            case ACTIONBAR -> showActionbarProgress(player, placeholders);
            case BOSSBAR -> showBossbarProgress(player, placeholders, currentProgress, requiredAmount);
            case NONE -> {
            }
        }
        // Do nothing
            }
    
    /**
     * Show progress notification for multi-objective quests (NEW).
     * Displays progress for all objectives in the quest.
     * 
     * @param player Player to show progress to
     * @param quest Quest being progressed
     * @param questProgress Quest progress instance
     */
    public void showProgress(Player player, Quest quest, QuestProgress questProgress) {
        // Skip if mode is NONE
        if (displayMode == DisplayMode.NONE) {
            return;
        }
        
        // Check if this is a multi-objective quest
        if (!quest.hasObjectives()) {
            // Fallback to legacy method for single-type quests
            showProgress(player, quest, questProgress.getCurrentProgress());
            return;
        }
        
        // Multi-objective quest - build comprehensive progress string
        StringBuilder progressText = new StringBuilder();
        int completedCount = 0;
        int totalCount = quest.getObjectives().size();
        
        for (Objective objective : quest.getObjectives()) {
            int objProgress = questProgress.getObjectiveProgress(objective.getObjectiveId());
            int objRequired = objective.getRequiredAmount();
            boolean objComplete = objProgress >= objRequired;
            
            if (objComplete) {
                completedCount++;
            }
            
            // Build individual objective progress string
            if (progressText.length() > 0) {
                progressText.append(" | ");
            }
            
            String status = objComplete ? "✓" : "";
            progressText.append(status)
                       .append(objective.getType().toUpperCase())
                       .append(": ")
                       .append(objProgress)
                       .append("/")
                       .append(objRequired);
        }
        
        // Build placeholders for multi-objective display
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("quest", quest.getDisplay());
        placeholders.put("objective", progressText.toString());
        placeholders.put("progress", String.valueOf(completedCount));
        placeholders.put("amount", String.valueOf(totalCount));
        placeholders.put("objectives_complete", String.valueOf(completedCount));
        placeholders.put("objectives_total", String.valueOf(totalCount));
        
        // Check if all objectives are complete
        if (completedCount >= totalCount) {
            // Don't show progress, this will be handled by completion message
            return;
        }
        
        switch (displayMode) {
            case CHAT -> showChatProgress(player, quest, placeholders);
            case ACTIONBAR -> showActionbarProgress(player, placeholders);
            case BOSSBAR -> showBossbarProgress(player, placeholders, completedCount, totalCount);
            case NONE -> {
            }
        }
        // Do nothing
            }
    
    /**
     * Show progress in chat with interval throttling.
     * 
     * @param player Player
     * @param quest Quest
     * @param placeholders Message placeholders
     */
    private void showChatProgress(Player player, Quest quest, Map<String, String> placeholders) {
        String key = quest.getQuestId() + "-" + player.getUniqueId();
        int count = progressCounts.getOrDefault(key, 0) + 1;
        progressCounts.put(key, count);
        
        // Only show message at interval milestones
        if (count % chatInterval == 0) {
            Component message = messageManager.getMessage("quest-progress-updated", placeholders);
            if (message != null) {
                player.sendMessage(message);
            }
        }
    }
    
    /**
     * Show progress in actionbar.
     * 
     * @param player Player
     * @param placeholders Message placeholders
     */
    private void showActionbarProgress(Player player, Map<String, String> placeholders) {
        Component message = messageManager.getMessage("quest-progress-actionbar", placeholders);
        if (message != null) {
            player.sendActionBar(message);
        }
    }
    
    /**
     * Show progress as a temporary bossbar.
     * 
     * @param player Player
     * @param quest Quest
     * @param placeholders Message placeholders
     * @param currentProgress Current progress
     * @param requiredAmount Required amount
     */
    private void showBossbarProgress(Player player, Map<String, String> placeholders, 
                                     int currentProgress, int requiredAmount) {
        UUID playerId = player.getUniqueId();
        
        // Remove existing bossbar if present
        BossBar existingBar = activeBossbars.get(playerId);
        if (existingBar != null) {
            existingBar.removeAll();
            activeBossbars.remove(playerId);
        }
        
        // Get bossbar title from messages
        Component titleComponent = messageManager.getMessage("quest-progress-bossbar", placeholders);
        String title = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
            .serialize(titleComponent);
        
        // Create new bossbar
        BossBar bossBar = Bukkit.createBossBar(title, bossbarColor, bossbarStyle);
        
        // Calculate progress (0.0 to 1.0)
        double progress = Math.min(1.0, (double) currentProgress / requiredAmount);
        bossBar.setProgress(progress);
        
        // Add player to bossbar
        bossBar.addPlayer(player);
        activeBossbars.put(playerId, bossBar);
        
        // Schedule removal after duration
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BossBar bar = activeBossbars.remove(playerId);
            if (bar != null) {
                bar.removeAll();
            }
        }, bossbarDuration * 20L); // Convert seconds to ticks
    }
    
    /**
     * Show quest completion message (always shown regardless of mode).
     * 
     * @param player Player
     * @param quest Quest
     */
    public void showCompletion(Player player, Quest quest) {
        // Clear any active bossbar
        if (displayMode == DisplayMode.BOSSBAR) {
            UUID playerId = player.getUniqueId();
            BossBar bar = activeBossbars.remove(playerId);
            if (bar != null) {
                bar.removeAll();
            }
        }
        
        // Clear chat progress counter
        String key = quest.getQuestId() + "-" + player.getUniqueId();
        progressCounts.remove(key);
        
        // Send completion message
        Component message = messageManager.getMessage("quest-complete", java.util.Map.of("quest", quest.getDisplay()));
        if (message != null) {
            player.sendMessage(message);
        }
    }
    
    /**
     * Show milestone notification to player.
     * 
     * @param player Player
     * @param quest Quest
     * @param objective The objective that reached a milestone
     * @param milestone The milestone percentage reached
     */
    @SuppressWarnings("nullness")
    public void showMilestone(Player player, Quest quest, com.soaps.quest.objectives.Objective objective, int milestone) {
        java.util.Map<String, String> placeholders = new java.util.HashMap<>();
        placeholders.put("quest", quest.getDisplay());
        placeholders.put("objective", objective.getDescription());
        placeholders.put("milestone", String.valueOf(milestone));
        placeholders.put("progress_percent", String.valueOf(milestone));
        
        // Send milestone message
        Component message = messageManager.getMessage("quest-milestone", placeholders);
        if (message != null) {
            player.sendMessage(message);
        }
        
        // Play milestone sound if configured
        String soundName = plugin.getConfig().getString("milestone-sound", "ENTITY_PLAYER_LEVELUP");
        if (soundName != null && !soundName.equalsIgnoreCase("none")) {
            try {
                org.bukkit.Sound sound = org.bukkit.Sound.valueOf(soundName.toUpperCase());
                org.bukkit.Location playerLoc = player.getLocation();
                if (playerLoc != null && sound != null) {
                    float volume = (float) plugin.getConfig().getDouble("milestone-sound-volume", 1.0);
                    float pitch = (float) plugin.getConfig().getDouble("milestone-sound-pitch", 1.0);
                    player.playSound(playerLoc, sound, volume, pitch);
                }
            } catch (IllegalArgumentException e) {
                // Invalid sound name in config
            }
        }
    }
    
    /**
     * Clear progress tracking for a player's quest.
     * 
     * @param player Player
     * @param questId Quest ID
     */
    public void clearProgress(Player player, String questId) {
        String key = questId + "-" + player.getUniqueId();
        progressCounts.remove(key);
        
        // Remove any active bossbar
        UUID playerId = player.getUniqueId();
        BossBar bar = activeBossbars.remove(playerId);
        if (bar != null) {
            bar.removeAll();
        }
    }
    
    /**
     * Clear all bossbars for a player (used on disconnect).
     * 
     * @param player Player
     */
    public void clearPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        BossBar bar = activeBossbars.remove(playerId);
        if (bar != null) {
            bar.removeAll();
        }
        
        // Clear all progress counts for this player
        progressCounts.entrySet().removeIf(entry -> entry.getKey().endsWith("-" + playerId));
    }
    
    /**
     * Reload configuration.
     */
    public void reload() {
        // Clear all active bossbars
        for (BossBar bar : activeBossbars.values()) {
            bar.removeAll();
        }
        activeBossbars.clear();
        progressCounts.clear();
        
        loadConfiguration();
    }
    
    /**
     * Get current display mode.
     * 
     * @return Display mode
     */
    public DisplayMode getDisplayMode() {
        return displayMode;
    }
}
