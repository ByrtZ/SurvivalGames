package dev.byrt.survivalgames.loot

import org.bukkit.Material
import kotlin.ranges.IntRange

/**
 * [lootTableName] Name of the loot table.
 * [possibleLoot] Map of possible materials to the range of amounts of such material.
 **/
enum class SGLootTable(val lootTableName: String, val possibleLoot: Map<Material, IntRange>) {
    LOOT_CHEST_1("loot_chest_1", possibleLoot = mutableMapOf(
        Material.APPLE to IntRange(1, 4),
        Material.LEATHER to IntRange(1, 2),
        Material.WOODEN_SWORD to IntRange(1, 1),
        Material.LEATHER_HELMET to IntRange(1, 1),
        Material.LEATHER_CHESTPLATE to IntRange(1, 1),
        Material.LEATHER_LEGGINGS to IntRange(1, 1),
        Material.LEATHER_BOOTS to IntRange(1, 1)
    )),
    LOOT_CHEST_2("loot_chest_2", possibleLoot = mutableMapOf(
        Material.PORKCHOP to IntRange(1, 3),
        Material.BEEF to IntRange(1, 2),
        Material.STICK to IntRange(1, 1),
        Material.STONE_SWORD to IntRange(1, 1),
        Material.ARROW to IntRange(1, 3),
        Material.COPPER_HELMET to IntRange(1, 1),
        Material.COPPER_CHESTPLATE to IntRange(1, 1),
        Material.COPPER_LEGGINGS to IntRange(1, 1),
        Material.COPPER_HELMET to IntRange(1, 1),
        Material.BOW to IntRange(1, 1)
    )),
    LOOT_CHEST_3("loot_chest_3", possibleLoot = mutableMapOf(
        Material.COOKED_CHICKEN to IntRange(1, 3),
        Material.COOKED_BEEF to IntRange(1, 2),
        Material.IRON_INGOT to IntRange(1, 1),
        Material.ARROW to IntRange(1, 3),
        Material.SPECTRAL_ARROW to IntRange(1, 2),
        Material.STICK to IntRange(1, 2),
        Material.CHAINMAIL_HELMET to IntRange(1, 1),
        Material.CHAINMAIL_LEGGINGS to IntRange(1, 1),
        Material.CHAINMAIL_LEGGINGS to IntRange(1, 1),
        Material.CHAINMAIL_BOOTS to IntRange(1, 1),
        Material.CROSSBOW to IntRange(1, 1)
    )),
    SUPPLY_DROP("supply_drop", possibleLoot = mutableMapOf(
        Material.IRON_BOOTS to IntRange(1, 1),
        Material.IRON_LEGGINGS to IntRange(1, 1),
        Material.IRON_CHESTPLATE to IntRange(1, 1),
        Material.IRON_HELMET to IntRange(1, 1),
        Material.DIAMOND to IntRange(1, 1),
        Material.GOLDEN_APPLE to IntRange(1, 1),
    ));
}
