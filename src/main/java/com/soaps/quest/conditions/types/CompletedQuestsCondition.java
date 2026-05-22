/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 */
package com.soaps.quest.conditions.types;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.conditions.ConditionResult;
import com.soaps.quest.conditions.QuestCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CompletedQuestsCondition
implements QuestCondition {
    private final int requiredCount;
    private final SoapsQuest plugin;

    public CompletedQuestsCondition(int requiredCount, SoapsQuest plugin) {
        this.requiredCount = requiredCount;
        this.plugin = plugin;
    }

    @Override
    public ConditionResult check(Player player, boolean consumeResources) {
        int completedCount = this.plugin.getDataManager().getCompletedQuestCount(player);
        if (completedCount >= this.requiredCount) {
            return ConditionResult.success();
        }
        return ConditionResult.failure(String.format("&cYou must complete &e%d &cquests before unlocking this quest. (&e%d&c/&e%d&c)", this.requiredCount, completedCount, this.requiredCount));
    }

    @Override
    public String getType() {
        return "require-completed-quests";
    }

    @Override
    public String getDescription() {
        return "&7Requires &e" + this.requiredCount + " &7completed quests";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", (Object)this.getType());
        section.set("count", (Object)this.requiredCount);
    }

    public static CompletedQuestsCondition deserialize(ConfigurationSection section, SoapsQuest plugin) {
        int count = section.getInt("count", 1);
        return new CompletedQuestsCondition(count, plugin);
    }

    public int getRequiredCount() {
        return this.requiredCount;
    }
}

