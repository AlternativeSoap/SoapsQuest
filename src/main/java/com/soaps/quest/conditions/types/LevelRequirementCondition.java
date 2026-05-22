/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 */
package com.soaps.quest.conditions.types;

import com.soaps.quest.conditions.ConditionResult;
import com.soaps.quest.conditions.QuestCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class LevelRequirementCondition
implements QuestCondition {
    private final int minLevel;

    public LevelRequirementCondition(int minLevel) {
        this.minLevel = minLevel;
    }

    @Override
    public String getType() {
        return "level";
    }

    @Override
    public ConditionResult check(Player player, boolean consumeResources) {
        int currentLevel = player.getLevel();
        if (currentLevel < this.minLevel) {
            return ConditionResult.failure(String.format("&cYou need level %d+! (Currently: %d)", this.minLevel, currentLevel));
        }
        return ConditionResult.success();
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", (Object)"level");
        section.set("min-level", (Object)this.minLevel);
    }

    @Override
    public String getDescription() {
        return String.format("Requires Level %d+", this.minLevel);
    }

    @Override
    public String getDisplayString() {
        return "&7Level: &f" + this.minLevel + "+";
    }

    public static LevelRequirementCondition deserialize(ConfigurationSection section) {
        int minLevel = section.getInt("min-level", 1);
        return new LevelRequirementCondition(minLevel);
    }

    public int getMinLevel() {
        return this.minLevel;
    }
}

