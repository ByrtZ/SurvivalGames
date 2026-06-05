package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*

@Suppress("unused")
class InventoryEvent: Listener {
    @EventHandler
    private fun onInventoryClick(e : InventoryClickEvent) {
        if(e.whoClicked.gameMode == GameMode.SPECTATOR) return
        if(e.whoClicked is Player) {
            val player = e.whoClicked as Player
            if(player.sgPlayer().currentContainer != null) {
                val container = player.sgPlayer().currentContainer!!
                if(container.instance.manager.getGameState() in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                    e.isCancelled = false
                } else {
                    e.isCancelled = e.whoClicked.gameMode != GameMode.CREATIVE
                }
            } else {
                e.isCancelled = e.whoClicked.gameMode != GameMode.CREATIVE
            }
        }
    }

    @EventHandler
    private fun onInventoryMove(e : InventoryMoveItemEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun onInventoryDrag(e : InventoryDragEvent) {
        if(e.whoClicked.gameMode == GameMode.SPECTATOR) return
        if(e.whoClicked is Player) {
            val player = e.whoClicked as Player
            if(player.sgPlayer().currentContainer != null) {
                val container = player.sgPlayer().currentContainer!!
                if(container.instance.manager.getGameState() in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                    e.isCancelled = false
                } else {
                    e.isCancelled = e.whoClicked.gameMode != GameMode.CREATIVE
                }
            } else {
                e.isCancelled = e.whoClicked.gameMode != GameMode.CREATIVE
            }
        }
    }

    @EventHandler
    private fun onInventoryPickupItem(e : InventoryPickupItemEvent) {
        e.isCancelled = true
    }
}