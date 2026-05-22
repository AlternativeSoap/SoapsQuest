/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 */
package com.soaps.quest.rewards.types;

import com.soaps.quest.rewards.QuestReward;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CommandQuestReward
implements QuestReward {
    private final String command;
    private final int chance;

    public CommandQuestReward(String command) {
        this(command, 100);
    }

    public CommandQuestReward(String command, int chance) {
        this.command = command;
        this.chance = Math.max(0, Math.min(100, chance));
    }

    @Override
    public String getType() {
        return "command";
    }

    @Override
    public boolean give(Player player) {
        String parsedCommand = this.command.replace("<player>", player.getName()).replace("{player}", player.getName()).replace("%player%", player.getName());
        Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)parsedCommand);
        return true;
    }

    @Override
    public String getDisplayDescription() {
        String base = "&b/" + this.command;
        if (this.chance < 100) {
            base = base + " &7(" + this.chance + "% chance)";
        }
        return base;
    }

    @Override
    public int getChance() {
        return this.chance;
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", (Object)"command");
        section.set("command", (Object)this.command);
        if (this.chance < 100) {
            section.set("chance", (Object)this.chance);
        }
    }

    public static CommandQuestReward deserialize(ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        String command = section.getString("command");
        int chance = section.getInt("chance", 100);
        if (command == null || command.isEmpty()) {
            return null;
        }
        return new CommandQuestReward(command, chance);
    }

    public String getCommand() {
        return this.command;
    }
}

