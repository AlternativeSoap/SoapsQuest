/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.entity.EntityPickupItemEvent
 *  org.bukkit.inventory.ItemStack
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class CollectObjective
extends AbstractObjective {
    private final Material itemType;

    public CollectObjective(String objectiveId, Material itemType, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.itemType = itemType;
    }

    public CollectObjective(String objectiveId, Material itemType, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.itemType = itemType;
    }

    @Override
    public String getType() {
        return "collect";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof EntityPickupItemEvent)) {
            return false;
        }
        EntityPickupItemEvent pickupEvent = (EntityPickupItemEvent)event;
        if (!(pickupEvent.getEntity() instanceof Player)) {
            return false;
        }
        Player pickupPlayer = (Player)pickupEvent.getEntity();
        if (!pickupPlayer.equals((Object)player)) {
            return false;
        }
        ItemStack item = pickupEvent.getItem().getItemStack();
        return this.itemType == null || item.getType() == this.itemType;
    }

    @Override
    public String getDescription() {
        String itemName = this.itemType != null ? this.formatName(this.itemType.name()) : "any items";
        return "Collect " + this.requiredAmount + " " + itemName;
    }

    @Override
    public String serialize() {
        String item = this.itemType != null ? this.itemType.name() : "ANY";
        return "collect:" + item + ":" + this.requiredAmount;
    }

    public Material getItemType() {
        return this.itemType;
    }
}

