package com.soaps.quest.objectives;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

/**
 * Objective that tracks damage dealt to entities.
 * Can track damage to specific entity types or any entity.
 */
public class DamageObjective extends AbstractObjective {
    
    private final EntityType targetEntity;
    
    public DamageObjective(String objectiveId, EntityType targetEntity, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetEntity = targetEntity;
    }
    
    public DamageObjective(String objectiveId, EntityType targetEntity, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetEntity = targetEntity;
    }
    
    public EntityType getTargetEntity() {
        return targetEntity;
    }
    
    @Override
    public String getType() {
        return "damage";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof EntityDamageByEntityEvent damageEvent)) {
            return false;
        }
        
        // Check if damager is the player
        if (!(damageEvent.getDamager() instanceof Player damager)) {
            return false;
        }
        
        if (!damager.getUniqueId().equals(player.getUniqueId())) {
            return false;
        }
        
        // Check if target entity matches (or any if null)
        if (targetEntity != null && damageEvent.getEntity().getType() != targetEntity) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        int damageAmount = (int) Math.ceil(damageEvent.getFinalDamage());
        incrementProgress(playerId, damageAmount);
        
        return true;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        String entityName = targetEntity != null ? targetEntity.name() : "ANY";
        return current + "/" + getRequiredAmount() + " damage to " + entityName;
    }
    
    @Override
    public String getDescription() {
        String entityName = targetEntity != null ? targetEntity.name() : "any entities";
        return "Deal " + getRequiredAmount() + " damage to " + entityName;
    }
    
    @Override
    public String serialize() {
        String entity = targetEntity != null ? targetEntity.name() : "ANY";
        return getType() + ":" + entity + ":" + getRequiredAmount();
    }
    
    /**
     * Deserialize a DamageObjective from a string.
     * Format: damage:ENTITY_TYPE:amount (use "ANY" for any entity)
     */
    public static DamageObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid damage objective data: " + data);
        }
        
        EntityType entityType = parts[1].equalsIgnoreCase("ANY") ? null : EntityType.valueOf(parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        
        return new DamageObjective(objectiveId, entityType, amount);
    }
}
