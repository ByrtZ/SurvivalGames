package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.player.PlayerManager
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.text.Formatting
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("unused")
class PlayerQuit: Listener {
    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        PlayerManager.unregisterPlayer(e.player.sgPlayer())
        //Jukebox.disconnect(e.player)
        //PlayerVisuals.disconnectInterrupt(e.player)
        e.quitMessage(Formatting.allTags.deserialize("${if(e.player.isOp) "<dark_red>" else "<speccolour>"}${e.player.name}<reset> left the game."))
    }
}