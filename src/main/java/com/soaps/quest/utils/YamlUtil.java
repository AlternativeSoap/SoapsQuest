/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 */
package com.soaps.quest.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;

public final class YamlUtil {
    private YamlUtil() {
    }

    public static void atomicSave(FileConfiguration config, File target) throws IOException {
        File parent = target.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        File temp = new File(parent, target.getName() + ".tmp");
        config.save(temp);
        int maxRetries = 3;
        IOException lastException = null;
        for (int attempt = 0; attempt < maxRetries; ++attempt) {
            try {
                Files.move(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                return;
            }
            catch (AtomicMoveNotSupportedException e) {
                try {
                    Files.move(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    return;
                }
                catch (IOException fallbackEx) {
                    lastException = fallbackEx;
                    continue;
                }
            }
            catch (IOException e) {
                lastException = e;
                if (attempt >= maxRetries - 1) continue;
                try {
                    Thread.sleep(50 * (attempt + 1));
                    continue;
                }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }
        if (temp.exists()) {
            temp.delete();
        }
        throw lastException != null ? lastException : new IOException("Failed to save " + target.getName());
    }

    public static boolean atomicSaveSilent(FileConfiguration config, File target, Logger logger) {
        try {
            YamlUtil.atomicSave(config, target);
            return true;
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save " + target.getName(), e);
            return false;
        }
    }
}

