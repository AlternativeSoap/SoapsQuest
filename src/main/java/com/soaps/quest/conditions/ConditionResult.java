/*
 * Decompiled with CFR 0.152.
 */
package com.soaps.quest.conditions;

public class ConditionResult {
    private final boolean success;
    private final String message;

    private ConditionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static ConditionResult success() {
        return new ConditionResult(true, null);
    }

    public static ConditionResult failure(String message) {
        return new ConditionResult(false, message);
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }
}

