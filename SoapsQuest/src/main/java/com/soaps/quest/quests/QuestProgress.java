package com.soaps.quest.quests;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a single active quest instance with its own unique UUID.
 * This allows players to have multiple quests of the same type without conflicts.
 * Each quest paper gets its own QuestProgress with a unique questInstanceId.
 */
public class QuestProgress {
    
    // Unique identifier for this specific quest instance
    private final UUID questInstanceId;
    
    // The quest ID (e.g., "zombie_hunter")
    private final String questId;
    
    // Current progress (e.g., killed 5 out of 50 zombies) - FOR LEGACY QUESTS ONLY
    private int currentProgress;
    
    // Required amount to complete (e.g., 50) - FOR LEGACY QUESTS ONLY
    private final int requiredAmount;
    
    // Whether this quest instance has been completed and redeemed
    private boolean redeemed;
    
    // Owner UUID - player who first made progress on this quest (null if not yet bound)
    private UUID ownerUUID;
    
    // Per-objective progress tracking (NEW) - Maps objective ID to progress amount
    private final Map<String, Integer> objectiveProgress;
    
    // Current active objective index for sequential quests (NEW)
    private int currentObjectiveIndex;
    
    /**
     * Constructor for QuestProgress.
     * 
     * @param questInstanceId Unique UUID for this quest instance
     * @param questId The quest identifier (e.g., "zombie_hunter")
     * @param requiredAmount Amount needed to complete
     */
    public QuestProgress(UUID questInstanceId, String questId, int requiredAmount) {
        this.questInstanceId = questInstanceId;
        this.questId = questId;
        this.requiredAmount = requiredAmount;
        this.currentProgress = 0;
        this.redeemed = false;
        this.ownerUUID = null; // Not yet bound to a player
        this.objectiveProgress = new HashMap<>();
        this.currentObjectiveIndex = 0; // Start at first objective
    }
    
    /**
     * Constructor for loading from data.
     * 
     * @param questInstanceId Unique UUID for this quest instance
     * @param questId The quest identifier
     * @param currentProgress Current progress amount
     * @param requiredAmount Required amount to complete
     * @param redeemed Whether quest has been redeemed
     * @param ownerUUID Owner player UUID (null if not bound)
     */
    public QuestProgress(UUID questInstanceId, String questId, int currentProgress, int requiredAmount, boolean redeemed, UUID ownerUUID) {
        this.questInstanceId = questInstanceId;
        this.questId = questId;
        this.currentProgress = currentProgress;
        this.requiredAmount = requiredAmount;
        this.redeemed = redeemed;
        this.ownerUUID = ownerUUID;
        this.objectiveProgress = new HashMap<>();
        this.currentObjectiveIndex = 0; // Start at first objective
    }
    
    /**
     * Constructor for loading from data with objective progress (NEW).
     * 
     * @param questInstanceId Unique UUID for this quest instance
     * @param questId The quest identifier
     * @param currentProgress Current progress amount (legacy)
     * @param requiredAmount Required amount to complete (legacy)
     * @param redeemed Whether quest has been redeemed
     * @param ownerUUID Owner player UUID (null if not bound)
     * @param objectiveProgress Map of objective ID to progress amount
     */
    public QuestProgress(UUID questInstanceId, String questId, int currentProgress, int requiredAmount, boolean redeemed, UUID ownerUUID, Map<String, Integer> objectiveProgress) {
        this.questInstanceId = questInstanceId;
        this.questId = questId;
        this.currentProgress = currentProgress;
        this.requiredAmount = requiredAmount;
        this.redeemed = redeemed;
        this.ownerUUID = ownerUUID;
        this.objectiveProgress = objectiveProgress != null ? new HashMap<>(objectiveProgress) : new HashMap<>();
        this.currentObjectiveIndex = 0; // Start at first objective
    }
    
    /**
     * Get the unique quest instance ID.
     * 
     * @return Quest instance UUID
     */
    public UUID getQuestInstanceId() {
        return questInstanceId;
    }
    
    /**
     * Get the quest ID.
     * 
     * @return Quest ID
     */
    public String getQuestId() {
        return questId;
    }
    
    /**
     * Get current progress.
     * 
     * @return Current progress
     */
    public int getCurrentProgress() {
        return currentProgress;
    }
    
    /**
     * Set current progress.
     * 
     * @param progress New progress amount
     */
    public void setCurrentProgress(int progress) {
        this.currentProgress = Math.min(progress, requiredAmount);
    }
    
    /**
     * Increment progress by 1.
     */
    public void incrementProgress() {
        if (currentProgress < requiredAmount) {
            currentProgress++;
        }
    }
    
    /**
     * Increment progress by a specific amount.
     * 
     * @param amount Amount to add
     */
    public void incrementProgress(int amount) {
        currentProgress = Math.min(currentProgress + amount, requiredAmount);
    }
    
    /**
     * Get required amount.
     * 
     * @return Required amount
     */
    public int getRequiredAmount() {
        return requiredAmount;
    }
    
    /**
     * Check if quest is complete (legacy single-type quests).
     * For multi-objective quests, use isComplete(Quest) instead.
     * 
     * @return True if progress >= required amount
     */
    public boolean isComplete() {
        return currentProgress >= requiredAmount;
    }
    
