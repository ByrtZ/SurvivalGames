package dev.byrt.survivalgames.game.instance

import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.PlayerType
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import dev.byrt.survivalgames.text.TextAlignment
import io.papermc.paper.scoreboard.numbers.NumberFormat
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.bossbar.BossBar.Color
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot

class GameInstanceInfo(val instance: GameInstance) {
    val preGameScoreboard = Bukkit.getScoreboardManager().newScoreboard
    private var preGameObjective = preGameScoreboard.registerNewObjective(
        "${plugin.name.lowercase()}-pregame-info-${instance.gameInstanceId}",
        Criteria.DUMMY,
        Formatting.allTags.deserialize("<green><bold>${SG_FONT_TAG}Awaiting players...<reset>")
    )
    private var preGamePlayersLine = preGameScoreboard.registerNewTeam("preGamePlayersLine")
    private val preGamePlayersLineKey = ChatColor.STRIKETHROUGH.toString()
    private var preGamePlayerCountLine = preGameScoreboard.registerNewTeam("preGamePlayerCountLine")
    private val preGamePlayerCountLineKey = ChatColor.MAGIC.toString()
    private var preGameGameLine = preGameScoreboard.registerNewTeam("preGameGameLine")
    private val preGameGameLineKey = ChatColor.UNDERLINE.toString()
    private var preGameGameNameLine = preGameScoreboard.registerNewTeam("preGameGameNameLine")
    private val preGameGameNameLineKey = ChatColor.BOLD.toString()
    private var preGameMapLine = preGameScoreboard.registerNewTeam("preGameMapLine")
    private val preGameMapLineKey = ChatColor.GRAY.toString()
    private var preGameMapNameLine = preGameScoreboard.registerNewTeam("preGameMapNameLine")
    private val preGameMapNameLineKey = ChatColor.DARK_GRAY.toString()
    private var preGameInstanceLine = preGameScoreboard.registerNewTeam("preGameInstanceLine")
    private val preGameInstanceLineKey = ChatColor.LIGHT_PURPLE.toString()
    private var preGameInstanceNameLine = preGameScoreboard.registerNewTeam("preGameInstanceNameLine")
    private val preGameInstanceNameLineKey = ChatColor.DARK_PURPLE.toString()
    private var preGameServerIpLine = preGameScoreboard.registerNewTeam("preGameServerIpLine")
    private val preGameServerIpLineKey = ChatColor.DARK_GREEN.toString()

