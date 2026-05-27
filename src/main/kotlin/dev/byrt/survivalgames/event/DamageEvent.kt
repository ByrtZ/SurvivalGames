package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.PlayerVisuals
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

@Suppress("unused", "unstableApiUsage")
class DamageEvent: Listener {
    @EventHandler
    //TODO: player.sgPlayer().currentContainer.instance.manager.getGameState()
    private fun onDamage(e: EntityDamageEvent) {
        // Cancel ALL damage when not in the following game states
        if (e.entity is Player) {
            val player = e.entity as Player
            if (player.sgPlayer().currentContainer != null) {
                val currentContainer = player.sgPlayer().currentContainer!!
                if (currentContainer.instance.manager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                    e.isCancelled = true
                    return
                } else {
                    // Cancel the following types of damage
                    if (e.cause == EntityDamageEvent.DamageCause.CRAMMING
                        || e.cause == EntityDamageEvent.DamageCause.LIGHTNING
                        || e.cause == EntityDamageEvent.DamageCause.WITHER
                        || e.cause == EntityDamageEvent.DamageCause.VOID
                        || e.cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
                        e.isCancelled = true
                        return
                    } else {
                        PlayerVisuals.damageIndicator(player, e.damage)
                    }
                }
            } else {
                e.isCancelled = true
                return
            }
        }
    }

    @EventHandler
    private fun onDamageByEntity(e: EntityDamageByEntityEvent) {
        if(e.entity is Player) {
            val player = e.entity as Player
            if(player.sgPlayer().currentContainer != null) {
                val currentContainer = player.sgPlayer().currentContainer!!
                if(currentContainer.instance.manager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                    e.isCancelled = true
                    return
                } else {
                    e.isCancelled = false
                    return
                }
            } else {
                e.isCancelled = true
                return
            }
        }
    }
}