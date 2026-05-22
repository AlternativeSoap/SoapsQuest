/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.entity.EntityBreedEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityBreedEvent;

public class BreedObjective
extends AbstractObjective {
    private final EntityType targetEntity;

    public BreedObjective(String objectiveId, EntityType targetEntity, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetEntity = targetEntity;
    }

    public BreedObjective(String objectiveId, EntityType targetEntity, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetEntity = targetEntity;
    }

    public EntityType getTargetEntity() {
        return this.targetEntity;
    }

    @Override
    public String getType() {
        return "breed";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        Player breeder;
        if (!(event instanceof EntityBreedEvent)) {
            return false;
        }
        EntityBreedEvent breedEvent = (EntityBreedEvent)event;
        if (this.targetEntity != null && breedEvent.getEntity().getType() != this.targetEntity) {
            return false;
        }
        LivingEntity livingEntity = breedEvent.getBreeder();
        if (!(livingEntity instanceof Player) || !(breeder = (Player)livingEntity).getUniqueId().equals(player.getUniqueId())) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        this.incrementProgress(playerId);
        return true;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        String entityName = this.targetEntity != null ? this.targetEntity.name() : "ANIMALS";
        return current + "/" + this.getRequiredAmount() + " " + entityName;
    }

    @Override
    public String getDescription() {
        String entityName = this.targetEntity != null ? this.formatName(this.targetEntity.name()) : "any animals";
        return "Breed " + this.getRequiredAmount() + " " + entityName;
    }

    @Override
    public String serialize() {
        String entity = this.targetEntity != null ? this.targetEntity.name() : "ANY";
        return this.getType() + ":" + entity + ":" + this.getRequiredAmount();
    }

    public static BreedObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid breed objective data: " + data);
        }
        EntityType entityType = parts[1].equalsIgnoreCase("ANY") ? null : EntityType.valueOf((String)parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        return new BreedObjective(objectiveId, entityType, amount);
    }
}

