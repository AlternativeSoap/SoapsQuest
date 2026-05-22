/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package com.soaps.quest.rewards;

import com.soaps.quest.rewards.Reward;
import com.soaps.quest.utils.ColorUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemReward
implements Reward {
    private final ItemStack item;
    private final int chance;

    public ItemReward(ItemStack item) {
        this(item, 100);
    }

    public ItemReward(ItemStack item, int chance) {
        this.item = item.clone();
        this.chance = Math.max(0, Math.min(100, chance));
    }

    public ItemReward(ItemStack item, int amount, int chance) {
        this.item = item.clone();
        this.item.setAmount(amount);
        this.chance = Math.max(0, Math.min(100, chance));
    }

    public ItemReward(Material material, int amount, String name, List<String> lore, Map<Enchantment, Integer> enchantments) {
        this(material, amount, name, lore, enchantments, 100);
    }

    public ItemReward(Material material, int amount, String name, List<String> lore, Map<Enchantment, Integer> enchantments, int chance) {
        this.chance = Math.max(0, Math.min(100, chance));
        this.item = new ItemStack(material, amount);
        ItemMeta meta = this.item.getItemMeta();
        if (meta != null) {
            if (name != null && !name.isEmpty()) {
                meta.displayName(this.parseColorCodes(name));
            }
            if (lore != null && !lore.isEmpty()) {
                ArrayList<Component> loreComponents = new ArrayList<Component>();
                for (String line : lore) {
                    loreComponents.add(this.parseColorCodes(line));
                }
                meta.lore(loreComponents);
            }
            if (enchantments != null && !enchantments.isEmpty()) {
                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    meta.addEnchant(entry.getKey(), entry.getValue().intValue(), true);
                }
            }
            this.item.setItemMeta(meta);
        }
    }

    @Override
    public boolean give(Player player) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(new ItemStack[]{this.item.clone()});
            return true;
        }
        Location location = player.getLocation();
        if (location != null && location.getWorld() != null) {
            location.getWorld().dropItem(location, this.item.clone());
        }
        return true;
    }

    @Override
    public String getDescription() {
        Component displayName;
        String itemName = this.item.getType().name();
        if (this.item.getItemMeta() != null && this.item.getItemMeta().hasDisplayName() && (displayName = this.item.getItemMeta().displayName()) != null) {
            itemName = LegacyComponentSerializer.legacyAmpersand().serialize(displayName);
        }
        return this.item.getAmount() + "x " + itemName;
    }

    public ItemStack getItem() {
        return this.item.clone();
    }

    @Override
    public int getChance() {
        return this.chance;
    }

    private Component parseColorCodes(String text) {
        return ColorUtil.colorize(text);
    }
}

