/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class KillMythicMobObjective
extends AbstractObjective {
    private final String mobType;

    public KillMythicMobObjective(String objectiveId, String mobType, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.mobType = mobType;
    }

    public KillMythicMobObjective(String objectiveId, String mobType, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.mobType = mobType;
    }

    @Override
    public String getType() {
        return "kill_mythicmob";
    }

    public String getMobType() {
        return this.mobType;
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        return false;
    }

    @Override
    public String getDescription() {
        return "Kill " + this.requiredAmount + " " + this.mobType;
    }

    @Override
    public String serialize() {
        return "kill_mythicmob:" + this.mobType + ":" + this.requiredAmount;
    }
}

