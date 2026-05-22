/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.economy.Economy
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 */
package com.soaps.quest.rewards;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.rewards.Reward;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MoneyReward
implements Reward {
    private final double amount;
    private final SoapsQuest plugin;
    private final int chance;

    public MoneyReward(SoapsQuest plugin, double amount) {
        this(plugin, amount, 100);
    }

    public MoneyReward(SoapsQuest plugin, double amount, int chance) {
        this.plugin = plugin;
        this.amount = amount;
        this.chance = Math.max(0, Math.min(100, chance));
    }

    @Override
    public boolean give(Player player) {
        Economy economy = this.plugin.getEconomy();
        if (economy == null) {
            this.plugin.getLogger().warning("Cannot give money reward - Vault economy not found!");
            return false;
        }
        economy.depositPlayer((OfflinePlayer)player, this.amount);
        return true;
    }

    @Override
    public String getDescription() {
        return "$" + this.amount;
    }

    public double getAmount() {
        return this.amount;
    }

    @Override
    public int getChance() {
        return this.chance;
    }
}

