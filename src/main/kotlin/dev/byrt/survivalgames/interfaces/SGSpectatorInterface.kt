package dev.byrt.survivalgames.interfaces

import com.noxcrew.interfaces.drawable.Drawable.Companion.drawable
import com.noxcrew.interfaces.element.StaticElement
import com.noxcrew.interfaces.interfaces.buildChestInterface
import dev.byrt.survivalgames.game.GameManager
import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.player.PlayerManager.sgPlayer
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

object SGSpectatorInterface {
    val INTERFACE_TITLE = Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<b><playercolour><shadow:#0:0.75>Spectating")
    suspend fun create(player: Player) = buildChestInterface {
        titleSupplier = { INTERFACE_TITLE }
        rows = 6
        val participantItems: List<ItemStack> = GameManager.getParticipantItems(player.sgPlayer().currentContainer!!)
        /** Apply pagination transform **/
        addTransform(SGSpectatorPaginationTransformation(participantItems))
        /** Add overview item **/
        withTransform { pane, _ ->
            val infoMenuItem = ItemStack(Material.NETHER_STAR)
            val infoMenuItemMeta = infoMenuItem.itemMeta
            infoMenuItemMeta.displayName(Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<playercolour>Spectating"))
            infoMenuItemMeta.lore(
                listOf(
                    Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<white>View all participants and"),
                    Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<white>click to spectate them.")
                )
            )
            infoMenuItem.itemMeta = infoMenuItemMeta
            pane[0, 4] = StaticElement(drawable(infoMenuItem))
        }
        /** Add close menu button **/
        withTransform { pane, _ ->
            val closeMenuItem = ItemStack(Material.BARRIER)
            val closeMenuItemMeta = closeMenuItem.itemMeta
            closeMenuItemMeta.displayName(Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<red>Close Menu"))
            closeMenuItem.itemMeta = closeMenuItemMeta
            pane[5, 4] = StaticElement(drawable(closeMenuItem)) {
                player.playSound(Sounds.Misc.INTERFACE_BACK)
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
            }
        }
        /** Draw central information item if the interface cannot be populated **/
        if (participantItems.isEmpty()) {
            withTransform { pane, _ ->
                val noContainersItem = ItemStack(Material.BARRIER)
                val noContainersItemMeta = noContainersItem.itemMeta
                noContainersItemMeta.displayName(Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<red>No participants available"))
                noContainersItemMeta.lore(
                    listOf(
                        Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<dark_gray>They're all gone?")
                    )
                )
                noContainersItem.itemMeta = noContainersItemMeta
                pane[2, 4] = StaticElement(drawable(noContainersItem))
            }
        }
        /** Fill border with blank items **/
        withTransform { pane, _ ->
            val borderItem = ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
                itemMeta = itemMeta.apply {
                    isHideTooltip = true
                }
            }
            val borderElement = StaticElement(drawable(borderItem))
            for (column in 0..8) {
                for (row in 0..<rows) {
                    if (column in listOf(0, 8) || row in listOf(0, rows - 1)) {
                        if (pane[row, column] == null) {
                            pane[row, column] = borderElement
                        }
                    }
                }
            }
        }
    }.open(player)
}