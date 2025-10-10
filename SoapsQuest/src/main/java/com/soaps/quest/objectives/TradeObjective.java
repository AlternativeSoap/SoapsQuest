package com.soaps.quest.objectives;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

/**
 * Objective that tracks trading with villagers.
 * Can track specific items traded or any trade.
 */
public class TradeObjective extends AbstractObjective {
    
    private final Material targetItem;
    
    public TradeObjective(String objectiveId, Material targetItem, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetItem = targetItem;
    }
    
    public TradeObjective(String objectiveId, Material targetItem, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetItem = targetItem;
    }
    
    public Material getTargetItem() {
        return targetItem;
    }
    
    @Override
    public String getType() {
        return "trade";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof InventoryClickEvent clickEvent)) {
            return false;
        }
        
        // Check if this is a merchant/villager inventory
        Inventory inventory = clickEvent.getInventory();
        if (!(inventory instanceof MerchantInventory)) {
            return false;
        }
        
        // Check if player clicked the result slot (slot 2 in merchant inventory)
        if (clickEvent.getRawSlot() != 2) {
            return false;
        }
        
        ItemStack result = clickEvent.getCurrentItem();
        if (result == null || result.getType() == Material.AIR) {
            return false;
        }
        
        // Check if the traded item matches our target (or if target is null, accept any)
        if (targetItem != null && result.getType() != targetItem) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        incrementProgress(playerId, result.getAmount());
        
        return true;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        String itemName = targetItem != null ? targetItem.name() : "TRADES";
        return current + "/" + getRequiredAmount() + " " + itemName;
    }
    
    @Override
    public String getDescription() {
        String itemName = targetItem != null ? targetItem.name() : "any items";
        return "Trade " + getRequiredAmount() + " " + itemName;
    }
    
    @Override
    public String serialize() {
        String item = targetItem != null ? targetItem.name() : "ANY";
        return getType() + ":" + item + ":" + getRequiredAmount();
    }
    
    /**
     * Deserialize a TradeObjective from a string.
     * Format: trade:MATERIAL:amount (use "ANY" for any trade)
     */
    public static TradeObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid trade objective data: " + data);
        }
        
        Material material = parts[1].equalsIgnoreCase("ANY") ? null : Material.valueOf(parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        
        return new TradeObjective(objectiveId, material, amount);
    }
}
