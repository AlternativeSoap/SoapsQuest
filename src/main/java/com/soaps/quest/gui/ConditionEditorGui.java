/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.configuration.Configuration
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package com.soaps.quest.gui;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.chat.ChatInputManager;
import com.soaps.quest.conditions.ConditionRegistry;
import com.soaps.quest.conditions.ConditionType;
import com.soaps.quest.conditions.QuestCondition;
import com.soaps.quest.conditions.types.ItemRequirementCondition;
import com.soaps.quest.gui.GuiMenu;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.utils.ColorUtil;
import com.soaps.quest.utils.GuiConfigManager;
import com.soaps.quest.utils.PlaceholderManager;
import java.io.File;
import java.io.IOException;
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
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class ConditionEditorGui {
    private final SoapsQuest plugin;
    private final GuiConfigManager configManager;
    private final ChatInputManager chatManager;
    private final String guiKey = "condition-editor";
    private final Map<UUID, String> editingQuests;
    private final Map<String, List<QuestCondition>> questConditions;

    public ConditionEditorGui(SoapsQuest plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getGuiManager().getConfigManager();
        this.chatManager = plugin.getGuiManager().getChatInputManager();
        this.editingQuests = new HashMap<UUID, String>();
        this.questConditions = new HashMap<String, List<QuestCondition>>();
    }

    public void open(Player player, Quest quest) {
        if (!player.hasPermission("soapsquest.gui.editor")) {
            player.sendMessage((Component)Component.text((String)"You don't have permission to use the quest editor!", (TextColor)NamedTextColor.RED));
            return;
        }
        if (!com.soaps.quest.util.QuestGuiGate.allow(this.plugin, player)) {
            return;
        }
        this.editingQuests.put(player.getUniqueId(), quest.getQuestId());
        this.loadConditions(quest);
        ItemStack[] items = this.buildInventory(quest);
        String titleTemplate = this.configManager.getTitle("condition-editor");
        String titleWithPlaceholders = PlaceholderManager.replaceQuestPlaceholders(titleTemplate, quest);
        Component title = ColorUtil.colorize(titleWithPlaceholders);
        GuiMenu menu = new GuiMenu(this.plugin, title, this.configManager.getSize("condition-editor"), (clicker, slot, clickType) -> this.handleClick(clicker, slot, quest, clickType));
        menu.open(player, items);
        this.plugin.debugLog("Opened condition editor for " + player.getName() + " (quest: " + quest.getQuestId() + ")");
    }

    private void loadConditions(Quest quest) {
        ArrayList<QuestCondition> conditions = new ArrayList<QuestCondition>();
        ConfigurationSection condSection = quest.getConditions();
        if (condSection != null) {
            block28: for (String conditionKey : condSection.getKeys(false)) {
                if (conditionKey.equals("consume-item")) continue;
                try {
                    Object value = condSection.get(conditionKey);
                    Configuration root = condSection.getRoot();
                    if (root == null) {
                        this.plugin.getLogger().log(Level.WARNING, "Cannot get root section for condition: {0}", conditionKey);
                        continue;
                    }
                    ConfigurationSection tempSection = root.createSection("_temp_" + conditionKey);
                    switch (conditionKey) {
                        case "min-level": {
                            tempSection.set("type", (Object)"level");
                            tempSection.set("min-level", value);
                            break;
                        }
                        case "max-level": {
                            tempSection.set("type", (Object)"max-level");
                            tempSection.set("max-level", value);
                            break;
                        }
                        case "cost": {
                            tempSection.set("type", (Object)"money-cost");
                            tempSection.set("cost", value);
                            break;
                        }
                        case "min-money": {
                            tempSection.set("type", (Object)"money");
                            tempSection.set("min-money", value);
                            break;
                        }
                        case "world": {
                            tempSection.set("type", (Object)"world");
                            tempSection.set("world", value);
                            break;
                        }
                        case "gamemode": {
                            tempSection.set("type", (Object)"gamemode");
                            tempSection.set("gamemode", value);
                            break;
                        }
                        case "time": {
                            tempSection.set("type", (Object)"time");
                            tempSection.set("time", value);
                            break;
                        }
                        case "permission": {
                            tempSection.set("type", (Object)"permission");
                            tempSection.set("permission", value);
                            break;
                        }
                        case "item": {
                            tempSection.set("type", (Object)"item");
                            tempSection.set("item", value);
                            if (!condSection.contains("consume-item")) break;
                            tempSection.set("consume-item", condSection.get("consume-item"));
                            break;
                        }
                        case "active-limit": {
                            tempSection.set("type", (Object)"active-limit");
                            tempSection.set("active-limit", value);
                            break;
                        }
                        case "placeholder": {
                            tempSection.set("type", (Object)"placeholder");
                            if (value instanceof ConfigurationSection) {
                                ConfigurationSection placeholderSection = (ConfigurationSection)value;
                                for (String key : placeholderSection.getKeys(false)) {
                                    tempSection.set(key, placeholderSection.get(key));
                                }
                                break;
                            }
                            tempSection.set("expression", value);
                            break;
                        }
                        default: {
                            this.plugin.getLogger().warning(() -> "Unknown condition key: " + conditionKey + " in quest " + quest.getQuestId());
                            continue block28;
                        }
                    }
                    QuestCondition condition = ConditionRegistry.deserialize(tempSection, this.plugin);
                    if (condition != null) {
                        conditions.add(condition);
                        this.plugin.debugLog("Successfully loaded condition: " + condition.getType() + " -> " + condition.getDisplayString());
                    } else {
                        this.plugin.getLogger().log(Level.WARNING, "Failed to deserialize condition ''{0}'' for quest {1} - deserializer returned null", new Object[]{conditionKey, quest.getQuestId()});
                    }
                    root.set("_temp_" + conditionKey, null);
                }
                catch (Exception e) {
                    this.plugin.getLogger().log(Level.WARNING, "Failed to load condition '" + conditionKey + "' for quest " + quest.getQuestId(), e);
                }
            }
        }
        this.questConditions.put(quest.getQuestId(), conditions);
        this.plugin.debugLog("=== Loaded " + conditions.size() + " conditions for quest " + quest.getQuestId() + " ===");
        for (int i = 0; i < conditions.size(); ++i) {
            QuestCondition cond = (QuestCondition)conditions.get(i);
            this.plugin.debugLog("  [" + i + "] Type: " + cond.getType());
            this.plugin.debugLog("      Display: " + cond.getDisplayString());
            this.plugin.debugLog("      Description: " + cond.getDescription());
        }
        this.plugin.debugLog("=== End conditions ===");
    }

    private ItemStack[] buildInventory(Quest quest) {
        int backSlot;
        int addSlot;
        ItemStack filler;
        GuiConfigManager.GuiCache cache = this.configManager.getGuiCache("condition-editor");
        ItemStack[] items = new ItemStack[cache.getSize()];
        if (cache.isFillEmpty() && (filler = this.configManager.getFillerItem("condition-editor")) != null) {
            for (int i = 0; i < cache.getSize(); ++i) {
                items[i] = filler.clone();
            }
        }
        List<Integer> conditionSlots = this.getConditionSlots();
        List conditions = this.questConditions.getOrDefault(quest.getQuestId(), new ArrayList());
        for (int i = 0; i < conditions.size() && i < conditionSlots.size(); ++i) {
            int slot = conditionSlots.get(i);
            QuestCondition condition = (QuestCondition)conditions.get(i);
            items[slot] = this.createConditionItem(condition);
        }
        if (conditions.isEmpty() && !conditionSlots.isEmpty()) {
            int infoSlot = conditionSlots.get(0);
            ItemStack infoItem = new ItemStack(Material.PAPER);
            ItemMeta infoMeta = infoItem.getItemMeta();
            if (infoMeta != null) {
                infoMeta.displayName(ColorUtil.colorize("&7No Conditions"));
                ArrayList<Component> infoLore = new ArrayList<>();
                infoLore.add(ColorUtil.colorize("&7This quest has no conditions yet."));
                infoLore.add(Component.empty());
                infoLore.add(ColorUtil.colorize("&7Click '&aAdd Condition&7' to create one!"));
                infoMeta.lore(infoLore);
                infoItem.setItemMeta(infoMeta);
            }
            items[infoSlot] = infoItem;
        }
        if ((addSlot = this.configManager.getNavigationSlot("condition-editor", "add-condition")) >= 0 && addSlot < items.length) {
            items[addSlot] = this.configManager.getItem("condition-editor", "add-condition");
        }
        if ((backSlot = this.configManager.getNavigationSlot("condition-editor", "back-button")) >= 0 && backSlot < items.length) {
            items[backSlot] = this.configManager.getNavigationItem("condition-editor", "back-button");
        }
        return items;
    }

    private List<Integer> getConditionSlots() {
        ConfigurationSection layoutSection = this.configManager.getConfig().getConfigurationSection("condition-editor.layout");
        if (layoutSection != null && layoutSection.contains("condition-slots")) {
            return layoutSection.getIntegerList("condition-slots");
        }
        return List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34);
    }

    private ItemStack createConditionItem(QuestCondition condition) {
        ConditionType type = ConditionRegistry.getType(condition.getType());
        ItemStack item = type != null ? new ItemStack(type.getIcon()) : new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String displayName = type != null ? type.getDisplayName() : this.formatTypeName(condition.getType());
            meta.displayName(ColorUtil.colorize("&e" + displayName));
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(ColorUtil.colorize("&7Type: &f" + condition.getType()));
            lore.add(Component.empty());
            String displayString = condition.getDisplayString();
            if (displayString != null && !displayString.isEmpty()) {
                if (displayString.contains("\n")) {
                    for (String line : displayString.split("\n")) {
                        lore.add(ColorUtil.colorize(line));
                    }
                } else {
                    lore.add(ColorUtil.colorize(displayString));
                }
            } else {
                lore.add(ColorUtil.colorize("&7" + condition.getDescription()));
            }
            lore.add(Component.empty());
            if (condition instanceof ItemRequirementCondition) {
                ItemRequirementCondition itemCondition = (ItemRequirementCondition)condition;
                String consumeStatus = itemCondition.isConsumable() ? "&cConsumable" : "&aNon-consumable";
                lore.add(ColorUtil.colorize("&7Status: " + consumeStatus));
                lore.add(Component.empty());
                lore.add(ColorUtil.colorize("&eRight-click to toggle consume"));
            }
            lore.add(ColorUtil.colorize("&c\u26a0 Left-click to remove"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private String formatTypeName(String type) {
        if (type == null || type.isEmpty()) {
            return "Unknown";
        }
        String[] words = type.split("[-_]");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; ++i) {
            String word;
            if (i > 0) {
                result.append(" ");
            }
            if ((word = words[i]).isEmpty()) continue;
            result.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() <= 1) continue;
            result.append(word.substring(1).toLowerCase());
        }
        return result.toString();
    }

    private void handleClick(Player player, int slot, Quest quest, ClickType clickType) {
        List conditions;
        int backSlot = this.configManager.getNavigationSlot("condition-editor", "back-button");
        if (slot == backSlot) {
            player.closeInventory();
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.getGuiManager().getQuestDetailsGui().open(player, quest));
            return;
        }
        int addSlot = this.configManager.getNavigationSlot("condition-editor", "add-condition");
        if (slot == addSlot) {
            this.openConditionTypeSelector(player, quest);
            return;
        }
        List<Integer> conditionSlots = this.getConditionSlots();
        if (!conditionSlots.contains(slot)) {
            return;
        }
        int conditionIndex = conditionSlots.indexOf(slot);
        if (conditionIndex >= (conditions = (List)this.questConditions.getOrDefault(quest.getQuestId(), new ArrayList())).size()) {
            return;
        }
        QuestCondition condition = (QuestCondition)conditions.get(conditionIndex);
        if (clickType.isRightClick() && condition instanceof ItemRequirementCondition) {
            ItemRequirementCondition itemCondition = (ItemRequirementCondition)condition;
            boolean newConsumeState = !itemCondition.isConsumable();
            ItemRequirementCondition newCondition = new ItemRequirementCondition(itemCondition.getRequiredItems(), newConsumeState);
            conditions.set(conditionIndex, newCondition);
            this.saveConditions(quest);
            String state = newConsumeState ? "&cConsumable" : "&aNon-consumable";
            player.sendMessage(ColorUtil.colorize("&eToggled item condition to: " + state));
            Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> {
                Quest reloadedQuest = this.plugin.getQuestManager().reloadQuest(quest.getQuestId());
                if (reloadedQuest != null) {
                    this.open(player, reloadedQuest);
                } else {
                    this.open(player, quest);
                }
            }, 2L);
            return;
        }
        if (clickType.isLeftClick()) {
            QuestCondition removed = (QuestCondition)conditions.remove(conditionIndex);
            this.saveConditions(quest);
            player.sendMessage(ColorUtil.colorize("&cRemoved condition: &f" + removed.getDescription()));
            Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> {
                Quest reloadedQuest = this.plugin.getQuestManager().reloadQuest(quest.getQuestId());
                if (reloadedQuest != null) {
                    this.open(player, reloadedQuest);
                } else {
                    this.open(player, quest);
                }
            }, 2L);
        }
    }

    private void openConditionTypeSelector(Player player, Quest quest) {
        player.closeInventory();
        Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
            ItemStack filler;
            String selectorKey = "condition-type-selector";
            GuiConfigManager.GuiCache cache = this.configManager.getGuiCache(selectorKey);
            ItemStack[] items = new ItemStack[cache.getSize()];
            if (cache.isFillEmpty() && (filler = this.configManager.getFillerItem(selectorKey)) != null) {
                for (int i = 0; i < cache.getSize(); ++i) {
                    items[i] = filler.clone();
                }
            }
            List<Integer> typeSlots = this.getTypeSlotsFromLayout(selectorKey);
            ArrayList<ConditionType> types = new ArrayList<ConditionType>(ConditionRegistry.getAllTypes());
            for (int i = 0; i < types.size() && i < typeSlots.size(); ++i) {
                int typeSlot = typeSlots.get(i);
                ConditionType type = (ConditionType)types.get(i);
                items[typeSlot] = this.createTypeItem(type);
            }
            int backSlot = this.configManager.getNavigationSlot(selectorKey, "back-button");
            if (backSlot >= 0 && backSlot < items.length) {
                items[backSlot] = this.configManager.getNavigationItem(selectorKey, "back-button");
            }
            GuiMenu menu = new GuiMenu(this.plugin, cache.getTitle(), cache.getSize(), (clicker, clickedSlot) -> this.handleTypeSelectorClick((Player)clicker, (int)clickedSlot, (List<ConditionType>)types, typeSlots, quest));
            menu.open(player, items);
        });
    }

    private List<Integer> getTypeSlotsFromLayout(String key) {
        ConfigurationSection layoutSection = this.configManager.getConfig().getConfigurationSection(key + ".layout");
        if (layoutSection != null && layoutSection.contains("type-slots")) {
            return layoutSection.getIntegerList("type-slots");
        }
        return List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25);
    }

    private ItemStack createTypeItem(ConditionType type) {
        ItemStack item = new ItemStack(type.getIcon());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ColorUtil.colorize("&e" + type.getDisplayName()));
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(ColorUtil.colorize("&7" + type.getDescription()));
            lore.add(Component.empty());
            if (type.requiresInput()) {
                lore.add(ColorUtil.colorize("&aClick to create"));
                lore.add(ColorUtil.colorize("&7(will prompt for input)"));
            } else {
                lore.add(ColorUtil.colorize("&aClick to add"));
            }
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void handleTypeSelectorClick(Player player, int slot, List<ConditionType> types, List<Integer> typeSlots, Quest quest) {
        int backSlot = this.configManager.getNavigationSlot("condition-type-selector", "back-button");
        if (slot == backSlot) {
            player.closeInventory();
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.open(player, quest));
            return;
        }
        if (!typeSlots.contains(slot)) {
            return;
        }
        int typeIndex = typeSlots.indexOf(slot);
        if (typeIndex >= types.size()) {
            return;
        }
        ConditionType type = types.get(typeIndex);
        if (type.requiresInput()) {
            this.requestConditionInput(player, quest, type);
        } else {
            try {
                QuestCondition condition = type.createCondition(null);
                this.addCondition(quest, condition);
                player.sendMessage(ColorUtil.colorize("&aAdded condition: &f" + type.getDisplayName()));
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
                    Quest reloadedQuest = this.plugin.getQuestManager().reloadQuest(quest.getQuestId());
                    this.open(player, reloadedQuest != null ? reloadedQuest : quest);
                });
            }
            catch (IllegalArgumentException e) {
                player.sendMessage((Component)Component.text((String)"Failed to create condition!", (TextColor)NamedTextColor.RED));
                this.plugin.getLogger().log(Level.SEVERE, "Failed to create condition: " + type.getId(), e);
            }
        }
    }

    private void requestConditionInput(Player player, Quest quest, ConditionType type) {
        player.closeInventory();
        player.sendMessage((Component)Component.empty());
        player.sendMessage(ColorUtil.colorize("&e&l\u270e " + type.getDisplayName()));
        player.sendMessage(ColorUtil.colorize("&7" + type.getDescription()));
        player.sendMessage((Component)Component.empty());
        this.chatManager.requestInput(player, type.getInputPrompt(), input -> {
            try {
                QuestCondition condition = type.createCondition((String)input);
                this.addCondition(quest, condition);
                player.sendMessage(ColorUtil.colorize("&aAdded condition: &f" + type.getDisplayName()));
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
                    Quest reloadedQuest = this.plugin.getQuestManager().reloadQuest(quest.getQuestId());
                    this.open(player, reloadedQuest != null ? reloadedQuest : quest);
                });
            }
            catch (NumberFormatException e) {
                player.sendMessage((Component)Component.text((String)"Invalid input! Please enter a valid number.", (TextColor)NamedTextColor.RED));
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.open(player, quest));
            }
            catch (IllegalArgumentException e) {
                player.sendMessage((Component)Component.text((String)"Failed to create condition!", (TextColor)NamedTextColor.RED));
                this.plugin.getLogger().log(Level.SEVERE, "Failed to create condition: " + type.getId(), e);
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.open(player, quest));
            }
        });
    }

    private void addCondition(Quest quest, QuestCondition condition) {
        List conditions = this.questConditions.computeIfAbsent(quest.getQuestId(), k -> new ArrayList());
        conditions.add(condition);
        this.saveConditions(quest);
    }

    private void saveConditions(Quest quest) {
        try {
            boolean isGenerated = this.plugin.getQuestManager().isGeneratedQuest(quest.getQuestId());
            String fileName = isGenerated ? "generated.yml" : "quests.yml";
            File questFile = new File(this.plugin.getDataFolder(), fileName);
            YamlConfiguration config = YamlConfiguration.loadConfiguration((File)questFile);
            String basePath = isGenerated ? quest.getQuestId() + ".conditions" : "quests." + quest.getQuestId() + ".conditions";
            config.set(basePath, null);
            List<QuestCondition> conditions = this.questConditions.get(quest.getQuestId());
            if (conditions != null && !conditions.isEmpty()) {
                ConfigurationSection condSection = config.createSection(basePath);
                ArrayList<ItemRequirementCondition> itemConditions = new ArrayList<ItemRequirementCondition>();
                for (QuestCondition condition : conditions) {
                    if (condition instanceof ItemRequirementCondition) {
                        ItemRequirementCondition itemCond = (ItemRequirementCondition)condition;
                        itemConditions.add(itemCond);
                        continue;
                    }
                    if (condition == null) continue;
                    condition.serialize(condSection);
                }
                if (!itemConditions.isEmpty()) {
                    StringBuilder itemStr = new StringBuilder();
                    boolean firstItem = true;
                    boolean itemConsumeState = false;
                    for (ItemRequirementCondition itemCond : itemConditions) {
                        itemConsumeState = itemCond.isConsumable();
                        for (Map.Entry<Material, Integer> entry : itemCond.getRequiredItems().entrySet()) {
                            if (!firstItem) {
                                itemStr.append(",");
                            }
                            itemStr.append(entry.getKey().name()).append(":").append(entry.getValue());
                            firstItem = false;
                        }
                    }
                    condSection.set("item", (Object)itemStr.toString());
                    if (itemConsumeState) {
                        condSection.set("consume-item", (Object)true);
                    }
                }
            }
            config.save(questFile);
            this.plugin.debugLog("Saved " + (conditions != null ? conditions.size() : 0) + " conditions for quest: " + quest.getQuestId() + " to " + fileName);
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save conditions", e);
        }
    }

    public void clearPlayerData(Player player) {
        this.editingQuests.remove(player.getUniqueId());
    }

    public void clearAllData() {
        this.editingQuests.clear();
        this.questConditions.clear();
    }
}

