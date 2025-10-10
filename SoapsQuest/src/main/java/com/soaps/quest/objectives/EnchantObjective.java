package com.soaps.quest.objectives;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.enchantment.EnchantItemEvent;

/**
 * Objective that tracks item enchanting.
 * Can track specific items or any enchantment.
 */
public class EnchantObjective extends AbstractObjective {
    
    private final Material targetItem;
    
    public EnchantObjective(String objectiveId, Material targetItem, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetItem = targetItem;
    }
    
    public EnchantObjective(String objectiveId, Material targetItem, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetItem = targetItem;
    }
    
    public Material getTargetItem() {
        return targetItem;
    }
    
    @Override
    public String getType() {
        return "enchant";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof EnchantItemEvent enchantEvent)) {
            return false;
        }
        
        // Check if the enchanted item matches our target (or if target is null, accept any)
        if (targetItem != null && enchantEvent.getItem().getType() != targetItem) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        incrementProgress(playerId);
        
        return true;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        String itemName = targetItem != null ? targetItem.name() : "ITEMS";
        return current + "/" + getRequiredAmount() + " " + itemName;
    }
    
    @Override
    public String getDescription() {
        String itemName = targetItem != null ? targetItem.name() : "any items";
        return "Enchant " + getRequiredAmount() + " " + itemName;
    }
    
    @Override
    public String serialize() {
        String item = targetItem != null ? targetItem.name() : "ANY";
        return getType() + ":" + item + ":" + getRequiredAmount();
    }
    
    /**
     * Deserialize an EnchantObjective from a string.
     * Format: enchant:MATERIAL:amount (use "ANY" for any item)
     */
    public static EnchantObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid enchant objective data: " + data);
        }
        
        Material material = parts[1].equalsIgnoreCase("ANY") ? null : Material.valueOf(parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        
        return new EnchantObjective(objectiveId, material, amount);
    }
}
