package dev.byrt.survivalgames.lobby.leaderboard

import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.player.data.SGPlayerData
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import org.bukkit.Bukkit
import org.bukkit.entity.Display
import org.bukkit.entity.TextDisplay
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

object SGLeaderboards {
    var leaderboardEliminationsData = mutableMapOf<UUID, Int>()
    var leaderboardWinsData = mutableMapOf<UUID, Int>()
    var leaderboardMatchesPlayedData = mutableMapOf<UUID, Int>()
    private var leaderboardPage = mutableMapOf<UUID, Int>()
    var leaderboards = mutableMapOf<TextDisplay, SGLeaderboard>()

    fun spawnAllLeaderboards() {
        SGLeaderboard.entries.forEach { leaderboard -> spawnLeaderboard(leaderboard, leaderboard.refreshInMins) }

        object: BukkitRunnable() {
            override fun run() {
                if(plugin.server.isStopping) cancel()
                logger.info("[Leaderboard Data] Refreshing all leaderboard data")
                SGPlayerData.getLeaderboardData()
            }
        }.runTaskTimer(plugin, 20L * 895L, 20L * 895L)
    }

    fun spawnLeaderboard(leaderboard: SGLeaderboard, refreshTime: Int) {
        leaderboard.location.chunk.isForceLoaded = true
        val leaderboardDisplay = leaderboard.location.world.spawn(leaderboard.location, TextDisplay::class.java)
        leaderboardDisplay.isShadowed = true
        leaderboardDisplay.billboard = Display.Billboard.FIXED
        leaderboardDisplay.brightness = Display.Brightness(15, 15)
        leaderboards[leaderboardDisplay] = leaderboard

        var refreshInMins = refreshTime
        updateLeaderboard(leaderboardDisplay, leaderboard, refreshInMins)

        // Decrement refresh timer
        object: BukkitRunnable() {
            override fun run() {
                if(!leaderboardDisplay.isValid) cancel()
                refreshInMins--
                if(refreshInMins <= 0) refreshInMins = refreshTime
            }
        }.runTaskTimer(plugin, 20L * 60L, 20L * 60L)

        // Turn page
        object: BukkitRunnable() {
            override fun run() {
                if(!leaderboardDisplay.isValid) cancel()
                val page = leaderboardPage.getOrDefault(leaderboardDisplay.uniqueId, 0) + 1
                leaderboardPage[leaderboardDisplay.uniqueId] = page
                updateLeaderboard(leaderboardDisplay, leaderboard, refreshInMins)
            }
        }.runTaskTimer(plugin, 20L * 10L, 20L * 10L)
    }

    private fun updateLeaderboard(display: TextDisplay, leaderboard: SGLeaderboard, refreshInMins: Int) {
        val data = when(leaderboard) {
            SGLeaderboard.ELIMINATIONS -> leaderboardEliminationsData
            SGLeaderboard.WINS -> leaderboardWinsData
            SGLeaderboard.MATCHES_PLAYED -> leaderboardMatchesPlayedData
        }

        val sorted = data.entries.sortedByDescending { it.value }
        val totalPages = maxOf(1, (sorted.size + 9) / 10)
        val currentPage = leaderboardPage.getOrDefault(display.uniqueId, 0) % totalPages
        leaderboardPage[display.uniqueId] = currentPage
        val start = currentPage * 10

        val text = buildString {
            append("<newline>")
            append("$SG_FONT_TAG<bold><playercolour>${leaderboard.leaderboardName}</playercolour></bold><newline>")
            for (i in 0 until 10) {
                val index = start + i
                if (index < sorted.size) {
                    val entry = sorted[index]
                    val player = Bukkit.getOfflinePlayer(entry.key).name ?: "Unknown"
                    append("$SG_FONT_TAG${index + 1}. <playercolour>$player</playercolour> - ${entry.value}")
                } else {
                    append("$SG_FONT_TAG-")
                }
                if (i != 9) append("<newline>")
            }
            append("<newline>${SG_FONT_TAG}Page ${currentPage + 1}/$totalPages")
            append("<newline>${SG_FONT_TAG}<yellow>Leaderboard update ${if (refreshInMins >= 1) "in ${refreshInMins}m" else "shortly!"}</yellow>")
            append("<newline>")
        }
        display.text(Formatting.allTags.deserialize(text))
        display.teleport(display.location)
    }

    fun destroyLeaderboards() {
        leaderboards.forEach { leaderboard -> leaderboard.key.remove() }
        leaderboards.clear()
    }
}