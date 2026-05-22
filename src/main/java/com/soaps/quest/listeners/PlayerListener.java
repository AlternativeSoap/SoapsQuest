/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityPickupItemEvent
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.inventory.InventoryAction
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerItemHeldEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerSwapHandItemsEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package com.soaps.quest.listeners;

import com.soaps.quest.SoapsQuest;
import com.soaps.common.api.gui.GuiHolder;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import com.soaps.quest.utils.QuestPaper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener
implements Listener {
    private final SoapsQuest plugin;
    private final Map<UUID, Long> lastRefreshTime;
    private final Set<UUID> updatingPapers;
    private final Set<UUID> recentDeaths;
    private static final long REFRESH_COOLDOWN_MS = 500L;

    public PlayerListener(SoapsQuest plugin) {
        this.plugin = plugin;
        this.lastRefreshTime = new HashMap<UUID, Long>();
        this.updatingPapers = new HashSet<UUID>();
        this.recentDeaths = ConcurrentHashMap.newKeySet();
    }

    private void finishPaperUpdateLater(final UUID playerUuid, final Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerListener.this.updatingPapers.remove(playerUuid);
                PlayerListener.this.plugin.debugLog(Level.INFO,
                        "[Debounce] Paper update complete for ''{0}'', events re-enabled", player.getName());
            }
        }.runTaskLater(this.plugin, 2L);
    }

    private void scheduleQueueRefresh(final Player player, final boolean updatePapers) {
        final UUID playerUuid = player.getUniqueId();
        if (this.updatingPapers.contains(playerUuid)) {
            this.plugin.debugLog(Level.INFO, "[Debounce] Ignoring refresh for ''{0}'' - currently updating papers", player.getName());
            return;
        }
        long now = System.currentTimeMillis();
        Long lastRefresh = this.lastRefreshTime.get(playerUuid);
        if (lastRefresh != null && now - lastRefresh < REFRESH_COOLDOWN_MS) {
            this.plugin.debugLog(Level.INFO, "[Debounce] Skipping refresh for ''{0}'' - too soon ({1}ms since last)", player.getName(), now - lastRefresh);
            return;
        }
        this.lastRefreshTime.put(playerUuid, now);
        this.plugin.debugLog(Level.INFO, "[Debounce] Scheduling refresh for ''{0}''", player.getName());
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerListener.this.plugin.getQuestManager().refreshPlayerQueues(player);
                if (!updatePapers) {
                    return;
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        PlayerListener.this.updatingPapers.add(playerUuid);
                        try {
                            PlayerListener.this.plugin.getQuestManager().updateAllQuestPapersForPlayer(player);
                        } catch (Throwable t) {
                            PlayerListener.this.finishPaperUpdateLater(playerUuid, player);
                            throw t;
                        }
                        PlayerListener.this.finishPaperUpdateLater(playerUuid, player);
                    }
                }.runTaskLater(PlayerListener.this.plugin, 1L);
            }
        }.runTask(this.plugin);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID playerUuid = player.getUniqueId();
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerListener.this.plugin.getQuestManager().refreshPlayerQueues(player);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        PlayerListener.this.updatingPapers.add(playerUuid);
                        try {
                            PlayerListener.this.plugin.getQuestManager().updateAllQuestPapersForPlayer(player);
                        } catch (Throwable t) {
                            PlayerListener.this.finishPaperUpdateLater(playerUuid, player);
                            throw t;
                        }
                        PlayerListener.this.finishPaperUpdateLater(playerUuid, player);
                    }
                }.runTaskLater(PlayerListener.this.plugin, 2L);
            }
        }.runTaskLater(this.plugin, 20L);
        if (this.plugin.getRecurringQuestManager() != null) {
            this.plugin.getRecurringQuestManager().onPlayerJoin(player);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();
        this.plugin.getProgressDisplayManager().clearPlayer(player);
        this.lastRefreshTime.remove(playerUuid);
        this.updatingPapers.remove(playerUuid);
        this.recentDeaths.remove(playerUuid);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        final UUID playerUuid = player.getUniqueId();
        this.recentDeaths.add(playerUuid);
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerListener.this.recentDeaths.remove(playerUuid);
            }
        }.runTaskLater(this.plugin, 1L);
        this.plugin.debugLog(Level.INFO, "[Death] ''{0}'' died \u2014 death-drop protection active for 1 tick", player.getName());
    }

    public void scheduleProtectedPaperUpdate(final Player player) {
        final UUID playerUuid = player.getUniqueId();
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerListener.this.updatingPapers.add(playerUuid);
                try {
                    PlayerListener.this.plugin.getQuestManager().updateAllQuestPapersForPlayer(player);
                } catch (Throwable t) {
                    PlayerListener.this.finishPaperUpdateLater(playerUuid, player);
                    throw t;
                }
                PlayerListener.this.finishPaperUpdateLater(playerUuid, player);
            }
        }.runTaskLater(this.plugin, 2L);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (!QuestPaper.isQuestPaper(item, this.plugin.getQuestIdKey())) {
            return;
        }
        Player player = event.getPlayer();
        this.plugin.debugLog(Level.INFO, "[Event] DROP item triggered refresh for ''{0}''", player.getName());
        boolean abandonEnabled = this.plugin.getConfig().getBoolean("abandon-on-drop", true);
        if (abandonEnabled && !this.recentDeaths.contains(player.getUniqueId())) {
            String questId = QuestPaper.getQuestId(item, this.plugin.getQuestIdKey());
            UUID instanceId = QuestPaper.getQuestInstanceId(item);
            if (questId != null && instanceId != null) {
                boolean shouldAbandon;
                Quest quest = this.plugin.getQuestManager().getQuest(questId);
                boolean bl = shouldAbandon = quest != null && !quest.isLockToPlayer() && !QuestPaper.isRedeemed(item, this.plugin.getQuestIdKey()) && !QuestPaper.isLocked(item);
                if (shouldAbandon) {
                    this.plugin.getDataManager().removeQuestInstance(player, instanceId);
                    this.plugin.getQuestManager().removeQuestFromQueue(player, questId, instanceId);
                    this.plugin.getProgressDisplayManager().clearProgress(player, questId);
                    player.sendMessage(this.plugin.getMessageManager().getMessage("quest-dropped", Map.of("quest", quest.getDisplay())));
                    this.plugin.debugLog(Level.INFO, "[AbandonOnDrop] ''{0}'' dropped quest ''{1}'' (instance {2}) \u2014 progress abandoned", player.getName(), questId, instanceId);
                } else if (quest != null && quest.isLockToPlayer()) {
                    this.plugin.debugLog(Level.INFO, "[AbandonOnDrop] ''{0}'' dropped lock-to-player quest ''{1}'' \u2014 skipping abandon", player.getName(), questId);
                }
            }
        } else if (this.recentDeaths.contains(player.getUniqueId())) {
            this.plugin.debugLog(Level.INFO, "[AbandonOnDrop] ''{0}'' drop ignored \u2014 death drop protection active", player.getName());
        }
        this.scheduleQueueRefresh(player, true);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onItemPickup(EntityPickupItemEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        ItemStack item = event.getItem().getItemStack();
        if (QuestPaper.isQuestPaper(item, this.plugin.getQuestIdKey())) {
            Quest quest;
            this.plugin.debugLog(Level.INFO, "[Event] PICKUP item triggered refresh for ''{0}''", player.getName());
            String questId = QuestPaper.getQuestId(item, this.plugin.getQuestIdKey());
            UUID paperOwner = QuestPaper.getPlayerUUID(item, this.plugin.getPlayerUuidKey());
            boolean isRedeemed = QuestPaper.isRedeemed(item, this.plugin.getQuestIdKey());
            if (questId != null && (quest = this.plugin.getQuestManager().getQuest(questId)) != null) {
                if (isRedeemed) {
                    this.plugin.debugLog(Level.INFO, "[Pickup] ''{0}'' picked up redeemed quest paper ''{1}'' \u2014 no progress action", player.getName(), questId);
                } else {
                    QuestProgress existingProgress;
                    UUID instanceId = QuestPaper.getQuestInstanceId(item);
                    boolean isClaimable = false;
                    if (instanceId != null && (existingProgress = this.plugin.getDataManager().getQuestInstance(player, instanceId)) != null && existingProgress.isClaimable()) {
                        isClaimable = true;
                    }
                    if (isClaimable) {
                        player.sendMessage(this.plugin.getMessageManager().getMessage("quest-completed-pickup", Map.of("quest", quest.getDisplay())));
                    } else if (paperOwner == null || !paperOwner.equals(player.getUniqueId())) {
                        player.sendMessage(this.plugin.getMessageManager().getMessage("quest-picked-up", Map.of("quest", quest.getDisplay())));
                    }
                    this.plugin.debugLog(Level.INFO, "[Pickup] ''{0}'' picked up quest ''{1}'' (previously owned by {2}, claimable={3})", player.getName(), questId, paperOwner != null ? paperOwner : "nobody", isClaimable);
                }
            }
            this.scheduleQueueRefresh(player, true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        final Player player = (Player)humanEntity;
        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        boolean currentIsQuest = currentItem != null && QuestPaper.isQuestPaper(currentItem, this.plugin.getQuestIdKey());
        boolean cursorIsQuest = QuestPaper.isQuestPaper(cursorItem, this.plugin.getQuestIdKey());
        if (!currentIsQuest && !cursorIsQuest) {
            return;
        }
        Inventory topInventory = event.getView().getTopInventory();
        if (topInventory.getHolder() instanceof GuiHolder) {
            return;
        }
        InventoryType topType = topInventory.getType();
        boolean preventWorkstation = this.plugin.getConfig().getBoolean("prevent-workstation-placement", true);
        if (preventWorkstation && this.isWorkstationInventory(topType)) {
            boolean placingIntoWorkstation = false;
            if (cursorIsQuest && event.getRawSlot() < topInventory.getSize()) {
                placingIntoWorkstation = true;
            }
            if (currentIsQuest && event.isShiftClick() && event.getRawSlot() >= topInventory.getSize()) {
                placingIntoWorkstation = true;
            }
            if (placingIntoWorkstation) {
                event.setCancelled(true);
                player.sendMessage(this.plugin.getMessageManager().getMessage("quest-cannot-place-workstation"));
                this.plugin.debugLog(Level.INFO, "[Workstation] Blocked ''{0}'' from placing quest paper in {1}", player.getName(), topType);
                return;
            }
        }
        if (this.isContainerInventory(topType)) {
            ItemStack hotbarItem;
            int hotbarSlot;
            InventoryAction action;
            ItemStack questPaperItem = null;
            boolean movingToContainer = false;
            if (event.isShiftClick() && event.getRawSlot() >= topInventory.getSize() && currentIsQuest) {
                questPaperItem = currentItem;
                movingToContainer = true;
            }
            if (cursorIsQuest && event.getRawSlot() < topInventory.getSize() && ((action = event.getAction()) == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE || action == InventoryAction.PLACE_SOME || action == InventoryAction.SWAP_WITH_CURSOR)) {
                questPaperItem = cursorItem;
                movingToContainer = true;
            }
            if (event.getAction() == InventoryAction.HOTBAR_SWAP && event.getRawSlot() < topInventory.getSize() && (hotbarSlot = event.getHotbarButton()) >= 0 && (hotbarItem = player.getInventory().getItem(hotbarSlot)) != null && QuestPaper.isQuestPaper(hotbarItem, this.plugin.getQuestIdKey())) {
                questPaperItem = hotbarItem;
                movingToContainer = true;
            }
            if (movingToContainer && questPaperItem != null) {
                boolean abandonOnContainer = this.plugin.getConfig().getBoolean("abandon-on-container-store", true);
                if (!abandonOnContainer) {
                    event.setCancelled(true);
                    this.plugin.debugLog(Level.INFO, "[Container] Blocked ''{0}'' from storing quest paper in container (abandon-on-container-store=false)", player.getName());
                    return;
                }
                String questId = QuestPaper.getQuestId(questPaperItem, this.plugin.getQuestIdKey());
                UUID instanceId = QuestPaper.getQuestInstanceId(questPaperItem);
                if (questId != null && instanceId != null) {
                    boolean shouldAbandon;
                    Quest quest = this.plugin.getQuestManager().getQuest(questId);
                    boolean bl = shouldAbandon = quest != null && !quest.isLockToPlayer() && !QuestPaper.isRedeemed(questPaperItem, this.plugin.getQuestIdKey()) && !QuestPaper.isLocked(questPaperItem);
                    if (shouldAbandon) {
                        this.plugin.getDataManager().removeQuestInstance(player, instanceId);
                        this.plugin.getQuestManager().removeQuestFromQueue(player, questId, instanceId);
                        this.plugin.getProgressDisplayManager().clearProgress(player, questId);
                        player.sendMessage(this.plugin.getMessageManager().getMessage("quest-stored-in-container", Map.of("quest", quest.getDisplay())));
                        this.plugin.debugLog(Level.INFO, "[ContainerAbandon] ''{0}'' stored quest ''{1}'' (instance {2}) in {3} \u2014 progress abandoned", player.getName(), questId, instanceId, topType);
                    }
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        PlayerListener.this.scheduleQueueRefresh(player, true);
                    }
                }.runTaskLater(PlayerListener.this.plugin, 1L);
                return;
            }
        }
        if (currentIsQuest || cursorIsQuest) {
            this.plugin.debugLog(Level.INFO, "[Event] INVENTORY CLICK triggered refresh for ''{0}''", player.getName());
            this.scheduleQueueRefresh(player, true);
        }
    }

    private boolean isContainerInventory(InventoryType type) {
        return switch (type) {
            case CHEST, ENDER_CHEST, BARREL, SHULKER_BOX, HOPPER, DISPENSER, DROPPER -> true;
            default -> false;
        };
    }

    private boolean isWorkstationInventory(InventoryType type) {
        return switch (type) {
            case CRAFTING, WORKBENCH, ANVIL, SMITHING, GRINDSTONE, STONECUTTER, CARTOGRAPHY, LOOM, FURNACE, BLAST_FURNACE, SMOKER, BREWING, BEACON, ENCHANTING, MERCHANT -> true;
            default -> false;
        };
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInventoryDrag(InventoryDragEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        ItemStack draggedItem = event.getOldCursor();
        if (!QuestPaper.isQuestPaper(draggedItem, this.plugin.getQuestIdKey())) {
            return;
        }
        Inventory topInventory = event.getView().getTopInventory();
        if (topInventory.getHolder() instanceof GuiHolder) {
            return;
        }
        int topSize = topInventory.getSize();
        InventoryType topType = topInventory.getType();
        boolean draggingIntoTop = event.getRawSlots().stream().anyMatch(slot -> slot < topSize);
        if (draggingIntoTop) {
            boolean preventWorkstation = this.plugin.getConfig().getBoolean("prevent-workstation-placement", true);
            if (preventWorkstation && this.isWorkstationInventory(topType)) {
                event.setCancelled(true);
                player.sendMessage(this.plugin.getMessageManager().getMessage("quest-cannot-place-workstation"));
                return;
            }
            if (this.isContainerInventory(topType)) {
                boolean abandonOnContainer = this.plugin.getConfig().getBoolean("abandon-on-container-store", true);
                if (!abandonOnContainer) {
                    event.setCancelled(true);
                    return;
                }
                String questId = QuestPaper.getQuestId(draggedItem, this.plugin.getQuestIdKey());
                UUID instanceId = QuestPaper.getQuestInstanceId(draggedItem);
                if (questId != null && instanceId != null) {
                    boolean shouldAbandon;
                    Quest quest = this.plugin.getQuestManager().getQuest(questId);
                    boolean bl = shouldAbandon = quest != null && !quest.isLockToPlayer() && !QuestPaper.isRedeemed(draggedItem, this.plugin.getQuestIdKey()) && !QuestPaper.isLocked(draggedItem);
                    if (shouldAbandon) {
                        this.plugin.getDataManager().removeQuestInstance(player, instanceId);
                        this.plugin.getQuestManager().removeQuestFromQueue(player, questId, instanceId);
                        this.plugin.getProgressDisplayManager().clearProgress(player, questId);
                        player.sendMessage(this.plugin.getMessageManager().getMessage("quest-stored-in-container", Map.of("quest", quest.getDisplay())));
                        this.plugin.debugLog(Level.INFO, "[ContainerAbandon] ''{0}'' dragged quest ''{1}'' into {2} \u2014 progress abandoned", player.getName(), questId, topType);
                    }
                }
            }
        }
        this.plugin.debugLog(Level.INFO, "[Event] INVENTORY DRAG triggered refresh for ''{0}''", player.getName());
        this.scheduleQueueRefresh(player, true);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        ItemStack previousItem = player.getInventory().getItem(event.getPreviousSlot());
        boolean hasQuestPaper = false;
        if (QuestPaper.isQuestPaper(newItem, this.plugin.getQuestIdKey())) {
            hasQuestPaper = true;
        }
        if (QuestPaper.isQuestPaper(previousItem, this.plugin.getQuestIdKey())) {
            hasQuestPaper = true;
        }
        if (hasQuestPaper) {
            this.plugin.debugLog(Level.INFO, "[Event] ITEM HELD CHANGE triggered refresh for ''{0}''", player.getName());
            this.scheduleQueueRefresh(player, false);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onHandSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHandItem = event.getMainHandItem();
        ItemStack offHandItem = event.getOffHandItem();
        boolean hasQuestPaper = false;
        if (QuestPaper.isQuestPaper(mainHandItem, this.plugin.getQuestIdKey())) {
            hasQuestPaper = true;
        }
        if (QuestPaper.isQuestPaper(offHandItem, this.plugin.getQuestIdKey())) {
            hasQuestPaper = true;
        }
        if (hasQuestPaper) {
            this.plugin.debugLog(Level.INFO, "[Event] HAND SWAP triggered refresh for ''{0}''", player.getName());
            this.scheduleQueueRefresh(player, false);
        }
    }
}

