package com.soaps.quest.objectives;

import java.util.UUID;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntitySpawnEvent;

/**
 * Objective that tracks firework launches.
 * Detects when players launch fireworks.
 */
public class FireworkObjective extends AbstractObjective {
    
    public FireworkObjective(String objectiveId, int requiredAmount) {
        super(objectiveId, requiredAmount);
    }
    
    public FireworkObjective(String objectiveId, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
    }
    
    @Override
    public String getType() {
        return "firework";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof EntitySpawnEvent spawnEvent)) {
            return false;
        }

        // Only handle firework spawns
        if (spawnEvent.getEntity().getType() != EntityType.FIREWORK) {
            return false;
        }

        // Early return if player is null
        if (player == null) {
            return false;
        }

        // Find a nearby player within 5 blocks matching this player
        boolean isNearby = spawnEvent.getEntity().getWorld()
                .getNearbyPlayers(spawnEvent.getLocation(), 5.0)
                .stream()
                .anyMatch(online -> online.getUniqueId().equals(player.getUniqueId()));

        if (!isNearby) {
            return false;
        }

        incrementProgress(player.getUniqueId());
        return true;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        return current + "/" + getRequiredAmount() + " fireworks";
    }
    
    @Override
    public String getDescription() {
        return "Launch " + getRequiredAmount() + " fireworks";
    }
    
    @Override
    public String serialize() {
        return getType() + ":FIREWORK:" + getRequiredAmount();
    }
    
    /**
     * Deserialize a FireworkObjective from a string.
     * Format: firework:FIREWORK:amount
     */
    public static FireworkObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid firework objective data: " + data);
        }
        
        int amount = Integer.parseInt(parts[2]);
        return new FireworkObjective(objectiveId, amount);
    }
}
