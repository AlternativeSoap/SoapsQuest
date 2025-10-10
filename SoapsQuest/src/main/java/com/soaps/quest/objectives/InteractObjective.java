package com.soaps.quest.objectives;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

/**
 * Objective that tracks block interactions.
 * Can track interactions with specific block types or any block.
 */
public class InteractObjective extends AbstractObjective {
    
    private final Material targetBlock;
    
    public InteractObjective(String objectiveId, Material targetBlock, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetBlock = targetBlock;
    }
    
    public InteractObjective(String objectiveId, Material targetBlock, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetBlock = targetBlock;
    }
    
    public Material getTargetBlock() {
        return targetBlock;
    }
    
    @Override
    public String getType() {
        return "interact";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerInteractEvent interactEvent)) {
            return false;
        }
        
        // Only count right-click on blocks
        if (interactEvent.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }
        
        Block block = interactEvent.getClickedBlock();
        if (block == null) {
            return false;
        }
        
        // Check if block matches target (or any if target is null)
        if (targetBlock != null && block.getType() != targetBlock) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        incrementProgress(playerId);
        
        return true;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        String blockName = targetBlock != null ? targetBlock.name() : "BLOCKS";
        return current + "/" + getRequiredAmount() + " " + blockName;
    }
    
    @Override
    public String getDescription() {
        String blockName = targetBlock != null ? targetBlock.name() : "any blocks";
        return "Interact with " + getRequiredAmount() + " " + blockName;
    }
    
    @Override
    public String serialize() {
        String block = targetBlock != null ? targetBlock.name() : "ANY";
        return getType() + ":" + block + ":" + getRequiredAmount();
    }
    
    /**
     * Deserialize an InteractObjective from a string.
     * Format: interact:MATERIAL:amount (use "ANY" for any block)
     */
    public static InteractObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid interact objective data: " + data);
        }
        
        Material material = parts[1].equalsIgnoreCase("ANY") ? null : Material.valueOf(parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        
        return new InteractObjective(objectiveId, material, amount);
    }
}
