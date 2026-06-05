package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.game.instance.GameTime
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.PlayerVisuals
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

@Suppress("unused", "unstableApiUsage")
class DamageEvent: Listener {
    @EventHandler
    private fun onDamage(e: EntityDamageEvent) {
        if (e.entity is Player) {
            val player = e.entity as Player
            if (player.sgPlayer().currentContainer != null) {
                val currentContainer = player.sgPlayer().currentContainer!!
                // Cancel ALL damage when not in the following game states
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
                        if(currentContainer.instance.timer.getTimer() <
                            if(currentContainer.instance.manager.map.isQuickMatch) GameTime.IN_GAME_TIME_QUICK_MATCH - GameTime.GRACE_PERIOD
                            else GameTime.IN_GAME_TIME - GameTime.GRACE_PERIOD) {
                            PlayerVisuals.damageIndicator(player, e.damage)
                        } else {
                            e.isCancelled = true
                            return
                        }
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
        if(e.damager is Firework) {
            e.isCancelled = true
            return
        }
        if(e.entity is Player) {
            val player = e.entity as Player
            if(player.sgPlayer().currentContainer != null) {
                val currentContainer = player.sgPlayer().currentContainer!!
                if(currentContainer.instance.manager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                    e.isCancelled = true
                    return
                } else {
                    if(currentContainer.instance.timer.getTimer() <
                        if(currentContainer.instance.manager.map.isQuickMatch) GameTime.IN_GAME_TIME_QUICK_MATCH - GameTime.GRACE_PERIOD
                        else GameTime.IN_GAME_TIME - GameTime.GRACE_PERIOD) {
                        e.isCancelled = false
                        return
                    } else {
                        e.isCancelled = true
                        return
                    }
                }
            } else {
                e.isCancelled = true
                return
            }
        }
    }
}