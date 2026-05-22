package com.soaps.quest.listeners;

import com.soaps.quest.SoapsQuest;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaceholderCheckListener {
    private final SoapsQuest plugin;
    private BukkitRunnable task;

    public PlaceholderCheckListener(SoapsQuest plugin) {
        this.plugin = plugin;
        this.start();
    }

    public void start() {
        this.stop();
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : PlaceholderCheckListener.this.plugin.getServer().getOnlinePlayers()) {
                    QuestListenerHelper.scanAndTrackPlaceholder(PlaceholderCheckListener.this.plugin, player);
                }
            }
        };
        this.task.runTaskTimer((Plugin) this.plugin, 20L, 20L);
    }

    public void stop() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }
}
