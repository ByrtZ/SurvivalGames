package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import org.bukkit.Material
import org.bukkit.block.data.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

@Suppress("unused")
class InteractEvent: Listener {
    @EventHandler
    private fun onInteract(e: PlayerInteractEvent) {
        if(e.player.vehicle != null) {
            if(e.player.isDead) {
                e.isCancelled = true
            }
        } else {
            if(e.player.sgPlayer().currentContainer != null) {
                val container = e.player.sgPlayer().currentContainer!!
                if(container.instance.manager.getGameState() !in listOf(GameState.IN_GAME, GameState.OVERTIME)) {
                    if(e.clickedBlock != null) {
                        if(e.clickedBlock!!.type == Material.CHEST && e.action.isRightClick) {
                            e.isCancelled = false
                        } else {
                            e.isCancelled = genericInteractionsCheck(e)
                        }
                    } else {
                        e.isCancelled = genericInteractionsCheck(e)
                    }
                }
            } else {
                e.isCancelled = genericInteractionsCheck(e)
            }
        }
    }

    fun genericInteractionsCheck(e: PlayerInteractEvent): Boolean {
        return e.action.isRightClick
            && (e.clickedBlock?.blockData is Openable
            || e.clickedBlock?.blockData is Directional
            || e.clickedBlock?.blockData is Orientable
            || e.clickedBlock?.blockData is Rotatable
            || e.clickedBlock?.blockData is Powerable
            || e.clickedBlock?.type == Material.FLOWER_POT
            || e.clickedBlock?.type == Material.BEACON
            || e.clickedBlock?.type?.name?.startsWith("POTTED_") == true)
    }
}