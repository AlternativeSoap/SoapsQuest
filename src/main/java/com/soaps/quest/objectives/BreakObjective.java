/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.block.BlockBreakEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakObjective
extends AbstractObjective {
    private final Material blockType;

    public BreakObjective(String objectiveId, Material blockType, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.blockType = blockType;
    }

    public BreakObjective(String objectiveId, Material blockType, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.blockType = blockType;
    }

    @Override
    public String getType() {
        return "break";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof BlockBreakEvent)) {
            return false;
        }
        BlockBreakEvent breakEvent = (BlockBreakEvent)event;
        return this.blockType == null || breakEvent.getBlock().getType() == this.blockType;
    }

    @Override
    public String getDescription() {
        String blockName = this.blockType != null ? this.formatName(this.blockType.name()) : "any blocks";
        return "Break " + this.requiredAmount + " " + blockName;
    }

    @Override
    public String serialize() {
        String block = this.blockType != null ? this.blockType.name() : "ANY";
        return "break:" + block + ":" + this.requiredAmount;
    }

    public Material getBlockType() {
        return this.blockType;
    }
}

