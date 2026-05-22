/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.soaps.quest.conditions.types;

import com.soaps.quest.conditions.ConditionResult;
import com.soaps.quest.conditions.QuestCondition;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemRequirementCondition
implements QuestCondition {
    private final Map<Material, Integer> requiredItems;
    private final boolean consumeOnAccept;

    public ItemRequirementCondition(Map<Material, Integer> requiredItems, boolean consumeOnAccept) {
        this.requiredItems = requiredItems;
        this.consumeOnAccept = consumeOnAccept;
    }

    public ItemRequirementCondition(Material material, int amount, boolean consumeOnAccept) {
        this.requiredItems = new HashMap<Material, Integer>();
        this.requiredItems.put(material, amount);
        this.consumeOnAccept = consumeOnAccept;
    }

    @Override
    public String getType() {
        return this.consumeOnAccept ? "item-cost" : "item";
    }

    @Override
    public ConditionResult check(Player player, boolean consumeResources) {
        Material material;
        for (Map.Entry<Material, Integer> entry : this.requiredItems.entrySet()) {
            material = entry.getKey();
            int required = entry.getValue();
            int count = 0;
            ItemStack[] contents = player.getInventory().getContents();
            if (contents != null) {
                ItemStack[] itemStackArray = contents;
                int n = itemStackArray.length;
                for (int i = 0; i < n; ++i) {
                    ItemStack item = itemStackArray[i];
                    if (item == null || item.getType() != material) continue;
                    count += item.getAmount();
                }
            }
            if (count >= required) continue;
            String itemName = this.formatName(material.name());
            if (this.consumeOnAccept) {
                return ConditionResult.failure(String.format("&cThis quest costs %d %s!", required, itemName));
            }
            return ConditionResult.failure(String.format("&cYou need %d %s! (You have: %d)", required, itemName, count));
        }
        if (this.consumeOnAccept && consumeResources) {
            for (Map.Entry<Material, Integer> entry : this.requiredItems.entrySet()) {
                material = entry.getKey();
                int remaining = entry.getValue();
                ItemStack[] consumeContents = player.getInventory().getContents();
                if (consumeContents == null) continue;
                for (ItemStack item : consumeContents) {
                    if (item == null || item.getType() != material || remaining <= 0) continue;
                    int toRemove = Math.min(item.getAmount(), remaining);
                    item.setAmount(item.getAmount() - toRemove);
                    remaining -= toRemove;
                }
            }
        }
        return ConditionResult.success();
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", (Object)this.getType());
        StringBuilder itemStr = new StringBuilder();
        int index = 0;
        for (Map.Entry<Material, Integer> entry : this.requiredItems.entrySet()) {
            if (index > 0) {
                itemStr.append(",");
            }
            itemStr.append(entry.getKey().name()).append(":").append(entry.getValue());
            ++index;
        }
        section.set("item", (Object)itemStr.toString());
        if (this.consumeOnAccept) {
            section.set("consume-item", (Object)true);
        }
    }

    @Override
    public String getDescription() {
        if (this.requiredItems.size() == 1) {
            Map.Entry<Material, Integer> entry = this.requiredItems.entrySet().iterator().next();
            String itemName = this.formatName(entry.getKey().name());
            if (this.consumeOnAccept) {
                return "Costs: " + String.valueOf(entry.getValue()) + " " + itemName;
            }
            return "Requires: " + String.valueOf(entry.getValue()) + " " + itemName;
        }
        if (this.consumeOnAccept) {
            return "Costs " + this.requiredItems.size() + " different items";
        }
        return "Requires " + this.requiredItems.size() + " different items";
    }

    @Override
    public String getDisplayString() {
        StringBuilder display = new StringBuilder();
        if (this.consumeOnAccept) {
            display.append("&7Item Cost:\n");
        } else {
            display.append("&7Required Items:\n");
        }
        for (Map.Entry<Material, Integer> entry : this.requiredItems.entrySet()) {
            String itemName = this.formatName(entry.getKey().name());
            display.append("&f  \u2022 ").append(entry.getValue()).append("x ").append(itemName).append("\n");
        }
        return display.toString().trim();
    }

    private String formatName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        String[] parts = name.toLowerCase().split("_");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length; ++i) {
            if (i > 0) {
                result.append(" ");
            }
            if (parts[i].isEmpty()) continue;
            result.append(parts[i].substring(0, 1).toUpperCase()).append(parts[i].substring(1));
        }
        return result.toString();
    }

    public static ItemRequirementCondition deserialize(ConfigurationSection section) {
        String[] itemEntries;
        String[] stringArray;
        String itemStr = section.getString("item");
        if (itemStr == null || !itemStr.contains(":")) {
            return null;
        }
        boolean consume = section.getBoolean("consume-item", false);
        HashMap<Material, Integer> items = new HashMap<Material, Integer>();
        if (itemStr.contains(",")) {
            stringArray = itemStr.split(",");
        } else {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = itemStr;
        }
        for (String entry : itemEntries = stringArray) {
            if (!(entry = entry.trim()).contains(":")) continue;
            String[] parts = entry.split(":");
            try {
                Material material = Material.valueOf((String)parts[0].toUpperCase());
                int amount = Integer.parseInt(parts[1]);
                items.put(material, amount);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        if (items.isEmpty()) {
            return null;
        }
        return new ItemRequirementCondition(items, consume);
    }

    public Map<Material, Integer> getRequiredItems() {
        return new HashMap<Material, Integer>(this.requiredItems);
    }

    public Material getMaterial() {
        if (this.requiredItems.size() == 1) {
            return this.requiredItems.keySet().iterator().next();
        }
        return null;
    }

    public int getAmount() {
        if (this.requiredItems.size() == 1) {
            return this.requiredItems.values().iterator().next();
        }
        return 0;
    }

    public boolean isConsumable() {
        return this.consumeOnAccept;
    }
}

