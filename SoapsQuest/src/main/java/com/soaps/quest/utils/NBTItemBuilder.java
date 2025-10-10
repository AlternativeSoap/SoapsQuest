package com.soaps.quest.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Utility class for building ItemStacks with NBT-like customization.
 * Provides a fluent API for setting custom names, lore, enchantments, flags, and other properties.
 */
public class NBTItemBuilder {
    
    private final ItemStack itemStack;
    private final ItemMeta itemMeta;
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();
    
    /**
     * Create a new NBTItemBuilder from a material.
     * 
     * @param material The material to use
     */
    public NBTItemBuilder(Material material) {
        this(material, 1);
    }
    
    /**
     * Create a new NBTItemBuilder from a material with specified amount.
     * 
     * @param material The material to use
     * @param amount The stack size
     */
    public NBTItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = itemStack.getItemMeta();
    }
    
    /**
     * Create a new NBTItemBuilder from an existing ItemStack.
     * 
     * @param itemStack The base ItemStack
     */
    public NBTItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        this.itemMeta = this.itemStack.getItemMeta();
    }
    
    /**
     * Set the custom display name of the item.
     * Supports legacy color codes with & character.
     * 
     * @param name The display name (supports & color codes)
     * @return This builder for chaining
     */
    public NBTItemBuilder setName(String name) {
        if (name != null && !name.isEmpty()) {
            Component component = legacySerializer.deserialize(name);
            itemMeta.displayName(component);
        }
        return this;
    }
    
    /**
     * Set the lore (description) of the item.
     * Supports legacy color codes with & character.
     * 
     * @param lore The lore lines (supports & color codes)
     * @return This builder for chaining
     */
    public NBTItemBuilder setLore(List<String> lore) {
        if (lore != null && !lore.isEmpty()) {
            List<Component> componentLore = new ArrayList<>();
            for (String line : lore) {
                componentLore.add(legacySerializer.deserialize(line));
            }
            itemMeta.lore(componentLore);
        }
        return this;
    }
    
    /**
     * Add a single enchantment to the item.
     * 
     * @param enchantment The enchantment to add
     * @param level The enchantment level
     * @return This builder for chaining
     */
    public NBTItemBuilder addEnchantment(Enchantment enchantment, int level) {
        if (enchantment != null) {
            itemMeta.addEnchant(enchantment, level, true); // true = ignore level restrictions
        }
        return this;
    }
    
    /**
     * Add multiple enchantments to the item.
     * 
     * @param enchantments Map of enchantments to levels
     * @return This builder for chaining
     */
    public NBTItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        if (enchantments != null) {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                itemMeta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }
        return this;
    }
    
    /**
     * Add a single item flag.
     * 
     * @param flag The flag to add
     * @return This builder for chaining
     */
    public NBTItemBuilder addFlag(ItemFlag flag) {
        if (flag != null) {
            itemMeta.addItemFlags(flag);
        }
        return this;
    }
    
    /**
     * Add multiple item flags.
     * 
     * @param flags The flags to add
     * @return This builder for chaining
     */
    public NBTItemBuilder addFlags(List<ItemFlag> flags) {
        if (flags != null) {
            for (ItemFlag flag : flags) {
                itemMeta.addItemFlags(flag);
            }
        }
        return this;
    }
    
    /**
     * Set whether the item is unbreakable.
     * 
     * @param unbreakable True to make unbreakable
     * @return This builder for chaining
     */
    public NBTItemBuilder setUnbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }
    
    /**
     * Set the custom model data for the item (for resource packs).
     * 
     * @param customModelData The custom model data value
     * @return This builder for chaining
     */
    public NBTItemBuilder setCustomModelData(int customModelData) {
        itemMeta.setCustomModelData(customModelData);
        return this;
    }
    
    /**
     * Set the stack size.
     * 
     * @param amount The stack size (1-64)
     * @return This builder for chaining
     */
    public NBTItemBuilder setAmount(int amount) {
        itemStack.setAmount(Math.max(1, Math.min(64, amount)));
        return this;
    }
    
    /**
     * Build and return the final ItemStack with all applied properties.
     * 
     * @return The customized ItemStack
     */
    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    /**
     * Helper method to parse an enchantment from a string.
     * Supports formats:
     * - "DAMAGE_ALL:5" (enchantment key and level)
     * - "DAMAGE_ALL" (enchantment key only, defaults to level 1)
     * 
     * @param enchantString The enchantment string
     * @return Map entry with enchantment and level, or null if invalid
     */
    public static Map.Entry<Enchantment, Integer> parseEnchantment(String enchantString) {
        if (enchantString == null || enchantString.isEmpty()) {
            return null;
        }
        
        try {
            String[] parts = enchantString.split(":");
            String enchantKey = parts[0].toUpperCase();
            int level = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
            
            // Try with minecraft: namespace (modern approach)
            Enchantment enchantment = org.bukkit.Registry.ENCHANTMENT.get(
                org.bukkit.NamespacedKey.minecraft(enchantKey.toLowerCase())
            );
            
            if (enchantment != null) {
                return Map.entry(enchantment, level);
            }
        } catch (IllegalArgumentException e) {
            // Invalid enchantment key or invalid level number (NumberFormatException extends IllegalArgumentException)
        }
        
        return null;
    }
    
    /**
     * Helper method to parse multiple enchantments from a list of strings.
     * 
     * @param enchantStrings List of enchantment strings
     * @return Map of enchantments to levels
     */
    public static Map<Enchantment, Integer> parseEnchantments(List<String> enchantStrings) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        
        if (enchantStrings != null) {
            for (String enchantString : enchantStrings) {
                Map.Entry<Enchantment, Integer> entry = parseEnchantment(enchantString);
                if (entry != null) {
                    enchantments.put(entry.getKey(), entry.getValue());
                }
            }
        }
        
        return enchantments;
    }
    
    /**
     * Helper method to parse an ItemFlag from a string.
     * 
     * @param flagString The flag name (e.g., "HIDE_ENCHANTS")
     * @return The ItemFlag, or null if invalid
     */
    public static ItemFlag parseFlag(String flagString) {
        if (flagString == null || flagString.isEmpty()) {
            return null;
        }
        
        try {
            return ItemFlag.valueOf(flagString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Helper method to parse multiple ItemFlags from a list of strings.
     * 
     * @param flagStrings List of flag names
     * @return List of ItemFlags
     */
    public static List<ItemFlag> parseFlags(List<String> flagStrings) {
        List<ItemFlag> flags = new ArrayList<>();
        
        if (flagStrings != null) {
            for (String flagString : flagStrings) {
                ItemFlag flag = parseFlag(flagString);
                if (flag != null) {
                    flags.add(flag);
                }
            }
        }
        
        return flags;
    }
}
