/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.TextComponent
 *  net.kyori.adventure.text.minimessage.MiniMessage
 *  net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
 *  net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
 */
package com.soaps.quest.utils;

import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ColorUtil {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_AMPERSAND = LegacyComponentSerializer.legacyAmpersand();
    private static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.legacySection();
    private static final PlainTextComponentSerializer PLAIN_TEXT = PlainTextComponentSerializer.plainText();
    private static final Map<Character, String> LEGACY_TO_MINIMESSAGE = Map.ofEntries(Map.entry(Character.valueOf('0'), "<black>"), Map.entry(Character.valueOf('1'), "<dark_blue>"), Map.entry(Character.valueOf('2'), "<dark_green>"), Map.entry(Character.valueOf('3'), "<dark_aqua>"), Map.entry(Character.valueOf('4'), "<dark_red>"), Map.entry(Character.valueOf('5'), "<dark_purple>"), Map.entry(Character.valueOf('6'), "<gold>"), Map.entry(Character.valueOf('7'), "<gray>"), Map.entry(Character.valueOf('8'), "<dark_gray>"), Map.entry(Character.valueOf('9'), "<blue>"), Map.entry(Character.valueOf('a'), "<green>"), Map.entry(Character.valueOf('b'), "<aqua>"), Map.entry(Character.valueOf('c'), "<red>"), Map.entry(Character.valueOf('d'), "<light_purple>"), Map.entry(Character.valueOf('e'), "<yellow>"), Map.entry(Character.valueOf('f'), "<white>"), Map.entry(Character.valueOf('k'), "<obfuscated>"), Map.entry(Character.valueOf('l'), "<bold>"), Map.entry(Character.valueOf('m'), "<strikethrough>"), Map.entry(Character.valueOf('n'), "<underlined>"), Map.entry(Character.valueOf('o'), "<italic>"), Map.entry(Character.valueOf('r'), "<reset>"));

    public static Component parse(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        return MINI_MESSAGE.deserialize(text);
    }

    public static Component msg(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        return LEGACY_AMPERSAND.deserialize(text);
    }

    public static String color(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        TextComponent component = LEGACY_AMPERSAND.deserialize(text);
        return LEGACY_SECTION.serialize((Component)component);
    }

    public static Component colorize(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        boolean hasMiniMessage = text.indexOf(60) >= 0 && text.indexOf(62) > text.indexOf(60);
        boolean hasLegacy = ColorUtil.hasLegacyCodes(text);
        if (hasMiniMessage) {
            try {
                String toDeserialize = hasLegacy ? ColorUtil.legacyToMiniMessage(text) : text;
                return MINI_MESSAGE.deserialize(toDeserialize);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return LEGACY_AMPERSAND.deserialize(text);
    }

    private static boolean hasLegacyCodes(String text) {
        for (int i = 0; i < text.length() - 1; ++i) {
            char code;
            if (text.charAt(i) != '&' || !LEGACY_TO_MINIMESSAGE.containsKey(Character.valueOf(code = Character.toLowerCase(text.charAt(i + 1))))) continue;
            return true;
        }
        return false;
    }

    private static String legacyToMiniMessage(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); ++i) {
            char code;
            String tag;
            if (text.charAt(i) == '&' && i + 1 < text.length() && (tag = LEGACY_TO_MINIMESSAGE.get(Character.valueOf(code = Character.toLowerCase(text.charAt(i + 1))))) != null) {
                sb.append(tag);
                ++i;
                continue;
            }
            sb.append(text.charAt(i));
        }
        return sb.toString();
    }

    public static String stripColor(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        TextComponent component = LEGACY_AMPERSAND.deserialize(text);
        return PLAIN_TEXT.serialize((Component)component);
    }
}

