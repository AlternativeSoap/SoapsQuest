package com.soaps.quest.objectives;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Objective for placing a specific number of blocks.
 * Supports ANY to track placing any block type.
 */
public class PlaceObjective extends AbstractObjective {
    
    private final Material blockType; // null means ANY block
    
    /**
     * Constructor for PlaceObjective.
     * 
     * @param objectiveId Unique identifier for this objective
     * @param blockType Type of block to place (null for ANY)
     * @param requiredAmount Number of blocks to place
     */
    public PlaceObjective(String objectiveId, Material blockType, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.blockType = blockType;
    }
    
    /**
     * Constructor for PlaceObjective with milestones.
     * 
     * @param objectiveId Unique identifier for this objective
     * @param blockType Type of block to place (null for ANY)
     * @param requiredAmount Number of blocks to place
     * @param milestones Custom milestone percentages
     */
    public PlaceObjective(String objectiveId, Material blockType, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.blockType = blockType;
    }
    
    @Override
    public String getType() {
        return "place";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof BlockPlaceEvent)) {
            return false;
        }
        
        BlockPlaceEvent placeEvent = (BlockPlaceEvent) event;
        
        // Check if the placed block matches our requirement
        // If blockType is null, accept ANY block
        if (blockType == null || placeEvent.getBlock().getType() == blockType) {
            incrementProgress(player.getUniqueId());
            return true;
        }
        
        return false;
    }
    
    @Override
    public String getDescription() {
        String blockName = blockType != null ? blockType.name() : "any blocks";
        return "Place " + requiredAmount + " " + blockName;
    }
    
    @Override
    public String serialize() {
        String block = blockType != null ? blockType.name() : "ANY";
        return "place:" + block + ":" + requiredAmount;
    }
    
    /**
     * Get the block type for this objective.
     * 
     * @return Block material (null for ANY)
     */
    public Material getBlockType() {
        return blockType;
    }
}
