package com.soaps.quest.quests.types;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestType;

/**
 * Quest type based on PlaceholderAPI numeric values.
 * Checks if a placeholder reaches a certain numeric threshold.
 */
public class PlaceholderQuest extends Quest {
    
    private final String placeholder;
    
    /**
     * Constructor for PlaceholderQuest.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Target numeric value
     * @param placeholder PlaceholderAPI placeholder (e.g., %player_level%)
     */
    public PlaceholderQuest(String questId, String display, int requiredAmount, String placeholder) {
        this(questId, display, requiredAmount, placeholder, null);
    }
    
    /**
     * Constructor for PlaceholderQuest with custom lore.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Target numeric value
     * @param placeholder PlaceholderAPI placeholder (e.g., %player_level%)
     * @param customLore Custom lore from config (null to use default)
     */
    public PlaceholderQuest(String questId, String display, int requiredAmount, String placeholder, List<String> customLore) {
        this(questId, display, requiredAmount, placeholder, customLore, Material.PAPER);
    }
    
    /**
     * Constructor for PlaceholderQuest with custom lore and material.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Target numeric value
     * @param placeholder PlaceholderAPI placeholder (e.g., %player_level%)
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     */
    public PlaceholderQuest(String questId, String display, int requiredAmount, String placeholder, List<String> customLore, Material material) {
        this(questId, display, requiredAmount, placeholder, customLore, material, true);
    }
    
    /**
     * Constructor for PlaceholderQuest with all parameters.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Target numeric value
     * @param placeholder PlaceholderAPI placeholder (e.g., %player_level%)
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     * @param lockToPlayer Whether quest is locked to first player who makes progress
     */
    public PlaceholderQuest(String questId, String display, int requiredAmount, String placeholder, List<String> customLore, Material material, boolean lockToPlayer) {
        this(questId, display, requiredAmount, placeholder, customLore, material, lockToPlayer, null);
    }
    
    /**
     * Constructor for PlaceholderQuest with all parameters including permission.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Target numeric value
     * @param placeholder PlaceholderAPI placeholder (e.g., %player_level%)
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     * @param lockToPlayer Whether quest is locked to first player who makes progress
     * @param permission Optional permission node required for this quest
     */
    public PlaceholderQuest(String questId, String display, int requiredAmount, String placeholder, List<String> customLore, Material material, boolean lockToPlayer, String permission) {
        super(questId, QuestType.PLACEHOLDER, display, requiredAmount, customLore, material, lockToPlayer, permission);
        this.placeholder = placeholder;
    }
    
    /**
     * Constructor with tier and milestones.
     */
    public PlaceholderQuest(String questId, String display, int requiredAmount, String placeholder, List<String> customLore, Material material, boolean lockToPlayer, String permission, com.soaps.quest.quests.QuestTier tier, List<Integer> milestones) {
        super(questId, QuestType.PLACEHOLDER, display, requiredAmount, customLore, material, lockToPlayer, permission, tier, milestones);
        this.placeholder = placeholder;
    }
    
    /**
     * Get the placeholder string.
     * 
     * @return Placeholder
     */
    public String getPlaceholder() {
        return placeholder;
    }
    
    @Override
    public boolean checkProgress(Player player, Object context) {
        // For placeholder quests, progress is checked periodically
        // Context would be the current value from PlaceholderAPI
        // Note: PlaceholderQuest doesn't work well with multi-paper system
        if (context instanceof Number number) {
            int value = number.intValue();
            setProgress(player.getUniqueId(), value);
            return true;
        }
        return false;
    }
    
    @Override
    public String getObjectiveDescription() {
        return "Reach " + requiredAmount + " " + placeholder;
    }
}
