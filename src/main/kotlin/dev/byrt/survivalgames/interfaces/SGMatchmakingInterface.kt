package dev.byrt.survivalgames.interfaces

import com.noxcrew.interfaces.drawable.Drawable.Companion.drawable
import com.noxcrew.interfaces.element.StaticElement
import com.noxcrew.interfaces.interfaces.buildChestInterface
import dev.byrt.survivalgames.defaultCoroutineScope
import dev.byrt.survivalgames.game.GameManager
import dev.byrt.survivalgames.library.Sounds
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

object SGMatchmakingInterface {
    val INTERFACE_TITLE = Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<b><playercolour><shadow:#0:0.75>Matchmaking")
    suspend fun create(player: Player) = buildChestInterface {
        titleSupplier = { INTERFACE_TITLE }
        rows = 6
        val containerItems: List<ItemStack> = GameManager.getContainerMatchmakingItems()
        /** Apply pagination transform **/
        addTransform(SGMatchmakingPaginationTransformation(containerItems))
        /** Add overview item **/
        withTransform { pane, _ ->
            val infoMenuItem = ItemStack(Material.NETHER_STAR)
            val infoMenuItemMeta = infoMenuItem.itemMeta
            infoMenuItemMeta.displayName(Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<playercolour>Matchmaking"))
            infoMenuItemMeta.lore(
                listOf(
                    Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<white>View all instances and click"),
                    Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<white>to join a match!")
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
        if (containerItems.isEmpty()) {
            withTransform { pane, _ ->
                val noContainersItem = ItemStack(Material.BARRIER)
                val noContainersItemMeta = noContainersItem.itemMeta
                noContainersItemMeta.displayName(Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<red>No matches available"))
                noContainersItemMeta.lore(
                    listOf(
                        Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<dark_gray>If this issue persists, please"),
                        Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<dark_gray>report this to an admin."),
                    )
                )
                noContainersItem.itemMeta = noContainersItemMeta
                pane[2, 4] = StaticElement(drawable(noContainersItem))
            }
        }
        if (player.isOp) {
            withTransform { pane, _ ->
                val createContainerItem = ItemStack(Material.COMMAND_BLOCK)
                val createContainerItemMeta = createContainerItem.itemMeta
                createContainerItemMeta.displayName(Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<green>Click to create a match"))
                createContainerItemMeta.lore(
                    listOf(
                        Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<playercolour>Builds a new default container."),
                        Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<playercolour>If you wish to run"),
                        Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<playercolour>custom settings, use"),
                        Formatting.allTags.deserialize("<!i>${SG_FONT_TAG}<playercolour>the command <yellow>'/container'")
                    )
                )
                createContainerItem.itemMeta = createContainerItemMeta
                pane[0, 0] = StaticElement(drawable(createContainerItem)) {
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
                    player.playSound(Sounds.Misc.INTERFACE_ENTER_SUB_MENU)
                    player.sendMessage(Formatting.allTags.deserialize("$SG_FONT_TAG<gray>[Matchmaking] Generating a new match."))
                    defaultCoroutineScope.launch {
                            GameManager.createContainer()
                        }.invokeOnCompletion { _ -> player.sendMessage(Formatting.allTags.deserialize("$SG_FONT_TAG<gray>[Matchmaking] New match is ready."))
                    }
                }
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

