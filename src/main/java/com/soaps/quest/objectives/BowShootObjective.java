/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.entity.EntityShootBowEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;

public class BowShootObjective
extends AbstractObjective {
    public BowShootObjective(String objectiveId, int requiredAmount) {
        super(objectiveId, requiredAmount);
    }

    public BowShootObjective(String objectiveId, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
    }

    @Override
    public String getType() {
        return "bowshoot";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof EntityShootBowEvent)) {
            return false;
        }
        EntityShootBowEvent bowEvent = (EntityShootBowEvent)event;
        LivingEntity livingEntity = bowEvent.getEntity();
        if (!(livingEntity instanceof Player)) {
            return false;
        }
        Player shooter = (Player)livingEntity;
        if (!shooter.getUniqueId().equals(player.getUniqueId())) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        this.incrementProgress(playerId);
        return true;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        return current + "/" + this.getRequiredAmount() + " arrows shot";
    }

    @Override
    public String getDescription() {
        return "Shoot " + this.getRequiredAmount() + " arrows";
    }

    @Override
    public String serialize() {
        return this.getType() + ":BOW:" + this.getRequiredAmount();
    }

    public static BowShootObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid bowshoot objective data: " + data);
        }
        int amount = Integer.parseInt(parts[2]);
        return new BowShootObjective(objectiveId, amount);
    }
}

