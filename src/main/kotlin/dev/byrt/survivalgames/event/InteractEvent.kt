package dev.byrt.survivalgames.event

import dev.byrt.survivalgames.defaultCoroutineScope
import dev.byrt.survivalgames.game.instance.GameState
import dev.byrt.survivalgames.interfaces.SGSpectatorInterface
import dev.byrt.survivalgames.lobby.npc.SGNPC
import dev.byrt.survivalgames.map.MapDataPointType
import dev.byrt.survivalgames.map.MapTools
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.player.PlayerType
import dev.byrt.survivalgames.util.Keys
import dev.byrt.survivalgames.util.cooldown.Cooldowns
import dev.byrt.survivalgames.util.extension.decrementItemInHand
import kotlinx.coroutines.launch
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Mannequin
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType
import java.util.Locale.getDefault

@Suppress("unused")
class InteractEvent: Listener {
    private val gameAllowBlockInteractionList = listOf(Material.CRAFTING_TABLE, Material.CHEST)
    private val gameAllowItemInteractionList = listOf(Material.BOW, Material.CROSSBOW, Material.TRIDENT, Material.WIND_CHARGE, Material.TNT, Material.ENDER_PEARL, Material.FISHING_ROD)
    @EventHandler
    private fun onInteract(e: PlayerInteractEvent) {
        /** Deny by default **/
        e.setUseInteractedBlock(Event.Result.DENY)
        e.setUseItemInHand(Event.Result.DENY)

        if(e.player.vehicle != null && e.player.sgPlayer().isDead) {
            return
        }

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
            /** Spectator compass logic **/
            if(e.player.sgPlayer().playerType == PlayerType.SPECTATOR && e.player.sgPlayer().currentContainer != null && e.player.inventory.itemInMainHand.persistentDataContainer.has(Keys.SPECTATOR_COMPASS) && e.action.isRightClick) {
                if(Cooldowns.attemptUseSpectatorCompass(e.player)) {
                    defaultCoroutineScope.launch { SGSpectatorInterface.create(e.player) }
                }
            }
            if(container.instance.manager.getGameState() in listOf(GameState.IN_GAME, GameState.OVERTIME) && e.player.sgPlayer().playerType == PlayerType.PARTICIPANT) {
                /** Item interactions **/
                val item = when(e.hand) {
                    EquipmentSlot.HAND -> e.player.inventory.itemInMainHand
                    EquipmentSlot.OFF_HAND -> e.player.inventory.itemInOffHand
                    else -> return
                }
                val material = item.type
                val allowItemUse = e.action.isRightClick && (
                        material.isEdible || material in gameAllowItemInteractionList ||
                                material.name.endsWith("_HELMET") || material.name.endsWith("_CHESTPLATE") ||
                                material.name.endsWith("_LEGGINGS") || material.name.endsWith("_BOOTS")
                        )
                if(allowItemUse) {
                    e.setUseItemInHand(Event.Result.ALLOW)
                    if(material == Material.TNT && e.hand == EquipmentSlot.HAND) {
                        if(Cooldowns.attemptUseTnt(e.player)) {
                            e.player.decrementItemInHand(e.player, e.player.inventory.itemInMainHand)
                            val tnt = e.player.world.spawn(e.player.eyeLocation, TNTPrimed::class.java)
                            tnt.source = e.player
                            val tntVelocity = e.player.location.direction.multiply(1.35)
                            tnt.velocity = tntVelocity
                        }
                    }
                }
                /** Block interactions **/
                if(e.clickedBlock != null) {
                    val block = e.clickedBlock
                    if (block != null) {
                        val type = block.type
                        if(type in gameAllowBlockInteractionList || type.name.endsWith("_DOOR") || type.name.endsWith("_TRAPDOOR")) {
                            e.setUseInteractedBlock(Event.Result.ALLOW)
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private fun onInteractEntity(e: PlayerInteractEntityEvent) {
        if(e.player.sgPlayer().currentContainer == null) {
            if(e.player.inventory.itemInMainHand.type == Material.AIR && e.rightClicked is Mannequin && e.player.gameMode in listOf(GameMode.SURVIVAL, GameMode.ADVENTURE)) {
                val npcPdcString = e.rightClicked.persistentDataContainer.get(Keys.LOBBY_NPC, PersistentDataType.STRING)
                val mannequin = e.rightClicked as Mannequin
                if(npcPdcString != null) {
                    val sgNpc = SGNPC.valueOf(npcPdcString.uppercase(getDefault()))
                    if(Cooldowns.attemptNpcInteraction(e.player)) {
                        sgNpc.onInteract(e.player, sgNpc, mannequin)
                    }
                }
            }
        }
    }
}