/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.soaps.quest.rewards.types;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.rewards.QuestReward;
import com.soaps.quest.utils.QuestPaper;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QuestQuestReward
implements QuestReward {
    private final SoapsQuest plugin;
    private final String questId;
    private final int chance;

    public QuestQuestReward(SoapsQuest plugin, String questId) {
        this(plugin, questId, 100);
    }

    public QuestQuestReward(SoapsQuest plugin, String questId, int chance) {
        this.plugin = plugin;
        this.questId = questId;
        this.chance = Math.max(0, Math.min(100, chance));
    }

    @Override
    public String getType() {
        return "quest";
    }

    @Override
    public boolean give(Player player) {
        Quest targetQuest = this.plugin.getQuestManager().getQuest(this.questId);
        if (targetQuest == null) {
            player.sendMessage(this.plugin.getMessageManager().getMessage("reward-quest-failed-unknown", Map.of("quest", this.questId)));
            this.plugin.getLogger().log(Level.WARNING, "Quest reward failed: Quest ID {0} not found", this.questId);
            return false;
        }
        ItemStack questPaper = QuestPaper.createQuestPaper(targetQuest, player, this.plugin.getMessageManager(), this.plugin.getQuestIdKey(), this.plugin.getPlayerUuidKey());
        if (questPaper == null) {
            player.sendMessage(this.plugin.getMessageManager().getMessage("reward-quest-failed-creation"));
            this.plugin.getLogger().log(Level.WARNING, "Quest reward failed: Could not create quest paper for quest {0}", this.questId);
            return false;
        }
        UUID questInstanceUuid = QuestPaper.getQuestInstanceId(questPaper);
        if (questInstanceUuid != null) {
            this.plugin.getDataManager().registerQuestInstance(player, questInstanceUuid, this.questId, targetQuest.getRequiredAmount());
            this.plugin.getQuestManager().addQuestToQueue(player, this.questId, questInstanceUuid);
        }
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), questPaper);
            player.sendMessage(this.plugin.getMessageManager().getMessage("reward-quest-inventory-full"));
        } else {
            player.getInventory().addItem(new ItemStack[]{questPaper});
        }
        player.sendMessage(this.plugin.getMessageManager().getMessage("reward-quest-received", Map.of("quest", targetQuest.getDisplay())));
        return true;
    }

    @Override
    public String getDisplayDescription() {
        String base = "&bQuest: &f" + this.questId;
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
        section.set("type", (Object)"quest");
        section.set("quest-id", (Object)this.questId);
        if (this.chance < 100) {
            section.set("chance", (Object)this.chance);
        }
    }

    public static QuestQuestReward deserialize(ConfigurationSection section, SoapsQuest plugin) {
        if (section == null) {
            return null;
        }
        String questId = section.getString("quest-id");
        if (questId == null || questId.isEmpty()) {
            return null;
        }
        int chance = section.getInt("chance", 100);
        return new QuestQuestReward(plugin, questId, chance);
    }

    public boolean isValid() {
        return this.plugin.getQuestManager().questExists(this.questId);
    }

    public String getQuestId() {
        return this.questId;
    }
}

