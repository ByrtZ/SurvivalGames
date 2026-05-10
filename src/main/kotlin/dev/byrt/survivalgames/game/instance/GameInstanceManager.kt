package dev.byrt.survivalgames.game.instance

import dev.byrt.survivalgames.game.GameManager
import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.text.ChatUtility
import dev.byrt.survivalgames.text.Formatting
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.time.Duration

class GameInstanceManager(val instance: GameInstance) {
    //public val teams = TeamManager<Team>()
    private var gameState = GameState.IDLE
    private var overtimeActive = false

    init {
        //Bukkit.getPluginManager().registerEvents(teams, plugin)
        //Team.PLAYER.container = instance.currentContainer
    }

    fun nextState() {
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
        if (newState == gameState) return
        ChatUtility.broadcastDev("<dark_gray>Game State: <red>$gameState<reset> <aqua>-> <green>$newState<dark_gray>.", true)
        this.gameState = newState
        instance.info.updateStatus()
        when(this.gameState) {
            GameState.IDLE -> {
                // automatically destroy container when game is over
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
                instance.timer.setTimer(GameTime.IN_GAME_TIME, null)
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
            player.playSound(Sounds.Timer.STARTING_GO)
            player.playSound(Sounds.Timer.CLOCK_TICK_HIGH)
            player.resetTitle()
        }
    }

    private fun starting() {
        if(instance.rounds.getRound() == 1) {
            for(player in instance.currentContainer?.players!!) {
                player.showTitle(Title.title(Formatting.glyph("\uD000"), Component.text(""), Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(2), Duration.ofSeconds(1))))
                player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 0, false, false))
            }
        }
        for(player in instance.currentContainer?.players!!) {
            /*if(teams.isParticipating(player.uniqueId)) {
                //TODO: CORNUCOPIA SPAWNS
            }*/
            //Jukebox.disconnect(player)
        }
    }

    private fun startOvertime() {}

    private fun gameEnd() {
        for(player in instance.currentContainer?.players!!) {
            player.playSound(Sounds.Round.GAME_OVER)
            player.playSound(Sounds.Round.ROUND_END)
            //Jukebox.disconnect(player)
            player.showTitle(
                Title.title(
                    Component.text("Game Over!", NamedTextColor.RED, TextDecoration.BOLD),
                    Component.text(""),
                    Title.Times.times(
                        Duration.ofSeconds(0),
                        Duration.ofSeconds(4),
                        Duration.ofSeconds(1)
                    )
                )
            )
        }
    }

    private fun roundEnd() {
        for(player in instance.currentContainer?.players!!) {
            //Jukebox.disconnect(player)
            player.playSound(Sounds.Round.ROUND_END)
            player.showTitle(
                Title.title(
                    Component.text("Round Over!", NamedTextColor.RED, TextDecoration.BOLD),
                    Component.text(""),
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


}

object GameTime {
    const val GAME_STARTING_TIME = 80
    const val ROUND_STARTING_TIME = 30
    const val IN_GAME_TIME = 900
    const val ROUND_END_TIME = 15
    const val GAME_END_TIME = 100
    const val OVERTIME_TIME = 30
}

enum class GameState {
    IDLE,
    STARTING,
    IN_GAME,
    ROUND_END,
    GAME_END,
    OVERTIME
}