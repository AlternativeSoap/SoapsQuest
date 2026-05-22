/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.economy.Economy
 *  org.bukkit.NamespacedKey
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.RegisteredServiceProvider
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.soaps.quest;

import com.soaps.quest.api.SoapsQuestAPI;
import com.soaps.quest.commands.QuestCommand;
import com.soaps.quest.conditions.ConditionRegistry;
import com.soaps.quest.features.loot.QuestLootListener;
import com.soaps.quest.features.loot.QuestLootManager;
import com.soaps.quest.gui.GuiManager;
import com.soaps.quest.listeners.AnvilRepairListener;
import com.soaps.quest.listeners.BlockBreakListener;
import com.soaps.quest.listeners.BlockPlaceListener;
import com.soaps.quest.listeners.BowShootListener;
import com.soaps.quest.listeners.BreedListener;
import com.soaps.quest.listeners.BrewListener;
import com.soaps.quest.listeners.ChatListener;
import com.soaps.quest.listeners.CommandListener;
import com.soaps.quest.listeners.PlaceholderCheckListener;
import com.soaps.quest.listeners.ConsumeListener;
import com.soaps.quest.listeners.CraftItemListener;
import com.soaps.quest.listeners.DamageListener;
import com.soaps.quest.listeners.DeathListener;
import com.soaps.quest.listeners.DropListener;
import com.soaps.quest.listeners.EnchantListener;
import com.soaps.quest.listeners.EntityKillListener;
import com.soaps.quest.listeners.FireworkListener;
import com.soaps.quest.listeners.FishListener;
import com.soaps.quest.listeners.HealListener;
import com.soaps.quest.listeners.InteractListener;
import com.soaps.quest.listeners.ItemPickupListener;
import com.soaps.quest.listeners.LevelListener;
import com.soaps.quest.listeners.MoveListener;
import com.soaps.quest.listeners.MythicMobKillListener;
import com.soaps.quest.listeners.PlayerListener;
import com.soaps.quest.listeners.ProjectileListener;
import com.soaps.quest.listeners.QuestItemInteractionBlockerListener;
import com.soaps.quest.listeners.QuestPaperListener;
import com.soaps.quest.listeners.ShearListener;
import com.soaps.quest.listeners.SleepListener;
import com.soaps.quest.listeners.SmeltListener;
import com.soaps.quest.listeners.TameListener;
import com.soaps.quest.listeners.TradeListener;
import com.soaps.quest.listeners.VehicleListener;
import com.soaps.quest.listeners.XpPickupListener;
import com.soaps.quest.managers.CustomItemManager;
import com.soaps.quest.managers.DataManager;
import com.soaps.quest.managers.DifficultyManager;
import com.soaps.quest.managers.MessageManager;
import com.soaps.quest.managers.ProgressDisplayManager;
import com.soaps.quest.managers.QuestGeneratorService;
import com.soaps.quest.managers.QuestManager;
import com.soaps.quest.managers.QuestProgressTracker;
import com.soaps.quest.managers.RecurringQuestManager;
import com.soaps.quest.managers.RewardManager;
import com.soaps.quest.managers.SigilManager;
import com.soaps.quest.managers.StatisticManager;
import com.soaps.quest.managers.TierManager;
import com.soaps.quest.objectives.ObjectiveRegistry;
import com.soaps.quest.placeholders.SoapsQuestPlaceholderExpansion;
import com.soaps.quest.rewards.RewardRegistry;
import com.soaps.quest.utils.AsyncTaskManager;
import com.soaps.quest.utils.PlaceholderManager;
import com.soaps.quest.utils.QuestLogger;

import java.util.List;
import java.util.logging.Level;

