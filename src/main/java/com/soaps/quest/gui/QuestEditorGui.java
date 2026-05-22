/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package com.soaps.quest.gui;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.gui.GuiMenu;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.utils.ColorUtil;
import com.soaps.quest.utils.GuiConfigManager;
import com.soaps.quest.utils.PlaceholderManager;
import com.soaps.quest.utils.StringUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class QuestEditorGui {
    private final SoapsQuest plugin;
    private final GuiConfigManager configManager;
    private final String guiKey = "quest-editor";
    private final Map<UUID, Integer> playerPages;

    public QuestEditorGui(SoapsQuest plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getGuiManager().getConfigManager();
        this.playerPages = new HashMap<UUID, Integer>();
    }

    public void open(Player player) {
        this.open(player, this.getCurrentPage(player));
    }

    public void open(Player player, int page) {
        if (!player.hasPermission("soapsquest.gui.editor")) {
            player.sendMessage((Component)Component.text((String)"You don't have permission to use the quest editor.", (TextColor)NamedTextColor.RED));
            return;
        }
        if (!com.soaps.quest.util.QuestGuiGate.allow(this.plugin, player)) {
            return;
        }
        ArrayList<Quest> allQuests = new ArrayList<Quest>(this.plugin.getQuestManager().getAllQuests().values());
        if (allQuests.isEmpty()) {
            player.sendMessage((Component)Component.text((String)"No quests available to edit.", (TextColor)NamedTextColor.YELLOW));
            return;
        }
        GuiConfigManager.GuiCache cache = this.configManager.getGuiCache("quest-editor");
        List<Integer> contentSlots = cache.getContentSlots();
        int slotsPerPage = contentSlots.size();
        int totalPages = (int)Math.ceil((double)allQuests.size() / (double)slotsPerPage);
        if (page < 0) {
            page = 0;
        } else if (page >= totalPages) {
            page = totalPages - 1;
        }
        this.playerPages.put(player.getUniqueId(), page);
        ItemStack[] items = this.buildInventory(player, allQuests, page, totalPages, contentSlots, cache);
        int finalPage = page;
        int finalTotalPages = totalPages;
        ArrayList<Quest> finalQuests = allQuests;
        GuiMenu menu = new GuiMenu(this.plugin, cache.getTitle(), cache.getSize(), (clicker, slot) -> this.handleClick((Player)clicker, (int)slot, (List<Quest>)finalQuests, finalPage, finalTotalPages));
        menu.open(player, items);
        this.plugin.debugLog("Opened quest editor for " + player.getName() + " (page " + (page + 1) + "/" + totalPages + ")");
    }

    private ItemStack[] buildInventory(Player player, List<Quest> quests, int page, int totalPages, List<Integer> contentSlots, GuiConfigManager.GuiCache cache) {
        int addQuestSlot;
        ItemStack addQuestItem;
        int backSlot;
        int closeSlot;
        ItemStack filler;
        ItemStack[] items = new ItemStack[cache.getSize()];
        if (cache.isFillEmpty() && (filler = this.configManager.getFillerItem("quest-editor")) != null) {
            for (int i = 0; i < cache.getSize(); ++i) {
                items[i] = filler.clone();
            }
        }
        int startIndex = page * contentSlots.size();
        int endIndex = Math.min(startIndex + contentSlots.size(), quests.size());
        for (int i = startIndex; i < endIndex; ++i) {
            Quest quest = quests.get(i);
            int slotIndex = i - startIndex;
            if (slotIndex >= contentSlots.size()) continue;
            int slot = contentSlots.get(slotIndex);
            items[slot] = this.createQuestEditItem(quest);
        }
        if (totalPages > 1) {
            int nextSlot;
            int prevSlot;
            if (page > 0 && (prevSlot = this.configManager.getNavigationSlot("quest-editor", "prev-page")) >= 0 && prevSlot < items.length) {
                items[prevSlot] = this.configManager.getNavigationItem("quest-editor", "prev-page");
            }
            if (page < totalPages - 1 && (nextSlot = this.configManager.getNavigationSlot("quest-editor", "next-page")) >= 0 && nextSlot < items.length) {
                items[nextSlot] = this.configManager.getNavigationItem("quest-editor", "next-page");
            }
        }
        if ((closeSlot = this.configManager.getNavigationSlot("quest-editor", "close-button")) >= 0 && closeSlot < items.length) {
            items[closeSlot] = this.configManager.getNavigationItem("quest-editor", "close-button");
        }
        if ((backSlot = this.configManager.getNavigationSlot("quest-editor", "back-button")) >= 0 && backSlot < items.length) {
            ItemMeta backMeta;
            ItemStack backItem = this.configManager.getNavigationItem("quest-editor", "back-button");
            if (backItem == null && (backMeta = (backItem = new ItemStack(Material.ARROW)).getItemMeta()) != null) {
                backMeta.displayName(ColorUtil.colorize("&e&l\u2190 Back to Quest Browser"));
                ArrayList<Component> backLore = new ArrayList<Component>();
                backLore.add(ColorUtil.colorize("&7Return to the quest list."));
                backMeta.lore(backLore);
                backItem.setItemMeta(backMeta);
            }
            items[backSlot] = backItem;
        }
        if ((player.hasPermission("soapsquest.admin") || player.hasPermission("soapsquest.quest.create")) && (addQuestItem = this.configManager.getItem("quest-editor", "add-quest")) != null && (addQuestSlot = this.configManager.getNavigationSlot("quest-editor", "add-quest")) >= 0 && addQuestSlot < items.length) {
            items[addQuestSlot] = addQuestItem;
        }
        return items;
    }

    private ItemStack createQuestEditItem(Quest quest) {
        ItemStack item = new ItemStack(quest.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ColorUtil.colorize("&e" + quest.getDisplay()));
            String origin = this.plugin.getQuestManager().isGeneratedQuest(quest.getQuestId()) ? "Generated" : "Manual";
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(ColorUtil.colorize(PlaceholderManager.replaceQuestPlaceholders("&7ID: &f<quest_id>", quest)));
            lore.add(ColorUtil.colorize(PlaceholderManager.replaceQuestPlaceholders("&7Type: &f<quest_type>", quest)));
            lore.add(ColorUtil.colorize(PlaceholderManager.replaceQuestPlaceholders("&7Difficulty: &f<quest_difficulty>", quest)));
            lore.add(ColorUtil.colorize(PlaceholderManager.replaceQuestPlaceholders("&7Tier: &f<quest_tier>", quest)));
            lore.add(ColorUtil.colorize("&7Lock-to-Player: &f" + quest.isLockToPlayer()));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize(PlaceholderManager.replaceQuestPlaceholders("&7Objectives: &f<quest_objective_count>", quest)));
            lore.add(ColorUtil.colorize(PlaceholderManager.replaceQuestPlaceholders("&7Conditions: &f<quest_condition_count>", quest)));
            lore.add(ColorUtil.colorize(PlaceholderManager.replaceQuestPlaceholders("&7Rewards: &f<quest_reward_count>", quest)));
            lore.add(ColorUtil.colorize("&7Origin: &f" + origin));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&aClick to edit this quest"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void handleClick(Player player, int slot, List<Quest> quests, int currentPage, int totalPages) {
        GuiConfigManager.GuiCache cache = this.configManager.getGuiCache("quest-editor");
        List<Integer> contentSlots = cache.getContentSlots();
        int closeSlot = this.configManager.getNavigationSlot("quest-editor", "close-button");
        if (slot == closeSlot) {
            player.closeInventory();
            return;
        }
        int backSlot = this.configManager.getNavigationSlot("quest-editor", "back-button");
        if (slot == backSlot) {
            player.closeInventory();
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.getGuiManager().getQuestBrowserGui().open(player));
            return;
        }
        int addQuestSlot = this.configManager.getNavigationSlot("quest-editor", "add-quest");
        if (slot == addQuestSlot && (player.hasPermission("soapsquest.admin") || player.hasPermission("soapsquest.quest.create"))) {
            this.handleCreateQuest(player, currentPage);
            return;
        }
        int prevSlot = this.configManager.getNavigationSlot("quest-editor", "prev-page");
        if (slot == prevSlot && currentPage > 0) {
            this.open(player, currentPage - 1);
            return;
        }
        int nextSlot = this.configManager.getNavigationSlot("quest-editor", "next-page");
        if (slot == nextSlot && currentPage < totalPages - 1) {
            this.open(player, currentPage + 1);
            return;
        }
        if (!contentSlots.contains(slot)) {
            return;
        }
        int slotIndex = contentSlots.indexOf(slot);
        int questIndex = currentPage * contentSlots.size() + slotIndex;
        if (questIndex >= quests.size()) {
            return;
        }
        Quest quest = quests.get(questIndex);
        player.closeInventory();
        Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.getGuiManager().getQuestDetailsGui().open(player, quest));
    }

    private int getCurrentPage(Player player) {
        return this.playerPages.getOrDefault(player.getUniqueId(), 0);
    }

    public void clearPlayerData(Player player) {
        this.playerPages.remove(player.getUniqueId());
    }

    public void clearAllData() {
        this.playerPages.clear();
    }

    private void handleCreateQuest(Player player, int returnPage) {
        player.closeInventory();
        player.sendMessage(ColorUtil.colorize("&e\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501"));
        player.sendMessage(ColorUtil.colorize("&6&lCreate New Quest"));
        player.sendMessage((Component)Component.empty());
        player.sendMessage(ColorUtil.colorize("&7Enter a unique quest ID (letters, numbers, _ or -):"));
        player.sendMessage(ColorUtil.colorize("&7Type &fcancel &7to abort."));
        player.sendMessage(ColorUtil.colorize("&e\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501"));
        this.plugin.getGuiManager().getChatInputManager().requestInput(player, "&eEnter quest ID:", input -> {
            String questId = StringUtil.sanitizeQuestId(input);
            if (questId.length() < 3 || questId.length() > 48) {
                player.sendMessage(ColorUtil.colorize("&cInvalid quest ID. Must be 3-48 characters long."));
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.open(player, returnPage));
                return;
            }
            if (this.plugin.getQuestManager().questExists(questId)) {
                player.sendMessage(ColorUtil.colorize("&cA quest with ID '&f" + questId + "&c' already exists."));
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.open(player, returnPage));
                return;
            }
            try {
                Quest newQuest = this.plugin.getQuestManager().createAndSaveNewQuest(questId);
                player.sendMessage(ColorUtil.colorize("&aQuest &f" + questId + " &acreated and opened for editing."));
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.getGuiManager().getQuestDetailsGui().open(player, newQuest));
            }
            catch (IllegalArgumentException e) {
                player.sendMessage(ColorUtil.colorize("&cError creating quest: &7" + e.getMessage()));
                this.plugin.getLogger().log(Level.SEVERE, "Error creating quest '" + questId + "'", e);
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.open(player, returnPage));
            }
        }, true);
    }
}

