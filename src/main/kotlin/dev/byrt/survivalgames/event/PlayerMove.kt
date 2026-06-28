package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.game.mechanic.light.SGPlayerDynamicLight
import dev.byrt.survivalgames.map.SGMap
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.PlayerType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

@Suppress("unused")
class PlayerMove: Listener {
    @EventHandler
    private fun onMove(e: PlayerMoveEvent) {
        val movedPlayer = e.player
        if(movedPlayer.sgPlayer().currentContainer != null && e.player.sgPlayer().playerType == PlayerType.PARTICIPANT) {
            val currentContainer = movedPlayer.sgPlayer().currentContainer!!
            if(currentContainer.instance.manager.getGameState() == GameState.STARTING) {
                val to = e.from
                to.pitch = e.to.pitch
                to.yaw = e.to.yaw
                e.to = to
            }
            if(movedPlayer.sgPlayer().playerType == PlayerType.PARTICIPANT && currentContainer.instance.manager.map == SGMap.MISTWOODS) {
                SGPlayerDynamicLight.onPlayerMove(movedPlayer, e.from, e.to)
            }
        }
    }
}