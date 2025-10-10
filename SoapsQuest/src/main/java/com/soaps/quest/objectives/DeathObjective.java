package com.soaps.quest.objectives;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

/**
 * Objective that tracks player deaths.
 * Can be used for "die X times" type quests.
 */
public class DeathObjective extends AbstractObjective {
    
    public DeathObjective(String objectiveId, int requiredAmount) {
        super(objectiveId, requiredAmount);
    }
    
    public DeathObjective(String objectiveId, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
    }
    
    @Override
    public String getType() {
        return "death";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerDeathEvent deathEvent)) {
            return false;
        }
        
        // Check if the dying player is the quest owner
        if (!deathEvent.getEntity().getUniqueId().equals(player.getUniqueId())) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        incrementProgress(playerId);
        
        return true;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        return current + "/" + getRequiredAmount() + " deaths";
    }
    
    @Override
    public String getDescription() {
        return "Die " + getRequiredAmount() + " times";
    }
    
    @Override
    public String serialize() {
        return getType() + ":DEATH:" + getRequiredAmount();
    }
    
    /**
     * Deserialize a DeathObjective from a string.
     * Format: death:DEATH:amount
     */
    public static DeathObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid death objective data: " + data);
        }
        
        int amount = Integer.parseInt(parts[2]);
        return new DeathObjective(objectiveId, amount);
    }
}
