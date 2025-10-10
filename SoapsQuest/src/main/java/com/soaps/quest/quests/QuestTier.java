package com.soaps.quest.quests;

/**
 * Represents the tier/rarity of a quest.
 * Tiers are purely visual and affect the quest paper's display name and lore color.
 */
public enum QuestTier {
    COMMON("&f", ""),
    RARE("&9", "&9[RARE] "),
    EPIC("&d", "&d[EPIC] "),
    LEGENDARY("&6", "&6[LEGENDARY] ");
    
    private final String colorCode;
    private final String prefix;
    
    QuestTier(String colorCode, String prefix) {
        this.colorCode = colorCode;
        this.prefix = prefix;
    }
    
    /**
     * Get the color code associated with this tier.
     * 
     * @return Color code string for this tier (e.g., "&9")
     */
    public String getColor() {
        return colorCode;
    }
    
    /**
     * Get the colored prefix for this tier.
     * 
     * @return Colored prefix string (e.g., "&9[RARE] ")
     */
    public String getPrefix() {
        return prefix;
    }
    
    /**
     * Get the color code for this tier.
     * 
     * @return Color code string (e.g., "&9")
     */
    public String getColorCode() {
        return colorCode;
    }
    
    /**
     * Parse a tier from a string name.
     * 
     * @param name The tier name (case-insensitive)
     * @return The matching QuestTier, or COMMON if not found
     */
    public static QuestTier fromString(String name) {
        if (name == null || name.isEmpty()) {
            return COMMON;
        }
        
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return COMMON;
        }
    }
}
