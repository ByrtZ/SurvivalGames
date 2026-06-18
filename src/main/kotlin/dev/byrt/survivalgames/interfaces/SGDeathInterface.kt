package dev.byrt.survivalgames.interfaces

import com.noxcrew.interfaces.drawable.Drawable.Companion.drawable
import com.noxcrew.interfaces.element.StaticElement
import com.noxcrew.interfaces.interfaces.buildChestInterface
import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.player.PlayerVisuals
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import org.bukkit.Material
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

object SGDeathInterface {
    val INTERFACE_TITLE = Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<b><#ff3333><shadow:#0:0.75>You died!")
    suspend fun create(player: Player) = buildChestInterface {
        titleSupplier = { INTERFACE_TITLE }
        rows = 5
        /** Add overview item **/
        withTransform { pane, _ ->
            val infoMenuItem = ItemStack(Material.NETHER_STAR)
            val infoMenuItemMeta = infoMenuItem.itemMeta
            infoMenuItemMeta.displayName(Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<#ff3333>You died!"))
            infoMenuItemMeta.lore(
                listOf(
                    Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<white>Continue spectating freely or"),
                    Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<white>return to the hub.")
                )
            )
            infoMenuItem.itemMeta = infoMenuItemMeta
            pane[0, 4] = StaticElement(drawable(infoMenuItem))
        }
        /** Continue spectating **/
        withTransform { pane, _ ->
            val continueSpectatingItem = ItemStack(Material.SPYGLASS)
            val continueSpectatingItemMeta = continueSpectatingItem.itemMeta
            continueSpectatingItemMeta.displayName(Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<playercolour><b>Continue spectating"))
            continueSpectatingItemMeta.lore(
                listOf(
                    Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<white>Click to continue"),
                    Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<white>spectating, you can"),
                    Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<white>exit at any time"),
                    Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<white>by running <yellow>/hub</yellow>")
                )
            )
            continueSpectatingItem.itemMeta = continueSpectatingItemMeta
            pane[2, 3] = StaticElement(drawable(continueSpectatingItem)) {
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
                player.playSound(Sounds.Misc.INTERFACE_INTERACT)
                PlayerVisuals.fullRespawn(player, player.vehicle as ItemDisplay)
            }
        }
        /** Return to hub **/
        withTransform { pane, _ ->
            val returnHubItem = ItemStack(Material.ENDER_EYE)
            val returnHubItemMeta = returnHubItem.itemMeta
            returnHubItemMeta.displayName(Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<playercolour><b>Return to hub"))
            returnHubItemMeta.lore(
                listOf(
                    Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<white>Click to return"),
                    Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<white>to the hub."),
                )
            )
            returnHubItem.itemMeta = returnHubItemMeta
            pane[2, 5] = StaticElement(drawable(returnHubItem)) {
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
                player.playSound(Sounds.Misc.INTERFACE_INTERACT)
                PlayerVisuals.fullRespawn(player, player.vehicle as ItemDisplay, true)
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