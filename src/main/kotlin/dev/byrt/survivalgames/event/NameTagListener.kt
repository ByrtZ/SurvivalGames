package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.nametag.provider.GameNameTagProvider
import dev.byrt.survivalgames.player.PlayerManager
import dev.byrt.survivalgames.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent

class NameTagListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onDamage(e: EntityDamageEvent) {
        val player = e.entity as? Player ?: return
        refresh(player)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onHeal(e: EntityRegainHealthEvent) {
        val player = e.entity as? Player ?: return
        refresh(player)
    }

    private fun refresh(entity: Entity?) {
        val player = entity as? Player ?: return
        val sgPlayer = PlayerManager.sgPlayers[player.uniqueId] ?: return
        if (!player.isOnline || !player.isValid) return
        val provider = sgPlayer.nameTagProvider ?: return
        if (provider !is GameNameTagProvider) return
        if (sgPlayer.nameTag == null) return

        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (!player.isOnline || !player.isValid) return@Runnable
            provider.update(sgPlayer)
        })
    }
}