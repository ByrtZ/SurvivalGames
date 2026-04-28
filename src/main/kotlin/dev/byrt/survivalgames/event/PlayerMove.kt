package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.game.GameManager
import dev.byrt.survivalgames.game.GameState
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

@Suppress("unused")
class PlayerMove: Listener {
    @EventHandler
    private fun onMove(e: PlayerMoveEvent) {
        if(GameManager.getGameState() == GameState.STARTING) {
            for(player in Bukkit.getOnlinePlayers().filter { online -> GameManager.teams.isParticipating(online.uniqueId) }) {
                val to = e.from
                to.pitch = e.to.pitch
                to.yaw = e.to.yaw
                e.to = to
            }
        }
    }
}