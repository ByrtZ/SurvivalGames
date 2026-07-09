package dev.byrt.survivalgames.loot.data

import dev.byrt.survivalgames.item.rarity.ItemRarity
import dev.byrt.survivalgames.item.type.ItemType
import dev.byrt.survivalgames.util.Keys
import io.papermc.paper.datacomponent.item.ItemEnchantments
import org.bukkit.Material
import org.bukkit.NamespacedKey

@Suppress("unstableApiUsage")
data class SGItem(val material: Material, val enchantments: ItemEnchantments =  ItemEnchantments.itemEnchantments().build(), val rarity: ItemRarity = ItemRarity.COMMON, val type: ItemType = ItemType.CONSUMABLE, val tooltip: NamespacedKey = Keys.COMMON_TOOLTIP, val nameOverride: String = "")