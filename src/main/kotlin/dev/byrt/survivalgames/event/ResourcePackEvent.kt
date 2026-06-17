package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.music.Jukebox
import dev.byrt.survivalgames.music.JukeboxTrack
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.PlayerType
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.ChatUtility
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerResourcePackStatusEvent
import org.bukkit.scheduler.BukkitRunnable

@Suppress("unused")
class ResourcePackEvent: Listener {
    @EventHandler
    private fun onResourcePackStatusUpdate(e: PlayerResourcePackStatusEvent) {
        when (e.status) {
            PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED -> {
                e.player.sgPlayer().setType(PlayerType.IDLE)
                e.player.teleport(Location(Bukkit.getWorlds()[0], -1914.5, 78.0, -1680.5, 0f, 0f))
                object : BukkitRunnable() {
                    override fun run() {
                        Jukebox.startMusicLoop(e.player, JukeboxTrack.LOBBY)
                    }
                }.runTaskLater(plugin, 30L)
                //TODO: title screen
            }
            in listOf(
                PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD,
                PlayerResourcePackStatusEvent.Status.FAILED_RELOAD,
                PlayerResourcePackStatusEvent.Status.DISCARDED,
                PlayerResourcePackStatusEvent.Status.INVALID_URL
            ) -> {
                ChatUtility.broadcastDev("Pack failed for ${e.player.name} due to ${e.status.name} (${e.id}).", false)
                logger.severe("Pack failed for ${e.player.name} due to ${e.status.name} (${e.id}).")
            } else -> {}
        }
    }
}