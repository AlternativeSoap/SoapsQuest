/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.registry.RegistryAccess
 *  io.papermc.paper.registry.RegistryKey
 *  net.kyori.adventure.text.Component
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package com.soaps.quest.utils;

import com.soaps.quest.utils.ColorUtil;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackBuilder {
    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemStackBuilder(Material material) {
        this(material, 1);
    }

    public ItemStackBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemStackBuilder setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.itemMeta.displayName(ColorUtil.colorize(name));
        }
        return this;
    }

    public ItemStackBuilder setLore(List<String> lore) {
        if (lore != null && !lore.isEmpty()) {
            ArrayList<Component> componentLore = new ArrayList<Component>();
            for (String line : lore) {
                componentLore.add(ColorUtil.colorize(line));
            }
            this.itemMeta.lore(componentLore);
        }
        return this;
    }

    public ItemStackBuilder addEnchantment(Enchantment enchantment, int level) {
        if (enchantment != null) {
            this.itemMeta.addEnchant(enchantment, level, true);
        }
        return this;
    }

    public ItemStackBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        if (enchantments != null) {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                this.itemMeta.addEnchant(entry.getKey(), entry.getValue().intValue(), true);
            }
        }
        return this;
    }

    public ItemStackBuilder addFlag(ItemFlag flag) {
        if (flag != null) {
            this.itemMeta.addItemFlags(new ItemFlag[]{flag});
        }
        return this;
    }

    public ItemStackBuilder addFlags(List<ItemFlag> flags) {
        if (flags != null) {
            for (ItemFlag flag : flags) {
                this.itemMeta.addItemFlags(new ItemFlag[]{flag});
            }
        }
        return this;
    }

    public ItemStackBuilder setUnbreakable(boolean unbreakable) {
        this.itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemStackBuilder setCustomModelData(int customModelData) {
        this.itemMeta.setItemModel(NamespacedKey.minecraft((String)("custom_" + customModelData)));
        return this;
    }

    public ItemStackBuilder setAmount(int amount) {
        this.itemStack.setAmount(Math.max(1, Math.min(64, amount)));
        return this;
    }

    public ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }

    public static Map.Entry<Enchantment, Integer> parseEnchantment(String enchantString) {
        if (enchantString == null || enchantString.isEmpty()) {
            return null;
        }
        try {
            String[] parts = enchantString.split(":");
            String enchantKey = parts[0].toUpperCase();
            int level = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
            Enchantment enchantment = (Enchantment)RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft((String)enchantKey.toLowerCase()));
            if (enchantment != null) {
                return Map.entry(enchantment, level);
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return null;
    }

    public static Map<Enchantment, Integer> parseEnchantments(List<String> enchantStrings) {
        HashMap<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
        if (enchantStrings != null) {
            for (String enchantString : enchantStrings) {
                Map.Entry<Enchantment, Integer> entry = ItemStackBuilder.parseEnchantment(enchantString);
                if (entry == null) continue;
                enchantments.put(entry.getKey(), entry.getValue());
            }
        }
        return enchantments;
    }

    public static ItemFlag parseFlag(String flagString) {
        if (flagString == null || flagString.isEmpty()) {
            return null;
        }
        try {
            return ItemFlag.valueOf((String)flagString.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static List<ItemFlag> parseFlags(List<String> flagStrings) {
        ArrayList<ItemFlag> flags = new ArrayList<ItemFlag>();
        if (flagStrings != null) {
            for (String flagString : flagStrings) {
                ItemFlag flag = ItemStackBuilder.parseFlag(flagString);
                if (flag == null) continue;
                flags.add(flag);
            }
        }
        return flags;
    }
}

