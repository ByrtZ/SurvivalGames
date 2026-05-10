package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.game.instance.GameInstanceManager
import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer

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
        if(GameInstanceManager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
            e.isCancelled = true
            return
        } else {
            // Cancel the following types of damage
            if(e.cause == EntityDamageEvent.DamageCause.CRAMMING
                || e.cause == EntityDamageEvent.DamageCause.LIGHTNING
                || e.cause == EntityDamageEvent.DamageCause.WITHER
                || e.cause == EntityDamageEvent.DamageCause.VOID
                || e.cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
                e.isCancelled = true
                return
            } else {
                //TODO: Damage indicators
                /*if(e.entity is Player) {
                    val player = e.entity as Player
                    // Cancel if player is dead
                    if(player.burbPlayer().isDead) {
                        e.isCancelled = true
                    } else {
                        if(e.damageSource.causingEntity != null) {
                            val damage = BurbWeapons.calculateDamage(player, e.damage)
                            if(e.damageSource.causingEntity is Player) {
                                val damager = e.damageSource.causingEntity as Player
                                if(!damager.burbPlayer().isDead && damage > 0.1) {
                                    PlayerVisuals.damageIndicator(player, damage)
                                }
                            } else {
                                if(damage > 0.1) {
                                    PlayerVisuals.damageIndicator(player, damage)
                                }
                            }
                        }
                    }
                }*/
            }
        }
    }

    @EventHandler
    private fun onDamageByEntity(e: EntityDamageByEntityEvent) {
        if(GameInstanceManager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
            e.isCancelled = true
            return
        }
    }
}