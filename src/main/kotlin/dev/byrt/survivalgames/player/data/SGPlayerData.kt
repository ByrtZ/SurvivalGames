package dev.byrt.survivalgames.player.data

import dev.byrt.survivalgames.ioCoroutineScope
import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.SGPlayer
import dev.byrt.survivalgames.player.progression.SGLevel
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.ChatUtility
import dev.byrt.survivalgames.text.SG_FONT_TAG
import kotlinx.coroutines.launch
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID
import kotlin.toString

object SGPlayerData {
    private val playerData = mutableMapOf<UUID, FileConfiguration>()

    fun getPlayerData(player: Player) {
        try {
            val folder = File("${plugin.dataFolder}/players")
            if(!folder.exists()) folder.mkdirs()
            val playerFile = File(folder,"${player.uniqueId}.yml")
            if(!playerFile.exists()) playerFile.createNewFile()
            val fileConfiguration = YamlConfiguration.loadConfiguration(playerFile)
            // Set all if null
            for(stat in SGStatistics.entries) {
                if(fileConfiguration.get("${player.uniqueId}${stat.statistic.statisticConfigPath}") == null) {
                    fileConfiguration.set("${player.uniqueId}${stat.statistic.statisticConfigPath}",
                        when(stat) { // Treat nonintegers as 'edge cases' as they have different properties
                            SGStatistics.RANK -> Rank.RECRUIT.name
                            SGStatistics.LEVEL -> SGLevel.LEVEL_1.name
                            else -> 0
                        }
                    )
                }
            }
            // Locally set data from config, save and store file configuration reference
            player.sgPlayer().rank = Rank.valueOf(fileConfiguration.get("${player.uniqueId}${SGStatistics.RANK.statistic.statisticConfigPath}").toString())
            player.sgPlayer().level = SGLevel.valueOf(fileConfiguration.get("${player.uniqueId}${SGStatistics.LEVEL.statistic.statisticConfigPath}").toString())
            player.sgPlayer().exp = fileConfiguration.get("${player.uniqueId}${SGStatistics.EXPERIENCE.statistic.statisticConfigPath}") as Int
            player.sgPlayer().eliminations = fileConfiguration.get("${player.uniqueId}${SGStatistics.ELIMINATIONS.statistic.statisticConfigPath}") as Int
            player.sgPlayer().wins = fileConfiguration.get("${player.uniqueId}${SGStatistics.WINS.statistic.statisticConfigPath}") as Int
            player.sgPlayer().matchesPlayed = fileConfiguration.get("${player.uniqueId}${SGStatistics.GAMES_PLAYED.statistic.statisticConfigPath}") as Int

            fileConfiguration.save(playerFile)
            if(playerData.containsKey(player.uniqueId)) playerData.remove(player.uniqueId)
            playerData[player.uniqueId] = fileConfiguration
            logger.info("Player data fetched for player ${player.name} (${player.uniqueId})")
        } catch(e: Exception) {
            ChatUtility.broadcastDev("$SG_FONT_TAG<#ff3333>Something went wrong while trying to fetch player data for player ${player.name} (${player.uniqueId}).", false)
            logger.warning("Something went wrong while trying to fetch player data for player ${player.name} (${player.uniqueId}).")
            e.printStackTrace()
        }
    }

    /** Saves all player data from their object, ideally called as little as possible to minimise IO load **/
    fun savePlayerData(player: SGPlayer) {
        val config = playerData[player.uuid] ?: return
        config.set("${player.uuid}${SGStatistics.RANK.statistic.statisticConfigPath}", player.rank.toString())
        config.set("${player.uuid}${SGStatistics.LEVEL.statistic.statisticConfigPath}", player.level.toString())
        config.set("${player.uuid}${SGStatistics.EXPERIENCE.statistic.statisticConfigPath}", player.exp)
        config.set("${player.uuid}${SGStatistics.ELIMINATIONS.statistic.statisticConfigPath}", player.eliminations)
        config.set("${player.uuid}${SGStatistics.WINS.statistic.statisticConfigPath}", player.wins)
        config.set("${player.uuid}${SGStatistics.GAMES_PLAYED.statistic.statisticConfigPath}", player.matchesPlayed)
        ioCoroutineScope.launch {
            config.save(File("${plugin.dataFolder}/players","${player.uuid}.yml"))
        }.invokeOnCompletion { logger.info("Player data saved for player ${player.playerName} (${player.uuid})") }
    }

    fun getPlayerConfiguration(player: Player): FileConfiguration {
        return playerData[player.uniqueId]!!
    }
}