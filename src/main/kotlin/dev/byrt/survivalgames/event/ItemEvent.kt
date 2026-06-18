package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.PlayerType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent

@Suppress("unused")
class ItemEvent: Listener {
    @EventHandler
    private fun blockDropItemEvent(e: BlockDropItemEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun playerPickupItemEvent(e: PlayerAttemptPickupItemEvent) {
        if(e.player.sgPlayer().currentContainer != null && e.player.sgPlayer().playerType == PlayerType.PARTICIPANT) {
            val container = e.player.sgPlayer().currentContainer!!
            e.isCancelled = container.instance.manager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)
        } else {
            e.isCancelled = true
        }
    }

    @EventHandler
    private fun playerDropItemEvent(e: PlayerDropItemEvent) {
        if(e.player.sgPlayer().currentContainer != null && e.player.sgPlayer().playerType == PlayerType.PARTICIPANT) {
            val container = e.player.sgPlayer().currentContainer!!
            e.isCancelled = container.instance.manager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)
        } else {
            e.isCancelled = false
        }
    }
}