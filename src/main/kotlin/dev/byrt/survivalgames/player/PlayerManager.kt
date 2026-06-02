package dev.byrt.survivalgames.player

import dev.byrt.survivalgames.exception.PlayerManagerException
import dev.byrt.survivalgames.game.GameManager
import dev.byrt.survivalgames.logger
import org.bukkit.entity.Player
import java.util.UUID

object PlayerManager {
    private val sgPlayers = mutableMapOf<UUID, SGPlayer>()
    fun registerPlayer(player: Player) {
        logger.info("Player Manager: Registering player ${player.name} as SGPlayer.")
        val sgPlayer = SGPlayer(player.uniqueId, player.name, PlayerType.UNREGISTERED)
        sgPlayers[player.uniqueId] = sgPlayer
        player.inventory.clear()
        //PlayerVisuals.showPlayer(player)
    }

    fun unregisterPlayer(sgPlayer: SGPlayer) {
        sgPlayer.isDead = true
        GameManager.removePlayerFromContainer(sgPlayer.bukkitPlayer())
        sgPlayer.currentContainer = null
        sgPlayer.setType(PlayerType.UNREGISTERED)
        sgPlayers.remove(sgPlayer.uuid)
    }

    fun UUID.sgPlayer(): SGPlayer = sgPlayers[this] ?: throw PlayerManagerException("Unable to find SGPlayer.")
    fun Player.sgPlayer(): SGPlayer = uniqueId.sgPlayer()
}