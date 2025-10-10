package com.soaps.quest;

import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.soaps.quest.commands.QuestCommand;
import com.soaps.quest.conditions.ConditionChecker;
import com.soaps.quest.listeners.BlockBreakListener;
import com.soaps.quest.listeners.BlockPlaceListener;
import com.soaps.quest.listeners.BowShootListener;
import com.soaps.quest.listeners.BrewListener;
import com.soaps.quest.listeners.ChatListener;
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
import com.soaps.quest.listeners.QuestPaperListener;
import com.soaps.quest.listeners.ShearListener;
import com.soaps.quest.listeners.SleepListener;
import com.soaps.quest.listeners.SmeltListener;
import com.soaps.quest.listeners.TameListener;
import com.soaps.quest.listeners.TradeListener;
import com.soaps.quest.listeners.VehicleListener;
import com.soaps.quest.managers.CustomItemManager;
import com.soaps.quest.managers.DataManager;
import com.soaps.quest.managers.DifficultyManager;
import com.soaps.quest.managers.MessageManager;
import com.soaps.quest.managers.ProgressDisplayManager;
import com.soaps.quest.managers.QuestManager;
import com.soaps.quest.managers.RewardManager;
import com.soaps.quest.managers.TierManager;
import com.soaps.quest.objectives.ObjectiveRegistry;

import net.milkbowl.vault.economy.Economy;

/**
 * Main plugin class for SoapsQuest.
 * Manages initialization, lifecycle, and access to all managers.
 */
public class SoapsQuest extends JavaPlugin {
    
    // Managers
    private MessageManager messageManager;
    private QuestManager questManager;
    private DataManager dataManager;
    private RewardManager rewardManager;
    private ProgressDisplayManager progressDisplayManager;
    private TierManager tierManager;
    private DifficultyManager difficultyManager;
    private CustomItemManager customItemManager;
    private ConditionChecker conditionChecker;
    
    // Vault economy
    private Economy economy;
    
    // NamespacedKeys for quest papers
    private NamespacedKey questIdKey;
    private NamespacedKey playerUuidKey;
    
