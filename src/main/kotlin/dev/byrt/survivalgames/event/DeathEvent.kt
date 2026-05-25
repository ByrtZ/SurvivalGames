package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.PlayerVisuals
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

@Suppress("unused")
class DeathEvent : Listener {
    @EventHandler
    private fun onDeath(e: PlayerDeathEvent) {
        if(e.player.sgPlayer().currentContainer != null) {
            e.player.killer?.playSound(Sounds.Score.ELIMINATION)
            PlayerVisuals.death(e.player, e.player.killer, true)
            e.isCancelled = true
        }
        e.isCancelled = true
    }
}