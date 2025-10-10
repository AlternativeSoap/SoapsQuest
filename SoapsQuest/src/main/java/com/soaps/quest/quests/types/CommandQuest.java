package com.soaps.quest.quests.types;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestType;

/**
 * Quest type triggered by commands or custom events.
 * Progress is manually incremented via plugin API.
 */
public class CommandQuest extends Quest {
    
    /**
     * Constructor for CommandQuest.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of times command/event must trigger
     */
    public CommandQuest(String questId, String display, int requiredAmount) {
        this(questId, display, requiredAmount, null);
    }
    
    /**
     * Constructor for CommandQuest with custom lore.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of actions required
     * @param customLore Custom lore from config (null to use default)
     */
    public CommandQuest(String questId, String display, int requiredAmount, List<String> customLore) {
        this(questId, display, requiredAmount, customLore, Material.PAPER);
    }
    
    /**
     * Constructor for CommandQuest with custom lore and material.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of actions required
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     */
    public CommandQuest(String questId, String display, int requiredAmount, List<String> customLore, Material material) {
        this(questId, display, requiredAmount, customLore, material, true);
    }
    
    /**
     * Constructor for CommandQuest with all parameters.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of actions required
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     * @param lockToPlayer Whether quest is locked to first player who makes progress
     */
    public CommandQuest(String questId, String display, int requiredAmount, List<String> customLore, Material material, boolean lockToPlayer) {
        this(questId, display, requiredAmount, customLore, material, lockToPlayer, null);
    }
    
    /**
     * Constructor for CommandQuest with all parameters including permission.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of actions required
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     * @param lockToPlayer Whether quest is locked to first player who makes progress
     * @param permission Optional permission node required for this quest
     */
    public CommandQuest(String questId, String display, int requiredAmount, List<String> customLore, Material material, boolean lockToPlayer, String permission) {
        super(questId, QuestType.COMMAND, display, requiredAmount, customLore, material, lockToPlayer, permission);
    }
    
    /**
     * Constructor with tier and milestones.
     */
    public CommandQuest(String questId, String display, int requiredAmount, List<String> customLore, Material material, boolean lockToPlayer, String permission, com.soaps.quest.quests.QuestTier tier, List<Integer> milestones) {
        super(questId, QuestType.COMMAND, display, requiredAmount, customLore, material, lockToPlayer, permission, tier, milestones);
    }
    
    @Override
    public boolean checkProgress(Player player, Object context) {
        // Command quests are manually triggered, always return true when called
        return true;
    }
    
    @Override
    public String getObjectiveDescription() {
        return "Complete " + requiredAmount + " actions";
    }
}
