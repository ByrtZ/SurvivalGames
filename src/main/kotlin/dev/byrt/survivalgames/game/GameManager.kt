package dev.byrt.survivalgames.game

import dev.byrt.survivalgames.game.instance.GamePlayerCount
import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.library.Translation
import dev.byrt.survivalgames.lobby.info.LobbyInfo
import dev.byrt.survivalgames.map.SGMap
import dev.byrt.survivalgames.music.Jukebox
import dev.byrt.survivalgames.music.JukeboxTrack
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.PlayerType
import dev.byrt.survivalgames.player.PlayerVisuals
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import dev.byrt.survivalgames.text.TextAlignment
import dev.byrt.survivalgames.util.Keys
import dev.byrt.survivalgames.util.extension.clean
import dev.byrt.survivalgames.util.extension.trimmed
import dev.byrt.survivalgames.world.SGWorld
import kotlinx.coroutines.suspendCancellableCoroutine
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.bossbar.BossBar.Color
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import java.time.Duration
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object GameManager {
    val gameContainers = mutableListOf<GameContainer>()
    suspend fun createContainer(isEditMode: Boolean = false, forcedMap: SGMap? = null, disableGameEndCheck: Boolean = false): GameContainer {
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
                        if(disableGameEndCheck) newContainer.disableGameEndCheck = true
                        newContainer.onCreate()

                        gameContainers.add(newContainer)
                        LobbyInfo.updateMatches()
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
        LobbyInfo.updateMatches()
    }

    fun destroyAllContainers() {
        for(container in gameContainers) {
            container.onDestroy()
        }
        gameContainers.clear()
        LobbyInfo.updateMatches()
    }

    fun getContainerById(id: String): GameContainer? {
        return gameContainers.find { container -> container.containerId.toString() == id }
    }

    fun getContainerById(id: UUID): GameContainer? {
        return gameContainers.find { container -> container.containerId == id }
    }

    //TODO: List existing items and modify when states and player counts change so items are not generated upon every interface creation
    fun getContainerMatchmakingItems(): List<ItemStack> {
        val items = mutableListOf<ItemStack>()
        for(container in gameContainers) {
            val gameState = container.instance.manager.getGameState()
            val containerPlayersSize = container.players.filter { it.playerType == PlayerType.PARTICIPANT }.size
            items.add(
                ItemStack(if(gameState == GameState.IDLE) Material.LIME_DYE else if(gameState in listOf(GameState.STARTING, GameState.IN_GAME, GameState.OVERTIME)) Material.YELLOW_DYE else Material.RED_DYE).apply {
                    editMeta {
                        it.displayName(Formatting.allTags.deserialize("<!i>$SG_FONT_TAG<playercolour><b>Survival Games"))
                        it.lore(listOf(
                            Formatting.allTags.deserialize("<!i>"),
                            Formatting.allTags.deserialize("<!i>$SG_FONT_TAG<playercolour>Players: ${if(containerPlayersSize < 16) "<green>" else "<#ff3333>"}${containerPlayersSize}<white>/${GamePlayerCount.MAX_PLAYERS}"),
                            Formatting.allTags.deserialize("<!i>$SG_FONT_TAG<playercolour>Map: <white>${container.instance.manager.map.mapName}"),
                            Formatting.allTags.deserialize("<!i>$SG_FONT_TAG<playercolour>State: <white>${gameState.name.clean()}"),
                            Formatting.allTags.deserialize("<!i>$SG_FONT_TAG<playercolour>Instance: <white>${container.containerId.trimmed()}"),
                            Formatting.allTags.deserialize("<!i>$SG_FONT_TAG<playercolour>Availability: ${if(container.isEditMode) "<#ff3333>Admin Only" else "<green>Public Match"}"),
                            Formatting.allTags.deserialize("<!i>"),
                            Formatting.allTags.deserialize("<!i>$SG_FONT_TAG${if(gameState == GameState.IDLE) "<green><b>CLICK TO JOIN THE FIGHT" else if(gameState in listOf(GameState.STARTING, GameState.IN_GAME, GameState.ROUND_END, GameState.OVERTIME)) "<yellow><b>CLICK TO SPECTATE" else "<#ff3333><b>MATCH ENDING"}"),
                            Formatting.allTags.deserialize("<!i>")
                        ))
                        it.persistentDataContainer.set(Keys.CONTAINER_ID, PersistentDataType.STRING, container.containerId.toString())
                    }
                }
            )
        }
        return items
    }

    fun addPlayerToContainer(player: Player, gameContainer: GameContainer, forceJoinAsSpectator: Boolean = false) {
        if(gameContainer.isEditMode && !player.isOp) {
            player.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}$SG_FONT_TAG<#ff3333>You do not have permission to join this instance."))
            return
        }
        player.sgPlayer().currentContainer = gameContainer
        player.showTitle(Title.title(
            Formatting.glyph("\uD000"),
            Component.empty(), Title.Times.times(Duration.ofMillis(50), Duration.ofSeconds(1), Duration.ofMillis(250))
        ))
        /** Teleport player to the correct place **/
        val preGameSpawnDataPoint = gameContainer.instance.manager.map.preGameSpawns.firstOrNull()
        val preGameSpawn = Location(gameContainer.containerWorld, preGameSpawnDataPoint?.x ?: 0.0, preGameSpawnDataPoint?.y ?: 0.0, preGameSpawnDataPoint?.z ?: 0.0)
        val spectatorSpawnDataPoint = gameContainer.instance.manager.map.spectatorSpawns.firstOrNull()
        val spectatorSpawn = Location(gameContainer.containerWorld, spectatorSpawnDataPoint?.x ?: 0.0, spectatorSpawnDataPoint?.y ?: 0.0, spectatorSpawnDataPoint?.z ?: 0.0)

        /** Auto-assign type to participant if joining regularly, if the game is not already running **/
        if(forceJoinAsSpectator) {
            player.sgPlayer().setType(PlayerType.SPECTATOR)
            player.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}$SG_FONT_TAG<gray>You joined the match as a spectator."))
            player.teleport(spectatorSpawn)
        } else {
            if(gameContainer.instance.manager.getGameState() == GameState.IDLE && gameContainer.players.filter { p -> p.playerType == PlayerType.PARTICIPANT }.size < GamePlayerCount.MAX_PLAYERS) {
                player.sgPlayer().setType(PlayerType.PARTICIPANT)
                player.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}$SG_FONT_TAG<gray>You joined the match as a participant."))
                player.teleport(preGameSpawn)
            } else {
                player.sgPlayer().setType(PlayerType.SPECTATOR)
                player.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}$SG_FONT_TAG<gray>You joined the match as a spectator; this match is already running or full."))
                player.teleport(spectatorSpawn)
            }
        }
        gameContainer.players.add(player.sgPlayer())
        /** Update player's current scoreboard and pre-game scoreboard if game idle **/
        /** Set player's current Jukebox track **/
        if(gameContainer.instance.manager.getGameState() == GameState.IDLE) {
            player.scoreboard = gameContainer.instance.info.preGameScoreboard
            gameContainer.instance.info.updatePreGamePlayersRequired()
            gameContainer.containerWorld.worldBorder.setCenter(preGameSpawn.x, preGameSpawn.z)
            gameContainer.containerWorld.worldBorder.size = 50.0
            when(gameContainer.instance.manager.map) {
                SGMap.AUBURN_FOREST -> Jukebox.startMusicLoop(player, JukeboxTrack.PRE_GAME_AUBURN_FOREST)
                SGMap.ROUGHWORKS -> Jukebox.startMusicLoop(player, JukeboxTrack.PRE_GAME_ROUGHWORKS)
                SGMap.MISTWOODS -> Jukebox.startMusicLoop(player, JukeboxTrack.PRE_GAME_MISTWOODS)
                SGMap.HIGHLANDS -> Jukebox.startMusicLoop(player, JukeboxTrack.PRE_GAME_HIGHLANDS)
                SGMap.AELUMIA_CITADEL -> Jukebox.startMusicLoop(player, JukeboxTrack.PRE_GAME_AELUMIA_CITADEL)
            }
            /** Game auto-start **/
            if(gameContainer.players.filter { p -> p.playerType == PlayerType.PARTICIPANT }.size >= GamePlayerCount.MIN_PLAYERS && !gameContainer.instance.manager.isAutoStarting) {
                gameContainer.instance.manager.isAutoStarting = true
                gameContainer.players.forEach {
                    player -> player.bukkitPlayer().playSound(Sounds.Alert.GAME_AUTO_START_INITIATED)
                    player.bukkitPlayer().sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}<#20d600>Match starting!<white> Please standby for the game to begin."))
                }
                object : BukkitRunnable() {
                    var startTimer = 20
                    val startBossBar = BossBar.bossBar(Formatting.allTags.deserialize(""), 0f, Color.RED, BossBar.Overlay.PROGRESS)
                    override fun run() {
                        if(startTimer <= 0 && gameContainer.instance.manager.getGameState() == GameState.IDLE) {
                            startBossBar.name(TextAlignment.centreBossBarText("MATCH STARTED (${gameContainer.players.size}/${GamePlayerCount.MAX_PLAYERS})"))
                            gameContainer.players.forEach { player -> if(player.bukkitPlayer().activeBossBars().contains(startBossBar)) startBossBar.removeViewer(player.bukkitPlayer()) }
                            gameContainer.instance.manager.nextState()
                            cancel()
                        }
                        // Add/Remove boss bar
                        if(gameContainer.instance.manager.getGameState() == GameState.IDLE) {
                            gameContainer.players.forEach { player -> if(!player.bukkitPlayer().activeBossBars().contains(startBossBar)) startBossBar.addViewer(player.bukkitPlayer()) }
                        } else {
                            gameContainer.players.forEach { player -> if(player.bukkitPlayer().activeBossBars().contains(startBossBar)) startBossBar.removeViewer(player.bukkitPlayer()) }
                            cancel()
                        }
                        // Countdown, cancel if not enough players
                        if(gameContainer.players.filter { p -> p.playerType == PlayerType.PARTICIPANT }.size >= GamePlayerCount.MIN_PLAYERS) {
                            startTimer--
                            if(startTimer <= 5) {
                                gameContainer.players.forEach { player -> player.bukkitPlayer().playSound(Sounds.Timer.CLOCK_TICK)}
                            }
                            gameContainer.instance.info.updatePreGameTitle(startTimer)
                            startBossBar.name(TextAlignment.centreBossBarText("MATCH STARTING (${gameContainer.players.size}/${GamePlayerCount.MAX_PLAYERS}): <#ffff00>${String.format("%02d:%02d", (this.startTimer + 1) / 60, (this.startTimer + 1) % 60)}"))
                        } else {
                            gameContainer.instance.manager.isAutoStarting = false
                            startBossBar.name(TextAlignment.centreBossBarText("AWAITING PLAYERS (${gameContainer.players.size}/${GamePlayerCount.MAX_PLAYERS})"))
                            gameContainer.instance.info.updatePreGameTitle()
                            gameContainer.players.forEach { player ->
                                if(player.bukkitPlayer().activeBossBars().contains(startBossBar)) startBossBar.removeViewer(player.bukkitPlayer())
                                player.bukkitPlayer().playSound(Sounds.Alert.GAME_AUTO_START_CANCELLED)
                                player.bukkitPlayer().sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}<#ff3333>Match cancelled<white>, there are not enough players to start the game."))
                            }
                            cancel()
                        }
                    }
                }.runTaskTimer(plugin, 0L, 20L)
            }
        } else {
            player.scoreboard = gameContainer.instance.info.gameScoreboard
            if(gameContainer.instance.manager.getGameState() == GameState.STARTING) {
                if(gameContainer.instance.timer.getTimer() < 26) Jukebox.startMusicLoop(player, JukeboxTrack.IN_GAME)
            } else {
                Jukebox.startMusicLoop(player, JukeboxTrack.IN_GAME)
            }
        }
        PlayerVisuals.resetPlayerState(player)
    }

    fun removePlayerFromContainer(player: Player) {
        player.sgPlayer().currentContainer?.players?.remove(player.sgPlayer())
        player.sgPlayer().currentContainer = null
        player.sgPlayer().setType(PlayerType.IDLE)
        PlayerVisuals.resetPlayerState(player, shouldClearBossBar = true, shouldClearInventory = true, shouldResetScoreboard = true, shouldResetVehicle = true)
        player.sgPlayer().currentContainer?.instance?.manager?.gameEndCheck()
        player.teleport(Location(Bukkit.getWorlds()[0], -1914.5, 78.0, -1680.5, 0f, 0f))
        Jukebox.startMusicLoop(player, JukeboxTrack.LOBBY)
    }
}