package dev.byrt.survivalgames.loot

import dev.byrt.survivalgames.item.rarity.ItemRarity
import dev.byrt.survivalgames.item.type.ItemType
import dev.byrt.survivalgames.logger
import io.papermc.paper.datacomponent.item.ItemEnchantments
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import kotlin.random.Random

@Suppress("unstableApiUsage")
data class SGLootItem(val material: Material, val amountMin: Int = 1, val amountMax: Int = 1, val weight: Double, val enchantments: ItemEnchantments =  ItemEnchantments.itemEnchantments().build(), val rarity: ItemRarity = ItemRarity.COMMON, val type: ItemType = ItemType.CONSUMABLE)

@Suppress("unstableApiUsage")
enum class SGLootPool(val lootPoolName: String, val lootItems: List<SGLootItem>) {
    LOOT_CHEST_1("loot_chest_1", listOf(
        SGLootItem(Material.APPLE, amountMax = 3, weight = 8.0),
        SGLootItem(Material.LEATHER, amountMax = 2, weight = 6.0),
        SGLootItem(Material.WOODEN_SWORD, weight = 5.0, type = ItemType.WEAPON),
        SGLootItem(Material.LEATHER_HELMET, weight = 4.0, type = ItemType.ARMOUR),
        SGLootItem(Material.LEATHER_CHESTPLATE, weight = 4.0, type = ItemType.ARMOUR),
        SGLootItem(Material.LEATHER_LEGGINGS, weight = 4.0, type = ItemType.ARMOUR),
        SGLootItem(Material.LEATHER_BOOTS, weight = 4.0, type = ItemType.ARMOUR),
        SGLootItem(Material.PORKCHOP, amountMax = 3, weight = 7.0),
        SGLootItem(Material.BEEF, amountMax = 2, weight = 6.0),
        SGLootItem(Material.WIND_CHARGE, weight = 2.0, rarity = ItemRarity.EPIC, type = ItemType.UTILITY),
        SGLootItem(Material.STICK, weight = 2.0, rarity = ItemRarity.UNCOMMON),
    )),
    LOOT_CHEST_2("loot_chest_2", listOf(
        SGLootItem(Material.COOKED_COD, amountMax = 3, weight = 9.0, rarity = ItemRarity.UNCOMMON),
        SGLootItem(Material.COOKED_CHICKEN, amountMax = 2, weight = 8.0, rarity = ItemRarity.UNCOMMON),
        SGLootItem(Material.STONE_SWORD, weight = 6.0, rarity = ItemRarity.UNCOMMON, type = ItemType.WEAPON),
        SGLootItem(Material.ARROW, amountMax = 2, weight = 5.0, rarity = ItemRarity.UNCOMMON),
        SGLootItem(Material.COPPER_HELMET, weight = 4.0, rarity = ItemRarity.UNCOMMON, type = ItemType.ARMOUR),
        SGLootItem(Material.COPPER_CHESTPLATE, weight = 4.0, rarity = ItemRarity.UNCOMMON, type = ItemType.ARMOUR),
        SGLootItem(Material.COPPER_LEGGINGS, weight = 4.0, rarity = ItemRarity.UNCOMMON, type = ItemType.ARMOUR),
        SGLootItem(Material.COPPER_BOOTS, weight = 4.0, rarity = ItemRarity.UNCOMMON, type = ItemType.ARMOUR),
        SGLootItem(Material.BOW, weight = 3.0, rarity = ItemRarity.UNCOMMON, type = ItemType.WEAPON),
        SGLootItem(Material.WIND_CHARGE, weight = 1.0, amountMax = 2, rarity = ItemRarity.EPIC, type = ItemType.UTILITY),
    )),
    LOOT_CHEST_3("loot_chest_3", listOf(
        SGLootItem(Material.COOKED_PORKCHOP, amountMax = 3, weight = 8.0, rarity = ItemRarity.RARE),
        SGLootItem(Material.COOKED_BEEF, amountMax = 2, weight = 7.0, rarity = ItemRarity.RARE),
        SGLootItem(Material.ARROW, amountMax = 3, weight = 5.0, rarity = ItemRarity.UNCOMMON),
        SGLootItem(Material.SPECTRAL_ARROW, amountMax = 2, weight = 2.0, rarity = ItemRarity.UNCOMMON),
        SGLootItem(Material.CHAINMAIL_HELMET, weight = 4.0, rarity = ItemRarity.RARE, type = ItemType.ARMOUR),
        SGLootItem(Material.CHAINMAIL_CHESTPLATE, weight = 4.0, rarity = ItemRarity.RARE, type = ItemType.ARMOUR),
        SGLootItem(Material.CHAINMAIL_LEGGINGS, weight = 4.0, rarity = ItemRarity.RARE, type = ItemType.ARMOUR),
        SGLootItem(Material.CHAINMAIL_BOOTS, weight = 4.0, rarity = ItemRarity.RARE, type = ItemType.ARMOUR),
        SGLootItem(Material.BOW, weight = 3.0, rarity = ItemRarity.UNCOMMON, type = ItemType.WEAPON),
    )),
    SUPPLY_DROP("supply_drop", listOf(
        SGLootItem(Material.IRON_HELMET, weight = 4.0, rarity = ItemRarity.EPIC, type = ItemType.ARMOUR),
        SGLootItem(Material.IRON_CHESTPLATE, weight = 4.0, rarity = ItemRarity.EPIC, type = ItemType.ARMOUR),
        SGLootItem(Material.IRON_LEGGINGS, weight = 4.0, rarity = ItemRarity.EPIC, type = ItemType.ARMOUR),
        SGLootItem(Material.IRON_BOOTS, weight = 4.0, rarity = ItemRarity.EPIC, type = ItemType.ARMOUR),
        SGLootItem(Material.DIAMOND, weight = 1.0, rarity = ItemRarity.LEGENDARY),
        SGLootItem(Material.GOLDEN_APPLE, weight = 0.5, rarity = ItemRarity.EPIC),
        SGLootItem(Material.CROSSBOW, weight = 2.0, rarity = ItemRarity.RARE, type = ItemType.WEAPON),
        SGLootItem(Material.TRIDENT, weight = 0.05, enchantments =  ItemEnchantments.itemEnchantments().add(Enchantment.LOYALTY, 1).build(), rarity = ItemRarity.LEGENDARY, type = ItemType.WEAPON),
    ));
    companion object {
        fun getRandomItem(lootPool: SGLootPool): SGLootItem {
            val totalItems = lootPool.lootItems
            val totalWeight = totalItems.sumOf { it.weight }
            val randomValue = Random.nextDouble(totalWeight)
            var cumulativeWeight = 0.0
            for (item in totalItems) {
                cumulativeWeight += item.weight
                if (randomValue < cumulativeWeight) {
                    return item
                }
            }
            logger.warning("Unreachable code hit! No item selected")
            return SGLootItem(Material.AIR, weight = 0.0) // Should be unreachable but default to null in case of issue
        }
    }
}