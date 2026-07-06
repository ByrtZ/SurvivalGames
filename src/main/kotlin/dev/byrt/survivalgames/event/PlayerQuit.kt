package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.lobby.info.LobbyInfo
import dev.byrt.survivalgames.music.Jukebox
import dev.byrt.survivalgames.player.PlayerManager
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.plugin
import org.bukkit.Bukkit
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
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            LobbyInfo.updateTotalPlayers()
        }, 20L)
    }
}