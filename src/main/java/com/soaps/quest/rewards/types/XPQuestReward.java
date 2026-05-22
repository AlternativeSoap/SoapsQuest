/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 */
package com.soaps.quest.rewards.types;

import com.soaps.quest.rewards.QuestReward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class XPQuestReward
implements QuestReward {
    private final int amount;
    private final int chance;

    public XPQuestReward(int amount) {
        this(amount, 100);
    }

    public XPQuestReward(int amount, int chance) {
        this.amount = amount;
        this.chance = Math.max(0, Math.min(100, chance));
    }

    @Override
    public String getType() {
        return "xp";
    }

    @Override
    public boolean give(Player player) {
        player.giveExp(this.amount);
        return true;
    }

    @Override
    public String getDisplayDescription() {
        String base = "&e" + this.amount + " XP";
        if (this.chance < 100) {
            base = base + " &7(" + this.chance + "% chance)";
        }
        return base;
    }

    @Override
    public int getChance() {
        return this.chance;
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", (Object)"xp");
        section.set("amount", (Object)this.amount);
        if (this.chance < 100) {
            section.set("chance", (Object)this.chance);
        }
    }

    public static XPQuestReward deserialize(ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        int amount = section.getInt("amount", 0);
        int chance = section.getInt("chance", 100);
        if (amount <= 0) {
            return null;
        }
        return new XPQuestReward(amount, chance);
    }

    public int getAmount() {
        return this.amount;
    }
}

