package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.PlayerType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

@Suppress("unused")
class PlayerFish: Listener {
    @EventHandler
    private fun onPlayerFish(e: PlayerFishEvent) {
        if(e.state == PlayerFishEvent.State.LURED) {
            e.isCancelled = true
            return
        }
        if(e.player.sgPlayer().currentContainer != null && e.player.sgPlayer().playerType == PlayerType.PARTICIPANT) {
            e.isCancelled = false
        } else {
            e.isCancelled = true
        }
    }
}