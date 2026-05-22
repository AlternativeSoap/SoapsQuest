package com.soaps.quest.gui;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.soaps.common.api.gui.GuiManager;
import com.soaps.common.api.gui.GuiScreen;
import com.soaps.quest.SoapsQuest;
import com.soaps.quest.utils.ColorUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Lightweight wrapper that presents the same API as the original {@code GuiMenu} but delegates
 * inventory lifecycle to SoapsCommon's {@link GuiBus}/{@link GuiScreen} instead of registering
 * its own per-menu Bukkit listeners.
 *
 * <p>Public surface is unchanged so all existing GUI classes ({@code QuestBrowserGui},
 * {@code QuestEditorGui}, etc.) continue to compile and function identically.</p>
 */
public class GuiMenu {

    private static final String DEFAULT_GUI_TYPE = "soaps-gui";

    private final SoapsQuest plugin;
    private final Component titleComponent;
    private final int size;
    private final String guiType;
    private final AdvancedClickHandler clickHandler;
    private final Set<Inventory> activeInventories;
    private final LegacyComponentSerializer serializer;

    private GuiManager commonGui;

    public GuiMenu(SoapsQuest plugin, String title, int size, BiConsumer<Player, Integer> clickHandler) {
        this(plugin, ColorUtil.colorize(title), size, (player, slot, clickType) -> clickHandler.accept(player, slot), DEFAULT_GUI_TYPE);
    }

    public GuiMenu(SoapsQuest plugin, String title, int size, BiConsumer<Player, Integer> clickHandler, String guiType) {
        this(plugin, ColorUtil.colorize(title), size, (player, slot, clickType) -> clickHandler.accept(player, slot), guiType);
    }

    public GuiMenu(SoapsQuest plugin, Component title, int size, BiConsumer<Player, Integer> clickHandler) {
        this(plugin, title, size, (player, slot, clickType) -> clickHandler.accept(player, slot), DEFAULT_GUI_TYPE);
    }

    public GuiMenu(SoapsQuest plugin, Component title, int size, AdvancedClickHandler clickHandler) {
        this(plugin, title, size, clickHandler, DEFAULT_GUI_TYPE);
    }

    public GuiMenu(SoapsQuest plugin, Component title, int size, AdvancedClickHandler clickHandler, String guiType) {
        if (size % 9 != 0 || size < 9 || size > 54) {
            throw new IllegalArgumentException("Invalid inventory size: " + size + " (must be multiple of 9, 9-54)");
        }
        this.plugin = plugin;
        this.serializer = LegacyComponentSerializer.legacyAmpersand();
        this.titleComponent = title;
        this.size = size;
        this.guiType = guiType;
        this.clickHandler = clickHandler;
        this.activeInventories = new HashSet<>();
    }

    private GuiManager commonGui() {
        if (commonGui == null) {
            commonGui = plugin.getGuiManager() != null
                    ? new GuiManager(plugin)
                    : new GuiManager(plugin);
        }
        return commonGui;
    }

    /**
     * Open the menu for the given player with pre-populated items.
     */
    public void open(Player player, ItemStack[] items) {
        QuestScreen screen = new QuestScreen(commonGui(), guiType, titleComponent, size, clickHandler, items);
        activeInventories.add(screen.inventory());
        screen.open(player);
        plugin.debugLog("Opened GUI '" + serializer.serialize(titleComponent) + "' for " + player.getName());
    }

    public void open(Player player) {
        open(player, new ItemStack[size]);
    }

    public boolean isActiveInventory(Inventory inventory) {
        return activeInventories.contains(inventory);
    }

    public int getSize() {
        return size;
    }

    public String getTitle() {
        return serializer.serialize(titleComponent);
    }

    public Component getTitleComponent() {
        return titleComponent;
    }

    /**
     * Unregister: close all active inventories. No longer needs to unregister Bukkit listeners
     * since SoapsCommon's GuiBus handles events globally.
     */
    public void unregister() {
        for (Inventory inv : new HashSet<>(activeInventories)) {
            for (var viewer : new HashSet<>(inv.getViewers())) {
                viewer.closeInventory();
            }
        }
        activeInventories.clear();
        plugin.debugLog("Unregistered GUI: " + serializer.serialize(titleComponent));
    }

    public int getActiveInventoryCount() {
        return activeInventories.size();
    }

    public boolean isRegistered() {
        return !activeInventories.isEmpty();
    }

    public String getGuiType() {
        return guiType;
    }

    // ──────────────── inner screen implementation ────────────────

    /**
     * A minimal {@link GuiScreen} that delegates clicks to the original {@link AdvancedClickHandler}.
     */
    private final class QuestScreen extends GuiScreen {
        private final AdvancedClickHandler handler;
        private final ItemStack[] items;

        QuestScreen(GuiManager manager, String id, Component title, int size,
                    AdvancedClickHandler handler, ItemStack[] items) {
            super(manager, id, title, size / 9);
            this.handler = handler;
            this.items = items;
        }

        @Override
        public void build(Player viewer) {
            if (items == null) return;
            int max = Math.min(items.length, inventory().getSize());
            for (int i = 0; i < max; i++) {
                if (items[i] != null) set(i, items[i]);
            }
        }

        @Override
        public void onClick(Player player, int rawSlot, ClickType clickType) {
            if (rawSlot < 0 || rawSlot >= size) return;
            try {
                handler.accept(player, rawSlot, clickType);
            } catch (Exception e) {
                plugin.getLogger().severe("Error handling GUI click for " + player.getName()
                        + " in '" + serializer.serialize(titleComponent) + "' at slot " + rawSlot + ": " + e.getMessage());
            }
        }

        @Override
        public void onClose(Player player) {
            activeInventories.remove(inventory());
            plugin.debugLog("GUI Close: Player=" + player.getName() + ", GUI='" + serializer.serialize(titleComponent) + "'");
        }
    }

    @FunctionalInterface
    public static interface AdvancedClickHandler {
        void accept(Player player, int slot, ClickType clickType);
    }
}
