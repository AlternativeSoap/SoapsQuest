/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 */
package com.soaps.quest.conditions.types;

import com.soaps.quest.conditions.ConditionResult;
import com.soaps.quest.conditions.QuestCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class WorldRequirementCondition
implements QuestCondition {
    private final List<String> allowedWorlds;

    public WorldRequirementCondition(List<String> allowedWorlds) {
        this.allowedWorlds = new ArrayList<String>(allowedWorlds);
    }

    @Override
    public String getType() {
        return "world";
    }

    @Override
    public ConditionResult check(Player player, boolean consumeResources) {
        String currentWorld = player.getWorld().getName();
        if (!this.allowedWorlds.contains(currentWorld)) {
            return ConditionResult.failure("&cYou must be in the correct world!");
        }
        return ConditionResult.success();
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", (Object)"world");
        section.set("world", this.allowedWorlds);
    }

    @Override
    public String getDescription() {
        if (this.allowedWorlds.size() == 1) {
            return "Must be in world: " + this.allowedWorlds.get(0);
        }
        return "Must be in worlds: " + String.join((CharSequence)", ", this.allowedWorlds);
    }

    @Override
    public String getDisplayString() {
        if (this.allowedWorlds.size() == 1) {
            return "&7World: &f" + this.allowedWorlds.get(0);
        }
        return "&7Worlds: &f" + String.join((CharSequence)", ", this.allowedWorlds);
    }

    public static WorldRequirementCondition deserialize(ConfigurationSection section) {
        String world;
        List<String> worlds = section.getStringList("world");
        if (worlds.isEmpty() && (world = section.getString("world")) != null && !world.isEmpty()) {
            worlds = List.of(world);
        }
        return new WorldRequirementCondition(worlds);
    }

    public List<String> getAllowedWorlds() {
        return new ArrayList<String>(this.allowedWorlds);
    }
}

