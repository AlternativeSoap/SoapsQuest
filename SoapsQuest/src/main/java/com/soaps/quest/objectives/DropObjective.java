package com.soaps.quest.objectives;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.UUID;

/**
 * Objective that tracks item dropping.
 * Can track specific items or any item dropped.
 */
public class DropObjective extends AbstractObjective {
    
    private final Material targetItem;
    
    public DropObjective(String objectiveId, Material targetItem, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetItem = targetItem;
    }
    
    public DropObjective(String objectiveId, Material targetItem, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetItem = targetItem;
    }
    
    public Material getTargetItem() {
        return targetItem;
    }
    
    @Override
    public String getType() {
        return "drop";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerDropItemEvent dropEvent)) {
            return false;
        }
        
        // Check if dropped item matches target (or any if target is null)
        if (targetItem != null && dropEvent.getItemDrop().getItemStack().getType() != targetItem) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        int dropAmount = dropEvent.getItemDrop().getItemStack().getAmount();
        incrementProgress(playerId, dropAmount);
        
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
        return "Drop " + getRequiredAmount() + " " + itemName;
    }
    
    @Override
    public String serialize() {
        String item = targetItem != null ? targetItem.name() : "ANY";
        return getType() + ":" + item + ":" + getRequiredAmount();
    }
    
    /**
     * Deserialize a DropObjective from a string.
     * Format: drop:MATERIAL:amount (use "ANY" for any item)
     */
    public static DropObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid drop objective data: " + data);
        }
        
        Material material = parts[1].equalsIgnoreCase("ANY") ? null : Material.valueOf(parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        
        return new DropObjective(objectiveId, material, amount);
    }
}
