package dev.byrt.survivalgames.command

import dev.byrt.survivalgames.game.instance.GameInstanceManager
import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.game.instance.GameInstanceTimer
import dev.byrt.survivalgames.game.instance.GameTimerState
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
        if(GameInstanceTimer.getTimerState() != GameTimerState.INACTIVE) {
            if(GameInstanceManager.getGameState() != GameState.IDLE) {
                if(seconds > 0) {
                    GameInstanceTimer.setTimer(seconds, sender)
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
        if(GameInstanceTimer.getTimerState() != GameTimerState.INACTIVE) {
            if(seconds != null) {
                if(seconds > 0) {
                    GameInstanceTimer.setTimer(GameInstanceTimer.getTimer() - seconds, sender)
                    GameInstanceTimer.setTimerState(GameTimerState.ACTIVE, sender)
                } else {
                    return
                }
            } else {
                GameInstanceTimer.setTimer(1, sender)
                GameInstanceTimer.setTimerState(GameTimerState.ACTIVE, sender)
            }
        } else {
            return
        }
    }

    @Command("timer pause")
    @CommandDescription("Pauses the current timer.")
    @Permission("sg.cmd.timer")
    fun timerPause(sender: CommandSender) {
        if(GameInstanceTimer.getTimerState() == GameTimerState.ACTIVE) {
            GameInstanceTimer.setTimerState(GameTimerState.PAUSED, sender)
        } else {
            return
        }
    }

    @Command("timer resume")
    @CommandDescription("Resumes the current timer.")
    @Permission("sg.cmd.timer")
    fun timerResume(sender: CommandSender) {
        if(GameInstanceTimer.getTimerState() == GameTimerState.PAUSED) {
            GameInstanceTimer.setTimerState(GameTimerState.ACTIVE, sender)
        } else {
            return
        }
    }
}