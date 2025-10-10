package com.soaps.quest.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.managers.QuestGenerator;
import com.soaps.quest.managers.RandomGeneratorConfig;
import com.soaps.quest.quests.Quest;

/**
 * Generate random quests with simplified command structure.
 * 
 * Primary Usage (Player Self-Generation):
 *   /sq generate <type>      - Generate a quest of specific type for yourself
 *   /sq generate             - Generate a random quest for yourself
 * 
 * Admin Usage (Give to Others):
 *   Use /sq give <player> <quest> after generation to distribute quests
 * 
 * Examples:
 *   /sq generate single      - Generate a single objective quest
 *   /sq generate multi       - Generate a multi objective quest
 *   /sq generate             - Generate any random quest type
 */
public class GenerateCommand implements CommandExecutor, TabCompleter {
    private final SoapsQuest plugin;
    private final RandomGeneratorConfig config;
    private final QuestGenerator generator;
    
    public GenerateCommand(SoapsQuest plugin) {
        this.plugin = plugin;
        this.config = new RandomGeneratorConfig(plugin);
        this.generator = new QuestGenerator(plugin, config);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permission
        if (!sender.hasPermission("soapsquests.generate")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        
        // Check if generator is enabled
        if (!config.isEnabled()) {
            sender.sendMessage(plugin.getMessageManager().getMessage("random-generator-disabled"));
            return true;
        }
        
        // Simplified: /sq generate <type>
        // Player runs it on themselves, no confusing player targeting
        return handleSelfGeneration(sender, args);
    }
    
    /**
     * Handle /sq generate <type>
     * Player generates a quest for themselves
     */
    private boolean handleSelfGeneration(CommandSender sender, String[] args) {
        // Must be a player
        if (!(sender instanceof Player player)) {
            if (sender != null) {
                sender.sendMessage(plugin.getMessageManager().getMessage("player-only"));
            }
            return true;
        }
        
        // Parse quest type from args[1] (if provided)
        String type = args.length >= 2 ? args[1].toLowerCase() : null;
        
        // Validate type if specified
        if (type != null && !config.getAllowedTypes().contains(type)) {
            player.sendMessage(plugin.getMessageManager().parseColorCodes(
                "&cInvalid quest type! Allowed types: " + String.join(", ", config.getAllowedTypes())));
            return true;
        }
        
        // Generate quest
        String questId = type != null ? generator.generateQuest(type) : generator.generateQuest();
        
        if (questId == null) {
            player.sendMessage(plugin.getMessageManager().getMessage("random-generator-error"));
            return true;
        }
        
        // Verify quest was loaded successfully
        Quest quest = plugin.getQuestManager().getQuest(questId);
        if (quest == null) {
            player.sendMessage(plugin.getMessageManager().parseColorCodes(
                "&cError: Quest was generated but failed to load. Check server logs."));
            return true;
        }
        
        // Notify player of successful generation
        player.sendMessage(plugin.getMessageManager().parseColorCodes(
            "&aSuccessfully generated quest: &f" + quest.getDisplay()));
        player.sendMessage(plugin.getMessageManager().parseColorCodes(
            "&7Quest ID: &f" + questId));
        player.sendMessage(plugin.getMessageManager().parseColorCodes(
            "&7Use &f/sq give <player> " + questId + " &7to distribute this quest."));
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        // /sq generate <type>
        // Only suggest quest types for the first argument
        if (args.length == 2 && args[0].equalsIgnoreCase("generate")) {
            String input = args[1].toLowerCase();
            
            // Add quest types
            for (String type : config.getAllowedTypes()) {
                if (type.toLowerCase().startsWith(input)) {
                    completions.add(type);
                }
            }
        }
        
        return completions;
    }
    
    /**
     * Reloads the generator configuration
     */
    public void reload() {
        config.loadConfig();
    }
}
