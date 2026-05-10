package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.PlayerType
import dev.byrt.survivalgames.text.ChatUtility
import dev.byrt.survivalgames.text.Formatting
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerResourcePackStatusEvent

@Suppress("unused")
class ResourcePackEvent: Listener {
    @EventHandler
    private fun onResourcePackStatusUpdate(e: PlayerResourcePackStatusEvent) {
        when (e.status) {
            PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED -> {
                e.player.sgPlayer().setType(PlayerType.SPECTATOR)
                e.player.teleport(Location(Bukkit.getWorlds()[0], -1914.5, 78.0, -1680.5, 0f, 0f))
                //TODO: title screen
                /*object : BukkitRunnable() {
                    override fun run() {
                        BurbLobby.playerJoinTitleScreen(e.player)
                    }
                }.runTaskLater(plugin, 30L)*/
            }
            in listOf(
                PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD,
                PlayerResourcePackStatusEvent.Status.FAILED_RELOAD,
                PlayerResourcePackStatusEvent.Status.DISCARDED,
                PlayerResourcePackStatusEvent.Status.INVALID_URL
            ) -> {
                ChatUtility.broadcastDev("RP failed for ${e.player.name} due to ${e.status.name} (${e.id}).", false)
                logger.severe("RP failed for ${e.player.name} due to ${e.status.name} (${e.id}).")
            } else -> {}
        }
    }
}