package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.game.instance.GameInstanceManager
import dev.byrt.survivalgames.game.instance.GameState
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

@Suppress("unused")
class PlayerMove: Listener {
    @EventHandler
    private fun onMove(e: PlayerMoveEvent) {
        if(GameInstanceManager.getGameState() == GameState.STARTING) {
            for(player in Bukkit.getOnlinePlayers().filter { online -> GameInstanceManager.teams.isParticipating(online.uniqueId) }) {
                val to = e.from
                to.pitch = e.to.pitch
                to.yaw = e.to.yaw
                e.to = to
            }
        }
    }
}