package com.soaps.quest.listeners;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.utils.QuestItemInteractionGuard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.Event.Result;
import org.bukkit.inventory.ItemStack;

public class QuestItemInteractionBlockerListener implements Listener {
    private final QuestItemInteractionGuard guard;

    public QuestItemInteractionBlockerListener(SoapsQuest plugin) {
        this.guard = new QuestItemInteractionGuard(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (!this.shouldBlockItemUse(item)) {
            return;
        }
        Action action = event.getAction();
        if (action == Action.PHYSICAL) {
            return;
        }
        this.cancelItemUse(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!this.guard.isQuestPaperInHand(event.getPlayer(), event.getHand())) {
            return;
        }
        this.cancelInteraction(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (!this.guard.isQuestPaperInHand(event.getPlayer(), event.getHand())) {
            return;
        }
        this.cancelInteraction(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (!this.shouldBlockItemUse(event.getItem())) {
            return;
        }
        this.cancelInteraction(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!this.shouldBlockItemUse(event.getItemInHand())) {
            return;
        }
        this.cancelInteraction(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof org.bukkit.entity.Player)) {
            return;
        }
        if (!this.shouldBlockItemUse(event.getBow())) {
            return;
        }
        this.cancelInteraction(event);
    }

    private boolean shouldBlockItemUse(ItemStack item) {
        return this.guard.isQuestPaper(item);
    }

    private void cancelItemUse(PlayerInteractEvent event) {
        event.setUseInteractedBlock(Result.DENY);
        event.setUseItemInHand(Result.DENY);
        event.setCancelled(true);
    }

    private void cancelInteraction(org.bukkit.event.Cancellable event) {
        event.setCancelled(true);
    }
}
