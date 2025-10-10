package com.soaps.quest.objectives;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.CraftItemEvent;

/**
 * Objective that tracks item crafting.
 * Triggered when a player crafts a specific item.
 * Supports ANY to track crafting any item type.
 */
public class CraftObjective extends AbstractObjective {
    
    private final Material targetItem; // null means ANY item
    
    public CraftObjective(String objectiveId, Material targetItem, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetItem = targetItem;
    }
    
    public CraftObjective(String objectiveId, Material targetItem, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetItem = targetItem;
    }
    
    public Material getTargetItem() {
        return targetItem;
    }
    
    @Override
    public String getType() {
        return "craft";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof CraftItemEvent craftEvent)) {
            return false;
        }
        
        // Check if the crafted item matches our target
        // If targetItem is null, accept ANY item
        if (targetItem != null && craftEvent.getRecipe().getResult().getType() != targetItem) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        
        // Get the amount being crafted (considering shift-click)
        int craftAmount = craftEvent.getRecipe().getResult().getAmount();
        if (craftEvent.isShiftClick()) {
            // For shift-click, we need to calculate how many can be crafted
            // This is a simplified version - actual amount depends on inventory space
            craftAmount = Math.min(craftAmount * 64, getRequiredAmount() - getCurrentProgress(playerId));
        }
        
        // Increment progress
        incrementProgress(playerId, craftAmount);
        
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
        return "Craft " + getRequiredAmount() + " " + itemName;
    }
    
    @Override
    public String serialize() {
        String item = targetItem != null ? targetItem.name() : "ANY";
        return getType() + ":" + item + ":" + getRequiredAmount();
    }
    
    /**
     * Deserialize a CraftObjective from a string.
     * Format: craft:MATERIAL:amount (use "ANY" for any item)
     */
    public static CraftObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid craft objective data: " + data);
        }
        
        Material material = parts[1].equalsIgnoreCase("ANY") ? null : Material.valueOf(parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        
        return new CraftObjective(objectiveId, material, amount);
    }
}
