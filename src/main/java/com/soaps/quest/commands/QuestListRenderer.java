/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.JoinConfiguration
 *  net.kyori.adventure.text.event.ClickEvent
 *  net.kyori.adventure.text.event.HoverEvent
 *  net.kyori.adventure.text.event.HoverEventSource
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.kyori.adventure.text.format.TextDecoration
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 */
package com.soaps.quest.commands;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.objectives.Objective;
import com.soaps.quest.quests.Quest;
import com.soaps.quest.rewards.RewardEntry;
import com.soaps.quest.utils.PlaceholderManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class QuestListRenderer {
    private final SoapsQuest plugin;
    private final PlaceholderManager placeholderManager;

    public QuestListRenderer(SoapsQuest plugin) {
        this.plugin = plugin;
        this.placeholderManager = new PlaceholderManager(plugin);
    }

    public void displayQuestEntry(CommandSender sender, String questId) {
        Quest quest = this.plugin.getQuestManager().getQuest(questId);
        if (quest == null) {
            return;
        }
        String typeDisplay = this.getTypeDisplay(quest);
        PlaceholderManager.PlaceholderContext context = new PlaceholderManager.PlaceholderContext().quest(quest);
        if (quest.getTier() != null) {
            context.tier(this.plugin.getTierManager().getTier(quest.getTier()));
        }
        if (quest.getDifficulty() != null) {
            context.difficulty(this.plugin.getDifficultyManager().getDifficulty(quest.getDifficulty()));
        }
        if (sender instanceof Player) {
            Player player = (Player)sender;
            this.displayToPlayer(player, quest, questId, typeDisplay, this.placeholderManager, context);
        } else {
            this.displayToConsole(sender, typeDisplay, this.placeholderManager, context);
        }
    }

    private String getTypeDisplay(Quest quest) {
        if (quest.hasObjectives()) {
            if (quest.getObjectives().size() > 1) {
                if (quest.isSequential()) {
                    return "SEQUENTIAL (" + quest.getObjectives().size() + " steps)";
                }
                return "MULTI (" + quest.getObjectives().size() + " objectives)";
            }
            return "SINGLE";
        }
        return "UNKNOWN";
    }

    private void displayToPlayer(Player player, Quest quest, String questId, String typeDisplay, PlaceholderManager placeholderManager, PlaceholderManager.PlaceholderContext context) {
        String entryText = this.plugin.getMessageManager().getRawMessage("quest-list-entry").replace("<type>", typeDisplay);
        entryText = placeholderManager.replacePlaceholders(entryText, context);
        Component entryComponent = this.plugin.getMessageManager().parseColorCodes(entryText);
        if (entryComponent == null) {
            player.sendMessage(entryText);
            return;
        }
        List<Component> hoverLines = this.buildHoverText(quest, questId);
        boolean canClick = player.hasPermission("soapsquest.list.click");
        if (canClick) {
            hoverLines.add((Component)Component.text((String)"\u2726 Click to claim this quest! \u2726", (TextColor)NamedTextColor.GREEN, (TextDecoration[])new TextDecoration[]{TextDecoration.ITALIC}));
        } else {
            hoverLines.add((Component)Component.text((String)"Use /sq give to claim", (TextColor)NamedTextColor.GRAY, (TextDecoration[])new TextDecoration[]{TextDecoration.ITALIC}));
        }
        Component hoverText = Component.join((JoinConfiguration)JoinConfiguration.newlines(), hoverLines);
        Component clickableEntry = entryComponent.hoverEvent((HoverEventSource)HoverEvent.showText((Component)hoverText));
        if (canClick) {
            clickableEntry = clickableEntry.clickEvent(ClickEvent.runCommand((String)("/sq give " + player.getName() + " " + questId)));
        }
        player.sendMessage(clickableEntry);
    }

    private void displayToConsole(CommandSender sender, String typeDisplay, PlaceholderManager placeholderManager, PlaceholderManager.PlaceholderContext context) {
        String message = this.plugin.getMessageManager().getRawMessage("quest-list-entry").replace("<type>", typeDisplay);
        message = placeholderManager.replacePlaceholders(message, context);
        Component textComponent = this.plugin.getMessageManager().parseColorCodes(message);
        if (textComponent != null) {
            sender.sendMessage(textComponent);
        } else if (message != null) {
            sender.sendMessage(message);
        }
    }

    private List<Component> buildHoverText(Quest quest, String questId) {
        ArrayList<Component> hoverLines = new ArrayList<Component>();
        hoverLines.add((Component)Component.text((String)"\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501", (TextColor)NamedTextColor.GOLD));
        hoverLines.add((Component)Component.text((String)"  Quest Details", (TextColor)NamedTextColor.YELLOW, (TextDecoration[])new TextDecoration[]{TextDecoration.BOLD}));
        hoverLines.add((Component)Component.text((String)"\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501", (TextColor)NamedTextColor.GOLD));
        hoverLines.add((Component)Component.empty());
        if (quest.hasObjectives() && !quest.getObjectives().isEmpty()) {
            hoverLines.add((Component)Component.text((String)"Objectives:", (TextColor)NamedTextColor.YELLOW));
            int maxObjectives = Math.min(3, quest.getObjectives().size());
            for (int i = 0; i < maxObjectives; ++i) {
                Objective obj = quest.getObjectives().get(i);
                String prefix = quest.isSequential() ? i + 1 + ". " : "\u2022 ";
                hoverLines.add((Component)Component.text((String)("  " + prefix + obj.getDescription()), (TextColor)NamedTextColor.WHITE));
            }
            if (quest.getObjectives().size() > 3) {
                hoverLines.add((Component)Component.text((String)("  ... and " + (quest.getObjectives().size() - 3) + " more"), (TextColor)NamedTextColor.GRAY, (TextDecoration[])new TextDecoration[]{TextDecoration.ITALIC}));
            }
            hoverLines.add((Component)Component.empty());
        }
        hoverLines.add((Component)Component.text((String)"Rewards:", (TextColor)NamedTextColor.YELLOW));
        this.buildRewardLines(hoverLines, questId);
        if (quest.getConditions() != null && !quest.getConditions().getKeys(false).isEmpty()) {
            String itemReq;
            hoverLines.add((Component)Component.empty());
            hoverLines.add((Component)Component.text((String)"Requirements:", (TextColor)NamedTextColor.YELLOW));
            ConfigurationSection conditions = quest.getConditions();
            if (conditions.contains("cost")) {
                hoverLines.add((Component)Component.text((String)("  \u2022 Cost: $" + conditions.getDouble("cost")), (TextColor)NamedTextColor.RED));
            }
            if (conditions.contains("min-level")) {
                hoverLines.add((Component)Component.text((String)("  \u2022 Level " + conditions.getInt("min-level") + "+"), (TextColor)NamedTextColor.YELLOW));
            }
            if (conditions.contains("permission")) {
                hoverLines.add((Component)Component.text((String)"  \u2022 Special permission required", (TextColor)NamedTextColor.LIGHT_PURPLE));
            }
            if (conditions.contains("item") && (itemReq = conditions.getString("item")) != null && itemReq.contains(":")) {
                String[] parts = itemReq.split(":");
                String itemName = parts[0].replace("_", " ").toLowerCase();
                hoverLines.add((Component)Component.text((String)("  \u2022 " + parts[1] + "x " + itemName), (TextColor)NamedTextColor.AQUA));
            }
        }
        hoverLines.add((Component)Component.empty());
        hoverLines.add((Component)Component.text((String)"\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501\u2501", (TextColor)NamedTextColor.GOLD));
        return hoverLines;
    }

    private void buildRewardLines(List<Component> hoverLines, String questId) {
        try {
            List<RewardEntry> rewards = this.plugin.getRewardManager().getRewardList(questId);
            if (rewards != null && !rewards.isEmpty()) {
                int maxRewards = Math.min(5, rewards.size());
                for (int i = 0; i < maxRewards; ++i) {
                    RewardEntry reward = rewards.get(i);
                    try {
                        String rewardText = switch (reward.getType()) {
                            default -> throw new MatchException(null, null);
                            case RewardEntry.RewardType.XP -> {
                                Object amount = reward.getData().get("amount");
                                yield String.valueOf(amount != null ? amount : "0") + " XP";
                            }
                            case RewardEntry.RewardType.MONEY -> {
                                Object amount = reward.getData().get("amount");
                                if (amount instanceof Number) {
                                    Number numAmount = (Number)amount;
                                    yield "$" + String.format("%.2f", numAmount.doubleValue());
                                }
                                yield "$0.00";
                            }
                            case RewardEntry.RewardType.ITEM -> {
                                Object amountObj = reward.getData().get("amount");
                                int amount = amountObj instanceof Integer ? (Integer)amountObj : 1;
                                String material = (String)reward.getData().get("material");
                                String itemName = material != null ? material.replace("_", " ").toLowerCase() : "item";
                                yield amount + "x " + itemName;
                            }
                            case RewardEntry.RewardType.COMMAND -> "Special reward";
                            case RewardEntry.RewardType.QUEST -> {
                                String targetQuestId = (String)reward.getData().get("quest-id");
                                yield "Quest: " + (targetQuestId != null ? targetQuestId : "unknown");
                            }
                        };
                        NamedTextColor color = switch (reward.getType()) {
                            default -> throw new MatchException(null, null);
                            case RewardEntry.RewardType.XP -> NamedTextColor.GREEN;
                            case RewardEntry.RewardType.MONEY -> NamedTextColor.GOLD;
                            case RewardEntry.RewardType.ITEM -> NamedTextColor.AQUA;
                            case RewardEntry.RewardType.COMMAND -> NamedTextColor.LIGHT_PURPLE;
                            case RewardEntry.RewardType.QUEST -> NamedTextColor.YELLOW;
                        };
                        hoverLines.add((Component)Component.text((String)("  \u2022 " + rewardText), (TextColor)color));
                        continue;
                    }
                    catch (Exception e) {
                        this.plugin.getLogger().log(Level.WARNING, "[Quest List] Failed to display reward for quest {0}: {1}", new Object[]{questId, e.getMessage()});
                    }
                }
                if (rewards.size() > 5) {
                    hoverLines.add((Component)Component.text((String)("  ... and " + (rewards.size() - 5) + " more"), (TextColor)NamedTextColor.GRAY, (TextDecoration[])new TextDecoration[]{TextDecoration.ITALIC}));
                }
            } else {
                hoverLines.add((Component)Component.text((String)"  \u2022 None", (TextColor)NamedTextColor.GRAY));
            }
        }
        catch (Exception e) {
            hoverLines.add((Component)Component.text((String)"  \u2022 Error loading rewards", (TextColor)NamedTextColor.RED));
            this.plugin.getLogger().log(Level.WARNING, "[Quest List] Failed to load rewards for quest {0}: {1}", new Object[]{"unknown", e.getMessage()});
        }
    }
}

