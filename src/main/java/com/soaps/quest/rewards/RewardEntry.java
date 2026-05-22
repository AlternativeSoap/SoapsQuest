/*
 * Decompiled with CFR 0.152.
 */
package com.soaps.quest.rewards;

import java.util.Map;

public class RewardEntry {
    private final RewardType type;
    private final Map<String, Object> data;

    public RewardEntry(RewardType type, Map<String, Object> data) {
        this.type = type;
        this.data = data;
    }

    public RewardType getType() {
        return this.type;
    }

    public Map<String, Object> getData() {
        return this.data;
    }

    public String getDisplayString() {
        return switch (this.type) {
            case XP -> "XP: " + String.valueOf(this.data.get("amount"));
            case MONEY -> "Money: $" + String.valueOf(this.data.get("amount"));
            case SIGILS -> "Sigils: " + String.valueOf(this.data.get("amount"));
            case ITEM -> "Item: " + String.valueOf(this.data.get("material")) + " x" + String.valueOf(this.data.get("amount"));
            case COMMAND -> "Command: " + String.valueOf(this.data.get("command"));
            case QUEST -> "Quest: " + String.valueOf(this.data.get("quest-id")) + (String)(this.data.containsKey("chance") && (Integer)this.data.get("chance") < 100 ? " (" + String.valueOf(this.data.get("chance")) + "% chance)" : "");
        };
    }

    public Map<String, Object> toConfigFormat() {
        return Map.of("type", this.type.toString().toLowerCase(), "data", this.data);
    }

    public static RewardEntry fromConfig(Map<String, Object> config) {
        String typeStr = (String)config.get("type");
        RewardType type = RewardType.valueOf(typeStr.toUpperCase());
        Map data = (Map)config.get("data");
        return new RewardEntry(type, data);
    }

    public static enum RewardType {
        XP,
        MONEY,
        SIGILS,
        ITEM,
        COMMAND,
        QUEST;

    }
}

