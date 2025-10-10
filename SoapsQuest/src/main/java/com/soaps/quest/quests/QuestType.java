package com.soaps.quest.quests;

/**
 * Enum representing the different types of quests available.
 * Each type corresponds to a specific action or objective the player must complete.
 */
public enum QuestType {
    KILL,           // Kill X entity type
    BREAK,          // Break X block type
    PLACE,          // Place X block type
    COLLECT,        // Collect X item type
    COMMAND,        // Triggered via command/event
    PLACEHOLDER     // Numeric PlaceholderAPI quests
}
