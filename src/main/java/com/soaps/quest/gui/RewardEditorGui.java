/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.TextComponent
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package com.soaps.quest.gui;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.chat.ChatInputManager;
import com.soaps.quest.gui.GuiMenu;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.rewards.RewardEntry;
import com.soaps.quest.rewards.RewardRegistry;
import com.soaps.quest.rewards.RewardType;
import com.soaps.quest.utils.ColorUtil;
import com.soaps.quest.utils.GuiConfigManager;
import com.soaps.quest.utils.PlaceholderManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class RewardEditorGui {
    private final SoapsQuest plugin;
    private final GuiConfigManager configManager;
    private final ChatInputManager chatManager;
    private final String guiKey = "reward-editor";
    private final Map<UUID, String> editingQuests;

    public RewardEditorGui(SoapsQuest plugin) {
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
        String titleTemplate = this.configManager.getTitle("reward-editor");
        String titleWithPlaceholders = PlaceholderManager.replaceQuestPlaceholders(titleTemplate, quest);
        Component title = ColorUtil.colorize(titleWithPlaceholders);
        GuiMenu menu = new GuiMenu(this.plugin, title, this.configManager.getSize("reward-editor"), (clicker, slot, clickType) -> this.handleClickAdvanced(clicker, slot, clickType, quest));
        menu.open(player, items);
        this.plugin.debugLog("Opened reward editor for " + player.getName() + " (quest: " + quest.getQuestId() + ")");
    }

    private ItemStack[] buildInventory(Quest quest) {
        int backSlot;
        int size = this.configManager.getSize("reward-editor");
        ItemStack[] items = new ItemStack[size];
        if (this.configManager.isFillEmpty("reward-editor")) {
            ItemStack filler = this.configManager.getFillerItem("reward-editor");
            for (int i = 0; i < size; ++i) {
                items[i] = filler;
            }
        }
        if ((backSlot = this.configManager.getNavigationSlot("reward-editor", "back-button")) >= 0 && backSlot < items.length) {
            items[backSlot] = this.configManager.getNavigationItem("reward-editor", "back-button");
        }
        if (this.configManager.getConfig().getConfigurationSection("reward-editor.add-reward") != null) {
            int slot = this.configManager.getNavigationSlot("reward-editor", "add-reward");
            if (slot < 0) {
                slot = 49;
            }
            items[slot] = this.createAddRewardButton();
        }
        List<RewardEntry> rewardEntries = this.plugin.getRewardManager().getRewardList(quest.getQuestId());
        List<Integer> contentSlots = this.configManager.getContentSlots("reward-editor");
        for (int i = 0; i < Math.min(rewardEntries.size(), contentSlots.size()); ++i) {
            int slot = contentSlots.get(i);
            items[slot] = this.createRewardItem(rewardEntries.get(i), i + 1);
        }
        return items;
    }

    private ItemStack createAddRewardButton() {
        Material material = this.configManager.getMaterial("reward-editor", "add-reward.material", Material.LIME_DYE);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = this.configManager.getString("reward-editor", "add-reward.name", "&a+ Add Reward");
            meta.displayName(ColorUtil.colorize(name));
            List<String> loreStrings = this.configManager.getStringList("reward-editor", "add-reward.lore");
            ArrayList<Component> lore = new ArrayList<>();
            for (String line : loreStrings) {
                lore.add(ColorUtil.colorize(line));
            }
            lore.add(Component.empty());
            lore.add(Component.text((String)"Hold item + Right-click to add held item", (TextColor)NamedTextColor.AQUA));
            lore.add(Component.text((String)"Left-click to choose reward type", (TextColor)NamedTextColor.GRAY));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createRewardItem(RewardEntry rewardEntry, int index) {
        String displayName;
        Material icon;
        ArrayList<Component> lore = new ArrayList<>();
        switch (rewardEntry.getType()) {
            case XP: {
                icon = Material.EXPERIENCE_BOTTLE;
                int amount = ((Number)rewardEntry.getData().get("amount")).intValue();
                displayName = "&a\u2726 XP Reward";
                lore.add(Component.text((String)("Amount: " + amount + " XP"), (TextColor)NamedTextColor.YELLOW));
                lore.add(Component.empty());
                lore.add(Component.text((String)"Given to player on completion", (TextColor)NamedTextColor.GRAY));
                break;
            }
            case MONEY: {
                icon = Material.GOLD_INGOT;
                double amount = ((Number)rewardEntry.getData().get("amount")).doubleValue();
                displayName = "&e$ Money Reward";
                lore.add(Component.text((String)("Amount: $" + String.format("%.2f", amount)), (TextColor)NamedTextColor.YELLOW));
                lore.add(Component.empty());
                lore.add(Component.text((String)"Requires Vault economy plugin", (TextColor)NamedTextColor.GRAY));
                break;
            }
            case ITEM: {
                String materialStr = (String)rewardEntry.getData().get("material");
                try {
                    icon = Material.valueOf((String)materialStr.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    icon = Material.CHEST;
                }
                int amount = rewardEntry.getData().containsKey("amount") ? ((Number)rewardEntry.getData().get("amount")).intValue() : 1;
                displayName = "&b\u2694 Item Reward";
                lore.add(Component.text((String)("Item: " + materialStr), (TextColor)NamedTextColor.AQUA));
                lore.add(Component.text((String)("Amount: " + amount), (TextColor)NamedTextColor.YELLOW));
                if (rewardEntry.getData().containsKey("name")) {
                    lore.add(ColorUtil.colorize("&7Custom Name: " + String.valueOf(rewardEntry.getData().get("name"))));
                }
                if (rewardEntry.getData().containsKey("enchantments")) {
                    lore.add(Component.text((String)"Has Enchantments", (TextColor)NamedTextColor.LIGHT_PURPLE));
                }
                if (rewardEntry.getData().containsKey("nbt") || rewardEntry.getData().containsKey("data")) {
                    lore.add(Component.text((String)"With NBT Data", (TextColor)NamedTextColor.DARK_GRAY));
                }
                lore.add(Component.empty());
                lore.add(Component.text((String)"Given to player inventory", (TextColor)NamedTextColor.GRAY));
                break;
            }
            case COMMAND: {
                icon = Material.COMMAND_BLOCK;
                String command = (String)rewardEntry.getData().get("command");
                displayName = "&d\u26a1 Command Reward";
                if (command.length() > 50) {
                    lore.add(Component.text((String)("/" + command.substring(0, 47) + "..."), (TextColor)NamedTextColor.LIGHT_PURPLE));
                } else {
                    lore.add(Component.text((String)("/" + command), (TextColor)NamedTextColor.LIGHT_PURPLE));
                }
                lore.add(Component.empty());
                if (command.contains("%player%")) {
                    lore.add(Component.text((String)"Uses player placeholder", (TextColor)NamedTextColor.YELLOW));
                }
                lore.add(Component.text((String)"Executed as console", (TextColor)NamedTextColor.GRAY));
                break;
            }
            case QUEST: {
                icon = Material.WRITABLE_BOOK;
                String targetQuestId = (String)rewardEntry.getData().get("quest-id");
                int chance = rewardEntry.getData().containsKey("chance") ? ((Number)rewardEntry.getData().get("chance")).intValue() : 100;
                displayName = "&b\ud83d\udcdc Quest Reward";
                lore.add(Component.text((String)("Target Quest: " + targetQuestId), (TextColor)NamedTextColor.AQUA));
                if (this.plugin.getQuestManager().questExists(targetQuestId)) {
                    Quest targetQuest = this.plugin.getQuestManager().getQuest(targetQuestId);
                    if (targetQuest != null) {
                        lore.add(ColorUtil.colorize("&7Display: " + targetQuest.getDisplay()));
                    }
                } else {
                    lore.add(Component.text((String)"\u26a0 Quest not found!", (TextColor)NamedTextColor.RED));
                }
                if (chance < 100) {
                    lore.add(Component.text((String)("Chance: " + chance + "%"), (TextColor)NamedTextColor.YELLOW));
                }
                lore.add(Component.empty());
                lore.add(Component.text((String)"Grants quest paper on completion", (TextColor)NamedTextColor.GRAY));
                break;
            }
            default: {
                icon = Material.BARRIER;
                displayName = "&c? Unknown Reward";
            }
        }
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text((String)("#" + index + " "), (TextColor)NamedTextColor.GOLD).append(ColorUtil.colorize(displayName)));
            lore.add(Component.empty());
            lore.add(Component.text((String)"Click to remove", (TextColor)NamedTextColor.RED));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void handleClickAdvanced(Player player, int slot, ClickType clickType, Quest quest) {
        int backSlot = this.configManager.getNavigationSlot("reward-editor", "back-button");
        if (slot == backSlot) {
            player.closeInventory();
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.getGuiManager().getQuestDetailsGui().open(player, quest));
            return;
        }
        int addRewardSlot = this.configManager.getNavigationSlot("reward-editor", "add-reward");
        if (addRewardSlot < 0) {
            addRewardSlot = 49;
        }
        if (slot == addRewardSlot) {
            player.closeInventory();
            if (clickType.isRightClick()) {
                ItemStack heldItem = player.getInventory().getItemInMainHand();
                if (heldItem.getType() != Material.AIR && heldItem.getAmount() > 0) {
                    boolean success = this.plugin.getRewardManager().addItemReward(quest.getQuestId(), heldItem.clone());
                    if (success) {
                        player.sendMessage((Component)Component.text((String)"\u2713 Added held item as reward!", (TextColor)NamedTextColor.GREEN));
                        Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.open(player, quest));
                    } else {
                        player.sendMessage((Component)Component.text((String)"\u2717 Failed to add item reward!", (TextColor)NamedTextColor.RED));
                    }
                } else {
                    player.sendMessage((Component)Component.text((String)"\u2717 You're not holding any item!", (TextColor)NamedTextColor.RED));
                    Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.open(player, quest));
                }
            } else {
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.openRewardTypeSelector(player, quest));
            }
            return;
        }
        List<Integer> contentSlots = this.configManager.getContentSlots("reward-editor");
        if (contentSlots.contains(slot)) {
            int index = contentSlots.indexOf(slot) + 1;
            this.handleRemoveReward(player, quest, index);
        }
    }

    private void openRewardTypeSelector(Player player, Quest quest) {
        int backSlot;
        String selectorKey = "reward-type-selector";
        int size = this.configManager.getSize(selectorKey);
        ItemStack[] items = new ItemStack[size];
        if (this.configManager.isFillEmpty(selectorKey)) {
            ItemStack filler = this.configManager.getFillerItem(selectorKey);
            for (int i = 0; i < size; ++i) {
                items[i] = filler;
            }
        }
        if ((backSlot = this.configManager.getNavigationSlot(selectorKey, "back-button")) >= 0 && backSlot < items.length) {
            items[backSlot] = this.configManager.getNavigationItem(selectorKey, "back-button");
        }
        List<Integer> typeSlots = this.configManager.getContentSlots(selectorKey);
        ArrayList<RewardType> types = new ArrayList<RewardType>(RewardRegistry.getAllTypes());
        for (int i = 0; i < Math.min(types.size(), typeSlots.size()); ++i) {
            RewardType type = (RewardType)types.get(i);
            int typeslot = typeSlots.get(i);
            items[typeslot] = this.createRewardTypeItem(type);
        }
        String title = this.configManager.getTitle(selectorKey);
        GuiMenu menu = new GuiMenu(this.plugin, title, size, (clicker, clickedSlot) -> {
            int index;
            int backButtonSlot = this.configManager.getNavigationSlot(selectorKey, "back-button");
            if (clickedSlot == backButtonSlot) {
                clicker.closeInventory();
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.open((Player)clicker, quest));
                return;
            }
            if (typeSlots.contains(clickedSlot) && (index = typeSlots.indexOf(clickedSlot)) >= 0 && index < types.size()) {
                RewardType selectedType = (RewardType)types.get(index);
                clicker.closeInventory();
                this.requestRewardInput((Player)clicker, quest, selectedType);
            }
        });
        menu.open(player, items);
    }

    private ItemStack createRewardTypeItem(RewardType type) {
        ItemStack item = new ItemStack(type.getIcon());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName((Component)Component.text((String)type.getDisplayName(), (TextColor)NamedTextColor.GOLD));
            ArrayList<TextComponent> lore = new ArrayList<TextComponent>();
            if (!type.getDescription().isEmpty()) {
                lore.add(Component.text((String)type.getDescription(), (TextColor)NamedTextColor.GRAY));
            }
            lore.add(Component.empty());
            lore.add(Component.text((String)"Click to add this reward", (TextColor)NamedTextColor.YELLOW));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void requestRewardInput(Player player, Quest quest, RewardType type) {
        player.sendMessage((Component)Component.text((String)"\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550", (TextColor)NamedTextColor.GOLD));
        player.sendMessage((Component)Component.text((String)type.getInputPrompt(), (TextColor)NamedTextColor.YELLOW));
        if (type.getId().equals("item")) {
            player.sendMessage((Component)Component.text((String)"Type 'HAND' to use the item you're holding", (TextColor)NamedTextColor.AQUA));
            player.sendMessage((Component)Component.text((String)"Or type: <material> <amount>", (TextColor)NamedTextColor.GRAY));
            player.sendMessage((Component)Component.text((String)"Example: DIAMOND 8", (TextColor)NamedTextColor.GRAY));
        }
        player.sendMessage((Component)Component.text((String)"Type 'cancel' to abort", (TextColor)NamedTextColor.GRAY));
        player.sendMessage((Component)Component.text((String)"\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550", (TextColor)NamedTextColor.GOLD));
        this.chatManager.requestInput(player, type.getInputPrompt(), input -> {
            if (input.equalsIgnoreCase("cancel")) {
                player.sendMessage((Component)Component.text((String)"\u2717 Cancelled", (TextColor)NamedTextColor.RED));
                this.open(player, quest);
                return;
            }
            boolean success = false;
            String typeId = type.getId();
            try {
                switch (typeId) {
                    case "xp": {
                        int xpAmount = Integer.parseInt(input.trim());
                        success = this.plugin.getRewardManager().addXPReward(quest.getQuestId(), xpAmount);
                        break;
                    }
                    case "money": {
                        double moneyAmount = Double.parseDouble(input.trim());
                        success = this.plugin.getRewardManager().addMoneyReward(quest.getQuestId(), moneyAmount);
                        break;
                    }
                    case "command": {
                        String command = input.trim();
                        if (command.startsWith("/")) {
                            command = command.substring(1);
                        }
                        success = this.plugin.getRewardManager().addCommandReward(quest.getQuestId(), command);
                        break;
                    }
                    case "item": {
                        if (input.trim().equalsIgnoreCase("HAND")) {
                            ItemStack heldItem = player.getInventory().getItemInMainHand();
                            if (heldItem.getType() != Material.AIR && heldItem.getAmount() > 0) {
                                success = this.plugin.getRewardManager().addItemReward(quest.getQuestId(), heldItem.clone());
                                break;
                            }
                            player.sendMessage((Component)Component.text((String)"\u2717 You're not holding any item!", (TextColor)NamedTextColor.RED));
                            this.requestRewardInput(player, quest, type);
                            return;
                        }
                        String[] parts = input.trim().split("\\s+");
                        if (parts.length == 2) {
                            Material material = Material.valueOf((String)parts[0].toUpperCase());
                            int amount = Integer.parseInt(parts[1]);
                            ItemStack item = new ItemStack(material, amount);
                            success = this.plugin.getRewardManager().addItemReward(quest.getQuestId(), item);
                            break;
                        }
                        player.sendMessage((Component)Component.text((String)"\u2717 Invalid format! Use: <material> <amount> or HAND", (TextColor)NamedTextColor.RED));
                        this.requestRewardInput(player, quest, type);
                        return;
                    }
                    case "quest": {
                        String targetQuestId = input.trim();
                        if (!this.plugin.getQuestManager().questExists(targetQuestId)) {
                            player.sendMessage((Component)Component.text((String)("\u2717 Quest '" + targetQuestId + "' not found!"), (TextColor)NamedTextColor.RED));
                            this.requestRewardInput(player, quest, type);
                            return;
                        }
                        success = this.plugin.getRewardManager().addQuestReward(quest.getQuestId(), targetQuestId);
                    }
                }
            }
            catch (NumberFormatException e) {
                success = false;
            }
            catch (IllegalArgumentException e) {
                player.sendMessage((Component)Component.text((String)"\u2717 Invalid material name!", (TextColor)NamedTextColor.RED));
                this.requestRewardInput(player, quest, type);
                return;
            }
            if (success) {
                player.sendMessage((Component)Component.text((String)"\u2713 Reward added!", (TextColor)NamedTextColor.GREEN));
                this.open(player, quest);
            } else {
                player.sendMessage((Component)Component.text((String)"\u2717 Invalid input! Try again or type 'cancel'", (TextColor)NamedTextColor.RED));
                this.requestRewardInput(player, quest, type);
            }
        });
    }

    private void handleRemoveReward(Player player, Quest quest, int index) {
        boolean success = this.plugin.getRewardManager().removeReward(quest.getQuestId(), index);
        if (success) {
            player.sendMessage((Component)Component.text((String)("\u2713 Removed reward #" + index), (TextColor)NamedTextColor.GREEN));
            player.closeInventory();
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.open(player, quest));
        } else {
            player.sendMessage((Component)Component.text((String)"\u2717 Failed to remove reward!", (TextColor)NamedTextColor.RED));
        }
    }
}

