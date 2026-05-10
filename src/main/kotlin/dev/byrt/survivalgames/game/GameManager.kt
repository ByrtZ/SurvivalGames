package dev.byrt.survivalgames.game

import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.event.PlayerJoinContainerEvent
import dev.byrt.survivalgames.player.event.PlayerLeaveContainerEvent
import dev.byrt.survivalgames.world.SGWorldCreator
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object GameManager {
    private val gameContainers = mutableListOf<GameContainer>()
    fun createContainer(): GameContainer {
        val newContainerId = UUID.randomUUID()
        val newContainerWorld = SGWorldCreator.createNewGameWorld(newContainerId)
        val newContainer = GameContainer(
            containerName = "sg-container-$newContainerId",
            containerId = newContainerId,
            containerWorld = newContainerWorld
        )
        newContainer.onCreate()
        gameContainers.add(newContainer)
        return newContainer
    }

    fun destroyContainer(gameContainer: GameContainer) {
        gameContainer.onDestroy()
        gameContainers.remove(gameContainer)
        // move to game end/automatic expiry?
    }

    fun addPlayerToContainer(player: Player, gameContainer: GameContainer) {
        player.sgPlayer().currentContainer = gameContainer
        Bukkit.getPluginManager().callEvent(PlayerJoinContainerEvent(player, gameContainer))
        player.scoreboard = player.sgPlayer().currentContainer?.instance?.info?.scoreboard!!
    }

    fun removePlayerFromContainer(player: Player) {
        Bukkit.getPluginManager().callEvent(PlayerLeaveContainerEvent(player, player.sgPlayer().currentContainer))
        player.sgPlayer().currentContainer = null
        player.scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    }
}