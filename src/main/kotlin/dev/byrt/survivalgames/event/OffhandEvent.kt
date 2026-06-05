package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerSwapHandItemsEvent

@Suppress("unused")
class OffhandEvent: Listener {
    @EventHandler
    private fun onOffhand(e: PlayerSwapHandItemsEvent) {
        if(e.player.sgPlayer().currentContainer != null) {
            val container = e.player.sgPlayer().currentContainer!!
            if(container.instance.manager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                e.isCancelled = true
            }
        } else {
            e.isCancelled = true
        }
        e.isCancelled = true
    }
}