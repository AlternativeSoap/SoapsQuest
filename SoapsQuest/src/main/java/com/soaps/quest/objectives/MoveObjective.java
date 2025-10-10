package com.soaps.quest.objectives;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Objective that tracks distance moved.
 * Accumulates total distance traveled.
 */
public class MoveObjective extends AbstractObjective {
    
    private final Map<UUID, Location> lastLocations = new HashMap<>();
    
    public MoveObjective(String objectiveId, int requiredAmount) {
        super(objectiveId, requiredAmount);
    }
    
    public MoveObjective(String objectiveId, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
    }
    
    @Override
    public String getType() {
        return "move";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerMoveEvent moveEvent)) {
            return false;
        }
        
        // Skip if only head movement
        if (moveEvent.getFrom().getBlockX() == moveEvent.getTo().getBlockX() &&
            moveEvent.getFrom().getBlockY() == moveEvent.getTo().getBlockY() &&
            moveEvent.getFrom().getBlockZ() == moveEvent.getTo().getBlockZ()) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        Location lastLoc = lastLocations.get(playerId);
        
        if (lastLoc == null || !lastLoc.getWorld().equals(moveEvent.getTo().getWorld())) {
            lastLocations.put(playerId, moveEvent.getTo().clone());
            return false;
        }
        
        // Calculate distance moved
        double distance = lastLoc.distance(moveEvent.getTo());
        int distanceBlocks = (int) Math.floor(distance);
        
        if (distanceBlocks > 0) {
            incrementProgress(playerId, distanceBlocks);
            lastLocations.put(playerId, moveEvent.getTo().clone());
            return true;
        }
        
        return false;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        return current + "/" + getRequiredAmount() + " blocks";
    }
    
    @Override
    public String getDescription() {
        return "Travel " + getRequiredAmount() + " blocks";
    }
    
    @Override
    public String serialize() {
        return getType() + ":MOVE:" + getRequiredAmount();
    }
    
    /**
     * Deserialize a MoveObjective from a string.
     * Format: move:MOVE:amount
     */
    public static MoveObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid move objective data: " + data);
        }
        
        int amount = Integer.parseInt(parts[2]);
        return new MoveObjective(objectiveId, amount);
    }
}
