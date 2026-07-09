package dev.byrt.survivalgames.map

import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.plugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object MapManager {
    private val configMap = mutableMapOf<String, List<MapDataPoint>>()
    fun readAllMapConfigs() {
        logger.info("Loading all map configurations")
        val mapFolderPath = "${plugin.dataFolder.path}/maps"
        val mapFolder = File(mapFolderPath)
        if(!mapFolder.exists()) {
            mapFolder.mkdirs()
        }
        val configs = mapFolder.listFiles()
        if(configs.isNullOrEmpty()) {
            logger.severe("Failed to find map configurations.")
            return
        } else {
            for(file in configs) {
                readMapConfig(file.name)
            }
            verifyMapData()
        }
    }

    fun readMapConfig(name: String) {
        if(!name.endsWith(".yml")) {
            logger.warning("Map configuration file '$name' is not of type YAML or is invalid, aborting configuration loading.")
        } else {
            logger.info("Loading map configuration for $name")
            val mapFilePath = "${plugin.dataFolder.path}/maps/$name"
            val mapFile = File(mapFilePath)
            if(!mapFile.exists()) {
                mapFile.mkdirs()
            }
            val config = YamlConfiguration.loadConfiguration(mapFile)
            val allPoints = MapDataPointType.entries
                .flatMap { type ->
                    config.getMapList(type.typeName).map { map ->
                        MapDataPoint(
                            x = (map["x"] as Number).toDouble(),
                            y = (map["y"] as Number).toDouble(),
                            z = (map["z"] as Number).toDouble(),
                            mapDataPointType = type
                        )
                    }
                }
            SGMap.entries.forEach { map ->
                if(map.mapConfigFile == mapFile.name) {
                    map.yamlConfigFile = config
                    allPoints.forEach { point ->
                        when(point.mapDataPointType) {
                            MapDataPointType.PREGAME_SPAWN -> map.preGameSpawns.add(point)
                            MapDataPointType.PARTICIPANT_SPAWN -> map.participantSpawns.add(point)
                            MapDataPointType.SPECTATOR_SPAWN -> map.spectatorSpawns.add(point)
                            MapDataPointType.SUPPLY_DROP_SPAWN -> map.supplyDropSpawns.add(point)
                            MapDataPointType.LOOT_CHEST -> map.lootChests.add(point)
                            MapDataPointType.WORLD_CENTER -> map.worldCenter.add(point)
                        }
                    }
                    logger.info("Assigned ${mapFile.name} data points to map ${map.mapName}")
                }
            }
            configMap[mapFile.name] = allPoints
            logger.info("Loaded map configuration for ${mapFile.name}")
        }
    }

    fun verifyMapData() {
        val allPoints = MapDataPointType.entries
        SGMap.entries.forEach { map ->
           logger.info("Verifying map configuration for map '${map.mapName}'")
           allPoints.forEach { point ->
                when(point) {
                    MapDataPointType.PREGAME_SPAWN -> if(map.preGameSpawns.isEmpty()) logger.warning("No data points found for $point")
                    MapDataPointType.PARTICIPANT_SPAWN -> if(map.participantSpawns.isEmpty()) logger.warning("No data points found for $point")
                    MapDataPointType.SPECTATOR_SPAWN -> if(map.spectatorSpawns.isEmpty()) logger.warning("No data points found for $point")
                    MapDataPointType.SUPPLY_DROP_SPAWN -> if(map.supplyDropSpawns.isEmpty()) logger.warning("No data points found for $point")
                    MapDataPointType.LOOT_CHEST -> if(map.lootChests.isEmpty()) logger.warning("No data points found for $point")
                    MapDataPointType.WORLD_CENTER -> if(map.worldCenter.isEmpty()) logger.warning("No data points found for $point")
                }
            }
        }
    }

    fun getMapData(config: String) = configMap[config] ?: emptyList<MapDataPoint>().also { logger.warning("Failed to load config '$config' or config is empty.") }
}