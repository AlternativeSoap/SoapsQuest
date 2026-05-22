/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.event.Event
 *  org.bukkit.event.entity.ProjectileLaunchEvent
 *  org.bukkit.projectiles.ProjectileSource
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

public class ProjectileObjective
extends AbstractObjective {
    private final EntityType projectileType;

    public ProjectileObjective(String objectiveId, EntityType projectileType, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.projectileType = projectileType;
    }

    public ProjectileObjective(String objectiveId, EntityType projectileType, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.projectileType = projectileType;
    }

    public EntityType getProjectileType() {
        return this.projectileType;
    }

    @Override
    public String getType() {
        return "projectile";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof ProjectileLaunchEvent)) {
            return false;
        }
        ProjectileLaunchEvent launchEvent = (ProjectileLaunchEvent)event;
        Projectile projectile = launchEvent.getEntity();
        ProjectileSource projectileSource = projectile.getShooter();
        if (!(projectileSource instanceof Player)) {
            return false;
        }
        Player shooter = (Player)projectileSource;
        if (!shooter.getUniqueId().equals(player.getUniqueId())) {
            return false;
        }
        if (this.projectileType != null && projectile.getType() != this.projectileType) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        this.incrementProgress(playerId);
        return true;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        String typeName = this.projectileType != null ? this.formatName(this.projectileType.name()) : "PROJECTILES";
        return current + "/" + this.getRequiredAmount() + " " + typeName;
    }

    @Override
    public String getDescription() {
        String typeName = this.projectileType != null ? this.formatName(this.projectileType.name()) : "projectiles";
        return "Launch " + this.getRequiredAmount() + " " + typeName;
    }

    @Override
    public String serialize() {
        String type = this.projectileType != null ? this.projectileType.name() : "ANY";
        return this.getType() + ":" + type + ":" + this.getRequiredAmount();
    }

    public static ProjectileObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid projectile objective data: " + data);
        }
        EntityType type = parts[1].equalsIgnoreCase("ANY") ? null : EntityType.valueOf((String)parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        return new ProjectileObjective(objectiveId, type, amount);
    }
}

