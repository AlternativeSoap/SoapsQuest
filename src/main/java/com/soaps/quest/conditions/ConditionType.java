/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package com.soaps.quest.conditions;

import com.soaps.quest.conditions.QuestCondition;
import java.util.function.Function;
import org.bukkit.Material;

public class ConditionType {
    private final String id;
    private final String displayName;
    private final String description;
    private final Material icon;
    private final String inputPrompt;
    private final Function<String, QuestCondition> factory;
    private final boolean requiresInput;

    public ConditionType(String id, String displayName, String description, Material icon, String inputPrompt, Function<String, QuestCondition> factory) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.inputPrompt = inputPrompt;
        this.factory = factory;
        this.requiresInput = true;
    }

    public ConditionType(String id, String displayName, String description, Material icon, Function<String, QuestCondition> factory) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.inputPrompt = null;
        this.factory = factory;
        this.requiresInput = false;
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

    public boolean requiresInput() {
        return this.requiresInput;
    }

    public QuestCondition createCondition(String input) throws IllegalArgumentException {
        return this.factory.apply(input);
    }
}

