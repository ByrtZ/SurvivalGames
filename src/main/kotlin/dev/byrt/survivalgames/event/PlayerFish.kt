package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.lobby.fishing.Fishing
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.PlayerType
import dev.byrt.survivalgames.text.Formatting
import org.bukkit.Tag
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

@Suppress("unused")
class PlayerFish: Listener {
    @EventHandler
    private fun onPlayerFish(e: PlayerFishEvent) {
        if(e.player.sgPlayer().currentContainer != null && e.player.sgPlayer().playerType == PlayerType.PARTICIPANT) {
            if(e.state in listOf(PlayerFishEvent.State.LURED, PlayerFishEvent.State.IN_GROUND)) {
                e.isCancelled = true
                return
            }
            e.isCancelled = false
        } else {
            if(e.hook.hookedEntity != null) {
                val hookedEntity = e.hook.hookedEntity!!
                if(hookedEntity !is Item) {
                    e.hook.remove()
                    e.isCancelled = true
                    return
                }
            }

            // Set wait time between 3..8 seconds
            e.hook.minWaitTime = 3 * 20
            e.hook.maxWaitTime = 8 * 20
            e.expToDrop = 0

            if(e.state !== PlayerFishEvent.State.CAUGHT_FISH) return
            val item = e.caught as Item

            if(Tag.ITEMS_FISHES.isTagged(item.itemStack.type)) {
                Fishing.catchFish(e.player, item, (e.caught as Item).location, null, null)
            } else {
                e.player.playSound(Sounds.Misc.INTERFACE_ERROR)
                e.player.sendActionBar(Formatting.allTags.deserialize("<font:sg:font><red>Your fishing line snapped!"))
                item.remove()
            }
        }
    }
}