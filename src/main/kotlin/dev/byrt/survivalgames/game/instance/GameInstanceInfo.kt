package dev.byrt.survivalgames.game.instance

import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.ChatUtility
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.TextAlignment
import io.papermc.paper.scoreboard.numbers.NumberFormat
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.bossbar.BossBar.Color
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import java.util.*

class GameInstanceInfo(val instance: GameInstance) {
    public val scoreboard = Bukkit.getScoreboardManager().newScoreboard
    private var objective = scoreboard.registerNewObjective(
        "${plugin.name.lowercase()}-info-${instance.gameInstanceId}",
        Criteria.DUMMY,
        Formatting.allTags.deserialize("<gold><bold>${ChatUtility.SG_FONT_TAG}FROOMIN IT<reset>")
    )

    private var currentGameLine = scoreboard.registerNewTeam("currentGameLine")
    private val currentGameLineKey = ChatColor.STRIKETHROUGH.toString()

    private var currentMapLine = scoreboard.registerNewTeam("currentMapLine")
    private val currentMapLineKey = ChatColor.BOLD.toString()

    private var currentScoreLine = scoreboard.registerNewTeam("currentScoreLine")
    private val currentScoreLineKey = ChatColor.DARK_PURPLE.toString()

    private var currentRoundLine = scoreboard.registerNewTeam("currentRoundLine")
    private val currentRoundLineKey = ChatColor.ITALIC.toString()

    private var gameStatusLine = scoreboard.registerNewTeam("gameStatusLine")
    private val gameStatusLineKey = ChatColor.MAGIC.toString()

    private var firstPlaceLine = scoreboard.registerNewTeam("firstPlaceLine")
    private val firstPlaceLineKey = ChatColor.UNDERLINE.toString()

    private var secondPlaceLine = scoreboard.registerNewTeam("secondPlaceLine")
    private val secondPlaceLineKey = ChatColor.LIGHT_PURPLE.toString()

    private var thirdPlaceLine = scoreboard.registerNewTeam("thirdPlaceLine")
    private val thirdPlaceLineKey = ChatColor.BLACK.toString()

    fun buildScoreboard() {
        plugin.logger.info("Building scoreboard...")
        objective.displaySlot = DisplaySlot.SIDEBAR
        objective.numberFormat(NumberFormat.blank())

        // Modifiable game text
        currentGameLine.addEntry(currentGameLineKey)
        currentGameLine.prefix(Formatting.allTags.deserialize("<aqua>${ChatUtility.SG_FONT_TAG}GAME:<reset> "))
        currentGameLine.suffix(Formatting.allTags.deserialize("${ChatUtility.SG_FONT_TAG}Survival Games"))
        objective.getScore(currentGameLineKey).score = 10

        // Modifiable map text
        currentMapLine.addEntry(currentMapLineKey)
        currentMapLine.prefix(Formatting.allTags.deserialize("<aqua>${ChatUtility.SG_FONT_TAG}MAP:<reset> "))
        currentMapLine.suffix(Formatting.allTags.deserialize("${ChatUtility.SG_FONT_TAG}Auburn Forest"))
        objective.getScore(currentMapLineKey).score = 9

        // Modifiable round information
        currentRoundLine.addEntry(currentRoundLineKey)
        currentRoundLine.prefix(Formatting.allTags.deserialize("<green>${ChatUtility.SG_FONT_TAG}ROUND:<reset> "))
        currentRoundLine.suffix(Formatting.allTags.deserialize("${ChatUtility.SG_FONT_TAG}NONE"))
        objective.getScore(currentRoundLineKey).score = 8

        // Modifiable game status information
        gameStatusLine.addEntry(gameStatusLineKey)
        gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.SG_FONT_TAG}GAME STATUS:<reset> "))
        gameStatusLine.suffix(Formatting.allTags.deserialize("<gray>${ChatUtility.SG_FONT_TAG}AWAITING PLAYERS..."))
        objective.getScore(gameStatusLineKey).score = 7

        // Static blank space
        objective.getScore("§").score = 6

        // Static score multiplier
        currentScoreLine.addEntry(currentScoreLineKey)
        currentScoreLine.prefix(Formatting.allTags.deserialize("<aqua>${ChatUtility.SG_FONT_TAG}GAME STANDINGS:"))
        currentScoreLine.suffix(Formatting.allTags.deserialize(""))
        objective.getScore(currentScoreLineKey).score = 5

        // Modifiable first placement score
        firstPlaceLine.addEntry(firstPlaceLineKey)
        firstPlaceLine.prefix(Formatting.allTags.deserialize("${ChatUtility.SG_FONT_TAG}<dark_gray>-<reset> "))
        firstPlaceLine.suffix(Formatting.allTags.deserialize("${ChatUtility.SG_FONT_TAG}TODO"))
        objective.getScore(firstPlaceLineKey).score = 4

        // Modifiable second placement score
        secondPlaceLine.addEntry(secondPlaceLineKey)
        secondPlaceLine.prefix(Formatting.allTags.deserialize("${ChatUtility.SG_FONT_TAG}<dark_gray>-<reset> "))
        secondPlaceLine.suffix(Formatting.allTags.deserialize("${ChatUtility.SG_FONT_TAG}TODO"))
        objective.getScore(secondPlaceLineKey).score = 3

