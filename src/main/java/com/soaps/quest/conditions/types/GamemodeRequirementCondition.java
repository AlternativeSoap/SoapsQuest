/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 */
package com.soaps.quest.conditions.types;

import com.soaps.quest.conditions.ConditionResult;
import com.soaps.quest.conditions.QuestCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class GamemodeRequirementCondition
implements QuestCondition {
    private final List<GameMode> allowedGamemodes;

    public GamemodeRequirementCondition(List<GameMode> allowedGamemodes) {
        this.allowedGamemodes = new ArrayList<GameMode>(allowedGamemodes);
    }

    @Override
    public String getType() {
        return "gamemode";
    }

    @Override
    public ConditionResult check(Player player, boolean consumeResources) {
        if (this.allowedGamemodes.isEmpty()) {
            return ConditionResult.success();
        }
        if (!this.allowedGamemodes.contains(player.getGameMode())) {
            String allowed = this.formatGamemodeList();
            return ConditionResult.failure(String.format("&cYou must be in %s gamemode!", allowed));
        }
        return ConditionResult.success();
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", (Object)this.getType());
        ArrayList<String> modes = new ArrayList<String>();
        for (GameMode mode : this.allowedGamemodes) {
            modes.add(mode.name());
        }
        section.set("gamemode", modes);
    }

    @Override
    public String getDescription() {
        return "Required gamemode: " + this.formatGamemodeList();
    }

    @Override
    public String getDisplayString() {
        return "&7Gamemode: &f" + this.formatGamemodeList();
    }

    private String formatGamemodeList() {
        if (this.allowedGamemodes.isEmpty()) {
            return "Any";
        }
        if (this.allowedGamemodes.size() == 1) {
            return this.formatGamemodeName(this.allowedGamemodes.get(0));
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.allowedGamemodes.size(); ++i) {
            if (i > 0) {
                if (i == this.allowedGamemodes.size() - 1) {
                    sb.append(" or ");
                } else {
                    sb.append(", ");
                }
            }
            sb.append(this.formatGamemodeName(this.allowedGamemodes.get(i)));
        }
        return sb.toString();
    }

    private String formatGamemodeName(GameMode mode) {
        return switch (mode) {
            case SURVIVAL -> "Survival";
            case CREATIVE -> "Creative";
            case ADVENTURE -> "Adventure";
            case SPECTATOR -> "Spectator";
        };
    }

    public static GamemodeRequirementCondition deserialize(ConfigurationSection section) {
        List<String> modeStrings = section.getStringList("gamemode");
        ArrayList<GameMode> modes = new ArrayList<GameMode>();
        for (String modeStr : modeStrings) {
            try {
                GameMode mode = GameMode.valueOf((String)modeStr.toUpperCase());
                modes.add(mode);
            }
            catch (IllegalArgumentException illegalArgumentException) {}
        }
        return new GamemodeRequirementCondition(modes);
    }
}

