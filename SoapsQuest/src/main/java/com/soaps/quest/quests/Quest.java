package com.soaps.quest.quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.soaps.quest.objectives.Objective;

/**
 * Abstract base class for all quest types.
 * Handles common quest properties and progress tracking logic.
 */
public abstract class Quest {
    
    protected final String questId;
    protected final QuestType type;
    protected final String display;
    protected final int requiredAmount;
    protected final Map<UUID, Integer> playerProgress;
    protected final List<String> customLore;
    protected final Material material;
    protected final boolean lockToPlayer;
    protected final List<Objective> objectives; // NEW: Support for multiple objectives
    protected final boolean sequential; // NEW: Support for sequential objectives
    protected final String permission; // NEW: Optional permission node required to access this quest
    protected final QuestTier tier; // NEW: Quest tier/rarity for visual customization
    protected final List<Integer> milestones; // NEW: Custom progress milestones (percentages)
    protected final String difficulty; // NEW: Quest difficulty for scaling (references config.yml)
    protected final org.bukkit.configuration.ConfigurationSection conditions; // NEW: Quest requirements/conditions
    
    /**
     * Constructor for base Quest class.
     * 
     * @param questId Unique identifier for this quest
     * @param type The type of quest
     * @param display Display name shown to players
     * @param requiredAmount Amount needed to complete the quest
     */
    public Quest(String questId, QuestType type, String display, int requiredAmount) {
        this(questId, type, display, requiredAmount, null, Material.PAPER, true, null);
    }
    
    /**
     * Constructor for base Quest class with custom lore.
     * 
     * @param questId Unique identifier for this quest
     * @param type The type of quest
     * @param display Display name shown to players
     * @param requiredAmount Amount needed to complete the quest
     * @param customLore Custom lore from config (null to use default)
     */
    public Quest(String questId, QuestType type, String display, int requiredAmount, List<String> customLore) {
        this(questId, type, display, requiredAmount, customLore, Material.PAPER, true, null);
    }
    
    /**
     * Constructor for base Quest class with custom lore and material.
     * 
     * @param questId Unique identifier for this quest
     * @param type The type of quest
     * @param display Display name shown to players
     * @param requiredAmount Amount needed to complete the quest
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     * @param lockToPlayer Whether quest is locked to the first player who makes progress
     * @param permission Optional permission node required for this quest (null for no restriction)
     */
    public Quest(String questId, QuestType type, String display, int requiredAmount, List<String> customLore, Material material, boolean lockToPlayer, String permission) {
        this(questId, type, display, requiredAmount, customLore, material, lockToPlayer, permission, QuestTier.COMMON, null);
    }
    
    /**
     * Full constructor for base Quest class with all options.
     * 
     * @param questId Unique identifier for this quest
     * @param type The type of quest
     * @param display Display name shown to players
     * @param requiredAmount Amount needed to complete the quest
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     * @param lockToPlayer Whether quest is locked to the first player who makes progress
     * @param permission Optional permission node required for this quest (null for no restriction)
     * @param tier Quest tier/rarity for visual customization
     * @param milestones Custom progress milestones (percentages)
     */
    public Quest(String questId, QuestType type, String display, int requiredAmount, List<String> customLore, Material material, boolean lockToPlayer, String permission, QuestTier tier, List<Integer> milestones) {
        this(questId, type, display, requiredAmount, customLore, material, lockToPlayer, permission, tier, milestones, "normal");
    }
    
    /**
     * Full constructor with difficulty support.
     */
    public Quest(String questId, QuestType type, String display, int requiredAmount, List<String> customLore, Material material, boolean lockToPlayer, String permission, QuestTier tier, List<Integer> milestones, String difficulty) {
        this.questId = questId;
        this.type = type;
        this.display = display;
        this.requiredAmount = requiredAmount;
        this.playerProgress = new HashMap<>();
        this.customLore = customLore;
        this.material = material != null ? material : Material.PAPER;
        this.lockToPlayer = lockToPlayer;
        this.objectives = new ArrayList<>(); // Empty by default for legacy quests
        this.sequential = false; // Legacy quests are not sequential
        this.permission = permission; // Optional permission requirement
        this.tier = tier != null ? tier : QuestTier.COMMON;
        this.milestones = milestones != null ? new ArrayList<>(milestones) : new ArrayList<>();
        this.difficulty = difficulty != null ? difficulty : "normal";
        this.conditions = null; // No conditions by default
    }
    
    /**
     * Constructor for Quest with multiple objectives (NEW).
     * 
     * @param questId Unique identifier for this quest
     * @param display Display name shown to players
     * @param objectives List of objectives for this quest
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     * @param lockToPlayer Whether quest is locked to the first player who makes progress
     * @param sequential Whether objectives must be completed in order
     * @param permission Optional permission node required for this quest (null for no restriction)
     */
    public Quest(String questId, String display, List<Objective> objectives, List<String> customLore, Material material, boolean lockToPlayer, boolean sequential, String permission) {
        this(questId, display, objectives, customLore, material, lockToPlayer, sequential, permission, QuestTier.COMMON, null);
    }
    