    @Override
    public void onEnable() {
        // Ensure config.yml exists and load it
        saveDefaultConfig();
        reloadConfig();
        
        // Initialize Objective Registry (NEW)
        ObjectiveRegistry.initialize();
        getLogger().info("Objective Registry initialized with all objective types");
        
        // Initialize NamespacedKeys
        questIdKey = new NamespacedKey(this, "quest_id");
        playerUuidKey = new NamespacedKey(this, "player_uuid");
        
        // Initialize managers
        messageManager = new MessageManager(this);
        progressDisplayManager = new ProgressDisplayManager(this);
        tierManager = new TierManager(this);
        difficultyManager = new DifficultyManager(this);
        customItemManager = new CustomItemManager(this);
        questManager = new QuestManager(this);
        rewardManager = new RewardManager(this);
        dataManager = new DataManager(this);
        
        // Load data
        questManager.loadQuests();
        rewardManager.loadRewards();
        
        // Setup Vault economy if available
        setupEconomy();
        
        // Initialize condition checker after economy setup
        conditionChecker = new ConditionChecker(this);
        
        // Register commands
        QuestCommand questCommand = new QuestCommand(this);
        PluginCommand command = getCommand("soapsquest");
        if (command != null) {
            command.setExecutor(questCommand);
            command.setTabCompleter(questCommand);
        } else {
            getLogger().severe("Failed to register command 'soapsquest' - check plugin.yml!");
        }
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityKillListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemPickupListener(this), this);
        getServer().getPluginManager().registerEvents(new CraftItemListener(this), this);
        getServer().getPluginManager().registerEvents(new SmeltListener(this), this);
        getServer().getPluginManager().registerEvents(new FishListener(this), this);
        getServer().getPluginManager().registerEvents(new EnchantListener(this), this);
        getServer().getPluginManager().registerEvents(new ConsumeListener(this), this);
        getServer().getPluginManager().registerEvents(new TameListener(this), this);
        getServer().getPluginManager().registerEvents(new TradeListener(this), this);
        getServer().getPluginManager().registerEvents(new BrewListener(this), this);
        getServer().getPluginManager().registerEvents(new ShearListener(this), this);
        getServer().getPluginManager().registerEvents(new SleepListener(this), this);
        getServer().getPluginManager().registerEvents(new HealListener(this), this);
        getServer().getPluginManager().registerEvents(new DropListener(this), this);
        getServer().getPluginManager().registerEvents(new DamageListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new LevelListener(this), this);
        getServer().getPluginManager().registerEvents(new MoveListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new InteractListener(this), this);
        getServer().getPluginManager().registerEvents(new BowShootListener(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileListener(this), this);
        getServer().getPluginManager().registerEvents(new FireworkListener(this), this);
        getServer().getPluginManager().registerEvents(new VehicleListener(this), this);
        getServer().getPluginManager().registerEvents(new QuestPaperListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        // Register MythicMobs listener if MythicMobs is installed
        if (isMythicMobsInstalled()) {
            getServer().getPluginManager().registerEvents(new MythicMobKillListener(this), this);
            getLogger().info("MythicMobs detected - kill_mythicmob objectives enabled");
        } else {
            getLogger().info("MythicMobs not found - kill_mythicmob objectives disabled");
        }
        
        // Start autosave
        dataManager.startAutosave();
        
        // Display startup message
        getLogger().info("SoapsQuest v1.0.0-BETA by AlternativeSoap - Enabled!");
        getLogger().info("Support: discord.gg/soapsuniverse");
        getLogger().warning("This is a BETA version - Please report issues on GitHub!");
    }

    @Override
    public void onDisable() {
        // Stop autosave
        if (dataManager != null) {
            dataManager.stopAutosave();
            // Save data synchronously on shutdown
            dataManager.saveData();
        }
        
        getLogger().info("SoapsQuest has been disabled!");
    }
    
    /**
     * Check if MythicMobs plugin is installed.
     * 
     * @return True if MythicMobs is present
     */
    private boolean isMythicMobsInstalled() {
        try {
            Class.forName("io.lumine.mythic.bukkit.MythicBukkit");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * Setup Vault economy integration.
     * 
     * @return True if economy was set up successfully
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().info("Vault not found - money rewards disabled");
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager()
            .getRegistration(Economy.class);
        
        if (rsp == null) {
            getLogger().warning("Vault found but no economy provider - money rewards disabled");
            return false;
        }
        
        economy = rsp.getProvider();
        getLogger().info("Vault economy hooked!");
        return true;
    }
    
    /**
     * Get the MessageManager instance.
     * 
     * @return MessageManager (guaranteed non-null after plugin enables)
     * @throws IllegalStateException if called before plugin is enabled
     */
    public MessageManager getMessageManager() {
        if (messageManager == null) {
            throw new IllegalStateException("MessageManager not initialized - plugin may not be enabled yet");
        }
        return messageManager;
    }
    
    /**
     * Get the QuestManager instance.
     * 
     * @return QuestManager
     */
    public QuestManager getQuestManager() {
        return questManager;
    }
    
    /**
     * Get the DataManager instance.
     * 
     * @return DataManager
     */
    public DataManager getDataManager() {
        return dataManager;
    }
    
    /**
     * Get the RewardManager instance.
     * 
     * @return RewardManager
     */
    public RewardManager getRewardManager() {
        return rewardManager;
    }
    
    /**
     * Get the ProgressDisplayManager instance.
     * 
     * @return ProgressDisplayManager
     */
    public ProgressDisplayManager getProgressDisplayManager() {
        return progressDisplayManager;
    }
    
    /**
     * Get the Vault Economy instance.
     * 
     * @return Economy, or null if not available
     */
    public Economy getEconomy() {
        return economy;
    }
    
    /**
     * Get the quest ID NamespacedKey.
     * 
     * @return Quest ID key
     */
    public NamespacedKey getQuestIdKey() {
        return questIdKey;
    }
    
    /**
     * Get the player UUID NamespacedKey.
     * 
     * @return Player UUID key
     */
    public NamespacedKey getPlayerUuidKey() {
        return playerUuidKey;
    }
    
    /**
     * Get the TierManager instance.
     * 
     * @return TierManager
     */
    public TierManager getTierManager() {
        return tierManager;
    }
    
    /**
     * Get the DifficultyManager instance.
     * 
     * @return DifficultyManager
     */
    public DifficultyManager getDifficultyManager() {
        return difficultyManager;
    }
    
    /**
     * Get the CustomItemManager instance.
     * 
     * @return CustomItemManager
     */
    public CustomItemManager getCustomItemManager() {
        return customItemManager;
    }
    
    /**
     * Get the ConditionChecker instance.
     * 
     * @return ConditionChecker
     */
    public ConditionChecker getConditionChecker() {
        return conditionChecker;
    }
}