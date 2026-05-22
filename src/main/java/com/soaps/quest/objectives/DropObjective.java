/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.player.PlayerDropItemEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerDropItemEvent;

public class DropObjective
extends AbstractObjective {
    private final Material targetItem;

    public DropObjective(String objectiveId, Material targetItem, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetItem = targetItem;
    }

    public DropObjective(String objectiveId, Material targetItem, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetItem = targetItem;
    }

    public Material getTargetItem() {
        return this.targetItem;
    }

    @Override
    public String getType() {
        return "drop";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerDropItemEvent)) {
            return false;
        }
        PlayerDropItemEvent dropEvent = (PlayerDropItemEvent)event;
        if (this.targetItem != null && dropEvent.getItemDrop().getItemStack().getType() != this.targetItem) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        int dropAmount = dropEvent.getItemDrop().getItemStack().getAmount();
        this.incrementProgress(playerId, dropAmount);
        return true;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        String itemName = this.targetItem != null ? this.targetItem.name() : "ITEMS";
        return current + "/" + this.getRequiredAmount() + " " + itemName;
    }

    @Override
    public String getDescription() {
        String itemName = this.targetItem != null ? this.formatName(this.targetItem.name()) : "any items";
        return "Drop " + this.getRequiredAmount() + " " + itemName;
    }

    @Override
    public String serialize() {
        String item = this.targetItem != null ? this.targetItem.name() : "ANY";
        return this.getType() + ":" + item + ":" + this.getRequiredAmount();
    }

    public static DropObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid drop objective data: " + data);
        }
        Material material = parts[1].equalsIgnoreCase("ANY") ? null : Material.valueOf((String)parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        return new DropObjective(objectiveId, material, amount);
    }
}

