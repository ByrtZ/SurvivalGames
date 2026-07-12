package dev.byrt.survivalgames.interfaces

import com.noxcrew.interfaces.drawable.Drawable.Companion.drawable
import com.noxcrew.interfaces.element.Element
import com.noxcrew.interfaces.element.StaticElement
import com.noxcrew.interfaces.grid.GridPoint
import com.noxcrew.interfaces.grid.GridPositionGenerator
import com.noxcrew.interfaces.pane.Pane
import com.noxcrew.interfaces.transform.builtin.PaginationButton
import com.noxcrew.interfaces.transform.builtin.PaginationTransformation
import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.library.Translation
import dev.byrt.survivalgames.player.PlayerManager
import dev.byrt.survivalgames.player.PlayerType
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import dev.byrt.survivalgames.util.Keys
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class SGSpectatorPaginationTransformation(items: List<ItemStack>): PaginationTransformation<Pane, ItemStack>(
    positionGenerator = GridPositionGenerator { buildList {
        for(row in 1..4) {
            for(col in 1..7) {
                add(GridPoint(row, col))
            }
        }
    }},
    items,
    back = PaginationButton(
        position = GridPoint(5, 2),
        drawable = drawable(ItemStack(Material.ARROW).apply { itemMeta = itemMeta.apply {
            displayName(Formatting.allTags.deserialize("<!i><playercolour>Previous Page"))
        } }),
        increments = mapOf(Pair(ClickType.LEFT, -1)),
        clickHandler = { player -> player.playSound(Sounds.Misc.INTERFACE_INTERACT) }
    ),
    forward = PaginationButton(
        position = GridPoint(5, 6),
        drawable = drawable(ItemStack(Material.ARROW).apply { itemMeta = itemMeta.apply {
            displayName(Formatting.allTags.deserialize("<!i><playercolour>Next Page"))
        } }),
        increments = mapOf(Pair(ClickType.LEFT, 1)),
        clickHandler = { player -> player.playSound(Sounds.Misc.INTERFACE_INTERACT) }
    )) {
    override suspend fun drawElement(index: Int, element: ItemStack): Element {
        return StaticElement(drawable(if(element.type == Material.AIR)
            ItemStack(Material.BARRIER).apply {
                itemMeta = itemMeta.apply {
                    displayName(Formatting.allTags.deserialize("<!i><red>An error occurred when loading this item."))
                }
            } else element)
        ) { click ->
            val player = click.player
            when(click.type) {
                ClickType.LEFT -> {
                    val participantIdString = element.persistentDataContainer.get(Keys.PARTICIPANT_UUID, PersistentDataType.STRING) ?: ""
                    val participant = PlayerManager.sgPlayers[UUID.fromString(participantIdString)]
                    if(participantIdString.isEmpty() || participant == null || participant.playerType != PlayerType.PARTICIPANT) {
                        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
                        player.playSound(Sounds.Misc.INTERFACE_ERROR)
                        player.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}$SG_FONT_TAG<#ff3333>This participant cannot be spectated."))
                        return@StaticElement
                    }
                    val targetLocation = participant.bukkitPlayer().location
                    val direction = targetLocation.direction.normalize()
                    val teleportLocation = targetLocation.add(direction.multiply(-1.5)).add(0.0, 0.5, 0.0)
                    teleportLocation.direction = targetLocation.direction

                    player.teleport(teleportLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
                    player.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}${SG_FONT_TAG}Teleported to <playercolour>${participant.playerName}</playercolour>!"))
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
                    player.playSound(Sounds.Misc.INTERFACE_INTERACT)
                }
                else -> player.playSound(Sounds.Misc.INTERFACE_ERROR)
            }
        }
    }
}