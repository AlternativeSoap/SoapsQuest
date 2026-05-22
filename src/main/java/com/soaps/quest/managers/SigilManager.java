package com.soaps.quest.managers;

import com.soaps.quest.SoapsQuest;
import com.soaps.quest.utils.YamlUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class SigilManager {
    private static final String BALANCES_PATH = "balances";

    private final SoapsQuest plugin;
    private final Map<UUID, Double> balances;
    private File sigilsFile;
    private FileConfiguration sigilsConfig;

    public SigilManager(SoapsQuest plugin) {
        this.plugin = plugin;
        this.balances = new ConcurrentHashMap<>();
        this.load();
    }

    public void load() {
        File dataFolder = new File(this.plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        this.sigilsFile = new File(dataFolder, "sigils.yml");
        if (!this.sigilsFile.exists()) {
            try {
                this.sigilsFile.createNewFile();
            } catch (IOException e) {
                this.plugin.getLogger().log(Level.SEVERE, "Failed to create sigils.yml", e);
            }
        }

        this.sigilsConfig = YamlConfiguration.loadConfiguration(this.sigilsFile);
        this.balances.clear();

        if (this.sigilsConfig.getConfigurationSection(BALANCES_PATH) == null) {
            return;
        }

        for (String uuidRaw : this.sigilsConfig.getConfigurationSection(BALANCES_PATH).getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidRaw);
                double value = this.sigilsConfig.getDouble(BALANCES_PATH + "." + uuidRaw, 0.0);
                if (value > 0.0) {
                    this.balances.put(uuid, value);
                }
            } catch (IllegalArgumentException ignored) {
                this.plugin.getLogger().log(Level.WARNING, "Invalid UUID in sigils.yml: {0}", uuidRaw);
            }
        }
    }

    public synchronized void save() {
        this.sigilsConfig.set(BALANCES_PATH, null);
        for (Map.Entry<UUID, Double> entry : this.balances.entrySet()) {
            this.sigilsConfig.set(BALANCES_PATH + "." + entry.getKey(), round(entry.getValue()));
        }

        try {
            YamlUtil.atomicSave(this.sigilsConfig, this.sigilsFile);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save sigils.yml", e);
        }
    }

    public double getBalance(UUID playerUuid) {
        return round(this.balances.getOrDefault(playerUuid, 0.0));
    }

    public double getBalance(OfflinePlayer player) {
        return getBalance(player.getUniqueId());
    }

    public double setBalance(UUID playerUuid, double amount) {
        double sanitized = Math.max(0.0, round(amount));
        if (sanitized <= 0.0) {
            this.balances.remove(playerUuid);
        } else {
            this.balances.put(playerUuid, sanitized);
        }
        save();
        return sanitized;
    }

    public double give(UUID playerUuid, double amount) {
        if (amount <= 0.0) {
            return getBalance(playerUuid);
        }
        return setBalance(playerUuid, getBalance(playerUuid) + amount);
    }

    public double take(UUID playerUuid, double amount) {
        if (amount <= 0.0) {
            return getBalance(playerUuid);
        }
        return setBalance(playerUuid, Math.max(0.0, getBalance(playerUuid) - amount));
    }

    public double reset(UUID playerUuid) {
        this.balances.remove(playerUuid);
        save();
        return 0.0;
    }

    public boolean has(UUID playerUuid, double amount) {
        return getBalance(playerUuid) >= Math.max(0.0, amount);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
