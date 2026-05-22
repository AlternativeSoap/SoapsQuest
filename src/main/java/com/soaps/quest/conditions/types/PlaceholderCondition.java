/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.clip.placeholderapi.PlaceholderAPI
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 */
package com.soaps.quest.conditions.types;

import com.soaps.quest.conditions.ConditionResult;
import com.soaps.quest.conditions.QuestCondition;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PlaceholderCondition
implements QuestCondition {
    private final String expression;

    public PlaceholderCondition(String expression) {
        this.expression = expression;
    }

    @Override
    public String getType() {
        return "placeholder";
    }

    @Override
    public ConditionResult check(Player player, boolean consumeResources) {
        try {
            if (this.evaluatePlaceholder(player, this.expression)) {
                return ConditionResult.success();
            }
            return ConditionResult.failure("&cYou don't meet the requirements!");
        }
        catch (Exception e) {
            return ConditionResult.failure("&cInvalid placeholder expression!");
        }
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", (Object)this.getType());
        section.set("placeholder", (Object)this.expression);
    }

    @Override
    public String getDescription() {
        return "Placeholder check: " + this.expression;
    }

    @Override
    public String getDisplayString() {
        return "&7Placeholder: &f" + this.expression;
    }

    private boolean evaluatePlaceholder(Player player, String expr) {
        String parsed = PlaceholderAPI.setPlaceholders((Player)player, (String)expr);
        for (String op : new String[]{">=", "<=", "==", "!=", ">", "<"}) {
            String[] parts;
            if (!parsed.contains(op) || (parts = parsed.split(op, 2)).length != 2) continue;
            try {
                double left = Double.parseDouble(parts[0].trim());
                double right = Double.parseDouble(parts[1].trim());
                return switch (op) {
                    case ">=" -> {
                        if (left >= right) {
                            yield true;
                        }
                        yield false;
                    }
                    case "<=" -> {
                        if (left <= right) {
                            yield true;
                        }
                        yield false;
                    }
                    case "==" -> {
                        if (Math.abs(left - right) < 1.0E-4) {
                            yield true;
                        }
                        yield false;
                    }
                    case "!=" -> {
                        if (Math.abs(left - right) >= 1.0E-4) {
                            yield true;
                        }
                        yield false;
                    }
                    case ">" -> {
                        if (left > right) {
                            yield true;
                        }
                        yield false;
                    }
                    case "<" -> {
                        if (left < right) {
                            yield true;
                        }
                        yield false;
                    }
                    default -> false;
                };
            }
            catch (NumberFormatException e) {
                String leftStr = parts[0].trim();
                String rightStr = parts[1].trim();
                return switch (op) {
                    case "==" -> leftStr.equals(rightStr);
                    case "!=" -> {
                        if (!leftStr.equals(rightStr)) {
                            yield true;
                        }
                        yield false;
                    }
                    default -> false;
                };
            }
        }
        return !parsed.isEmpty() && !parsed.equalsIgnoreCase("false") && !parsed.equals("0");
    }

    public static PlaceholderCondition deserialize(ConfigurationSection section) {
        String expression = section.getString("placeholder");
        if (expression == null || expression.isEmpty()) {
            return null;
        }
        return new PlaceholderCondition(expression);
    }
}

