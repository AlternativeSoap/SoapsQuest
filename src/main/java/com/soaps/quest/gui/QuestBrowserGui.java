/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.kyori.adventure.text.minimessage.MiniMessage
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package com.soaps.quest.gui;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.gui.GuiMenu;
import com.soaps.common.api.gui.GuiHolder;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.utils.ColorUtil;
import com.soaps.quest.utils.GuiConfigManager;
import com.soaps.quest.utils.PlaceholderManager;
import com.soaps.quest.utils.QuestPaper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class QuestBrowserGui
implements Listener {
    private final SoapsQuest plugin;
    private final GuiConfigManager configManager;
    private final String guiKey = "quest-browser";
    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final Map<UUID, Integer> playerPages;
    private final Map<UUID, BrowserFilterState> filterStates;

    public QuestBrowserGui(SoapsQuest plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getGuiManager().getConfigManager();
        this.playerPages = new HashMap<UUID, Integer>();
        this.filterStates = new HashMap<UUID, BrowserFilterState>();
    }

    public void initialize() {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this.plugin);
    }

    public void open(Player player) {
        this.open(player, this.getCurrentPage(player));
    }

    public void open(Player player, int page) {
        int totalPages;
        if (!player.hasPermission("soapsquest.gui.browser")) {
            player.sendMessage((Component)Component.text((String)"You don't have permission to use the quest browser!", (TextColor)NamedTextColor.RED));
            return;
        }
        if (!com.soaps.quest.util.QuestGuiGate.allow(this.plugin, player)) {
            return;
        }
        this.filterStates.putIfAbsent(player.getUniqueId(), new BrowserFilterState());
        BrowserFilterState filterState = this.filterStates.get(player.getUniqueId());
        ArrayList<Quest> allQuests = new ArrayList<Quest>(this.plugin.getQuestManager().getAllQuests().values());
        List<Quest> permissionFiltered = allQuests.stream().filter(quest -> quest.getPermission() == null || player.hasPermission(quest.getPermission())).collect(Collectors.toList());
        List<Quest> availableQuests = this.applyFiltersAndSort(permissionFiltered, filterState);
        GuiConfigManager.GuiCache cache = this.configManager.getGuiCache("quest-browser");
        List<Integer> contentSlots = cache.getContentSlots();
        if (contentSlots == null || contentSlots.isEmpty()) {
            player.sendMessage(MM.deserialize("<red>Error: Quest browser GUI is not properly configured (no content slots defined).</red>"));
            this.plugin.getLogger().warning("Quest browser GUI 'quest-browser' has no content slots defined in gui.yml!");
            return;
        }
        int slotsPerPage = contentSlots.size();
        totalPages = availableQuests.isEmpty() ? 1 : (int)Math.ceil((double)availableQuests.size() / (double)slotsPerPage);
        if (availableQuests.isEmpty()) {
            if (filterState.hasActiveFilters() || !filterState.search.isEmpty()) {
                player.sendMessage(MM.deserialize("<red>No quests found with current filters.</red>"));
                player.sendMessage(MM.deserialize("<gray>Click filters to adjust your search.</gray>"));
            } else {
                player.sendMessage(MM.deserialize("<yellow>No quests available!</yellow>"));
            }
            page = 0;
        } else if (page < 0) {
            page = 0;
        } else if (page >= totalPages) {
            page = totalPages - 1;
        }
        this.playerPages.put(player.getUniqueId(), page);
        String title = this.buildTitle(cache.getTitle(), filterState, availableQuests.size(), permissionFiltered.size());
        ItemStack[] items = this.buildInventory(player, availableQuests, page, totalPages, contentSlots, cache, filterState);
        int finalPage = page;
        int finalTotalPages = totalPages;
        List<Quest> finalQuests = availableQuests;
        GuiMenu menu = new GuiMenu(this.plugin, title, cache.getSize(), (clicker, slot) -> this.handleClick((Player)clicker, (int)slot, finalQuests, finalPage, finalTotalPages), "quest-browser");
        menu.open(player, items);
        this.plugin.debugLog("Opened quest browser for " + player.getName() + " (page " + (page + 1) + "/" + totalPages + ", " + availableQuests.size() + " of " + permissionFiltered.size() + " quests)");
    }

    private List<Quest> applyFiltersAndSort(List<Quest> quests, BrowserFilterState filter) {
        List<Quest> filtered = quests.stream().filter(q -> {
            if (filter.search.isEmpty()) {
                return true;
            }
            String searchLower = filter.search.toLowerCase();
            if (q.getDisplay() != null && q.getDisplay().toLowerCase().contains(searchLower)) {
                return true;
            }
            if (q.getQuestId() != null && q.getQuestId().toLowerCase().contains(searchLower)) {
                return true;
            }
            if (q.getCustomLore() != null) {
                for (String lore : q.getCustomLore()) {
                    if (lore == null || !lore.toLowerCase().contains(searchLower)) continue;
                    return true;
                }
            }
            return false;
        }).filter(q -> filter.tier.equalsIgnoreCase("ALL") || q.getTier() != null && q.getTier().equalsIgnoreCase(filter.tier)).filter(q -> filter.difficulty.equalsIgnoreCase("ALL") || q.getDifficulty() != null && q.getDifficulty().equalsIgnoreCase(filter.difficulty)).filter(q -> {
            if (filter.type.equalsIgnoreCase("ALL")) {
                return true;
            }
            String questType = this.getQuestTypeCategory((Quest)q);
            return questType.equalsIgnoreCase(filter.type);
        }).filter(q -> {
            if (filter.origin.equalsIgnoreCase("ALL")) {
                return true;
            }
            boolean isGenerated = this.plugin.getQuestManager().isGeneratedQuest(q.getQuestId());
            return filter.origin.equalsIgnoreCase("GENERATED") && isGenerated || filter.origin.equalsIgnoreCase("MANUAL") && !isGenerated;
        }).collect(Collectors.toList());
        filtered.sort(this.getSortComparator(filter.sort));
        return filtered;
    }

    private String getQuestTypeCategory(Quest quest) {
        int objectiveCount;
        int n = objectiveCount = quest.getObjectives() != null ? quest.getObjectives().size() : 0;
        if (objectiveCount == 0) {
            return "SINGLE";
        }
        if (objectiveCount <= 1) {
            return "SINGLE";
        }
        if (quest.isSequential()) {
            return "SEQUENCE";
        }
        return "MULTI";
    }

    private Comparator<Quest> getSortComparator(SortMode mode) {
        return switch (mode.ordinal()) {
            case 1 -> (a, b) -> this.safeCompare(a.getTier(), b.getTier());
            case 2 -> (a, b) -> this.safeCompare(a.getDifficulty(), b.getDifficulty());
            case 3 -> (a, b) -> {
                String aType = a.hasObjectives() ? String.valueOf(a.getObjectives().size()) : "0";
                String bType = b.hasObjectives() ? String.valueOf(b.getObjectives().size()) : "0";
                return aType.compareToIgnoreCase(bType);
            };
            default -> (a, b) -> this.safeCompare(a.getDisplay(), b.getDisplay());
        };
    }

    private int safeCompare(String a, String b) {
        if (a == null && b == null) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }
        return a.compareToIgnoreCase(b);
    }

    private String buildTitle(String baseTitle, BrowserFilterState filter, int showing, int total) {
        if (!filter.hasActiveFilters() && filter.sort == SortMode.ALPHA) {
            return baseTitle;
        }
        StringBuilder title = new StringBuilder("Quests");
        ArrayList<String> indicators = new ArrayList<>();
        if (!filter.tier.equals("ALL")) {
            indicators.add("T:" + filter.tier);
        }
        if (!filter.difficulty.equals("ALL")) {
            indicators.add("D:" + filter.difficulty);
        }
        if (!filter.type.equals("ALL")) {
            indicators.add(filter.type);
        }
        if (!filter.search.isEmpty()) {
            indicators.add("Search");
        }
        if (!indicators.isEmpty()) {
            title.append(" [").append(String.join((CharSequence)" ", indicators)).append("]");
        }
        if (filter.sort != SortMode.ALPHA) {
            title.append(" | ").append(filter.sort.getDisplayName());
        }
        title.append(" (").append(showing).append("/").append(total).append(")");
        return title.toString();
    }

    private ItemStack[] buildInventory(Player player, List<Quest> quests, int page, int totalPages, List<Integer> contentSlots, GuiConfigManager.GuiCache cache, BrowserFilterState filterState) {
        int editorSlot;
        int closeSlot;
        ItemStack filler;
        ItemStack[] items = new ItemStack[cache.getSize()];
        if (cache.isFillEmpty() && (filler = this.configManager.getFillerItem("quest-browser")) != null) {
            for (int i = 0; i < cache.getSize(); ++i) {
                items[i] = filler.clone();
            }
        }
        items[0] = this.createSearchButton(filterState);
        items[1] = this.createTierFilterButton(filterState, quests.isEmpty());
        items[2] = this.createDifficultyFilterButton(filterState, quests.isEmpty());
        items[3] = this.createTypeFilterButton(filterState);
        items[4] = this.createOriginFilterButton(filterState);
        items[8] = this.createSortButton(filterState);
        if (!contentSlots.isEmpty() && !quests.isEmpty()) {
            int startIndex = page * contentSlots.size();
            int endIndex = Math.min(startIndex + contentSlots.size(), quests.size());
            for (int i = startIndex; i < endIndex; ++i) {
                Quest quest = quests.get(i);
                int slotIndex = i - startIndex;
                if (slotIndex >= contentSlots.size()) continue;
                int slot = contentSlots.get(slotIndex);
                items[slot] = this.createQuestItem(quest, player);
            }
        }
        if (totalPages > 1) {
            int nextSlot;
            int prevSlot;
            if (page > 0 && (prevSlot = this.configManager.getNavigationSlot("quest-browser", "prev-page")) >= 0 && prevSlot < items.length) {
                items[prevSlot] = this.configManager.getNavigationItem("quest-browser", "prev-page");
            }
            if (page < totalPages - 1 && (nextSlot = this.configManager.getNavigationSlot("quest-browser", "next-page")) >= 0 && nextSlot < items.length) {
                items[nextSlot] = this.configManager.getNavigationItem("quest-browser", "next-page");
            }
        }
        if ((closeSlot = this.configManager.getNavigationSlot("quest-browser", "close-button")) >= 0 && closeSlot < items.length) {
            items[closeSlot] = this.configManager.getNavigationItem("quest-browser", "close-button");
        }
        if ((editorSlot = this.configManager.getNavigationSlot("quest-browser", "editor-button")) >= 0 && editorSlot < items.length && player.hasPermission("soapsquest.gui.editor")) {
            items[editorSlot] = this.configManager.getNavigationItem("quest-browser", "editor-button");
        }
        return items;
    }

    private ItemStack createSearchButton(BrowserFilterState filter) {
        ItemStack item = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ColorUtil.colorize("&e&l\ud83d\udd0d Search Quests"));
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(ColorUtil.colorize("&7Click to search by name or description"));
            lore.add(Component.empty());
            if (!filter.search.isEmpty()) {
                lore.add(ColorUtil.colorize("&aCurrent: &f" + filter.search));
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            } else {
                lore.add(ColorUtil.colorize("&7No search active"));
            }
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&eType &fcancel &eto abort"));
            lore.add(ColorUtil.colorize("&eType &fclear &eto reset search"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createTierFilterButton(BrowserFilterState filter, boolean noQuestsAvailable) {
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ColorUtil.colorize("&e&lTier Filter"));
            String tierDisplay = filter.tier == null || filter.tier.isBlank() ? "ALL" : filter.tier.toUpperCase();
            String safeTierDisplay = MiniMessage.miniMessage().escapeTags(tierDisplay);
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(ColorUtil.colorize("&7Filter quests by tier."));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&7Current:"));
            lore.add(ColorUtil.colorize("&f" + safeTierDisplay));
            if (noQuestsAvailable && !tierDisplay.equals("ALL")) {
                lore.add(Component.empty());
                lore.add(ColorUtil.colorize("&c\u26a0 No quests for this tier"));
            }
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&8Click to cycle through tiers"));
            meta.lore(lore);
            if (!tierDisplay.equals("ALL")) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createDifficultyFilterButton(BrowserFilterState filter, boolean noQuestsAvailable) {
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ColorUtil.colorize("&e&lDifficulty Filter"));
            String difficultyDisplay = filter.difficulty == null || filter.difficulty.isBlank() ? "ALL" : filter.difficulty.toUpperCase();
            String safeDifficultyDisplay = MiniMessage.miniMessage().escapeTags(difficultyDisplay);
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(ColorUtil.colorize("&7Filter quests by difficulty."));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&7Current:"));
            lore.add(ColorUtil.colorize("&f" + safeDifficultyDisplay));
            if (noQuestsAvailable && !difficultyDisplay.equals("ALL")) {
                lore.add(Component.empty());
                lore.add(ColorUtil.colorize("&c\u26a0 No quests for this difficulty"));
            }
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&8Click to cycle difficulties"));
            meta.lore(lore);
            if (!difficultyDisplay.equals("ALL")) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createTypeFilterButton(BrowserFilterState filter) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ColorUtil.colorize("&d&lType Filter"));
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(ColorUtil.colorize("&7Filter quests by type"));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&aCurrent: &f" + filter.type));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&7Options: &fALL, SINGLE, MULTI, SEQUENCE"));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&8SINGLE: &71 objective"));
            lore.add(ColorUtil.colorize("&8MULTI: &7Multiple parallel objectives"));
            lore.add(ColorUtil.colorize("&8SEQUENCE: &7Sequential objectives"));
            meta.lore(lore);
            if (!filter.type.equals("ALL")) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createOriginFilterButton(BrowserFilterState filter) {
        ItemStack item = new ItemStack(Material.MAP);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ColorUtil.colorize("&a&lOrigin Filter"));
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(ColorUtil.colorize("&7Click to cycle origin types"));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&aCurrent: &f" + filter.origin));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&7Options: &fALL, MANUAL, GENERATED"));
            meta.lore(lore);
            if (!filter.origin.equals("ALL")) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createSortButton(BrowserFilterState filter) {
        ItemStack item = new ItemStack(Material.COMPASS);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ColorUtil.colorize("&6&lSort Mode"));
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(ColorUtil.colorize("&7Click to cycle sort modes"));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&aCurrent: &f" + filter.sort.getDisplayName()));
            lore.add(Component.empty());
            ArrayList<String> modes = new ArrayList<String>();
            for (SortMode mode : SortMode.values()) {
                modes.add(mode.getDisplayName());
            }
            lore.add(ColorUtil.colorize("&7Options: &f" + String.join((CharSequence)", ", modes)));
            meta.lore(lore);
            if (filter.sort != SortMode.ALPHA) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createQuestItem(Quest quest, Player player) {
        ItemStack item = this.configManager.getItem("quest-browser", "quest-item");
        if (item == null) {
            item = new ItemStack(quest.getMaterial());
        } else {
            item = item.clone();
            item.setType(quest.getMaterial());
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        String description = this.buildQuestDescription(quest);
        String displayFormat = this.configManager.getString("quest-browser", "quest-item.display", "&e<quest_display>");
        List<String> loreFormat = this.configManager.getStringList("quest-browser", "quest-item.lore");
        String displayName = PlaceholderManager.replaceQuestPlaceholders(displayFormat, quest);
        displayName = displayName.replace("<quest_description>", description);
        meta.displayName(ColorUtil.colorize(displayName));
        ArrayList<Component> lore = new ArrayList<Component>();
        for (String line : loreFormat) {
            String replaced = PlaceholderManager.replaceQuestPlaceholders(line, quest);
            replaced = replaced.replace("<quest_description>", description);
            lore.add(ColorUtil.colorize(replaced));
        }
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private String buildQuestDescription(Quest quest) {
        if (quest.getCustomLore() != null && !quest.getCustomLore().isEmpty()) {
            return quest.getCustomLore().get(0);
        }
        if (quest.getObjectives() != null && !quest.getObjectives().isEmpty()) {
            return quest.getObjectives().get(0).getType() + " quest";
        }
        return "Complete this quest!";
    }

    private void handleClick(Player player, int slot, List<Quest> quests, int currentPage, int totalPages) {
        GuiConfigManager.GuiCache cache = this.configManager.getGuiCache("quest-browser");
        List<Integer> contentSlots = cache.getContentSlots();
        BrowserFilterState filterState = this.filterStates.get(player.getUniqueId());
        switch (slot) {
            case 0: {
                this.handleSearchClick(player);
                return;
            }
            case 1: {
                this.handleTierFilterClick(player, filterState);
                return;
            }
            case 2: {
                this.handleDifficultyFilterClick(player, filterState);
                return;
            }
            case 3: {
                this.handleTypeFilterClick(player, filterState);
                return;
            }
            case 4: {
                this.handleOriginFilterClick(player, filterState);
                return;
            }
            case 8: {
                this.handleSortClick(player, filterState);
                return;
            }
        }
        int closeSlot = this.configManager.getNavigationSlot("quest-browser", "close-button");
        if (slot == closeSlot) {
            player.closeInventory();
            return;
        }
        int editorSlot = this.configManager.getNavigationSlot("quest-browser", "editor-button");
        if (slot == editorSlot) {
            if (!player.hasPermission("soapsquest.gui.editor")) {
                player.sendMessage(ColorUtil.colorize("&cYou don't have permission to open the Quest Editor!"));
                return;
            }
            player.closeInventory();
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.getGuiManager().getQuestEditorGui().open(player));
            return;
        }
        int prevSlot = this.configManager.getNavigationSlot("quest-browser", "prev-page");
        if (slot == prevSlot && currentPage > 0) {
            this.open(player, currentPage - 1);
            return;
        }
        int nextSlot = this.configManager.getNavigationSlot("quest-browser", "next-page");
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
        this.giveQuestPaper(player, quest);
    }

    private void handleSearchClick(Player player) {
        BrowserFilterState filterState = this.filterStates.get(player.getUniqueId());
        player.closeInventory();
        player.sendMessage(ColorUtil.colorize("&7Type your search term in chat."));
        player.sendMessage(ColorUtil.colorize("&7Or type &fcancel&7 to abort, or &fclear&7 to reset search."));
        this.plugin.getGuiManager().getChatInputManager().requestInput(player, "&e\ud83d\udd0d Search Quests\n&7Enter a search term to filter quests by name or description:", input -> {
            String text = input.trim();
            if (text.equalsIgnoreCase("clear")) {
                filterState.setSearch("");
                player.sendMessage(ColorUtil.colorize("&aSearch cleared."));
            } else {
                filterState.setSearch(text);
                player.sendMessage(ColorUtil.colorize("&aSearch set to: &f" + text));
            }
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.open(player));
        });
    }

    private void handleTierFilterClick(Player player, BrowserFilterState filterState) {
        ArrayList<String> tiers = new ArrayList<String>(this.plugin.getTierManager().getTierNames());
        List<String> normalizedTiers = tiers.stream().map(String::toUpperCase).distinct().collect(Collectors.toList());
        normalizedTiers.removeIf(t -> t.equalsIgnoreCase("ALL"));
        normalizedTiers.add(0, "ALL");
        String currentTier = filterState.tier == null || filterState.tier.isBlank() ? "ALL" : filterState.tier.toUpperCase();
        int currentIndex = normalizedTiers.indexOf(currentTier);
        if (currentIndex == -1) {
            currentIndex = 0;
        }
        int nextIndex = (currentIndex + 1) % normalizedTiers.size();
        String newTier = (String)normalizedTiers.get(nextIndex);
        filterState.setTier(newTier);
        player.sendMessage(MM.deserialize("<gray>Tier filter set to <white>" + newTier + "</white></gray>"));
        this.open(player);
    }

    private void handleDifficultyFilterClick(Player player, BrowserFilterState filterState) {
        ArrayList<String> difficulties = new ArrayList<String>(this.plugin.getDifficultyManager().getDifficultyNames());
        List<String> normalizedDifficulties = difficulties.stream().map(String::toUpperCase).distinct().collect(Collectors.toList());
        normalizedDifficulties.removeIf(d -> d.equalsIgnoreCase("ALL"));
        normalizedDifficulties.add(0, "ALL");
        String currentDifficulty = filterState.difficulty == null || filterState.difficulty.isBlank() ? "ALL" : filterState.difficulty.toUpperCase();
        int currentIndex = normalizedDifficulties.indexOf(currentDifficulty);
        if (currentIndex == -1) {
            currentIndex = 0;
        }
        int nextIndex = (currentIndex + 1) % normalizedDifficulties.size();
        String newDifficulty = (String)normalizedDifficulties.get(nextIndex);
        filterState.setDifficulty(newDifficulty);
        player.sendMessage(MM.deserialize("<gray>Difficulty filter set to <white>" + newDifficulty + "</white></gray>"));
        this.open(player);
    }

    private void handleTypeFilterClick(Player player, BrowserFilterState filterState) {
        List<String> types = List.of("ALL", "SINGLE", "MULTI", "SEQUENCE");
        int currentIndex = -1;
        for (int i = 0; i < types.size(); ++i) {
            if (!types.get(i).equalsIgnoreCase(filterState.type)) continue;
            currentIndex = i;
            break;
        }
        if (currentIndex == -1) {
            currentIndex = 0;
        }
        int nextIndex = (currentIndex + 1) % types.size();
        String newType = types.get(nextIndex);
        filterState.setType(newType);
        player.sendMessage(MM.deserialize("<gray>Type filter set to <white>" + newType + "</white></gray>"));
        this.open(player);
    }

    private void handleOriginFilterClick(Player player, BrowserFilterState filterState) {
        List<String> origins = List.of("ALL", "MANUAL", "GENERATED");
        int currentIndex = origins.indexOf(filterState.origin.toUpperCase());
        if (currentIndex == -1) {
            currentIndex = 0;
        }
        int nextIndex = (currentIndex + 1) % origins.size();
        String newOrigin = origins.get(nextIndex);
        filterState.setOrigin(newOrigin);
        player.sendMessage(MM.deserialize("<gray>Origin filter set to <white>" + newOrigin + "</white></gray>"));
        this.open(player);
    }

    private void handleSortClick(Player player, BrowserFilterState filterState) {
        SortMode[] modes = SortMode.values();
        int currentIndex = filterState.sort.ordinal();
        int nextIndex = (currentIndex + 1) % modes.length;
        filterState.setSort(modes[nextIndex]);
        player.sendMessage(MM.deserialize("<gray>Sort mode set to <white>" + modes[nextIndex].getDisplayName() + "</white></gray>"));
        this.open(player);
    }

    private void giveQuestPaper(Player player, Quest quest) {
        ItemStack questPaper = QuestPaper.createQuestPaper(quest, player, this.plugin.getMessageManager(), this.plugin.getQuestIdKey(), this.plugin.getPlayerUuidKey());
        UUID questInstanceUuid = QuestPaper.getQuestInstanceId(questPaper);
        if (questInstanceUuid == null) {
            player.sendMessage((Component)Component.text((String)"Error creating quest paper!", (TextColor)NamedTextColor.RED));
            this.plugin.getLogger().log(Level.SEVERE, "Failed to get quest instance UUID for quest: {0}", quest.getQuestId());
            return;
        }
        if (QuestPaper.isLocked(questPaper)) {
            QuestPaper.setLocked(questPaper, false);
        }
        this.plugin.getDataManager().registerQuestInstance(player, questInstanceUuid, quest.getQuestId(), quest.getRequiredAmount());
        String queueAction = this.plugin.getQuestManager().addQuestToQueue(player, quest.getQuestId(), questInstanceUuid);
        player.getInventory().addItem(new ItemStack[]{questPaper});
        if ("queued".equals(queueAction)) {
            player.sendMessage(this.plugin.getMessageManager().getMessage("quest-received-queued", Map.of("quest", quest.getDisplay())));
        } else {
            player.sendMessage(this.plugin.getMessageManager().getMessage("quest-received", Map.of("quest", quest.getDisplay())));
        }
        this.plugin.getQuestManager().refreshPlayerQueues(player);
        this.plugin.getQuestManager().updateAllQuestPapersForPlayer(player);
        this.plugin.debugLog(player.getName() + " received quest paper for: " + quest.getQuestId() + " (action: " + queueAction + ")");
    }

    private int getCurrentPage(Player player) {
        return this.playerPages.getOrDefault(player.getUniqueId(), 0);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        HumanEntity humanEntity = event.getPlayer();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (!(inventoryHolder instanceof GuiHolder holder) || !"quest-browser".equals(holder.screenId())) {
            return;
        }
        BrowserFilterState filterState = this.filterStates.get(player.getUniqueId());
        if (filterState != null && !filterState.search.isEmpty()) {
            filterState.setSearch("");
            this.plugin.debugLog("Reset search filter for " + player.getName() + " on GUI close");
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (this.playerPages.remove(uuid) != null || this.filterStates.remove(uuid) != null) {
            this.plugin.debugLog("Cleaned up Quest Browser data for " + player.getName() + " on disconnect");
        }
    }

    public void clearPlayerData(Player player) {
        this.playerPages.remove(player.getUniqueId());
        this.filterStates.remove(player.getUniqueId());
    }

    public void clearAllData() {
        this.playerPages.clear();
        this.filterStates.clear();
    }

    public static class BrowserFilterState {
        private String search = "";
        private String tier = "ALL";
        private String difficulty = "ALL";
        private String type = "ALL";
        private String origin = "ALL";
        private SortMode sort = SortMode.ALPHA;

        public String getSearch() {
            return this.search;
        }

        public void setSearch(String search) {
            this.search = search == null ? "" : search;
        }

        public String getTier() {
            return this.tier;
        }

        public void setTier(String tier) {
            this.tier = tier == null || tier.isBlank() ? "ALL" : tier.toUpperCase();
        }

        public String getDifficulty() {
            return this.difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty == null || difficulty.isBlank() ? "ALL" : difficulty.toUpperCase();
        }

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type == null || type.isBlank() ? "ALL" : type.toUpperCase();
        }

        public String getOrigin() {
            return this.origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin == null || origin.isBlank() ? "ALL" : origin.toUpperCase();
        }

        public SortMode getSort() {
            return this.sort;
        }

        public void setSort(SortMode sort) {
            this.sort = sort == null ? SortMode.ALPHA : sort;
        }

        public boolean hasActiveFilters() {
            return !this.search.isEmpty() || !this.tier.equals("ALL") || !this.difficulty.equals("ALL") || !this.type.equals("ALL") || !this.origin.equals("ALL");
        }
    }

    public static enum SortMode {
        ALPHA("Name"),
        TIER("Tier"),
        DIFFICULTY("Difficulty"),
        TYPE("Type");

        private final String displayName;

        private SortMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return this.displayName;
        }
    }
}

