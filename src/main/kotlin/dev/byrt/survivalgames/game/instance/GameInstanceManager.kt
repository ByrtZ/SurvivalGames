package dev.byrt.survivalgames.game.instance

import dev.byrt.survivalgames.defaultCoroutineScope
import dev.byrt.survivalgames.game.GameManager
import dev.byrt.survivalgames.item.SGItem
import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.library.Translation
import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.loot.SGLoot
import dev.byrt.survivalgames.map.SGMap
import dev.byrt.survivalgames.music.Jukebox
import dev.byrt.survivalgames.player.PlayerType
import dev.byrt.survivalgames.player.PlayerVisuals
import dev.byrt.survivalgames.player.progression.SGExperienceLevels
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import dev.byrt.survivalgames.util.extension.trimmed
import io.papermc.paper.entity.LookAnchor
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.time.Duration
import java.util.*
import kotlin.random.Random

class GameInstanceManager(val instance: GameInstance) {
    private var gameState = GameState.IDLE
    private var overtimeActive = true
    val activeSupplyDrops = mutableMapOf<UUID, Location>()
    /** Not to be set outside of initialisation under any circumstance **/
    var map = listOf(SGMap.AUBURN_FOREST, SGMap.AELUMIA_CITADEL).random()
        set(value) {
            if(field == value) return
            field = value
            instance.info.updatePreGameMap()
        }
    var isGracePeriod: Boolean? = null
        set(value) {
            if(field == value) return
            field = value
            if(field == true) PlayerVisuals.gracePeriodStart(instance.currentContainer)
            if(field == false) PlayerVisuals.gracePeriodEnd(instance.currentContainer)
        }

    fun nextState() {
        if(instance.currentContainer?.isEditMode == true) return
        when(this.gameState) {
            GameState.IDLE -> { setGameState(GameState.STARTING) }
            GameState.STARTING -> { setGameState(GameState.IN_GAME) }
            GameState.IN_GAME -> {
                if (overtimeActive) {
                    setGameState(GameState.OVERTIME)
                } else if (instance.rounds.getRound() >= instance.rounds.getTotalRounds()) {
                    setGameState(GameState.GAME_END)
                } else {
                    setGameState(GameState.ROUND_END)
                }
            }
            GameState.ROUND_END -> { setGameState(GameState.STARTING) }
            GameState.GAME_END -> { setGameState(GameState.IDLE) }
            GameState.OVERTIME -> {
                if (instance.rounds.getRound() >= instance.rounds.getTotalRounds()) {
                    setGameState(GameState.GAME_END)
                } else {
                    setGameState(GameState.ROUND_END)
                }
            }
        }
    }

    fun setGameState(newState: GameState) {
        if(instance.currentContainer?.isEditMode == true) return
        if (newState == gameState) return
        logger.info("Game State: $gameState -> $newState.")
        this.gameState = newState
        instance.info.updateGameStatus()
        when(this.gameState) {
            GameState.IDLE -> {
                instance.task.stopGameLoop()
                GameManager.destroyContainer(instance.currentContainer!!)
            }
            GameState.STARTING -> {
                if (instance.rounds.getRound() == 1) {
                    instance.timer.setTimerState(GameTimerState.ACTIVE, null)
                    instance.timer.setTimer(GameTime.GAME_STARTING_TIME, null)
                    instance.task.startGameLoop()
                    instance.info.timerBossBar()
                    starting()
                } else {
                    instance.timer.setTimerState(GameTimerState.ACTIVE, null)
                    instance.timer.setTimer(GameTime.ROUND_STARTING_TIME, null)
                    starting()
                }
            }
            GameState.IN_GAME -> {
                instance.timer.setTimerState(GameTimerState.ACTIVE, null)
                instance.timer.setTimer(if(map.isQuickMatch) GameTime.IN_GAME_TIME_QUICK_MATCH else GameTime.IN_GAME_TIME, null)
                startRound()
            }
            GameState.ROUND_END -> {
                instance.timer.setTimerState(GameTimerState.ACTIVE, null)
                instance.timer.setTimer(GameTime.ROUND_END_TIME, null)
                roundEnd()
            }
            GameState.GAME_END -> {
                instance.timer.setTimerState(GameTimerState.ACTIVE, null)
                instance.timer.setTimer(GameTime.GAME_END_TIME, null)
                gameEnd()
            }
            GameState.OVERTIME -> {
                instance.timer.setTimerState(GameTimerState.ACTIVE, null)
                instance.timer.setTimer(GameTime.OVERTIME_TIME, null)
                startOvertime()
            }
        }
    }

