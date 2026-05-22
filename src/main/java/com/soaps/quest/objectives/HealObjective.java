/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.entity.EntityRegainHealthEvent
 *  org.bukkit.event.entity.EntityRegainHealthEvent$RegainReason
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class HealObjective
extends AbstractObjective {
    private final EntityRegainHealthEvent.RegainReason reason;

    public HealObjective(String objectiveId, EntityRegainHealthEvent.RegainReason reason, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.reason = reason;
    }

    public HealObjective(String objectiveId, EntityRegainHealthEvent.RegainReason reason, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.reason = reason;
    }

    public EntityRegainHealthEvent.RegainReason getReason() {
        return this.reason;
    }

    @Override
    public String getType() {
        return "heal";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof EntityRegainHealthEvent)) {
            return false;
        }
        EntityRegainHealthEvent healEvent = (EntityRegainHealthEvent)event;
        if (healEvent.getEntity().getType() != EntityType.PLAYER) {
            return false;
        }
        Player healedPlayer = (Player)healEvent.getEntity();
        if (!healedPlayer.getUniqueId().equals(player.getUniqueId())) {
            return false;
        }
        if (this.reason != null && healEvent.getRegainReason() != this.reason) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        int healAmount = (int)Math.ceil(healEvent.getAmount());
        this.incrementProgress(playerId, healAmount);
        return true;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        return current + "/" + this.getRequiredAmount() + " HP";
    }

    @Override
    public String getDescription() {
        String reasonText = this.reason != null ? " (" + this.formatName(this.reason.name()) + ")" : "";
        return "Heal " + this.getRequiredAmount() + " HP" + reasonText;
    }

    @Override
    public String serialize() {
        String reasonStr = this.reason != null ? this.reason.name() : "ANY";
        return this.getType() + ":" + reasonStr + ":" + this.getRequiredAmount();
    }

    public static HealObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid heal objective data: " + data);
        }
        EntityRegainHealthEvent.RegainReason reason = null;
        if (!parts[1].equalsIgnoreCase("ANY")) {
            reason = EntityRegainHealthEvent.RegainReason.valueOf((String)parts[1].toUpperCase());
        }
        int amount = Integer.parseInt(parts[2]);
        return new HealObjective(objectiveId, reason, amount);
    }
}

