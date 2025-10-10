package com.soaps.quest.objectives;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemConsumeEvent;

/**
 * Objective that tracks consuming items (food, potions, etc.).
 * Triggered when a player consumes a specific item.
 */
public class ConsumeObjective extends AbstractObjective {
    
    private final Material targetItem;
    
    public ConsumeObjective(String objectiveId, Material targetItem, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetItem = targetItem;
    }
    
    public ConsumeObjective(String objectiveId, Material targetItem, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetItem = targetItem;
    }
    
    public Material getTargetItem() {
        return targetItem;
    }
    
    @Override
    public String getType() {
        return "consume";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerItemConsumeEvent consumeEvent)) {
            return false;
        }
        
        // Check if the consumed item matches our target
        if (consumeEvent.getItem().getType() != targetItem) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        incrementProgress(playerId);
        
        return true;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        return current + "/" + getRequiredAmount() + " " + targetItem.name();
    }
    
    @Override
    public String getDescription() {
        return "Consume " + getRequiredAmount() + " " + targetItem.name();
    }
    
    @Override
    public String serialize() {
        return getType() + ":" + targetItem.name() + ":" + getRequiredAmount();
    }
    
    /**
     * Deserialize a ConsumeObjective from a string.
     * Format: consume:MATERIAL:amount
     */
    public static ConsumeObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid consume objective data: " + data);
        }
        
        Material material = Material.valueOf(parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        
        return new ConsumeObjective(objectiveId, material, amount);
    }
}
