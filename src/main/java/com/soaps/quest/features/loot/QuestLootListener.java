/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.NamespacedKey
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.world.LootGenerateEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.loot.LootContext
 *  org.bukkit.persistence.PersistentDataContainer
 *  org.bukkit.persistence.PersistentDataType
 *  org.bukkit.plugin.Plugin
 */
package com.soaps.quest.features.loot;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.features.loot.QuestLootManager;
import com.soaps.quest.managers.QuestGeneratorService;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.utils.QuestPaper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class QuestLootListener
implements Listener {
    private final SoapsQuest plugin;
    private final QuestLootManager lootManager;
    private final NamespacedKey lootMarkerKey;

    public QuestLootListener(SoapsQuest plugin, QuestLootManager lootManager) {
        this.plugin = plugin;
        this.lootManager = lootManager;
        this.lootMarkerKey = new NamespacedKey((Plugin)plugin, "quest_loot_marker");
    }

    public void refreshGeneratorCache() {
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onLootGenerate(LootGenerateEvent event) {
        if (!this.lootManager.isEnabled() || !this.lootManager.isChestLootEnabled()) {
            return;
        }
        LootContext context = event.getLootContext();
        Location location = context.getLocation();
        if (location.getWorld() == null) {
            return;
        }
        String worldName = location.getWorld().getName();
        if (!this.lootManager.isChestWorldAllowed(worldName)) {
            this.lootManager.debug("Skipping chest loot in world: %s", worldName);
            return;
        }
        if (!this.lootManager.rollChestChance()) {
            this.lootManager.debug("Chest loot chance failed at %s", location);
            return;
        }
        int amount = this.lootManager.getRandomChestAmount();
        amount = Math.min(amount, this.lootManager.getMaxPerEvent());
        this.lootManager.debug("Generating %d quest(s) for chest at %s", amount, location);
        List<ItemStack> questItems = this.generateQuestItems(amount, null, this.lootManager.getChestSourceMode(), this.lootManager.getChestQuestIds());
        if (!questItems.isEmpty()) {
            event.getLoot().addAll(questItems);
            this.lootManager.debug("Added %d quest paper(s) to chest loot", questItems.size());
        }
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!this.lootManager.isEnabled() || !this.lootManager.isMobLootEnabled()) {
            return;
        }
        String worldName = event.getEntity().getWorld().getName();
        if (!this.lootManager.isMobWorldAllowed(worldName)) {
            return;
        }
        if (!this.lootManager.rollMobChance(event.getEntityType())) {
            return;
        }
        int amount = this.lootManager.getRandomMobAmount(event.getEntityType());
        amount = Math.min(amount, this.lootManager.getMaxPerEvent());
        this.lootManager.debug("Generating %d quest(s) from %s at %s", amount, event.getEntityType(), event.getEntity().getLocation());
        Player killer = event.getEntity().getKiller();
        List<ItemStack> questItems = this.generateQuestItems(amount, killer, this.lootManager.getMobSourceMode(), this.lootManager.getMobQuestIds());
        for (ItemStack questItem : questItems) {
            this.markAsQuestLoot(questItem);
            event.getDrops().add(questItem);
        }
        if (!questItems.isEmpty()) {
            this.lootManager.debug("Added %d quest paper(s) to %s drops", questItems.size(), event.getEntityType());
        }
    }

    private List<ItemStack> generateQuestItems(int amount, Player contextPlayer, String sourceMode, List<String> manualQuestIds) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (int i = 0; i < amount; ++i) {
            ItemStack questPaper = null;
            if ("random".equalsIgnoreCase(sourceMode)) {
                questPaper = this.generateRandomQuestPaper(contextPlayer);
            } else if ("manual".equalsIgnoreCase(sourceMode)) {
                questPaper = this.generateManualQuestPaper(contextPlayer, manualQuestIds);
            } else if ("mixed".equalsIgnoreCase(sourceMode)) {
                questPaper = ThreadLocalRandom.current().nextBoolean() ? this.generateRandomQuestPaper(contextPlayer) : this.generateManualQuestPaper(contextPlayer, manualQuestIds);
            } else {
                this.plugin.getLogger().warning("[QuestLoot] Unknown source-mode '" + sourceMode + "', falling back to manual");
                questPaper = this.generateManualQuestPaper(contextPlayer, manualQuestIds);
            }
            if (questPaper == null) continue;
            items.add(questPaper);
        }
        return items;
    }

    private ItemStack generateRandomQuestPaper(Player contextPlayer) {
        try {
            QuestGeneratorService service = this.plugin.getQuestGeneratorService();
            if (service == null || !service.isEnabled()) {
                this.lootManager.debug("Random generator not available (premium feature), skipping");
                return null;
            }
            String questId = service.generateQuest();
            if (questId == null) {
                this.lootManager.debug("Failed to generate random quest");
                return null;
            }
            Quest quest = this.plugin.getQuestManager().getQuest(questId);
            if (quest == null) {
                this.lootManager.debug("Generated quest not found: %s", questId);
                return null;
            }
            return this.createUnboundQuestPaper(quest);
        }
        catch (Exception e) {
            this.lootManager.debug("Error generating random quest: %s", e.getMessage());
            return null;
        }
    }

    private ItemStack generateManualQuestPaper(Player contextPlayer, List<String> questIds) {
        if (questIds.isEmpty()) {
            this.lootManager.debug("No manual quest IDs configured");
            return null;
        }
        String questId = questIds.get(ThreadLocalRandom.current().nextInt(questIds.size()));
        Quest quest = this.plugin.getQuestManager().getQuest(questId);
        if (quest == null) {
            this.lootManager.debug("Manual quest not found: %s", questId);
            return null;
        }
        return this.createUnboundQuestPaper(quest);
    }

    private ItemStack createUnboundQuestPaper(Quest quest) {
        return QuestPaper.createUnboundQuestPaper(quest, this.plugin.getMessageManager(), this.plugin.getQuestIdKey(), quest.getCustomLore());
    }

    private void markAsQuestLoot(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(this.lootMarkerKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
    }

    public boolean isQuestLoot(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(this.lootMarkerKey, PersistentDataType.BYTE);
    }
}

