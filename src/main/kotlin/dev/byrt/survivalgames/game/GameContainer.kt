package dev.byrt.survivalgames.game

import dev.byrt.survivalgames.game.instance.GameInstance
import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.map.MapTools
import dev.byrt.survivalgames.map.SGMap
import dev.byrt.survivalgames.world.SGWorld
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

data class GameContainer(val containerName: String, val containerId: UUID, val containerWorld: World) {
    init {
        logger.info("Initialising game container $containerId")
    }
    /* Forced container flags */
    var isEditMode: Boolean = false
    var forcedMap: SGMap? = null

    val players: MutableList<Player> = mutableListOf()
    val instance: GameInstance = GameInstance(containerId)

    fun onCreate() {
        instance.currentContainer = this
        instance.info.buildPreGameBoard()
        instance.info.buildGameBoard()
        /** Forced map property acted upon here, as [instance] is initialised before [forcedMap] is set.**/
        if(forcedMap != null) instance.manager.map = forcedMap!!
        /** Automagically spawn data point visualisations when [isEditMode] is enabled **/
        if(isEditMode) MapTools.visualiseDataPoints(this, instance.manager.map)
        logger.info("Game container $containerId finished initialisation.")
    }

    fun onDestroy() {
        instance.task.stopGameLoop()
        instance.info.destroyAllScoreboards()
        SGWorld.deleteGameWorld(containerWorld)
        logger.info("Game container $containerId was destroyed.")
    }
}