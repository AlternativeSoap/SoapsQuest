/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.event.player.AsyncChatEvent
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.TextComponent
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.kyori.adventure.text.format.TextDecoration
 *  net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 */
package com.soaps.quest.chat;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.utils.ColorUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class ChatInputManager
implements Listener {
    private final SoapsQuest plugin;
    private final Map<UUID, ChatInputRequest> awaitingInput;
    private final Map<UUID, Integer> timeoutTasks;
    private static final int TIMEOUT_SECONDS = 30;

    public ChatInputManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.awaitingInput = new ConcurrentHashMap<UUID, ChatInputRequest>();
        this.timeoutTasks = new ConcurrentHashMap<UUID, Integer>();
        plugin.debugLog("ChatInputManager initialized");
    }

    public void initialize() {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this.plugin);
    }

    public void requestInput(Player player, String prompt, Consumer<String> callback) {
        this.requestInput(player, prompt, callback, true);
    }

    public void requestInput(Player player, String prompt, Consumer<String> callback, boolean allowCancel) {
        UUID uuid = player.getUniqueId();
        if (this.awaitingInput.containsKey(uuid)) {
            player.sendMessage((Component)Component.text((String)"Previous input request cancelled.", (TextColor)NamedTextColor.YELLOW));
            this.cancelTimeout(uuid);
            this.plugin.debugLog("Chat Input Request: Player=" + player.getName() + ", Action=CANCELLED_PREVIOUS");
        }
        this.awaitingInput.put(uuid, new ChatInputRequest(callback, allowCancel));
        int taskId = Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> {
            if (this.awaitingInput.remove(uuid) != null) {
                this.timeoutTasks.remove(uuid);
                if (player.isOnline()) {
                    player.sendMessage((Component)Component.empty());
                    player.sendMessage((Component)Component.text((String)"\u23f1 Input request timed out. No action taken.", (TextColor)NamedTextColor.GRAY));
                    player.sendMessage((Component)Component.empty());
                }
                this.plugin.debugLog("Chat Input Timeout: Player=" + player.getName() + ", Remaining=" + this.awaitingInput.size());
            }
        }, 600L).getTaskId();
        this.timeoutTasks.put(uuid, taskId);
        player.sendMessage((Component)Component.empty());
        player.sendMessage((Component)Component.text((String)"\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501", (TextColor)NamedTextColor.GOLD, (TextDecoration[])new TextDecoration[]{TextDecoration.STRIKETHROUGH}));
        player.sendMessage(ColorUtil.colorize(prompt));
        if (allowCancel) {
            player.sendMessage(((TextComponent)Component.text((String)"Type ", (TextColor)NamedTextColor.GRAY, (TextDecoration[])new TextDecoration[]{TextDecoration.ITALIC}).append((Component)Component.text((String)"cancel", (TextColor)NamedTextColor.WHITE))).append((Component)Component.text((String)" to abort.", (TextColor)NamedTextColor.GRAY, (TextDecoration[])new TextDecoration[]{TextDecoration.ITALIC})));
        }
        player.sendMessage((Component)Component.text((String)"\u23f1 Timeout: 30 seconds", (TextColor)NamedTextColor.GRAY, (TextDecoration[])new TextDecoration[]{TextDecoration.ITALIC}));
        player.sendMessage((Component)Component.text((String)"\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501", (TextColor)NamedTextColor.GOLD, (TextDecoration[])new TextDecoration[]{TextDecoration.STRIKETHROUGH}));
        player.sendMessage((Component)Component.empty());
        this.plugin.debugLog("Chat Input Request: Player=" + player.getName() + ", Prompt='" + prompt + "', AllowCancel=" + allowCancel + ", Timeout=30s, ActiveRequests=" + this.awaitingInput.size());
    }

    public boolean cancelInput(Player player) {
        UUID uuid = player.getUniqueId();
        if (this.awaitingInput.containsKey(uuid)) {
            this.awaitingInput.remove(uuid);
            player.sendMessage((Component)Component.text((String)"Input cancelled.", (TextColor)NamedTextColor.YELLOW));
            this.plugin.debugLog("Chat Input Cancel: Player=" + player.getName() + ", Remaining=" + this.awaitingInput.size());
            return true;
        }
        return false;
    }

    public boolean isAwaitingInput(Player player) {
        return this.awaitingInput.containsKey(player.getUniqueId());
    }

    public int getActiveRequestCount() {
        return this.awaitingInput.size();
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!this.awaitingInput.containsKey(uuid)) {
            return;
        }
        event.setCancelled(true);
        this.cancelTimeout(uuid);
        ChatInputRequest request = this.awaitingInput.remove(uuid);
        String message = PlainTextComponentSerializer.plainText().serialize(event.message()).trim();
        if (request.allowCancel() && message.equalsIgnoreCase("cancel")) {
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> player.sendMessage((Component)Component.text((String)"Action cancelled.", (TextColor)NamedTextColor.YELLOW)));
            this.plugin.debugLog("Chat Input Response: Player=" + player.getName() + ", Action=CANCELLED, Remaining=" + this.awaitingInput.size());
            return;
        }
        if (message.isEmpty()) {
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
                player.sendMessage((Component)Component.text((String)"Input cannot be empty. Please try again.", (TextColor)NamedTextColor.RED));
                this.requestInput(player, "Please enter a value:", request.callback(), request.allowCancel());
            });
            this.plugin.debugLog("Chat Input Response: Player=" + player.getName() + ", Action=EMPTY_REJECTED");
            return;
        }
        this.plugin.debugLog("Chat Input Response: Player=" + player.getName() + ", Input='" + message + "', Length=" + message.length());
        Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
            try {
                request.callback().accept(message);
                this.plugin.debugLog("Chat Input Processed: Player=" + player.getName() + ", Success=true");
            }
            catch (Exception e) {
                player.sendMessage((Component)Component.text((String)("Error processing input: " + e.getMessage()), (TextColor)NamedTextColor.RED));
                this.plugin.getLogger().log(Level.SEVERE, "Chat Input Error: Player={0}, Input=''{1}'', Error={2}", new Object[]{player.getName(), message, e.getMessage()});
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        this.cancelTimeout(uuid);
        if (this.awaitingInput.remove(uuid) != null) {
            this.plugin.debugLog("Chat Input Disconnect: Player=" + event.getPlayer().getName() + ", Remaining=" + this.awaitingInput.size());
        }
    }

    public void clearAll() {
        int count = this.awaitingInput.size();
        this.awaitingInput.clear();
        for (Integer taskId : this.timeoutTasks.values()) {
            Bukkit.getScheduler().cancelTask(taskId.intValue());
        }
        this.timeoutTasks.clear();
        this.plugin.debugLog("Chat Input Clear: Cleared=" + count + " pending requests");
    }

    private void cancelTimeout(UUID uuid) {
        Integer taskId = this.timeoutTasks.remove(uuid);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId.intValue());
            this.plugin.debugLog("Chat Input Timeout Cancelled: Player=" + String.valueOf(uuid));
        }
    }

    private record ChatInputRequest(Consumer<String> callback, boolean allowCancel) {
    }
}

