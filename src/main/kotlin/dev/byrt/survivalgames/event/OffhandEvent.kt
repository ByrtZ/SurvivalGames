package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.PlayerType
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerSwapHandItemsEvent

@Suppress("unused")
class OffhandEvent: Listener {
    @EventHandler
    private fun onOffhand(e: PlayerSwapHandItemsEvent) {
        if(e.player.sgPlayer().currentContainer != null) {
            val container = e.player.sgPlayer().currentContainer!!
            if(container.instance.manager.getGameState() in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                if(e.player.sgPlayer().isDead || e.player.sgPlayer().playerType != PlayerType.PARTICIPANT || e.player.gameMode == GameMode.SPECTATOR) {
                    e.isCancelled = true
                } else {
                    e.isCancelled = false
                }
            } else {
                e.isCancelled = true
            }
        } else {
            e.isCancelled = true
        }
    }
}