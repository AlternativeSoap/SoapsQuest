package com.soaps.quest.quests.types;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestType;

/**
 * Quest type for breaking a specific number of blocks.
 */
public class BreakQuest extends Quest {
    
    private final Material blockType;
    
    /**
     * Constructor for BreakQuest.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of blocks to break
     * @param blockType Type of block to break
     */
    public BreakQuest(String questId, String display, int requiredAmount, Material blockType) {
        this(questId, display, requiredAmount, blockType, null);
    }
    
    /**
     * Constructor for BreakQuest with custom lore.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of blocks to break
     * @param blockType Type of block to break
     * @param customLore Custom lore from config (null to use default)
     */
    public BreakQuest(String questId, String display, int requiredAmount, Material blockType, List<String> customLore) {
        this(questId, display, requiredAmount, blockType, customLore, Material.PAPER);
    }
    
    /**
     * Constructor for BreakQuest with custom lore and material.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of blocks to break
     * @param blockType Type of block to break
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     */
    public BreakQuest(String questId, String display, int requiredAmount, Material blockType, List<String> customLore, Material material) {
        this(questId, display, requiredAmount, blockType, customLore, material, true);
    }
    
    /**
     * Constructor for BreakQuest with all parameters.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of blocks to break
     * @param blockType Type of block to break
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     * @param lockToPlayer Whether quest is locked to first player who makes progress
     */
    public BreakQuest(String questId, String display, int requiredAmount, Material blockType, List<String> customLore, Material material, boolean lockToPlayer) {
        this(questId, display, requiredAmount, blockType, customLore, material, lockToPlayer, null);
    }
    
    /**
     * Constructor for BreakQuest with all parameters including permission.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of blocks to break
     * @param blockType Type of block to break
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     * @param lockToPlayer Whether quest is locked to first player who makes progress
     * @param permission Optional permission node required for this quest
     */
    public BreakQuest(String questId, String display, int requiredAmount, Material blockType, List<String> customLore, Material material, boolean lockToPlayer, String permission) {
        super(questId, QuestType.BREAK, display, requiredAmount, customLore, material, lockToPlayer, permission);
        this.blockType = blockType;
    }
    
    /**
     * Constructor with tier and milestones.
     */
    public BreakQuest(String questId, String display, int requiredAmount, Material blockType, List<String> customLore, Material material, boolean lockToPlayer, String permission, com.soaps.quest.quests.QuestTier tier, List<Integer> milestones) {
        super(questId, QuestType.BREAK, display, requiredAmount, customLore, material, lockToPlayer, permission, tier, milestones);
        this.blockType = blockType;
    }
    
    /**
     * Get the block type required for this quest.
     * 
     * @return Block material
     */
    public Material getBlockType() {
        return blockType;
    }
    
    @Override
    public boolean checkProgress(Player player, Object context) {
        // Context should be a Material from the block break event
        if (context instanceof Material) {
            return context == this.blockType;
        }
        return false;
    }
    
    @Override
    public String getObjectiveDescription() {
        return "Break " + requiredAmount + " " + blockType.name();
    }
}
