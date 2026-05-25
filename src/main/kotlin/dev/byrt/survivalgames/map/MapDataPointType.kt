package dev.byrt.survivalgames.map

enum class MapDataPointType(val typeName: String) {
    PREGAME_SPAWN("pregame_spawn"),
    PARTICIPANT_SPAWN("participant_spawn"),
    SPECTATOR_SPAWN("spectator_spawn"),
    SUPPLY_DROP_SPAWN("supply_drop_spawn"),
    LOOT_CHEST_1("loot_chest_1"),
    LOOT_CHEST_2("loot_chest_2"),
    LOOT_CHEST_3("loot_chest_3"),
    WORLD_CENTER("world_center");
}