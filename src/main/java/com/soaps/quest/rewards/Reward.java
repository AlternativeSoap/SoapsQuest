/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.soaps.quest.rewards;

import org.bukkit.entity.Player;

public interface Reward {
    public boolean give(Player var1);

    public String getDescription();

    public int getChance();

    default public boolean giveWithChance(Player player) {
        int chance = this.getChance();
        if (chance >= 100) {
            return this.give(player);
        }
        int roll = (int)(Math.random() * 100.0);
        if (roll < chance) {
            return this.give(player);
        }
        return false;
    }
}

