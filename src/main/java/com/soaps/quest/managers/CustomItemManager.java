/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.Indyuce.mmoitems.MMOItems
 *  net.Indyuce.mmoitems.api.Type
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.persistence.PersistentDataContainer
 *  org.bukkit.persistence.PersistentDataType
 *  org.bukkit.plugin.Plugin
 */
package com.soaps.quest.managers;

import com.soaps.quest.SoapsQuest;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class CustomItemManager {
    private final SoapsQuest plugin;
    private boolean mmoItemsEnabled = false;

    public CustomItemManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.detectPlugins();
    }

    private void detectPlugins() {
        Plugin mmoItems = Bukkit.getPluginManager().getPlugin("MMOItems");
        if (mmoItems != null && mmoItems.isEnabled()) {
            this.mmoItemsEnabled = true;
            this.plugin.debugLog("MMOItems detected! Custom item support enabled.");
        }
    }

    public boolean isMMOItemsEnabled() {
        return this.mmoItemsEnabled;
    }

    public boolean isPluginItem(String itemString) {
        if (itemString == null || itemString.isEmpty()) {
            return false;
        }
        String lower = itemString.toLowerCase();
        return lower.contains(":") && !lower.startsWith("minecraft:");
    }

    public String getNamespace(String itemString) {
        if (!this.isPluginItem(itemString)) {
            return null;
        }
        int colonIndex = itemString.indexOf(58);
        if (colonIndex > 0) {
            return itemString.substring(0, colonIndex).toLowerCase();
        }
        return null;
    }

    public String detectPluginNamespace(ItemStack item) {
        NamespacedKey mmoKey;
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (this.mmoItemsEnabled && pdc.has(mmoKey = new NamespacedKey("mmoitems", "type"))) {
            return "mmoitems";
        }
        return null;
    }

    public String getPluginItemId(ItemStack item, String namespace) {
        if (item == null || !item.hasItemMeta() || namespace == null) {
            return null;
        }
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        try {
            switch (namespace.toLowerCase()) {
                case "mmoitems": 
                case "mmoitem": {
                    if (!this.mmoItemsEnabled) break;
                    NamespacedKey idKey = new NamespacedKey("mmoitems", "id");
                    return (String)pdc.get(idKey, PersistentDataType.STRING);
                }
            }
        }
        catch (Exception e) {
            this.plugin.getLogger().log(Level.WARNING, "Error getting plugin item ID: {0}", e.getMessage());
        }
        return null;
    }

    public ItemStack parseCustomItem(String itemString) {
        String lower;
        if (itemString == null || itemString.isEmpty()) {
            return null;
        }
        if (!itemString.contains(":")) {
            try {
                Material material = Material.valueOf((String)itemString.toUpperCase());
                return new ItemStack(material);
            }
            catch (IllegalArgumentException material) {
                // empty catch block
            }
        }
        if ((lower = itemString.toLowerCase()).startsWith("mmoitem:") || lower.startsWith("mmoitems:")) {
            return this.parseMMOItem(itemString);
        }
        return null;
    }

    private ItemStack parseMMOItem(String itemString) {
        if (!this.mmoItemsEnabled) {
            this.plugin.getLogger().log(Level.WARNING, "Cannot create MMOItem ''{0}'' - MMOItems is not installed!", itemString);
            return null;
        }
        try {
            String[] parts = itemString.split(":");
            if (parts.length < 3) {
                this.plugin.getLogger().log(Level.WARNING, "Invalid MMOItems format: {0} (expected: mmoitems:TYPE:ID)", itemString);
                return null;
            }
            String typeString = parts[1].toUpperCase();
            String itemId = parts[2].toUpperCase();
            Type type = MMOItems.plugin.getTypes().get(typeString);
            if (type == null) {
                this.plugin.getLogger().log(Level.WARNING, "Unknown MMOItems type: {0}", typeString);
                return null;
            }
            ItemStack item = MMOItems.plugin.getItem(type, itemId);
            if (item == null) {
                this.plugin.getLogger().log(Level.WARNING, "MMOItems item not found: {0}:{1}", new Object[]{typeString, itemId});
                return null;
            }
            this.plugin.getLogger().log(Level.FINE, "Created MMOItems item: {0}:{1}", new Object[]{typeString, itemId});
            return item;
        }
        catch (Exception e) {
            this.plugin.getLogger().log(Level.WARNING, "Error creating MMOItems item ''{0}'': {1}", new Object[]{itemString, e.getMessage()});
            return null;
        }
    }

    @Deprecated
    public boolean isCustomItem(String itemString) {
        return this.isPluginItem(itemString);
    }
}

