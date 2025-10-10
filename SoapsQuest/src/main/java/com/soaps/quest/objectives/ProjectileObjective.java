package com.soaps.quest.objectives;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.UUID;

/**
 * Objective that tracks projectile launches.
 * Can track specific projectile types like snowballs, eggs, ender pearls.
 */
public class ProjectileObjective extends AbstractObjective {
    
    private final EntityType projectileType;
    
    public ProjectileObjective(String objectiveId, EntityType projectileType, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.projectileType = projectileType;
    }
    
    public ProjectileObjective(String objectiveId, EntityType projectileType, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.projectileType = projectileType;
    }
    
    public EntityType getProjectileType() {
        return projectileType;
    }
    
    @Override
    public String getType() {
        return "projectile";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof ProjectileLaunchEvent launchEvent)) {
            return false;
        }
        
        Projectile projectile = launchEvent.getEntity();
        
        // Check if shooter is the player
        if (!(projectile.getShooter() instanceof Player shooter)) {
            return false;
        }
        
        if (!shooter.getUniqueId().equals(player.getUniqueId())) {
            return false;
        }
        
        // Check projectile type if specified
        if (projectileType != null && projectile.getType() != projectileType) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        incrementProgress(playerId);
        
        return true;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        String typeName = projectileType != null ? projectileType.name() : "PROJECTILES";
        return current + "/" + getRequiredAmount() + " " + typeName;
    }
    
    @Override
    public String getDescription() {
        String typeName = projectileType != null ? projectileType.name() : "projectiles";
        return "Launch " + getRequiredAmount() + " " + typeName;
    }
    
    @Override
    public String serialize() {
        String type = projectileType != null ? projectileType.name() : "ANY";
        return getType() + ":" + type + ":" + getRequiredAmount();
    }
    
    /**
     * Deserialize a ProjectileObjective from a string.
     * Format: projectile:ENTITY_TYPE:amount (use "ANY" for any projectile)
     */
    public static ProjectileObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid projectile objective data: " + data);
        }
        
        EntityType type = parts[1].equalsIgnoreCase("ANY") ? null : EntityType.valueOf(parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        
        return new ProjectileObjective(objectiveId, type, amount);
    }
}
