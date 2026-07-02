package dev.byrt.survivalgames.player.data

import dev.byrt.survivalgames.ioCoroutineScope
import dev.byrt.survivalgames.lobby.leaderboard.SGLeaderboards
import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.SGPlayer
import dev.byrt.survivalgames.player.progression.SGLevel
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.ChatUtility
import dev.byrt.survivalgames.text.SG_FONT_TAG
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID
import kotlin.toString

object SGPlayerData {
    private val playerData = mutableMapOf<UUID, FileConfiguration>()

    /** Fetches player data on join and stores locally to their SGPlayer object **/
    fun getPlayerData(player: SGPlayer) {
        try {
            val folder = File("${plugin.dataFolder}/players")
            if(!folder.exists()) folder.mkdirs()
            val playerFile = File(folder,"${player.uuid}.yml")
            if(!playerFile.exists()) playerFile.createNewFile()
            val fileConfiguration = YamlConfiguration.loadConfiguration(playerFile)
            // Set all if null
            for(stat in SGStatistics.entries) {
                if(fileConfiguration.get("${player.uuid}${stat.statistic.statisticConfigPath}") == null) {
                    fileConfiguration.set("${player.uuid}${stat.statistic.statisticConfigPath}",
                        when(stat) { // Treat nonintegers as 'edge cases' as they have different properties
                            SGStatistics.RANK -> Rank.RECRUIT.name
                            SGStatistics.LEVEL -> SGLevel.LEVEL_1.name
                            else -> 0
                        }
                    )
                }
            }
            // Locally set data from config, save and store file configuration reference
            player.rank = Rank.valueOf(fileConfiguration.getString("${player.uuid}${SGStatistics.RANK.statistic.statisticConfigPath}") ?: Rank.RECRUIT.name)
            player.level = SGLevel.valueOf(fileConfiguration.getString("${player.uuid}${SGStatistics.LEVEL.statistic.statisticConfigPath}") ?: SGLevel.LEVEL_1.name)
            player.exp = fileConfiguration.getInt("${player.uuid}${SGStatistics.EXPERIENCE.statistic.statisticConfigPath}")
            player.eliminations = fileConfiguration.getInt("${player.uuid}${SGStatistics.ELIMINATIONS.statistic.statisticConfigPath}")
            player.wins = fileConfiguration.getInt("${player.uuid}${SGStatistics.WINS.statistic.statisticConfigPath}")
            player.matchesPlayed = fileConfiguration.getInt("${player.uuid}${SGStatistics.GAMES_PLAYED.statistic.statisticConfigPath}")

            fileConfiguration.save(playerFile)
            if(playerData.containsKey(player.uuid)) playerData.remove(player.uuid)
            playerData[player.uuid] = fileConfiguration
            logger.info("Player data fetched for player ${player.playerName} (${player.uuid})")
        } catch(e: Exception) {
            ChatUtility.broadcastDev("$SG_FONT_TAG<#ff3333>Something went wrong while trying to fetch player data for player ${player.playerName} (${player.uuid}).", false)
            logger.warning("Something went wrong while trying to fetch player data for player ${player.playerName} (${player.uuid}).")
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

    fun getLeaderboardData() {
        val folder = File("${plugin.dataFolder}/players")
        if(!folder.exists()) return
        // Lookup online players
        val onlineUuids = mutableListOf<UUID>()
        Bukkit.getOnlinePlayers().forEach { player -> onlineUuids.add(player.uniqueId) }

        ioCoroutineScope.launch {
            for(playerFile in folder.listFiles()) {
                // Use online player data before fetching from config as local data is newer than config data; config data is only saved on quit
                if(onlineUuids.contains(UUID.fromString(playerFile.nameWithoutExtension))) {
                    val uuid = UUID.fromString(playerFile.nameWithoutExtension)
                    val sgPlayer = uuid.sgPlayer()
                    try {
                        SGLeaderboards.leaderboardWinsData[uuid] = sgPlayer.wins
                        SGLeaderboards.leaderboardEliminationsData[uuid] = sgPlayer.eliminations
                        SGLeaderboards.leaderboardMatchesPlayedData[uuid] = sgPlayer.matchesPlayed
                    } catch (e: Exception) {
                        logger.warning("[Leaderboard Data] Failed to fetch online player data for player ${sgPlayer.playerName} (${sgPlayer.uuid})")
                        e.printStackTrace()
                    }
                } else {
                    val config = YamlConfiguration.loadConfiguration(playerFile)
                    SGLeaderboards.leaderboardWinsData[UUID.fromString(playerFile.nameWithoutExtension)] = config.getInt("${UUID.fromString(playerFile.nameWithoutExtension)}${SGStatistics.WINS.statistic.statisticConfigPath}")
                    SGLeaderboards.leaderboardEliminationsData[UUID.fromString(playerFile.nameWithoutExtension)] = config.getInt("${UUID.fromString(playerFile.nameWithoutExtension)}${SGStatistics.ELIMINATIONS.statistic.statisticConfigPath}")
                    SGLeaderboards.leaderboardMatchesPlayedData[UUID.fromString(playerFile.nameWithoutExtension)] = config.getInt("${UUID.fromString(playerFile.nameWithoutExtension)}${SGStatistics.GAMES_PLAYED.statistic.statisticConfigPath}")
                }
            }
            SGLeaderboards.leaderboardWinsData = SGLeaderboards.leaderboardWinsData.toList().sortedBy { (_, value) -> value }.toMap().toMutableMap()
            SGLeaderboards.leaderboardEliminationsData = SGLeaderboards.leaderboardEliminationsData.toList().sortedBy { (_, value) -> value }.toMap().toMutableMap()
            SGLeaderboards.leaderboardMatchesPlayedData = SGLeaderboards.leaderboardMatchesPlayedData.toList().sortedBy { (_, value) -> value }.toMap().toMutableMap()
        }.invokeOnCompletion {
            logger.info("[Leaderboard Data] Data finished populating for all leaderboards. ")
            Bukkit.getScheduler().runTask(plugin, Runnable {
                if(SGLeaderboards.leaderboards.isEmpty()) {
                    SGLeaderboards.spawnAllLeaderboards()
                }
            })
        }
    }

    fun getPlayerConfiguration(player: Player): FileConfiguration {
        return playerData[player.uniqueId]!!
    }
}