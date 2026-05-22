/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.player.PlayerLevelChangeEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class LevelObjective
extends AbstractObjective {
    private final boolean isReachLevel;

    public LevelObjective(String objectiveId, int requiredAmount, boolean isReachLevel) {
        super(objectiveId, requiredAmount);
        this.isReachLevel = isReachLevel;
    }

    public LevelObjective(String objectiveId, int requiredAmount, boolean isReachLevel, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.isReachLevel = isReachLevel;
    }

    public boolean isReachLevel() {
        return this.isReachLevel;
    }

    @Override
    public String getType() {
        return this.isReachLevel ? "reachlevel" : "gainlevel";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerLevelChangeEvent)) {
            return false;
        }
        PlayerLevelChangeEvent levelEvent = (PlayerLevelChangeEvent)event;
        UUID playerId = player.getUniqueId();
        if (this.isReachLevel) {
            if (levelEvent.getNewLevel() >= this.getRequiredAmount()) {
                this.setCurrentProgress(playerId, this.getRequiredAmount());
                return true;
            }
        } else {
            int levelsGained = levelEvent.getNewLevel() - levelEvent.getOldLevel();
            if (levelsGained > 0) {
                this.incrementProgress(playerId, levelsGained);
                return true;
            }
        }
        return false;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        if (this.isReachLevel) {
            return "Level " + current + "/" + this.getRequiredAmount();
        }
        return current + "/" + this.getRequiredAmount() + " levels gained";
    }

    @Override
    public String getDescription() {
        if (this.isReachLevel) {
            return "Reach level " + this.getRequiredAmount();
        }
        return "Gain " + this.getRequiredAmount() + " levels";
    }

    @Override
    public String serialize() {
        return this.getType() + ":LEVEL:" + this.getRequiredAmount();
    }

    public static LevelObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid level objective data: " + data);
        }
        boolean isReach = parts[0].equalsIgnoreCase("reachlevel");
        int amount = Integer.parseInt(parts[2]);
        return new LevelObjective(objectiveId, amount, isReach);
    }
}

