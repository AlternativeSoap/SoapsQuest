package com.soaps.quest.objectives;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Abstract base class for all objectives.
 * Provides common implementation for progress tracking and storage.
 */
public abstract class AbstractObjective implements Objective {
    
    protected final String objectiveId;
    protected final int requiredAmount;
    protected final Map<UUID, Integer> progressMap;
    protected final List<Integer> milestones; // Milestone percentages
    protected final Map<UUID, Set<Integer>> reachedMilestones; // Track which milestones each player has reached
    
    /**
     * Constructor for AbstractObjective.
     * 
     * @param objectiveId Unique identifier for this objective instance
     * @param requiredAmount Amount required to complete
     */
    public AbstractObjective(String objectiveId, int requiredAmount) {
        this(objectiveId, requiredAmount, null);
    }
    
    /**
     * Constructor for AbstractObjective with milestones.
     * 
     * @param objectiveId Unique identifier for this objective instance
     * @param requiredAmount Amount required to complete
     * @param milestones Custom milestone percentages (null for none)
     */
    public AbstractObjective(String objectiveId, int requiredAmount, List<Integer> milestones) {
        this.objectiveId = objectiveId;
        this.requiredAmount = requiredAmount;
        this.progressMap = new HashMap<>();
        this.milestones = milestones != null ? new ArrayList<>(milestones) : new ArrayList<>();
        this.reachedMilestones = new HashMap<>();
    }
    
    @Override
    public String getObjectiveId() {
        return objectiveId;
    }
    
    @Override
    public int getCurrentProgress(UUID playerUUID) {
        return progressMap.getOrDefault(playerUUID, 0);
    }
    
    @Override
    public void setCurrentProgress(UUID playerUUID, int progress) {
        progressMap.put(playerUUID, Math.min(progress, requiredAmount));
    }
    
    @Override
    public int getRequiredAmount() {
        return requiredAmount;
    }
    
    @Override
    public void incrementProgress(UUID playerUUID) {
        incrementProgress(playerUUID, 1);
    }
    
    @Override
    public void incrementProgress(UUID playerUUID, int amount) {
        int oldProgress = getCurrentProgress(playerUUID);
        int newProgress = oldProgress + amount;
        setCurrentProgress(playerUUID, newProgress);
        
        // Check for newly reached milestones (tracking is automatic)
        checkNewMilestones(playerUUID, oldProgress, newProgress);
    }
    
    @Override
    public boolean isComplete(UUID playerUUID) {
        return getCurrentProgress(playerUUID) >= requiredAmount;
    }
    
    @Override
    public String getProgressString(UUID playerUUID) {
        return getCurrentProgress(playerUUID) + "/" + requiredAmount;
    }
    
    @Override
    public void resetProgress(UUID playerUUID) {
        progressMap.remove(playerUUID);
    }
    
    /**
     * Get all player progress data.
     * Used for serialization and debugging.
     * 
     * @return Map of UUID to progress amount
     */
    public Map<UUID, Integer> getAllProgress() {
        return new HashMap<>(progressMap);
    }
    
    @Override
    public List<Integer> getMilestones() {
        return new ArrayList<>(milestones);
    }
    
    @Override
    public boolean hasMilestones() {
        return milestones != null && !milestones.isEmpty();
    }
    
    @Override
    public Set<Integer> getReachedMilestones(UUID playerUUID) {
        return new HashSet<>(reachedMilestones.getOrDefault(playerUUID, new HashSet<>()));
    }
    
    @Override
    public List<Integer> checkNewMilestones(UUID playerUUID, int oldProgress, int newProgress) {
        if (!hasMilestones()) {
            return new ArrayList<>();
        }
        
        List<Integer> newMilestones = new ArrayList<>();
        Set<Integer> reached = reachedMilestones.computeIfAbsent(playerUUID, k -> new HashSet<>());
        
        // Calculate progress percentages
        double oldPercent = (double) oldProgress / requiredAmount * 100.0;
        double newPercent = (double) newProgress / requiredAmount * 100.0;
        
        // Check each milestone
        for (Integer milestone : milestones) {
            // If this milestone hasn't been reached yet, and we've now passed it
            if (!reached.contains(milestone) && oldPercent < milestone && newPercent >= milestone) {
                reached.add(milestone);
                newMilestones.add(milestone);
            }
        }
        
        return newMilestones;
    }
}
