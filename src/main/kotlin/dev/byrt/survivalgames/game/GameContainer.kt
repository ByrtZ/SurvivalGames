package dev.byrt.survivalgames.game

import dev.byrt.survivalgames.game.instance.GameInstance
import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.text.ChatUtility
import dev.byrt.survivalgames.world.SGWorld
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

data class GameContainer(val containerName: String, val containerId: UUID, val containerWorld: World) {
    init {
        logger.info("Initialising game container $containerId")
        ChatUtility.broadcastDev("<dark_gray>Initialising game container $containerId", true)
    }
    val players: MutableList<Player> = mutableListOf()
    val instance: GameInstance = GameInstance(containerId)

    fun onCreate() {
        instance.currentContainer = this
        instance.info.buildPreGameBoard()
        instance.info.buildGameBoard()
        logger.info("Game container $containerId finished initialisation.")
        ChatUtility.broadcastDev("<dark_gray>Game container $containerId finished initialisation.", true)
    }

    fun onDestroy() {
        instance.task.stopGameLoop()
        instance.info.destroyAllScoreboards()
        SGWorld.deleteGameWorld(containerWorld)
        logger.info("Game container $containerId was destroyed.")
        ChatUtility.broadcastDev("<dark_gray>Game container $containerId was destroyed.", true)
    }
}