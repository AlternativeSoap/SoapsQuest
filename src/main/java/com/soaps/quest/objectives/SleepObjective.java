/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.player.PlayerBedEnterEvent
 *  org.bukkit.event.player.PlayerBedEnterEvent$BedEnterResult
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class SleepObjective
extends AbstractObjective {
    public SleepObjective(String objectiveId, int requiredAmount) {
        super(objectiveId, requiredAmount);
    }

    public SleepObjective(String objectiveId, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
    }

    @Override
    public String getType() {
        return "sleep";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerBedEnterEvent)) {
            return false;
        }
        PlayerBedEnterEvent bedEvent = (PlayerBedEnterEvent)event;
        if (bedEvent.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        this.incrementProgress(playerId);
        return true;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        return current + "/" + this.getRequiredAmount() + " times";
    }

    @Override
    public String getDescription() {
        return "Sleep in bed " + this.getRequiredAmount() + " times";
    }

    @Override
    public String serialize() {
        return this.getType() + ":SLEEP:" + this.getRequiredAmount();
    }

    public static SleepObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid sleep objective data: " + data);
        }
        int amount = Integer.parseInt(parts[2]);
        return new SleepObjective(objectiveId, amount);
    }
}

