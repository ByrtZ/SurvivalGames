package dev.byrt.survivalgames.loot.pool

import dev.byrt.survivalgames.logger
import dev.byrt.survivalgames.loot.data.SGLootItem
import dev.byrt.survivalgames.loot.data.SGLootOdds
import dev.byrt.survivalgames.loot.items.SGItems
import kotlin.random.Random

@Suppress("unstableApiUsage")
enum class SGLootPool(val lootPoolName: String, val lootItems: List<SGLootItem>) {
    LOOT_CHEST_1("loot_chest_1", listOf(
        SGLootItem(SGItems.APPLE, SGLootOdds(weight = 6.0, amountMax = 3)),
        SGLootItem(SGItems.WOODEN_SWORD, SGLootOdds(weight = 5.0)),
        SGLootItem(SGItems.LEATHER_HELMET, SGLootOdds(weight = 3.0)),
        SGLootItem(SGItems.LEATHER_CHESTPLATE, SGLootOdds(weight = 3.0)),
        SGLootItem(SGItems.LEATHER_LEGGINGS, SGLootOdds(weight = 3.0)),
        SGLootItem(SGItems.LEATHER_BOOTS, SGLootOdds(weight = 3.0)),
        SGLootItem(SGItems.COPPER_HELMET, SGLootOdds(weight = 2.0)),
        SGLootItem(SGItems.COPPER_CHESTPLATE, SGLootOdds(weight = 2.0)),
        SGLootItem(SGItems.COPPER_LEGGINGS, SGLootOdds(weight = 2.0)),
        SGLootItem(SGItems.COPPER_BOOTS, SGLootOdds(weight = 2.0)),
        SGLootItem(SGItems.COOKED_PORKCHOP, SGLootOdds(weight = 5.0, amountMax = 3)),
        SGLootItem(SGItems.COOKED_BEEF, SGLootOdds(weight = 5.0, amountMax = 2)),
        SGLootItem(SGItems.WIND_CHARGE, SGLootOdds(weight = 2.0)),
        SGLootItem(SGItems.STICK, SGLootOdds(weight = 2.0)),
        SGLootItem(SGItems.IRON_INGOT, SGLootOdds(weight = 1.0)),
        SGLootItem(SGItems.TNT, SGLootOdds(weight = 1.0)),
        SGLootItem(SGItems.ARROW, SGLootOdds(weight = 2.0)),
    )),
    LOOT_CHEST_2("loot_chest_2", listOf(
        SGLootItem(SGItems.COOKED_COD, SGLootOdds(weight = 4.0, amountMax = 3)),
        SGLootItem(SGItems.COOKED_CHICKEN, SGLootOdds(weight = 5.0, amountMax = 2)),
        SGLootItem(SGItems.STONE_SWORD, SGLootOdds(weight = 6.0)),
        SGLootItem(SGItems.ARROW, SGLootOdds(weight = 5.0, amountMax = 2)),
        SGLootItem(SGItems.GOLDEN_HELMET, SGLootOdds(weight = 3.0)),
        SGLootItem(SGItems.GOLDEN_CHESTPLATE, SGLootOdds(weight = 3.0)),
        SGLootItem(SGItems.GOLDEN_LEGGINGS, SGLootOdds(weight = 3.0)),
        SGLootItem(SGItems.GOLDEN_BOOTS, SGLootOdds(weight = 3.0)),
        SGLootItem(SGItems.CHAINMAIL_HELMET, SGLootOdds(weight = 1.5)),
        SGLootItem(SGItems.CHAINMAIL_CHESTPLATE, SGLootOdds(weight = 1.5)),
        SGLootItem(SGItems.CHAINMAIL_LEGGINGS, SGLootOdds(weight = 1.5)),
        SGLootItem(SGItems.CHAINMAIL_BOOTS, SGLootOdds(weight = 1.5)),
        SGLootItem(SGItems.BOW, SGLootOdds(weight = 3.0)),
        SGLootItem(SGItems.WIND_CHARGE, SGLootOdds(weight = 2.5, amountMax = 2)),
        SGLootItem(SGItems.TNT, SGLootOdds(weight = 3.0)),
        SGLootItem(SGItems.STICK, SGLootOdds(weight = 2.0)),
        SGLootItem(SGItems.FISHING_ROD, SGLootOdds(weight = 1.0)),
        SGLootItem(SGItems.IRON_INGOT, SGLootOdds(weight = 1.0)),
        SGLootItem(SGItems.ENDER_PEARL, SGLootOdds(weight = 0.5)),
    )),
    LOOT_CHEST_3("loot_chest_3", listOf(
        SGLootItem(SGItems.COOKED_PORKCHOP, SGLootOdds(weight = 6.0, amountMax = 3)),
        SGLootItem(SGItems.COOKED_BEEF, SGLootOdds(weight = 5.0, amountMax = 2)),
        SGLootItem(SGItems.IRON_SWORD, SGLootOdds(weight = 3.0)),
        SGLootItem(SGItems.ARROW, SGLootOdds(weight = 5.0, amountMax = 3)),
        SGLootItem(SGItems.SPECTRAL_ARROW, SGLootOdds(weight = 2.0, amountMax = 2)),
        SGLootItem(SGItems.CHAINMAIL_HELMET, SGLootOdds(weight = 4.0)),
        SGLootItem(SGItems.CHAINMAIL_CHESTPLATE, SGLootOdds(weight = 4.0)),
        SGLootItem(SGItems.CHAINMAIL_LEGGINGS, SGLootOdds(weight = 4.0)),
        SGLootItem(SGItems.CHAINMAIL_BOOTS, SGLootOdds(weight = 4.0)),
        SGLootItem(SGItems.IRON_HELMET, SGLootOdds(weight = 1.0)),
        SGLootItem(SGItems.IRON_CHESTPLATE, SGLootOdds(weight = 1.0)),
        SGLootItem(SGItems.IRON_LEGGINGS, SGLootOdds(weight = 1.0)),
        SGLootItem(SGItems.IRON_BOOTS, SGLootOdds(weight = 1.0)),
        SGLootItem(SGItems.BOW, SGLootOdds(weight = 3.0)),
        SGLootItem(SGItems.CROSSBOW, SGLootOdds(weight = 2.0)),
        SGLootItem(SGItems.TNT, SGLootOdds(weight = 2.5)),
        SGLootItem(SGItems.STICK, SGLootOdds(weight = 3.0, amountMax = 2)),
        SGLootItem(SGItems.FISHING_ROD, SGLootOdds(weight = 3.0)),
        SGLootItem(SGItems.ENDER_PEARL, SGLootOdds(weight = 0.75)),
        SGLootItem(SGItems.IRON_INGOT, SGLootOdds(weight = 3.0)),
    )),
    SUPPLY_DROP("supply_drop", listOf(
        SGLootItem(SGItems.IRON_HELMET, SGLootOdds(weight = 3.0)),
        SGLootItem(SGItems.IRON_CHESTPLATE, SGLootOdds(weight = 3.0)),
        SGLootItem(SGItems.IRON_LEGGINGS, SGLootOdds(weight = 3.0)),
        SGLootItem(SGItems.IRON_BOOTS, SGLootOdds(weight = 3.0)),
        SGLootItem(SGItems.DIAMOND, SGLootOdds(weight = 0.5)),
        SGLootItem(SGItems.GOLDEN_APPLE, SGLootOdds(weight = 1.5)),
        SGLootItem(SGItems.CROSSBOW, SGLootOdds(weight = 2.0)),
        SGLootItem(SGItems.LOYALTY_TRIDENT, SGLootOdds(weight = 0.5)),
        SGLootItem(SGItems.TNT, SGLootOdds(weight = 1.75, amountMax = 2)),
        SGLootItem(SGItems.ENDER_PEARL, SGLootOdds(weight = 1.0)),
        SGLootItem(SGItems.MACE, SGLootOdds(weight = 0.25)),
        SGLootItem(SGItems.TOTEM_OF_UNDYING, SGLootOdds(weight = 0.5)),
    ));
    companion object {
        fun getRandomLoot(lootPool: SGLootPool): SGLootItem {
            val totalItems = lootPool.lootItems
            val totalWeight = totalItems.sumOf { it.odds.weight }
            val randomValue = Random.nextDouble(totalWeight)
            var cumulativeWeight = 0.0
            for (item in totalItems) {
                cumulativeWeight += item.odds.weight
                if (randomValue < cumulativeWeight) {
                    return item
                }
            }
            logger.warning("Unreachable code hit! No item selected")
            return SGLootItem(SGItems.APPLE, SGLootOdds(weight = 1.0)) // Should be unreachable but default to low value item in case of issue
        }
    }
}