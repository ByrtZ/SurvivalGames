package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.lobby.info.LobbyInfo
import dev.byrt.survivalgames.music.Jukebox
import dev.byrt.survivalgames.player.PlayerManager
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("unused")
class PlayerQuit: Listener {
    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        PlayerManager.unregisterPlayer(e.player.sgPlayer())
        Jukebox.disconnect(e.player)
        e.quitMessage(null)
        LobbyInfo.updateTotalPlayers()
    }
}