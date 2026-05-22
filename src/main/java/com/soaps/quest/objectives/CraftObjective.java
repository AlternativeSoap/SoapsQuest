/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.inventory.CraftItemEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.CraftItemEvent;

public class CraftObjective
extends AbstractObjective {
    private final Material targetItem;

    public CraftObjective(String objectiveId, Material targetItem, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetItem = targetItem;
    }

    public CraftObjective(String objectiveId, Material targetItem, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetItem = targetItem;
    }

    public Material getTargetItem() {
        return this.targetItem;
    }

    @Override
    public String getType() {
        return "craft";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof CraftItemEvent)) {
            return false;
        }
        CraftItemEvent craftEvent = (CraftItemEvent)event;
        if (this.targetItem != null && craftEvent.getRecipe().getResult().getType() != this.targetItem) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        int craftAmount = craftEvent.getRecipe().getResult().getAmount();
        if (craftEvent.isShiftClick()) {
            craftAmount = Math.min(craftAmount * 64, this.getRequiredAmount() - this.getCurrentProgress(playerId));
        }
        this.incrementProgress(playerId, craftAmount);
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
        return "Craft " + this.getRequiredAmount() + " " + itemName;
    }

    @Override
    public String serialize() {
        String item = this.targetItem != null ? this.targetItem.name() : "ANY";
        return this.getType() + ":" + item + ":" + this.getRequiredAmount();
    }

    public static CraftObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid craft objective data: " + data);
        }
        Material material = parts[1].equalsIgnoreCase("ANY") ? null : Material.valueOf((String)parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        return new CraftObjective(objectiveId, material, amount);
    }
}

