/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.TextComponent$Builder
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 */
package com.soaps.quest.commands;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.managers.QuestGeneratorService;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.utils.ColorUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class GenerateCommand
implements CommandExecutor,
TabCompleter {
    private final SoapsQuest plugin;
    private static final String PERM_GENERATE = "soapsquest.generate";
    private static final String PERM_BYPASS_COOLDOWN = "soapsquest.generate.bypass-cooldown";

    public GenerateCommand(SoapsQuest plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERM_GENERATE)) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        QuestGeneratorService service = this.plugin.getQuestGeneratorService();
        if (service == null) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("premium-only"));
            return true;
        }
        if (!service.isEnabled()) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("random-generator-disabled"));
            return true;
        }
        return this.handleSelfGeneration(sender, args, service);
    }

    private boolean handleSelfGeneration(CommandSender sender, String[] args, QuestGeneratorService service) {
        String type;
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("player-only"));
            return true;
        }
        Player player = (Player)sender;
        String string = type = args.length >= 2 ? args[1].toLowerCase() : null;
        if (type != null && !service.getAllowedTypes().contains(type)) {
            player.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cInvalid quest type! Allowed: " + String.join((CharSequence)", ", service.getAllowedTypes())));
            return true;
        }
        int maxBatch = service.getMaxBatchGenerate();
        int count = 1;
        if (args.length >= 3) {
            try {
                count = Integer.parseInt(args[2]);
            }
            catch (NumberFormatException e) {
                player.sendMessage(this.plugin.getMessageManager().getMessage("random-generator-batch-invalid-count", Map.of("max", String.valueOf(maxBatch))));
                return true;
            }
            if (count < 1 || count > maxBatch) {
                String key = count > maxBatch ? "random-generator-batch-too-many" : "random-generator-batch-invalid-count";
                player.sendMessage(this.plugin.getMessageManager().getMessage(key, Map.of("max", String.valueOf(maxBatch))));
                return true;
            }
        }
        if (!player.hasPermission(PERM_BYPASS_COOLDOWN) && service.isOnCooldown(player.getUniqueId())) {
            long remaining = service.getCooldownRemaining(player.getUniqueId());
            player.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cYou must wait &f" + remaining + "s &cbefore generating another quest."));
            return true;
        }
        return count == 1 ? this.handleSingleGeneration(player, type, service) : this.handleBatchGeneration(player, type, count, service);
    }

    private boolean handleSingleGeneration(Player player, String type, QuestGeneratorService service) {
        String questId;
        String string = questId = type != null ? service.generateQuest(type) : service.generateQuest();
        if (questId == null) {
            player.sendMessage(this.plugin.getMessageManager().getMessage("random-generator-error"));
            return true;
        }
        Quest quest = this.plugin.getQuestManager().getQuest(questId);
        if (quest == null) {
            player.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cError: Quest was generated but could not be loaded. Check server logs."));
            return true;
        }
        service.startCooldown(player.getUniqueId());
        this.plugin.getQuestLogger().logQuestGeneration(questId, type != null ? type : "random", quest.getTier() != null ? quest.getTier() : "unknown", quest.getDifficulty());
        player.sendMessage((Component)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(ColorUtil.colorize("&aSuccessfully generated quest: "))).append(ColorUtil.colorize(quest.getDisplay()))).build());
        player.sendMessage(ColorUtil.colorize("&7Quest ID: &f" + questId));
        player.sendMessage(ColorUtil.colorize("&7Use &f/sq give <player> " + questId + " &7to distribute this quest."));
        player.sendMessage(ColorUtil.colorize("&e\u26a0 &7Run &f/sq reload &7to update lore and rewards on existing papers."));
        return true;
    }

    private boolean handleBatchGeneration(Player player, String type, int count, QuestGeneratorService service) {
        int generated = 0;
        int failed = 0;
        String displayType = type != null ? type : "random";
        for (int i = 0; i < count; ++i) {
            String questId;
            String string = questId = type != null ? service.generateQuest(type) : service.generateQuest();
            if (questId != null) {
                Quest quest = this.plugin.getQuestManager().getQuest(questId);
                if (quest != null) {
                    ++generated;
                    this.plugin.getQuestLogger().logQuestGeneration(questId, displayType, quest.getTier() != null ? quest.getTier() : "unknown", quest.getDifficulty());
                    continue;
                }
                ++failed;
                continue;
            }
            ++failed;
        }
        service.startCooldown(player.getUniqueId());
        if (failed > 0) {
            player.sendMessage(this.plugin.getMessageManager().getMessage("random-generator-batch-success", Map.of("count", String.valueOf(generated), "type", displayType, "failed", String.valueOf(failed))));
        } else {
            player.sendMessage(this.plugin.getMessageManager().getMessage("random-generator-batch-complete", Map.of("count", String.valueOf(generated), "type", displayType)));
        }
        if (generated > 0) {
            player.sendMessage(ColorUtil.colorize("&7Use &f/sq editor &7to browse and manage generated quests."));
            player.sendMessage(ColorUtil.colorize("&e\u26a0 &7Run &f/sq reload &7to update lore and rewards on existing papers."));
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        QuestGeneratorService service = this.plugin.getQuestGeneratorService();
        if (service == null) {
            return List.of();
        }
        if (args.length == 2) {
            String input = args[1].toLowerCase();
            return service.getAllowedTypes().stream().filter(t -> t.startsWith(input)).toList();
        }
        if (args.length == 3) {
            int maxBatch = service.getMaxBatchGenerate();
            String input = args[2];
            ArrayList<String> suggestions = new ArrayList<String>();
            for (int n : new int[]{1, 5, 10, 25}) {
                if (n > maxBatch || !String.valueOf(n).startsWith(input)) continue;
                suggestions.add(String.valueOf(n));
            }
            return suggestions;
        }
        return List.of();
    }

    public void reload() {
    }
}

