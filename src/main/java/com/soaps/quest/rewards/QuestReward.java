/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 */
package com.soaps.quest.rewards;

import com.soaps.quest.rewards.Reward;
import org.bukkit.configuration.ConfigurationSection;

public interface QuestReward
extends Reward {
    public String getType();

    public String getDisplayDescription();

    public void serialize(ConfigurationSection var1);

    @Override
    default public int getChance() {
        return 100;
    }

    @Override
    default public String getDescription() {
        return this.getDisplayDescription();
    }
}

