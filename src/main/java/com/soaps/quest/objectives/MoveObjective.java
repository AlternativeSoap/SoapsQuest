/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.player.PlayerMoveEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveObjective
extends AbstractObjective {
    private final Map<UUID, Location> lastLocations = new HashMap<UUID, Location>();

    public MoveObjective(String objectiveId, int requiredAmount) {
        super(objectiveId, requiredAmount);
    }

    public MoveObjective(String objectiveId, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
    }

    @Override
    public void resetProgress(UUID playerUUID) {
        super.resetProgress(playerUUID);
        this.lastLocations.remove(playerUUID);
    }

    @Override
    public String getType() {
        return "move";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerMoveEvent)) {
            return false;
        }
        PlayerMoveEvent moveEvent = (PlayerMoveEvent)event;
        if (moveEvent.getFrom().getBlockX() == moveEvent.getTo().getBlockX() && moveEvent.getFrom().getBlockY() == moveEvent.getTo().getBlockY() && moveEvent.getFrom().getBlockZ() == moveEvent.getTo().getBlockZ()) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        Location lastLoc = this.lastLocations.get(playerId);
        if (lastLoc == null || !lastLoc.getWorld().equals((Object)moveEvent.getTo().getWorld())) {
            this.lastLocations.put(playerId, moveEvent.getTo().clone());
            return false;
        }
        double distance = lastLoc.distance(moveEvent.getTo());
        int distanceBlocks = (int)Math.floor(distance);
        if (distanceBlocks > 0) {
            this.incrementProgress(playerId, distanceBlocks);
            this.lastLocations.put(playerId, moveEvent.getTo().clone());
            return true;
        }
        return false;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        return current + "/" + this.getRequiredAmount() + " blocks";
    }

    @Override
    public String getDescription() {
        return "Travel " + this.getRequiredAmount() + " blocks";
    }

    @Override
    public String serialize() {
        return this.getType() + ":MOVE:" + this.getRequiredAmount();
    }

    public static MoveObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid move objective data: " + data);
        }
        int amount = Integer.parseInt(parts[2]);
        return new MoveObjective(objectiveId, amount);
    }
}

