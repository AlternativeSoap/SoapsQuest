/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 */
package com.soaps.quest.conditions;

import com.soaps.quest.conditions.ConditionResult;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public interface QuestCondition {
    public String getType();

    public ConditionResult check(Player var1, boolean var2);

    public void serialize(ConfigurationSection var1);

    public String getDescription();

    default public String getDisplayString() {
        return this.getDescription();
    }
}

