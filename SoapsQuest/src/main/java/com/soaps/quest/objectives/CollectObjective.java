package com.soaps.quest.objectives;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Objective for collecting a specific number of items.
 * Supports ANY to track collecting any item type.
 */
public class CollectObjective extends AbstractObjective {
    
    private final Material itemType; // null means ANY item
    
    /**
     * Constructor for CollectObjective.
     * 
     * @param objectiveId Unique identifier for this objective
     * @param itemType Type of item to collect (null for ANY)
     * @param requiredAmount Number of items to collect
     */
    public CollectObjective(String objectiveId, Material itemType, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.itemType = itemType;
    }
    
    /**
     * Constructor for CollectObjective with milestones.
     * 
     * @param objectiveId Unique identifier for this objective
     * @param itemType Type of item to collect (null for ANY)
     * @param requiredAmount Number of items to collect
     * @param milestones Custom milestone percentages
     */
    public CollectObjective(String objectiveId, Material itemType, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.itemType = itemType;
    }
    
    @Override
    public String getType() {
        return "collect";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof EntityPickupItemEvent)) {
            return false;
        }
        
        EntityPickupItemEvent pickupEvent = (EntityPickupItemEvent) event;
        
        // Verify it's the player picking up the item
        if (!(pickupEvent.getEntity() instanceof Player)) {
            return false;
        }
        
        Player pickupPlayer = (Player) pickupEvent.getEntity();
        if (!pickupPlayer.equals(player)) {
            return false;
        }
        
        // Check if the picked up item matches our requirement
        // If itemType is null, accept ANY item
        ItemStack item = pickupEvent.getItem().getItemStack();
        if (itemType == null || item.getType() == itemType) {
            incrementProgress(player.getUniqueId(), item.getAmount());
            return true;
        }
        
        return false;
    }
    
    @Override
    public String getDescription() {
        String itemName = itemType != null ? itemType.name() : "any items";
        return "Collect " + requiredAmount + " " + itemName;
    }
    
    @Override
    public String serialize() {
        String item = itemType != null ? itemType.name() : "ANY";
        return "collect:" + item + ":" + requiredAmount;
    }
    
    /**
     * Get the item type for this objective.
     * 
     * @return Item material (null for ANY)
     */
    public Material getItemType() {
        return itemType;
    }
}
