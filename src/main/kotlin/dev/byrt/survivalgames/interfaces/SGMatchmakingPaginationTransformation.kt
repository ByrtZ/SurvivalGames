package dev.byrt.survivalgames.interfaces

import com.noxcrew.interfaces.drawable.Drawable.Companion.drawable
import com.noxcrew.interfaces.element.Element
import com.noxcrew.interfaces.element.StaticElement
import com.noxcrew.interfaces.grid.GridPoint
import com.noxcrew.interfaces.grid.GridPositionGenerator
import com.noxcrew.interfaces.pane.Pane
import com.noxcrew.interfaces.transform.builtin.PaginationButton
import com.noxcrew.interfaces.transform.builtin.PaginationTransformation
import dev.byrt.survivalgames.game.GameManager
import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.library.Translation
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import dev.byrt.survivalgames.util.Keys
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class SGMatchmakingPaginationTransformation(items: List<ItemStack>): PaginationTransformation<Pane, ItemStack>(
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
                    val containerStringId = element.persistentDataContainer.get(Keys.CONTAINER_ID, PersistentDataType.STRING) ?: ""
                    val container = GameManager.getContainerById(containerStringId)
                    if(containerStringId.isEmpty() || container == null) {
                        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
                        player.playSound(Sounds.Misc.INTERFACE_ERROR)
                        player.sendMessage(Formatting.allTags.deserialize("${Translation.Generic.ARROW_PREFIX}$SG_FONT_TAG<#ff3333>This instance is not joinable or no longer exists."))
                        return@StaticElement
                    }
                    GameManager.addPlayerToContainer(player, container)
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
                    player.playSound(Sounds.Misc.INTERFACE_INTERACT)
                }
                else -> player.playSound(Sounds.Misc.INTERFACE_ERROR)
            }
        }
    }
}