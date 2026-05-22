/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.player.PlayerExpChangeEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class XpPickupObjective
extends AbstractObjective {
    public XpPickupObjective(String objectiveId, int requiredAmount) {
        super(objectiveId, requiredAmount);
    }

    public XpPickupObjective(String objectiveId, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
    }

    @Override
    public String getType() {
        return "xp_pickup";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerExpChangeEvent)) {
            return false;
        }
        PlayerExpChangeEvent expEvent = (PlayerExpChangeEvent)event;
        return expEvent.getAmount() > 0;
    }

    @Override
    public String getDescription() {
        return "Gain " + this.requiredAmount + " XP";
    }

    @Override
    public String serialize() {
        return "xp_pickup:ANY:" + this.requiredAmount;
    }
}

