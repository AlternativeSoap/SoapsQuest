/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package com.soaps.quest.gui;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.gui.GuiMenu;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.quests.QuestProgress;
import com.soaps.quest.utils.ColorUtil;
import com.soaps.quest.utils.QuestPaper;
import java.util.ArrayList;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerQuestsGui {
    private static final int GUI_SIZE = 54;
    private static final int CLOSE_SLOT = 49;
    private final SoapsQuest plugin;

    public PlayerQuestsGui(SoapsQuest plugin) {
        this.plugin = plugin;
    }

    public void open(Player viewer, Player target) {
        if (!com.soaps.quest.util.QuestGuiGate.allow(this.plugin, viewer)) {
            return;
        }
        boolean isSelf = viewer.getUniqueId().equals(target.getUniqueId());
        String rawTitle = isSelf ? "&e&lYour Active Quests" : "&e&lActive Quests &7\u00bb &f" + target.getName();
        ItemStack[] items = this.buildInventory(viewer, target);
        GuiMenu menu = new GuiMenu(this.plugin, rawTitle, 54, (clicker, slot) -> this.handleClick((Player)clicker, (int)slot, target));
        menu.open(viewer, items);
    }

    private ItemStack[] buildInventory(Player viewer, Player target) {
        ItemStack[] items = new ItemStack[54];
        ItemStack filler = this.buildFiller();
        for (int i = 0; i < 54; ++i) {
            items[i] = filler;
        }
        ArrayList<ItemStack> papers = new ArrayList<ItemStack>();
        for (ItemStack item : target.getInventory().getContents()) {
            if (item == null || !QuestPaper.isQuestPaper(item, this.plugin.getQuestIdKey())) continue;
            papers.add(item);
        }
        int[] contentSlots = this.buildContentSlots();
        int questIndex = 0;
        for (int i = 0; i < contentSlots.length && i < papers.size(); ++i) {
            items[contentSlots[i]] = this.buildQuestDisplay((ItemStack)papers.get(i), target.getUniqueId());
            ++questIndex;
        }
        if (papers.isEmpty()) {
            items[22] = this.buildEmptyIndicator(target);
        }
        items[49] = this.buildCloseButton();
        return items;
    }

    private int[] buildContentSlots() {
        int[] slots = new int[21];
        int idx = 0;
        for (int row = 1; row <= 3; ++row) {
            for (int col = 1; col <= 7; ++col) {
                slots[idx++] = row * 9 + col;
            }
        }
        return slots;
    }

    private ItemStack buildQuestDisplay(ItemStack paper, UUID targetUuid) {
        QuestProgress progress;
        String questId = QuestPaper.getQuestId(paper, this.plugin.getQuestIdKey());
        Quest quest = questId != null ? this.plugin.getQuestManager().getQuest(questId) : null;
        Material material = quest != null && quest.getMaterial() != null ? quest.getMaterial() : Material.MAP;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        String displayName = quest != null ? "&e" + quest.getDisplay() : "&e" + (questId != null ? questId : "Unknown Quest");
        meta.displayName(ColorUtil.colorize(displayName));
        ArrayList<Component> lore = new ArrayList<Component>();
        lore.add(ColorUtil.colorize("&8" + (questId != null ? questId : "")));
        UUID instanceId = QuestPaper.getQuestInstanceId(paper);
        if (instanceId != null && (progress = this.plugin.getDataManager().getQuestInstance(targetUuid, instanceId)) != null) {
            if (progress.isRedeemed()) {
                lore.add(ColorUtil.colorize("&aStatus: &2Redeemed"));
            } else if (progress.isClaimable()) {
                lore.add(ColorUtil.colorize("&aStatus: &eReady to claim!"));
            } else if (quest != null && quest.hasObjectives()) {
                int done = 0;
                for (Objective obj : quest.getObjectives()) {
                    if (progress.getObjectiveProgress(obj.getObjectiveId()) < obj.getRequiredAmount()) continue;
                    ++done;
                }
                lore.add(ColorUtil.colorize("&aObjectives: &f" + done + "/" + quest.getObjectives().size()));
            } else {
                lore.add(ColorUtil.colorize("&aProgress: &f" + progress.getCurrentProgress() + "/" + progress.getRequiredAmount()));
            }
        }
        if (quest != null && quest.getTier() != null) {
            lore.add(ColorUtil.colorize("&7Tier: &f" + quest.getTier()));
        }
        if (quest != null && quest.getDifficulty() != null) {
            lore.add(ColorUtil.colorize("&7Difficulty: &f" + quest.getDifficulty()));
        }
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildFiller() {
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.displayName((Component)Component.empty());
            filler.setItemMeta(meta);
        }
        return filler;
    }

    private ItemStack buildCloseButton() {
        ItemStack btn = new ItemStack(Material.BARRIER);
        ItemMeta meta = btn.getItemMeta();
        if (meta != null) {
            meta.displayName(ColorUtil.colorize("&c&lClose"));
            btn.setItemMeta(meta);
        }
        return btn;
    }

    private ItemStack buildEmptyIndicator(Player target) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ColorUtil.colorize("&7No active quests"));
            ArrayList<Component> lore = new ArrayList<Component>();
            lore.add(ColorUtil.colorize("&8" + target.getName() + " has no quest papers"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void handleClick(Player viewer, int slot, Player target) {
        if (slot == 49) {
            viewer.closeInventory();
        }
    }
}

