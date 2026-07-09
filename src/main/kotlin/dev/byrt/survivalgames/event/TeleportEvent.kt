package dev.byrt.survivalgames.event

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause

@Suppress("unused")
class TeleportEvent: Listener {
    @EventHandler
    fun onPlayerTeleport(e: PlayerTeleportEvent) {
        if(e.cause in listOf(TeleportCause.SPECTATE, TeleportCause.COMMAND)) {
            e.isCancelled = true
        }
    }
}