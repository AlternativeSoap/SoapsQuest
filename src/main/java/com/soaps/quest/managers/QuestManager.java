/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.configuration.Configuration
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.soaps.quest.managers;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.managers.DifficultyManager;
import com.soaps.quest.objectives.BreakObjective;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.objectives.ObjectiveRegistry;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import com.soaps.quest.utils.ConfigMigrationUtil;
import com.soaps.quest.utils.ConfigNormalizer;
import com.soaps.quest.utils.QuestPaper;
import com.soaps.quest.utils.YamlUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QuestManager {
    private final SoapsQuest plugin;
    private final Map<String, Quest> quests;
    private final Set<String> generatedQuestIds;
    private final Map<UUID, Map<String, Queue<UUID>>> playerQuestQueues;

    public QuestManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.quests = new HashMap<String, Quest>();
        this.generatedQuestIds = new HashSet<String>();
        this.playerQuestQueues = new HashMap<UUID, Map<String, Queue<UUID>>>();
    }

    public void loadQuests() {
        this.quests.clear();
        this.generatedQuestIds.clear();
        File questsFile = new File(this.plugin.getDataFolder(), "quests.yml");
        if (!questsFile.exists()) {
            this.plugin.saveResource("quests.yml", false);
        }
        int loaded = 0;
        loaded += this.loadQuestsFromFile(questsFile, "quests.yml", false);
        File generatedFile = new File(this.plugin.getDataFolder(), "generated.yml");
        if (generatedFile.exists()) {
            loaded += this.loadQuestsFromFile(generatedFile, "generated.yml", true);
        }
        this.plugin.debugLog(Level.INFO, "Loaded {0} quests.", loaded);
    }

    private int loadQuestsFromFile(File file, String fileName, boolean isGenerated) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration((File)file);
        int migrated = ConfigMigrationUtil.migrateLegacyAmountKeys(config, this.plugin.getLogger());
        if (migrated > 0) {
            try {
                YamlUtil.atomicSave((FileConfiguration)config, file);
                this.plugin.getLogger().log(Level.INFO, "[Migration] Updated {0} legacy field(s) to ''amount'' in {1}", new Object[]{migrated, fileName});
            }
            catch (IOException e) {
                this.plugin.getLogger().log(Level.WARNING, "[Migration] Could not save migrated config {0}: {1}", new Object[]{fileName, e.getMessage()});
            }
        }
        final ConfigurationSection questsRoot;
        if (isGenerated) {
            questsRoot = config;
        } else {
            questsRoot = config.getConfigurationSection("quests");
            if (questsRoot == null) {
                this.plugin.getLogger().log(Level.WARNING, "No quests section found in {0}!", new Object[]{fileName});
                return 0;
            }
        }
        int loaded = 0;
        Set<String> questKeys = questsRoot.getKeys(false);
        for (String questId : questKeys) {
            try {
                Quest quest;
                ConfigurationSection questSection = questsRoot.getConfigurationSection(questId);
                if (isGenerated && questSection != null) {
                    ConfigNormalizer.normalizeConfigurationSection(questSection);
                }
                if ((quest = this.loadQuest(questId, questSection, isGenerated)) == null) continue;
                this.quests.put(questId, quest);
                if (isGenerated) {
                    this.generatedQuestIds.add(questId.toLowerCase());
                }
                ++loaded;
            }
            catch (Exception e) {
                this.plugin.getLogger().log(Level.SEVERE, "Error loading quest ''{0}'' from {1}: {2}", new Object[]{questId, fileName, e.getMessage()});
            }
        }
        return loaded;
    }

    private Quest loadQuest(String questId, ConfigurationSection section, boolean isGenerated) {
        if (section == null) {
            return null;
        }
        if (!section.contains("objectives")) {
            this.plugin.getLogger().log(Level.WARNING, "Quest ''{0}'' is missing ''objectives'' section! Legacy format is no longer supported.", questId);
            return null;
        }
        return this.loadQuestWithObjectives(questId, section, isGenerated);
    }

    private Quest loadQuestWithObjectives(String questId, ConfigurationSection section, boolean isGenerated) {
        String display = section.getString("display", questId);
        List<String> customLore = null;
        if (section.contains("lore")) {
            customLore = section.getStringList("lore");
        }
        Material material = Material.PAPER;
        String materialString = null;
        if (section.contains("quest_paper.material")) {
            materialString = section.getString("quest_paper.material");
        } else if (section.contains("material")) {
            materialString = section.getString("material");
        }
        if (materialString != null) {
            try {
                material = Material.valueOf((String)materialString.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                this.plugin.getLogger().log(Level.WARNING, "Quest ''{0}'' has invalid material: {1}, defaulting to PAPER", new Object[]{questId, materialString});
            }
        }
        boolean lockToPlayer = section.getBoolean("lock-to-player", false);
        boolean sequential = section.getBoolean("sequential", false);
        String permission = section.getString("permission", null);
        String tier = section.getString("tier", "common");
        String difficulty = section.getString("difficulty", "normal");
        boolean temporary = section.getBoolean("temporary", false);
        DifficultyManager.Difficulty difficultyConfig = this.plugin.getDifficultyManager().getDifficulty(difficulty);
        double objectiveMultiplier = difficultyConfig != null ? difficultyConfig.objectiveMultiplier : 1.0;
        List<Integer> milestones = section.getIntegerList("milestones");
        ConfigurationSection conditions = section.getConfigurationSection("conditions");
        List<Map<?, ?>> objectivesList = section.getMapList("objectives");
        if (objectivesList.isEmpty()) {
            this.plugin.getLogger().log(Level.WARNING, "Quest ''{0}'' has objectives key but no objectives defined!", questId);
            return null;
        }
        ArrayList<Objective> objectives = new ArrayList<Objective>();
        int objIndex = 0;
        for (Map<?, ?> objectiveMap : objectivesList) {
            ConfigurationSection objSection = this.plugin.getConfig().createSection("temp_" + questId + "_" + objIndex);
            for (Map.Entry<?, ?> entry : objectiveMap.entrySet()) {
                objSection.set(String.valueOf(entry.getKey()), entry.getValue());
            }
            if (!isGenerated && objSection.contains("amount") && objectiveMultiplier != 1.0) {
                int baseAmount = objSection.getInt("amount");
                int scaledAmount = (int)Math.ceil((double)baseAmount * objectiveMultiplier);
                objSection.set("amount", (Object)Math.max(1, scaledAmount));
            }
            if (!objSection.contains("milestones") && !milestones.isEmpty()) {
                objSection.set("milestones", (Object)milestones);
            }
            String objectiveId = questId + "_obj_" + objIndex;
            String objectiveType = objSection.getString("type");
            if ("kill_mythicmob".equalsIgnoreCase(objectiveType) && !ObjectiveRegistry.isMythicMobsInstalled()) {
                this.plugin.getLogger().log(Level.WARNING, "Quest ''{0}'' objective {1} uses kill_mythicmob but MythicMobs plugin is not installed - skipping objective", new Object[]{questId, objIndex});
                ++objIndex;
                continue;
            }
            ObjectiveRegistry.ValidationResult validation = ObjectiveRegistry.validateObjective(objSection);
            if (!validation.valid) {
                this.plugin.getLogger().log(Level.WARNING, "[Validation] Quest ''{0}'' objective {1} failed validation: {2}", new Object[]{questId, objIndex, validation.errorMessage});
                if (validation.missingFields != null && validation.missingFields.length > 0) {
                    this.plugin.getLogger().log(Level.WARNING, "[Validation] Missing fields: {0}", String.join((CharSequence)", ", validation.missingFields));
                }
                ++objIndex;
                continue;
            }
            Objective objective = ObjectiveRegistry.createObjective(objectiveId, objSection);
            if (objective != null) {
                objectives.add(objective);
            } else {
                this.plugin.getLogger().log(Level.WARNING, "Quest ''{0}'' has invalid objective at index {1}", new Object[]{questId, objIndex});
            }
            ++objIndex;
        }
        if (objectives.isEmpty()) {
            this.plugin.getLogger().log(Level.WARNING, "[Validation] Quest ''{0}'' skipped - no valid objectives", questId);
            return null;
        }
        return Quest.builder(questId, display).objectives(objectives).customLore(customLore).material(material).lockToPlayer(lockToPlayer).sequential(sequential).permission(permission).tier(tier).milestones(milestones).difficulty(difficulty).conditions(conditions).temporary(temporary).build();
    }

    public Quest getQuest(String questId) {
        return this.quests.get(questId);
    }

    public Set<String> getQuestIds() {
        return this.quests.keySet();
    }

    public Map<String, Quest> getAllQuests() {
        return new HashMap<String, Quest>(this.quests);
    }

    public boolean isGeneratedQuest(String questId) {
        return this.generatedQuestIds.contains(questId.toLowerCase());
    }

    public boolean questExists(String questId) {
        return this.quests.containsKey(questId);
    }

    public Quest reloadQuest(String questId) {
        File generatedFile;
        File questsFile = new File(this.plugin.getDataFolder(), "quests.yml");
        if (!questsFile.exists()) {
            return null;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration((File)questsFile);
        ConfigurationSection questsSection = config.getConfigurationSection("quests");
        if (questsSection == null) {
            return null;
        }
        ConfigurationSection questSection = questsSection.getConfigurationSection(questId);
        boolean isGenerated = false;
        if (questSection == null && (generatedFile = new File(this.plugin.getDataFolder(), "generated.yml")).exists()) {
            config = YamlConfiguration.loadConfiguration((File)generatedFile);
            questSection = config.getConfigurationSection(questId);
            boolean bl = isGenerated = questSection != null;
        }
        if (questSection == null) {
            return null;
        }
        try {
            Quest quest = this.loadQuest(questId, questSection, isGenerated);
            if (quest != null) {
                this.quests.put(questId, quest);
                this.plugin.debugLog("Reloaded quest: " + questId);
            }
            return quest;
        }
        catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Error reloading quest: " + questId, e);
            return null;
        }
    }

    public Quest createAndSaveNewQuest(String questId) {
        if (this.questExists(questId)) {
            throw new IllegalArgumentException("Quest with ID '" + questId + "' already exists!");
        }
        String defaultTier = this.plugin.getDefaultTier();
        String defaultDifficulty = this.plugin.getDefaultDifficulty();
        String tier = defaultTier != null ? defaultTier : "common";
        String display = questId;
        List<String> description = List.of("A freshly created quest.", "Edit me to add objectives and rewards!");
        Material material = Material.WRITABLE_BOOK;
        boolean lockToPlayer = false;
        boolean sequential = false;
        ArrayList<Objective> objectives = new ArrayList<Objective>();
        BreakObjective defaultObjective = new BreakObjective("objective-0", null, 1);
        objectives.add(defaultObjective);
        Quest quest = Quest.builder(questId, display).objectives(objectives).customLore(description).material(material).lockToPlayer(lockToPlayer).sequential(sequential).tier(tier).difficulty(defaultDifficulty).build();
        this.saveQuestToFileSync(quest);
        this.quests.put(questId, quest);
        this.plugin.getLogger().log(Level.INFO, "Created new quest: {0}", questId);
        return quest;
    }

    private void saveQuestToFileSync(Quest quest) {
        try {
            ConfigurationSection questsSection;
            File questsFile = new File(this.plugin.getDataFolder(), "quests.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration((File)questsFile);
            if (!config.contains("quests")) {
                config.createSection("quests");
            }
            if ((questsSection = config.getConfigurationSection("quests")) == null) {
                this.plugin.getLogger().log(Level.SEVERE, "Failed to create quests section in quests.yml!");
                return;
            }
            ConfigurationSection questSection = questsSection.createSection(quest.getQuestId());
            questSection.set("display", (Object)quest.getDisplay());
            if (quest.getCustomLore() != null && !quest.getCustomLore().isEmpty()) {
                questSection.set("lore", quest.getCustomLore());
            }
            if (quest.getMaterial() != null) {
                questSection.set("material", (Object)quest.getMaterial().name());
            }
            questSection.set("lock-to-player", (Object)quest.isLockToPlayer());
            questSection.set("sequential", (Object)quest.isSequential());
            if (quest.getTier() != null) {
                questSection.set("tier", (Object)quest.getTier());
            }
            if (quest.getDifficulty() != null) {
                questSection.set("difficulty", (Object)quest.getDifficulty());
            }
            if (quest.isTemporary()) {
                questSection.set("temporary", (Object)true);
            }
            if (!quest.getObjectives().isEmpty()) {
                ArrayList objectivesList = new ArrayList();
                for (Objective obj : quest.getObjectives()) {
                    HashMap<String, Object> objMap = new HashMap<String, Object>();
                    String serialized = obj.serialize();
                    String[] parts = serialized.split(":");
                    if (parts.length >= 3) {
                        objMap.put("type", parts[0]);
                        objMap.put("target", parts[1]);
                        objMap.put("amount", Integer.valueOf(parts[2]));
                    }
                    objectivesList.add(objMap);
                }
                questSection.set("objectives", objectivesList);
            } else {
                questSection.set("objectives", new ArrayList());
            }
            YamlUtil.atomicSave((FileConfiguration)config, questsFile);
            this.plugin.debugLog("Quest '" + quest.getQuestId() + "' saved to quests.yml");
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save quest ''{0}'' to file: {1}", new Object[]{quest.getQuestId(), e.getMessage()});
        }
    }

    public boolean removeQuest(String questId) {
        File targetFile;
        YamlConfiguration generatedConfig;
        if (!this.questExists(questId)) {
            return false;
        }
        File generatedFile = new File(this.plugin.getDataFolder(), "generated.yml");
        File questsFile = new File(this.plugin.getDataFolder(), "quests.yml");
        boolean isInGenerated = false;
        if (generatedFile.exists() && (generatedConfig = YamlConfiguration.loadConfiguration((File)generatedFile)).contains(questId)) {
            isInGenerated = true;
        }
        File file = targetFile = isInGenerated ? generatedFile : questsFile;
        if (!targetFile.exists()) {
            return false;
        }
        try {
            Object path;
            YamlConfiguration config = YamlConfiguration.loadConfiguration((File)targetFile);
            Object object = path = isInGenerated ? questId : "quests." + questId;
            if (!config.contains((String)path)) {
                return false;
            }
            config.set((String)path, null);
            YamlUtil.atomicSave((FileConfiguration)config, targetFile);
            this.quests.remove(questId);
            this.plugin.getDataManager().removeAllQuestInstances(questId);
            this.plugin.debugLog(Level.INFO, "Removed quest ''{0}'' from {1} and cleaned up all progress data", questId, targetFile.getName());
            return true;
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to remove quest ''{0}'': {1}", new Object[]{questId, e.getMessage()});
            return false;
        }
    }

    public boolean copyQuest(String sourceId, String newId) {
        if (!this.questExists(sourceId) || this.questExists(newId)) {
            return false;
        }
        File questsFile = new File(this.plugin.getDataFolder(), "quests.yml");
        File generatedFile = new File(this.plugin.getDataFolder(), "generated.yml");
        try {
            YamlConfiguration questsCfg;
            ConfigurationSection qSection;
            YamlConfiguration generatedCfg;
            ConfigurationSection sourceSection = null;
            if (generatedFile.exists() && (generatedCfg = YamlConfiguration.loadConfiguration((File)generatedFile)).contains(sourceId)) {
                sourceSection = generatedCfg.getConfigurationSection(sourceId);
            }
            if (sourceSection == null && (qSection = (questsCfg = YamlConfiguration.loadConfiguration((File)questsFile)).getConfigurationSection("quests")) != null && qSection.contains(sourceId)) {
                sourceSection = qSection.getConfigurationSection(sourceId);
            }
            if (sourceSection == null) {
                this.plugin.getLogger().log(Level.WARNING, "copyQuest: source section for ''{0}'' not found on disk", sourceId);
                return false;
            }
            YamlConfiguration targetCfg = YamlConfiguration.loadConfiguration((File)questsFile);
            if (!targetCfg.contains("quests")) {
                targetCfg.createSection("quests");
            }
            ConfigurationSection targetQuests = targetCfg.getConfigurationSection("quests");
            ConfigurationSection destSection = targetQuests.createSection(newId);
            for (String key : sourceSection.getKeys(true)) {
                destSection.set(key, sourceSection.get(key));
            }
            YamlUtil.atomicSave((FileConfiguration)targetCfg, questsFile);
            Quest sourceQuest = this.quests.get(sourceId);
            if (sourceQuest != null) {
                Quest copy = Quest.builder(newId, sourceQuest.getDisplay()).customLore(sourceQuest.getCustomLore()).material(sourceQuest.getMaterial()).objectives(sourceQuest.getObjectives()).requiredAmount(sourceQuest.getRequiredAmount()).lockToPlayer(sourceQuest.isLockToPlayer()).sequential(sourceQuest.isSequential()).tier(sourceQuest.getTier()).difficulty(sourceQuest.getDifficulty()).temporary(sourceQuest.isTemporary()).build();
                this.quests.put(newId, copy);
            } else {
                this.loadQuests();
            }
            this.plugin.debugLog("Copied quest ''" + sourceId + "'' \u2192 ''" + newId + "''");
            return true;
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to copy quest ''{0}'' to ''{1}'': {2}", new Object[]{sourceId, newId, e.getMessage()});
            return false;
        }
    }

    public void reload() {
        this.loadQuests();
    }

    public String addQuestToQueue(Player player, String questId, UUID questInstanceUuid) {
        UUID playerUuid = player.getUniqueId();
        this.playerQuestQueues.putIfAbsent(playerUuid, new HashMap());
        Map<String, Queue<UUID>> questTypeQueues = this.playerQuestQueues.get(playerUuid);
        this.cleanupQuestQueue(player, questId);
        Queue questQueue = questTypeQueues.computeIfAbsent(questId, k -> new LinkedList());
        if (questQueue.isEmpty()) {
            questQueue.offer(questInstanceUuid);
            this.updateQuestPaperStatus(player, questId, questInstanceUuid, true);
            return "activated";
        }
        questQueue.offer(questInstanceUuid);
        this.updateQuestPaperStatus(player, questId, questInstanceUuid, false);
        return "queued";
    }

    public void completeQuest(Player player, String questId, UUID questInstanceUuid) {
        boolean wasActive;
        UUID playerUuid = player.getUniqueId();
        Map<String, Queue<UUID>> questTypeQueues = this.playerQuestQueues.get(playerUuid);
        if (questTypeQueues == null) {
            return;
        }
        Queue<UUID> questQueue = questTypeQueues.get(questId);
        if (questQueue == null) {
            return;
        }
        boolean bl = wasActive = questQueue.peek() != null && questQueue.peek().equals(questInstanceUuid);
        if (!wasActive) {
            return;
        }
        questQueue.remove(questInstanceUuid);
        Quest quest = this.getQuest(questId);
        if (quest != null) {
            this.updateQuestPaperStatus(player, questId, questInstanceUuid, false);
        }
        if (!questQueue.isEmpty()) {
            UUID nextActiveUuid = questQueue.peek();
            this.updateQuestPaperStatus(player, questId, nextActiveUuid, true);
            if (quest != null) {
                player.sendMessage(this.plugin.getMessageManager().getMessage("quest-activated", Map.of("quest", quest.getDisplay())));
            }
        }
        if (questQueue.isEmpty()) {
            questTypeQueues.remove(questId);
        }
        if (questTypeQueues.isEmpty()) {
            this.playerQuestQueues.remove(playerUuid);
        }
    }

    public void removeQuestFromQueue(Player player, String questId, UUID questInstanceUuid) {
        UUID playerUuid = player.getUniqueId();
        Map<String, Queue<UUID>> questTypeQueues = this.playerQuestQueues.get(playerUuid);
        if (questTypeQueues == null) {
            this.plugin.debugLog(Level.INFO, "[Queue] Player ''{0}'' has no quest queues", player.getName());
            return;
        }
        Queue<UUID> questQueue = questTypeQueues.get(questId);
        if (questQueue == null) {
            this.plugin.debugLog(Level.INFO, "[Queue] Player ''{0}'' has no queue for quest ''{1}''", player.getName(), questId);
            return;
        }
        boolean wasActive = questQueue.peek() != null && questQueue.peek().equals(questInstanceUuid);
        questQueue.remove(questInstanceUuid);
        Quest quest = this.getQuest(questId);
        if (quest != null) {
            this.plugin.debugLog(Level.INFO, "[Queue] Player ''{0}'' claimed quest ''{1}''", player.getName(), quest.getDisplay());
            this.plugin.debugLog(Level.INFO, "[Queue] Quest ''{0}'' removed from queue", quest.getDisplay());
        }
        if (wasActive && !questQueue.isEmpty()) {
            UUID nextActiveUuid = questQueue.peek();
            if (quest != null) {
                QuestProgress nextProgress = quest.isLockToPlayer() ? this.plugin.getDataManager().getLockedQuestInstance(nextActiveUuid) : this.plugin.getDataManager().getQuestInstance(player, nextActiveUuid);
                if (nextProgress == null) {
                    this.plugin.debugLog(Level.INFO, "[Data] Creating new progress for quest ''{0}'' (UUID: {1})", quest.getDisplay(), nextActiveUuid);
                    this.plugin.getDataManager().registerQuestInstance(player, nextActiveUuid, questId, quest.getRequiredAmount());
                    nextProgress = this.plugin.getDataManager().getQuestInstance(player, nextActiveUuid);
                }
                if (nextProgress != null) {
                    if (nextProgress.isRedeemed()) {
                        this.plugin.debugLog(Level.WARNING, "[Queue] Next quest UUID {0} is marked as REDEEMED but shouldn''t be", nextActiveUuid);
                    }
                    if (nextProgress.isComplete(quest)) {
                        this.plugin.debugLog(Level.WARNING, "[Queue] Next quest UUID {0} is marked as COMPLETE before activation", nextActiveUuid);
                    }
                }
            }
            this.updateQuestPaperStatus(player, questId, nextActiveUuid, true);
            this.plugin.debugLog(Level.INFO, "[Queue] Next quest ''{0}'' set active", quest != null ? quest.getDisplay() : questId);
            this.plugin.debugLog(Level.INFO, "[Queue] Paper updated -> Active", new Object[0]);
            this.plugin.debugLog(Level.INFO, "[SoapsQuest][Check] Activated next queued quest {0}", nextActiveUuid);
            UUID verifyActive = this.getActiveQuestInstance(player, questId);
            if (verifyActive != null && verifyActive.equals(nextActiveUuid)) {
                this.plugin.debugLog(Level.INFO, "[Queue] \u2713 Verified quest {0} is now ACTIVE and ready for progress tracking", nextActiveUuid);
            } else {
                this.plugin.debugLog(Level.WARNING, "[Queue] Quest {0} activation verification failed (active: {1})", nextActiveUuid, verifyActive);
            }
            if (quest != null) {
                player.sendMessage(this.plugin.getMessageManager().getMessage("quest-activated", Map.of("quest", quest.getDisplay())));
            }
        } else if (wasActive && questQueue.isEmpty()) {
            this.plugin.debugLog(Level.INFO, "[Queue] No more quests of type ''{0}'' in queue", questId);
        }
        if (questQueue.isEmpty()) {
            questTypeQueues.remove(questId);
        }
        if (questTypeQueues.isEmpty()) {
            this.playerQuestQueues.remove(playerUuid);
        }
    }

    public UUID getActiveQuestInstance(Player player, String questId) {
        QuestProgress progress;
        Quest quest;
        UUID playerUuid = player.getUniqueId();
        Map<String, Queue<UUID>> questTypeQueues = this.playerQuestQueues.get(playerUuid);
        if (questTypeQueues == null) {
            this.plugin.debugLog(Level.INFO, "[ActiveCheck] Player ''{0}'' has no quest queues", player.getName());
            return null;
        }
        Queue<UUID> questQueue = questTypeQueues.get(questId);
        if (questQueue == null || questQueue.isEmpty()) {
            this.plugin.debugLog(Level.INFO, "[ActiveCheck] Player ''{0}'' has no queue for quest ''{1}''", player.getName(), questId);
            return null;
        }
        UUID activeUuid = questQueue.peek();
        this.plugin.debugLog(Level.INFO, "[ActiveCheck] Active quest for ''{0}'': UUID {1} (queue size: {2})", questId, activeUuid, questQueue.size());
        if (activeUuid != null && (quest = this.getQuest(questId)) != null && (progress = quest.isLockToPlayer() ? this.plugin.getDataManager().getLockedQuestInstance(activeUuid) : this.plugin.getDataManager().getQuestInstance(player, activeUuid)) == null) {
            this.plugin.getLogger().log(Level.WARNING, "[Data] CRITICAL: Active quest ''{0}'' (UUID: {1}) missing progress data!", new Object[]{quest.getDisplay(), activeUuid});
            this.plugin.getLogger().log(Level.WARNING, "[Data] This should NOT happen - progress should be created when paper is added to queue");
            this.plugin.getLogger().log(Level.WARNING, "[Data] Creating progress now as fallback, but investigate why this happened");
            this.plugin.getDataManager().registerQuestInstance(player, activeUuid, questId, quest.getRequiredAmount());
            progress = quest.isLockToPlayer() ? this.plugin.getDataManager().getLockedQuestInstance(activeUuid) : this.plugin.getDataManager().getQuestInstance(player, activeUuid);
            if (progress != null) {
                this.plugin.debugLog(Level.INFO, "[Data] \u2713 Fallback registration successful - progress now exists", new Object[0]);
            } else {
                this.plugin.getLogger().log(Level.SEVERE, "[Data] \u2717 FAILED to create progress data - quest will NOT work!");
            }
        }
        return activeUuid;
    }

    public boolean isQuestActive(Player player, String questId, UUID questInstanceUuid) {
        UUID activeInstance = this.getActiveQuestInstance(player, questId);
        return activeInstance != null && activeInstance.equals(questInstanceUuid);
    }

    /**
     * Captures which quest instance is active per quest type at the start of an event.
     * Prevents promoting the next queued instance mid-scan from receiving the same event.
     */
    public int countQueueActiveQuestTypes(Player player) {
        Map<String, Queue<UUID>> questTypeQueues = this.playerQuestQueues.get(player.getUniqueId());
        if (questTypeQueues == null || questTypeQueues.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (Queue<UUID> queue : questTypeQueues.values()) {
            if (queue != null && !queue.isEmpty()) {
                ++count;
            }
        }
        return count;
    }

    public boolean hasQuestTypeInQueue(Player player, String questId) {
        Map<String, Queue<UUID>> questTypeQueues = this.playerQuestQueues.get(player.getUniqueId());
        if (questTypeQueues == null) {
            return false;
        }
        Queue<UUID> queue = questTypeQueues.get(questId);
        return queue != null && !queue.isEmpty();
    }

    public boolean isWithinActiveLimit(Player player, String questId, int limit) {
        if (questId != null && this.hasQuestTypeInQueue(player, questId)) {
            return true;
        }
        return this.countQueueActiveQuestTypes(player) < limit;
    }

    public Map<String, UUID> snapshotActiveQuestInstances(Player player) {
        HashMap<String, UUID> snapshot = new HashMap<>();
        Map<String, Queue<UUID>> questTypeQueues = this.playerQuestQueues.get(player.getUniqueId());
        if (questTypeQueues == null) {
            return snapshot;
        }
        for (Map.Entry<String, Queue<UUID>> entry : questTypeQueues.entrySet()) {
            UUID activeUuid;
            Queue<UUID> queue = entry.getValue();
            if (queue == null || queue.isEmpty() || (activeUuid = queue.peek()) == null) continue;
            snapshot.put(entry.getKey(), activeUuid);
        }
        return snapshot;
    }

    public int getQuestQueuePosition(Player player, String questId, UUID questInstanceUuid) {
        UUID playerUuid = player.getUniqueId();
        Map<String, Queue<UUID>> questTypeQueues = this.playerQuestQueues.get(playerUuid);
        if (questTypeQueues == null) {
            return -1;
        }
        Queue<UUID> questQueue = questTypeQueues.get(questId);
        if (questQueue == null) {
            return -1;
        }
        this.cleanupQuestQueue(player, questId);
        int position = 0;
        for (UUID uuid : questQueue) {
            if (uuid.equals(questInstanceUuid)) {
                return position;
            }
            ++position;
        }
        return -1;
    }

    private void cleanupQuestQueue(Player player, String questId) {
        UUID playerUuid = player.getUniqueId();
        Map<String, Queue<UUID>> questTypeQueues = this.playerQuestQueues.get(playerUuid);
        if (questTypeQueues == null) {
            return;
        }
        Queue<UUID> questQueue = questTypeQueues.get(questId);
        if (questQueue == null) {
            return;
        }
        Set<UUID> inventoryQuestUuids = this.getQuestPapersInInventory(player, questId);
        UUID oldActiveUuid = questQueue.peek();
        this.plugin.debugLog(Level.INFO, "[Cleanup] Cleaning up queue for ''{0}'', inventory has {1} papers, queue has {2} entries", questId, inventoryQuestUuids.size(), questQueue.size());
        questQueue.removeIf(uuid -> {
            boolean shouldRemove;
            boolean bl = shouldRemove = !inventoryQuestUuids.contains(uuid);
            if (shouldRemove) {
                QuestProgress progress;
                this.plugin.debugLog(Level.INFO, "[Cleanup] Quest UUID {0} not in inventory, checking if safe to remove", uuid);
                Quest quest = this.getQuest(questId);
                boolean safeToRemove = true;
                if (quest != null && quest.isLockToPlayer() && (progress = this.plugin.getDataManager().getLockedQuestInstance((UUID)uuid)) != null && progress.isBound()) {
                    safeToRemove = progress.getOwnerUUID().equals(playerUuid);
                    this.plugin.debugLog(Level.INFO, "[Cleanup] Locked quest, safe to remove: {0}", safeToRemove);
                }
                if (safeToRemove) {
                    this.plugin.debugLog(Level.INFO, "[Cleanup] Removing quest instance {0} from DataManager (paper not in inventory)", uuid);
                    this.plugin.getDataManager().removeQuestInstance(player, (UUID)uuid);
                } else {
                    this.plugin.debugLog(Level.INFO, "[Cleanup] NOT removing quest instance {0} - owned by someone else", uuid);
                }
            }
            return shouldRemove;
        });
        UUID newActiveUuid = questQueue.peek();
        if (oldActiveUuid != null && newActiveUuid != null && !oldActiveUuid.equals(newActiveUuid)) {
            this.plugin.debugLog(Level.INFO, "[Queue] Active quest changed due to cleanup, activating next quest", new Object[0]);
            this.updateQuestPaperStatus(player, questId, newActiveUuid, true);
        }
        if (questQueue.isEmpty()) {
            questTypeQueues.remove(questId);
        }
        if (questTypeQueues.isEmpty()) {
            this.playerQuestQueues.remove(playerUuid);
        }
    }

    private Set<UUID> getQuestPapersInInventory(Player player, String questId) {
        HashSet<UUID> foundUuids = new HashSet<UUID>();
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) {
            return foundUuids;
        }
        for (ItemStack item : contents) {
            UUID instanceUuid;
            Quest quest;
            String paperQuestId;
            if (item == null || (paperQuestId = QuestPaper.getQuestId(item, this.plugin.getQuestIdKey())) == null || !paperQuestId.equals(questId) || (quest = this.getQuest(questId)) != null && quest.isLockToPlayer() && !QuestPaper.belongsToPlayer(item, player, this.plugin.getPlayerUuidKey()) || (instanceUuid = QuestPaper.getQuestInstanceId(item)) == null) continue;
            foundUuids.add(instanceUuid);
        }
        return foundUuids;
    }

    private void updateQuestPaperStatus(Player player, String questId, UUID questInstanceUuid, boolean isActive) {
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) {
            return;
        }
        for (int i = 0; i < contents.length; ++i) {
            ItemStack item = contents[i];
            if (item == null) continue;
            String paperQuestId = QuestPaper.getQuestId(item, this.plugin.getQuestIdKey());
            UUID paperInstanceUuid = QuestPaper.getQuestInstanceId(item);
            if (paperQuestId == null || !paperQuestId.equals(questId) || paperInstanceUuid == null || !paperInstanceUuid.equals(questInstanceUuid)) continue;
            Quest quest = this.getQuest(questId);
            QuestProgress progress = null;
            if (quest != null) {
                if (quest.isLockToPlayer()) {
                    progress = this.plugin.getDataManager().getLockedQuestInstance(questInstanceUuid);
                } else {
                    progress = this.plugin.getDataManager().getQuestInstance(player, questInstanceUuid);
                    if (progress == null) {
                        progress = this.plugin.getDataManager().transferOrAdoptQuest(player, questInstanceUuid, questId, quest.getRequiredAmount());
                    }
                }
            }
            if (quest == null || progress == null) break;
            ItemStack updatedPaper = QuestPaper.updateQuestPaperWithStatus(item, quest, progress, this.plugin.getMessageManager(), quest.getCustomLore(), isActive);
            player.getInventory().setItem(i, updatedPaper);
            break;
        }
    }

    public void updateAllQuestPapersForPlayer(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) {
            return;
        }
        int updated = 0;
        for (int slot = 0; slot < contents.length; ++slot) {
            QuestProgress progress;
            Quest quest;
            ItemStack item = contents[slot];
            if (item == null || !QuestPaper.isQuestPaper(item, this.plugin.getQuestIdKey())) continue;
            String questId = QuestPaper.getQuestId(item, this.plugin.getQuestIdKey());
            UUID questInstanceId = QuestPaper.getQuestInstanceId(item);
            if (questId == null || questInstanceId == null || (quest = this.getQuest(questId)) == null || (progress = quest.isLockToPlayer() ? this.plugin.getDataManager().getLockedQuestInstance(questInstanceId) : this.plugin.getDataManager().getQuestInstance(player, questInstanceId)) == null) continue;
            boolean isActive = this.isQuestActive(player, questId, questInstanceId);
            ItemStack updatedPaper = QuestPaper.updateQuestPaperWithStatus(item, quest, progress, this.plugin.getMessageManager(), quest.getCustomLore(), isActive);
            player.getInventory().setItem(slot, updatedPaper);
            ++updated;
        }
        if (updated > 0) {
            this.plugin.debugLog(Level.INFO, "[Lore] Updated {0} quest papers for player ''{1}''", updated, player.getName());
        }
    }

    public void refreshPlayerQueues(Player player) {
        UUID playerUuid = player.getUniqueId();
        this.plugin.debugLog(Level.INFO, "[Queue] Refreshing quest queues for player ''{0}''", player.getName());
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) {
            this.playerQuestQueues.remove(playerUuid);
            this.plugin.debugLog(Level.INFO, "[Queue] Player ''{0}'' has null inventory, clearing queues", player.getName());
            return;
        }
        HashMap<String, UUID> oldActiveQuests = new HashMap<String, UUID>();
        Map<String, Queue<UUID>> oldQueues = this.playerQuestQueues.get(playerUuid);
        if (oldQueues != null) {
            for (Map.Entry<String, Queue<UUID>> entry : oldQueues.entrySet()) {
                UUID activeUuid = entry.getValue().peek();
                if (activeUuid == null) continue;
                oldActiveQuests.put(entry.getKey(), activeUuid);
            }
        }
        Map<String, List<PaperInfo>> papersByType = new HashMap<>();
        int totalPapersFound = 0;
        for (int slot = 0; slot < contents.length; ++slot) {
            ItemStack item = contents[slot];
            if (item == null) continue;
            String questId = QuestPaper.getQuestId(item, this.plugin.getQuestIdKey());
            UUID instanceUuid = QuestPaper.getQuestInstanceId(item);
            if (questId == null || instanceUuid == null) continue;
            ++totalPapersFound;
            this.plugin.debugLog(Level.FINE, "[Scan] Found paper: questId=''{0}'', UUID={1}, slot={2}", questId, instanceUuid, slot);
            Quest quest = this.getQuest(questId);
            QuestProgress progress = null;
            if (quest != null) {
                if (quest.isLockToPlayer()) {
                    progress = this.plugin.getDataManager().getLockedQuestInstance(instanceUuid);
                    if (progress == null) {
                        this.plugin.debugLog(Level.INFO, "[Data] Creating new progress for locked quest ''{0}'' (UUID: {1}) during queue refresh", quest.getDisplay(), instanceUuid);
                        this.plugin.getDataManager().registerQuestInstance(player, instanceUuid, questId, quest.getRequiredAmount());
                        progress = this.plugin.getDataManager().getQuestInstance(player, instanceUuid);
                        if (progress == null) {
                            this.plugin.getLogger().log(Level.SEVERE, "[Data] FAILED to create progress for locked quest ''{0}''!", quest.getDisplay());
                        } else {
                            this.plugin.debugLog(Level.INFO, "[Data] \u2713 Progress created successfully for locked quest", new Object[0]);
                        }
                    }
                    if (progress != null && progress.isBound() && !progress.getOwnerUUID().equals(playerUuid)) {
                        this.plugin.debugLog(Level.INFO, "[Queue] Skipping locked quest ''{0}'' - bound to another player", quest.getDisplay());
                        continue;
                    }
                } else {
                    progress = this.plugin.getDataManager().getQuestInstance(player, instanceUuid);
                    if (progress == null) {
                        this.plugin.debugLog(Level.INFO, "[Data] Transferring/adopting unlocked quest ''{0}'' (UUID: {1}) during queue refresh", quest.getDisplay(), instanceUuid);
                        progress = this.plugin.getDataManager().transferOrAdoptQuest(player, instanceUuid, questId, quest.getRequiredAmount());
                        if (progress == null) {
                            this.plugin.getLogger().log(Level.SEVERE, "[Data] FAILED to transfer/adopt quest ''{0}''!", quest.getDisplay());
                        } else {
                            this.plugin.debugLog(Level.INFO, "[Data] \u2713 Quest transferred/adopted successfully", new Object[0]);
                        }
                    }
                }
            }
            if (progress != null) {
                if (progress.isRedeemed()) {
                    this.plugin.getLogger().log(Level.WARNING, "[Queue] Found redeemed quest in inventory - removing from queue: {0}", instanceUuid);
                    continue;
                }
                if (progress.isClaimable()) {
                    this.plugin.debugLog(Level.INFO, "[Queue] Skipping claimable quest ''{0}'' (UUID: {1}) - not added to queue", questId, instanceUuid);
                    continue;
                }
                papersByType.computeIfAbsent(questId, k -> new ArrayList<>()).add(new PaperInfo(instanceUuid, slot));
                continue;
            }
            if (quest != null) {
                this.plugin.debugLog(Level.INFO, "[Data] Registering missing progress for quest ''{0}'' (UUID: {1}) during queue refresh", quest.getDisplay(), instanceUuid);
                this.plugin.getDataManager().registerQuestInstance(player, instanceUuid, questId, quest.getRequiredAmount());
                progress = quest.isLockToPlayer() ? this.plugin.getDataManager().getLockedQuestInstance(instanceUuid) : this.plugin.getDataManager().getQuestInstance(player, instanceUuid);
                if (progress != null && !progress.isRedeemed() && !progress.isClaimable()) {
                    papersByType.computeIfAbsent(questId, k -> new ArrayList<>()).add(new PaperInfo(instanceUuid, slot));
                }
            } else {
                this.plugin.getLogger().log(Level.WARNING, "[Queue] Paper found but NO PROGRESS DATA for quest ''{0}'' (UUID: {1})", new Object[]{questId, instanceUuid});
            }
        }
        this.plugin.debugLog(Level.INFO, "[Queue] Found {0} quest papers in inventory", totalPapersFound);
        Map<String, Queue<UUID>> newQuestQueues = new HashMap<>();
        for (Map.Entry<String, List<PaperInfo>> entry : papersByType.entrySet()) {
            String questId = entry.getKey();
            List<PaperInfo> papers = entry.getValue();
            if (papers.isEmpty()) continue;
            Quest quest = this.getQuest(questId);
            String questDisplay = quest != null ? quest.getDisplay() : questId;
            papers.sort(Comparator.comparingInt(p -> p.slot));
            UUID oldActive = (UUID)oldActiveQuests.get(questId);
            int activeIndex = 0;
            if (oldActive != null) {
                for (int i = 0; i < papers.size(); ++i) {
                    if (!((PaperInfo)papers.get((int)i)).uuid.equals(oldActive)) continue;
                    activeIndex = i;
                    break;
                }
            }
            if (activeIndex >= papers.size()) {
                activeIndex = 0;
            }
            LinkedList<UUID> queue = new LinkedList<UUID>();
            queue.offer(((PaperInfo)papers.get((int)activeIndex)).uuid);
            this.plugin.debugLog(Level.INFO, "[Queue] Quest ''{0}'' (UUID: {1}) set as ACTIVE", questDisplay, ((PaperInfo)papers.get((int)activeIndex)).uuid);
            for (int i = 0; i < papers.size(); ++i) {
                if (i == activeIndex) continue;
                queue.offer(((PaperInfo)papers.get((int)i)).uuid);
                this.plugin.debugLog(Level.INFO, "[Queue] Quest ''{0}'' (UUID: {1}) set as QUEUED", questDisplay, ((PaperInfo)papers.get((int)i)).uuid);
            }
            newQuestQueues.put(questId, queue);
            for (PaperInfo paperInfo : papers) {
                boolean isActive = paperInfo.uuid.equals(queue.peek());
                this.updateQuestPaperStatus(player, questId, paperInfo.uuid, isActive);
            }
        }
        if (newQuestQueues.isEmpty()) {
            this.playerQuestQueues.remove(playerUuid);
            this.plugin.debugLog(Level.INFO, "[Queue] No quest queues for player ''{0}''", player.getName());
        } else {
            this.playerQuestQueues.put(playerUuid, newQuestQueues);
            this.plugin.debugLog(Level.INFO, "[Queue] Player ''{0}'' now has {1} quest type(s) in queue", player.getName(), newQuestQueues.size());
        }
    }

    private static class PaperInfo {
        UUID uuid;
        int slot;

        PaperInfo(UUID uuid, int slot) {
            this.uuid = uuid;
            this.slot = slot;
        }
    }
}

