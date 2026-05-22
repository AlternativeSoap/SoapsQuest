/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.data.Ageable
 *  org.bukkit.block.data.BlockData
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.block.BlockBreakEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

public class HarvestObjective
extends AbstractObjective {
    private final Material cropType;

    public HarvestObjective(String objectiveId, Material cropType, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.cropType = cropType;
    }

    public HarvestObjective(String objectiveId, Material cropType, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.cropType = cropType;
    }

    @Override
    public String getType() {
        return "harvest";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof BlockBreakEvent)) {
            return false;
        }
        BlockBreakEvent breakEvent = (BlockBreakEvent)event;
        Block block = breakEvent.getBlock();
        Material blockMat = block.getType();
        if (this.cropType != null && blockMat != this.cropType) {
            return false;
        }
        if (blockMat == Material.MELON || blockMat == Material.PUMPKIN) {
            return true;
        }
        BlockData data = block.getBlockData();
        if (data instanceof Ageable) {
            Ageable ageable = (Ageable)data;
            return ageable.getAge() == ageable.getMaximumAge();
        }
        return false;
    }

    @Override
    public String getDescription() {
        String name = this.cropType != null ? this.formatName(this.cropType.name()) : "any crops";
        return "Harvest " + this.requiredAmount + " " + name;
    }

    @Override
    public String serialize() {
        String crop = this.cropType != null ? this.cropType.name() : "ANY";
        return "harvest:" + crop + ":" + this.requiredAmount;
    }

    public Material getCropType() {
        return this.cropType;
    }
}

