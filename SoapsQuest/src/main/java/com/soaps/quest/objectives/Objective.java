package com.soaps.quest.objectives;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Core interface for all quest objectives.
 * Each objective type implements this to handle its own progress tracking,
 * event handling, completion checks, and display.
 */
public interface Objective {
    
    /**
     * Get the unique identifier for this objective instance.
     * This allows multiple objectives of the same type within a single quest.
     * 
     * @return Unique objective ID (e.g., "break_stone_1", "kill_zombie_2")
     */
    String getObjectiveId();
    
    /**
     * Get the type identifier for this objective.
     * Used for registry lookup and serialization.
     * 
     * @return Type identifier (e.g., "break", "kill", "place")
     */
    String getType();
    
    /**
     * Get the current progress for a specific player.
     * 
     * @param playerUUID Player's UUID
     * @return Current progress amount
     */
    int getCurrentProgress(UUID playerUUID);
    
    /**
     * Set the current progress for a specific player.
     * 
     * @param playerUUID Player's UUID
     * @param progress Progress amount to set
     */
    void setCurrentProgress(UUID playerUUID, int progress);
    
    /**
     * Get the required amount to complete this objective.
     * 
     * @return Required amount
     */
    int getRequiredAmount();
    
    /**
     * Increment progress for a player by 1.
     * 
     * @param playerUUID Player's UUID
     */
    void incrementProgress(UUID playerUUID);
    
    /**
     * Increment progress for a player by a specific amount.
     * 
     * @param playerUUID Player's UUID
     * @param amount Amount to increment by
     */
    void incrementProgress(UUID playerUUID, int amount);
    
    /**
     * Check if this objective is complete for a player.
     * 
     * @param playerUUID Player's UUID
     * @return True if progress >= required amount
     */
    boolean isComplete(UUID playerUUID);
    
    /**
     * Handle a Bukkit event and update progress if applicable.
     * Each objective type checks if the event is relevant and increments progress.
     * 
     * @param player The player who triggered the event
     * @param event The Bukkit event
     * @return True if progress was incremented, false otherwise
     */
    boolean handleEvent(Player player, Event event);
    
    /**
     * Get the progress display string for a player.
     * Example: "5/10", "Break Stone: 15/50"
     * 
     * @param playerUUID Player's UUID
     * @return Progress display string
     */
    String getProgressString(UUID playerUUID);
    
    /**
     * Get the objective description shown in lore.
     * Example: "Break 50 STONE", "Kill 10 ZOMBIE"
     * 
     * @return Objective description string
     */
    String getDescription();
    
    /**
     * Reset progress for a specific player.
     * 
     * @param playerUUID Player's UUID
     */
    void resetProgress(UUID playerUUID);
    
    /**
     * Serialize this objective to a string for storage.
     * Format: "type:data"
     * Example: "break:STONE:50", "kill:ZOMBIE:10"
     * 
     * @return Serialized objective string
     */
    String serialize();
    
    /**
     * Get the custom progress milestones for this objective.
     * 
     * @return List of milestone percentages
     */
    java.util.List<Integer> getMilestones();
    
    /**
     * Check if this objective has custom milestones configured.
     * 
     * @return True if milestones are configured
     */
    boolean hasMilestones();
    
    /**
     * Get the set of milestones already reached by a player.
     * 
     * @param playerUUID Player's UUID
     * @return Set of reached milestone percentages
     */
    java.util.Set<Integer> getReachedMilestones(UUID playerUUID);
    
    /**
     * Check for new milestones reached between old and new progress.
     * Updates internal tracking and returns list of newly reached milestones.
     * 
     * @param playerUUID Player's UUID
     * @param oldProgress Previous progress amount
     * @param newProgress New progress amount
     * @return List of newly reached milestone percentages
     */
    java.util.List<Integer> checkNewMilestones(UUID playerUUID, int oldProgress, int newProgress);
}
