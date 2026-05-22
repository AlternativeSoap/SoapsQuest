package com.soaps.quest.listeners;

import com.soaps.quest.SoapsQuest;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {
    private final SoapsQuest plugin;

    public CommandListener(SoapsQuest plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        QuestListenerHelper.scanAndTrackDirect(this.plugin, event.getPlayer(), (Event) event);
    }
}
