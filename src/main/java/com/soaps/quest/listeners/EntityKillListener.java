/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDeathEvent
 */
package com.soaps.quest.listeners;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.listeners.QuestListenerHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityKillListener
implements Listener {
    private final SoapsQuest plugin;

    public EntityKillListener(SoapsQuest plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!this.plugin.isEnabled()) {
            return;
        }
        Player player = event.getEntity().getKiller();
        if (!(player instanceof Player)) {
            return;
        }
        Player player2 = player;
        QuestListenerHelper.scanAndTrackProgress(this.plugin, player2, (Event)event);
    }
}

