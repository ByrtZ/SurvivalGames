package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.text.ChatUtility
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerResourcePackStatusEvent

@Suppress("unused")
class ResourcePackEvent: Listener {
    @EventHandler
    private fun onResourcePackStatusUpdate(e: PlayerResourcePackStatusEvent) {
        if(e.status == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            //TODO: title screen
            /*object : BukkitRunnable() {
                override fun run() {
                    BurbLobby.playerJoinTitleScreen(e.player)
                }
            }.runTaskLater(plugin, 30L)*/
        }
        if(e.status in listOf(PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD, PlayerResourcePackStatusEvent.Status.FAILED_RELOAD, PlayerResourcePackStatusEvent.Status.DISCARDED, PlayerResourcePackStatusEvent.Status.INVALID_URL)) {
            ChatUtility.broadcastDev("RP failed for ${e.player.name} due to ${e.status.name} (${e.id}).", false)
            logger.severe("RP failed for ${e.player.name} due to ${e.status.name} (${e.id}).")
        }
    }
}