package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.map.MapDataPointType
import dev.byrt.survivalgames.map.MapTools
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.util.Keys
import dev.byrt.survivalgames.util.cooldown.Cooldowns
import org.bukkit.Material
import org.bukkit.block.data.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType

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
                /** Data Point creation in edit mode logic **/
                if(container.isEditMode && e.player.inventory.itemInMainHand.persistentDataContainer.has(Keys.DATA_POINT_EDIT) && e.action.isRightClick && e.clickedBlock != null) {
                    if(Cooldowns.attemptCreateDataPoint(e.player)) {
                        val dataPointType = e.player.inventory.itemInMainHand.persistentDataContainer.get(Keys.DATA_POINT_EDIT, PersistentDataType.STRING)?.let { MapDataPointType.valueOf(it) }
                        if(dataPointType != null) {
                            MapTools.createDataPoint(e.player,e.clickedBlock!!, dataPointType, container.instance.manager.map)
                        }
                    }
                }
                /** Generic interactions checks **/
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
            || e.clickedBlock?.type?.name?.startsWith("POTTED_") == true
            || e.clickedBlock?.type?.name?.endsWith("_TABLE") == true)
    }
}