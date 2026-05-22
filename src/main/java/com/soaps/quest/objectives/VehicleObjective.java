/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Vehicle
 *  org.bukkit.event.Event
 *  org.bukkit.event.vehicle.VehicleMoveEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event;
import org.bukkit.event.vehicle.VehicleMoveEvent;

public class VehicleObjective
extends AbstractObjective {
    private final EntityType vehicleType;
    private final Map<UUID, Location> lastVehicleLocations = new HashMap<UUID, Location>();
    private final Map<UUID, Double> fractionalDistance = new HashMap<UUID, Double>();

    public VehicleObjective(String objectiveId, EntityType vehicleType, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.vehicleType = vehicleType;
    }

    public VehicleObjective(String objectiveId, EntityType vehicleType, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.vehicleType = vehicleType;
    }

    public EntityType getVehicleType() {
        return this.vehicleType;
    }

    @Override
    public void resetProgress(UUID playerUUID) {
        super.resetProgress(playerUUID);
        this.lastVehicleLocations.remove(playerUUID);
        this.fractionalDistance.remove(playerUUID);
    }

    @Override
    public String getType() {
        return "vehicle";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof VehicleMoveEvent)) {
            return false;
        }
        VehicleMoveEvent vehicleEvent = (VehicleMoveEvent)event;
        Vehicle vehicle = vehicleEvent.getVehicle();
        if (this.vehicleType != null && vehicle.getType() != this.vehicleType) {
            return false;
        }
        boolean isPlayerPassenger = false;
        for (Entity passenger : vehicle.getPassengers()) {
            Player p;
            if (!(passenger instanceof Player) || !(p = (Player)passenger).getUniqueId().equals(player.getUniqueId())) continue;
            isPlayerPassenger = true;
            break;
        }
        if (!isPlayerPassenger) {
            return false;
        }
        UUID vehicleId = vehicle.getUniqueId();
        Location lastLoc = this.lastVehicleLocations.get(vehicleId);
        if (lastLoc == null || !lastLoc.getWorld().equals((Object)vehicleEvent.getTo().getWorld())) {
            this.lastVehicleLocations.put(vehicleId, vehicleEvent.getTo().clone());
            return false;
        }
        double distance = lastLoc.distance(vehicleEvent.getTo());
        UUID playerId = player.getUniqueId();
        double accumulated = this.fractionalDistance.getOrDefault(playerId, 0.0) + distance;
        int distanceBlocks = (int)Math.floor(accumulated);
        if (distanceBlocks > 0) {
            this.fractionalDistance.put(playerId, accumulated - (double)distanceBlocks);
            this.incrementProgress(playerId, distanceBlocks);
            this.lastVehicleLocations.put(vehicleId, vehicleEvent.getTo().clone());
            return true;
        }
        this.fractionalDistance.put(playerId, accumulated);
        this.lastVehicleLocations.put(vehicleId, vehicleEvent.getTo().clone());
        return false;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        String vehicleName = this.vehicleType != null ? this.vehicleType.name() : "VEHICLE";
        return current + "/" + this.getRequiredAmount() + " blocks in " + vehicleName;
    }

    @Override
    public String getDescription() {
        String vehicleName = this.vehicleType != null ? this.formatName(this.vehicleType.name()) : "any vehicle";
        return "Travel " + this.getRequiredAmount() + " blocks in " + vehicleName;
    }

    @Override
    public String serialize() {
        String vehicle = this.vehicleType != null ? this.vehicleType.name() : "ANY";
        return this.getType() + ":" + vehicle + ":" + this.getRequiredAmount();
    }

    public static VehicleObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid vehicle objective data: " + data);
        }
        EntityType type = parts[1].equalsIgnoreCase("ANY") ? null : EntityType.valueOf((String)parts[1].toUpperCase());
        int amount = Integer.parseInt(parts[2]);
        return new VehicleObjective(objectiveId, type, amount);
    }
}

