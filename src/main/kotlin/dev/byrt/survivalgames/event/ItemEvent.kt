package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import org.bukkit.GameMode
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
        if(e.player.sgPlayer().currentContainer != null) {
            val container = e.player.sgPlayer().currentContainer!!
            if(container.instance.manager.getGameState() in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                e.isCancelled = false
            } else {
                e.isCancelled = e.player.gameMode != GameMode.CREATIVE
            }
        } else {
            e.isCancelled = e.player.gameMode != GameMode.CREATIVE
        }
        e.isCancelled = true
    }

    @EventHandler
    private fun playerDropItemEvent(e: PlayerDropItemEvent) {
        if(e.player.sgPlayer().currentContainer != null) {
            val container = e.player.sgPlayer().currentContainer!!
            if(container.instance.manager.getGameState() in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                e.isCancelled = false
            } else {
                e.isCancelled = e.player.gameMode != GameMode.CREATIVE
            }
        } else {
            e.isCancelled = e.player.gameMode != GameMode.CREATIVE
        }
        e.isCancelled = true
    }
}