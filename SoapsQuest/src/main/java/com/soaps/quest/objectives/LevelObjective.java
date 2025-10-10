package com.soaps.quest.objectives;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import java.util.UUID;

/**
 * Objective that tracks player level changes.
 * Can track reaching a specific level or gaining levels.
 */
public class LevelObjective extends AbstractObjective {
    
    private final boolean isReachLevel; // true = reach level, false = gain levels
    
    public LevelObjective(String objectiveId, int requiredAmount, boolean isReachLevel) {
        super(objectiveId, requiredAmount);
        this.isReachLevel = isReachLevel;
    }
    
    public LevelObjective(String objectiveId, int requiredAmount, boolean isReachLevel, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.isReachLevel = isReachLevel;
    }
    
    public boolean isReachLevel() {
        return isReachLevel;
    }
    
    @Override
    public String getType() {
        return isReachLevel ? "reachlevel" : "gainlevel";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerLevelChangeEvent levelEvent)) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        
        if (isReachLevel) {
            // Reach specific level
            if (levelEvent.getNewLevel() >= getRequiredAmount()) {
                setCurrentProgress(playerId, getRequiredAmount());
                return true;
            }
        } else {
            // Gain levels
            int levelsGained = levelEvent.getNewLevel() - levelEvent.getOldLevel();
            if (levelsGained > 0) {
                incrementProgress(playerId, levelsGained);
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        if (isReachLevel) {
            return "Level " + current + "/" + getRequiredAmount();
        } else {
            return current + "/" + getRequiredAmount() + " levels gained";
        }
    }
    
    @Override
    public String getDescription() {
        if (isReachLevel) {
            return "Reach level " + getRequiredAmount();
        } else {
            return "Gain " + getRequiredAmount() + " levels";
        }
    }
    
    @Override
    public String serialize() {
        return getType() + ":LEVEL:" + getRequiredAmount();
    }
    
    /**
     * Deserialize a LevelObjective from a string.
     * Format: reachlevel:LEVEL:amount or gainlevel:LEVEL:amount
     */
    public static LevelObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid level objective data: " + data);
        }
        
        boolean isReach = parts[0].equalsIgnoreCase("reachlevel");
        int amount = Integer.parseInt(parts[2]);
        
        return new LevelObjective(objectiveId, amount, isReach);
    }
}
