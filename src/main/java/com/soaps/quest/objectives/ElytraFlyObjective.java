/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.player.PlayerMoveEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

public class ElytraFlyObjective
extends AbstractObjective {
    public ElytraFlyObjective(String objectiveId, int requiredAmount) {
        super(objectiveId, requiredAmount);
    }

    public ElytraFlyObjective(String objectiveId, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
    }

    @Override
    public String getType() {
        return "elytra_fly";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerMoveEvent)) {
            return false;
        }
        PlayerMoveEvent moveEvent = (PlayerMoveEvent)event;
        if (!player.isGliding()) {
            return false;
        }
        if (moveEvent.getFrom().getBlockX() == moveEvent.getTo().getBlockX() && moveEvent.getFrom().getBlockY() == moveEvent.getTo().getBlockY() && moveEvent.getFrom().getBlockZ() == moveEvent.getTo().getBlockZ()) {
            return false;
        }
        this.incrementProgress(player.getUniqueId());
        return true;
    }

    @Override
    public String getDescription() {
        return "Fly " + this.requiredAmount + " blocks with Elytra";
    }

    @Override
    public String serialize() {
        return "elytra_fly:ANY:" + this.requiredAmount;
    }
}

