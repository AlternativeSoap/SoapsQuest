package com.soaps.quest.objectives;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandObjective extends AbstractObjective {
    private final String commandId;

    public CommandObjective(String objectiveId, String commandId, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.commandId = commandId;
    }

    public CommandObjective(String objectiveId, String commandId, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.commandId = commandId;
    }

    @Override
    public String getType() {
        return "command";
    }

    public static String normalizeCommand(String raw) {
        if (raw == null) {
            return "";
        }
        String normalized = raw.trim();
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        return normalized.toLowerCase(Locale.ROOT);
    }

    public boolean matchesCommand(String executedLine) {
        String expected = normalizeCommand(this.commandId);
        String actual = normalizeCommand(executedLine);
        if (expected.isEmpty() || actual.isEmpty()) {
            return false;
        }
        return actual.equals(expected) || actual.startsWith(expected + " ");
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof PlayerCommandPreprocessEvent commandEvent)) {
            return false;
        }
        String message = commandEvent.getMessage();
        if (message == null || message.isBlank()) {
            return false;
        }
        if (!this.matchesCommand(message.startsWith("/") ? message.substring(1) : message)) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        if (this.getCurrentProgress(playerId) >= this.requiredAmount) {
            return false;
        }
        this.incrementProgress(playerId);
        return true;
    }

    public boolean trigger(UUID playerUUID, String triggerId) {
        if (!this.matchesCommand(triggerId)) {
            return false;
        }
        if (this.getCurrentProgress(playerUUID) >= this.requiredAmount) {
            return false;
        }
        this.incrementProgress(playerUUID);
        return true;
    }

    @Override
    public String getDescription() {
        return "Run '/" + normalizeCommand(this.commandId) + "' " + this.requiredAmount + " times";
    }

    @Override
    public String serialize() {
        return "command:" + this.commandId + ":" + this.requiredAmount;
    }

    public String getCommandId() {
        return this.commandId;
    }
}
