/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Biome
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.EntityType
 *  org.bukkit.event.entity.EntityRegainHealthEvent$RegainReason
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AnvilRepairObjective;
import com.soaps.quest.objectives.BowShootObjective;
import com.soaps.quest.objectives.BreakObjective;
import com.soaps.quest.objectives.BreedObjective;
import com.soaps.quest.objectives.BrewObjective;
import com.soaps.quest.objectives.ChatObjective;
import com.soaps.quest.objectives.CollectObjective;
import com.soaps.quest.objectives.CommandObjective;
import com.soaps.quest.objectives.ConsumeObjective;
import com.soaps.quest.objectives.CraftObjective;
import com.soaps.quest.objectives.DamageObjective;
import com.soaps.quest.objectives.DeathObjective;
import com.soaps.quest.objectives.DropObjective;
import com.soaps.quest.objectives.ElytraFlyObjective;
import com.soaps.quest.objectives.EnchantObjective;
import com.soaps.quest.objectives.ExploreBiomeObjective;
import com.soaps.quest.objectives.FireworkObjective;
import com.soaps.quest.objectives.FishObjective;
import com.soaps.quest.objectives.HarvestObjective;
import com.soaps.quest.objectives.HealObjective;
import com.soaps.quest.objectives.InteractObjective;
import com.soaps.quest.objectives.JumpObjective;
import com.soaps.quest.objectives.KillMythicMobObjective;
import com.soaps.quest.objectives.KillObjective;
import com.soaps.quest.objectives.LevelObjective;
import com.soaps.quest.objectives.MoveObjective;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.objectives.ObjectiveType;
import com.soaps.quest.objectives.PlaceObjective;
import com.soaps.quest.objectives.PlaceholderObjective;
import com.soaps.quest.objectives.ProjectileObjective;
import com.soaps.quest.objectives.ShearObjective;
import com.soaps.quest.objectives.SleepObjective;
import com.soaps.quest.objectives.SmeltObjective;
import com.soaps.quest.objectives.TameObjective;
import com.soaps.quest.objectives.TradeObjective;
import com.soaps.quest.objectives.VehicleObjective;
import com.soaps.quest.objectives.XpPickupObjective;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class ObjectiveRegistry {
    private static final Map<String, ObjectiveFactory> factories = new HashMap<String, ObjectiveFactory>();
    private static final Map<String, ObjectiveType> types = new HashMap<String, ObjectiveType>();
    private static boolean initialized = false;

    public static String resolveCanonicalType(String type) {
        if (type == null) {
            return null;
        }
        return switch (type.toLowerCase()) {
            case "shoot_bow" -> "bowshoot";
            case "launch_firework" -> "firework";
            case "ride_vehicle" -> "vehicle";
            case "level" -> "gainlevel";
            default -> type.toLowerCase();
        };
    }

    private static String getConfigString(ConfigurationSection config, String primaryKey, String fallbackKey) {
        String value = config.getString(primaryKey);
        if (value == null || value.isEmpty()) {
            value = config.getString(fallbackKey);
        }
        return value;
    }

    public static ValidationResult validateObjective(ConfigurationSection config) {
        if (config == null) {
            return ValidationResult.failure("Configuration section is null", new String[0]);
        }
        String type = config.getString("type");
        if (type == null || type.isEmpty()) {
            return ValidationResult.failure("Missing required field: 'type'", "type");
        }
        if (!ObjectiveRegistry.isRegistered(type)) {
            return ValidationResult.failure("Unknown objective type: '" + type + "' (not registered in ObjectiveRegistry)", new String[0]);
        }
        String typeKey = ObjectiveRegistry.resolveCanonicalType(type);
        ArrayList<String> missing = new ArrayList<String>();
        switch (typeKey) {
            case "break": 
            case "place": 
            case "interact": {
                if (!config.contains("target")) {
                    missing.add("target");
                }
                if (config.contains("amount")) break;
                missing.add("amount");
                break;
            }
            case "kill": 
            case "breed": 
            case "tame": 
            case "shear": {
                if (!config.contains("target")) {
                    missing.add("target");
                }
                if (config.contains("amount")) break;
                missing.add("amount");
                break;
            }
            case "damage": {
                if (config.contains("amount")) break;
                missing.add("amount");
                break;
            }
            case "collect": 
            case "craft": 
            case "smelt": 
            case "fish": 
            case "enchant": 
            case "consume": 
            case "trade": 
            case "brew": 
            case "drop": {
                if (!config.contains("target")) {
                    missing.add("target");
                }
                if (config.contains("amount")) break;
                missing.add("amount");
                break;
            }
            case "heal": 
            case "projectile": {
                if (config.contains("amount")) break;
                missing.add("amount");
                break;
            }
            case "move": {
                if (config.contains("amount") || config.contains("distance")) break;
                missing.add("amount or distance");
                break;
            }
            case "vehicle": {
                if (config.contains("amount") || config.contains("distance")) break;
                missing.add("amount or distance");
                break;
            }
            case "jump": 
            case "bowshoot": 
            case "firework": {
                if (config.contains("amount")) break;
                missing.add("amount");
                break;
            }
            case "chat": {
                if (config.contains("amount")) break;
                missing.add("amount");
                break;
            }
            case "sleep": 
            case "death": {
                if (config.contains("amount")) break;
                missing.add("amount");
                break;
            }
            case "reachlevel": 
            case "gainlevel": {
                if (typeKey.equals("reachlevel")) {
                    if (config.contains("level")) break;
                    missing.add("level");
                    break;
                }
                if (config.contains("amount")) break;
                missing.add("amount");
                break;
            }
            case "command": {
                if (!config.contains("command") && !config.contains("target")) {
                    missing.add("command");
                }
                if (config.contains("amount")) break;
                missing.add("amount");
                break;
            }
            case "placeholder": {
                if (!config.contains("placeholder") && !config.contains("target")) {
                    missing.add("placeholder");
                }
                if (config.contains("amount")) break;
                missing.add("amount");
                break;
            }
            case "kill_mythicmob": {
                if (!config.contains("target")) {
                    missing.add("target");
                }
                if (!config.contains("amount")) {
                    missing.add("amount");
                }
                if (ObjectiveRegistry.isMythicMobsInstalled()) break;
                return ValidationResult.failure("MythicMobs plugin is not installed (required for kill_mythicmob objective)", new String[0]);
            }
            case "harvest": {
                if (config.contains("amount")) break;
                missing.add("amount");
                break;
            }
            case "elytra_fly": {
                if (config.contains("amount")) break;
                missing.add("amount");
                break;
            }
            case "explore_biome": {
                if (config.contains("amount")) break;
                missing.add("amount");
                break;
            }
            case "xp_pickup": {
                if (config.contains("amount")) break;
                missing.add("amount");
                break;
            }
            case "anvil_repair": {
                if (config.contains("amount")) break;
                missing.add("amount");
            }
        }
        if (!missing.isEmpty()) {
            return ValidationResult.failure("Missing required fields for '" + type + "' objective: " + String.join((CharSequence)", ", missing), (String[])missing.toArray(String[]::new));
        }
        return ValidationResult.success();
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        ObjectiveRegistry.registerGuiTypes();
        ObjectiveRegistry.registerConfigFactories();
        initialized = true;
    }

    private static void registerGuiTypes() {
        ObjectiveRegistry.registerType(new ObjectiveType("kill", "Kill Entity", "Kill specific entities", Material.IRON_SWORD, "Enter entity type and amount (e.g. ZOMBIE 5):", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String entityName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                return switch (entityName) {
                    case "ANY" -> new KillObjective(objectiveId, null, amount, KillObjective.EntityFilter.ANY);
                    case "HOSTILE" -> new KillObjective(objectiveId, null, amount, KillObjective.EntityFilter.HOSTILE);
                    case "PASSIVE" -> new KillObjective(objectiveId, null, amount, KillObjective.EntityFilter.PASSIVE);
                    default -> {
                        EntityType entityType = EntityType.valueOf((String)entityName);
                        yield new KillObjective(objectiveId, entityType, amount, KillObjective.EntityFilter.SPECIFIC);
                    }
                };
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("break", "Break Block", "Break specific blocks", Material.DIAMOND_PICKAXE, "Enter block type and amount (e.g. STONE 64) or ANY 100:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String blockName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                if (blockName.equals("ANY")) {
                    return new BreakObjective(objectiveId, null, amount);
                }
                Material blockType = Material.valueOf((String)blockName);
                return new BreakObjective(objectiveId, blockType, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("place", "Place Block", "Place specific blocks", Material.GRASS_BLOCK, "Enter block type and amount (e.g. COBBLESTONE 100) or ANY 200:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String blockName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                if (blockName.equals("ANY")) {
                    return new PlaceObjective(objectiveId, null, amount);
                }
                Material blockType = Material.valueOf((String)blockName);
                return new PlaceObjective(objectiveId, blockType, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("collect", "Collect Item", "Collect/obtain specific items", Material.CHEST, "Enter item type and amount (e.g. DIAMOND 10) or ANY 50:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String itemName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                if (itemName.equals("ANY")) {
                    return new CollectObjective(objectiveId, null, amount);
                }
                Material itemType = Material.valueOf((String)itemName);
                return new CollectObjective(objectiveId, itemType, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("craft", "Craft Item", "Craft specific items", Material.CRAFTING_TABLE, "Enter item type and amount (e.g. IRON_SWORD 5) or ANY 20:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String itemName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                if (itemName.equals("ANY")) {
                    return new CraftObjective(objectiveId, null, amount);
                }
                Material itemType = Material.valueOf((String)itemName);
                return new CraftObjective(objectiveId, itemType, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("fish", "Catch Fish", "Catch fish or other items", Material.FISHING_ROD, "Enter fish type and amount (e.g. COD 20) or ANY 30:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String fishName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                Material fishType = fishName.equals("ANY") ? null : Material.valueOf((String)fishName);
                return new FishObjective(objectiveId, fishType, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("tame", "Tame Entity", "Tame specific animals", Material.BONE, "Enter entity type and amount (e.g. WOLF 3):", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String entityName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                EntityType entityType = entityName.equals("ANY") ? null : EntityType.valueOf((String)entityName);
                return new TameObjective(objectiveId, entityType, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("move", "Move Distance", "Walk or travel a certain distance", Material.LEATHER_BOOTS, "Enter distance in blocks (e.g. 1000):", input -> {
            try {
                int distance = Integer.parseInt(input.trim());
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                return new MoveObjective(objectiveId, distance);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("reachlevel", "Reach Level", "Reach a specific experience level", Material.EXPERIENCE_BOTTLE, "Enter target level (e.g. 30):", input -> {
            try {
                int level = Integer.parseInt(input.trim());
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                return new LevelObjective(objectiveId, level, true);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("interact", "Interact Block", "Interact with specific blocks", Material.OAK_BUTTON, "Enter block type and amount (e.g. LEVER 10) or ANY 50:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String blockName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                Material blockType = blockName.equals("ANY") ? null : Material.valueOf((String)blockName);
                return new InteractObjective(objectiveId, blockType, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("smelt", "Smelt Item", "Smelt items in a furnace", Material.FURNACE, "Enter item type and amount (e.g. IRON_INGOT 32) or ANY 64:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String itemName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                Material itemType = itemName.equals("ANY") ? null : Material.valueOf((String)itemName);
                return new SmeltObjective(objectiveId, itemType, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("enchant", "Enchant Item", "Enchant items at an enchanting table", Material.ENCHANTING_TABLE, "Enter item type and amount (e.g. DIAMOND_SWORD 5) or ANY 10:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String itemName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                Material itemType = itemName.equals("ANY") ? null : Material.valueOf((String)itemName);
                return new EnchantObjective(objectiveId, itemType, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("consume", "Consume Item", "Eat or drink specific items", Material.COOKED_BEEF, "Enter item type and amount (e.g. BREAD 10) or ANY 30:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String itemName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                Material itemType = itemName.equals("ANY") ? null : Material.valueOf((String)itemName);
                return new ConsumeObjective(objectiveId, itemType, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("trade", "Trade with Villager", "Trade with villagers", Material.EMERALD, "Enter item type and amount (e.g. EMERALD 10) or ANY 20:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String itemName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                Material itemType = itemName.equals("ANY") ? null : Material.valueOf((String)itemName);
                return new TradeObjective(objectiveId, itemType, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("brew", "Brew Potion", "Brew potions in a brewing stand", Material.BREWING_STAND, "Enter potion type and amount (e.g. POTION 5) or ANY 10:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String potionName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                Material potionType = potionName.equals("ANY") ? null : Material.valueOf((String)potionName);
                return new BrewObjective(objectiveId, potionType, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("breed", "Breed Animal", "Breed specific animals", Material.WHEAT, "Enter entity type and amount (e.g. COW 5) or ANY 10:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String entityName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                EntityType entityType = entityName.equals("ANY") ? null : EntityType.valueOf((String)entityName);
                return new BreedObjective(objectiveId, entityType, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("shear", "Shear Entity", "Shear sheep or other entities", Material.SHEARS, "Enter entity type and amount (e.g. SHEEP 20):", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String entityName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                EntityType entityType = entityName.equals("ANY") ? null : EntityType.valueOf((String)entityName);
                return new ShearObjective(objectiveId, entityType, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("sleep", "Sleep in Bed", "Sleep in a bed", Material.RED_BED, "Enter amount of times (e.g. 5):", input -> {
            try {
                int amount = Integer.parseInt(input.trim());
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                return new SleepObjective(objectiveId, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("heal", "Heal", "Regenerate health (ANY reason)", Material.GOLDEN_APPLE, "Enter amount of health to heal (e.g. 20):", input -> {
            try {
                int amount = Integer.parseInt(input.trim());
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                return new HealObjective(objectiveId, null, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("drop", "Drop Item", "Drop specific items", Material.DROPPER, "Enter item type and amount (e.g. DIRT 64) or ANY 100:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String itemName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                Material itemType = itemName.equals("ANY") ? null : Material.valueOf((String)itemName);
                return new DropObjective(objectiveId, itemType, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("damage", "Deal Damage", "Deal damage to ANY entity", Material.DIAMOND_SWORD, "Enter amount of damage (e.g. 100):", input -> {
            try {
                int amount = Integer.parseInt(input.trim());
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                return new DamageObjective(objectiveId, null, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("death", "Die", "Die a certain number of times", Material.SKELETON_SKULL, "Enter amount of deaths (e.g. 1):", input -> {
            try {
                int amount = Integer.parseInt(input.trim());
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                return new DeathObjective(objectiveId, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("gainlevel", "Gain Levels", "Gain experience levels", Material.EXPERIENCE_BOTTLE, "Enter amount of levels to gain (e.g. 10):", input -> {
            try {
                int amount = Integer.parseInt(input.trim());
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                return new LevelObjective(objectiveId, amount, false);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("jump", "Jump", "Jump a certain number of times", Material.RABBIT_FOOT, "Enter amount of jumps (e.g. 100):", input -> {
            try {
                int amount = Integer.parseInt(input.trim());
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                return new JumpObjective(objectiveId, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("chat", "Send Chat Message", "Send messages in chat", Material.WRITABLE_BOOK, "Enter message text and count (e.g. 'hello 50') or 'ANY 50':", input -> {
            String[] parts = input.trim().split("\\s+", 2);
            if (parts.length != 2) {
                return null;
            }
            try {
                String text = parts[0];
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                String requiredText = text.equals("ANY") ? null : text;
                return new ChatObjective(objectiveId, requiredText, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("bowshoot", "Shoot Bow", "Shoot arrows with a bow", Material.BOW, "Enter amount of shots (e.g. 100):", input -> {
            try {
                int amount = Integer.parseInt(input.trim());
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                return new BowShootObjective(objectiveId, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("projectile", "Launch Projectile", "Launch projectiles (arrows, snowballs, etc.)", Material.SNOWBALL, "Enter projectile type and amount (e.g. ARROW 50) or ANY 100:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String projectileName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                EntityType projectileType = projectileName.equals("ANY") ? null : EntityType.valueOf((String)projectileName);
                return new ProjectileObjective(objectiveId, projectileType, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("firework", "Launch Firework", "Launch firework rockets", Material.FIREWORK_ROCKET, "Enter amount of fireworks (e.g. 20):", input -> {
            try {
                int amount = Integer.parseInt(input.trim());
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                return new FireworkObjective(objectiveId, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("vehicle", "Ride Vehicle", "Ride vehicles or entities", Material.MINECART, "Enter entity type and distance (e.g. MINECART 1000) or ANY 5000:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String vehicleName = parts[0].toUpperCase();
                int distance = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                EntityType vehicleType = vehicleName.equals("ANY") ? null : EntityType.valueOf((String)vehicleName);
                return new VehicleObjective(objectiveId, vehicleType, distance);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("command", "Execute Command", "Run a specific command", Material.COMMAND_BLOCK, "Enter command and times (e.g. /help 5):", input -> {
            String[] parts = input.trim().split("\\s+", 2);
            if (parts.length != 2) {
                return null;
            }
            try {
                String command = parts[0];
                int times = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                return new CommandObjective(objectiveId, command, times);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("placeholder", "Placeholder Condition", "Check PlaceholderAPI value", Material.NAME_TAG, "Enter placeholder and target amount (e.g. %player_level% 30):", input -> {
            String[] parts = input.trim().split("\\s+", 2);
            if (parts.length != 2) {
                return null;
            }
            try {
                String placeholder = parts[0];
                int targetAmount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                return new PlaceholderObjective(objectiveId, placeholder, targetAmount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("harvest", "Harvest Crop", "Harvest fully-grown crops", Material.WHEAT, "Enter crop type and amount (e.g. WHEAT 64) or ANY 100:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String cropName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                Material cropType = cropName.equals("ANY") ? null : Material.valueOf((String)cropName);
                return new HarvestObjective(objectiveId, cropType, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("elytra_fly", "Fly with Elytra", "Glide a distance using an elytra", Material.ELYTRA, "Enter distance in blocks (e.g. 500):", input -> {
            try {
                int amount = Integer.parseInt(input.trim());
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                return new ElytraFlyObjective(objectiveId, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("explore_biome", "Explore Biome", "Enter a specific biome (or any new biome)", Material.FILLED_MAP, "Enter biome name and times (e.g. JUNGLE 3) or ANY 10:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String biomeName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                if (biomeName.equals("ANY")) {
                    return new ExploreBiomeObjective(objectiveId, null, amount);
                }
                return new ExploreBiomeObjective(objectiveId, biomeName, amount);
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("xp_pickup", "Collect XP", "Collect a certain amount of experience points", Material.EXPERIENCE_BOTTLE, "Enter total XP to collect (e.g. 1000):", input -> {
            try {
                int amount = Integer.parseInt(input.trim());
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                return new XpPickupObjective(objectiveId, amount);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }));
        ObjectiveRegistry.registerType(new ObjectiveType("anvil_repair", "Repair on Anvil", "Repair items using an anvil", Material.ANVIL, "Enter item type and times (e.g. DIAMOND_SWORD 5) or ANY 10:", input -> {
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            try {
                String itemName = parts[0].toUpperCase();
                int amount = Integer.parseInt(parts[1]);
                String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                Material itemType = itemName.equals("ANY") ? null : Material.valueOf((String)itemName);
                return new AnvilRepairObjective(objectiveId, itemType, amount);
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }));
        if (ObjectiveRegistry.isMythicMobsInstalled()) {
            ObjectiveRegistry.registerType(new ObjectiveType("kill_mythicmob", "Kill MythicMob", "Kill specific MythicMobs (requires MythicMobs plugin)", Material.WITHER_SKELETON_SKULL, "Enter mob internal name and amount (e.g. SkeletonKing 1):", input -> {
                String[] parts = input.trim().split("\\s+");
                if (parts.length != 2) {
                    return null;
                }
                try {
                    String mobName = parts[0];
                    int amount = Integer.parseInt(parts[1]);
                    String objectiveId = "obj_" + UUID.randomUUID().toString().substring(0, 8);
                    return new KillMythicMobObjective(objectiveId, mobName, amount);
                }
                catch (NumberFormatException e) {
                    return null;
                }
            }));
        }
    }

    private static void registerConfigFactories() {
        ObjectiveRegistry.register("break", (id, config) -> {
            String blockName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            if (blockName == null) {
                return null;
            }
            if (blockName.equalsIgnoreCase("ANY")) {
                return new BreakObjective(id, null, amount, milestones);
            }
            try {
                Material blockType = Material.valueOf((String)blockName.toUpperCase());
                return new BreakObjective(id, blockType, amount, milestones);
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        });
        ObjectiveRegistry.register("place", (id, config) -> {
            String blockName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            if (blockName == null) {
                return null;
            }
            if (blockName.equalsIgnoreCase("ANY")) {
                return new PlaceObjective(id, null, amount, milestones);
            }
            try {
                Material blockType = Material.valueOf((String)blockName.toUpperCase());
                return new PlaceObjective(id, blockType, amount, milestones);
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        });
        ObjectiveRegistry.register("kill", (id, config) -> {
            String entityUpper;
            String entityName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            if (entityName == null) {
                return null;
            }
            return switch (entityUpper = entityName.toUpperCase()) {
                case "ANY" -> {
                    KillObjective var8_8;
                    yield var8_8 = new KillObjective(id, null, amount, KillObjective.EntityFilter.ANY, milestones);
                }
                case "HOSTILE" -> {
                    KillObjective var8_9;
                    yield var8_9 = new KillObjective(id, null, amount, KillObjective.EntityFilter.HOSTILE, milestones);
                }
                case "PASSIVE" -> {
                    KillObjective var8_10;
                    yield var8_10 = new KillObjective(id, null, amount, KillObjective.EntityFilter.PASSIVE, milestones);
                }
                default -> {
                    try {
                        KillObjective var8_11;
                        EntityType entityType = EntityType.valueOf((String)entityUpper);
                        yield var8_11 = new KillObjective(id, entityType, amount, KillObjective.EntityFilter.SPECIFIC, milestones);
                    }
                    catch (IllegalArgumentException e) {
                        KillObjective var8_12;
                        yield var8_12 = null;
                    }
                }
            };
        });
        ObjectiveRegistry.register("collect", (id, config) -> {
            String itemName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            if (itemName == null) {
                return null;
            }
            if (itemName.equalsIgnoreCase("ANY")) {
                return new CollectObjective(id, null, amount, milestones);
            }
            try {
                Material itemType = Material.valueOf((String)itemName.toUpperCase());
                return new CollectObjective(id, itemType, amount, milestones);
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        });
        ObjectiveRegistry.register("command", (id, config) -> {
            String commandId = ObjectiveRegistry.getConfigString(config, "command", "target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            if (commandId == null || commandId.isEmpty()) {
                return null;
            }
            return new CommandObjective(id, commandId, amount, milestones);
        });
        ObjectiveRegistry.register("placeholder", (id, config) -> {
            String placeholder = ObjectiveRegistry.getConfigString(config, "placeholder", "target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            if (placeholder == null || placeholder.isEmpty()) {
                return null;
            }
            return new PlaceholderObjective(id, placeholder, amount, milestones);
        });
        ObjectiveRegistry.register("craft", (id, config) -> {
            String itemName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            if (itemName == null) {
                return null;
            }
            if (itemName.equalsIgnoreCase("ANY")) {
                return new CraftObjective(id, null, amount, milestones);
            }
            try {
                Material itemType = Material.valueOf((String)itemName.toUpperCase());
                return new CraftObjective(id, itemType, amount, milestones);
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        });
        ObjectiveRegistry.register("smelt", (id, config) -> {
            String itemName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            if (itemName == null) {
                return null;
            }
            try {
                Material itemType = Material.valueOf((String)itemName.toUpperCase());
                return new SmeltObjective(id, itemType, amount, milestones);
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        });
        ObjectiveRegistry.register("fish", (id, config) -> {
            String itemName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            Material fishType = null;
            if (itemName != null && !itemName.equalsIgnoreCase("ANY")) {
                try {
                    fishType = Material.valueOf((String)itemName.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return new FishObjective(id, fishType, amount, milestones);
        });
        ObjectiveRegistry.register("enchant", (id, config) -> {
            String itemName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            Material itemType = null;
            if (itemName != null && !itemName.equalsIgnoreCase("ANY")) {
                try {
                    itemType = Material.valueOf((String)itemName.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return new EnchantObjective(id, itemType, amount, milestones);
        });
        ObjectiveRegistry.register("consume", (id, config) -> {
            String itemName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            if (itemName == null) {
                return null;
            }
            try {
                Material itemType = Material.valueOf((String)itemName.toUpperCase());
                return new ConsumeObjective(id, itemType, amount, milestones);
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        });
        ObjectiveRegistry.register("tame", (id, config) -> {
            String entityName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            EntityType entityType = null;
            if (entityName != null && !entityName.equalsIgnoreCase("ANY")) {
                try {
                    entityType = EntityType.valueOf((String)entityName.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return new TameObjective(id, entityType, amount, milestones);
        });
        ObjectiveRegistry.register("trade", (id, config) -> {
            String itemName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            Material itemType = null;
            if (itemName != null && !itemName.equalsIgnoreCase("ANY")) {
                try {
                    itemType = Material.valueOf((String)itemName.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return new TradeObjective(id, itemType, amount, milestones);
        });
        ObjectiveRegistry.register("brew", (id, config) -> {
            String itemName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            Material potionType = null;
            if (itemName != null && !itemName.equalsIgnoreCase("ANY")) {
                try {
                    potionType = Material.valueOf((String)itemName.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return new BrewObjective(id, potionType, amount, milestones);
        });
        ObjectiveRegistry.register("breed", (id, config) -> {
            String entityName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            EntityType entityType = null;
            if (entityName != null && !entityName.equalsIgnoreCase("ANY")) {
                try {
                    entityType = EntityType.valueOf((String)entityName.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return new BreedObjective(id, entityType, amount, milestones);
        });
        ObjectiveRegistry.register("shear", (id, config) -> {
            String entityName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            EntityType entityType = null;
            if (entityName != null && !entityName.equalsIgnoreCase("ANY")) {
                try {
                    entityType = EntityType.valueOf((String)entityName.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return new ShearObjective(id, entityType, amount, milestones);
        });
        ObjectiveRegistry.register("sleep", (id, config) -> {
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            return new SleepObjective(id, amount, milestones);
        });
        ObjectiveRegistry.register("heal", (id, config) -> {
            String reasonName = config.getString("reason");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            EntityRegainHealthEvent.RegainReason reason = null;
            if (reasonName != null && !reasonName.equalsIgnoreCase("ANY")) {
                try {
                    reason = EntityRegainHealthEvent.RegainReason.valueOf((String)reasonName.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return new HealObjective(id, reason, amount, milestones);
        });
        ObjectiveRegistry.register("drop", (id, config) -> {
            String itemName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            Material itemType = null;
            if (itemName != null && !itemName.equalsIgnoreCase("ANY")) {
                try {
                    itemType = Material.valueOf((String)itemName.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return new DropObjective(id, itemType, amount, milestones);
        });
        ObjectiveRegistry.register("damage", (id, config) -> {
            String entityName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            EntityType entityType = null;
            if (entityName != null && !entityName.equalsIgnoreCase("ANY")) {
                try {
                    entityType = EntityType.valueOf((String)entityName.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return new DamageObjective(id, entityType, amount, milestones);
        });
        ObjectiveRegistry.register("death", (id, config) -> {
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            return new DeathObjective(id, amount, milestones);
        });
        ObjectiveRegistry.register("reachlevel", (id, config) -> {
            int level = config.getInt("level", 1);
            List milestones = config.getIntegerList("milestones");
            return new LevelObjective(id, level, true, milestones);
        });
        ObjectiveRegistry.register("gainlevel", (id, config) -> {
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            return new LevelObjective(id, amount, false, milestones);
        });
        ObjectiveRegistry.register("level", (id, config) -> {
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            return new LevelObjective(id, amount, false, milestones);
        });
        ObjectiveRegistry.register("move", (id, config) -> {
            int distance = config.getInt("amount", config.getInt("distance", 100));
            List milestones = config.getIntegerList("milestones");
            return new MoveObjective(id, distance, milestones);
        });
        ObjectiveRegistry.register("jump", (id, config) -> {
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            return new JumpObjective(id, amount, milestones);
        });
        ObjectiveRegistry.register("chat", (id, config) -> {
            String text = ObjectiveRegistry.getConfigString(config, "text", "target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            return new ChatObjective(id, text, amount, milestones);
        });
        ObjectiveRegistry.register("interact", (id, config) -> {
            String blockName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            Material blockType = null;
            if (blockName != null && !blockName.equalsIgnoreCase("ANY")) {
                try {
                    blockType = Material.valueOf((String)blockName.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return new InteractObjective(id, blockType, amount, milestones);
        });
        ObjectiveRegistry.register("bowshoot", (id, config) -> {
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            return new BowShootObjective(id, amount, milestones);
        });
        ObjectiveRegistry.register("shoot_bow", (id, config) -> {
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            return new BowShootObjective(id, amount, milestones);
        });
        ObjectiveRegistry.register("projectile", (id, config) -> {
            String projectileName = config.getString("projectile");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            EntityType projectileType = null;
            if (projectileName != null && !projectileName.equalsIgnoreCase("ANY")) {
                try {
                    projectileType = EntityType.valueOf((String)projectileName.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return new ProjectileObjective(id, projectileType, amount, milestones);
        });
        ObjectiveRegistry.register("firework", (id, config) -> {
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            return new FireworkObjective(id, amount, milestones);
        });
        ObjectiveRegistry.register("launch_firework", (id, config) -> {
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            return new FireworkObjective(id, amount, milestones);
        });
        ObjectiveRegistry.register("vehicle", (id, config) -> {
            String vehicleName = ObjectiveRegistry.getConfigString(config, "vehicle", "target");
            int distance = config.getInt("amount", config.getInt("distance", 100));
            List milestones = config.getIntegerList("milestones");
            EntityType vehicleType = null;
            if (vehicleName != null && !vehicleName.equalsIgnoreCase("ANY")) {
                try {
                    vehicleType = EntityType.valueOf((String)vehicleName.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return new VehicleObjective(id, vehicleType, distance, milestones);
        });
        ObjectiveRegistry.register("ride_vehicle", (id, config) -> {
            String vehicleName = ObjectiveRegistry.getConfigString(config, "vehicle", "target");
            int distance = config.getInt("amount", config.getInt("distance", 100));
            List milestones = config.getIntegerList("milestones");
            EntityType vehicleType = null;
            if (vehicleName != null && !vehicleName.equalsIgnoreCase("ANY")) {
                try {
                    vehicleType = EntityType.valueOf((String)vehicleName.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return new VehicleObjective(id, vehicleType, distance, milestones);
        });
        ObjectiveRegistry.register("harvest", (id, config) -> {
            String cropName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            Material cropType = null;
            if (cropName != null && !cropName.equalsIgnoreCase("ANY")) {
                try {
                    cropType = Material.valueOf((String)cropName.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return new HarvestObjective(id, cropType, amount, milestones);
        });
        ObjectiveRegistry.register("elytra_fly", (id, config) -> {
            int amount = config.getInt("amount", 100);
            List milestones = config.getIntegerList("milestones");
            return new ElytraFlyObjective(id, amount, milestones);
        });
        ObjectiveRegistry.register("explore_biome", (id, config) -> {
            String biomeName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            return new ExploreBiomeObjective(id, biomeName, amount, milestones);
        });
        ObjectiveRegistry.register("xp_pickup", (id, config) -> {
            int amount = config.getInt("amount", 100);
            List milestones = config.getIntegerList("milestones");
            return new XpPickupObjective(id, amount, milestones);
        });
        ObjectiveRegistry.register("anvil_repair", (id, config) -> {
            String itemName = config.getString("target");
            int amount = config.getInt("amount", 1);
            List milestones = config.getIntegerList("milestones");
            Material itemType = null;
            if (itemName != null && !itemName.equalsIgnoreCase("ANY")) {
                try {
                    itemType = Material.valueOf((String)itemName.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return new AnvilRepairObjective(id, itemType, amount, milestones);
        });
        if (ObjectiveRegistry.isMythicMobsInstalled()) {
            ObjectiveRegistry.register("kill_mythicmob", (id, config) -> {
                String mobType = config.getString("target");
                int amount = config.getInt("amount", 1);
                List milestones = config.getIntegerList("milestones");
                if (mobType == null) {
                    return null;
                }
                return new KillMythicMobObjective(id, mobType, amount, milestones);
            });
        }
    }

    public static boolean isMythicMobsInstalled() {
        try {
            Class.forName("io.lumine.mythic.bukkit.MythicBukkit");
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void register(String type, ObjectiveFactory factory) {
        factories.put(type.toLowerCase(), factory);
    }

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

    public static Objective deserialize(String objectiveId, String serialized) {
        int amount;
        String[] parts = serialized.split(":");
        if (parts.length < 3) {
            return null;
        }
        String type = parts[0].toLowerCase();
        String target = parts[1];
        try {
            amount = Integer.parseInt(parts[2]);
        }
        catch (NumberFormatException e) {
            return null;
        }
        switch (type) {
            case "break": {
                try {
                    Material blockType = Material.valueOf((String)target.toUpperCase());
                    return new BreakObjective(objectiveId, blockType, amount);
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            case "place": {
                try {
                    Material blockType = Material.valueOf((String)target.toUpperCase());
                    return new PlaceObjective(objectiveId, blockType, amount);
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            case "kill": {
                String targetUpper;
                return switch (targetUpper = target.toUpperCase()) {
                    case "ANY" -> {
                        KillObjective var11_42;
                        yield var11_42 = new KillObjective(objectiveId, null, amount, KillObjective.EntityFilter.ANY);
                    }
                    case "HOSTILE" -> {
                        KillObjective var11_43;
                        yield var11_43 = new KillObjective(objectiveId, null, amount, KillObjective.EntityFilter.HOSTILE);
                    }
                    case "PASSIVE" -> {
                        KillObjective var11_44;
                        yield var11_44 = new KillObjective(objectiveId, null, amount, KillObjective.EntityFilter.PASSIVE);
                    }
                    default -> {
                        try {
                            KillObjective var11_45;
                            EntityType entityType = EntityType.valueOf((String)targetUpper);
                            yield var11_45 = new KillObjective(objectiveId, entityType, amount, KillObjective.EntityFilter.SPECIFIC);
                        }
                        catch (IllegalArgumentException e) {
                            KillObjective var11_46;
                            yield var11_46 = null;
                        }
                    }
                };
            }
            case "collect": {
                try {
                    Material itemType = Material.valueOf((String)target.toUpperCase());
                    return new CollectObjective(objectiveId, itemType, amount);
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            case "command": {
                return new CommandObjective(objectiveId, target, amount);
            }
            case "placeholder": {
                return new PlaceholderObjective(objectiveId, target, amount);
            }
            case "level": {
                return new LevelObjective(objectiveId, amount, false);
            }
            case "craft": {
                try {
                    Material itemType = Material.valueOf((String)target.toUpperCase());
                    return new CraftObjective(objectiveId, itemType, amount);
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            case "smelt": {
                try {
                    Material itemType = Material.valueOf((String)target.toUpperCase());
                    return new SmeltObjective(objectiveId, itemType, amount);
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            case "fish": {
                Material fishType = target.equalsIgnoreCase("ANY") ? null : Material.valueOf((String)target.toUpperCase());
                return new FishObjective(objectiveId, fishType, amount);
            }
            case "enchant": {
                Material itemType = target.equalsIgnoreCase("ANY") ? null : Material.valueOf((String)target.toUpperCase());
                return new EnchantObjective(objectiveId, itemType, amount);
            }
            case "consume": {
                try {
                    Material itemType = Material.valueOf((String)target.toUpperCase());
                    return new ConsumeObjective(objectiveId, itemType, amount);
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            }
            case "tame": {
                EntityType entityType = target.equalsIgnoreCase("ANY") ? null : EntityType.valueOf((String)target.toUpperCase());
                return new TameObjective(objectiveId, entityType, amount);
            }
            case "trade": {
                Material itemType = target.equalsIgnoreCase("ANY") ? null : Material.valueOf((String)target.toUpperCase());
                return new TradeObjective(objectiveId, itemType, amount);
            }
            case "brew": {
                Material potionType = target.equalsIgnoreCase("ANY") ? null : Material.valueOf((String)target.toUpperCase());
                return new BrewObjective(objectiveId, potionType, amount);
            }
            case "breed": {
                EntityType entityType = target.equalsIgnoreCase("ANY") ? null : EntityType.valueOf((String)target.toUpperCase());
                return new BreedObjective(objectiveId, entityType, amount);
            }
            case "shear": {
                EntityType entityType = target.equalsIgnoreCase("ANY") ? null : EntityType.valueOf((String)target.toUpperCase());
                return new ShearObjective(objectiveId, entityType, amount);
            }
            case "sleep": {
                return new SleepObjective(objectiveId, amount);
            }
            case "heal": {
                EntityRegainHealthEvent.RegainReason reason = target.equalsIgnoreCase("ANY") ? null : EntityRegainHealthEvent.RegainReason.valueOf((String)target.toUpperCase());
                return new HealObjective(objectiveId, reason, amount);
            }
            case "drop": {
                Material itemType = target.equalsIgnoreCase("ANY") ? null : Material.valueOf((String)target.toUpperCase());
                return new DropObjective(objectiveId, itemType, amount);
            }
            case "damage": {
                EntityType entityType = target.equalsIgnoreCase("ANY") ? null : EntityType.valueOf((String)target.toUpperCase());
                return new DamageObjective(objectiveId, entityType, amount);
            }
            case "death": {
                return new DeathObjective(objectiveId, amount);
            }
            case "reachlevel": 
            case "gainlevel": {
                boolean isReach = type.equals("reachlevel");
                return new LevelObjective(objectiveId, amount, isReach);
            }
            case "move": {
                return new MoveObjective(objectiveId, amount);
            }
            case "jump": {
                return new JumpObjective(objectiveId, amount);
            }
            case "chat": {
                String text = target.equalsIgnoreCase("ANY") ? null : target;
                return new ChatObjective(objectiveId, text, amount);
            }
            case "interact": {
                Material blockType = target.equalsIgnoreCase("ANY") ? null : Material.valueOf((String)target.toUpperCase());
                return new InteractObjective(objectiveId, blockType, amount);
            }
            case "bowshoot": {
                return new BowShootObjective(objectiveId, amount);
            }
            case "projectile": {
                EntityType projectileType = target.equalsIgnoreCase("ANY") ? null : EntityType.valueOf((String)target.toUpperCase());
                return new ProjectileObjective(objectiveId, projectileType, amount);
            }
            case "firework": {
                return new FireworkObjective(objectiveId, amount);
            }
            case "vehicle": {
                EntityType vehicleType = target.equalsIgnoreCase("ANY") ? null : EntityType.valueOf((String)target.toUpperCase());
                return new VehicleObjective(objectiveId, vehicleType, amount);
            }
            case "kill_mythicmob": {
                if (ObjectiveRegistry.isMythicMobsInstalled()) {
                    return new KillMythicMobObjective(objectiveId, target, amount);
                }
                return null;
            }
            case "harvest": {
                Material cropType = target.equalsIgnoreCase("ANY") ? null : Material.valueOf((String)target.toUpperCase());
                return new HarvestObjective(objectiveId, cropType, amount);
            }
            case "elytra_fly": {
                return new ElytraFlyObjective(objectiveId, amount);
            }
            case "explore_biome": {
                String targetBiome = target.equalsIgnoreCase("ANY") ? null : target;
                return new ExploreBiomeObjective(objectiveId, targetBiome, amount);
            }
            case "xp_pickup": {
                return new XpPickupObjective(objectiveId, amount);
            }
            case "anvil_repair": {
                Material itemType = target.equalsIgnoreCase("ANY") ? null : Material.valueOf((String)target.toUpperCase());
                return new AnvilRepairObjective(objectiveId, itemType, amount);
            }
        }
        return null;
    }

    public static boolean isRegistered(String type) {
        return factories.containsKey(type.toLowerCase());
    }

    public static void registerType(ObjectiveType type) {
        types.put(type.getId().toLowerCase(), type);
    }

    public static ObjectiveType getType(String id) {
        return types.get(id.toLowerCase());
    }

    public static Map<String, ObjectiveType> getAllTypes() {
        return new HashMap<String, ObjectiveType>(types);
    }

    public static Map<String, ObjectiveFactory> getRegisteredTypes() {
        return new HashMap<String, ObjectiveFactory>(factories);
    }

    public static class ValidationResult {
        public final boolean valid;
        public final String errorMessage;
        public final String[] missingFields;

        private ValidationResult(boolean valid, String errorMessage, String ... missingFields) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.missingFields = missingFields;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null, new String[0]);
        }

        public static ValidationResult failure(String errorMessage, String ... missingFields) {
            return new ValidationResult(false, errorMessage, missingFields);
        }
    }

    @FunctionalInterface
    public static interface ObjectiveFactory {
        public Objective create(String var1, ConfigurationSection var2);
    }
}