import com.soaps.common.api.SoapsCommon;
import com.soaps.common.api.SoapsConfigKeys;
import com.soaps.common.api.SoapsStartup;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SoapsQuest
extends JavaPlugin {
    private MessageManager messageManager;
    private QuestManager questManager;
    private DataManager dataManager;
    private StatisticManager statisticManager;
    private SigilManager sigilManager;
    private RewardManager rewardManager;
    private ProgressDisplayManager progressDisplayManager;
    private TierManager tierManager;
    private DifficultyManager difficultyManager;
    private CustomItemManager customItemManager;
    private QuestProgressTracker questProgressTracker;
    private RecurringQuestManager recurringQuestManager;
    private AsyncTaskManager asyncTaskManager;
    private QuestLootManager questLootManager;
    private GuiManager guiManager;
    private QuestGeneratorService questGeneratorService;
    private QuestLogger questLogger;
    private PlayerListener playerListener;
    private PlaceholderCheckListener placeholderCheckListener;
    private Economy economy;
    private NamespacedKey questIdKey;
    private NamespacedKey playerUuidKey;
    private boolean debugMode = false;
    private boolean logQuestCompletions = false;
    private boolean logAdminActions = false;
    private boolean premium = false;
    private boolean hasVault = false;
    private boolean hasPlaceholderAPI = false;
    private boolean hasMythicMobs = false;

    private long startupBeginMs;

    public void onEnable() {
        if (!SoapsCommon.require(this)) return;
        this.saveDefaultConfig();
        this.reloadConfig();
        this.loadLoggingSettings();
        try {
            this.startupBeginMs = System.currentTimeMillis();
            PluginCommand alias;
            ObjectiveRegistry.initialize();
            ConditionRegistry.initialize(this);
            RewardRegistry.initialize(this);
            this.questIdKey = new NamespacedKey((Plugin)this, "quest_id");
            this.playerUuidKey = new NamespacedKey((Plugin)this, "player_uuid");
            this.messageManager = new MessageManager(this);
            this.progressDisplayManager = new ProgressDisplayManager(this);
            this.tierManager = new TierManager(this);
            this.difficultyManager = new DifficultyManager(this);
            this.customItemManager = new CustomItemManager(this);
            this.questLogger = new QuestLogger(this);
            this.questManager = new QuestManager(this);
            this.dataManager = new DataManager(this);
            this.statisticManager = new StatisticManager(this);
            this.rewardManager = new RewardManager(this);
            this.sigilManager = new SigilManager(this);
            this.questProgressTracker = new QuestProgressTracker(this);
            this.asyncTaskManager = new AsyncTaskManager(this);
            this.questManager.loadQuests();
            int migratedRewards = this.rewardManager.migrateLegacyRewardsInGeneratedFile();
            if (migratedRewards > 0) {
                this.getLogger().log(Level.INFO, "Migrated {0} quest reward(s) from legacy format", migratedRewards);
            }
            this.rewardManager.loadRewards();
            this.statisticManager.loadStatistics();
            PlaceholderManager.setPlugin(this);
            SoapsQuestAPI.initialize(this);
            try {
                Class<?> configClass = Class.forName("com.soaps.quest.premium.RandomGeneratorConfig");
                Class<?> generatorClass = Class.forName("com.soaps.quest.premium.QuestGenerator");
                Object configInstance = configClass.getDeclaredConstructor(SoapsQuest.class).newInstance(new Object[]{this});
                Object generatorInstance = generatorClass.getDeclaredConstructor(SoapsQuest.class, configClass).newInstance(new Object[]{this, configInstance});
                this.questGeneratorService = (QuestGeneratorService)generatorInstance;
                this.premium = true;
                this.getLogger().info("\u2605 Premium features unlocked");
            }
            catch (ClassNotFoundException e) {
                this.premium = false;
                this.getLogger().info("Free edition \u2014 Random Quest Generator, Daily/Weekly Quests, Quest Loot System, and Quest Editor GUI are not available. Upgrade to Premium to unlock these features.");
            }
            catch (Exception e) {
                this.premium = false;
                this.getLogger().warning("Failed to initialise premium features: " + e.getMessage());
            }
            if (this.premium) {
                this.recurringQuestManager = new RecurringQuestManager(this);
            }
            if (this.premium) {
                this.questLootManager = new QuestLootManager(this);
            }
            this.guiManager = new GuiManager(this);
            this.guiManager.initializeGuis();
            this.setupEconomy();
            QuestCommand questCommand = new QuestCommand(this);
            PluginCommand primary = this.getCommand("soapsquest");
            if (primary != null) {
                primary.setExecutor((CommandExecutor)questCommand);
                primary.setTabCompleter((TabCompleter)questCommand);
            } else {
                this.getLogger().warning("Command 'soapsquest' is not defined in plugin.yml; primary quest commands will not work.");
            }
            if ((alias = this.getCommand("sq")) != null) {
                alias.setExecutor((CommandExecutor)questCommand);
                alias.setTabCompleter((TabCompleter)questCommand);
            }
            this.getServer().getPluginManager().registerEvents((Listener)new BlockBreakListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new BlockPlaceListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new EntityKillListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new ItemPickupListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new CraftItemListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new SmeltListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new FishListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new EnchantListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new ConsumeListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new TameListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new BreedListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new TradeListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new BrewListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new ShearListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new SleepListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new HealListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new DropListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new DamageListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new DeathListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new LevelListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new MoveListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new ChatListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new CommandListener(this), (Plugin)this);
            this.placeholderCheckListener = new PlaceholderCheckListener(this);
            this.getServer().getPluginManager().registerEvents((Listener)new InteractListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new BowShootListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new ProjectileListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new FireworkListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new VehicleListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new QuestItemInteractionBlockerListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new QuestPaperListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new XpPickupListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new AnvilRepairListener(this), (Plugin)this);
            this.playerListener = new PlayerListener(this);
            this.getServer().getPluginManager().registerEvents((Listener)this.playerListener, (Plugin)this);
            if (this.questLootManager != null && this.questLootManager.isEnabled()) {
                this.getServer().getPluginManager().registerEvents((Listener)new QuestLootListener(this, this.questLootManager), (Plugin)this);
            }
            boolean bl = this.hasVault = this.economy != null;
            if (this.isMythicMobsInstalled()) {
                this.getServer().getPluginManager().registerEvents((Listener)new MythicMobKillListener(this), (Plugin)this);
                this.hasMythicMobs = true;
            }
            if (this.isPlaceholderAPIInstalled()) {
                new SoapsQuestPlaceholderExpansion(this).register();
                this.hasPlaceholderAPI = true;
            }
            this.dataManager.startAutosave();
            this.logStartupSuite();
        }
        catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "Failed to enable SoapsQuest - disabling plugin", t);
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
        }
    }

    public void onDisable() {
        if (this.placeholderCheckListener != null) {
            this.placeholderCheckListener.stop();
            this.placeholderCheckListener = null;
        }
        if (this.guiManager != null) {
            this.guiManager.shutdown();
        }
        if (this.asyncTaskManager != null) {
            this.asyncTaskManager.shutdown();
        }
        if (this.recurringQuestManager != null) {
            this.recurringQuestManager.shutdown();
        }
        if (this.dataManager != null) {
            this.dataManager.stopAutosave();
            this.dataManager.flushPendingSaves();
            if (this.statisticManager != null) {
                this.statisticManager.saveAllStatistics();
            }
            this.dataManager.saveData();
        }
        if (this.sigilManager != null) {
            this.sigilManager.save();
        }
        this.getLogger().info("SoapsQuest safely disabled.");
    }

    private boolean isMythicMobsInstalled() {
        try {
            Class.forName("io.lumine.mythic.bukkit.MythicBukkit");
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    private boolean isPlaceholderAPIInstalled() {
        return this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            this.getLogger().info("Vault not found - money rewards disabled");
            return false;
        }
        RegisteredServiceProvider rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            this.getLogger().warning("Vault found but no economy provider - money rewards disabled");
            return false;
        }
        this.economy = (Economy)rsp.getProvider();
        this.getLogger().info("Vault economy hooked!");
        return true;
    }

    public MessageManager getMessageManager() {
        if (this.messageManager == null) {
            throw new IllegalStateException("MessageManager not initialized - plugin may not be enabled yet");
        }
        return this.messageManager;
    }

    public QuestManager getQuestManager() {
        return this.questManager;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public RewardManager getRewardManager() {
        return this.rewardManager;
    }

    public SigilManager getSigilManager() {
        return this.sigilManager;
    }

    public ProgressDisplayManager getProgressDisplayManager() {
        return this.progressDisplayManager;
    }

    public Economy getEconomy() {
        return this.economy;
    }

    public NamespacedKey getQuestIdKey() {
        return this.questIdKey;
    }

    public NamespacedKey getPlayerUuidKey() {
        return this.playerUuidKey;
    }

    public TierManager getTierManager() {
        return this.tierManager;
    }

    public DifficultyManager getDifficultyManager() {
        return this.difficultyManager;
    }

    public CustomItemManager getCustomItemManager() {
        return this.customItemManager;
    }

    public PlayerListener getPlayerListener() {
        return this.playerListener;
    }

    public QuestProgressTracker getQuestProgressTracker() {
        return this.questProgressTracker;
    }

    public StatisticManager getStatisticManager() {
        return this.statisticManager;
    }

    public RecurringQuestManager getRecurringQuestManager() {
        return this.recurringQuestManager;
    }

    public AsyncTaskManager getAsyncTaskManager() {
        return this.asyncTaskManager;
    }

    public QuestLootManager getQuestLootManager() {
        return this.questLootManager;
    }

    public GuiManager getGuiManager() {
        return this.guiManager;
    }

    public QuestLogger getQuestLogger() {
        return this.questLogger;
    }

    public QuestGeneratorService getQuestGeneratorService() {
        return this.questGeneratorService;
    }

    public boolean isPremium() {
        return this.premium;
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }

    public boolean isLogQuestCompletions() {
        return this.logQuestCompletions;
    }

    public boolean isLogAdminActions() {
        return this.logAdminActions;
    }

    public void loadLoggingSettings() {
        this.debugMode = this.getConfig().getBoolean("debug", false);
        this.logQuestCompletions = this.getConfig().getBoolean("log-quest-completions", false);
        this.logAdminActions = this.getConfig().getBoolean("log-admin-actions", false);
    }

    public boolean toggleDebugMode() {
        this.debugMode = !this.debugMode;
        this.getLogger().log(Level.INFO, "Debug mode {0}", this.debugMode ? "ENABLED" : "DISABLED");
        return this.debugMode;
    }

    public void debugLog(String message) {
        if (this.debugMode) {
            this.getLogger().log(Level.INFO, "[DEBUG] {0}", message);
        }
    }

    public void debugLog(Level level, String message, Object ... params) {
        if (this.debugMode) {
            this.getLogger().log(level, "[DEBUG] " + message, params);
        }
    }

    private void logStartupSuite() {
        int totalQuests = this.questManager.getAllQuests().size();
        int generatedQuests = (int) this.questManager.getAllQuests().keySet().stream()
                .filter(this.questManager::isGeneratedQuest).count();
        int manualQuests = totalQuests - generatedQuests;
        String lootStatus;
        if (this.questLootManager != null && this.questLootManager.isEnabled()) {
            StringBuilder lootParts = new StringBuilder();
            if (this.questLootManager.isChestLootEnabled()) {
                lootParts.append(String.format("Chests %.0f%%", this.questLootManager.getChestChance()));
            }
            if (this.questLootManager.isMobLootEnabled()) {
                if (lootParts.length() > 0) {
                    lootParts.append(", ");
                }
                lootParts.append(String.format("Mobs %.0f%%", this.questLootManager.getMobDefaultChance()));
            }
            lootStatus = lootParts.length() > 0 ? lootParts.toString() : "enabled";
        } else {
            lootStatus = "disabled";
        }
        if (SoapsConfigKeys.readStartupBannerFull(this.getConfig())) {
            SoapsStartup.printFullBranding(this);
        }
        SoapsStartup.printCompact(this, this.startupBeginMs, List.of(
                "quests=" + totalQuests + "(" + manualQuests + "m+" + generatedQuests + "g)",
                "loot=" + lootStatus,
                "vault=" + (this.hasVault ? "on" : "off"),
                "papi=" + (this.hasPlaceholderAPI ? "on" : "off"),
                "mythicmobs=" + (this.hasMythicMobs ? "on" : "off"),
                "premium=" + this.premium,
                "gui=" + (SoapsConfigKeys.readGuiEnabled(this.getConfig()) ? "on" : "off")));
    }

    public String getDefaultTier() {
        return this.getConfig().getString("default-tier", "common");
    }

    public String getDefaultDifficulty() {
        return this.getConfig().getString("default-difficulty", "normal");
    }
}

