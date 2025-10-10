package com.soaps.quest.objectives;

import java.util.UUID;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityTameEvent;

/**
 * Objective that tracks taming animals.
 * Triggered when a player tames a specific entity type.
 */
public class TameObjective extends AbstractObjective {
    
    private final EntityType targetEntity;
    
    public TameObjective(String objectiveId, EntityType targetEntity, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetEntity = targetEntity;
    }
    
    public TameObjective(String objectiveId, EntityType targetEntity, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetEntity = targetEntity;
    }
    
    public EntityType getTargetEntity() {
        return targetEntity;
    }
    
    @Override
    public String getType() {
        return "tame";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof EntityTameEvent tameEvent)) {
            return false;
        }
        
        // Check if the tamed entity matches our target (or if target is null, accept any)
        if (targetEntity != null && tameEvent.getEntity().getType() != targetEntity) {
            return false;
        }
        
        // Verify the tamer is the player
        if (!(tameEvent.getOwner() instanceof Player tamer) || !tamer.getUniqueId().equals(player.getUniqueId())) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        incrementProgress(playerId);
        
        return true;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        String entityName = targetEntity != null ? targetEntity.name() : "ANIMALS";
        return current + "/" + getRequiredAmount() + " " + entityName;
    }
    
    @Override
    public String getDescription() {
        String entityName = targetEntity != null ? targetEntity.name() : "any animals";
        return "Tame " + getRequiredAmount() + " " + entityName;
    }
    
    @Override
    public String serialize() {
        String entity = targetEntity != null ? targetEntity.name() : "ANY";
        return getType() + ":" + entity + ":" + getRequiredAmount();
    }
    
    /**
     * Deserialize a TameObjective from a string.
     * Format: tame:ENTITY_TYPE:amount (use "ANY" for any animal)
     */
    public static TameObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid tame objective data: " + data);
        }
        
        EntityType entityType = parts[1].equalsIgnoreCase("ANY") ? null : EntityType.valueOf(parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        
        return new TameObjective(objectiveId, entityType, amount);
    }
}
