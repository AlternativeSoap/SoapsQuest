package com.soaps.quest.objectives;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Objective for killing a specific number of MythicMobs.
 * This objective integrates with the MythicMobs plugin API.
 * 
 * Note: This objective is only registered if MythicMobs is installed.
 * Progress tracking is handled by MythicMobKillListener.
 */
public class KillMythicMobObjective extends AbstractObjective {
    
    private final String mobType; // MythicMob internal name (e.g., "SkeletalKnight", "SkeletonKing")
    
    /**
     * Constructor for KillMythicMobObjective.
     * 
     * @param objectiveId Unique identifier for this objective
     * @param mobType Internal name of the MythicMob to kill (case-insensitive)
     * @param requiredAmount Number of mobs to kill
     */
    public KillMythicMobObjective(String objectiveId, String mobType, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.mobType = mobType;
    }
    
    public KillMythicMobObjective(String objectiveId, String mobType, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.mobType = mobType;
    }
    
    @Override
    public String getType() {
        return "kill_mythicmob";
    }
    
    /**
     * Get the MythicMob type required for this objective.
     * 
     * @return MythicMob internal name
     */
    public String getMobType() {
        return mobType;
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        // Event handling is delegated to MythicMobKillListener
        // This method exists to satisfy the Objective interface
        // but is not called directly for MythicMob kills
        return false;
    }
    
    @Override
    public String getDescription() {
        return "Kill " + requiredAmount + " " + mobType;
    }
    
    @Override
    public String serialize() {
        return "kill_mythicmob:" + mobType + ":" + requiredAmount;
    }
}
