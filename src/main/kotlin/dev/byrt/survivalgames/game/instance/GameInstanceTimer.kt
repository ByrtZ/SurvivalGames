package dev.byrt.survivalgames.game.instance

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
            ChatUtility.broadcastDev("<dark_gray>Timer Updated: <yellow>${newTime}s<green> remaining<dark_gray> [${sender.name}].", true)
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

    fun setTimerState(newState : GameTimerState, sender: CommandSender?) {
        if (newState == gameTimerState) return
        ChatUtility.broadcastDev("<dark_gray>Timer State: <red>$gameTimerState<reset> <aqua>-> <green>$newState<dark_gray>${if (sender != null) " [${sender.name}]." else "."}", true)
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