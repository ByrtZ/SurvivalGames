package dev.byrt.survivalgames.game

import dev.byrt.survivalgames.game.instance.GamePlayerCount
import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.PlayerType
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
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

    fun addPlayerToContainer(player: Player, gameContainer: GameContainer, forceJoinAsSpectator: Boolean = false) {
        player.sgPlayer().currentContainer = gameContainer
        /** Auto-assign type to participant if joining regularly, if the game is not already running **/
        if(forceJoinAsSpectator) {
            player.sgPlayer().setType(PlayerType.SPECTATOR)
        } else {
            if(gameContainer.instance.manager.getGameState() == GameState.IDLE && gameContainer.players.filter { p -> p.sgPlayer().playerType == PlayerType.PARTICIPANT }.size < GamePlayerCount.MAX_PLAYERS) {
                player.sgPlayer().setType(PlayerType.PARTICIPANT)
                player.sendMessage(Formatting.allTags.deserialize("Joined as participant"))
            } else {
                player.sgPlayer().setType(PlayerType.SPECTATOR)
                player.sendMessage(Formatting.allTags.deserialize("Joined as spectator, game is already running or full"))
            }
        }
        gameContainer.players.add(player)
        /** Update player's current scoreboard and pre-game scoreboard if game idle **/
        if(gameContainer.instance.manager.getGameState() == GameState.IDLE) {
            player.scoreboard = gameContainer.instance.info.preGameScoreboard
            gameContainer.instance.info.updatePreGamePlayersRequired()
        } else {
            player.scoreboard = gameContainer.instance.info.gameScoreboard
        }
        player.teleport(Location(gameContainer.containerWorld, -1914.5, 78.0, -1680.5, 0f, 0f))
    }

    fun removePlayerFromContainer(player: Player) {
        player.sgPlayer().setType(PlayerType.SPECTATOR)
        player.sgPlayer().currentContainer?.instance?.manager?.gameEndCheck()
        player.sgPlayer().currentContainer?.players?.remove(player)
        player.sgPlayer().currentContainer = null
        player.scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        player.activeBossBars().forEach { bossBar -> bossBar.removeViewer(player) }
        player.teleport(Location(Bukkit.getWorlds()[0], -1914.5, 78.0, -1680.5, 0f, 0f))
    }
}