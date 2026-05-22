/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.MerchantInventory
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

public class TradeObjective
extends AbstractObjective {
    private final Material targetItem;

    public TradeObjective(String objectiveId, Material targetItem, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetItem = targetItem;
    }

    public TradeObjective(String objectiveId, Material targetItem, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetItem = targetItem;
    }

    public Material getTargetItem() {
        return this.targetItem;
    }

    @Override
    public String getType() {
        return "trade";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof InventoryClickEvent)) {
            return false;
        }
        InventoryClickEvent clickEvent = (InventoryClickEvent)event;
        Inventory inventory = clickEvent.getInventory();
        if (!(inventory instanceof MerchantInventory)) {
            return false;
        }
        if (clickEvent.getRawSlot() != 2) {
            return false;
        }
        ItemStack result = clickEvent.getCurrentItem();
        if (result == null || result.getType() == Material.AIR) {
            return false;
        }
        if (this.targetItem != null && result.getType() != this.targetItem) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        this.incrementProgress(playerId, result.getAmount());
        return true;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        String itemName = this.targetItem != null ? this.targetItem.name() : "TRADES";
        return current + "/" + this.getRequiredAmount() + " " + itemName;
    }

    @Override
    public String getDescription() {
        String itemName = this.targetItem != null ? this.formatName(this.targetItem.name()) : "any items";
        return "Trade " + this.getRequiredAmount() + " " + itemName;
    }

    @Override
    public String serialize() {
        String item = this.targetItem != null ? this.targetItem.name() : "ANY";
        return this.getType() + ":" + item + ":" + this.getRequiredAmount();
    }

    public static TradeObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid trade objective data: " + data);
        }
        Material material = parts[1].equalsIgnoreCase("ANY") ? null : Material.valueOf((String)parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        return new TradeObjective(objectiveId, material, amount);
    }
}

