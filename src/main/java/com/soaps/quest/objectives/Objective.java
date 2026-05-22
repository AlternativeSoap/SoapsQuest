/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 */
package com.soaps.quest.objectives;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public interface Objective {
    public String getObjectiveId();

    public String getType();

    public int getCurrentProgress(UUID var1);

    public void setCurrentProgress(UUID var1, int var2);

    public int getRequiredAmount();

    public void incrementProgress(UUID var1);

    public void incrementProgress(UUID var1, int var2);

    public boolean isComplete(UUID var1);

    public boolean handleEvent(Player var1, Event var2);

    public String getProgressString(UUID var1);

    public String getDescription();

    public void resetProgress(UUID var1);

    public String serialize();

    public List<Integer> getMilestones();

    public boolean hasMilestones();

    public Set<Integer> getReachedMilestones(UUID var1);

    public List<Integer> checkNewMilestones(UUID var1, int var2, int var3);
}

