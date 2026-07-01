package dev.byrt.survivalgames.player.data

import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.progression.SGLevel
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.ChatUtility
import dev.byrt.survivalgames.text.SG_FONT_TAG
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID

object SGPlayerData {
    const val EXPERIENCE_PATH = ".experience"
    const val LEVEL_PATH = ".level"
    const val RANK_PATH = ".rank"
    private val playerData = mutableMapOf<UUID, FileConfiguration>()

    fun getPlayerData(player: Player) {
        try {
            val folder = File("${plugin.dataFolder}/players")
            if(!folder.exists()) folder.mkdirs()
            val playerFile = File(folder,"${player.uniqueId}.yml")
            if(!playerFile.exists()) playerFile.createNewFile()
            val fileConfiguration = YamlConfiguration.loadConfiguration(playerFile)
            if(fileConfiguration.get("${player.uniqueId}${EXPERIENCE_PATH}") == null) {
                fileConfiguration.set("${player.uniqueId}${EXPERIENCE_PATH}", 0)
            }
            if(fileConfiguration.get("${player.uniqueId}${LEVEL_PATH}") == null) {
                fileConfiguration.set("${player.uniqueId}${LEVEL_PATH}", SGLevel.LEVEL_1.name)
            }
            if(fileConfiguration.get("${player.uniqueId}${RANK_PATH}") == null) {
                fileConfiguration.set("${player.uniqueId}${RANK_PATH}", Rank.RECRUIT.toString())
            }
            val level = SGLevel.valueOf(fileConfiguration.get("${player.uniqueId}${LEVEL_PATH}").toString()).ordinal + 1
            val exp = fileConfiguration.get("${player.uniqueId}${EXPERIENCE_PATH}") as Int / SGLevel.valueOf(fileConfiguration.get("${player.uniqueId}${LEVEL_PATH}").toString()).requiredXp.toFloat()
            val rank = Rank.valueOf(fileConfiguration.get("${player.uniqueId}${RANK_PATH}").toString())
            player.level = level
            player.exp = exp
            player.sgPlayer().rank = rank
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

    fun getPlayerConfiguration(player: Player): FileConfiguration {
        return playerData[player.uniqueId]!!
    }
}