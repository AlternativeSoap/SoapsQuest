package com.soaps.quest.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.utils.QuestPaper;

/**
 * Listener for player events related to quest queue management.
 * Handles player join/quit, inventory changes, and quest paper management.
 */
public class PlayerListener implements Listener {
    
    private final SoapsQuest plugin;
    
    /**
     * Constructor for PlayerListener.
     * 
     * @param plugin Plugin instance
     */
    public PlayerListener(SoapsQuest plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Handle player join events.
     * Refreshes quest queues based on inventory contents.
     * 
     * @param event PlayerJoinEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Refresh quest queues after a short delay to ensure inventory is loaded
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getQuestManager().refreshPlayerQueues(player);
            }
        }.runTaskLater(plugin, 20L); // 1 second delay
    }
    
    /**
     * Handle player quit events.
     * Clean up any temporary data for the player.
     * 
     * @param event PlayerQuitEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Clear any active bossbars and progress tracking for the player
        // Prevents memory leaks from active bossbar HashMap entries
        plugin.getProgressDisplayManager().clearPlayer(player);
        
        // Quest data cleanup is automatic (handled by empty map removal in DataManager/QuestManager)
    }
    
    /**
     * Handle player dropping quest papers.
     * Triggers queue cleanup to remove dropped papers from tracking.
     * 
     * @param event PlayerDropItemEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        
        // Check if the dropped item is a quest paper
        if (QuestPaper.isQuestPaper(item, plugin.getQuestIdKey())) {
            Player player = event.getPlayer();
            
            // Refresh queue immediately on next tick to clean up dropped papers
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getQuestManager().refreshPlayerQueues(player);
                }
            }.runTask(plugin); // Run on next tick synchronously
        }
    }
    
    /**
     * Handle player picking up quest papers from the ground.
     * Triggers queue refresh to add picked up papers to tracking.
     * 
     * @param event EntityPickupItemEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        
        ItemStack item = event.getItem().getItemStack();
        
        // Check if the picked up item is a quest paper
        if (QuestPaper.isQuestPaper(item, plugin.getQuestIdKey())) {
            // Refresh queue immediately on next tick to ensure it's ready for progress checks
            // Using LOWEST priority delay to execute as soon as possible
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getQuestManager().refreshPlayerQueues(player);
                }
            }.runTask(plugin); // Run on next tick synchronously
        }
    }
    
    /**
     * Handle inventory click events.
     * Detects when quest papers are moved or removed from inventory.
     * 
     * @param event InventoryClickEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        
        boolean hasQuestPaper = false;
        
        // Check if any quest papers are involved in this click
        if (currentItem != null && QuestPaper.isQuestPaper(currentItem, plugin.getQuestIdKey())) {
            hasQuestPaper = true;
        }
        if (QuestPaper.isQuestPaper(cursorItem, plugin.getQuestIdKey())) {
            hasQuestPaper = true;
        }
        
        // If quest papers are involved, refresh queues after the click is processed
        if (hasQuestPaper) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getQuestManager().refreshPlayerQueues(player);
                }
            }.runTask(plugin); // Run on next tick synchronously
        }
    }
    
    /**
     * Handle inventory drag events.
     * Detects when quest papers are moved via dragging.
     * 
     * @param event InventoryDragEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        ItemStack draggedItem = event.getOldCursor();
        
        // Check if the dragged item is a quest paper
        if (QuestPaper.isQuestPaper(draggedItem, plugin.getQuestIdKey())) {
            // Refresh queues after the drag is processed
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getQuestManager().refreshPlayerQueues(player);
                }
            }.runTask(plugin); // Run on next tick synchronously
        }
    }
}