package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.player.PlayerManager
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent

@Suppress("unused")
class PlayerJoin: Listener {
    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        e.joinMessage(Formatting.allTags.deserialize("${if(e.player.isOp) "<dark_red>" else "<speccolour>"}${e.player.name}<reset> joined the game."))
        PlayerManager.registerPlayer(e.player)
        e.player.teleport(Location(Bukkit.getWorlds()[0], -1914.5, 78.0, -1680.5, 0f, 0f))
    }

    @EventHandler
    fun onAttemptJoin(e: AsyncPlayerPreLoginEvent) {
        val player = Bukkit.getPlayer(e.uniqueId) ?: return
        if(!player.isOp && !player.isWhitelisted && plugin.server.isWhitelistEnforced) {
            logger.info("Player ${player.name} (${player.uniqueId}) attempted to join but is not whitelisted.")
        }
    }
}