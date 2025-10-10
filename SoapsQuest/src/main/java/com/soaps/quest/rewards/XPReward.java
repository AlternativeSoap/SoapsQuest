package com.soaps.quest.rewards;

import org.bukkit.entity.Player;

/**
 * Reward that gives experience points to the player.
 */
public class XPReward implements Reward {
    
    private final int amount;
    private final int chance; // 0-100, 100 = guaranteed
    
    /**
     * Constructor for XPReward.
     * 
     * @param amount Amount of XP to give
     */
    public XPReward(int amount) {
        this(amount, 100);
    }
    
    /**
     * Constructor for XPReward with chance.
     * 
     * @param amount Amount of XP to give
     * @param chance Chance percentage (0-100)
     */
    public XPReward(int amount, int chance) {
        this.amount = amount;
        this.chance = Math.max(0, Math.min(100, chance));
    }
    
    @Override
    public boolean give(Player player) {
        player.giveExp(amount);
        return true;
    }
    
    @Override
    public String getDescription() {
        return amount + " XP";
    }
    
    /**
     * Get the XP amount.
     * 
     * @return XP amount
     */
    public int getAmount() {
        return amount;
    }
    
    @Override
    public int getChance() {
        return chance;
    }
}
