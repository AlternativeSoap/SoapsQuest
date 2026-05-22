/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.vehicle.VehicleMoveEvent
 */
package com.soaps.quest.listeners;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.listeners.QuestListenerHelper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

public class VehicleListener
implements Listener {
    private final SoapsQuest plugin;

    public VehicleListener(SoapsQuest plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onVehicleMove(VehicleMoveEvent event) {
        for (Entity passenger : event.getVehicle().getPassengers()) {
            if (!(passenger instanceof Player)) continue;
            Player player = (Player)passenger;
            QuestListenerHelper.scanAndTrackDirect(this.plugin, player, (Event)event);
        }
    }
}

