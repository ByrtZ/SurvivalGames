package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent

class HungerEvent: Listener {
    @EventHandler
    private fun onHungerChange(e: FoodLevelChangeEvent) {
        if(e.entity is Player) {
            val player = e.entity as Player
            if(player.sgPlayer().currentContainer != null) {
                val container = player.sgPlayer().currentContainer!!
                if(container.instance.manager.getGameState() in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                    e.isCancelled = false
                } else {
                    e.isCancelled = true
                    e.entity.foodLevel = 20
                }
            } else {
                e.isCancelled = true
                e.entity.foodLevel = 20
            }
        }
    }
}