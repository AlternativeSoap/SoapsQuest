/*
 * Decompiled with CFR 0.152.
 */
package com.soaps.quest.utils;

import com.soaps.quest.utils.ColorUtil;

public class StringUtil {
    private StringUtil() {
    }

    public static String sanitizeQuestId(String input) {
        if (input == null || input.isEmpty()) {
            return "quest";
        }
        String clean = ColorUtil.stripColor(input);
        clean = clean.replaceAll("[^a-zA-Z0-9_-]", "_");
        clean = clean.replaceAll("^_+|_+$", "");
        clean = clean.replaceAll("_+", "_");
        if ((clean = clean.toLowerCase()).isEmpty()) {
            return "quest";
        }
        return clean;
    }
}