    fun buildPreGameBoard() {
        plugin.logger.info("Building pre-game scoreboard...")
        preGameObjective.displaySlot = DisplaySlot.SIDEBAR
        preGameObjective.numberFormat(NumberFormat.blank())
        preGamePlayersLine.addEntry(preGamePlayersLineKey)
        preGamePlayersLine.prefix(Formatting.allTags.deserialize("<b><yellow>${SG_FONT_TAG}Players<reset>"))
        preGameObjective.getScore(preGamePlayersLineKey).score = 12
        preGamePlayerCountLine.addEntry(preGamePlayerCountLineKey)
        preGamePlayerCountLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}0/0"))
        preGameObjective.getScore(preGamePlayerCountLineKey).score = 11
        preGameObjective.getScore("§").score = 10
        preGameGameLine.addEntry(preGameGameLineKey)
        preGameGameLine.prefix(Formatting.allTags.deserialize("<b><gold>${SG_FONT_TAG}Game<reset>"))
        preGameObjective.getScore(preGameGameLineKey).score = 9
        preGameGameNameLine.addEntry(preGameGameNameLineKey)
        preGameGameNameLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}Survival Games"))
        preGameObjective.getScore(preGameGameNameLineKey).score = 8
        preGameObjective.getScore("§§").score = 7
        preGameMapLine.addEntry(preGameMapLineKey)
        preGameMapLine.prefix(Formatting.allTags.deserialize("<b><red>${SG_FONT_TAG}Map<reset>"))
        preGameObjective.getScore(preGameMapLineKey).score = 6
        preGameMapNameLine.addEntry(preGameMapNameLineKey)
        preGameMapNameLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}${instance.manager.map.mapName}"))
        preGameObjective.getScore(preGameMapNameLineKey).score = 5
        preGameObjective.getScore("§§§").score = 4
        preGameInstanceLine.addEntry(preGameInstanceLineKey)
        preGameInstanceLine.prefix(Formatting.allTags.deserialize("<b><aqua>${SG_FONT_TAG}Instance<reset>"))
        preGameObjective.getScore(preGameInstanceLineKey).score = 3
        preGameInstanceNameLine.addEntry(preGameInstanceNameLineKey)
        preGameInstanceNameLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}${instance.gameInstanceId.toString().toCharArray(0, 8).joinToString("")}"))
        preGameObjective.getScore(preGameInstanceNameLineKey).score = 2
        preGameObjective.getScore("§§§§").score = 1
        preGameServerIpLine.addEntry(preGameServerIpLineKey)
        preGameServerIpLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}<gold>mc.byrt.dev</gold> <dark_gray>(${plugin.server.minecraftVersion})<reset>"))
        preGameObjective.getScore(preGameServerIpLineKey).score = 0
        plugin.logger.info("Scoreboard constructed with ID ${preGameObjective.name}.")
    }

    //TODO pre game scoreboard update player count line (link to queue), update map name line

    val gameScoreboard = Bukkit.getScoreboardManager().newScoreboard
    private var gameObjective = gameScoreboard.registerNewObjective(
        "${plugin.name.lowercase()}-game-info-${instance.gameInstanceId}",
        Criteria.DUMMY,
        Formatting.allTags.deserialize("<gold><bold>${SG_FONT_TAG}Survival Games<reset>")
    )
    private var gameTimeLine = gameScoreboard.registerNewTeam("gameTimeLine")
    private val gameTimeLineKey = ChatColor.GRAY.toString()
    private var gameTimeRemainingLine = gameScoreboard.registerNewTeam("gameTimeRemainingLine")
    private val gameTimeRemainingLineKey = ChatColor.DARK_GRAY.toString()
    private var gamePlayersLine = gameScoreboard.registerNewTeam("gamePlayersLine")
    private val gamePlayersLineKey = ChatColor.LIGHT_PURPLE.toString()
    private var gamePlayerCountLine = gameScoreboard.registerNewTeam("gamePlayerCountLine")
    private val gamePlayerCountLineKey = ChatColor.DARK_PURPLE.toString()
    private var gameServerIpLine = gameScoreboard.registerNewTeam("gameServerIpLine")
    private val gameServerIpLineKey = ChatColor.DARK_GREEN.toString()

    fun buildGameBoard() {
        plugin.logger.info("Building game scoreboard...")
        gameObjective.numberFormat(NumberFormat.blank())
        gameTimeLine.addEntry(gameTimeLineKey)
        gameTimeLine.prefix(Formatting.allTags.deserialize("<b><dark_gray>${SG_FONT_TAG}Game status<reset>"))
        gameObjective.getScore(gameTimeLineKey).score = 6
        gameTimeRemainingLine.addEntry(gameTimeRemainingLineKey)
        gameTimeRemainingLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}Inactive"))
        gameObjective.getScore(gameTimeRemainingLineKey).score = 5
        gameObjective.getScore("§§§").score = 4
        gamePlayersLine.addEntry(gamePlayersLineKey)
        gamePlayersLine.prefix(Formatting.allTags.deserialize("<b><green>${SG_FONT_TAG}Players remaining<reset>"))
        gameObjective.getScore(gamePlayersLineKey).score = 3
        gamePlayerCountLine.addEntry(gamePlayerCountLineKey)
        gamePlayerCountLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}0/0"))
        gameObjective.getScore(gamePlayerCountLineKey).score = 2
        gameObjective.getScore("§§§§").score = 1
        gameServerIpLine.addEntry(gameServerIpLineKey)
        gameServerIpLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}<gold>mc.byrt.dev</gold> <dark_gray>(${plugin.server.minecraftVersion})<reset>"))
        gameObjective.getScore(gameServerIpLineKey).score = 0
        plugin.logger.info("Scoreboard constructed with ID ${gameObjective.name}.")
    }

    fun updatePreGameMap() {
        if(instance.manager.getGameState() == GameState.IDLE) {
            preGameMapNameLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}${instance.manager.map.mapName}"))
        } else return
    }

    fun updatePreGamePlayersRequired() {
        if(instance.manager.getGameState() == GameState.IDLE) {
            preGamePlayerCountLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}${instance.currentContainer?.players?.filter { player -> player.sgPlayer().playerType == PlayerType.PARTICIPANT && !player.sgPlayer().isDead }?.size}/${GamePlayerCount.MAX_PLAYERS}"))
        } else return
    }

    fun updateGamePlayersRemaining() {
        if(instance.manager.getGameState() in listOf(GameState.STARTING, GameState.IN_GAME, GameState.OVERTIME)) {
            gamePlayerCountLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}${instance.currentContainer?.players?.filter { player -> player.sgPlayer().playerType == PlayerType.PARTICIPANT && !player.sgPlayer().isDead }?.size}/${GamePlayerCount.MAX_PLAYERS}"))
        } else return
    }

    fun updateGameStatus() {
        when (instance.manager.getGameState()) {
            GameState.IDLE -> {
                gameTimeLine.prefix(Formatting.allTags.deserialize("<dark_gray>${SG_FONT_TAG}Game idle<reset> "))
                gameObjective.displaySlot = null
                preGameObjective.displaySlot = null
            }

            GameState.STARTING -> {
                preGameObjective.displaySlot = null
                gameObjective.displaySlot = DisplaySlot.SIDEBAR
                if (instance.rounds.getRound() == 1) {
                    gameTimeLine.prefix(Formatting.allTags.deserialize("<b><red>${SG_FONT_TAG}Game begins<reset> "))
                } else {
                    gameTimeLine.prefix(Formatting.allTags.deserialize("<b><red>${SG_FONT_TAG}Round begins<reset> "))
                }
            }

            GameState.IN_GAME -> {
                gameTimeLine.prefix(Formatting.allTags.deserialize("<b><red>${SG_FONT_TAG}Time left<reset> "))
            }

            GameState.ROUND_END -> {
                gameTimeLine.prefix(Formatting.allTags.deserialize("<b><red>${SG_FONT_TAG}Next round<reset> "))
            }

            GameState.GAME_END -> {
                gameTimeLine.prefix(Formatting.allTags.deserialize("<b><red>${SG_FONT_TAG}Game ending<reset> "))
            }

            GameState.OVERTIME -> {
                gameTimeLine.prefix(Formatting.allTags.deserialize("<b><red>${SG_FONT_TAG}Overtime<reset> "))
            }
        }
    }

    fun updateGameTimer() {
        if (instance.manager.getGameState() == GameState.IDLE) {
            gameTimeRemainingLine.prefix(Formatting.allTags.deserialize("<dark_gray>${SG_FONT_TAG}Game Inactive"))
        } else {
            if(instance.manager.getGameState() == GameState.OVERTIME) {
                gameTimeRemainingLine.prefix(Formatting.allTags.deserialize(""))
            } else {
                gameTimeRemainingLine.prefix(Formatting.allTags.deserialize("${SG_FONT_TAG}${instance.timer.getDisplayTimer()}"))
            }
        }
    }

    fun timerBossBar() {
        object : BukkitRunnable() {
            val timerBossBar =
                BossBar.bossBar(Formatting.allTags.deserialize(""), 0f, Color.RED, BossBar.Overlay.PROGRESS)

            override fun run() {
                if (instance.manager.getGameState() != GameState.IDLE) {
                    instance.currentContainer?.players?.forEach { player -> if(!player.activeBossBars().contains(timerBossBar)) timerBossBar.addViewer(player) }
                }
                when(instance.timer.getTimerState()) {
                    GameTimerState.ACTIVE -> {
                        when (instance.manager.getGameState()) {
                            GameState.IDLE -> {
                                instance.currentContainer?.players?.forEach { player -> timerBossBar.removeViewer(player) }
                                this.cancel()
                            }

                            GameState.STARTING -> {
                                timerBossBar.name(TextAlignment.centreBossBarText("GAME STARTING IN: ${if (instance.timer.getTimer() <= 9) "<#ff3333>" else "<#ffff00>"}${instance.timer.getDisplayTimer()}"))
                            }

                            GameState.IN_GAME -> {
                                timerBossBar.name(TextAlignment.centreBossBarText("TIME LEFT: ${if (instance.timer.getTimer() <= 89) "<#ff3333>" else "<#ffff00>"}${instance.timer.getDisplayTimer()}"))
                            }

                            GameState.ROUND_END -> {
                                timerBossBar.name(TextAlignment.centreBossBarText("NEXT ROUND IN: <#ffff00>${instance.timer.getDisplayTimer()}"))
                            }

                            GameState.GAME_END -> {
                                timerBossBar.name(TextAlignment.centreBossBarText("BACK TO HUB: <#ffff00>${instance.timer.getDisplayTimer()}"))
                            }

                            GameState.OVERTIME -> {
                                timerBossBar.name(TextAlignment.centreBossBarText("<#ff3333>OVERTIME"))
                            }
                        }
                    }

                    GameTimerState.INACTIVE -> {
                        timerBossBar.name(TextAlignment.centreBossBarText("<#ff3333>TIMER UNAVAILABLE"))
                    }

                    GameTimerState.PAUSED -> {
                        timerBossBar.name(TextAlignment.centreBossBarText("<yellow>TIMER PAUSED"))
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    fun destroyAllScoreboards() {
        preGameObjective.unregister()
        gameObjective.unregister()
    }
}