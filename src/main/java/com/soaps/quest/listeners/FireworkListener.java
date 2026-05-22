/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntitySpawnEvent
 */
package com.soaps.quest.listeners;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.listeners.QuestListenerHelper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class FireworkListener
implements Listener {
    private final SoapsQuest plugin;

    public FireworkListener(SoapsQuest plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity().getType() != EntityType.FIREWORK_ROCKET) {
            return;
        }
        for (Player player : event.getEntity().getWorld().getNearbyPlayers(event.getLocation(), 5.0)) {
            QuestListenerHelper.scanAndTrackDirect(this.plugin, player, (Event)event);
        }
    }
}

