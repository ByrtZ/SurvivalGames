package dev.byrt.survivalgames.game.mechanic.light

import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.util.Keys
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.block.Block
import org.bukkit.block.data.Levelled
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object SGPlayerDynamicLight {
    private val lightMap = ConcurrentHashMap<UUID, Block>()
    private const val LIGHT_DECAY_TICKS = 30L
    private const val MAX_LIGHT_LEVEL = 15
    private const val MIN_LIGHT_LEVEL = 0

    fun onPlayerMove(player: Player, from: Location, to: Location?) {
        if(to == null) return
        if(from.blockX == to.blockX && from.blockY == to.blockY && from.blockZ == to.blockZ) return
        val block = player.location.block
        if(!block.type.isAir) return

        val lastBlock = lightMap[player.uniqueId]
        if(lastBlock != null && lastBlock.location != block.location) {
            val originalState = lastBlock.state

            lastBlock.type = Material.LIGHT
            val lightData = lastBlock.blockData as Levelled
            if(player.inventory.itemInMainHand.persistentDataContainer.get(Keys.LANTERN, PersistentDataType.BOOLEAN) == true || player.inventory.itemInOffHand.persistentDataContainer.get(
                    Keys.LANTERN, PersistentDataType.BOOLEAN) == true) {
                lightData.level = 15
            } else {
                val playerArmourLevel = player.getAttribute(Attribute.ARMOR)?.value?.toInt()
                if(playerArmourLevel != null) {
                    lightData.level = when {
                        playerArmourLevel <= 0 -> MIN_LIGHT_LEVEL
                        playerArmourLevel in 1..3 -> 3
                        playerArmourLevel in 4..7 -> 6
                        playerArmourLevel in 8..12 -> 9
                        playerArmourLevel in 13..16 -> 12
                        else -> MAX_LIGHT_LEVEL
                    }
                }
            }
            lastBlock.blockData = lightData
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                if(lastBlock.type == Material.LIGHT) {
                    originalState.update(true, false)
                }
            }, LIGHT_DECAY_TICKS)
        }
        lightMap[player.uniqueId] = block
    }
}