/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Ambient
 *  org.bukkit.entity.Animals
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.entity.EntityDeathEvent
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import java.util.List;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillObjective
extends AbstractObjective {
    private final EntityType entityType;
    private final EntityFilter filter;

    public KillObjective(String objectiveId, EntityType entityType, int requiredAmount) {
        this(objectiveId, entityType, requiredAmount, EntityFilter.SPECIFIC);
    }

    public KillObjective(String objectiveId, EntityType entityType, int requiredAmount, EntityFilter filter) {
        super(objectiveId, requiredAmount);
        this.entityType = entityType;
        this.filter = filter;
    }

    public KillObjective(String objectiveId, EntityType entityType, int requiredAmount, EntityFilter filter, List<Integer> milestones) {
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
        EntityDeathEvent deathEvent = (EntityDeathEvent)event;
        Player killer = deathEvent.getEntity().getKiller();
        if (killer == null || !killer.equals((Object)player)) {
            return false;
        }
        LivingEntity entity = deathEvent.getEntity();
        return switch (this.filter) {
            case SPECIFIC -> entity.getType() == this.entityType;
            case ANY -> !(entity instanceof Player);
            case HOSTILE -> this.isHostileMob(entity);
            case PASSIVE -> this.isPassiveMob(entity);
        };
    }

    private boolean isHostileMob(LivingEntity entity) {
        if (entity == null || entity instanceof Player) {
            return false;
        }
        EntityType type = entity.getType();
        return switch (type) {
            case EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON, EntityType.CREEPER, EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.SILVERFISH, EntityType.PHANTOM, EntityType.SHULKER, EntityType.HOGLIN, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.ZOGLIN, EntityType.ENDER_DRAGON, EntityType.WITHER, EntityType.SLIME, EntityType.MAGMA_CUBE, EntityType.GHAST, EntityType.BLAZE, EntityType.ELDER_GUARDIAN, EntityType.GUARDIAN, EntityType.WITCH, EntityType.VEX, EntityType.EVOKER, EntityType.VINDICATOR, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.ZOMBIFIED_PIGLIN, EntityType.BREEZE, EntityType.WARDEN, EntityType.BOGGED -> true;
            default -> false;
        };
    }

    private boolean isPassiveMob(LivingEntity entity) {
        if (entity == null || entity instanceof Player) {
            return false;
        }
        if (entity instanceof Animals) {
            return true;
        }
        if (entity instanceof Ambient) {
            return true;
        }
        EntityType type = entity.getType();
        return switch (type) {
            case EntityType.VILLAGER, EntityType.WANDERING_TRADER, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.STRIDER, EntityType.ALLAY, EntityType.FROG, EntityType.TADPOLE, EntityType.CAMEL, EntityType.SNIFFER, EntityType.GLOW_SQUID, EntityType.AXOLOTL, EntityType.SQUID, EntityType.COD, EntityType.SALMON, EntityType.PUFFERFISH, EntityType.TROPICAL_FISH, EntityType.DOLPHIN, EntityType.TURTLE, EntityType.ARMADILLO -> true;
            default -> false;
        };
    }

    @Override
    public String getDescription() {
        return switch (this.filter.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> "Kill " + this.requiredAmount + " " + this.formatName(this.entityType.name());
            case 1 -> "Kill " + this.requiredAmount + " entities";
            case 2 -> "Kill " + this.requiredAmount + " hostile mobs";
            case 3 -> "Kill " + this.requiredAmount + " passive mobs";
        };
    }

    @Override
    public String serialize() {
        String target = switch (this.filter.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.entityType.name();
            case 1 -> "ANY";
            case 2 -> "HOSTILE";
            case 3 -> "PASSIVE";
        };
        return "kill:" + target + ":" + this.requiredAmount;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public static enum EntityFilter {
        SPECIFIC,
        ANY,
        HOSTILE,
        PASSIVE;

    }
}

