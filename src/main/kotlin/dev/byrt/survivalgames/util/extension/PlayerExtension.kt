package dev.byrt.survivalgames.util.extension

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
/**
 * Gracefully decrements the amount of the held item by one,
 * or removes it entirely if only one item remains.
 **/
fun Player.decrementItemInHand(player: Player, item: ItemStack) {
    if(player.inventory.itemInMainHand.amount > 1) {
        player.inventory.itemInMainHand.amount--
    } else {
        player.inventory.setItemInMainHand(null)
    }
}