    /**
     * Full constructor for Quest with multiple objectives and all options.
     * 
     * @param questId Unique identifier for this quest
     * @param display Display name shown to players
     * @param objectives List of objectives for this quest
     * @param customLore Custom lore from config (null to use default)
     * @param material Material type for quest item (defaults to PAPER)
     * @param lockToPlayer Whether quest is locked to the first player who makes progress
     * @param sequential Whether objectives must be completed in order
     * @param permission Optional permission node required for this quest (null for no restriction)
     * @param tier Quest tier/rarity for visual customization
     * @param milestones Custom progress milestones (percentages)
     */
    public Quest(String questId, String display, List<Objective> objectives, List<String> customLore, Material material, boolean lockToPlayer, boolean sequential, String permission, QuestTier tier, List<Integer> milestones) {
        this(questId, display, objectives, customLore, material, lockToPlayer, sequential, permission, tier, milestones, "normal");
    }
    
    /**
     * Full constructor with difficulty support.
     */
    public Quest(String questId, String display, List<Objective> objectives, List<String> customLore, Material material, boolean lockToPlayer, boolean sequential, String permission, QuestTier tier, List<Integer> milestones, String difficulty) {
        this(questId, display, objectives, customLore, material, lockToPlayer, sequential, permission, tier, milestones, difficulty, null);
    }
    
    /**
     * Constructor for multi-objective quests with conditions support.
     *
     * @param questId Unique quest identifier
     * @param display Display name shown in quest items
     * @param objectives List of quest objectives
     * @param customLore Optional custom lore for the quest item
     * @param material Material for the quest paper
     * @param lockToPlayer Whether quest paper locks to first player who picks it up
     * @param sequential Whether objectives must be completed in order
     * @param permission Optional permission node required to start quest
     * @param tier Quest tier (determines visual effects)
     * @param milestones Custom milestone percentages for multi-objective quests
     * @param difficulty Quest difficulty label
     * @param conditions Quest conditions (requirements to start/claim)
     */
    public Quest(String questId, String display, List<Objective> objectives, List<String> customLore, Material material, boolean lockToPlayer, boolean sequential, String permission, QuestTier tier, List<Integer> milestones, String difficulty, ConfigurationSection conditions) {
        this.questId = questId;
        this.type = null; // No single type for multi-objective quests
        this.display = display;
        this.requiredAmount = 0; // Not used for multi-objective quests
        this.playerProgress = new HashMap<>();
        this.customLore = customLore;
        this.material = material != null ? material : Material.PAPER;
        this.lockToPlayer = lockToPlayer;
        this.objectives = objectives != null ? new ArrayList<>(objectives) : new ArrayList<>();
        this.sequential = sequential;
        this.permission = permission; // Optional permission requirement
        this.tier = tier != null ? tier : QuestTier.COMMON;
        this.milestones = milestones != null ? new ArrayList<>(milestones) : new ArrayList<>();
        this.difficulty = difficulty != null ? difficulty : "normal";
        this.conditions = conditions; // Quest conditions
    }
    
    /**
     * Get the unique quest identifier.
     * 
     * @return Quest ID
     */
    public String getQuestId() {
        return questId;
    }
    
    /**
     * Get the quest type.
     * 
     * @return Quest type
     */
    public QuestType getType() {
        return type;
    }
    
    /**
     * Get the display name of the quest.
     * 
     * @return Display name
     */
    public String getDisplay() {
        return display;
    }
    
    /**
     * Get the required amount to complete the quest.
     * 
     * @return Required amount
     */
    public int getRequiredAmount() {
        return requiredAmount;
    }
    
    /**
     * Get the custom lore for this quest.
     * 
     * @return Custom lore list, or null if using default
     */
    public List<String> getCustomLore() {
        return customLore;
    }
    
    /**
     * Get the material type for this quest item.
     * 
     * @return Material type (defaults to PAPER)
     */
    public Material getMaterial() {
        return material;
    }
    
    /**
     * Check if this quest is locked to the player who starts it.
     * 
     * @return True if quest is owner-locked, false if transferable
     */
    public boolean isLockToPlayer() {
        return lockToPlayer;
    }
    
    /**
     * Get the current progress for a player.
     * 
     * @deprecated This uses legacy per-player tracking. Use QuestProgress.getCurrentProgress() instead.
     * @param player The player
     * @return Current progress amount (always 0 in multi-paper system)
     */
    @Deprecated
    public int getProgress(Player player) {
        return playerProgress.getOrDefault(player.getUniqueId(), 0);
    }
    
    /**
     * Set the progress for a player.
     * 
     * @deprecated This uses legacy per-player tracking. Use QuestProgress.setProgress() instead.
     * @param player The player
     * @param amount The progress amount
     */
    @Deprecated
    public void setProgress(Player player, int amount) {
        playerProgress.put(player.getUniqueId(), Math.min(amount, requiredAmount));
    }
    
    /**
     * Set progress for a player by UUID.
     * Used when loading data from storage.
     * 
     * @param uuid Player UUID
     * @param amount The progress amount
     */
    public void setProgress(UUID uuid, int amount) {
        playerProgress.put(uuid, Math.min(amount, requiredAmount));
    }
    
