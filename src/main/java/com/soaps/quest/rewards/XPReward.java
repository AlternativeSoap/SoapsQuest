/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.soaps.quest.rewards;

import com.soaps.quest.rewards.Reward;
import org.bukkit.entity.Player;

public class XPReward
implements Reward {
    private final int amount;
    private final int chance;

    public XPReward(int amount) {
        this(amount, 100);
    }

    public XPReward(int amount, int chance) {
        this.amount = amount;
        this.chance = Math.max(0, Math.min(100, chance));
    }

    @Override
    public boolean give(Player player) {
        player.giveExp(this.amount);
        return true;
    }

    @Override
    public String getDescription() {
        return this.amount + " XP";
    }

    public int getAmount() {
        return this.amount;
    }

    @Override
    public int getChance() {
        return this.chance;
    }
}

