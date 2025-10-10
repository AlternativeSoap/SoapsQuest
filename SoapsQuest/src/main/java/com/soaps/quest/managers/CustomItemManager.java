package com.soaps.quest.managers;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.soaps.quest.SoapsQuest;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;

/**
 * Manages integration with custom item plugins (MMOItems).
 * Handles detection, parsing, and creation of custom items.
 */
public class CustomItemManager {
    
    private final SoapsQuest plugin;
    private boolean mmoItemsEnabled = false;
    
    public CustomItemManager(SoapsQuest plugin) {
        this.plugin = plugin;
        detectPlugins();
    }
    
    /**
     * Detect which custom item plugins are installed and enabled.
     */
    private void detectPlugins() {
        Plugin mmoItems = Bukkit.getPluginManager().getPlugin("MMOItems");
        if (mmoItems != null && mmoItems.isEnabled()) {
            mmoItemsEnabled = true;
            plugin.getLogger().info("MMOItems detected! Custom item support enabled.");
        }
    }
    
    /**
     * Check if MMOItems is available.
     */
    public boolean isMMOItemsEnabled() {
        return mmoItemsEnabled;
    }
    
    /**
     * Parse and create an item from a custom item string.
     * Supports formats:
     * - "mmoitem:TYPE:ID" or "mmoitems:TYPE:ID" for MMOItems
     * - "VANILLA_MATERIAL" for vanilla items (returns null to use default parsing)
     * 
     * @param itemString The item identifier string
     * @return ItemStack if custom item found, null if vanilla item or not found
     */
    public ItemStack parseCustomItem(String itemString) {
        if (itemString == null || itemString.isEmpty()) {
            return null;
        }
        
        // Check for MMOItems format: mmoitem:TYPE:ID or mmoitems:TYPE:ID
        if (itemString.toLowerCase().startsWith("mmoitem:") || 
            itemString.toLowerCase().startsWith("mmoitems:")) {
            return parseMMOItem(itemString);
        }
        
        // Not a custom item format
        return null;
    }
    
    /**
     * Parse and create an MMOItems item.
     * Format: mmoitem:TYPE:ID or mmoitems:TYPE:ID
     * Example: mmoitem:SWORD:LEGENDARY_BLADE
     */
    private ItemStack parseMMOItem(String itemString) {
        if (!mmoItemsEnabled) {
            plugin.getLogger().warning(() -> "Cannot create MMOItem '" + itemString + "' - MMOItems is not installed!");
            return null;
        }
        
        try {
            String[] parts = itemString.split(":");
            if (parts.length < 3) {
                plugin.getLogger().warning(() -> "Invalid MMOItems format: " + itemString + " (expected: mmoitem:TYPE:ID)");
                return null;
            }
            
            String typeString = parts[1].toUpperCase();
            String itemId = parts[2].toUpperCase();
            
            // Get MMOItems type
            Type type = MMOItems.plugin.getTypes().get(typeString);
            if (type == null) {
                plugin.getLogger().warning(() -> "Unknown MMOItems type: " + typeString);
                return null;
            }
            
            // Generate item
            ItemStack item = MMOItems.plugin.getItem(type, itemId);
            if (item == null) {
                plugin.getLogger().warning(() -> "MMOItems item not found: " + typeString + ":" + itemId);
                return null;
            }
            
            plugin.getLogger().fine(() -> "Created MMOItems item: " + typeString + ":" + itemId);
            return item;
            
        } catch (Exception e) {
            plugin.getLogger().warning(() -> "Error creating MMOItems item '" + itemString + "': " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Check if a string represents a custom item identifier.
     * 
     * @param itemString The item identifier string
     * @return True if this is a custom item format
     */
    public boolean isCustomItem(String itemString) {
        if (itemString == null || itemString.isEmpty()) {
            return false;
        }
        
        String lower = itemString.toLowerCase();
        return lower.startsWith("mmoitem:") || lower.startsWith("mmoitems:");
    }
}
