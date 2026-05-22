/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.inventory.FurnaceExtractEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.FurnaceExtractEvent;

public class SmeltObjective
extends AbstractObjective {
    private final Material targetItem;

    public SmeltObjective(String objectiveId, Material targetItem, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetItem = targetItem;
    }

    public SmeltObjective(String objectiveId, Material targetItem, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetItem = targetItem;
    }

    public Material getTargetItem() {
        return this.targetItem;
    }

    @Override
    public String getType() {
        return "smelt";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof FurnaceExtractEvent)) {
            return false;
        }
        FurnaceExtractEvent extractEvent = (FurnaceExtractEvent)event;
        return extractEvent.getItemType() == this.targetItem;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        return current + "/" + this.getRequiredAmount() + " " + this.targetItem.name();
    }

    @Override
    public String getDescription() {
        return "Smelt " + this.getRequiredAmount() + " " + this.formatName(this.targetItem.name());
    }

    @Override
    public String serialize() {
        return this.getType() + ":" + this.targetItem.name() + ":" + this.getRequiredAmount();
    }

    public static SmeltObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid smelt objective data: " + data);
        }
        Material material = Material.valueOf((String)parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        return new SmeltObjective(objectiveId, material, amount);
    }
}

