package dev.byrt.survivalgames.command

import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.game.instance.GameTimerState
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import org.bukkit.entity.Player
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
    fun timerSet(sender: Player, seconds: Int) {
        if(sender.sgPlayer().currentContainer != null) {
            val currentContainer = sender.sgPlayer().currentContainer!!
            if(currentContainer.instance.timer.getTimerState() != GameTimerState.INACTIVE) {
                if(currentContainer.instance.manager.getGameState() != GameState.IDLE) {
                    if(seconds > 0) {
                        currentContainer.instance.timer.setTimer(seconds, sender)
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
    }

    @Command("timer skip [seconds]")
    @CommandDescription("Skips the current timer by x seconds or fully.")
    @Permission("sg.cmd.timer")
    fun timerSkip(sender: Player, seconds: Int?) {
        if(sender.sgPlayer().currentContainer != null) {
            val currentContainer = sender.sgPlayer().currentContainer!!
            if(currentContainer.instance.timer.getTimerState() != GameTimerState.INACTIVE) {
                if(seconds != null) {
                    if(seconds > 0) {
                        currentContainer.instance.timer.setTimer(currentContainer.instance.timer.getTimer() - seconds, sender)
                        currentContainer.instance.timer.setTimerState(GameTimerState.ACTIVE, sender)
                    } else {
                        return
                    }
                } else {
                    currentContainer.instance.timer.setTimer(1, sender)
                    currentContainer.instance.timer.setTimerState(GameTimerState.ACTIVE, sender)
                }
            } else {
                return
            }
        }
    }

    @Command("timer pause")
    @CommandDescription("Pauses the current timer.")
    @Permission("sg.cmd.timer")
    fun timerPause(sender: Player) {
        if(sender.sgPlayer().currentContainer != null) {
            val currentContainer = sender.sgPlayer().currentContainer!!
            if(currentContainer.instance.timer.getTimerState() == GameTimerState.ACTIVE) {
                currentContainer.instance.timer.setTimerState(GameTimerState.PAUSED, sender)
            } else {
                return
            }
        }
    }

    @Command("timer resume")
    @CommandDescription("Resumes the current timer.")
    @Permission("sg.cmd.timer")
    fun timerResume(sender: Player) {
        if(sender.sgPlayer().currentContainer != null) {
            val currentContainer = sender.sgPlayer().currentContainer!!
            if(currentContainer.instance.timer.getTimerState() == GameTimerState.PAUSED) {
                currentContainer.instance.timer.setTimerState(GameTimerState.ACTIVE, sender)
            } else {
                return
            }
        }
    }
}