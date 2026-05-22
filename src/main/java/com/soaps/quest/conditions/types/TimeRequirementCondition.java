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

public class TimeRequirementCondition
implements QuestCondition {
    private final TimeType timeType;

    public TimeRequirementCondition(TimeType timeType) {
        this.timeType = timeType;
    }

    @Override
    public String getType() {
        return "time";
    }

    @Override
    public ConditionResult check(Player player, boolean consumeResources) {
        boolean isDay;
        if (this.timeType == TimeType.ANY) {
            return ConditionResult.success();
        }
        long worldTime = player.getWorld().getTime();
        boolean bl = isDay = worldTime >= 0L && worldTime < 13000L;
        if (this.timeType == TimeType.DAY && !isDay) {
            return ConditionResult.failure("&cThis quest is only available during the day!");
        }
        if (this.timeType == TimeType.NIGHT && isDay) {
            return ConditionResult.failure("&cThis quest is only available at night!");
        }
        return ConditionResult.success();
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", (Object)this.getType());
        section.set("time", (Object)this.timeType.name());
    }

    @Override
    public String getDescription() {
        return switch (this.timeType.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> "Only available during day";
            case 1 -> "Only available during night";
            case 2 -> "Available any time";
        };
    }

    @Override
    public String getDisplayString() {
        return switch (this.timeType.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> "&7Time: &eDay only";
            case 1 -> "&7Time: &9Night only";
            case 2 -> "&7Time: &fAny time";
        };
    }

    public static TimeRequirementCondition deserialize(ConfigurationSection section) {
        TimeType timeType;
        String timeStr = section.getString("time");
        if (timeStr == null || timeStr.isEmpty()) {
            timeType = TimeType.ANY;
        } else {
            try {
                timeType = TimeType.valueOf(timeStr.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                timeType = TimeType.ANY;
            }
        }
        return new TimeRequirementCondition(timeType);
    }

    public static enum TimeType {
        DAY,
        NIGHT,
        ANY;

    }
}

