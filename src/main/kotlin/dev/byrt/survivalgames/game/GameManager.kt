package dev.byrt.survivalgames.game

import dev.byrt.survivalgames.game.instance.GamePlayerCount
import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.map.SGMap
import dev.byrt.survivalgames.music.Jukebox
import dev.byrt.survivalgames.music.MusicTrack
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.PlayerType
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.world.SGWorld
import kotlinx.coroutines.suspendCancellableCoroutine
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object GameManager {
    val gameContainers = mutableListOf<GameContainer>()
    suspend fun createContainer(isEditMode: Boolean = false, forcedMap: SGMap? = null): GameContainer {
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
                        /** Creation flags **/
                        if(isEditMode) newContainer.isEditMode = true
                        if(forcedMap != null) newContainer.forcedMap = forcedMap
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
        if(gameContainer.isEditMode && !player.isOp) {
            player.sendMessage(Formatting.allTags.deserialize("<red>You do not have permission to join this instance."))
            return
        }
        player.sgPlayer().currentContainer = gameContainer

        /** Teleport player to the correct place **/
        val preGameSpawnDataPoint = gameContainer.instance.manager.map.preGameSpawns.firstOrNull()
        val preGameSpawn = Location(gameContainer.containerWorld, preGameSpawnDataPoint?.x ?: 0.0, preGameSpawnDataPoint?.y ?: 0.0, preGameSpawnDataPoint?.z ?: 0.0)
        val spectatorSpawnDataPoint = gameContainer.instance.manager.map.spectatorSpawns.firstOrNull()
        val spectatorSpawn = Location(gameContainer.containerWorld, spectatorSpawnDataPoint?.x ?: 0.0, spectatorSpawnDataPoint?.y ?: 0.0, spectatorSpawnDataPoint?.z ?: 0.0)

        /** Auto-assign type to participant if joining regularly, if the game is not already running **/
        if(forceJoinAsSpectator) {
            player.sgPlayer().setType(PlayerType.SPECTATOR)
            player.sendMessage(Formatting.allTags.deserialize("You joined the match as a spectator"))
            player.teleport(spectatorSpawn)
        } else {
            if(gameContainer.instance.manager.getGameState() == GameState.IDLE && gameContainer.players.filter { p -> p.sgPlayer().playerType == PlayerType.PARTICIPANT }.size < GamePlayerCount.MAX_PLAYERS) {
                player.sgPlayer().setType(PlayerType.PARTICIPANT)
                player.sendMessage(Formatting.allTags.deserialize("You joined the match as participant"))
                player.teleport(preGameSpawn)
            } else {
                player.sgPlayer().setType(PlayerType.SPECTATOR)
                player.sendMessage(Formatting.allTags.deserialize("You joined the match as a spectator, game is already running or full"))
                player.teleport(spectatorSpawn)
            }
        }
        gameContainer.players.add(player)
        /** Update player's current scoreboard and pre-game scoreboard if game idle **/
        /** Set player's current Jukebox track **/
        if(gameContainer.instance.manager.getGameState() == GameState.IDLE) {
            player.scoreboard = gameContainer.instance.info.preGameScoreboard
            gameContainer.instance.info.updatePreGamePlayersRequired()
            when(gameContainer.instance.manager.map) {
                SGMap.AUBURN_FOREST -> Jukebox.startMusicLoop(player, MusicTrack.PRE_GAME_AUBURN_FOREST)
                SGMap.ROUGHWORKS -> Jukebox.startMusicLoop(player, MusicTrack.PRE_GAME_ROUGHWORKS)
                SGMap.MISTWOODS -> Jukebox.startMusicLoop(player, MusicTrack.PRE_GAME_MISTWOODS)
                SGMap.HIGHLANDS -> Jukebox.startMusicLoop(player, MusicTrack.PRE_GAME_HIGHLANDS)
                SGMap.AELUMIA_CITADEL -> Jukebox.startMusicLoop(player, MusicTrack.PRE_GAME_AELUMIA_CITADEL)
            }
        } else {
            player.scoreboard = gameContainer.instance.info.gameScoreboard
            if(gameContainer.instance.manager.getGameState() == GameState.STARTING) {
                if(gameContainer.instance.timer.getTimer() < 26) Jukebox.startMusicLoop(player, MusicTrack.IN_GAME)
            } else {
                Jukebox.startMusicLoop(player, MusicTrack.IN_GAME)
            }
        }
    }

    fun removePlayerFromContainer(player: Player) {
        player.sgPlayer().currentContainer?.instance?.manager?.gameEndCheck()
        player.sgPlayer().currentContainer?.players?.remove(player)
        player.sgPlayer().currentContainer = null
        player.sgPlayer().setType(PlayerType.SPECTATOR)
        player.activeBossBars().forEach { bossBar -> bossBar.removeViewer(player) }
        player.inventory.clear()
        player.scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        player.gameMode = GameMode.ADVENTURE
        player.teleport(Location(Bukkit.getWorlds()[0], -1914.5, 78.0, -1680.5, 0f, 0f))
        Jukebox.startMusicLoop(player, MusicTrack.LOBBY)
    }
}