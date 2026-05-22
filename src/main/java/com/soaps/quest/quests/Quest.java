/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 */
package com.soaps.quest.quests;

import com.soaps.quest.objectives.Objective;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Quest {
    private final String questId;
    private final String display;
    private final int requiredAmount;
    private final List<String> customLore;
    private final Material material;
    private final boolean lockToPlayer;
    private final List<Objective> objectives;
    private final boolean sequential;
    private final String permission;
    private final String tier;
    private final List<Integer> milestones;
    private final String difficulty;
    private final ConfigurationSection conditions;
    private final boolean temporary;

    private Quest(Builder builder) {
        this.questId = builder.questId;
        this.display = builder.display;
        this.requiredAmount = builder.requiredAmount;
        this.customLore = builder.customLore;
        this.material = builder.material != null ? builder.material : Material.PAPER;
        this.lockToPlayer = builder.lockToPlayer;
        this.objectives = builder.objectives != null ? new ArrayList<Objective>(builder.objectives) : new ArrayList();
        this.sequential = builder.sequential;
        this.permission = builder.permission;
        this.tier = builder.tier != null ? builder.tier : "common";
        this.milestones = builder.milestones != null ? new ArrayList<Integer>(builder.milestones) : new ArrayList();
        this.difficulty = builder.difficulty != null ? builder.difficulty : "normal";
        this.conditions = builder.conditions;
        this.temporary = builder.temporary;
    }

    public static Builder builder(String questId, String display) {
        return new Builder(questId, display);
    }

    public String getQuestId() {
        return this.questId;
    }

    public String getDisplay() {
        return this.display;
    }

    public int getRequiredAmount() {
        return this.requiredAmount;
    }

    public List<String> getCustomLore() {
        return this.customLore;
    }

    public Material getMaterial() {
        return this.material;
    }

    public boolean isLockToPlayer() {
        return this.lockToPlayer;
    }

    public boolean checkProgress(Player player, Object context) {
        return false;
    }

    public String getObjectiveDescription() {
        if (this.objectives.isEmpty()) {
            return "No objectives";
        }
        if (this.objectives.size() == 1) {
            return this.objectives.get(0).getDescription();
        }
        return this.objectives.size() + " objectives";
    }

    public List<Objective> getObjectives() {
        return new ArrayList<Objective>(this.objectives);
    }

    public void resetObjectiveRuntimeState(UUID playerUuid) {
        for (Objective objective : this.objectives) {
            objective.resetProgress(playerUuid);
        }
    }

    public void addObjective(Objective objective) {
        this.objectives.add(objective);
    }

    public boolean hasObjectives() {
        return !this.objectives.isEmpty();
    }

    public boolean isSequential() {
        return this.sequential;
    }

    public String getPermission() {
        return this.permission;
    }

    public boolean hasPermissionRequirement() {
        return this.permission != null && !this.permission.isEmpty();
    }

    public boolean hasPermission(Player player) {
        if (!this.hasPermissionRequirement()) {
            return true;
        }
        return player.hasPermission(this.permission);
    }

    public String getTier() {
        return this.tier;
    }

    public List<Integer> getMilestones() {
        return new ArrayList<Integer>(this.milestones);
    }

    public String getDifficulty() {
        return this.difficulty;
    }

    public ConfigurationSection getConditions() {
        return this.conditions;
    }

    public boolean hasMilestones() {
        return this.milestones != null && !this.milestones.isEmpty();
    }

    public boolean isTemporary() {
        return this.temporary;
    }

    public static class Builder {
        private final String questId;
        private final String display;
        private int requiredAmount = 0;
        private List<String> customLore;
        private Material material;
        private boolean lockToPlayer = true;
        private List<Objective> objectives;
        private boolean sequential = false;
        private String permission;
        private String tier;
        private List<Integer> milestones;
        private String difficulty;
        private ConfigurationSection conditions;
        private boolean temporary = false;

        private Builder(String questId, String display) {
            this.questId = questId;
            this.display = display;
        }

        public Builder requiredAmount(int requiredAmount) {
            this.requiredAmount = requiredAmount;
            return this;
        }

        public Builder customLore(List<String> customLore) {
            this.customLore = customLore;
            return this;
        }

        public Builder material(Material material) {
            this.material = material;
            return this;
        }

        public Builder lockToPlayer(boolean lockToPlayer) {
            this.lockToPlayer = lockToPlayer;
            return this;
        }

        public Builder objectives(List<Objective> objectives) {
            this.objectives = objectives;
            return this;
        }

        public Builder sequential(boolean sequential) {
            this.sequential = sequential;
            return this;
        }

        public Builder permission(String permission) {
            this.permission = permission;
            return this;
        }

        public Builder tier(String tier) {
            this.tier = tier;
            return this;
        }

        public Builder milestones(List<Integer> milestones) {
            this.milestones = milestones;
            return this;
        }

        public Builder difficulty(String difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public Builder conditions(ConfigurationSection conditions) {
            this.conditions = conditions;
            return this;
        }

        public Builder temporary(boolean temporary) {
            this.temporary = temporary;
            return this;
        }

        public Quest build() {
            return new Quest(this);
        }
    }
}

