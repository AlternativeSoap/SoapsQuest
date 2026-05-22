/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.clip.placeholderapi.PlaceholderAPI
 *  net.milkbowl.vault.economy.Economy
 *  org.bukkit.GameMode
 *  org.bukkit.Material
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.RegisteredServiceProvider
 */
package com.soaps.quest.conditions;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.conditions.ConditionResult;
import com.soaps.quest.conditions.ConditionType;
import com.soaps.quest.conditions.QuestCondition;
import com.soaps.quest.conditions.types.ActiveLimitCondition;
import com.soaps.quest.conditions.types.CompletedQuestsCondition;
import com.soaps.quest.conditions.types.GamemodeRequirementCondition;
import com.soaps.quest.conditions.types.ItemRequirementCondition;
import com.soaps.quest.conditions.types.LevelRequirementCondition;
import com.soaps.quest.conditions.types.MaxLevelRequirementCondition;
import com.soaps.quest.conditions.types.MoneyRequirementCondition;
import com.soaps.quest.conditions.types.PermissionRequirementCondition;
import com.soaps.quest.conditions.types.PlaceholderCondition;
import com.soaps.quest.conditions.types.QuestCompletionCondition;
import com.soaps.quest.conditions.types.TimeRequirementCondition;
import com.soaps.quest.conditions.types.WorldRequirementCondition;
import com.soaps.quest.quests.Quest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

public class ConditionRegistry {
    private static final Map<String, ConditionType> types = new HashMap<String, ConditionType>();
    private static boolean initialized = false;
    private static SoapsQuest plugin;
    private static Economy economy;
    private static boolean vaultEnabled;
    private static boolean placeholderAPIEnabled;

    public static void register(String id, ConditionType type) {
        types.put(id.toLowerCase(), type);
    }

    public static ConditionType getType(String id) {
        return types.get(id.toLowerCase());
    }

    public static Collection<ConditionType> getAllTypes() {
        return types.values();
    }

    public static boolean isRegistered(String id) {
        return types.containsKey(id.toLowerCase());
    }

