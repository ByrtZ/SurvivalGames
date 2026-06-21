package dev.byrt.survivalgames.loot.data

import dev.byrt.survivalgames.item.rarity.ItemRarity
import dev.byrt.survivalgames.item.type.ItemType
import io.papermc.paper.datacomponent.item.ItemEnchantments
import org.bukkit.Material

@Suppress("unstableApiUsage")
data class SGItem(val material: Material, val enchantments: ItemEnchantments =  ItemEnchantments.itemEnchantments().build(), val rarity: ItemRarity = ItemRarity.COMMON, val type: ItemType = ItemType.CONSUMABLE, val nameOverride: String = "")