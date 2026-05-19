package dev.byrt.survivalgames.game.instance

import dev.byrt.survivalgames.game.GameContainer
import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.text.ChatUtility
import java.util.UUID

data class GameInstance(val gameInstanceId: UUID) {
    init {
        logger.info("Initialising game instance $gameInstanceId")
        ChatUtility.broadcastDev("<dark_gray>Initialising game instance $gameInstanceId", true)
    }
    val manager = GameInstanceManager(this)
    val task = GameInstanceTask(this)
    val timer = GameInstanceTimer(this)
    val rounds = GameInstanceRounds(this)
    val info = GameInstanceInfo(this)
    var currentContainer: GameContainer? = null
        set(value) {
            if (field == value) return
            field = value
        }
}
