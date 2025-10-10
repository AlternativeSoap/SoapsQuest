package com.soaps.quest.rewards;

import java.util.Map;

/**
 * Represents a unified reward entry with type and data
 */
public class RewardEntry {
    public enum RewardType {
        XP,
        MONEY,
        ITEM,
        COMMAND
    }
    
    private final RewardType type;
    private final Map<String, Object> data;
    
    public RewardEntry(RewardType type, Map<String, Object> data) {
        this.type = type;
        this.data = data;
    }
    
    public RewardType getType() {
        return type;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    /**
     * Get a string representation for display purposes
     */
    public String getDisplayString() {
        return switch (type) {
            case XP -> "XP: " + data.get("amount");
            case MONEY -> "Money: $" + data.get("amount");
            case ITEM -> "Item: " + data.get("material") + " x" + data.get("amount");
            case COMMAND -> "Command: " + data.get("command");
            default -> "Unknown reward";
        };
    }
    
    /**
     * Convert reward entry to config format
     */
    public Map<String, Object> toConfigFormat() {
        return Map.of(
            "type", type.toString().toLowerCase(),
            "data", data
        );
    }
    
    /**
     * Create reward entry from config data
     */
    public static RewardEntry fromConfig(Map<String, Object> config) {
        String typeStr = (String) config.get("type");
        RewardType type = RewardType.valueOf(typeStr.toUpperCase());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) config.get("data");
        return new RewardEntry(type, data);
    }
}