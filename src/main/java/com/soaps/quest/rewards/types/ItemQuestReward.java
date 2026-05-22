/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.registry.RegistryAccess
 *  io.papermc.paper.registry.RegistryKey
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.World
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package com.soaps.quest.rewards.types;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.managers.CustomItemManager;
import com.soaps.quest.rewards.QuestReward;
import com.soaps.quest.utils.ColorUtil;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class ItemQuestReward
implements QuestReward {
    private final ItemStack item;
    private final int chance;
    private final String target;

    public ItemQuestReward(ItemStack item) {
        this(item, null, 100);
    }

    public ItemQuestReward(ItemStack item, int chance) {
        this(item, null, chance);
    }

    public ItemQuestReward(ItemStack item, String target, int chance) {
        this.item = item.clone();
        this.target = target;
        this.chance = Math.max(0, Math.min(100, chance));
    }

    public ItemQuestReward(Material material, int amount) {
        this(new ItemStack(material, amount), material.name(), 100);
    }

    public ItemQuestReward(Material material, int amount, int chance) {
        this(new ItemStack(material, amount), material.name(), chance);
    }

    public ItemQuestReward(Material material, int amount, String name, List<String> lore, Map<Enchantment, Integer> enchantments) {
        this(material, amount, name, lore, enchantments, 100);
    }

    public ItemQuestReward(Material material, int amount, String name, List<String> lore, Map<Enchantment, Integer> enchantments, int chance) {
        this.chance = Math.max(0, Math.min(100, chance));
        this.target = material.name();
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
    public String getType() {
        return "item";
    }

    @Override
    public boolean give(Player player) {
        World world;
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(new ItemStack[]{this.item.clone()});
            return true;
        }
        Location location = player.getLocation();
        if (location != null && (world = location.getWorld()) != null) {
            world.dropItem(location, this.item.clone());
        }
        return true;
    }

    @Override
    public String getDisplayDescription() {
        Component displayName;
        String itemName = this.item.getType().name();
        if (this.item.getItemMeta() != null && this.item.getItemMeta().hasDisplayName() && (displayName = this.item.getItemMeta().displayName()) != null) {
            itemName = LegacyComponentSerializer.legacyAmpersand().serialize(displayName);
        }
        String base = "&f" + this.item.getAmount() + "x &6" + itemName;
        if (this.chance < 100) {
            base = base + " &7(" + this.chance + "% chance)";
        }
        return base;
    }

    @Override
    public int getChance() {
        return this.chance;
    }

    @Override
    public void serialize(ConfigurationSection section) {
        ItemMeta meta;
        boolean isVanilla;
        section.set("type", (Object)"item");
        String targetValue = this.target != null ? this.target : this.item.getType().name();
        section.set("target", (Object)targetValue);
        section.set("amount", (Object)this.item.getAmount());
        boolean bl = isVanilla = targetValue != null && !targetValue.contains(":");
        if (isVanilla && (meta = this.item.getItemMeta()) != null) {
            List<Component> loreList;
            if (meta.hasDisplayName() && meta.displayName() != null) {
                section.set("name", (Object)LegacyComponentSerializer.legacyAmpersand().serialize(meta.displayName()));
            }
            if (meta.hasLore() && (loreList = meta.lore()) != null) {
                ArrayList<String> loreStrings = new ArrayList<String>();
                for (Component line : loreList) {
                    loreStrings.add(LegacyComponentSerializer.legacyAmpersand().serialize(line));
                }
                section.set("lore", loreStrings);
            }
            if (meta.hasEnchants()) {
                ArrayList<String> enchantList = new ArrayList<>();
                for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                    enchantList.add(entry.getKey().getKey().getKey().toUpperCase() + ":" + entry.getValue());
                }
                section.set("enchantments", enchantList);
            }
            if (meta.isUnbreakable()) {
                section.set("unbreakable", (Object)true);
            }
            if (!meta.getItemFlags().isEmpty()) {
                ArrayList<String> flags = new ArrayList<String>();
                for (ItemFlag flag : meta.getItemFlags()) {
                    flags.add(flag.name());
                }
                section.set("flags", flags);
            }
        }
        if (this.chance < 100) {
            section.set("chance", (Object)this.chance);
        }
    }

    public static ItemQuestReward deserialize(ConfigurationSection section) {
        HashMap<Enchantment, Integer> enchantments;
        List<String> lore;
        String name;
        Material material;
        int chance;
        int amount;
        block12: {
            ConfigurationSection enchantSection;
            block13: {
                if (section == null) {
                    return null;
                }
                Plugin plugin = Bukkit.getPluginManager().getPlugin("SoapsQuest");
                if (!(plugin instanceof SoapsQuest)) {
                    return null;
                }
                SoapsQuest soapsQuest = (SoapsQuest)plugin;
                String target = section.getString("target");
                if (target == null) {
                    target = section.getString("material");
                }
                if (target == null) {
                    return null;
                }
                amount = section.getInt("amount", 1);
                chance = section.getInt("chance", 100);
                CustomItemManager customItemManager = soapsQuest.getCustomItemManager();
                if (customItemManager.isPluginItem(target)) {
                    ItemStack item = customItemManager.parseCustomItem(target);
                    if (item == null) {
                        soapsQuest.getLogger().log(Level.WARNING, "Failed to parse plugin item: {0}", target);
                        return null;
                    }
                    item.setAmount(amount);
                    return new ItemQuestReward(item, target, chance);
                }
                try {
                    material = Material.valueOf((String)target.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    soapsQuest.getLogger().log(Level.WARNING, "Invalid material: {0}", target);
                    return null;
                }
                name = section.getString("name");
                lore = section.getStringList("lore");
                enchantments = new HashMap<Enchantment, Integer>();
                if (!section.contains("enchantments")) break block12;
                Object enchantObj = section.get("enchantments");
                if (!(enchantObj instanceof List)) break block13;
                List<String> enchantList = section.getStringList("enchantments");
                for (String enchantString : enchantList) {
                    String[] parts = enchantString.split(":");
                    if (parts.length != 2) continue;
                    try {
                        Enchantment enchant = (Enchantment)RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft((String)parts[0].toLowerCase()));
                        if (enchant == null) continue;
                        enchantments.put(enchant, Integer.valueOf(parts[1]));
                    }
                    catch (IllegalArgumentException | NullPointerException e) {
                        soapsQuest.getLogger().log(Level.WARNING, "Invalid enchantment: {0}", enchantString);
                    }
                }
                break block12;
            }
            if (!section.isConfigurationSection("enchantments") || (enchantSection = section.getConfigurationSection("enchantments")) == null) break block12;
            for (String key : enchantSection.getKeys(false)) {
                Enchantment enchant = (Enchantment)RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft((String)key));
                if (enchant == null) continue;
                enchantments.put(enchant, enchantSection.getInt(key));
            }
        }
        return new ItemQuestReward(material, amount, name, lore, enchantments, chance);
    }

    public ItemStack getItem() {
        return this.item.clone();
    }

    public String getTarget() {
        return this.target;
    }

    private Component parseColorCodes(String text) {
        return ColorUtil.colorize(text);
    }
}

