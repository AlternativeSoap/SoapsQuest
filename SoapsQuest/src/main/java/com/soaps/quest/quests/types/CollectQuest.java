package com.soaps.quest.quests.types;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestType;

/**
 * Quest type for collecting a specific number of items.
 */
public class CollectQuest extends Quest {
    
    private final Material itemType;
    
    /**
     * Constructor for CollectQuest.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of items to collect
     * @param itemType Type of item to collect
     */
    public CollectQuest(String questId, String display, int requiredAmount, Material itemType) {
        this(questId, display, requiredAmount, itemType, null);
    }
    
    /**
     * Constructor for CollectQuest with custom lore.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of items to collect
     * @param itemType Type of item to collect
     * @param customLore Custom lore from config (null to use default)
     */
    public CollectQuest(String questId, String display, int requiredAmount, Material itemType, List<String> customLore) {
        this(questId, display, requiredAmount, itemType, customLore, Material.PAPER);
    }
    
    /**
     * Constructor for CollectQuest with custom lore and material.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of items to collect
     * @param itemType Type of item to collect
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     */
    public CollectQuest(String questId, String display, int requiredAmount, Material itemType, List<String> customLore, Material material) {
        this(questId, display, requiredAmount, itemType, customLore, material, true);
    }
    
    /**
     * Constructor for CollectQuest with all parameters.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of items to collect
     * @param itemType Type of item to collect
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     * @param lockToPlayer Whether quest is locked to first player who makes progress
     */
    public CollectQuest(String questId, String display, int requiredAmount, Material itemType, List<String> customLore, Material material, boolean lockToPlayer) {
        this(questId, display, requiredAmount, itemType, customLore, material, lockToPlayer, null);
    }
    
    /**
     * Constructor for CollectQuest with all parameters including permission.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of items to collect
     * @param itemType Type of item to collect
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     * @param lockToPlayer Whether quest is locked to first player who makes progress
     * @param permission Optional permission node required for this quest
     */
    public CollectQuest(String questId, String display, int requiredAmount, Material itemType, List<String> customLore, Material material, boolean lockToPlayer, String permission) {
        super(questId, QuestType.COLLECT, display, requiredAmount, customLore, material, lockToPlayer, permission);
        this.itemType = itemType;
    }
    
    /**
     * Constructor with tier and milestones.
     */
    public CollectQuest(String questId, String display, int requiredAmount, Material itemType, List<String> customLore, Material material, boolean lockToPlayer, String permission, com.soaps.quest.quests.QuestTier tier, List<Integer> milestones) {
        super(questId, QuestType.COLLECT, display, requiredAmount, customLore, material, lockToPlayer, permission, tier, milestones);
        this.itemType = itemType;
    }
    
    /**
     * Get the item type required for this quest.
     * 
     * @return Item material
     */
    public Material getItemType() {
        return itemType;
    }
    
    @Override
    public boolean checkProgress(Player player, Object context) {
        // Context should be a Material from the pickup event
        if (context instanceof Material) {
            return context == this.itemType;
        }
        return false;
    }
    
    @Override
    public String getObjectiveDescription() {
        return "Collect " + requiredAmount + " " + itemType.name();
    }
}
