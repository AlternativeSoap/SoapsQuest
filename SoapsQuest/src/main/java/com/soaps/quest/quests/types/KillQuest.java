package com.soaps.quest.quests.types;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.WaterMob;

import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestType;

/**
 * Quest type for killing a specific number of entities.
 * Supports specific entity types, or special values: ANY, HOSTILE, PASSIVE
 */
public class KillQuest extends Quest {
    
    private final EntityType entityType; // null for ANY/HOSTILE/PASSIVE
    private final EntityFilter filter;
    
    /**
     * Entity filter type for special matching
     */
    public enum EntityFilter {
        SPECIFIC,  // Match specific entity type
        ANY,       // Match any entity
        HOSTILE,   // Match hostile mobs (monsters)
        PASSIVE    // Match passive mobs (animals, water mobs, ambient)
    }
    
    /**
     * Constructor for KillQuest.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of entities to kill
     * @param entityType Type of entity to kill
     */
    public KillQuest(String questId, String display, int requiredAmount, EntityType entityType) {
        this(questId, display, requiredAmount, entityType, null, Material.PAPER, true, EntityFilter.SPECIFIC, null);
    }
    
    /**
     * Constructor for KillQuest with custom lore.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of entities to kill
     * @param entityType Type of entity to kill
     * @param customLore Custom lore from config (null to use default)
     */
    public KillQuest(String questId, String display, int requiredAmount, EntityType entityType, List<String> customLore) {
        this(questId, display, requiredAmount, entityType, customLore, Material.PAPER, true, EntityFilter.SPECIFIC, null);
    }
    
    /**
     * Constructor for KillQuest with custom lore and material.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of entities to kill
     * @param entityType Type of entity to kill
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     */
    public KillQuest(String questId, String display, int requiredAmount, EntityType entityType, List<String> customLore, Material material) {
        this(questId, display, requiredAmount, entityType, customLore, material, true, EntityFilter.SPECIFIC, null);
    }
    
    /**
     * Constructor for KillQuest with all parameters.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of entities to kill
     * @param entityType Type of entity to kill (null if using filter)
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     * @param lockToPlayer Whether quest is locked to first player who makes progress
     * @param filter Entity filter type (SPECIFIC, ANY, HOSTILE, PASSIVE)
     * @param permission Optional permission node required for this quest
     */
    public KillQuest(String questId, String display, int requiredAmount, EntityType entityType, List<String> customLore, Material material, boolean lockToPlayer, EntityFilter filter, String permission) {
        super(questId, QuestType.KILL, display, requiredAmount, customLore, material, lockToPlayer, permission);
        this.entityType = entityType;
        this.filter = filter;
    }
    
    /**
     * Constructor for KillQuest with tier and milestones.
     * 
     * @param questId Unique quest identifier
     * @param display Display name
     * @param requiredAmount Number of entities to kill
     * @param entityType Type of entity to kill (null if using filter)
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     * @param lockToPlayer Whether quest is locked to first player who makes progress
     * @param filter Entity filter type (SPECIFIC, ANY, HOSTILE, PASSIVE)
     * @param permission Optional permission node required for this quest
     * @param tier Quest tier/rarity
     * @param milestones Custom progress milestones
     */
    public KillQuest(String questId, String display, int requiredAmount, EntityType entityType, List<String> customLore, Material material, boolean lockToPlayer, EntityFilter filter, String permission, com.soaps.quest.quests.QuestTier tier, List<Integer> milestones) {
        super(questId, QuestType.KILL, display, requiredAmount, customLore, material, lockToPlayer, permission, tier, milestones);
        this.entityType = entityType;
        this.filter = filter;
    }
    
    /**
     * Get the entity type required for this quest.
     * 
     * @return Entity type (null for ANY/HOSTILE/PASSIVE)
     */
    public EntityType getEntityType() {
        return entityType;
    }
    
    /**
     * Get the entity filter for this quest.
     * 
     * @return Entity filter
     */
    public EntityFilter getFilter() {
        return filter;
    }
    
    @Override
    public boolean checkProgress(Player player, Object context) {
        // Context should be a LivingEntity from the death event
        if (context instanceof LivingEntity entity) {
            return switch (filter) {
                case SPECIFIC -> entity.getType() == entityType;
                case ANY -> true; // Any entity counts
                case HOSTILE -> entity instanceof Monster; // Hostile mobs
                case PASSIVE -> entity instanceof Animals || entity instanceof WaterMob || entity instanceof Ambient; // Passive mobs
            };
        }
        // Legacy support: Context as EntityType
        if (context instanceof EntityType legacyEntityType) {
            return filter == EntityFilter.SPECIFIC && legacyEntityType == this.entityType;
        }
        return false;
    }
    
    @Override
    public String getObjectiveDescription() {
        return switch (filter) {
            case SPECIFIC -> "Kill " + requiredAmount + " " + entityType.name();
            case ANY -> "Kill " + requiredAmount + " entities";
            case HOSTILE -> "Kill " + requiredAmount + " hostile mobs";
            case PASSIVE -> "Kill " + requiredAmount + " passive mobs";
        };
    }
}
