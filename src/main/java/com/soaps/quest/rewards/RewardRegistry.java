/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.inventory.ItemStack
 */
package com.soaps.quest.rewards;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.rewards.QuestReward;
import com.soaps.quest.rewards.RewardType;
import com.soaps.quest.rewards.types.CommandQuestReward;
import com.soaps.quest.rewards.types.ItemQuestReward;
import com.soaps.quest.rewards.types.MoneyQuestReward;
import com.soaps.quest.rewards.types.QuestQuestReward;
import com.soaps.quest.rewards.types.XPQuestReward;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class RewardRegistry {
    private static final Map<String, RewardType> types = new HashMap<String, RewardType>();
    private static boolean initialized = false;

    public static void registerType(RewardType type) {
        types.put(type.getId().toLowerCase(), type);
    }

    public static RewardType getType(String id) {
        return types.get(id.toLowerCase());
    }

    public static Collection<RewardType> getAllTypes() {
        return types.values();
    }

    public static boolean isTypeRegistered(String id) {
        return types.containsKey(id.toLowerCase());
    }

    public static QuestReward deserialize(ConfigurationSection section, SoapsQuest plugin) {
        if (section == null) {
            return null;
        }
        String type = section.getString("type");
        if (type == null) {
            plugin.getLogger().log(Level.WARNING, "Reward missing type field");
            return null;
        }
        try {
            return RewardRegistry.deserializeByType(type, section, plugin);
        }
        catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to deserialize reward: " + type, e);
            return null;
        }
    }

    private static QuestReward deserializeByType(String type, ConfigurationSection section, SoapsQuest plugin) {
        return switch (type.toLowerCase()) {
            case "xp" -> XPQuestReward.deserialize(section);
            case "money" -> MoneyQuestReward.deserialize(section, plugin);
            case "item" -> ItemQuestReward.deserialize(section);
            case "command" -> CommandQuestReward.deserialize(section);
            case "quest" -> QuestQuestReward.deserialize(section, plugin);
            default -> null;
        };
    }

    public static void initialize(SoapsQuest plugin) {
        if (initialized) {
            return;
        }
        RewardRegistry.registerType(new RewardType("xp", "XP Reward", "Grant experience points", Material.EXPERIENCE_BOTTLE, "Enter amount of XP to reward (e.g., 100):", input -> {
            try {
                int amount = Integer.parseInt(input.trim());
                if (amount <= 0) {
                    return null;
                }
                return new XPQuestReward(amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        RewardRegistry.registerType(new RewardType("money", "Money Reward", "Grant money (requires Vault)", Material.GOLD_INGOT, "Enter amount to reward ($) (e.g., 100.50):", input -> {
            try {
                double amount = Double.parseDouble(input.trim());
                if (amount <= 0.0) {
                    return null;
                }
                return new MoneyQuestReward(plugin, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        RewardRegistry.registerType(new RewardType("item", "Item Reward", "Grant items to player", Material.CHEST, "Enter item in format: MATERIAL AMOUNT (e.g., DIAMOND 5):", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                Material material = Material.valueOf((String)parts[0].toUpperCase());
                int amount = Integer.parseInt(parts[1]);
                if (amount <= 0 || amount > 64) {
                    return null;
                }
                return new ItemQuestReward(material, amount);
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }));
        RewardRegistry.registerType(new RewardType("command", "Command Reward", "Execute a command (use <player> for player name)", Material.COMMAND_BLOCK, "Enter command to execute (e.g., give <player> diamond 1):", input -> {
            String command = input.trim();
            if (command.isEmpty()) {
                return null;
            }
            if (command.startsWith("/")) {
                command = command.substring(1);
            }
            return new CommandQuestReward(command);
        }));
        RewardRegistry.registerType(new RewardType("quest", "Quest Reward", "Grant another quest paper as a reward", Material.WRITABLE_BOOK, "Enter the quest ID to reward (e.g., blacksmith_challenge):", input -> {
            String questId = input.trim();
            if (questId.isEmpty()) {
                return null;
            }
            if (!plugin.getQuestManager().questExists(questId)) {
                return null;
            }
            return new QuestQuestReward(plugin, questId);
        }));
        initialized = true;
        plugin.debugLog(Level.INFO, "Registered {0} reward types", types.size());
    }

    public static ItemQuestReward createFromHeldItem(ItemStack heldItem) {
        if (heldItem == null || heldItem.getType() == Material.AIR) {
            return null;
        }
        return new ItemQuestReward(heldItem);
    }

    public static void clear() {
        types.clear();
        initialized = false;
    }
}

