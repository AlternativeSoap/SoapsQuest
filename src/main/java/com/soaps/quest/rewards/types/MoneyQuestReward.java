/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.economy.Economy
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 */
package com.soaps.quest.rewards.types;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.rewards.QuestReward;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class MoneyQuestReward
implements QuestReward {
    private final double amount;
    private final SoapsQuest plugin;
    private final int chance;

    public MoneyQuestReward(SoapsQuest plugin, double amount) {
        this(plugin, amount, 100);
    }

    public MoneyQuestReward(SoapsQuest plugin, double amount, int chance) {
        this.plugin = plugin;
        this.amount = amount;
        this.chance = Math.max(0, Math.min(100, chance));
    }

    @Override
    public String getType() {
        return "money";
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
    public String getDisplayDescription() {
        String base = "&a$" + String.format("%.2f", this.amount);
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
        section.set("type", (Object)"money");
        section.set("amount", (Object)this.amount);
        if (this.chance < 100) {
            section.set("chance", (Object)this.chance);
        }
    }

    public static MoneyQuestReward deserialize(ConfigurationSection section, SoapsQuest plugin) {
        if (section == null || plugin == null) {
            return null;
        }
        double amount = section.getDouble("amount", 0.0);
        int chance = section.getInt("chance", 100);
        if (amount <= 0.0) {
            return null;
        }
        return new MoneyQuestReward(plugin, amount, chance);
    }

    public double getAmount() {
        return this.amount;
    }
}

