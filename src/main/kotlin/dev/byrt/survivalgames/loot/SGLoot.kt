package dev.byrt.survivalgames.loot

import dev.byrt.survivalgames.loot.data.SGLootItem
import dev.byrt.survivalgames.loot.items.SGItems
import dev.byrt.survivalgames.loot.pool.SGLootPool
import dev.byrt.survivalgames.map.SGMap
import dev.byrt.survivalgames.text.ChatUtility
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SG_FONT_TAG
import dev.byrt.survivalgames.util.Keys
import dev.byrt.survivalgames.util.extension.name
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Chest
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.random.Random

@Suppress("unstableApiUsage")
object SGLoot {
    fun populateMapLoot(world: World, map: SGMap) {
        ChatUtility.broadcastDebug("Started populating loot for map '${map.mapName}'")
        if(map.lootChests.isEmpty()) {
            ChatUtility.broadcastDebug("No possible loot locations in map '${map.mapName}'")
        } else {
            map.lootChests.forEach { point ->
                spawnLootChest(world, point.x.toInt(), point.y.toInt(), point.z.toInt(), SGLootPool.getRandomLootPool())
            }
            ChatUtility.broadcastDebug("Finished populating loot for map '${map.mapName}'")
        }
    }

    fun spawnLootChest(world: World, x: Int, y: Int, z: Int, lootPool: SGLootPool) {
        val chest = world.getBlockAt(x, y, z).apply { type = Material.CHEST }
        val chestState = chest.state as Chest
        val inventory = chestState.inventory

        /** Loot filter for supply drops to ensure unique items each time **/
        val lootFilter = mutableListOf<SGLootItem>()
        repeat(Random.nextInt(3, 6)) {
            val lootItem = SGLootPool.getRandomLoot(lootPool, filter = lootFilter)
            if(lootPool == SGLootPool.SUPPLY_DROP) lootFilter.add(lootItem)
            if (lootItem.item.item.material == Material.AIR) return
            if (lootItem.odds.amountMin < 1) return
            if (lootItem.odds.amountMax < 1) return

            val slotIndex = Random.nextInt(0, inventory.size - 1)
            val item = getItem(lootItem.item, Random.nextInt(lootItem.odds.amountMin, lootItem.odds.amountMax.inc()))
            if (inventory.getItem(slotIndex) == null) {
                inventory.setItem(slotIndex, item)
            } else {
                inventory.setItem(inventory.firstEmpty(), item)
            }
        }
    }

    fun getItem(sgItem: SGItems, amount: Int = 1): ItemStack {
        return ItemStack(sgItem.item.material, amount).apply {
            editMeta {
                it.displayName(Formatting.allTags.deserialize("<reset><!i><${sgItem.item.rarity.rarityColour}>${SG_FONT_TAG}${sgItem.item.nameOverride.ifBlank { sgItem.item.material.name() }}</reset>"))
                it.lore(listOf(Formatting.allTags.deserialize("<reset><!i><white>${sgItem.item.rarity.asMiniMessage()}${sgItem.item.type.asMiniMessage()}")))
                it.isUnbreakable = true
                it.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
                it.persistentDataContainer.set(Keys.LOOT_ITEM, PersistentDataType.BOOLEAN, true)
                it.tooltipStyle = sgItem.item.tooltip
                if(sgItem.item.enchantments.enchantments().isNotEmpty()) {
                    sgItem.item.enchantments.enchantments().forEach { (enchantment, level) ->
                        it.addEnchant(enchantment, level, true)
                    }
                }
            }
        }
    }
}