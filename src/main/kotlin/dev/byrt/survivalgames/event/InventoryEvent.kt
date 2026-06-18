package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.defaultCoroutineScope
import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.interfaces.SGDeathInterface
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.PlayerType
import dev.byrt.survivalgames.plugin
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
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
            if(player.sgPlayer().currentContainer != null && player.sgPlayer().playerType == PlayerType.PARTICIPANT) {
                val container = player.sgPlayer().currentContainer!!
                e.isCancelled = container.instance.manager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)
            } else {
                e.isCancelled = true
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
            if(player.sgPlayer().currentContainer != null && player.sgPlayer().playerType == PlayerType.PARTICIPANT) {
                val container = player.sgPlayer().currentContainer!!
                e.isCancelled = container.instance.manager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)
            } else {
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    private fun onInventoryPickupItem(e : InventoryPickupItemEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun onInventoryClose(e : InventoryCloseEvent) {
        if(e.player is Player) {
            val player = e.player as Player
            if(player.isOnline && player.isConnected) {
                if(player.sgPlayer().currentContainer != null && e.reason == InventoryCloseEvent.Reason.PLAYER) {
                    /** Reopen death interface if the player closes it by themself, they must make a choice **/
                    if(e.view.title() == SGDeathInterface.INTERFACE_TITLE) {
                        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                            defaultCoroutineScope.launch { SGDeathInterface.create(player) }
                        }, 2L)
                    }
                }
            }
        }
    }
}