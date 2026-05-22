/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageObjective
extends AbstractObjective {
    private final EntityType targetEntity;

    public DamageObjective(String objectiveId, EntityType targetEntity, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetEntity = targetEntity;
    }

    public DamageObjective(String objectiveId, EntityType targetEntity, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetEntity = targetEntity;
    }

    public EntityType getTargetEntity() {
        return this.targetEntity;
    }

    @Override
    public String getType() {
        return "damage";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) {
            return false;
        }
        EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent)event;
        Entity entity = damageEvent.getDamager();
        if (!(entity instanceof Player)) {
            return false;
        }
        Player damager = (Player)entity;
        if (!damager.getUniqueId().equals(player.getUniqueId())) {
            return false;
        }
        if (this.targetEntity != null && damageEvent.getEntity().getType() != this.targetEntity) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        int damageAmount = (int)Math.ceil(damageEvent.getFinalDamage());
        this.incrementProgress(playerId, damageAmount);
        return true;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        String entityName = this.targetEntity != null ? this.formatName(this.targetEntity.name()) : "ANY";
        return current + "/" + this.getRequiredAmount() + " damage to " + entityName;
    }

    @Override
    public String getDescription() {
        String entityName = this.targetEntity != null ? this.formatName(this.targetEntity.name()) : "any entities";
        return "Deal " + this.getRequiredAmount() + " damage to " + entityName;
    }

    @Override
    public String serialize() {
        String entity = this.targetEntity != null ? this.targetEntity.name() : "ANY";
        return this.getType() + ":" + entity + ":" + this.getRequiredAmount();
    }

    public static DamageObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid damage objective data: " + data);
        }
        EntityType entityType = parts[1].equalsIgnoreCase("ANY") ? null : EntityType.valueOf((String)parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        return new DamageObjective(objectiveId, entityType, amount);
    }
}

