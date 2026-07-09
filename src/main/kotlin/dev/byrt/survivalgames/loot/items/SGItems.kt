package dev.byrt.survivalgames.loot.items

import dev.byrt.survivalgames.item.rarity.ItemRarity
import dev.byrt.survivalgames.item.type.ItemType
import dev.byrt.survivalgames.loot.data.SGItem
import dev.byrt.survivalgames.util.Keys
import io.papermc.paper.datacomponent.item.ItemEnchantments
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

@Suppress("unstableApiUsage")
enum class SGItems(val item: SGItem) {
    /* Food */
    APPLE(SGItem(material = Material.APPLE)),
    COOKED_BEEF(SGItem(material = Material.COOKED_BEEF)),
    COOKED_CHICKEN(SGItem(material = Material.COOKED_CHICKEN)),
    COOKED_COD(SGItem(material = Material.COOKED_COD)),
    COOKED_PORKCHOP(SGItem(material = Material.COOKED_PORKCHOP)),
    GOLDEN_APPLE(SGItem(material = Material.GOLDEN_APPLE, rarity = ItemRarity.EPIC, tooltip = Keys.EPIC_TOOLTIP)),

    /* Materials */
    DIAMOND(SGItem(material = Material.DIAMOND, rarity = ItemRarity.LEGENDARY, tooltip = Keys.LEGENDARY_TOOLTIP)),
    IRON_INGOT(SGItem(material = Material.IRON_INGOT, rarity = ItemRarity.RARE, tooltip = Keys.RARE_TOOLTIP)),
    STICK(SGItem(material = Material.STICK)),

    /* Utility */
    ARROW(SGItem(material = Material.ARROW, rarity = ItemRarity.UNCOMMON, type = ItemType.UTILITY, tooltip = Keys.UNCOMMON_TOOLTIP)),
    ENDER_PEARL(SGItem(material = Material.ENDER_PEARL, rarity = ItemRarity.EPIC, type = ItemType.UTILITY, tooltip = Keys.EPIC_TOOLTIP)),
    FISHING_ROD(SGItem(material = Material.FISHING_ROD, rarity = ItemRarity.RARE, type = ItemType.UTILITY, tooltip = Keys.RARE_TOOLTIP)),
    SPECTRAL_ARROW(SGItem(material = Material.SPECTRAL_ARROW, rarity = ItemRarity.RARE, type = ItemType.UTILITY, tooltip = Keys.RARE_TOOLTIP)),
    TNT(SGItem(material = Material.TNT, rarity = ItemRarity.RARE, type = ItemType.UTILITY, nameOverride = "Throwable TNT", tooltip = Keys.RARE_TOOLTIP)),
    TOTEM_OF_UNDYING(SGItem(material = Material.TOTEM_OF_UNDYING, rarity = ItemRarity.LEGENDARY, type = ItemType.UTILITY, tooltip = Keys.LEGENDARY_TOOLTIP)),
    WIND_CHARGE(SGItem(material = Material.WIND_CHARGE, rarity = ItemRarity.EPIC, type = ItemType.UTILITY, tooltip = Keys.EPIC_TOOLTIP)),

