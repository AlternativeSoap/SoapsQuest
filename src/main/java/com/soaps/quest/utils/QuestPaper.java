/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  org.bukkit.Bukkit
 *  org.bukkit.NamespacedKey
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.persistence.PersistentDataContainer
 *  org.bukkit.persistence.PersistentDataType
 */
package com.soaps.quest.utils;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.managers.DifficultyManager;
import com.soaps.quest.managers.MessageManager;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import com.soaps.quest.utils.PlaceholderManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class QuestPaper {
    private static final String REDEEMED_KEY_SUFFIX = "_redeemed";

    public static ItemStack createQuestPaper(Quest quest, Player player, MessageManager messageManager, NamespacedKey questKey, NamespacedKey playerKey) {
        return QuestPaper.createQuestPaper(quest, player, messageManager, questKey, playerKey, null);
    }

    public static ItemStack createQuestPaper(Quest quest, Player player, MessageManager messageManager, NamespacedKey questKey, NamespacedKey playerKey, List<String> customLore) {
        ItemStack paper = new ItemStack(quest.getMaterial());
        ItemMeta meta = paper.getItemMeta();
        if (meta == null) {
            return paper;
        }
        Component displayName = messageManager.parseColorCodes(quest.getDisplay());
        meta.displayName(displayName);
        List<Component> lore = QuestPaper.buildQuestLore(quest, messageManager, customLore);
        meta.lore(lore);
        UUID questInstanceId = UUID.randomUUID();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(questKey, PersistentDataType.STRING, quest.getQuestId());
        container.set(playerKey, PersistentDataType.STRING, player.getUniqueId().toString());
        NamespacedKey questInstanceKey = new NamespacedKey("soapsquest", "quest_instance_id");
        container.set(questInstanceKey, PersistentDataType.STRING, questInstanceId.toString());
        NamespacedKey timestampKey = new NamespacedKey("soapsquest", "creation_time");
        container.set(timestampKey, PersistentDataType.LONG, System.currentTimeMillis());
        if (quest.getConditions() != null) {
            boolean hasCost = quest.getConditions().contains("cost");
            boolean hasSigilCost = quest.getConditions().contains("sigil-cost");
            boolean hasConsumeItem = quest.getConditions().getBoolean("consume-item", false);
            if (hasCost || hasSigilCost || hasConsumeItem) {
                NamespacedKey lockedKey = new NamespacedKey("soapsquest", "quest_locked");
                container.set(lockedKey, PersistentDataType.BYTE, (byte) 1);
                meta.lore(QuestPaper.buildLockedLore(quest, messageManager));
            }
        }
        paper.setItemMeta(meta);
        return paper;
    }

    public static ItemStack createUnboundQuestPaper(Quest quest, MessageManager messageManager, NamespacedKey questKey, List<String> customLore) {
        ItemStack paper = new ItemStack(quest.getMaterial());
        ItemMeta meta = paper.getItemMeta();
        if (meta == null) {
            return paper;
        }
        Component displayName = messageManager.parseColorCodes(quest.getDisplay());
        meta.displayName(displayName);
        List<Component> lore = QuestPaper.buildQuestLore(quest, messageManager, customLore);
        meta.lore(lore);
        UUID questInstanceId = UUID.randomUUID();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(questKey, PersistentDataType.STRING, quest.getQuestId());
        NamespacedKey questInstanceKey = new NamespacedKey("soapsquest", "quest_instance_id");
        container.set(questInstanceKey, PersistentDataType.STRING, questInstanceId.toString());
        NamespacedKey timestampKey = new NamespacedKey("soapsquest", "creation_time");
        container.set(timestampKey, PersistentDataType.LONG, System.currentTimeMillis());
        if (quest.getConditions() != null) {
            boolean hasCost = quest.getConditions().contains("cost");
            boolean hasSigilCost = quest.getConditions().contains("sigil-cost");
            boolean hasConsumeItem = quest.getConditions().getBoolean("consume-item", false);
            if (hasCost || hasSigilCost || hasConsumeItem) {
                NamespacedKey lockedKey = new NamespacedKey("soapsquest", "quest_locked");
                container.set(lockedKey, PersistentDataType.BYTE, (byte) 1);
                meta.lore(QuestPaper.buildLockedLore(quest, messageManager));
            }
        }
        paper.setItemMeta(meta);
        return paper;
    }

    public static ItemStack updateQuestPaper(ItemStack paper, Quest quest, Player player, MessageManager messageManager) {
        return QuestPaper.updateQuestPaper(paper, quest, player, messageManager, null);
    }

    public static ItemStack updateQuestPaper(ItemStack paper, Quest quest, Player player, MessageManager messageManager, List<String> customLore) {
        if (paper == null) {
            return paper;
        }
        ItemMeta meta = paper.getItemMeta();
        if (meta == null) {
            return paper;
        }
        if (QuestPaper.isLocked(paper)) {
            return paper;
        }
        List<Component> lore = QuestPaper.buildQuestLore(quest, messageManager, customLore);
        meta.lore(lore);
        paper.setItemMeta(meta);
        return paper;
    }

    public static ItemStack updateQuestPaperWithStatus(ItemStack paper, Quest quest, QuestProgress questProgress, MessageManager messageManager, List<String> customLore, boolean isActive) {
        if (paper == null) {
            return paper;
        }
        ItemMeta meta = paper.getItemMeta();
        if (meta == null) {
            return paper;
        }
        if (QuestPaper.isLocked(paper)) {
            return paper;
        }
        boolean isRedeemed = questProgress.isRedeemed();
        List<Component> lore = QuestPaper.buildQuestLoreWithStatus(quest, questProgress, messageManager, customLore, isRedeemed, isActive);
        meta.lore(lore);
        paper.setItemMeta(meta);
        return paper;
    }

    public static boolean isQuestPaper(ItemStack item, NamespacedKey questKey) {
        if (item == null) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(questKey, PersistentDataType.STRING);
    }

    public static String getQuestId(ItemStack item, NamespacedKey questKey) {
        if (!QuestPaper.isQuestPaper(item, questKey)) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return (String)container.get(questKey, PersistentDataType.STRING);
    }

    public static UUID getPlayerUUID(ItemStack item, NamespacedKey playerKey) {
        if (item == null) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String uuidString = (String)container.get(playerKey, PersistentDataType.STRING);
        if (uuidString == null) {
            return null;
        }
        try {
            return UUID.fromString(uuidString);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static UUID getQuestInstanceId(ItemStack item) {
        NamespacedKey questInstanceKey;
        if (item == null) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String uuidString = (String)container.get(questInstanceKey = new NamespacedKey("soapsquest", "quest_instance_id"), PersistentDataType.STRING);
        if (uuidString == null) {
            return null;
        }
        try {
            return UUID.fromString(uuidString);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static boolean isLocked(ItemStack item) {
        if (item == null) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey lockedKey = new NamespacedKey("soapsquest", "quest_locked");
        return container.getOrDefault(lockedKey, PersistentDataType.BYTE, (byte) 0) == (byte) 1;
    }

    public static void setLocked(ItemStack item, boolean locked) {
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey lockedKey = new NamespacedKey("soapsquest", "quest_locked");
        container.set(lockedKey, PersistentDataType.BYTE, locked ? (byte) 1 : (byte) 0);
        item.setItemMeta(meta);
    }

    public static boolean belongsToPlayer(ItemStack item, Player player, NamespacedKey playerKey) {
        UUID paperUUID = QuestPaper.getPlayerUUID(item, playerKey);
        return paperUUID != null && paperUUID.equals(player.getUniqueId());
    }

    public static ItemStack transferQuestPaperOwnership(ItemStack item, Player newPlayer, NamespacedKey playerKey) {
        if (item == null || !item.hasItemMeta()) {
            return item;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(playerKey, PersistentDataType.STRING, newPlayer.getUniqueId().toString());
        item.setItemMeta(meta);
        return item;
    }

    private static List<Component> buildQuestLore(Quest quest, MessageManager messageManager, List<String> customLore) {
        String questType;
        String progressUnit;
        String amountValue;
        ArrayList<Component> lore = new ArrayList<Component>();
        String progressValue = "0";
        if (quest.hasObjectives()) {
            if (quest.isSequential()) {
                amountValue = String.valueOf(quest.getObjectives().size());
                progressUnit = " steps";
                questType = messageManager.getRawMessage("quest-type-sequential");
            } else if (quest.getObjectives().size() == 1) {
                amountValue = String.valueOf(quest.getObjectives().get(0).getRequiredAmount());
                progressUnit = "";
                questType = messageManager.getRawMessage("quest-type-single");
            } else {
                amountValue = String.valueOf(quest.getObjectives().size());
                progressUnit = " objectives";
                questType = messageManager.getRawMessage("quest-type-multi");
            }
        } else {
            amountValue = String.valueOf(quest.getRequiredAmount());
            progressUnit = "";
            questType = messageManager.getRawMessage("quest-type-single");
        }
        if (customLore != null && !customLore.isEmpty()) {
            for (String string : customLore) {
                String formatted = string.replace("<quest>", quest.getDisplay()).replace("<objective>", quest.getObjectiveDescription()).replace("<progress>", progressValue).replace("<amount>", amountValue).replace("<progress_unit>", progressUnit).replace("<type>", questType);
                if (quest.getTier() != null) {
                    String tierName = quest.getTier();
                    String tierDisplay = tierName.substring(0, 1).toUpperCase() + tierName.substring(1).toLowerCase();
                    formatted = formatted.replace("<tier>", tierDisplay).replace("<tier_prefix>", "[" + tierName.toUpperCase() + "] ").replace("<tier_color>", "");
                }
                if (quest.getDifficulty() != null) {
                    formatted = formatted.replace("<difficulty>", QuestPaper.resolveDifficultyDisplay(quest.getDifficulty()));
                }
                lore.add(messageManager.parseColorCodes(formatted));
            }
        } else {
            List<String> defaultTemplate = messageManager.getConfig().getStringList("quest-default-lore");
            if (!defaultTemplate.isEmpty()) {
                for (String line : defaultTemplate) {
                    String formatted = line.replace("<quest>", quest.getDisplay()).replace("<objective>", quest.getObjectiveDescription()).replace("<progress>", progressValue).replace("<amount>", amountValue).replace("<progress_unit>", progressUnit).replace("<type>", questType);
                    if (quest.getTier() != null) {
                        String tierName = quest.getTier();
                        String tierDisplay = tierName.substring(0, 1).toUpperCase() + tierName.substring(1).toLowerCase();
                        formatted = formatted.replace("<tier>", tierDisplay).replace("<tier_prefix>", "[" + tierName.toUpperCase() + "] ").replace("<tier_color>", "");
                    }
                    if (quest.getDifficulty() != null) {
                        formatted = formatted.replace("<difficulty>", QuestPaper.resolveDifficultyDisplay(quest.getDifficulty()));
                    }
                    lore.add(messageManager.parseColorCodes(formatted));
                }
            }
        }
        if (quest.hasObjectives() && quest.getObjectives().size() > 1) {
            lore.add((Component)Component.empty());
            if (quest.isSequential()) {
                lore.add(messageManager.parseColorCodes(messageManager.getRawMessage("objectives-header-sequential")));
                lore.add(messageManager.parseColorCodes(messageManager.getRawMessage("objectives-divider")));
                int stepNum = 1;
                for (Objective obj : quest.getObjectives()) {
                    lore.add(messageManager.parseColorCodes("  &e" + stepNum + "&8. &f" + obj.getDescription()));
                    ++stepNum;
                }
                lore.add(messageManager.parseColorCodes(messageManager.getRawMessage("objectives-divider")));
            } else {
                lore.add(messageManager.parseColorCodes(messageManager.getRawMessage("objectives-header-multi")));
                lore.add(messageManager.parseColorCodes(messageManager.getRawMessage("objectives-divider")));
                for (Objective objective : quest.getObjectives()) {
                    lore.add(messageManager.parseColorCodes("  &b\u2022&f " + objective.getDescription()));
                }
                lore.add(messageManager.parseColorCodes(messageManager.getRawMessage("objectives-divider")));
            }
        }
        return lore;
    }

    private static String resolveDifficultyDisplay(String difficulty) {
        DifficultyManager.Difficulty diff;
        SoapsQuest plugin = PlaceholderManager.getPlugin();
        if (plugin != null && (diff = plugin.getDifficultyManager().getDifficulty(difficulty)) != null) {
            return diff.display;
        }
        return difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1).toLowerCase();
    }

    private static List<Component> buildLockedLore(Quest quest, MessageManager messageManager) {
        ArrayList<Component> lore = new ArrayList<Component>();
        List<String> lockedTemplate = messageManager.getConfig().getStringList("quest-locked-lore");
        StringBuilder requirements = new StringBuilder();
        ConfigurationSection conditions = quest.getConditions();
        if (conditions != null) {
            String permission;
            String time;
            List<String> gamemodes;
            List<String> worlds;
            String itemStr;
            if (conditions.contains("cost")) {
                double cost = conditions.getDouble("cost");
                requirements.append("&e$").append(String.format("%.2f", cost));
            }
            if (conditions.contains("item") && conditions.getBoolean("consume-item", false) && (itemStr = conditions.getString("item")) != null && itemStr.contains(":")) {
                String[] parts = itemStr.split(":");
                if (requirements.length() > 0) {
                    requirements.append("\n");
                }
                requirements.append("&e").append(parts[1]).append("x ").append(parts[0].replace("_", " ").toLowerCase());
            }
            if (conditions.contains("world") && !(worlds = conditions.getStringList("world")).isEmpty()) {
                if (requirements.length() > 0) {
                    requirements.append("\n");
                }
                if (worlds.size() == 1) {
                    requirements.append("&7Must be in: &e").append((String)worlds.get(0));
                } else {
                    requirements.append("&7Must be in: &e").append(String.join((CharSequence)"&7, &e", worlds));
                }
            }
            if (conditions.contains("min-level")) {
                int minLevel = conditions.getInt("min-level");
                if (requirements.length() > 0) {
                    requirements.append("\n");
                }
                requirements.append("&7Min Level: &e").append(minLevel);
            }
            if (conditions.contains("max-level")) {
                int maxLevel = conditions.getInt("max-level");
                if (requirements.length() > 0) {
                    requirements.append("\n");
                }
                requirements.append("&7Max Level: &e").append(maxLevel);
            }
            if (conditions.contains("min-money")) {
                double minMoney = conditions.getDouble("min-money");
                if (requirements.length() > 0) {
                    requirements.append("\n");
                }
                requirements.append("&7Min Balance: &e$").append(String.format("%.2f", minMoney));
            }
            if (conditions.contains("min-sigils")) {
                double minSigils = conditions.getDouble("min-sigils");
                if (requirements.length() > 0) {
                    requirements.append("\n");
                }
                requirements.append("&7Min Sigils: &e").append(String.format("%.2f", minSigils));
            }
            if (conditions.contains("sigil-cost")) {
                double sigilCost = conditions.getDouble("sigil-cost");
                if (requirements.length() > 0) {
                    requirements.append("\n");
                }
                requirements.append("&7Sigil Cost: &e").append(String.format("%.2f", sigilCost));
            }
            if (conditions.contains("gamemode") && !(gamemodes = conditions.getStringList("gamemode")).isEmpty()) {
                if (requirements.length() > 0) {
                    requirements.append("\n");
                }
                if (gamemodes.size() == 1) {
                    requirements.append("&7Gamemode: &e").append((String)gamemodes.get(0));
                } else {
                    requirements.append("&7Gamemodes: &e").append(String.join((CharSequence)"&7, &e", gamemodes));
                }
            }
            if (conditions.contains("time") && (time = conditions.getString("time")) != null) {
                if (requirements.length() > 0) {
                    requirements.append("\n");
                }
                requirements.append("&7Time: &e").append(time);
            }
            if (conditions.contains("permission") && (permission = conditions.getString("permission")) != null) {
                if (requirements.length() > 0) {
                    requirements.append("\n");
                }
                requirements.append("&7Permission: &e").append(permission);
            }
        }
        for (String line : lockedTemplate) {
            String formatted = line.replace("<quest>", quest.getDisplay()).replace("<requirements>", requirements.toString());
            lore.add(messageManager.parseColorCodes(formatted));
        }
        return lore;
    }

    private static List<Component> buildQuestLoreWithStatus(Quest quest, QuestProgress questProgress, MessageManager messageManager, List<String> customLore, boolean isRedeemed, boolean isActive) {
        String questType;
        String progressUnit;
        String amountValue;
        String progressValue;
        UUID ownerUuid;
        ArrayList<Component> lore = new ArrayList<Component>();
        if (questProgress.isClaimable() && !isRedeemed) {
            lore.add(messageManager.parseColorCodes("&7[&2&lClaimable&7]"));
        } else if (isActive) {
            lore.add(messageManager.parseColorCodes("&7[&aActive&7]"));
        } else {
            lore.add(messageManager.parseColorCodes("&7[&6Queued&7]"));
        }
        if (quest.isLockToPlayer() && questProgress.isBound() && (ownerUuid = questProgress.getOwnerUUID()) != null) {
            OfflinePlayer owner = Bukkit.getOfflinePlayer((UUID)ownerUuid);
            String ownerName = owner.getName() != null ? owner.getName() : "Unknown";
            lore.add(messageManager.parseColorCodes("&7Bound to: &e" + ownerName));
        }
        lore.add((Component)Component.empty());
        if (quest.hasObjectives()) {
            if (quest.isSequential()) {
                int currentIndex = questProgress.getCurrentObjectiveIndex();
                int totalCount = quest.getObjectives().size();
                int displayNumber = Math.min(currentIndex + 1, totalCount);
                progressValue = String.valueOf(displayNumber);
                amountValue = String.valueOf(totalCount);
                progressUnit = " objectives";
                questType = "Sequential";
            } else if (quest.getObjectives().size() == 1) {
                Objective objective = quest.getObjectives().get(0);
                int objProgress = questProgress.getObjectiveProgress(objective.getObjectiveId());
                progressValue = String.valueOf(objProgress);
                amountValue = String.valueOf(objective.getRequiredAmount());
                progressUnit = "";
                questType = "Single";
            } else {
                int completedCount = 0;
                int totalCount = quest.getObjectives().size();
                for (Objective objective : quest.getObjectives()) {
                    int objProgress = questProgress.getObjectiveProgress(objective.getObjectiveId());
                    if (objProgress < objective.getRequiredAmount()) continue;
                    ++completedCount;
                }
                progressValue = String.valueOf(completedCount);
                amountValue = String.valueOf(totalCount);
                progressUnit = " objectives";
                questType = "Multi";
            }
        } else {
            progressValue = String.valueOf(questProgress.getCurrentProgress());
            amountValue = String.valueOf(questProgress.getRequiredAmount());
            progressUnit = "";
            questType = "Single";
        }
        if (customLore != null && !customLore.isEmpty()) {
            for (String line : customLore) {
                String formatted = line.replace("<quest>", quest.getDisplay()).replace("<objective>", quest.getObjectiveDescription()).replace("<progress>", progressValue).replace("<amount>", amountValue).replace("<progress_unit>", progressUnit).replace("<type>", questType);
                if (quest.getTier() != null) {
                    String tierName = quest.getTier();
                    String tierDisplay = tierName.substring(0, 1).toUpperCase() + tierName.substring(1).toLowerCase();
                    formatted = formatted.replace("<tier>", tierDisplay).replace("<tier_prefix>", "[" + tierName.toUpperCase() + "] ").replace("<tier_color>", "");
                }
                if (quest.getDifficulty() != null) {
                    formatted = formatted.replace("<difficulty>", QuestPaper.resolveDifficultyDisplay(quest.getDifficulty()));
                }
                lore.add(messageManager.parseColorCodes(formatted));
            }
        } else {
            List<String> defaultTemplate = messageManager.getConfig().getStringList("quest-default-lore");
            if (!defaultTemplate.isEmpty()) {
                for (String line : defaultTemplate) {
                    String formatted = line.replace("<quest>", quest.getDisplay()).replace("<objective>", quest.getObjectiveDescription()).replace("<progress>", progressValue).replace("<amount>", amountValue).replace("<progress_unit>", progressUnit).replace("<type>", questType);
                    if (quest.getTier() != null) {
                        String tierName = quest.getTier();
                        String tierDisplay = tierName.substring(0, 1).toUpperCase() + tierName.substring(1).toLowerCase();
                        formatted = formatted.replace("<tier>", tierDisplay).replace("<tier_prefix>", "[" + tierName.toUpperCase() + "] ").replace("<tier_color>", "");
                    }
                    if (quest.getDifficulty() != null) {
                        formatted = formatted.replace("<difficulty>", QuestPaper.resolveDifficultyDisplay(quest.getDifficulty()));
                    }
                    lore.add(messageManager.parseColorCodes(formatted));
                }
            }
        }
        if (quest.hasObjectives() && quest.getObjectives().size() > 1) {
            lore.add((Component)Component.empty());
            lore.add(messageManager.parseColorCodes("&7Objectives:"));
            List<Objective> objectives = quest.getObjectives();
            int currentIndex = questProgress.getCurrentObjectiveIndex();
            for (int i = 0; i < objectives.size(); ++i) {
                String prefix;
                boolean isCompleted;
                Objective obj = objectives.get(i);
                int objProgress = questProgress.getObjectiveProgress(obj.getObjectiveId());
                int objRequired = obj.getRequiredAmount();
                String objDescription = obj.getDescription();
                isCompleted = objProgress >= objRequired;
                if (isCompleted) {
                    prefix = " &a\u2713 ";
                    objDescription = "&a" + (String)objDescription + " &7(" + objProgress + "/" + objRequired + ")";
                } else if (quest.isSequential()) {
                    if (i == currentIndex) {
                        prefix = " &e> ";
                        objDescription = "&e" + (String)objDescription + " &7(" + objProgress + "/" + objRequired + ")";
                    } else if (i < currentIndex) {
                        prefix = " &a\u2713 ";
                        objDescription = "&a" + (String)objDescription + " &7(" + objProgress + "/" + objRequired + ")";
                    } else {
                        prefix = " &7  ";
                        objDescription = "&7" + (String)objDescription + " &8(Locked)";
                    }
                } else {
                    prefix = " &e> ";
                    objDescription = "&e" + (String)objDescription + " &7(" + objProgress + "/" + objRequired + ")";
                }
                lore.add(messageManager.parseColorCodes(prefix + (String)objDescription));
            }
        }
        if (isRedeemed) {
            lore.add((Component)Component.empty());
            lore.add(messageManager.parseColorCodes("&7[&aRedeemed&7]"));
        } else if (questProgress.isClaimable()) {
            lore.add((Component)Component.empty());
            lore.add(messageManager.parseColorCodes(messageManager.getRawMessage("paper-lore-complete")));
            lore.add(messageManager.parseColorCodes("&e&oRight-click to claim rewards!"));
        }
        return lore;
    }

    public static boolean isRedeemed(ItemStack item, NamespacedKey questKey) {
        if (!QuestPaper.isQuestPaper(item, questKey)) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey redeemedKey = new NamespacedKey(questKey.getNamespace(), questKey.getKey() + REDEEMED_KEY_SUFFIX);
        return container.has(redeemedKey, PersistentDataType.BYTE);
    }

    public static void markAsRedeemed(ItemStack item, NamespacedKey questKey) {
        if (!QuestPaper.isQuestPaper(item, questKey)) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey redeemedKey = new NamespacedKey(questKey.getNamespace(), questKey.getKey() + REDEEMED_KEY_SUFFIX);
        container.set(redeemedKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
    }

    public static void updateQuestPapersInInventory(Player player, Quest quest, MessageManager messageManager, NamespacedKey questIdKey, NamespacedKey playerUuidKey) {
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) {
            return;
        }
        for (ItemStack item : contents) {
            String questId;
            if (item == null || (questId = QuestPaper.getQuestId(item, questIdKey)) == null || !questId.equals(quest.getQuestId()) || !QuestPaper.belongsToPlayer(item, player, playerUuidKey)) continue;
            QuestPaper.updateQuestPaper(item, quest, player, messageManager, quest.getCustomLore());
        }
    }
}

