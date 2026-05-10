package dev.byrt.survivalgames.player.event

import dev.byrt.survivalgames.game.GameContainer
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

data class PlayerLeaveContainerEvent(
    val player: Player,
    val previousContainer: GameContainer?
) : Event() {

    public companion object {
        @JvmStatic val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}