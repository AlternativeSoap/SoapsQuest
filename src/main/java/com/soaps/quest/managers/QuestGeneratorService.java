/*
 * Decompiled with CFR 0.152.
 */
package com.soaps.quest.managers;

import java.util.List;
import java.util.UUID;

public interface QuestGeneratorService {
    public String generateQuest();

    public String generateQuest(String var1);

    public boolean isEnabled();

    public List<String> getAllowedTypes();

    public int getMaxBatchGenerate();

    public long getCooldownSeconds();

    public boolean isOnCooldown(UUID var1);

    public long getCooldownRemaining(UUID var1);

    public void startCooldown(UUID var1);

    public void reload();
}

