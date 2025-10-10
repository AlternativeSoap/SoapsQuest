package com.soaps.quest.objectives;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityRegainHealthEvent;

/**
 * Central registry for all objective types.
 * Manages objective factories and provides methods to create objectives from config data.
 */
public class ObjectiveRegistry {
    
    private static final Map<String, ObjectiveFactory> factories = new HashMap<>();
    private static boolean initialized = false;
    
    /**
     * Functional interface for creating objectives.
     */
    @FunctionalInterface
    public interface ObjectiveFactory {
        /**
         * Create an objective from config data.
         * 
         * @param objectiveId Unique identifier for this objective instance
         * @param config Configuration section containing objective data
         * @return Created objective, or null if invalid
         */
        Objective create(String objectiveId, ConfigurationSection config);
    }
    
    /**
     * Validation result for objective configuration.
     */
    public static class ValidationResult {
        public final boolean valid;
        public final String errorMessage;
        public final String[] missingFields;
        
        private ValidationResult(boolean valid, String errorMessage, String... missingFields) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.missingFields = missingFields;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult failure(String errorMessage, String... missingFields) {
            return new ValidationResult(false, errorMessage, missingFields);
        }
    }
    
    /**
     * Validate an objective configuration before creation.
     * Checks if the objective type is registered and all required fields are present.
     * 
     * @param config Configuration section containing objective data
     * @return ValidationResult indicating success or failure with details
     */
    public static ValidationResult validateObjective(ConfigurationSection config) {
        if (config == null) {
            return ValidationResult.failure("Configuration section is null");
        }
        
        // Check if type is present
        String type = config.getString("type");
        if (type == null || type.isEmpty()) {
            return ValidationResult.failure("Missing required field: 'type'", "type");
        }
        
        // Check if type is registered
        if (!isRegistered(type)) {
            return ValidationResult.failure("Unknown objective type: '" + type + "' (not registered in ObjectiveRegistry)");
        }
        
        // Check type-specific required fields
        String typeKey = type.toLowerCase();
        java.util.List<String> missing = new java.util.ArrayList<>();
        
        switch (typeKey) {
            case "break", "place" -> {
                if (!config.contains("block")) missing.add("block");
                if (!config.contains("amount")) missing.add("amount");
            }
            case "kill", "breed", "tame", "shear" -> {
                if (!config.contains("entity")) missing.add("entity");
                if (!config.contains("amount")) missing.add("amount");
            }
            case "collect", "craft", "smelt", "fish", "enchant", "consume", "trade", "brew" -> {
                if (!config.contains("item")) missing.add("item");
                if (!config.contains("amount")) missing.add("amount");
            }
            case "damage", "heal", "drop", "projectile" -> {
                // These can have optional entity/item/reason filters
                if (!config.contains("amount")) missing.add("amount");
            }
            case "move", "jump", "bowshoot", "firework", "vehicle", "chat", "interact" -> {
                if (!config.contains("amount")) missing.add("amount");
            }
            case "sleep", "death" -> {
                if (!config.contains("amount")) missing.add("amount");
            }
            case "reachlevel", "gainlevel" -> {
                // reachlevel uses "level", gainlevel uses "amount"
                if (typeKey.equals("reachlevel")) {
                    if (!config.contains("level")) missing.add("level");
                } else {
                    if (!config.contains("amount")) missing.add("amount");
                }
            }
            case "command", "placeholder" -> {
                if (!config.contains("amount")) missing.add("amount");
            }
            case "kill_mythicmob" -> {
                if (!config.contains("mob")) missing.add("mob");
                if (!config.contains("amount")) missing.add("amount");
                if (!isMythicMobsInstalled()) {
                    return ValidationResult.failure("MythicMobs plugin is not installed (required for kill_mythicmob objective)");
                }
            }
        }
        
        if (!missing.isEmpty()) {
            return ValidationResult.failure(
                "Missing required fields for '" + type + "' objective: " + String.join(", ", missing),
                missing.toArray(String[]::new)
            );
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Initialize the registry with all built-in objective types.
     * Should be called once at plugin startup.
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        
        // Register break objective
        register("break", (id, config) -> {
            String blockName = config.getString("block");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            if (blockName == null) {
                return null;
            }
            
            // Support ANY keyword for breaking any block
            if (blockName.equalsIgnoreCase("ANY")) {
                return new BreakObjective(id, null, amount, milestones);
            }
            
            try {
                Material blockType = Material.valueOf(blockName.toUpperCase());
                return new BreakObjective(id, blockType, amount, milestones);
            } catch (IllegalArgumentException e) {
                return null;
            }
        });
        
        // Register place objective
        register("place", (id, config) -> {
            String blockName = config.getString("block");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            if (blockName == null) {
                return null;
            }
            
            // Support ANY keyword for placing any block
            if (blockName.equalsIgnoreCase("ANY")) {
                return new PlaceObjective(id, null, amount, milestones);
            }
            
            try {
                Material blockType = Material.valueOf(blockName.toUpperCase());
                return new PlaceObjective(id, blockType, amount, milestones);
            } catch (IllegalArgumentException e) {
                return null;
            }
        });
        
        // Register kill objective
        register("kill", (id, config) -> {
            String entityName = config.getString("entity");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            if (entityName == null) {
                return null;
            }
            
            String entityUpper = entityName.toUpperCase();
            
            // Handle special entity filters and specific entity types
            return switch (entityUpper) {
                case "ANY" -> new KillObjective(id, null, amount, KillObjective.EntityFilter.ANY, milestones);
                case "HOSTILE" -> new KillObjective(id, null, amount, KillObjective.EntityFilter.HOSTILE, milestones);
                case "PASSIVE" -> new KillObjective(id, null, amount, KillObjective.EntityFilter.PASSIVE, milestones);
                default -> {
                    try {
                        EntityType entityType = EntityType.valueOf(entityUpper);
                        yield new KillObjective(id, entityType, amount, KillObjective.EntityFilter.SPECIFIC, milestones);
                    } catch (IllegalArgumentException e) {
                        yield null;
                    }
                }
            };
        });
        
        // Register collect objective
        register("collect", (id, config) -> {
            String itemName = config.getString("item");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            if (itemName == null) {
                return null;
            }
            
            // Support ANY keyword for collecting any item
            if (itemName.equalsIgnoreCase("ANY")) {
                return new CollectObjective(id, null, amount, milestones);
            }
            
            try {
                Material itemType = Material.valueOf(itemName.toUpperCase());
                return new CollectObjective(id, itemType, amount, milestones);
            } catch (IllegalArgumentException e) {
                return null;
            }
        });
        
        // Register command objective
        register("command", (id, config) -> {
            String commandId = config.getString("command");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            if (commandId == null) {
                return null;
            }
            
            return new CommandObjective(id, commandId, amount, milestones);
        });
        
        // Register placeholder objective
        register("placeholder", (id, config) -> {
            String placeholder = config.getString("placeholder");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            if (placeholder == null) {
                return null;
            }
            
            return new PlaceholderObjective(id, placeholder, amount, milestones);
        });
        
        // Register craft objective
        register("craft", (id, config) -> {
            String itemName = config.getString("item");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            if (itemName == null) {
                return null;
            }
            
            // Support ANY keyword for crafting any item
            if (itemName.equalsIgnoreCase("ANY")) {
                return new CraftObjective(id, null, amount, milestones);
            }
            
            try {
                Material itemType = Material.valueOf(itemName.toUpperCase());
                return new CraftObjective(id, itemType, amount, milestones);
            } catch (IllegalArgumentException e) {
                return null;
            }
        });
        
        // Register smelt objective
        register("smelt", (id, config) -> {
            String itemName = config.getString("item");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            if (itemName == null) {
                return null;
            }
            
            try {
                Material itemType = Material.valueOf(itemName.toUpperCase());
                return new SmeltObjective(id, itemType, amount, milestones);
            } catch (IllegalArgumentException e) {
                return null;
            }
        });
        
        // Register fish objective
        register("fish", (id, config) -> {
            String itemName = config.getString("item");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            Material fishType = null;
            if (itemName != null && !itemName.equalsIgnoreCase("ANY")) {
                try {
                    fishType = Material.valueOf(itemName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            
            return new FishObjective(id, fishType, amount, milestones);
        });
        
        // Register enchant objective
        register("enchant", (id, config) -> {
            String itemName = config.getString("item");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            Material itemType = null;
            if (itemName != null && !itemName.equalsIgnoreCase("ANY")) {
                try {
                    itemType = Material.valueOf(itemName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            
            return new EnchantObjective(id, itemType, amount, milestones);
        });
        
        // Register consume objective
        register("consume", (id, config) -> {
            String itemName = config.getString("item");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            if (itemName == null) {
                return null;
            }
            
            try {
                Material itemType = Material.valueOf(itemName.toUpperCase());
                return new ConsumeObjective(id, itemType, amount, milestones);
            } catch (IllegalArgumentException e) {
                return null;
            }
        });
        
        // Register tame objective
        register("tame", (id, config) -> {
            String entityName = config.getString("entity");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            EntityType entityType = null;
            if (entityName != null && !entityName.equalsIgnoreCase("ANY")) {
                try {
                    entityType = EntityType.valueOf(entityName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            
            return new TameObjective(id, entityType, amount, milestones);
        });
        
        // Register trade objective
        register("trade", (id, config) -> {
            String itemName = config.getString("item");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            Material itemType = null;
            if (itemName != null && !itemName.equalsIgnoreCase("ANY")) {
                try {
                    itemType = Material.valueOf(itemName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            
            return new TradeObjective(id, itemType, amount, milestones);
        });
        
        // Register brew objective
        register("brew", (id, config) -> {
            String itemName = config.getString("item");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            Material potionType = null;
            if (itemName != null && !itemName.equalsIgnoreCase("ANY")) {
                try {
                    potionType = Material.valueOf(itemName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            
            return new BrewObjective(id, potionType, amount, milestones);
        });
        
        // Register shear objective
        register("shear", (id, config) -> {
            String entityName = config.getString("entity");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            EntityType entityType = null;
            if (entityName != null && !entityName.equalsIgnoreCase("ANY")) {
                try {
                    entityType = EntityType.valueOf(entityName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            
            return new ShearObjective(id, entityType, amount, milestones);
        });
        
        // Register sleep objective
        register("sleep", (id, config) -> {
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            return new SleepObjective(id, amount, milestones);
        });
        
        // Register heal objective
        register("heal", (id, config) -> {
            String reasonName = config.getString("reason");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            EntityRegainHealthEvent.RegainReason reason = null;
            if (reasonName != null && !reasonName.equalsIgnoreCase("ANY")) {
                try {
                    reason = EntityRegainHealthEvent.RegainReason.valueOf(reasonName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            
            return new HealObjective(id, reason, amount, milestones);
        });
        
        // Register drop objective
        register("drop", (id, config) -> {
            String itemName = config.getString("item");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            Material itemType = null;
            if (itemName != null && !itemName.equalsIgnoreCase("ANY")) {
                try {
                    itemType = Material.valueOf(itemName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            
            return new DropObjective(id, itemType, amount, milestones);
        });
        
        // Register damage objective
        register("damage", (id, config) -> {
            String entityName = config.getString("entity");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            EntityType entityType = null;
            if (entityName != null && !entityName.equalsIgnoreCase("ANY")) {
                try {
                    entityType = EntityType.valueOf(entityName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            
            return new DamageObjective(id, entityType, amount, milestones);
        });
        
        // Register death objective
        register("death", (id, config) -> {
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            return new DeathObjective(id, amount, milestones);
        });
        
        // Register level objectives (reach and gain)
        register("reachlevel", (id, config) -> {
            int level = config.getInt("level", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            return new LevelObjective(id, level, true, milestones);
        });
        
        register("gainlevel", (id, config) -> {
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            return new LevelObjective(id, amount, false, milestones);
        });
        
        // Alias: 'level' defaults to gainlevel for backward compatibility
        register("level", (id, config) -> {
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            return new LevelObjective(id, amount, false, milestones);
        });
        
        // Register move objective
        register("move", (id, config) -> {
            // Support both 'distance' (legacy) and 'amount' (standard)
            int distance = config.getInt("amount", config.getInt("distance", 100));
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            return new MoveObjective(id, distance, milestones);
        });
        
        // Register jump objective
        register("jump", (id, config) -> {
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            return new JumpObjective(id, amount, milestones);
        });
        
        // Register chat objective
        register("chat", (id, config) -> {
            String text = config.getString("text");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            return new ChatObjective(id, text, amount, milestones);
        });
        
        // Register interact objective
        register("interact", (id, config) -> {
            String blockName = config.getString("block");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            Material blockType = null;
            if (blockName != null && !blockName.equalsIgnoreCase("ANY")) {
                try {
                    blockType = Material.valueOf(blockName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            
            return new InteractObjective(id, blockType, amount, milestones);
        });
        
        // Register bowshoot objective
        register("bowshoot", (id, config) -> {
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            return new BowShootObjective(id, amount, milestones);
        });
        
        // Alias: 'shoot_bow' for backward compatibility
        register("shoot_bow", (id, config) -> {
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            return new BowShootObjective(id, amount, milestones);
        });
        
        // Register projectile objective
        register("projectile", (id, config) -> {
            String projectileName = config.getString("projectile");
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            EntityType projectileType = null;
            if (projectileName != null && !projectileName.equalsIgnoreCase("ANY")) {
                try {
                    projectileType = EntityType.valueOf(projectileName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            
            return new ProjectileObjective(id, projectileType, amount, milestones);
        });
        
        // Register firework objective
        register("firework", (id, config) -> {
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            return new FireworkObjective(id, amount, milestones);
        });
        
        // Alias: 'launch_firework' for backward compatibility
        register("launch_firework", (id, config) -> {
            int amount = config.getInt("amount", 1);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            return new FireworkObjective(id, amount, milestones);
        });
        
        // Register vehicle objective
        register("vehicle", (id, config) -> {
            String vehicleName = config.getString("vehicle");
            int distance = config.getInt("distance", 100);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            EntityType vehicleType = null;
            if (vehicleName != null && !vehicleName.equalsIgnoreCase("ANY")) {
                try {
                    vehicleType = EntityType.valueOf(vehicleName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            
            return new VehicleObjective(id, vehicleType, distance, milestones);
        });
        
        // Alias: 'ride_vehicle' for backward compatibility
        register("ride_vehicle", (id, config) -> {
            String vehicleName = config.getString("vehicle");
            int distance = config.getInt("distance", 100);
            java.util.List<Integer> milestones = config.getIntegerList("milestones");
            
            EntityType vehicleType = null;
            if (vehicleName != null && !vehicleName.equalsIgnoreCase("ANY")) {
                try {
                    vehicleType = EntityType.valueOf(vehicleName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            
            return new VehicleObjective(id, vehicleType, distance, milestones);
        });
        
        // Register MythicMobs objective if MythicMobs is installed
        if (isMythicMobsInstalled()) {
            register("kill_mythicmob", (id, config) -> {
                String mobType = config.getString("mob");
                int amount = config.getInt("amount", 1);
                java.util.List<Integer> milestones = config.getIntegerList("milestones");
                
                if (mobType == null) {
                    return null;
                }
                
                return new KillMythicMobObjective(id, mobType, amount, milestones);
            });
        }
        
        initialized = true;
    }
    
    /**
     * Check if MythicMobs plugin is installed and available.
     * Public method to allow other classes to check MythicMobs availability.
     * 
     * @return True if MythicMobs is present
     */
    public static boolean isMythicMobsInstalled() {
        try {
            Class.forName("io.lumine.mythic.bukkit.MythicBukkit");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * Register a new objective type.
     * 
     * @param type Type identifier (e.g., "break", "kill")
     * @param factory Factory function to create objectives of this type
     */
    public static void register(String type, ObjectiveFactory factory) {
        factories.put(type.toLowerCase(), factory);
    }
    
    /**
     * Create an objective from a config section.
     * 
     * @param objectiveId Unique identifier for this objective instance
     * @param config Configuration section containing objective data
     * @return Created objective, or null if invalid
     */
    public static Objective createObjective(String objectiveId, ConfigurationSection config) {
        String type = config.getString("type");
        if (type == null) {
            return null;
        }
        
        ObjectiveFactory factory = factories.get(type.toLowerCase());
        if (factory == null) {
            return null;
        }
        
        return factory.create(objectiveId, config);
    }
    
    /**
     * Create an objective from a serialized string.
     * Format: "type:data"
     * Examples: "break:STONE:50", "kill:ZOMBIE:10"
     * 
     * @param objectiveId Unique identifier for this objective instance
     * @param serialized Serialized objective string
     * @return Created objective, or null if invalid
     */
    public static Objective deserialize(String objectiveId, String serialized) {
        String[] parts = serialized.split(":");
        if (parts.length < 3) {
            return null;
        }
        
        String type = parts[0].toLowerCase();
        String target = parts[1];
        int amount;
        
        try {
            amount = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return null;
        }
        
        switch (type) {
            case "break" -> {
                try {
                    Material blockType = Material.valueOf(target.toUpperCase());
                    return new BreakObjective(objectiveId, blockType, amount);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
                
            case "place" -> {
                try {
                    Material blockType = Material.valueOf(target.toUpperCase());
                    return new PlaceObjective(objectiveId, blockType, amount);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
                
            case "kill" -> {
                String targetUpper = target.toUpperCase();
                
                // Handle special entity filters and specific entity types
                return switch (targetUpper) {
                    case "ANY" -> new KillObjective(objectiveId, null, amount, KillObjective.EntityFilter.ANY);
                    case "HOSTILE" -> new KillObjective(objectiveId, null, amount, KillObjective.EntityFilter.HOSTILE);
                    case "PASSIVE" -> new KillObjective(objectiveId, null, amount, KillObjective.EntityFilter.PASSIVE);
                    default -> {
                        try {
                            EntityType entityType = EntityType.valueOf(targetUpper);
                            yield new KillObjective(objectiveId, entityType, amount, KillObjective.EntityFilter.SPECIFIC);
                        } catch (IllegalArgumentException e) {
                            yield null;
                        }
                    }
                };
            }
                
            case "collect" -> {
                try {
                    Material itemType = Material.valueOf(target.toUpperCase());
                    return new CollectObjective(objectiveId, itemType, amount);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
                
            case "command" -> {
                return new CommandObjective(objectiveId, target, amount);
            }
                
            case "placeholder" -> {
                return new PlaceholderObjective(objectiveId, target, amount);
            }
                
            case "craft" -> {
                try {
                    Material itemType = Material.valueOf(target.toUpperCase());
                    return new CraftObjective(objectiveId, itemType, amount);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
                
            case "smelt" -> {
                try {
                    Material itemType = Material.valueOf(target.toUpperCase());
                    return new SmeltObjective(objectiveId, itemType, amount);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
                
            case "fish" -> {
                Material fishType = target.equalsIgnoreCase("ANY") ? null : Material.valueOf(target.toUpperCase());
                return new FishObjective(objectiveId, fishType, amount);
            }
                
            case "enchant" -> {
                Material itemType = target.equalsIgnoreCase("ANY") ? null : Material.valueOf(target.toUpperCase());
                return new EnchantObjective(objectiveId, itemType, amount);
            }
                
            case "consume" -> {
                try {
                    Material itemType = Material.valueOf(target.toUpperCase());
                    return new ConsumeObjective(objectiveId, itemType, amount);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
                
            case "tame" -> {
                EntityType entityType = target.equalsIgnoreCase("ANY") ? null : EntityType.valueOf(target.toUpperCase());
                return new TameObjective(objectiveId, entityType, amount);
            }
                
            case "trade" -> {
                Material itemType = target.equalsIgnoreCase("ANY") ? null : Material.valueOf(target.toUpperCase());
                return new TradeObjective(objectiveId, itemType, amount);
            }
                
            case "brew" -> {
                Material potionType = target.equalsIgnoreCase("ANY") ? null : Material.valueOf(target.toUpperCase());
                return new BrewObjective(objectiveId, potionType, amount);
            }
                
            case "shear" -> {
                EntityType entityType = target.equalsIgnoreCase("ANY") ? null : EntityType.valueOf(target.toUpperCase());
                return new ShearObjective(objectiveId, entityType, amount);
            }
                
            case "sleep" -> {
                return new SleepObjective(objectiveId, amount);
            }
                
            case "heal" -> {
                EntityRegainHealthEvent.RegainReason reason = target.equalsIgnoreCase("ANY") ? null : EntityRegainHealthEvent.RegainReason.valueOf(target.toUpperCase());
                return new HealObjective(objectiveId, reason, amount);
            }
                
            case "drop" -> {
                Material itemType = target.equalsIgnoreCase("ANY") ? null : Material.valueOf(target.toUpperCase());
                return new DropObjective(objectiveId, itemType, amount);
            }
                
            case "damage" -> {
                EntityType entityType = target.equalsIgnoreCase("ANY") ? null : EntityType.valueOf(target.toUpperCase());
                return new DamageObjective(objectiveId, entityType, amount);
            }
                
            case "death" -> {
                return new DeathObjective(objectiveId, amount);
            }
                
            case "reachlevel", "gainlevel" -> {
                boolean isReach = type.equals("reachlevel");
                return new LevelObjective(objectiveId, amount, isReach);
            }
                
            case "move" -> {
                return new MoveObjective(objectiveId, amount);
            }
                
            case "jump" -> {
                return new JumpObjective(objectiveId, amount);
            }
                
            case "chat" -> {
                String text = target.equalsIgnoreCase("ANY") ? null : target;
                return new ChatObjective(objectiveId, text, amount);
            }
                
            case "interact" -> {
                Material blockType = target.equalsIgnoreCase("ANY") ? null : Material.valueOf(target.toUpperCase());
                return new InteractObjective(objectiveId, blockType, amount);
            }
                
            case "bowshoot" -> {
                return new BowShootObjective(objectiveId, amount);
            }
                
            case "projectile" -> {
                EntityType projectileType = target.equalsIgnoreCase("ANY") ? null : EntityType.valueOf(target.toUpperCase());
                return new ProjectileObjective(objectiveId, projectileType, amount);
            }
                
            case "firework" -> {
                return new FireworkObjective(objectiveId, amount);
            }
                
            case "vehicle" -> {
                EntityType vehicleType = target.equalsIgnoreCase("ANY") ? null : EntityType.valueOf(target.toUpperCase());
                return new VehicleObjective(objectiveId, vehicleType, amount);
            }
                
            case "kill_mythicmob" -> {
                // Only deserialize if MythicMobs is installed
                if (isMythicMobsInstalled()) {
                    return new KillMythicMobObjective(objectiveId, target, amount);
                }
                return null;
            }
                
            default -> {
                return null;
            }
        }
    }
    
    /**
     * Check if a type is registered.
     * 
     * @param type Type identifier
     * @return True if registered
     */
    public static boolean isRegistered(String type) {
        return factories.containsKey(type.toLowerCase());
    }
    
    /**
     * Get all registered objective types.
     * 
     * @return Map of type identifiers to factories
     */
    public static Map<String, ObjectiveFactory> getRegisteredTypes() {
        return new HashMap<>(factories);
    }
}
