package dev.byrt.survivalgames.map

import org.bukkit.configuration.file.YamlConfiguration

enum class SGMap(val mapName: String,
                 val borderSize: Double = 750.0,
                 val mapConfigFile: String,
                 var yamlConfigFile: YamlConfiguration?,
                 var preGameSpawns: MutableList<MapDataPoint>,
                 var participantSpawns: MutableList<MapDataPoint>,
                 var spectatorSpawns: MutableList<MapDataPoint>,
                 var supplyDropSpawns: MutableList<MapDataPoint>,
                 var lootChests: MutableList<MapDataPoint>,
                 var worldCenter: MutableList<MapDataPoint>,
                 val isQuickMatch: Boolean = false) {
    AUBURN_FOREST("Auburn Forest", 750.0,"auburn_forest.yml", null, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()),
    ROUGHWORKS("Roughworks", 750.0, "roughworks.yml", null, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()),
    MISTWOODS("Mistwoods", 600.0, "mistwoods.yml", null, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), isQuickMatch = true),
    HIGHLANDS("Highlands", 750.0, "highlands.yml", null, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()),
    AELUMIA_CITADEL("Aelumia Citadel", 425.0, "aelumia_citadel.yml", null, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), isQuickMatch = true),
}