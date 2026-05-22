/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.soaps.quest.commands;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.managers.CustomItemManager;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.rewards.RewardEntry;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QuestRewardSubcommand {
    private final SoapsQuest plugin;

    public QuestRewardSubcommand(SoapsQuest plugin) {
        this.plugin = plugin;
    }

    public boolean handleListReward(CommandSender sender, String[] args) {
        if (!sender.hasPermission("soapsquest.admin")) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("usage-listreward"));
            return true;
        }
        String questId = args[1];
        Quest quest = this.plugin.getQuestManager().getQuest(questId);
        if (quest == null) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("quest-not-found", Map.of("quest", questId)));
            return true;
        }
        List<RewardEntry> rewards = this.plugin.getRewardManager().getRewardList(questId);
        if (rewards.isEmpty()) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("rewards-empty", Map.of("quest", questId)));
            return true;
        }
        sender.sendMessage(this.plugin.getMessageManager().getMessage("rewards-header", Map.of("quest", questId)));
        for (int i = 0; i < rewards.size(); ++i) {
            RewardEntry reward = rewards.get(i);
            sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&8[&a" + (i + 1) + "&8] &f" + reward.getDisplayString()));
        }
        return true;
    }

    public boolean handleAddReward(CommandSender sender, String[] args) {
        if (!sender.hasPermission("soapsquest.addreward")) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("usage-addreward"));
            return true;
        }
        String questId = args[1];
        String rewardType = args[2].toLowerCase();
        if (!this.plugin.getQuestManager().questExists(questId)) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("quest-not-found", Map.of("quest", questId)));
            return true;
        }
        switch (rewardType) {
            case "item": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(this.plugin.getMessageManager().getMessage("player-only"));
                    return true;
                }
                Player player = (Player)sender;
                this.plugin.getGuiManager().getChatInputManager().requestInput(player, "&eEnter item to add:\n&7- Type &fHAND &7to use the item you're holding\n&7- Type &f<material> <amount> &7(e.g., &fDIAMOND_SWORD 1&7)\n&7- Type &f<namespace:id> &7(e.g., &fmmoitems:SWORD:DRAGON_SLAYER&7)", input -> {
                    try {
                        this.handleItemRewardInput(player, questId, (String)input);
                    }
                    catch (Exception e) {
                        player.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cError adding item reward: " + e.getMessage()));
                        this.plugin.getLogger().log(Level.WARNING, "Error adding item reward: {0}", e.getMessage());
                    }
                });
                break;
            }
            case "xp": {
                if (args.length < 4) {
                    sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cUsage: /sq addreward <quest> xp <amount>"));
                    return true;
                }
                try {
                    int amount = Integer.parseInt(args[3]);
                    this.plugin.getRewardManager().addXPReward(questId, amount);
                    sender.sendMessage(this.plugin.getMessageManager().getMessage("reward-added", Map.of("quest", questId)));
                }
                catch (NumberFormatException e) {
                    sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cInvalid XP amount: " + args[3]));
                }
                break;
            }
            case "money": {
                if (args.length < 4) {
                    sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cUsage: /sq addreward <quest> money <amount>"));
                    return true;
                }
                try {
                    double amount = Double.parseDouble(args[3]);
                    this.plugin.getRewardManager().addMoneyReward(questId, amount);
                    sender.sendMessage(this.plugin.getMessageManager().getMessage("reward-added", Map.of("quest", questId)));
                }
                catch (NumberFormatException e) {
                    sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cInvalid money amount: " + args[3]));
                }
                break;
            }
            case "sigils": {
                if (!this.plugin.isPremium()) {
                    sender.sendMessage(this.plugin.getMessageManager().getMessage("premium-only"));
                    return true;
                }
                if (args.length < 4) {
                    sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cUsage: /sq addreward <quest> sigils <amount>"));
                    return true;
                }
                try {
                    double amount = Double.parseDouble(args[3]);
                    this.plugin.getRewardManager().addSigilReward(questId, amount);
                    sender.sendMessage(this.plugin.getMessageManager().getMessage("reward-added", Map.of("quest", questId)));
                }
                catch (NumberFormatException e) {
                    sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cInvalid sigil amount: " + args[3]));
                }
                break;
            }
            case "command": {
                if (args.length < 4) {
                    sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cUsage: /sq addreward <quest> command <command>"));
                    return true;
                }
                StringBuilder commandBuilder = new StringBuilder();
                for (int i = 3; i < args.length; ++i) {
                    if (i > 3) {
                        commandBuilder.append(" ");
                    }
                    commandBuilder.append(args[i]);
                }
                this.plugin.getRewardManager().addCommandReward(questId, commandBuilder.toString());
                sender.sendMessage(this.plugin.getMessageManager().getMessage("reward-added", Map.of("quest", questId)));
                break;
            }
            default: {
                sender.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cInvalid reward type. Use: &7item&c, &7xp&c, &7money&c, &7sigils&c, or &7command&c."));
            }
        }
        return true;
    }

    public boolean handleRemoveReward(CommandSender sender, String[] args) {
        int index;
        if (!sender.hasPermission("soapsquest.removereward")) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("usage-removereward"));
            return true;
        }
        String questId = args[1];
        if (!this.plugin.getQuestManager().questExists(questId)) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("quest-not-found", Map.of("quest", questId)));
            return true;
        }
        try {
            index = Integer.parseInt(args[2]);
        }
        catch (NumberFormatException e) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("reward-index-invalid"));
            return true;
        }
        if (this.plugin.getRewardManager().removeReward(questId, index)) {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("reward-removed", Map.of("quest", questId)));
        } else {
            sender.sendMessage(this.plugin.getMessageManager().getMessage("reward-index-invalid"));
        }
        return true;
    }

    private void handleItemRewardInput(Player player, String questId, String input) {
        ItemStack itemToAdd;
        String[] parts = input.trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            player.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cInvalid input. Please try again."));
            return;
        }
        String firstPart = parts[0].toUpperCase();
        if (firstPart.equals("HAND")) {
            ItemStack heldItem = player.getInventory().getItemInMainHand();
            if (heldItem.getType() == Material.AIR) {
                player.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cYou must be holding an item to use HAND."));
                return;
            }
            itemToAdd = heldItem.clone();
        } else if (input.contains(":")) {
            CustomItemManager customItemManager = this.plugin.getCustomItemManager();
            itemToAdd = customItemManager.parseCustomItem(input.trim());
            if (itemToAdd == null) {
                player.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cFailed to parse plugin item: &f" + input));
                player.sendMessage(this.plugin.getMessageManager().parseColorCodes("&7Make sure the plugin is installed and the item ID is correct."));
                return;
            }
        } else {
            try {
                Material material = Material.valueOf((String)firstPart);
                int amount = 1;
                if (parts.length >= 2) {
                    try {
                        amount = Integer.parseInt(parts[1]);
                    }
                    catch (NumberFormatException e) {
                        player.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cInvalid amount: &f" + parts[1]));
                        return;
                    }
                }
                itemToAdd = new ItemStack(material, amount);
            }
            catch (IllegalArgumentException e) {
                player.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cInvalid material: &f" + firstPart));
                player.sendMessage(this.plugin.getMessageManager().parseColorCodes("&7Use a valid material name or plugin item format."));
                return;
            }
        }
        boolean success = this.plugin.getRewardManager().addItemReward(questId, itemToAdd);
        if (success) {
            player.sendMessage(this.plugin.getMessageManager().getMessage("reward-added", Map.of("quest", questId)));
            player.sendMessage(this.plugin.getMessageManager().parseColorCodes("&aAdded: &f" + itemToAdd.getAmount() + "x " + (itemToAdd.hasItemMeta() && itemToAdd.getItemMeta().hasDisplayName() ? LegacyComponentSerializer.legacyAmpersand().serialize(itemToAdd.getItemMeta().displayName()) : itemToAdd.getType().name())));
        } else {
            player.sendMessage(this.plugin.getMessageManager().parseColorCodes("&cFailed to add item reward. Check console for errors."));
        }
    }
}

