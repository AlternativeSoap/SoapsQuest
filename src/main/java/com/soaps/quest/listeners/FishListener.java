package com.soaps.quest.listeners;

import com.soaps.quest.SoapsQuest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishListener implements Listener {
    private final SoapsQuest plugin;

    public FishListener(SoapsQuest plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        QuestListenerHelper.scanAndTrackProgress(this.plugin, event.getPlayer(), (Event) event);
    }
}
