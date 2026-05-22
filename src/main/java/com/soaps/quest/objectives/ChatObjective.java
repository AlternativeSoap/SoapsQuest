/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.event.player.AsyncChatEvent
 *  net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.AbstractObjective;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class ChatObjective
extends AbstractObjective {
    private final String requiredText;

    public ChatObjective(String objectiveId, String requiredText, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.requiredText = requiredText;
    }

    public ChatObjective(String objectiveId, String requiredText, int requiredAmount, List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.requiredText = requiredText;
    }

    public String getRequiredText() {
        return this.requiredText;
    }

    @Override
    public String getType() {
        return "chat";
    }

    @Override
    public boolean handleEvent(Player player, Event event) {
        String message;
        if (!(event instanceof AsyncChatEvent)) {
            return false;
        }
        AsyncChatEvent chatEvent = (AsyncChatEvent)event;
        if (this.requiredText != null && !(message = PlainTextComponentSerializer.plainText().serialize(chatEvent.message()).toLowerCase()).contains(this.requiredText.toLowerCase())) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        this.incrementProgress(playerId);
        return true;
    }

    @Override
    public String getProgressString(UUID playerId) {
        int current = this.getCurrentProgress(playerId);
        String textPart = this.requiredText != null ? " containing '" + this.requiredText + "'" : "";
        return current + "/" + this.getRequiredAmount() + " messages" + textPart;
    }

    @Override
    public String getDescription() {
        String textPart = this.requiredText != null ? " containing '" + this.requiredText + "'" : "";
        return "Send " + this.getRequiredAmount() + " messages" + textPart;
    }

    @Override
    public String serialize() {
        String text = this.requiredText != null ? this.requiredText : "ANY";
        return this.getType() + ":" + text + ":" + this.getRequiredAmount();
    }

    public static ChatObjective deserialize(String objectiveId, String data) {
        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid chat objective data: " + data);
        }
        String text = parts[1].equalsIgnoreCase("ANY") ? null : parts[1];
        int amount = Integer.parseInt(parts[2]);
        return new ChatObjective(objectiveId, text, amount);
    }
}

