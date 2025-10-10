package com.soaps.quest.rewards;

import org.bukkit.entity.Player;

import com.soaps.quest.SoapsQuest;

import net.milkbowl.vault.economy.Economy;

/**
 * Reward that gives money to the player using Vault economy.
 */
public class MoneyReward implements Reward {
    
    private final double amount;
    private final SoapsQuest plugin;
    private final int chance; // 0-100, 100 = guaranteed
    
    /**
     * Constructor for MoneyReward.
     * 
     * @param plugin Plugin instance
     * @param amount Amount of money to give
     */
    public MoneyReward(SoapsQuest plugin, double amount) {
        this(plugin, amount, 100);
    }
    
    /**
     * Constructor for MoneyReward with chance.
     * 
     * @param plugin Plugin instance
     * @param amount Amount of money to give
     * @param chance Chance percentage (0-100)
     */
    public MoneyReward(SoapsQuest plugin, double amount, int chance) {
        this.plugin = plugin;
        this.amount = amount;
        this.chance = Math.max(0, Math.min(100, chance));
    }
    
    @Override
    public boolean give(Player player) {
        Economy economy = plugin.getEconomy();
        if (economy == null) {
            plugin.getLogger().warning("Cannot give money reward - Vault economy not found!");
            return false;
        }
        
        economy.depositPlayer(player, amount);
        return true;
    }
    
    @Override
    public String getDescription() {
        return "$" + amount;
    }
    
    /**
     * Get the money amount.
     * 
     * @return Money amount
     */
    public double getAmount() {
        return amount;
    }
    
    @Override
    public int getChance() {
        return chance;
    }
}
