package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.PlayerType
import dev.byrt.survivalgames.text.ChatUtility
import dev.byrt.survivalgames.text.Formatting
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
            } else -> {
                ChatUtility.broadcastDev("An error occurred for ${e.player.name} when applying the resource pack.", false)
                logger.severe("An error occurred for ${e.player.name} when applying the resource pack.")
                e.player.kick(Formatting.allTags.deserialize("<red>An error occurred applying the resource pack.<newline>Please contact an administrator."))
            }
        }
    }
}