package dev.byrt.survivalgames.game.instance

import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.text.ChatUtility
import org.bukkit.command.CommandSender

class GameInstanceTimer(val instance: GameInstance) {
    private var timer = 0
    private var gameTimerState = GameTimerState.INACTIVE
    private var displayTime = "00:00"

    fun setTimer(newTime: Int, sender: CommandSender?) {
        if (newTime == timer) return
        this.timer = newTime
        this.displayTime = String.format("%02d:%02d", (this.timer + 1) / 60, (this.timer + 1) % 60)
        instance.info.updateGameTimer()
        if (sender != null) {
            logger.info("Timer Updated: ${newTime}s remaining [${sender.name}].")
        }
    }

    fun decrement() {
        setTimer(timer - 1, null)
    }

    fun getTimer(): Int {
        return this.timer
    }

    fun getDisplayTimer(): String {
        return this.displayTime
    }

    fun getRemainingGrace(): Int {
        val remainingGrace = GameTime.GRACE_PERIOD - (if(instance.manager.map.isQuickMatch) GameTime.IN_GAME_TIME_QUICK_MATCH - this.timer else GameTime.IN_GAME_TIME - this.timer)
        return if(remainingGrace >= 0) remainingGrace else 0
    }

    fun getRemainingGraceDisplay(): String {
        val remainingGrace = getRemainingGrace()
        val remainingGraceDisplay = String.format("%02d:%02d", (remainingGrace + 1) / 60, (remainingGrace + 1) % 60)
        return remainingGraceDisplay
    }

    fun setTimerState(newState : GameTimerState, sender: CommandSender?) {
        if (newState == gameTimerState) return
        logger.info("Timer State: $gameTimerState -> $newState${if (sender != null) " [${sender.name}]." else "."}")
        this.gameTimerState = newState
    }

    fun getTimerState() : GameTimerState {
        return this.gameTimerState
    }
}

enum class GameTimerState {
    ACTIVE,
    INACTIVE,
    PAUSED
}