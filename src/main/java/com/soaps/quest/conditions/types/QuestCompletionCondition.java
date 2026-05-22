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
import com.soaps.quest.quests.Quest;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class QuestCompletionCondition
implements QuestCondition {
    private final List<String> requiredQuests;
    private final SoapsQuest plugin;

    public QuestCompletionCondition(List<String> requiredQuests, SoapsQuest plugin) {
        this.requiredQuests = requiredQuests;
        this.plugin = plugin;
    }

    @Override
    public ConditionResult check(Player player, boolean consumeResources) {
        StringBuilder missingQuests = new StringBuilder();
        int missingCount = 0;
        for (String questId : this.requiredQuests) {
            String displayName;
            if (this.plugin.getDataManager().hasCompletedQuest(player, questId)) continue;
            Quest quest = this.plugin.getQuestManager().getQuest(questId);
            String string = displayName = quest != null ? quest.getDisplay() : questId;
            if (missingCount > 0) {
                missingQuests.append("&7, ");
            }
            missingQuests.append("&e").append(displayName);
            ++missingCount;
        }
        if (missingCount == 0) {
            return ConditionResult.success();
        }
        String message = String.format("&cYou must complete %s &cbefore unlocking this quest.", missingQuests.toString());
        return ConditionResult.failure(message);
    }

    @Override
    public String getType() {
        return "require-quest-completed";
    }

    @Override
    public String getDescription() {
        if (this.requiredQuests.size() == 1) {
            Quest quest = this.plugin.getQuestManager().getQuest(this.requiredQuests.get(0));
            String displayName = quest != null ? quest.getDisplay() : this.requiredQuests.get(0);
            return "&7Requires quest: &e" + displayName;
        }
        return "&7Requires &e" + this.requiredQuests.size() + " &7quests to be completed";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", (Object)this.getType());
        section.set("quests", this.requiredQuests);
    }

    public static QuestCompletionCondition deserialize(ConfigurationSection section, SoapsQuest plugin) {
        List quests = section.getStringList("quests");
        return new QuestCompletionCondition(quests, plugin);
    }

    public List<String> getRequiredQuests() {
        return this.requiredQuests;
    }
}

