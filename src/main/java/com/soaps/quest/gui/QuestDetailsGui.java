/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.kyori.adventure.text.format.TextDecoration
 *  net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package com.soaps.quest.gui;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.chat.ChatInputManager;
import com.soaps.quest.gui.GuiMenu;
import com.soaps.quest.managers.TierManager;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.utils.ColorUtil;
import com.soaps.quest.utils.GuiConfigManager;
import com.soaps.quest.utils.PlaceholderManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class QuestDetailsGui {
    private final SoapsQuest plugin;
    private final GuiConfigManager configManager;
    private final ChatInputManager chatManager;
    private final String guiKey = "quest-details";
    private final Map<UUID, String> editingQuests;
    private final List<String> pendingReload = new ArrayList<String>();
    private final Map<String, List<String>> questChanges = new HashMap<String, List<String>>();

    public QuestDetailsGui(SoapsQuest plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getGuiManager().getConfigManager();
        this.chatManager = plugin.getGuiManager().getChatInputManager();
        this.editingQuests = new HashMap<UUID, String>();
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
        ItemStack[] items = this.buildInventory(quest);
        String titleTemplate = this.configManager.getTitle("quest-details");
        String titleWithPlaceholders = PlaceholderManager.replaceQuestPlaceholders(titleTemplate, quest);
        Component title = ColorUtil.colorize(titleWithPlaceholders);
        GuiMenu menu = new GuiMenu(this.plugin, title, this.configManager.getSize("quest-details"), (clicker, slot) -> this.handleClick((Player)clicker, (int)slot, quest));
        menu.open(player, items);
        this.plugin.debugLog("Opened quest details editor for " + player.getName() + " (quest: " + quest.getQuestId() + ")");
    }

    private ItemStack[] buildInventory(Quest quest) {
        int saveSlot;
        int backSlot;
        int deleteSlot;
        int editRewardsSlot;
        int editCondSlot;
        int editObjSlot;
        int editMaterialSlot;
        int lockToggleSlot;
        int editTierSlot;
        int editDiffSlot;
        int editTypeSlot;
        int editDescSlot;
        int editDisplaySlot;
        int questInfoSlot;
        ItemStack filler;
        GuiConfigManager.GuiCache cache = this.configManager.getGuiCache("quest-details");
        ItemStack[] items = new ItemStack[cache.getSize()];
        if (cache.isFillEmpty() && (filler = this.configManager.getFillerItem("quest-details")) != null) {
            for (int i = 0; i < cache.getSize(); ++i) {
                items[i] = filler.clone();
            }
        }
        if ((questInfoSlot = this.configManager.getNavigationSlot("quest-details", "quest-info")) >= 0) {
            items[questInfoSlot] = this.createQuestInfoItem(quest);
        }
        if ((editDisplaySlot = this.configManager.getNavigationSlot("quest-details", "edit-display")) >= 0) {
            items[editDisplaySlot] = this.createEditDisplayItem(quest);
        }
        if ((editDescSlot = this.configManager.getNavigationSlot("quest-details", "edit-description")) >= 0) {
            items[editDescSlot] = this.createEditDescriptionItem(quest);
        }
        if ((editTypeSlot = this.configManager.getNavigationSlot("quest-details", "edit-type")) >= 0) {
            items[editTypeSlot] = this.createTypeItem(quest);
        }
        if ((editDiffSlot = this.configManager.getNavigationSlot("quest-details", "edit-difficulty")) >= 0) {
            items[editDiffSlot] = this.createDifficultyItem(quest);
        }
        if ((editTierSlot = this.configManager.getNavigationSlot("quest-details", "edit-tier")) >= 0) {
            items[editTierSlot] = this.createTierItem(quest);
        }
        if ((lockToggleSlot = this.configManager.getNavigationSlot("quest-details", "toggle-lock")) >= 0) {
            items[lockToggleSlot] = this.createLockToggleItem(quest);
        }
        if ((editMaterialSlot = this.configManager.getNavigationSlot("quest-details", "edit-material")) >= 0) {
            items[editMaterialSlot] = this.createEditMaterialItem(quest);
        }
        if ((editObjSlot = this.configManager.getNavigationSlot("quest-details", "edit-objectives")) >= 0) {
            items[editObjSlot] = this.replacePlaceholders(this.configManager.getItem("quest-details", "edit-objectives"), quest);
        }
        if ((editCondSlot = this.configManager.getNavigationSlot("quest-details", "edit-conditions")) >= 0) {
            items[editCondSlot] = this.replacePlaceholders(this.configManager.getItem("quest-details", "edit-conditions"), quest);
        }
        if ((editRewardsSlot = this.configManager.getNavigationSlot("quest-details", "edit-rewards")) >= 0) {
            items[editRewardsSlot] = this.createRewardsItem(quest);
        }
        if ((deleteSlot = this.configManager.getNavigationSlot("quest-details", "delete-quest")) >= 0) {
            items[deleteSlot] = this.configManager.getItem("quest-details", "delete-quest");
        }
        if ((backSlot = this.configManager.getNavigationSlot("quest-details", "back-button")) >= 0) {
            items[backSlot] = this.configManager.getNavigationItem("quest-details", "back-button");
        }
        if ((saveSlot = this.configManager.getNavigationSlot("quest-details", "save-button")) >= 0) {
            items[saveSlot] = this.configManager.getItem("quest-details", "save-button");
        }
        return items;
    }

    private ItemStack createQuestInfoItem(Quest quest) {
        ItemStack item = new ItemStack(quest.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ColorUtil.colorize(quest.getDisplay()));
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(ColorUtil.colorize("&7ID: &f" + quest.getQuestId()));
            lore.add(Component.empty());
            List<String> description = quest.getCustomLore();
            if (description != null && !description.isEmpty()) {
                for (String line : description) {
                    lore.add(ColorUtil.colorize(line));
                }
            } else {
                lore.add(ColorUtil.colorize("&7No description set"));
            }
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createEditDisplayItem(Quest quest) {
        ItemStack baseItem = this.configManager.getItem("quest-details", "edit-display");
        if (baseItem == null) {
            return new ItemStack(Material.NAME_TAG);
        }
        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(ColorUtil.colorize("&7Current: &f" + quest.getDisplay()));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&aClick to change"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createEditDescriptionItem(Quest quest) {
        ItemStack baseItem = this.configManager.getItem("quest-details", "edit-description");
        if (baseItem == null) {
            return new ItemStack(Material.WRITABLE_BOOK);
        }
        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(ColorUtil.colorize("&7Current description:"));
            List<String> description = quest.getCustomLore();
            if (description != null && !description.isEmpty()) {
                for (String line : description) {
                    lore.add(ColorUtil.colorize("&f  " + line));
                }
            } else {
                lore.add(ColorUtil.colorize("&7  (No description)"));
            }
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&aClick to change"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack replacePlaceholders(ItemStack item, Quest quest) {
        List<Component> lore;
        if (item == null || !item.hasItemMeta()) {
            return item;
        }
        ItemStack cloned = item.clone();
        ItemMeta meta = cloned.getItemMeta();
        if (meta == null) {
            return cloned;
        }
        if (meta.hasDisplayName()) {
            Component displayName = meta.displayName();
            String serialized = LegacyComponentSerializer.legacyAmpersand().serialize(displayName);
            String withPlaceholders = PlaceholderManager.replaceQuestPlaceholders(serialized, quest);
            meta.displayName(ColorUtil.colorize(withPlaceholders));
        }
        if ((lore = meta.lore()) != null && !lore.isEmpty()) {
            ArrayList<Component> newLore = new ArrayList<Component>();
            for (Component line : lore) {
                String serialized = LegacyComponentSerializer.legacyAmpersand().serialize(line);
                String withPlaceholders = PlaceholderManager.replaceQuestPlaceholders(serialized, quest);
                newLore.add(ColorUtil.colorize(withPlaceholders));
            }
            meta.lore(newLore);
        }
        cloned.setItemMeta(meta);
        return cloned;
    }

    private ItemStack createTypeItem(Quest quest) {
        ItemStack baseItem = this.configManager.getItem("quest-details", "edit-type");
        if (baseItem == null) {
            return new ItemStack(Material.COMPASS);
        }
        ItemStack item = this.replacePlaceholders(baseItem.clone(), quest);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            QuestFileInfo fileInfo = this.getQuestFileInfo(quest.getQuestId());
            YamlConfiguration config = YamlConfiguration.loadConfiguration((File)fileInfo.file);
            String questType = config.getString(fileInfo.path + ".type", null);
            if (questType == null) {
                int objCount;
                questType = quest.isSequential() ? "sequence" : ((objCount = quest.getObjectives().size()) <= 1 ? "single" : "multi");
            }
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(ColorUtil.colorize("&7Current: &f" + questType));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&7Types:"));
            lore.add(ColorUtil.colorize("&f  single &7- One objective only"));
            lore.add(ColorUtil.colorize("&f  multi &7- Multiple parallel objectives"));
            lore.add(ColorUtil.colorize("&f  sequence &7- Objectives in order"));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&aLeft-click to cycle"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createDifficultyItem(Quest quest) {
        ItemStack baseItem = this.configManager.getItem("quest-details", "edit-difficulty");
        if (baseItem == null) {
            return new ItemStack(Material.IRON_SWORD);
        }
        ItemStack item = this.replacePlaceholders(baseItem.clone(), quest);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String currentDiff = quest.getDifficulty();
            ConfigurationSection difficultiesSection = this.plugin.getConfig().getConfigurationSection("difficulties");
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            double currentMultiplier = 1.0;
            if (difficultiesSection != null && difficultiesSection.contains(currentDiff)) {
                currentMultiplier = difficultiesSection.getDouble(currentDiff + ".multiplier.objective-amount", 1.0);
            }
            lore.add(ColorUtil.colorize("&7Current: &f" + currentDiff));
            lore.add(ColorUtil.colorize("&7Multiplier: &fx" + currentMultiplier));
            lore.add(Component.empty());
            if (difficultiesSection != null) {
                lore.add(ColorUtil.colorize("&7Available difficulties:"));
                ArrayList<String> difficulties = new ArrayList<>(difficultiesSection.getKeys(false));
                Collections.sort(difficulties);
                for (String diffName : difficulties) {
                    double multiplier = difficultiesSection.getDouble(diffName + ".multiplier.objective-amount", 1.0);
                    String display = difficultiesSection.getString(diffName + ".display", diffName);
                    String prefix = diffName.equalsIgnoreCase(currentDiff) ? "&a\u27a4 " : "  ";
                    lore.add(ColorUtil.colorize(prefix + display + " &7(x" + multiplier + ")"));
                }
                lore.add(Component.empty());
            }
            lore.add(ColorUtil.colorize("&e\u26a0 &7Changing difficulty adjusts"));
            lore.add(ColorUtil.colorize("&7objective amounts by multiplier"));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&aLeft-click to cycle difficulties"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createTierItem(Quest quest) {
        ItemStack baseItem = this.configManager.getItem("quest-details", "edit-tier");
        if (baseItem == null) {
            return new ItemStack(Material.DIAMOND);
        }
        ItemStack item = this.replacePlaceholders(baseItem.clone(), quest);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String questId = quest.getQuestId();
            if (questId == null) {
                this.plugin.getLogger().warning("Quest has null ID");
                return item;
            }
            QuestFileInfo fileInfo = this.getQuestFileInfo(questId);
            if (fileInfo == null || fileInfo.file == null || fileInfo.path == null) {
                this.plugin.getLogger().log(Level.WARNING, "Could not find quest file or path for: {0}", questId);
                return item;
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration((File)fileInfo.file);
            String currentTierName = config.getString(fileInfo.path + ".tier");
            currentTierName = currentTierName == null || currentTierName.isEmpty() ? this.plugin.getDefaultTier() : currentTierName.toLowerCase();
            TierManager.Tier currentTier = this.plugin.getTierManager().getTier(currentTierName);
            String displayName = currentTier != null ? currentTier.display : currentTierName;
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&7Current: " + displayName));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&7Available tiers:"));
            Set<String> tierNames = this.plugin.getTierManager().getTierNames();
            ArrayList<String> sortedTiers = new ArrayList<String>(tierNames);
            for (String tierName : sortedTiers) {
                TierManager.Tier tier = this.plugin.getTierManager().getTier(tierName);
                if (tier == null) continue;
                String prefix = tierName.equals(currentTierName) ? "&a\u27a4 " : "  ";
                lore.add(ColorUtil.colorize(prefix + tier.display));
            }
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&aLeft-click to cycle tiers"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createLockToggleItem(Quest quest) {
        ItemStack baseItem = this.configManager.getItem("quest-details", "toggle-lock");
        if (baseItem == null) {
            return new ItemStack(quest.isLockToPlayer() ? Material.LIME_DYE : Material.GRAY_DYE);
        }
        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            boolean locked = quest.isLockToPlayer();
            meta.displayName(ColorUtil.colorize(locked ? "&aLock-to-Player: Enabled" : "&7Lock-to-Player: Disabled"));
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(ColorUtil.colorize("&7Status: " + (locked ? "&aEnabled" : "&cDisabled")));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&7When enabled, quest paper is"));
            lore.add(ColorUtil.colorize("&7bound to specific player"));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&aClick to toggle"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createEditMaterialItem(Quest quest) {
        ItemStack baseItem = this.configManager.getItem("quest-details", "edit-material");
        if (baseItem == null) {
            return new ItemStack(Material.ITEM_FRAME);
        }
        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(ColorUtil.colorize("&7Change the material used for this quest's physical item."));
            lore.add(ColorUtil.colorize("&7Current: &f" + quest.getMaterial().name()));
            lore.add(ColorUtil.colorize("&7Default: PAPER"));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&eClick to enter a new material name."));
            lore.add(ColorUtil.colorize("&7Example: 'BOOK', 'DIAMOND', or 'HAND'"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createRewardsItem(Quest quest) {
        ItemStack baseItem = this.configManager.getItem("quest-details", "edit-rewards");
        if (baseItem == null) {
            return new ItemStack(Material.CHEST);
        }
        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<Component> lore;
            if (meta.hasDisplayName()) {
                Component displayName = meta.displayName();
                String serialized = LegacyComponentSerializer.legacyAmpersand().serialize(displayName);
                String withPlaceholders = PlaceholderManager.replaceQuestPlaceholders(serialized, quest);
                meta.displayName(ColorUtil.colorize(withPlaceholders));
            }
            if ((lore = meta.lore()) != null && !lore.isEmpty()) {
                ArrayList<Component> newLore = new ArrayList<Component>();
                for (Component line : lore) {
                    String serialized = LegacyComponentSerializer.legacyAmpersand().serialize(line);
                    String withPlaceholders = PlaceholderManager.replaceQuestPlaceholders(serialized, quest);
                    newLore.add(ColorUtil.colorize(withPlaceholders));
                }
                meta.lore(newLore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private void handleClick(Player player, int slot, Quest quest) {
        this.plugin.debugLog("QuestDetailsGui click: slot=" + slot + ", quest=" + quest.getQuestId());
        int backSlot = this.configManager.getNavigationSlot("quest-details", "back-button");
        if (slot == backSlot) {
            player.closeInventory();
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.getGuiManager().getQuestEditorGui().open(player));
            return;
        }
        int saveSlot = this.configManager.getNavigationSlot("quest-details", "save-button");
        if (slot == saveSlot) {
            this.saveQuest(player, quest);
            return;
        }
        int editDisplaySlot = this.configManager.getNavigationSlot("quest-details", "edit-display");
        if (slot == editDisplaySlot) {
            this.requestDisplayNameEdit(player, quest);
            return;
        }
        int editDescSlot = this.configManager.getNavigationSlot("quest-details", "edit-description");
        if (slot == editDescSlot) {
            this.requestDescriptionEdit(player, quest);
            return;
        }
        int editTypeSlot = this.configManager.getNavigationSlot("quest-details", "edit-type");
        if (slot == editTypeSlot) {
            this.cycleQuestType(player, quest);
            return;
        }
        int editDiffSlot = this.configManager.getNavigationSlot("quest-details", "edit-difficulty");
        if (slot == editDiffSlot) {
            this.cycleQuestDifficulty(player, quest);
            return;
        }
        int editTierSlot = this.configManager.getNavigationSlot("quest-details", "edit-tier");
        if (slot == editTierSlot) {
            this.cycleQuestTier(player, quest);
            return;
        }
        int lockToggleSlot = this.configManager.getNavigationSlot("quest-details", "toggle-lock");
        if (slot == lockToggleSlot) {
            this.toggleLockToPlayer(player, quest);
            return;
        }
        int editMaterialSlot = this.configManager.getNavigationSlot("quest-details", "edit-material");
        if (slot == editMaterialSlot) {
            this.requestMaterialEdit(player, quest);
            return;
        }
        int editObjSlot = this.configManager.getNavigationSlot("quest-details", "edit-objectives");
        if (slot == editObjSlot) {
            player.sendMessage((Component)Component.text((String)"Opening objective editor...", (TextColor)NamedTextColor.GREEN));
            player.closeInventory();
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.getGuiManager().getObjectiveEditorGui().open(player, quest));
            return;
        }
        int editCondSlot = this.configManager.getNavigationSlot("quest-details", "edit-conditions");
        if (slot == editCondSlot) {
            player.sendMessage((Component)Component.text((String)"Opening condition editor...", (TextColor)NamedTextColor.GREEN));
            player.closeInventory();
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.getGuiManager().getConditionEditorGui().open(player, quest));
            return;
        }
        int editRewardsSlot = this.configManager.getNavigationSlot("quest-details", "edit-rewards");
        if (slot == editRewardsSlot) {
            player.sendMessage((Component)Component.text((String)"Opening reward editor...", (TextColor)NamedTextColor.GREEN));
            player.closeInventory();
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.getGuiManager().getRewardEditorGui().open(player, quest));
            return;
        }
        int deleteSlot = this.configManager.getNavigationSlot("quest-details", "delete-quest");
        if (slot == deleteSlot) {
            this.requestQuestDeletion(player, quest);
        }
    }

    private void requestDisplayNameEdit(Player player, Quest quest) {
        player.closeInventory();
        player.sendMessage((Component)Component.empty());
        player.sendMessage(ColorUtil.colorize("&e&l\u270e Edit Quest Display Name"));
        player.sendMessage(ColorUtil.colorize("&7Current: &f" + quest.getDisplay()));
        player.sendMessage((Component)Component.empty());
        this.chatManager.requestInput(player, "Enter the new display name:", input -> {
            this.updateQuestDisplay(quest, (String)input);
            player.sendMessage(ColorUtil.colorize("&aDisplay name updated to: &f" + input));
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.reloadAndReopen(player, quest));
        });
    }

    private void requestDescriptionEdit(Player player, Quest quest) {
        player.closeInventory();
        player.sendMessage((Component)Component.empty());
        player.sendMessage(ColorUtil.colorize("&e&l\u270e Edit Quest Description"));
        player.sendMessage(ColorUtil.colorize("&7Current description:"));
        List<String> description = quest.getCustomLore();
        if (description != null && !description.isEmpty()) {
            for (String line : description) {
                player.sendMessage(ColorUtil.colorize("&7  " + line));
            }
        } else {
            player.sendMessage(ColorUtil.colorize("&7  (No description)"));
        }
        player.sendMessage((Component)Component.empty());
        player.sendMessage(ColorUtil.colorize("&7Use &f| &7to separate lines."));
        player.sendMessage(ColorUtil.colorize("&7Type 'cancel' to cancel."));
        this.chatManager.requestInput(player, "Enter the new description:", input -> {
            if (input.equalsIgnoreCase("cancel")) {
                player.sendMessage(ColorUtil.colorize("&cCancelled description edit."));
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.reloadAndReopen(player, quest));
                return;
            }
            List<String> lines = List.of(input.split("\\|"));
            player.sendMessage((Component)Component.empty());
            player.sendMessage(ColorUtil.colorize("&e&l\u2501\u2501\u2501\u2501 PREVIEW \u2501\u2501\u2501\u2501"));
            for (String line : lines) {
                player.sendMessage(ColorUtil.colorize(line));
            }
            player.sendMessage(ColorUtil.colorize("&e&l\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501"));
            player.sendMessage((Component)Component.empty());
            this.updateQuestDescription(quest, lines);
            player.sendMessage(ColorUtil.colorize("&aDescription updated!"));
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.reloadAndReopen(player, quest));
        });
    }

    private void cycleQuestType(Player player, Quest quest) {
        String newType;
        QuestFileInfo fileInfo = this.getQuestFileInfo(quest.getQuestId());
        YamlConfiguration config = YamlConfiguration.loadConfiguration((File)fileInfo.file);
        String currentType = config.getString(fileInfo.path + ".type", "single");
        boolean currentSequential = config.getBoolean(fileInfo.path + ".sequential", false);
        if (currentType == null || !config.contains(fileInfo.path + ".type")) {
            int objCount;
            currentType = currentSequential ? "sequence" : ((objCount = quest.getObjectives().size()) <= 1 ? "single" : "multi");
        }
        boolean newSequential = switch (currentType.toLowerCase()) {
            case "single" -> {
                newType = "multi";
                yield false;
            }
            case "multi" -> {
                newType = "sequence";
                yield true;
            }
            default -> {
                newType = "single";
                yield false;
            }
        };
        try {
            config.set(fileInfo.path + ".type", (Object)newType);
            config.set(fileInfo.path + ".sequential", (Object)newSequential);
            config.save(fileInfo.file);
            this.recordChange(quest.getQuestId(), "Quest type: " + newType);
            player.sendMessage(ColorUtil.colorize("&aQuest type changed to: &f" + newType));
            this.reloadAndReopen(player, quest);
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save quest type", e);
            player.sendMessage(ColorUtil.colorize("&cFailed to change quest type!"));
        }
    }

    private void cycleQuestDifficulty(Player player, Quest quest) {
        String currentDiff = quest.getDifficulty();
        ConfigurationSection diffSection = this.plugin.getConfig().getConfigurationSection("difficulties");
        if (diffSection == null) {
            player.sendMessage(ColorUtil.colorize("&cNo difficulties configured!"));
            return;
        }
        ArrayList difficulties = new ArrayList(diffSection.getKeys(false));
        if (difficulties.isEmpty()) {
            player.sendMessage(ColorUtil.colorize("&cNo difficulties configured!"));
            return;
        }
        Collections.sort(difficulties);
        int currentIndex = 0;
        for (int i = 0; i < difficulties.size(); ++i) {
            if (!((String)difficulties.get(i)).equalsIgnoreCase(currentDiff)) continue;
            currentIndex = i;
            break;
        }
        int nextIndex = (currentIndex + 1) % difficulties.size();
        String newDiff = (String)difficulties.get(nextIndex);
        double currentMultiplier = this.plugin.getConfig().getDouble("difficulties." + currentDiff + ".multiplier.objective-amount", 1.0);
        double newMultiplier = this.plugin.getConfig().getDouble("difficulties." + newDiff + ".multiplier.objective-amount", 1.0);
        this.adjustObjectiveAmounts(quest, currentMultiplier, newMultiplier);
        this.updateQuestDifficulty(quest, newDiff);
        player.sendMessage(ColorUtil.colorize("&aQuest difficulty changed to: &f" + newDiff + " &7(x" + newMultiplier + ")"));
        this.reloadAndReopen(player, quest);
    }

    private void adjustObjectiveAmounts(Quest quest, double oldMultiplier, double newMultiplier) {
        try {
            QuestFileInfo fileInfo = this.getQuestFileInfo(quest.getQuestId());
            YamlConfiguration config = YamlConfiguration.loadConfiguration((File)fileInfo.file);
            String objectivesPath = fileInfo.path + ".objectives";
            if (!config.contains(objectivesPath)) {
                return;
            }
            List objectives = config.getList(objectivesPath);
            if (objectives == null || objectives.isEmpty()) {
                return;
            }
            boolean hasChanges = false;
            for (int i = 0; i < objectives.size(); ++i) {
                int newAmount;
                int baseAmount;
                String objPath = objectivesPath + "." + i;
                if (!config.contains(objPath + ".amount")) continue;
                int currentAmount = config.getInt(objPath + ".amount");
                if (config.contains(objPath + ".base-amount")) {
                    baseAmount = config.getInt(objPath + ".base-amount");
                    newAmount = (int)Math.max(1L, Math.round((double)baseAmount * newMultiplier));
                    config.set(objPath + ".amount", (Object)newAmount);
                    hasChanges = true;
                    continue;
                }
                baseAmount = (int)Math.max(1L, Math.round((double)currentAmount / oldMultiplier));
                config.set(objPath + ".base-amount", (Object)baseAmount);
                newAmount = (int)Math.max(1L, Math.round((double)baseAmount * newMultiplier));
                config.set(objPath + ".amount", (Object)newAmount);
                hasChanges = true;
            }
            if (hasChanges) {
                config.save(fileInfo.file);
                this.recordChange(quest.getQuestId(), "Adjusted objective amounts for difficulty multiplier");
            }
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save adjusted objective amounts", e);
        }
        catch (IllegalArgumentException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Invalid objective configuration", e);
        }
    }

    private void cycleQuestTier(Player player, Quest quest) {
        Set<String> tierNames;
        QuestFileInfo fileInfo = this.getQuestFileInfo(quest.getQuestId());
        YamlConfiguration config = YamlConfiguration.loadConfiguration((File)fileInfo.file);
        String currentTierName = config.getString(fileInfo.path + ".tier", this.plugin.getDefaultTier());
        if (currentTierName == null) {
            currentTierName = this.plugin.getDefaultTier();
        }
        if ((tierNames = this.plugin.getTierManager().getTierNames()).isEmpty()) {
            player.sendMessage(ColorUtil.colorize("&cNo tiers configured!"));
            return;
        }
        List<String> tiers = this.plugin.getTierManager().getSortedTierNames();
        int currentIndex = tiers.indexOf(currentTierName.toLowerCase());
        if (currentIndex == -1) {
            currentIndex = 0;
        }
        int nextIndex = (currentIndex + 1) % tiers.size();
        String newTierName = tiers.get(nextIndex);
        TierManager.Tier newTier = this.plugin.getTierManager().getTier(newTierName);
        String displayName = newTier != null ? newTier.display : newTierName;
        this.updateQuestTierByName(quest, newTierName);
        player.sendMessage(ColorUtil.colorize("&aQuest tier changed to: " + displayName));
        this.reloadAndReopen(player, quest);
    }

    private void toggleLockToPlayer(Player player, Quest quest) {
        boolean newValue = !quest.isLockToPlayer();
        this.updateLockToPlayer(quest, newValue);
        player.sendMessage(ColorUtil.colorize("&aLock-to-Player " + (newValue ? "enabled" : "disabled")));
        this.reloadAndReopen(player, quest);
    }

    private void requestMaterialEdit(Player player, Quest quest) {
        player.closeInventory();
        player.sendMessage((Component)Component.empty());
        player.sendMessage(ColorUtil.colorize("&e&l\u270e Edit Quest Material"));
        player.sendMessage(ColorUtil.colorize("&7Current: &f" + quest.getMaterial().name()));
        player.sendMessage((Component)Component.empty());
        player.sendMessage(ColorUtil.colorize("&7Enter a new material name for this quest."));
        player.sendMessage(ColorUtil.colorize("&7Example: &fBOOK&7, &fDIAMOND&7, or type &fHAND&7 to use the item you're holding."));
        player.sendMessage(ColorUtil.colorize("&7Type 'cancel' to abort."));
        player.sendMessage((Component)Component.empty());
        this.chatManager.requestInput(player, "", input -> {
            Material mat;
            String val = input.trim().toUpperCase();
            if (val.equalsIgnoreCase("cancel")) {
                player.sendMessage((Component)Component.empty());
                player.sendMessage(ColorUtil.colorize("&eMaterial change cancelled."));
                player.sendMessage((Component)Component.empty());
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.reloadAndReopen(player, quest));
                return;
            }
            if (val.equalsIgnoreCase("HAND")) {
                ItemStack hand = player.getInventory().getItemInMainHand();
                if (hand.getType().isAir()) {
                    player.sendMessage((Component)Component.empty());
                    player.sendMessage(ColorUtil.colorize("&cYou must hold a valid item in your hand."));
                    player.sendMessage((Component)Component.empty());
                    Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.reloadAndReopen(player, quest));
                    return;
                }
                mat = hand.getType();
            } else {
                mat = Material.matchMaterial((String)val);
                if (mat == null) {
                    player.sendMessage((Component)Component.empty());
                    player.sendMessage(ColorUtil.colorize("&cInvalid material name. Try again."));
                    player.sendMessage((Component)Component.empty());
                    Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.reloadAndReopen(player, quest));
                    return;
                }
            }
            this.updateQuestMaterial(quest, mat);
            player.sendMessage((Component)Component.empty());
            player.sendMessage(ColorUtil.colorize("&aQuest material set to: &f" + mat.name()));
            player.sendMessage((Component)Component.empty());
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.reloadAndReopen(player, quest));
        });
    }

    private void requestQuestDeletion(Player player, Quest quest) {
        player.closeInventory();
        player.sendMessage((Component)Component.empty());
        player.sendMessage(ColorUtil.colorize("&c&l\u26a0 DELETE QUEST"));
        player.sendMessage(ColorUtil.colorize("&7You are about to permanently delete:"));
        player.sendMessage(ColorUtil.colorize("&e  " + quest.getDisplay() + " &7(&f" + quest.getQuestId() + "&7)"));
        player.sendMessage((Component)Component.empty());
        player.sendMessage(ColorUtil.colorize("&c\u26a0 This action cannot be undone!"));
        player.sendMessage((Component)Component.empty());
        player.sendMessage(ColorUtil.colorize("&7Type &fconfirm &7to delete this quest."));
        player.sendMessage(ColorUtil.colorize("&7Type &fcancel &7to abort."));
        player.sendMessage((Component)Component.empty());
        this.chatManager.requestInput(player, "", input -> {
            String text;
            switch (text = input.trim().toLowerCase()) {
                case "confirm": {
                    this.deleteQuest(player, quest);
                    player.sendMessage((Component)Component.empty());
                    player.sendMessage(ColorUtil.colorize("&c\u2713 Quest deleted: &f" + quest.getDisplay()));
                    player.sendMessage((Component)Component.empty());
                    Location loc = player.getLocation();
                    if (loc != null) {
                        player.playSound(loc, Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                    }
                    Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.getGuiManager().getQuestEditorGui().open(player));
                    break;
                }
                case "cancel": {
                    player.sendMessage((Component)Component.empty());
                    player.sendMessage(ColorUtil.colorize("&e\u2713 Quest deletion cancelled."));
                    player.sendMessage((Component)Component.empty());
                    Location loc = player.getLocation();
                    if (loc != null) {
                        player.playSound(loc, Sound.UI_BUTTON_CLICK, 1.0f, 1.2f);
                    }
                    Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.open(player, quest));
                    break;
                }
                default: {
                    player.sendMessage((Component)Component.empty());
                    player.sendMessage(ColorUtil.colorize("&7Invalid input. Quest deletion cancelled."));
                    player.sendMessage((Component)Component.empty());
                    Location loc = player.getLocation();
                    if (loc != null) {
                        player.playSound(loc, Sound.UI_BUTTON_CLICK, 1.0f, 0.8f);
                    }
                    Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.open(player, quest));
                }
            }
        });
    }

    private void updateQuestDisplay(Quest quest, String newDisplay) {
        try {
            QuestFileInfo fileInfo = this.getQuestFileInfo(quest.getQuestId());
            YamlConfiguration config = YamlConfiguration.loadConfiguration((File)fileInfo.file);
            String path = fileInfo.path + ".display";
            config.set(path, (Object)newDisplay);
            config.save(fileInfo.file);
            this.recordChange(quest.getQuestId(), "Display name: " + newDisplay);
            this.plugin.debugLog("Updated display for quest: " + quest.getQuestId());
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save quest display", e);
        }
    }

    private void updateQuestDescription(Quest quest, List<String> newDescription) {
        try {
            QuestFileInfo fileInfo = this.getQuestFileInfo(quest.getQuestId());
            YamlConfiguration config = YamlConfiguration.loadConfiguration((File)fileInfo.file);
            String path = fileInfo.path + ".lore";
            config.set(path, newDescription);
            config.save(fileInfo.file);
            this.recordChange(quest.getQuestId(), "Description updated (" + newDescription.size() + " lines)");
            this.plugin.debugLog("Updated description for quest: " + quest.getQuestId());
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save quest description", e);
        }
    }

    private void updateQuestDifficulty(Quest quest, String newDifficulty) {
        try {
            QuestFileInfo fileInfo = this.getQuestFileInfo(quest.getQuestId());
            YamlConfiguration config = YamlConfiguration.loadConfiguration((File)fileInfo.file);
            String path = fileInfo.path + ".difficulty";
            config.set(path, (Object)newDifficulty);
            config.save(fileInfo.file);
            this.recordChange(quest.getQuestId(), "Difficulty: " + newDifficulty);
            this.plugin.debugLog("Updated difficulty for quest: " + quest.getQuestId());
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save quest difficulty", e);
        }
    }

    private void updateQuestTierByName(Quest quest, String tierName) {
        try {
            QuestFileInfo fileInfo = this.getQuestFileInfo(quest.getQuestId());
            YamlConfiguration config = YamlConfiguration.loadConfiguration((File)fileInfo.file);
            String path = fileInfo.path + ".tier";
            config.set(path, (Object)tierName);
            config.save(fileInfo.file);
            this.recordChange(quest.getQuestId(), "Tier: " + tierName);
            this.plugin.debugLog("Updated tier for quest: " + quest.getQuestId());
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save quest tier", e);
        }
    }

    private void updateLockToPlayer(Quest quest, boolean newValue) {
        try {
            QuestFileInfo fileInfo = this.getQuestFileInfo(quest.getQuestId());
            YamlConfiguration config = YamlConfiguration.loadConfiguration((File)fileInfo.file);
            String path = fileInfo.path + ".lock-to-player";
            config.set(path, (Object)newValue);
            config.save(fileInfo.file);
            this.recordChange(quest.getQuestId(), "Lock to player: " + (newValue ? "enabled" : "disabled"));
            this.plugin.debugLog("Updated lock-to-player for quest: " + quest.getQuestId());
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save lock-to-player", e);
        }
    }

    private void updateQuestMaterial(Quest quest, Material newMaterial) {
        try {
            QuestFileInfo fileInfo = this.getQuestFileInfo(quest.getQuestId());
            YamlConfiguration config = YamlConfiguration.loadConfiguration((File)fileInfo.file);
            String path = fileInfo.path + ".material";
            config.set(path, (Object)newMaterial.name());
            config.save(fileInfo.file);
            this.recordChange(quest.getQuestId(), "Material: " + newMaterial.name());
            this.plugin.debugLog("Updated material for quest: " + quest.getQuestId());
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save quest material", e);
        }
    }

    private void reloadAndReopen(Player player, Quest quest) {
        String questId = quest.getQuestId();
        CompletableFuture.runAsync(() -> {
            try {
                this.plugin.getQuestManager().reload();
                this.pendingReload.remove(questId);
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
                    Quest reloadedQuest = this.plugin.getQuestManager().getQuest(questId);
                    if (reloadedQuest != null) {
                        this.open(player, reloadedQuest);
                    } else {
                        player.sendMessage((Component)Component.text((String)"Error: Quest not found after reload", (TextColor)NamedTextColor.RED));
                        this.plugin.getGuiManager().getQuestEditorGui().open(player);
                    }
                });
            }
            catch (IllegalArgumentException e) {
                this.plugin.getLogger().log(Level.SEVERE, "Failed to reload quest", e);
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
                    player.sendMessage((Component)Component.text((String)"Error reloading quest!", (TextColor)NamedTextColor.RED));
                    this.open(player, quest);
                });
            }
        });
    }

    private void deleteQuest(Player player, Quest quest) {
        boolean success = this.plugin.getQuestManager().removeQuest(quest.getQuestId());
        if (!success) {
            player.sendMessage((Component)Component.text((String)"Error deleting quest!", (TextColor)NamedTextColor.RED));
        }
    }

    private QuestFileInfo getQuestFileInfo(String questId) {
        YamlConfiguration generatedConfig;
        File generatedFile = new File(this.plugin.getDataFolder(), "generated.yml");
        File questsFile = new File(this.plugin.getDataFolder(), "quests.yml");
        if (generatedFile.exists() && (generatedConfig = YamlConfiguration.loadConfiguration((File)generatedFile)).contains(questId)) {
            return new QuestFileInfo(generatedFile, questId);
        }
        return new QuestFileInfo(questsFile, "quests." + questId);
    }

    private void recordChange(String questId, String changeDescription) {
        this.questChanges.computeIfAbsent(questId, k -> new ArrayList()).add(changeDescription);
        this.pendingReload.add(questId);
    }

    private void saveQuest(Player player, Quest quest) {
        player.closeInventory();
        List<String> changes = this.questChanges.get(quest.getQuestId());
        if (changes == null || changes.isEmpty()) {
            player.sendMessage((Component)Component.text((String)"\u2713 No changes made.", (TextColor)NamedTextColor.GRAY));
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.getGuiManager().getQuestEditorGui().open(player));
            return;
        }
        player.sendMessage((Component)Component.text((String)"\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501", (TextColor)NamedTextColor.GOLD));
        player.sendMessage((Component)Component.text((String)"  Quest Changes Summary", (TextColor)NamedTextColor.YELLOW, (TextDecoration[])new TextDecoration[]{TextDecoration.BOLD}));
        player.sendMessage((Component)Component.text((String)"\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501", (TextColor)NamedTextColor.GOLD));
        player.sendMessage((Component)Component.empty());
        for (String change : changes) {
            player.sendMessage(Component.text((String)"  \u2713 ", (TextColor)NamedTextColor.GREEN).append((Component)Component.text((String)change, (TextColor)NamedTextColor.WHITE)));
        }
        player.sendMessage((Component)Component.empty());
        player.sendMessage((Component)Component.text((String)"All changes were saved automatically.", (TextColor)NamedTextColor.GRAY, (TextDecoration[])new TextDecoration[]{TextDecoration.ITALIC}));
        player.sendMessage((Component)Component.text((String)"\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501", (TextColor)NamedTextColor.GOLD));
        this.questChanges.remove(quest.getQuestId());
        this.pendingReload.remove(quest.getQuestId());
        Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.getGuiManager().getQuestEditorGui().open(player));
    }

    public void clearPlayerData(Player player) {
        this.editingQuests.remove(player.getUniqueId());
    }

    public void clearAllData() {
        this.editingQuests.clear();
        this.pendingReload.clear();
        this.questChanges.clear();
    }

    private static class QuestFileInfo {
        final File file;
        final String path;

        QuestFileInfo(File file, String path) {
            this.file = file;
            this.path = path;
        }
    }
}

