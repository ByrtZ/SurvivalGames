package dev.byrt.survivalgames.command

import dev.byrt.survivalgames.game.GameManager
import dev.byrt.survivalgames.game.GameState
import dev.byrt.survivalgames.game.GameTimer
import dev.byrt.survivalgames.game.GameTimerState
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class TimerCommands {
    @Command("timer set <seconds>")
    @CommandDescription("Sets the current timer.")
    @Permission("sg.cmd.timer")
    fun timerSet(sender: CommandSender, seconds: Int) {
        if(GameTimer.getTimerState() != GameTimerState.INACTIVE) {
            if(GameManager.getGameState() != GameState.IDLE) {
                if(seconds > 0) {
                    GameTimer.setTimer(seconds, sender)
                } else {
                    return
                }
            } else {
                return
            }
        } else {
            return
        }
    }

    @Command("timer skip [seconds]")
    @CommandDescription("Skips the current timer by x seconds or fully.")
    @Permission("sg.cmd.timer")
    fun timerSkip(sender: CommandSender, seconds: Int?) {
        if(GameTimer.getTimerState() != GameTimerState.INACTIVE) {
            if(seconds != null) {
                if(seconds > 0) {
                    GameTimer.setTimer(GameTimer.getTimer() - seconds, sender)
                    GameTimer.setTimerState(GameTimerState.ACTIVE, sender)
                } else {
                    return
                }
            } else {
                GameTimer.setTimer(1, sender)
                GameTimer.setTimerState(GameTimerState.ACTIVE, sender)
            }
        } else {
            return
        }
    }

    @Command("timer pause")
    @CommandDescription("Pauses the current timer.")
    @Permission("sg.cmd.timer")
    fun timerPause(sender: CommandSender) {
        if(GameTimer.getTimerState() == GameTimerState.ACTIVE) {
            GameTimer.setTimerState(GameTimerState.PAUSED, sender)
        } else {
            return
        }
    }

    @Command("timer resume")
    @CommandDescription("Resumes the current timer.")
    @Permission("sg.cmd.timer")
    fun timerResume(sender: CommandSender) {
        if(GameTimer.getTimerState() == GameTimerState.PAUSED) {
            GameTimer.setTimerState(GameTimerState.ACTIVE, sender)
        } else {
            return
        }
    }
}