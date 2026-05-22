/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package com.soaps.quest.managers;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.managers.StatisticManager;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import com.soaps.quest.utils.QuestPaper;
import com.soaps.quest.utils.YamlUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class DataManager {
    private final SoapsQuest plugin;
    private File dataFile;
    private FileConfiguration dataConfig;
    private BukkitRunnable autosaveTask;
    private final Map<UUID, Map<UUID, QuestProgress>> playerActiveQuests;
    private final Queue<SaveOperation> pendingSaveOperations;
    private BukkitRunnable batchSaveTask;
    private final Object saveLock = new Object();
    private final AtomicBoolean saveInProgress;
    private boolean batchSaveEnabled;
    private int batchSaveIntervalTicks;

    public DataManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.playerActiveQuests = new ConcurrentHashMap<UUID, Map<UUID, QuestProgress>>();
        this.pendingSaveOperations = new ConcurrentLinkedQueue<SaveOperation>();
        this.saveInProgress = new AtomicBoolean(false);
        this.loadPerformanceConfig();
        this.loadData();
        if (this.batchSaveEnabled) {
            this.startBatchSaveProcessor();
        }
    }

    private void loadPerformanceConfig() {
        this.batchSaveEnabled = this.plugin.getConfig().getBoolean("performance.batch-save-enabled", true);
        this.batchSaveIntervalTicks = this.plugin.getConfig().getInt("performance.batch-save-interval-ticks", 100);
        this.plugin.debugLog(Level.INFO, "DataManager: Batch save={0}, Interval={1} ticks", this.batchSaveEnabled, this.batchSaveIntervalTicks);
    }

    private void startBatchSaveProcessor() {
        this.stopBatchSaveProcessor();
        this.batchSaveTask = new BukkitRunnable(){

            public void run() {
                DataManager.this.processBatchSaves();
            }
        };
        this.batchSaveTask.runTaskTimerAsynchronously((Plugin)this.plugin, (long)this.batchSaveIntervalTicks, (long)this.batchSaveIntervalTicks);
        this.plugin.debugLog("DataManager: Batch save processor started");
    }

    private void stopBatchSaveProcessor() {
        if (this.batchSaveTask != null) {
            this.batchSaveTask.cancel();
            this.batchSaveTask = null;
        }
    }

    private void processBatchSaves() {
        if (this.pendingSaveOperations.isEmpty() || !this.saveInProgress.compareAndSet(false, true)) {
            return;
        }
        try {
            SaveOperation operation;
            int processedCount = 0;
            while ((operation = this.pendingSaveOperations.poll()) != null) {
                operation.execute(this);
                ++processedCount;
            }
            if (processedCount > 0) {
                this.saveDataFile();
                this.plugin.debugLog(Level.FINE, "[DataManager] Batch saved {0} operations", processedCount);
            }
        }
        finally {
            this.saveInProgress.set(false);
        }
    }

    private void queueSaveOperation(SaveOperation operation) {
        if (this.batchSaveEnabled) {
            this.pendingSaveOperations.offer(operation);
        } else {
            operation.execute(this);
            this.saveDataFile();
        }
    }

    public void flushPendingSaves() {
        SaveOperation operation;
        if (this.pendingSaveOperations.isEmpty()) {
            return;
        }
        this.plugin.getLogger().log(Level.INFO, "Flushing {0} pending save operation(s)...", this.pendingSaveOperations.size());
        int processedCount = 0;
        while ((operation = this.pendingSaveOperations.poll()) != null) {
            operation.execute(this);
            ++processedCount;
        }
        if (processedCount > 0) {
            this.saveDataFile();
            this.plugin.getLogger().log(Level.INFO, "Flushed {0} save operation(s) to disk", processedCount);
        }
    }

    public StatisticManager.PlayerStatistics loadPlayerStatistics(UUID playerUuid) {
        String path = "players." + playerUuid.toString() + ".statistics";
        if (!this.dataConfig.contains(path)) {
            return new StatisticManager.PlayerStatistics();
        }
        int totalCompletions = this.dataConfig.getInt(path + ".total", 0);
        int rewardsClaimed = this.dataConfig.getInt(path + ".rewardsClaimed", 0);
        HashMap<String, Integer> tierCompletions = new HashMap<String, Integer>();
        ConfigurationSection tierSection = this.dataConfig.getConfigurationSection(path + ".tiers");
        if (tierSection != null) {
            for (String tier : tierSection.getKeys(false)) {
                tierCompletions.put(tier, this.dataConfig.getInt(path + ".tiers." + tier, 0));
            }
        }
        HashMap<String, Integer> difficultyCompletions = new HashMap<String, Integer>();
        ConfigurationSection diffSection = this.dataConfig.getConfigurationSection(path + ".difficulties");
        if (diffSection != null) {
            for (String difficulty : diffSection.getKeys(false)) {
                difficultyCompletions.put(difficulty, this.dataConfig.getInt(path + ".difficulties." + difficulty, 0));
            }
        }
        return new StatisticManager.PlayerStatistics(totalCompletions, tierCompletions, difficultyCompletions, rewardsClaimed);
    }

    public void savePlayerStatistics(UUID playerUuid, StatisticManager.PlayerStatistics statistics) {
        this.queueSaveOperation(dataManager -> this.savePlayerStatisticsToConfig(playerUuid, statistics));
    }

    public void savePlayerStatisticsDirect(UUID playerUuid, StatisticManager.PlayerStatistics statistics) {
        this.savePlayerStatisticsToConfig(playerUuid, statistics);
    }

    private void savePlayerStatisticsToConfig(UUID playerUuid, StatisticManager.PlayerStatistics statistics) {
        String path = "players." + playerUuid.toString() + ".statistics";
        this.dataConfig.set(path + ".total", (Object)statistics.totalCompletions);
        this.dataConfig.set(path + ".rewardsClaimed", (Object)statistics.rewardsClaimed);
        this.dataConfig.set(path + ".tiers", null);
        for (Map.Entry<String, Integer> entry : statistics.tierCompletions.entrySet()) {
            this.dataConfig.set(path + ".tiers." + entry.getKey(), (Object)entry.getValue());
        }
        this.dataConfig.set(path + ".difficulties", null);
        for (Map.Entry<String, Integer> entry : statistics.difficultyCompletions.entrySet()) {
            this.dataConfig.set(path + ".difficulties." + entry.getKey(), (Object)entry.getValue());
        }
    }

    public void loadAllPlayerStatistics(Map<UUID, StatisticManager.PlayerStatistics> cache) {
        ConfigurationSection playersSection = this.dataConfig.getConfigurationSection("players");
        if (playersSection == null) {
            return;
        }
        for (String playerUuidStr : playersSection.getKeys(false)) {
            try {
                UUID playerUuid = UUID.fromString(playerUuidStr);
                if (cache.containsKey(playerUuid)) continue;
                StatisticManager.PlayerStatistics stats = this.loadPlayerStatistics(playerUuid);
                if (stats.totalCompletions <= 0) continue;
                cache.put(playerUuid, stats);
            }
            catch (IllegalArgumentException e) {
                this.plugin.getLogger().log(Level.WARNING, "Invalid player UUID in statistics: {0}", playerUuidStr);
            }
        }
    }

    public void loadData() {
        File dataFolder = new File(this.plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.dataFile = new File(dataFolder, "playerdata.yml");
        if (!this.dataFile.exists()) {
            try {
                this.dataFile.createNewFile();
            }
            catch (IOException e) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not create playerdata.yml!", e);
            }
        }
        this.dataConfig = YamlConfiguration.loadConfiguration((File)this.dataFile);
        this.loadQuestInstances();
    }

    private void loadQuestInstances() {
        this.playerActiveQuests.clear();
        ConfigurationSection playersSection = this.dataConfig.getConfigurationSection("players");
        if (playersSection == null) {
            return;
        }
        for (String playerUuidStr : playersSection.getKeys(false)) {
            try {
                UUID playerUuid = UUID.fromString(playerUuidStr);
                ConcurrentHashMap<UUID, QuestProgress> activeQuests = new ConcurrentHashMap<UUID, QuestProgress>();
                ConfigurationSection questsSection = this.dataConfig.getConfigurationSection("players." + playerUuidStr + ".quests");
                if (questsSection == null) continue;
                for (String questInstanceUuidStr : questsSection.getKeys(false)) {
                    try {
                        UUID questInstanceUuid = UUID.fromString(questInstanceUuidStr);
                        String path = "players." + playerUuidStr + ".quests." + questInstanceUuidStr;
                        String questId = this.dataConfig.getString(path + ".q");
                        if (questId == null) {
                            questId = this.dataConfig.getString(path + ".questId");
                        }
                        int progress = this.dataConfig.getInt(path + ".p", this.dataConfig.getInt(path + ".progress", 0));
                        int required = this.dataConfig.getInt(path + ".r", this.dataConfig.getInt(path + ".required", 1));
                        boolean redeemed = this.dataConfig.getBoolean(path + ".rd", this.dataConfig.getBoolean(path + ".redeemed", false));
                        boolean claimable = this.dataConfig.getBoolean(path + ".c", this.dataConfig.getBoolean(path + ".claimable", false));
                        UUID ownerUuid = null;
                        String ownerUuidStr = this.dataConfig.getString(path + ".o");
                        if (ownerUuidStr == null) {
                            ownerUuidStr = this.dataConfig.getString(path + ".owner");
                        }
                        if (ownerUuidStr != null) {
                            try {
                                ownerUuid = UUID.fromString(ownerUuidStr);
                            }
                            catch (IllegalArgumentException e) {
                                this.plugin.getLogger().log(Level.WARNING, "Invalid owner UUID in quest data: {0}", ownerUuidStr);
                            }
                        }
                        if (redeemed || questId == null) continue;
                        HashMap<String, Integer> objectiveProgress = new HashMap<String, Integer>();
                        ConfigurationSection objectivesSection = this.dataConfig.getConfigurationSection(path + ".obj");
                        if (objectivesSection == null) {
                            objectivesSection = this.dataConfig.getConfigurationSection(path + ".objectives");
                        }
                        if (objectivesSection != null) {
                            for (String objectiveId : objectivesSection.getKeys(false)) {
                                int objProgress = objectivesSection.getInt(objectiveId, 0);
                                objectiveProgress.put(objectiveId, objProgress);
                            }
                        }
                        QuestProgress questProgress = QuestProgress.builder(questInstanceUuid, questId, required).currentProgress(progress).redeemed(redeemed).claimable(claimable).ownerUUID(ownerUuid).objectiveProgress(objectiveProgress).build();
                        int currentObjectiveIndex = this.dataConfig.getInt(path + ".i", this.dataConfig.getInt(path + ".currentObjectiveIndex", 0));
                        questProgress.setCurrentObjectiveIndex(currentObjectiveIndex);
                        activeQuests.put(questInstanceUuid, questProgress);
                    }
                    catch (IllegalArgumentException e) {
                        this.plugin.getLogger().log(Level.WARNING, "Invalid quest instance UUID in playerdata.yml: {0}", questInstanceUuidStr);
                    }
                }
                if (activeQuests.isEmpty()) continue;
                this.playerActiveQuests.put(playerUuid, activeQuests);
            }
            catch (IllegalArgumentException e) {
                this.plugin.getLogger().log(Level.WARNING, "Invalid player UUID in playerdata.yml: {0}", playerUuidStr);
            }
        }
        this.plugin.debugLog(Level.INFO, "Loaded {0} players with active quest instances", this.playerActiveQuests.size());
        if (this.plugin.getConfig().getBoolean("data-cleanup.cleanup-on-startup", true)) {
            this.cleanupStaleDataAsync();
        }
    }

    public void cleanupStaleData() {
        if (!this.plugin.getConfig().getBoolean("data-cleanup.enabled", true)) {
            return;
        }
        int removedInstances = 0;
        int removedPlayers = 0;
        long now = System.currentTimeMillis();
        int inactiveDays = this.plugin.getConfig().getInt("data-cleanup.remove-inactive-after-days", 90);
        long inactiveThreshold = now - (long)inactiveDays * 24L * 60L * 60L * 1000L;
        Set<String> validQuestIds = this.plugin.getQuestManager().getQuestIds();
        Iterator<Map.Entry<UUID, Map<UUID, QuestProgress>>> playerIter = this.playerActiveQuests.entrySet().iterator();
        while (playerIter.hasNext()) {
            Map.Entry<UUID, Map<UUID, QuestProgress>> playerEntry = playerIter.next();
            UUID playerUuid = playerEntry.getKey();
            Map<UUID, QuestProgress> quests = playerEntry.getValue();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer((UUID)playerUuid);
            if (offlinePlayer.isOnline()) continue;
            long lastPlayed = offlinePlayer.getLastSeen();
            if (lastPlayed > 0L && lastPlayed < inactiveThreshold) {
                playerIter.remove();
                ++removedPlayers;
                this.plugin.debugLog("Removed inactive player data: " + offlinePlayer.getName());
                continue;
            }
            Iterator<Map.Entry<UUID, QuestProgress>> questIter = quests.entrySet().iterator();
            while (questIter.hasNext()) {
                Map.Entry<UUID, QuestProgress> questEntry = questIter.next();
                QuestProgress progress = questEntry.getValue();
                if (validQuestIds.contains(progress.getQuestId())) continue;
                questIter.remove();
                ++removedInstances;
                this.plugin.debugLog("Removed orphaned quest instance: " + progress.getQuestId());
            }
            if (!quests.isEmpty()) continue;
            playerIter.remove();
            ++removedPlayers;
        }
        if (removedInstances > 0 || removedPlayers > 0) {
            this.plugin.getLogger().info(String.format("Data cleanup removed %d orphaned quest instances and %d inactive players", removedInstances, removedPlayers));
        }
    }

    public void cleanupStaleDataAsync() {
        if (!this.plugin.getConfig().getBoolean("data-cleanup.enabled", true)) {
            return;
        }
        new BukkitRunnable(){

            public void run() {
                try {
                    File backupFile = new File(DataManager.this.dataFile.getParentFile(), "playerdata-backup.yml");
                    Files.copy(DataManager.this.dataFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    DataManager.this.plugin.debugLog("Created playerdata backup before cleanup");
                }
                catch (IOException e) {
                    DataManager.this.plugin.getLogger().log(Level.WARNING, "Could not create playerdata backup: {0}", e.getMessage());
                }
                DataManager.this.cleanupStaleData();
            }
        }.runTaskAsynchronously((Plugin)this.plugin);
    }

    public void saveData() {
        ConfigurationSection playersSection;
        if (this.plugin.getConfig().getBoolean("data-cleanup.cleanup-on-save", true)) {
            this.cleanupStaleData();
        }
        if ((playersSection = this.dataConfig.getConfigurationSection("players")) != null) {
            for (String string : playersSection.getKeys(false)) {
                this.dataConfig.set("players." + string + ".quests", null);
            }
        }
        for (Map.Entry<UUID, Map<UUID, QuestProgress>> entry : this.playerActiveQuests.entrySet()) {
            UUID playerUuid = entry.getKey();
            Map<UUID, QuestProgress> activeQuests = entry.getValue();
            for (Map.Entry<UUID, QuestProgress> questEntry : activeQuests.entrySet()) {
                UUID questInstanceUuid = questEntry.getKey();
                QuestProgress progress = questEntry.getValue();
                if (progress.isRedeemed()) continue;
                String path = "players." + playerUuid.toString() + ".quests." + questInstanceUuid.toString();
                this.dataConfig.set(path + ".q", (Object)progress.getQuestId());
                this.dataConfig.set(path + ".p", (Object)progress.getCurrentProgress());
                this.dataConfig.set(path + ".r", (Object)progress.getRequiredAmount());
                if (progress.isRedeemed()) {
                    this.dataConfig.set(path + ".rd", (Object)true);
                }
                if (progress.isClaimable()) {
                    this.dataConfig.set(path + ".c", (Object)true);
                }
                if (progress.getOwnerUUID() != null) {
                    this.dataConfig.set(path + ".o", (Object)progress.getOwnerUUID().toString());
                }
                if (progress.getCurrentObjectiveIndex() > 0) {
                    this.dataConfig.set(path + ".i", (Object)progress.getCurrentObjectiveIndex());
                }
                if (progress.getAllObjectiveProgress().isEmpty()) continue;
                for (Map.Entry<String, Integer> entry2 : progress.getAllObjectiveProgress().entrySet()) {
                    this.dataConfig.set(path + ".obj." + entry2.getKey(), (Object)entry2.getValue());
                }
            }
        }
        this.saveDataFile();
    }

    public void saveDataAsync() {
        new BukkitRunnable(){

            public void run() {
                DataManager.this.saveData();
            }
        }.runTaskAsynchronously((Plugin)this.plugin);
    }

    public void saveDataSync() {
        this.saveData();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void saveDataFile() {
        Object object = this.saveLock;
        synchronized (object) {
            try {
                this.plugin.debugLog(Level.FINE, "Writing data to file: {0}", this.dataFile.getAbsolutePath());
                YamlUtil.atomicSave((FileConfiguration)((YamlConfiguration)this.dataConfig), this.dataFile);
                this.plugin.debugLog(Level.FINE, "Data file saved successfully!", new Object[0]);
            }
            catch (IOException e) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not save playerdata.yml!", e);
            }
        }
    }

    public void flushToDisk() {
        this.saveDataFile();
    }

    public void startAutosave() {
        this.stopAutosave();
        int interval = this.plugin.getConfig().getInt("autosave-interval", 5);
        long ticks = interval * 60 * 20;
        this.autosaveTask = new BukkitRunnable(){

            public void run() {
                DataManager.this.plugin.debugLog(Level.FINE, "Auto-saving player data...", new Object[0]);
                DataManager.this.saveDataAsync();
            }
        };
        this.autosaveTask.runTaskTimerAsynchronously((Plugin)this.plugin, ticks, ticks);
        this.plugin.debugLog(Level.INFO, "Autosave enabled (interval: {0} minutes)", interval);
    }

    public void stopAutosave() {
        if (this.autosaveTask != null) {
            this.autosaveTask.cancel();
            this.autosaveTask = null;
        }
        this.stopBatchSaveProcessor();
        this.processBatchSaves();
    }

    public void reload() {
        this.loadData();
    }

    public FileConfiguration getConfig() {
        return this.dataConfig;
    }

    public void registerQuestInstance(Player player, UUID questInstanceUuid, String questId, int requiredAmount) {
        UUID playerUuid = player.getUniqueId();
        this.playerActiveQuests.putIfAbsent(playerUuid, new ConcurrentHashMap());
        if (this.playerActiveQuests.get(playerUuid).containsKey(questInstanceUuid)) {
            this.plugin.debugLog(Level.INFO, "[Data] Quest instance already registered for player ''{0}'': {1}", player.getName(), questInstanceUuid);
            return;
        }
        QuestProgress progress = QuestProgress.builder(questInstanceUuid, questId, requiredAmount).build();
        this.playerActiveQuests.get(playerUuid).put(questInstanceUuid, progress);
        Quest quest = this.plugin.getQuestManager().getQuest(questId);
        if (quest != null) {
            quest.resetObjectiveRuntimeState(playerUuid);
        }
        this.plugin.debugLog(Level.INFO, "[Data] Registered new quest instance for player ''{0}'': {1} (Quest: {2})", player.getName(), questInstanceUuid, questId);
        this.plugin.debugLog(Level.INFO, "[SoapsQuest][Check] Created quest instance for {0} {1}", questId, questInstanceUuid);
        this.saveDataSync();
    }

    public QuestProgress getQuestInstance(Player player, UUID questInstanceUuid) {
        Map<UUID, QuestProgress> quests = this.playerActiveQuests.get(player.getUniqueId());
        if (quests == null) {
            return null;
        }
        return quests.get(questInstanceUuid);
    }

    public QuestProgress getQuestInstance(UUID playerUuid, UUID questInstanceUuid) {
        Map<UUID, QuestProgress> quests = this.playerActiveQuests.get(playerUuid);
        if (quests == null) {
            return null;
        }
        return quests.get(questInstanceUuid);
    }

    public Map<UUID, QuestProgress> getActiveQuests(Player player) {
        return this.playerActiveQuests.getOrDefault(player.getUniqueId(), new HashMap());
    }

    public UUID findQuestInstanceOwner(UUID questInstanceUuid) {
        for (Map.Entry<UUID, Map<UUID, QuestProgress>> playerEntry : this.playerActiveQuests.entrySet()) {
            QuestProgress progress = playerEntry.getValue().get(questInstanceUuid);
            if (progress == null || !progress.isBound()) continue;
            return progress.getOwnerUUID();
        }
        return null;
    }

    public QuestProgress getLockedQuestInstance(UUID questInstanceUuid) {
        for (Map.Entry<UUID, Map<UUID, QuestProgress>> playerEntry : this.playerActiveQuests.entrySet()) {
            QuestProgress progress = playerEntry.getValue().get(questInstanceUuid);
            if (progress == null) continue;
            return progress;
        }
        return null;
    }

    public void removeQuestInstance(Player player, UUID questInstanceUuid) {
        Map<UUID, QuestProgress> quests = this.playerActiveQuests.get(player.getUniqueId());
        if (quests != null) {
            quests.remove(questInstanceUuid);
            if (quests.isEmpty()) {
                this.playerActiveQuests.remove(player.getUniqueId());
            }
        }
    }

    public int removeAllQuestInstances(String questId) {
        int removedCount = 0;
        for (Map<UUID, QuestProgress> playerQuests : this.playerActiveQuests.values()) {
            Iterator<Map.Entry<UUID, QuestProgress>> iterator = playerQuests.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, QuestProgress> entry2 = iterator.next();
                QuestProgress progress = entry2.getValue();
                if (!progress.getQuestId().equals(questId)) continue;
                iterator.remove();
                ++removedCount;
                this.plugin.debugLog(Level.INFO, "[Data] Removed quest instance {0} for deleted quest ''{1}''", entry2.getKey(), questId);
            }
        }
        this.playerActiveQuests.entrySet().removeIf(entry -> ((Map)entry.getValue()).isEmpty());
        if (removedCount > 0) {
            this.plugin.getLogger().log(Level.INFO, "[Data] Cleaned up {0} quest instance(s) for deleted quest ''{1}''", new Object[]{removedCount, questId});
            this.saveDataAsync();
        }
        return removedCount;
    }

    public boolean incrementQuestProgress(Player player, UUID questInstanceUuid) {
        QuestProgress progress = this.getQuestInstance(player, questInstanceUuid);
        if (progress != null && !progress.isRedeemed()) {
            progress.incrementProgress();
            return true;
        }
        return false;
    }

    public void markQuestRedeemed(Player player, UUID questInstanceUuid) {
        QuestProgress progress = this.getQuestInstance(player, questInstanceUuid);
        if (progress != null) {
            progress.setRedeemed();
            this.markQuestCompleted(player, progress.getQuestId());
            this.plugin.debugLog(Level.INFO, "[Data] Marking quest instance as redeemed and removing: {0} (Quest: {1})", questInstanceUuid, progress.getQuestId());
            this.removeQuestInstance(player, questInstanceUuid);
        } else {
            this.plugin.getLogger().log(Level.WARNING, "[Data] Attempted to mark non-existent quest instance as redeemed: {0}", questInstanceUuid);
        }
    }

    public QuestProgress transferOrAdoptQuest(Player newPlayer, UUID questInstanceUuid, String questId, int required) {
        Map<UUID, QuestProgress> originalPlayerQuests;
        int objectiveIndex;
        QuestProgress existingProgress = this.getQuestInstance(newPlayer, questInstanceUuid);
        if (existingProgress != null) {
            return existingProgress;
        }
        QuestProgress originalProgress = null;
        UUID originalOwnerUuid = null;
        for (Map.Entry<UUID, Map<UUID, QuestProgress>> entry : this.playerActiveQuests.entrySet()) {
            UUID playerUuid = entry.getKey();
            Map<UUID, QuestProgress> playerQuests = entry.getValue();
            QuestProgress progress = playerQuests.get(questInstanceUuid);
            if (progress == null) continue;
            originalProgress = progress;
            originalOwnerUuid = playerUuid;
            break;
        }
        int currentProgress = originalProgress != null ? originalProgress.getCurrentProgress() : 0;
        boolean redeemed = originalProgress != null && originalProgress.isRedeemed();
        boolean claimable = originalProgress != null && originalProgress.isClaimable();
        Map<String, Integer> objProgress = originalProgress != null ? originalProgress.getAllObjectiveProgress() : null;
        int n = objectiveIndex = originalProgress != null ? originalProgress.getCurrentObjectiveIndex() : 0;
        if (originalOwnerUuid != null && (originalPlayerQuests = this.playerActiveQuests.get(originalOwnerUuid)) != null) {
            originalPlayerQuests.remove(questInstanceUuid);
            if (originalPlayerQuests.isEmpty()) {
                this.playerActiveQuests.remove(originalOwnerUuid);
            }
        }
        QuestProgress newProgress = QuestProgress.builder(questInstanceUuid, questId, required).currentProgress(currentProgress).redeemed(redeemed).claimable(claimable).objectiveProgress(objProgress).currentObjectiveIndex(objectiveIndex).build();
        Map<UUID, QuestProgress> playerQuests = this.playerActiveQuests.get(newPlayer.getUniqueId());
        if (playerQuests == null) {
            playerQuests = new ConcurrentHashMap<UUID, QuestProgress>();
            this.playerActiveQuests.put(newPlayer.getUniqueId(), playerQuests);
        }
        playerQuests.put(questInstanceUuid, newProgress);
        ItemStack[] contents = newPlayer.getInventory().getContents();
        if (contents != null) {
            for (int i = 0; i < contents.length; ++i) {
                UUID paperInstanceUuid;
                ItemStack item = contents[i];
                if (item == null || (paperInstanceUuid = QuestPaper.getQuestInstanceId(item)) == null || !paperInstanceUuid.equals(questInstanceUuid)) continue;
                ItemStack updatedPaper = QuestPaper.transferQuestPaperOwnership(item, newPlayer, this.plugin.getPlayerUuidKey());
                newPlayer.getInventory().setItem(i, updatedPaper);
                break;
            }
        }
        this.plugin.getQuestManager().addQuestToQueue(newPlayer, questId, questInstanceUuid);
        this.saveDataAsync();
        return newProgress;
    }

    public ConfigurationSection getRecurringQuestSection() {
        return this.dataConfig.getConfigurationSection("recurring-quests");
    }

    public void setRecurringQuestData(String path, Object value) {
        this.dataConfig.set("recurring-quests." + path, value);
        this.saveDataAsync();
    }

    public int getCompletedQuestCount(Player player) {
        StatisticManager.PlayerStatistics stats = this.loadPlayerStatistics(player.getUniqueId());
        return stats.totalCompletions;
    }

    public boolean hasCompletedQuest(Player player, String questId) {
        String path = "players." + player.getUniqueId().toString() + ".completed-quests." + questId;
        return this.dataConfig.getBoolean(path, false);
    }

    public void markQuestCompleted(Player player, String questId) {
        String path = "players." + player.getUniqueId().toString() + ".completed-quests." + questId;
        this.dataConfig.set(path, (Object)true);
        this.saveDataAsync();
    }

    @FunctionalInterface
    private static interface SaveOperation {
        public void execute(DataManager var1);
    }
}

