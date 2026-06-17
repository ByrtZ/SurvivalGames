package dev.byrt.survivalgames.map

import dev.byrt.survivalgames.game.GameContainer
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.ChatUtility
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import dev.byrt.survivalgames.util.Keys
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.io.File

object MapTools {
    fun createDataPoint(player: Player, clickedBlock: Block, dataPointType: MapDataPointType, map: SGMap) {
        val pointsToBeCentered = listOf(MapDataPointType.PREGAME_SPAWN, MapDataPointType.PARTICIPANT_SPAWN, MapDataPointType.SPECTATOR_SPAWN)
        ChatUtility.broadcastDev("<yellow>${player.name} added data point $dataPointType at ${clickedBlock.location.x + if(pointsToBeCentered.contains(dataPointType)) 0.5 else 0.0}, ${clickedBlock.location.y + 1}, ${clickedBlock.location.z + if(pointsToBeCentered.contains(dataPointType)) 0.5 else 0.0} on ${map}.", false)

        clickedBlock.getRelative(BlockFace.UP).type = Material.OCHRE_FROGLIGHT
        clickedBlock.location.world.spawn(clickedBlock.location.add(0.5, 2.5, 0.5), TextDisplay::class.java).apply {
            isShadowed = true
            billboard = Display.Billboard.VERTICAL
            text(Formatting.allTags.deserialize("<gold><b>${SG_FONT_TAG}NEW DATA POINT</b></gold><newline>Placed by <yellow>${player.name}</yellow><newline>Type: <yellow>${dataPointType.typeName}</yellow><newline>Location: <yellow>(${clickedBlock.location.x + 0.5}, ${clickedBlock.location.y + 1}, ${clickedBlock.location.z + 0.5})</yellow><newline>Map: <yellow>${map.mapName}</yellow>"))
        }
        val config = map.yamlConfigFile ?: return
        val path = dataPointType.typeName
        val points = config.getMapList(path).toMutableList()
        points.add(
            mapOf(
                "x" to clickedBlock.location.x + if(pointsToBeCentered.contains(dataPointType)) 0.5 else 0.0,
                "y" to clickedBlock.location.y + 1,
                "z" to clickedBlock.location.z + if(pointsToBeCentered.contains(dataPointType)) 0.5 else 0.0
            )
        )
        config.set(path, points)
        config.save(File("${plugin.dataFolder}/maps/${map.mapConfigFile}"))
    }

    fun visualiseDataPoints(container: GameContainer, map: SGMap) {
        val dataPoints = map.lootChests + map.supplyDropSpawns + map.preGameSpawns + map.participantSpawns + map.spectatorSpawns + map.worldCenter
        dataPoints.forEach { point ->
            val location = Location(container.containerWorld, point.x, point.y, point.z)
            location.block.type = Material.OCHRE_FROGLIGHT
            location.world.spawn(location.add(if(location.x == 0.0) 0.5 else 0.0, 1.5, if(location.z == 0.0) 0.5 else 0.0), TextDisplay::class.java).apply {
                isShadowed = true
                billboard = Display.Billboard.VERTICAL
                text(Formatting.allTags.deserialize("<yellow><b>${SG_FONT_TAG}DATA POINT</b></yellow><newline>Type: <yellow>${point.mapDataPointType.typeName}</yellow><newline>Location: <yellow>(${point.x}, ${point.y}, ${point.z})</yellow><newline>Map: <yellow>${map.mapName}</yellow>"))
            }
        }
    }
}