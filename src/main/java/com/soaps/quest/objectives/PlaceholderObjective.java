package com.soaps.quest.objectives;

import java.util.List;
import java.util.UUID;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class PlaceholderObjective extends AbstractObjective {
    private final String placeholder;

    private static boolean isPlaceholderApiEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public PlaceholderObjective(String objectiveId, String placeholder, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.placeholder = normalizePlaceholderKey(placeholder);
    }

    public PlaceholderObjective(String objectiveId, String placeholder, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.placeholder = normalizePlaceholderKey(placeholder);
    }

    public static String normalizePlaceholderKey(String raw) {
        if (raw == null) {
            return "";
        }
        String key = raw.trim();
        if (key.startsWith("%") && key.endsWith("%") && key.length() > 2) {
            key = key.substring(1, key.length() - 1);
        }
        return key;
    }

    public static String toPlaceholderExpression(String key) {
        String normalized = normalizePlaceholderKey(key);
        if (normalized.isEmpty()) {
            return "";
        }
        return "%" + normalized + "%";
    }

    @Override
    public String getType() {
        return "placeholder";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        return false;
    }

    public boolean checkPlaceholder(Player player) {
        if (!PlaceholderObjective.isPlaceholderApiEnabled()) {
            return false;
        }
        String expression = PlaceholderObjective.toPlaceholderExpression(this.placeholder);
        if (expression.isEmpty()) {
            return false;
        }
        try {
            String result = PlaceholderAPI.setPlaceholders(player, expression);
            int value = Integer.parseInt(result.trim());
            int oldProgress = this.getCurrentProgress(player.getUniqueId());
            if (value != oldProgress) {
                this.setCurrentProgress(player.getUniqueId(), value);
                return true;
            }
        } catch (NumberFormatException ignored) {
            return false;
        }
        return false;
    }

    @Override
    public boolean isComplete(UUID playerUUID) {
        return this.getCurrentProgress(playerUUID) >= this.requiredAmount;
    }

    @Override
    public String getDescription() {
        return "Reach " + this.requiredAmount + " for %" + this.placeholder + "%";
    }

    @Override
    public String serialize() {
        return "placeholder:" + this.placeholder + ":" + this.requiredAmount;
    }

    public String getPlaceholder() {
        return this.placeholder;
    }

    public static boolean isPlaceholderAPIEnabled() {
        return PlaceholderObjective.isPlaceholderApiEnabled();
    }
}
