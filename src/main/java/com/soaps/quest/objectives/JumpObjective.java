/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.player.PlayerMoveEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

public class JumpObjective
extends AbstractObjective {
    private final Map<UUID, Boolean> wasOnGround = new HashMap<UUID, Boolean>();

    public JumpObjective(String objectiveId, int requiredAmount) {
        super(objectiveId, requiredAmount);
    }

    public JumpObjective(String objectiveId, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
    }

    @Override
    public void resetProgress(UUID playerUUID) {
        super.resetProgress(playerUUID);
        this.wasOnGround.remove(playerUUID);
    }

    @Override
    public String getType() {
        return "jump";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerMoveEvent)) {
            return false;
        }
        PlayerMoveEvent moveEvent = (PlayerMoveEvent)event;
        UUID playerId = player.getUniqueId();
        double currentY = moveEvent.getTo().getY();
        double previousY = moveEvent.getFrom().getY();
        boolean isOnGroundNow = player.getFallDistance() == 0.0f;
        boolean wasGrounded = this.wasOnGround.getOrDefault(playerId, true);
        if (wasGrounded && !isOnGroundNow && currentY > previousY + 0.1 && player.getVelocity().getY() > 0.3) {
            this.wasOnGround.put(playerId, false);
            this.incrementProgress(playerId);
            return true;
        }
        this.wasOnGround.put(playerId, isOnGroundNow);
        return false;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        return current + "/" + this.getRequiredAmount() + " jumps";
    }

    @Override
    public String getDescription() {
        return "Jump " + this.getRequiredAmount() + " times";
    }

    @Override
    public String serialize() {
        return this.getType() + ":JUMP:" + this.getRequiredAmount();
    }

    public static JumpObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid jump objective data: " + data);
        }
        int amount = Integer.parseInt(parts[2]);
        return new JumpObjective(objectiveId, amount);
    }
}

