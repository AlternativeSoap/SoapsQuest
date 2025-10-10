package com.soaps.quest.objectives;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.UUID;

/**
 * Objective that tracks health regeneration.
 * Can track healing from any source or specific reasons.
 */
public class HealObjective extends AbstractObjective {
    
    private final EntityRegainHealthEvent.RegainReason reason;
    
    public HealObjective(String objectiveId, EntityRegainHealthEvent.RegainReason reason, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.reason = reason;
    }
    
    public HealObjective(String objectiveId, EntityRegainHealthEvent.RegainReason reason, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.reason = reason;
    }
    
    public EntityRegainHealthEvent.RegainReason getReason() {
        return reason;
    }
    
    @Override
    public String getType() {
        return "heal";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof EntityRegainHealthEvent healEvent)) {
            return false;
        }
        
        // Check if entity is a player
        if (healEvent.getEntity().getType() != EntityType.PLAYER) {
            return false;
        }
        
        Player healedPlayer = (Player) healEvent.getEntity();
        if (!healedPlayer.getUniqueId().equals(player.getUniqueId())) {
            return false;
        }
        
        // Check reason if specified
        if (reason != null && healEvent.getRegainReason() != reason) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        // Track amount of health healed
        int healAmount = (int) Math.ceil(healEvent.getAmount());
        incrementProgress(playerId, healAmount);
        
        return true;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        return current + "/" + getRequiredAmount() + " HP";
    }
    
    @Override
    public String getDescription() {
        String reasonText = reason != null ? " (" + reason.name() + ")" : "";
        return "Heal " + getRequiredAmount() + " HP" + reasonText;
    }
    
    @Override
    public String serialize() {
        String reasonStr = reason != null ? reason.name() : "ANY";
        return getType() + ":" + reasonStr + ":" + getRequiredAmount();
    }
    
    /**
     * Deserialize a HealObjective from a string.
     * Format: heal:REASON:amount (use "ANY" for any reason)
     */
    public static HealObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid heal objective data: " + data);
        }
        
        EntityRegainHealthEvent.RegainReason reason = null;
        if (!parts[1].equalsIgnoreCase("ANY")) {
            reason = EntityRegainHealthEvent.RegainReason.valueOf(parts[1].toUpperCase());
        }
        
        int amount = Integer.parseInt(parts[2]);
        return new HealObjective(objectiveId, reason, amount);
    }
}
