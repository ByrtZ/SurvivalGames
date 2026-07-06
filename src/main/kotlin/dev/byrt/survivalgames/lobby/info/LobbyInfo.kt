package dev.byrt.survivalgames.lobby.info

import dev.byrt.survivalgames.game.GameManager
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import io.papermc.paper.scoreboard.numbers.NumberFormat
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import java.util.UUID

@Suppress("DEPRECATION")
object LobbyInfo {
    val lobbyScoreboard = Bukkit.getScoreboardManager().newScoreboard
    private var lobbyObjective = lobbyScoreboard.registerNewObjective(
        "${plugin.name.lowercase()}-lobby-info-${UUID.randomUUID()}",
        Criteria.DUMMY,
        Formatting.allTags.deserialize("  <playercolour><b>${SG_FONT_TAG}mc.byrt.dev<reset>  ")
    )
    private var lobbyPlayersLine = lobbyScoreboard.registerNewTeam("lobbyPlayersLine")
    private val lobbyPlayersLineKey = ChatColor.GRAY.toString()
    private var lobbyPlayerCountLine = lobbyScoreboard.registerNewTeam("lobbyPlayerCountLine")
    private val lobbyPlayerCountLineKey = ChatColor.DARK_GRAY.toString()
    private var lobbyMatchesLine = lobbyScoreboard.registerNewTeam("lobbyMatchesLine")
    private val lobbyMatchesLineKey = ChatColor.LIGHT_PURPLE.toString()
    private var lobbyMatchesCountLine = lobbyScoreboard.registerNewTeam("lobbyMatchesCountLine")
    private val lobbyMatchesCountLineKey = ChatColor.DARK_PURPLE.toString()
    private var lobbyInstanceLine = lobbyScoreboard.registerNewTeam("lobbyInstanceLine")
    private val lobbyInstanceLineKey = ChatColor.DARK_GREEN.toString()
    private var lobbyInstanceNameLine = lobbyScoreboard.registerNewTeam("lobbyInstanceNameLine")
    private val lobbyInstanceNameLineKey = ChatColor.DARK_BLUE.toString()
    private var lobbyVersionLine = lobbyScoreboard.registerNewTeam("lobbyVersionLine")
    private val lobbyVersionLineKey = ChatColor.STRIKETHROUGH.toString()
    private var lobbyVersionNumberLine = lobbyScoreboard.registerNewTeam("lobbyVersionNumberLine")
    private val lobbyVersionNumberLineKey = ChatColor.UNDERLINE.toString()

    fun build() {
        plugin.logger.info("Building lobby scoreboard")
        lobbyObjective.numberFormat(NumberFormat.blank())
        lobbyObjective.displaySlot = DisplaySlot.SIDEBAR
        lobbyPlayersLine.addEntry(lobbyPlayersLineKey)
        lobbyPlayersLine.prefix(Formatting.allTags.deserialize("<b><yellow>${SG_FONT_TAG}Players<reset>"))
        lobbyObjective.getScore(lobbyPlayersLineKey).score = 10
        lobbyPlayerCountLine.addEntry(lobbyPlayerCountLineKey)
        lobbyPlayerCountLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}${Bukkit.getOnlinePlayers().size}"))
        lobbyObjective.getScore(lobbyPlayerCountLineKey).score = 9
        lobbyObjective.getScore("§").score = 8
        lobbyMatchesLine.addEntry(lobbyMatchesLineKey)
        lobbyMatchesLine.prefix(Formatting.allTags.deserialize("<b><gold>${SG_FONT_TAG}Matches<reset>"))
        lobbyObjective.getScore(lobbyMatchesLineKey).score = 7
        lobbyMatchesCountLine.addEntry(lobbyMatchesCountLineKey)
        lobbyMatchesCountLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}0"))
        lobbyObjective.getScore(lobbyMatchesCountLineKey).score = 6
        lobbyObjective.getScore("§§").score = 5
        lobbyInstanceLine.addEntry(lobbyInstanceLineKey)
        lobbyInstanceLine.prefix(Formatting.allTags.deserialize("<b><aqua>${SG_FONT_TAG}Instance<reset>"))
        lobbyObjective.getScore(lobbyInstanceLineKey).score = 4
        lobbyInstanceNameLine.addEntry(lobbyInstanceNameLineKey)
        lobbyInstanceNameLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}Lobby"))
        lobbyObjective.getScore(lobbyInstanceNameLineKey).score = 3
        lobbyObjective.getScore("§§§").score = 2
        lobbyVersionLine.addEntry(lobbyVersionLineKey)
        lobbyVersionLine.prefix(Formatting.allTags.deserialize("<b><green>${SG_FONT_TAG}Version<reset>"))
        lobbyObjective.getScore(lobbyVersionLineKey).score = 1
        lobbyVersionNumberLine.addEntry(lobbyVersionNumberLineKey)
        lobbyVersionNumberLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}${Bukkit.getServer().minecraftVersion}"))
        lobbyObjective.getScore(lobbyVersionNumberLineKey).score = 0
        plugin.logger.info("Scoreboard constructed with ID ${lobbyObjective.name}")
    }

    fun updateTotalPlayers() {
        lobbyPlayerCountLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}${Bukkit.getOnlinePlayers().filter { player -> player.isOnline }.size}"))
    }

    fun updateMatches() {
        lobbyMatchesCountLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}${GameManager.gameContainers.size}"))
    }

    fun destroy() {
        plugin.logger.info("Destroying lobby scoreboard")
        lobbyObjective.unregister()
    }
}