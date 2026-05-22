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

public class ActiveLimitCondition
implements QuestCondition {
    private final int limit;
    private final SoapsQuest plugin;

    public ActiveLimitCondition(int limit, SoapsQuest plugin) {
        this.limit = limit;
        this.plugin = plugin;
    }

    @Override
    public String getType() {
        return "active-limit";
    }

    @Override
    public ConditionResult check(Player player, boolean consumeResources) {
        if (!this.plugin.getQuestManager().isWithinActiveLimit(player, null, this.limit)) {
            int activeCount = this.plugin.getQuestManager().countQueueActiveQuestTypes(player);
            return ConditionResult.failure(String.format("&cYou can only have %d active quest types! (Currently: %d)", this.limit, activeCount));
        }
        return ConditionResult.success();
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", (Object)this.getType());
        section.set("active-limit", (Object)this.limit);
    }

    @Override
    public String getDescription() {
        return "Max active quests: " + this.limit;
    }

    @Override
    public String getDisplayString() {
        return "&7Max Active Quests: &f" + this.limit;
    }

    public static ActiveLimitCondition deserialize(ConfigurationSection section, SoapsQuest plugin) {
        int limit = section.getInt("active-limit");
        return new ActiveLimitCondition(limit, plugin);
    }
}

