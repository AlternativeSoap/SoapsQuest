/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package com.soaps.quest.rewards;

import com.soaps.quest.rewards.QuestReward;
import java.util.function.Function;
import org.bukkit.Material;

public class RewardType {
    private final String id;
    private final String displayName;
    private final String description;
    private final Material icon;
    private final String inputPrompt;
    private final Function<String, QuestReward> factory;

    public RewardType(String id, String displayName, String description, Material icon, String inputPrompt, Function<String, QuestReward> factory) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.inputPrompt = inputPrompt;
        this.factory = factory;
    }

    public RewardType(String id, String displayName, Material icon, String inputPrompt, Function<String, QuestReward> factory) {
        this(id, displayName, "", icon, inputPrompt, factory);
    }

    public String getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getDescription() {
        return this.description;
    }

    public Material getIcon() {
        return this.icon;
    }

    public String getInputPrompt() {
        return this.inputPrompt;
    }

    public Function<String, QuestReward> getFactory() {
        return this.factory;
    }

    public QuestReward createFromInput(String input) {
        try {
            return this.factory.apply(input);
        }
        catch (Exception e) {
            return null;
        }
    }
}

