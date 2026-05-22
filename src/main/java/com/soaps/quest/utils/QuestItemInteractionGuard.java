package com.soaps.quest.utils;

import com.soaps.quest.SoapsQuest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Central quest-item interaction policy.
 *
 * Guarded event families:
 * - player item use (interact/consume/entity-use)
 * - place/bucket style interactions
 * - projectile/bow launch
 * - elytra firework boost
 *
 * This keeps quest papers non-interactable as vanilla items regardless of
 * quest material while still allowing plugin-side claim/progress handling.
 */
public class QuestItemInteractionGuard {
    private final SoapsQuest plugin;

    public QuestItemInteractionGuard(SoapsQuest plugin) {
        this.plugin = plugin;
    }

    public boolean isQuestPaper(ItemStack item) {
        return QuestPaper.isQuestPaper(item, this.plugin.getQuestIdKey());
    }

    public boolean isQuestPaperInHand(Player player, EquipmentSlot hand) {
        if (player == null || hand == null) {
            return false;
        }
        ItemStack item = hand == EquipmentSlot.OFF_HAND ? player.getInventory().getItemInOffHand() : player.getInventory().getItemInMainHand();
        return this.isQuestPaper(item);
    }

    public boolean isQuestPaperInEitherHand(Player player) {
        return this.isQuestPaperInHand(player, EquipmentSlot.HAND) || this.isQuestPaperInHand(player, EquipmentSlot.OFF_HAND);
    }
}
