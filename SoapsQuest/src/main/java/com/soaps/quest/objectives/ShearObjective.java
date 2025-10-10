package com.soaps.quest.objectives;

import java.util.UUID;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerShearEntityEvent;

/**
 * Objective that tracks shearing animals.
 * Triggered when a player shears a specific entity type.
 */
public class ShearObjective extends AbstractObjective {
    
    private final EntityType targetEntity;
    
    public ShearObjective(String objectiveId, EntityType targetEntity, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetEntity = targetEntity;
    }
    
    public ShearObjective(String objectiveId, EntityType targetEntity, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetEntity = targetEntity;
    }
    
    public EntityType getTargetEntity() {
        return targetEntity;
    }
    
    @Override
    public String getType() {
        return "shear";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerShearEntityEvent shearEvent)) {
            return false;
        }
        
        // Check if the sheared entity matches our target (or if target is null, accept any)
        if (targetEntity != null && shearEvent.getEntity().getType() != targetEntity) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        incrementProgress(playerId);
        
        return true;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        String entityName = targetEntity != null ? targetEntity.name() : "ENTITIES";
        return current + "/" + getRequiredAmount() + " " + entityName;
    }
    
    @Override
    public String getDescription() {
        String entityName = targetEntity != null ? targetEntity.name() : "any entities";
        return "Shear " + getRequiredAmount() + " " + entityName;
    }
    
    @Override
    public String serialize() {
        String entity = targetEntity != null ? targetEntity.name() : "ANY";
        return getType() + ":" + entity + ":" + getRequiredAmount();
    }
    
    /**
     * Deserialize a ShearObjective from a string.
     * Format: shear:ENTITY_TYPE:amount (use "ANY" for any entity)
     */
    public static ShearObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid shear objective data: " + data);
        }
        
        EntityType entityType = parts[1].equalsIgnoreCase("ANY") ? null : EntityType.valueOf(parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        
        return new ShearObjective(objectiveId, entityType, amount);
    }
}
