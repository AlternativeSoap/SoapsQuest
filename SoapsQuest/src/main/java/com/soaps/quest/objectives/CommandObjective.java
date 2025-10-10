package com.soaps.quest.objectives;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Objective that is triggered manually via commands or external events.
 * Progress is incremented programmatically rather than through Bukkit events.
 */
public class CommandObjective extends AbstractObjective {
    
    private final String commandId;
    
    /**
     * Constructor for CommandObjective.
     * 
     * @param objectiveId Unique identifier for this objective
     * @param commandId The command/event identifier that triggers this
     * @param requiredAmount Number of times to trigger
     */
    public CommandObjective(String objectiveId, String commandId, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.commandId = commandId;
    }
    
    public CommandObjective(String objectiveId, String commandId, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.commandId = commandId;
    }
    
    @Override
    public String getType() {
        return "command";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        // Command objectives are triggered manually, not through Bukkit events
        // This method is not used for command objectives
        return false;
    }
    
    /**
     * Manually trigger progress for this command objective.
     * Should be called from external command handlers or event processors.
     * 
     * @param playerUUID Player UUID
     * @param triggerId The trigger ID to match against commandId
     * @return True if progress was incremented
     */
    public boolean trigger(UUID playerUUID, String triggerId) {
        if (this.commandId.equalsIgnoreCase(triggerId)) {
            incrementProgress(playerUUID);
            return true;
        }
        return false;
    }
    
    @Override
    public String getDescription() {
        return "Trigger '" + commandId + "' " + requiredAmount + " times";
    }
    
    @Override
    public String serialize() {
        return "command:" + commandId + ":" + requiredAmount;
    }
    
    /**
     * Get the command ID for this objective.
     * 
     * @return Command identifier
     */
    public String getCommandId() {
        return commandId;
    }
}
