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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PermissionRequirementCondition
implements QuestCondition {
    private final String permission;

    public PermissionRequirementCondition(String permission) {
        this.permission = permission;
    }

    @Override
    public String getType() {
        return "permission";
    }

    @Override
    public ConditionResult check(Player player, boolean consumeResources) {
        if (!player.hasPermission(this.permission)) {
            return ConditionResult.failure("&cYou don't have permission for this quest!");
        }
        return ConditionResult.success();
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", (Object)"permission");
        section.set("permission", (Object)this.permission);
    }

    @Override
    public String getDescription() {
        return "Requires permission: " + this.permission;
    }

    @Override
    public String getDisplayString() {
        return "&7Permission: &f" + this.permission;
    }

    public static PermissionRequirementCondition deserialize(ConfigurationSection section) {
        String permission = section.getString("permission", "");
        return new PermissionRequirementCondition(permission);
    }

    public String getPermission() {
        return this.permission;
    }
}

