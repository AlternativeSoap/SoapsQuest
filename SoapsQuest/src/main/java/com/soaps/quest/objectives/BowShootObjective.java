package com.soaps.quest.objectives;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.UUID;

/**
 * Objective that tracks bow shooting.
 * Counts arrows shot from bows.
 */
public class BowShootObjective extends AbstractObjective {
    
    public BowShootObjective(String objectiveId, int requiredAmount) {
        super(objectiveId, requiredAmount);
    }
    
    public BowShootObjective(String objectiveId, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
    }
    
    @Override
    public String getType() {
        return "bowshoot";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof EntityShootBowEvent bowEvent)) {
            return false;
        }
        
        // Check if shooter is the player
        if (!(bowEvent.getEntity() instanceof Player shooter)) {
            return false;
        }
        
        if (!shooter.getUniqueId().equals(player.getUniqueId())) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        incrementProgress(playerId);
        
        return true;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        return current + "/" + getRequiredAmount() + " arrows shot";
    }
    
    @Override
    public String getDescription() {
        return "Shoot " + getRequiredAmount() + " arrows";
    }
    
    @Override
    public String serialize() {
        return getType() + ":BOW:" + getRequiredAmount();
    }
    
    /**
     * Deserialize a BowShootObjective from a string.
     * Format: bowshoot:BOW:amount
     */
    public static BowShootObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid bowshoot objective data: " + data);
        }
        
        int amount = Integer.parseInt(parts[2]);
        return new BowShootObjective(objectiveId, amount);
    }
}
