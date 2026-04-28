package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.game.GameInfo
import dev.byrt.survivalgames.text.Formatting
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

@Suppress("unused")
class PlayerJoin: Listener {
    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        e.joinMessage(Formatting.allTags.deserialize("${if(e.player.isOp) "<dark_red>" else "<speccolour>"}${e.player.name}<reset> joined the game."))
        //PlayerManager.registerPlayer(e.player)
        e.player.scoreboard = GameInfo.scoreboard
    }
}