/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerMoveEvent
 */
package com.soaps.quest.listeners;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.listeners.QuestListenerHelper;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener
implements Listener {
    private final SoapsQuest plugin;

    public MoveListener(SoapsQuest plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerMove(PlayerMoveEvent event) {
        QuestListenerHelper.scanAndTrackDirect(this.plugin, event.getPlayer(), (Event)event);
    }
}

