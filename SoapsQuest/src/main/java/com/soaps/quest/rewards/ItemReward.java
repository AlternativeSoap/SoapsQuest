package com.soaps.quest.rewards;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Reward that gives an item to the player.
 */
public class ItemReward implements Reward {
    
    private final ItemStack item;
    private final int chance; // 0-100, 100 = guaranteed
    
    /**
     * Constructor for ItemReward.
     * 
     * @param item ItemStack to give
     */
    public ItemReward(ItemStack item) {
        this(item, 100);
    }
    
    /**
     * Constructor for ItemReward with chance.
     * 
     * @param item ItemStack to give
     * @param chance Chance percentage (0-100)
     */
    public ItemReward(ItemStack item, int chance) {
        this.item = item.clone();
        this.chance = Math.max(0, Math.min(100, chance));
    }
    
    /**
     * Constructor for custom items (MMOItems, Crucible) with amount and chance.
     * 
     * @param item ItemStack to give (already configured)
     * @param amount Stack amount
     * @param chance Chance percentage (0-100)
     */
    public ItemReward(ItemStack item, int amount, int chance) {
        this.item = item.clone();
        this.item.setAmount(amount);
        this.chance = Math.max(0, Math.min(100, chance));
    }
    
    /**
     * Constructor that builds an ItemStack from parameters.
     * 
     * @param material Item material
     * @param amount Stack amount
     * @param name Display name (supports color codes)
     * @param lore List of lore lines (supports color codes)
     * @param enchantments Map of enchantment to level
     */
    public ItemReward(Material material, int amount, String name, List<String> lore, 
                      Map<Enchantment, Integer> enchantments) {
        this(material, amount, name, lore, enchantments, 100);
    }
    
    /**
     * Constructor that builds an ItemStack from parameters with chance.
     * 
     * @param material Item material
     * @param amount Stack amount
     * @param name Display name (supports color codes)
     * @param lore List of lore lines (supports color codes)
     * @param enchantments Map of enchantment to level
     * @param chance Chance percentage (0-100)
     */
    public ItemReward(Material material, int amount, String name, List<String> lore, 
                      Map<Enchantment, Integer> enchantments, int chance) {
        this.chance = Math.max(0, Math.min(100, chance));
        this.item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            // Set display name
            if (name != null && !name.isEmpty()) {
                meta.displayName(parseColorCodes(name));
            }
            
            // Set lore
            if (lore != null && !lore.isEmpty()) {
                List<Component> loreComponents = new ArrayList<>();
                for (String line : lore) {
                    loreComponents.add(parseColorCodes(line));
                }
                meta.lore(loreComponents);
            }
            
            // Add enchantments
            if (enchantments != null && !enchantments.isEmpty()) {
                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    meta.addEnchant(entry.getKey(), entry.getValue(), true);
                }
            }
            
            item.setItemMeta(meta);
        }
    }
    
    @Override
    public boolean give(Player player) {
        // Try to add to inventory
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item.clone());
            return true;
        } else {
            // Drop at player's location if inventory is full
            org.bukkit.Location location = player.getLocation();
            if (location != null && location.getWorld() != null) {
                location.getWorld().dropItem(location, item.clone());
            }
            return true;
        }
    }
    
    @Override
    public String getDescription() {
        String itemName = item.getType().name();
        if (item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
            Component displayName = item.getItemMeta().displayName();
            if (displayName != null) {
                itemName = LegacyComponentSerializer.legacySection().serialize(displayName);
            }
        }
        return item.getAmount() + "x " + itemName;
    }
    
    /**
     * Get the item stack.
     * 
     * @return ItemStack
     */
    public ItemStack getItem() {
        return item.clone();
    }
    
    @Override
    public int getChance() {
        return chance;
    }
    
    /**
     * Parse color codes supporting both legacy (&) and MiniMessage formats.
     * 
     * @param text Text to parse
     * @return Parsed Component
     */
    private Component parseColorCodes(String text) {
        // First try MiniMessage (for <gradient>, hex, etc.)
        if (text.contains("<") && text.contains(">")) {
            try {
                return MiniMessage.miniMessage().deserialize(text);
            } catch (Exception e) {
                // Fall back to legacy if MiniMessage fails
            }
        }
        
        // Use legacy format for & codes
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}
