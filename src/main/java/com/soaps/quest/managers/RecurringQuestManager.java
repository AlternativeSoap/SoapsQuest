/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
 *  net.kyori.adventure.title.Title
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.NamespacedKey
 *  org.bukkit.Registry
 *  org.bukkit.Sound
 *  org.bukkit.boss.BarColor
 *  org.bukkit.boss.BarFlag
 *  org.bukkit.boss.BarStyle
 *  org.bukkit.boss.BossBar
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package com.soaps.quest.managers;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.utils.QuestPaper;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class RecurringQuestManager {
    private final SoapsQuest plugin;
    private FileConfiguration config;
    private List<String> dailyQuestIds;
    private List<String> weeklyQuestIds;
    private boolean dailyEnabled;
    private boolean weeklyEnabled;
    private LocalTime dailyResetTime;
    private DayOfWeek weeklyResetDay;
    private LocalTime weeklyResetTime;
    private boolean randomizeDaily;
    private boolean randomizeWeekly;
    private int dailyQuestCount;
    private int weeklyQuestCount;
    private boolean dailyBonusEnabled;
    private boolean weeklyBonusEnabled;
    private ConfigurationSection dailyBonusRewardSection;
    private ConfigurationSection weeklyBonusRewardSection;
    private String resetNotificationMode;
    private boolean sendResetSound;
    private String resetSoundType;
    private float resetSoundVolume;
    private float resetSoundPitch;
    private final Map<UUID, RecurringQuestData> playerData;
    private BukkitTask dailyResetTask;
    private BukkitTask weeklyResetTask;
    private long lastDailyReset;
    private long lastWeeklyReset;

    public RecurringQuestManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.playerData = new HashMap<UUID, RecurringQuestData>();
        this.dailyQuestIds = new ArrayList<String>();
        this.weeklyQuestIds = new ArrayList<String>();
        this.loadConfiguration();
        this.loadPlayerData();
        this.scheduleResetTasks();
        plugin.debugLog(Level.INFO, "[RecurringQuests] Manager initialized - Daily: {0}, Weekly: {1}", this.dailyEnabled, this.weeklyEnabled);
    }

    private void loadConfiguration() {
        File file = new File(this.plugin.getDataFolder(), "daily.yml");
        if (!file.exists()) {
            this.plugin.saveResource("daily.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration((File)file);
        this.dailyEnabled = this.config.getBoolean("daily.enabled", false);
        this.dailyQuestIds = this.config.getStringList("daily.quests");
        String dailyResetTimeStr = this.config.getString("daily.reset-time", "00:00");
        this.dailyResetTime = this.parseTime(dailyResetTimeStr);
        this.randomizeDaily = this.config.getBoolean("daily.selection.random", this.config.getBoolean("daily.randomize", false));
        this.dailyQuestCount = this.config.getInt("daily.selection.count", this.config.getInt("daily.count", this.dailyQuestIds.size()));
        this.weeklyEnabled = this.config.getBoolean("weekly.enabled", false);
        this.weeklyQuestIds = this.config.getStringList("weekly.quests");
        String weeklyResetDayStr = this.config.getString("weekly.reset-day", "MONDAY");
        this.weeklyResetDay = this.parseDayOfWeek(weeklyResetDayStr);
        String weeklyResetTimeStr = this.config.getString("weekly.reset-time", "00:00");
        this.weeklyResetTime = this.parseTime(weeklyResetTimeStr);
        this.randomizeWeekly = this.config.getBoolean("weekly.selection.random", this.config.getBoolean("weekly.randomize", false));
        this.weeklyQuestCount = this.config.getInt("weekly.selection.count", this.config.getInt("weekly.count", this.weeklyQuestIds.size()));
        this.dailyBonusEnabled = this.config.getBoolean("daily.completion-bonus.enabled", false);
        this.weeklyBonusEnabled = this.config.getBoolean("weekly.completion-bonus.enabled", false);
        this.dailyBonusRewardSection = this.config.getConfigurationSection("daily.completion-bonus.reward");
        this.weeklyBonusRewardSection = this.config.getConfigurationSection("weekly.completion-bonus.reward");
        this.resetNotificationMode = this.config.getString("notifications.mode", "actionbar");
        this.sendResetSound = this.config.getBoolean("notifications.sound.enabled", true);
        this.resetSoundType = this.config.getString("notifications.sound.type", "ENTITY_PLAYER_LEVELUP");
        this.resetSoundVolume = (float)this.config.getDouble("notifications.sound.volume", 1.0);
        this.resetSoundPitch = (float)this.config.getDouble("notifications.sound.pitch", 1.0);
        ConfigurationSection internalData = this.plugin.getDataManager().getConfig().getConfigurationSection("recurring-quests.internal");
        if (internalData != null) {
            this.lastDailyReset = internalData.getLong("last-daily-reset", 0L);
            this.lastWeeklyReset = internalData.getLong("last-weekly-reset", 0L);
        } else {
            this.lastDailyReset = 0L;
            this.lastWeeklyReset = 0L;
        }
        this.plugin.debugLog(Level.INFO, "[RecurringQuests] Configuration loaded - Daily quests: {0}, Weekly quests: {1}", this.dailyQuestIds.size(), this.weeklyQuestIds.size());
    }

    private LocalTime parseTime(String timeStr) {
        try {
            String[] parts = timeStr.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            return LocalTime.of(hour, minute);
        }
        catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            this.plugin.getLogger().log(Level.WARNING, "Invalid time format: {0}, using 00:00", timeStr);
            return LocalTime.MIDNIGHT;
        }
    }

    private DayOfWeek parseDayOfWeek(String dayStr) {
        try {
            return DayOfWeek.valueOf(dayStr.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            this.plugin.getLogger().log(Level.WARNING, "Invalid day of week: {0}, using MONDAY", dayStr);
            return DayOfWeek.MONDAY;
        }
    }

    private void loadPlayerData() {
        this.playerData.clear();
        ConfigurationSection section = this.plugin.getDataManager().getRecurringQuestSection();
        if (section == null) {
            this.plugin.debugLog(Level.INFO, "[RecurringQuests] No player data to load", new Object[0]);
            return;
        }
        for (String uuidStr : section.getKeys(false)) {
            if (uuidStr.equals("internal")) continue;
            try {
                UUID playerUuid = UUID.fromString(uuidStr);
                RecurringQuestData data = new RecurringQuestData();
                Object value = section.get(uuidStr);
                if (value instanceof String) {
                    String[] weeklyParts;
                    String[] dailyParts;
                    String compact = (String)value;
                    String[] parts = compact.split("\\|");
                    if (parts.length > 0 && (dailyParts = parts[0].split(":", 3)).length == 3 && dailyParts[0].equals("d")) {
                        if (!dailyParts[1].isEmpty()) {
                            data.dailyQuestsGiven.addAll(Arrays.asList(dailyParts[1].split(",")));
                        }
                        data.lastDailyReset = Long.parseLong(dailyParts[2]);
                    }
                    if (parts.length > 1 && (weeklyParts = parts[1].split(":", 3)).length == 3 && weeklyParts[0].equals("w")) {
                        if (!weeklyParts[1].isEmpty()) {
                            data.weeklyQuestsGiven.addAll(Arrays.asList(weeklyParts[1].split(",")));
                        }
                        data.lastWeeklyReset = Long.parseLong(weeklyParts[2]);
                    }
                    for (String part : parts) {
                        if (part.startsWith("dc:")) {
                            String completed = part.substring(3);
                            if (!completed.isEmpty()) {
                                data.dailyQuestsCompleted.addAll(Arrays.asList(completed.split(",")));
                            }
                        } else if (part.startsWith("wc:")) {
                            String completed = part.substring(3);
                            if (!completed.isEmpty()) {
                                data.weeklyQuestsCompleted.addAll(Arrays.asList(completed.split(",")));
                            }
                        } else if (part.equals("db:1")) {
                            data.dailyBonusClaimed = true;
                        } else if (part.equals("wb:1")) {
                            data.weeklyBonusClaimed = true;
                        }
                    }
                    this.plugin.debugLog(Level.INFO, "[RecurringQuests] Loaded compact data for player {0}: {1} daily, {2} weekly", uuidStr, data.dailyQuestsGiven.size(), data.weeklyQuestsGiven.size());
                } else {
                    List dailyGiven = section.getStringList(uuidStr + ".daily.given");
                    data.dailyQuestsGiven.addAll(dailyGiven);
                    data.lastDailyReset = section.getLong(uuidStr + ".daily.last-reset", 0L);
                    data.dailyQuestsCompleted.addAll(section.getStringList(uuidStr + ".daily.completed"));
                    data.dailyBonusClaimed = section.getBoolean(uuidStr + ".daily.bonus-claimed", false);
                    List weeklyGiven = section.getStringList(uuidStr + ".weekly.given");
                    data.weeklyQuestsGiven.addAll(weeklyGiven);
                    data.lastWeeklyReset = section.getLong(uuidStr + ".weekly.last-reset", 0L);
                    data.weeklyQuestsCompleted.addAll(section.getStringList(uuidStr + ".weekly.completed"));
                    data.weeklyBonusClaimed = section.getBoolean(uuidStr + ".weekly.bonus-claimed", false);
                    this.plugin.debugLog(Level.INFO, "[RecurringQuests] Loaded legacy data for player {0}: {1} daily, {2} weekly", uuidStr, dailyGiven.size(), weeklyGiven.size());
                }
                this.playerData.put(playerUuid, data);
            }
            catch (IllegalArgumentException e) {
                this.plugin.getLogger().log(Level.WARNING, "Failed to parse recurring quest data for {0}: {1}", new Object[]{uuidStr, e.getMessage()});
            }
        }
        this.plugin.getLogger().log(Level.INFO, "Loaded recurring quest data for {0} players", this.playerData.size());
    }

    public void savePlayerData() {
        for (Map.Entry<UUID, RecurringQuestData> entry : this.playerData.entrySet()) {
            UUID playerUuid = entry.getKey();
            RecurringQuestData data = entry.getValue();
            StringBuilder compact = new StringBuilder();
            compact.append("d:");
            if (!data.dailyQuestsGiven.isEmpty()) {
                compact.append(String.join((CharSequence)",", data.dailyQuestsGiven));
            }
            compact.append(":");
            compact.append(data.lastDailyReset);
            compact.append("|w:");
            if (!data.weeklyQuestsGiven.isEmpty()) {
                compact.append(String.join((CharSequence)",", data.weeklyQuestsGiven));
            }
            compact.append(":");
            compact.append(data.lastWeeklyReset);
            compact.append("|dc:");
            if (!data.dailyQuestsCompleted.isEmpty()) {
                compact.append(String.join((CharSequence)",", data.dailyQuestsCompleted));
            }
            compact.append("|wc:");
            if (!data.weeklyQuestsCompleted.isEmpty()) {
                compact.append(String.join((CharSequence)",", data.weeklyQuestsCompleted));
            }
            compact.append("|db:");
            compact.append(data.dailyBonusClaimed ? "1" : "0");
            compact.append("|wb:");
            compact.append(data.weeklyBonusClaimed ? "1" : "0");
            this.plugin.getDataManager().setRecurringQuestData(playerUuid.toString(), compact.toString());
        }
        this.plugin.getDataManager().getConfig().set("recurring-quests.internal.last-daily-reset", (Object)this.lastDailyReset);
        this.plugin.getDataManager().getConfig().set("recurring-quests.internal.last-weekly-reset", (Object)this.lastWeeklyReset);
        this.plugin.getDataManager().saveData();
        this.plugin.debugLog(Level.INFO, "[RecurringQuests] Saved data for {0} players", this.playerData.size());
    }

    private void scheduleResetTasks() {
        if (this.dailyResetTask != null) {
            this.dailyResetTask.cancel();
        }
        if (this.weeklyResetTask != null) {
            this.weeklyResetTask.cancel();
        }
        if (this.dailyEnabled) {
            long ticksUntilDailyReset = this.calculateTicksUntilNextReset(this.dailyResetTime, null);
            this.dailyResetTask = new BukkitRunnable(){

                public void run() {
                    RecurringQuestManager.this.performDailyReset();
                    long ticksUntilNext = 1728000L;
                    RecurringQuestManager.this.dailyResetTask = new BukkitRunnable(){

                        public void run() {
                            RecurringQuestManager.this.performDailyReset();
                        }
                    }.runTaskTimer((Plugin)RecurringQuestManager.this.plugin, ticksUntilNext, ticksUntilNext);
                }
            }.runTaskLater((Plugin)this.plugin, ticksUntilDailyReset);
            this.plugin.getLogger().log(Level.INFO, "Daily quest reset scheduled for {0} (in {1} seconds)", new Object[]{this.dailyResetTime, ticksUntilDailyReset / 20L});
        }
        if (this.weeklyEnabled) {
            long ticksUntilWeeklyReset = this.calculateTicksUntilNextReset(this.weeklyResetTime, this.weeklyResetDay);
            this.weeklyResetTask = new BukkitRunnable(){

                public void run() {
                    RecurringQuestManager.this.performWeeklyReset();
                    long ticksUntilNext = 12096000L;
                    RecurringQuestManager.this.weeklyResetTask = new BukkitRunnable(){

                        public void run() {
                            RecurringQuestManager.this.performWeeklyReset();
                        }
                    }.runTaskTimer((Plugin)RecurringQuestManager.this.plugin, ticksUntilNext, ticksUntilNext);
                }
            }.runTaskLater((Plugin)this.plugin, ticksUntilWeeklyReset);
            this.plugin.getLogger().log(Level.INFO, "Weekly quest reset scheduled for {0} {1} (in {2} seconds)", new Object[]{this.weeklyResetDay, this.weeklyResetTime, ticksUntilWeeklyReset / 20L});
        }
    }

    private long calculateTicksUntilNextReset(LocalTime resetTime, DayOfWeek dayOfWeek) {
        LocalDateTime nextReset;
        LocalDateTime now = LocalDateTime.now();
        if (dayOfWeek == null) {
            nextReset = now.with(resetTime);
            if (nextReset.isBefore(now) || nextReset.isEqual(now)) {
                nextReset = nextReset.plusDays(1L);
            }
        } else {
            nextReset = now.with(TemporalAdjusters.nextOrSame(dayOfWeek)).with(resetTime);
            if (nextReset.isBefore(now) || nextReset.isEqual(now)) {
                nextReset = nextReset.plusWeeks(1L);
            }
        }
        long secondsUntil = ChronoUnit.SECONDS.between(now, nextReset);
        return secondsUntil * 20L;
    }

    private void performDailyReset() {
        this.plugin.getLogger().info("Performing daily quest reset...");
        this.lastDailyReset = System.currentTimeMillis();
        for (RecurringQuestData data : this.playerData.values()) {
            data.dailyQuestsGiven.clear();
            data.dailyQuestsCompleted.clear();
            data.dailyBonusClaimed = false;
            data.lastDailyReset = this.lastDailyReset;
        }
        this.savePlayerData();
        for (final Player player : Bukkit.getOnlinePlayers()) {
            this.notifyPlayerOfReset(player, "daily");
            new BukkitRunnable() {
                @Override
                public void run() {
                    RecurringQuestManager.this.giveEligibleDailyQuests(player);
                }
            }.runTaskLater(this.plugin, 20L);
        }
        this.plugin.debugLog(Level.INFO, "[RecurringQuests] Daily reset completed for {0} online players", Bukkit.getOnlinePlayers().size());
    }

    private void performWeeklyReset() {
        this.plugin.getLogger().info("Performing weekly quest reset...");
        this.lastWeeklyReset = System.currentTimeMillis();
        for (RecurringQuestData data : this.playerData.values()) {
            data.weeklyQuestsGiven.clear();
            data.weeklyQuestsCompleted.clear();
            data.weeklyBonusClaimed = false;
            data.lastWeeklyReset = this.lastWeeklyReset;
        }
        this.savePlayerData();
        for (final Player player : Bukkit.getOnlinePlayers()) {
            this.notifyPlayerOfReset(player, "weekly");
            new BukkitRunnable() {
                @Override
                public void run() {
                    RecurringQuestManager.this.giveEligibleWeeklyQuests(player);
                }
            }.runTaskLater(this.plugin, 20L);
        }
        this.plugin.debugLog(Level.INFO, "[RecurringQuests] Weekly reset completed for {0} online players", Bukkit.getOnlinePlayers().size());
    }

    private void notifyPlayerOfReset(Player player, String resetType) {
        Component message = this.plugin.getMessageManager().getMessage("recurring-quest-reset-" + resetType);
        switch (this.resetNotificationMode.toLowerCase()) {
            case "actionbar": {
                player.sendActionBar(message);
                break;
            }
            case "chat": {
                player.sendMessage(message);
                break;
            }
            case "title": {
                Component titleText = this.plugin.getMessageManager().getMessage("recurring-quest-reset-" + resetType + "-title");
                Component subtitleText = this.plugin.getMessageManager().getMessage("recurring-quest-reset-" + resetType + "-subtitle");
                player.showTitle(Title.title((Component)titleText, (Component)subtitleText));
                break;
            }
            case "bossbar": {
                String legacyText = LegacyComponentSerializer.legacySection().serialize(message);
                BarColor barColor = resetType.equals("daily") ? BarColor.GREEN : BarColor.YELLOW;
                final BossBar bar = Bukkit.createBossBar((String)legacyText, (BarColor)barColor, (BarStyle)BarStyle.SOLID, (BarFlag[])new BarFlag[0]);
                bar.addPlayer(player);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        bar.removeAll();
                    }
                }.runTaskLater(this.plugin, 100L);
                break;
            }
            case "none": {
                break;
            }
            default: {
                this.plugin.debugLog(Level.WARNING, "[RecurringQuests] Unknown notification mode: {0}", this.resetNotificationMode);
            }
        }
        if (this.sendResetSound) {
            try {
                String soundKey = this.resetSoundType.toLowerCase().replace('_', '.');
                NamespacedKey key = NamespacedKey.minecraft((String)soundKey);
                Sound sound = (Sound)Registry.SOUNDS.get(key);
                Location location = player.getLocation();
                if (location != null && sound != null) {
                    player.playSound(location, sound, this.resetSoundVolume, this.resetSoundPitch);
                }
            }
            catch (IllegalArgumentException e) {
                this.plugin.debugLog(Level.WARNING, "[RecurringQuests] Invalid sound type: {0}", this.resetSoundType);
            }
        }
    }

    public void giveEligibleDailyQuests(Player player) {
        List<String> questsToGive;
        if (!this.dailyEnabled || this.dailyQuestIds.isEmpty()) {
            return;
        }
        RecurringQuestData data = this.playerData.computeIfAbsent(player.getUniqueId(), k -> new RecurringQuestData());
        if (data.lastDailyReset < this.lastDailyReset) {
            data.dailyQuestsGiven.clear();
            data.dailyQuestsCompleted.clear();
            data.dailyBonusClaimed = false;
            data.lastDailyReset = this.lastDailyReset;
        }
        if ((questsToGive = this.selectQuestsToGive(this.dailyQuestIds, data.dailyQuestsGiven, this.randomizeDaily, this.dailyQuestCount, player)).isEmpty()) {
            this.plugin.debugLog(Level.INFO, "[RecurringQuests] No eligible daily quests for {0}", player.getName());
            return;
        }
        int given = 0;
        for (String questId : questsToGive) {
            if (!this.giveQuestPaper(player, questId, "daily")) continue;
            data.dailyQuestsGiven.add(questId);
            ++given;
        }
        if (given > 0) {
            player.sendMessage(this.plugin.getMessageManager().getMessage("recurring-quest-received-daily", Map.of("count", String.valueOf(given))));
            this.plugin.debugLog(Level.INFO, "[RecurringQuests] Gave {0} daily quests to {1}", given, player.getName());
        }
    }

    public void giveEligibleWeeklyQuests(Player player) {
        List<String> questsToGive;
        if (!this.weeklyEnabled || this.weeklyQuestIds.isEmpty()) {
            return;
        }
        RecurringQuestData data = this.playerData.computeIfAbsent(player.getUniqueId(), k -> new RecurringQuestData());
        if (data.lastWeeklyReset < this.lastWeeklyReset) {
            data.weeklyQuestsGiven.clear();
            data.weeklyQuestsCompleted.clear();
            data.weeklyBonusClaimed = false;
            data.lastWeeklyReset = this.lastWeeklyReset;
        }
        if ((questsToGive = this.selectQuestsToGive(this.weeklyQuestIds, data.weeklyQuestsGiven, this.randomizeWeekly, this.weeklyQuestCount, player)).isEmpty()) {
            this.plugin.debugLog(Level.INFO, "[RecurringQuests] No eligible weekly quests for {0}", player.getName());
            return;
        }
        int given = 0;
        for (String questId : questsToGive) {
            if (!this.giveQuestPaper(player, questId, "weekly")) continue;
            data.weeklyQuestsGiven.add(questId);
            ++given;
        }
        if (given > 0) {
            player.sendMessage(this.plugin.getMessageManager().getMessage("recurring-quest-received-weekly", Map.of("count", String.valueOf(given))));
            this.plugin.debugLog(Level.INFO, "[RecurringQuests] Gave {0} weekly quests to {1}", given, player.getName());
        }
    }

    private List<String> selectQuestsToGive(List<String> availableQuests, Set<String> alreadyGiven, boolean randomize, int count, Player player) {
        List<String> eligible = availableQuests.stream().filter(questId -> !alreadyGiven.contains(questId)).filter(questId -> {
            Quest quest = this.plugin.getQuestManager().getQuest(questId);
            if (quest == null) {
                this.plugin.getLogger().log(Level.WARNING, "Recurring quest not found: {0}", questId);
                return false;
            }
            if (!quest.hasPermission(player)) {
                this.plugin.debugLog(Level.INFO, "[RecurringQuests] Player {0} missing permission for quest ''{1}'' \u2014 skipping", player.getName(), questId);
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        if (eligible.isEmpty()) {
            return new ArrayList<String>();
        }
        if (randomize) {
            ArrayList<String> shuffled = new ArrayList<>(eligible);
            Collections.shuffle(shuffled);
            return shuffled.stream().limit(count).collect(Collectors.toList());
        }
        return eligible.stream().limit(count).collect(Collectors.toList());
    }

    private boolean giveQuestPaper(Player player, String questId, String questType) {
        Quest quest = this.plugin.getQuestManager().getQuest(questId);
        if (quest == null) {
            this.plugin.getLogger().log(Level.WARNING, "Cannot give recurring quest - quest not found: {0}", questId);
            return false;
        }
        ItemStack paper = QuestPaper.createQuestPaper(quest, player, this.plugin.getMessageManager(), this.plugin.getQuestIdKey(), this.plugin.getPlayerUuidKey());
        UUID questInstanceUuid = QuestPaper.getQuestInstanceId(paper);
        if (questInstanceUuid != null) {
            this.plugin.getDataManager().registerQuestInstance(player, questInstanceUuid, questId, quest.getRequiredAmount());
            this.plugin.getQuestManager().addQuestToQueue(player, questId, questInstanceUuid);
        }
        if (player.getInventory().firstEmpty() == -1) {
            Location location = player.getLocation();
            if (location != null) {
                player.getWorld().dropItemNaturally(location, paper);
                this.plugin.debugLog(Level.INFO, "[RecurringQuests] Dropped {0} quest paper for {1} (inventory full)", questType, player.getName());
            }
        } else {
            player.getInventory().addItem(new ItemStack[]{paper});
            this.plugin.debugLog(Level.INFO, "[RecurringQuests] Gave {0} quest '{1}' to {2}", questType, questId, player.getName());
        }
        return true;
    }

    public void onPlayerJoin(final Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                RecurringQuestManager.this.giveEligibleDailyQuests(player);
                RecurringQuestManager.this.giveEligibleWeeklyQuests(player);
            }
        }.runTaskLater(this.plugin, 40L);
    }

    public boolean isDailyQuest(String questId) {
        return this.dailyQuestIds.contains(questId);
    }

    public boolean isWeeklyQuest(String questId) {
        return this.weeklyQuestIds.contains(questId);
    }

    public boolean hasReceivedDailyQuest(Player player, String questId) {
        RecurringQuestData data = this.playerData.get(player.getUniqueId());
        return data != null && data.dailyQuestsGiven.contains(questId);
    }

    public boolean hasReceivedWeeklyQuest(Player player, String questId) {
        RecurringQuestData data = this.playerData.get(player.getUniqueId());
        return data != null && data.weeklyQuestsGiven.contains(questId);
    }

    public List<String> getDailyQuestIds() {
        return new ArrayList<String>(this.dailyQuestIds);
    }

    public List<String> getWeeklyQuestIds() {
        return new ArrayList<String>(this.weeklyQuestIds);
    }

    public void handleQuestClaimed(Player player, String questId) {
        RecurringQuestData data = this.playerData.computeIfAbsent(player.getUniqueId(), k -> new RecurringQuestData());
        boolean touched = false;
        if (this.dailyQuestIds.contains(questId) && data.dailyQuestsGiven.contains(questId)) {
            data.dailyQuestsCompleted.add(questId);
            this.tryAwardCycleBonus(player, data, true);
            touched = true;
        }
        if (this.weeklyQuestIds.contains(questId) && data.weeklyQuestsGiven.contains(questId)) {
            data.weeklyQuestsCompleted.add(questId);
            this.tryAwardCycleBonus(player, data, false);
            touched = true;
        }
        if (touched) {
            this.savePlayerData();
        }
    }

    private void tryAwardCycleBonus(Player player, RecurringQuestData data, boolean daily) {
        Set<String> given = daily ? data.dailyQuestsGiven : data.weeklyQuestsGiven;
        Set<String> completed = daily ? data.dailyQuestsCompleted : data.weeklyQuestsCompleted;
        boolean claimed = daily ? data.dailyBonusClaimed : data.weeklyBonusClaimed;
        boolean enabled = daily ? this.dailyBonusEnabled : this.weeklyBonusEnabled;
        ConfigurationSection rewardSection = daily ? this.dailyBonusRewardSection : this.weeklyBonusRewardSection;
        if (!enabled || claimed || given.isEmpty() || rewardSection == null) {
            return;
        }
        if (!completed.containsAll(given)) {
            return;
        }
        this.plugin.getRewardManager().giveRewardsFromSection(rewardSection, player);
        if (daily) {
            data.dailyBonusClaimed = true;
            player.sendMessage(this.plugin.getMessageManager().getMessage("recurring-quest-daily-bonus"));
        } else {
            data.weeklyBonusClaimed = true;
            player.sendMessage(this.plugin.getMessageManager().getMessage("recurring-quest-weekly-bonus"));
        }
    }

    public void reload() {
        this.plugin.getLogger().info("Reloading recurring quest manager...");
        this.loadConfiguration();
        this.scheduleResetTasks();
        this.plugin.getLogger().info("Recurring quest manager reloaded successfully");
    }

    public void shutdown() {
        if (this.dailyResetTask != null) {
            this.dailyResetTask.cancel();
        }
        if (this.weeklyResetTask != null) {
            this.weeklyResetTask.cancel();
        }
        this.savePlayerData();
        this.plugin.debugLog(Level.INFO, "[RecurringQuests] Manager shutdown complete", new Object[0]);
    }

    private static class RecurringQuestData {
        Set<String> dailyQuestsGiven = new HashSet<String>();
        Set<String> weeklyQuestsGiven = new HashSet<String>();
        Set<String> dailyQuestsCompleted = new HashSet<String>();
        Set<String> weeklyQuestsCompleted = new HashSet<String>();
        boolean dailyBonusClaimed;
        boolean weeklyBonusClaimed;
        long lastDailyReset = 0L;
        long lastWeeklyReset = 0L;

        private RecurringQuestData() {
        }
    }
}

