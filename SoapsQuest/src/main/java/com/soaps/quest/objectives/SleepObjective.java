package com.soaps.quest.objectives;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerBedEnterEvent;

import java.util.UUID;

/**
 * Objective that tracks sleeping in a bed.
 * Triggered when a player enters a bed.
 */
public class SleepObjective extends AbstractObjective {
    
    public SleepObjective(String objectiveId, int requiredAmount) {
        super(objectiveId, requiredAmount);
    }
    
    public SleepObjective(String objectiveId, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
    }
    
    @Override
    public String getType() {
        return "sleep";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerBedEnterEvent bedEvent)) {
            return false;
        }
        
        // Only count successful bed entries
        if (bedEvent.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        incrementProgress(playerId);
        
        return true;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        return current + "/" + getRequiredAmount() + " times";
    }
    
    @Override
    public String getDescription() {
        return "Sleep in bed " + getRequiredAmount() + " times";
    }
    
    @Override
    public String serialize() {
        return getType() + ":SLEEP:" + getRequiredAmount();
    }
    
    /**
     * Deserialize a SleepObjective from a string.
     * Format: sleep:SLEEP:amount
     */
    public static SleepObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid sleep objective data: " + data);
        }
        
        int amount = Integer.parseInt(parts[2]);
        return new SleepObjective(objectiveId, amount);
    }
}
