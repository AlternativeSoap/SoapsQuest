/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.soaps.quest.commands;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.commands.GenerateCommand;
import com.soaps.quest.commands.QuestListRenderer;
import com.soaps.quest.commands.QuestRewardSubcommand;
import com.soaps.quest.commands.StatisticCommand;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import com.soaps.quest.rewards.RewardEntry;
import com.soaps.quest.utils.ColorUtil;
import com.soaps.quest.utils.QuestPaper;
import com.soaps.quest.utils.StringUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QuestCommand
implements CommandExecutor,
TabCompleter {
    private final SoapsQuest plugin;
    private final GenerateCommand generateCommand;
    private final StatisticCommand statisticCommand;
    private final QuestRewardSubcommand rewardSubcommand;
    private final QuestListRenderer listRenderer;

    public QuestCommand(SoapsQuest plugin) {
        this.plugin = plugin;
        this.generateCommand = new GenerateCommand(plugin);
        this.statisticCommand = new StatisticCommand(plugin);
        this.rewardSubcommand = new QuestRewardSubcommand(plugin);
        this.listRenderer = new QuestListRenderer(plugin);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String subCommand;
        if (args.length == 0) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("usage-main"));
            return true;
        }
        switch (subCommand = args[0].toLowerCase()) {
            case "help": {
                return this.handleHelp(sender);
            }
            case "info": {
                return this.handleInfo(sender);
            }
            case "debug": {
                return this.handleDebug(sender, args);
            }
            case "give": {
                return this.handleGive(sender, args);
            }
            case "list": {
                if (args.length >= 2) {
                    try {
                        int page = Integer.parseInt(args[1]);
                        return this.handleList(sender, page);
                    }
                    catch (NumberFormatException e) {
                        sender.sendMessage(this.plugin.getMessageManager().getMessage("invalid-page-number"));
                        return true;
                    }
                }
                return this.handleList(sender);
            }
            case "reload": {
                return this.handleReload(sender);
            }
            case "listreward": {
                return this.rewardSubcommand.handleListReward(sender, args);
            }
            case "addreward": {
                return this.rewardSubcommand.handleAddReward(sender, args);
            }
            case "removereward": {
                return this.rewardSubcommand.handleRemoveReward(sender, args);
            }
            case "remove": {
                return this.handleRemove(sender, args);
            }
            case "generate": {
                return this.generateCommand.onCommand(sender, command, label, args);
            }
            case "statistic": 
            case "statistics": 
            case "stats": {
                return this.statisticCommand.handle(sender, args);
            }
            case "browse": 
            case "browser": 
            case "gui": {
                return this.handleBrowse(sender);
            }
            case "editor": 
            case "edit": {
                return this.handleEditor(sender, args);
            }
            case "abandon": {
                return this.handleAbandon(sender, args);
            }
            case "copy": {
                return this.handleCopy(sender, args);
            }
            case "reset": {
                return this.handleReset(sender, args);
            }
            case "complete": {
                return this.handleComplete(sender, args);
            }
            case "active": 
            case "myquests": 
            case "quests": {
                return this.handleActive(sender, args);
            }
            case "sigils": {
                return this.handleSigils(sender, args);
            }
            case "drop": {
                return this.handleDrop(sender, args);
            }
        }
        sender.sendMessage(this.plugin.getMessageManager().getMessage("usage-main"));
        return true;
    }

    private boolean handleHelp(CommandSender sender) {
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-header"));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-give"));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-list"));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-browse"));
        if (this.plugin.isPremium()) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("help-editor"));
        }
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-statistic"));
        if (this.plugin.isPremium()) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("help-generate"));
        }
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-reload"));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-listreward"));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-addreward"));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-removereward"));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-debug"));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-remove"));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-abandon"));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-copy"));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-reset"));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-complete"));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-active"));
        if (this.plugin.isPremium()) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("help-sigils"));
            sender.sendMessage(this.plugin.getMessageManager().getMessage("help-drop"));
        }
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-info"));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("help-help"));
        return true;
    }

    private boolean handleBrowse(CommandSender sender) {
        if (!(sender instanceof Player)) {
            Component message = this.plugin.getMessageManager().getMessage("player-only");
            if (message != null) {
                sender.sendMessage(message);
            } else {
                sender.sendMessage(ColorUtil.colorize("&cThis command can only be used by players."));
            }
            return true;
        }
        Player player = (Player)sender;
        if (!player.hasPermission("soapsquest.gui.browser")) {
            Component message = this.plugin.getMessageManager().getMessage("no-permission");
            if (message != null) {
                player.sendMessage(message);
            }
            return true;
        }
        this.plugin.getGuiManager().getQuestBrowserGui().open(player);
        return true;
    }

    private boolean handleEditor(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Component message = this.plugin.getMessageManager().getMessage("player-only");
            if (message != null) {
                sender.sendMessage(message);
            } else {
                sender.sendMessage(ColorUtil.colorize("&cThis command can only be used by players."));
            }
            return true;
        }
        Player player = (Player)sender;
        if (!player.hasPermission("soapsquest.gui.editor")) {
            Component message = this.plugin.getMessageManager().getMessage("no-permission");
            if (message != null) {
                sender.sendMessage(message);
            }
            return true;
        }
        if (!this.plugin.isPremium()) {
            player.sendMessage(this.plugin.getMessageManager().getMessage("premium-only"));
            return true;
        }
        if (args.length >= 2) {
            String questId = args[1];
            Quest quest = this.plugin.getQuestManager().getQuest(questId);
            if (quest == null) {
                player.sendMessage(this.plugin.getMessageManager().getMessage("quest-not-found", Map.of("quest", questId)));
                return true;
            }
            this.plugin.getGuiManager().getQuestDetailsGui().open(player, quest);
        } else {
            this.plugin.getGuiManager().getQuestEditorGui().open(player);
        }
        return true;
    }

    private boolean handleInfo(CommandSender sender) {
        String version = this.plugin.getPluginMeta().getVersion();
        String editionKey = this.plugin.isPremium() ? "info-header-premium" : "info-header-free";
        sender.sendMessage(this.plugin.getMessageManager().getMessage(editionKey));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("info-version", Map.of("version", version)));
        sender.sendMessage(this.plugin.getMessageManager().getMessage("info-description"));
        if (!this.plugin.isPremium()) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("info-premium-hint"));
        }
        return true;
    }

    private boolean handleDebug(CommandSender sender, String[] args) {
        if (!sender.hasPermission("soapsquest.debug")) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        if (args.length < 2 || !args[1].equalsIgnoreCase("toggle")) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("usage-debug"));
            return true;
        }
        boolean newState = this.plugin.toggleDebugMode();
        String messageKey = newState ? "debug-mode-enabled" : "debug-mode-disabled";
        sender.sendMessage(this.plugin.getMessageManager().getMessage(messageKey));
        return true;
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        String queueAction;
        String questId;
        Player target;
        if (!sender.hasPermission("soapsquest.give")) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        if (args.length == 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(this.plugin.getMessageManager().getMessage("player-only"));
                return true;
            }
            target = (Player)sender;
            questId = args[1];
        } else if (args.length >= 3) {
            target = Bukkit.getPlayer((String)args[1]);
            if (target == null) {
                sender.sendMessage(this.plugin.getMessageManager().getMessage("player-not-found", Map.of("player", args[1])));
                return true;
            }
            questId = args[2];
        } else {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("usage-give"));
            return true;
        }
        Quest quest = this.plugin.getQuestManager().getQuest(questId);
        if (quest == null) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("quest-not-found", Map.of("quest", questId)));
            return true;
        }
        if (!quest.hasPermission(target)) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("quest-give-no-permission", Map.of("player", target.getName(), "quest", quest.getDisplay())));
            target.sendMessage(this.plugin.getMessageManager().getMessage("quest-no-permission"));
            return true;
        }
        ItemStack questPaper = QuestPaper.createQuestPaper(quest, target, this.plugin.getMessageManager(), this.plugin.getQuestIdKey(), this.plugin.getPlayerUuidKey(), quest.getCustomLore());
        UUID questInstanceUuid = QuestPaper.getQuestInstanceId(questPaper);
        if (questInstanceUuid == null) {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cError: Failed to create quest instance."));
            return true;
        }
        if (QuestPaper.isLocked(questPaper)) {
            QuestPaper.setLocked(questPaper, false);
            this.plugin.debugLog(Level.INFO, "[QuestGive] Unlocking quest paper for quest ''{0}'' (UUID: {1})", questId, questInstanceUuid);
        }
        this.plugin.getDataManager().registerQuestInstance(target, questInstanceUuid, questId, quest.getRequiredAmount());
        queueAction = this.plugin.getQuestManager().addQuestToQueue(target, questId, questInstanceUuid);
        target.getInventory().addItem(new ItemStack[]{questPaper});
        if ("queued".equals(queueAction)) {
            target.sendMessage(this.plugin.getMessageManager().getMessage("quest-received-queued", Map.of("quest", quest.getDisplay())));
            if (!sender.equals((Object)target)) {
                sender.sendMessage(this.plugin.getMessageManager().getMessage("quest-give-sender-queued", Map.of("player", target.getName(), "quest", quest.getDisplay())));
            }
        } else {
            target.sendMessage(this.plugin.getMessageManager().getMessage("quest-received", Map.of("quest", quest.getDisplay())));
            if (!sender.equals((Object)target)) {
                sender.sendMessage(this.plugin.getMessageManager().getMessage("quest-give-sender-activated", Map.of("player", target.getName(), "quest", quest.getDisplay())));
            }
        }
        this.plugin.getQuestManager().refreshPlayerQueues(target);
        this.plugin.getQuestManager().updateAllQuestPapersForPlayer(target);
        return true;
    }

    private boolean handleList(CommandSender sender) {
        return this.handleList(sender, 1);
    }

    private boolean handleList(CommandSender sender, int page) {
        if (!sender.hasPermission("soapsquest.list")) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        Set<String> questIds = this.plugin.getQuestManager().getQuestIds();
        if (questIds.isEmpty()) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("quest-list-empty"));
            return true;
        }
        ArrayList allQuests = new ArrayList();
        ArrayList<String> regularQuests = new ArrayList<String>();
        ArrayList<String> generatedQuests = new ArrayList<String>();
        for (String questId : questIds) {
            if (questId.startsWith("generated_")) {
                generatedQuests.add(questId);
                continue;
            }
            regularQuests.add(questId);
        }
        allQuests.addAll(regularQuests);
        allQuests.addAll(generatedQuests);
        int questsPerPage = 10;
        int totalQuests = allQuests.size();
        int totalPages = (int)Math.ceil((double)totalQuests / (double)questsPerPage);
        if (page < 1) {
            page = 1;
        } else if (page > totalPages) {
            page = totalPages;
        }
        int startIndex = (page - 1) * questsPerPage;
        int endIndex = Math.min(startIndex + questsPerPage, totalQuests);
        sender.sendMessage(this.plugin.getMessageManager().getMessage("quest-list-header", Map.of("page", String.valueOf(page), "total_pages", String.valueOf(totalPages))));
        for (int i = startIndex; i < endIndex; ++i) {
            String questId = (String)allQuests.get(i);
            if (i == regularQuests.size() && !generatedQuests.isEmpty()) {
                sender.sendMessage((Component)Component.empty());
                sender.sendMessage(this.plugin.getMessageManager().getMessage("quest-list-generated-header"));
            } else if (i == 0 && !regularQuests.isEmpty()) {
                sender.sendMessage(this.plugin.getMessageManager().getMessage("quest-list-regular-header"));
            }
            this.listRenderer.displayQuestEntry(sender, questId);
        }
        if (totalPages > 1) {
            sender.sendMessage((Component)Component.empty());
            String previous = page > 1 ? " &8| &7Previous: &e/sq list " + (page - 1) : "";
            String next = page < totalPages ? " &8| &7Next: &e/sq list " + (page + 1) : "";
            sender.sendMessage(this.plugin.getMessageManager().getMessage("quest-list-navigation", Map.of("page", String.valueOf(page), "total_pages", String.valueOf(totalPages), "previous", previous, "next", next)));
        }
        return true;
    }

    private void displayQuestEntry(CommandSender sender, String questId) {
        this.listRenderer.displayQuestEntry(sender, questId);
    }

    private boolean handleAbandon(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("player-only"));
            return true;
        }
        Player player = (Player)sender;
        if (args.length < 2) {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cUsage: /sq abandon <questId>"));
            return true;
        }
        String questId = args[1];
        Quest quest = this.plugin.getQuestManager().getQuest(questId);
        if (quest == null) {
            player.sendMessage(this.plugin.getMessageManager().getMessage("quest-not-found", Map.of("quest", questId)));
            return true;
        }
        boolean foundPaper = false;
        for (int i = 0; i < player.getInventory().getSize(); ++i) {
            UUID instanceId;
            String paperId;
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || !QuestPaper.isQuestPaper(item, this.plugin.getQuestIdKey()) || !questId.equals(paperId = QuestPaper.getQuestId(item, this.plugin.getQuestIdKey())) || (instanceId = QuestPaper.getQuestInstanceId(item)) == null) continue;
            this.plugin.getDataManager().removeQuestInstance(player, instanceId);
            this.plugin.getQuestManager().removeQuestFromQueue(player, questId, instanceId);
            player.getInventory().setItem(i, null);
            foundPaper = true;
        }
        if (foundPaper) {
            this.plugin.getQuestManager().refreshPlayerQueues(player);
            this.plugin.getProgressDisplayManager().clearProgress(player, questId);
            this.plugin.getDataManager().saveDataAsync();
            player.sendMessage(this.plugin.getMessageManager().getMessage("quest-abandoned", Map.of("quest", quest.getDisplay())));
        } else {
            player.sendMessage(this.plugin.getMessageManager().getMessage("quest-abandon-not-found", Map.of("quest", quest.getDisplay())));
        }
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        File questsFile;
        if (!sender.hasPermission("soapsquest.reload")) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        File configFile = new File(this.plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.plugin.getLogger().info("config.yml was deleted. Regenerating from defaults...");
            this.plugin.saveDefaultConfig();
        }
        if ((questsFile = new File(this.plugin.getDataFolder(), "quests.yml")).exists()) {
            try {
                YamlConfiguration testConfig = YamlConfiguration.loadConfiguration((File)questsFile);
                ConfigurationSection testQuests = testConfig.getConfigurationSection("quests");
                if (testQuests != null) {
                    ArrayList<String> errors = new ArrayList<>();
                    for (String string : testQuests.getKeys(false)) {
                        ConfigurationSection qSection = testQuests.getConfigurationSection(string);
                        if (qSection == null) {
                            errors.add("Quest '" + string + "': Invalid section (not a map)");
                            continue;
                        }
                        if (!qSection.contains("objectives")) {
                            errors.add("Quest '" + string + "': Missing 'objectives' section");
                        }
                        if (qSection.contains("display")) continue;
                        errors.add("Quest '" + string + "': Missing 'display' name");
                    }
                    if (!errors.isEmpty()) {
                        sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&c[SoapsQuest] &fReload aborted! Found " + errors.size() + " error(s) in quests.yml:"));
                        for (String string : errors) {
                            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("  &c\u2022 &7" + string));
                        }
                        sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&7Fix the errors above and try again. Old config is still active."));
                        return true;
                    }
                }
            }
            catch (Exception e) {
                sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&c[SoapsQuest] &fReload aborted! quests.yml has syntax errors:"));
                sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("  &c\u2022 &7" + e.getMessage()));
                sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&7Fix the YAML syntax and try again. Old config is still active."));
                return true;
            }
        }
        this.plugin.reloadConfig();
        this.plugin.loadLoggingSettings();
        this.plugin.getMessageManager().reload();
        this.plugin.getProgressDisplayManager().reload();
        this.plugin.getDifficultyManager().reload();
        this.plugin.getTierManager().reload();
        this.plugin.getQuestManager().reload();
        this.plugin.getRewardManager().reload();
        if (this.plugin.getRecurringQuestManager() != null) {
            this.plugin.getRecurringQuestManager().reload();
        }
        this.plugin.getAsyncTaskManager().reload();
        if (this.plugin.getQuestLootManager() != null) {
            this.plugin.getQuestLootManager().reload();
        }
        if (this.plugin.getQuestGeneratorService() != null) {
            this.plugin.getQuestGeneratorService().reload();
        }
        this.plugin.getGuiManager().reload();
        this.generateCommand.reload();
        int questCount = this.plugin.getQuestManager().getAllQuests().size();
        sender.sendMessage(this.plugin.getMessageManager().getMessage("config-reloaded", Map.of("count", String.valueOf(questCount))));
        return true;
    }

    private boolean handleRemove(CommandSender sender, String[] args) {
        if (!sender.hasPermission("soapsquest.remove")) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cUsage: /sq remove <questId>"));
            return true;
        }
        String questId = args[1];
        if (!this.plugin.getQuestManager().questExists(questId)) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("quest-not-found", Map.of("quest", questId)));
            return true;
        }
        boolean removed = this.plugin.getQuestManager().removeQuest(questId);
        if (removed) {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&a[SoapsQuest] &fQuest '&e" + questId + "&f' has been removed!"));
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&7Use /sq reload to apply changes."));
        } else {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&c[SoapsQuest] Failed to remove quest '&e" + questId + "&c'!"));
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if (args.length == 1) {
            ArrayList<String> subcommands = new ArrayList<String>();
            subcommands.add("help");
            subcommands.add("info");
            if (sender.hasPermission("soapsquest.give")) {
                subcommands.add("give");
            }
            if (sender.hasPermission("soapsquest.list")) {
                subcommands.add("list");
            }
            if (sender.hasPermission("soapsquest.gui.browser")) {
                subcommands.add("browse");
                subcommands.add("gui");
            }
            if (sender.hasPermission("soapsquest.gui.editor")) {
                subcommands.add("editor");
                subcommands.add("edit");
            }
            if (sender.hasPermission("soapsquest.generate")) {
                subcommands.add("generate");
            }
            if (sender.hasPermission("soapsquest.reload")) {
                subcommands.add("reload");
            }
            if (sender.hasPermission("soapsquest.listreward")) {
                subcommands.add("listreward");
            }
            if (sender.hasPermission("soapsquest.addreward")) {
                subcommands.add("addreward");
            }
            if (sender.hasPermission("soapsquest.removereward")) {
                subcommands.add("removereward");
            }
            if (sender.hasPermission("soapsquest.remove")) {
                subcommands.add("remove");
            }
            if (sender.hasPermission("soapsquest.debug")) {
                subcommands.add("debug");
            }
            if (sender.hasPermission("soapsquest.sigils")) {
                subcommands.add("sigils");
            }
            if (sender.hasPermission("soapsquest.drop")) {
                subcommands.add("drop");
            }
            if (sender.hasPermission("soapsquest.statistic")) {
                subcommands.add("statistic");
            }
            subcommands.add("abandon");
            return subcommands.stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        String subCommand = args[0].toLowerCase();
        if (subCommand.equals("generate")) {
            return this.generateCommand.onTabComplete(sender, command, label, args);
        }
        if (args.length == 2) {
            switch (subCommand) {
                case "debug": {
                    return Arrays.asList("toggle").stream().filter(s -> s.startsWith(args[1].toLowerCase())).collect(Collectors.toList());
                }
                case "statistic": 
                case "statistics": 
                case "stats": {
                    return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
                }
                case "give": {
                    ArrayList<String> suggestions = new ArrayList<String>();
                    Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase())).forEach(suggestions::add);
                    this.plugin.getQuestManager().getQuestIds().stream().filter(id -> id.toLowerCase().startsWith(args[1].toLowerCase())).forEach(suggestions::add);
                    return suggestions;
                }
                case "list": {
                    Set<String> questIds = this.plugin.getQuestManager().getQuestIds();
                    int totalQuests = questIds.size();
                    int totalPages = (int)Math.ceil((double)totalQuests / 10.0);
                    ArrayList<String> pages = new ArrayList<String>();
                    for (int i = 1; i <= Math.min(totalPages, 10); ++i) {
                        pages.add(String.valueOf(i));
                    }
                    return pages.stream().filter(p -> p.startsWith(args[1])).collect(Collectors.toList());
                }
                case "listreward": 
                case "addreward": 
                case "removereward": 
                case "remove": {
                    return this.plugin.getQuestManager().getQuestIds().stream().filter(id -> id.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
                }
                case "drop": {
                    ArrayList<String> suggestions = new ArrayList<String>();
                    this.plugin.getQuestManager().getQuestIds().stream().filter(id -> id.toLowerCase().startsWith(args[1].toLowerCase())).forEach(suggestions::add);
                    suggestions.add("entity");
                    return suggestions.stream().filter(id -> id.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
                }
                case "sigils": {
                    return Arrays.asList("give", "take", "set", "reset", "balance").stream().filter(s -> s.startsWith(args[1].toLowerCase())).collect(Collectors.toList());
                }
                case "editor": 
                case "edit": {
                    return this.plugin.getQuestManager().getQuestIds().stream().filter(id -> id.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
                }
                case "abandon": {
                    if (!(sender instanceof Player)) break;
                    Player player = (Player)sender;
                    LinkedHashSet<String> heldQuestIds = new LinkedHashSet<String>();
                    for (ItemStack item : player.getInventory().getContents()) {
                        String qid;
                        if (item == null || !QuestPaper.isQuestPaper(item, this.plugin.getQuestIdKey()) || (qid = QuestPaper.getQuestId(item, this.plugin.getQuestIdKey())) == null) continue;
                        heldQuestIds.add(qid);
                    }
                    return heldQuestIds.stream().filter(id -> id.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
                }
            }
        }
        if (args.length == 3) {
            switch (subCommand) {
                case "give": {
                    return this.plugin.getQuestManager().getQuestIds().stream().filter(id -> id.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
                }
                case "addreward": {
                    return Arrays.asList("item", "xp", "money", "sigils", "command").stream().filter(type -> type.startsWith(args[2].toLowerCase())).collect(Collectors.toList());
                }
                case "removereward": {
                    String questId = args[1];
                    if (!this.plugin.getQuestManager().questExists(questId)) break;
                    List<RewardEntry> rewards = this.plugin.getRewardManager().getRewardList(questId);
                    ArrayList<String> indices = new ArrayList<String>();
                    for (int i = 1; i <= rewards.size(); ++i) {
                        indices.add(String.valueOf(i));
                    }
                    return indices.stream().filter(index -> index.startsWith(args[2])).collect(Collectors.toList());
                }
                case "reset": 
                case "complete": {
                    return this.plugin.getQuestManager().getQuestIds().stream().filter(id -> id.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
                }
                case "copy": {
                    return this.plugin.getQuestManager().getQuestIds().stream().filter(id -> id.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
                }
                case "sigils": {
                    return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
                }
                case "drop": {
                    if ("entity".equalsIgnoreCase(args[1])) {
                        ArrayList<String> entityIds = new ArrayList<String>();
                        Bukkit.getOnlinePlayers().stream().map(Player::getName).forEach(entityIds::add);
                        Bukkit.getWorlds().forEach(world -> world.getEntities().stream().limit(20L).forEach(entity -> entityIds.add(entity.getUniqueId().toString())));
                        return entityIds.stream().filter(id -> id.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
                    }
                    return Arrays.asList("entity", "~", "0", "100").stream().filter(type -> type.startsWith(args[2].toLowerCase())).collect(Collectors.toList());
                }
            }
        }
        if (args.length == 4 && subCommand.equals("drop") && "entity".equalsIgnoreCase(args[1])) {
            return this.plugin.getQuestManager().getQuestIds().stream().filter(id -> id.toLowerCase().startsWith(args[3].toLowerCase())).collect(Collectors.toList());
        }
        if (args.length == 6 && subCommand.equals("drop")) {
            return Bukkit.getWorlds().stream().map(World::getName).filter(name -> name.toLowerCase().startsWith(args[5].toLowerCase())).collect(Collectors.toList());
        }
        return completions;
    }

    private boolean handleCopy(CommandSender sender, String[] args) {
        if (!sender.hasPermission("soapsquest.admin")) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cUsage: /sq copy <sourceQuestId> <newQuestId>"));
            return true;
        }
        String sourceId = args[1];
        String newId = StringUtil.sanitizeQuestId(args[2]);
        if (!this.plugin.getQuestManager().questExists(sourceId)) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("quest-not-found", Map.of("quest", sourceId)));
            return true;
        }
        if (this.plugin.getQuestManager().questExists(newId)) {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cA quest with ID '&e" + newId + "&c' already exists."));
            return true;
        }
        boolean ok = this.plugin.getQuestManager().copyQuest(sourceId, newId);
        if (ok) {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&a[SoapsQuest] &fQuest '&e" + sourceId + "&f' copied to '&e" + newId + "&f'!"));
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&7Use /sq editor " + newId + " to customise the copy."));
        } else {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&c[SoapsQuest] Failed to copy quest. Check server logs."));
        }
        return true;
    }

    private boolean handleReset(CommandSender sender, String[] args) {
        if (!sender.hasPermission("soapsquest.admin")) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cUsage: /sq reset <player> <questId>"));
            return true;
        }
        Player target = Bukkit.getPlayer((String)args[1]);
        if (target == null) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("player-not-found", Map.of("player", args[1])));
            return true;
        }
        String questId = args[2];
        if (!this.plugin.getQuestManager().questExists(questId)) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("quest-not-found", Map.of("quest", questId)));
            return true;
        }
        boolean found = false;
        for (ItemStack item : target.getInventory().getContents()) {
            UUID instanceId;
            if (item == null || !QuestPaper.isQuestPaper(item, this.plugin.getQuestIdKey()) || !questId.equals(QuestPaper.getQuestId(item, this.plugin.getQuestIdKey())) || (instanceId = QuestPaper.getQuestInstanceId(item)) == null) continue;
            QuestProgress progress = this.plugin.getDataManager().getQuestInstance(target.getUniqueId(), instanceId);
            if (progress != null) {
                progress.setCurrentProgress(0);
                progress.getAllObjectiveProgress().keySet().forEach(k -> progress.setObjectiveProgress((String)k, 0));
                progress.setCurrentObjectiveIndex(0);
            }
            this.plugin.getQuestManager().updateAllQuestPapersForPlayer(target);
            found = true;
        }
        if (found) {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&a[SoapsQuest] &fReset &e" + target.getName() + "&f's progress for quest '&e" + questId + "&f'."));
        } else {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&c[SoapsQuest] &e" + target.getName() + "&c does not have an active paper for '&e" + questId + "&c'."));
        }
        return true;
    }

    private boolean handleComplete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("soapsquest.admin")) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cUsage: /sq complete <player> <questId>"));
            return true;
        }
        Player target = Bukkit.getPlayer((String)args[1]);
        if (target == null) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("player-not-found", Map.of("player", args[1])));
            return true;
        }
        String questId = args[2];
        Quest quest = this.plugin.getQuestManager().getQuest(questId);
        if (quest == null) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("quest-not-found", Map.of("quest", questId)));
            return true;
        }
        boolean found = false;
        for (ItemStack item : target.getInventory().getContents()) {
            UUID instanceId;
            if (item == null || !QuestPaper.isQuestPaper(item, this.plugin.getQuestIdKey()) || !questId.equals(QuestPaper.getQuestId(item, this.plugin.getQuestIdKey())) || (instanceId = QuestPaper.getQuestInstanceId(item)) == null) continue;
            QuestProgress progress = this.plugin.getDataManager().getQuestInstance(target.getUniqueId(), instanceId);
            if (progress != null) {
                progress.setCurrentProgress(quest.getRequiredAmount());
                progress.setClaimable(true);
            }
            this.plugin.getQuestManager().updateAllQuestPapersForPlayer(target);
            target.sendMessage(this.plugin.getMessageManager().parseColorCodes("&a[SoapsQuest] &fAn admin has completed quest '&e" + quest.getDisplay() + "&f' for you!"));
            found = true;
        }
        if (found) {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&a[SoapsQuest] &fForce-completed '&e" + questId + "&f' for &e" + target.getName() + "&f."));
        } else {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&c[SoapsQuest] &e" + target.getName() + "&c does not have an active paper for '&e" + questId + "&c'."));
        }
        return true;
    }

    private boolean handleActive(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("player-only"));
            return true;
        }
        Player player = (Player)sender;
        Player target = player;
        if (args.length >= 2) {
            if (!player.hasPermission("soapsquest.progress.others")) {
                player.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
                return true;
            }
            target = Bukkit.getPlayer((String)args[1]);
            if (target == null) {
                player.sendMessage(this.plugin.getMessageManager().getMessage("player-not-found", Map.of("player", args[1])));
                return true;
            }
        }
        this.plugin.getGuiManager().getPlayerQuestsGui().open(player, target);
        return true;
    }

    private boolean handleSigils(CommandSender sender, String[] args) {
        if (!this.plugin.isPremium()) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("premium-only"));
            return true;
        }
        if (!sender.hasPermission("soapsquest.sigils")) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        if (this.plugin.getSigilManager() == null) {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cSigil manager is unavailable."));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cUsage: /sq sigils <give|take|set|reset|balance> <player> [amount]"));
            return true;
        }
        String action = args[1].toLowerCase();
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("player-not-found", Map.of("player", args[2])));
            return true;
        }
        UUID uuid = target.getUniqueId();
        switch (action) {
            case "balance": {
                double balance = this.plugin.getSigilManager().getBalance(uuid);
                sender.sendMessage(this.plugin.getMessageManager().parseColorCodes(String.format("&a%s has %.2f sigils.", target.getName(), balance)));
                return true;
            }
            case "reset": {
                this.plugin.getSigilManager().reset(uuid);
                sender.sendMessage(this.plugin.getMessageManager().parseColorCodes(String.format("&aReset sigils for %s.", target.getName())));
                return true;
            }
            case "give":
            case "take":
            case "set": {
                if (args.length < 4) {
                    sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cUsage: /sq sigils <give|take|set> <player> <amount>"));
                    return true;
                }
                double amount;
                try {
                    amount = Double.parseDouble(args[3]);
                }
                catch (NumberFormatException e) {
                    sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cInvalid amount: " + args[3]));
                    return true;
                }
                double newBalance = switch (action) {
                    case "give" -> this.plugin.getSigilManager().give(uuid, amount);
                    case "take" -> this.plugin.getSigilManager().take(uuid, amount);
                    default -> this.plugin.getSigilManager().setBalance(uuid, amount);
                };
                sender.sendMessage(this.plugin.getMessageManager().parseColorCodes(String.format("&aSigils updated for %s. New balance: %.2f", target.getName(), newBalance)));
                return true;
            }
            default: {
                sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cUnknown sigil action."));
                return true;
            }
        }
    }

    private boolean handleDrop(CommandSender sender, String[] args) {
        if (!this.plugin.isPremium()) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("premium-only"));
            return true;
        }
        if (!sender.hasPermission("soapsquest.drop")) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cUsage: /sq drop <questId> [x y z [world]] | /sq drop entity <entity|uuid> <questId>"));
            return true;
        }

        String questId;
        Location dropLocation;
        if ("entity".equalsIgnoreCase(args[1])) {
            if (args.length < 4) {
                sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cUsage: /sq drop entity <entity|uuid> <questId>"));
                return true;
            }
            Entity entity = Bukkit.getPlayer(args[2]);
            if (entity == null) {
                try {
                    entity = Bukkit.getEntity(UUID.fromString(args[2]));
                }
                catch (IllegalArgumentException ignored) {
                }
            }
            if (entity == null) {
                sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cEntity not found: " + args[2]));
                return true;
            }
            dropLocation = entity.getLocation();
            questId = args[3];
        } else {
            questId = args[1];
            if (args.length >= 5) {
                double x;
                double y;
                double z;
                try {
                    x = Double.parseDouble(args[2]);
                    y = Double.parseDouble(args[3]);
                    z = Double.parseDouble(args[4]);
                }
                catch (NumberFormatException e) {
                    sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cCoordinates must be numbers."));
                    return true;
                }
                World world;
                if (args.length >= 6) {
                    world = Bukkit.getWorld(args[5]);
                } else if (sender instanceof Player player) {
                    world = player.getWorld();
                } else {
                    sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cConsole must specify a world: /sq drop <questId> <x> <y> <z> <world>"));
                    return true;
                }
                if (world == null) {
                    sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cWorld not found."));
                    return true;
                }
                dropLocation = new Location(world, x, y, z);
            } else if (sender instanceof Player player) {
                dropLocation = player.getLocation();
            } else {
                sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cConsole must provide coordinates."));
                return true;
            }
        }

        Quest quest = this.plugin.getQuestManager().getQuest(questId);
        if (quest == null) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("quest-not-found", Map.of("quest", questId)));
            return true;
        }
        ItemStack droppedPaper = QuestPaper.createUnboundQuestPaper(quest, this.plugin.getMessageManager(), this.plugin.getQuestIdKey(), quest.getCustomLore());
        dropLocation.getWorld().dropItemNaturally(dropLocation, droppedPaper);
        sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&aDropped quest paper for '&f" + quest.getDisplay() + "&a' at the target location."));
        return true;
    }
}

