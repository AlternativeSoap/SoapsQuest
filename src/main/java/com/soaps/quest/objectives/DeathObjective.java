/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.entity.PlayerDeathEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathObjective
extends AbstractObjective {
    public DeathObjective(String objectiveId, int requiredAmount) {
        super(objectiveId, requiredAmount);
    }

    public DeathObjective(String objectiveId, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
    }

    @Override
    public String getType() {
        return "death";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerDeathEvent)) {
            return false;
        }
        PlayerDeathEvent deathEvent = (PlayerDeathEvent)event;
        if (!deathEvent.getEntity().getUniqueId().equals(player.getUniqueId())) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        this.incrementProgress(playerId);
        return true;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        return current + "/" + this.getRequiredAmount() + " deaths";
    }

    @Override
    public String getDescription() {
        return "Die " + this.getRequiredAmount() + " times";
    }

    @Override
    public String serialize() {
        return this.getType() + ":DEATH:" + this.getRequiredAmount();
    }

    public static DeathObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid death objective data: " + data);
        }
        int amount = Integer.parseInt(parts[2]);
        return new DeathObjective(objectiveId, amount);
    }
}

