package com.soaps.quest.rewards;

import com.soaps.quest.SoapsQuest;
import org.bukkit.entity.Player;

public class SigilReward implements Reward {
    private final SoapsQuest plugin;
    private final double amount;
    private final int chance;

    public SigilReward(SoapsQuest plugin, double amount, int chance) {
        this.plugin = plugin;
        this.amount = amount;
        this.chance = Math.max(0, Math.min(100, chance));
    }

    public SigilReward(SoapsQuest plugin, double amount) {
        this(plugin, amount, 100);
    }

    @Override
    public boolean give(Player player) {
        if (this.plugin.getSigilManager() == null) {
            return false;
        }
        this.plugin.getSigilManager().give(player.getUniqueId(), this.amount);
        return true;
    }

    @Override
    public String getDescription() {
        return String.format("%.2f sigils", this.amount);
    }

    public double getAmount() {
        return this.amount;
    }

    @Override
    public int getChance() {
        return this.chance;
    }
}
