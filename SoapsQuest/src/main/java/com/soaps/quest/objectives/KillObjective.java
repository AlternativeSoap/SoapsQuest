package com.soaps.quest.objectives;

import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Objective for killing a specific number of entities.
 * Supports specific entity types, or special values: ANY, HOSTILE, PASSIVE
 */
public class KillObjective extends AbstractObjective {
    
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
     * Constructor for KillObjective with specific entity type.
     * 
     * @param objectiveId Unique identifier for this objective
     * @param entityType Type of entity to kill (null for ANY)
     * @param requiredAmount Number of entities to kill
     */
    public KillObjective(String objectiveId, EntityType entityType, int requiredAmount) {
        this(objectiveId, entityType, requiredAmount, EntityFilter.SPECIFIC);
    }
    
    /**
     * Constructor for KillObjective with entity filter.
     * 
     * @param objectiveId Unique identifier for this objective
     * @param entityType Type of entity to kill (null if using filter)
     * @param requiredAmount Number of entities to kill
     * @param filter Entity filter type
     */
    public KillObjective(String objectiveId, EntityType entityType, int requiredAmount, EntityFilter filter) {
        super(objectiveId, requiredAmount);
        this.entityType = entityType;
        this.filter = filter;
    }
    
    /**
     * Constructor for KillObjective with entity filter and milestones.
     * 
     * @param objectiveId Unique identifier for this objective
     * @param entityType Type of entity to kill (null if using filter)
     * @param requiredAmount Number of entities to kill
     * @param filter Entity filter type
     * @param milestones Custom milestone percentages
     */
    public KillObjective(String objectiveId, EntityType entityType, int requiredAmount, EntityFilter filter, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.entityType = entityType;
        this.filter = filter;
    }
    
    @Override
    public String getType() {
        return "kill";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof EntityDeathEvent)) {
            return false;
        }
        
        EntityDeathEvent deathEvent = (EntityDeathEvent) event;
        
        // Verify the player killed it
        Player killer = deathEvent.getEntity().getKiller();
        if (killer == null || !killer.equals(player)) {
            return false;
        }
        
        // Check if the killed entity matches our requirement based on filter
        LivingEntity entity = deathEvent.getEntity();
        boolean matches = switch (filter) {
            case SPECIFIC -> entity.getType() == entityType;
            case ANY -> !(entity instanceof Player); // Any entity counts except players
            case HOSTILE -> isHostileMob(entity);
            case PASSIVE -> isPassiveMob(entity);
        };
        
        if (matches) {
            incrementProgress(player.getUniqueId());
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if an entity is a hostile mob.
     * Includes all aggressive/hostile mobs in Minecraft 1.21.8.
     * 
     * @param entity The entity to check
     * @return True if hostile
     */
    private boolean isHostileMob(LivingEntity entity) {
        if (entity == null || entity instanceof Player) {
            return false;
        }
        
        // Check entity type directly for all hostile mobs
        EntityType type = entity.getType();
        return switch (type) {
            case ZOMBIE, HUSK, DROWNED, SKELETON, STRAY, WITHER_SKELETON,
                 CREEPER, SPIDER, CAVE_SPIDER, ENDERMAN, ENDERMITE, SILVERFISH,
                 PHANTOM, SHULKER, HOGLIN, PIGLIN, PIGLIN_BRUTE, ZOGLIN,
                 ENDER_DRAGON, WITHER, SLIME, MAGMA_CUBE, GHAST,
                 BLAZE, ELDER_GUARDIAN, GUARDIAN, WITCH, VEX, EVOKER,
                 VINDICATOR, PILLAGER, RAVAGER, ZOMBIFIED_PIGLIN,
                 BREEZE -> true;
            default -> false;
        };
    }
    
    /**
     * Check if an entity is a passive mob.
     * Includes all peaceful/passive mobs in Minecraft 1.21.8.
     * 
     * @param entity The entity to check
     * @return True if passive
     */
    private boolean isPassiveMob(LivingEntity entity) {
        if (entity == null || entity instanceof Player) {
            return false;
        }
        
        // Animals (cows, pigs, sheep, chickens, horses, llamas, etc.)
        if (entity instanceof Animals) {
            return true;
        }
        
        // Ambient creatures (bats)
        if (entity instanceof Ambient) {
            return true;
        }
        
        // Additional passive mobs for MC 1.21.8 (including water creatures)
        EntityType type = entity.getType();
        return switch (type) {
            case VILLAGER, WANDERING_TRADER, IRON_GOLEM, SNOWMAN,
                 STRIDER, ALLAY, FROG, TADPOLE, CAMEL, SNIFFER,
                 GLOW_SQUID, AXOLOTL, SQUID, COD, SALMON, PUFFERFISH,
                 TROPICAL_FISH, DOLPHIN, TURTLE -> true;
            default -> false;
        };
    }
    
    @Override
    public String getDescription() {
        return switch (filter) {
            case SPECIFIC -> "Kill " + requiredAmount + " " + entityType.name();
            case ANY -> "Kill " + requiredAmount + " entities";
            case HOSTILE -> "Kill " + requiredAmount + " hostile mobs";
            case PASSIVE -> "Kill " + requiredAmount + " passive mobs";
        };
    }
    
    @Override
    public String serialize() {
        String target = switch (filter) {
            case SPECIFIC -> entityType.name();
            case ANY -> "ANY";
            case HOSTILE -> "HOSTILE";
            case PASSIVE -> "PASSIVE";
        };
        return "kill:" + target + ":" + requiredAmount;
    }
    
    /**
     * Get the entity type for this objective.
     * 
     * @return Entity type
     */
    public EntityType getEntityType() {
        return entityType;
    }
}
