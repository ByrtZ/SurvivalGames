package dev.byrt.survivalgames.player

import dev.byrt.survivalgames.exception.PlayerManagerException
import dev.byrt.survivalgames.logger
import org.bukkit.entity.Player
import java.util.UUID

object PlayerManager {
    private val sgPlayers = mutableMapOf<UUID, SGPlayer>()
    fun registerPlayer(player: Player) {
        logger.info("Player Manager: Registering player ${player.name} as SGPlayer.")
        val sgPlayer = SGPlayer(player.uniqueId, player.name, PlayerType.UNREGISTERED)
        sgPlayers[player.uniqueId] = sgPlayer
        //ItemManager.clearItems(player)
        //PlayerVisuals.showPlayer(player)
    }

    fun unregisterPlayer(sgPlayer: SGPlayer) {
        sgPlayer.setType(PlayerType.UNREGISTERED)
        sgPlayer.isDead = true
        sgPlayer.currentContainer = null
        sgPlayers.remove(sgPlayer.uuid)
    }

    fun UUID.sgPlayer(): SGPlayer = sgPlayers[this] ?: throw PlayerManagerException("Unable to find SGPlayer.")
    fun Player.sgPlayer(): SGPlayer = uniqueId.sgPlayer()
}