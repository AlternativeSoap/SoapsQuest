/*
 * Decompiled with CFR 0.152.
 */
package com.soaps.quest.quests;

import com.soaps.quest.objectives.Objective;
import com.soaps.quest.quests.Quest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuestProgress {
    private final UUID questInstanceId;
    private final String questId;
    private int currentProgress;
    private final int requiredAmount;
    private boolean redeemed;
    private boolean claimable;
    private UUID ownerUUID;
    private final Map<String, Integer> objectiveProgress;
    private int currentObjectiveIndex;

    private QuestProgress(Builder builder) {
        this.questInstanceId = builder.questInstanceId;
        this.questId = builder.questId;
        this.currentProgress = builder.currentProgress;
        this.requiredAmount = builder.requiredAmount;
        this.redeemed = builder.redeemed;
        this.claimable = builder.claimable;
        this.ownerUUID = builder.ownerUUID;
        this.objectiveProgress = builder.objectiveProgress != null ? new HashMap<String, Integer>(builder.objectiveProgress) : new HashMap();
        this.currentObjectiveIndex = builder.currentObjectiveIndex;
    }

    public static Builder builder(UUID questInstanceId, String questId, int requiredAmount) {
        return new Builder(questInstanceId, questId, requiredAmount);
    }

    public UUID getQuestInstanceId() {
        return this.questInstanceId;
    }

    public String getQuestId() {
        return this.questId;
    }

    public int getCurrentProgress() {
        return this.currentProgress;
    }

    public void setCurrentProgress(int progress) {
        this.currentProgress = Math.min(progress, this.requiredAmount);
    }

    public void incrementProgress() {
        if (this.currentProgress < this.requiredAmount) {
            ++this.currentProgress;
        }
    }

    public void incrementProgress(int amount) {
        this.currentProgress = Math.min(this.currentProgress + amount, this.requiredAmount);
    }

    public int getRequiredAmount() {
        return this.requiredAmount;
    }

    public boolean isComplete() {
        return this.currentProgress >= this.requiredAmount;
    }

    public boolean isComplete(Quest quest) {
        boolean complete;
        if (quest.hasObjectives()) {
            if (quest.isSequential()) {
                List<Objective> objectives = quest.getObjectives();
                if (this.currentObjectiveIndex < objectives.size() - 1) {
                    return false;
                }
                for (Objective objective : objectives) {
                    int progress = this.getObjectiveProgress(objective.getObjectiveId());
                    if (progress >= objective.getRequiredAmount()) continue;
                    return false;
                }
                complete = true;
            } else {
                for (Objective objective : quest.getObjectives()) {
                    int progress = this.getObjectiveProgress(objective.getObjectiveId());
                    if (progress >= objective.getRequiredAmount()) continue;
                    return false;
                }
                complete = true;
            }
        } else {
            complete = this.isComplete();
        }
        return complete;
    }

    public boolean isRedeemed() {
        return this.redeemed;
    }

    public void setRedeemed() {
        this.redeemed = true;
    }

    public boolean isClaimable() {
        return this.claimable;
    }

    public void setClaimable(boolean claimable) {
        this.claimable = claimable;
    }

    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        if (this.ownerUUID == null) {
            this.ownerUUID = ownerUUID;
        }
    }

    public boolean isBound() {
        return this.ownerUUID != null;
    }

    public int getObjectiveProgress(String objectiveId) {
        return this.objectiveProgress.getOrDefault(objectiveId, 0);
    }

    public void setObjectiveProgress(String objectiveId, int progress) {
        this.objectiveProgress.put(objectiveId, progress);
    }

    public void incrementObjectiveProgress(String objectiveId, int amount) {
        int current = this.getObjectiveProgress(objectiveId);
        this.setObjectiveProgress(objectiveId, current + amount);
    }

    public Map<String, Integer> getAllObjectiveProgress() {
        return new HashMap<String, Integer>(this.objectiveProgress);
    }

    public int getCurrentObjectiveIndex() {
        return this.currentObjectiveIndex;
    }

    public void setCurrentObjectiveIndex(int index) {
        this.currentObjectiveIndex = index;
    }

    public boolean advanceToNextObjective(int totalObjectives) {
        if (this.currentObjectiveIndex >= totalObjectives - 1) {
            return false;
        }
        ++this.currentObjectiveIndex;
        return true;
    }

    public boolean isObjectiveActive(Quest quest, int objectiveIndex) {
        if (!quest.isSequential()) {
            return true;
        }
        return objectiveIndex == this.currentObjectiveIndex;
    }

    public String toString() {
        return "QuestProgress{questInstanceId=" + String.valueOf(this.questInstanceId) + ", questId='" + this.questId + "', currentProgress=" + this.currentProgress + ", requiredAmount=" + this.requiredAmount + ", redeemed=" + this.redeemed + ", objectiveProgress=" + String.valueOf(this.objectiveProgress) + ", currentObjectiveIndex=" + this.currentObjectiveIndex + "}";
    }

    public static class Builder {
        private final UUID questInstanceId;
        private final String questId;
        private final int requiredAmount;
        private int currentProgress = 0;
        private boolean redeemed = false;
        private boolean claimable = false;
        private UUID ownerUUID = null;
        private Map<String, Integer> objectiveProgress = null;
        private int currentObjectiveIndex = 0;

        private Builder(UUID questInstanceId, String questId, int requiredAmount) {
            this.questInstanceId = questInstanceId;
            this.questId = questId;
            this.requiredAmount = requiredAmount;
        }

        public Builder currentProgress(int currentProgress) {
            this.currentProgress = currentProgress;
            return this;
        }

        public Builder redeemed(boolean redeemed) {
            this.redeemed = redeemed;
            return this;
        }

        public Builder claimable(boolean claimable) {
            this.claimable = claimable;
            return this;
        }

        public Builder ownerUUID(UUID ownerUUID) {
            this.ownerUUID = ownerUUID;
            return this;
        }

        public Builder objectiveProgress(Map<String, Integer> objectiveProgress) {
            this.objectiveProgress = objectiveProgress;
            return this;
        }

        public Builder currentObjectiveIndex(int currentObjectiveIndex) {
            this.currentObjectiveIndex = currentObjectiveIndex;
            return this;
        }

        public QuestProgress build() {
            return new QuestProgress(this);
        }
    }
}

