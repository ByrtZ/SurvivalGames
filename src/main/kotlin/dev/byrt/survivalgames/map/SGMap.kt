package dev.byrt.survivalgames.map

enum class SGMap(val mapName: String,
                 val mapConfig: String,
                 var preGameSpawns: MutableList<MapDataPoint>,
                 var participantSpawns: MutableList<MapDataPoint>,
                 var spectatorSpawns: MutableList<MapDataPoint>,
                 var supplyDropSpawns: MutableList<MapDataPoint>,
                 var lootChests: MutableList<MapDataPoint>,
                 var worldCenter: MutableList<MapDataPoint>,
                 val isQuickMatch: Boolean = false) {
    AUBURN_FOREST("Auburn Forest", "auburn_forest.yml", mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()),
    ROUGHWORKS("Roughworks", "roughworks.yml", mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()),
    MISTWOODS("Mistwoods", "mistwoods.yml", mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), isQuickMatch = true),
    HIGHLANDS("Highlands", "highlands.yml", mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()),
    AELUMIA_CITADEL("Aelumia Citadel", "aelumia_citadel.yml", mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), isQuickMatch = true),
}