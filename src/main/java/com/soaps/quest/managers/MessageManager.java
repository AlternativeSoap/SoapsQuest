/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package com.soaps.quest.managers;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.utils.ColorUtil;
import java.io.File;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class MessageManager {
    private final SoapsQuest plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;

    public MessageManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.loadMessages();
    }

    public void loadMessages() {
        this.messagesFile = new File(this.plugin.getDataFolder(), "messages.yml");
        if (!this.messagesFile.exists()) {
            this.plugin.saveResource("messages.yml", false);
            this.plugin.debugLog("Created messages.yml with default messages.");
        }
        this.messagesConfig = YamlConfiguration.loadConfiguration((File)this.messagesFile);
        if (this.messagesConfig.getKeys(false).isEmpty()) {
            this.plugin.getLogger().warning("messages.yml is empty or corrupt. Regenerating...");
            this.messagesFile.delete();
            this.plugin.saveResource("messages.yml", false);
            this.messagesConfig = YamlConfiguration.loadConfiguration((File)this.messagesFile);
        }
    }

    public void reload() {
        if (!this.messagesFile.exists()) {
            this.plugin.debugLog("messages.yml was deleted. Regenerating...");
        }
        this.loadMessages();
    }

    public Component getMessage(String key, Map<String, String> placeholders) {
        Component result;
        Object prefix;
        String message = this.messagesConfig.getString(key, key);
        if (message == null) {
            message = key;
        }
        if (message.contains("<prefix>") && (prefix = this.messagesConfig.getString("prefix", "&8[&2SoapsQuest&8]&r")) != null) {
            message = message.replace("<prefix>", (CharSequence)prefix);
        }
        if (placeholders != null) {
            for (Map.Entry entry : placeholders.entrySet()) {
                message = message.replace("<" + (String)entry.getKey() + ">", (CharSequence)entry.getValue());
            }
        }
        return (result = this.parseColorCodes(message)) != null ? result : ColorUtil.colorize(message);
    }

    public Component getMessage(String key) {
        return this.getMessage(key, null);
    }

    public String getRawMessage(String key) {
        return this.messagesConfig.getString(key, key);
    }

    public Component parseColorCodes(String text) {
        return ColorUtil.colorize(text);
    }

    public FileConfiguration getConfig() {
        return this.messagesConfig;
    }
}

