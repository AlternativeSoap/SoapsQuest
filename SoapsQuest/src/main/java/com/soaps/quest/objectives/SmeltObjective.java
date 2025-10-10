package com.soaps.quest.objectives;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Objective that tracks item smelting in furnaces.
 * Triggered when a player smelts a specific item.
 */
public class SmeltObjective extends AbstractObjective {
    
    private final Material targetItem;
    
    public SmeltObjective(String objectiveId, Material targetItem, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetItem = targetItem;
    }
    
    public SmeltObjective(String objectiveId, Material targetItem, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetItem = targetItem;
    }
    
    public Material getTargetItem() {
        return targetItem;
    }
    
    @Override
    public String getType() {
        return "smelt";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof FurnaceSmeltEvent smeltEvent)) {
            return false;
        }
        
        // Check if the smelted item matches our target
        ItemStack result = smeltEvent.getResult();
        if (result.getType() != targetItem) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        
        // Increment progress by the amount smelted
        int smeltAmount = result.getAmount();
        incrementProgress(playerId, smeltAmount);
        
        return true;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        return current + "/" + getRequiredAmount() + " " + targetItem.name();
    }
    
    @Override
    public String getDescription() {
        return "Smelt " + getRequiredAmount() + " " + targetItem.name();
    }
    
    @Override
    public String serialize() {
        return getType() + ":" + targetItem.name() + ":" + getRequiredAmount();
    }
    
    /**
     * Deserialize a SmeltObjective from a string.
     * Format: smelt:MATERIAL:amount
     */
    public static SmeltObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid smelt objective data: " + data);
        }
        
        Material material = Material.valueOf(parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        
        return new SmeltObjective(objectiveId, material, amount);
    }
}
