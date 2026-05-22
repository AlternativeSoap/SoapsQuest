/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractObjective
extends AbstractObjective {
    private final Material targetBlock;

    public InteractObjective(String objectiveId, Material targetBlock, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetBlock = targetBlock;
    }

    public InteractObjective(String objectiveId, Material targetBlock, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetBlock = targetBlock;
    }

    public Material getTargetBlock() {
        return this.targetBlock;
    }

    @Override
    public String getType() {
        return "interact";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerInteractEvent)) {
            return false;
        }
        PlayerInteractEvent interactEvent = (PlayerInteractEvent)event;
        if (interactEvent.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }
        Block block = interactEvent.getClickedBlock();
        if (block == null) {
            return false;
        }
        if (this.targetBlock != null && block.getType() != this.targetBlock) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        this.incrementProgress(playerId);
        return true;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        String blockName = this.targetBlock != null ? this.targetBlock.name() : "BLOCKS";
        return current + "/" + this.getRequiredAmount() + " " + blockName;
    }

    @Override
    public String getDescription() {
        String blockName = this.targetBlock != null ? this.formatName(this.targetBlock.name()) : "any blocks";
        return "Interact with " + this.getRequiredAmount() + " " + blockName;
    }

    @Override
    public String serialize() {
        String block = this.targetBlock != null ? this.targetBlock.name() : "ANY";
        return this.getType() + ":" + block + ":" + this.getRequiredAmount();
    }

    public static InteractObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid interact objective data: " + data);
        }
        Material material = parts[1].equalsIgnoreCase("ANY") ? null : Material.valueOf((String)parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        return new InteractObjective(objectiveId, material, amount);
    }
}