    /**
     * Increment the progress for a player by 1.
     * 
     * @deprecated This uses legacy per-player tracking. Use QuestProgress.incrementProgress() instead.
     * @param player The player
     */
    @Deprecated
    public void incrementProgress(Player player) {
        incrementProgress(player, 1);
    }
    
    /**
     * Increment the progress for a player by a specific amount.
     * 
     * @param player The player
     * @param amount Amount to increment by
     */
    public void incrementProgress(Player player, int amount) {
        int current = getProgress(player);
        setProgress(player, current + amount);
    }
    
    /**
     * Check if the player has completed this quest.
     * 
     * @deprecated This uses legacy per-player tracking. Use QuestProgress.isComplete() instead.
     * @param player The player
     * @return True if quest is complete (always false in multi-paper system)
     */
    @Deprecated
    public boolean isComplete(Player player) {
        return getProgress(player) >= requiredAmount;
    }
    
    /**
     * Check if the quest progress should be tracked for a specific event.
     * This method should be implemented by subclasses to validate
     * if the event matches the quest requirements.
     * 
     * @param player The player
     * @param context Additional context (entity type, block type, etc.)
     * @return True if progress should be incremented
     */
    public abstract boolean checkProgress(Player player, Object context);
    
    /**
     * Get the progress display string (e.g., "5/10").
     * 
     * @deprecated This uses legacy per-player tracking. Use QuestProgress for accurate display.
     * @param player The player
     * @return Progress string
     */
    @Deprecated
    public String getProgressDisplay(Player player) {
        return getProgress(player) + "/" + requiredAmount;
    }
    
    /**
     * Reset progress for a player.
     * 
     * @deprecated This uses legacy per-player tracking. Not needed in multi-paper system.
     * @param player The player
     */
    @Deprecated
    public void resetProgress(Player player) {
        playerProgress.remove(player.getUniqueId());
    }
    
    /**
     * Get all player progress data.
     * 
     * @deprecated Not used in multi-paper system. Progress is tracked in DataManager.
     * @return Map of UUID to progress amount (empty in multi-paper system)
     */
    @Deprecated
    public Map<UUID, Integer> getAllProgress() {
        return new HashMap<>(playerProgress);
    }
    
    /**
     * Get the objective description for this quest.
     * Should return a detailed description like "Place 50 STONE" or "Kill 10 ZOMBIE".
     * Must be implemented by subclasses.
     * 
     * @return Objective description string
     */
    public abstract String getObjectiveDescription();
    
    /**
     * Get the list of objectives for this quest.
     * For legacy quests, this will be empty.
     * For new multi-objective quests, this contains all objectives.
     * 
     * @return List of objectives
     */
    public List<Objective> getObjectives() {
        return new ArrayList<>(objectives);
    }
    
    /**
     * Add an objective to this quest.
     * 
     * @param objective Objective to add
     */
    public void addObjective(Objective objective) {
        this.objectives.add(objective);
    }
    
    /**
     * Check if this quest uses the new multi-objective system.
     * 
     * @return True if quest has objectives configured
     */
    public boolean hasObjectives() {
        return !objectives.isEmpty();
    }
    
    /**
     * Check if this quest has sequential objectives.
     * 
     * @return True if objectives must be completed in order
     */
    public boolean isSequential() {
        return sequential;
    }
    
    /**
     * Get the permission node required for this quest.
     * 
     * @return Permission node string, or null if no permission required
     */
    public String getPermission() {
        return permission;
    }
    
    /**
     * Check if this quest requires a permission to access.
     * 
     * @return True if a permission is required
     */
    public boolean hasPermissionRequirement() {
        return permission != null && !permission.isEmpty();
    }
    
    /**
     * Check if a player has permission to access this quest.
     * Returns true if no permission is required or if player has the permission.
     * 
     * @param player The player to check
     * @return True if player can access this quest
     */
    public boolean hasPermission(Player player) {
        // No permission requirement - always allow
        if (!hasPermissionRequirement()) {
            return true;
        }
        // Check if player has the required permission
        return player.hasPermission(permission);
    }
    
    /**
     * Get the tier/rarity of this quest.
     * 
     * @return Quest tier
     */
    public QuestTier getTier() {
        return tier;
    }
    
    /**
     * Get the custom progress milestones for this quest.
     * 
     * @return List of milestone percentages
     */
    public List<Integer> getMilestones() {
        return new ArrayList<>(milestones);
    }
    
    /**
     * Get the difficulty level for this quest.
     * 
     * @return Difficulty name (references config.yml)
     */
    public String getDifficulty() {
        return difficulty;
    }
    
    /**
     * Get quest conditions configuration section
     * 
     * @return Conditions section or null if no conditions
     */
    public org.bukkit.configuration.ConfigurationSection getConditions() {
        return conditions;
    }
    
    /**
     * Check if this quest has custom milestones configured.
     * 
     * @return True if milestones are configured
     */
    public boolean hasMilestones() {
        return milestones != null && !milestones.isEmpty();
    }
}
