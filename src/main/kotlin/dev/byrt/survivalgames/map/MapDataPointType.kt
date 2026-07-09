package dev.byrt.survivalgames.map

enum class MapDataPointType(val typeName: String) {
    PREGAME_SPAWN("pregame_spawn"),
    PARTICIPANT_SPAWN("participant_spawn"),
    SPECTATOR_SPAWN("spectator_spawn"),
    SUPPLY_DROP_SPAWN("supply_drop_spawn"),
    LOOT_CHEST("loot_chest"),
    WORLD_CENTER("world_center");
}