package com.soaps.quest.objectives;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Objective that tracks fishing.
 * Can track specific fish types or any fish caught.
 */
public class FishObjective extends AbstractObjective {
    
    private final Material targetFish;
    
    public FishObjective(String objectiveId, Material targetFish, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetFish = targetFish;
    }
    
    public FishObjective(String objectiveId, Material targetFish, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetFish = targetFish;
    }
    
    public Material getTargetFish() {
        return targetFish;
    }
    
    @Override
    public String getType() {
        return "fish";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerFishEvent fishEvent)) {
            return false;
        }
        
        // Only count successful catches
        if (fishEvent.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return false;
        }
        
        // Get the caught item
        if (!(fishEvent.getCaught() instanceof Item caughtItem)) {
            return false;
        }
        
        ItemStack caught = caughtItem.getItemStack();
        
        // Check if it matches our target (or if target is null, accept any fish)
        if (targetFish != null && caught.getType() != targetFish) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        incrementProgress(playerId, caught.getAmount());
        
        return true;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        String fishName = targetFish != null ? targetFish.name() : "FISH";
        return current + "/" + getRequiredAmount() + " " + fishName;
    }
    
    @Override
    public String getDescription() {
        String fishName = targetFish != null ? targetFish.name() : "any fish";
        return "Catch " + getRequiredAmount() + " " + fishName;
    }
    
    @Override
    public String serialize() {
        String fish = targetFish != null ? targetFish.name() : "ANY";
        return getType() + ":" + fish + ":" + getRequiredAmount();
    }
    
    /**
     * Deserialize a FishObjective from a string.
     * Format: fish:MATERIAL:amount (use "ANY" for any fish)
     */
    public static FishObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid fish objective data: " + data);
        }
        
        Material material = parts[1].equalsIgnoreCase("ANY") ? null : Material.valueOf(parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        
        return new FishObjective(objectiveId, material, amount);
    }
}
