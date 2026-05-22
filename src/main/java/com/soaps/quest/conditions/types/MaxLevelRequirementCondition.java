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

public class MaxLevelRequirementCondition
implements QuestCondition {
    private final int maxLevel;

    public MaxLevelRequirementCondition(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    @Override
    public String getType() {
        return "max-level";
    }

    @Override
    public ConditionResult check(Player player, boolean consumeResources) {
        if (player.getLevel() > this.maxLevel) {
            return ConditionResult.failure(String.format("&cYou must be level %d or below! (Currently: %d)", this.maxLevel, player.getLevel()));
        }
        return ConditionResult.success();
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", (Object)this.getType());
        section.set("max-level", (Object)this.maxLevel);
    }

    @Override
    public String getDescription() {
        return "Max level: " + this.maxLevel;
    }

    @Override
    public String getDisplayString() {
        return "&7Max Level: &f" + this.maxLevel;
    }

    public static MaxLevelRequirementCondition deserialize(ConfigurationSection section) {
        int maxLevel = section.getInt("max-level");
        return new MaxLevelRequirementCondition(maxLevel);
    }
}

