/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.inventory.BrewEvent
 *  org.bukkit.inventory.BrewerInventory
 *  org.bukkit.inventory.ItemStack
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

public class BrewObjective
extends AbstractObjective {
    private final Material targetPotion;

    public BrewObjective(String objectiveId, Material targetPotion, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetPotion = targetPotion;
    }

    public BrewObjective(String objectiveId, Material targetPotion, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetPotion = targetPotion;
    }

    public Material getTargetPotion() {
        return this.targetPotion;
    }

    @Override
    public String getType() {
        return "brew";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof BrewEvent)) {
            return false;
        }
        BrewEvent brewEvent = (BrewEvent)event;
        BrewerInventory inventory = brewEvent.getContents();
        int matchingPotions = 0;
        for (int i = 0; i < 3; ++i) {
            ItemStack item = inventory.getItem(i);
            if (item == null || this.targetPotion != null && item.getType() != this.targetPotion) continue;
            ++matchingPotions;
        }
        if (matchingPotions == 0) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        this.incrementProgress(playerId, matchingPotions);
        return true;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        String potionName = this.targetPotion != null ? this.targetPotion.name() : "POTIONS";
        return current + "/" + this.getRequiredAmount() + " " + potionName;
    }

    @Override
    public String getDescription() {
        String potionName = this.targetPotion != null ? this.formatName(this.targetPotion.name()) : "any potions";
        return "Brew " + this.getRequiredAmount() + " " + potionName;
    }

    @Override
    public String serialize() {
        String potion = this.targetPotion != null ? this.targetPotion.name() : "ANY";
        return this.getType() + ":" + potion + ":" + this.getRequiredAmount();
    }

    public static BrewObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid brew objective data: " + data);
        }
        Material material = parts[1].equalsIgnoreCase("ANY") ? null : Material.valueOf((String)parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        return new BrewObjective(objectiveId, material, amount);
    }
}

