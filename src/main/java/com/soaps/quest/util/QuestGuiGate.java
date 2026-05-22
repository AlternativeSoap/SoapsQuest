package com.soaps.quest.util;

import org.bukkit.entity.Player;

import com.soaps.common.api.SoapsConfigKeys;
import com.soaps.quest.SoapsQuest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Blocks inventory GUIs when {@code gui.enabled} is false in config.yml.
 */
public final class QuestGuiGate {

    private QuestGuiGate() {
    }

    public static boolean allow(SoapsQuest plugin, Player player) {
        if (!SoapsConfigKeys.readGuiEnabled(plugin.getConfig())) {
            player.sendMessage(Component.text(
                    "Quest GUIs are disabled in config.yml (gui.enabled: false).", NamedTextColor.RED));
            return false;
        }
        return true;
    }
}
