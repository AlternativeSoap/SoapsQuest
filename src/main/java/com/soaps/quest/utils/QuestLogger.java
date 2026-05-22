package com.soaps.quest.utils;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.quests.Quest;
import java.util.logging.Level;
import org.bukkit.entity.Player;

public class QuestLogger {
    private final SoapsQuest plugin;

    public QuestLogger(SoapsQuest plugin) {
        this.plugin = plugin;
    }

    public void logQuestCompletion(Player player, Quest quest) {
        if (!this.plugin.isLogQuestCompletions()) {
            return;
        }
        this.plugin.getLogger().log(Level.INFO, "Player {0} claimed quest: {1} [{2}/{3}]", new Object[]{player.getName(), quest.getQuestId(), quest.getTier() != null ? quest.getTier() : "unknown", quest.getDifficulty() != null ? quest.getDifficulty() : "unknown"});
    }

    public void logQuestAcceptance(Player player, Quest quest) {
        if (!this.plugin.isLogQuestCompletions()) {
            return;
        }
        this.plugin.getLogger().log(Level.INFO, "Player {0} accepted quest: {1}", new Object[]{player.getName(), quest.getQuestId()});
    }

    public void logQuestGeneration(String questId, String type, String tier, String difficulty) {
        if (!this.plugin.isLogAdminActions()) {
            return;
        }
        this.plugin.getLogger().log(Level.INFO, "Generated quest: {0} [{1}/{2}/{3}]", new Object[]{questId, type, tier, difficulty});
    }

    public void logQuestEdit(Player editor, String questId, String action) {
        if (!this.plugin.isLogAdminActions()) {
            return;
        }
        this.plugin.getLogger().log(Level.INFO, "Player {0} edited quest {1}: {2}", new Object[]{editor.getName(), questId, action});
    }
}
