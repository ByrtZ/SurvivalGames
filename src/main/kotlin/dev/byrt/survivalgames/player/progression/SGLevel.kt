package dev.byrt.survivalgames.player.progression

import dev.byrt.survivalgames.player.data.Rank

enum class SGLevel(val levelName: String, val requiredXp: Int, val rankAssociated: Rank) {
    LEVEL_1("Level 1", 100, Rank.RECRUIT),
    LEVEL_2("Level 2", 200, Rank.RECRUIT),
    LEVEL_3("Level 3", 300, Rank.RECRUIT),
    LEVEL_4("Level 4", 400, Rank.RECRUIT),
    LEVEL_5("Level 5", 500, Rank.SOLDIER),
    LEVEL_6("Level 6", 600, Rank.SOLDIER),
    LEVEL_7("Level 7", 700, Rank.SOLDIER),
    LEVEL_8("Level 8", 800, Rank.SOLDIER),
    LEVEL_9("Level 9", 900, Rank.SOLDIER),
    LEVEL_10("Level 10", 1000, Rank.CONSTABLE),
    LEVEL_11("Level 11", 1100, Rank.CONSTABLE),
    LEVEL_12("Level 12", 1200, Rank.CONSTABLE),
    LEVEL_13("Level 13", 1300, Rank.CONSTABLE),
    LEVEL_14("Level 14", 1400, Rank.CONSTABLE),
    LEVEL_15("Level 15", 1500, Rank.WINGS),
    LEVEL_16("Level 16", 1600, Rank.WINGS),
    LEVEL_17("Level 17", 1700, Rank.WINGS),
    LEVEL_18("Level 18", 1800, Rank.WINGS),
    LEVEL_19("Level 19", 1900, Rank.WINGS),
    LEVEL_20("Level 20", 2000, Rank.GENDARME),
    LEVEL_21("Level 21", 2100, Rank.GENDARME),
    LEVEL_22("Level 22", 2200, Rank.GENDARME),
    LEVEL_23("Level 23", 2300, Rank.GENDARME),
    LEVEL_24("Level 24", 2400, Rank.GENDARME),
    LEVEL_25("Level 25", 2500, Rank.GENDARME),
    LEVEL_26("Level 26", 32500, Rank.ROYAL);
}