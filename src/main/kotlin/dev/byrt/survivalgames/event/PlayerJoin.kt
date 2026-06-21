package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.lobby.info.LobbyInfo
import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.player.PlayerManager
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import net.kyori.adventure.text.Component
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
        e.joinMessage(null)
        PlayerManager.registerPlayer(e.player)
        e.player.teleport(Location(Bukkit.getWorlds()[0], -1914.5, 78.0, -1680.5, 0f, 0f))
        e.player.sendPlayerListHeaderAndFooter(Formatting.allTags.deserialize(" <b><playercolour>${SG_FONT_TAG}Survival Games<reset><newline>${SG_FONT_TAG} An unbeatable classic Minecraft minigame. "), Component.empty())
        LobbyInfo.updateTotalPlayers()
    }

    @EventHandler
    fun onAttemptJoin(e: AsyncPlayerPreLoginEvent) {
        val player = Bukkit.getOfflinePlayer(e.uniqueId)
        if(!player.isOp && !player.isWhitelisted && plugin.server.isWhitelistEnforced) {
            logger.info("Player ${player.name} (${player.uniqueId}) attempted to join but is not whitelisted.")
        }
    }
}