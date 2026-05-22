/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.AnvilInventory
 *  org.bukkit.inventory.ItemStack
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class AnvilRepairObjective
extends AbstractObjective {
    private final Material targetMaterial;

    public AnvilRepairObjective(String objectiveId, Material targetMaterial, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetMaterial = targetMaterial;
    }

    public AnvilRepairObjective(String objectiveId, Material targetMaterial, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetMaterial = targetMaterial;
    }

    @Override
    public String getType() {
        return "anvil_repair";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof InventoryClickEvent)) {
            return false;
        }
        InventoryClickEvent clickEvent = (InventoryClickEvent)event;
        if (!(clickEvent.getInventory() instanceof AnvilInventory)) {
            return false;
        }
        if (clickEvent.getRawSlot() != 2) {
            return false;
        }
        ItemStack result = clickEvent.getCurrentItem();
        if (result == null || result.getType() == Material.AIR) {
            return false;
        }
        if (this.targetMaterial != null) {
            return result.getType() == this.targetMaterial;
        }
        return true;
    }

    @Override
    public String getDescription() {
        String mat = this.targetMaterial != null ? this.formatName(this.targetMaterial.name()) : "any item";
        return "Repair " + this.requiredAmount + " " + mat + " on an anvil";
    }

    @Override
    public String serialize() {
        String mat = this.targetMaterial != null ? this.targetMaterial.name() : "ANY";
        return "anvil_repair:" + mat + ":" + this.requiredAmount;
    }

    public Material getTargetMaterial() {
        return this.targetMaterial;
    }
}

