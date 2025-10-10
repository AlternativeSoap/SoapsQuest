package com.soaps.quest.rewards;

import org.bukkit.entity.Player;

/**
 * Interface for all reward types.
 * Each reward implementation defines how it should be given to a player.
 */
public interface Reward {
    
    /**
     * Give this reward to a player.
     * 
     * @param player The player receiving the reward
     * @return True if the reward was successfully given
     */
    boolean give(Player player);
    
    /**
     * Get a description of this reward for display purposes.
     * 
     * @return Reward description
     */
    String getDescription();
    
    /**
     * Get the chance for this reward to drop (0-100).
     * 
     * @return Chance percentage (100 = guaranteed, 50 = 50% chance, etc.)
     */
    int getChance();
    
    /**
     * Roll the chance and give the reward if successful.
     * 
     * @param player The player receiving the reward
     * @return True if the reward was rolled successfully and given
     */
    default boolean giveWithChance(Player player) {
        int chance = getChance();
        
        // If chance is 100 or higher, always give
        if (chance >= 100) {
            return give(player);
        }
        
        // Roll random number between 0-99
        int roll = (int) (Math.random() * 100);
        
        // Give reward if roll is less than chance
        if (roll < chance) {
            return give(player);
        }
        
        return false; // Chance failed
    }
}