    /**
     * Check if quest is complete (supports both legacy and multi-objective quests).
     * 
     * @param quest The quest to check completion for
     * @return True if all objectives are complete (or legacy progress >= required)
     */
    public boolean isComplete(Quest quest) {
        if (quest.hasObjectives()) {
            if (quest.isSequential()) {
                // SEQUENTIAL: All objectives must be complete
                // Check if current index is at or beyond the last objective
                java.util.List<com.soaps.quest.objectives.Objective> objectives = quest.getObjectives();
                
                // Must have advanced past the last objective OR be at last with it complete
                if (currentObjectiveIndex < objectives.size() - 1) {
                    // Still have future objectives to complete
                    return false;
                }
                
                // Verify all objectives are complete
                for (com.soaps.quest.objectives.Objective objective : objectives) {
                    int progress = getObjectiveProgress(objective.getObjectiveId());
                    if (progress < objective.getRequiredAmount()) {
                        return false;
                    }
                }
                return true;
            } else {
                // NON-SEQUENTIAL: Check all objectives are complete
                for (com.soaps.quest.objectives.Objective objective : quest.getObjectives()) {
                    int progress = getObjectiveProgress(objective.getObjectiveId());
                    if (progress < objective.getRequiredAmount()) {
                        return false; // At least one objective not complete
                    }
                }
                return true; // All objectives complete
            }
        } else {
            // LEGACY: Check single progress value
            return isComplete();
        }
    }
    
    /**
     * Check if quest has been redeemed.
     * 
     * @return True if redeemed
     */
    public boolean isRedeemed() {
        return redeemed;
    }
    
    /**
     * Mark quest as redeemed.
     */
    public void setRedeemed() {
        this.redeemed = true;
    }
    
    /**
     * Get the owner UUID.
     * 
     * @return Owner UUID, or null if not yet bound
     */
    public UUID getOwnerUUID() {
        return ownerUUID;
    }
    
    /**
     * Set the owner UUID (bind quest to player).
     * Should only be called once when first progress is made.
     * 
     * @param ownerUUID Player UUID
     */
    public void setOwnerUUID(UUID ownerUUID) {
        // Only allow setting once
        if (this.ownerUUID == null) {
            this.ownerUUID = ownerUUID;
        }
    }
    
    /**
     * Check if quest is bound to a player.
     * 
     * @return True if bound to a player
     */
    public boolean isBound() {
        return ownerUUID != null;
    }
    
    /**
     * Get progress for a specific objective.
     * 
     * @param objectiveId Objective ID
     * @return Current progress for that objective
     */
    public int getObjectiveProgress(String objectiveId) {
        return objectiveProgress.getOrDefault(objectiveId, 0);
    }
    
    /**
     * Set progress for a specific objective.
     * 
     * @param objectiveId Objective ID
     * @param progress Progress amount
     */
    public void setObjectiveProgress(String objectiveId, int progress) {
        objectiveProgress.put(objectiveId, progress);
    }
    
    /**
     * Increment progress for a specific objective.
     * 
     * @param objectiveId Objective ID
     * @param amount Amount to increment by
     */
    public void incrementObjectiveProgress(String objectiveId, int amount) {
        int current = getObjectiveProgress(objectiveId);
        setObjectiveProgress(objectiveId, current + amount);
    }
    
    /**
     * Get all objective progress data.
     * 
     * @return Map of objective ID to progress amount
     */
    public Map<String, Integer> getAllObjectiveProgress() {
        return new HashMap<>(objectiveProgress);
    }
    
    /**
     * Get the current active objective index (for sequential quests).
     * 
     * @return Current objective index (0-based)
     */
    public int getCurrentObjectiveIndex() {
        return currentObjectiveIndex;
    }
    
    /**
     * Set the current active objective index (for sequential quests).
     * 
     * @param index New objective index
     */
    public void setCurrentObjectiveIndex(int index) {
        this.currentObjectiveIndex = index;
    }
    
    /**
     * Advance to the next objective (for sequential quests).
     * 
     * @return True if advanced, false if already at last objective
     */
    public boolean advanceToNextObjective() {
        currentObjectiveIndex++;
        return true;
    }
    
    /**
     * Check if an objective is the current active objective (for sequential quests).
     * For non-sequential quests, always returns true.
     * 
     * @param quest The quest
     * @param objectiveIndex The objective index to check
     * @return True if this objective is currently active
     */
    public boolean isObjectiveActive(Quest quest, int objectiveIndex) {
        if (!quest.isSequential()) {
            return true; // All objectives are active for non-sequential quests
        }
        return objectiveIndex == currentObjectiveIndex;
    }
    
    @Override
    public String toString() {
        return "QuestProgress{" +
                "questInstanceId=" + questInstanceId +
                ", questId='" + questId + '\'' +
                ", currentProgress=" + currentProgress +
                ", requiredAmount=" + requiredAmount +
                ", redeemed=" + redeemed +
                ", objectiveProgress=" + objectiveProgress +
                ", currentObjectiveIndex=" + currentObjectiveIndex +
                '}';
    }
}
