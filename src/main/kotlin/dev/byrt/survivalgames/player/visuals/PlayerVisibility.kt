package dev.byrt.survivalgames.player.visuals

import dev.byrt.survivalgames.player.SGPlayer
import dev.byrt.survivalgames.plugin
import org.bukkit.Bukkit

object PlayerVisibility {
    private var hiddenPlayers = mutableSetOf<SGPlayer>()
    /** Hides already hidden players to the receiver, used to sync on join. **/
    fun hideHiddenPlayers(receiver: SGPlayer) {
        for(player in hiddenPlayers) {
            receiver.bukkitPlayer().hidePlayer(plugin, player.bukkitPlayer())
        }
    }

    /** Hides the [player] from all online players. **/
    fun hide(player: SGPlayer) {
        for(p in Bukkit.getOnlinePlayers()) {
            p.hidePlayer(plugin, player.bukkitPlayer())
        }
        hiddenPlayers.add(player)
    }

    /** Shows the [player] to all online players. **/
    fun show(player: SGPlayer) {
        for(p in Bukkit.getOnlinePlayers()) {
            p.showPlayer(plugin, player.bukkitPlayer())
        }
        hiddenPlayers.remove(player)
    }
}