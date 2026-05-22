/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.entity.EntitySpawnEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntitySpawnEvent;

public class FireworkObjective
extends AbstractObjective {
    public FireworkObjective(String objectiveId, int requiredAmount) {
        super(objectiveId, requiredAmount);
    }

    public FireworkObjective(String objectiveId, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
    }

    @Override
    public String getType() {
        return "firework";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof EntitySpawnEvent)) {
            return false;
        }
        EntitySpawnEvent spawnEvent = (EntitySpawnEvent)event;
        if (spawnEvent.getEntity().getType() != EntityType.FIREWORK_ROCKET) {
            return false;
        }
        if (player == null) {
            return false;
        }
        boolean isNearby = spawnEvent.getEntity().getWorld().getNearbyPlayers(spawnEvent.getLocation(), 5.0).stream().anyMatch(online -> online.getUniqueId().equals(player.getUniqueId()));
        if (!isNearby) {
            return false;
        }
        this.incrementProgress(player.getUniqueId());
        return true;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        return current + "/" + this.getRequiredAmount() + " fireworks";
    }

    @Override
    public String getDescription() {
        return "Launch " + this.getRequiredAmount() + " fireworks";
    }

    @Override
    public String serialize() {
        return this.getType() + ":FIREWORK:" + this.getRequiredAmount();
    }

    public static FireworkObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid firework objective data: " + data);
        }
        int amount = Integer.parseInt(parts[2]);
        return new FireworkObjective(objectiveId, amount);
    }
}

