package com.soaps.quest.conditions;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.quests.Quest;

import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;

public class ConditionChecker {
    
    private final SoapsQuest plugin;
    private final Economy economy;
    private final boolean vaultEnabled;
    private final boolean placeholderAPIEnabled;
    
    public ConditionChecker(SoapsQuest plugin) {
        this.plugin = plugin;
        this.vaultEnabled = plugin.getServer().getPluginManager().getPlugin("Vault") != null;
        this.placeholderAPIEnabled = plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
        
        if (vaultEnabled) {
            var reg = plugin.getServer().getServicesManager().getRegistration(Economy.class);
            this.economy = reg != null ? reg.getProvider() : null;
        } else {
            this.economy = null;
        }
    }
    
    public ConditionResult checkConditions(Player player, Quest quest, ConfigurationSection conditions, boolean consumeResources) {
        if (conditions == null) {
            return ConditionResult.success();
        }
        
        // 1. min-level
        if (conditions.contains("min-level")) {
            int minLevel = conditions.getInt("min-level");
            if (player.getLevel() < minLevel) {
                return ConditionResult.failure(String.format("&cYou need level %d+! (Currently: %d)", minLevel, player.getLevel()));
            }
        }
        
        // 2. max-level
        if (conditions.contains("max-level")) {
            int maxLevel = conditions.getInt("max-level");
            if (player.getLevel() > maxLevel) {
                return ConditionResult.failure(String.format("&cYou must be level %d or below! (Currently: %d)", maxLevel, player.getLevel()));
            }
        }
        
        // 3. permission
        if (conditions.contains("permission")) {
            String permission = conditions.getString("permission");
            if (permission != null && !player.hasPermission(permission)) {
                return ConditionResult.failure("&cYou don't have permission for this quest!");
            }
        }
        
        // 4. world
        if (conditions.contains("world")) {
            List<String> allowedWorlds = conditions.getStringList("world");
            if (!allowedWorlds.isEmpty() && !allowedWorlds.contains(player.getWorld().getName())) {
                return ConditionResult.failure("&cYou must be in the correct world!");
            }
        }
        
        // 5. active-limit
        if (conditions.contains("active-limit")) {
            int limit = conditions.getInt("active-limit");
            int activeCount = plugin.getDataManager().getActiveQuests(player).size();
            if (activeCount >= limit) {
                return ConditionResult.failure(String.format("&cYou can only have %d active quests!", limit));
            }
        }
        
        // 6. min-money
        if (vaultEnabled && economy != null && conditions.contains("min-money")) {
            double minMoney = conditions.getDouble("min-money");
            double balance = economy.getBalance(player);
            if (balance < minMoney) {
                return ConditionResult.failure(String.format("&cYou need $%.2f! (You have: $%.2f)", minMoney, balance));
            }
        }
        
        // 7. cost (locking condition - consumes money)
        if (vaultEnabled && economy != null && conditions.contains("cost")) {
            double cost = conditions.getDouble("cost");
            double balance = economy.getBalance(player);
            if (balance < cost) {
                return ConditionResult.failure(String.format("&cThis quest costs $%.2f!", cost));
            }
            
            if (consumeResources) {
                economy.withdrawPlayer(player, cost);
            }
        }
        
        // 8. item (required item in inventory)
        if (conditions.contains("item")) {
            String itemStr = conditions.getString("item");
            if (itemStr != null && itemStr.contains(":")) {
                String[] parts = itemStr.split(":");
                try {
                    Material material = Material.valueOf(parts[0].toUpperCase());
                    int required = Integer.parseInt(parts[1]);
                    
                    int count = 0;
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (item != null && item.getType() == material) {
                            count += item.getAmount();
                        }
                    }
                    
                    if (count < required) {
                        return ConditionResult.failure(String.format("&cYou need %d %s!", required, material.name().toLowerCase()));
                    }
                    
                    // 9. consume-item (locking condition - consumes items)
                    if (conditions.getBoolean("consume-item", false) && consumeResources) {
                        int remaining = required;
                        for (ItemStack item : player.getInventory().getContents()) {
                            if (item != null && item.getType() == material && remaining > 0) {
                                int toRemove = Math.min(item.getAmount(), remaining);
                                item.setAmount(item.getAmount() - toRemove);
                                remaining -= toRemove;
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    plugin.getLogger().log(Level.WARNING, "Invalid item condition: {0}", itemStr);
                }
            }
        }
        
        // 10. time
        if (conditions.contains("time")) {
            String timeStr = conditions.getString("time");
            if (timeStr != null) {
                long worldTime = player.getWorld().getTime();
                boolean isDay = worldTime >= 0 && worldTime < 13000;
                
                if (timeStr.equalsIgnoreCase("DAY") && !isDay) {
                    return ConditionResult.failure("&cThis quest is only available during the day!");
                } else if (timeStr.equalsIgnoreCase("NIGHT") && isDay) {
                    return ConditionResult.failure("&cThis quest is only available at night!");
                }
            }
        }
        
        // 11. placeholder (checked during progress)
        if (placeholderAPIEnabled && conditions.contains("placeholder")) {
            String expression = conditions.getString("placeholder");
            if (expression != null) {
                try {
                    if (!evaluatePlaceholder(player, expression)) {
                        return ConditionResult.failure("&cYou don't meet the requirements!");
                    }
                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Invalid placeholder: {0}", expression);
                }
            }
        }
        
        // 12. gamemode (checked during progress)
        if (conditions.contains("gamemode")) {
            List<String> allowedModes = conditions.getStringList("gamemode");
            if (!allowedModes.isEmpty() && !allowedModes.contains(player.getGameMode().name())) {
                return ConditionResult.failure("&cWrong gamemode for this quest!");
            }
        }
        
        return ConditionResult.success();
    }
    
    private boolean evaluatePlaceholder(Player player, String expression) {
        expression = PlaceholderAPI.setPlaceholders(player, expression);
        
        for (String op : new String[]{">=", "<=", "==", "!=", ">", "<"}) {
            if (expression.contains(op)) {
                String[] parts = expression.split(op);
                if (parts.length == 2) {
                    try {
                        double left = Double.parseDouble(parts[0].trim());
                        double right = Double.parseDouble(parts[1].trim());
                        
                        return switch (op) {
                            case ">=" -> left >= right;
                            case "<=" -> left <= right;
                            case "==" -> left == right;
                            case "!=" -> left != right;
                            case ">" -> left > right;
                            case "<" -> left < right;
                            default -> false;
                        };
                    } catch (NumberFormatException e) {
                        String left = parts[0].trim();
                        String right = parts[1].trim();
                        return op.equals("==") ? left.equals(right) : !left.equals(right);
                    }
                }
            }
        }
        return false;
    }
}
