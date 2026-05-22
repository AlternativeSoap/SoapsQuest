/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package com.soaps.quest.objectives;

import com.soaps.quest.objectives.Objective;
import java.util.function.Function;
import org.bukkit.Material;

public class ObjectiveType {
    private final String id;
    private final String displayName;
    private final String description;
    private final Material icon;
    private final String inputPrompt;
    private final Function<String, Objective> factory;
    private final boolean requiresInput;

    public ObjectiveType(String id, String displayName, Material icon, String inputPrompt, Function<String, Objective> factory) {
        this.id = id;
        this.displayName = displayName;
        this.description = null;
        this.icon = icon;
        this.inputPrompt = inputPrompt;
        this.factory = factory;
        this.requiresInput = true;
    }

    public ObjectiveType(String id, String displayName, String description, Material icon, String inputPrompt, Function<String, Objective> factory) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.inputPrompt = inputPrompt;
        this.factory = factory;
        this.requiresInput = true;
    }

    public ObjectiveType(String id, String displayName, Material icon, Function<String, Objective> factory) {
        this.id = id;
        this.displayName = displayName;
        this.description = null;
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

    public Function<String, Objective> getFactory() {
        return this.factory;
    }

    public boolean requiresInput() {
        return this.requiresInput;
    }
}

