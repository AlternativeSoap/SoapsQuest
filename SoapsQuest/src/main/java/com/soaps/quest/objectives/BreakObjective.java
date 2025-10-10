package com.soaps.quest.objectives;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Objective for breaking a specific number of blocks.
 * Supports ANY to track breaking any block type.
 */
public class BreakObjective extends AbstractObjective {
    
    private final Material blockType; // null means ANY block
    
    /**
     * Constructor for BreakObjective.
     * 
     * @param objectiveId Unique identifier for this objective
     * @param blockType Type of block to break (null for ANY)
     * @param requiredAmount Number of blocks to break
     */
    public BreakObjective(String objectiveId, Material blockType, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.blockType = blockType;
    }
    
    /**
     * Constructor for BreakObjective with milestones.
     * 
     * @param objectiveId Unique identifier for this objective
     * @param blockType Type of block to break (null for ANY)
     * @param requiredAmount Number of blocks to break
     * @param milestones Custom milestone percentages
     */
    public BreakObjective(String objectiveId, Material blockType, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.blockType = blockType;
    }
    
    @Override
    public String getType() {
        return "break";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof BlockBreakEvent)) {
            return false;
        }
        
        BlockBreakEvent breakEvent = (BlockBreakEvent) event;
        
        // Check if the broken block matches our requirement
        // If blockType is null, accept ANY block
        if (blockType == null || breakEvent.getBlock().getType() == blockType) {
            incrementProgress(player.getUniqueId());
            return true;
        }
        
        return false;
    }
    
    @Override
    public String getDescription() {
        String blockName = blockType != null ? blockType.name() : "any blocks";
        return "Break " + requiredAmount + " " + blockName;
    }
    
    @Override
    public String serialize() {
        String block = blockType != null ? blockType.name() : "ANY";
        return "break:" + block + ":" + requiredAmount;
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
