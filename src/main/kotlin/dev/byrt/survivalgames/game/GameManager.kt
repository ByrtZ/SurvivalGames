package dev.byrt.survivalgames.game

import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.event.PlayerJoinContainerEvent
import dev.byrt.survivalgames.player.event.PlayerLeaveContainerEvent
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.world.SGWorld
import kotlinx.coroutines.suspendCancellableCoroutine
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object GameManager {
    val gameContainers = mutableListOf<GameContainer>()
    suspend fun createContainer(): GameContainer {
        val newContainerId = UUID.randomUUID()
        val newContainerWorld = SGWorld.createNewGameWorld(newContainerId)
        return suspendCancellableCoroutine { cont ->
            object : BukkitRunnable() {
                override fun run() {
                    try {
                        val newContainer = GameContainer(
                            containerName = "sg-container-$newContainerId",
                            containerId = newContainerId,
                            containerWorld = newContainerWorld
                        )
                        newContainer.onCreate()
                        gameContainers.add(newContainer)
                        cont.resume(newContainer)
                    } catch (e: Exception) {
                        cont.resumeWithException(e)
                    }
                }
            }.runTask(plugin)
        }
    }

    fun destroyContainer(gameContainer: GameContainer) {
        gameContainer.onDestroy()
        gameContainers.remove(gameContainer)
    }

    fun getContainerById(id: String): GameContainer? {
        return gameContainers.find { container -> container.containerId.toString() == id }
    }

    fun getContainerById(id: UUID): GameContainer? {
        return gameContainers.find { container -> container.containerId == id }
    }

    fun addPlayerToContainer(player: Player, gameContainer: GameContainer) {
        player.sgPlayer().currentContainer = gameContainer
        gameContainer.players.add(player)
        Bukkit.getPluginManager().callEvent(PlayerJoinContainerEvent(player, gameContainer))
        player.scoreboard = if(gameContainer.instance.manager.getGameState() == GameState.IDLE) gameContainer.instance.info.preGameScoreboard else gameContainer.instance.info.gameScoreboard
        player.teleport(Location(gameContainer.containerWorld, -1914.5, 78.0, -1680.5, 0f, 0f))
    }

    fun removePlayerFromContainer(player: Player) {
        Bukkit.getPluginManager().callEvent(PlayerLeaveContainerEvent(player, player.sgPlayer().currentContainer))
        player.sgPlayer().currentContainer?.players?.remove(player)
        player.sgPlayer().currentContainer = null
        player.scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        player.activeBossBars().forEach { bossBar -> bossBar.removeViewer(player) }
        player.teleport(Location(Bukkit.getWorlds()[0], -1914.5, 78.0, -1680.5, 0f, 0f))
    }
}