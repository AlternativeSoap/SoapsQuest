/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
 *  org.bukkit.Material
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 */
package com.soaps.quest.managers;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.managers.CustomItemManager;
import com.soaps.quest.managers.DifficultyManager;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.rewards.CommandReward;
import com.soaps.quest.rewards.ItemReward;
import com.soaps.quest.rewards.MoneyReward;
import com.soaps.quest.rewards.Reward;
import com.soaps.quest.rewards.RewardEntry;
import com.soaps.quest.rewards.SigilReward;
import com.soaps.quest.rewards.XPReward;
import com.soaps.quest.rewards.types.QuestQuestReward;
import com.soaps.quest.utils.ItemStackBuilder;
import com.soaps.quest.utils.YamlUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class RewardManager {
    private final SoapsQuest plugin;
    private final Map<String, List<Reward>> questRewards;
    private final Map<String, List<RewardEntry>> questRewardEntries;

    public RewardManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.questRewards = new HashMap<String, List<Reward>>();
        this.questRewardEntries = new HashMap<String, List<RewardEntry>>();
    }

    public void loadRewards() {
        this.questRewards.clear();
        this.questRewardEntries.clear();
        File questsFile = new File(this.plugin.getDataFolder(), "quests.yml");
        if (questsFile.exists()) {
            this.loadRewardsFromFile(questsFile, "quests");
        } else {
            this.plugin.saveResource("quests.yml", false);
        }
        File generatedFile = new File(this.plugin.getDataFolder(), "generated.yml");
        if (generatedFile.exists()) {
            this.loadRewardsFromFile(generatedFile, null);
        }
    }

    private void loadRewardsFromFile(File file, String wrapperKey) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection questsSection = wrapperKey != null
                ? config.getConfigurationSection(wrapperKey)
                : config;
        if (questsSection == null) {
            return;
        }
        for (String questId : questsSection.getKeys(false)) {
            ConfigurationSection questSection = questsSection.getConfigurationSection(questId);
            if (questSection == null) continue;
            ConfigurationSection rewardSection = questSection.getConfigurationSection("reward");
            if (rewardSection == null && questSection.contains("rewards")) {
                Object rewardsNode = questSection.get("rewards");
                if (rewardsNode instanceof List<?> list && !list.isEmpty()) {
                    Object first = list.get(0);
                    if (first instanceof Map<?, ?> rewardMap) {
                        YamlConfiguration tempConfig = new YamlConfiguration();
                        for (Map.Entry<?, ?> entry : rewardMap.entrySet()) {
                            tempConfig.set(String.valueOf(entry.getKey()), entry.getValue());
                        }
                        rewardSection = tempConfig;
                        this.plugin.getLogger().log(Level.FINE, "Loaded legacy ''rewards'' list for quest: {0}", questId);
                    }
                } else if (rewardsNode instanceof ConfigurationSection section) {
                    rewardSection = section;
                    this.plugin.getLogger().log(Level.FINE, "Loaded legacy ''rewards'' section for quest: {0}", questId);
                }
            }
            if (rewardSection == null) continue;
            List<Reward> rewards = this.loadRewardsForQuest(rewardSection);
            List<RewardEntry> rewardEntries = this.loadRewardEntriesForQuest(rewardSection);
            if (!rewards.isEmpty()) {
                this.questRewards.put(questId, rewards);
            }
            if (rewardEntries.isEmpty()) continue;
            this.questRewardEntries.put(questId, rewardEntries);
        }
    }

    private List<Reward> loadRewardsForQuest(ConfigurationSection rewardSection) {
        ArrayList<Reward> rewards = new ArrayList<Reward>();
        if (rewardSection == null) {
            return rewards;
        }
        if (rewardSection.contains("xp")) {
            int xp = rewardSection.getInt("xp");
            int chance = rewardSection.getInt("xp-chance", 100);
            rewards.add(new XPReward(xp, chance));
        }
        if (rewardSection.contains("money")) {
            double money = rewardSection.getDouble("money");
            int chance = rewardSection.getInt("money-chance", 100);
            rewards.add(new MoneyReward(this.plugin, money, chance));
        }
        if (rewardSection.contains("sigils")) {
            double amount = rewardSection.getDouble("sigils");
            int chance = rewardSection.getInt("sigils-chance", 100);
            rewards.add(new SigilReward(this.plugin, amount, chance));
        }
        List<?> itemsList;
        if (rewardSection.contains("items") && (itemsList = rewardSection.getList("items")) != null) {
            for (Object obj : itemsList) {
                if (!(obj instanceof Map)) continue;
                @SuppressWarnings("unchecked")
                Map<String, Object> itemMap = (Map<String, Object>) obj;
                ItemReward itemReward = this.loadItemReward(itemMap);
                if (itemReward == null) continue;
                rewards.add(itemReward);
            }
        }
        if (rewardSection.contains("commands")) {
            List<String> commands = rewardSection.getStringList("commands");
            int commandChance = rewardSection.getInt("command-chance", 100);
            for (String command : commands) {
                rewards.add(new CommandReward(command, commandChance));
            }
        }
        if (rewardSection.contains("quest") || rewardSection.isConfigurationSection("quest")) {
            String targetQuestId;
            if (rewardSection.isConfigurationSection("quest")) {
                ConfigurationSection questSection = rewardSection.getConfigurationSection("quest");
                if (questSection != null) {
                    String targetQuestId2 = questSection.getString("quest-id");
                    int questChance = questSection.getInt("chance", 100);
                    if (targetQuestId2 != null && !targetQuestId2.isEmpty()) {
                        rewards.add(new QuestQuestReward(this.plugin, targetQuestId2, questChance));
                    }
                }
            } else if (rewardSection.isString("quest") && (targetQuestId = rewardSection.getString("quest")) != null && !targetQuestId.isEmpty()) {
                rewards.add(new QuestQuestReward(this.plugin, targetQuestId, 100));
            }
        }
        return rewards;
    }

    private List<RewardEntry> loadRewardEntriesForQuest(ConfigurationSection rewardSection) {
        Map<String, Object> data;
        List<?> itemsList;
        ArrayList<RewardEntry> rewardEntries = new ArrayList<RewardEntry>();
        if (rewardSection == null) {
            return rewardEntries;
        }
        if (rewardSection.contains("xp")) {
            int xp = rewardSection.getInt("xp");
            Map<String, Object> data2 = Map.of("amount", xp);
            rewardEntries.add(new RewardEntry(RewardEntry.RewardType.XP, data2));
        }
        if (rewardSection.contains("money")) {
            double money = rewardSection.getDouble("money");
            Map<String, Object> data3 = Map.of("amount", money);
            rewardEntries.add(new RewardEntry(RewardEntry.RewardType.MONEY, data3));
        }
        if (rewardSection.contains("sigils")) {
            double amount = rewardSection.getDouble("sigils");
            Map<String, Object> sigilData = Map.of("amount", amount);
            rewardEntries.add(new RewardEntry(RewardEntry.RewardType.SIGILS, sigilData));
        }
        if (rewardSection.contains("items") && (itemsList = rewardSection.getList("items")) != null) {
            for (Object obj : itemsList) {
                if (!(obj instanceof Map)) continue;
                @SuppressWarnings("unchecked")
                Map<String, Object> itemMap = (Map<String, Object>) obj;
                String materialString = (String) itemMap.get("material");
                if (materialString == null) continue;
                HashMap<String, Object> data4 = new HashMap<String, Object>(itemMap);
                data4.putIfAbsent("amount", 1);
                rewardEntries.add(new RewardEntry(RewardEntry.RewardType.ITEM, data4));
            }
        }
        if (rewardSection.contains("commands")) {
            List<String> commands = rewardSection.getStringList("commands");
            for (String command : commands) {
                data = Map.<String, Object>of("command", command);
                rewardEntries.add(new RewardEntry(RewardEntry.RewardType.COMMAND, data));
            }
        }
        if (rewardSection.contains("quest") || rewardSection.isConfigurationSection("quest")) {
            String targetQuestId;
            if (rewardSection.isConfigurationSection("quest")) {
                ConfigurationSection questSection = rewardSection.getConfigurationSection("quest");
                if (questSection != null) {
                    String targetQuestId2 = questSection.getString("quest-id");
                    int questChance = questSection.getInt("chance", 100);
                    if (targetQuestId2 != null && !targetQuestId2.isEmpty()) {
                        data = new HashMap<String, Object>();
                        data.put("quest-id", targetQuestId2);
                        data.put("chance", questChance);
                        rewardEntries.add(new RewardEntry(RewardEntry.RewardType.QUEST, data));
                    }
                }
            } else if (rewardSection.isString("quest") && (targetQuestId = rewardSection.getString("quest")) != null && !targetQuestId.isEmpty()) {
                Map<String, Object> questData = Map.of("quest-id", targetQuestId, "chance", 100);
                rewardEntries.add(new RewardEntry(RewardEntry.RewardType.QUEST, questData));
            }
        }
        return rewardEntries;
    }

    private ItemReward loadItemReward(Map<String, Object> itemMap) {
        String materialString = (String)itemMap.get("material");
        if (materialString == null) {
            return null;
        }
        CustomItemManager customItemManager = this.plugin.getCustomItemManager();
        if (customItemManager != null && customItemManager.isPluginItem(materialString)) {
            ItemStack customItem = customItemManager.parseCustomItem(materialString);
            if (customItem != null) {
                int amount = itemMap.containsKey("amount") ? (Integer)itemMap.get("amount") : 1;
                int chance = itemMap.containsKey("chance") ? (Integer)itemMap.get("chance") : 100;
                return new ItemReward(customItem, amount, chance);
            }
            this.plugin.getLogger().log(Level.WARNING, "Could not parse custom item: {0}", materialString);
            return null;
        }
        try {
            boolean hasCustomProperties;
            Material material = Material.valueOf((String)materialString.toUpperCase());
            int amount = itemMap.containsKey("amount") ? (Integer)itemMap.get("amount") : 1;
            int chance = itemMap.containsKey("chance") ? (Integer)itemMap.get("chance") : 100;
            boolean bl = hasCustomProperties = itemMap.containsKey("name") || itemMap.containsKey("lore") || itemMap.containsKey("enchantments") || itemMap.containsKey("flags") || itemMap.containsKey("unbreakable") || itemMap.containsKey("custom-model-data");
            if (hasCustomProperties) {
                List flagsList;
                List enchantList;
                List lore;
                ItemStackBuilder builder = new ItemStackBuilder(material, amount);
                String name = (String)itemMap.get("name");
                if (name != null) {
                    builder.setName(name);
                }
                if ((lore = (List)itemMap.get("lore")) != null) {
                    builder.setLore(lore);
                }
                if ((enchantList = (List)itemMap.get("enchantments")) != null) {
                    Map<Enchantment, Integer> enchantments = ItemStackBuilder.parseEnchantments(enchantList);
                    builder.addEnchantments(enchantments);
                }
                if ((flagsList = (List)itemMap.get("flags")) != null) {
                    builder.addFlags(ItemStackBuilder.parseFlags(flagsList));
                }
                if (itemMap.containsKey("unbreakable") && itemMap.get("unbreakable") instanceof Boolean) {
                    builder.setUnbreakable((Boolean)itemMap.get("unbreakable"));
                }
                if (itemMap.containsKey("custom-model-data") && itemMap.get("custom-model-data") instanceof Number) {
                    builder.setCustomModelData(((Number)itemMap.get("custom-model-data")).intValue());
                }
                ItemStack customizedItem = builder.build();
                return new ItemReward(customizedItem, chance);
            }
            ItemStack simpleItem = new ItemStack(material, amount);
            return new ItemReward(simpleItem, chance);
        }
        catch (IllegalArgumentException e) {
            this.plugin.getLogger().log(Level.WARNING, "Invalid material: {0}", materialString);
            return null;
        }
    }

    public boolean giveRewards(String questId, Player player) {
        Component message;
        DifficultyManager.Difficulty difficultyConfig;
        List<Reward> rewards = this.questRewards.get(questId);
        if (rewards == null || rewards.isEmpty()) {
            return true;
        }
        double rewardMultiplier = 1.0;
        Quest quest = this.plugin.getQuestManager().getQuest(questId);
        if (quest != null && quest.getDifficulty() != null && (difficultyConfig = this.plugin.getDifficultyManager().getDifficulty(quest.getDifficulty())) != null) {
            rewardMultiplier = difficultyConfig.rewardMultiplier;
        }
        boolean allSuccess = true;
        int rewardsFailed = 0;
        Iterator<Reward> iterator = rewards.iterator();
        while (iterator.hasNext()) {
            boolean given;
            Reward reward;
            Reward scaledReward = reward = iterator.next();
            if (rewardMultiplier != 1.0) {
                scaledReward = this.scaleReward(reward, rewardMultiplier);
            }
            if (given = scaledReward.giveWithChance(player)) continue;
            if (reward.getChance() < 100) {
                ++rewardsFailed;
                continue;
            }
            this.plugin.getLogger().log(Level.WARNING, "Failed to give reward to {0}: {1}", new Object[]{player.getName(), reward.getDescription()});
            allSuccess = false;
        }
        if (rewardsFailed > 0 && (message = this.plugin.getMessageManager().getMessage("reward-chance-failed")) != null) {
            player.sendMessage(message);
        }
        return allSuccess;
    }

    private Reward scaleReward(Reward reward, double multiplier) {
        Objects.requireNonNull(reward);
        if (reward instanceof XPReward xpReward) {
            int scaledAmount = (int)Math.ceil((double)xpReward.getAmount() * multiplier);
            return new XPReward(Math.max(1, scaledAmount), xpReward.getChance());
        }
        if (reward instanceof MoneyReward moneyReward) {
            double scaledAmount = moneyReward.getAmount() * multiplier;
            return new MoneyReward(this.plugin, Math.max(0.01, scaledAmount), moneyReward.getChance());
        }
        if (reward instanceof SigilReward sigilReward) {
            double scaledAmount = sigilReward.getAmount() * multiplier;
            return new SigilReward(this.plugin, Math.max(0.01, scaledAmount), sigilReward.getChance());
        }
        if (reward instanceof ItemReward itemReward) {
            ItemStack originalItem = itemReward.getItem();
            int scaledAmount = (int)Math.ceil((double)originalItem.getAmount() * multiplier);
            ItemStack scaledItem = originalItem.clone();
            scaledItem.setAmount(Math.max(1, scaledAmount));
            return new ItemReward(scaledItem, Math.max(1, scaledAmount), itemReward.getChance());
        }
        return reward;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean addItemReward(String questId, ItemStack item) {
        CustomItemManager customItemManager;
        String namespace;
        List existingItems;
        boolean isGenerated = this.plugin.getQuestManager().isGeneratedQuest(questId);
        String fileName = isGenerated ? "generated.yml" : "quests.yml";
        File questFile = new File(this.plugin.getDataFolder(), fileName);
        if (!questFile.exists()) {
            return false;
        }
        YamlConfiguration questsConfig = YamlConfiguration.loadConfiguration((File)questFile);
        String path = isGenerated ? questId + ".reward.items" : "quests." + questId + ".reward.items";
        ArrayList<Map<String, Object>> items = new ArrayList<>();
        if (questsConfig.contains(path) && (existingItems = questsConfig.getList(path)) != null) {
            for (Object obj : existingItems) {
                if (!(obj instanceof Map)) continue;
                @SuppressWarnings("unchecked")
                Map<String, Object> itemMap = (Map<String, Object>) obj;
                items.add(itemMap);
            }
        }
        boolean isPluginItem = (namespace = (customItemManager = this.plugin.getCustomItemManager()).detectPluginNamespace(item)) != null;
        HashMap<String, Object> newItem = new HashMap<String, Object>();
        if (isPluginItem) {
            String itemId = customItemManager.getPluginItemId(item, namespace);
            if (itemId == null) {
                this.plugin.getLogger().log(Level.WARNING, "Failed to get plugin item ID for namespace: {0}", namespace);
                return false;
            }
            String target = namespace + ":" + itemId;
            newItem.put("material", target);
            newItem.put("amount", item.getAmount());
            this.plugin.debugLog(Level.INFO, "Added plugin item reward: {0}", target);
        } else {
            newItem.put("material", item.getType().name());
            newItem.put("amount", item.getAmount());
            if (item.getItemMeta() != null) {
                if (item.getItemMeta().hasDisplayName()) {
                    newItem.put("name", LegacyComponentSerializer.legacyAmpersand().serialize(item.getItemMeta().displayName()));
                }
                if (item.getItemMeta().hasLore() && item.getItemMeta().lore() != null) {
                    ArrayList<String> lore = new ArrayList<String>();
                    List<Component> itemLore = item.getItemMeta().lore();
                    if (itemLore != null) {
                        for (Component component : itemLore) {
                            lore.add(LegacyComponentSerializer.legacyAmpersand().serialize(component));
                        }
                    }
                    newItem.put("lore", lore);
                }
                if (item.getItemMeta().hasEnchants()) {
                    ArrayList<String> enchants = new ArrayList<>();
                    for (Map.Entry<Enchantment, Integer> entry : item.getItemMeta().getEnchants().entrySet()) {
                        enchants.add(entry.getKey().getKey().getKey().toUpperCase() + ":" + entry.getValue());
                    }
                    newItem.put("enchantments", enchants);
                }
                if (item.getItemMeta().isUnbreakable()) {
                    newItem.put("unbreakable", true);
                }
                if (!item.getItemMeta().getItemFlags().isEmpty()) {
                    ArrayList<String> flags = new ArrayList<String>();
                    for (ItemFlag flag : item.getItemMeta().getItemFlags()) {
                        flags.add(flag.name());
                    }
                    newItem.put("flags", flags);
                }
            }
            this.plugin.debugLog(Level.INFO, "Added vanilla item reward: {0}", item.getType().name());
        }
        items.add(newItem);
        questsConfig.set(path, items);
        try {
            YamlUtil.atomicSave((FileConfiguration)questsConfig, questFile);
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save " + fileName, e);
            return false;
        }
        this.loadRewards();
        return true;
    }

    public boolean removeReward(String questId, int index) {
        List<RewardEntry> rewardEntries = this.getRewardList(questId);
        int arrayIndex = index - 1;
        if (arrayIndex < 0 || arrayIndex >= rewardEntries.size()) {
            return false;
        }
        boolean isGenerated = this.plugin.getQuestManager().isGeneratedQuest(questId);
        String fileName = isGenerated ? "generated.yml" : "quests.yml";
        File questFile = new File(this.plugin.getDataFolder(), fileName);
        if (!questFile.exists()) {
            return false;
        }
        YamlConfiguration questsConfig = YamlConfiguration.loadConfiguration((File)questFile);
        RewardEntry toRemove = rewardEntries.get(arrayIndex);
        String rewardPath = isGenerated ? questId + ".reward" : "quests." + questId + ".reward";
        switch (toRemove.getType()) {
            case XP: {
                questsConfig.set(rewardPath + ".xp", null);
                break;
            }
            case MONEY: {
                questsConfig.set(rewardPath + ".money", null);
                break;
            }
            case SIGILS: {
                questsConfig.set(rewardPath + ".sigils", null);
                break;
            }
            case ITEM: {
                List items = questsConfig.getList(rewardPath + ".items");
                if (items == null) break;
                int itemIndex = 0;
                for (int i = 0; i < arrayIndex; ++i) {
                    if (rewardEntries.get(i).getType() != RewardEntry.RewardType.ITEM) continue;
                    ++itemIndex;
                }
                if (itemIndex >= items.size()) break;
                items.remove(itemIndex);
                questsConfig.set(rewardPath + ".items", (Object)items);
                break;
            }
            case COMMAND: {
                List commands = questsConfig.getStringList(rewardPath + ".commands");
                if (commands.isEmpty()) break;
                int commandIndex = 0;
                for (int i = 0; i < arrayIndex; ++i) {
                    if (rewardEntries.get(i).getType() != RewardEntry.RewardType.COMMAND) continue;
                    ++commandIndex;
                }
                if (commandIndex >= commands.size()) break;
                commands.remove(commandIndex);
                questsConfig.set(rewardPath + ".commands", (Object)commands);
                break;
            }
            case QUEST: {
                questsConfig.set(rewardPath + ".quest", null);
            }
        }
        try {
            YamlUtil.atomicSave((FileConfiguration)questsConfig, questFile);
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save " + fileName, e);
            return false;
        }
        this.loadRewards();
        return true;
    }

    public List<Reward> getRewards(String questId) {
        return this.questRewards.getOrDefault(questId, new ArrayList());
    }

    public List<RewardEntry> getRewardList(String questId) {
        return this.questRewardEntries.getOrDefault(questId, new ArrayList());
    }

    public boolean addXPReward(String questId, int amount) {
        boolean isGenerated = this.plugin.getQuestManager().isGeneratedQuest(questId);
        String fileName = isGenerated ? "generated.yml" : "quests.yml";
        File questFile = new File(this.plugin.getDataFolder(), fileName);
        if (!questFile.exists()) {
            return false;
        }
        YamlConfiguration questsConfig = YamlConfiguration.loadConfiguration((File)questFile);
        String path = isGenerated ? questId + ".reward.xp" : "quests." + questId + ".reward.xp";
        questsConfig.set(path, (Object)amount);
        try {
            YamlUtil.atomicSave((FileConfiguration)questsConfig, questFile);
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save " + fileName, e);
            return false;
        }
        this.loadRewards();
        return true;
    }

    public boolean addMoneyReward(String questId, double amount) {
        boolean isGenerated = this.plugin.getQuestManager().isGeneratedQuest(questId);
        String fileName = isGenerated ? "generated.yml" : "quests.yml";
        File questFile = new File(this.plugin.getDataFolder(), fileName);
        if (!questFile.exists()) {
            return false;
        }
        YamlConfiguration questsConfig = YamlConfiguration.loadConfiguration((File)questFile);
        String path = isGenerated ? questId + ".reward.money" : "quests." + questId + ".reward.money";
        questsConfig.set(path, (Object)amount);
        try {
            YamlUtil.atomicSave((FileConfiguration)questsConfig, questFile);
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save " + fileName, e);
            return false;
        }
        this.loadRewards();
        return true;
    }

    public boolean addSigilReward(String questId, double amount) {
        boolean isGenerated = this.plugin.getQuestManager().isGeneratedQuest(questId);
        String fileName = isGenerated ? "generated.yml" : "quests.yml";
        File questFile = new File(this.plugin.getDataFolder(), fileName);
        if (!questFile.exists()) {
            return false;
        }
        YamlConfiguration questsConfig = YamlConfiguration.loadConfiguration((File)questFile);
        String path = isGenerated ? questId + ".reward.sigils" : "quests." + questId + ".reward.sigils";
        questsConfig.set(path, (Object)amount);
        try {
            YamlUtil.atomicSave((FileConfiguration)questsConfig, questFile);
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save " + fileName, e);
            return false;
        }
        this.loadRewards();
        return true;
    }

    public boolean addCommandReward(String questId, String command) {
        boolean isGenerated = this.plugin.getQuestManager().isGeneratedQuest(questId);
        String fileName = isGenerated ? "generated.yml" : "quests.yml";
        File questFile = new File(this.plugin.getDataFolder(), fileName);
        if (!questFile.exists()) {
            return false;
        }
        YamlConfiguration questsConfig = YamlConfiguration.loadConfiguration((File)questFile);
        String path = isGenerated ? questId + ".reward.commands" : "quests." + questId + ".reward.commands";
        List commands = questsConfig.getStringList(path);
        commands.add(command);
        questsConfig.set(path, (Object)commands);
        try {
            YamlUtil.atomicSave((FileConfiguration)questsConfig, questFile);
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save " + fileName, e);
            return false;
        }
        this.loadRewards();
        return true;
    }

    public boolean addQuestReward(String questId, String targetQuestId, int chance) {
        if (!this.plugin.getQuestManager().questExists(targetQuestId)) {
            this.plugin.getLogger().log(Level.WARNING, "Cannot add quest reward: Target quest {0} does not exist", targetQuestId);
            return false;
        }
        boolean isGenerated = this.plugin.getQuestManager().isGeneratedQuest(questId);
        String fileName = isGenerated ? "generated.yml" : "quests.yml";
        File questFile = new File(this.plugin.getDataFolder(), fileName);
        if (!questFile.exists()) {
            return false;
        }
        YamlConfiguration questsConfig = YamlConfiguration.loadConfiguration((File)questFile);
        String basePath = isGenerated ? questId + ".reward.quest" : "quests." + questId + ".reward.quest";
        questsConfig.set(basePath + ".quest-id", (Object)targetQuestId);
        questsConfig.set(basePath + ".chance", (Object)chance);
        try {
            YamlUtil.atomicSave((FileConfiguration)questsConfig, questFile);
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save " + fileName, e);
            return false;
        }
        this.loadRewards();
        return true;
    }

    public boolean addQuestReward(String questId, String targetQuestId) {
        return this.addQuestReward(questId, targetQuestId, 100);
    }

    public boolean giveRewardsFromSection(ConfigurationSection rewardSection, Player player) {
        if (rewardSection == null) {
            return true;
        }
        List<Reward> rewards = this.loadRewardsForQuest(rewardSection);
        if (rewards.isEmpty()) {
            return true;
        }
        boolean allSuccess = true;
        int rewardsFailed = 0;
        for (Reward reward : rewards) {
            boolean given = reward.giveWithChance(player);
            if (given) {
                continue;
            }
            if (reward.getChance() < 100) {
                ++rewardsFailed;
                continue;
            }
            allSuccess = false;
            this.plugin.getLogger().log(Level.WARNING, "Failed to give section reward to {0}: {1}", new Object[]{player.getName(), reward.getDescription()});
        }
        if (rewardsFailed > 0) {
            Component message = this.plugin.getMessageManager().getMessage("reward-chance-failed");
            if (message != null) {
                player.sendMessage(message);
            }
        }
        return allSuccess;
    }

    public void reload() {
        this.loadRewards();
    }

    public int migrateLegacyRewardsInGeneratedFile() {
        File generatedFile = new File(this.plugin.getDataFolder(), "generated.yml");
        if (!generatedFile.exists()) {
            return 0;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration((File)generatedFile);
        int migratedCount = 0;
        boolean modified = false;
        for (String questId : config.getKeys(false)) {
            ConfigurationSection questSection = config.getConfigurationSection(questId);
            if (questSection == null || !questSection.contains("rewards")) continue;
            if (questSection.contains("reward")) {
                this.plugin.getLogger().log(Level.FINE, "Quest {0} already has ''reward'' key, skipping migration", questId);
                continue;
            }
            Object rewardsNode = questSection.get("rewards");
            if (rewardsNode instanceof List<?> list && !list.isEmpty()) {
                Object first = list.get(0);
                if (!(first instanceof Map<?, ?> rewardMap)) continue;
                for (Map.Entry<?, ?> entry : rewardMap.entrySet()) {
                    config.set(questId + ".reward." + String.valueOf(entry.getKey()), entry.getValue());
                }
                config.set(questId + ".rewards", null);
                ++migratedCount;
                modified = true;
                this.plugin.getLogger().log(Level.INFO, "[Migration] Converted ''rewards'' list to ''reward'' for quest: {0}", questId);
                continue;
            }
            if (!(rewardsNode instanceof ConfigurationSection)) continue;
            ConfigurationSection section = (ConfigurationSection)rewardsNode;
            for (String key : section.getKeys(false)) {
                config.set(questId + ".reward." + key, section.get(key));
            }
            config.set(questId + ".rewards", null);
            ++migratedCount;
            modified = true;
            this.plugin.getLogger().log(Level.INFO, "[Migration] Converted ''rewards'' section to ''reward'' for quest: {0}", questId);
        }
        if (modified) {
            try {
                YamlUtil.atomicSave((FileConfiguration)config, generatedFile);
                this.plugin.getLogger().log(Level.INFO, "[Migration] Successfully migrated {0} quest(s) in generated.yml", migratedCount);
            }
            catch (IOException e) {
                this.plugin.getLogger().log(Level.SEVERE, "[Migration] Failed to save generated.yml after migration", e);
            }
        }
        return migratedCount;
    }
}

