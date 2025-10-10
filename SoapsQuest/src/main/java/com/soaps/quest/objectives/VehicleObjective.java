package com.soaps.quest.objectives;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Objective that tracks vehicle travel distance.
 * Can track specific vehicle types or any vehicle.
 */
public class VehicleObjective extends AbstractObjective {
    
    private final EntityType vehicleType;
    private final Map<UUID, Location> lastVehicleLocations = new HashMap<>();
    
    public VehicleObjective(String objectiveId, EntityType vehicleType, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.vehicleType = vehicleType;
    }
    
    public VehicleObjective(String objectiveId, EntityType vehicleType, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.vehicleType = vehicleType;
    }
    
    public EntityType getVehicleType() {
        return vehicleType;
    }
    
    @Override
    public String getType() {
        return "vehicle";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof VehicleMoveEvent vehicleEvent)) {
            return false;
        }
        
        Vehicle vehicle = vehicleEvent.getVehicle();
        
        // Check vehicle type if specified
        if (vehicleType != null && vehicle.getType() != vehicleType) {
            return false;
        }
        
        // Check if player is passenger
        boolean isPlayerPassenger = false;
        for (Entity passenger : vehicle.getPassengers()) {
            if (passenger instanceof Player p && p.getUniqueId().equals(player.getUniqueId())) {
                isPlayerPassenger = true;
                break;
            }
        }
        
        if (!isPlayerPassenger) {
            return false;
        }
        
        UUID vehicleId = vehicle.getUniqueId();
        Location lastLoc = lastVehicleLocations.get(vehicleId);
        
        if (lastLoc == null || !lastLoc.getWorld().equals(vehicleEvent.getTo().getWorld())) {
            lastVehicleLocations.put(vehicleId, vehicleEvent.getTo().clone());
            return false;
        }
        
        // Calculate distance traveled
        double distance = lastLoc.distance(vehicleEvent.getTo());
        int distanceBlocks = (int) Math.floor(distance);
        
        if (distanceBlocks > 0) {
            UUID playerId = player.getUniqueId();
            incrementProgress(playerId, distanceBlocks);
            lastVehicleLocations.put(vehicleId, vehicleEvent.getTo().clone());
            return true;
        }
        
        return false;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        String vehicleName = vehicleType != null ? vehicleType.name() : "VEHICLE";
        return current + "/" + getRequiredAmount() + " blocks in " + vehicleName;
    }
    
    @Override
    public String getDescription() {
        String vehicleName = vehicleType != null ? vehicleType.name() : "any vehicle";
        return "Travel " + getRequiredAmount() + " blocks in " + vehicleName;
    }
    
    @Override
    public String serialize() {
        String vehicle = vehicleType != null ? vehicleType.name() : "ANY";
        return getType() + ":" + vehicle + ":" + getRequiredAmount();
    }
    
    /**
     * Deserialize a VehicleObjective from a string.
     * Format: vehicle:ENTITY_TYPE:amount (use "ANY" for any vehicle)
     */
    public static VehicleObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid vehicle objective data: " + data);
        }
        
        EntityType type = parts[1].equalsIgnoreCase("ANY") ? null : EntityType.valueOf(parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        
        return new VehicleObjective(objectiveId, type, amount);
    }
}