    /* Armour */
    LEATHER_HELMET(SGItem(material = Material.LEATHER_HELMET, type = ItemType.ARMOUR)),
    LEATHER_CHESTPLATE(SGItem(material = Material.LEATHER_CHESTPLATE, type = ItemType.ARMOUR)),
    LEATHER_LEGGINGS(SGItem(material = Material.LEATHER_LEGGINGS, type = ItemType.ARMOUR)),
    LEATHER_BOOTS(SGItem(material = Material.LEATHER_BOOTS, type = ItemType.ARMOUR)),
    COPPER_HELMET(SGItem(material = Material.COPPER_HELMET, rarity = ItemRarity.UNCOMMON, type = ItemType.ARMOUR, tooltip = Keys.UNCOMMON_TOOLTIP)),
    COPPER_CHESTPLATE(SGItem(material = Material.COPPER_CHESTPLATE, rarity = ItemRarity.UNCOMMON, type = ItemType.ARMOUR, tooltip = Keys.UNCOMMON_TOOLTIP)),
    COPPER_LEGGINGS(SGItem(material = Material.COPPER_LEGGINGS, rarity = ItemRarity.UNCOMMON, type = ItemType.ARMOUR, tooltip = Keys.UNCOMMON_TOOLTIP)),
    COPPER_BOOTS(SGItem(material = Material.COPPER_BOOTS, rarity = ItemRarity.UNCOMMON, type = ItemType.ARMOUR, tooltip = Keys.UNCOMMON_TOOLTIP)),
    GOLDEN_HELMET(SGItem(material = Material.GOLDEN_HELMET, rarity = ItemRarity.RARE, type = ItemType.ARMOUR, tooltip = Keys.RARE_TOOLTIP)),
    GOLDEN_CHESTPLATE(SGItem(material = Material.GOLDEN_CHESTPLATE, rarity = ItemRarity.RARE, type = ItemType.ARMOUR, tooltip = Keys.RARE_TOOLTIP)),
    GOLDEN_LEGGINGS(SGItem(material = Material.GOLDEN_LEGGINGS, rarity = ItemRarity.RARE, type = ItemType.ARMOUR, tooltip = Keys.RARE_TOOLTIP)),
    GOLDEN_BOOTS(SGItem(material = Material.GOLDEN_BOOTS, rarity = ItemRarity.RARE, type = ItemType.ARMOUR, tooltip = Keys.RARE_TOOLTIP)),
    CHAINMAIL_HELMET(SGItem(material = Material.CHAINMAIL_HELMET, rarity = ItemRarity.RARE, type = ItemType.ARMOUR, tooltip = Keys.RARE_TOOLTIP)),
    CHAINMAIL_CHESTPLATE(SGItem(material = Material.CHAINMAIL_CHESTPLATE, rarity = ItemRarity.RARE, type = ItemType.ARMOUR, tooltip = Keys.RARE_TOOLTIP)),
    CHAINMAIL_LEGGINGS(SGItem(material = Material.CHAINMAIL_LEGGINGS, rarity = ItemRarity.RARE, type = ItemType.ARMOUR, tooltip = Keys.RARE_TOOLTIP)),
    CHAINMAIL_BOOTS(SGItem(material = Material.CHAINMAIL_BOOTS, rarity = ItemRarity.RARE, type = ItemType.ARMOUR, tooltip = Keys.RARE_TOOLTIP)),
    IRON_HELMET(SGItem(material = Material.IRON_HELMET, rarity = ItemRarity.EPIC, type = ItemType.ARMOUR, tooltip = Keys.EPIC_TOOLTIP)),
    IRON_CHESTPLATE(SGItem(material = Material.IRON_CHESTPLATE, rarity = ItemRarity.EPIC, type = ItemType.ARMOUR, tooltip = Keys.EPIC_TOOLTIP)),
    IRON_LEGGINGS(SGItem(material = Material.IRON_LEGGINGS, rarity = ItemRarity.EPIC, type = ItemType.ARMOUR, tooltip = Keys.EPIC_TOOLTIP)),
    IRON_BOOTS(SGItem(material = Material.IRON_BOOTS, rarity = ItemRarity.EPIC, type = ItemType.ARMOUR, tooltip = Keys.EPIC_TOOLTIP)),

    /* Weapons */
    BOW(SGItem(material = Material.BOW, rarity = ItemRarity.UNCOMMON, type = ItemType.WEAPON, tooltip = Keys.UNCOMMON_TOOLTIP)),
    CROSSBOW(SGItem(material = Material.CROSSBOW, rarity = ItemRarity.RARE, type = ItemType.WEAPON, tooltip = Keys.RARE_TOOLTIP)),
    DIAMOND_SWORD(SGItem(material = Material.DIAMOND_SWORD, rarity = ItemRarity.LEGENDARY, type = ItemType.WEAPON, tooltip = Keys.LEGENDARY_TOOLTIP)),
    IRON_SWORD(SGItem(material = Material.IRON_SWORD, rarity = ItemRarity.EPIC, type = ItemType.WEAPON, tooltip = Keys.EPIC_TOOLTIP)),
    LOYALTY_TRIDENT(SGItem(material = Material.TRIDENT, rarity = ItemRarity.LEGENDARY, type = ItemType.WEAPON, tooltip = Keys.LEGENDARY_TOOLTIP, enchantments = ItemEnchantments.itemEnchantments().add(Enchantment.LOYALTY, 1).build(), nameOverride = "Loyalty Trident")),
    MACE(SGItem(material = Material.MACE, rarity = ItemRarity.MYTHIC, type = ItemType.WEAPON, tooltip = Keys.MYTHIC_TOOLTIP)),
    STONE_SWORD(SGItem(material = Material.STONE_SWORD, rarity = ItemRarity.UNCOMMON, type = ItemType.WEAPON, tooltip = Keys.UNCOMMON_TOOLTIP)),
    WOODEN_SWORD(SGItem(material = Material.WOODEN_SWORD, type = ItemType.WEAPON))
}