    public static QuestCondition deserialize(ConfigurationSection section, SoapsQuest plugin) {
        if (section == null) {
            return null;
        }
        String type = section.getString("type");
        if (type == null) {
            plugin.getLogger().log(Level.WARNING, "Condition missing type field");
            return null;
        }
        ConditionType conditionType = ConditionRegistry.getType(type);
        if (conditionType == null) {
            plugin.getLogger().log(Level.WARNING, "Unknown condition type: {0}", type);
            return null;
        }
        try {
            return ConditionRegistry.deserializeByType(type, section, plugin);
        }
        catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to deserialize condition: " + type, e);
            return null;
        }
    }

    private static QuestCondition deserializeByType(String type, ConfigurationSection section, SoapsQuest plugin) {
        return switch (type.toLowerCase()) {
            case "level" -> LevelRequirementCondition.deserialize(section);
            case "max-level" -> MaxLevelRequirementCondition.deserialize(section);
            case "money" -> MoneyRequirementCondition.deserialize(section);
            case "money-cost" -> MoneyRequirementCondition.deserialize(section);
            case "sigils" -> MoneyRequirementCondition.deserialize(section);
            case "sigil-cost" -> MoneyRequirementCondition.deserialize(section);
            case "permission" -> PermissionRequirementCondition.deserialize(section);
            case "world" -> WorldRequirementCondition.deserialize(section);
            case "active-limit" -> ActiveLimitCondition.deserialize(section, plugin);
            case "item", "item-cost" -> ItemRequirementCondition.deserialize(section);
            case "time" -> TimeRequirementCondition.deserialize(section);
            case "gamemode" -> GamemodeRequirementCondition.deserialize(section);
            case "placeholder" -> PlaceholderCondition.deserialize(section);
            case "require-completed-quests" -> CompletedQuestsCondition.deserialize(section, plugin);
            case "require-quest-completed" -> QuestCompletionCondition.deserialize(section, plugin);
            default -> null;
        };
    }

    public static ConditionResult checkConditions(Player player, Quest quest, ConfigurationSection conditions, boolean consumeResources) {
        List allowedModes;
        String expression;
        String timeStr;
        String itemStr;
        double balance;
        List allowedWorlds;
        String permission;
        if (conditions == null) {
            return ConditionResult.success();
        }
        if (conditions.contains("min-level")) {
            int minLevel = conditions.getInt("min-level");
            if (player.getLevel() < minLevel) {
                return ConditionResult.failure(String.format("&cYou need level %d+! (Currently: %d)", minLevel, player.getLevel()));
            }
        }
        if (conditions.contains("max-level")) {
            int maxLevel = conditions.getInt("max-level");
            if (player.getLevel() > maxLevel) {
                return ConditionResult.failure(String.format("&cYou must be level %d or below! (Currently: %d)", maxLevel, player.getLevel()));
            }
        }
        if (conditions.contains("permission") && (permission = conditions.getString("permission")) != null && !player.hasPermission(permission)) {
            return ConditionResult.failure("&cYou don't have permission for this quest!");
        }
        if (conditions.contains("world") && !(allowedWorlds = conditions.getStringList("world")).isEmpty() && !allowedWorlds.contains(player.getWorld().getName())) {
            return ConditionResult.failure("&cYou must be in the correct world!");
        }
        if (conditions.contains("active-limit")) {
            int limit = conditions.getInt("active-limit");
            if (!plugin.getQuestManager().isWithinActiveLimit(player, quest.getQuestId(), limit)) {
                int activeCount = plugin.getQuestManager().countQueueActiveQuestTypes(player);
                return ConditionResult.failure(String.format("&cYou can only have %d active quest types! (Currently: %d)", limit, activeCount));
            }
        }
        if (conditions.contains("min-money")) {
            if (!vaultEnabled || economy == null) {
                return ConditionResult.failure("&cVault/Economy plugin required for this quest!");
            }
            double minMoney = conditions.getDouble("min-money");
            balance = economy.getBalance((OfflinePlayer)player);
            if (balance < minMoney) {
                return ConditionResult.failure(String.format("&cYou need $%.2f! (You have: $%.2f)", minMoney, balance));
            }
        }
        if (conditions.contains("cost")) {
            if (!vaultEnabled || economy == null) {
                return ConditionResult.failure("&cVault/Economy plugin required for this quest!");
            }
            double cost = conditions.getDouble("cost");
            balance = economy.getBalance((OfflinePlayer)player);
            if (balance < cost) {
                return ConditionResult.failure(String.format("&cThis quest costs $%.2f!", cost));
            }
            if (consumeResources) {
                economy.withdrawPlayer((OfflinePlayer)player, cost);
            }
        }
        if (conditions.contains("min-sigils")) {
            if (plugin.getSigilManager() == null) {
                return ConditionResult.failure("&cSigil system is unavailable!");
            }
            double requiredSigils = conditions.getDouble("min-sigils");
            double balanceSigils = plugin.getSigilManager().getBalance(player.getUniqueId());
            if (balanceSigils < requiredSigils) {
                return ConditionResult.failure(String.format("&cYou need %.2f sigils! (You have: %.2f)", requiredSigils, balanceSigils));
            }
        }
        if (conditions.contains("sigil-cost")) {
            if (plugin.getSigilManager() == null) {
                return ConditionResult.failure("&cSigil system is unavailable!");
            }
            double costSigils = conditions.getDouble("sigil-cost");
            double balanceSigils = plugin.getSigilManager().getBalance(player.getUniqueId());
            if (balanceSigils < costSigils) {
                return ConditionResult.failure(String.format("&cThis quest costs %.2f sigils!", costSigils));
            }
            if (consumeResources) {
                plugin.getSigilManager().take(player.getUniqueId(), costSigils);
            }
        }
        if (conditions.contains("item") && (itemStr = conditions.getString("item")) != null && itemStr.contains(":")) {
            String[] parts = itemStr.split(":");
            try {
                Material material = Material.valueOf((String)parts[0].toUpperCase());
                int required = Integer.parseInt(parts[1]);
                int count = 0;
                ItemStack[] contents = player.getInventory().getContents();
                if (contents != null) {
                    for (ItemStack item : contents) {
                        if (item == null || item.getType() != material) continue;
                        count += item.getAmount();
                    }
                }
                if (count < required) {
                    return ConditionResult.failure(String.format("&cYou need %d %s!", required, material.name().toLowerCase()));
                }
                if (conditions.getBoolean("consume-item", false) && consumeResources) {
                    int remaining = required;
                    ItemStack[] consumeContents = player.getInventory().getContents();
                    if (consumeContents != null) {
                        for (ItemStack item : consumeContents) {
                            if (item == null || item.getType() != material || remaining <= 0) continue;
                            int toRemove = Math.min(item.getAmount(), remaining);
                            item.setAmount(item.getAmount() - toRemove);
                            remaining -= toRemove;
                        }
                    }
                }
            }
            catch (NumberFormatException e) {
                plugin.getLogger().log(Level.WARNING, "Invalid item condition: {0}", itemStr);
            }
        }
        if (conditions.contains("time") && (timeStr = conditions.getString("time")) != null) {
            boolean isDay;
            long worldTime = player.getWorld().getTime();
            boolean bl = isDay = worldTime >= 0L && worldTime < 13000L;
            if (timeStr.equalsIgnoreCase("DAY") && !isDay) {
                return ConditionResult.failure("&cThis quest is only available during the day!");
            }
            if (timeStr.equalsIgnoreCase("NIGHT") && isDay) {
                return ConditionResult.failure("&cThis quest is only available at night!");
            }
        }
        if (placeholderAPIEnabled && conditions.contains("placeholder") && (expression = conditions.getString("placeholder")) != null) {
            try {
                if (!ConditionRegistry.evaluatePlaceholder(player, expression)) {
                    return ConditionResult.failure("&cYou don't meet the requirements!");
                }
            }
            catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Invalid placeholder: {0}", expression);
            }
        }
        if (conditions.contains("gamemode") && !(allowedModes = conditions.getStringList("gamemode")).isEmpty() && !allowedModes.contains(player.getGameMode().name())) {
            return ConditionResult.failure("&cWrong gamemode for this quest!");
        }
        if (conditions.contains("completed-quests")) {
            List<String> requiredQuests = conditions.getStringList("completed-quests");
            if (!requiredQuests.isEmpty()) {
                QuestCompletionCondition questCondition = new QuestCompletionCondition(requiredQuests, plugin);
                ConditionResult questResult = questCondition.check(player, consumeResources);
                if (!questResult.isSuccess()) {
                    return questResult;
                }
            }
        }
        return ConditionResult.success();
    }

    private static boolean evaluatePlaceholder(Player player, String expression) {
        expression = PlaceholderAPI.setPlaceholders((Player)player, (String)expression);
        for (String op : new String[]{">=", "<=", "==", "!=", ">", "<"}) {
            String[] parts;
            if (!expression.contains(op) || (parts = expression.split(op)).length != 2) continue;
            try {
                double left = Double.parseDouble(parts[0].trim());
                double right = Double.parseDouble(parts[1].trim());
                return switch (op) {
                    case ">=" -> {
                        if (left >= right) {
                            yield true;
                        }
                        yield false;
                    }
                    case "<=" -> {
                        if (left <= right) {
                            yield true;
                        }
                        yield false;
                    }
                    case "==" -> {
                        if (left == right) {
                            yield true;
                        }
                        yield false;
                    }
                    case "!=" -> {
                        if (left != right) {
                            yield true;
                        }
                        yield false;
                    }
                    case ">" -> {
                        if (left > right) {
                            yield true;
                        }
                        yield false;
                    }
                    case "<" -> {
                        if (left < right) {
                            yield true;
                        }
                        yield false;
                    }
                    default -> false;
                };
            }
            catch (NumberFormatException e) {
                String left = parts[0].trim();
                String right = parts[1].trim();
                return op.equals("==") ? left.equals(right) : !left.equals(right);
            }
        }
        return false;
    }

    public static void initialize(SoapsQuest plugin) {
        RegisteredServiceProvider reg;
        if (initialized) {
            return;
        }
        ConditionRegistry.plugin = plugin;
        vaultEnabled = plugin.getServer().getPluginManager().getPlugin("Vault") != null;
        boolean bl = placeholderAPIEnabled = plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
        economy = vaultEnabled ? ((reg = plugin.getServer().getServicesManager().getRegistration(Economy.class)) != null ? (Economy)reg.getProvider() : null) : null;
        ConditionRegistry.register("level", new ConditionType("level", "Level Requirement", "Requires player to have at least X levels", Material.EXPERIENCE_BOTTLE, "Enter minimum level:", input -> new LevelRequirementCondition(Integer.parseInt(input))));
        ConditionRegistry.register("money", new ConditionType("money", "Money Requirement", "Requires player to have at least $X", Material.GOLD_INGOT, "Enter minimum money (e.g., 100.50):", input -> new MoneyRequirementCondition(Double.parseDouble(input), false)));
        ConditionRegistry.register("money-cost", new ConditionType("money-cost", "Money Cost (Consumable)", "Costs $X to unlock (deducts money)", Material.EMERALD, "Enter cost (e.g., 50.00):", input -> new MoneyRequirementCondition(Double.parseDouble(input), true)));
        ConditionRegistry.register("sigils", new ConditionType("sigils", "Sigil Requirement", "Requires player to have at least X SoapsQuest Sigils", Material.GOLD_NUGGET, "Enter minimum sigils (e.g., 100):", input -> new MoneyRequirementCondition(Double.parseDouble(input), false)));
        ConditionRegistry.register("sigil-cost", new ConditionType("sigil-cost", "Sigil Cost (Consumable)", "Costs SoapsQuest Sigils to unlock", Material.RAW_GOLD, "Enter sigil cost (e.g., 50):", input -> new MoneyRequirementCondition(Double.parseDouble(input), true)));
        ConditionRegistry.register("permission", new ConditionType("permission", "Permission Requirement", "Requires specific permission node", Material.DIAMOND, "Enter permission node (e.g., quests.vip):", PermissionRequirementCondition::new));
        ConditionRegistry.register("world", new ConditionType("world", "World Requirement", "Must be in specific world(s)", Material.GRASS_BLOCK, "Enter world name(s) separated by comma:", input -> {
            String[] worlds = input.split(",");
            for (int i = 0; i < worlds.length; ++i) {
                worlds[i] = worlds[i].trim();
            }
            return new WorldRequirementCondition(Arrays.asList(worlds));
        }));
        ConditionRegistry.register("max-level", new ConditionType("max-level", "Max Level Requirement", "Requires player to be at or below X levels", Material.GLASS_BOTTLE, "Enter maximum level:", input -> new MaxLevelRequirementCondition(Integer.parseInt(input))));
        ConditionRegistry.register("active-limit", new ConditionType("active-limit", "Active Quest Limit", "Limits number of active quests", Material.WRITABLE_BOOK, "Enter max active quests:", input -> new ActiveLimitCondition(Integer.parseInt(input), plugin)));
        ConditionRegistry.register("item", new ConditionType("item", "Item Requirement", "Requires specific items in inventory", Material.CHEST, "Enter item and amount (e.g., DIAMOND 5):", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            Material material = Material.valueOf((String)parts[0].toUpperCase());
            int amount = Integer.parseInt(parts[1]);
            return new ItemRequirementCondition(material, amount, false);
        }));
        ConditionRegistry.register("item-cost", new ConditionType("item-cost", "Item Cost (Consumable)", "Costs items to unlock (consumes items)", Material.HOPPER, "Enter item and amount (e.g., EMERALD 10):", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            Material material = Material.valueOf((String)parts[0].toUpperCase());
            int amount = Integer.parseInt(parts[1]);
            return new ItemRequirementCondition(material, amount, true);
        }));
        ConditionRegistry.register("time", new ConditionType("time", "Time Requirement", "Requires specific time of day", Material.CLOCK, "Enter time (DAY or NIGHT):", input -> {
            TimeRequirementCondition.TimeType timeType;
            try {
                timeType = TimeRequirementCondition.TimeType.valueOf(input.trim().toUpperCase());
            }
            catch (IllegalArgumentException e) {
                timeType = TimeRequirementCondition.TimeType.ANY;
            }
            return new TimeRequirementCondition(timeType);
        }));
        ConditionRegistry.register("gamemode", new ConditionType("gamemode", "Gamemode Requirement", "Requires specific gamemode(s)", Material.COMMAND_BLOCK, "Enter gamemode(s) separated by comma (e.g., SURVIVAL,ADVENTURE):", input -> {
            String[] modeStrings = input.split(",");
            ArrayList<GameMode> modes = new ArrayList<GameMode>();
            for (String modeStr : modeStrings) {
                try {
                    GameMode mode = GameMode.valueOf((String)modeStr.trim().toUpperCase());
                    modes.add(mode);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
            }
            return new GamemodeRequirementCondition(modes);
        }));
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            ConditionRegistry.register("placeholder", new ConditionType("placeholder", "Placeholder Check", "Checks PlaceholderAPI expression", Material.NAME_TAG, "Enter placeholder expression (e.g., %level% >= 30):", PlaceholderCondition::new));
        }
        ConditionRegistry.register("require-completed-quests", new ConditionType("require-completed-quests", "Completed Quests Requirement", "Requires X total quests completed", Material.BOOK, "Enter minimum completed quests (e.g., 5):", input -> new CompletedQuestsCondition(Integer.parseInt(input), plugin)));
        ConditionRegistry.register("require-quest-completed", new ConditionType("require-quest-completed", "Specific Quest Requirement", "Requires specific quest(s) completed", Material.WRITTEN_BOOK, "Enter quest ID(s) separated by comma:", input -> {
            String[] questIds = input.split(",");
            ArrayList<String> quests = new ArrayList<String>();
            for (String id : questIds) {
                quests.add(id.trim());
            }
            return new QuestCompletionCondition(quests, plugin);
        }));
        initialized = true;
        plugin.debugLog(Level.INFO, "Registered {0} condition types", types.size());
    }

    public static void clear() {
        types.clear();
        initialized = false;
    }
}

