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
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package com.soaps.quest.utils;

import com.soaps.quest.utils.ColorUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class GuiConfigManager {
    private final Plugin plugin;
    private FileConfiguration config;
    private final Map<String, GuiCache> cache;
    private final LegacyComponentSerializer serializer;

    public GuiConfigManager(Plugin plugin) {
        this.plugin = plugin;
        this.cache = new HashMap<String, GuiCache>();
        this.serializer = LegacyComponentSerializer.legacyAmpersand();
        this.reload();
    }

    public final void reload() {
        File configFile = new File(this.plugin.getDataFolder(), "gui.yml");
        if (!configFile.exists()) {
            this.plugin.saveResource("gui.yml", false);
            this.plugin.getLogger().log(Level.FINE, "Created default gui.yml configuration");
        }
        try {
            this.config = YamlConfiguration.loadConfiguration((File)configFile);
            this.cache.clear();
            this.plugin.getLogger().log(Level.FINE, "Loaded GUI configuration from gui.yml");
        }
        catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to load gui.yml: {0}", e.getMessage());
            this.config = new YamlConfiguration();
        }
    }

    public String getTitle(String guiKey) {
        return this.getTitle(guiKey, "&6&lSoapsQuest &7| GUI");
    }

    public String getTitle(String guiKey, String defaultTitle) {
        String path = guiKey + ".title";
        String title = this.config.getString(path, defaultTitle);
        if (title == null) {
            title = defaultTitle;
        }
        return title;
    }

    public Component getTitleComponent(String guiKey) {
        String title = this.getTitle(guiKey);
        return ColorUtil.colorize(title);
    }

    public int getSize(String guiKey) {
        return this.getSize(guiKey, 54);
    }

    public int getSize(String guiKey, int defaultSize) {
        String path = guiKey + ".size";
        int size = this.config.getInt(path, defaultSize);
        if (size < 9 || size > 54 || size % 9 != 0) {
            this.plugin.getLogger().log(Level.WARNING, "Invalid inventory size {0} for GUI ''{1}'', using {2}", new Object[]{size, guiKey, defaultSize});
            return defaultSize;
        }
        return size;
    }

    public boolean isFillEmpty(String guiKey) {
        return this.config.getBoolean(guiKey + ".fill-empty", true);
    }

    public List<Integer> getSlots(String guiKey, String path) {
        String fullPath = guiKey + "." + path;
        if (this.config.isList(fullPath)) {
            return this.config.getIntegerList(fullPath);
        }
        return new ArrayList<Integer>();
    }

    public List<Integer> getContentSlots(String guiKey) {
        List<Integer> slots = this.getSlots(guiKey, "layout.quest-slots");
        if (!slots.isEmpty()) {
            return slots;
        }
        slots = this.getSlots(guiKey, "layout.content-slots");
        if (!slots.isEmpty()) {
            return slots;
        }
        return this.generateDefaultSlots(this.getSize(guiKey));
    }

    private List<Integer> generateDefaultSlots(int size) {
        ArrayList<Integer> slots = new ArrayList<Integer>();
        int rows = size / 9;
        if (rows <= 2) {
            for (int i = 1; i < size - 1; ++i) {
                if (i % 9 == 0 || i % 9 == 8) continue;
                slots.add(i);
            }
        } else {
            for (int row = 1; row < rows - 1; ++row) {
                for (int col = 1; col < 8; ++col) {
                    slots.add(row * 9 + col);
                }
            }
        }
        return slots;
    }

    public Material getMaterial(String guiKey, String path) {
        return this.getMaterial(guiKey, path, Material.STONE);
    }

    public Material getMaterial(String guiKey, String path, Material defaultMaterial) {
        String fullPath = guiKey + "." + path;
        String materialName = this.config.getString(fullPath);
        if (materialName == null || materialName.isEmpty()) {
            return defaultMaterial;
        }
        Material material = Material.matchMaterial((String)materialName);
        if (material == null) {
            this.plugin.getLogger().log(Level.WARNING, "Invalid material ''{0}'' at {1}, using {2}", new Object[]{materialName, fullPath, defaultMaterial.name()});
            return defaultMaterial;
        }
        return material;
    }

    public int getSlot(String guiKey, String path) {
        return this.getSlot(guiKey, path, -1);
    }

    public int getSlot(String guiKey, String path, int defaultSlot) {
        String fullPath = guiKey + "." + path;
        return this.config.getInt(fullPath, defaultSlot);
    }

    public String getString(String guiKey, String path) {
        return this.getString(guiKey, path, null);
    }

    public String getString(String guiKey, String path, String defaultValue) {
        String fullPath = guiKey + "." + path;
        return this.config.getString(fullPath, defaultValue);
    }

    public List<String> getStringList(String guiKey, String path) {
        String fullPath = guiKey + "." + path;
        if (this.config.isList(fullPath)) {
            return this.config.getStringList(fullPath);
        }
        return new ArrayList<String>();
    }

    public ItemStack getItem(String guiKey, String itemKey) {
        String path = guiKey + "." + itemKey;
        ConfigurationSection section = this.config.getConfigurationSection(path);
        if (section == null) {
            return null;
        }
        Material material = this.getMaterial(guiKey, itemKey + ".material", Material.PAPER);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = section.getString("name");
            if (name != null && !name.isEmpty()) {
                meta.displayName(ColorUtil.colorize(name));
            }
            if (section.isList("lore")) {
                List<String> loreStrings = section.getStringList("lore");
                ArrayList<Component> lore = new ArrayList<Component>();
                for (String line : loreStrings) {
                    lore.add(ColorUtil.colorize(line));
                }
                meta.lore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack getItemWithPlaceholders(String guiKey, String itemKey, Map<String, String> placeholders) {
        List<Component> lore;
        ItemStack item = this.getItem(guiKey, itemKey);
        if (item == null || !item.hasItemMeta()) {
            return item;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
            Component displayName = meta.displayName();
            String nameStr = this.serializer.serialize(displayName);
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                nameStr = nameStr.replace(entry.getKey(), entry.getValue());
            }
            meta.displayName(ColorUtil.colorize(nameStr));
        }
        if (meta.hasLore() && (lore = meta.lore()) != null) {
            ArrayList<Component> newLore = new ArrayList<Component>();
            for (Component line : lore) {
                String lineStr = this.serializer.serialize(line);
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    lineStr = lineStr.replace(entry.getKey(), entry.getValue());
                }
                newLore.add(ColorUtil.colorize(lineStr));
            }
            meta.lore(newLore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getFillerItem(String guiKey) {
        if (!this.isFillEmpty(guiKey)) {
            return null;
        }
        return this.getItem(guiKey, "filler-item");
    }

    public ItemStack getNavigationItem(String guiKey, String navKey) {
        return this.getItem(guiKey, navKey);
    }

    public int getNavigationSlot(String guiKey, String navKey) {
        return this.getSlot(guiKey, navKey + ".slot", -1);
    }

    public boolean hasGui(String guiKey) {
        return this.config.isConfigurationSection(guiKey);
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public GuiCache getGuiCache(String guiKey) {
        return this.cache.computeIfAbsent(guiKey, key -> new GuiCache((String)key, this));
    }

    public void clearCache() {
        this.cache.clear();
    }

    public static class GuiCache {
        private final String guiKey;
        private final String title;
        private final Component titleComponent;
        private final int size;
        private final boolean fillEmpty;
        private final List<Integer> contentSlots;

        private GuiCache(String guiKey, GuiConfigManager manager) {
            this.guiKey = guiKey;
            this.title = manager.getTitle(guiKey);
            this.titleComponent = manager.getTitleComponent(guiKey);
            this.size = manager.getSize(guiKey);
            this.fillEmpty = manager.isFillEmpty(guiKey);
            this.contentSlots = manager.getContentSlots(guiKey);
        }

        public String getGuiKey() {
            return this.guiKey;
        }

        public String getTitle() {
            return this.title;
        }

        public Component getTitleComponent() {
            return this.titleComponent;
        }

        public int getSize() {
            return this.size;
        }

        public boolean isFillEmpty() {
            return this.fillEmpty;
        }

        public List<Integer> getContentSlots() {
            return this.contentSlots;
        }
    }
}

