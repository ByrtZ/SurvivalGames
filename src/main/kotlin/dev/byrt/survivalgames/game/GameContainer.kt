package dev.byrt.survivalgames.game

import dev.byrt.survivalgames.game.instance.GameInstance
import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.text.ChatUtility
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

data class GameContainer(val containerName: String, val containerId: UUID, val containerWorld: World) {
    init {
        logger.info("Initialising game container $containerId")
        ChatUtility.broadcastDev("Initialising game container $containerId", true)
    }
    val players: MutableList<Player> = mutableListOf()
    val instance: GameInstance = GameInstance(containerId)

    fun onCreate() {
        instance.currentContainer = this
        instance.info.buildScoreboard()
    }

    fun onDestroy() {
        instance.info.destroyScoreboard()
        this.players.forEach { player ->
            player.sgPlayer().currentContainer = null
            player.teleport(Location(Bukkit.getWorlds()[0], 0.0, 0.0, 0.0, 0f, 0f))
        }
        this.players.clear()
        instance.currentContainer = null
        plugin.server.unloadWorld(containerWorld, false)
    }
}