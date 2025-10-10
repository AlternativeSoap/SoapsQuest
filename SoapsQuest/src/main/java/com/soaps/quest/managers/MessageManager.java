package com.soaps.quest.managers;

import java.io.File;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.soaps.quest.SoapsQuest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Manages all plugin messages with support for customization.
 * Generates messages.yml from defaults and handles placeholders.
 */
public final class MessageManager {
    
    private final SoapsQuest plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;
    
    /**
     * Constructor for MessageManager.
     * 
     * @param plugin Plugin instance
     */
    public MessageManager(SoapsQuest plugin) {
        this.plugin = plugin;
        loadMessages();
    }
    
    /**
     * Load or create messages.yml with defaults from resources.
     */
    public void loadMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        
        // Create messages.yml from resources if it doesn't exist
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
            plugin.getLogger().info("Created messages.yml with default messages.");
        }
        
        // Load the configuration
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        
        // Validate that file loaded correctly
        if (messagesConfig.getKeys(false).isEmpty()) {
            plugin.getLogger().warning("messages.yml is empty or corrupt. Regenerating...");
            messagesFile.delete();
            plugin.saveResource("messages.yml", false);
            messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        }
    }
    
    /**
     * Reload messages from file.
     * Regenerates messages.yml if it was deleted.
     */
    public void reload() {
        // Check if file was deleted
        if (!messagesFile.exists()) {
            plugin.getLogger().info("messages.yml was deleted. Regenerating...");
        }
        loadMessages();
    }
    
    /**
     * Get a message by key with placeholders replaced.
     * 
     * @param key Message key
     * @param placeholders Map of placeholder names to values
     * @return Formatted Component (never null)
     */
    public Component getMessage(String key, Map<String, String> placeholders) {
        String message = messagesConfig.getString(key, key);
        
        // Null safety: if config returns null, use the key as fallback
        if (message == null) {
            message = key;
        }
        
        // Replace <prefix> first
        if (message.contains("<prefix>")) {
            String prefix = messagesConfig.getString("prefix", "&8[&2SoapsQuest&8]&r");
            if (prefix != null) {
                message = message.replace("<prefix>", prefix);
            }
        }
        
        // Replace other placeholders
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("<" + entry.getKey() + ">", entry.getValue());
            }
        }
        
        Component result = parseColorCodes(message);
        // Ensure we never return null
        return result != null ? result : Component.text(message);
    }
    
    /**
     * Get a message by key without placeholders.
     * 
     * @param key Message key
     * @return Formatted Component
     */
    public Component getMessage(String key) {
        return getMessage(key, null);
    }
    
    /**
     * Get a raw message string by key.
     * 
     * @param key Message key
     * @return Raw message string
     */
    public String getRawMessage(String key) {
        return messagesConfig.getString(key, key);
    }
    
    /**
     * Parse color codes supporting both legacy (&) and MiniMessage formats.
     * 
     * @param text Text to parse
     * @return Parsed Component (never null)
     */
    public Component parseColorCodes(String text) {
        // Null safety for input
        if (text == null) {
            return Component.empty();
        }
        
        // Check if text contains MiniMessage-specific tags (not just any angle brackets)
        boolean hasMiniMessage = text.matches(".*<(gradient|#[0-9a-fA-F]{6}|rainbow|color:[^>]+|[a-z]+:[^>]+)>.*");
        
        if (hasMiniMessage) {
            try {
                return MiniMessage.miniMessage().deserialize(text);
            } catch (Exception e) {
                // Fall back to legacy if MiniMessage fails
            }
        }
        
        // Use legacy format for & codes (never returns null)
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
    
    /**
     * Get the messages configuration.
     * 
     * @return Messages FileConfiguration
     */
    public FileConfiguration getConfig() {
        return messagesConfig;
    }
}
