package com.soaps.quest.rewards;

import com.soaps.quest.rewards.Reward;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReward implements Reward {
    private final String command;
    private final int chance;

    public CommandReward(String command) {
        this(command, 100);
    }

    public CommandReward(String command, int chance) {
        this.command = command;
        this.chance = Math.max(0, Math.min(100, chance));
    }

    @Override
    public boolean give(Player player) {
        String parsedCommand = this.command
                .replace("<player>", player.getName())
                .replace("{player}", player.getName())
                .replace("%player%", player.getName());
        Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), parsedCommand);
        return true;
    }

    @Override
    public String getDescription() {
        return "Command: " + this.command;
    }

    public String getCommand() {
        return this.command;
    }

    @Override
    public int getChance() {
        return this.chance;
    }
}
