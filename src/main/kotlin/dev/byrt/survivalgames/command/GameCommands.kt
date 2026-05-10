package dev.byrt.survivalgames.command

import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.text.ChatUtility
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer
import org.incendo.cloud.processors.confirmation.annotation.Confirmation

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class GameCommands {
    @Command("game start")
    @CommandDescription("Starts the game in the executing player's game container.")
    @Permission("burb.cmd.game")
    @Confirmation
    fun start(sender: Player) {
        if(sender.sgPlayer().currentContainer != null) {
            val currentContainer = sender.sgPlayer().currentContainer!!
            if(currentContainer.instance.manager.getGameState() == GameState.IDLE) {
                ChatUtility.broadcastDev("<dark_gray>${sender.name} started match in container ${currentContainer.containerId}.", false)
                currentContainer.instance.manager.nextState()
            }
        }
    }

    @Command("game stop")
    @CommandDescription("Stops the game in the executing player's game container.")
    @Permission("burb.cmd.game")
    fun stop(sender: Player) {
        if(sender.sgPlayer().currentContainer != null) {
            val currentContainer = sender.sgPlayer().currentContainer!!
            if(currentContainer.instance.manager.getGameState() != GameState.IDLE) {
                ChatUtility.broadcastDev("<dark_gray>${sender.name} stopped match in container ${currentContainer.containerId}.", false)
                currentContainer.instance.manager.setGameState(GameState.GAME_END)
            }
        }
    }

    @Command("game next_phase")
    @CommandDescription("Pushes the game to it's next phase in the executing player's game container.")
    @Permission("burb.cmd.game")
    @Confirmation
    fun nextPhase(sender: Player) {
        if(sender.sgPlayer().currentContainer != null) {
            val currentContainer = sender.sgPlayer().currentContainer!!
            if(currentContainer.instance.manager.getGameState() == GameState.IDLE) return
            ChatUtility.broadcastDev("<dark_gray>${sender.name} pushed match ${currentContainer.containerId} to its next state.", false)
            currentContainer.instance.manager.nextState()
        }
    }

    @Command("game force_state <state>")
    @CommandDescription("Forces the game into the specified phase in the executing player's game container.")
    @Permission("burb.cmd.game")
    @Confirmation
    fun forceState(sender: Player, @Argument(value = "state") state: GameState) {
        if(sender.sgPlayer().currentContainer != null) {
            val currentContainer = sender.sgPlayer().currentContainer!!
            if(currentContainer.instance.manager.getGameState() == GameState.IDLE) return
            ChatUtility.broadcastDev("<dark_gray>${sender.name} forced match ${currentContainer.containerId} into $state state.", false)
            currentContainer.instance.manager.forceState(state)
        }
    }

    @Command("game set_rounds <amount>")
    @CommandDescription("Sets the total number of rounds in the executing player's game container.")
    @Permission("burb.cmd.game")
    fun setRounds(sender: Player, @Argument amount: Int) {
        if(sender.sgPlayer().currentContainer != null) {
            val currentContainer = sender.sgPlayer().currentContainer!!
            if(currentContainer.instance.manager.getGameState() == GameState.IDLE) {
                if(amount <= 1) return
                if(amount == currentContainer.instance.rounds.getTotalRounds()) return
                ChatUtility.broadcastDev("<dark_gray>${sender.name} set the total number of rounds in match ${currentContainer.containerId} to $amount.", false)
                currentContainer.instance.rounds.setTotalRounds(amount)
            }
        }
    }
}