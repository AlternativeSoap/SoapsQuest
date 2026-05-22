/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.soaps.quest.gui;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.chat.ChatInputManager;
import com.soaps.quest.gui.ConditionEditorGui;
import com.soaps.quest.gui.ObjectiveEditorGui;
import com.soaps.quest.gui.PlayerQuestsGui;
import com.soaps.quest.gui.QuestBrowserGui;
import com.soaps.quest.gui.QuestDetailsGui;
import com.soaps.quest.gui.QuestEditorGui;
import com.soaps.quest.gui.RewardEditorGui;
import com.soaps.quest.utils.GuiConfigManager;
import org.bukkit.plugin.Plugin;

public class GuiManager {
    private final SoapsQuest plugin;
    private final GuiConfigManager configManager;
    private final ChatInputManager chatInputManager;
    private QuestBrowserGui questBrowserGui;
    private QuestEditorGui questEditorGui;
    private QuestDetailsGui questDetailsGui;
    private ConditionEditorGui conditionEditorGui;
    private ObjectiveEditorGui objectiveEditorGui;
    private RewardEditorGui rewardEditorGui;
    private PlayerQuestsGui playerQuestsGui;

    public GuiManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.configManager = new GuiConfigManager((Plugin)plugin);
        this.chatInputManager = new ChatInputManager(plugin);
        this.chatInputManager.initialize();
        plugin.debugLog("GuiManager core initialized");
    }

    public void initializeGuis() {
        this.questBrowserGui = new QuestBrowserGui(this.plugin);
        this.questBrowserGui.initialize();
        this.questEditorGui = new QuestEditorGui(this.plugin);
        this.questDetailsGui = new QuestDetailsGui(this.plugin);
        this.conditionEditorGui = new ConditionEditorGui(this.plugin);
        this.objectiveEditorGui = new ObjectiveEditorGui(this.plugin);
        this.rewardEditorGui = new RewardEditorGui(this.plugin);
        this.playerQuestsGui = new PlayerQuestsGui(this.plugin);
        this.plugin.debugLog("All GUI instances initialized");
    }

    public GuiConfigManager getConfigManager() {
        return this.configManager;
    }

    public ChatInputManager getChatInputManager() {
        return this.chatInputManager;
    }

    public QuestBrowserGui getQuestBrowserGui() {
        return this.questBrowserGui;
    }

    public QuestEditorGui getQuestEditorGui() {
        return this.questEditorGui;
    }

    public QuestDetailsGui getQuestDetailsGui() {
        return this.questDetailsGui;
    }

    public ConditionEditorGui getConditionEditorGui() {
        return this.conditionEditorGui;
    }

    public ObjectiveEditorGui getObjectiveEditorGui() {
        return this.objectiveEditorGui;
    }

    public RewardEditorGui getRewardEditorGui() {
        return this.rewardEditorGui;
    }

    public PlayerQuestsGui getPlayerQuestsGui() {
        return this.playerQuestsGui;
    }

    public void reload() {
        this.configManager.reload();
        this.questBrowserGui = new QuestBrowserGui(this.plugin);
        this.questBrowserGui.initialize();
        this.questEditorGui = new QuestEditorGui(this.plugin);
        this.questDetailsGui = new QuestDetailsGui(this.plugin);
        this.conditionEditorGui = new ConditionEditorGui(this.plugin);
        this.objectiveEditorGui = new ObjectiveEditorGui(this.plugin);
        this.rewardEditorGui = new RewardEditorGui(this.plugin);
        this.playerQuestsGui = new PlayerQuestsGui(this.plugin);
        this.plugin.debugLog("GUI configurations reloaded");
    }

    public void shutdown() {
        this.chatInputManager.clearAll();
        this.configManager.clearCache();
        if (this.questBrowserGui != null) {
            this.questBrowserGui.clearAllData();
        }
        if (this.questEditorGui != null) {
            this.questEditorGui.clearAllData();
        }
        if (this.questDetailsGui != null) {
            this.questDetailsGui.clearAllData();
        }
        if (this.conditionEditorGui != null) {
            this.conditionEditorGui.clearAllData();
        }
        if (this.objectiveEditorGui != null) {
            this.objectiveEditorGui.clearAllData();
        }
        this.plugin.debugLog("GuiManager shut down");
    }
}

