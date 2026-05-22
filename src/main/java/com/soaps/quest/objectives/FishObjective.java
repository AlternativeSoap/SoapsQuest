/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.player.PlayerFishEvent
 *  org.bukkit.event.player.PlayerFishEvent$State
 *  org.bukkit.inventory.ItemStack
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class FishObjective
extends AbstractObjective {
    private final Material targetFish;

    public FishObjective(String objectiveId, Material targetFish, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetFish = targetFish;
    }

    public FishObjective(String objectiveId, Material targetFish, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetFish = targetFish;
    }

    public Material getTargetFish() {
        return this.targetFish;
    }

    @Override
    public String getType() {
        return "fish";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerFishEvent)) {
            return false;
        }
        PlayerFishEvent fishEvent = (PlayerFishEvent)event;
        if (fishEvent.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return false;
        }
        Entity entity = fishEvent.getCaught();
        if (!(entity instanceof Item)) {
            return false;
        }
        Item caughtItem = (Item)entity;
        ItemStack caught = caughtItem.getItemStack();
        if (this.targetFish != null && caught.getType() != this.targetFish) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        this.incrementProgress(playerId, caught.getAmount());
        return true;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        String fishName = this.targetFish != null ? this.targetFish.name() : "FISH";
        return current + "/" + this.getRequiredAmount() + " " + fishName;
    }

    @Override
    public String getDescription() {
        String fishName = this.targetFish != null ? this.formatName(this.targetFish.name()) : "any fish";
        return "Catch " + this.getRequiredAmount() + " " + fishName;
    }

    @Override
    public String serialize() {
        String fish = this.targetFish != null ? this.targetFish.name() : "ANY";
        return this.getType() + ":" + fish + ":" + this.getRequiredAmount();
    }

    public static FishObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid fish objective data: " + data);
        }
        Material material = parts[1].equalsIgnoreCase("ANY") ? null : Material.valueOf((String)parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        return new FishObjective(objectiveId, material, amount);
    }
}

