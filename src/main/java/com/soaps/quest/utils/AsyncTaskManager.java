/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package com.soaps.quest.utils;

import com.soaps.quest.SoapsQuest;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class AsyncTaskManager {
    private final SoapsQuest plugin;
    private final BlockingQueue<QuestProgressUpdate> progressUpdateQueue;
    private final BlockingQueue<StatisticUpdate> statisticUpdateQueue;
    private BukkitTask progressProcessorTask;
    private BukkitTask statisticProcessorTask;
    private int batchSize;
    private int processingIntervalTicks;
    private boolean asyncEnabled;
    private final AtomicInteger totalProgressUpdates;
    private final AtomicInteger totalStatisticUpdates;
    private final AtomicInteger batchesProcessed;

    public AsyncTaskManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.progressUpdateQueue = new LinkedBlockingQueue<QuestProgressUpdate>();
        this.statisticUpdateQueue = new LinkedBlockingQueue<StatisticUpdate>();
        this.totalProgressUpdates = new AtomicInteger(0);
        this.totalStatisticUpdates = new AtomicInteger(0);
        this.batchesProcessed = new AtomicInteger(0);
        this.loadConfiguration();
        if (this.asyncEnabled) {
            this.startProcessors();
        }
    }

    private void loadConfiguration() {
        this.asyncEnabled = this.plugin.getConfig().getBoolean("performance.async-processing-enabled", true);
        this.batchSize = this.plugin.getConfig().getInt("performance.batch-size", 50);
        this.processingIntervalTicks = this.plugin.getConfig().getInt("performance.processing-interval-ticks", 20);
        this.plugin.debugLog(Level.INFO, "AsyncTaskManager: Async={0}, Batch size={1}, Interval={2} ticks", this.asyncEnabled, this.batchSize, this.processingIntervalTicks);
    }

    private void startProcessors() {
        this.stopProcessors();
        this.progressProcessorTask = new BukkitRunnable(){

            public void run() {
                AsyncTaskManager.this.processProgressUpdates();
            }
        }.runTaskTimerAsynchronously((Plugin)this.plugin, (long)this.processingIntervalTicks, (long)this.processingIntervalTicks);
        this.statisticProcessorTask = new BukkitRunnable(){

            public void run() {
                AsyncTaskManager.this.processStatisticUpdates();
            }
        }.runTaskTimerAsynchronously((Plugin)this.plugin, (long)this.processingIntervalTicks, (long)this.processingIntervalTicks);
        this.plugin.debugLog("AsyncTaskManager: Processors started");
    }

    public void stopProcessors() {
        if (this.progressProcessorTask != null) {
            this.progressProcessorTask.cancel();
            this.progressProcessorTask = null;
        }
        if (this.statisticProcessorTask != null) {
            this.statisticProcessorTask.cancel();
            this.statisticProcessorTask = null;
        }
    }

    public void queueProgressUpdate(QuestProgressUpdate update) {
        if (!this.asyncEnabled) {
            update.execute(this.plugin);
            return;
        }
        this.progressUpdateQueue.offer(update);
        this.totalProgressUpdates.incrementAndGet();
    }

    public void queueStatisticUpdate(StatisticUpdate update) {
        if (!this.asyncEnabled) {
            update.execute(this.plugin);
            return;
        }
        this.statisticUpdateQueue.offer(update);
        this.totalStatisticUpdates.incrementAndGet();
    }

    private void processProgressUpdates() {
        if (this.progressUpdateQueue.isEmpty()) {
            return;
        }
        int processed = 0;
        while (processed < this.batchSize && !this.progressUpdateQueue.isEmpty()) {
            QuestProgressUpdate update = (QuestProgressUpdate)this.progressUpdateQueue.poll();
            if (update == null) continue;
            try {
                update.execute(this.plugin);
                ++processed;
            }
            catch (Exception e) {
                this.plugin.getLogger().log(Level.WARNING, "[AsyncTaskManager] Error processing progress update", e);
            }
        }
        if (processed > 0) {
            this.batchesProcessed.incrementAndGet();
            this.plugin.debugLog(Level.FINE, "[AsyncTaskManager] Processed batch of {0} progress updates", processed);
        }
    }

    private void processStatisticUpdates() {
        if (this.statisticUpdateQueue.isEmpty()) {
            return;
        }
        int processed = 0;
        while (processed < this.batchSize && !this.statisticUpdateQueue.isEmpty()) {
            StatisticUpdate update = (StatisticUpdate)this.statisticUpdateQueue.poll();
            if (update == null) continue;
            try {
                update.execute(this.plugin);
                ++processed;
            }
            catch (Exception e) {
                this.plugin.getLogger().log(Level.WARNING, "[AsyncTaskManager] Error processing statistic update", e);
            }
        }
        if (processed > 0) {
            this.plugin.debugLog(Level.FINE, "[AsyncTaskManager] Processed batch of {0} statistic updates", processed);
        }
    }

    public void flush() {
        this.plugin.debugLog("[AsyncTaskManager] Flushing pending updates...");
        while (!this.progressUpdateQueue.isEmpty()) {
            QuestProgressUpdate update = this.progressUpdateQueue.poll();
            if (update == null) continue;
            try {
                update.execute(this.plugin);
            }
            catch (Exception e) {
                this.plugin.getLogger().log(Level.WARNING, "[AsyncTaskManager] Error flushing progress update", e);
            }
        }
        while (!this.statisticUpdateQueue.isEmpty()) {
            StatisticUpdate update = this.statisticUpdateQueue.poll();
            if (update == null) continue;
            try {
                update.execute(this.plugin);
            }
            catch (Exception e) {
                this.plugin.getLogger().log(Level.WARNING, "[AsyncTaskManager] Error flushing statistic update", e);
            }
        }
        this.plugin.debugLog("[AsyncTaskManager] Flush complete");
    }

    public String getMetrics() {
        return String.format("AsyncTaskManager Metrics: Progress=%d, Statistics=%d, Batches=%d, Queued=%d/%d", this.totalProgressUpdates.get(), this.totalStatisticUpdates.get(), this.batchesProcessed.get(), this.progressUpdateQueue.size(), this.statisticUpdateQueue.size());
    }

    public void reload() {
        this.loadConfiguration();
        if (this.asyncEnabled) {
            this.startProcessors();
        } else {
            this.stopProcessors();
        }
    }

    public void shutdown() {
        this.stopProcessors();
        this.flush();
        this.progressUpdateQueue.clear();
        this.statisticUpdateQueue.clear();
    }

    public boolean isAsyncEnabled() {
        return this.asyncEnabled;
    }

    @FunctionalInterface
    public static interface QuestProgressUpdate {
        public void execute(SoapsQuest var1);
    }

    @FunctionalInterface
    public static interface StatisticUpdate {
        public void execute(SoapsQuest var1);
    }
}

