/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.kyori.adventure.text.format.TextDecoration
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
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
import com.soaps.quest.gui.GuiMenu;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.objectives.ObjectiveRegistry;
import com.soaps.quest.objectives.ObjectiveType;
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
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class ObjectiveEditorGui {
    private final SoapsQuest plugin;
    private final GuiConfigManager configManager;
    private final ChatInputManager chatManager;
    private final String guiKey = "objective-editor";
    private final Map<UUID, String> editingQuests;
    private final Map<String, List<Objective>> questObjectives;

    public ObjectiveEditorGui(SoapsQuest plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getGuiManager().getConfigManager();
        this.chatManager = plugin.getGuiManager().getChatInputManager();
        this.editingQuests = new HashMap<UUID, String>();
        this.questObjectives = new HashMap<String, List<Objective>>();
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
        this.loadObjectives(quest);
        ItemStack[] items = this.buildInventory(quest);
        String titleTemplate = this.configManager.getTitle("objective-editor");
        String titleWithPlaceholders = PlaceholderManager.replaceQuestPlaceholders(titleTemplate, quest);
        Component title = ColorUtil.colorize(titleWithPlaceholders);
        GuiMenu menu = new GuiMenu(this.plugin, title, this.configManager.getSize("objective-editor"), (clicker, slot, clickType) -> this.handleClickAdvanced(clicker, slot, clickType, quest));
        menu.open(player, items);
        this.plugin.debugLog("Opened objective editor for " + player.getName() + " (quest: " + quest.getQuestId() + ")");
    }

    private void loadObjectives(Quest quest) {
        ArrayList<Objective> objectives = new ArrayList<Objective>(quest.getObjectives());
        this.questObjectives.put(quest.getQuestId(), objectives);
        this.plugin.debugLog("Loaded " + objectives.size() + " objectives for quest: " + quest.getQuestId());
    }

    private ItemStack[] buildInventory(Quest quest) {
        int backSlot;
        ItemStack filler;
        GuiConfigManager.GuiCache cache = this.configManager.getGuiCache("objective-editor");
        ItemStack[] items = new ItemStack[cache.getSize()];
        if (cache.isFillEmpty() && (filler = this.configManager.getFillerItem("objective-editor")) != null) {
            for (int i = 0; i < cache.getSize(); ++i) {
                items[i] = filler.clone();
            }
        }
        List<Integer> objectiveSlots = this.getObjectiveSlots();
        List objectives = this.questObjectives.getOrDefault(quest.getQuestId(), new ArrayList());
        for (int i = 0; i < objectives.size() && i < objectiveSlots.size(); ++i) {
            ItemStack icon;
            int slot = objectiveSlots.get(i);
            Objective objective = (Objective)objectives.get(i);
            items[slot] = icon = this.createObjectiveIcon(objective);
        }
        int addSlot = this.configManager.getNavigationSlot("objective-editor", "add-objective");
        if (addSlot >= 0 && addSlot < items.length) {
            items[addSlot] = this.configManager.getItem("objective-editor", "add-objective");
        }
        if ((backSlot = this.configManager.getNavigationSlot("objective-editor", "back-button")) >= 0 && backSlot < items.length) {
            items[backSlot] = this.configManager.getNavigationItem("objective-editor", "back-button");
        }
        return items;
    }

    private List<Integer> getObjectiveSlots() {
        ConfigurationSection layoutSection = this.configManager.getConfig().getConfigurationSection("objective-editor.layout");
        if (layoutSection != null && layoutSection.contains("objective-slots")) {
            return layoutSection.getIntegerList("objective-slots");
        }
        return List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34);
    }

    private ItemStack createObjectiveIcon(Objective objective) {
        Material material = this.getMaterialForObjectiveType(objective.getType());
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ColorUtil.colorize("&e" + objective.getDescription()));
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(ColorUtil.colorize("&7Type: &f" + objective.getType()));
            lore.add(ColorUtil.colorize("&7Required: &f" + objective.getRequiredAmount()));
            lore.add(Component.empty());
            lore.add(ColorUtil.colorize("&cClick to remove"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private Material getMaterialForObjectiveType(String type) {
        ObjectiveType objectiveType = ObjectiveRegistry.getType(type);
        if (objectiveType != null) {
            return objectiveType.getIcon();
        }
        return switch (type.toLowerCase()) {
            case "kill" -> Material.IRON_SWORD;
            case "break" -> Material.DIAMOND_PICKAXE;
            case "place" -> Material.GRASS_BLOCK;
            case "collect" -> Material.CHEST;
            case "craft" -> Material.CRAFTING_TABLE;
            case "smelt" -> Material.FURNACE;
            case "fish" -> Material.FISHING_ROD;
            case "enchant" -> Material.ENCHANTING_TABLE;
            case "consume" -> Material.COOKED_BEEF;
            case "tame" -> Material.BONE;
            case "trade" -> Material.EMERALD;
            case "brew" -> Material.BREWING_STAND;
            case "shear" -> Material.SHEARS;
            case "sleep" -> Material.RED_BED;
            case "heal" -> Material.GOLDEN_APPLE;
            case "drop" -> Material.DROPPER;
            case "damage" -> Material.DIAMOND_SWORD;
            case "death" -> Material.SKELETON_SKULL;
            case "reachlevel", "gainlevel", "level" -> Material.EXPERIENCE_BOTTLE;
            case "move" -> Material.LEATHER_BOOTS;
            case "jump" -> Material.SLIME_BALL;
            case "chat" -> Material.WRITABLE_BOOK;
            case "interact" -> Material.OAK_BUTTON;
            case "bowshoot", "shoot_bow" -> Material.BOW;
            case "projectile" -> Material.ARROW;
            case "firework", "launch_firework" -> Material.FIREWORK_ROCKET;
            case "vehicle", "ride_vehicle" -> Material.MINECART;
            case "command" -> Material.COMMAND_BLOCK;
            case "placeholder" -> Material.NAME_TAG;
            case "kill_mythicmob" -> Material.BLAZE_ROD;
            default -> Material.PAPER;
        };
    }

    private void handleClickAdvanced(Player player, int slot, ClickType clickType, Quest quest) {
        List<Objective> objectives;
        int backSlot = this.configManager.getNavigationSlot("objective-editor", "back-button");
        if (slot == backSlot) {
            player.closeInventory();
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.getGuiManager().getQuestDetailsGui().open(player, quest));
            return;
        }
        int addSlot = this.configManager.getNavigationSlot("objective-editor", "add-objective");
        if (slot == addSlot) {
            List<Objective> objectives2 = this.questObjectives.get(quest.getQuestId());
            int objCount = objectives2 != null ? objectives2.size() : 0;
            String questType = this.getQuestType(quest);
            if (objCount >= 1 && questType.equals("single")) {
                player.sendMessage(ColorUtil.colorize("&cCannot add more objectives! This quest is type 'single'."));
                player.sendMessage(ColorUtil.colorize("&eChange quest type to 'multi' or 'sequence' first."));
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> this.open(player, quest), 10L);
                return;
            }
            player.closeInventory();
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.openObjectiveTypeSelector(player, quest));
            return;
        }
        List<Integer> objectiveSlots = this.getObjectiveSlots();
        int objectiveIndex = objectiveSlots.indexOf(slot);
        if (objectiveIndex >= 0 && (objectives = this.questObjectives.get(quest.getQuestId())) != null && objectiveIndex < objectives.size()) {
            String questType = this.getQuestType(quest);
            boolean isSingleType = questType.equals("single");
            if (clickType == ClickType.RIGHT) {
                if (isSingleType) {
                    player.sendMessage(ColorUtil.colorize("&eSelect a new objective type to replace the current one:"));
                    player.closeInventory();
                    Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.openObjectiveReplacementSelector(player, quest, objectiveIndex));
                } else {
                    player.sendMessage(ColorUtil.colorize("&7Right-click to change objective is only available for single-type quests."));
                }
            } else if (isSingleType) {
                player.sendMessage(ColorUtil.colorize("&cCannot remove the only objective from a single-type quest!"));
                player.sendMessage(ColorUtil.colorize("&eUse &aRight-Click &eto change it instead."));
            } else {
                Objective removed = objectives.remove(objectiveIndex);
                player.closeInventory();
                this.saveObjectives(quest, objectives);
                player.sendMessage(Component.text((String)"Removed objective: ", (TextColor)NamedTextColor.RED).append((Component)Component.text((String)removed.getDescription(), (TextColor)NamedTextColor.GRAY)));
                Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> {
                    Quest reloadedQuest = this.plugin.getQuestManager().reloadQuest(quest.getQuestId());
                    this.open(player, reloadedQuest != null ? reloadedQuest : quest);
                }, 2L);
            }
        }
    }

    private void openObjectiveTypeSelector(Player player, Quest quest) {
        int backSlot;
        ItemStack filler;
        String selectorKey = "objective-type-selector";
        GuiConfigManager.GuiCache cache = this.configManager.getGuiCache(selectorKey);
        ItemStack[] items = new ItemStack[cache.getSize()];
        if (cache.isFillEmpty() && (filler = this.configManager.getFillerItem(selectorKey)) != null) {
            for (int i = 0; i < cache.getSize(); ++i) {
                items[i] = filler.clone();
            }
        }
        if ((backSlot = this.configManager.getNavigationSlot(selectorKey, "back-button")) >= 0 && backSlot < items.length) {
            items[backSlot] = this.configManager.getNavigationItem(selectorKey, "back-button");
        }
        List<Integer> typeSlots = this.getTypeSelectorSlots();
        Map<String, ObjectiveType> types = ObjectiveRegistry.getAllTypes();
        int slotIndex = 0;
        for (ObjectiveType type : types.values()) {
            ItemStack icon;
            if (slotIndex >= typeSlots.size()) break;
            int slot2 = typeSlots.get(slotIndex++);
            items[slot2] = icon = this.createTypeIcon(type);
        }
        String title = this.configManager.getTitle(selectorKey);
        GuiMenu menu = new GuiMenu(this.plugin, title, cache.getSize(), (clicker, slot) -> this.handleTypeSelectorClick((Player)clicker, (int)slot, quest));
        menu.open(player, items);
    }

    private void openObjectiveReplacementSelector(Player player, Quest quest, int replaceIndex) {
        int backSlot;
        ItemStack filler;
        String selectorKey = "objective-type-selector";
        GuiConfigManager.GuiCache cache = this.configManager.getGuiCache(selectorKey);
        ItemStack[] items = new ItemStack[cache.getSize()];
        if (cache.isFillEmpty() && (filler = this.configManager.getFillerItem(selectorKey)) != null) {
            for (int i = 0; i < cache.getSize(); ++i) {
                items[i] = filler.clone();
            }
        }
        if ((backSlot = this.configManager.getNavigationSlot(selectorKey, "back-button")) >= 0 && backSlot < items.length) {
            items[backSlot] = this.configManager.getNavigationItem(selectorKey, "back-button");
        }
        List<Integer> typeSlots = this.getTypeSelectorSlots();
        Map<String, ObjectiveType> types = ObjectiveRegistry.getAllTypes();
        int slotIndex = 0;
        for (ObjectiveType type : types.values()) {
            ItemStack icon;
            if (slotIndex >= typeSlots.size()) break;
            int slot2 = typeSlots.get(slotIndex++);
            items[slot2] = icon = this.createReplacementTypeIcon(type);
        }
        GuiMenu menu = new GuiMenu(this.plugin, ColorUtil.colorize("&6&lReplace Objective"), cache.getSize(), (clicker, slot) -> this.handleReplacementTypeSelectorClick((Player)clicker, (int)slot, quest, replaceIndex));
        menu.open(player, items);
    }

    private ItemStack createReplacementTypeIcon(ObjectiveType type) {
        ItemStack item = new ItemStack(type.getIcon());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ColorUtil.colorize("&e&l" + type.getDisplayName()));
            ArrayList<Component> lore = new ArrayList<>();
            if (type.getDescription() != null) {
                lore.add(ColorUtil.colorize("&7" + type.getDescription()));
                lore.add(Component.empty());
            }
            lore.add(ColorUtil.colorize("&aClick to replace with this objective"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void handleReplacementTypeSelectorClick(Player player, int slot, Quest quest, int replaceIndex) {
        ArrayList<ObjectiveType> types;
        String selectorKey = "objective-type-selector";
        int backSlot = this.configManager.getNavigationSlot(selectorKey, "back-button");
        if (slot == backSlot) {
            player.closeInventory();
            Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> this.open(player, quest), 2L);
            return;
        }
        List<Integer> typeSlots = this.getTypeSelectorSlots();
        int typeIndex = typeSlots.indexOf(slot);
        if (typeIndex >= 0 && typeIndex < (types = new ArrayList<ObjectiveType>(ObjectiveRegistry.getAllTypes().values())).size()) {
            ObjectiveType selectedType = (ObjectiveType)types.get(typeIndex);
            player.closeInventory();
            this.requestObjectiveReplacementInput(player, quest, selectedType, replaceIndex);
        }
    }

    private List<Integer> getTypeSelectorSlots() {
        String selectorKey = "objective-type-selector";
        ConfigurationSection layoutSection = this.configManager.getConfig().getConfigurationSection(selectorKey + ".layout");
        if (layoutSection != null && layoutSection.contains("type-slots")) {
            return layoutSection.getIntegerList("type-slots");
        }
        return List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43);
    }

    private ItemStack createTypeIcon(ObjectiveType type) {
        ItemStack item = new ItemStack(type.getIcon());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ColorUtil.colorize("&e&l" + type.getDisplayName()));
            ArrayList<Component> lore = new ArrayList<>();
            if (type.getDescription() != null) {
                lore.add(ColorUtil.colorize("&7" + type.getDescription()));
                lore.add(Component.empty());
            }
            lore.add(ColorUtil.colorize("&aClick to add"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void handleTypeSelectorClick(Player player, int slot, Quest quest) {
        ArrayList<ObjectiveType> types;
        String selectorKey = "objective-type-selector";
        int backSlot = this.configManager.getNavigationSlot(selectorKey, "back-button");
        if (slot == backSlot) {
            player.closeInventory();
            Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> this.open(player, quest), 2L);
            return;
        }
        List<Integer> typeSlots = this.getTypeSelectorSlots();
        int typeIndex = typeSlots.indexOf(slot);
        if (typeIndex >= 0 && typeIndex < (types = new ArrayList<ObjectiveType>(ObjectiveRegistry.getAllTypes().values())).size()) {
            ObjectiveType selectedType = (ObjectiveType)types.get(typeIndex);
            player.closeInventory();
            this.requestObjectiveInput(player, quest, selectedType);
        }
    }

    private void requestObjectiveInput(Player player, Quest quest, ObjectiveType type) {
        player.sendMessage((Component)Component.text((String)"\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501", (TextColor)NamedTextColor.GRAY));
        player.sendMessage(Component.text((String)"Creating: ", (TextColor)NamedTextColor.YELLOW).append((Component)Component.text((String)type.getDisplayName(), (TextColor)NamedTextColor.GOLD)));
        player.sendMessage((Component)Component.empty());
        player.sendMessage((Component)Component.text((String)type.getInputPrompt(), (TextColor)NamedTextColor.GREEN));
        player.sendMessage((Component)Component.empty());
        player.sendMessage((Component)Component.text((String)"Type 'cancel' to cancel", (TextColor)NamedTextColor.GRAY));
        player.sendMessage((Component)Component.text((String)"\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501", (TextColor)NamedTextColor.GRAY));
        this.chatManager.requestInput(player, type.getInputPrompt(), input -> {
            if (input.equalsIgnoreCase("cancel")) {
                player.sendMessage((Component)Component.text((String)"Cancelled objective creation.", (TextColor)NamedTextColor.YELLOW));
                Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> this.open(player, quest), 2L);
                return;
            }
            try {
                Objective objective = type.getFactory().apply((String)input);
                if (objective == null) {
                    player.sendMessage((Component)Component.text((String)"Invalid input! Please try again.", (TextColor)NamedTextColor.RED));
                    player.sendMessage((Component)Component.text((String)("Expected format: " + type.getInputPrompt()), (TextColor)NamedTextColor.GRAY));
                    Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> this.open(player, quest), 2L);
                    return;
                }
                this.addObjective(player, quest, objective);
            }
            catch (IllegalArgumentException e) {
                player.sendMessage((Component)Component.text((String)("Error creating objective: " + e.getMessage()), (TextColor)NamedTextColor.RED));
                this.plugin.getLogger().log(Level.WARNING, "Error creating objective from input: " + input, e);
                Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> this.open(player, quest), 2L);
            }
        });
    }

    private void requestObjectiveReplacementInput(Player player, Quest quest, ObjectiveType type, int replaceIndex) {
        player.sendMessage((Component)Component.text((String)"\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501", (TextColor)NamedTextColor.GRAY));
        player.sendMessage(Component.text((String)"Replacing with: ", (TextColor)NamedTextColor.YELLOW).append((Component)Component.text((String)type.getDisplayName(), (TextColor)NamedTextColor.GOLD)));
        player.sendMessage((Component)Component.empty());
        player.sendMessage((Component)Component.text((String)type.getInputPrompt(), (TextColor)NamedTextColor.GREEN));
        player.sendMessage((Component)Component.empty());
        player.sendMessage((Component)Component.text((String)"Type 'cancel' to cancel", (TextColor)NamedTextColor.GRAY));
        player.sendMessage((Component)Component.text((String)"\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501", (TextColor)NamedTextColor.GRAY));
        this.chatManager.requestInput(player, type.getInputPrompt(), input -> {
            if (input.equalsIgnoreCase("cancel")) {
                player.sendMessage((Component)Component.text((String)"Cancelled objective replacement.", (TextColor)NamedTextColor.YELLOW));
                Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> this.open(player, quest), 2L);
                return;
            }
            try {
                Objective newObjective = type.getFactory().apply((String)input);
                if (newObjective == null) {
                    player.sendMessage((Component)Component.text((String)"Invalid input! Please try again.", (TextColor)NamedTextColor.RED));
                    player.sendMessage((Component)Component.text((String)("Expected format: " + type.getInputPrompt()), (TextColor)NamedTextColor.GRAY));
                    Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> this.open(player, quest), 2L);
                    return;
                }
                this.replaceObjective(player, quest, replaceIndex, newObjective);
            }
            catch (IllegalArgumentException e) {
                player.sendMessage((Component)Component.text((String)("Error creating objective: " + e.getMessage()), (TextColor)NamedTextColor.RED));
                this.plugin.getLogger().log(Level.WARNING, "Error creating objective from input: " + input, e);
                Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> this.open(player, quest), 2L);
            }
        });
    }

    private void addObjective(Player player, Quest quest, Objective objective) {
        List objectives = this.questObjectives.computeIfAbsent(quest.getQuestId(), k -> new ArrayList());
        objectives.add(objective);
        this.saveObjectives(quest, objectives);
        player.sendMessage(Component.text((String)"Added objective: ", (TextColor)NamedTextColor.GREEN).append((Component)Component.text((String)objective.getDescription(), (TextColor)NamedTextColor.WHITE)));
        Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> {
            Quest reloadedQuest = this.plugin.getQuestManager().reloadQuest(quest.getQuestId());
            if (reloadedQuest != null) {
                this.open(player, reloadedQuest);
            } else {
                this.open(player, quest);
            }
        }, 2L);
    }

    private void replaceObjective(Player player, Quest quest, int replaceIndex, Objective newObjective) {
        List<Objective> objectives = this.questObjectives.get(quest.getQuestId());
        if (objectives == null || replaceIndex < 0 || replaceIndex >= objectives.size()) {
            player.sendMessage((Component)Component.text((String)"Error: Invalid objective index.", (TextColor)NamedTextColor.RED));
            Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> this.open(player, quest), 2L);
            return;
        }
        Objective oldObjective = objectives.get(replaceIndex);
        objectives.set(replaceIndex, newObjective);
        this.saveObjectives(quest, objectives);
        player.sendMessage((Component)Component.text((String)"Replaced objective:", (TextColor)NamedTextColor.GREEN));
        player.sendMessage(Component.text((String)"  Old: ", (TextColor)NamedTextColor.GRAY).append((Component)Component.text((String)oldObjective.getDescription(), (TextColor)NamedTextColor.DARK_GRAY, (TextDecoration[])new TextDecoration[]{TextDecoration.STRIKETHROUGH})));
        player.sendMessage(Component.text((String)"  New: ", (TextColor)NamedTextColor.GRAY).append((Component)Component.text((String)newObjective.getDescription(), (TextColor)NamedTextColor.WHITE)));
        Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> {
            Quest reloadedQuest = this.plugin.getQuestManager().reloadQuest(quest.getQuestId());
            if (reloadedQuest != null) {
                this.open(player, reloadedQuest);
            } else {
                this.open(player, quest);
            }
        }, 2L);
    }

    private void saveObjectives(Quest quest, List<Objective> objectives) {
        Object questPath;
        boolean isGenerated = this.plugin.getQuestManager().isGeneratedQuest(quest.getQuestId());
        String fileName = isGenerated ? "generated.yml" : "quests.yml";
        File questFile = new File(this.plugin.getDataFolder(), fileName);
        YamlConfiguration config = YamlConfiguration.loadConfiguration((File)questFile);
        ConfigurationSection questSection = config.getConfigurationSection((String)(questPath = isGenerated ? quest.getQuestId() : "quests." + quest.getQuestId()));
        if (questSection == null) {
            this.plugin.getLogger().log(Level.WARNING, "Quest section not found: {0} in {1}", new Object[]{questPath, fileName});
            return;
        }
        ArrayList objectivesList = new ArrayList();
        for (Objective objective : objectives) {
            HashMap<String, Object> objMap = new HashMap<String, Object>();
            String serialized = objective.serialize();
            String[] parts = serialized.split(":");
            if (parts.length < 3) continue;
            String type = parts[0].toLowerCase();
            objMap.put("type", type);
            switch (type) {
                case "command": 
                case "placeholder": 
                case "chat": {
                    objMap.put(this.getTargetFieldName(type), parts[1]);
                    objMap.put("amount", Integer.valueOf(parts[2]));
                    break;
                }
                case "move": 
                case "jump": 
                case "bowshoot": 
                case "firework": 
                case "sleep": 
                case "death": {
                    objMap.put("amount", Integer.valueOf(parts[2]));
                    break;
                }
                case "reachlevel": {
                    objMap.put("amount", Integer.valueOf(parts[2]));
                    break;
                }
                case "gainlevel": 
                case "level": {
                    objMap.put("amount", Integer.valueOf(parts[2]));
                    break;
                }
                case "projectile": {
                    objMap.put("projectile", parts[1]);
                    objMap.put("amount", Integer.valueOf(parts[2]));
                    break;
                }
                case "vehicle": 
                case "ride_vehicle": {
                    objMap.put("vehicle", parts[1]);
                    objMap.put("amount", Integer.valueOf(parts[2]));
                    break;
                }
                case "heal": {
                    if (!parts[1].equals("ANY")) {
                        objMap.put("reason", parts[1]);
                    }
                    objMap.put("amount", Integer.valueOf(parts[2]));
                    break;
                }
                default: {
                    objMap.put("target", parts[1]);
                    objMap.put("amount", Integer.valueOf(parts[2]));
                }
            }
            objectivesList.add(objMap);
        }
        questSection.set("objectives", objectivesList);
        try {
            config.save(questFile);
            this.plugin.debugLog("Saved " + objectives.size() + " objectives for quest: " + quest.getQuestId() + " to " + fileName);
        }
        catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save objectives for quest: " + quest.getQuestId() + " to " + fileName, e);
        }
    }

    private String getTargetFieldName(String type) {
        return switch (type.toLowerCase()) {
            case "command" -> "command";
            case "placeholder" -> "placeholder";
            case "chat" -> "text";
            case "projectile" -> "projectile";
            case "vehicle", "ride_vehicle" -> "vehicle";
            case "heal" -> "reason";
            default -> "target";
        };
    }

    private String getQuestType(Quest quest) {
        try {
            YamlConfiguration generatedConfig;
            File questsFile = new File(this.plugin.getDataFolder(), "quests.yml");
            File generatedFile = new File(this.plugin.getDataFolder(), "generated.yml");
            YamlConfiguration config = null;
            Object questPath = null;
            if (generatedFile.exists() && (generatedConfig = YamlConfiguration.loadConfiguration((File)generatedFile)).contains(quest.getQuestId())) {
                config = generatedConfig;
                questPath = quest.getQuestId();
            }
            if (config == null && questsFile.exists()) {
                config = YamlConfiguration.loadConfiguration((File)questsFile);
                questPath = "quests." + quest.getQuestId();
            }
            if (config != null && config.contains(questPath + ".type")) {
                return config.getString((String)questPath + ".type", "single");
            }
            if (quest.isSequential()) {
                return "sequence";
            }
            int objCount = quest.getObjectives().size();
            return objCount <= 1 ? "single" : "multi";
        }
        catch (Exception e) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to read quest type for: " + quest.getQuestId(), e);
            return "single";
        }
    }

    public void clearAllData() {
        this.editingQuests.clear();
        this.questObjectives.clear();
    }
}

