package com.soaps.quest.rewards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Reward that executes a console command.
 * Supports <player> placeholder for player name.
 */
public class CommandReward implements Reward {
    
    private final String command;
    private final int chance; // 0-100, 100 = guaranteed
    
    /**
     * Constructor for CommandReward.
     * 
     * @param command Command to execute (without leading /)
     */
    public CommandReward(String command) {
        this(command, 100);
    }
    
    /**
     * Constructor for CommandReward with chance.
     * 
     * @param command Command to execute (without leading /)
     * @param chance Chance percentage (0-100)
     */
    public CommandReward(String command, int chance) {
        this.command = command;
        this.chance = Math.max(0, Math.min(100, chance));
    }
    
    @Override
    public boolean give(Player player) {
        String parsedCommand = command.replace("<player>", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
        return true;
    }
    
    @Override
    public String getDescription() {
        return "Command: " + command;
    }
    
    /**
     * Get the command string.
     * 
     * @return Command
     */
    public String getCommand() {
        return command;
    }
    
    @Override
    public int getChance() {
        return chance;
    }
}
