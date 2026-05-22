/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.BrewEvent
 */
package com.soaps.quest.listeners;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.listeners.QuestListenerHelper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;

public class BrewListener
implements Listener {
    private final SoapsQuest plugin;

    public BrewListener(SoapsQuest plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBrew(BrewEvent event) {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            Location playerLoc;
            if (player.getWorld() != event.getBlock().getWorld() || (playerLoc = player.getLocation()) == null || playerLoc.distance(event.getBlock().getLocation()) > 5.0) continue;
            QuestListenerHelper.scanAndTrackDirect(this.plugin, player, (Event)event);
        }
    }
}