    private fun startRound() {
        for(player in instance.currentContainer?.players!!) {
            player.bukkitPlayer().playSound(Sounds.Timer.STARTING_GO)
            player.bukkitPlayer().playSound(Sounds.Timer.CLOCK_TICK_HIGH)
            player.bukkitPlayer().resetTitle()
            player.bukkitPlayer().addPotionEffects(listOf(
                    PotionEffect(PotionEffectType.SPEED, 20 * GameTime.GRACE_PERIOD, 1, true, true),
                    PotionEffect(PotionEffectType.ABSORPTION, 20 * GameTime.GRACE_PERIOD, 1, true, true)
                )
            )
            if(map == SGMap.MISTWOODS) player.bukkitPlayer().give(SGItem.getLantern())
        }
        isGracePeriod = true

        if(map == SGMap.MISTWOODS) {
            instance.currentContainer?.containerWorld?.time = 10000
            object : BukkitRunnable() {
                override fun run() {
                    if(instance.currentContainer != null) {
                        if(instance.currentContainer?.containerWorld?.time!! < 18000) {
                            instance.currentContainer?.containerWorld?.time += 5
                        } else {
                            instance.currentContainer?.containerWorld?.time = 18000
                            cancel()
                        }
                    } else {
                        cancel()
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L)
        }
    }

    private fun starting() {
        defaultCoroutineScope.launch {
            GameManager.createContainer()
        }.invokeOnCompletion { _ ->
            logger.info("[Matchmaking] Game started in instance ${instance.gameInstanceId.trimmed()}, server generated another match.")
        }
        /** Populate map loot **/
        instance.currentContainer?.containerWorld?.let { SGLoot.populateMapLoot(it, map) }
        /** Set world border center and size **/
        val borderCenter = map.worldCenter.first()
        instance.currentContainer?.containerWorld?.worldBorder?.setCenter(borderCenter.x + 0.5, borderCenter.z + 0.5)
        instance.currentContainer?.containerWorld?.worldBorder?.size = map.borderSize
        instance.currentContainer?.containerWorld?.worldBorder?.damageBuffer = 0.0
        instance.currentContainer?.containerWorld?.worldBorder?.damageAmount = 0.05
        /** Spawn allocation, only use first available spectator spawn and cast participant spawns to list and iterate for each participant **/
        val spectatorSpawn = map.spectatorSpawns.first()
        val participantSpawns = map.participantSpawns.flatMap { listOf(Location(instance.currentContainer?.containerWorld, it.x, it.y, it.z)) }
        var participantSpawnIndex = 0

        instance.info.updateGamePlayersRemaining()
        for(player in instance.currentContainer?.players!!) {
            player.bukkitPlayer().showTitle(Title.title(Formatting.glyph("\uD000"), Component.text(""), Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(2), Duration.ofSeconds(1))))
            player.bukkitPlayer().addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 8 * 20, 0, false, false))
            PlayerVisuals.resetPlayerState(player.bukkitPlayer(), shouldClearInventory = true)
            player.bukkitPlayer().scoreboard = instance.info.gameScoreboard
            when(player.playerType) {
                PlayerType.SPECTATOR -> player.bukkitPlayer().teleport(Location(instance.currentContainer?.containerWorld, spectatorSpawn.x, spectatorSpawn.y, spectatorSpawn.z))
                PlayerType.PARTICIPANT -> {
                    if(participantSpawnIndex > participantSpawns.size - 1) participantSpawnIndex = 0
                    player.bukkitPlayer().teleport(participantSpawns[participantSpawnIndex])
                    participantSpawnIndex++
                }
                else -> logger.info("Unregistered player in container.")
            }
            player.bukkitPlayer().lookAt(borderCenter.x + 0.5, borderCenter.y, borderCenter.z + 0.5, LookAnchor.EYES)
            Jukebox.disconnect(player.bukkitPlayer())
        }
    }

    private fun startOvertime() {
        for(player in instance.currentContainer?.players!!) {
            player.bukkitPlayer().playSound(Sounds.Round.OVERTIME_START)
            player.bukkitPlayer().sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}<#ff3333><b>${SG_FONT_TAG}OVERTIME: </b></#ff3333>${SG_FONT_TAG}Fight until one player remains, maximum health is now decreasing!"))
            player.bukkitPlayer().showTitle(
                Title.title(
                    Formatting.allTags.deserialize("${SG_FONT_TAG}<#ff3333><b>Overtime"),
                    Formatting.allTags.deserialize("${SG_FONT_TAG}Fight to the death!"),
                    Title.Times.times(
                        Duration.ofSeconds(0),
                        Duration.ofSeconds(2),
                        Duration.ofMillis(250)
                    )
                )
            )
        }
        instance.currentContainer?.containerWorld?.worldBorder?.size = 50.0
    }

    private fun gameEnd() {
        for(player in instance.currentContainer?.players!!) {
            player.bukkitPlayer().playSound(Sounds.Round.GAME_OVER)
            player.bukkitPlayer().playSound(Sounds.Round.ROUND_END)
            Jukebox.disconnect(player.bukkitPlayer())
            player.bukkitPlayer().showTitle(
                Title.title(
                    Formatting.allTags.deserialize("${SG_FONT_TAG}<#ff3333><b>Game Over!"),
                    Component.empty(),
                    Title.Times.times(
                        Duration.ofSeconds(0),
                        Duration.ofSeconds(4),
                        Duration.ofSeconds(1)
                    )
                )
            )
        }
        instance.currentContainer?.containerWorld?.worldBorder?.changeSize(instance.currentContainer?.containerWorld?.worldBorder!!.size, 0)
    }

    private fun roundEnd() {
        for(player in instance.currentContainer?.players!!) {
            Jukebox.disconnect(player.bukkitPlayer())
            player.bukkitPlayer().playSound(Sounds.Round.ROUND_END)
            player.bukkitPlayer().showTitle(
                Title.title(
                    Formatting.allTags.deserialize("${SG_FONT_TAG}<#ff3333><b>Round Over!"),
                    Component.empty(),
                    Title.Times.times(
                        Duration.ofSeconds(0),
                        Duration.ofSeconds(4),
                        Duration.ofSeconds(1)
                    )
                )
            )
        }
    }

    fun getGameState(): GameState {
        return gameState
    }

    fun setOvertimeState(isActive: Boolean) {
        overtimeActive = isActive
    }

    fun isOvertimeActive(): Boolean {
        return overtimeActive
    }

    fun forceState(forcedState: GameState) {
        setGameState(forcedState)
    }

    fun gameEndCheck() {
        instance.info.updateGamePlayersRemaining()
        if(gameState !in listOf(GameState.IN_GAME, GameState.OVERTIME)) return
        if(instance.currentContainer?.disableGameEndCheck == true) return
        val playersAlive = instance.currentContainer?.players?.filter { player -> player.playerType == PlayerType.PARTICIPANT }
        if(playersAlive?.isNotEmpty() == true) {
            if(playersAlive.size == 1) {
                setGameState(GameState.GAME_END)
                val remainingPlayer = playersAlive[0]
                remainingPlayer.bukkitPlayer().playSound(Sounds.Score.WIN_GAME)
                SGExperienceLevels.appendExperience(remainingPlayer.bukkitPlayer(), 200)
                repeat(5) {
                    PlayerVisuals.firework(
                        remainingPlayer.bukkitPlayer().location.clone().add(Random.nextDouble(-3.0, 3.0), Random.nextDouble(-3.0, 3.0), Random.nextDouble(-3.0, 3.0)),
                        flicker = true,
                        trail = true,
                        color = Color.ORANGE,
                        fireworkType = FireworkEffect.Type.STAR,
                        variedVelocity = true
                    )
                }
                instance.currentContainer?.players!!.forEach { player -> player.bukkitPlayer().sendMessage(Formatting.allTags.deserialize("<newline>${SG_FONT_TAG}<playercolour>${if(player == remainingPlayer) "<b>You</b>" else remainingPlayer.playerName}</playercolour> won the game!<newline>")) }
                remainingPlayer.bukkitPlayer().showTitle(
                    Title.title(
                        Formatting.allTags.deserialize("${SG_FONT_TAG}<playercolour><b>Victory"),
                        Formatting.allTags.deserialize("${SG_FONT_TAG}You won the game!"),
                        Title.Times.times(
                            Duration.ofSeconds(0),
                            Duration.ofSeconds(8),
                            Duration.ofSeconds(1)
                        )
                    )
                )
            }
        } else {
            // Force game to end if empty
            setGameState(GameState.GAME_END)
        }
    }
}

object GameTime {
    const val GAME_STARTING_TIME = 30
    const val ROUND_STARTING_TIME = 15
    const val IN_GAME_TIME = 480
    const val IN_GAME_TIME_QUICK_MATCH = 240
    const val ROUND_END_TIME = 10
    const val GAME_END_TIME = 20
    const val OVERTIME_TIME = 600
    const val GRACE_PERIOD = 30
}

object GamePlayerCount {
    const val MAX_PLAYERS = 24
    const val MIN_PLAYERS = 24
}

enum class GameState {
    IDLE,
    STARTING,
    IN_GAME,
    ROUND_END,
    GAME_END,
    OVERTIME
}