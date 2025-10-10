package com.soaps.quest.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.rewards.RewardEntry;
import com.soaps.quest.utils.PlaceholderManager;
import com.soaps.quest.utils.QuestPaper;

import net.kyori.adventure.text.Component;

/**
 * Main command handler for /soapsquest and /sq.
 * Handles all quest-related commands with tab completion.
 */
public class QuestCommand implements CommandExecutor, TabCompleter {
    
    private final SoapsQuest plugin;
    private final GenerateCommand generateCommand;
    
    /**
     * Constructor for QuestCommand.
     * 
     * @param plugin Plugin instance
     */
    public QuestCommand(SoapsQuest plugin) {
        this.plugin = plugin;
        this.generateCommand = new GenerateCommand(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Base command - show simple help message
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessageManager().getMessage("usage-main"));
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "help" -> {
                return handleHelp(sender);
            }
            case "give" -> {
                return handleGive(sender, args);
            }
            case "list" -> {
                // Check if page number is provided
                if (args.length >= 2) {
                    try {
                        int page = Integer.parseInt(args[1]);
                        return handleList(sender, page);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(plugin.getMessageManager().parseColorCodes("&cInvalid page number!"));
                        return true;
                    }
                }
                return handleList(sender);
            }
            case "reload" -> {
                return handleReload(sender);
            }
            case "listreward" -> {
                return handleListReward(sender, args);
            }
            case "addreward" -> {
                return handleAddReward(sender, args);
            }
            case "removereward" -> {
                return handleRemoveReward(sender, args);
            }
            case "generate" -> {
                return generateCommand.onCommand(sender, command, label, args);
            }
            default -> {
                sender.sendMessage(plugin.getMessageManager().getMessage("usage-main"));
                return true;
            }
        }
    }
    
    /**
     * Handle /sq help - Display all available commands
     */
    private boolean handleHelp(CommandSender sender) {
        sender.sendMessage(plugin.getMessageManager().getMessage("help-header"));
        sender.sendMessage(plugin.getMessageManager().getMessage("help-give"));
        sender.sendMessage(plugin.getMessageManager().getMessage("help-list"));
        sender.sendMessage(plugin.getMessageManager().getMessage("help-generate"));
        sender.sendMessage(plugin.getMessageManager().getMessage("help-reload"));
        sender.sendMessage(plugin.getMessageManager().getMessage("help-listreward"));
        sender.sendMessage(plugin.getMessageManager().getMessage("help-addreward"));
        sender.sendMessage(plugin.getMessageManager().getMessage("help-removereward"));
        sender.sendMessage(plugin.getMessageManager().getMessage("help-help"));
        return true;
    }
    
    /**
     * Handle /sq give <player> <quest>
     * Uses the new multi-paper queue system for advanced quest management.
     */
    private boolean handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("soapsquest.give")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length < 3) {
            sender.sendMessage(plugin.getMessageManager().getMessage("usage-give"));
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.getMessageManager().getMessage("player-not-found",
                Map.of("player", args[1])));
            return true;
        }
        
        String questId = args[2];
        Quest quest = plugin.getQuestManager().getQuest(questId);
        if (quest == null) {
            sender.sendMessage(plugin.getMessageManager().getMessage("quest-not-found",
                Map.of("quest", questId)));
            return true;
        }
        
        // Check if target player has required permission for this quest
        if (!quest.hasPermission(target)) {
            sender.sendMessage(plugin.getMessageManager().getMessage("quest-give-no-permission",
                Map.of("player", target.getName(), "quest", quest.getDisplay())));
            target.sendMessage(plugin.getMessageManager().getMessage("quest-no-permission"));
            return true;
        }
        
        // Create quest paper with custom lore from quest config
        // This also generates a unique quest instance UUID
        ItemStack questPaper = QuestPaper.createQuestPaper(quest, target, 
            plugin.getMessageManager(), plugin.getQuestIdKey(), plugin.getPlayerUuidKey(), 
            quest.getCustomLore());
        
        // Get the quest instance UUID from the paper we just created
        java.util.UUID questInstanceUuid = QuestPaper.getQuestInstanceId(questPaper);
        if (questInstanceUuid == null) {
            sender.sendMessage(plugin.getMessageManager().parseColorCodes("&cError: Failed to create quest instance"));
            return true;
        }
        
        // Register this quest instance in the DataManager first
        plugin.getDataManager().registerQuestInstance(target, questInstanceUuid, 
            questId, quest.getRequiredAmount());
        
        // Use the new queue system to handle multi-paper logic
        String queueAction = plugin.getQuestManager().addQuestToQueue(target, questId, questInstanceUuid);
        
        switch (queueAction) {
            case "blocked":
                // Remove the quest instance we just registered since it was blocked
                plugin.getDataManager().removeQuestInstance(target, questInstanceUuid);
                sender.sendMessage(plugin.getMessageManager().getMessage("quest-already-active",
                    Map.of("quest", quest.getDisplay())));
                return true;
                
            case "replaced":
                // Give to player
                target.getInventory().addItem(questPaper);
                // Send messages
                sender.sendMessage(plugin.getMessageManager().getMessage("quest-given-replaced",
                    Map.of("quest", quest.getDisplay(), "player", target.getName())));
                target.sendMessage(plugin.getMessageManager().getMessage("quest-received-replaced",
                    Map.of("quest", quest.getDisplay())));
                break;
                
            case "queued":
                // Give to player
                target.getInventory().addItem(questPaper);
                // Send messages
                sender.sendMessage(plugin.getMessageManager().getMessage("quest-given-queued",
                    Map.of("quest", quest.getDisplay(), "player", target.getName())));
                target.sendMessage(plugin.getMessageManager().getMessage("quest-received-queued",
                    Map.of("quest", quest.getDisplay())));
                break;
                
            case "activated":
            default:
                // Give to player
                target.getInventory().addItem(questPaper);
                // Send messages
                sender.sendMessage(plugin.getMessageManager().getMessage("quest-given",
                    Map.of("quest", quest.getDisplay(), "player", target.getName())));
                target.sendMessage(plugin.getMessageManager().getMessage("quest-received",
                    Map.of("quest", quest.getDisplay())));
                break;
        }
        
        return true;
    }
    
    /**
     * Handle /sq list [page]
     */
    private boolean handleList(CommandSender sender) {
        return handleList(sender, 1); // Default to page 1
    }
    
    /**
     * Handle /sq list with pagination
     */
    private boolean handleList(CommandSender sender, int page) {
        if (!sender.hasPermission("soapsquest.list")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        
        Set<String> questIds = plugin.getQuestManager().getQuestIds();
        
        if (questIds.isEmpty()) {
            sender.sendMessage(plugin.getMessageManager().getMessage("quest-list-empty"));
            return true;
        }
        
        // Separate quests into regular and generated
        java.util.List<String> allQuests = new java.util.ArrayList<>();
        java.util.List<String> regularQuests = new java.util.ArrayList<>();
        java.util.List<String> generatedQuests = new java.util.ArrayList<>();
        
        for (String questId : questIds) {
            if (questId.startsWith("generated_")) {
                generatedQuests.add(questId);
            } else {
                regularQuests.add(questId);
            }
        }
        
        // Combine lists: regular quests first, then generated quests
        allQuests.addAll(regularQuests);
        allQuests.addAll(generatedQuests);
        
        // Pagination settings
        int questsPerPage = 10;
        int totalQuests = allQuests.size();
        int totalPages = (int) Math.ceil((double) totalQuests / questsPerPage);
        
        // Validate page number
        if (page < 1) {
            page = 1;
        } else if (page > totalPages) {
            page = totalPages;
        }
        
        // Calculate indices for current page
        int startIndex = (page - 1) * questsPerPage;
        int endIndex = Math.min(startIndex + questsPerPage, totalQuests);
        
        // Header
        sender.sendMessage(plugin.getMessageManager().getMessage("quest-list-header",
            Map.of("page", String.valueOf(page), 
                   "total_pages", String.valueOf(totalPages))
        ));
        
        // Display quests for current page
        for (int i = startIndex; i < endIndex; i++) {
            String questId = allQuests.get(i);
            
            // Add section header when transitioning from regular to generated
            if (i == regularQuests.size() && !generatedQuests.isEmpty()) {
                sender.sendMessage("");
                sender.sendMessage(plugin.getMessageManager().parseColorCodes("&d&lGenerated Quests:"));
            } else if (i == 0 && !regularQuests.isEmpty()) {
                sender.sendMessage(plugin.getMessageManager().parseColorCodes("&e&lRegular Quests:"));
            }
            
            displayQuestEntry(sender, questId);
        }
        
        // Footer with navigation
        if (totalPages > 1) {
            sender.sendMessage("");
            String navigation = "&7Page &e" + page + "&7/&e" + totalPages;
            if (page > 1) {
                navigation += " &8| &7Previous: &e/sq list " + (page - 1);
            }
            if (page < totalPages) {
                navigation += " &8| &7Next: &e/sq list " + (page + 1);
            }
            sender.sendMessage(plugin.getMessageManager().parseColorCodes(navigation));
        }
        
        return true;
    }
    
    /**
     * Helper method to display a single quest entry.
     * Creates clickable text components with hover tooltips showing quest paper preview.
     */
    private void displayQuestEntry(CommandSender sender, String questId) {
        Quest quest = plugin.getQuestManager().getQuest(questId);
        if (quest == null) {
            return;
        }
        
        // Handle both single-objective and multi-objective quests
        String typeDisplay;
        if (quest.hasObjectives()) {
            if (quest.getObjectives().size() > 1) {
                if (quest.isSequential()) {
                    typeDisplay = "SEQUENTIAL (" + quest.getObjectives().size() + " steps)";
                } else {
                    typeDisplay = "MULTI (" + quest.getObjectives().size() + " objectives)";
                }
            } else {
                typeDisplay = "SINGLE";
            }
        } else if (quest.getType() != null) {
            typeDisplay = quest.getType().toString();
        } else {
            typeDisplay = "UNKNOWN";
        }
        
        // Build placeholder context
        PlaceholderManager placeholderManager = new PlaceholderManager(plugin);
        PlaceholderManager.PlaceholderContext context = new PlaceholderManager.PlaceholderContext()
            .quest(quest);
        
        if (quest.getTier() != null) {
            context.tier(plugin.getTierManager().getTier(quest.getTier().toString()));
        }
        
        if (quest.getDifficulty() != null) {
            context.difficulty(plugin.getDifficultyManager().getDifficulty(quest.getDifficulty()));
        }
        
        // For players: create interactive clickable component with item preview
        if (sender instanceof Player player) {
            // Build the quest entry text with placeholders
            String entryText = plugin.getMessageManager().getRawMessage("quest-list-entry")
                .replace("<type>", typeDisplay);
            entryText = placeholderManager.replacePlaceholders(entryText, context);
            
            // Parse the text into a Component
            Component entryComponent = plugin.getMessageManager().parseColorCodes(entryText);
            
            // Build hover tooltip with quest details
            List<Component> hoverLines = new ArrayList<>();
            hoverLines.add(Component.text("━━━━━━━━━━━━━━━━━━━━━━━", net.kyori.adventure.text.format.NamedTextColor.GOLD));
            hoverLines.add(Component.text("  Quest Details", net.kyori.adventure.text.format.NamedTextColor.YELLOW, net.kyori.adventure.text.format.TextDecoration.BOLD));
            hoverLines.add(Component.text("━━━━━━━━━━━━━━━━━━━━━━━", net.kyori.adventure.text.format.NamedTextColor.GOLD));
            hoverLines.add(Component.empty());
            
            // Add objectives preview
            if (quest.hasObjectives() && !quest.getObjectives().isEmpty()) {
                hoverLines.add(Component.text("Objectives:", net.kyori.adventure.text.format.NamedTextColor.YELLOW));
                int maxObjectives = Math.min(3, quest.getObjectives().size());
                for (int i = 0; i < maxObjectives; i++) {
                    com.soaps.quest.objectives.Objective obj = quest.getObjectives().get(i);
                    String prefix = quest.isSequential() ? (i + 1) + ". " : "• ";
                    hoverLines.add(Component.text("  " + prefix + obj.getDescription(), net.kyori.adventure.text.format.NamedTextColor.WHITE));
                }
                if (quest.getObjectives().size() > 3) {
                    hoverLines.add(Component.text("  ... and " + (quest.getObjectives().size() - 3) + " more", net.kyori.adventure.text.format.NamedTextColor.GRAY, net.kyori.adventure.text.format.TextDecoration.ITALIC));
                }
                hoverLines.add(Component.empty());
            }
            
            // Add rewards preview using RewardManager with error handling
            hoverLines.add(Component.text("Rewards:", net.kyori.adventure.text.format.NamedTextColor.YELLOW));
            try {
                List<RewardEntry> rewards = plugin.getRewardManager().getRewardList(questId);
                if (rewards != null && !rewards.isEmpty()) {
                    int maxRewards = Math.min(5, rewards.size());
                    for (int i = 0; i < maxRewards; i++) {
                        RewardEntry reward = rewards.get(i);
                        try {
                            String rewardText = switch (reward.getType()) {
                                case XP -> {
                                    Object amount = reward.getData().get("amount");
                                    yield (amount != null ? amount : "0") + " XP";
                                }
                                case MONEY -> {
                                    Object amount = reward.getData().get("amount");
                                    if (amount instanceof Number numAmount) {
                                        yield "$" + String.format("%.2f", numAmount.doubleValue());
                                    }
                                    yield "$0.00";
                                }
                                case ITEM -> {
                                    Object amountObj = reward.getData().get("amount");
                                    int amount = amountObj instanceof Integer ? (Integer) amountObj : 1;
                                    String material = (String) reward.getData().get("material");
                                    String itemName = material != null ? material.replace("_", " ").toLowerCase() : "item";
                                    yield amount + "x " + itemName;
                                }
                                case COMMAND -> "Special reward";
                            };
                            
                            net.kyori.adventure.text.format.NamedTextColor color = switch (reward.getType()) {
                                case XP -> net.kyori.adventure.text.format.NamedTextColor.GREEN;
                                case MONEY -> net.kyori.adventure.text.format.NamedTextColor.GOLD;
                                case ITEM -> net.kyori.adventure.text.format.NamedTextColor.AQUA;
                                case COMMAND -> net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE;
                            };
                            
                            hoverLines.add(Component.text("  • " + rewardText, color));
                        } catch (Exception e) {
                            // Skip corrupted reward entry
                            plugin.getLogger().log(Level.WARNING, "[Quest List] Failed to display reward for quest {0}: {1}", new Object[]{questId, e.getMessage()});
                        }
                    }
                    if (rewards.size() > 5) {
                        hoverLines.add(Component.text("  ... and " + (rewards.size() - 5) + " more", net.kyori.adventure.text.format.NamedTextColor.GRAY, net.kyori.adventure.text.format.TextDecoration.ITALIC));
                    }
                } else {
                    hoverLines.add(Component.text("  • None", net.kyori.adventure.text.format.NamedTextColor.GRAY));
                }
            } catch (Exception e) {
                // Error loading rewards - show fallback message
                hoverLines.add(Component.text("  • Error loading rewards", net.kyori.adventure.text.format.NamedTextColor.RED));
                plugin.getLogger().log(Level.WARNING, "[Quest List] Failed to load rewards for quest {0}: {1}", new Object[]{questId, e.getMessage()});
            }
            
            // Add requirements/conditions if present
            if (quest.getConditions() != null && !quest.getConditions().getKeys(false).isEmpty()) {
                hoverLines.add(Component.empty());
                hoverLines.add(Component.text("Requirements:", net.kyori.adventure.text.format.NamedTextColor.YELLOW));
                
                org.bukkit.configuration.ConfigurationSection conditions = quest.getConditions();
                if (conditions.contains("cost")) {
                    hoverLines.add(Component.text("  • Cost: $" + conditions.getDouble("cost"), net.kyori.adventure.text.format.NamedTextColor.RED));
                }
                if (conditions.contains("min-level")) {
                    hoverLines.add(Component.text("  • Level " + conditions.getInt("min-level") + "+", net.kyori.adventure.text.format.NamedTextColor.YELLOW));
                }
                if (conditions.contains("permission")) {
                    hoverLines.add(Component.text("  • Special permission required", net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE));
                }
                if (conditions.contains("item")) {
                    String itemReq = conditions.getString("item");
                    if (itemReq != null && itemReq.contains(":")) {
                        String[] parts = itemReq.split(":");
                        String itemName = parts[0].replace("_", " ").toLowerCase();
                        hoverLines.add(Component.text("  • " + parts[1] + "x " + itemName, net.kyori.adventure.text.format.NamedTextColor.AQUA));
                    }
                }
            }
            
            hoverLines.add(Component.empty());
            hoverLines.add(Component.text("━━━━━━━━━━━━━━━━━━━━━━━", net.kyori.adventure.text.format.NamedTextColor.GOLD));
            
            // Check if player has permission to click and claim
            boolean canClick = player.hasPermission("soapsquest.list.click");
            if (canClick) {
                hoverLines.add(Component.text("✦ Click to claim this quest! ✦", net.kyori.adventure.text.format.NamedTextColor.GREEN, net.kyori.adventure.text.format.TextDecoration.ITALIC));
            } else {
                hoverLines.add(Component.text("Use /sq give to claim", net.kyori.adventure.text.format.NamedTextColor.GRAY, net.kyori.adventure.text.format.TextDecoration.ITALIC));
            }
            
            Component hoverText = Component.join(net.kyori.adventure.text.JoinConfiguration.newlines(), hoverLines);
            
            // Create the component with hover event and conditional click event
            Component clickableEntry = entryComponent
                .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(hoverText));
            
            // Only add click event if player has permission
            if (canClick) {
                clickableEntry = clickableEntry.clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/sq give " + player.getName() + " " + questId));
            }
            
            // Send the component
            player.sendMessage(clickableEntry);
            
        } else {
            // For console: send plain text (no click events)
            String message = plugin.getMessageManager().getRawMessage("quest-list-entry")
                .replace("<type>", typeDisplay);
            message = placeholderManager.replacePlaceholders(message, context);
            Component textComponent = plugin.getMessageManager().parseColorCodes(message);
            assert textComponent != null : "parseColorCodes() should never return null";
            sender.sendMessage(textComponent);
        }
    }
    
    /**
     * Handle /sq reload
     */
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("soapsquest.reload")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        
        // Ensure config.yml exists before reloading
        java.io.File configFile = new java.io.File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.getLogger().info("config.yml was deleted. Regenerating from defaults...");
            plugin.saveDefaultConfig();
        }
        
        // Reload config
        plugin.reloadConfig();
        
        // Reload managers (messages.yml will regenerate if missing)
        plugin.getMessageManager().reload();
        plugin.getProgressDisplayManager().reload();
        plugin.getQuestManager().reload();
        plugin.getRewardManager().reload();
        
        // Reload random generator config
        generateCommand.reload();
        
        // Note: Don't reload DataManager to preserve player progress
        
        sender.sendMessage(plugin.getMessageManager().getMessage("config-reloaded"));
        return true;
    }
    
    /**
     * Handle /sq listreward <questId>
     */
    private boolean handleListReward(CommandSender sender, String[] args) {
        if (!sender.hasPermission("soapsquest.admin")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(plugin.getMessageManager().getMessage("usage-listreward"));
            return true;
        }
        
        String questId = args[1];
        Quest quest = plugin.getQuestManager().getQuest(questId);
        
        if (quest == null) {
            sender.sendMessage(plugin.getMessageManager().getMessage("quest-not-found",
                Map.of("quest", questId)));
            return true;
        }
        
        List<RewardEntry> rewards = plugin.getRewardManager().getRewardList(questId);
        
        if (rewards.isEmpty()) {
            sender.sendMessage(plugin.getMessageManager().getMessage("rewards-empty",
                Map.of("quest", questId)));
            return true;
        }
        
        sender.sendMessage(plugin.getMessageManager().getMessage("rewards-header",
            Map.of("quest", questId)));
        
        for (int i = 0; i < rewards.size(); i++) {
            RewardEntry reward = rewards.get(i);
            sender.sendMessage(plugin.getMessageManager().parseColorCodes(
                "&8[&a" + (i + 1) + "&8] &f" + reward.getDisplayString()));
        }
        
        return true;
    }

    /**
     * Handle /sq addreward <questId> <type> [args...]
     * Usage: 
     * - /sq addreward <quest> item (while holding item)
     * - /sq addreward <quest> xp <amount>
     * - /sq addreward <quest> money <amount>
     * - /sq addreward <quest> command <command>
     */
    private boolean handleAddReward(CommandSender sender, String[] args) {
        if (!sender.hasPermission("soapsquest.addreward")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length < 3) {
            sender.sendMessage(plugin.getMessageManager().getMessage("usage-addreward"));
            return true;
        }
        
        String questId = args[1];
        String rewardType = args[2].toLowerCase();
        
        // Check if quest exists
        if (!plugin.getQuestManager().questExists(questId)) {
            sender.sendMessage(plugin.getMessageManager().getMessage("quest-not-found",
                Map.of("quest", questId)));
            return true;
        }
        
        switch (rewardType) {
            case "item" -> {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getMessageManager().getMessage("player-only"));
                    return true;
                }
                
                Player player = (Player) sender;
                ItemStack heldItem = player.getInventory().getItemInMainHand();
                if (heldItem.getType() == Material.AIR) {
                    sender.sendMessage(plugin.getMessageManager().getMessage("must-hold-item"));
                    return true;
                }
                
                plugin.getRewardManager().addItemReward(questId, heldItem);
                sender.sendMessage(plugin.getMessageManager().getMessage("reward-added",
                    Map.of("quest", questId)));
            }
            case "xp" -> {
                if (args.length < 4) {
                    sender.sendMessage(plugin.getMessageManager().parseColorCodes(
                        "&cUsage: /sq addreward <quest> xp <amount>"));
                    return true;
                }
                
                try {
                    int amount = Integer.parseInt(args[3]);
                    plugin.getRewardManager().addXPReward(questId, amount);
                    sender.sendMessage(plugin.getMessageManager().getMessage("reward-added",
                        Map.of("quest", questId)));
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getMessageManager().parseColorCodes(
                        "&cInvalid XP amount: " + args[3]));
                }
            }
            case "money" -> {
                if (args.length < 4) {
                    sender.sendMessage(plugin.getMessageManager().parseColorCodes(
                        "&cUsage: /sq addreward <quest> money <amount>"));
                    return true;
                }
                
                try {
                    double amount = Double.parseDouble(args[3]);
                    plugin.getRewardManager().addMoneyReward(questId, amount);
                    sender.sendMessage(plugin.getMessageManager().getMessage("reward-added",
                        Map.of("quest", questId)));
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getMessageManager().parseColorCodes(
                        "&cInvalid money amount: " + args[3]));
                }
            }
            case "command" -> {
                if (args.length < 4) {
                    sender.sendMessage(plugin.getMessageManager().parseColorCodes(
                        "&cUsage: /sq addreward <quest> command <command>"));
                    return true;
                }
                
                StringBuilder commandBuilder = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    if (i > 3) commandBuilder.append(" ");
                    commandBuilder.append(args[i]);
                }
                
                plugin.getRewardManager().addCommandReward(questId, commandBuilder.toString());
                sender.sendMessage(plugin.getMessageManager().getMessage("reward-added",
                    Map.of("quest", questId)));
            }
            default -> {
                sender.sendMessage(plugin.getMessageManager().parseColorCodes(
                    "&cInvalid reward type. Use: item, xp, money, or command"));
            }
        }
        
        return true;
    }
    
    /**
     * Handle /sq removereward <quest> <index>
     */
    private boolean handleRemoveReward(CommandSender sender, String[] args) {
        if (!sender.hasPermission("soapsquest.removereward")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length < 3) {
            sender.sendMessage(plugin.getMessageManager().getMessage("usage-removereward"));
            return true;
        }
        
        String questId = args[1];
        
        // Check if quest exists
        if (!plugin.getQuestManager().questExists(questId)) {
            sender.sendMessage(plugin.getMessageManager().getMessage("quest-not-found",
                Map.of("quest", questId)));
            return true;
        }
        
        // Parse index
        int index;
        try {
            index = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMessageManager().getMessage("reward-index-invalid"));
            return true;
        }
        
        // Remove reward
        if (plugin.getRewardManager().removeReward(questId, index)) {
            sender.sendMessage(plugin.getMessageManager().getMessage("reward-removed",
                Map.of("quest", questId)));
        } else {
            sender.sendMessage(plugin.getMessageManager().getMessage("reward-index-invalid"));
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Suggest subcommands
            List<String> subcommands = Arrays.asList("give", "list", "generate", "reload", "listreward", 
                "addreward", "removereward");
            return subcommands.stream()
                .filter(s -> s.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        
        String subCommand = args[0].toLowerCase();
        
        // Delegate tab completion for generate command
        if (subCommand.equals("generate")) {
            return generateCommand.onTabComplete(sender, command, label, args);
        }
        
        if (args.length == 2) {
            switch (subCommand) {
                case "give" -> {
                    // Suggest online players
                    return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
                }
                case "list" -> {
                    // Suggest page numbers
                    Set<String> questIds = plugin.getQuestManager().getQuestIds();
                    int totalQuests = questIds.size();
                    int totalPages = (int) Math.ceil((double) totalQuests / 10.0);
                    
                    List<String> pages = new ArrayList<>();
                    for (int i = 1; i <= Math.min(totalPages, 10); i++) { // Limit suggestions to first 10 pages
                        pages.add(String.valueOf(i));
                    }
                    return pages.stream()
                        .filter(p -> p.startsWith(args[1]))
                        .collect(Collectors.toList());
                }
                case "listreward", "addreward", "removereward" -> {
                    // Suggest quest IDs
                    return plugin.getQuestManager().getQuestIds().stream()
                        .filter(id -> id.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
                }
            }
        }
        
        if (args.length == 3) {
            switch (subCommand) {
                case "give" -> {
                    // Suggest quest IDs
                    return plugin.getQuestManager().getQuestIds().stream()
                        .filter(id -> id.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
                }
                case "addreward" -> {
                    // Suggest reward types
                    return Arrays.asList("item", "xp", "money", "command").stream()
                        .filter(type -> type.startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
                }
                case "removereward" -> {
                    // For removereward, suggest index numbers based on quest rewards
                    String questId = args[1];
                    if (plugin.getQuestManager().questExists(questId)) {
                        List<RewardEntry> rewards = plugin.getRewardManager().getRewardList(questId);
                        List<String> indices = new ArrayList<>();
                        for (int i = 1; i <= rewards.size(); i++) {
                            indices.add(String.valueOf(i));
                        }
                        return indices.stream()
                            .filter(index -> index.startsWith(args[2]))
                            .collect(Collectors.toList());
                    }
                }
            }
        }
        
        return completions;
    }
}
