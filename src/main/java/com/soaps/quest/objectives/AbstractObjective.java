/*
 * Decompiled with CFR 0.152.
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.Objective;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractObjective
implements Objective {
    protected final String objectiveId;
    protected final int requiredAmount;
    protected final Map<UUID, Integer> progressMap;
    protected final List<Integer> milestones;
    protected final Map<UUID, Set<Integer>> reachedMilestones;

    public AbstractObjective(String objectiveId, int requiredAmount) {
        this(objectiveId, requiredAmount, null);
    }

    public AbstractObjective(String objectiveId, int requiredAmount, List<Integer> milestones) {
        this.objectiveId = objectiveId;
        this.requiredAmount = requiredAmount;
        this.progressMap = new HashMap<UUID, Integer>();
        this.milestones = milestones != null ? new ArrayList<Integer>(milestones) : new ArrayList();
        this.reachedMilestones = new HashMap<UUID, Set<Integer>>();
    }

    @Override
    public String getObjectiveId() {
        return this.objectiveId;
    }

    @Override
    public int getCurrentProgress(UUID playerUUID) {
        return this.progressMap.getOrDefault(playerUUID, 0);
    }

    @Override
    public void setCurrentProgress(UUID playerUUID, int progress) {
        this.progressMap.put(playerUUID, Math.min(progress, this.requiredAmount));
    }

    @Override
    public int getRequiredAmount() {
        return this.requiredAmount;
    }

    @Override
    public void incrementProgress(UUID playerUUID) {
        this.incrementProgress(playerUUID, 1);
    }

    @Override
    public void incrementProgress(UUID playerUUID, int amount) {
        int oldProgress = this.getCurrentProgress(playerUUID);
        int newProgress = oldProgress + amount;
        this.setCurrentProgress(playerUUID, newProgress);
        this.checkNewMilestones(playerUUID, oldProgress, newProgress);
    }

    @Override
    public boolean isComplete(UUID playerUUID) {
        return this.getCurrentProgress(playerUUID) >= this.requiredAmount;
    }

    @Override
    public String getProgressString(UUID playerUUID) {
        return this.getCurrentProgress(playerUUID) + "/" + this.requiredAmount;
    }

    @Override
    public void resetProgress(UUID playerUUID) {
        this.progressMap.remove(playerUUID);
    }

    public Map<UUID, Integer> getAllProgress() {
        return new HashMap<UUID, Integer>(this.progressMap);
    }

    @Override
    public List<Integer> getMilestones() {
        return new ArrayList<Integer>(this.milestones);
    }

    @Override
    public boolean hasMilestones() {
        return this.milestones != null && !this.milestones.isEmpty();
    }

    @Override
    public Set<Integer> getReachedMilestones(UUID playerUUID) {
        return new HashSet<Integer>(this.reachedMilestones.getOrDefault(playerUUID, new HashSet()));
    }

    @Override
    public List<Integer> checkNewMilestones(UUID playerUUID, int oldProgress, int newProgress) {
        if (!this.hasMilestones()) {
            return new ArrayList<Integer>();
        }
        ArrayList<Integer> newMilestones = new ArrayList<Integer>();
        Set reached = this.reachedMilestones.computeIfAbsent(playerUUID, k -> new HashSet());
        double oldPercent = (double)oldProgress / (double)this.requiredAmount * 100.0;
        double newPercent = (double)newProgress / (double)this.requiredAmount * 100.0;
        for (Integer milestone : this.milestones) {
            if (reached.contains(milestone) || !(oldPercent < (double)milestone.intValue()) || !(newPercent >= (double)milestone.intValue())) continue;
            reached.add(milestone);
            newMilestones.add(milestone);
        }
        return newMilestones;
    }

    protected String formatName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        String[] parts = name.toLowerCase().split("_");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length; ++i) {
            if (i > 0) {
                result.append(" ");
            }
            result.append(this.capitalize(parts[i]));
        }
        return result.toString();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}

