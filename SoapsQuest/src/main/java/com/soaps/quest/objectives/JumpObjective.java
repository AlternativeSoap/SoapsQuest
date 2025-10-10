package com.soaps.quest.objectives;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Objective that tracks jumping.
 * Detects vertical movement indicating a jump.
 */
public class JumpObjective extends AbstractObjective {
    
    public JumpObjective(String objectiveId, int requiredAmount) {
        super(objectiveId, requiredAmount);
    }
    
    public JumpObjective(String objectiveId, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
    }
    
    @Override
    public String getType() {
        return "jump";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerMoveEvent moveEvent)) {
            return false;
        }
        
        // Detect jump: player moving up while not on ground
        double yDiff = moveEvent.getTo().getY() - moveEvent.getFrom().getY();
        
        // Jump detection: positive Y movement of at least 0.1 blocks
        // Use fall distance as an alternative to isOnGround() for jump detection
        if (yDiff > 0.1 && player.getFallDistance() == 0 && player.getVelocity().getY() > 0) {
            UUID playerId = player.getUniqueId();
            incrementProgress(playerId);
            return true;
        }
        
        return false;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        return current + "/" + getRequiredAmount() + " jumps";
    }
    
    @Override
    public String getDescription() {
        return "Jump " + getRequiredAmount() + " times";
    }
    
    @Override
    public String serialize() {
        return getType() + ":JUMP:" + getRequiredAmount();
    }
    
    /**
     * Deserialize a JumpObjective from a string.
     * Format: jump:JUMP:amount
     */
    public static JumpObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid jump objective data: " + data);
        }
        
        int amount = Integer.parseInt(parts[2]);
        return new JumpObjective(objectiveId, amount);
    }
}
