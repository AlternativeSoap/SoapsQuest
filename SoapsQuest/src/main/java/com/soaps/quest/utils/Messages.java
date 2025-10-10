package com.soaps.quest.utils;

/**
 * Message key constants for the plugin.
 * All actual message text is stored in messages.yml for full customization.
 * This class only contains the keys to prevent typos when referencing messages.
 */
public class Messages {
    
    // General messages
    public static final String PREFIX = "prefix";
    public static final String NO_PERMISSION = "no-permission";
    public static final String PLAYER_NOT_FOUND = "player-not-found";
    public static final String QUEST_NOT_FOUND = "quest-not-found";
    public static final String INVALID_QUEST_TYPE = "invalid-quest-type";
    public static final String PLAYER_ONLY = "player-only";
    public static final String CONFIG_RELOADED = "config-reloaded";
    
    // Quest paper messages
    public static final String QUEST_GIVEN = "quest-given";
    public static final String QUEST_RECEIVED = "quest-received";
    public static final String QUEST_ALREADY_ACTIVE = "quest-already-active";
    public static final String QUEST_PROGRESS = "quest-progress";
    public static final String QUEST_COMPLETE = "quest-complete";
    public static final String QUEST_REWARD_GIVEN = "quest-reward-given";
    
    // Multi-objective system (NEW)
    public static final String OBJECTIVE_PROGRESS = "objective-progress";
    public static final String OBJECTIVE_COMPLETE = "objective-complete";
    public static final String ALL_OBJECTIVES_COMPLETE = "all-objectives-complete";
    
    // Progress display
    public static final String PROGRESS_SELF = "progress-self";
    public static final String PROGRESS_OTHER = "progress-other";
    public static final String PROGRESS_NO_QUESTS = "progress-no-quests";
    public static final String PROGRESS_LINE = "progress-line";
    
    // Quest list
    public static final String QUEST_LIST_HEADER = "quest-list-header";
    public static final String QUEST_LIST_ENTRY = "quest-list-entry";
    public static final String QUEST_LIST_EMPTY = "quest-list-empty";
    
    // Reward management
    public static final String REWARD_ADDED = "reward-added";
    public static final String REWARD_REMOVED = "reward-removed";
    public static final String REWARD_INDEX_INVALID = "reward-index-invalid";
    public static final String MUST_HOLD_ITEM = "must-hold-item";
    
    // Quest paper lore
    public static final String PAPER_LORE_HEADER = "paper-lore-header";
    public static final String PAPER_LORE_PROGRESS = "paper-lore-progress";
    public static final String PAPER_LORE_TYPE = "paper-lore-type";
    public static final String PAPER_LORE_COMPLETE = "paper-lore-complete";
    public static final String PAPER_LORE_FOOTER = "paper-lore-footer";
    
    // Error messages
    public static final String ERROR_SAVING_DATA = "error-saving-data";
    public static final String ERROR_LOADING_DATA = "error-loading-data";
    public static final String ERROR_INVALID_CONFIG = "error-invalid-config";
    
    // Usage messages
    public static final String USAGE_MAIN = "usage-main";
    public static final String USAGE_GIVE = "usage-give";
    public static final String USAGE_PROGRESS = "usage-progress";
    public static final String USAGE_ADDREWARD = "usage-addreward";
    public static final String USAGE_REMOVEREWARD = "usage-removereward";
    
    private Messages() {
        // Prevent instantiation of utility class
    }
}
