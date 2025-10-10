package com.soaps.quest.objectives;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import io.papermc.paper.event.player.AsyncChatEvent;

import java.util.UUID;

/**
 * Objective that tracks chat messages sent.
 * Can track total messages or messages containing specific text.
 */
public class ChatObjective extends AbstractObjective {
    
    private final String requiredText; // null = any message
    
    public ChatObjective(String objectiveId, String requiredText, int requiredAmount) {
        super(objectiveId, requiredAmount);
        this.requiredText = requiredText;
    }
    
    public ChatObjective(String objectiveId, String requiredText, int requiredAmount, java.util.List<Integer> milestones) {
        super(objectiveId, requiredAmount, milestones);
        this.requiredText = requiredText;
    }
    
    public String getRequiredText() {
        return requiredText;
    }
    
    @Override
    public String getType() {
        return "chat";
    }
    
    @Override
    public boolean handleEvent(Player player, Event event) {
        if (!(event instanceof AsyncChatEvent chatEvent)) {
            return false;
        }
        
        // Check if text matches (if specified)
        if (requiredText != null) {
            String message = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                .serialize(chatEvent.message()).toLowerCase();
            
            if (!message.contains(requiredText.toLowerCase())) {
                return false;
            }
        }
        
        UUID playerId = player.getUniqueId();
        incrementProgress(playerId);
        
        return true;
    }
    
    @Override
    public String getProgressString(UUID playerId) {
        int current = getCurrentProgress(playerId);
        String textPart = requiredText != null ? " containing '" + requiredText + "'" : "";
        return current + "/" + getRequiredAmount() + " messages" + textPart;
    }
    
    @Override
    public String getDescription() {
        String textPart = requiredText != null ? " containing '" + requiredText + "'" : "";
        return "Send " + getRequiredAmount() + " messages" + textPart;
    }
    
    @Override
    public String serialize() {
        String text = requiredText != null ? requiredText : "ANY";
        return getType() + ":" + text + ":" + getRequiredAmount();
    }
    
    /**
     * Deserialize a ChatObjective from a string.
     * Format: chat:TEXT:amount (use "ANY" for any message)
     */
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
