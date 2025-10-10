package com.soaps.quest.objectives;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Objective that uses PlaceholderAPI to check numeric placeholder values.
 * Progress is checked periodically rather than event-driven.
 */
public class PlaceholderObjective extends AbstractObjective {
    
    private final String placeholder;
    private static final boolean PLACEHOLDER_API_ENABLED = 
        Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    
    /**
     * Constructor for PlaceholderObjective.
     * 
     * @param objectiveId Unique identifier for this objective
     * @param placeholder The PlaceholderAPI placeholder to check (without %)
     * @param requiredAmount Required value for the placeholder
     */
    public PlaceholderObjective(String objectiveId, String placeholder, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.placeholder = placeholder;
    }
    
    public PlaceholderObjective(String objectiveId, String placeholder, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.placeholder = placeholder;
    }
    
    @Override
    public String getType() {
        return "placeholder";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        // Placeholder objectives are checked periodically, not event-driven
        return false;
    }
    
    /**
     * Check and update the placeholder value for a player.
     * Should be called periodically (e.g., every few seconds).
     * 
     * @param player The player to check
     * @return True if progress was updated
     */
    public boolean checkPlaceholder(Player player) {
        if (!PLACEHOLDER_API_ENABLED) {
            return false;
        }
        
        try {
            // Use PlaceholderAPI to parse the placeholder
            String result = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, "%" + placeholder + "%");
            
            // Try to parse as integer
            int value = Integer.parseInt(result);
            
            // Update progress if value changed
            int oldProgress = getCurrentProgress(player.getUniqueId());
            if (value != oldProgress) {
                setCurrentProgress(player.getUniqueId(), value);
                return true;
            }
        } catch (NumberFormatException e) {
            // Placeholder doesn't return a valid number
            return false;
        }
        
        return false;
    }
    
    @Override
    public boolean isComplete(UUID playerUUID) {
        // For placeholder objectives, check if current value >= required
        return getCurrentProgress(playerUUID) >= requiredAmount;
    }
    
    @Override
    public String getDescription() {
        return "Reach " + requiredAmount + " for " + placeholder;
    }
    
    @Override
    public String serialize() {
        return "placeholder:" + placeholder + ":" + requiredAmount;
    }
    
    /**
     * Get the placeholder string for this objective.
     * 
     * @return Placeholder (without % symbols)
     */
    public String getPlaceholder() {
        return placeholder;
    }
    
    /**
     * Check if PlaceholderAPI is available.
     * 
     * @return True if PlaceholderAPI is loaded
     */
    public static boolean isPlaceholderAPIEnabled() {
        return PLACEHOLDER_API_ENABLED;
    }
}
