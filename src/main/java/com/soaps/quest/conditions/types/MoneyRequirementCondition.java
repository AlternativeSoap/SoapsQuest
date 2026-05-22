/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.economy.Economy
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.RegisteredServiceProvider
 *  org.bukkit.plugin.ServicesManager
 */
package com.soaps.quest.conditions.types;

import com.soaps.quest.conditions.ConditionResult;
import com.soaps.quest.conditions.QuestCondition;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

public class MoneyRequirementCondition
implements QuestCondition {
    private final double amount;
    private final boolean consumable;

    public MoneyRequirementCondition(double amount, boolean consumable) {
        this.amount = amount;
        this.consumable = consumable;
    }

    @Override
    public String getType() {
        return this.consumable ? "money-cost" : "money";
    }

    @Override
    public ConditionResult check(Player player, boolean consumeResources) {
        ServicesManager servicesManager = Bukkit.getServer().getServicesManager();
        RegisteredServiceProvider economyReg = servicesManager.getRegistration(Economy.class);
        if (economyReg == null) {
            return ConditionResult.failure("&cVault/Economy plugin required for this quest!");
        }
        Economy economy = (Economy)economyReg.getProvider();
        double balance = economy.getBalance((OfflinePlayer)player);
        if (balance < this.amount) {
            String message = this.consumable ? String.format("&cThis quest costs $%.2f! (You have: $%.2f)", this.amount, balance) : String.format("&cYou need $%.2f! (You have: $%.2f)", this.amount, balance);
            return ConditionResult.failure(message);
        }
        if (this.consumable && consumeResources) {
            economy.withdrawPlayer((OfflinePlayer)player, this.amount);
        }
        return ConditionResult.success();
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", (Object)this.getType());
        if (this.consumable) {
            section.set("cost", (Object)this.amount);
        } else {
            section.set("min-money", (Object)this.amount);
        }
    }

    @Override
    public String getDescription() {
        if (this.consumable) {
            return String.format("Costs $%.2f to unlock", this.amount);
        }
        return String.format("Requires $%.2f+", this.amount);
    }

    @Override
    public String getDisplayString() {
        if (this.consumable) {
            return String.format("&7Cost: &a$%.2f", this.amount);
        }
        return String.format("&7Money: &a$%.2f+", this.amount);
    }

    public static MoneyRequirementCondition deserialize(ConfigurationSection section) {
        boolean consumable = section.contains("cost");
        double amount = consumable ? section.getDouble("cost", 0.0) : section.getDouble("min-money", 0.0);
        return new MoneyRequirementCondition(amount, consumable);
    }

    public double getAmount() {
        return this.amount;
    }

    public boolean isConsumable() {
        return this.consumable;
    }
}

