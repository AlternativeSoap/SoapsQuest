package com.soaps.quest.objectives;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Keyed;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

public class ExploreBiomeObjective extends AbstractObjective {
    private final String targetBiomeKey;
    private final Map<UUID, String> lastBiomeKey = new HashMap<>();

    public ExploreBiomeObjective(String objectiveId, String targetBiomeKey, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.targetBiomeKey = ExploreBiomeObjective.normalizeBiomeKey(targetBiomeKey);
    }

    public ExploreBiomeObjective(String objectiveId, String targetBiomeKey, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.targetBiomeKey = ExploreBiomeObjective.normalizeBiomeKey(targetBiomeKey);
    }

    public static String normalizeBiomeKey(String raw) {
        if (raw == null || raw.isBlank() || raw.equalsIgnoreCase("ANY")) {
            return null;
        }
        String normalized = raw.trim();
        if (normalized.contains(":")) {
            normalized = normalized.substring(normalized.indexOf(':') + 1);
        }
        return normalized.toLowerCase(Locale.ROOT);
    }

    public static String biomeKey(Biome biome) {
        if (biome == null) {
            return "";
        }
        if (biome instanceof Keyed keyed) {
            return keyed.getKey().getKey().toLowerCase(Locale.ROOT);
        }
        return biome.name().toLowerCase(Locale.ROOT);
    }

    private boolean matchesTargetBiome(Biome biome) {
        if (this.targetBiomeKey == null) {
            return true;
        }
        String current = ExploreBiomeObjective.biomeKey(biome);
        if (current.equals(this.targetBiomeKey)) {
            return true;
        }
        return biome.name().equalsIgnoreCase(this.targetBiomeKey);
    }

    @Override
    public String getType() {
        return "explore_biome";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerMoveEvent moveEvent)) {
            return false;
        }
        if (moveEvent.getFrom().getBlockX() == moveEvent.getTo().getBlockX()
                && moveEvent.getFrom().getBlockZ() == moveEvent.getTo().getBlockZ()) {
            return false;
        }
        Biome currentBiome = player.getLocation().getBlock().getBiome();
        String currentKey = ExploreBiomeObjective.biomeKey(currentBiome);
        String previousKey = this.lastBiomeKey.getOrDefault(player.getUniqueId(), currentKey);
        this.lastBiomeKey.put(player.getUniqueId(), currentKey);
        if (currentKey.equals(previousKey)) {
            return false;
        }
        if (!this.matchesTargetBiome(currentBiome)) {
            return false;
        }
        this.incrementProgress(player.getUniqueId());
        return true;
    }

    @Override
    public String getDescription() {
        if (this.targetBiomeKey != null) {
            return "Visit " + this.formatName(this.targetBiomeKey) + " biome " + this.requiredAmount + " time(s)";
        }
        return "Explore " + this.requiredAmount + " distinct biome(s)";
    }

    @Override
    public String serialize() {
        String biome = this.targetBiomeKey != null ? this.targetBiomeKey.toUpperCase(Locale.ROOT) : "ANY";
        return "explore_biome:" + biome + ":" + this.requiredAmount;
    }

    public String getTargetBiomeKey() {
        return this.targetBiomeKey;
    }

    @Override
    public void resetProgress(UUID playerUUID) {
        super.resetProgress(playerUUID);
        this.lastBiomeKey.remove(playerUUID);
    }
}
