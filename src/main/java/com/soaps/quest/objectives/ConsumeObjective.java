/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.player.PlayerItemConsumeEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class ConsumeObjective
extends AbstractObjective {
    private final Material targetItem;

    public ConsumeObjective(String objectiveId, Material targetItem, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetItem = targetItem;
    }

    public ConsumeObjective(String objectiveId, Material targetItem, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetItem = targetItem;
    }

    public Material getTargetItem() {
        return this.targetItem;
    }

    @Override
    public String getType() {
        return "consume";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerItemConsumeEvent)) {
            return false;
        }
        PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent)event;
        if (consumeEvent.getItem().getType() != this.targetItem) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        this.incrementProgress(playerId);
        return true;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        return current + "/" + this.getRequiredAmount() + " " + this.targetItem.name();
    }

    @Override
    public String getDescription() {
        return "Consume " + this.getRequiredAmount() + " " + this.formatName(this.targetItem.name());
    }

    @Override
    public String serialize() {
        return this.getType() + ":" + this.targetItem.name() + ":" + this.getRequiredAmount();
    }

    public static ConsumeObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid consume objective data: " + data);
        }
        Material material = Material.valueOf((String)parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        return new ConsumeObjective(objectiveId, material, amount);
    }
}

