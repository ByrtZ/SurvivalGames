package dev.byrt.survivalgames.map

import org.bukkit.configuration.file.YamlConfiguration

enum class SGMap(val mapName: String,
                 val mapConfigFile: String,
                 var yamlConfigFile: YamlConfiguration?,
                 var preGameSpawns: MutableList<MapDataPoint>,
                 var participantSpawns: MutableList<MapDataPoint>,
                 var spectatorSpawns: MutableList<MapDataPoint>,
                 var supplyDropSpawns: MutableList<MapDataPoint>,
                 var lootChests: MutableList<MapDataPoint>,
                 var worldCenter: MutableList<MapDataPoint>,
                 val isQuickMatch: Boolean = false) {
    AUBURN_FOREST("Auburn Forest", "auburn_forest.yml", null, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()),
    ROUGHWORKS("Roughworks", "roughworks.yml", null, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()),
    MISTWOODS("Mistwoods", "mistwoods.yml", null, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), isQuickMatch = true),
    HIGHLANDS("Highlands", "highlands.yml", null, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()),
    AELUMIA_CITADEL("Aelumia Citadel", "aelumia_citadel.yml", null, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), isQuickMatch = true),
}