        // Modifiable third placement score
        thirdPlaceLine.addEntry(thirdPlaceLineKey)
        thirdPlaceLine.prefix(Formatting.allTags.deserialize("${ChatUtility.SG_FONT_TAG}<dark_gray>-<reset> "))
        thirdPlaceLine.suffix(Formatting.allTags.deserialize("${ChatUtility.SG_FONT_TAG}TODO"))
        objective.getScore(thirdPlaceLineKey).score = 2

        // Static blank space
        objective.getScore("§§").score = 0

        plugin.logger.info("Scoreboard constructed with ID ${objective.name}.")
    }

    fun updateRound() {
        if (instance.manager.getGameState() == GameState.IDLE) {
            currentRoundLine.suffix(Formatting.allTags.deserialize("${ChatUtility.SG_FONT_TAG}NONE"))
        } else {
            currentRoundLine.suffix(Formatting.allTags.deserialize("${ChatUtility.SG_FONT_TAG}${instance.rounds}/${instance.rounds.getTotalRounds()}"))
        }
    }

    fun updateStatus() {
        when (instance.manager.getGameState()) {
            GameState.IDLE -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.SG_FONT_TAG}GAME STATUS:<reset> "))
                gameStatusLine.suffix(Formatting.allTags.deserialize("<gray>${ChatUtility.SG_FONT_TAG}AWAITING<reset> ${ChatUtility.SG_FONT_TAG}<gray>PLAYERS..."))
                objective.displaySlot = null
            }

            GameState.STARTING -> {
                objective.displaySlot = DisplaySlot.SIDEBAR
                if (instance.rounds.getRound() == 1) {
                    gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.SG_FONT_TAG}GAME BEGINS:<reset> "))
                } else {
                    gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.SG_FONT_TAG}ROUND BEGINS:<reset> "))
                }
            }

            GameState.IN_GAME -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.SG_FONT_TAG}TIME LEFT:<reset> "))
            }

            GameState.ROUND_END -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.SG_FONT_TAG}NEXT ROUND:<reset> "))
            }

            GameState.GAME_END -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.SG_FONT_TAG}GAME ENDING:<reset> "))
            }

            GameState.OVERTIME -> {
                gameStatusLine.prefix(Formatting.allTags.deserialize("<red>${ChatUtility.SG_FONT_TAG}OVERTIME<reset> "))
            }
        }
    }

    fun updateTimer() {
        if (instance.manager.getGameState() == GameState.OVERTIME) {
            gameStatusLine.suffix(Formatting.allTags.deserialize(""))
        } else if (instance.manager.getGameState() == GameState.IDLE) {
            gameStatusLine.suffix(Formatting.allTags.deserialize("<gray>${ChatUtility.SG_FONT_TAG}AWAITING PLAYERS..."))
        } else {
            gameStatusLine.suffix(Formatting.allTags.deserialize("${ChatUtility.SG_FONT_TAG}${instance.timer.getDisplayTimer()}"))
        }
    }

    fun timerBossBar() {
        object : BukkitRunnable() {
            val timerBossBar =
                BossBar.bossBar(Formatting.allTags.deserialize(""), 0f, Color.RED, BossBar.Overlay.PROGRESS)

            override fun run() {
                if (instance.manager.getGameState() != GameState.IDLE) {
                    for (player in Bukkit.getOnlinePlayers()) {
                        if (!player.activeBossBars().contains(timerBossBar)) timerBossBar.addViewer(player)
                    }
                }
                when(instance.timer.getTimerState()) {
                    GameTimerState.ACTIVE -> {
                        when (instance.manager.getGameState()) {
                            GameState.IDLE -> {
                                for (player in Bukkit.getOnlinePlayers()) {
                                    timerBossBar.removeViewer(player)
                                }
                                this.cancel()
                            }

                            GameState.STARTING -> {
                                timerBossBar.name(TextAlignment.centreBossBarText("GAME STARTING IN: ${if (instance.timer.getTimer() <= 9) "<red>" else "<#ffff00>"}${instance.timer.getDisplayTimer()}"))
                            }

                            GameState.IN_GAME -> {
                                timerBossBar.name(TextAlignment.centreBossBarText("TIME LEFT: ${if (instance.timer.getTimer() <= 89) "<red>" else "<#ffff00>"}${instance.timer.getDisplayTimer()}"))
                            }

                            GameState.ROUND_END -> {
                                timerBossBar.name(TextAlignment.centreBossBarText("NEXT ROUND IN: <#ffff00>${instance.timer.getDisplayTimer()}"))
                            }

                            GameState.GAME_END -> {
                                timerBossBar.name(TextAlignment.centreBossBarText("BACK TO HUB: <#ffff00>${instance.timer.getDisplayTimer()}"))
                            }

                            GameState.OVERTIME -> {
                                timerBossBar.name(TextAlignment.centreBossBarText("<red>OVERTIME"))
                            }
                        }
                    }

                    GameTimerState.INACTIVE -> {
                        timerBossBar.name(TextAlignment.centreBossBarText("<red>TIMER UNAVAILABLE"))
                    }

                    GameTimerState.PAUSED -> {
                        timerBossBar.name(TextAlignment.centreBossBarText("TIMER PAUSED"))
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    fun destroyScoreboard() {
        currentGameLine.unregister()
        currentMapLine.unregister()
        currentScoreLine.unregister()
        currentRoundLine.unregister()
        gameStatusLine.unregister()
        firstPlaceLine.unregister()
        secondPlaceLine.unregister()
        objective.unregister()
    }
}