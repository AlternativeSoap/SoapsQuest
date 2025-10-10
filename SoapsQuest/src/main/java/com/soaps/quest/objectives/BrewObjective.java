package com.soaps.quest.objectives;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

/**
 * Objective that tracks potion brewing.
 * Counts successful brew operations.
 */
public class BrewObjective extends AbstractObjective {
    
    private final Material targetPotion;
    
    public BrewObjective(String objectiveId, Material targetPotion, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetPotion = targetPotion;
    }
    
    public BrewObjective(String objectiveId, Material targetPotion, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetPotion = targetPotion;
    }
    
    public Material getTargetPotion() {
        return targetPotion;
    }
    
    @Override
    public String getType() {
        return "brew";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof BrewEvent brewEvent)) {
            return false;
        }
        
        // Count how many potions match our target
        BrewerInventory inventory = brewEvent.getContents();
        int matchingPotions = 0;
        
        // Check the 3 potion slots (0, 1, 2)
        for (int i = 0; i < 3; i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && (targetPotion == null || item.getType() == targetPotion)) {
                matchingPotions++;
            }
        }
        
        if (matchingPotions == 0) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        incrementProgress(playerId, matchingPotions);
        
        return true;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        String potionName = targetPotion != null ? targetPotion.name() : "POTIONS";
        return current + "/" + getRequiredAmount() + " " + potionName;
    }
    
    @Override
    public String getDescription() {
        String potionName = targetPotion != null ? targetPotion.name() : "any potions";
        return "Brew " + getRequiredAmount() + " " + potionName;
    }
    
    @Override
    public String serialize() {
        String potion = targetPotion != null ? targetPotion.name() : "ANY";
        return getType() + ":" + potion + ":" + getRequiredAmount();
    }
    
    /**
     * Deserialize a BrewObjective from a string.
     * Format: brew:MATERIAL:amount (use "ANY" for any potion)
     */
    public static BrewObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid brew objective data: " + data);
        }
        
        Material material = parts[1].equalsIgnoreCase("ANY") ? null : Material.valueOf(parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        
        return new BrewObjective(objectiveId, material, amount);
    }
}
