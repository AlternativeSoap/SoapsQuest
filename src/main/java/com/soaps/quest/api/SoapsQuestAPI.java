package com.soaps.quest.api;

import com.soaps.quest.SoapsQuest;

import java.util.UUID;

public final class SoapsQuestAPI {
    private static SoapsQuest plugin;

    private SoapsQuestAPI() {
    }

    public static void initialize(SoapsQuest soapsQuest) {
        plugin = soapsQuest;
    }

    public static SoapsQuest getPlugin() {
        return plugin;
    }

    public static boolean isAvailable() {
        return plugin != null && plugin.getSigilManager() != null;
    }

    public static double getSigilBalance(UUID playerUuid) {
        if (!isAvailable()) {
            return 0.0;
        }
        return plugin.getSigilManager().getBalance(playerUuid);
    }

    public static double setSigilBalance(UUID playerUuid, double amount) {
        if (!isAvailable()) {
            return 0.0;
        }
        return plugin.getSigilManager().setBalance(playerUuid, amount);
    }

    public static double giveSigils(UUID playerUuid, double amount) {
        if (!isAvailable()) {
            return 0.0;
        }
        return plugin.getSigilManager().give(playerUuid, amount);
    }

    public static double takeSigils(UUID playerUuid, double amount) {
        if (!isAvailable()) {
            return 0.0;
        }
        return plugin.getSigilManager().take(playerUuid, amount);
    }

    public static double resetSigils(UUID playerUuid) {
        if (!isAvailable()) {
            return 0.0;
        }
        return plugin.getSigilManager().reset(playerUuid);
    }
}
